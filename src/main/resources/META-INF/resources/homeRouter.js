/**
 * Created by lvcheng on 2017/2/24.
 */

var userMsg;
var ls = window.sessionStorage;
var parameterPar = {token: '', data: {}};
var userId;

var dps_um = getSessionMsg();

$.when(dps_um).then(function(){
  if(!userMsg){
    window.location.href="index.html";
    return;
  }
  userId = userMsg.userId;
  if(!userId){
    window.location.href="index.html";
  }else{

// 当我修改完新的功能栏的时候，可以对此处修改
    var llqHeight = document.documentElement.clientHeight;
    var cssStyle = '.qjbjDiv,.qdCreate,.qdListBox{height:'+ (llqHeight-138) +'px;}#sidebar .panel-body{overflow:auto;border-top:0;border-bottom:0;padding:0;height: '+ (llqHeight-441) +'px;} ';
    $('head').append($('<style></style>').html(cssStyle));
    $("#Route_conter").css("height",llqHeight-150+"px");
    $(".qdCreate").css("height",llqHeight-138+"px");
//重写的全局绑定事件
    $(function(){
      var auri=window.location.hash;
      console.log($("[href='"+auri+"']").parent().addClass("active").siblings().removeClass("active"));
      $("[href='"+auri+"']").parent().addClass("active").siblings().removeClass("active").end().parent().parent(".candrop").addClass("active").siblings().removeClass("active").find(".candrop>p.active").removeClass();
      //功能栏一级菜单的点击事件
      $("#sidebarlt>ul").on("click","li",function () {
        $(this).siblings().removeClass("active").end().addClass("active");
      });
      //功能栏二级菜单的点击事件
      $('#sidebarlt>ul').on("click","p",function () {
        $(this).siblings().removeClass("active").end().addClass("active").parent().parent().siblings().find("p").removeClass("active");
      })
    });

    vipspa.start({
      view: '.ui-view',// 页面路由的div
      router: {
        '/sy': {        //首页
          templateUrl: 'a.html',
          controller: ''
        },
        '/yqd':{       //源清单
          templateUrl: 'webApp/yqd/qdList/qdList.html',
          controller: 'webApp/yqd/qdList/qdList.js'
        },
        '/newQd':{       //源清单
          templateUrl: 'webApp/yqd/createQD/createNewQd.html',
          controller: 'webApp/yqd/createQD/createNewQd.js'
        },


        '/rwgl': {      //任务管理-任务情景
          templateUrl: 'webApp/task01/task01.html',
          controller: 'webApp/task01/task01.js'
        },
        '/yabj': {      //任务管理-区域时段
          templateUrl: 'webApp/task02/task02.html',
          controller: 'webApp/task02/task02.js'
        },
        '/csbj':{		//任务管理-措施子措施
          templateUrl: 'webApp/task03/task03.html',
          controller: 'webApp/task03/task03.js'
        },
        '/rwgl_reductAnalys':{		//任务管理-减排分析
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
          templateUrl: 'a.html',
          controller: ''
        },
        '/kqzlyb_v2': {      //空气质量预报-时间序列
          templateUrl: 'webApp/kqzlyb/v2/timeArray.html',
          controller: 'webApp/kqzlyb/v2/timeArray.js'
        },
        '/kqzlyb_v3': {      //空气质量预报-水平分布
          templateUrl: 'a.html',
          controller: ''
        },
        '/kqzlyb_v4': {      //空气质量预报-垂直分布
          templateUrl: 'a.html',
          controller: ''
        },


        '/userSZ':{//用户设置
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
  var url = '';
  ajaxPost(url, {}).success(function () {
    window.location.href="index.html";
  })
}

/*获取session信息*/
function getSessionMsg(){
  var url='/user/get_sessionInfo';
  return ajaxPost(url,{}).success(function(res){
    userMsg = res.data;
  })
}

/*初始化zTree数据*/
function initZTree() {
  var url = '/area/find_areas_new';
  ajaxPost(url, {
    userId: userId
  }).success(function (res) {
    zTreeData = res.data;
  });
}

// 左侧菜单栏隐藏展开功能
$("#toggle-sidebar").on("click",function(e){
  if($(this).hasClass("toggle-flag")){
    $("#sidebarlt").show();
    $("#content-title").css({'left':'210px'});
    $("#content").css({'margin-left':'210px'});
    $(this).removeClass("toggle-flag");
  }else{
    $("#sidebarlt").hide();
    $("#content-title").css({'left':'0px'});
    $("#content").css({'margin-left':'0px'});
    $(this).addClass("toggle-flag");
  }
});

/*当url改变的时候，左侧菜单栏中内容改变*/
$(window).bind('hashchange', function(e) {
  console.log('url改变了====' + e.target.location.hash);
  if(e.target.location.hash == '#/yabj'){
    $('.qyCon').removeClass('disNone');
  }else{
    $('.qyCon').addClass('disNone');
  }

  if(e.target.location.hash == '#/csbj'){
    $('.csCon').removeClass('disNone');
  }else{
    $('.csCon').addClass('disNone');
  }

  if(e.target.location.hash == '#/rwgl_reductAnalys'){
    $('.jpfxCon').removeClass('disNone');
  }else{
    $('.jpfxCon').addClass('disNone');
  }
});


/*左侧菜单点击后处理路由信息*/
$('#sidebar a').click(function (e) {
  e.preventDefault();
  var href = e.target.hash.replace('#','#/');
  var a = $('#clickA');
  if(a.attr('href') == href) return;
  if(href == '#/xgpg'|| href == '#/kqzlyb')return;
  a.attr('href',href);
  a[0].click();
});

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

/*看名字*/
function ajaxPost(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  return $.ajax('/ampc' + url, {
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