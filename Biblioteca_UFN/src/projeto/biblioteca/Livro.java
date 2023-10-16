package projeto.biblioteca;

public class Livro {

    private String titulo;
    private String autor;
    private String isbn;
    private boolean emprestado;

    //Método construtor
    public Livro(String titulo, String autor, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.emprestado = false;
    }

    //Métodos da classe

    public boolean reservar() {
        if(!this.isEmprestado()) {
            setEmprestado(true); //Livro reservado
            return true;
        } else {
            return false;
        }
    }

    public void devolver() {
        if(this.isEmprestado()) {
            this.setEmprestado(false); //Livro devolvido
        } else  {
            System.out.println("O livro não está emprestado");
        }
    }

    public String exibir_info() {
        return "Titulo: " + this.titulo +
                "\nAutor: " + this.autor +
                "\nISBN: " + this.isbn +
                "\nEmprestado: " + this.emprestado;
    }


    //Método acessor e modificador


    public String getTitulo() {
        return titulo;
    }


    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }
}
