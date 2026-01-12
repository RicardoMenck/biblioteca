package br.com.biblioteca.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.InicializadorBanco;

public class LivroDAOTest {

  private LivroDAO livroDAO;
  private TituloDAO tituloDAO;

  @Before
  public void setup() {
    // 1. Garante a infraestrutura (Tabelas criadas)
    InicializadorBanco.criarTabelas();

    // 2. Inicializa os DAOs
    livroDAO = new LivroDAO();
    tituloDAO = new TituloDAO();

    // Dica Sênior: Não precisamos limpar o banco inteiro aqui se usarmos dados
    // novos,
    // mas em sistemas reais usaríamos um banco em memória (H2) ou @Transactional
    // rollback.
  }

  @Test
  public void testSalvarLivroComTituloVinculado() {
    // --- PREPARAÇÃO (Cenário) ---
    // Passo 1: Precisamos de um Título (Obra) já salvo no banco
    Titulo titulo = new Titulo();
    titulo.setNome("Livro Teste JUnit - Volume 1");
    titulo.setPrazo(7);

    // Persiste o Pai (Titulo) para gerar o ID
    tituloDAO.salvar(titulo);

    // Validação defensiva do teste
    Assert.assertTrue("O Título precisa ter gerado um ID", titulo.getId() > 0);

    // Passo 2: Criar o Livro (Exemplar) vinculado a este Título
    Livro livro = new Livro(titulo.getId()); // Construtor do professor
    livro.setTitulo(titulo); // Vínculo do objeto completo
    livro.setExemplarBiblioteca(false);

    // --- AÇÃO ---
    livroDAO.salvar(livro);

    // --- VERIFICAÇÃO ---
    Assert.assertTrue("O Livro deve ter gerado um ID após salvar", livro.getId() > 0);

    // Verificação de Leitura (Round-trip)
    Livro livroSalvo = livroDAO.buscaPorId(livro.getId());
    Assert.assertNotNull("O livro deve ser encontrado no banco", livroSalvo);

    // Teste do JOIN (Se o título veio junto)
    Assert.assertNotNull("O Título dentro do Livro não pode ser nulo (Eager Loading)", livroSalvo.getTitulo());
    Assert.assertEquals("O nome do título deve bater", "Livro Teste JUnit - Volume 1",
        livroSalvo.getTitulo().getNome());
  }
}
