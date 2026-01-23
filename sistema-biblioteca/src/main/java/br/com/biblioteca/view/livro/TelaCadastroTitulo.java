package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.AreaDAO;
import br.com.biblioteca.dao.AutorDAO;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Area;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Titulo;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaCadastroTitulo extends JDialog {

  private JTextField txtNome;
  private JTextField txtIsbn;
  private JTextField txtPrazo;
  private JTextField txtEdicao;
  private JTextField txtAno;

  // Componentes de Seleção
  private JComboBox<Area> comboArea;
  private JList<Autor> listaAutores; // Lista para seleção múltipla

  private Titulo titulo;
  private TituloDAO daoTitulo;
  private AreaDAO daoArea;
  private AutorDAO daoAutor;

  public TelaCadastroTitulo(Frame owner, Titulo titulo) {
    super(owner, true);
    this.titulo = titulo;
    this.daoTitulo = new TituloDAO();
    this.daoArea = new AreaDAO();
    this.daoAutor = new AutorDAO();

    setTitle(titulo == null ? "Novo Título" : "Editar Título");
    setSize(600, 500); // Aumentei um pouco a altura
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

    // Edição
    c.gridx = 0;
    c.gridy = 3;
    add(new JLabel("Edição:"), c);
    txtEdicao = new JTextField(5);
    c.gridx = 1;
    add(txtEdicao, c);

    // Ano
    c.gridx = 0;
    c.gridy = 4;
    add(new JLabel("Ano:"), c);
    txtAno = new JTextField(5);
    c.gridx = 1;
    add(txtAno, c);

    // --- ÁREA (COMBOBOX) ---
    c.gridx = 0;
    c.gridy = 5;
    add(new JLabel("Área / Gênero:"), c);
    comboArea = new JComboBox<>();
    carregarComboAreas();
    c.gridx = 1;
    add(comboArea, c);

    // --- AUTORES (LISTA MÚLTIPLA) ---
    c.gridx = 0;
    c.gridy = 6;
    c.anchor = GridBagConstraints.NORTH; // Alinha label no topo
    add(new JLabel("Autor(es):"), c);
    c.anchor = GridBagConstraints.CENTER; // Reseta alinhamento

    listaAutores = new JList<>();
    listaAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permite selecionar vários com CTRL
    listaAutores.setVisibleRowCount(4); // Mostra 4 linhas, o resto rola
    carregarListaAutores();

    // Adiciona Scroll na lista caso tenha muitos autores
    JScrollPane scrollAutores = new JScrollPane(listaAutores);
    c.gridx = 1;
    c.gridy = 6;
    c.ipady = 40; // Dá uma altura extra pra lista
    add(scrollAutores, c);
    c.ipady = 0; // Reseta altura

    // Dica para o usuário
    c.gridx = 1;
    c.gridy = 7;
    JLabel lblDica = new JLabel("(Segure CTRL para selecionar mais de um autor)");
    lblDica.setFont(new Font("Arial", Font.ITALIC, 10));
    add(lblDica, c);

    // Botões
    JPanel panelBotoes = new JPanel();
    JButton btnSalvar = new JButton("Salvar");
    JButton btnCancelar = new JButton("Cancelar");

    panelBotoes.add(btnSalvar);
    panelBotoes.add(btnCancelar);
    c.gridx = 0;
    c.gridy = 8;
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
      for (Area a : areas)
        comboArea.addItem(a);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void carregarListaAutores() {
    try {
      List<Autor> autores = daoAutor.listarTodos();
      // JList usa um "Model" próprio, mas podemos passar array direto no setListData
      // O ideal é Vector ou Array. Vamos converter List para Array.
      Autor[] arrayAutores = autores.toArray(new Autor[0]);
      listaAutores.setListData(arrayAutores);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void preencherCampos() {
    txtNome.setText(titulo.getNome());
    txtIsbn.setText(titulo.getIsbn());
    txtPrazo.setText(String.valueOf(titulo.getPrazo()));
    txtEdicao.setText(String.valueOf(titulo.getEdicao()));
    txtAno.setText(String.valueOf(titulo.getAno()));

    // Selecionar Área
    if (titulo.getArea() != null) {
      for (int i = 0; i < comboArea.getItemCount(); i++) {
        if (comboArea.getItemAt(i).getId().equals(titulo.getArea().getId())) {
          comboArea.setSelectedIndex(i);
          break;
        }
      }
    }

    // Selecionar Autores (O Pulo do Gato)
    if (titulo.getAutores() != null && !titulo.getAutores().isEmpty()) {
      // Precisamos descobrir quais índices da lista correspondem aos autores do
      // título
      List<Integer> indicesParaSelecionar = new ArrayList<>();
      ListModel<Autor> model = listaAutores.getModel();

      for (int i = 0; i < model.getSize(); i++) {
        Autor autorDaLista = model.getElementAt(i);
        // Verifica se esse autor da lista está na lista de autores do título
        for (Autor autorDoTitulo : titulo.getAutores()) {
          if (autorDoTitulo.getId().equals(autorDaLista.getId())) {
            indicesParaSelecionar.add(i);
          }
        }
      }
      // Converte List<Integer> para int[] para o JList selecionar
      int[] indices = indicesParaSelecionar.stream().mapToInt(i -> i).toArray();
      listaAutores.setSelectedIndices(indices);
    }
  }

  private void salvar() {
    try {
      if (titulo == null)
        titulo = new Titulo();

      titulo.setNome(txtNome.getText()); // Corrigido getText()
      titulo.setIsbn(txtIsbn.getText());
      titulo.setPrazo(Integer.parseInt(txtPrazo.getText()));
      titulo.setEdicao(Integer.parseInt(txtEdicao.getText()));
      titulo.setAno(Integer.parseInt(txtAno.getText()));

      // Pega a Área
      titulo.setArea((Area) comboArea.getSelectedItem());

      // Pega os Autores selecionados
      List<Autor> autoresSelecionados = listaAutores.getSelectedValuesList();
      if (autoresSelecionados.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecione pelo menos um autor!");
        return;
      }
      titulo.setAutores(autoresSelecionados);

      daoTitulo.salvar(titulo);

      JOptionPane.showMessageDialog(this, "Título salvo com sucesso!");
      dispose();

    } catch (NumberFormatException ne) {
      JOptionPane.showMessageDialog(this, "Verifique os campos numéricos!");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
