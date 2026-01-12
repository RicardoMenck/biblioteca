package br.com.biblioteca.dao;

import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.util.InicializadorBanco; // <--- Importante!
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AlunoDAOTest {

  private AlunoDAO dao;

  @Before
  public void setup() {
    // 1. A SOLUÇÃO DO SEU ERRO ESTÁ AQUI:
    // Cria as tabelas se elas não existirem antes de cada teste
    InicializadorBanco.criarTabelas();

    // 2. Inicializa o DAO
    dao = new AlunoDAO();

    // 3. Limpa dados de testes anteriores para evitar duplicidade
    dao.excluir("TESTE-01");
  }

  @Test
  public void testSalvarAluno() {
    // 1. Cenário
    Aluno aluno = new Aluno("TESTE-01", "Aluno Teste JUnit 4");

    // 2. Ação
    dao.salvar(aluno);

    // 3. Verificação
    Aluno alunoSalvo = dao.buscarPorRa("TESTE-01");

    Assert.assertNotNull("O aluno não deveria ser nulo", alunoSalvo);
    Assert.assertEquals("O nome deve ser igual", "Aluno Teste JUnit 4", alunoSalvo.getNome());
  }

  @Test
  public void testListarTodos() {
    List<Aluno> lista = dao.listarTodos();
    Assert.assertNotNull("A lista não pode ser nula", lista);
  }
}