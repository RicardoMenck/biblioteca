package br.com.biblioteca.model;

public class Livro {
  private Integer id;
  private boolean disponivel;
  private boolean exemplarBiblioteca;

  private Titulo titulo;

  public Livro() {
    this.disponivel = true;
    this.exemplarBiblioteca = false;
  }

  public Livro(Titulo titulo, boolean disponivel, boolean exemplarBiblioteca) {
    this.titulo = titulo;
    this.disponivel = disponivel;
    this.exemplarBiblioteca = exemplarBiblioteca;
  }

  public Livro(Integer id, Titulo titulo, boolean disponivel, boolean exemplarBiblioteca) {
    this.id = id;
    this.titulo = titulo;
    this.disponivel = disponivel;
    this.exemplarBiblioteca = exemplarBiblioteca;
  }

  public int verPrazo() {
    if (this.titulo != null && this.titulo.getPrazo() != null) {
      return this.titulo.getPrazo();
    }

    return 0;
  }

  public boolean verificaLivro() {
    return this.exemplarBiblioteca;
  }

  public boolean verificarDisponibilidade() {

    if (!this.disponivel) {
      return false;
    }

    if (this.verificaLivro()) {
      return false;
    }

    return true;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Titulo getTitulo() {
    return titulo;
  }

  public void setTitulo(Titulo titulo) {
    this.titulo = titulo;
  }

  public boolean isDisponivel() {
    return disponivel;
  }

  public void setDisponivel(boolean disponivel) {
    this.disponivel = disponivel;
  }

  public boolean isExemplarBiblioteca() {
    return exemplarBiblioteca;
  }

  public void setExemplarBiblioteca(boolean exemplarBiblioteca) {
    this.exemplarBiblioteca = exemplarBiblioteca;
  }

  @Override
  public String toString() {
    if (titulo != null) {
      return titulo.getNome() + " (ID: " + id + ")";
    }
    return "Livro ID: " + id + " (Sem Título)";
  }

}
