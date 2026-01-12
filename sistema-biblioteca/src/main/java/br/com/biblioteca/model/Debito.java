package br.com.biblioteca.model;

import java.math.BigDecimal;
import java.util.Date;

public class Debito {
  private Integer id;
  private String raAluno; // Referência ao aluno
  private BigDecimal valor;
  private Date dataDebito;

  public Debito(String raAluno) {
    this.raAluno = raAluno;
  }

  // Método de negócio (Mockado do professor)
  public boolean verificaDebito() {

    if ("4".equals(this.raAluno))
      return false;
    else
      return true;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRaAluno() {
    return raAluno;
  }

  public void setRaAluno(String raAluno) {
    this.raAluno = raAluno;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public Date getDataDebito() {
    return dataDebito;
  }

  public void setDataDebito(Date dataDebito) {
    this.dataDebito = dataDebito;
  }

}
