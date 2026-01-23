package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AutorDAO;
import br.com.biblioteca.model.Autor;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaAutor extends JFrame {

  private JTable tabela;
  private AutorDAO dao;

  public TelaListaAutor() {
    super("Gestão de Autores");
    this.dao = new AutorDAO();
    setSize(600, 400);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnNovo = new JButton("Novo Autor");
    JButton btnEditar = new JButton("Editar");
    JButton btnExcluir = new JButton("Excluir");
    topo.add(btnNovo);
    topo.add(btnEditar);
    topo.add(btnExcluir);
    add(topo, BorderLayout.NORTH);

    tabela = new JTable();
    add(new JScrollPane(tabela), BorderLayout.CENTER);
    carregar();

    btnNovo.addActionListener(e -> {
      new TelaCadastroAutor(this, null).setVisible(true);
      carregar();
    });

    btnEditar.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Autor a = ((AutorTableModel) tabela.getModel()).getAutorAt(row);
        new TelaCadastroAutor(this, a).setVisible(true);
        carregar();
      }
    });

    btnExcluir.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Autor a = ((AutorTableModel) tabela.getModel()).getAutorAt(row);
        try {
          dao.excluir(a.getId());
          carregar();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
      }
    });
  }

  private void carregar() {
    try {
      List<Autor> lista = dao.listarTodos();
      tabela.setModel(new AutorTableModel(lista));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
