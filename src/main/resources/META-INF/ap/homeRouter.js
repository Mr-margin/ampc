/**
 * Created by lvcheng on 2017/2/24.
 */

var userMsg, llqHeight;
var ls = window.sessionStorage;
var parameterPar = {token: '', data: {}};
var userId;

var dps_um = getSessionMsg();

$.when(dps_um).then(function () {
    if (!userMsg) {
      window.location.href = "index.html";
      return;
    }
      userId = userMsg.userId;
      if (!userId) {
        window.location.href = "index.html";
      } else {

// 当我修改完新的功能栏的时候，可以对此处修改
        llqHeight = document.documentElement.clientHeight;
        var cssStyle = '.qjbjDiv,.qdCreate,.qdListBox{height:' + (llqHeight - 138) + 'px;}#sidebar .panel-body{overflow:auto;border-top:0;border-bottom:0;padding:0;height: ' + (llqHeight - 441) + 'px;} ';
        $('head').append($('<style></style>').html(cssStyle));
        // $("#Route_conter").css("height",llqHeight-150+"px");
        $(".qdCreate").css("height", llqHeight - 138 + "px");
        console.log(0);
        $(function () {
            //文档加载完成后需要执行的代码
            var auri = window.location.hash;
            // console.log($("[href='" + auri + "']").parent().addClass("active").siblings().removeClass("active"));
            $("[href='" + auri + "']").parent().addClass("active").siblings().removeClass("active").end().parentsUntil('#sidebarlt>ul>li').last().parent(".candrop").addClass("active").siblings().removeClass("active").find(".candrop>p.active").removeClass();
            //功能栏一级菜单的点击事件
          console.log(1);
            $("#sidebarlt>ul").on("click", "li", function () {
                $(this).siblings().removeClass("active").end().addClass("active").siblings().find(".dropmenu>div p.active").removeClass('active');
            });
            //功能栏二级菜单的点击事件
            $('#sidebarlt>ul').on("click", "p", function () {
                $(this).siblings().removeClass("active").end().addClass("active").parentsUntil("#sidebarlt>ul>li").last().parent().siblings().find("p.active").removeClass("active");
            })

            /*为功能栏增加滚动条
             * 功能栏下所有展开框都需要滚动条
             * 要求：高度自适应*/
            $("#sidebarlt>ul>li>.dropmenu>div").slimScroll({
                height: '100%'
            })
        });
        // 左侧菜单栏隐藏展开功能
        $("#toggle-sidebar").on("click", function (e) {
            console.log(1);
          if ($(this).hasClass("toggle-flag")) {
            $("#sidebarlt").show();
            $("#content-title").css({'left': '210px'});
            $("#content").css({'left': '210px'});
            $(this).removeClass("toggle-flag");
          } else {
            $("#sidebarlt").hide();
            $("#content-title").css({'left': '0px'});
            $("#content").css({'left': '0px'});
            $(this).addClass("toggle-flag");
          }
        });

          /*当url改变的时候，左侧菜单栏中内容改变*/
        $(window).bind('hashchange', function (e) {
          console.log('url改变了====' + e.target.location.hash);
          if (e.target.location.hash == '#/yabj') {
            $('.qyCon').removeClass('disNone');
          } else {
            $('.qyCon').addClass('disNone');
          }

          if (e.target.location.hash == '#/csbj') {
            $('.csCon').removeClass('disNone');
          } else {
            $('.csCon').addClass('disNone');
          }

          if (e.target.location.hash == '#/rwgl_reductAnalys') {
            $('.jpfxCon').removeClass('disNone');
          } else {
            $('.jpfxCon').addClass('disNone');
          }
        });


          /*左侧菜单点击后处理路由信息*/
        $('#sidebar a').click(function (e) {
          e.preventDefault();
          var href = e.target.hash.replace('#', '#/');
          var a = $('#clickA');
          if (a.attr('href') == href) return;
          if (href == '#/xgpg' || href == '#/kqzlyb')return;
          a.attr('href', href);
          a[0].click();
        });

        vipspa.start({
            view: '.ui-view',// 页面路由的div
            router: {
                '/sy': {        //首页
                    templateUrl: 'a.html',
                    controller: ''
                },
                '/yqd': {       //源清单
                    templateUrl: 'webApp/yqd/qdList/qdList.html',
                    controller: 'webApp/yqd/qdList/qdList.js'
                },
                '/newQd': {       //源清单
                    templateUrl: 'webApp/yqd/createQD/createNewQd.html',
                    controller: 'webApp/yqd/createQD/createNewQd.js'
                },


                '/rwgl': {      //任务管理-任务情景
                    templateUrl: 'webApp/task01/task1.html',
                    controller: 'webApp/task01/task1.js'
                },
                '/yabj': {      //任务管理-区域时段
                    templateUrl: 'webApp/task02/task02.html',
                    controller: 'webApp/task02/task02.js'
                },
                '/csbj': {		//任务管理-措施子措施
                    templateUrl: 'webApp/task03/task03.html',
                    controller: 'webApp/task03/task03.js'
                },
                '/rwgl_reductAnalys': {		//任务管理-减排分析
                    templateUrl: 'webApp/task02/reductAnalys.html',
                    controller: 'webApp/task02/reductAnalys.js'
                },


                '/xgpg_v1': {      //效果评估-减排分析
                    templateUrl: 'webApp/xgpg/v1/reductAnalys.html',
                    controller: 'webApp/xgpg/v1/reductAnalys.js'
                },
                '/xgpg_v2': {      //效果评估-网格化排放
                    templateUrl: 'webApp/xgpg/v2/Gridemission.html',
                    controller: 'webApp/xgpg/v2/Gridemission.js'
                },
                '/xgpg_v3': {      //效果评估-时间序列
                    templateUrl: 'webApp/xgpg/v3/effectEval01.html',
                    controller: 'webApp/xgpg/v3/effectEval01.js'
                },
                '/xgpg_v4': {      //效果评估-水平分布
                    templateUrl: 'webApp/xgpg/v4/a0101.html',
                    controller: 'webApp/xgpg/v4/a0101.js'
                },
                '/xgpg_v5': {      //效果评估-垂直分布
                    templateUrl: 'webApp/xgpg/v5/verticalSpread02.html',
                    controller: 'webApp/xgpg/v5/verticalSpread02.js'
                },
                '/xgpg_v6': {      //效果评估-评估报告
                    templateUrl: '',
                    controller: ''
                },


                '/kqzlyb_v1': {      //空气质量预报-预报检验
                    templateUrl: 'webApp/kqzlyb/v1/forecastTest.html',
                    controller: 'webApp/kqzlyb/v1/forecastTest.js'
                },
                '/kqzlyb_v2': {      //空气质量预报-时间序列
                    templateUrl: 'webApp/kqzlyb/v2/timeArray.html',
                    controller: 'webApp/kqzlyb/v2/timeArray.js'
                },
                '/kqzlyb_v3': {      //空气质量预报-水平分布
                    templateUrl: 'webApp/kqzlyb/v3/distributed.html',
                    controller: 'webApp/kqzlyb/v3/distributed.js'
                },
                '/kqzlyb_v4': {      //空气质量预报-垂直分布
                    templateUrl: 'webApp/kqzlyb/v4/verticalSpread02.html',
                    controller: 'webApp/kqzlyb/v4/verticalSpread02.js'
                },


                '/userSZ': {//用户设置
                    templateUrl: 'a.html',
                    controller: ''
                },
                '/sjkj': {      //数据空间
                    templateUrl: 'a.html',
                    controller: ''
                },
                // '/hsz': {        //首页回收站
                //   templateUrl: '',
                //   controller: ''
                // },
                '/test': {
                    templateUrl: 'a.html',
                    controller: ''
                },

                'defaults': '/rwgl'     //默认显示页面
            },
            errorTemplateId: '#error'//错误显示页面
        });

        initZTree()
    }
})


