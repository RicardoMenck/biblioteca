package br.com.biblioteca.dao;

import br.com.biblioteca.factory.ConexaoFactory;
import br.com.biblioteca.model.Debito;
import br.com.biblioteca.model.Devolucao;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemDevolucao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DevolucaoDAO {

  private static final String SQL_INSERT_DEVOLUCAO = "INSERT INTO devolucao (id_emprestimo, data_devolucao, multa_total) VALUES (?, ?, ?)";
  private static final String SQL_INSERT_ITEM_DEV = "INSERT INTO item_devolucao (id_devolucao, id_item_emprestimo, data_devolucao, dias_atraso, valor_multa) VALUES (?, ?, ?, ?, ?)";
  private static final String SQL_UPDATE_ITEM_EMP = "UPDATE item_emprestimo SET data_devolucao = ? WHERE id = ?";
  private static final String SQL_UPDATE_LIVRO_DISPONIVEL = "UPDATE livro SET disponivel = 1 WHERE id = ?";
  private static final String SQL_INSERT_DEBITO = "INSERT INTO debito (id_aluno, valor, data_debito) VALUES (?, ?, ?)";

  public void salvar(Devolucao devolucao) throws SQLException {
    Connection conexao = null;
    PreparedStatement comando = null;
    ResultSet rs = null;

    try {
      conexao = ConexaoFactory.getConexao();
      conexao.setAutoCommit(false);

      comando = conexao.prepareStatement(SQL_INSERT_DEVOLUCAO, Statement.RETURN_GENERATED_KEYS);
      comando.setInt(1, devolucao.getEmprestimo().getId());
      comando.setDate(2, new java.sql.Date(devolucao.getDataDevolucao().getTime()));
      comando.setDouble(3, devolucao.getMultaTotal());
      comando.executeUpdate();

      rs = comando.getGeneratedKeys();
      if (rs.next()) {
        devolucao.setId(rs.getInt(1));
      }

      salvarItens(conexao, devolucao);

      Debito debito = devolucao.gerarDebito();
      if (debito != null) {
        salvarDebito(conexao, debito);
      }

      conexao.commit();
      System.out.println("Devolução #" + devolucao.getId() + " registrada com sucesso!");

    } catch (SQLException e) {
      if (conexao != null)
        conexao.rollback();
      e.printStackTrace();
      throw new SQLException("Erro ao processar devolução: " + e.getMessage());
    } finally {
      if (conexao != null)
        conexao.setAutoCommit(true);
      if (rs != null)
        rs.close();
      if (comando != null)
        comando.close();
    }
  }

  private void salvarItens(Connection conexao, Devolucao devolucao) throws SQLException {

    try (PreparedStatement comandoItemDev = conexao.prepareStatement(SQL_INSERT_ITEM_DEV);
        PreparedStatement comandoItemEmp = conexao.prepareStatement(SQL_UPDATE_ITEM_EMP);
        PreparedStatement pstmUpdateLivro = conexao.prepareStatement(SQL_UPDATE_LIVRO_DISPONIVEL)) {

      for (ItemDevolucao item : devolucao.getItens()) {

        comandoItemDev.setInt(1, devolucao.getId());
        comandoItemDev.setInt(2, item.getItemEmprestimo().getId());
        comandoItemDev.setDate(3, new java.sql.Date(item.getDataDevolucao().getTime()));
        comandoItemDev.setInt(4, item.getDiasAtraso());
        comandoItemDev.setDouble(5, item.getValorMulta());
        comandoItemDev.executeUpdate();

        comandoItemEmp.setDate(1, new java.sql.Date(item.getDataDevolucao().getTime()));
        comandoItemEmp.setInt(2, item.getItemEmprestimo().getId());
        comandoItemEmp.executeUpdate();

        pstmUpdateLivro.setInt(1, item.getItemEmprestimo().getLivro().getId());
        pstmUpdateLivro.executeUpdate();
      }
    }
  }

  private void salvarDebito(Connection conexao, Debito debito) throws SQLException {
    try (PreparedStatement comando = conexao.prepareStatement(SQL_INSERT_DEBITO)) {
      comando.setInt(1, debito.getCodigoAluno()); // No banco é id_aluno
      comando.setDouble(2, debito.getValor());
      comando.setDate(3, new java.sql.Date(debito.getDataDebito().getTime()));
      comando.executeUpdate();
      System.out.println(">> Débito de R$ " + debito.getValor() + " gerado para o aluno.");
    }
  }

}
