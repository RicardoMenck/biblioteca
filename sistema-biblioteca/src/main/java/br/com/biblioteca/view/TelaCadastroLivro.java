package br.com.biblioteca.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;

public class TelaCadastroLivro extends JFrame {

  private JComboBox<Titulo> cmbObras;
  private JCheckBox chkBiblioteca; // Caixa de "Sim/Não"
  private JButton btnSalvar;
  private JButton btnCancelar;

  public TelaCadastroLivro() {
    setTitle("Cadastro de Exemplar (Livro)");
    setSize(400, 250);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    initUI();
    carregarObras(); // <--- Importante: Busca dados do banco ao abrir
  }

  private void initUI() {
    JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // --- FORMULÁRIO ---
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    formPanel.add(new JLabel("Selecione a Obra:"));

    // ComboBox tipado com Titulo
    cmbObras = new JComboBox<>();
    formPanel.add(cmbObras);

    formPanel.add(new JLabel("Exemplar da Biblioteca?"));
    // Checkbox para boolean
    chkBiblioteca = new JCheckBox("Sim, é de uso interno (Não sai)");
    formPanel.add(chkBiblioteca);

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
    btnSalvar.addActionListener(e -> salvarLivro());
  }

  private void carregarObras() {
    try {
      TituloDAO dao = new TituloDAO();
      List<Titulo> lista = dao.listarTodos();

      cmbObras.removeAllItems(); // Limpa para garantir

      for (Titulo t : lista) {
        cmbObras.addItem(t); // Adiciona o Objeto inteiro!
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao carregar obras: " + e.getMessage());
    }
  }

  private void salvarLivro() {
    Titulo obraSelecionada = (Titulo) cmbObras.getSelectedItem();

    if (obraSelecionada == null) {
      JOptionPane.showMessageDialog(this, "Você precisa selecionar uma obra!", "Aviso", JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      // 1. Cria o objeto Livro
      // Nota: O construtor do Livro pede o ID do título
      Livro livro = new Livro(obraSelecionada.getId());

      // Vincula o objeto Titulo também (boa prática)
      livro.setTitulo(obraSelecionada);

      // Define se é exemplar de biblioteca (true/false)
      livro.setExemplarBiblioteca(chkBiblioteca.isSelected());

      // 2. Salva no Banco
      new LivroDAO().salvar(livro);

      JOptionPane.showMessageDialog(this, "Exemplar cadastrado com ID gerado!");

      // Opcional: Não limpamos o combo box, pois as vezes
      // a pessoa quer cadastrar 10 livros da mesma obra seguidos.
      // Apenas desmarcamos o checkbox se quiser
      chkBiblioteca.setSelected(false);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
    }
  }
}
