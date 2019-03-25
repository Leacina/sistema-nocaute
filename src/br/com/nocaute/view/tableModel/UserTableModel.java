package br.com.nocaute.view.tableModel;

import java.util.List;

import br.com.nocaute.model.UserModel;

public class UserTableModel extends AbstractTableModel<UserModel>{

	public UserTableModel() {
		super(new String[] {"ID","Usuario"});
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void setModelValueAt(int columnIndex, UserModel model, Object aValue) {
		switch (columnIndex) {
		case 0:
			model.setCode(Integer.parseInt(aValue.toString()));
		case 1:
			model.setUser(aValue.toString());
		default:
			System.err.println("�ndice da coluna inv�lido");
	  }
	}

	@Override
	protected Object getModelValueAt(int columnIndex, UserModel model) {
		String valueObject = null;
		
		switch (columnIndex) {
		case 0:
			valueObject = model.getCode().toString();
			break;
		case 1:
			valueObject = model.getUser();
			break;
		default:
			System.err.println("�ndice da coluna inv�lido");
		}

		return valueObject;
	}

}
