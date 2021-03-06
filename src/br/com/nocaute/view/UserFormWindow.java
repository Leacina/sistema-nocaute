package br.com.nocaute.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameEvent;

import br.com.nocaute.dao.UserDAO;
import br.com.nocaute.image.MasterImage;
import br.com.nocaute.model.UserModel;
import br.com.nocaute.util.InternalFrameListener;

public class UserFormWindow extends AbstractToolbar {
	private static final long serialVersionUID = -2537423200954897351L;

	private UserModel model = new UserModel();
	private UserDAO userDao;
	private ListUsersWindow searchUsersWindow;

	// Guarda os fields em uma lista para facilitar manipula��o em massa
	List<Component> formFields = new ArrayList<Component>();

	// Componentes
	private JLabel label;
	private JTextField txfUsuario;
	private JPasswordField txfSenha, txfConfirmarSenha;
	private JComboBox<String> cbxPerfil;

	JDesktopPane desktop;
	private Connection CONNECTION;

	public UserFormWindow(JDesktopPane desktop, Connection CONNECTION) {
		super("Usu�rios", 455, 200, desktop, false);
		setFrameIcon(MasterImage.user_16x16);

		this.desktop = desktop;
		this.CONNECTION = CONNECTION;

		try {
			userDao = new UserDAO(CONNECTION);
		} catch (Exception e) {
			e.printStackTrace();
		}

		criarComponentes();

		// Por padr�o campos s�o desabilitados ao iniciar
		disableComponents(formFields);

		setButtonsActions();
	}

