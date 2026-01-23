package br.com.biblioteca.view.emprestimo;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.DebitoDAO;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.ReservaDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemEmprestimo;
import br.com.biblioteca.model.Livro;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TelaNovoEmprestimo extends JFrame {

  // Componentes de Tela
  private JTextField txtRaAluno;
  private JLabel lblNomeAluno;
  private JTextField txtIdLivro;
  private JTable tabelaItens;
  private DefaultTableModel tableModel;
  private JLabel lblTotalLivros;

  // Dados em Memória (O "Carrinho" do Empréstimo)
  private Aluno alunoSelecionado;
  private List<Livro> livrosNoCarrinho;

  // DAOs
  private AlunoDAO alunoDAO;
  private LivroDAO livroDAO;
  private EmprestimoDAO emprestimoDAO;
  private ReservaDAO reservaDAO;
  private DebitoDAO debitoDAO;

  public TelaNovoEmprestimo() {
    super("Novo Empréstimo");
    // Inicializa listas e DAOs
    this.livrosNoCarrinho = new ArrayList<>();
    this.alunoDAO = new AlunoDAO();
    this.livroDAO = new LivroDAO();
    this.emprestimoDAO = new EmprestimoDAO();
    this.reservaDAO = new ReservaDAO();
    this.debitoDAO = new DebitoDAO();

    setSize(800, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // --- 1. PAINEL SUPERIOR (Identificação do Aluno) ---
    JPanel panelAluno = new JPanel(new GridBagLayout());
    panelAluno.setBorder(BorderFactory.createTitledBorder("1. Dados do Aluno"));

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.HORIZONTAL;

    c.gridx = 0;
    c.gridy = 0;
    panelAluno.add(new JLabel("RA do Aluno:"), c);

    txtRaAluno = new JTextField(15);
    c.gridx = 1;
    panelAluno.add(txtRaAluno, c);

    JButton btnBuscarAluno = new JButton("Buscar");
    c.gridx = 2;
    panelAluno.add(btnBuscarAluno, c);

    lblNomeAluno = new JLabel("Nenhum aluno selecionado");
    lblNomeAluno.setFont(new Font("Arial", Font.BOLD, 14));
    lblNomeAluno.setForeground(Color.RED);
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 2;
    panelAluno.add(lblNomeAluno, c);

    add(panelAluno, BorderLayout.NORTH);

    // --- 2. PAINEL CENTRAL (Adicionar Livros) ---
    JPanel panelCentral = new JPanel(new BorderLayout());
    panelCentral.setBorder(BorderFactory.createTitledBorder("2. Livros do Empréstimo"));

    // Barra de input do livro
    JPanel panelInputLivro = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelInputLivro.add(new JLabel("ID do Exemplar (Código de Barras):"));
    txtIdLivro = new JTextField(10);
    JButton btnAdicionarLivro = new JButton("Adicionar Livro");
    panelInputLivro.add(txtIdLivro);
    panelInputLivro.add(btnAdicionarLivro);

    panelCentral.add(panelInputLivro, BorderLayout.NORTH);

    // Tabela de itens
    String[] colunas = { "ID", "Título da Obra", "ISBN", "Prazo (Dias)" };
    tableModel = new DefaultTableModel(colunas, 0); // Modelo simples sem classe dedicada pra agilizar
    tabelaItens = new JTable(tableModel);
    panelCentral.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

    add(panelCentral, BorderLayout.CENTER);

    // --- 3. PAINEL INFERIOR (Finalizar) ---
    JPanel panelRodape = new JPanel(new BorderLayout());
    panelRodape.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    lblTotalLivros = new JLabel("Total de Livros: 0");
    lblTotalLivros.setFont(new Font("Arial", Font.BOLD, 16));

    JButton btnFinalizar = new JButton("FINALIZAR EMPRÉSTIMO");
    btnFinalizar.setFont(new Font("Arial", Font.BOLD, 14));
    btnFinalizar.setBackground(new Color(50, 150, 50)); // Verde
    btnFinalizar.setForeground(Color.WHITE);

    panelRodape.add(lblTotalLivros, BorderLayout.WEST);
    panelRodape.add(btnFinalizar, BorderLayout.EAST);

    add(panelRodape, BorderLayout.SOUTH);

    // --- AÇÕES (LISTENERS) ---

    // 1. Buscar Aluno por RA
    btnBuscarAluno.addActionListener(e -> buscarAluno());
    txtRaAluno.addActionListener(e -> buscarAluno()); // Funciona ao dar Enter no campo

    // 2. Adicionar Livro
    btnAdicionarLivro.addActionListener(e -> adicionarLivro());
    txtIdLivro.addActionListener(e -> adicionarLivro()); // Funciona ao dar Enter no campo

    // 3. Finalizar
    btnFinalizar.addActionListener(e -> finalizarEmprestimo());
  }

  private void buscarAluno() {
    String ra = txtRaAluno.getText().trim();
    if (ra.isEmpty())
      return;

    try {
      Aluno a = alunoDAO.buscarPorRa(ra);

      if (a == null) {
        this.alunoSelecionado = null;
        lblNomeAluno.setText("Aluno não encontrado!");
        lblNomeAluno.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, "Erro: Aluno não cadastrado no sistema.");
        return;
      }

      if (debitoDAO.possuiDebitoPendente(a.getId())) {
        this.alunoSelecionado = null;
        lblNomeAluno.setText("ALUNO BLOQUEADO (DÉBITO)");
        lblNomeAluno.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this,
            "Operação Negada: O aluno possui pendências financeiras.\n" +
                "Regularize a situação antes de realizar novos empréstimos.");
        return; // (4.a.2) Finaliza caso de uso
      }

      this.alunoSelecionado = a;
      lblNomeAluno.setText(a.getNome() + " (RA: " + a.getRa() + ")");
      lblNomeAluno.setForeground(new Color(0, 100, 0));
      txtIdLivro.requestFocus();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao buscar aluno: " + e.getMessage());
    }
  }

  private void adicionarLivro() {
    String idStr = txtIdLivro.getText().trim();
    if (idStr.isEmpty())
      return;

    try {
      int id = Integer.parseInt(idStr);

      // Verifica se já não está na lista (Carrinho)
      for (Livro l : livrosNoCarrinho) {
        if (l.getId() == id) {
          JOptionPane.showMessageDialog(this, "Este livro já está na lista!");
          txtIdLivro.setText("");
          return;
        }
      }

      // Busca no banco
      Livro livro = livroDAO.buscarPorId(id);

      if (livro == null) {
        JOptionPane.showMessageDialog(this, "Livro ID " + id + " não existe!");
        return;
      }

      if (livro.isExemplarBiblioteca()) {
        JOptionPane.showMessageDialog(this,
            "Bloqueio: Este exemplar é de CONSULTA LOCAL e não pode ser emprestado.");
        return;
      }

      if (!livro.isDisponivel()) {
        JOptionPane.showMessageDialog(this, "Livro ID " + id + " já está emprestado!");
        return;
      }

      if (reservaDAO.isLivroReservado(livro.getTitulo().getId())) {
        JOptionPane.showMessageDialog(this,
            "Bloqueio: Este livro está RESERVADO para outro aluno.\n" +
                "Não é possível realizar o empréstimo.");
        return;
      }

      int idTitulo = livro.getTitulo().getId();
      int idAluno = this.alunoSelecionado.getId();

      int qtdReservas = reservaDAO.contarReservasAtivas(idTitulo);
      int qtdDisponiveis = livroDAO.contarDisponiveisPorTitulo(idTitulo);

      if (qtdReservas > 0) {

        if (qtdDisponiveis <= qtdReservas) {

          boolean alunoEhDonoDaReserva = reservaDAO.alunoPossuiReserva(idTitulo, idAluno);

          if (!alunoEhDonoDaReserva) {
            JOptionPane.showMessageDialog(this,
                "BLOQUEIO DE RESERVA:\n" +
                    "Existem " + qtdReservas + " reservas para apenas " + qtdDisponiveis + " exemplares disponíveis.\n"
                    +
                    "A prioridade é de quem reservou.");
            return;
          } else {
            JOptionPane.showMessageDialog(this, "Nota: Reserva identificada para este aluno. Liberando exemplar!");
          }
        }
      }

      // Adiciona ao carrinho
      livrosNoCarrinho.add(livro);

      // Adiciona na tabela visual
      tableModel.addRow(new Object[] {
          livro.getId(),
          livro.getTitulo().getNome(),
          livro.getTitulo().getIsbn(),
          livro.getTitulo().getPrazo() + " dias"
      });

      lblTotalLivros.setText("Total de Livros: " + livrosNoCarrinho.size());
      txtIdLivro.setText(""); // Limpa campo
      txtIdLivro.requestFocus(); // Mantém foco para bipar o próximo

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "ID inválido (digite apenas números).");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
    }
  }

  private void finalizarEmprestimo() {
    // Validações Finais
    if (alunoSelecionado == null) {
      JOptionPane.showMessageDialog(this, "Selecione um aluno!");
      txtRaAluno.requestFocus();
      return;
    }
    if (livrosNoCarrinho.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Adicione pelo menos um livro!");
      txtIdLivro.requestFocus();
      return;
    }

    try {
      // 1. Cria o objeto Emprestimo (Cabeçalho)
      Emprestimo emp = new Emprestimo();
      emp.setAluno(alunoSelecionado);
      emp.setDataEmprestimo(new Date()); // Hoje

      // Lógica: Data Prevista Geral será a do livro com maior prazo? Ou menor?
      // Vamos simplificar: Pega a data de hoje + prazo do primeiro livro
      // Num sistema real, cada item tem sua data, mas o cabeçalho precisa de uma
      // referência.
      int diasPrazo = livrosNoCarrinho.get(0).getTitulo().getPrazo();
      emp.setDataPrevista(calcularDataDevolucao(diasPrazo));

      // 2. Cria os Itens do Emprestimo
      List<ItemEmprestimo> itens = new ArrayList<>();
      for (Livro l : livrosNoCarrinho) {
        ItemEmprestimo item = new ItemEmprestimo();
        item.setLivro(l);
        // Calcula data individual baseada no título daquele livro
        item.setDataPrevista(calcularDataDevolucao(l.getTitulo().getPrazo()));
        itens.add(item);
      }
      emp.setItens(itens);

      // 3. Salva no Banco (Transação Atômica do DAO)
      emprestimoDAO.salvar(emp);

      // 4. O Sistema Imprime os dados (Simulação)
      imprimirComprovante(emp);

      // 5. Sucesso!
      JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!\nID: " + emp.getId());
      dispose(); // Fecha tela

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro fatal ao salvar empréstimo: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // Método Utilitário para somar dias na data
  private Date calcularDataDevolucao(int dias) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DAY_OF_MONTH, dias);
    return c.getTime();
  }

  private void imprimirComprovante(Emprestimo emp) {
    StringBuilder recibo = new StringBuilder();
    recibo.append("============== RECIBO DE EMPRÉSTIMO ==============\n");
    recibo.append("Biblioteca Sênior - Data: ").append(new java.util.Date()).append("\n");
    recibo.append("--------------------------------------------------\n");
    recibo.append("Aluno: ").append(emp.getAluno().getNome()).append("\n");
    recibo.append("RA:    ").append(emp.getAluno().getRa()).append("\n");
    recibo.append("--------------------------------------------------\n");
    recibo.append("LIVROS EMPRESTADOS:\n");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    for (ItemEmprestimo item : emp.getItens()) {
      recibo.append(String.format("- [ID %d] %s\n  Devolução prevista: %s\n",
          item.getLivro().getId(),
          item.getLivro().getTitulo().getNome(),
          sdf.format(item.getDataPrevista())));
    }
    recibo.append("==================================================\n");
    recibo.append("Guarde este comprovante!");

    // Mostra o recibo num popup scrollável simulando impressão
    JTextArea area = new JTextArea(recibo.toString());
    area.setEditable(false);
    JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(350, 400));

    JOptionPane.showMessageDialog(this, scroll, "Imprimindo...", JOptionPane.INFORMATION_MESSAGE);
  }

}
