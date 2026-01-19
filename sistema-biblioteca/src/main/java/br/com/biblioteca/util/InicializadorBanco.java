package br.com.biblioteca.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class InicializadorBanco {

  public static void criarTabelas() {
    List<String> sqls = List.of(
        // 1. Tabela Area (Dependência de Titulo)
        """
                CREATE TABLE IF NOT EXISTS area (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(100) NOT NULL,
                    descricao VARCHAR(200)
                )
            """,

        // 2. Tabela Autor (Dependência de Titulo_Autor)
        """
            CREATE TABLE IF NOT EXISTS autor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome VARCHAR(100) NOT NULL,
                sobrenome VARCHAR(100),
                titulacao VARCHAR(50)
            )
            """,

        // 3. Tabela Titulo (Atualizada com Area e novos campos)
        """
            CREATE TABLE IF NOT EXISTS titulo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome VARCHAR(100),
                prazo INTEGER NOT NULL,
                isbn VARCHAR(20),
                edicao INTEGER,
                ano INTEGER,
                id_area INTEGER,
                FOREIGN KEY (id_area) REFERENCES area(id)
            )
            """,

        // 4. Tabela Associativa Titulo_Autor (N:N)
        """
            CREATE TABLE IF NOT EXISTS titulo_autor (
                id_titulo INTEGER,
                id_autor INTEGER,
                PRIMARY KEY (id_titulo, id_autor),
                FOREIGN KEY (id_titulo) REFERENCES titulo(id),
                FOREIGN KEY (id_autor) REFERENCES autor(id)
            )
            """,

        // 5. Tabela Aluno
        """
            CREATE TABLE IF NOT EXISTS aluno (
                ra VARCHAR(20) PRIMARY KEY,
                nome VARCHAR(100) NOT NULL
            )
            """,

        // 6. Tabela Livro
        """
            CREATE TABLE IF NOT EXISTS livro (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_titulo INTEGER NOT NULL,
                exemplar_biblioteca INTEGER NOT NULL, -- 0 = false, 1 = true
                FOREIGN KEY (id_titulo) REFERENCES titulo(id)
            )
            """,

        // 7. Tabela Emprestimo (Data agora é INTEGER para guardar milissegundos)
        """
            CREATE TABLE IF NOT EXISTS emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ra_aluno VARCHAR(20) NOT NULL,
                data_emprestimo INTEGER NOT NULL,
                data_prevista_devolucao INTEGER NOT NULL,
                FOREIGN KEY (ra_aluno) REFERENCES aluno(ra)
            )
            """,

        // 8. Tabela Item Emprestimo (Data agora é INTEGER)
        """
            CREATE TABLE IF NOT EXISTS item_emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_emprestimo INTEGER NOT NULL,
                id_livro INTEGER NOT NULL,
                data_devolucao_real INTEGER,
                FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id),
                FOREIGN KEY (id_livro) REFERENCES livro(id)
            )
            """,

        // 9. Tabela Debito
        """
            CREATE TABLE IF NOT EXISTS debito (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ra_aluno VARCHAR(20) NOT NULL,
                valor DECIMAL(10, 2),
                data_debito INTEGER,
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
