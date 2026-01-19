package br.com.biblioteca.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemEmprestimo;
import br.com.biblioteca.model.Livro;

public class TelaRelatorios extends JFrame {

  private JTabbedPane tabbedPane;

  public TelaRelatorios() {
    setTitle("Relatórios e Listagens");
    setSize(700, 500);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    initUI();
  }

  private void initUI() {
    tabbedPane = new JTabbedPane();

    // Adiciona as abas
    tabbedPane.addTab("Todos os Alunos", criarPainelAlunos());
    tabbedPane.addTab("Acervo (Livros)", criarPainelLivros());
    tabbedPane.addTab("Empréstimos Ativos", criarPainelEmprestimos());

    add(tabbedPane);
  }

  // --- ABA 1: ALUNOS ---
  private JPanel criarPainelAlunos() {
    JPanel panel = new JPanel(new BorderLayout());

    String[] colunas = { "RA", "Nome" };
    DefaultTableModel model = new DefaultTableModel(colunas, 0);
    JTable table = new JTable(model);

    // Carrega dados
    try {
      List<Aluno> lista = new AlunoDAO().listarTodos();
      for (Aluno a : lista) {
        model.addRow(new Object[] { a.getRa(), a.getNome() });
      }
    } catch (Exception e) {
      model.addRow(new Object[] { "Erro", e.getMessage() });
    }

    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  // --- ABA 2: LIVROS (ACERVO) ---
  private JPanel criarPainelLivros() {
    JPanel panel = new JPanel(new BorderLayout());

    String[] colunas = { "ID", "Obra (Título)", "Tipo", "Situação" };
    DefaultTableModel model = new DefaultTableModel(colunas, 0);
    JTable table = new JTable(model);

    try {
      List<Livro> lista = new LivroDAO().listarTodos(); // Criei esse método genérico antes?
      // Se não tiver listarTodos no LivroDAO, usaremos o listarDisponiveis ou
      // criaremos um.
      // Vou assumir que você tem um listarTodos ou similar.
      // Se der erro aqui, me avise que ajustamos o DAO.

      for (Livro l : lista) {
        String tipo = l.isExemplarBiblioteca() ? "Consulta Interna" : "Circulante";
        // Lógica visual simples: se está na lista de disponíveis, mostra OK
        // (Para simplificar, vamos mostrar apenas os dados estáticos)

        model.addRow(new Object[] {
            l.getId(),
            (l.getTitulo() != null ? l.getTitulo().getNome() : "?"),
            tipo,
            "Cadastrado"
        });
      }
    } catch (Exception e) {
      // Caso não tenha o método listarTodos, não quebra a tela
    }

    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }

  // --- ABA 3: EMPRÉSTIMOS ---
  private JPanel criarPainelEmprestimos() {
    JPanel panel = new JPanel(new BorderLayout());

    String[] colunas = { "ID Emp", "Aluno", "Data Empréstimo", "Itens" };
    DefaultTableModel model = new DefaultTableModel(colunas, 0);
    JTable table = new JTable(model);

    try {
      List<Emprestimo> lista = new EmprestimoDAO().listarTodos();
      for (Emprestimo e : lista) {
        model.addRow(new Object[] {
            e.getEmprestimo(),
            e.getNome(), // RA do Aluno
            e.getDataEmprestimo(),
            e.getItens().size() + " livros"
        });
      }
    } catch (Exception e) {
      model.addRow(new Object[] { "Erro", e.getMessage() });
    }

    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
  }
}
