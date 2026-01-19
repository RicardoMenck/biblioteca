package br.com.biblioteca.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;

public class TelaRealizarEmprestimo extends JFrame {

  private JComboBox<Aluno> cmbAluno;
  private JComboBox<Livro> cmbLivros;
  private DefaultListModel<Livro> listModelLivros; // Modelo de dados da lista visual
  private JList<Livro> listaVisual;
  private JButton btnAdicionar;
  private JButton btnFinalizar;

  // Lista temporária para guardar os livros antes de salvar no banco
  private List<Livro> livrosNoCarrinho;

  public TelaRealizarEmprestimo() {
    setTitle("Realizar Empréstimo");
    setSize(600, 450);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    livrosNoCarrinho = new ArrayList<>();
    listModelLivros = new DefaultListModel<>();

    initUI();
    carregarDados();
  }

  private void initUI() {
    JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // --- TOPO: Seleção de Aluno ---
    JPanel panelTopo = new JPanel(new GridLayout(2, 1));
    panelTopo.add(new JLabel("Selecione o Aluno:"));
    cmbAluno = new JComboBox<>();
    panelTopo.add(cmbAluno);

    painelPrincipal.add(panelTopo, BorderLayout.NORTH);

    // --- CENTRO: Seleção de Livros e Lista ---
    JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

    // Parte de adicionar livro
    JPanel panelAddLivro = new JPanel(new BorderLayout(5, 5));
    panelAddLivro.setBorder(BorderFactory.createTitledBorder("Adicionar Livros ao Empréstimo"));

    cmbLivros = new JComboBox<>();
    btnAdicionar = new JButton("Adicionar à Lista (+)");

    panelAddLivro.add(cmbLivros, BorderLayout.CENTER);
    panelAddLivro.add(btnAdicionar, BorderLayout.EAST);

    panelCentro.add(panelAddLivro, BorderLayout.NORTH);

    // A Lista Visual
    listaVisual = new JList<>(listModelLivros);
    JScrollPane scrollLista = new JScrollPane(listaVisual);
    scrollLista.setBorder(BorderFactory.createTitledBorder("Livros Selecionados (Carrinho)"));

    panelCentro.add(scrollLista, BorderLayout.CENTER);

    painelPrincipal.add(panelCentro, BorderLayout.CENTER);

    // --- RODAPÉ: Botão Finalizar ---
    btnFinalizar = new JButton("FINALIZAR EMPRÉSTIMO");
    btnFinalizar.setFont(new Font("Arial", Font.BOLD, 14));
    btnFinalizar.setBackground(new Color(60, 179, 113));
    btnFinalizar.setForeground(Color.WHITE);
    btnFinalizar.setPreferredSize(new Dimension(100, 50));

    painelPrincipal.add(btnFinalizar, BorderLayout.SOUTH);

    add(painelPrincipal);

    // --- AÇÕES ---
    btnAdicionar.addActionListener(e -> adicionarLivroAoCarrinho());
    btnFinalizar.addActionListener(e -> salvarEmprestimo());
  }

  private void carregarDados() {
    try {
      // 1. Carrega Alunos
      cmbAluno.removeAllItems();
      List<Aluno> alunos = new AlunoDAO().listarTodos();
      for (Aluno a : alunos)
        cmbAluno.addItem(a);

      // 2. Carrega Livros
      carregarLivrosDisponiveis();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
    }
  }

  // Dentro de TelaRealizarEmprestimo.java

  private void carregarLivrosDisponiveis() {
    cmbLivros.removeAllItems();

    try {
      // MUDANÇA AQUI: Chamamos o novo método inteligente
      LivroDAO dao = new LivroDAO();
      List<Livro> disponiveis = dao.listarDisponiveis();

      for (Livro l : disponiveis) {
        cmbLivros.addItem(l);
      }

      // UX Extra: Se a lista estiver vazia, avisa ou desabilita
      if (disponiveis.isEmpty()) {
        btnAdicionar.setEnabled(false);
        cmbLivros.setEnabled(false);
      } else {
        btnAdicionar.setEnabled(true);
        cmbLivros.setEnabled(true);
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao buscar livros: " + e.getMessage());
    }
  }

  private void adicionarLivroAoCarrinho() {
    Livro livroSelecionado = (Livro) cmbLivros.getSelectedItem();

    if (livroSelecionado == null)
      return;

    // Verifica se já não adicionou esse mesmo livro
    if (livrosNoCarrinho.contains(livroSelecionado)) {
      JOptionPane.showMessageDialog(this, "Este livro já está na lista!");
      return;
    }

    // Adiciona na lista lógica e visual
    livrosNoCarrinho.add(livroSelecionado);
    listModelLivros.addElement(livroSelecionado); // Usa o toString() do Livro
  }

  private void salvarEmprestimo() {
    if (livrosNoCarrinho.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Selecione pelo menos um livro!", "Aviso", JOptionPane.WARNING_MESSAGE);
      return;
    }

    Aluno alunoSelecionado = (Aluno) cmbAluno.getSelectedItem();

    try {
      // 1. Cria o objeto Emprestimo
      Emprestimo emp = new Emprestimo();
      emp.setNome(alunoSelecionado.getRa()); // Vincula o RA

      // 2. Regra de Negócio: Calcula datas
      emp.emprestar(livrosNoCarrinho);

      // 3. Persistência: Salva no Banco (Transação)
      new EmprestimoDAO().salvar(emp);

      // 4. Sucesso
      JOptionPane.showMessageDialog(this,
          "Empréstimo realizado com sucesso!\n" +
              "Devolução prevista para: " + emp.getDataPrevista());

      dispose(); // Fecha a tela

    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Erro ao finalizar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }

}
