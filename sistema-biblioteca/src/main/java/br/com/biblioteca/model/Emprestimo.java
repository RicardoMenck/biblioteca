package br.com.biblioteca.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Emprestimo {
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

    // 2. Chama o SEU método de cálculo para definir as datas
    this.dataPrevista = this.calculaDataDevolucao();

    return true;
  }

  /**
   * Lógica de cálculo de datas com regra de bônus.
   * (Sua implementação com correções de sintaxe Java)
   */
  private Date calculaDataDevolucao() {
    Date date = this.dataEmprestimo; // Data base é a do empréstimo (hoje)
    Date dataPrevistaCalc = this.dataEmprestimo; // Inicializa segura para comparação

    // 1. Itera para encontrar a data mais distante entre os livros
    for (ItemEmprestimo item : this.itens) {
      // O Item calcula a data dele baseada no prazo do título
      Date data_aux = item.calculaDataDevolucao(date);

      // Se a data calculada do item for maior que a prevista atual, atualiza
      if (data_aux.compareTo(dataPrevistaCalc) > 0) {
        dataPrevistaCalc = data_aux;
      }
    }

    // 2. Regra extra: Se levar mais de 2 livros, ganha prazo extra
    if (this.itens.size() > 2) {
      int tam = this.itens.size() - 2;
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dataPrevistaCalc);
      calendar.add(Calendar.DATE, (tam * 2)); // Adiciona 2 dias para cada livro extra além de 2
      dataPrevistaCalc = calendar.getTime();
    }

    // 3. Atualiza a data PREVISTA em todos os itens para ficarem iguais ao
    // empréstimo
    // Assim, todos os livros desse pacote devem ser devolvidos juntos na data
    // bonificada
    for (ItemEmprestimo item : this.itens) {
      item.setDataPrevista(dataPrevistaCalc);
      // OBS: Não usamos setDataDevolucao aqui para não dar baixa automática
    }

    return dataPrevistaCalc;
  }

  public void adicionarItem(ItemEmprestimo item) {
    item.setEmprestimo(this);
    this.itens.add(item);
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

  // Get e Set

}
