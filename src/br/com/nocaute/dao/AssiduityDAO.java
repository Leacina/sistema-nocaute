package br.com.nocaute.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.nocaute.dao.contracts.Insertable;
import br.com.nocaute.dao.contracts.Searchable;
import br.com.nocaute.dao.contracts.Selectable;
import br.com.nocaute.model.AssiduityModel;
import br.com.nocaute.model.CityModel;

public class AssiduityDAO extends AbstractDAO<CityModel>
		implements Selectable<AssiduityModel>, Searchable<AssiduityModel>, Insertable<AssiduityModel> {
	private static final String TABLE_NAME = "assiduidade";

	private String columnId = "id_assiduidade";

	private String defaultOrderBy = "id_assiduidade ASC";

	private String[] defaultValuesToInsert = new String[] { "DEFAULT" };

	private String[] columnsToInsert = new String[] { "id_assiduidade","codigo_matricula", "data_entrada"};

	Connection connection;

	public AssiduityDAO(Connection connection) throws SQLException {
		this.connection = connection;

		this.connection.setAutoCommit(false);
	}

	@Override
	public List<AssiduityModel> search(String word) throws SQLException {
		String query = "select id_assiduidade,  "
				      +"       codigo_matricula,"
				      +"       data_entrada     "
				      +"from " + TABLE_NAME
				      +" where codigo_matricula = ?";
		
		PreparedStatement pst = connection.prepareStatement(query);
		
		setParam(pst, 1, Integer.parseInt(word));
		
		List<AssiduityModel> modelList = new ArrayList<AssiduityModel>();
		
		ResultSet rst = pst.executeQuery();
		
		while(rst.next()) {
			AssiduityModel model = new AssiduityModel();
			
			model.setRegistrationCode(rst.getInt("codigo_matricula"));
			model.setInputDate(rst.getTimestamp("data_entrada"));
			model.setAssiduityCode(rst.getInt("id_assiduidade"));
			
			modelList.add(model);
		}
		return modelList;
	}

	@Override
	public List<AssiduityModel> selectAll() throws SQLException {
		String query = getSelectAllQuery(TABLE_NAME, "*", defaultOrderBy);

		PreparedStatement pst = connection.prepareStatement(query);

		List<AssiduityModel> AssiduityModelList = new ArrayList<AssiduityModel>();

		ResultSet rst = pst.executeQuery();

		while (rst.next()) {
			AssiduityModel model = createModelFromResultSet(rst);

			AssiduityModelList.add(model);
		}

		return AssiduityModelList;
	}

	@Override
	public AssiduityModel findById(Integer id) throws SQLException {
		AssiduityModel model = null;

		String query = getFindByQuery(TABLE_NAME, columnId, "*", defaultOrderBy);

		PreparedStatement pst = connection.prepareStatement(query);

		setParam(pst, 1, id);

		ResultSet rst = pst.executeQuery();

		if (rst.next()) {
			model = createModelFromResultSet(rst);

			return model;
		}

		return null;
	}

	@Override
	public AssiduityModel insert(AssiduityModel model) throws SQLException {
		String query = getInsertQuery(TABLE_NAME, columnsToInsert, defaultValuesToInsert);

		PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		pst.clearParameters();

		setParam(pst, 1, model.getRegistrationCode());
		setParam(pst, 2, model.getInputDate());
		System.out.println(model.getInputDate().getTime());
		int result = pst.executeUpdate();
		if (result > 0) {
			connection.commit();

			ResultSet rs = pst.getGeneratedKeys();
			if (rs.next()) {
				return model;
			}
		}

		return null;
	}
	
	public boolean isFirstAssiduity(Timestamp date, int registrationCode) throws SQLException {
		
		String query = "SELECT count(*) 		"
				+      "  from assiduidade		"
				+      "where codigo_matricula = ? and DATE_PART('DAY', data_entrada) = DATE_PART('DAY', CURRENT_TIMESTAMP)"
				;
		
		PreparedStatement pst = connection.prepareStatement(query);
	
		setParam(pst, 1, registrationCode);
	
		ResultSet rst = pst.executeQuery();
	
		if(rst.next()) {
			if(rst.getInt("count") > 0) {
				return false;
			}
		}
	
		return true;
	}

	/**
	 * Cria um objeto Model a partir do resultado obtido no banco de dados
	 * 
	 * @param rst
	 * @return AssiduityModel
	 * @throws SQLException
	 */
	private AssiduityModel createModelFromResultSet(ResultSet rst) throws SQLException {
		AssiduityModel model = new AssiduityModel();

		model.setRegistrationCode(rst.getInt("codigo_matricula"));
		model.setInputDate(rst.getTimestamp("data_entrada"));
		model.setAssiduityCode(rst.getInt("id_assiduidade"));

		return model;
	}

}
