package org.example.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử cấu hình SiteMesh 3 Decorator theo tài liệu giảng viên")
public class SiteMesh3ConfigurationTest {

    private final Path projectRoot = Path.of(System.getProperty("user.dir"));

    @Test
    @DisplayName("Bước 1: Kiểm tra pom.xml đã khai báo dependency sitemesh-webfilter 3.3.0")
    void testStep1_PomDependency() throws Exception {
        Path pomPath = projectRoot.resolve("pom.xml");
        assertTrue(Files.exists(pomPath), "pom.xml phải tồn tại");

        String content = Files.readString(pomPath);
        assertTrue(content.contains("sitemesh-webfilter"), "pom.xml phải có artifactId sitemesh-webfilter");
        assertTrue(content.contains("3.3.0"), "pom.xml phải sử dụng phiên bản 3.3.0");
        assertTrue(content.contains("org.sitemesh"), "pom.xml phải có groupId org.sitemesh");
    }

    @Test
    @DisplayName("Bước 2: Kiểm tra web.xml đã cấu hình ConfigurableSiteMeshFilter với url-pattern /*")
    void testStep2_WebXmlFilter() throws Exception {
        Path webXmlPath = projectRoot.resolve("src/main/webapp/WEB-INF/web.xml");
        assertTrue(Files.exists(webXmlPath), "web.xml phải tồn tại");

        String content = Files.readString(webXmlPath);
        assertTrue(content.contains("org.sitemesh.config.ConfigurableSiteMeshFilter"),
                "web.xml phải cấu hình ConfigurableSiteMeshFilter");
        assertTrue(content.contains("<url-pattern>/*</url-pattern>"),
                "SiteMesh Filter phải được map tới /*");
    }

    @Test
    @DisplayName("Bước 3: Kiểm tra WEB-INF/sitemesh3.xml cấu hình đúng các mapping")
    void testStep3_SiteMesh3Xml() throws Exception {
        Path sitemeshXmlPath = projectRoot.resolve("src/main/webapp/WEB-INF/sitemesh3.xml");
        assertTrue(Files.exists(sitemeshXmlPath), "sitemesh3.xml phải tồn tại trong WEB-INF");

        String content = Files.readString(sitemeshXmlPath);
        assertTrue(content.contains("path=\"/*\""), "Phải có mapping path=\"/*\"");
        assertTrue(content.contains("decorator=\"web.jsp\""), "Phải map tới decorator web.jsp");
        assertTrue(content.contains("path=\"/admin/*\""), "Phải có mapping path=\"/admin/*\"");
        assertTrue(content.contains("decorator=\"admin.jsp\""), "Phải map tới decorator admin.jsp");
        assertTrue(content.contains("path=\"/api*\""), "Phải loại trừ api path=\"/api*\"");
        assertTrue(content.contains("exclude=\"true\""), "Phải có thuộc tính exclude=\"true\"");
    }

    @Test
    @DisplayName("Bước 4: Kiểm tra cấu trúc thư mục common, admin, web, decorators")
    void testStep4_DirectoryStructure() {
        Path webapp = projectRoot.resolve("src/main/webapp");

        // Thư mục common/web
        assertTrue(Files.exists(webapp.resolve("common/web/header.jsp")), "Phải có common/web/header.jsp");
        assertTrue(Files.exists(webapp.resolve("common/web/footer.jsp")), "Phải có common/web/footer.jsp");
        assertTrue(Files.exists(webapp.resolve("common/web/left.jsp")), "Phải có common/web/left.jsp");
        assertTrue(Files.exists(webapp.resolve("common/web/navbar.jsp")), "Phải có common/web/navbar.jsp");
        assertTrue(Files.exists(webapp.resolve("common/web/topbar.jsp")), "Phải có common/web/topbar.jsp");

        // Thư mục common/admin
        assertTrue(Files.exists(webapp.resolve("common/admin/header.jsp")), "Phải có common/admin/header.jsp");
        assertTrue(Files.exists(webapp.resolve("common/admin/footer.jsp")), "Phải có common/admin/footer.jsp");
        assertTrue(Files.exists(webapp.resolve("common/admin/left.jsp")), "Phải có common/admin/left.jsp");

        // Các file common dùng chung
        assertTrue(Files.exists(webapp.resolve("common/info.jsp")), "Phải có common/info.jsp");
        assertTrue(Files.exists(webapp.resolve("common/taglib.jsp")), "Phải có common/taglib.jsp");

        // Thư mục WEB-INF/decorators
        assertTrue(Files.exists(webapp.resolve("WEB-INF/decorators/web.jsp")), "Phải có WEB-INF/decorators/web.jsp");
        assertTrue(Files.exists(webapp.resolve("WEB-INF/decorators/admin.jsp")), "Phải có WEB-INF/decorators/admin.jsp");
        assertTrue(Files.exists(webapp.resolve("WEB-INF/decorators/login.jsp")), "Phải có WEB-INF/decorators/login.jsp");
    }

    @Test
    @DisplayName("Bước 5: Kiểm tra cấu trúc trang layout web.jsp theo đúng tài liệu giảng viên")
    void testStep5_WebLayoutJsp() throws Exception {
        Path webJspPath = projectRoot.resolve("src/main/webapp/WEB-INF/decorators/web.jsp");
        assertTrue(Files.exists(webJspPath), "web.jsp phải tồn tại");

        String content = Files.readString(webJspPath);
        assertTrue(content.contains("/common/web/header.jsp") || content.contains("/commons/web/header.jsp"),
                "Layout web.jsp phải include header.jsp");
        assertTrue(content.contains("<sitemesh:write property=\"body\""),
                "Layout web.jsp phải có thẻ sitemesh:write property=\"body\"");
        assertTrue(content.contains("/common/web/footer.jsp") || content.contains("/commons/web/footer.jsp"),
                "Layout web.jsp phải import footer.jsp");
        assertTrue(content.contains("bootstrap"), "Layout web.jsp phải tích hợp Bootstrap");
    }
}
