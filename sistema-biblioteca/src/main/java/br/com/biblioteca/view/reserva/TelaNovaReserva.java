package br.com.biblioteca.view.reserva;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.ReservaDAO;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Reserva;
import br.com.biblioteca.model.Titulo;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaNovaReserva extends JDialog {

  private JTextField txtRaAluno;
  private JLabel lblNomeAluno;
  private JComboBox<Titulo> comboTitulos; // Seleção fácil pelo nome

  private Aluno alunoSelecionado;
  private AlunoDAO alunoDAO;
  private TituloDAO tituloDAO;
  private ReservaDAO reservaDAO;

  public TelaNovaReserva(Frame owner) {
    super(owner, true);
    setTitle("Nova Reserva");
    setSize(500, 350);
    setLocationRelativeTo(owner);
    setLayout(new GridBagLayout());

    this.alunoDAO = new AlunoDAO();
    this.tituloDAO = new TituloDAO();
    this.reservaDAO = new ReservaDAO();

    montarTela();
  }

  private void montarTela() {
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 10, 10, 10);
    c.fill = GridBagConstraints.HORIZONTAL;

    // --- 1. Seleção de Aluno ---
    c.gridx = 0;
    c.gridy = 0;
    add(new JLabel("RA do Aluno:"), c);

    JPanel panelAluno = new JPanel(new BorderLayout());
    txtRaAluno = new JTextField(15);
    JButton btnBuscar = new JButton("🔍");
    panelAluno.add(txtRaAluno, BorderLayout.CENTER);
    panelAluno.add(btnBuscar, BorderLayout.EAST);

    c.gridx = 1;
    add(panelAluno, c);

    // Label para mostrar nome do aluno
    c.gridx = 1;
    c.gridy = 1;
    lblNomeAluno = new JLabel("...");
    lblNomeAluno.setFont(new Font("Arial", Font.BOLD, 12));
    lblNomeAluno.setForeground(Color.BLUE);
    add(lblNomeAluno, c);

    // --- 2. Seleção de Título ---
    c.gridx = 0;
    c.gridy = 2;
    add(new JLabel("Título (Obra):"), c);

    comboTitulos = new JComboBox<>();
    carregarTitulos();
    c.gridx = 1;
    add(comboTitulos, c);

    // --- 3. Botões ---
    JPanel panelBtn = new JPanel();
    JButton btnSalvar = new JButton("Confirmar Reserva");
    JButton btnCancelar = new JButton("Cancelar");

    btnSalvar.setBackground(new Color(50, 150, 50));
    btnSalvar.setForeground(Color.WHITE);

    panelBtn.add(btnSalvar);
    panelBtn.add(btnCancelar);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    add(panelBtn, c);

    // --- AÇÕES ---
    btnBuscar.addActionListener(e -> buscarAluno());
    txtRaAluno.addActionListener(e -> buscarAluno());

    btnCancelar.addActionListener(e -> dispose());

    btnSalvar.addActionListener(e -> salvarReserva());
  }

  private void carregarTitulos() {
    try {
      // Traz todos os títulos para o combo
      List<Titulo> lista = tituloDAO.listarTodos();
      comboTitulos.removeAllItems();
      for (Titulo t : lista) {
        // Para aparecer o nome, lembre-se que a classe Titulo precisa do toString()
        // Se não tiver, o combo mostra memória.
        comboTitulos.addItem(t);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao carregar títulos: " + e.getMessage());
    }
  }

  private void buscarAluno() {
    try {
      String ra = txtRaAluno.getText();
      this.alunoSelecionado = alunoDAO.buscarPorRa(ra);

      if (alunoSelecionado != null) {
        lblNomeAluno.setText(alunoSelecionado.getNome());
        lblNomeAluno.setForeground(new Color(0, 100, 0));
      } else {
        lblNomeAluno.setText("Aluno não encontrado!");
        lblNomeAluno.setForeground(Color.RED);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void salvarReserva() {
    if (alunoSelecionado == null) {
      JOptionPane.showMessageDialog(this, "Selecione um aluno primeiro.");
      return;
    }

    Titulo titulo = (Titulo) comboTitulos.getSelectedItem();
    if (titulo == null) {
      JOptionPane.showMessageDialog(this, "Selecione um título.");
      return;
    }

    try {
      // Cria a reserva
      Reserva r = new Reserva(alunoSelecionado, titulo);
      reservaDAO.salvar(r);

      JOptionPane.showMessageDialog(this, "Reserva realizada com sucesso!");
      dispose();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao reservar: " + e.getMessage());
    }
  }
}
