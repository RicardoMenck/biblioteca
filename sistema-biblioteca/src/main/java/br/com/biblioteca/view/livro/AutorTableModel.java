package br.com.biblioteca.view.livro;

import br.com.biblioteca.model.Autor;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AutorTableModel extends AbstractTableModel {

  private List<Autor> dados;
  private String[] colunas = { "ID", "Nome", "Sobrenome", "Titulação" };

  public AutorTableModel(List<Autor> dados) {
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
  public String getColumnName(int col) {
    return colunas[col];
  }

  @Override
  public Object getValueAt(int row, int col) {
    Autor a = dados.get(row);
    switch (col) {
      case 0:
        return a.getId();
      case 1:
        return a.getNome();
      case 2:
        return a.getSobrenome();
      case 3:
        return a.getTitulacao();
      default:
        return null;
    }
  }

  public Autor getAutorAt(int row) {
    if (row >= 0 && row < dados.size())
      return dados.get(row);
    return null;
  }
}
