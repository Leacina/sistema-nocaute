package br.com.nocaute.view;

import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import br.com.nocaute.dao.StudentDAO;
import br.com.nocaute.model.StudentModel;
import br.com.nocaute.view.tableModel.StudentTableModel;

public class ListStudentFormWindow extends AbstractGridWindow {
	private static final long serialVersionUID = -8074030868088770858L;
	
	private StudentDAO dao;
	private StudentModel selectedModel;

	private JButton btnSearch;
	private JTextField txfSearch;

	private StudentTableModel tableModel;
	private JTable jTableStudents;
	private TableCellRenderer renderer = new EvenOddRenderer();

	public ListStudentFormWindow(JDesktopPane desktop) {
		super("Alunos", 445, 310, desktop);
		
		try {
			dao = new StudentDAO(CONNECTION);
		} catch (SQLException error) {
			error.printStackTrace();
		}

		createComponents();
	}
	
	public StudentModel getSelectedModel() {
		return selectedModel;
	}

	private void createComponents() {

		txfSearch = new JTextField();
		txfSearch.setBounds(5, 10, 330, 20);
		txfSearch.setToolTipText("Informe o aluno");
		getContentPane().add(txfSearch);

		btnSearch = new JButton("Buscar");
		btnSearch.setBounds(340, 10, 85, 22);
		getContentPane().add(btnSearch);

		loadGrid();
	}

	private void loadGrid() {
		tableModel = new StudentTableModel();
		jTableStudents = new JTable(tableModel);

		// Habilita a sele��o por linha
		jTableStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTableStudents.setDefaultRenderer(Object.class, renderer);
		
		try {
			tableModel.addModelsList(dao.selectAll());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		grid = new JScrollPane(jTableStudents);
		setLayout(null);
		resizeGrid(grid, 5, 40, 420, 230);
		grid.setVisible(true);

		add(grid);

	}

	//TODO: Refatorar para utilizar e todas as grids
	class EvenOddRenderer implements TableCellRenderer {
		public DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			((JLabel) renderer).setOpaque(true);
			Color background;
			if (isSelected) {
				background = new Color(65, 105, 225);
			} else {
				if (row % 2 == 0) {
					background = new Color(220, 220, 220);
				} else {
					background = Color.WHITE;
				}
			}

			renderer.setBackground(background);
			return renderer;
		}
	}

}
