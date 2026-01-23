package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Aluno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

  private static final String SQL_INSERT = "INSERT INTO aluno (nome, ra, cpf, endereco) VALUES (?, ?, ?, ?)";
  private static final String SQL_UPDATE = "UPDATE aluno SET nome = ?, ra = ?, cpf = ?, endereco = ? WHERE id = ?";
  private static final String SQL_DELETE = "DELETE FROM aluno WHERE id = ?";
  private static final String SQL_SELECT_ALL = "SELECT * FROM aluno ORDER BY nome";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM aluno WHERE id = ?";

  private static final String SQL_SELECT_BY_RA = "SELECT * FROM aluno WHERE ra = ?";

  public void salvar(Aluno aluno) throws SQLException {
    if (aluno.getId() == null || aluno.getId() == 0) {
      this.inserir(aluno);
    } else {
      this.atualizar(aluno);
    }
  }

  private void inserir(Aluno aluno) throws SQLException {
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;

    try {
      conn = ConexaoFactory.getConexao();

      pstm = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);

      pstm.setString(1, aluno.getNome());
      pstm.setString(2, aluno.getRa());
      pstm.setString(3, aluno.getCpf());
      pstm.setString(4, aluno.getEndereco());

      pstm.executeUpdate();

      // Atualiza o objeto com o ID novo
      rs = pstm.getGeneratedKeys();
      if (rs.next()) {
        aluno.setId(rs.getInt(1));
      }

    } finally {
      if (rs != null)
        rs.close();
      if (pstm != null)
        pstm.close();
    }
  }

  private void atualizar(Aluno aluno) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_UPDATE)) {

      pstm.setString(1, aluno.getNome());
      pstm.setString(2, aluno.getRa());
      pstm.setString(3, aluno.getCpf());
      pstm.setString(4, aluno.getEndereco());
      pstm.setInt(5, aluno.getId());

      pstm.executeUpdate();
    }
  }

  public void excluir(int id) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_DELETE)) {

      pstm.setInt(1, id);
      pstm.execute();
    }
  }

  public List<Aluno> listarTodos() throws SQLException {
    List<Aluno> lista = new ArrayList<>();
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_SELECT_ALL);
        ResultSet rs = pstm.executeQuery()) {

      while (rs.next()) {
        lista.add(this.mapearAluno(rs));
      }
    }
    return lista;
  }

  public Aluno buscarPorId(int id) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_SELECT_BY_ID)) {

      pstm.setInt(1, id);
      try (ResultSet rs = pstm.executeQuery()) {
        if (rs.next()) {
          return this.mapearAluno(rs);
        }
      }
    }
    return null;
  }

  public Aluno buscarPorRa(String ra) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_SELECT_BY_RA)) {

      pstm.setString(1, ra);
      try (ResultSet rs = pstm.executeQuery()) {
        if (rs.next()) {
          return this.mapearAluno(rs);
        }
      }
    }
    return null;
  }

  // --- (Evita repetição de código) ---
  private Aluno mapearAluno(ResultSet rs) throws SQLException {
    Aluno aluno = new Aluno();
    aluno.setId(rs.getInt("id"));
    aluno.setNome(rs.getString("nome"));
    aluno.setRa(rs.getString("ra"));
    aluno.setCpf(rs.getString("cpf"));
    aluno.setEndereco(rs.getString("endereco"));
    return aluno;
  }
}
