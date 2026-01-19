package br.com.biblioteca.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.util.ConexaoBD;

public class AutorDAO {

  public void salvar(Autor autor) {
    String sql = "INSERT INTO autor (nome, sobrenome, titulacao) VALUES (?, ?, ?)";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      comando.setString(1, autor.getNome());
      comando.setString(2, autor.getSobrenome());
      comando.setString(3, autor.getTitulacao());

      comando.executeUpdate();

      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          autor.setId(rs.getInt(1)); // Padronizado como int
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar autor: " + e.getMessage());
    }
  }

  public List<Autor> listarTodos() {
    List<Autor> lista = new ArrayList<>();
    String sql = "SELECT * FROM autor";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Autor a = new Autor();
        a.setId(rs.getInt("id")); // Lendo Integer
        a.setNome(rs.getString("nome"));
        a.setSobrenome(rs.getString("sobrenome"));
        a.setTitulacao(rs.getString("titulacao"));
        lista.add(a);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar autores: " + e.getMessage());
    }
    return lista;
  }
}
