package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Devolucao {

  private Integer id;
  private Date dataDevolucao;
  private Aluno aluno; // Quem está devolvendo
  private Double multaTotal; // Soma das multas dos itens

  private List<ItemDevolucao> itens = new ArrayList<>();

  public Devolucao() {
    this.dataDevolucao = new Date(); // Data atual
    this.multaTotal = 0.0;
  }

  // --- MÉTODOS DE NEGÓCIO ---

  /**
   * Adiciona um item para ser devolvido.
   * Já dispara o cálculo de multa individual e atualiza o total.
   */
  public void adicionarItem(ItemEmprestimo itemEmprestimo) {
    // Cria o item de devolução vinculado a esta devolução
    ItemDevolucao itemDev = new ItemDevolucao(itemEmprestimo, this);

    // Calcula se tem multa nesse livro específico
    itemDev.calcularMulta();

    // Adiciona na lista
    this.itens.add(itemDev);

    // Atualiza o total do cabeçalho
    this.multaTotal += itemDev.getValorMulta();

    // IMPORTANTE: Atualiza o status do livro físico para disponível novamente!
    if (itemEmprestimo.getLivro() != null) {
      itemEmprestimo.getLivro().setDisponivel(true);
    }

    // IMPORTANTE: Marca o item do empréstimo original como devolvido
    itemEmprestimo.setDataDevolucao(this.dataDevolucao);
  }

  /**
   * Gera um objeto Débito se houver multa total.
   * 
   * @return Objeto Debito ou null se não houver multa.
   */
  public Debito gerarDebito() {
    if (this.multaTotal > 0 && this.aluno != null) {
      return new Debito(this.aluno.getId(), this.multaTotal, this.dataDevolucao);
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

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public Double getMultaTotal() {
    return multaTotal;
  }

  public void setMultaTotal(Double multaTotal) {
    this.multaTotal = multaTotal;
  }

  public List<ItemDevolucao> getItens() {
    return itens;
  }

  public void setItens(List<ItemDevolucao> itens) {
    this.itens = itens;
  }
}
