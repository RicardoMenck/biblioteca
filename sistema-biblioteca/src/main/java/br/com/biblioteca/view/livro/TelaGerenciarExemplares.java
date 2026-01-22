package br.com.biblioteca.view.livro;

import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaGerenciarExemplares extends JDialog {

  private Titulo tituloSelecionado;
  private LivroDAO dao;
  private JTable tabela;

  public TelaGerenciarExemplares(Frame owner, Titulo titulo) {
    super(owner, true);
    this.tituloSelecionado = titulo;
    this.dao = new LivroDAO();

    setTitle("Gerenciar Exemplares: " + titulo.getNome());
    setSize(600, 400);
    setLocationRelativeTo(owner);
    setLayout(new BorderLayout());

    // 1. Cabeçalho com infos do Título
    JPanel panelInfo = new JPanel(new GridLayout(2, 1));
    panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JLabel lblNome = new JLabel("Obra: " + titulo.getNome());
    lblNome.setFont(new Font("Arial", Font.BOLD, 14));
    JLabel lblIsbn = new JLabel("ISBN: " + titulo.getIsbn() + " | Edição: " + titulo.getEdicao());
    panelInfo.add(lblNome);
    panelInfo.add(lblIsbn);
    add(panelInfo, BorderLayout.NORTH);

    // 2. Tabela de Exemplares
    tabela = new JTable();
    add(new JScrollPane(tabela), BorderLayout.CENTER);

    // 3. Botões de Ação
    JPanel panelBotoes = new JPanel();
    JButton btnAdicionar = new JButton("Adicionar Novo Exemplar");
    JButton btnExcluir = new JButton("Excluir Exemplar");
    JButton btnMudarTipo = new JButton("Alternar Tipo (Fixo/Normal)");

    panelBotoes.add(btnAdicionar);
    panelBotoes.add(btnMudarTipo);
    panelBotoes.add(btnExcluir);
    add(panelBotoes, BorderLayout.SOUTH);

    // Carrega dados iniciais
    carregarTabela();

    // --- AÇÕES ---

    // Adiciona um exemplar imediatamente
    btnAdicionar.addActionListener(e -> {
      try {
        Livro novo = new Livro();
        novo.setTitulo(tituloSelecionado);
        novo.setDisponivel(true);
        novo.setExemplarBiblioteca(false); // Padrão é circulante

        dao.salvar(novo);
        carregarTabela();
        JOptionPane.showMessageDialog(this, "Exemplar ID " + novo.getId() + " adicionado!");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
      }
    });

    // Muda de Circulante para Fixo (Consulta Local) e vice-versa
    btnMudarTipo.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Livro l = ((LivroTableModel) tabela.getModel()).getLivroAt(row);
        // Inverte o status
        l.setExemplarBiblioteca(!l.isExemplarBiblioteca());
        try {
          dao.salvar(l); // Salvar faz update se já tem ID
          carregarTabela();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage());
        }
      } else {
        JOptionPane.showMessageDialog(this, "Selecione um exemplar.");
      }
    });

    btnExcluir.addActionListener(e -> {
      int row = tabela.getSelectedRow();
      if (row >= 0) {
        Livro l = ((LivroTableModel) tabela.getModel()).getLivroAt(row);
        if (JOptionPane.showConfirmDialog(this, "Excluir exemplar ID " + l.getId() + "?") == JOptionPane.YES_OPTION) {
          try {
            dao.excluir(l.getId());
            carregarTabela();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
          }
        }
      }
    });
  }

  private void carregarTabela() {
    try {
      // Usa aquele método listarPorTitulo que criamos na LivroDAO
      List<Livro> lista = dao.listarPorTitulo(tituloSelecionado.getId());
      tabela.setModel(new LivroTableModel(lista));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
