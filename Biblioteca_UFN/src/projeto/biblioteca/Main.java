package projeto.biblioteca;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        Livro l1 = new Livro("Entendendno algoritmos "," Aditya Bhargava ", " 4564654 " );

        biblioteca.adicionarLivro(l1);
        boolean reserva1 = l1.reservar();
        System.out.println("Reservado!" + reserva1);

        Livro l2 = new Livro(" As seis lições "," Ludwig von Mises ", " 4565 " );

        biblioteca.adicionarLivro(l2);
        biblioteca.emprestarLivro("4565");
        System.out.println(l2.exibir_info());

    }
}