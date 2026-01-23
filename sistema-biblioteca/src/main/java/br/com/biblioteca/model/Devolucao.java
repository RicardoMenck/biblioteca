package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Devolucao {

  private Integer id;
  private Date dataDevolucao;
  private Double multaTotal;

  private Emprestimo emprestimo;

  private List<ItemDevolucao> itens = new ArrayList<>();

  public Devolucao() {
  }

  public void fecharEmprestimo(Emprestimo emprestimo) {
    this.emprestimo = emprestimo;
    this.dataDevolucao = new Date();
    this.multaTotal = 0.0;

    if (emprestimo == null || emprestimo.getItens() == null) {
      return;
    }

    for (ItemEmprestimo itemEmp : emprestimo.getItens()) {

      ItemDevolucao itemDev = new ItemDevolucao(itemEmp, this);

      this.itens.add(itemDev);

      this.multaTotal += itemDev.getValorMulta();

      if (itemEmp.getLivro() != null) {
        itemEmp.getLivro().setDisponivel(true);
      }

      itemEmp.setDataDevolucao(this.dataDevolucao);
    }
  }

  public Debito gerarDebito() {
    if (this.multaTotal > 0 && this.emprestimo != null && this.emprestimo.getAluno() != null) {
      return new Debito(
          this.emprestimo.getAluno().getId(),
          this.multaTotal,
          this.dataDevolucao);
    }
    return null;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getDataDevolucao() {
    return dataDevolucao;
  }

  public void setDataDevolucao(Date dataDevolucao) {
    this.dataDevolucao = dataDevolucao;
  }

  public Double getMultaTotal() {
    return multaTotal;
  }

  public void setMultaTotal(Double multaTotal) {
    this.multaTotal = multaTotal;
  }

  public Emprestimo getEmprestimo() {
    return emprestimo;
  }

  public void setEmprestimo(Emprestimo emprestimo) {
    this.emprestimo = emprestimo;
  }

  public List<ItemDevolucao> getItens() {
    return itens;
  }

  public void setItens(List<ItemDevolucao> itens) {
    this.itens = itens;
  }

  @Override
  public String toString() {
    return "Devolução do Emp. #" + (emprestimo != null ? emprestimo.getId() : "?");
  }
}
