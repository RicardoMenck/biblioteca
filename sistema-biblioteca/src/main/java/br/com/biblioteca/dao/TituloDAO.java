package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.ConexaoBD;

public class TituloDAO {

  public void salvar(Titulo titulo) {
    String sql = "INSERT INTO titulo (nome, prazo) VALUES (?,?)";

    // Try-with-resources para garantir fechamento
    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      comando.setString(1, titulo.getNome());
      comando.setInt(2, titulo.getPrazo());

      comando.executeUpdate();

      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          titulo.setId(rs.getInt(1));
        }
      }
      System.out.println("Título salvo: " + titulo.getNome());

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar título: " + e.getMessage());
    }
  }

  public List<Titulo> listarTodos() {
    List<Titulo> titulos = new ArrayList<>();
    String sql = "SELECT * FROM titulo";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Titulo t = new Titulo();
        t.setId(rs.getInt("id"));
        t.setNome(rs.getString("nome"));
        t.setPrazo(rs.getInt("prazo"));
        titulos.add(t);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar títulos: " + e.getMessage());
    }
    return titulos;
  }

  public Titulo buscarPorId(int id) {
    String sql = "SELECT * FROM titulo WHERE id = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement ps = conexao.prepareStatement(sql)) {

      ps.setInt(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Titulo t = new Titulo();
          t.setId(rs.getInt("id"));
          t.setNome(rs.getString("nome"));
          t.setPrazo(rs.getInt("prazo"));
          return t;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar título: " + e.getMessage());
    }
    return null;
  }
}
