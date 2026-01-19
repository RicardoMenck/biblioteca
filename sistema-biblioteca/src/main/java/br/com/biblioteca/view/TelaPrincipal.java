package br.com.biblioteca.view;

import javax.swing.*;
import br.com.biblioteca.util.InicializadorBanco;

public class TelaPrincipal extends JFrame {

  public TelaPrincipal() {
    // 1. Configurações da Janela
    configurarJanela();

    // 2. Inicializa o Banco de Dados
    InicializadorBanco.criarTabelas();

    // 3. Constrói a Barra de Menu
    setJMenuBar(construirMenuBar());
  }

  // Método auxiliar para configurar o JFrame
  private void configurarJanela() {
    setTitle("Sistema de Biblioteca - Gestão v1.0");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Centraliza
  }

  // Método principal que monta a barra inteira
  private JMenuBar construirMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // Adiciona cada "grupo" de menu separadamente
    menuBar.add(criarMenuArquivo());
    menuBar.add(criarMenuCadastros());
    menuBar.add(criarMenuMovimentacao());
    menuBar.add(criarMenuRelatorios());

    return menuBar;
  }

  // --- MÉTODOS FABRICA DE MENUS (Organização) ---

  private JMenu criarMenuArquivo() {
    JMenu menu = new JMenu("Arquivo");

    JMenuItem itemSair = new JMenuItem("Sair");
    itemSair.addActionListener(e -> System.exit(0));

    menu.add(itemSair);
    return menu;
  }

  private JMenu criarMenuCadastros() {
    JMenu menu = new JMenu("Cadastros");

    JMenuItem itemAluno = new JMenuItem("Alunos");
    JMenuItem itemObra = new JMenuItem("Obras (Títulos)");
    JMenuItem itemLivro = new JMenuItem("Exemplares (Livros)");

    // Ações de Navegação
    itemAluno.addActionListener(e -> new TelaCadastroAluno().setVisible(true));
    itemObra.addActionListener(e -> new TelaCadastroTitulo().setVisible(true));
    itemLivro.addActionListener(e -> new TelaCadastroLivro().setVisible(true));

    menu.add(itemAluno);
    menu.add(itemObra);
    menu.add(itemLivro);

    return menu;
  }

  private JMenu criarMenuMovimentacao() {
    JMenu menu = new JMenu("Movimentação");

    // Agora o itemEmprestimo é criado e usado no lugar certo!
    JMenuItem itemEmprestimo = new JMenuItem("Realizar Empréstimo");

    // Chama a tela que acabamos de criar
    itemEmprestimo.addActionListener(e -> new TelaRealizarEmprestimo().setVisible(true));

    // Futuro: Item Devolução
    JMenuItem itemDevolucao = new JMenuItem("Realizar Devolução");
    itemDevolucao.addActionListener(e -> new TelaDevolucao().setVisible(true));

    menu.add(itemEmprestimo);
    menu.add(itemDevolucao);

    return menu;
  }

  private JMenu criarMenuRelatorios() {
    JMenu menu = new JMenu("Relatórios");
    JMenuItem itemGeral = new JMenuItem("Visão Geral");

    itemGeral.addActionListener(e -> new TelaRelatorios().setVisible(true));

    menu.add(itemGeral);
    return menu;
  }

  // --- MAIN ---
  public static void main(String[] args) {
    try {
      // Tenta usar o visual nativo do sistema operacional (Windows/Linux/Mac)
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
      new TelaPrincipal().setVisible(true);
    });
  }
}
