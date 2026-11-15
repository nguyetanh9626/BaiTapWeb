package vn.iotstar.dao;

import vn.iotstar.entity.Category;

import java.util.List;

/**
 * Interface DAO quản lý các thao tác CRUD danh mục bằng JPA/Hibernate.
 */
public interface ICategoryDao {

    void insert(Category category);

    void update(Category category);

    void delete(int cateid) throws Exception;

    Category findById(int cateid);

    Category findByCategoryname(String name) throws Exception;

    List<Category> findAll();

    List<Category> searchByName(String catname);

    List<Category> findAll(int page, int pagesize);

    int count();
}
