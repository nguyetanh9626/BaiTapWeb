package org.example.service;

import org.example.dao.CategoryDao;
import org.example.dao.CategoryDaoImpl;
import org.example.model.Category;
import org.example.util.ImageUploadUtil;

import java.util.List;

/**
 * Triển khai Business Logic Layer cho quản lý Category (CRUD, tìm kiếm, giữ/xóa ảnh an toàn).
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public void insert(Category category) {
        if (category == null || isBlank(category.getCatename())) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
        category.setCatename(category.getCatename().trim());
        categoryDao.insert(category);
    }

    @Override
    public void edit(Category newCategory) {
        if (newCategory == null || isBlank(newCategory.getCatename())) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }

        Category oldCategory = categoryDao.get(newCategory.getCateid());
        if (oldCategory == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục cần cập nhật.");
        }

        String oldIcon = oldCategory.getIcon();
        String newIcon = newCategory.getIcon();
        boolean hasNewIcon = !isBlank(newIcon) && !newIcon.trim().equals(oldIcon);

        oldCategory.setCatename(newCategory.getCatename().trim());
        if (hasNewIcon) {
            oldCategory.setIcon(newIcon.trim());
        }

        categoryDao.edit(oldCategory);

        if (hasNewIcon && !isBlank(oldIcon)
                && !categoryDao.isIconUsedByOtherCategory(oldIcon, oldCategory.getCateid())) {
            ImageUploadUtil.deleteCategoryImageIfExists(oldIcon);
        }
    }

    @Override
    public void delete(int id) {
        Category existing = categoryDao.get(id);
        if (existing == null) {
            return;
        }
        String icon = existing.getIcon();
        categoryDao.delete(id);

        if (!isBlank(icon) && !categoryDao.isIconUsedByOtherCategory(icon, id)) {
            ImageUploadUtil.deleteCategoryImageIfExists(icon);
        }
    }

    @Override
    public Category get(int id) {
        return categoryDao.get(id);
    }

    @Override
    public Category get(String name) {
        if (isBlank(name)) {
            return null;
        }
        return categoryDao.get(name.trim());
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    @Override
    public List<Category> search(String keyword) {
        if (isBlank(keyword)) {
            return categoryDao.getAll();
        }
        return categoryDao.search(keyword.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
