package vn.iotstar.service;

import vn.iotstar.dao.CategoryDao;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;

import java.util.List;

/**
 * Lớp triển khai ICategoryService xử lý nghiệp vụ danh mục trước khi gọi DAO.
 * Đảm bảo phân biệt rõ ràng giữa "không tìm thấy" và "lỗi kết nối CSDL", không nuốt ngoại lệ.
 */
public class CategoryServiceImpl implements ICategoryService {

    public ICategoryDao cateDao = new CategoryDao();

    @Override
    public List<Category> findAll() {
        return cateDao.findAll();
    }

    @Override
    public Category findById(int id) {
        if (id <= 0) {
            return null;
        }
        try {
            return cateDao.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối cơ sở dữ liệu khi tìm danh mục theo ID: " + id + " - " + e.getMessage(), e);
        }
    }

    @Override
    public List<Category> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        try {
            return cateDao.searchByName(keyword.trim());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi truy vấn cơ sở dữ liệu khi tìm kiếm danh mục: " + e.getMessage(), e);
        }
    }

    @Override
    public void insert(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Thông tin danh mục không được để trống");
        }
        String name = category.getCategoryname();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được phép rỗng");
        }
        category.setCategoryname(name.trim());

        // Kiểm tra danh mục trùng tên
        Category cate = this.findByCategoryname(category.getCategoryname());
        if (cate != null) {
            throw new IllegalArgumentException("Tên danh mục '" + category.getCategoryname() + "' đã tồn tại!");
        }

        cateDao.insert(category);
    }

    @Override
    public void update(Category category) {
        if (category == null || category.getCategoryid() <= 0) {
            throw new IllegalArgumentException("Mã danh mục không hợp lệ");
        }
        String name = category.getCategoryname();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được phép rỗng");
        }
        category.setCategoryname(name.trim());

        // Kiểm tra danh mục tồn tại trước khi cập nhật
        Category cate = this.findById(category.getCategoryid());
        if (cate == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục có ID: " + category.getCategoryid());
        }

        // Kiểm tra trùng tên với danh mục khác
        Category existByName = this.findByCategoryname(category.getCategoryname());
        if (existByName != null && existByName.getCategoryid() != category.getCategoryid()) {
            throw new IllegalArgumentException("Tên danh mục '" + category.getCategoryname() + "' đã được sử dụng bởi danh mục khác!");
        }

        cateDao.update(category);
    }

    @Override
    public void delete(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ: " + id);
        }
        Category cate = this.findById(id);
        if (cate == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id);
        }
        cateDao.delete(id);
    }

    @Override
    public int count() {
        return cateDao.count();
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        if (page < 0) page = 0;
        if (pagesize <= 0) pagesize = 10;
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public Category findByCategoryname(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            return cateDao.findByCategoryname(name.trim());
        } catch (Exception e) {
            // Không nuốt lỗi database! Báo lỗi rõ ràng nếu có sự cố kết nối/truy vấn CSDL
            throw new RuntimeException("Lỗi kết nối cơ sở dữ liệu khi kiểm tra tên danh mục: " + e.getMessage(), e);
        }
    }
}
