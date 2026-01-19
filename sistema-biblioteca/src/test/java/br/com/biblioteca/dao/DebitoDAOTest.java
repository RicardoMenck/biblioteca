package br.com.biblioteca.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.biblioteca.model.Aluno;
import br.com.biblioteca.model.Debito;
import br.com.biblioteca.util.InicializadorBanco;

public class DebitoDAOTest {

  private DebitoDAO debitoDAO;
  private AlunoDAO alunoDAO;

  // RA DE TESTE (Sem caracteres especiais para evitar problemas simples)
  private final String RA_TESTE = "99999";

  @Before
  public void setup() {
    // 1. Cria tabelas
    InicializadorBanco.criarTabelas();

    // 2. Instancia DAOs
    debitoDAO = new DebitoDAO();
    alunoDAO = new AlunoDAO();

    // 3. Limpeza de segurança
    try {
      alunoDAO.excluir(RA_TESTE);
    } catch (Exception e) {
    }

    // 4. CRIA ALUNO (Sem aluno, o débito falha por Chave Estrangeira!)
    Aluno aluno = new Aluno(RA_TESTE, "Aluno Teste Debito");
    alunoDAO.salvar(aluno);
  }

  @Test
  public void testSalvarDebito() {
    // --- CENÁRIO ---
    Debito debito = new Debito(RA_TESTE);
    debito.setValor(new BigDecimal("50.00")); // Valor fixo
    debito.setDataDebito(new Date());

    // --- AÇÃO ---
    debitoDAO.salvar(debito);

    // --- VERIFICAÇÃO ---
    // 1. Validar ID
    Assert.assertNotNull("ID não pode ser nulo", debito.getId());
    Assert.assertTrue("ID deve ser maior que 0", debito.getId() > 0);

    // 2. Validar Recuperação
    List<Debito> lista = debitoDAO.listarPorAluno(RA_TESTE);

    Assert.assertFalse("A lista de débitos não pode ser vazia", lista.isEmpty());

    Debito recuperado = lista.get(0);
    Assert.assertEquals("O RA deve bater", RA_TESTE, recuperado.getRaAluno());

    // Comparação de BigDecimal deve usar compareTo
    // (Isso evita erros onde 50.0 != 50.00)
    Assert.assertTrue("O valor deve ser 50.00",
        new BigDecimal("50.00").compareTo(recuperado.getValor()) == 0);
  }
}
