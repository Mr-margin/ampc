/**
 * Created by lvcheng on 2017/4/25.
 */

/**
 * 设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">水平分布</span>');


/**
 * 页面默认参数
 */
var dps_Date, dps_YBd;
var changeMsg = {
    species: ['PM₂.₅'],//物种
    rms: 'd',//逐日
    time: '',//选择的时间日期
    YBDate: '',
    YBHour: 0,
    domain: 2,//中国东部，domain层级
    minDate: '',//最大最小时间
    maxDate: '',
    startD: '',//默认开始结束时间
    endD: '',
    speed: '',//动画速度
    field: '',//风场
    opt: 0.7,//默认的图层透明度
    borderType: 1,
    showWind: '-1',
    showType: ['concn'],//"emis"代表排放、"concn"代表浓度、"wind"代表风场
    calcType: 'show',//"show"当前情景，"diff"差值，"ratio"比例
    missionId: '',//任务ID
    domainId: '',//模拟范围ID
    qj1Id: '',//左边情景ID
    GPserver_type: [],//
    layer: 1,
    rows: 350,
    cols: 350,
    pointNums:30,//参数在点击两点之后获取
    xi:'',
    yi:'',
    xa:'',
    ya:''
}

/*不同时间分辨率下，不同的污染物*/
var speciesArr = {
    d: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄²⁻', 'NO₃⁻', 'NH₄⁺', 'BC', 'OM', 'PMFINE'],
    h: ['PM₂.₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄²⁻', 'NO₃⁻', 'NH₄⁺', 'BC', 'OM', 'PMFINE']
};
var mappingSpecies = {
    d: {
        'PM₂.₅': 'PM25',
        'PM₁₀': 'PM10',
        'O₃_8_max': 'o3_8_max',
        'O₃_1_max': 'o3_1_max',
        'O₃_avg': 'o3_avg',
        'SO₂': 'so2_daily',
        'NO₂': 'no2_daily',
        'CO': 'co_daily',
        'SO₄²⁻': 'PM25',
        'NO₃⁻': 'PM25',
        'NH₄⁺': 'PM25',
        'BC': 'PM25',
        'OM': 'PM25',
        'PMFINE': 'PM25'
    },
    h: {
        'PM₂.₅': 'PM25',
        'PM₁₀': 'PM10',
        'O₃': 'o3_hourly',
        'SO₂': 'so2_hourly',
        'NO₂': 'no2_hourly',
        'CO': 'co_hourly',
        'SO₄²⁻': 'PM25',
        'NO₃⁻': 'PM25',
        'NH₄⁺': 'PM25',
        'BC': 'PM25',
        'OM': 'PM25',
        'PMFINE': 'PM25'
    }
};
var speciesObj = {
    'PM₂.₅': 'PM25',
    'PM₁₀': 'PM10',
    'O₃_8_max': 'O3_8_MAX',
    'O₃_1_max': 'O3_1_MAX',
    'O₃_avg': 'O3_AVG',
    'SO₂': 'SO2',
    'NO₂': 'NO2',
    'CO': 'CO',
    'SO₄²⁻': 'SO4',
    'NO₃⁻': 'NO3',
    'NH₄⁺': 'NH4',
    'BC': 'BC',
    'OM': 'OM',
    'PMFINE': 'PMFINE',
    'O₃': 'O3'
};

var videoPlayScale = [];
var playDay = '',playHour = '',play = false;

var stat = {cPointx: 106, cPointy: 35}, app = {}, dong = {};
var dojoConfig = {
    async: true,
    parseOnLoad: true,
    packages: [{
        name: 'tdlib',
        location: "/js/tdlib"
    }],
    paths: {
        extras: location.pathname.replace(/\/[^/]+$/, '') + "/js/extras"
    }
};





/**
 * 样式控制
 */
