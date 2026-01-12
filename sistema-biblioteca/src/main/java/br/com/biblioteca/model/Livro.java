package br.com.biblioteca.model;

public class Livro {
  private Integer id;
  private Titulo titulo; // Associação com a classe Titulo
  private boolean exemplarBiblioteca;

  public Livro() {
  }

  public Livro(int codigo) {
    this.titulo = new Titulo(codigo);

    // Lógica original: código aleatório para definir se o livro é exemplar unico
    if (codigo == 2 || codigo == 4) {
      this.exemplarBiblioteca = true;
    } else {
      this.exemplarBiblioteca = false;
    }
  }

  public int verPrazo() {
    return titulo.getPrazo();
  }

  public boolean verificaLivro() {
    return exemplarBiblioteca;
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

  public boolean isExemplarBiblioteca() {
    return exemplarBiblioteca;
  }

  public void setExemplarBiblioteca(boolean exemplarBiblioteca) {
    this.exemplarBiblioteca = exemplarBiblioteca;
  }
}
