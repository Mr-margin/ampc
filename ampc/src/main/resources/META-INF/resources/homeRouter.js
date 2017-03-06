/**
 * Created by lvcheng on 2017/2/24.
 */
vipspa.start({
  view: '.ui-view',// 页面路由的div
  router: {
    '/sy': {        //首页
    	templateUrl: 'a.html',
    	controller: ''
    },
    '/yqd':{       //源清单
    	templateUrl: 'a.html',
    	controller: ''
    },
    '/rwgl': {      //任务管理
    	templateUrl: 'webApp/task01/task01.html',
    	controller: 'webApp/task01/task01.js'
    },
    '/yabj': {      //预评估
    	templateUrl: 'webApp/task02/task02.html',
    	controller: 'webApp/task02/task02.js'
    },
    '/xgpg': {      //效果评估
        templateUrl: 'webApp/Analysis02/a0201.html',
        controller: 'webApp/Analysis02/a0201.js'
    },
    '/kqzlyb': {      //空气质量预报
    	templateUrl: 'a.html',
    	controller: ''
    },
    '/sjkj': {      //数据空间
    	templateUrl: 'a.html',
    	controller: ''
    },
    '/hsz': {        //首页回收站
    	templateUrl: 'webApp/Analysis01/a0101.html',
    	controller: 'webApp/Analysis01/a0101.js'
    },
    '/csbj':{
      templateUrl: 'webApp/task03/task03.html',
      controller: 'webApp/task03/task03.js'
    },
    '/test': {
    	templateUrl: 'a.html',
    	controller: ''
    },
    'defaults': '/rwgl'     //默认显示页面
  },
  errorTemplateId: '#error'//错误显示页面
});




var parameterPar = {total: '', data: {}};
var userId = 1;
/*������*/
function ajaxPost(url, parameter) {
    parameterPar.data = parameter;
    var p = JSON.stringify(parameterPar);
    //return $.ajax(BackstageIP + url, {
    return $.ajax('/ampc' + url, {
        contentType: "application/json",
        type: "POST",
        async: true,
        dataType: 'JSON',
        data: p
    })
}

var llqHeight = document.documentElement.clientHeight;
var cssStyle = '#sidebar .panel-body{overflow:auto;border-top:0;border-bottom:0;padding:0;height: '+ (llqHeight-400) +'px;} ';
$('head').append($('<style></style>').html(cssStyle));
$("#Route_conter").css("height",llqHeight-150+"px");


$('#sidebar a').click(function (e) {
    e.preventDefault();
    var href = e.target.hash.replace('#','#/');
    var a = $('#clickA');
    if(a.attr('href') == href) return;
    a.attr('href',href);
    a[0].click();
});