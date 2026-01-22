package br.com.biblioteca.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reserva {

  private Integer id;
  private Date dataReserva;
  private boolean ativa;

  // Relacionamentos essenciais
  private Aluno aluno;
  private Titulo titulo;

  // Construtor Vazio
  public Reserva() {
    this.dataReserva = new Date();
    this.ativa = true; // Por padrão, ao criar vazio, assume-se intenção de nova reserva
  }

  // Construtor Prático (Para criar novas reservas na Tela/Controller)
  public Reserva(Aluno aluno, Titulo titulo) {
    this.aluno = aluno;
    this.titulo = titulo;
    this.dataReserva = new Date(); // Data de agora
    this.ativa = true; // Nasce ativa
  }

  // Construtor Completo (Para o DAO carregar do banco)
  public Reserva(Integer id, Date dataReserva, boolean ativa, Aluno aluno, Titulo titulo) {
    this.id = id;
    this.dataReserva = dataReserva;
    this.ativa = ativa;
    this.aluno = aluno;
    this.titulo = titulo;
  }

  // --- MÉTODOS DE NEGÓCIO ---

  /**
   * Valida se os dados mínimos estão presentes
   */
  public boolean validar() {
    return this.aluno != null && this.titulo != null;
  }

  /**
   * Método opcional para verificar se a reserva expirou (Ex: 10 dias).
   * Isso NÃO substitui o isAtiva(). O isAtiva diz se foi cancelada/atendida.
   * Este diz se o prazo venceu.
   */
  public boolean isExpirada() {
    if (this.dataReserva == null)
      return true;

    long diferenca = new Date().getTime() - this.dataReserva.getTime();
    long dias = TimeUnit.DAYS.convert(diferenca, TimeUnit.MILLISECONDS);

    return dias > 10; // Exemplo: Expira em 10 dias
  }

  @Override
  public String toString() {
    return "Reserva: " + (titulo != null ? titulo.getNome() : "?")
        + " - " + (aluno != null ? aluno.getNome() : "?")
        + (ativa ? " [ATIVA]" : " [INATIVA]");
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

  // CORREÇÃO: O getter retorna o atributo real!
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
