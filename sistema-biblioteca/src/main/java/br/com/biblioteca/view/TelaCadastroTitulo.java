package br.com.biblioteca.view;

import javax.swing.*;
import java.awt.*;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Titulo;

public class TelaCadastroTitulo extends JFrame {

  private JTextField txtNome;
  private JTextField txtPrazo;
  private JButton btnSalvar;
  private JButton btnCancelar;

  public TelaCadastroTitulo() {
    setTitle("Cadastro de Obra (Título)");
    setSize(400, 250);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);
    initUI();
  }

  private void initUI() {
    JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // --- FORMULÁRIO ---
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    formPanel.add(new JLabel("Nome da Obra:"));
    txtNome = new JTextField();
    formPanel.add(txtNome);

    formPanel.add(new JLabel("Prazo (dias):"));
    txtPrazo = new JTextField();
    // Dica de UX: Tooltip para ajudar o usuário
    txtPrazo.setToolTipText("Ex: 7 para uma semana, 14 para duas semanas");
    formPanel.add(txtPrazo);

    painelPrincipal.add(formPanel, BorderLayout.CENTER);

    // --- BOTÕES ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnCancelar = new JButton("Cancelar");
    btnSalvar = new JButton("Salvar");

    btnSalvar.setBackground(new Color(60, 179, 113));
    btnSalvar.setForeground(Color.WHITE);

    buttonPanel.add(btnCancelar);
    buttonPanel.add(btnSalvar);
    painelPrincipal.add(buttonPanel, BorderLayout.SOUTH);

    add(painelPrincipal);

    // --- AÇÕES ---
    btnCancelar.addActionListener(e -> dispose());
    btnSalvar.addActionListener(e -> salvarTitulo());
  }

  private void salvarTitulo() {
    // 1. Validação
    if (txtNome.getText().trim().isEmpty() || txtPrazo.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      // 2. Coleta e Converte
      String nome = txtNome.getText().trim();
      int prazo = Integer.parseInt(txtPrazo.getText().trim()); // Pode dar erro se digitar letra

      // 3. Cria Objeto
      Titulo titulo = new Titulo();
      titulo.setNome(nome);
      titulo.setPrazo(prazo);

      // 4. Salva via DAO
      new TituloDAO().salvar(titulo);

      // 5. Feedback
      JOptionPane.showMessageDialog(this, "Obra cadastrada com sucesso!");

      // Limpa
      txtNome.setText("");
      txtPrazo.setText("");
      txtNome.requestFocus();

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "O campo Prazo deve conter apenas números inteiros.", "Erro de Formato",
          JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }
}
