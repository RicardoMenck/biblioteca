package br.com.biblioteca.model;

import java.util.Date;

public class Debito {
  private Integer id;
  private Integer codigoAluno;
  private Double valor;
  private Date dataDebito;

  public Debito() {
  }

  public Debito(Integer codigoAluno) {
    this.codigoAluno = codigoAluno;
    this.dataDebito = new Date();
    this.valor = 0.0;
  }

  public Debito(Integer codigoAluno, Double valor, Date dataDebito) {
    this.codigoAluno = codigoAluno;
    this.valor = valor;
    this.dataDebito = dataDebito;
  }

  public Debito(Integer id, Integer codigoAluno, Double valor, Date dataDebito) {
    this.id = id;
    this.codigoAluno = codigoAluno;
    this.valor = valor;
    this.dataDebito = dataDebito;
  }

  public boolean verificarDebito() {
    if (this.codigoAluno == null) {
      return false;
    }

    if (this.valor != null && this.valor > 0.0) {
      return true;
    }

    return false;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getCodigoAluno() {
    return codigoAluno;
  }

  public void setCodigoAluno(Integer codigoAluno) {
    this.codigoAluno = codigoAluno;
  }

  public Double getValor() {
    return valor;
  }

  public void setValor(Double valor) {
    this.valor = valor;
  }

  public Date getDataDebito() {
    return dataDebito;
  }

  public void setDataDebito(Date dataDebito) {
    this.dataDebito = dataDebito;
  }

  @Override
  public String toString() {
    return "Debito [id=" + id + ", codigoAluno=" + codigoAluno + ", valor=" + valor + ", dataDebito=" + dataDebito
        + "]";
  }

}
