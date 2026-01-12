package br.com.biblioteca.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.InicializadorBanco;

public class TituloDAOTest {

  private TituloDAO dao;

  @Before
  public void setup() {
    // 1. Garante tabela criada
    InicializadorBanco.criarTabelas();

    // 2. Inicializa DAO
    dao = new TituloDAO();
  }

  @Test
  public void testSalvarEBuscarPorId() {
    // --- CENÁRIO ---
    Titulo titulo = new Titulo();
    titulo.setNome("Engenharia de Software - O Livro Teste");
    titulo.setPrazo(14); // 14 dias

    // --- AÇÃO ---
    dao.salvar(titulo);

    // --- VERIFICAÇÃO ---

    // 1. Verifica se o ID foi gerado (Autoincrement funciona?)
    Assert.assertTrue("O ID do título deve ser maior que 0 após salvar", titulo.getId() > 0);

    // 2. Verifica se salvou corretamente buscando no banco
    Titulo tituloSalvo = dao.buscarPorId(titulo.getId());

    Assert.assertNotNull("O título deveria ser encontrado no banco", tituloSalvo);
    Assert.assertEquals("O nome deve ser igual ao salvo", "Engenharia de Software - O Livro Teste",
        tituloSalvo.getNome());
    Assert.assertEquals("O prazo deve ser igual ao salvo", 14, tituloSalvo.getPrazo());
  }

  @Test
  public void testListarTodos() {
    // Cria um dado novo para garantir que a lista não esteja vazia
    Titulo t = new Titulo();
    t.setNome("Livro Extra para Listagem");
    t.setPrazo(7);
    dao.salvar(t);

    List<Titulo> lista = dao.listarTodos();

    Assert.assertNotNull("A lista não pode ser nula", lista);
    Assert.assertTrue("A lista deve conter pelo menos 1 elemento", lista.size() > 0);
  }
}
