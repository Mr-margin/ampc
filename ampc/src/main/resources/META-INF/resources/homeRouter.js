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
      
      
    '/xgpg_v1': {      //效果评估-减排分析
        templateUrl: 'webApp/xgpg/v1/reductAnalys.html',
        controller: 'webApp/xgpg/v1/reductAnalys.js'
    },
    '/xgpg_v2': {      //效果评估-网格化排放
    	templateUrl: '',
    	controller: ''
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
    '/hsz': {        //首页回收站
    	templateUrl: '',
    	controller: ''
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
var cssStyle = '.qjbjDiv,.rwContent,.qdCreate,.qdListBox{height:'+ (llqHeight-138) +'px;}#sidebar .panel-body{overflow:auto;border-top:0;border-bottom:0;padding:0;height: '+ (llqHeight-441) +'px;} ';
$('head').append($('<style></style>').html(cssStyle));
$("#Route_conter").css("height",llqHeight-150+"px");
$(".qdCreate").css("height",llqHeight-138+"px");


$('#sidebar a').click(function (e) {
    e.preventDefault();
    var href = e.target.hash.replace('#','#/');
    var a = $('#clickA');
    if(a.attr('href') == href) return;
    if(href == '#/xgpg'|| href == '#/kqzlyb')return;
    a.attr('href',href);
    a[0].click();
});