package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Area;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

  // --- SQL CONSTANTS ---
  private static final String SQL_INSERT = "INSERT INTO area (nome, descricao) VALUES (?, ?)";
  private static final String SQL_UPDATE = "UPDATE area SET nome = ?, descricao = ? WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM area WHERE id = ?";
  private static final String SQL_SELECT_ALL = "SELECT * FROM area ORDER BY nome";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM area WHERE id = ?";

  /**
   * Salva ou Atualiza baseado na existência do ID.
   */
  public void salvar(Area area) throws SQLException {
    if (area.getId() == null || area.getId() == 0) {
      this.inserir(area);
    } else {
      this.atualizar(area);
    }
  }

  private void inserir(Area area) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();

      // RETURN_GENERATED_KEYS é vital para recuperar o ID
      comando = conexao.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

      comando.setString(1, area.getNome());
      comando.setString(2, area.getDescricao());

      comando.executeUpdate();

      // Pega o ID gerado e atualiza o objeto Java
      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        area.setId(rs.getInt(1));
      }

    } finally {
      // Fechamos Statement e ResultSet, mas a Connection fica gerenciada pela Factory
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
    }
  }

  private void atualizar(Area area) throws SQLException {
    // try-with-resources: Fecha comando e conexao automaticamente ao final do bloco
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_UPDATE)) {

      comando.setString(1, area.getNome());
      comando.setString(2, area.getDescricao());
      comando.setInt(3, area.getId());

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

  public List<Area> listarTodos() throws SQLException {
    List<Area> areas = new ArrayList<>();

    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        // Reaproveita o mapeamento
        areas.add(this.mapearArea(rs));
      }
    }
    return areas;
  }

  public Area buscarPorId(int id) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_BY_ID)) {

      comando.setInt(1, id);

      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          return this.mapearArea(rs);
        }
      }
    }
    return null;
  }

  /**
   * Método Auxiliar (Clean Code):
   * Centraliza a conversão de ResultSet para Objeto Area.
   * Se adicionar campos no banco, altera só aqui.
   */
  private Area mapearArea(ResultSet rs) throws SQLException {
    Area area = new Area();
    area.setId(rs.getInt("id"));
    area.setNome(rs.getString("nome"));
    area.setDescricao(rs.getString("descricao"));
    return area;
  }
}
