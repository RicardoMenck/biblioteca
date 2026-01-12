package br.com.biblioteca.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.InicializadorBanco;

public class EmprestimoDAOTest {

  private EmprestimoDAO emprestimoDAO;
  private AlunoDAO alunoDAO;
  private LivroDAO livroDAO;
  private TituloDAO tituloDAO;

  // Constantes para o cenário de teste
  private final String RA_TESTE = "2026-TESTE-EMP";
  private int idTituloTeste;
  private int idLivroTeste1;
  private int idLivroTeste2;

  @Before
  public void setup() {
    // 1. Infraestrutura
    InicializadorBanco.criarTabelas();

    // 2. Inicializa DAOs
    emprestimoDAO = new EmprestimoDAO();
    alunoDAO = new AlunoDAO();
    livroDAO = new LivroDAO();
    tituloDAO = new TituloDAO();

    // 3. Limpeza preventiva (para poder rodar o teste várias vezes)
    try {
      alunoDAO.excluir(RA_TESTE);
    } catch (Exception e) {
    }

    // 4. PREPARAÇÃO DO CENÁRIO (O "Mundo" do Teste)

    // A) Criar o Aluno
    Aluno aluno = new Aluno(RA_TESTE, "Aluno Teste Transação");
    alunoDAO.salvar(aluno);

    // B) Criar a Obra (Título)
    Titulo titulo = new Titulo();
    titulo.setNome("Arquitetura Limpa");
    titulo.setPrazo(7);
    tituloDAO.salvar(titulo);
    this.idTituloTeste = titulo.getId();

    // C) Criar 2 Exemplares (Livros) para esse Título
    Livro l1 = new Livro(idTituloTeste);
    l1.setTitulo(titulo);
    l1.setExemplarBiblioteca(false);
    livroDAO.salvar(l1);
    this.idLivroTeste1 = l1.getId();

    Livro l2 = new Livro(idTituloTeste);
    l2.setTitulo(titulo);
    l2.setExemplarBiblioteca(false);
    livroDAO.salvar(l2);
    this.idLivroTeste2 = l2.getId();
  }

  @Test
  public void testSalvarEmprestimoComTransacao() {
    // --- CENÁRIO ---

    // 1. Recupera os objetos do banco (simulando a tela selecionando)
    Aluno aluno = alunoDAO.buscarPorRa(RA_TESTE);
    Livro livro1 = livroDAO.buscaPorId(idLivroTeste1);
    Livro livro2 = livroDAO.buscaPorId(idLivroTeste2);

    // 2. Monta a lista de livros que o aluno vai levar
    List<Livro> livrosSelecionados = new ArrayList<>();
    livrosSelecionados.add(livro1);
    livrosSelecionados.add(livro2);

    // 3. Cria o objeto Empréstimo e roda a Regra de Negócio
    Emprestimo emprestimo = new Emprestimo();
    emprestimo.setNome(aluno.getRa()); // Vincula ao RA

    // O método 'emprestar' calcula as datas e cria os ItemEmprestimo internamente
    boolean regraOk = emprestimo.emprestar(livrosSelecionados);

    Assert.assertTrue("A regra de negócio deve permitir o empréstimo", regraOk);
    Assert.assertEquals("Devem ter sido criados 2 itens", 2, emprestimo.getItens().size());

    // --- AÇÃO (TESTE DA TRANSAÇÃO) ---
    emprestimoDAO.salvar(emprestimo);

    // --- VERIFICAÇÃO ---

    // 1. O ID do empréstimo foi gerado?
    Assert.assertTrue("ID do empréstimo deve ser gerado", emprestimo.getEmprestimo() > 0);

    // 2. O empréstimo foi salvo no banco?
    List<Emprestimo> listaBanco = emprestimoDAO.listarTodos();
    Emprestimo emprestimoSalvo = null;

    for (Emprestimo e : listaBanco) {
      if (e.getEmprestimo() == emprestimo.getEmprestimo()) {
        emprestimoSalvo = e;
        break;
      }
    }
    Assert.assertNotNull("O empréstimo deve estar no banco", emprestimoSalvo);

    // 3. PROVA DE FOGO: Os Itens vieram junto? (Eager Loading)
    Assert.assertNotNull("A lista de itens não pode ser nula", emprestimoSalvo.getItens());
    Assert.assertEquals("Devem vir 2 itens do banco", 2, emprestimoSalvo.getItens().size());

    // 4. Valida se os dados do livro dentro do item estão corretos (JOIN
    // funcionou?)
    String nomeObra = emprestimoSalvo.getItens().get(0).getLivro().getTitulo().getNome();
    Assert.assertEquals("O nome do livro deve ser 'Arquitetura Limpa'", "Arquitetura Limpa", nomeObra);
  }
}
