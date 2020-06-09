package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface DepartmentDao {
	
	Department insert(String name);
	Department update(Department obj);
	List<Seller> deleteById(Integer id);
	Department findById(Integer id);
	List<Department> findAll();
	List<Seller> findSellersByDepartmentId(Integer id);
}
