package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AreaDAO;
import br.com.biblioteca.model.Area;
import javax.swing.*;
import java.awt.*;

public class TelaCadastroArea extends JDialog {

  private JTextField txtNome;
  private JTextArea txtDescricao;

  private Area area;
  private AreaDAO dao;

  public TelaCadastroArea(Frame owner, Area area) {
    super(owner, true);
    this.area = area;
    this.dao = new AreaDAO();

    setTitle(area == null ? "Nova Área" : "Editar Área");
    setSize(400, 300);
    setLocationRelativeTo(owner);
    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.HORIZONTAL;

    // Nome
    c.gridx = 0;
    c.gridy = 0;
    add(new JLabel("Nome (Gênero):"), c);
    txtNome = new JTextField(20);
    c.gridx = 1;
    add(txtNome, c);

    // Descrição
    c.gridx = 0;
    c.gridy = 1;
    add(new JLabel("Descrição:"), c);
    txtDescricao = new JTextArea(5, 20);
    txtDescricao.setLineWrap(true);
    JScrollPane scroll = new JScrollPane(txtDescricao);
    c.gridx = 1;
    add(scroll, c);

    // Botões
    JPanel botoes = new JPanel();
    JButton btnSalvar = new JButton("Salvar");
    JButton btnCancelar = new JButton("Cancelar");
    botoes.add(btnSalvar);
    botoes.add(btnCancelar);

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    add(botoes, c);

    // Preencher se for edição
    if (area != null) {
      txtNome.setText(area.getNome());
      txtDescricao.setText(area.getDescricao());
    }

    btnCancelar.addActionListener(e -> dispose());
    btnSalvar.addActionListener(e -> salvar());
  }

  private void salvar() {
    try {
      if (txtNome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
        return;
      }

      if (this.area == null)
        this.area = new Area();
      this.area.setNome(txtNome.getText());
      this.area.setDescricao(txtDescricao.getText());

      dao.salvar(this.area);
      JOptionPane.showMessageDialog(this, "Área salva com sucesso!");
      dispose();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
    }
  }
}
