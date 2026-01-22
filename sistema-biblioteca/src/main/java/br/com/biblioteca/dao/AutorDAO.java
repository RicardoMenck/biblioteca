package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Autor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

  // --- SQL CONSTANTS ---
  private static final String SQL_INSERT = "INSERT INTO autor (nome, sobrenome, titulacao) VALUES (?, ?, ?)";
  private static final String SQL_UPDATE = "UPDATE autor SET nome = ?, sobrenome = ?, titulacao = ? WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM autor WHERE id = ?";
  private static final String SQL_SELECT_ALL = "SELECT * FROM autor ORDER BY nome";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM autor WHERE id = ?";

  public void salvar(Autor autor) throws SQLException {
    if (autor.getId() == null || autor.getId() == 0) {
      this.inserir(autor);
    } else {
      this.atualizar(autor);
    }
  }

  private void inserir(Autor autor) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

      comando.setString(1, autor.getNome());
      comando.setString(2, autor.getSobrenome());
      comando.setString(3, autor.getTitulacao());

      comando.executeUpdate();

      // Recupera ID gerado
      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        autor.setId(rs.getInt(1));
      }
    } finally {
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
    }
  }

  private void atualizar(Autor autor) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_UPDATE)) {

      comando.setString(1, autor.getNome());
      comando.setString(2, autor.getSobrenome());
      comando.setString(3, autor.getTitulacao());
      comando.setInt(4, autor.getId());

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

  public List<Autor> listarTodos() throws SQLException {
    List<Autor> lista = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        lista.add(this.mapearAutor(rs));
      }
    }
    return lista;
  }

  public Autor buscarPorId(int id) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_BY_ID)) {

      comando.setInt(1, id);
      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          return this.mapearAutor(rs);
        }
      }
    }
    return null;
  }

  // --- Helper Method ---
  private Autor mapearAutor(ResultSet rs) throws SQLException {
    Autor autor = new Autor();
    autor.setId(rs.getInt("id"));
    autor.setNome(rs.getString("nome"));
    autor.setSobrenome(rs.getString("sobrenome"));
    autor.setTitulacao(rs.getString("titulacao"));
    return autor;
  }
}
