package br.com.biblioteca.model;

public class Aluno {
  private String ra;
  private String nome;

  public Aluno() {
  }

  public Aluno(String ra, String nome) {
    this.ra = ra;
    this.nome = nome;
  }

  public boolean verificaAluno() {
    if (this.ra != null && this.ra.equals("10"))
      return false;
    else
      return true;
  }

  public boolean verificaDebito() {
    try {
      Debito debito = new Debito(this.ra);
      return debito.verificaDebito();
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public String getRa() {
    return ra;
  }

  public void setRa(String ra) {
    this.ra = ra;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  @Override
  public String toString() {
    return this.ra + " - " + this.nome;
  }
}
