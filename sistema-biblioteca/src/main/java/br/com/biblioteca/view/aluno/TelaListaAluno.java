package br.com.biblioteca.view.aluno;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.model.Aluno;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaAluno extends JFrame {

  private JTable tabela;
  private AlunoTableModel tableModel;
  private AlunoDAO dao;

  public TelaListaAluno() {
    super("Gestão de Alunos");
    this.dao = new AlunoDAO();

    setSize(800, 500);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só essa janela
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // 1. Painel de Topo (Barra de Ferramentas)
    JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnNovo = new JButton("Novo Aluno");
    JButton btnEditar = new JButton("Editar");
    JButton btnExcluir = new JButton("Excluir");
    JButton btnAtualizar = new JButton("Atualizar Lista");

    panelTopo.add(btnNovo);
    panelTopo.add(btnEditar);
    panelTopo.add(btnExcluir);
    panelTopo.add(btnAtualizar);
    add(panelTopo, BorderLayout.NORTH);

    // 2. Tabela (Centro)
    tabela = new JTable();
    JScrollPane scroll = new JScrollPane(tabela);
    add(scroll, BorderLayout.CENTER);

    // 3. Carregar Dados Iniciais
    carregarTabela();

    // --- AÇÕES DOS BOTÕES ---

    btnNovo.addActionListener(e -> {
      // Abre o formulário passando NULL (Novo cadastro)
      new TelaCadastroAluno(this, null).setVisible(true);
      carregarTabela(); // Recarrega ao voltar
    });

    btnEditar.addActionListener(e -> {
      int linha = tabela.getSelectedRow();
      if (linha >= 0) {
        Aluno alunoSelecionado = tableModel.getAlunoAt(linha);
        // Abre o formulário passando o aluno (Edição)
        new TelaCadastroAluno(this, alunoSelecionado).setVisible(true);
        carregarTabela();
      } else {
        JOptionPane.showMessageDialog(this, "Selecione um aluno para editar.");
      }
    });

    btnExcluir.addActionListener(e -> {
      int linha = tabela.getSelectedRow();
      if (linha >= 0) {
        Aluno aluno = tableModel.getAlunoAt(linha);
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir " + aluno.getNome() + "?");
        if (resposta == JOptionPane.YES_OPTION) {
          try {
            dao.excluir(aluno.getId());
            carregarTabela();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
          }
        }
      }
    });

    btnAtualizar.addActionListener(e -> carregarTabela());
  }

  private void carregarTabela() {
    try {
      List<Aluno> lista = dao.listarTodos();
      tableModel = new AlunoTableModel(lista);
      tabela.setModel(tableModel);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
    }
  }
}
