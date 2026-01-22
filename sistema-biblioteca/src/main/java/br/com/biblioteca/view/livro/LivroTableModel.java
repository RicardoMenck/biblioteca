package br.com.biblioteca.view.livro;

import br.com.biblioteca.model.Livro;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class LivroTableModel extends AbstractTableModel {

  private List<Livro> dados;
  private String[] colunas = { "Cód. Exemplar (ID)", "Status", "Tipo" };

  public LivroTableModel(List<Livro> dados) {
    this.dados = dados;
  }

  @Override
  public int getRowCount() {
    return dados.size();
  }

  @Override
  public int getColumnCount() {
    return colunas.length;
  }

  @Override
  public String getColumnName(int column) {
    return colunas[column];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Livro l = dados.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return l.getId();
      case 1:
        return l.isDisponivel() ? "Disponível" : "Emprestado/Indisp.";
      case 2:
        return l.isExemplarBiblioteca() ? "Consulta Local (Fixo)" : "Circulante";
      default:
        return null;
    }
  }

  public Livro getLivroAt(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < dados.size())
      return dados.get(rowIndex);
    return null;
  }
}
