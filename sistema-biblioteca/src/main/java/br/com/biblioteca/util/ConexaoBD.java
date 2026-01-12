package br.com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

  private static ConexaoBD instancia;
  private Connection conexao;

  // Construtor privado
  private ConexaoBD() {
    abrirConexao();
  }

  // Método auxiliar para abrir conexão
  private void abrirConexao() {
    try {
      String url = "jdbc:sqlite:biblioteca.db";
      this.conexao = DriverManager.getConnection(url);
    } catch (SQLException e) {
      System.err.println("Erro fatal ao conectar no banco: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static ConexaoBD getInstancia() {
    if (instancia == null) {
      instancia = new ConexaoBD();
    }
    return instancia;
  }

  public Connection getConexao() {
    try {

      if (this.conexao == null || this.conexao.isClosed()) {
        abrirConexao();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return this.conexao;
  }
}