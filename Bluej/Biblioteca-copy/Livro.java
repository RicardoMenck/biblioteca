
public class Livro {
	// Atributos do Livro
    private String titulo;
    private String autor;
    private String isbn;
    private boolean emprestado;

    // Construtor do Livro
    public Livro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.emprestado = false;
    }
    
    

    // Métodos aplicáveis no livro
    
    // Verifica e realiza a reserva de um livro
    public boolean reservar() {
        if (!this.emprestado) {
            this.emprestado = true;
            return true;
        } else {
            return false;
        }
    }

    //Realiza a devolução do Livro
    public void devolver() {
        this.emprestado = false;
    }
    
    //Exibe as informações gerais do livro
    public String exibir_info() {
        return "Titulo: " + this.titulo + 
        		"\nAutor: " + this.autor + 
        		"\nISBN: " + this.isbn + 
        		"\nEmprestado: " + this.emprestado;
    }

    
    //Métodos Modificadores (Getter and Setter)
	public String getTitulo() {
		return titulo;
	}

	private void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	private void setAutor(String autor) {
		this.autor = autor;
	}

	public String getIsbn() {
		return isbn;
	}

	private void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean isEmprestado() {
		return emprestado;
	}

	private void setEmprestado(boolean emprestado) {
		this.emprestado = emprestado;
	
		}
    }
