<%--
  Created by IntelliJ IDEA.
  User: chr
  Date: 19-9-19
  Time: 下午7:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="comment/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <!-- 静态包含-->
    <%@include file="comment/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="pane1 pane1-default text-center">
            <div class="pannel-heading">
                <h1>${seckill.name}</h1>
            </div>
        </div>
        <div class="pane1-body">

        </div>
    </div>
</body>



<!-- jQuery文件 务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>
