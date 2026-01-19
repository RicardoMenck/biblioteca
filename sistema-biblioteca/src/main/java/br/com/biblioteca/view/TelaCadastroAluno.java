package br.com.biblioteca.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.model.Aluno;

public class TelaCadastroAluno extends JFrame {

  private JTextField txtRa;
  private JTextField txtNome;
  private JButton btnSalvar;
  private JButton btnCancelar;

  public TelaCadastroAluno() {
    // 1. Configurações da Janela
    setTitle("Cadastro de Aluno");
    setSize(400, 250);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só essa janela, não o app todo
    setLocationRelativeTo(null); // Centraliza
    setResizable(false); // Evita que o usuário quebre o layout

    // 2. Inicializando Componentes
    initUI();
  }

  private void initUI() {
    // Painel Principal com borda para não ficar colado na janela
    JPanel painelPrincipal = new JPanel();
    painelPrincipal.setLayout(new BorderLayout(10, 10));
    painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // --- PAINEL DE FORMULÁRIO (Centro) ---
    // GridLayout: 2 linhas, 2 colunas (Label + Campo)
    JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    formPanel.add(new JLabel("RA do Aluno:"));
    txtRa = new JTextField();
    formPanel.add(txtRa);

    formPanel.add(new JLabel("Nome Completo:"));
    txtNome = new JTextField();
    formPanel.add(txtNome);

    painelPrincipal.add(formPanel, BorderLayout.CENTER);

    // --- PAINEL DE BOTÕES (Rodapé) ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    btnCancelar = new JButton("Cancelar");
    btnSalvar = new JButton("Salvar");

    // Estilizando o botão salvar (Opcional)
    btnSalvar.setBackground(new Color(60, 179, 113)); // Um verde suave
    btnSalvar.setForeground(Color.WHITE);
    btnSalvar.setFocusPainted(false);

    buttonPanel.add(btnCancelar);
    buttonPanel.add(btnSalvar);

    painelPrincipal.add(buttonPanel, BorderLayout.SOUTH);

    // Adiciona tudo à janela
    add(painelPrincipal);

    // 3. Configurando Ações (Listeners)
    configurarAcoes();
  }

  private void configurarAcoes() {
    // Ação do Botão Cancelar
    btnCancelar.addActionListener(e -> dispose()); // Fecha a janela

    // Ação do Botão Salvar
    btnSalvar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        salvarAluno();
      }
    });
  }

  private void salvarAluno() {
    // 1. Validação Simples
    if (txtRa.getText().trim().isEmpty() || txtNome.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Por favor, preencha todos os campos!",
          "Erro de Validação",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      // 2. Coleta dados da tela
      String ra = txtRa.getText().trim();
      String nome = txtNome.getText().trim();

      // 3. Cria objeto
      Aluno aluno = new Aluno(ra, nome);

      // 4. Chama o Backend (DAO)
      AlunoDAO dao = new AlunoDAO();
      dao.salvar(aluno);

      // 5. Sucesso!
      JOptionPane.showMessageDialog(this, "Aluno salvo com sucesso!");

      // Limpa campos para novo cadastro
      txtRa.setText("");
      txtNome.setText("");
      txtRa.requestFocus(); // Coloca o cursor no RA de novo

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Erro ao salvar aluno: " + ex.getMessage(),
          "Erro",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  // Para testar a tela isoladamente se quiser
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TelaCadastroAluno().setVisible(true));
  }
}
