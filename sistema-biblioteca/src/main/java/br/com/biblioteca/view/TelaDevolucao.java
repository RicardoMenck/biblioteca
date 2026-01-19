package br.com.biblioteca.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.model.ItemEmprestimo;

public class TelaDevolucao extends JFrame {

  private JTextField txtRaAluno;
  private JButton btnPesquisar;
  private JTable tabelaPendencias;
  private DefaultTableModel tableModel;
  private JButton btnDevolver;

  // Guarda a lista original para recuperarmos os IDs
  private List<ItemEmprestimo> listaAtual;

  public TelaDevolucao() {
    setTitle("Realizar Devolução");
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    initUI();
  }

  private void initUI() {
    JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // --- TOPO: Pesquisa ---
    JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTopo.add(new JLabel("RA do Aluno:"));
    txtRaAluno = new JTextField(15);
    btnPesquisar = new JButton("Buscar Pendências");

    panelTopo.add(txtRaAluno);
    panelTopo.add(btnPesquisar);

    painelPrincipal.add(panelTopo, BorderLayout.NORTH);

    // --- CENTRO: Tabela ---
    // Colunas: ID Item, Nome do Livro, Data Prevista
    String[] colunas = { "ID", "Obra / Livro", "Data Prevista Devolução" };
    tableModel = new DefaultTableModel(colunas, 0) {
      @Override // Bloqueia edição das células
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tabelaPendencias = new JTable(tableModel);
    JScrollPane scroll = new JScrollPane(tabelaPendencias);
    painelPrincipal.add(scroll, BorderLayout.CENTER);

    // --- RODAPÉ: Botão Devolver ---
    JPanel panelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnDevolver = new JButton("Confirmar Devolução");
    btnDevolver.setBackground(new Color(220, 20, 60)); // Vermelho Carmim
    btnDevolver.setForeground(Color.WHITE);
    btnDevolver.setEnabled(false); // Só habilita se selecionar algo

    panelRodape.add(btnDevolver);
    painelPrincipal.add(panelRodape, BorderLayout.SOUTH);

    add(painelPrincipal);

    // --- AÇÕES ---
    btnPesquisar.addActionListener(e -> pesquisarPendencias());

    // Habilita botão devolver quando clica na tabela
    tabelaPendencias.getSelectionModel().addListSelectionListener(e -> {
      btnDevolver.setEnabled(tabelaPendencias.getSelectedRow() >= 0);
    });

    btnDevolver.addActionListener(e -> realizarDevolucao());
  }

  private void pesquisarPendencias() {
    String ra = txtRaAluno.getText().trim();
    if (ra.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Digite o RA do aluno.");
      return;
    }

    try {
      EmprestimoDAO dao = new EmprestimoDAO();
      listaAtual = dao.buscarPendenciasPorAluno(ra);

      // Limpa tabela
      tableModel.setRowCount(0);
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

      if (listaAtual.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nenhuma pendência encontrada para este aluno.");
        return;
      }

      for (ItemEmprestimo item : listaAtual) {
        Object[] linha = {
            item.getId(),
            item.getLivro().getTitulo().getNome(),
            (item.getDataDevolucao() != null) ? sdf.format(item.getDataDevolucao()) : "Sem data"
        };
        tableModel.addRow(linha);
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
    }
  }

  private void realizarDevolucao() {
    int linhaSelecionada = tabelaPendencias.getSelectedRow();
    if (linhaSelecionada == -1)
      return;

    // Recupera o objeto da lista (usando o índice da tabela)
    ItemEmprestimo item = listaAtual.get(linhaSelecionada);

    int confirm = JOptionPane.showConfirmDialog(this,
        "Confirma a devolução do livro: " + item.getLivro().getTitulo().getNome() + "?",
        "Confirmar Devolução", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        // Chama DAO para dar UPDATE com a data de HOJE
        new EmprestimoDAO().registrarDevolucao(item.getId(), new Date());

        JOptionPane.showMessageDialog(this, "Livro devolvido com sucesso!");

        // Recarrega a tabela para sumir o item devolvido
        pesquisarPendencias();

      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erro ao devolver: " + e.getMessage());
      }
    }
  }
}
