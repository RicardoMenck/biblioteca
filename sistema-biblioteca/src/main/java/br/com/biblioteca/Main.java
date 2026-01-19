package br.com.biblioteca;

import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.dao.AlunoDAO;
import br.com.biblioteca.dao.EmprestimoDAO;
import br.com.biblioteca.dao.LivroDAO;
import br.com.biblioteca.dao.TituloDAO;
import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.ItemEmprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.InicializadorBanco;

public class Main {
  public static void main(String[] args) {
    // 1. Limpa tudo para o teste visual
    InicializadorBanco.criarTabelas();
    System.out.println("--- SISTEMA INICIADO ---");

    // 2. Cria os Dados Básicos
    Titulo t1 = new Titulo();
    t1.setNome("Clean Code");
    t1.setPrazo(7);
    new TituloDAO().salvar(t1);

    Titulo t2 = new Titulo();
    t2.setNome("O Senhor dos Anéis");
    t2.setPrazo(14);
    new TituloDAO().salvar(t2);

    Livro l1 = new Livro(t1.getId());
    l1.setTitulo(t1);
    l1.setExemplarBiblioteca(false);
    new LivroDAO().salvar(l1); // Exemplar do Clean Code

    Livro l2 = new Livro(t2.getId());
    l2.setTitulo(t2);
    l2.setExemplarBiblioteca(false);
    new LivroDAO().salvar(l2); // Exemplar do Senhor dos Anéis

    Aluno aluno = new Aluno("2024-TESTE", "Maria Developer");
    new AlunoDAO().salvar(aluno);

    // 3. Realiza o Empréstimo (Levando os 2 livros)
    Emprestimo emp = new Emprestimo();
    emp.setNome(aluno.getRa());
    List<Livro> livros = new ArrayList<>();
    livros.add(l1);
    livros.add(l2);
    emp.emprestar(livros);

    System.out.println("Salvnado empréstimo...");
    new EmprestimoDAO().salvar(emp);

    // 4. LEITURA FINAL (A Prova Real)
    System.out.println("\n--- RELATÓRIO DE EMPRÉSTIMOS ---");
    List<Emprestimo> lista = new EmprestimoDAO().listarTodos();

    for (Emprestimo e : lista) {
      System.out.println("ID Empréstimo: " + e.getEmprestimo());
      System.out.println("Aluno: " + e.getNome());
      System.out.println("Data Prevista: " + e.getDataPrevista());
      System.out.println("Itens Levados (" + e.getItens().size() + "):");

      for (ItemEmprestimo item : e.getItens()) {
        System.out.println("   |__ Livro ID: " + item.getLivro().getId());
        System.out.println("   |   Obra: " + item.getLivro().getTitulo().getNome());
        System.out.println("   |   Prazo Original: " + item.getLivro().getTitulo().getPrazo() + " dias");
        System.out.println("   |__ -----------------");
      }
    }
  }
}
