package projeto.biblioteca;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        Livro l1 = new Livro("O Príncipe "," Nicolau Maquiavél ", " Isbn1 " );

        biblioteca.adicionarLivro(l1);
        boolean reserva1 = l1.reservar();
        System.out.println("Reserva 1 efetuada!" + reserva1);

        Livro l2 = new Livro(" O Poder do Hábito "," Charles Duhigg ", " Isbn2 " );

        biblioteca.adicionarLivro(l2);
        biblioteca.emprestarLivro("Isbn2");
        System.out.println(l2.exibir_info());

    }
}