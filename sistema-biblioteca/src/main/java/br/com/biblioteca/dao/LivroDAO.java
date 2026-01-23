package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

  // JOIN essencial: Traz o nome do título junto com os dados do livro físico
  private static final String SQL_SELECT_ALL = "SELECT l.*, t.nome as nome_titulo, t.isbn, t.prazo " +
      "FROM livro l " +
      "INNER JOIN titulo t ON l.id_titulo = t.id";

  private static final String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE l.id = ?";

  // Útil para ver quantos exemplares existem de um determinado título
  private static final String SQL_SELECT_BY_TITULO = SQL_SELECT_ALL + " WHERE l.id_titulo = ?";
  private static final String SQL_INSERT = "INSERT INTO livro (id_titulo, disponivel, exemplar_biblioteca) VALUES (?, ?, ?)";
  private static final String SQL_UPDATE = "UPDATE livro SET id_titulo=?, disponivel=?, exemplar_biblioteca=? WHERE id=?";
  private static final String SQL_DELETE = "DELETE FROM livro WHERE id=?";

  public void salvar(Livro livro) throws SQLException {
    if (livro.getId() == null || livro.getId() == 0) {
      this.inserir(livro);
    } else {
      this.atualizar(livro);
    }
  }

  private void inserir(Livro livro) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

      comando.setInt(1, livro.getTitulo().getId());
      comando.setInt(2, livro.isDisponivel() ? 1 : 0);
      comando.setInt(3, livro.isExemplarBiblioteca() ? 1 : 0);

      comando.executeUpdate();

      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        livro.setId(rs.getInt(1));
      }
    } finally {
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
    }
  }

  private void atualizar(Livro livro) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_UPDATE)) {

      comando.setInt(1, livro.getTitulo().getId());
      comando.setInt(2, livro.isDisponivel() ? 1 : 0);
      comando.setInt(3, livro.isExemplarBiblioteca() ? 1 : 0);
      comando.setInt(4, livro.getId());

      comando.executeUpdate();
    }
  }

  public void excluir(int id) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_DELETE)) {
      comando.setInt(1, id);
      comando.execute();
    }
  }

  public List<Livro> listarTodos() throws SQLException {
    List<Livro> livros = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        livros.add(mapearLivro(rs));
      }
    }
    return livros;
  }

  public List<Livro> listarPorTitulo(int idTitulo) throws SQLException {
    List<Livro> livros = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_BY_TITULO)) {

      comando.setInt(1, idTitulo);
      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          livros.add(mapearLivro(rs));
        }
      }
    }
    return livros;
  }

  public Livro buscarPorId(int id) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_BY_ID)) {

      comando.setInt(1, id);
      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          return mapearLivro(rs);
        }
      }
    }
    return null;
  }

  private Livro mapearLivro(ResultSet rs) throws SQLException {
    Livro livro = new Livro();
    livro.setId(rs.getInt("id"));

    // Conversão int (0/1) -> boolean (false/true)
    livro.setDisponivel(rs.getInt("disponivel") == 1);
    livro.setExemplarBiblioteca(rs.getInt("exemplar_biblioteca") == 1);

    // Monta o objeto Titulo
    Titulo titulo = new Titulo();
    titulo.setId(rs.getInt("id_titulo"));
    titulo.setNome(rs.getString("nome_titulo"));
    titulo.setIsbn(rs.getString("isbn"));
    titulo.setPrazo(rs.getInt("prazo"));

    livro.setTitulo(titulo);

    return livro;
  }
}
