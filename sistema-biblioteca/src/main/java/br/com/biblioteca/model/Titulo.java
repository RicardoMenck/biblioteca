package br.com.biblioteca.model;

public class Titulo {
  private Integer id; // Necessário para o Banco de Dados
  private String nome; // Adicionado para podermos dar nome ao livro
  private int prazo;

  public Titulo() {
  }

  public Titulo(int codigo) {
    this.prazo = codigo + 1;
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

  public int getPrazo() {
    return prazo;
  }

  public void setPrazo(int prazo) {
    this.prazo = prazo;
  }
}
