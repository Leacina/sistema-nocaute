package br.com.nocaute.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.nocaute.dao.contracts.Searchable;
import br.com.nocaute.dao.contracts.Selectable;
import br.com.nocaute.model.CityModel;

public class CityDAO extends AbstractDAO<CityModel> implements Selectable<CityModel>, Searchable<CityModel> {
	private static final String TABLE_NAME = "cidades";

	private String columnId = "id_cidade";

	private String defaultOrderBy = "cidade";

	Connection connection;

	public CityDAO(Connection connection) throws SQLException {
		this.connection = connection;

		this.connection.setAutoCommit(false);
	}

	@Override
	public List<CityModel> selectAll() throws SQLException {
		String query = getSelectAllQuery(TABLE_NAME, "*", defaultOrderBy);

		PreparedStatement pst = connection.prepareStatement(query);

		List<CityModel> cityList = new ArrayList<CityModel>();

		ResultSet rst = pst.executeQuery();

		while (rst.next()) {
			CityModel model = new CityModel();
			model.setId(rst.getInt("id_cidade"));
			model.setName(rst.getString("cidade"));
			model.setState(rst.getString("estado"));
			model.setCountry(rst.getString("pais"));

			cityList.add(model);
		}

		return cityList;
	}

	@Override
	public List<CityModel> search(String word) throws SQLException {
		String query = "SELECT * FROM " + TABLE_NAME
				+ " WHERE cidade ILIKE ? OR estado ILIKE ?  OR pais ILIKE ? ORDER BY "
				+ defaultOrderBy;
		PreparedStatement pst = connection.prepareStatement(query);

		setParam(pst, 1, "%" + word + "%");
		setParam(pst, 2, word + "%");
		setParam(pst, 3, word + "%");

		List<CityModel> citiesList = new ArrayList<CityModel>();

		ResultSet rst = pst.executeQuery();

		while (rst.next()) {
			CityModel model = createModelFromResultSet(rst);

			citiesList.add(model);
		}

		return citiesList;
	}

	@Override
	public CityModel findById(Integer id) throws SQLException {
		CityModel model = null;

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

	/**
	 * Cria um objeto Model a partir do resultado obtido no banco de dados
	 * 
	 * @param rst
	 * @return CityModel
	 * @throws SQLException
	 */
	private CityModel createModelFromResultSet(ResultSet rst) throws SQLException {
		CityModel model = new CityModel();

		model.setId(rst.getInt("id_cidade"));
		model.setName(rst.getString("cidade"));
		model.setState(rst.getString("estado"));
		model.setCountry(rst.getString("pais"));

		return model;
	}
}
