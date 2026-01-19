package br.com.biblioteca.model;

public class Autor {
  private Integer id;
  private String nome;
  private String sobrenome;
  private String titulacao;

  public Autor() {
  }

  public Autor(String nome, String sobrenome) {
    this.nome = nome;
    this.sobrenome = sobrenome;
  }

  // Getters e Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getSobrenome() {
    return sobrenome;
  }

  public void setSobrenome(String sobrenome) {
    this.sobrenome = sobrenome;
  }

  public String getTitulacao() {
    return titulacao;
  }

  public void setTitulacao(String titulacao) {
    this.titulacao = titulacao;
  }

  @Override
  public String toString() {
    return this.nome + " " + this.sobrenome;
  }
}
