package br.com.biblioteca.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
      String caminhoDoProjeto = System.getProperty("user.dir");
      String caminhoBanco = caminhoDoProjeto + File.separator + "biblioteca.db";

      String url = "jdbc:sqlite:" + caminhoBanco;
      this.conexao = DriverManager.getConnection(url);

      try (Statement comando = this.conexao.createStatement()) {
        comando.execute("PRAGMA foreign_keys = ON;");
      }

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
