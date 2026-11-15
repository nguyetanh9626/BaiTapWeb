package org.example.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Các hằng số dùng chung trong ứng dụng (Session, Cookie, Role, đường dẫn upload và JSP).
 */
public final class Constant {

    private Constant() {
    }

    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_USER = "user";
    public static final String SESSION_USERNAME = "username";
    public static final String COOKIE_REMEMBER = "username";

    /** Thời gian tồn tại Cookie Remember Me: 30 phút (tính bằng giây). */
    public static final int COOKIE_MAX_AGE_SECONDS = 30 * 60;

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_MANAGER = 2;
    public static final int ROLE_USER = 5;

    /**
     * Thư mục gốc lưu file upload (cấu hình qua BAITAPWEB_UPLOAD_DIR, app.upload.dir trong db.properties,
     * hoặc mặc định tại thư mục baitapweb-uploads trong user.home để tránh phụ thuộc ổ cứng cố định).
     */
    public static final String DIR = resolveBaseUploadDir();
    public static final String CATEGORY_UPLOAD_SUBDIR = "category";

    private static String resolveBaseUploadDir() {
        String envDir = trimOrNull(System.getenv("BAITAPWEB_UPLOAD_DIR"));
        if (envDir != null) {
            return envDir;
        }
        Properties props = new Properties();
        try (InputStream in = Constant.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                String propDir = trimOrNull(props.getProperty("app.upload.dir"));
                if (propDir != null) {
                    return propDir;
                }
            }
        } catch (IOException ignored) {
            // Dùng thư mục mặc định bên dưới nếu không đọc được cấu hình tùy chọn
        }
        return System.getProperty("user.home") + File.separator + "baitapweb-uploads";
    }

    /**
     * Đường dẫn thư mục lưu ảnh upload của Category.
     */
    public static java.nio.file.Path getCategoryUploadDir() {
        return Paths.get(DIR, CATEGORY_UPLOAD_SUBDIR);
    }

    private static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static final class Path {
        private Path() {
        }

        public static final String INDEX = "/views/index.jsp";
        public static final String LOGIN = "/views/login.jsp";
        public static final String REGISTER = "/views/register.jsp";
        public static final String ERROR = "/views/error.jsp";
        public static final String MY_ACCOUNT = "/views/myaccount.jsp";
        public static final String ADMIN_HOME = "/views/admin-home.jsp";
        public static final String MANAGER_HOME = "/views/manager-home.jsp";

        public static final String CATEGORY_LIST = "/views/admin/list-category.jsp";
        public static final String CATEGORY_ADD = "/views/admin/add-category.jsp";
        public static final String CATEGORY_EDIT = "/views/admin/edit-category.jsp";
        public static final String ADMIN_USER_LIST = "/views/admin/list-users.jsp";
    }
}
