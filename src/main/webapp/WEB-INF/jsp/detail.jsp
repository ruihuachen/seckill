<%--
  Created by IntelliJ IDEA.
  User: chr
  Date: 19-9-19
  Time: 下午7:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="comment/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <!-- 静态包含-->
    <%@include file="comment/head.jsp" %>

</head>
<body>
<div class="container">
    <div class="pane1 pane1-default text-center">
        <div class="pannel-heading">
            <h1>${seckill.name}</h1>
        </div>
        <div class="pane1-body">
            <h2 class="text-dialog">
                <!-- 显示time图标-->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>

<!-- 弹出层 手机验证-->
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-litter text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                </h3>
            </div>

            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey" placeholder="填写手机号码" class="form-control">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 验证信息-->
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    提交
                </button>
            </div>
        </div>
    </div>
</div>
</body>

<!-- jQuery文件 务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!-- JQuery cookie操作插件-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>

<!-- JQuery countDown倒计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.js"></script>

<!-- 交互逻辑-->
<script src="${pageContext.request.contextPath}/resources/script/seckill.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function (){
        //使用EL表达式
        seckill.detail.init({
            seckillId: ${seckill.seckillId},
            startTime: ${seckill.startTime.time},
            endTime: ${seckill.endTime.time}
        });
    });
</script>


</html>
