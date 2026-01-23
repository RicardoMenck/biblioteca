package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Emprestimo {

  private static final int QTD_MINIMA_PARA_BONUS = 2; // Acima de 2 livros
  private static final int DIAS_BONUS_POR_LIVRO = 2; // Ganha 2 dias por livro extra

  private Integer id;
  private Date dataEmprestimo;
  private Date dataPrevista;
  private Aluno aluno;

  private List<ItemEmprestimo> itens = new ArrayList<>();

  public Emprestimo() {
    this.dataEmprestimo = new Date();
  }

  public boolean emprestar(List<Livro> livrosSelecionados) {
    if (livrosSelecionados == null || livrosSelecionados.isEmpty()) {
      return false;
    }

    for (Livro livro : livrosSelecionados) {
      if (livro.verificarDisponibilidade()) {
        ItemEmprestimo item = new ItemEmprestimo(livro, this);
        this.itens.add(item);

        livro.setDisponivel(false);
      }
    }

    if (this.itens.isEmpty()) {
      return false;
    }

    this.definirDatasComRegraDeBonus();

    return true;
  }

  private void definirDatasComRegraDeBonus() {
    Date maiorDataEncontrada = this.dataEmprestimo;

    for (ItemEmprestimo item : this.itens) {
      if (item.getDataPrevista() != null && item.getDataPrevista().after(maiorDataEncontrada)) {
        maiorDataEncontrada = item.getDataPrevista();
      }
    }

    if (this.itens.size() > QTD_MINIMA_PARA_BONUS) {
      int livrosExtras = this.itens.size() - QTD_MINIMA_PARA_BONUS;
      int diasBonusTotal = livrosExtras * DIAS_BONUS_POR_LIVRO;

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(maiorDataEncontrada);
      calendar.add(Calendar.DAY_OF_MONTH, diasBonusTotal);

      maiorDataEncontrada = calendar.getTime();
    }

    for (ItemEmprestimo item : this.itens) {
      item.setDataPrevista(maiorDataEncontrada);
    }

    this.dataPrevista = maiorDataEncontrada;
  }

  public void adicionarItem(ItemEmprestimo item) {
    this.itens.add(item);
  }

  @Override
  public String toString() {
    return "Empréstimo #" + id + " - " + (aluno != null ? aluno.getNome() : "Sem Aluno");
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getDataEmprestimo() {
    return dataEmprestimo;
  }

  public void setDataEmprestimo(Date dataEmprestimo) {
    this.dataEmprestimo = dataEmprestimo;
  }

  public Date getDataPrevista() {
    return dataPrevista;
  }

  public void setDataPrevista(Date dataPrevista) {
    this.dataPrevista = dataPrevista;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public void setAluno(Aluno aluno) {
    this.aluno = aluno;
  }

  public List<ItemEmprestimo> getItens() {
    return itens;
  }

  public void setItens(List<ItemEmprestimo> itens) {
    this.itens = itens;
  }

}
