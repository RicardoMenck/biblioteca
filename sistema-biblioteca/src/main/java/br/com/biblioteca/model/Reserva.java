package br.com.biblioteca.model;

import java.util.Date;

public class Reserva {

  private Integer id;
  private Date dataReserva;

  // Relacionamentos essenciais
  private Aluno aluno;
  private Titulo titulo;

  // Construtor Vazio
  public Reserva() {
    this.dataReserva = new Date(); // Por padrão, a data é "agora"
  }

  // Construtor Completo
  public Reserva(Integer id, Date dataReserva, Aluno aluno, Titulo titulo) {
    this.id = id;
    this.dataReserva = dataReserva;
    this.aluno = aluno;
    this.titulo = titulo;
  }

  // --- MÉTODOS DE NEGÓCIO ---

  /**
   * Verifica se a reserva ainda é válida.
   * Exemplo de regra: Reservas com mais de 30 dias expiram.
   */
  public boolean isAtiva() {
    // Implementação futura: comparar dataReserva com data atual
    // Por enquanto, assumimos que toda reserva no sistema está ativa
    return true;
  }

  public boolean validar() {
    return this.aluno != null && this.titulo != null;
  }

  @Override
  public String toString() {
    // Ex: "Reserva: Dom Casmurro - João (10/05/2025)"
    // Simples e direto para logs ou listas
    return "Reserva: " + (titulo != null ? titulo.getNome() : "?")
        + " - " + (aluno != null ? aluno.getNome() : "?");
  }

  // --- GETTERS E SETTERS ---

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
