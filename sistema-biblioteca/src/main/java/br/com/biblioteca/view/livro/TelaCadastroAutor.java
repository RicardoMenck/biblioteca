package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AutorDAO;
import br.com.biblioteca.model.Autor;
import javax.swing.*;
import java.awt.*;

public class TelaCadastroAutor extends JDialog {

  private JTextField txtNome;
  private JTextField txtSobrenome;
  private JTextField txtTitulacao;

  private Autor autor;
  private AutorDAO dao;

  public TelaCadastroAutor(Frame owner, Autor autor) {
    super(owner, true);
    this.autor = autor;
    this.dao = new AutorDAO();

    setTitle(autor == null ? "Novo Autor" : "Editar Autor");
    setSize(400, 250);
    setLocationRelativeTo(owner);
    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.HORIZONTAL;

    c.gridx = 0;
    c.gridy = 0;
    add(new JLabel("Nome:"), c);
    txtNome = new JTextField(20);
    c.gridx = 1;
    add(txtNome, c);

    c.gridx = 0;
    c.gridy = 1;
    add(new JLabel("Sobrenome:"), c);
    txtSobrenome = new JTextField(20);
    c.gridx = 1;
    add(txtSobrenome, c);

    c.gridx = 0;
    c.gridy = 2;
    add(new JLabel("Titulação:"), c);
    txtTitulacao = new JTextField(20); // Ex: PhD, Mestre
    c.gridx = 1;
    add(txtTitulacao, c);

    JPanel botoes = new JPanel();
    JButton btnSalvar = new JButton("Salvar");
    JButton btnCancelar = new JButton("Cancelar");
    botoes.add(btnSalvar);
    botoes.add(btnCancelar);

    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    add(botoes, c);

    if (autor != null) {
      txtNome.setText(autor.getNome());
      txtSobrenome.setText(autor.getSobrenome());
      txtTitulacao.setText(autor.getTitulacao());
    }

    btnCancelar.addActionListener(e -> dispose());
    btnSalvar.addActionListener(e -> {
      try {
        if (this.autor == null)
          this.autor = new Autor();
        this.autor.setNome(txtNome.getText());
        this.autor.setSobrenome(txtSobrenome.getText());
        this.autor.setTitulacao(txtTitulacao.getText());

        dao.salvar(this.autor);
        JOptionPane.showMessageDialog(this, "Salvo!");
        dispose();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
      }
    });
  }
}
