package br.com.biblioteca.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import br.com.biblioteca.model.Area;
import br.com.biblioteca.util.ConexaoBD;

public class AreaDAO {

  public void salvar(Area area) {
    // Se o ID for 0, é um Insert (Novo). Se for > 0, seria um Update (não
    // implementado aqui)
    String sql = "INSERT INTO area (nome, descricao) VALUES (?, ?)";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      comando.setString(1, area.getNome());
      comando.setString(2, area.getDescricao());

      comando.executeUpdate();

      // Pega o ID (Integer) gerado pelo banco e coloca no objeto
      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          area.setId(rs.getInt(1)); // Padronizado como int
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar área: " + e.getMessage());
    }
  }

  public List<Area> listarTodos() {
    List<Area> lista = new ArrayList<>();
    String sql = "SELECT * FROM area";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Area a = new Area();
        a.setId(rs.getInt("id")); // Lendo Integer
        a.setNome(rs.getString("nome"));
        a.setDescricao(rs.getString("descricao"));
        lista.add(a);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar áreas: " + e.getMessage());
    }
    return lista;
  }
}
