package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.util.ConexaoBD;

public class AlunoDAO {

  public void salvar(Aluno aluno) {
    String sql = "INSERT INTO aluno (ra, nome) VALUES (?, ?)";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, aluno.getRa());
      comando.setString(2, aluno.getNome());

      comando.executeUpdate();
      System.out.println("Aluno salvo: " + aluno.getNome());
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar aluno: " + e.getMessage());
    }
  }

  public List<Aluno> listarTodos() {
    List<Aluno> alunos = new ArrayList<>();
    String sql = "SELECT * FROM aluno";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Aluno a = new Aluno();
        a.setRa(rs.getString("ra"));
        a.setNome(rs.getString("nome"));
        alunos.add(a);
      }

    } catch (Exception e) {
      System.err.println("Erro ao listar alunos: " + e.getMessage());
    }
    return alunos;
  }

  public Aluno buscarPorRa(String ra) {
    String sql = "SELECT * FROM aluno WHERE ra = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, ra);

      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          Aluno aluno = new Aluno();
          aluno.setRa(rs.getString("ra"));
          aluno.setNome(rs.getString("nome"));
          return aluno;
        }
      }

    } catch (SQLException e) {
      System.err.println("Erro ao buscar aluno: " + e.getMessage());
    }
    return null;
  }

  public void atualizar(Aluno aluno) {
    String sql = "UPDATE aluno SET nome = ? WHERE ra = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, aluno.getNome());
      comando.setString(2, aluno.getRa());

      comando.executeUpdate();
      System.out.println("Aluno atualizado com sucesso!");

    } catch (SQLException e) {
      System.err.println("Erro ao atualizar aluno: " + e.getMessage());
    }
  }

  public void excluir(String ra) {
    String sql = "DELETE FROM aluno WHERE ra = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, ra);
      comando.executeUpdate();
      System.out.println("Aluno excluído com sucesso!");

    } catch (SQLException e) {
      System.err.println("Erro ao excluir aluno: " + e.getMessage());
    }
  }

}
