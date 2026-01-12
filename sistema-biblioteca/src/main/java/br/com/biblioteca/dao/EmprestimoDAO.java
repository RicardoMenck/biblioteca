package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemEmprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.ConexaoBD;

public class EmprestimoDAO {

  // --- CREATE (Com Transação) ---
  public void salvar(Emprestimo emprestimo) {
    String sqlEmprestimo = "INSERT INTO emprestimo (ra_aluno, data_emprestimo, data_prevista_devolucao) VALUES (?, ?, ?)";
    String sqlItem = "INSERT INTO item_emprestimo (id_emprestimo, id_livro, data_devolucao_real) VALUES (?, ?, ?)";

    Connection conexao = null;
    PreparedStatement psEmprestimo = null;
    PreparedStatement psItem = null;

    try {
      conexao = ConexaoBD.getInstancia().getConexao();

      // 1. INÍCIO DA TRANSAÇÃO: Desliga o salvamento automático
      conexao.setAutoCommit(false);

      // --- Passo A: Salvar o Cabeçalho (Emprestimo) ---
      psEmprestimo = conexao.prepareStatement(sqlEmprestimo, Statement.RETURN_GENERATED_KEYS);

      // Mapeamento: Model 'Nome' -> Banco 'ra_aluno'
      psEmprestimo.setString(1, emprestimo.getNome());

      // Conversão de Datas
      java.sql.Date dataEmp = (emprestimo.getDataEmprestimo() != null)
          ? new java.sql.Date(emprestimo.getDataEmprestimo().getTime())
          : new java.sql.Date(System.currentTimeMillis());

      java.sql.Date dataPrev = (emprestimo.getDataPrevista() != null)
          ? new java.sql.Date(emprestimo.getDataPrevista().getTime())
          : new java.sql.Date(System.currentTimeMillis());

      psEmprestimo.setDate(2, dataEmp);
      psEmprestimo.setDate(3, dataPrev);

      psEmprestimo.executeUpdate();

      // Recupera o ID gerado (Autoincrement)
      try (ResultSet rs = psEmprestimo.getGeneratedKeys()) {
        if (rs.next()) {
          emprestimo.setEmprestimo(rs.getInt(1)); // Atualiza o ID no objeto Java
        }
      }

      // --- Passo B: Salvar os Itens (Livros) ---b
      psItem = conexao.prepareStatement(sqlItem);

      for (ItemEmprestimo item : emprestimo.getItens()) {
        // Validação: O livro precisa existir e ter ID
        if (item.getLivro() == null || item.getLivro().getId() == 0) {
          throw new SQLException("Tentativa de emprestar um livro sem ID válido.");
        }

        psItem.setInt(1, emprestimo.getEmprestimo()); // ID do pai
        psItem.setInt(2, item.getLivro().getId()); // ID do livro

        // Salvamos também a data calculada individualmente para o item
        java.sql.Date dataDevItem = (item.getDataDevolucao() != null)
            ? new java.sql.Date(item.getDataDevolucao().getTime())
            : dataPrev; // Se nulo, usa a do empréstimo
        psItem.setDate(3, dataDevItem);

        psItem.addBatch(); // Adiciona ao lote para performance
      }

      psItem.executeBatch(); // Executa todos os itens de uma vez

      // 2. COMMIT: Confirma tudo se chegou até aqui sem erro
      conexao.commit();
      System.out.println("Transação Finalizada! Empréstimo ID " + emprestimo.getEmprestimo() + " salvo com sucesso.");

    } catch (SQLException e) {
      // 3. ROLLBACK: Desfaz tudo em caso de erro
      if (conexao != null) {
        try {
          System.err.println("Erro na transação. Realizando Rollback...");
          conexao.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      throw new RuntimeException("Erro ao realizar empréstimo: " + e.getMessage());
    } finally {
      // Restaura o estado da conexão e fecha recursos
      try {
        if (psEmprestimo != null)
          psEmprestimo.close();
        if (psItem != null)
          psItem.close();
        if (conexao != null)
          conexao.setAutoCommit(true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  // --- READ (Listar Todos com Itens) ---
  public List<Emprestimo> listarTodos() {
    List<Emprestimo> lista = new ArrayList<>();
    String sql = "SELECT * FROM emprestimo";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Emprestimo e = new Emprestimo();
        e.setEmprestimo(rs.getInt("id"));
        e.setNome(rs.getString("ra_aluno"));

        java.sql.Date dtEmp = rs.getDate("data_emprestimo");
        if (dtEmp != null)
          e.setDataEmprestimo(new java.util.Date(dtEmp.getTime()));

        java.sql.Date dtPrev = rs.getDate("data_prevista_devolucao");
        if (dtPrev != null)
          e.setDataPrevista(new java.util.Date(dtPrev.getTime()));

        // **Eager Loading**: Carregar os itens deste empréstimo
        e.setItens(buscarItensPorEmprestimo(e.getEmprestimo()));

        lista.add(e);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar empréstimos: " + e.getMessage());
    }
    return lista;
  }

  // Método Auxiliar (Privado) para buscar os itens de um empréstimo específico
  private List<ItemEmprestimo> buscarItensPorEmprestimo(int idEmprestimo) {
    List<ItemEmprestimo> itens = new ArrayList<>();
    // Fazemos JOIN para já trazer os dados do Livro e Titulo
    String sql = """
            SELECT item.id as item_id, item.data_devolucao_real,
                   livro.id as livro_id, livro.exemplar_biblioteca,
                   titulo.id as titulo_id, titulo.nome as titulo_nome, titulo.prazo
              FROM item_emprestimo item
             INNER JOIN livro ON item.id_livro = livro.id
             INNER JOIN titulo ON livro.id_titulo = titulo.id
             WHERE item.id_emprestimo = ?
        """;

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setInt(1, idEmprestimo);

      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          // Reconstrói o objeto Titulo
          Titulo t = new Titulo();
          t.setId(rs.getInt("titulo_id"));
          t.setNome(rs.getString("titulo_nome"));
          t.setPrazo(rs.getInt("prazo"));

          // Reconstrói o objeto Livro
          Livro l = new Livro();
          l.setId(rs.getInt("livro_id"));
          l.setExemplarBiblioteca(rs.getInt("exemplar_biblioteca") == 1);
          l.setTitulo(t);

          // Reconstrói o Item
          ItemEmprestimo item = new ItemEmprestimo();
          item.setId(rs.getInt("item_id"));
          item.setLivro(l);

          java.sql.Date dtDev = rs.getDate("data_devolucao_real");
          if (dtDev != null)
            item.setDataDevolucao(new java.util.Date(dtDev.getTime()));

          itens.add(item);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar itens do empréstimo: " + e.getMessage());
    }
    return itens;
  }
}
