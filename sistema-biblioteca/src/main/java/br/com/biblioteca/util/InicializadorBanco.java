package br.com.biblioteca.util;

import br.com.biblioteca.factory.ConexaoFactory;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class InicializadorBanco {

  public static void criarTabelas() {
    // Lista ordenada para respeitar as dependências (FKs)
    List<String> sqls = List.of(

        // 1. Tabela AREA check
        """
            CREATE TABLE IF NOT EXISTS area (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                descricao TEXT
            );
            """,

        // 2. Tabela AUTOR check
        """
            CREATE TABLE IF NOT EXISTS autor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                sobrenome TEXT,
                titulacao TEXT
            );
            """,

        // 3. Tabela ALUNO
        """
            CREATE TABLE IF NOT EXISTS aluno (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ra TEXT NOT NULL UNIQUE,
                nome TEXT NOT NULL,
                cpf TEXT,
                endereco TEXT
            );
            """,

        // 4. Tabela TITULO check
        """
            CREATE TABLE IF NOT EXISTS titulo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                prazo INTEGER NOT NULL,
                isbn TEXT,
                edicao INTEGER,
                ano INTEGER,
                id_area INTEGER,
                FOREIGN KEY (id_area) REFERENCES area(id)
            );
            """,

        // 5. Tabela Associativa TITULO_AUTOR check
        """
            CREATE TABLE IF NOT EXISTS titulo_autor (
                id_titulo INTEGER,
                id_autor INTEGER,
                PRIMARY KEY (id_titulo, id_autor),
                FOREIGN KEY (id_titulo) REFERENCES titulo(id),
                FOREIGN KEY (id_autor) REFERENCES autor(id)
            );
            """,

        // 6. Tabela LIVRO (Exemplar Físico) check
        """
            CREATE TABLE IF NOT EXISTS livro (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_titulo INTEGER NOT NULL,
                disponivel INTEGER DEFAULT 1,         -- 1 = True (Disponível)
                exemplar_biblioteca INTEGER DEFAULT 0,-- 1 = True (Não sai da biblio)
                FOREIGN KEY (id_titulo) REFERENCES titulo(id)
            );
            """,

        // 7. Tabela EMPRESTIMO check
        """
            CREATE TABLE IF NOT EXISTS emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                data_emprestimo DATE NOT NULL,
                data_prevista DATE,
                FOREIGN KEY (id_aluno) REFERENCES aluno(id)
            );
            """,

        // 8. Tabela ITEM_EMPRESTIMO (Detalhe do Empréstimo) check
        """
            CREATE TABLE IF NOT EXISTS item_emprestimo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_emprestimo INTEGER NOT NULL,
                id_livro INTEGER NOT NULL,
                data_prevista DATE,
                data_devolucao DATE,
                FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id),
                FOREIGN KEY (id_livro) REFERENCES livro(id)
            );
            """,

        // 9. Tabela DEVOLUCAO (Relação 1:1 com Empréstimo) check
        """
            CREATE TABLE IF NOT EXISTS devolucao (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_emprestimo INTEGER UNIQUE NOT NULL, -- UNIQUE garante 1:1
                data_devolucao DATE NOT NULL,
                multa_total REAL,
                FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id)
            );
            """,

        // 10. Tabela ITEM_DEVOLUCAO (Detalhe da multa por livro) check
        """
            CREATE TABLE IF NOT EXISTS item_devolucao (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_devolucao INTEGER NOT NULL,
                id_item_emprestimo INTEGER NOT NULL,
                data_devolucao DATE,
                dias_atraso INTEGER,
                valor_multa REAL,
                FOREIGN KEY (id_devolucao) REFERENCES devolucao(id),
                FOREIGN KEY (id_item_emprestimo) REFERENCES item_emprestimo(id)
            );
            """,

        // 11. Tabela DEBITO (Financeiro) check
        """
            CREATE TABLE IF NOT EXISTS debito (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                valor REAL,
                data_debito DATE,
                FOREIGN KEY (id_aluno) REFERENCES aluno(id)
            );
            """,
        // 12. Tabela RESERVA
        """
            CREATE TABLE IF NOT EXISTS reserva (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                id_titulo INTEGER NOT NULL,
                data_reserva DATE NOT NULL,
                ativa INTEGER DEFAULT 1,     -- 1 = True (Ativa/Aguardando), 0 = False (Finalizada/Cancelada)
                FOREIGN KEY (id_aluno) REFERENCES aluno(id),
                FOREIGN KEY (id_titulo) REFERENCES titulo(id)
            );
            """);

    try {
      Connection conexao = ConexaoFactory.getConexao();
      Statement comando = conexao.createStatement();

      System.out.println("🔄 Iniciando atualização do Banco de Dados...");

      for (String sql : sqls) {
        comando.execute(sql);
      }

      System.out.println("Tabelas criadas/verificadas com sucesso!");
      comando.close();
      // Conexão permanece aberta na Factory (Singleton)

    } catch (Exception e) {
      System.err.println("Erro ao inicializar banco: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // Main para teste isolado
  public static void main(String[] args) {
    criarTabelas();
  }
}
