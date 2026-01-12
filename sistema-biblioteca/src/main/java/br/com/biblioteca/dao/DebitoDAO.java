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

      // 1. Conversão de Tipos (Model -> Banco)

      // RA: int para String (pois o banco aceita alfanumérico, mas sua regra de
      // negócio usa int)
      comando.setString(1, String.valueOf(debito.getRaAluno()));

      // Valor: BigDecimal passa direto para JDBC
      comando.setBigDecimal(2, debito.getValor());

      // Data: java.util.Date para java.sql.Date
      java.sql.Date dataSql = (debito.getDataDebito() != null)
          ? new java.sql.Date(debito.getDataDebito().getTime())
          : new java.sql.Date(System.currentTimeMillis());
      comando.setDate(3, dataSql);

      comando.executeUpdate();

      // Recupera ID gerado
      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          debito.setId(rs.getInt(1));
        }
      }

      System.out.println("Débito registrado com sucesso para o aluno RA: " + debito.getRaAluno());

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar débito: " + e.getMessage());
    }
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
      throw new RuntimeException("Erro ao buscar débitos do aluno: " + e.getMessage());
    }
    return debitos;
  }

  // Método utilitário para evitar repetição de código (Clean Code)
  private Debito montarObjeto(ResultSet rs) throws SQLException {
    // Como o construtor pede o código do aluno, pegamos ele primeiro
    // Conversão String (Banco) -> int (Model)
    String ra = rs.getString("ra_aluno");

    Debito d = new Debito(ra);
    d.setId(rs.getInt("id"));
    d.setValor(rs.getBigDecimal("valor"));

    // Conversão java.sql.Date -> java.util.Date
    java.sql.Date dataSql = rs.getDate("data_debito");
    if (dataSql != null) {
      d.setDataDebito(new java.util.Date(dataSql.getTime()));
    }

    return d;
  }
}
