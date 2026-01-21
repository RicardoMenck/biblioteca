package br.com.biblioteca.model;

public class Livro {
  private Integer id;
  private Titulo titulo; // Associação: O Livro "é um exemplar de" um Título
  private boolean disponivel; // True = Na estante, False = Emprestado
  private boolean exemplarBiblioteca; // True = Apenas consulta interna (não sai)

  public Livro() {
    this.disponivel = true;
    this.exemplarBiblioteca = false;
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
    // Retorna 0 (ou um padrão seguro) se houver erro nos dados, evitando crash do
    // sistema
    return 0;
  }

  public boolean verificaLivro() {
    return this.exemplarBiblioteca;
  }

  public boolean verificarDisponibilidade() {
    // Se o livro já estiver emprestado (disponivel = false), retorna false.
    if (!this.disponivel) {
      return false;
    }

    // Se o livro for de uso exclusivo da biblioteca (verificaLivro = true), retorna
    // false.
    if (this.verificaLivro()) {
      return false;
    }

    // Se passou pelos dois filtros, está livre para levar.
    return true;
  }

  // Getters e Setters para o DAO
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
