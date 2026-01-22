package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Titulo;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaTitulo extends JFrame {

  private JTable tabela;
  private TituloTableModel tableModel;
  private TituloDAO dao;

  public TelaListaTitulo() {
    super("Gestão de Títulos (Obras)");
    this.dao = new TituloDAO();

    setSize(900, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Topo
    JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnNovo = new JButton("Novo Título");
    JButton btnEditar = new JButton("Editar");
    JButton btnExcluir = new JButton("Excluir");
    JButton btnAtualizar = new JButton("Atualizar");
    JButton btnExemplares = new JButton("Gerenciar Exemplares");

    panelTopo.add(btnNovo);
    panelTopo.add(btnEditar);
    panelTopo.add(btnExcluir);
    panelTopo.add(btnAtualizar);
    panelTopo.add(btnExemplares);
    add(panelTopo, BorderLayout.NORTH);

    // Centro
    tabela = new JTable();
    add(new JScrollPane(tabela), BorderLayout.CENTER);

    carregarTabela();

    // Ações
    btnNovo.addActionListener(e -> {
      new TelaCadastroTitulo(this, null).setVisible(true);
      carregarTabela();
    });

    btnEditar.addActionListener(e -> {
      int linha = tabela.getSelectedRow();
      if (linha >= 0) {
        Titulo t = tableModel.getTituloAt(linha);
        new TelaCadastroTitulo(this, t).setVisible(true);
        carregarTabela();
      } else {
        JOptionPane.showMessageDialog(this, "Selecione um título.");
      }
    });

    btnExcluir.addActionListener(e -> {
      int linha = tabela.getSelectedRow();
      if (linha >= 0) {
        Titulo t = tableModel.getTituloAt(linha);
        if (JOptionPane.showConfirmDialog(this, "Excluir " + t.getNome() + "?") == JOptionPane.YES_OPTION) {
          try {
            dao.excluir(t.getId());
            carregarTabela();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
          }
        }
      }
    });

    btnExemplares.addActionListener(e -> {
      int linha = tabela.getSelectedRow();
      if (linha >= 0) {
        // Pega o título selecionado na grade
        Titulo titulo = tableModel.getTituloAt(linha);

        // Abre a tela de exemplares passando esse título
        new TelaGerenciarExemplares(this, titulo).setVisible(true);
      } else {
        JOptionPane.showMessageDialog(this, "Selecione um título para ver seus exemplares.");
      }
    });

    btnAtualizar.addActionListener(e -> carregarTabela());
  }

  private void carregarTabela() {
    try {
      List<Titulo> lista = dao.listarTodos();
      tableModel = new TituloTableModel(lista);
      tabela.setModel(tableModel);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
