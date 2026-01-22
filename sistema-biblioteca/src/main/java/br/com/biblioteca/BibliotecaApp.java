package br.com.biblioteca;

import br.com.biblioteca.util.InicializadorBanco;
import br.com.biblioteca.view.menu.TelaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BibliotecaApp {

  public static void main(String[] args) {
    // 1. Inicializa o Backend (Cria tabelas se não existirem)
    System.out.println(">> Inicializando Banco de Dados...");
    InicializadorBanco.criarTabelas();

    // 2. Inicia a Interface Gráfica (Thread Segura do Swing)
    SwingUtilities.invokeLater(() -> {
      try {
        // Tenta aplicar o Look and Feel 'Nimbus' (Mais moderno)
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());
            break;
          }
        }
      } catch (Exception e) {
        System.err.println("Erro ao aplicar tema: " + e.getMessage());
      }

      // Abre a Tela Principal
      TelaPrincipal tela = new TelaPrincipal();
      tela.setVisible(true);
    });
  }
}
