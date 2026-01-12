package br.com.biblioteca.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class InicializadorBanco {

  public static void criarTabelas() {
    List<String> sqls = List.of(
        // 1. Tabela Titulo
        """
            CREATE TABLE IF NOT EXISTS titulo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome VARCHAR(100),
                prazo INTEGER NOT NULL
            )
            """,
        // 2. Tabela Aluno
        """
            CREATE TABLE IF NOT EXISTS aluno (
                ra VARCHAR(20) PRIMARY KEY,
                nome VARCHAR(100) NOT NULL
            )
            """,
        // 3. Tabela Livro
        """
            CREATE TABLE IF NOT EXISTS livro (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_titulo INTEGER NOT NULL,
                exemplar_biblioteca INTEGER NOT NULL, -- 0 = false, 1 = true
                FOREIGN KEY (id_titulo) REFERENCES titulo(id)
            )
            """,
        // 4. Tabela Emprestimo
        """
            CREATE TABLE IF NOT EXISTS emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ra_aluno VARCHAR(20) NOT NULL,
                data_emprestimo TEXT NOT NULL,
                data_prevista_devolucao TEXT NOT NULL,
                FOREIGN KEY (ra_aluno) REFERENCES aluno(ra)
            )
            """,
        // 5. Tabela Item Emprestimo
        """
            CREATE TABLE IF NOT EXISTS item_emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_emprestimo INTEGER NOT NULL,
                id_livro INTEGER NOT NULL,
                data_devolucao_real TEXT,
                FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id),
                FOREIGN KEY (id_livro) REFERENCES livro(id)
            )
            """,
        // 6. Tabela Debito
        """
            CREATE TABLE IF NOT EXISTS debito (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ra_aluno VARCHAR(20) NOT NULL,
                valor DECIMAL(10, 2),
                data_debito TEXT,
                FOREIGN KEY (ra_aluno) REFERENCES aluno(ra)
            )
            """);

    try {
      Connection conexao = ConexaoBD.getInstancia().getConexao();
      Statement comando = conexao.createStatement();

      for (String sql : sqls) {
        comando.execute(sql);
      }

      System.out.println("Banco atualizado (Estrutura Titulo-Livro) criado com sucesso");
      comando.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    criarTabelas();
  }
}
