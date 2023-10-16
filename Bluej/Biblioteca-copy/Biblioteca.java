
import java.util.ArrayList;

    public class Biblioteca {
    
            private ArrayList<Livro> catalogo;
	 	// Construtor
	    public Biblioteca() {
	        this.catalogo = new ArrayList<Livro>();
	    }

        	 // Métodos 
	    public void adicionar_livro(Livro livro) {
	        this.catalogo.add(livro); // Adicionando um livro ao catálogo
	    }
	    
	    
	    public void remover_livro(String isbn) {
	        // Removendo um livro do catálogo com base no ISBN
	        for (int i = 0; i < this.catalogo.size(); i++) { // Percorrendo o catálogo
	            Livro livro = this.catalogo.get(i); // Obtendo o livro na posição i
	            if (livro.getIsbn().equals(isbn)) { // Comparando o ISBN do livro com o ISBN informado
	                this.catalogo.remove(i); // Removendo o livro da lista
	                break; // Saindo do loop
	            }
	        }
	    }
	    
	    
	    public ArrayList<Livro> buscar_por_titulo(String titulo) {
	        // Retornando uma lista com todos os livros que contêm o título especificado
	        ArrayList<Livro> resultado = new ArrayList<Livro>(); // Criando uma lista vazia para armazenar o resultado
	        for (Livro livro : this.catalogo) { // Percorrendo o catálogo usando o for-each
	            if (livro.getTitulo().contains(titulo)) { // Verificando se o título do livro contém o título informado
	                resultado.add(livro); // Adicionando o livro ao resultado
	            }
	        }
	        return resultado; // Retornando o resultado
	    }

	    
            public boolean emprestar_livro(String isbn) {
	        // Tentando emprestar um livro usando o ISBN
	        for (Livro livro : this.catalogo) { // Percorrendo o catálogo usando o for-each
	            if (livro.getIsbn().equals(isbn)) { // Comparando o ISBN do livro com o ISBN informado
	                return livro.reservar(); // Chamando o método reservar() da classe Livro e retornando o seu resultado
	            }
	        }
	        return false; // Retornando false se não encontrar o livro no catálogo
        	}
	    
	    
            public void devolver_livro(String isbn) {
                for (Livro livro : this.catalogo) { // Percorrendo o catálogo usando o for-each
                    if (livro.getIsbn().equals(isbn)) { // Comparando o ISBN do livro com o ISBN informado
        	                livro.devolver(); // Chamando o método devolver() da classe Livro
        	                break; // Saindo do loop
        	    }
            	}
                }

	    public ArrayList<Livro> listar_livros() {
	        // Retornando uma lista com todos os livros do catálogo
	        return this.catalogo; // Retornando o atributo catalogo
    	    }
    	}
