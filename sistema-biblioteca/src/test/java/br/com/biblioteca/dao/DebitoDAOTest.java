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

  // RA com letras para provar que o sistema aceita String
  private final String RA_DEVEDOR = "RA-2024-B";

  @Before
  public void setup() {
    InicializadorBanco.criarTabelas();
    debitoDAO = new DebitoDAO();
    alunoDAO = new AlunoDAO();

    try {
      alunoDAO.excluir(RA_DEVEDOR);
    } catch (Exception e) {
    }

    Aluno aluno = new Aluno(RA_DEVEDOR, "João Inadimplente");
    alunoDAO.salvar(aluno);
  }

  @Test
  public void testSalvarDebito() {
    // --- CENÁRIO ---
    Debito debito = new Debito(RA_DEVEDOR);
    debito.setValor(new BigDecimal("15.50"));
    debito.setDataDebito(new Date());

    // --- AÇÃO ---
    debitoDAO.salvar(debito);

    // --- VERIFICAÇÃO ---
    Assert.assertTrue("O ID do débito deve ser gerado", debito.getId() > 0);

    // Agora a chamada é direta, passando a String!
    List<Debito> debitosDoAluno = debitoDAO.listarPorAluno(RA_DEVEDOR);

    Assert.assertFalse("A lista de débitos não pode estar vazia", debitosDoAluno.isEmpty());

    Debito debitoSalvo = debitosDoAluno.get(0);

    Assert.assertEquals("O RA deve bater", RA_DEVEDOR, debitoSalvo.getRaAluno());

    // Comparação segura de BigDecimal
    Assert.assertEquals(0, new BigDecimal("15.50").compareTo(debitoSalvo.getValor()));
  }
}
