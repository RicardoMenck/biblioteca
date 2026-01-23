package br.com.biblioteca.view.reserva;

import br.com.biblioteca.dao.ReservaDAO;
import br.com.biblioteca.model.Reserva;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaReserva extends JFrame {

  private JTable tabela;
  private ReservaDAO dao;

  public TelaListaReserva() {
    super("Gestão de Reservas (Fila de Espera)");
    this.dao = new ReservaDAO();

    setSize(800, 500);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Topo
    JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton btnNova = new JButton("Nova Reserva");
    JButton btnCancelar = new JButton("Cancelar Selecionada");
    JButton btnAtualizar = new JButton("Atualizar Lista");

    topo.add(btnNova);
    topo.add(btnCancelar);
    topo.add(btnAtualizar);
    add(topo, BorderLayout.NORTH);

    // Centro
    tabela = new JTable();
    add(new JScrollPane(tabela), BorderLayout.CENTER);

    carregarTabela();

    // Ações
    btnNova.addActionListener(e -> {
      new TelaNovaReserva(this).setVisible(true);
      carregarTabela();
    });

    btnCancelar.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Reserva r = ((ReservaTableModel) tabela.getModel()).getReservaAt(row);
        int opt = JOptionPane.showConfirmDialog(this, "Deseja cancelar a reserva de " + r.getAluno().getNome() + "?");
        if (opt == JOptionPane.YES_OPTION) {
          try {
            dao.cancelarReserva(r.getId());
            carregarTabela();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
          }
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selecione uma reserva.");
      }
    });

    btnAtualizar.addActionListener(e -> carregarTabela());
  }

  private void carregarTabela() {
    try {
      List<Reserva> lista = dao.listarReservasAtivas();
      tabela.setModel(new ReservaTableModel(lista));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
