package org.example.model;

import java.io.Serializable;

/**
 * Model Category tương ứng bảng Category (cate_id, cate_name, icons) trong SQL Server.
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int cateid;
    private String catename;
    private String icon;

    public Category() {
    }

    public Category(String catename, String icon) {
        this.catename = catename;
        this.icon = icon;
    }

    public Category(int cateid, String catename, String icon) {
        this.cateid = cateid;
        this.catename = catename;
        this.icon = icon;
    }

    public int getCateid() {
        return cateid;
    }

    public void setCateid(int cateid) {
        this.cateid = cateid;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Các getter/setter alias theo tài liệu 14_HD_Servlet_JDBC_CRUD.pdf
    public int getId() {
        return cateid;
    }

    public void setId(int id) {
        this.cateid = id;
    }

    public int getCateId() {
        return cateid;
    }

    public void setCateId(int cateId) {
        this.cateid = cateId;
    }

    public String getName() {
        return catename;
    }

    public void setName(String name) {
        this.catename = name;
    }

    public String getCateName() {
        return catename;
    }

    public void setCateName(String cateName) {
        this.catename = cateName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "cateid=" + cateid +
                ", catename='" + catename + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