	protected void setButtonsActions() {
		// A��es de bot�es
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFormMode(CREATE_MODE);

				// Ativa campos
				enableComponents(formFields);

				// Limpar dados dos campos
				clearFormFields(formFields);

				// Cria nova entidade model
				model = new UserModel();

				// Ativa bot�o salvar
				btnSalvar.setEnabled(true);

				// Desativa bot�o Remover
				btnRemover.setEnabled(false);
			}
		});

		btnSalvar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!validateFields()) {
					return;
				}

				model.setUser(txfUsuario.getText());
				model.setPassword(new String(txfSenha.getPassword()));
				model.setProfile(cbxPerfil.getSelectedItem().toString());

				try {
					// EDI��O CADASTRO
					if (isEditing()) {
						boolean result = userDao.update(model);

						if (result) {
							bubbleSuccess("Usuario editado com sucesso");
						} else {
							bubbleError("Houve um erro ao editar usuario");
						}
						// NOVO CADASTRO
					} else {
						UserModel insertedModel = userDao.insert(model);

						if (insertedModel != null) {
							bubbleSuccess("Usuario cadastrado com sucesso");

							// Atribui o model rec�m criado ao model
							model = insertedModel;

							// Seta form para edi��o
							setFormMode(UPDATE_MODE);

							// Ativa bot�o Remover
							btnRemover.setEnabled(true);
						} else {
							bubbleError("Houve um erro ao cadastrar usuario");
						}
					}

				} catch (SQLException error) {
					bubbleError(error.getMessage());
					error.printStackTrace();
				}
			}
		});

		// A��o Buscar
		btnBuscar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (searchUsersWindow == null) {
					searchUsersWindow = new ListUsersWindow(desktop, CONNECTION);

					searchUsersWindow.addInternalFrameListener(new InternalFrameListener() {
						@Override
						public void internalFrameClosed(InternalFrameEvent e) {
							UserModel selectedModel = ((ListUsersWindow) e.getInternalFrame()).getSelectedModel();

							if (selectedModel != null) {
								// Atribui o model selecionado
								model = selectedModel;

								txfUsuario.setText(model.getUser());
								cbxPerfil.setSelectedItem(model.getProfile());
								
								try {
									txfSenha.setText(userDao.searchPassword(model.getUser()));
									txfConfirmarSenha.setText(userDao.searchPassword(model.getUser()));
								} catch (SQLException e1) {
								
									e1.printStackTrace();
								}
							
								// Seta form para modo Edi��o
								setFormMode(UPDATE_MODE);

								// Ativa campos
								enableComponents(formFields);

								if (model.getUser().equals("admin") == true) {
									cbxPerfil.setEnabled(false);
								}

								// Ativa bot�o salvar
								btnSalvar.setEnabled(true);

								// Ativa bot�o remover
								btnRemover.setEnabled(true);
								txfUsuario.setEnabled(false);

							}

							// Reseta janela
							searchUsersWindow = null;
						}
					});
				}
			}
		});

		// A��o Remover
		btnRemover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (isEditing()) {
						boolean result = userDao.delete(model);

						if (result) {
							bubbleSuccess("Usuario exclu�do com sucesso");

							// Seta form para modo Cadastro
							setFormMode(CREATE_MODE);

							// Desativa campos
							disableComponents(formFields);

							// Limpar dados dos campos
							clearFormFields(formFields);

							// Cria nova entidade model
							model = new UserModel();

							// Desativa bot�o salvar
							btnSalvar.setEnabled(false);

							// Desativa bot�o remover
							btnRemover.setEnabled(false);
						} else {
							bubbleError("Houve um erro ao excluir usuario");
						}
					}
				} catch (SQLException error) {
					bubbleError(error.getMessage());
					error.printStackTrace();
				}
			}
		});

	}

	private void criarComponentes() {

		label = new JLabel("Usu�rio: ");
		label.setBounds(5, 55, 50, 25);
		getContentPane().add(label);

		txfUsuario = new JTextField();
		txfUsuario.setBounds(100, 55, 325, 20);
		txfUsuario.setToolTipText("Digite o nome do usu�rio");
		getContentPane().add(txfUsuario);
		formFields.add(txfUsuario);

		label = new JLabel("Senha: ");
		label.setBounds(5, 80, 70, 25);
		getContentPane().add(label);

		txfSenha = new JPasswordField();
		txfSenha.setBounds(100, 80, 325, 20);
		txfSenha.setToolTipText("Digite a senha do usu�rio");
		getContentPane().add(txfSenha);
		formFields.add(txfSenha);

		label = new JLabel("Confirmar Senha: ");
		label.setBounds(5, 105, 90, 25);
		getContentPane().add(label);

		txfConfirmarSenha = new JPasswordField();
		txfConfirmarSenha.setBounds(100, 105, 325, 20);
		txfConfirmarSenha.setToolTipText("Confirme a senha do usu�rio");
		getContentPane().add(txfConfirmarSenha);
		formFields.add(txfConfirmarSenha);

		label = new JLabel("Perfil: ");
		label.setBounds(5, 130, 110, 25);
		getContentPane().add(label);

		cbxPerfil = new JComboBox<String>();
		cbxPerfil.addItem("-- Selecione --");
		cbxPerfil.addItem("Cadastral");
		cbxPerfil.addItem("Matricular");
		cbxPerfil.addItem("Financeiro");
		cbxPerfil.addItem("Completo");
		cbxPerfil.setBounds(100, 130, 325, 20);
		cbxPerfil.setToolTipText("Informe o perfil");
		getContentPane().add(cbxPerfil);
		formFields.add(cbxPerfil);

	}

	public boolean validateFields() {
		if (txfUsuario.getText().isEmpty() || txfUsuario.getText() == null) {
			bubbleWarning("Informe o nome do Usuario!");
			return false;
		}

		if ((new String(txfSenha.getPassword()).isEmpty() || new String(txfSenha.getPassword()) == null)
				&& txfSenha.isEnabled()) {
			bubbleWarning("Informe uma senha para usu�rio!");
			return false;
		}

		if ((new String(txfConfirmarSenha.getPassword()).isEmpty()
				|| new String(txfConfirmarSenha.getPassword()) == null) && txfConfirmarSenha.isEnabled()) {
			bubbleWarning("Confirme sua senha para o usuario!");
			return false;
		}

		if (!(new String(txfConfirmarSenha.getPassword()).equals(new String(txfSenha.getPassword())))) {
			bubbleWarning("Senhas n�o conferem!");
			return false;
		}

		if (cbxPerfil.getSelectedIndex() == 0) {
			bubbleWarning("Informe o perfil do usuario!");
			return false;
		}
		
		/*if(txfSenha.getPassword() != txfConfirmarSenha.getPassword()) {
			bubbleWarning("Senhas n�o conferem!");
			return false;
		}*/

		return true;
	}

}
