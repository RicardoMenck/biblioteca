package br.com.biblioteca.model;

public class Area {
  private Integer id;
  private String nome;
  private String descricao;

  public Area() {
  }

  public Area(Integer id, String nome) {
    this.id = id;
    this.nome = nome;
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

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  @Override
  public String toString() {
    return this.nome; // Importante para o ComboBox
  }
}
