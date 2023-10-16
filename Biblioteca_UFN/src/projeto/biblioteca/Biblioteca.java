package projeto.biblioteca;

import java.util.ArrayList;

public class Biblioteca {

    private ArrayList<Livro> catalogo;

    //Método construtor
    public Biblioteca() {
        this.catalogo = new ArrayList<Livro>(); // Cria um novo objeto Livro no array catalogo
    }

    // Método da classe

    public void adicionarLivro(Livro livro) {
        this.catalogo.add(livro); // Adiciona um livro no catálogo
    }

    public void removerLivro(String isbn) {
        catalogo.removeIf(livro -> livro.getIsbn().equals(isbn));
    }

    public ArrayList<Livro> buscarTitulo(String titulo) {
        // Retornando uma lista com todos os livros que contêm o título especificado
        ArrayList<Livro> resultado = new ArrayList<Livro>(); //Cria um array resultado, para armazenar a busca
        for(Livro livro : this.catalogo) {
            if(livro.getTitulo().contains(titulo)){
                resultado.add(livro);
            }
        }
        return resultado;
    }

    public boolean emprestarLivro(String isbn) {
        for(Livro livro : catalogo) {
            if(livro.getIsbn().equals(isbn)) {
                return livro.reservar();
            }
        }
        return false;
    }

    public void devolverLivro(String isbn) {
        for(Livro livro : this.catalogo) {
           if(livro.getIsbn().equals(isbn)) {
               livro.devolver();
               break;
           }
        }
    }

    public ArrayList<Livro> listarLivros() {
        return this.catalogo;
    }
}
