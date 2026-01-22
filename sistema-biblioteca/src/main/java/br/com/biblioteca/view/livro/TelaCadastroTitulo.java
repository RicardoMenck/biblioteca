package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AreaDAO;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Area;
import br.com.biblioteca.model.Titulo;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroTitulo extends JDialog {

  private JTextField txtNome;
  private JTextField txtIsbn;
  private JTextField txtPrazo;
  private JTextField txtEdicao;
  private JTextField txtAno;

  // O ComboBox guarda OBJETOS Area inteiros, não só Strings
  private JComboBox<Area> comboArea;

  private Titulo titulo;
  private TituloDAO daoTitulo;
  private AreaDAO daoArea;

  public TelaCadastroTitulo(Frame owner, Titulo titulo) {
    super(owner, true);
    this.titulo = titulo;
    this.daoTitulo = new TituloDAO();
    this.daoArea = new AreaDAO();

    setTitle(titulo == null ? "Novo Título" : "Editar Título");
    setSize(500, 400);
    setLocationRelativeTo(owner);
    setLayout(new GridBagLayout());

    montarFormulario();

    if (titulo != null)
      preencherCampos();
  }

  private void montarFormulario() {
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.fill = GridBagConstraints.HORIZONTAL;

    // Nome
    c.gridx = 0;
    c.gridy = 0;
    add(new JLabel("Nome da Obra:"), c);
    txtNome = new JTextField(25);
    c.gridx = 1;
    add(txtNome, c);

    // ISBN
    c.gridx = 0;
    c.gridy = 1;
    add(new JLabel("ISBN:"), c);
    txtIsbn = new JTextField(15);
    c.gridx = 1;
    add(txtIsbn, c);

    // Prazo
    c.gridx = 0;
    c.gridy = 2;
    add(new JLabel("Prazo (dias):"), c);
    txtPrazo = new JTextField(5);
    c.gridx = 1;
    add(txtPrazo, c);

    // Edição e Ano (Na mesma linha visual, mas aqui simplificado)
    c.gridx = 0;
    c.gridy = 3;
    add(new JLabel("Edição:"), c);
    txtEdicao = new JTextField(5);
    c.gridx = 1;
    add(txtEdicao, c);

    c.gridx = 0;
    c.gridy = 4;
    add(new JLabel("Ano:"), c);
    txtAno = new JTextField(5);
    c.gridx = 1;
    add(txtAno, c);

    // --- COMBOBOX DE ÁREA (Core Feature) ---
    c.gridx = 0;
    c.gridy = 5;
    add(new JLabel("Área / Gênero:"), c);
    comboArea = new JComboBox<>();
    carregarComboAreas(); // Busca do banco
    c.gridx = 1;
    add(comboArea, c);

    // Botões
    JPanel panelBotoes = new JPanel();
    JButton btnSalvar = new JButton("Salvar");
    JButton btnCancelar = new JButton("Cancelar");

    panelBotoes.add(btnSalvar);
    panelBotoes.add(btnCancelar);
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 2;
    add(panelBotoes, c);

    // Listeners
    btnCancelar.addActionListener(e -> dispose());
    btnSalvar.addActionListener(e -> salvar());
  }

  private void carregarComboAreas() {
    try {
      List<Area> areas = daoArea.listarTodos();
      comboArea.removeAllItems();
      for (Area a : areas) {
        comboArea.addItem(a); // Adiciona o objeto. O toString() cuida do texto.
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao carregar áreas: " + e.getMessage());
    }
  }

  private void preencherCampos() {
    txtNome.setText(titulo.getNome());
    txtIsbn.setText(titulo.getIsbn());
    txtPrazo.setText(String.valueOf(titulo.getPrazo()));
    txtEdicao.setText(String.valueOf(titulo.getEdicao()));
    txtAno.setText(String.valueOf(titulo.getAno()));

    // Selecionar a Área correta no Combo
    if (titulo.getArea() != null) {
      // Percorre o combo para achar o ID igual e selecionar
      for (int i = 0; i < comboArea.getItemCount(); i++) {
        Area a = comboArea.getItemAt(i);
        if (a.getId().equals(titulo.getArea().getId())) {
          comboArea.setSelectedIndex(i);
          break;
        }
      }
    }
  }

  private void salvar() {
    try {
      if (titulo == null)
        titulo = new Titulo();

      titulo.setNome(txtNome.getText());
      titulo.setIsbn(txtIsbn.getText());
      titulo.setPrazo(Integer.parseInt(txtPrazo.getText()));
      titulo.setEdicao(Integer.parseInt(txtEdicao.getText()));
      titulo.setAno(Integer.parseInt(txtAno.getText()));

      // Pega o objeto selecionado no Combo
      titulo.setArea((Area) comboArea.getSelectedItem());

      // TODO: Falta selecionar Autores (N:N).
      // Para simplificar esta tela, assumiremos autores vazios por enquanto
      // ou teríamos que ter outra lista de seleção múltipla.

      daoTitulo.salvar(titulo);

      JOptionPane.showMessageDialog(this, "Título salvo com sucesso!");
      dispose();

    } catch (NumberFormatException ne) {
      JOptionPane.showMessageDialog(this, "Campos numéricos inválidos!");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
