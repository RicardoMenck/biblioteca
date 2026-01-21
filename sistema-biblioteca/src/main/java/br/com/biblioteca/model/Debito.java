package br.com.biblioteca.model;

import java.util.Date;

public class Debito {
  private Integer id;
  private Integer codigoAluno; // Referência ao aluno
  private Double valor;
  private Date dataDebito;

  public Debito() {
  }

  public Debito(Integer codigoAluno) {
    this.codigoAluno = codigoAluno;
    this.dataDebito = new Date(); // Assume data de hoje ao instanciar verificação
    this.valor = 0.0; // Valor inicial zero para verificação
  }

  public Debito(Integer id, Integer codigoAluno, Double valor, Date dataDebito) {
    this.id = id;
    this.codigoAluno = codigoAluno;
    this.valor = valor;
    this.dataDebito = dataDebito;
  }

  public boolean verificaDebito() {
    // Código aleatório para definir se o aluno tem débito
    // É necessário fazer a verificação de forma persistente no futuro (via DAO)

    if (this.codigoAluno != null) {
      return false;
    } else {
      return true;
    }
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
