package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Emprestimo {
  private int emprestimo; // id
  private String nome; // ra
  private Date dataEmprestimo = new Date();
  private Date dataPrevista = new Date();
  private Date data_aux = new Date();

  // Lista de itens
  private List<ItemEmprestimo> itens = new ArrayList<>();

  public Emprestimo() {
  }

  public Emprestimo(String raAluno) {
    this.nome = raAluno;
    this.dataEmprestimo = new Date();
  }

  public boolean emprestar(List<Livro> livros) {
    // Adiciona um novo item para cada livro passado
    for (Livro livro : livros) {
      ItemEmprestimo novoItem = new ItemEmprestimo(livro);
      novoItem.setEmprestimo(this); // Garante a referência bidirecional
      this.itens.add(novoItem);
    }

    // Chama o método para calcular a data de devolução
    calculaDataDevolucao();

    // Logs para conferência (Mantido do original)
    System.out.print("\nNumero de Livros Emprestados: " + this.itens.size());
    System.out.print("\nData de Empréstimo: " + this.dataEmprestimo);
    System.out.print("\nData de Devolução Calculada: " + this.dataPrevista);

    return true;
  }

  private Date calculaDataDevolucao() {
    Date date = new Date(); // Data base é hoje

    // 1. Itera para encontrar a data mais distante entre os livros
    for (ItemEmprestimo item : this.itens) {
      data_aux = item.calculaDataDevolucao(date);

      // Se a data calculada do item for maior que a prevista atual, atualiza
      if (dataPrevista.compareTo(data_aux) < 0) {
        dataPrevista = data_aux;
      }
    }

    // 2. Regra extra: Se levar mais de 2 livros, ganha prazo extra
    if (this.itens.size() > 2) {
      int tam = this.itens.size() - 2;
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dataPrevista);
      calendar.add(Calendar.DATE, (tam * 2)); // Adiciona 2 dias para cada livro extra
      dataPrevista = calendar.getTime();
    }

    // 3. Atualiza a data de devolução em todos os itens para ficarem iguais ao
    // empréstimo
    for (ItemEmprestimo item : this.itens) {
      item.setDataDevolucao(dataPrevista);
    }

    return dataPrevista;
  }

  public void adicionarItem(ItemEmprestimo item) {
    item.setEmprestimo(this);
    this.itens.add(item);
  }

  // Get e Set

  public int getEmprestimo() {
    return emprestimo;
  }

  public void setEmprestimo(int emprestimo) {
    this.emprestimo = emprestimo;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
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

  public Date getData_aux() {
    return data_aux;
  }

  public void setData_aux(Date data_aux) {
    this.data_aux = data_aux;
  }

  public List<ItemEmprestimo> getItens() {
    return itens;
  }

  public void setItens(List<ItemEmprestimo> itens) {
    this.itens = itens;
  }

}
