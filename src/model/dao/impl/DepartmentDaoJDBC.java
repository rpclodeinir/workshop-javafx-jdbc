package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("INSERT INTO department (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected == 1) {
				rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			st.setInt(1, id);
			st.executeUpdate();
		}catch(SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}finally{
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer Id) {
		PreparedStatement st = null;
		ResultSet rs = null;	
		try {
			st = conn.prepareStatement(
					"SELECT department.Id as DepId, department.Name as DepName FROM department WHERE department.Id = ?");
			st.setInt(1, Id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department department = new Department();
				department.setId(rs.getInt("DepId"));
				department.setName(rs.getString("DepName"));
				return department;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			List<Department> list = new ArrayList<>();
			st = conn.prepareStatement(
					"SELECT department.Id as DepId, department.Name as DepName FROM department ORDER BY Name");
			rs = st.executeQuery();
			while (rs.next()) {
				Department dep = instantiateDepartment(rs);
				list.add(dep);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findSellersByDepartmentId(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> list = new ArrayList<>();
		try {
			Department depResult = findById(id);
			if (depResult.getId() != null) {
				st = conn.prepareStatement("SELECT seller.*, " + "department.Id as DepId, department.Name as DepName "
						+ "FROM seller INNER JOIN department " + "ON seller.DepartmentId = department.Id "
						+ "WHERE department.Id = ?");

				st.setInt(1, id);
				rs = st.executeQuery();

				Map<Integer, Department> map = new HashMap<>();
				while (rs.next()) {
					Department dep = map.get(rs.getInt("DepartmentId"));
					if (dep == null) {
						dep = instantiateDepartment(rs);
						map.put(rs.getInt("DepartmentId"), dep);
					}
					Seller obj = instantiateSeller(rs, dep);
					list.add(obj);
				}
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		return list;
	}
}
