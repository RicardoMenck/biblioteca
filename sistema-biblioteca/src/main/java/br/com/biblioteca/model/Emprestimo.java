package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Emprestimo {

  private static final int QTD_MINIMA_PARA_BONUS = 2; // Acima de 2 livros
  private static final int DIAS_BONUS_POR_LIVRO = 2; // Ganha 2 dias por livro extra

  private Integer id; // id
  private Date dataEmprestimo;
  private Date dataPrevista;
  private Aluno aluno;

  // Lista de itens
  private List<ItemEmprestimo> itens = new ArrayList<>();

  public Emprestimo() {
    this.dataEmprestimo = new Date(); // Define data atual ao criar
  }

  public boolean emprestar(List<Livro> livrosSelecionados) {
    if (livrosSelecionados == null || livrosSelecionados.isEmpty()) {
      return false;
    }

    // 1. Cria os itens baseados nos livros
    for (Livro livro : livrosSelecionados) {
      // Verifica se o livro pode ser emprestado (Disponível e não é exemplar de
      // consulta)
      if (livro.verificarDisponibilidade()) {
        ItemEmprestimo item = new ItemEmprestimo(livro, this);
        this.itens.add(item);

        // Marca o livro como indisponível na memória imediatamente
        livro.setDisponivel(false);
      }
    }

    // Se nenhum livro pôde ser adicionado, retorna falso
    if (this.itens.isEmpty()) {
      return false;
    }

    // Passo 2: Calcular data final considerando a regra de bônus
    this.definirDatasComRegraDeBonus();

    return true;
  }

  /**
   * Lógica de cálculo de datas com regra de bônus.
   * (Sua implementação com correções de sintaxe Java)
   */
  /**
   * Lógica segregada para cálculo de datas.
   * Evita números mágicos e centraliza a regra de negócio.
   */
  private void definirDatasComRegraDeBonus() {
    Date maiorDataEncontrada = this.dataEmprestimo;

    // 1. Encontra a data mais distante entre os livros selecionados
    for (ItemEmprestimo item : this.itens) {
      // Usa a data que o item já calculou ao nascer
      if (item.getDataPrevista() != null && item.getDataPrevista().after(maiorDataEncontrada)) {
        maiorDataEncontrada = item.getDataPrevista();
      }
    }

    // 2. Aplica a REGRA DE BÔNUS (Se levar mais de 2 livros)
    if (this.itens.size() > QTD_MINIMA_PARA_BONUS) {
      int livrosExtras = this.itens.size() - QTD_MINIMA_PARA_BONUS;
      int diasBonusTotal = livrosExtras * DIAS_BONUS_POR_LIVRO;

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(maiorDataEncontrada);
      calendar.add(Calendar.DAY_OF_MONTH, diasBonusTotal);

      maiorDataEncontrada = calendar.getTime(); // Nova data estendida
    }

    // 3. Unifica os prazos: Todos os itens vencem na mesma data (a bonificada)
    for (ItemEmprestimo item : this.itens) {
      item.setDataPrevista(maiorDataEncontrada);
    }

    // Atualiza a data do cabeçalho do empréstimo
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
