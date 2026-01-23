package br.com.biblioteca.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reserva {

  private Integer id;
  private Date dataReserva;
  private boolean ativa;

  private Aluno aluno;
  private Titulo titulo;

  public Reserva() {
    this.dataReserva = new Date();
    this.ativa = true;
  }

  public Reserva(Aluno aluno, Titulo titulo) {
    this.aluno = aluno;
    this.titulo = titulo;
    this.dataReserva = new Date(); // Data de agora
    this.ativa = true; // Nasce ativa
  }

  public Reserva(Integer id, Date dataReserva, boolean ativa, Aluno aluno, Titulo titulo) {
    this.id = id;
    this.dataReserva = dataReserva;
    this.ativa = ativa;
    this.aluno = aluno;
    this.titulo = titulo;
  }

  public boolean validar() {
    return this.aluno != null && this.titulo != null;
  }

  public boolean isExpirada() {
    if (this.dataReserva == null)
      return true;

    long diferenca = new Date().getTime() - this.dataReserva.getTime();
    long dias = TimeUnit.DAYS.convert(diferenca, TimeUnit.MILLISECONDS);

    return dias > 10;
  }

  @Override
  public String toString() {
    return "Reserva: " + (titulo != null ? titulo.getNome() : "?")
        + " - " + (aluno != null ? aluno.getNome() : "?")
        + (ativa ? " [ATIVA]" : " [INATIVA]");
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getDataReserva() {
    return dataReserva;
  }

  public void setDataReserva(Date dataReserva) {
    this.dataReserva = dataReserva;
  }

  public boolean isAtiva() {
    return ativa;
  }

  public void setAtiva(boolean ativa) {
    this.ativa = ativa;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public Titulo getTitulo() {
    return titulo;
  }

  public void setTitulo(Titulo titulo) {
    this.titulo = titulo;
  }
}
