package br.com.biblioteca.model;

import java.time.Year;
import java.util.Random;

public class Aluno {
  private Integer id;
  private String ra;
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
    this.gerarNovoRA(); // Gera ra automaticamente ao criar
  }

  // Construtor do banco
  public Aluno(Integer id, String ra, String nome, String cpf, String endereco) {
    this.id = id;
    this.ra = ra;
    this.nome = nome;
    this.cpf = cpf;
    this.endereco = endereco;
  }

  public void gerarNovoRA() {
    int ano = Year.now().getValue();
    int mes = java.time.LocalDate.now().getMonthValue();
    int sequencial = new Random().nextInt(9000) + 1000;

    // Gera a String formatada. Ex: "2026019999"
    this.ra = String.format("%d%02d%d", ano, mes, sequencial);
  }

  public boolean isValido() {
    return this.nome != null && !this.nome.isEmpty() && this.ra != null;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRa() {
    return ra;
  }

  public void setRa(String ra) {
    this.ra = ra;
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
    return this.ra + " - " + this.nome;
  }

}
