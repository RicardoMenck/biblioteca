package br.com.biblioteca.view.menu;

import br.com.biblioteca.view.aluno.TelaListaAluno;
import br.com.biblioteca.view.emprestimo.TelaNovoEmprestimo;
import br.com.biblioteca.view.livro.TelaListaTitulo;
import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

  public TelaPrincipal() {
    // Configurações da Janela
    setTitle("Sistema de Biblioteca Sênior - v1.0");
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
    panelRodape.add(new JLabel("Banco de Dados: Conectado (SQLite) | Usuário: Admin"));
    add(panelRodape, BorderLayout.SOUTH);
  }

  private void criarMenu() {
    JMenuBar menuBar = new JMenuBar();

    // --- MENU CADASTROS ---
    JMenu menuCadastros = new JMenu("Cadastros");

    JMenuItem itemAluno = new JMenuItem("Alunos");
    JMenuItem itemTitulo = new JMenuItem("Títulos (Livros)");
    JMenuItem itemAutor = new JMenuItem("Autores"); // Ainda não temos tela específica

    // --- AÇÕES CADASTROS ---

    // 1. Tela de Alunos (Lista -> Cadastro)
    itemAluno.addActionListener(e -> {
      new TelaListaAluno().setVisible(true);
    });

    // 2. Tela de Títulos (Lista -> Cadastro -> Exemplares)
    itemTitulo.addActionListener(e -> {
      new TelaListaTitulo().setVisible(true);
    });

    // 3. Tela de Autores (Pendente)
    itemAutor.addActionListener(e -> {
      JOptionPane.showMessageDialog(this, "Tela de Autores em construção!");
      // Dica: Seria igualzinha a de Aluno
    });

    menuCadastros.add(itemAluno);
    menuCadastros.add(itemTitulo);
    menuCadastros.add(itemAutor);

    // --- MENU CIRCULAÇÃO ---
    JMenu menuCirculacao = new JMenu("Circulação");

    JMenuItem itemEmprestimo = new JMenuItem("Novo Empréstimo");
    JMenuItem itemDevolucao = new JMenuItem("Devolução");
    JMenuItem itemReservas = new JMenuItem("Gerenciar Reservas");

    // --- AÇÕES CIRCULAÇÃO ---

    // 4. Tela de Novo Empréstimo (O Core)
    itemEmprestimo.addActionListener(e -> {
      new TelaNovoEmprestimo().setVisible(true);
    });

    // 5. Devolução (Pendente)
    itemDevolucao.addActionListener(e -> {
      new br.com.biblioteca.view.emprestimo.TelaDevolucao().setVisible(true);
    });

    // 6. Reservas (Pendente)
    itemReservas.addActionListener(e -> {
      JOptionPane.showMessageDialog(this, "Tela de Reservas em construção!");
    });

    menuCirculacao.add(itemEmprestimo);
    menuCirculacao.add(itemDevolucao);
    menuCirculacao.addSeparator();
    menuCirculacao.add(itemReservas);

    // --- MENU AJUDA ---
    JMenu menuAjuda = new JMenu("Ajuda");
    JMenuItem itemSobre = new JMenuItem("Sobre");
    itemSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
        "Sistema Biblioteca Sênior\nDesenvolvido em Java/Swing\nVersão 1.0"));
    menuAjuda.add(itemSobre);

    // Adiciona menus à barra
    menuBar.add(menuCadastros);
    menuBar.add(menuCirculacao);
    menuBar.add(menuAjuda);

    setJMenuBar(menuBar);
  }
}
