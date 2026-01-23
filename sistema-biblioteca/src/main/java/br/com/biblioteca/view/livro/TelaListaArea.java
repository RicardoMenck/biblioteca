package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AreaDAO;
import br.com.biblioteca.model.Area;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaArea extends JFrame {

  private JTable tabela;
  private AreaDAO dao;

  public TelaListaArea() {
    super("Gestão de Áreas (Gêneros)");
    this.dao = new AreaDAO();
    setSize(600, 400);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnNovo = new JButton("Nova Área");
    JButton btnEditar = new JButton("Editar");
    JButton btnExcluir = new JButton("Excluir");
    JButton btnAtualizar = new JButton("Atualizar");

    topo.add(btnNovo);
    topo.add(btnEditar);
    topo.add(btnExcluir);
    topo.add(btnAtualizar);
    add(topo, BorderLayout.NORTH);

    tabela = new JTable();
    add(new JScrollPane(tabela), BorderLayout.CENTER);
    carregar();

    btnNovo.addActionListener(e -> {
      new TelaCadastroArea(this, null).setVisible(true);
      carregar();
    });

    btnEditar.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Area a = ((AreaTableModel) tabela.getModel()).getAreaAt(row);
        new TelaCadastroArea(this, a).setVisible(true);
        carregar();
      } else {
        JOptionPane.showMessageDialog(this, "Selecione uma área.");
      }
    });

    btnExcluir.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Area a = ((AreaTableModel) tabela.getModel()).getAreaAt(row);
        if (JOptionPane.showConfirmDialog(this, "Excluir " + a.getNome() + "?") == JOptionPane.YES_OPTION) {
          try {
            dao.excluir(a.getId());
            carregar();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro (Pode estar em uso): " + ex.getMessage());
          }
        }
      }
    });

    btnAtualizar.addActionListener(e -> carregar());
  }

  private void carregar() {
    try {
      List<Area> lista = dao.listarTodos();
      tabela.setModel(new AreaTableModel(lista));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
