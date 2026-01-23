package br.com.biblioteca.model;

public class Area {
  private Integer id;
  private String nome;
  private String descricao;

  public Area() {
  }

  public Area(String nome, String descricao) {
    this.nome = nome;
    this.descricao = descricao;
  }

  public Area(Integer id, String nome, String descricao) {
    this.id = id;
    this.nome = nome;
    this.descricao = descricao;
  }

  public boolean validar() {

    if (this.nome == null || this.nome.trim().isEmpty()) {
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
    return this.nome;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Area area = (Area) obj;
    return id != null && id.equals(area.id);
  }
}
