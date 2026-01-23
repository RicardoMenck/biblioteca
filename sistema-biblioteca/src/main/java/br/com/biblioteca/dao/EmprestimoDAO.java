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

  private static final String SQL_SELECT_ALL = "SELECT e.*, a.nome as nome_aluno, a.ra " +
      "FROM emprestimo e " +
      "INNER JOIN aluno a ON e.id_aluno = a.id " +
      "ORDER BY e.data_emprestimo DESC";

  private static final String SQL_SELECT_BY_ID = "SELECT e.*, a.nome as nome_aluno, a.ra " +
      "FROM emprestimo e " +
      "INNER JOIN aluno a ON e.id_aluno = a.id " +
      "WHERE e.id = ?";

  private static final String SQL_INSERT = "INSERT INTO emprestimo (id_aluno, data_emprestimo, data_prevista) VALUES (?, ?, ?)";

  private static final String SQL_INSERT_ITEM = "INSERT INTO item_emprestimo (id_emprestimo, id_livro, data_prevista, data_devolucao) VALUES (?, ?, ?, ?)";

  private static final String SQL_SELECT_ITENS_POR_EMPRESTIMO = "SELECT ie.*, l.id as id_livro_real, t.nome as nome_titulo "
      +
      "FROM item_emprestimo ie " +
      "INNER JOIN livro l ON ie.id_livro = l.id " +
      "INNER JOIN titulo t ON l.id_titulo = t.id " +
      "WHERE ie.id_emprestimo = ?";

  private static final String SQL_UPDATE_DISPONIBILIDADE_LIVRO = "UPDATE livro SET disponivel = ? WHERE id = ?";

  public void salvar(Emprestimo emprestimo) throws SQLException {
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
      conexao.setAutoCommit(false);

      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      comando.setInt(1, emprestimo.getAluno().getId());
      comando.setDate(2, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));

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

      salvarItens(conexao, emprestimo);

      conexao.commit();
      System.out.println("Empréstimo #" + emprestimo.getId() + " realizado com sucesso!");

    } catch (SQLException e) {
      if (conexao != null)
        conexao.rollback();
      throw e;
    } finally {
      if (conexao != null)
        conexao.setAutoCommit(true);
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();

    }
  }

  private void salvarItens(Connection conexao, Emprestimo emprestimo) throws SQLException {
    if (emprestimo.getItens() == null || emprestimo.getItens().isEmpty()) {
      return;
    }

    try (PreparedStatement comandoItem = conexao.prepareStatement(SQL_INSERT_ITEM);
        PreparedStatement comandoStock = conexao.prepareStatement(SQL_UPDATE_DISPONIBILIDADE_LIVRO)) {

      for (ItemEmprestimo item : emprestimo.getItens()) {

        comandoItem.setInt(1, emprestimo.getId());
        comandoItem.setInt(2, item.getLivro().getId());
        comandoItem.setDate(3, new java.sql.Date(item.getDataPrevista().getTime()));
        comandoItem.setNull(4, java.sql.Types.DATE); // Data Devolução nasce NULL
        comandoItem.executeUpdate();

        comandoStock.setInt(1, 0); // 0 = Indisponível
        comandoStock.setInt(2, item.getLivro().getId());
        comandoStock.executeUpdate();
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

  public Emprestimo buscarEmprestimoAbertoPorRa(String ra) throws SQLException {

    String sql = "SELECT e.* " +
        "FROM emprestimo e " +
        "INNER JOIN aluno a ON e.id_aluno = a.id " +
        "INNER JOIN item_emprestimo ie ON ie.id_emprestimo = e.id " +
        "WHERE a.ra = ? AND ie.data_devolucao IS NULL " +
        "LIMIT 1";

    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, ra);

      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          // Reaproveita o método que já existe para montar o objeto completo
          // (com itens, aluno, livros, etc.)
          return buscarPorId(rs.getInt("id"));
        }
      }
    }
    return null; // Não achou empréstimo pendente para este RA
  }

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