//easyui 添加
$(".toolAll").hide();
$(".upDownBtn").text("更多搜索条件");
$(".upDownBtn").append("<i class='en-arrow-up7'></i>")
$(".upDownBtn").click(function(){
  if($(".upDownBtn").text()=="收起"){
      $(".upDownBtn").text("更多搜索条件");
      $(".toolAll").hide();
      $(".upDownBtn i").remove();
      $(".upDownBtn").append("<i class='en-arrow-down8'></i>")
      $(".upDownBtn i").attr("class","en-arrow-down8")
      // headerH=$(".cloudui .searchT").height();
      // $(".charContent").css({"top":headerH+"px"})
  }else{
      $(".upDownBtn").text("收起");
      $(".toolAll").show();
      // headerH=$(".cloudui .searchT").height();
      $(".upDownBtn i").remove();
      $(".upDownBtn").append("<i class='en-arrow-up7'></i>");
      // $(".charContent").css({"top":headerH+"px"});
  }
})
$(".cloudui .verticalCon .searchT .upDown").hover(function(){
  $(".cloudui .verticalCon .searchT .upDown").css({"border-top":"1px solid #0275D8"});
  $(".cloudui .verticalCon .searchT .upDown .upDownBtn").css({"border":"1px solid #0275D8"});
},function(){
  $(".cloudui .verticalCon .searchT .upDown").css({"border-top":"1px solid #d9d9d9"});
  $(".cloudui .verticalCon .searchT .upDown .upDownBtn").css({"border":"1px solid #d9d9d9"});
})


//修改easyui 下拉菜单按钮样式
$(".dropdownCon").hover(function () {
    $(".vidioDown").css({"display":"block"})
},function () {
    $(".vidioDown").css({"display":"none"})
})
$(".vidioDown div").hover(function () {
    $(".vidioDown div").css({"background-color":"transparent"})
    $(this).css({"background-color":"#e4e9eb"})
},function () {
    $(".vidioDown div").css({"background-color":"transparent"})
})



/**
 * 页面事件定义
 */
$('input[name=rms]').on('change', function (e) { //时间分辨率选择，逐日还是逐小时
    var rms = $(e.target).val();//获取当前选择的时间分辨率
    changeMsg.rms = rms;
    $('#species').empty();//移除物种分辨率下拉框的所有内容，准备添加新的内容
    for (var i = 0; i < speciesArr[rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
    }
    $('#sTime-d').css('width', '100%');//日期选择框的天选项，宽度设置为100%，默认已经是100%了，不知道单独设置的意思
    if (rms == 'd') {//如果是逐日，隐藏小时下拉框
        $('#sTime-h').addClass('disNone');
    } else if (rms == 'h') {//如果是逐小时，天下拉框设置为65%，同时打开小时选择
        $('#sTime-d').css('width', '65%');
        $('#sTime-h').removeClass('disNone');
    }
    updata();
});

$('input[name=domain]').on('change', function (e) {//地域范围选择   1:中国       2:中国东部       3:河北省
    var domain = $(e.target).val();
    changeMsg.domain = domain;
    updata();
});

$('input[name=speed]').on('change', function (e) {//动画速度    1:快速      2:中速      3:慢速
    var speed = $(e.target).val();
    changeMsg.speed = speed;
    updata();
});

$('input[name=field]').on('change', function (e) {//风场     0：无       1：箭头         2：F
    var field = $(e.target).val();
    changeMsg.field = field;
    updataWind();
});

$('#species').on('change', function (e) {//物种分辨率
    var species = $(e.target).val();
    changeMsg.species = [];
    changeMsg.species.push(species);
    updata();
});

$('#sTime-d').on('change', function (e) {//选择日期
    var date = $(e.target).val();
    changeMsg.YBDate = moment(date).format('YYYY-MM-DD');
    updata();
});

$('#sTime-h').on('change', function (e) {//选择时间
    var date = $(e.target).val();
    // changeMsg.YBHour = date - 0;
    changeMsg.YBHour = date;
    updata();
});




require(
    [
        "esri/map", "esri/graphic", "esri/layers/GraphicsLayer", "esri/geometry/Extent", "tdlib/gaodeLayer", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
        "esri/symbols/PictureFillSymbol", "esri/layers/MapImageLayer", "esri/layers/MapImage", "esri/geometry/Point", "dijit/registry", "esri/toolbars/draw", 
        "esri/symbols/SimpleFillSymbol", "dojo/parser", "dijit/layout/ContentPane", 
        "dojo/domReady!"
    ],
    function (Map, Graphic,  GraphicsLayer, Extent, gaodeLayer, SpatialReference, PictureMarkerSymbol, 
    		PictureFillSymbol, MapImageLayer, MapImage, Point, registry, Draw, SimpleFillSymbol, parser) {

    	dong.GraphicsLayer = GraphicsLayer;//画布层，用于加点，标线等等
    	dong.Extent = Extent;//空间范围
        dong.gaodeLayer = gaodeLayer;//高德地图
        dong.SpatialReference = SpatialReference;//投影坐标
        dong.PictureMarkerSymbol = PictureMarkerSymbol;//图片点样式
        dong.PictureFillSymbol = PictureFillSymbol;//
        dong.MapImageLayer = MapImageLayer;//
        dong.MapImage = MapImage;//
        dong.Point = Point;
        dong.Graphic = Graphic;
        dong.Draw = Draw;
        dong.SimpleFillSymbol = SimpleFillSymbol;

        //arcgis代理，在GP服务时使用
//      esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
//      esri.config.defaults.io.alwaysUseProxy = false;

        parser.parse();
        
        app.map = new Map("map_in", {
            logo: false,
            center: [stat.cPointx, stat.cPointy],
            minZoom: 4,
            maxZoom: 13,
            zoom: 5
        });

        app.baselayerList = new dong.gaodeLayer();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new dong.gaodeLayer({layertype: "st"});//加载卫星图
        app.labellayerList = new dong.gaodeLayer({layertype: "label"});//加载标注图
        app.map.addLayer(app.baselayerList);//添加高德地图到map容器
        app.map.addLayers([app.baselayerList]);//添加高德地图到map容器

        app.gLyr = new dong.GraphicsLayer({"id": "gLyr"});
        app.map.addLayer(app.gLyr);
        
        app.mapimagelayer = new dong.MapImageLayer({"id":"myil"});
        app.map.addLayer(app.mapimagelayer);
        app.mapimagelayer.setOpacity(changeMsg.opt);


//        app.map.on("load", createToolbar);//启动后立即执行获取数据
        
        registry.forEach(function(d) {
        	if ( d.declaredClass === "dijit.form.Button" ) {
        		d.on("click", activateTool);
        	}
        });

        //地图显示区域改变后触发事件
        app.map.on("extent-change", function (event) {
            updata();//排放数据地图
            updataWind();//风场数据地图
        });

        initialize();//初始化函数
        
        function activateTool() {
        	app.toolbar.activate(dong.Draw.LINE);
        	app.map.hideZoomSlider();
        }
        
    });




/*初始化函数*/
/**
 * 添加或修改物种选择框，同时关联计算最大最小日期并初始化日期控件
 */
function initialize() {
	
	//实例化地图控件
	app.toolbar = new dong.Draw(app.map, { showTooltips: true });
	app.toolbar.on("draw-end", addToMap);
	
    $('#species').empty();//清空物种分辨率，根据默认参数，添加物种分辨率的内容
    for (var i = 0; i < speciesArr[changeMsg.rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[changeMsg.rms][i] + '</option>'))
    }
    requestDate();//请求可选日期范围

    $.when(dps_Date).then(function () {
        dps_YBd = setYBdate();
        $.when(dps_YBd).then(function () {
            window.setTimeout(function () {
                updata();
            },50);
        });
    })
    //当时间获取完成，开始请求数据

}

