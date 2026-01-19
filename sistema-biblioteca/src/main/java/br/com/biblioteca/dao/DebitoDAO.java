package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.model.Debito;
import br.com.biblioteca.util.ConexaoBD;

public class DebitoDAO {

  public void salvar(Debito debito) {
    String sql = "INSERT INTO debito (ra_aluno, valor, data_debito) VALUES (?, ?, ?)";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      comando.setString(1, debito.getRaAluno());
      comando.setBigDecimal(2, debito.getValor());

      // --- CORREÇÃO AQUI ---
      // Em vez de brigar com String, salvamos os MILISEGUNDOS (Long).
      // O SQLite aceita guardar número na coluna TEXT sem problemas.
      long millis = (debito.getDataDebito() != null)
          ? debito.getDataDebito().getTime()
          : System.currentTimeMillis();

      comando.setLong(3, millis); // Salva como número

      comando.executeUpdate();

      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          debito.setId(rs.getInt(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Erro ao salvar débito: " + e.getMessage());
    }
  }

  public List<Debito> listarPorAluno(String raAluno) {
    List<Debito> debitos = new ArrayList<>();
    String sql = "SELECT * FROM debito WHERE ra_aluno = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setString(1, raAluno);

      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          debitos.add(montarObjeto(rs));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Erro ao buscar débitos do aluno: " + e.getMessage());
    }
    return debitos;
  }

  public List<Debito> listarTodos() {
    List<Debito> debitos = new ArrayList<>();
    String sql = "SELECT * FROM debito";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        debitos.add(montarObjeto(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar débitos: " + e.getMessage());
    }
    return debitos;
  }

  private Debito montarObjeto(ResultSet rs) throws SQLException {
    String ra = rs.getString("ra_aluno");

    Debito d = new Debito(ra);
    d.setId(rs.getInt("id"));
    d.setValor(rs.getBigDecimal("valor"));

    // --- CORREÇÃO AQUI ---
    // Lemos o número (Long) e transformamos de volta em Data
    long millis = rs.getLong("data_debito");
    if (millis > 0) {
      d.setDataDebito(new java.util.Date(millis));
    }

    return d;
  }
}
