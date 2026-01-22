package br.com.biblioteca.factory;

import br.com.biblioteca.util.ConexaoBD;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexaoFactory {

  /**
   * Padrão Factory Method.
   * Centraliza a entrega de conexões para o sistema.
   * * @return Uma conexão ativa com o banco de dados.
   *
   * @throws SQLException Caso não seja possível obter a conexão.
   */

  public static Connection getConexao() throws SQLException {
    // A Factory decide usar o Singleton interno.
    return ConexaoBD.getInstancia().getConexao();
  }
}
