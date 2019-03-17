package br.com.noucate.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.nocaute.model.ModalityModel;

//TODO: Reestruturar a tabela 'modalidades' no banco de dados para que essa classe funcione corretamente.

public class ModalityDAO extends AbstractDAO<ModalityModel>{	
	private static final String TABLE_NAME = "modalidades";
	
	private String columnId = "id_modalidade";
	
	private String defaultOrderBy = "modalidade ASC";
	
	private String[] defaultValuesToInsert = new String[] {
			"DEFAULT"
		};
	
	private String[] columnsToInsert = new String[] {
			"id_modalidade",
			"nome_modalidade"
		};
	
	private String[] columnsToUpdate = new String[] {
			"nome_modalidade"
		};
	
	Connection connection;
	
	public ModalityDAO(Connection connection) throws SQLException{
		this.connection = connection;
	}
	
	@Override
	public List<ModalityModel> selectAll() throws SQLException {
		String query = getSelectAllQuery(TABLE_NAME, "*", defaultOrderBy);
		
		PreparedStatement pst = connection.prepareStatement(query);
		
		List<ModalityModel> modalitysList = new ArrayList<ModalityModel>();
		
		ResultSet rst = pst.executeQuery();
		
		while (rst.next()) {
			ModalityModel model = createModelFromResultSet(rst);
			
			modalitysList.add(model);
		}
		
		return modalitysList;
	}
	
	@Override
	public ModalityModel findById(Integer id) throws SQLException {
		ModalityModel model = null;

		String query = getFindByQuery(TABLE_NAME, columnId, "*", defaultOrderBy);
		PreparedStatement pst = connection.prepareStatement(query);

		setParam(pst, 1, id);
		ResultSet rst = pst.executeQuery();

		if (rst.next()) {
			model = createModelFromResultSet(rst);
		}

		return model;
	}
	
	@Override
	public ModalityModel insert(ModalityModel model) throws SQLException {
		String query = getInsertQuery(TABLE_NAME, columnsToInsert, defaultValuesToInsert);
		
		PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
		pst.clearParameters();
		
		setParam(pst, 1, model.getModalityName());
		
		int result = pst.executeUpdate();
		if (result > 0) {
			connection.commit();

			ResultSet rs = pst.getGeneratedKeys();
			if (rs.next()) {
				int lastInsertedCode = rs.getInt(columnId);

				// Antes de retornar, seta o id ao objeto modalidade
				model.setModalityId(lastInsertedCode);

				return model;
			}
		}

		return null;
	}
	
	@Override
	public boolean update(ModalityModel model) throws SQLException {
		String query = getUpdateQuery(TABLE_NAME, columnId, columnsToUpdate);

		PreparedStatement pst = connection.prepareStatement(query);

		setParam(pst, 1, model.getModalityName());

		// Identificador WHERE
		setParam(pst, 2, model.getModalityId());

		int result = pst.executeUpdate();
		if (result > 0) {
			connection.commit();

			return true;
		}

		return false;
	}
		
	@Override
	public boolean delete(ModalityModel model) throws SQLException {
		return deleteById(model.getModalityId());
	}

	@Override
	public boolean deleteById(Integer id) throws SQLException {
		String query = getDeleteQuery(TABLE_NAME, columnId);
		PreparedStatement pst = connection.prepareStatement(query);
		
		setParam(pst, 1, id);
		
		int result = pst.executeUpdate();
        if (result > 0) {
        	connection.commit();
        	
        	return true;
        }
        
        return false;
	}
	
	/**
	 * Cria um objeto Model a partir do resultado obtido no banco de dados
	 * @param rst
	 * @return ModalityModel
	 * @throws SQLException
	 */
	private ModalityModel createModelFromResultSet(ResultSet rst) throws SQLException {
		ModalityModel model = new ModalityModel();
		
		model.setModalityId(rst.getInt("id_modalidade"));
		model.setModalityName(rst.getString("modalidade"));
		
		return model;
	}

} 

