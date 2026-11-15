package org.example.dao;

import org.example.model.Category;

import java.util.List;

/**
 * Interface CategoryDao định nghĩa các thao tác CRUD và tìm kiếm bảng Category.
 */
public interface CategoryDao {

    void insert(Category category);

    void edit(Category category);

    void delete(int id);

    Category get(int id);

    Category get(String name);

    List<Category> getAll();

    List<Category> search(String keyword);

    /**
     * Kiểm tra xem tên file icon có đang được danh mục khác sử dụng hay không
     * để tránh xóa nhầm file ảnh dùng chung.
     */
    boolean isIconUsedByOtherCategory(String icon, int excludeCateId);
}
