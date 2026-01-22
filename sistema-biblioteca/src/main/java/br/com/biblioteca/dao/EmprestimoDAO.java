package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemEmprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

  // --- SQLs ---
  // Join com Aluno para saber o nome de quem pegou na listagem
  private static final String SQL_SELECT_ALL = "SELECT e.*, a.nome as nome_aluno, a.ra " +
      "FROM emprestimo e " +
      "INNER JOIN aluno a ON e.id_aluno = a.id " +
      "ORDER BY e.data_emprestimo DESC";

  private static final String SQL_SELECT_BY_ID = "SELECT e.*, a.nome as nome_aluno, a.ra " +
      "FROM emprestimo e " +
      "INNER JOIN aluno a ON e.id_aluno = a.id " +
      "WHERE e.id = ?";

  private static final String SQL_INSERT = "INSERT INTO emprestimo (id_aluno, data_emprestimo, data_prevista) VALUES (?, ?, ?)";

  // Itens
  private static final String SQL_INSERT_ITEM = "INSERT INTO item_emprestimo (id_emprestimo, id_livro, data_prevista, data_devolucao) VALUES (?, ?, ?, ?)";

  private static final String SQL_SELECT_ITENS_POR_EMPRESTIMO = "SELECT ie.*, l.id as id_livro_real, t.nome as nome_titulo "
      +
      "FROM item_emprestimo ie " +
      "INNER JOIN livro l ON ie.id_livro = l.id " +
      "INNER JOIN titulo t ON l.id_titulo = t.id " +
      "WHERE ie.id_emprestimo = ?";

  // Atualização de Estoque
  private static final String SQL_UPDATE_DISPONIBILIDADE_LIVRO = "UPDATE livro SET disponivel = ? WHERE id = ?";

  // --- MÉTODOS ---

  public void salvar(Emprestimo emprestimo) throws SQLException {
    // Empréstimos geralmente só são criados (Insert).
    // Edição de empréstimo é raro (geralmente se cancela e faz outro),
    // mas aqui vamos focar no INSERT que é o fluxo principal.
    if (emprestimo.getId() == null) {
      this.inserir(emprestimo);
    } else {
      throw new SQLException("Edição de Empréstimo não implementada por segurança. Cancele e faça um novo.");
    }
  }

  private void inserir(Emprestimo emprestimo) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      conexao.setAutoCommit(false); // INÍCIO DA TRANSAÇÃO

      // 1. Insere o Cabeçalho (Emprestimo)
      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      comando.setInt(1, emprestimo.getAluno().getId());
      comando.setDate(2, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));

      // Data prevista geral (pode ser null se definida só nos itens, mas salvamos por
      // segurança)
      if (emprestimo.getDataPrevista() != null) {
        comando.setDate(3, new java.sql.Date(emprestimo.getDataPrevista().getTime()));
      } else {
        comando.setNull(3, java.sql.Types.DATE);
      }

      comando.executeUpdate();

      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        emprestimo.setId(rs.getInt(1));
      }

      // 2. Insere os Itens e Baixa o Estoque
      salvarItens(conexao, emprestimo);

      conexao.commit(); // EFETIVA TUDO
      System.out.println("Empréstimo #" + emprestimo.getId() + " realizado com sucesso!");

    } catch (SQLException e) {
      if (conexao != null)
        conexao.rollback(); // DESFAZ TUDO EM CASO DE ERRO
      throw e;
    } finally {
      if (conexao != null)
        conexao.setAutoCommit(true);
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
      // Connection fica aberta (Factory/Singleton)
    }
  }

  /**
   * Método privado que percorre a lista de livros, salva na tabela de ligação
   * e atualiza o status do livro físico.
   */
  private void salvarItens(Connection conexao, Emprestimo emprestimo) throws SQLException {
    if (emprestimo.getItens() == null || emprestimo.getItens().isEmpty()) {
      return;
    }

    try (PreparedStatement pstmItem = conexao.prepareStatement(SQL_INSERT_ITEM);
        PreparedStatement pstmStock = conexao.prepareStatement(SQL_UPDATE_DISPONIBILIDADE_LIVRO)) {

      for (ItemEmprestimo item : emprestimo.getItens()) {
        // a) Insert na tabela item_emprestimo
        pstmItem.setInt(1, emprestimo.getId());
        pstmItem.setInt(2, item.getLivro().getId());
        pstmItem.setDate(3, new java.sql.Date(item.getDataPrevista().getTime()));
        pstmItem.setNull(4, java.sql.Types.DATE); // Data Devolução nasce NULL
        pstmItem.executeUpdate();

        // b) Update na tabela livro (Disponivel = FALSE/0)
        pstmStock.setInt(1, 0); // 0 = Indisponível
        pstmStock.setInt(2, item.getLivro().getId());
        pstmStock.executeUpdate();
      }
    }
  }

  public List<Emprestimo> listarTodos() throws SQLException {
    List<Emprestimo> lista = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        lista.add(mapearEmprestimo(rs));
        // Nota: Por performance, NÃO carregamos os itens aqui (Lazy Loading).
        // Se precisar ver os livros, chamamos buscarPorId ou um método específico.
      }
    }
    return lista;
  }

  public Emprestimo buscarPorId(int id) throws SQLException {
    Emprestimo emp = null;
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      comando = conexao.prepareStatement(SQL_SELECT_BY_ID);
      comando.setInt(1, id);
      rs = comando.executeQuery();

      if (rs.next()) {
        emp = mapearEmprestimo(rs);
        // Aqui sim: Carregamos os itens porque estamos vendo o detalhe
        carregarItens(conexao, emp);
      }
    } finally {
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
    }
    return emp;
  }

  // Carrega os livros associados a este empréstimo
  private void carregarItens(Connection conexao, Emprestimo emp) throws SQLException {
    try (PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ITENS_POR_EMPRESTIMO)) {
      comando.setInt(1, emp.getId());
      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          ItemEmprestimo item = new ItemEmprestimo();
          item.setId(rs.getInt("id"));
          item.setDataPrevista(rs.getDate("data_prevista"));
          item.setDataDevolucao(rs.getDate("data_devolucao"));
          item.setEmprestimo(emp); // Referência circular controlada

          // Monta o Livro (Mínimo necessário)
          Livro livro = new Livro();
          livro.setId(rs.getInt("id_livro_real"));
          Titulo titulo = new Titulo();
          titulo.setNome(rs.getString("nome_titulo")); // Join
          livro.setTitulo(titulo);

          item.setLivro(livro);
          emp.getItens().add(item);
        }
      }
    }
  }

  private Emprestimo mapearEmprestimo(ResultSet rs) throws SQLException {
    Emprestimo emprestimo = new Emprestimo();
    emprestimo.setId(rs.getInt("id"));
    emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
    emprestimo.setDataPrevista(rs.getDate("data_prevista"));

    // Monta o Aluno com dados do Join
    Aluno a = new Aluno();
    a.setId(rs.getInt("id_aluno"));
    a.setNome(rs.getString("nome_aluno"));
    a.setRa(rs.getString("ra"));
    emprestimo.setAluno(a);

    return emprestimo;
  }
}
