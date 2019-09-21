//存放主要的交互逻辑js代码
//javaScript 模块化
var seckill = {
    //封装秒杀相关的ajax的url
    URL: {

    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        }
        return false;
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证 计时交互
            //规划流程
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];

            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                console.log("未填写手机号码");

                //绑定手机号
                //控制输出
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    console.log("提交手机号码按钮被点击");

                    var inputPhone = $('#killPhoneKey').val();
                    console.log("inputPhone" + inputPhone);


                    if (seckill.validatePhone(inputPhone)) {
                        //手机号写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/', secure: false});

                        //验证通过则刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html("<lable class='label1 label-danger'>手机号错误</lable>").show(300);
                    }
                });
            }
        }
    }
};