package br.com.biblioteca.model;

import java.util.Calendar;
import java.util.Date;

public class ItemEmprestimo {
  private Integer id;
  private Date dataDevolucao;
  private Date dataPrevista;

  private Livro livro;
  private Emprestimo emprestimo;

  public ItemEmprestimo() {
  }

  public ItemEmprestimo(Livro livro, Emprestimo emprestimo) {
    this.livro = livro;
    this.emprestimo = emprestimo;
    if (emprestimo != null) {
      this.calculaDataDevolucao(emprestimo.getDataEmprestimo());
    }
  }

  public Date calculaDataDevolucao(Date dataInicio) {
    if (this.livro == null) {
      return null;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dataInicio);
    // Pega o prazo específico deste livro (Ex: 7 dias, 14 dias...)
    int diasPrazo = this.livro.verPrazo();
    // Soma os dias
    calendar.add(Calendar.DAY_OF_MONTH, diasPrazo);
    // Grava na variável PREVISTA (O alvo a ser cumprido)
    this.dataPrevista = calendar.getTime();

    return this.dataPrevista;
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

  public Date getDataPrevista() {
    return dataPrevista;
  }

  public void setDataPrevista(Date dataPrevista) {
    this.dataPrevista = dataPrevista;
  }

}
