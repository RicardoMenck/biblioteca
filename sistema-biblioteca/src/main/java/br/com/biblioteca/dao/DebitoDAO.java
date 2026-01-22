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
  private static final String SQL_DELETE = "DELETE FROM debito WHERE id = ?"; // Usado para quitar a dívida

  public void salvar(Debito debito) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_INSERT)) {

      pstm.setInt(1, debito.getCodigoAluno());
      pstm.setDouble(2, debito.getValor());
      pstm.setDate(3, new java.sql.Date(debito.getDataDebito().getTime()));
      pstm.executeUpdate();
    }
  }

  /**
   * Busca todas as dívidas de um aluno específico.
   * Método vital para validar se ele pode emprestar livros.
   */
  public List<Debito> listarPorAluno(int idAluno) throws SQLException {
    List<Debito> debitos = new ArrayList<>();
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_SELECT_POR_ALUNO)) {

      pstm.setInt(1, idAluno);
      try (ResultSet rs = pstm.executeQuery()) {
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

  /**
   * Dar baixa (Quitar) um débito.
   * Simplesmente removemos o registro. Num sistema contábil real,
   * teríamos uma tabela de "Pagamentos" e mudaríamos status.
   */
  public void quitarDebito(int idDebito) throws SQLException {
    try (Connection conn = ConexaoFactory.getConexao();
        PreparedStatement pstm = conn.prepareStatement(SQL_DELETE)) {
      pstm.setInt(1, idDebito);
      pstm.executeUpdate();
    }
  }
}
