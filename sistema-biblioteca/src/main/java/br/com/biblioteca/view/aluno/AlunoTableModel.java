package br.com.biblioteca.view.aluno;

import br.com.biblioteca.model.Aluno;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AlunoTableModel extends AbstractTableModel {

  private List<Aluno> dados;
  private String[] colunas = { "ID", "RA", "Nome", "CPF", "Endereço" };

  public AlunoTableModel(List<Aluno> dados) {
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
    Aluno a = dados.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return a.getId();
      case 1:
        return a.getRa();
      case 2:
        return a.getNome();
      case 3:
        return a.getCpf();
      case 4:
        return a.getEndereco();
      default:
        return null;
    }
  }

  // Métodos auxiliares para pegar o objeto selecionado
  public Aluno getAlunoAt(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < dados.size()) {
      return dados.get(rowIndex);
    }
    return null;
  }

  public void atualizarDados(List<Aluno> novosDados) {
    this.dados = novosDados;
    fireTableDataChanged(); // Avisa a tela que mudou tudo
  }
}
