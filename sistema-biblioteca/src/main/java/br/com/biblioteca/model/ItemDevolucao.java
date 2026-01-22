package br.com.biblioteca.model;

import java.util.Date;

public class ItemDevolucao {

  // Configuração de banco no futuro
  private static final double VALOR_MULTA_DIARIA = 2.00;

  private Integer id;
  private Date dataDevolucao;
  private Integer diasAtraso;
  private Double valorMulta;

  private ItemEmprestimo itemEmprestimo;
  private Devolucao devolucao;

  public ItemDevolucao() {
  }

  public ItemDevolucao(ItemEmprestimo itemEmprestimo, Devolucao devolucao) {
    this.itemEmprestimo = itemEmprestimo;
    this.devolucao = devolucao;
    this.dataDevolucao = new Date(); // Assume que devolveu "agora"
    this.diasAtraso = 0;
    this.valorMulta = 0.0;
    this.calcularMulta();
  }

  // --- MÉTODOS DE NEGÓCIO ---

  /**
   * Calcula se houve atraso e define o valor da multa.
   * Compara a Data Prevista (do Empréstimo) com a Data Real (desta Devolução).
   */
  public void calcularMulta() {
    if (itemEmprestimo == null || itemEmprestimo.getDataPrevista() == null) {
      this.zerarMulta();
      return;
    }

    Date prevista = itemEmprestimo.getDataPrevista();
    Date entrega = this.dataDevolucao;

    // Se a entrega foi ANTES ou IGUAL à prevista, não tem multa
    if (!entrega.after(prevista)) {
      this.zerarMulta();
      return;
    }

    // Pega a diferença em milissegundos
    long diferenca = entrega.getTime() - prevista.getTime();

    // Converte para dias (divide pelos milissegundos de um dia: 86.400.000)
    // (1000ms * 60s * 60m * 24h)
    int dias = (int) (diferenca / (1000 * 60 * 60 * 24));

    this.diasAtraso = dias;
    this.valorMulta = dias * VALOR_MULTA_DIARIA;
  }

  private void zerarMulta() {
    this.diasAtraso = 0;
    this.valorMulta = 0.0;
  }

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
    // Se alterar a data de devolução manualmente, recalcula a multa
    this.calcularMulta();
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
