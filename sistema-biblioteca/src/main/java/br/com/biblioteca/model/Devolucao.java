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

  /**
   * Método "Mágico" para Cardinalidade 1:1.
   * Recebe o Empréstimo e processa a devolução de TODOS os itens dele.
   */
  public void fecharEmprestimo(Emprestimo emprestimo) {
    this.emprestimo = emprestimo;
    this.dataDevolucao = new Date(); // Data de hoje
    this.multaTotal = 0.0;

    // Se o empréstimo não tiver itens, não faz nada
    if (emprestimo == null || emprestimo.getItens() == null) {
      return;
    }

    // Itera sobre os itens do EMPRÉSTIMO para gerar os itens da DEVOLUÇÃO
    for (ItemEmprestimo itemEmp : emprestimo.getItens()) {

      // 1. Cria o item de devolução (que já calcula a multa individualmente)
      ItemDevolucao itemDev = new ItemDevolucao(itemEmp, this);

      // 2. Adiciona na lista desta devolução
      this.itens.add(itemDev);

      // 3. Soma ao total
      this.multaTotal += itemDev.getValorMulta();

      // 4. Baixa no estoque (Livro volta a ficar disponível)
      if (itemEmp.getLivro() != null) {
        itemEmp.getLivro().setDisponivel(true);
      }

      // 5. Marca o item original como devolvido
      itemEmp.setDataDevolucao(this.dataDevolucao);
    }
  }

  /**
   * Gera o Débito para o Aluno dono do Empréstimo.
   * Note que pegamos o aluno INDIRETAMENTE via getEmprestimo().getAluno()
   */
  public Debito gerarDebito() {
    if (this.multaTotal > 0 && this.emprestimo != null && this.emprestimo.getAluno() != null) {
      return new Debito(
          this.emprestimo.getAluno().getId(), // ID do aluno vem do empréstimo
          this.multaTotal,
          this.dataDevolucao);
    }
    return null;
  }

  // --- GETTERS E SETTERS ---

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
