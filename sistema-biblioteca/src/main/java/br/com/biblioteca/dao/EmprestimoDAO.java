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
import br.com.biblioteca.util.ConexaoBD;

public class EmprestimoDAO {

  public void salvar(Emprestimo emprestimo) {
    String sqlEmprestimo = "INSERT INTO emprestimo (ra_aluno, data_emprestimo, data_prevista_devolucao) VALUES (?, ?, ?)";
    String sqlItem = "INSERT INTO item_emprestimo (id_emprestimo, id_livro, data_devolucao_real) VALUES (?, ?, ?)";

    Connection conexao = null;
    PreparedStatement psEmprestimo = null;
    PreparedStatement psItem = null;

    try {
      conexao = ConexaoBD.getInstancia().getConexao();
      conexao.setAutoCommit(false);

      // Passo A: Salvar Cabeçalho
      psEmprestimo = conexao.prepareStatement(sqlEmprestimo, Statement.RETURN_GENERATED_KEYS);
      psEmprestimo.setString(1, emprestimo.getNome());

      long dtEmp = (emprestimo.getDataEmprestimo() != null) ? emprestimo.getDataEmprestimo().getTime()
          : System.currentTimeMillis();
      long dtPrev = (emprestimo.getDataPrevista() != null) ? emprestimo.getDataPrevista().getTime()
          : System.currentTimeMillis();

      psEmprestimo.setLong(2, dtEmp);
      psEmprestimo.setLong(3, dtPrev);

      psEmprestimo.executeUpdate();

      try (ResultSet rs = psEmprestimo.getGeneratedKeys()) {
        if (rs.next()) {
          emprestimo.setEmprestimo(rs.getInt(1));
        }
      }

      // Passo B: Salvar Itens
      psItem = conexao.prepareStatement(sqlItem);

      for (ItemEmprestimo item : emprestimo.getItens()) {
        psItem.setInt(1, emprestimo.getEmprestimo());
        psItem.setInt(2, item.getLivro().getId());

        long dtDevReal = (item.getDataDevolucao() != null) ? item.getDataDevolucao().getTime() : 0;
        psItem.setLong(3, dtDevReal);

        psItem.addBatch();
      }
      psItem.executeBatch();

      conexao.commit();

    } catch (SQLException e) {
      if (conexao != null) {
        try {
          conexao.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      throw new RuntimeException("Erro ao realizar empréstimo: " + e.getMessage());
    } finally {
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

  public List<Emprestimo> listarTodos() {
    List<Emprestimo> lista = new ArrayList<>();
    String sql = "SELECT * FROM emprestimo";

    // 1. AQUI A CONEXÃO É ABERTA
    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Emprestimo e = new Emprestimo();
        e.setEmprestimo(rs.getInt("id"));
        e.setNome(rs.getString("ra_aluno"));

        long dtEmp = rs.getLong("data_emprestimo");
        if (dtEmp > 0)
          e.setDataEmprestimo(new java.util.Date(dtEmp));

        long dtPrev = rs.getLong("data_prevista_devolucao");
        if (dtPrev > 0)
          e.setDataPrevista(new java.util.Date(dtPrev));

        // 2. PASSAMOS A CONEXÃO (conexao) COMO PARÂMETRO
        // Assim o método filho usa a mesma conexão sem fechar ela.
        e.setItens(buscarItensPorEmprestimo(conexao, e.getEmprestimo()));

        lista.add(e);
      }

    } catch (SQLException e) {
      e.printStackTrace(); // Ajuda a ver o erro no console
      throw new RuntimeException("Erro ao listar empréstimos: " + e.getMessage());
    }
    return lista;
  }

  // MUDANÇA NA ASSINATURA: Recebe a Connection
  private List<ItemEmprestimo> buscarItensPorEmprestimo(Connection conexao, int idEmprestimo) {
    List<ItemEmprestimo> itens = new ArrayList<>();
    String sql = """
            SELECT item.id as item_id, item.data_devolucao_real,
                   livro.id as livro_id, livro.exemplar_biblioteca,
                   titulo.id as titulo_id, titulo.nome as titulo_nome, titulo.prazo
              FROM item_emprestimo item
             INNER JOIN livro ON item.id_livro = livro.id
             INNER JOIN titulo ON livro.id_titulo = titulo.id
             WHERE item.id_emprestimo = ?
        """;

    // NÃO USAMOS try-with-resources na Conexão aqui!
    // Apenas no PreparedStatement
    try (PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setInt(1, idEmprestimo);

      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          br.com.biblioteca.model.Titulo t = new br.com.biblioteca.model.Titulo();
          t.setId(rs.getInt("titulo_id"));
          t.setNome(rs.getString("titulo_nome"));
          t.setPrazo(rs.getInt("prazo"));

          Livro l = new Livro();
          l.setId(rs.getInt("livro_id"));
          l.setExemplarBiblioteca(rs.getInt("exemplar_biblioteca") == 1);
          l.setTitulo(t);

          ItemEmprestimo item = new ItemEmprestimo();
          item.setId(rs.getInt("item_id"));
          item.setLivro(l);

          long dtDev = rs.getLong("data_devolucao_real");
          if (dtDev > 0)
            item.setDataDevolucao(new java.util.Date(dtDev));

          itens.add(item);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar itens do empréstimo: " + e.getMessage());
    }
    return itens;
  }

  // --- Adicione estes métodos no final da classe EmprestimoDAO ---

  // 1. Busca apenas os itens que NÃO foram devolvidos de um aluno
  public List<ItemEmprestimo> buscarPendenciasPorAluno(String raAluno) {
    List<ItemEmprestimo> pendencias = new ArrayList<>();

    // Query que cruza Item -> Emprestimo -> Livro -> Titulo
    String sql = """
            SELECT item.id AS item_id,
                   t.nome AS titulo_nome,
                   e.data_emprestimo,
                   e.data_prevista_devolucao
              FROM item_emprestimo item
             INNER JOIN emprestimo e ON item.id_emprestimo = e.id
             INNER JOIN livro l ON item.id_livro = l.id
             INNER JOIN titulo t ON l.id_titulo = t.id
             WHERE e.ra_aluno = ?
               AND (item.data_devolucao_real IS NULL OR item.data_devolucao_real = 0)
        """;

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, raAluno);

      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          // Monta um objeto híbrido só para exibir na tela
          ItemEmprestimo item = new ItemEmprestimo();
          item.setId(rs.getInt("item_id"));

          // Truque: Usamos o objeto Livro/Titulo só para carregar o nome da obra
          br.com.biblioteca.model.Titulo t = new br.com.biblioteca.model.Titulo();
          t.setNome(rs.getString("titulo_nome"));

          Livro l = new Livro();
          l.setTitulo(t);
          item.setLivro(l);

          // Carrega as datas (convertendo de Long)
          long dtEmp = rs.getLong("data_emprestimo");
          long dtPrev = rs.getLong("data_prevista_devolucao");

          // Colocamos essas datas temporariamente no item (ou poderíamos criar um DTO)
          // Aqui, vamos assumir que quem chama sabe que essas datas pertencem ao
          // Empréstimo pai.
          // Para simplificar a tela, vamos usar setDataDevolucao para guardar a PREVISTA
          // (gambiarra visual)
          if (dtPrev > 0)
            item.setDataDevolucao(new java.util.Date(dtPrev));

          pendencias.add(item);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar pendências: " + e.getMessage());
    }
    return pendencias;
  }

  // 2. Registra a devolução (UPDATE)
  public void registrarDevolucao(int idItem, java.util.Date dataDevolucao) {
    String sql = "UPDATE item_emprestimo SET data_devolucao_real = ? WHERE id = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      long millis = dataDevolucao.getTime();
      comando.setLong(1, millis);
      comando.setInt(2, idItem);

      comando.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao devolver item: " + e.getMessage());
    }
  }
}
