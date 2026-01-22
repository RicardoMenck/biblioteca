package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.Titulo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

  // Join agora é direto em TITULO (mais simples)
  private static final String SQL_SELECT_ATIVAS = "SELECT r.*, a.nome as nome_aluno, a.ra, t.nome as nome_titulo " +
      "FROM reserva r " +
      "INNER JOIN aluno a ON r.id_aluno = a.id " +
      "INNER JOIN titulo t ON r.id_titulo = t.id " + // Join direto no Título
      "WHERE r.ativa = 1 " +
      "ORDER BY r.data_reserva ASC"; // Importante: Ordem de chegada (Fila)

  private static final String SQL_INSERT = "INSERT INTO reserva (id_aluno, id_titulo, data_reserva, ativa) VALUES (?, ?, ?, ?)";

  private static final String SQL_CANCELAR = "UPDATE reserva SET ativa = 0 WHERE id = ?";

  // Novo método útil: Verificar se existe reserva para um título específico
  // Isso será usado na Devolução para avisar: "Livro devolvido! Tem reserva pra
  // ele!"
  private static final String SQL_COUNT_RESERVAS = "SELECT COUNT(*) FROM reserva WHERE id_titulo = ? AND ativa = 1";

  public void salvar(Reserva reserva) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_INSERT)) {

      comando.setInt(1, reserva.getAluno().getId());
      comando.setInt(2, reserva.getTitulo().getId()); // ID do Título
      comando.setDate(3, new java.sql.Date(reserva.getDataReserva().getTime()));
      comando.setInt(4, reserva.isAtiva() ? 1 : 0);

      comando.executeUpdate();
    }
  }

  public void cancelarReserva(int idReserva) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_CANCELAR)) {
      comando.setInt(1, idReserva);
      comando.executeUpdate();
    }
  }

  public List<Reserva> listarReservasAtivas() throws SQLException {
    List<Reserva> lista = new ArrayList<>();
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_SELECT_ATIVAS);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id"));
        reserva.setDataReserva(rs.getDate("data_reserva"));
        reserva.setAtiva(rs.getInt("ativa") == 1);

        // Aluno
        Aluno aluno = new Aluno();
        aluno.setId(rs.getInt("id_aluno"));
        aluno.setNome(rs.getString("nome_aluno"));
        aluno.setRa(rs.getString("ra"));
        reserva.setAluno(aluno);

        // Título
        Titulo titulo = new Titulo();
        titulo.setId(rs.getInt("id_titulo"));
        titulo.setNome(rs.getString("nome_titulo"));
        reserva.setTitulo(titulo);

        lista.add(reserva);
      }
    }
    return lista;
  }

  public boolean existeReservaParaTitulo(int idTitulo) throws SQLException {
    try (Connection conexao = ConexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(SQL_COUNT_RESERVAS)) {
      comando.setInt(1, idTitulo);
      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    }
    return false;
  }
}
