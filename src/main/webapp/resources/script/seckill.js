//存放主要的交互逻辑js代码
//javaScript 模块化
var seckill = {
    //封装秒杀相关的ajax的url
    URL: {
        now:function () {
            return '/seckill_war_exploded/seckill/time/now';
        },
        exposer:function (seckillId) {
            return '/seckill_war_exploded/seckill/' + seckillId + "/exposer";
        },
        execution:function (seckillId, md5) {
            return '/seckill_war_exploded/seckill/'+ seckillId + '/' + md5 + '/execute';
        }
    },
    handleSeckillkill: function(seckillId, node) {


        console.log("进入 处理秒杀接口逻辑");

        //处理秒杀逻辑
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');

        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //返回值存在且判断为true
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //已经开启秒杀 秒杀按钮可以显示 用户执行秒杀操作
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var seckillUrl = seckill.URL.execution(seckillId, md5);

                    $('#killBtn').one('click', function () {

                        console.log("开始进行秒杀,按钮被禁用");

                        //绑定秒杀请求操作
                        //1.禁用按钮
                        $(this).addClass("disabled");

                        //2.发送请求
                        $.post(seckillUrl,{}, function (result) {
                            var killResult = result['data'];

                            console.log("KillResult: "+ killResult);

                            var state = killResult['state'];
                            var stateInfo = killResult['stateInfo'];
                            console.log("秒杀状态" + stateInfo);

                            //3.显示秒杀结果
                            node.html('<span class="label label-success">'+stateInfo+'</span>');
                        });
                    });
                    node.show();

                }else {
                    //未到开启秒杀时间 (客户端pc机和服务端pc时间)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算计时逻辑
                    seckill.countDown(seckillId, now, start, end);
                }

            }else {
                console.log("result: "+result);
            }
        });

    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        }
        return false;
    },
    //判断是否达到秒杀时间
    countDown: function(seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');

        //秒杀结束
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        }else if (nowTime < startTime) {
            //秒杀未开启
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function (event) {
               //时间格式
               var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
               seckillBox.html(format);
               /*时间完成后回调秒杀地址(回调事件)*/
            }).on('finish.countdown', function () {
                //获取秒杀地址 控制显示逻辑
                //用户执行秒杀 todo
                seckill.handleSeckillkill(seckillId, seckillBox);
            });
        }else {
            //秒杀开启
            seckill.handleSeckillkill(seckillId, seckillBox);
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证 计时交互
            //规划流程
            var killPhone = $.cookie('killPhone');

            console.log("dddd");

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
            }else {
                console.log("在cookie中找到了电话号码,开启计时");

                //已经登录
                //计时交互
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];

                // alert("id " + seckillId);
                // alert("开始秒杀时间=======" + startTime);
                // alert("结束秒杀时间========" + endTime);

                $.get(seckill.URL.now(), {}, function (result) {
                    //存在且为true
                    if (result && result['success']) {
                        var nowTime = result['data'];

                        // alert(seckillId +" " +nowTime +" "+ startTime +" "+ endTime);

                        //时间判断
                        seckill.countDown(seckillId, nowTime, startTime, endTime);

                        console.log(seckillId +" " +nowTime +" "+ startTime +" "+ endTime);
                    }else {
                        console.log('result: '+ result);
                    }
                })
            }
        }
    }
};