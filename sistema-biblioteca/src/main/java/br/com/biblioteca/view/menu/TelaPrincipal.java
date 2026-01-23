package br.com.biblioteca.view.menu;

import br.com.biblioteca.view.aluno.TelaListaAluno;
import br.com.biblioteca.view.emprestimo.TelaNovoEmprestimo;
import br.com.biblioteca.view.livro.TelaListaTitulo;
import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

  public TelaPrincipal() {
    // Configurações da Janela
    setTitle("Sistema de Biblioteca");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Centraliza na tela

    // Layout Principal
    setLayout(new BorderLayout());

    // 1. Barra de Menus (Topo)
    criarMenu();

    // 2. Painel de Boas-vindas (Centro)
    JPanel panelCentro = new JPanel();
    panelCentro.setLayout(new GridBagLayout()); // Centraliza o conteúdo

    JLabel lblTitulo = new JLabel("Bem-vindo ao Sistema Bibliotecário");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
    lblTitulo.setForeground(new Color(50, 50, 150));

    panelCentro.add(lblTitulo);
    add(panelCentro, BorderLayout.CENTER);

    // 3. Barra de Status (Rodapé)
    JPanel panelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelRodape.setBorder(BorderFactory.createEtchedBorder());
    panelRodape.add(new JLabel("Banco de Dados: Conectado (SQLite)"));
    add(panelRodape, BorderLayout.SOUTH);
  }

  private void criarMenu() {
    JMenuBar menuBar = new JMenuBar();

    JMenu menuCadastros = new JMenu("Cadastros");

    JMenuItem itemAluno = new JMenuItem("Alunos");
    JMenuItem itemTitulo = new JMenuItem("Títulos (Livros)");
    JMenuItem itemAutor = new JMenuItem("Autores");
    JMenuItem itemArea = new JMenuItem("Áreas (Gêneros)");

    // --- AÇÕES CADASTROS ---

    // 1. Tela de Alunos
    itemAluno.addActionListener(e -> {
      new TelaListaAluno().setVisible(true);
    });

    // 2. Tela de Títulos
    itemTitulo.addActionListener(e -> {
      new TelaListaTitulo().setVisible(true);
    });

    // 3. Tela de Autores
    itemAutor.addActionListener(e -> {
      new br.com.biblioteca.view.livro.TelaListaAutor().setVisible(true);
    });

    itemArea.addActionListener(e -> {
      new br.com.biblioteca.view.livro.TelaListaArea().setVisible(true);
    });

    menuCadastros.add(itemAluno);
    menuCadastros.add(itemTitulo);
    menuCadastros.add(itemAutor);
    menuCadastros.add(itemArea);

    // --- MENU CIRCULAÇÃO ---
    JMenu menuCirculacao = new JMenu("Circulação");

    JMenuItem itemEmprestimo = new JMenuItem("Novo Empréstimo");
    JMenuItem itemDevolucao = new JMenuItem("Devolução");
    JMenuItem itemReservas = new JMenuItem("Gerenciar Reservas");

    // --- AÇÕES CIRCULAÇÃO ---

    // 4. Tela de Novo Empréstimo
    itemEmprestimo.addActionListener(e -> {
      new TelaNovoEmprestimo().setVisible(true);
    });

    // 5. Devolução
    itemDevolucao.addActionListener(e -> {
      new br.com.biblioteca.view.emprestimo.TelaDevolucao().setVisible(true);
    });

    // 6. Reservas
    itemReservas.addActionListener(e -> {
      new br.com.biblioteca.view.reserva.TelaListaReserva().setVisible(true);
    });

    // TelaPrincipal.java
    itemAutor.addActionListener(e -> {
      new br.com.biblioteca.view.livro.TelaListaAutor().setVisible(true);
    });

    menuCirculacao.add(itemEmprestimo);
    menuCirculacao.add(itemDevolucao);
    menuCirculacao.addSeparator();
    menuCirculacao.add(itemReservas);

    // --- MENU AJUDA ---
    JMenu menuAjuda = new JMenu("Ajuda");
    JMenuItem itemSobre = new JMenuItem("Sobre");
    itemSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
        "Sistema Biblioteca\nDesenvolvido em Java/Swing\nVersão 1.0"));
    menuAjuda.add(itemSobre);

    // Adiciona menus à barra
    menuBar.add(menuCadastros);
    menuBar.add(menuCirculacao);
    menuBar.add(menuAjuda);

    setJMenuBar(menuBar);
  }
}