/**
 * 垂直剖面地图标线
 * @param evt
 */
function addToMap(evt) {
    app.map.graphics.clear();
	var symbol = new dong.SimpleFillSymbol();
	app.toolbar.deactivate();
	app.map.showZoomSlider();
	var graphic = new dong.Graphic(evt.geometry, symbol);
	app.map.graphics.add(graphic);

	var xi = graphic.geometry.paths[0][0][0];
	var yi = graphic.geometry.paths[0][0][1];
	var xa = graphic.geometry.paths[0][1][0];
	var ya = graphic.geometry.paths[0][1][1]

    changeMsg.xi = xi;
    changeMsg.yi = yi;
    changeMsg.xa = xa;
    changeMsg.ya = ya;

    getVerticalImg(changeMsg.xa,changeMsg.xi,changeMsg.ya,changeMsg.yi);
//	graphic.geometry.paths[0][0]

}


/*请求可选日期范围*/
function requestDate() {
    var url = '/Air/findpathdate';
    dps_Date = ajaxPost(url, {
        userId: userId
    }).success(function (res) {

        if (res.status == 0) {
          /*这里要初始化时间*/
            changeMsg.minDate = moment(res.data.mintime).format('YYYY-MM-DD');
            changeMsg.maxDate = moment(res.data.maxtime).format('YYYY-MM-DD');

            if(!(moment(res.data.maxtime).isBefore(moment(moment().add(-1,'d').format('YYYY-MM-DD')+' 08'),'h'))){
                changeMsg.startD = moment().add(-1,'d').format('YYYY-MM-DD')
            }else{
                changeMsg.startD = moment(res.data.maxtime).format('YYYY-MM-DD')
            }

            // if (!(moment(res.data.maxtime).add(-7, 'd').isBefore(moment(res.data.mintime)))) {
            //     changeMsg.startD = moment(res.data.maxtime).add(-7, 'd').format('YYYY-MM-DD')
            // } else {
            //     changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
            // }
            changeMsg.endD = changeMsg.startD;
            changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');

          /*测试使用*/
            // changeMsg.minDate = '2017-04-28';
//            changeMsg.maxDate = '2017-04-16';
//            changeMsg.startD = '2017-04-14';
//            changeMsg.endD = '2017-04-16';
//            changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');
          /*测试使用 end*/
            initSPDate(changeMsg.minDate, changeMsg.maxDate, changeMsg.startD, changeMsg.endD);

        }

    })
}

