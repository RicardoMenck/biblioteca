package br.com.biblioteca.view.aluno;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.model.Aluno;
import javax.swing.*;
import java.awt.*;

public class TelaCadastroAluno extends JDialog {

  private JTextField txtNome;
  private JTextField txtRa;
  private JTextField txtCpf;
  private JTextField txtEndereco;

  private Aluno aluno; // Se for null é INCLUSÃO, se tiver objeto é EDIÇÃO
  private AlunoDAO dao;

  public TelaCadastroAluno(Frame owner, Aluno alunoParaEdicao) {
    super(owner, true); // TRUE = Modal (bloqueia a janela de trás)
    this.aluno = alunoParaEdicao;
    this.dao = new AlunoDAO();

    // Se tem aluno, título é "Editar", senão "Novo"
    setTitle(aluno == null ? "Novo Aluno" : "Editar Aluno");
    setSize(400, 300);
    setLocationRelativeTo(owner);
    setLayout(new GridBagLayout()); // GridBag é chato mas é profissional para alinhar

    montarFormulario();

    // Se for edição, preenche os campos
    if (aluno != null) {
      preencherCampos();
    }
  }

  private void montarFormulario() {
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5); // Margem
    c.fill = GridBagConstraints.HORIZONTAL;

    // Linha 1: Nome
    c.gridx = 0;
    c.gridy = 0;
    add(new JLabel("Nome:"), c);
    txtNome = new JTextField(20);
    c.gridx = 1;
    add(txtNome, c);

    // Linha 2: RA (Matrícula) - Se for novo, vamos deixar bloqueado pois é
    // automático?
    // Não, seu model tem construtor que gera, mas DAO permite salvar manual.
    // Vamos deixar liberado caso queira forçar, mas sugerindo automático.
    c.gridx = 0;
    c.gridy = 1;
    add(new JLabel("RA (Matrícula):"), c);
    txtRa = new JTextField(15);
    if (aluno == null)
      txtRa.setToolTipText("Deixe vazio para gerar automático");
    c.gridx = 1;
    add(txtRa, c);

    // Linha 3: CPF
    c.gridx = 0;
    c.gridy = 2;
    add(new JLabel("CPF:"), c);
    txtCpf = new JTextField(15);
    c.gridx = 1;
    add(txtCpf, c);

    // Linha 4: Endereço
    c.gridx = 0;
    c.gridy = 3;
    add(new JLabel("Endereço:"), c);
    txtEndereco = new JTextField(20);
    c.gridx = 1;
    add(txtEndereco, c);

    // Painel de Botões
    JPanel panelBotoes = new JPanel();
    JButton btnSalvar = new JButton("Salvar");
    JButton btnCancelar = new JButton("Cancelar");
    panelBotoes.add(btnSalvar);
    panelBotoes.add(btnCancelar);

    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 2;
    add(panelBotoes, c);

    // --- AÇÕES ---
    btnCancelar.addActionListener(e -> dispose());

    btnSalvar.addActionListener(e -> salvar());
  }

  private void preencherCampos() {
    txtNome.setText(aluno.getNome());
    txtRa.setText(aluno.getRa());
    txtCpf.setText(aluno.getCpf());
    txtEndereco.setText(aluno.getEndereco());
    // Em edição de RA geralmente bloqueamos para não quebrar histórico, mas vou
    // deixar livre
    txtRa.setEditable(false);
  }

  private void salvar() {
    try {
      // Validação simples
      if (txtNome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
        return;
      }

      // Se for inclusão, cria novo. Se edição, aproveita o ID.
      if (aluno == null) {
        aluno = new Aluno();
        // Se o usuário digitou RA, usa. Se não, gera.
        if (txtRa.getText().trim().isEmpty()) {
          aluno.gerarNovoRA();
        } else {
          aluno.setRa(txtRa.getText());
        }
      }

      aluno.setNome(txtNome.getText());
      aluno.setCpf(txtCpf.getText());
      aluno.setEndereco(txtEndereco.getText());

      // DAO decide se é Insert ou Update
      dao.salvar(aluno);

      JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
      dispose(); // Fecha janela

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}
