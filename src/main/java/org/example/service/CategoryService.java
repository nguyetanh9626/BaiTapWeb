package org.example.service;

import org.example.model.Category;

import java.util.List;

/**
 * Interface CategoryService xử lý nghiệp vụ quản lý danh mục.
 */
public interface CategoryService {

    void insert(Category category);

    void edit(Category category);

    void delete(int id);

    Category get(int id);

    Category get(String name);

    List<Category> getAll();

    List<Category> search(String keyword);
}
