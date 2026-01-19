package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Titulo {
  private Integer id; // Necessário para o Banco de Dados
  private String nome; // Adicionado para podermos dar nome ao livro
  private int prazo;
  private String isbn;
  private Integer edicao;
  private Integer ano;

  // RELACIONAMENTOS DO DIAGRAMA
  private Area area; // 1..1 (Um Título tem uma Área)
  private List<Autor> autores = new ArrayList<>(); // 1..* (Tem vários autores)

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
