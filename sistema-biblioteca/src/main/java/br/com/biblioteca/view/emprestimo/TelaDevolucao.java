package br.com.biblioteca.view.emprestimo;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.DevolucaoDAO;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Devolucao;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemDevolucao;
import br.com.biblioteca.model.ItemEmprestimo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TelaDevolucao extends JFrame {

  // Componentes
  private JTextField txtIdEmprestimo;
  private JLabel lblInfoAluno;
  private JLabel lblStatus;
  private JTable tabelaItens;
  private DefaultTableModel tableModel;
  private JButton btnConfirmar;

  // Dados
  private Emprestimo emprestimoAtual;
  private Devolucao devolucaoCalculada; // Objeto temporário antes de salvar

  // DAOs
  private EmprestimoDAO emprestimoDAO;
  private DevolucaoDAO devolucaoDAO;
  private AlunoDAO alunoDAO;

  public TelaDevolucao() {
    super("Devolução de Livros");
    this.emprestimoDAO = new EmprestimoDAO();
    this.devolucaoDAO = new DevolucaoDAO();
    this.alunoDAO = new AlunoDAO();

    setSize(850, 550);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // --- 1. PAINEL DE BUSCA ---
    JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Empréstimo"));

    panelBusca.add(new JLabel("ID do Empréstimo:"));
    txtIdEmprestimo = new JTextField(10);
    JButton btnBuscar = new JButton("Buscar");

    panelBusca.add(txtIdEmprestimo);
    panelBusca.add(btnBuscar);

    // Info do Aluno (Aparece após buscar)
    lblInfoAluno = new JLabel(" ");
    lblInfoAluno.setFont(new Font("Arial", Font.BOLD, 12));
    lblInfoAluno.setForeground(Color.BLUE);
    panelBusca.add(Box.createHorizontalStrut(20)); // Espaço
    panelBusca.add(lblInfoAluno);

    add(panelBusca, BorderLayout.NORTH);

    // --- 2. TABELA DE ITENS (LIVROS) ---
    // Colunas: ID Livro | Título | Data Prevista | Dias Atraso | Multa Calc.
    String[] colunas = { "ID Livro", "Título", "Data Prevista", "Situação", "Multa (R$)" };
    tableModel = new DefaultTableModel(colunas, 0);
    tabelaItens = new JTable(tableModel);

    add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

    // --- 3. RODAPÉ (RESUMO E AÇÃO) ---
    JPanel panelRodape = new JPanel(new BorderLayout());
    panelRodape.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    lblStatus = new JLabel("Total Multa: R$ 0,00");
    lblStatus.setFont(new Font("Arial", Font.BOLD, 18));
    lblStatus.setForeground(Color.RED);

    btnConfirmar = new JButton("CONFIRMAR DEVOLUÇÃO");
    btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
    btnConfirmar.setBackground(new Color(0, 100, 0));
    btnConfirmar.setForeground(Color.WHITE);
    btnConfirmar.setEnabled(false); // Só habilita se tiver emprestimo carregado

    panelRodape.add(lblStatus, BorderLayout.WEST);
    panelRodape.add(btnConfirmar, BorderLayout.EAST);

    add(panelRodape, BorderLayout.SOUTH);

    // --- AÇÕES ---
    btnBuscar.addActionListener(e -> buscarEmprestimo());
    txtIdEmprestimo.addActionListener(e -> buscarEmprestimo());

    btnConfirmar.addActionListener(e -> finalizarDevolucao());
  }

  private void buscarEmprestimo() {
    String termoBusca = txtIdEmprestimo.getText().trim();
    if (termoBusca.isEmpty())
      return;

    try {
      Emprestimo emp = emprestimoDAO.buscarEmprestimoAbertoPorRa(termoBusca);

      if (emp == null) {
        JOptionPane.showMessageDialog(this,
            "Nenhum empréstimo pendente encontrado para este Aluno (RA: " + termoBusca + ").");
        limparTela();
        return;
      }

      // Verifica se já foi devolvido (Lógica: se todos os itens já têm
      // data_devolucao)
      boolean todosDevolvidos = true;
      for (ItemEmprestimo item : emp.getItens()) {
        if (item.getDataDevolucao() == null) {
          todosDevolvidos = false;
          break;
        }
      }

      if (todosDevolvidos) {
        JOptionPane.showMessageDialog(this, "Este empréstimo já foi totalmente finalizado/devolvido!");
        limparTela();
        return;
      }

      this.emprestimoAtual = emp;
      exibirDados(emp);
      btnConfirmar.setEnabled(true);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao buscar: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void exibirDados(Emprestimo emp) {
    lblInfoAluno.setText("Aluno: " + emp.getAluno().getNome() + " | RA: " + emp.getAluno().getRa());

    tableModel.setRowCount(0); // Limpa tabela

    // Prepara o objeto Devolucao (Virtual) para calcular multas
    this.devolucaoCalculada = new Devolucao();
    this.devolucaoCalculada.setEmprestimo(emp);
    this.devolucaoCalculada.setDataDevolucao(new Date()); // Data de Hoje

    List<ItemDevolucao> itensDev = new ArrayList<>();
    double totalMulta = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    for (ItemEmprestimo itemEmp : emp.getItens()) {
      // Só mostra itens que AINDA NÃO foram devolvidos
      if (itemEmp.getDataDevolucao() == null) {

        // Calcula dias de atraso
        long diff = new Date().getTime() - itemEmp.getDataPrevista().getTime();
        long diasAtraso = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (diasAtraso < 0)
          diasAtraso = 0; // Se entregou antes, atraso é 0

        // Regra de Negócio: R$ 2,00 por dia de atraso
        double multaItem = diasAtraso * 2.00;
        totalMulta += multaItem;

        // Cria item devolução para salvar depois
        ItemDevolucao itemDev = new ItemDevolucao();
        itemDev.setItemEmprestimo(itemEmp);
        itemDev.setDataDevolucao(new Date());
        itemDev.setDiasAtraso((int) diasAtraso);
        itemDev.setValorMulta(multaItem);
        itensDev.add(itemDev);

        // Adiciona na tabela visual
        String situacao = diasAtraso > 0 ? diasAtraso + " dias de atraso" : "No prazo";
        tableModel.addRow(new Object[] {
            itemEmp.getLivro().getId(),
            itemEmp.getLivro().getTitulo().getNome(),
            sdf.format(itemEmp.getDataPrevista()),
            situacao,
            String.format("R$ %.2f", multaItem)
        });
      }
    }

    this.devolucaoCalculada.setItens(itensDev);
    this.devolucaoCalculada.setMultaTotal(totalMulta);

    lblStatus.setText(String.format("Total Multa: R$ %.2f", totalMulta));
    if (totalMulta > 0)
      lblStatus.setForeground(Color.RED);
    else
      lblStatus.setForeground(new Color(0, 100, 0)); // Verde
  }

  private void finalizarDevolucao() {
    if (emprestimoAtual == null)
      return;

    int confirm = JOptionPane.showConfirmDialog(this,
        "Confirma a devolução e recebimento dos livros?",
        "Finalizar", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        // O método salvar do DAO faz tudo:
        // Salva Devolução -> Atualiza Itens -> Libera Estoque -> Gera Débito
        devolucaoDAO.salvar(devolucaoCalculada);

        JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
        limparTela();

      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private void limparTela() {
    txtIdEmprestimo.setText("");
    lblInfoAluno.setText("");
    lblStatus.setText("Total Multa: R$ 0,00");
    tableModel.setRowCount(0);
    btnConfirmar.setEnabled(false);
    emprestimoAtual = null;
    devolucaoCalculada = null;
    txtIdEmprestimo.requestFocus();
  }
}
