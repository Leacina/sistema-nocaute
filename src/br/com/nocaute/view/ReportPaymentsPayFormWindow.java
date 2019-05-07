package br.com.nocaute.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDesktopPane;

public class ReportPaymentsPayFormWindow extends AbstractReportWindowFrame {
	private static final long serialVersionUID = -49128325842856068L;

	public ReportPaymentsPayFormWindow(JDesktopPane desktop) {
		super("Faturas Pagas", desktop);

		// Seta as a��es esperadas para cada bot�o
		setButtonsActions();
	}

	protected void setButtonsActions() {
		// Recuperar data do campo. Esse m�todo � chamado
		// na primeira vez que executa o sistema
		// SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		// String novaData = formatador.format(jDateChooser.getDate());

		// A��o Processar relatorio
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: processar relatorio
			}
		});
	}
}