/*退出登录*/
function dengluOut() {
    var url = '/ampc/user/loginOut';
    ajaxPost(url, {}).success(function () {
        window.location.href = "index.html";
    })
}

/*获取session信息*/
function getSessionMsg() {
    var url = '/ampc/user/get_sessionInfo';
    return ajaxPost(url, {}).success(function (res) {
        userMsg = res.data;
    })
}

/*初始化zTree数据*/
function initZTree() {
    var url = '/ampc/area/find_areas_new';
    ajaxPost(url, {
        userId: userId
    }).success(function (res) {
        zTreeData = res.data;
    });
}



//经度转墨卡托
function handle_x(x) {
    return (x / 180.0) * 20037508.34;
}

//纬度度转墨卡托
function handle_y(y) {
    if (y > 85.05112) {
        y = 85.05112;
    }
    if (y < -85.05112) {
        y = -85.05112;
    }
    y = (Math.PI / 180.0) * y;
    var tmp = Math.PI / 4.0 + y / 2.0;
    return 20037508.34 * Math.log(Math.tan(tmp)) / Math.PI;
}
/*封装一个遮罩层载入效果
 * 使用插件为blockUI
 * parameter：selector，method
 * selector：string，为css选择器，按照jquery中$的选择器
 * state：string，'start'or'end','start'表示启动载入效果，'end'表示关闭载入效果
 * */
function zmblockUI(selector, method) {
    if ($(selector).length == 0) {
        console.info(selector + "没有匹配dom元素,无法执行加载动画");
        return
    }
    if (typeof method === undefined || ["start", "end"].toString().indexOf(method) == -1) {
        console.info(selector + "的加载动画的method参数有误！");
        return
    }
    if (method === "start") {
        $(selector).block({
            message: '<div class="loading-message-boxed"><img src="images/loading-spinner-blue.gif"><span>&nbsp;&nbsp;努力加载中…</span></div>',
            css: {border: 0, backgroundColor: 'transparent'},
            overlayCSS: {opacity: 0.4}
        });
    } else if (method === 'end') {
        $(selector).unblock();
    }
}

/*看名字*/
function ajaxPost(url, parameter) {
    parameterPar.data = parameter;
    var p = JSON.stringify(parameterPar);
    return $.ajax(localhttp + url, {
        contentType: "application/json",
        type: "POST",
        async: true,
        dataType: 'JSON',
        data: p
    })
}

function ajaxPost_w(url, parameter) {
    return $.ajax(url, {
        contentType: "application/json",
        type: "POST",
        async: true,
        dataType: 'JSON',
        data: JSON.stringify(parameter)
    })
}
