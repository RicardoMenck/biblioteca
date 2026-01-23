package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Titulo {
  private Integer id;
  private String nome;
  private Integer prazo;
  private String isbn;
  private Integer edicao;
  private Integer ano;

  private Area area;
  private List<Autor> autores = new ArrayList<>();

  public Titulo() {
  }

  public Titulo(String nome, Integer prazo, String isbn, Integer edicao, Integer ano) {
    this.nome = nome;
    this.prazo = prazo;
    this.isbn = isbn;
    this.edicao = edicao;
    this.ano = ano;
  }

  public boolean validar() {
    return this.nome != null && !this.nome.isEmpty()
        && this.prazo != null && this.prazo > 0
        && this.area != null;

  }

  public void adicionarAutor(Autor autor) {
    if (autor != null && !this.autores.contains(autor)) {
      this.autores.add(autor);
    }
  }

  public void removerAutor(Autor autor) {
    this.autores.remove(autor);
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

  public Integer getPrazo() {
    return prazo;
  }

  public void setPrazo(Integer prazo) {
    this.prazo = prazo;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public Integer getEdicao() {
    return edicao;
  }

  public void setEdicao(Integer edicao) {
    this.edicao = edicao;
  }

  public Integer getAno() {
    return ano;
  }

  public void setAno(Integer ano) {
    this.ano = ano;
  }

  public Area getArea() {
    return area;
  }

  public void setArea(Area area) {
    this.area = area;
  }

  public List<Autor> getAutores() {
    return autores;
  }

  public void setAutores(List<Autor> autores) {
    this.autores = autores;
  }

  @Override
  public String toString() {
    return this.nome;
  }

}
