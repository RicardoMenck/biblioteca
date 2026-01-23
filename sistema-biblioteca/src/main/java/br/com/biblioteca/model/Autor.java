package br.com.biblioteca.model;

public class Autor {
  private Integer id;
  private String nome;
  private String sobrenome;
  private String titulacao;

  public Autor() {
  }

  public Autor(String nome, String sobrenome, String titulacao) {
    this.nome = nome;
    this.sobrenome = sobrenome;
    this.titulacao = titulacao;
  }

  public Autor(Integer id, String nome, String sobrenome, String titulacao) {
    this.id = id;
    this.nome = nome;
    this.sobrenome = sobrenome;
    this.titulacao = titulacao;
  }

  public boolean validar() {
    if (this.nome == null || this.nome.trim().isEmpty()) {
      return false;
    }
    return true;
  }

  public String getNomeBibliografico() {
    String sobrenome = (this.sobrenome != null) ? this.sobrenome.toUpperCase() : "";
    String nome = (this.nome != null) ? this.nome : "";
    return sobrenome + ", " + nome;
  }

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
    String display = this.nome;
    if (this.sobrenome != null && !this.sobrenome.isEmpty()) {
      display += " " + this.sobrenome;
    }
    return display;
  }
}
