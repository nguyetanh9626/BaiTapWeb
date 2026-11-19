package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filter chuyển tiếp request dispatch trên Tomcat 11 tương thích với SiteMesh 3.
 * Trên Tomcat 11 / Servlet 6.0, RequestDispatcher.forward() đóng và commit response wrapper trước khi trả về,
 * làm cho bộ đệm SiteMesh 3 không thể ghi decorator layout vào client.
 * Bằng cách chuyển lời gọi forward() thành include() ở tầng RequestDispatcher, response vẫn ở trạng thái
 * mở (uncommitted), cho phép SiteMesh 3 parse hoàn chỉnh nội dung JSP và bọc decorator template Bootstrap 5.
 */
public class TomcatDispatchFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpReq && response instanceof HttpServletResponse httpResp) {
            HttpServletRequest wrappedReq = new HttpServletRequestWrapper(httpReq) {
                @Override
                public RequestDispatcher getRequestDispatcher(String path) {
                    RequestDispatcher rd = super.getRequestDispatcher(path);
                    if (rd == null) {
                        return null;
                    }
                    return new RequestDispatcher() {
                        @Override
                        public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                            rd.include(req, res);
                        }

                        @Override
                        public void include(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                            rd.include(req, res);
                        }
                    };
                }
            };
            chain.doFilter(wrappedReq, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