/**
 * 初始化时间范围--时间控件
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initSPDate(s, e, start, end) {
    $('#SPDate').daterangepicker({
        singleDatePicker: true,  //显示单个日历
        timePicker: false,  //允许选择时间
        timePicker24Hour: true, //时间24小时制
        minDate: s,//最早可选日期
        maxDate: e,//最大可选日期
        locale: {
            format: "YYYY-MM-DD",
            separator: " 至 ",
            applyLabel: "确定", //按钮文字
            cancelLabel: "取消",//按钮文字
            weekLabel: "W",
            daysOfWeek: [
                "日", "一", "二", "三", "四", "五", "六"
            ],
            monthNames: [
                "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
            ],
            firstDay: 1
        },
        "startDate": start,
        "endDate": end,
        "opens": "right"
    }, function (start, end, label) {
        changeMsg.time = start.format('YYYY-MM-DD HH');
      /*时间请求*/
        var YBd = setYBdate();
        $.when(YBd).done(function (res) {
            updata();
        })
    })
    var d = $('#SPDate').data('daterangepicker');
    d.element.off();
}
/*
 * 设置时间下拉框内容
 */
function setYBdate() {
    videoPlayScale = [];
    return ajaxPost('/Air/times', {
        userId: userId,
        date: changeMsg.time
    }).success(function (res) {
        if (res.status == 0) {
            $('#sTime-d').empty();
            changeMsg.domainId = res.data.domainId;
            changeMsg.missionId = res.data.missionId;
            changeMsg.qj1Id = res.data.scenarioId;

            for (var i = 0; i < res.data.timearr.length; i++) {
                $('#sTime-d').append($('<option>' + moment(res.data.timearr[i]).format('YYYY-MM-DD') + '</option>'))
                videoPlayScale.push(moment(res.data.timearr[i]).format('YYYY-MM-DD'));
            }
            changeMsg.YBDate = $('#sTime-d').val();
            initVideoPlay();

        } else {
            swal({
                title: res.msg,
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        }
    }).error(function () {
        swal({
            title: '接口故障！',
            type: 'error',
            timer: 1000,
            showConfirmButton: false
        });
    })
}

/*按钮打开日期*/
function showDate(type) {
    var d = $('#SPDate').data('daterangepicker');
    if (!d) {
        swal({
            title: '无可选日期!',
            type: 'error',
            timer: 1000,
            showConfirmButton: false
        });
        return
    }
    d.toggle();
}




/**
 * 排放数据更新并加载
 */
function updata() {
    // console.log(changeMsg.YBHour,"updata")
	zmblockUI1("#map_in", "start");
	if(oldMsg.YBDate != changeMsg.YBDate || oldMsg.YBHour != changeMsg.YBHour || oldMsg.domain != changeMsg.domain || oldMsg.rms != changeMsg.rms || oldMsg.species[0] != changeMsg.species[0] || oldMsg.xa != changeMsg.xa || oldMsg.ya != changeMsg.ya){
        if($('.showImg').css('display') == 'block'){
            getVerticalImg(changeMsg.xa,changeMsg.xi,changeMsg.ya,changeMsg.yi);
        }
    }
    var parameter = {
        calcType: 'show',//请求类型 show：当前情景，还有相对变化绝对变化，此处只需要show
        showType: 'concn',//请求类型 concn：浓度，还有风场排放，此处只需要concn
        userId: userId,
        layer: changeMsg.layer,//高度层参数
        rows: changeMsg.rows,//绘制图片的横向格数
        cols: changeMsg.cols,//绘制图片纵向格数
        domainId: changeMsg.domainId,//
        domain: changeMsg.domain,//地图范围1：最大范围，2：中等范围，3：最小范围
        missionId: changeMsg.missionId,//任务ID
        scenarioId1: changeMsg.qj1Id,//情景ID
        species: (function () {//循环查看选择的污染物
            var arr = [];
            for (var i = 0; i < changeMsg.species.length; i++) {
                arr.push(speciesObj[changeMsg.species[i]])
            }
            return arr;
        })(),
        timePoint: changeMsg.rms,//时间分辨率d：逐日，h：逐小时
        borderType: "0"
    };
    var p1 = $.extend({}, parameter);

    if (changeMsg.rms == 'd') {
        p1.day = moment(changeMsg.YBDate).format('YYYY-MM-DD');//格式化请求参数时间
    } else if (changeMsg.rms == 'h') {
        p1.day = moment(changeMsg.YBDate).format('YYYY-MM-DD');
        p1.hour = changeMsg.YBHour;
    }
//    p1.GPserver_type = [];
//    for (var i = 0; i < changeMsg.species.length; i++) {
//        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);//地图图片所需污染物对照
//    }
    load_gis(p1);
    
    showTitleFun();//更新标题行显示的查询条件
}

/**
 * 加载预报数据，只有update方法可以触发
 */
function load_gis(p) {

    //获取屏幕显示的地图范围
    var par = p;
    par.xmax = app.map.extent.xmax;
    par.xmin = app.map.extent.xmin;
    par.ymax = app.map.extent.ymax;
    par.ymin = app.map.extent.ymin;

    par.width = $("#map_in").css("width").replace("px","")-2;
	par.height = $("#map_in").css("height").replace("px","")-2;
	par.borderType = 1;
	
    var v1 = new Date().getTime();
    
    ajaxPost('/extract/png', par).success(function (data) {
     // console.log(JSON.stringify(data));

     if(data.status == 0){
     //			app.mapimagelayer.removeAllImages();//删除全部的图片图层
     //
     //			console.log(data.data.imagePath);

     var imageURL = pngUrl + "/ampc/"+data.data.imagePath+"?t="+Math.random();
     // console.log(imageURL);

     var initE = new dong.Extent({ 'xmin': par.xmin, 'ymin': par.ymin, 'xmax': par.xmax, 'ymax': par.ymax, 'spatialReference': { 'wkid': 3857 }});
     var mapImage = new dong.MapImage({
     'extent': initE,
     'href': imageURL
     });

     app.mapimagelayer.addImage(mapImage);//将新的图片图层添加到地图

     $('#colorBar').html("<img src='img/cb/"+par.species[0]+".png' width='75%' height='75px' />");//添加图例
     zmblockUI1("#map_in", "end");//打开锁屏控制
     // console.log((new Date().getTime() - v1) + "处理完成");//记录处理时间
     judgmentObj.push('true')
     judgment()

     }else{
     zmblockUI1("#map_in", "end");//打开锁屏控制
     }


     }).error(function (res) {
     zmblockUI1("#map_in", "end");
     swal('抽数，内部错误', '', 'error');
     });

//     ajaxPost_w('http://166.111.42.85:8300/ampc/extract/png', {token:'',data:par}).success(function (data) {
//         // console.log(JSON.stringify(data));
//
//         if(data.status == 0){
// //			app.mapimagelayer.removeAllImages();//删除全部的图片图层
// //
// //			console.log(data.data.imagePath);
//
//             var imageURL = pngUrl + "/ampc/"+data.data.imagePath+"?t="+Math.random();
//             // console.log(imageURL);
//
//             var initE = new dong.Extent({ 'xmin': par.xmin, 'ymin': par.ymin, 'xmax': par.xmax, 'ymax': par.ymax, 'spatialReference': { 'wkid': 3857 }});
//             var mapImage = new dong.MapImage({
//                 'extent': initE,
//                 'href': imageURL
//             });
//
//             app.mapimagelayer.addImage(mapImage);//将新的图片图层添加到地图
//
//             $('#colorBar').html("<img src='img/cb/"+par.species[0]+".png' width='75%' height='75px' />");//添加图例
//             zmblockUI1("#map_in", "end");//打开锁屏控制
//             // console.log((new Date().getTime() - v1) + "处理完成");//记录处理时间
//             judgmentObj.push('true')
//             judgment()
//
//         }else{
//             zmblockUI1("#map_in", "end");//打开锁屏控制
//         }
//
//
//     }).error(function (res) {
//         zmblockUI1("#map_in", "end");
//         swal('抽数，内部错误', '', 'error');
//     });
}



/**
 * 风场数据更新并加载
 */
function updataWind() {
    var parameter = {
        calcType: 'show',//请求类型 show：当前情景，还有相对变化绝对变化，此处只需要show
        showType: 'wind', //请求类型 wind：风场，还有浓度排放，此处只需要wind
        userId: userId,
        layer: changeMsg.layer,//高度层参数
        rows: changeMsg.rows,//绘制图片的横向格数
        cols: changeMsg.cols,//绘制图片纵向格数
        domainId: changeMsg.domainId,//
        domain: changeMsg.domain,//地图范围1：最大范围，2：中等范围，3：最小范围
        missionId: changeMsg.missionId,//任务ID
        scenarioId1: changeMsg.qj1Id,//情景ID
        species: ['WSPD', 'WDIR'],//请求风场时，所需必要参数
        field:changeMsg.field,//风场类型
        timePoint: changeMsg.rms,//时间分辨率d：逐日，h：逐小时
        borderType: "0"
    };
    var p1 = $.extend({}, parameter);

    /*请求在逐时和逐日不同情况下，所附带参数*/
    if (changeMsg.rms == 'd') {
        p1.day = changeMsg.YBDate;
    } else if (changeMsg.rms == 'h') {
        p1.day = changeMsg.YBDate;
        p1.hour = changeMsg.YBHour;
    }
//    p1.GPserver_type = [];
//    for (var i = 0; i < changeMsg.species.length; i++) {
//        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
//    }
    app.gLyr.clear();
    if(changeMsg.field > 0){
    	fengchang(p1);
    }
}


/**
 * 加载风场
 */
function fengchang(p){
	
	//获取屏幕显示的地图范围
    var par = p;
    par.xmax = app.map.extent.xmax;
    par.xmin = app.map.extent.xmin;
    par.ymax = app.map.extent.ymax;
    par.ymin = app.map.extent.ymin;
    
	//风场
    var lujing = "";
    var fxOpacity = 0;
    if(par.field == 1){
        lujing = "fxj";
        par.windSymbol = 0;
        fxOpacity = 0.5;
    }else if(par.field == 2){
        lujing = "fx";
        par.windSymbol = 1;//1代表F风，F风最大到20级，箭头风最大到12级
        fxOpacity = 1;
    }
    par.rows = 20;
    par.cols = 20;

    par.species = ['WSPD','WDIR'];
    
    ajaxPost('/extract/data', par).success(function (data) {
        if (!data.data) {
            console.log("data.data-null");
            swal('获取当前范围风场数据失败', '', 'error');
            return;
        }
        if (data.data.length == 0) {
            console.log("length-null");
            swal('当前范围缺少风场数据', '', 'error');
            return;
        }
        
        $.each(data.data, function (i, col) {

            if (typeof col.x == "undefined") {
                console.log("x-null");
                return;
            }
            if (typeof col.y == "undefined") {
                console.log("y-null");
                return;
            }

            var p_url = "img/"+lujing+"/"+col.WSPD+".png";
            var angle = 0;
            switch (col.WDIR) {
                case "N" :
                    angle = 0;
                    break;
                case "NNE" :
                    angle = 22.5;
                    break;
                case "NE" :
                    angle = 45;
                    break;
                case "ENE" :
                    angle = 67.5;
                    break;
                case "E" :
                    angle = 90;
                    break;
                case "ESE" :
                    angle = 112.5;
                    break;
                case "SE" :
                    angle = 135;
                    break;
                case "SSE" :
                    angle = 157.5;
                    break;
                case "S" :
                    angle = 180;
                    break;
                case "SSW" :
                    angle = 202.5;
                    break;
                case "SW" :
                    angle = 225;
                    break;
                case "WSW" :
                    angle = 247.5;
                    break;
                case "W" :
                    angle = 270;
                    break;
                case "WNW" :
                    angle = 292.5;
                    break;
                case "NW" :
                    angle = 315;
                    break;
                case "NNW" :
                    angle = 337.5;
                    break;
                default :
                    angle = 0;
            }

            var symbol = new dong.PictureMarkerSymbol(p_url,20,20);
            symbol.setOffset(-10,18);
            symbol.setAngle(angle);
            var point = new dong.Point(col.x, col.y, new dong.SpatialReference({wkid: 3857}));
            var graphic = new dong.Graphic(point, symbol);
            
            app.gLyr.add(graphic);
            app.gLyr.setOpacity(fxOpacity);
        });
        // console.log((new Date().getTime() - v1) + "num:" +g_num);
    }).error(function (res) {
        swal('风场抽数，内部错误', '', 'error');
    });

}






/*------------------------滑动条js------------------------------------------------------------------------*/
var $document = $(document);
var selector = '[data-rangeslider]';
var $inputRange = $(selector);

// Example functionality to demonstrate a value feedback
// and change the output's value.
function valueOutput(element,t) {
    var value = element.value;
    var output = element.parentNode.getElementsByTagName('output')[0];

    output.innerHTML = value;
//    console.log(value);
    changeMsg.opt = parseFloat(value);
    //改变地图的透明度----------------------------------------------------------------
    if(t){
    	app.mapimagelayer.setOpacity(changeMsg.opt);
    }
}

// Initial value output
for (var i = $inputRange.length - 1; i >= 0; i--) {
    valueOutput($inputRange[i]);
};

// Update value output
$document.on('input', selector, function (e) {
    valueOutput(e.target,true);
});

// Initialize the elements
$inputRange.rangeslider({
    polyfill: false
});

// Example functionality to demonstrate programmatic value changes
$document.on('click', '#js-example-change-value button', function (e) {
    var $inputRange = $('input[type="range"]', e.target.parentNode);
    var value = $('input[type="number"]', e.target.parentNode)[0].value;
    $inputRange.val(value).change();
});

// Example functionality to demonstrate programmatic attribute changes
$document.on('click', '#js-example-change-attributes button', function (e) {
    var $inputRange = $('input[type="range"]', e.target.parentNode);
    var attributes = {
        min: $('input[name="min"]', e.target.parentNode)[0].value,
        max: $('input[name="max"]', e.target.parentNode)[0].value,
        step: $('input[name="step"]', e.target.parentNode)[0].value
    };
    $inputRange.attr(attributes).rangeslider('update', true);
});

// Example functionality to demonstrate destroy functionality
$document.on('click', '#js-example-destroy button[data-behaviour="destroy"]', function (e) {
    $('input[type="range"]', e.target.parentNode).rangeslider('destroy');
}).on('click', '#js-example-destroy button[data-behaviour="initialize"]', function (e) {
    $('input[type="range"]', e.target.parentNode).rangeslider({polyfill: false});
});
/*------------------------滑动条js------------------------------------------------------------------------*/



/**
 * 更新标题行显示的查询条件
 */
function showTitleFun() {
    $('#showTitle span').empty();
    $('#showTitle span').css({"margin-right":"0px"});
    $("#sTime-d").val(changeMsg.YBDate)
    // $("#sTime-h").val(changeMsg.YBHour)

    if(changeMsg.YBHour<10){
        $("#sTime-h").val("0"+changeMsg.YBHour)
    }else{
        $("#sTime-h").val(changeMsg.YBHour)
    }
    var sData=moment($("#SPDate").val(),"YYYYMMDD").format("YYYY-MM-DD");
    var dataT=moment($("#sTime-d").val(),"YYYYMMDD").format("YYYY-MM-DD");
    var dataState=moment($("#sTime-d").val()+$("#sTime-h").val(),"YYYYMMDDH").format("YYYY-MM-DD H");
    $("#showTitle .rangeName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;地域范围：</span>"+($('input[name=domain]:checked').val()=="1"?"中国":($('input[name=domain]:checked').val()==2?"中国东部":"河北省"))).css({"margin-right":"40px"});
    $("#showTitle .rmsName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;时间分辨率：</span>"+($('input[name=rms]:checked').val()=="h"?"逐小时":"逐日")).css({"margin-right":"40px"});
    $("#showTitle .speciesName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;物种分辨率：</span>"+$("#species").val()).css({"margin-right":"40px"});
    $("#showTitle .sDateName").html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;起报日期：</span>"+sData).css({"margin-right":"40px"});
    if($('input[name=rms]:checked').val()=='d'){
        $('#showTitle .dateName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+changeMsg.YBDate).css({"margin-right":"40px"});
    }else{
        $('#showTitle .dateName').html("<span  class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+changeMsg.YBDate+" "+changeMsg.YBHour).css({"margin-right":"40px"});
    }
}

var oldMsg = {};//用于确定变量是否被改变
/**
 * 获取垂直剖面图片地址
 */
function getVerticalImg(xa,xi,ya,yi) {
    var url = '/extract/vertical';
    /*请求参数*/
    var par = {

        /*测试*/
        // "domainId": 1,"missionId": 353,"domain": 3,"scenarioId1": 806,day: 20170523,
        /*测试 end*/

        calcType:changeMsg.calcType,//计算类型，当前只有show
        showType:'concn',//表现类型，当前只有浓度concn
        userId:userId,//用户id
        domainId:changeMsg.domainId,//domain范围id
        domain:changeMsg.domain,//domain范围
        missionId:changeMsg.missionId,//任务id
        scenarioId1:changeMsg.qj1Id,//情景id
        timePoint:changeMsg.rms,//时间分辨率d/h
        species:(function () { //污染物数组，暂且只有一个
            var arr = [];
            for(var i=0;i<changeMsg.species.length;i++){
                arr.push(speciesObj[changeMsg.species[i]])
            }
            return arr;
        })(),
        pointNums:changeMsg.pointNums,//参数在点击两点之后获取
        xmax:xa,
        xmin:xi,
        ymax:ya,
        ymin:yi,
    }

    if (changeMsg.rms == 'd') {
        par.day = moment(changeMsg.YBDate).format('YYYYMMDD');//格式化请求参数时间
    } else if (changeMsg.rms == 'h') {
        par.day = moment(changeMsg.YBDate).format('YYYYMMDD');
        par.hour = changeMsg.YBHour;
    }
     // $('.showImg').css('display','block');

    if($('.showImg').css('display') == 'none'){
        $('.showImg').css('display','block');
        $('.showImg').css('width','30%');
        $('.showImg img').attr('src','img/Loading.gif');
    }else{
        $('.showImg img').attr('src','img/Loading.gif');
    }

    // ajaxPost_w('http://166.111.42.85:8300/ampc'+url,{token:'',data:par}).success(function (res) {
    //     oldMsg = $.extend(true,{},changeMsg);
    //     $('.showImg img').attr('src','http://166.111.42.85:8300/ampc/verticalPath/'+res.data);
    //     console.log(res);
    // })

   ajaxPost(url,par).success(function (res) {
       // $('.showImg').css('display','block');
       // $('.showImg').css('width','30%');
       $('.showImg img').attr('src','verticalPath/'+res.data);
   })

}


function openImg() {
    $('.showImg').css('width','60%');
}

function smallImg() {
    $('.showImg').css('width','30%');
}

function closeImg() {
    app.map.graphics.clear();
    $('.showImg').css('display','none');
}
//视频播放相关

$('.videoS_in').on('click',function (e) {
    if($(e.target).hasClass('startV')){
        play = true;
        $(e.target).removeClass('startV').addClass('stopV');
        videoPlay();
    }else if($(e.target).hasClass('stopV')){
        play = false;
        $(e.target).removeClass('stopV').addClass('startV');
    }else if($(e.target).hasClass('xhV')){
        play = true;
        initVideoPlay();
        videoPlay();
        $(e.target).removeClass('xhV').removeClass('startV').addClass('stopV');
    }

});

function initVideoPlay() {
    playDay = videoPlayScale[0];
    playHour = 0;
    $('.videoK').css('left','calc(0% - 3px)');
    $('.videoShow').css('width','0');
    $('.videoS_in').removeClass('stopV').removeClass('xhV').addClass('startV')
}

/*视频播放处理*/
function videoPlay() {
    judgmentObj = [];
    var index = videoPlayScale.indexOf(playDay);
    if(index == -1){
        return;
    }
    changeMsg.YBDate = moment(playDay).format("YYYY-MM-DD");
    if(changeMsg.rms == 'h'){
        changeMsg.YBHour = playHour;
    }
    if(play){

        var num = '';

        if(changeMsg.rms == 'd'){
            num = index/(videoPlayScale.length-1)*100;
        }else{
            num = (index/(videoPlayScale.length)*100) + (playHour/23/(videoPlayScale.length)*100);
        }

        $('.videoK').css('left','calc('+ num +'% - 3px)');
        $('.videoShow').css('width',num +'%');
        console.log(playDay,playHour);
        setVideoPlayTime();
        updata();
        if(changeMsg.showWind !=-1){
            updataWind();
        }

    }
}

/*时间加一天加一小时处理*/
function setVideoPlayTime() {
    var index = videoPlayScale.indexOf(playDay);
    if(changeMsg.rms == 'd'){
        playDay = videoPlayScale[index+1];
        if(!playDay){
            play = false;
            playDay = videoPlayScale[0];
            $('.videoS_in').removeClass('stopV').removeClass('startV').addClass('xhV')
        }
    }else{
        playHour++;
        if(playHour == 24){
            playHour = 0;
            playDay = videoPlayScale[index+1];
            if(!playDay){
                play = false;
                playDay = videoPlayScale[0];
                $('.videoS_in').removeClass('stopV').removeClass('startV').addClass('xhV')
            }
        }
    }
}

var judgmentObj = [];
/*判断是否可以调用视频播放函数*/
function judgment() {
    console.log(judgmentObj)
    if(changeMsg.showWind == -1){
        if(judgmentObj.length == 1){
            window.setTimeout(function () {
                videoPlay();
            },2000)
        }
    }else{
        if(judgmentObj.length == 1){
            window.setTimeout(function () {
                videoPlay();
            },2000)
        }
    }
}