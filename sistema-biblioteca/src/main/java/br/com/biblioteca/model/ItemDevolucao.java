package br.com.biblioteca.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ItemDevolucao {

  private Integer id;
  private ItemEmprestimo itemEmprestimo; // Referência ao empréstimo original
  private Devolucao devolucao; // Referência ao cabeçalho (Pai)
  private Date dataDevolucao; // Data real que entregou
  private Double valorMulta; // Multa específica deste livro
  private int diasAtraso; // Quantos dias atrasou

  // Configuração de multa (poderia vir de um arquivo de config/banco)
  private static final double MULTA_DIARIA = 2.50;

  public ItemDevolucao() {
  }

  public ItemDevolucao(ItemEmprestimo itemEmprestimo, Devolucao devolucao) {
    this.itemEmprestimo = itemEmprestimo;
    this.devolucao = devolucao;
    this.dataDevolucao = new Date(); // Data de hoje (momento da criação)
    this.valorMulta = 0.0;
    this.diasAtraso = 0;
  }

  // --- MÉTODOS DE NEGÓCIO ---

  /**
   * Calcula se houve atraso e define o valor da multa.
   * Compara a Data Prevista (do Empréstimo) com a Data Real (desta Devolução).
   */
  public void calcularMulta() {
    if (itemEmprestimo == null || itemEmprestimo.getDataPrevista() == null) {
      return;
    }

    Date dataPrevista = itemEmprestimo.getDataPrevista();

    // Se a data de devolução for depois da prevista
    if (this.dataDevolucao.after(dataPrevista)) {
      // Cálculo da diferença em milissegundos
      long diffEmMillies = Math.abs(this.dataDevolucao.getTime() - dataPrevista.getTime());
      // Converte para dias
      long diffEmDias = TimeUnit.DAYS.convert(diffEmMillies, TimeUnit.MILLISECONDS);

      this.diasAtraso = (int) diffEmDias;
      this.valorMulta = this.diasAtraso * MULTA_DIARIA;
    } else {
      this.diasAtraso = 0;
      this.valorMulta = 0.0;
    }
  }

  // --- GETTERS E SETTERS ---

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ItemEmprestimo getItemEmprestimo() {
    return itemEmprestimo;
  }

  public void setItemEmprestimo(ItemEmprestimo itemEmprestimo) {
    this.itemEmprestimo = itemEmprestimo;
  }

  public Devolucao getDevolucao() {
    return devolucao;
  }

  public void setDevolucao(Devolucao devolucao) {
    this.devolucao = devolucao;
  }

  public Date getDataDevolucao() {
    return dataDevolucao;
  }

  public void setDataDevolucao(Date dataDevolucao) {
    this.dataDevolucao = dataDevolucao;
  }

  public Double getValorMulta() {
    return valorMulta;
  }

  public void setValorMulta(Double valorMulta) {
    this.valorMulta = valorMulta;
  }

  public int getDiasAtraso() {
    return diasAtraso;
  }
}
