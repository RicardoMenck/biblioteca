package br.com.biblioteca.view.livro;

import br.com.biblioteca.model.Area;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AreaTableModel extends AbstractTableModel {

  private List<Area> dados;
  private String[] colunas = { "ID", "Nome", "Descrição" };

  public AreaTableModel(List<Area> dados) {
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
    Area a = dados.get(row);
    switch (col) {
      case 0:
        return a.getId();
      case 1:
        return a.getNome();
      case 2:
        return a.getDescricao();
      default:
        return null;
    }
  }

  public Area getAreaAt(int row) {
    if (row >= 0 && row < dados.size())
      return dados.get(row);
    return null;
  }
}
