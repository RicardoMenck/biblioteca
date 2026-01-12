package br.com.biblioteca.model;

import java.util.Calendar;
import java.util.Date;

public class ItemEmprestimo {
  private Integer id;
  private Emprestimo emprestimo;
  private Livro livro;
  private Date dataDevolucao;

  public ItemEmprestimo() {
  }

  public ItemEmprestimo(Livro livro) {
    this.livro = livro;
  }

  public Date calculaDataDevolucao(Date data) {
    dataDevolucao = data;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data);
    calendar.add(Calendar.DATE, livro.verPrazo());
    dataDevolucao = calendar.getTime();
    return dataDevolucao;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Emprestimo getEmprestimo() {
    return emprestimo;
  }

  public void setEmprestimo(Emprestimo emprestimo) {
    this.emprestimo = emprestimo;
  }

  public Livro getLivro() {
    return livro;
  }

  public void setLivro(Livro livro) {
    this.livro = livro;
  }

  public Date getDataDevolucao() {
    return dataDevolucao;
  }

  public void setDataDevolucao(Date dataDevolucao) {
    this.dataDevolucao = dataDevolucao;
  }

}
