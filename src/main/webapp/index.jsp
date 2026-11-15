<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Tự động chuyển hướng về servlet /home nếu người dùng truy cập trực tiếp file gốc
    response.sendRedirect(request.getContextPath() + "/home");
%>

