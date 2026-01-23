package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Debito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebitoDAO {

  private static final String SQL_INSERT = "INSERT INTO debito (id_aluno, valor, data_debito) VALUES (?, ?, ?)";
  private static final String SQL_SELECT_POR_ALUNO = "SELECT * FROM debito WHERE id_aluno = ?";
  private static final String SQL_DELETE = "DELETE FROM debito WHERE id = ?";

  public void salvar(Debito debito) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_INSERT)) {

      comando.setInt(1, debito.getCodigoAluno());
      comando.setDouble(2, debito.getValor());
      comando.setDate(3, new java.sql.Date(debito.getDataDebito().getTime()));
      comando.executeUpdate();
    }
  }

  public List<Debito> listarPorAluno(int idAluno) throws SQLException {
    List<Debito> debitos = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_POR_ALUNO)) {

      comando.setInt(1, idAluno);
      try (ResultSet rs = comando.executeQuery()) {
        while (rs.next()) {
          Debito d = new Debito();
          d.setId(rs.getInt("id"));
          d.setCodigoAluno(rs.getInt("id_aluno"));
          d.setValor(rs.getDouble("valor"));
          d.setDataDebito(rs.getDate("data_debito"));
          debitos.add(d);
        }
      }
    }
    return debitos;
  }

  public void quitarDebito(int idDebito) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_DELETE)) {
      comando.setInt(1, idDebito);
      comando.executeUpdate();
    }
  }

  public boolean possuiDebitoPendente(int idAluno) throws SQLException {

    String sql = "SELECT COUNT(*) FROM debito WHERE id_aluno = ? AND data_pagamento IS NULL";

    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setInt(1, idAluno);
      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    } catch (Exception e) {

      return false;
    }
    return false;
  }
}
