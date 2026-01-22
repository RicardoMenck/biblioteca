package br.com.biblioteca.view.livro;

import br.com.biblioteca.model.Titulo;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TituloTableModel extends AbstractTableModel {

  private List<Titulo> dados;
  private String[] colunas = { "ID", "Nome", "ISBN", "Edição", "Ano", "Área" };

  public TituloTableModel(List<Titulo> dados) {
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
    Titulo t = dados.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return t.getId();
      case 1:
        return t.getNome();
      case 2:
        return t.getIsbn();
      case 3:
        return t.getEdicao();
      case 4:
        return t.getAno();
      case 5:
        // Navegação no objeto: Se tiver área, mostra o nome
        if (t.getArea() != null)
          return t.getArea().getNome();
        return "Sem Área";
      default:
        return null;
    }
  }

  public Titulo getTituloAt(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < dados.size())
      return dados.get(rowIndex);
    return null;
  }
}
