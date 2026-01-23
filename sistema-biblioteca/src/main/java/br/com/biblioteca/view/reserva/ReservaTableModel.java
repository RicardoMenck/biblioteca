package br.com.biblioteca.view.reserva;

import br.com.biblioteca.model.Reserva;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ReservaTableModel extends AbstractTableModel {

  private List<Reserva> dados;
  private String[] colunas = { "ID", "Data", "Título (Obra)", "Aluno", "Status" };
  private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  public ReservaTableModel(List<Reserva> dados) {
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
  public Object getValueAt(int rowIndex, int columnIndex) {
    Reserva r = dados.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return r.getId();
      case 1:
        return sdf.format(r.getDataReserva());
      case 2:
        return r.getTitulo().getNome();
      case 3:
        return r.getAluno().getNome();
      case 4:
        return r.isAtiva() ? "Aguardando" : "Finalizada";
      default:
        return null;
    }
  }

  public Reserva getReservaAt(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < dados.size())
      return dados.get(rowIndex);
    return null;
  }
}
