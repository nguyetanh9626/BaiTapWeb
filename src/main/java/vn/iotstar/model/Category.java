package vn.iotstar.model;

import java.io.Serializable;

/**
 * Model Category JDBC theo Slide 9 tài liệu "Hướng dẫn CRUD với Servlet JDBC".
 */
public class Category extends org.example.model.Category implements Serializable {
    private static final long serialVersionUID = 1L;

    public Category() {
        super();
    }

    public Category(int cateid, String catename, String icon) {
        super(cateid, catename, icon);
    }
}
