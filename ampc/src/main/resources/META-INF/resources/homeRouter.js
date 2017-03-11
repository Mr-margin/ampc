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
    '/sjxl': {       //空气质量报告》》时间序列
    	templateUrl: 'webApp/timeArray01/timeArray.html',
    	controller: 'webApp/timeArray01/timeArray.js'
    },
    '/effectTime': {       //效果评估》》时间序列
    	templateUrl: 'webApp/effectEvaluate/effectEval01.html',
    	controller: 'webApp/effectEvaluate/effectEval01.js'
    },
    'defaults': '/rwgl'     //默认显示页面
  },
  errorTemplateId: '#error'//错误显示页面
});




var parameterPar = {total: '', data: {}};
var userId = 1;
/*看名字*/
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

function ajaxPost_w(url, parameter) {
    //parameterPar.data = parameter;
    //var p = JSON.stringify(parameterPar);
    //return $.ajax(BackstageIP + url, {
    return $.ajax(url, {
        contentType: "application/json",
        type: "POST",
        async: true,
        dataType: 'JSON',
        data: JSON.stringify(parameter)
    })
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