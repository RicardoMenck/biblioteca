package br.com.biblioteca.model;

import java.time.Year;
import java.util.List;
import java.util.Random;

public class Aluno {
  private Integer id;
  private Integer matricula;
  private String nome;
  private String cpf;
  private String endereco;

  // Construtor vazio
  public Aluno() {
  }

  // Construtor utilitário para criação rápida (sem ID, pois o banco gera)
  public Aluno(String nome, String cpf, String endereco) {
    this.nome = nome;
    this.cpf = cpf;
    this.endereco = endereco;
    this.gerarNovaMatricula(); // Gera matrícula automaticamente ao criar
  }

  public boolean verificaAluno() {
    if (this.matricula == null) {
      return false;
    }
    return true;
  }

  public boolean verificaDebito() {
    // Instancia o objeto Débito passando o ID deste aluno
    Debito debito = new Debito(this.id);

    return debito.verificaDebito();
  }

  public boolean emprestar(List<Livro> livros) {
    // Validações prévias do aluno antes de tentar emprestar
    if (!verificaAluno() || verificaDebito()) {
      return false; // Bloqueia se aluno inválido ou com débito
    }

    // Instancia o objeto Emprestimo
    Emprestimo emprestimo = new Emprestimo();

    // Configura este aluno no empréstimo
    emprestimo.setAluno(this);

    // Chama o método emprestar da classe Emprestimo delegando a lista
    return emprestimo.emprestar(livros);
  }

  public void gerarNovaMatricula() {
    int ano = Year.now().getValue();
    int mes = java.time.LocalDate.now().getMonthValue();
    int sequencial = new Random().nextInt(9000) + 1000;

    String matriculaGerada = String.format("%d%02d%d", ano, mes, sequencial);
    this.matricula = Integer.parseInt(matriculaGerada);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getMatricula() {
    return matricula;
  }

  public void setMatricula(Integer matricula) {
    this.matricula = matricula;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  @Override
  public String toString() {
    return this.matricula + " - " + this.nome;
  }

}
