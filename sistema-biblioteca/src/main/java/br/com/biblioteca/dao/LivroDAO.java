package br.com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importante para as chaves geradas
import java.util.ArrayList;
import java.util.List;

import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Titulo;
import br.com.biblioteca.util.ConexaoBD;

public class LivroDAO {

  public void salvar(Livro livro) {
    // Validação: Não permite salvar sem um título válido
    if (livro.getTitulo() == null || livro.getTitulo().getId() == 0) {
      throw new RuntimeException("Erro: O livro precisa de um Título já cadastrado para ser salvo.");
    }

    String sql = "INSERT INTO livro(id_titulo, exemplar_biblioteca) VALUES (?, ?)";

    // Try-with-resources: Fecha o PreparedStatement automaticamente
    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      // --- CORREÇÃO AQUI ---
      // O 1º parâmetro é 'id_titulo', então pegamos do objeto Titulo
      comando.setInt(1, livro.getTitulo().getId());

      // O 2º parâmetro é o boolean convertido
      comando.setInt(2, livro.isExemplarBiblioteca() ? 1 : 0);

      comando.executeUpdate();

      // Recupera o ID gerado para o Livro
      try (ResultSet rs = comando.getGeneratedKeys()) {
        if (rs.next()) {
          livro.setId(rs.getInt(1));
        }
      }

      System.out.println("Exemplar (Livro) salvo com sucesso para o título ID: " + livro.getTitulo().getId());

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao salvar livro: " + e.getMessage());
    }
  }

  public List<Livro> listarTodos() {
    List<Livro> livros = new ArrayList<>();
    String sql = """
          SELECT livro.id as id_livro, livro.exemplar_biblioteca,
                 titulo.id as id_titulo, titulo.nome as nome_titulo, titulo.prazo
            FROM livro
           INNER JOIN titulo ON livro.id_titulo = titulo.id;
        """;

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql);
        ResultSet rs = comando.executeQuery()) {

      while (rs.next()) {
        // Monta o objeto Titulo
        Titulo t = new Titulo();
        t.setId(rs.getInt("id_titulo"));
        t.setNome(rs.getString("nome_titulo"));
        t.setPrazo(rs.getInt("prazo"));

        // Monta o objeto Livro com a composição
        Livro l = new Livro();
        l.setId(rs.getInt("id_livro"));
        l.setExemplarBiblioteca(rs.getInt("exemplar_biblioteca") == 1);
        l.setTitulo(t);

        livros.add(l);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao listar livros: " + e.getMessage());
    }
    return livros;
  }

  public Livro buscaPorId(int id) {
    String sql = """
        SELECT livro.id as id_livro, livro.exemplar_biblioteca,
               titulo.id as id_titulo, titulo.nome as nome_titulo, titulo.prazo
          FROM livro
         INNER JOIN titulo ON livro.id_titulo = titulo.id
         WHERE livro.id = ?
        """;

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setInt(1, id);

      try (ResultSet rs = comando.executeQuery()) {
        if (rs.next()) {
          Titulo t = new Titulo();
          t.setId(rs.getInt("id_titulo"));
          t.setNome(rs.getString("nome_titulo"));
          t.setPrazo(rs.getInt("prazo"));

          Livro l = new Livro();
          l.setId(rs.getInt("id_livro"));
          l.setExemplarBiblioteca(rs.getInt("exemplar_biblioteca") == 1);
          l.setTitulo(t);
          return l;
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao buscar livro: " + e.getMessage());
    }

    return null;
  }

  public void excluir(int id) {
    String sql = "DELETE FROM livro WHERE id = ?";

    try (Connection conexao = ConexaoBD.getInstancia().getConexao();
        PreparedStatement comando = conexao.prepareStatement(sql)) {

      comando.setInt(1, id);
      comando.executeUpdate();
      System.out.println("Livro excluído com sucesso!");

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao excluir livro: " + e.getMessage());
    }
  }
}
