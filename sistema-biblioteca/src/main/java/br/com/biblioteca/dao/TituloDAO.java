package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Area;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Titulo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TituloDAO {

  // --- SQLs PRINCIPAIS ---
  // Note o LEFT JOIN com Area: Trazemos o nome da área junto com o título em uma
  // viagem só.
  private static final String SQL_SELECT_ALL = "SELECT t.*, a.nome as nome_area " +
      "FROM titulo t " +
      "LEFT JOIN area a ON t.id_area = a.id";

  private static final String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE t.id = ?";
  private static final String SQL_INSERT = "INSERT INTO titulo (nome, prazo, isbn, edicao, ano, id_area) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SQL_UPDATE = "UPDATE titulo SET nome=?, prazo=?, isbn=?, edicao=?, ano=?, id_area=? WHERE id=?";
  private static final String SQL_DELETE = "DELETE FROM titulo WHERE id=?";

  // --- SQLs DE ASSOCIAÇÃO (N:N com Autores) ---
  private static final String SQL_INSERT_AUTOR = "INSERT INTO titulo_autor (id_titulo, id_autor) VALUES (?, ?)";
  private static final String SQL_DELETE_AUTORES = "DELETE FROM titulo_autor WHERE id_titulo = ?";

  // Busca autores de um título específico fazendo JOIN com a tabela de autores
  private static final String SQL_SELECT_AUTORES = "SELECT au.* FROM autor au " +
      "INNER JOIN titulo_autor ta ON au.id = ta.id_autor " +
      "WHERE ta.id_titulo = ?";

  public void salvar(Titulo titulo) throws SQLException {
    if (titulo.getId() == null || titulo.getId() == 0) {
      this.inserir(titulo);
    } else {
      this.atualizar(titulo);
    }
  }

  private void inserir(Titulo titulo) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      conexao.setAutoCommit(false);

      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      configurarStatement(comando, titulo);
      comando.executeUpdate();

      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        titulo.setId(rs.getInt(1));
      }

      salvarAutores(conexao, titulo);

      conexao.commit();

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

  private void atualizar(Titulo titulo) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;

    try {
      conexao = ConexaoFactory.getConexao();
      conexao.setAutoCommit(false);

      comando = conexao.prepareStatement(SQL_UPDATE);
      configurarStatement(comando, titulo);
      comando.setInt(7, titulo.getId());
      comando.executeUpdate();

      try (PreparedStatement comandoDel = conexao.prepareStatement(SQL_DELETE_AUTORES)) {
        comandoDel.setInt(1, titulo.getId());
        comandoDel.executeUpdate();
      }

      salvarAutores(conexao, titulo);

      conexao.commit();

    } catch (SQLException e) {
      if (conexao != null)
        conexao.rollback();
      throw e;
    } finally {
      if (conexao != null)
        conexao.setAutoCommit(true);
      if (comando != null)
        comando.close();
    }
  }

  public void excluir(int id) throws SQLException {

    try (Connection conexao = ConexaoFactory.getConexao()) {
      conexao.setAutoCommit(false);
      try {

        try (PreparedStatement comando = conexao.prepareStatement(SQL_DELETE_AUTORES)) {
          comando.setInt(1, id);
          comando.executeUpdate();
        }

        try (PreparedStatement comando = conexao.prepareStatement(SQL_DELETE)) {
          comando.setInt(1, id);
          comando.executeUpdate();
        }
        conexao.commit();
      } catch (Exception e) {
        conexao.rollback();
        throw e;
      } finally {
        conexao.setAutoCommit(true);
      }
    }
  }

  public List<Titulo> listarTodos() throws SQLException {
    List<Titulo> titulos = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Titulo titulo = mapearTitulo(rs);

        titulo.setAutores(buscarAutoresDoTitulo(conexao, titulo.getId()));
        titulos.add(titulo);
      }
    }
    return titulos;
  }

  public Titulo buscarPorId(int id) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_BY_ID)) {

      comando.setInt(1, id);
      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          Titulo titulo = mapearTitulo(rs);
          titulo.setAutores(buscarAutoresDoTitulo(conexao, id));
          return titulo;
        }
      }
    }
    return null;
  }

  private void configurarStatement(PreparedStatement comando, Titulo titulo) throws SQLException {
    comando.setString(1, titulo.getNome());
    comando.setInt(2, titulo.getPrazo());
    comando.setString(3, titulo.getIsbn());
    comando.setInt(4, titulo.getEdicao());
    comando.setInt(5, titulo.getAno());

    if (titulo.getArea() != null) {
      comando.setInt(6, titulo.getArea().getId());
    } else {
      comando.setNull(6, java.sql.Types.INTEGER);
    }
  }

  private void salvarAutores(Connection conexao, Titulo titulo) throws SQLException {
    if (titulo.getAutores() == null || titulo.getAutores().isEmpty()) {
      return;
    }
    try (PreparedStatement comando = conexao.prepareStatement(SQL_INSERT_AUTOR)) {
      for (Autor autor : titulo.getAutores()) {
        comando.setInt(1, titulo.getId());
        comando.setInt(2, autor.getId());
        comando.executeUpdate();
      }
    }
  }

  private List<Autor> buscarAutoresDoTitulo(Connection conexao, int idTitulo) throws SQLException {
    List<Autor> autores = new ArrayList<>();
    try (PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_AUTORES)) {
      comando.setInt(1, idTitulo);
      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          Autor autor = new Autor();
          autor.setId(rs.getInt("id"));
          autor.setNome(rs.getString("nome"));
          autor.setSobrenome(rs.getString("sobrenome"));
          autor.setTitulacao(rs.getString("titulacao"));
          autores.add(autor);
        }
      }
    }
    return autores;
  }

  private Titulo mapearTitulo(ResultSet rs) throws SQLException {
    Titulo titulo = new Titulo();
    titulo.setId(rs.getInt("id"));
    titulo.setNome(rs.getString("nome"));
    titulo.setPrazo(rs.getInt("prazo"));
    titulo.setIsbn(rs.getString("isbn"));
    titulo.setEdicao(rs.getInt("edicao"));
    titulo.setAno(rs.getInt("ano"));

    int idArea = rs.getInt("id_area");
    if (idArea > 0) {
      Area area = new Area();
      area.setId(idArea);
      area.setNome(rs.getString("nome_area"));
      titulo.setArea(area);
    }
    return titulo;
  }
}
