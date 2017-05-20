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
var opacity = 0.7;//默认的图层透明度
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
    opt: 0.5,//透明度
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
    cols: 350
}

var speciesArr = {
    d: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄²¯', 'NO₃¯', 'NH₄⁺', 'BC', 'OM', 'PMFINE'],
    h: ['PM₂.₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄²¯', 'NO₃¯', 'NH₄⁺', 'BC', 'OM', 'PMFINE']
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
        'SO₄²¯': 'PM25',
        'NO₃¯': 'PM25',
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
        'SO₄²¯': 'PM25',
        'NO₃¯': 'PM25',
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
    'SO₄²¯': 'SO4',
    'NO₃¯': 'NO3',
    'NH₄⁺': 'NH4',
    'BC': 'BC',
    'OM': 'OM',
    'PMFINE': 'PMFINE',
    'O₃': 'O3'
};

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
$(".cloudui .distributed_map #dropdownMenu1 .l-btn-left span").removeClass("m-btn-downarrow")
$(".cloudui .distributed_map #dropdownMenu1").hover(function(){
  $(this).removeClass("m-btn-plain-active");
},function(){
  $(this).removeClass("m-btn-plain-active")
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
    changeMsg.YBHour = date - 0;
    updata();
});

/*------------------------滑动条js------------------------------------------------------------------------*/
var $document = $(document);
var selector = '[data-rangeslider]';
var $inputRange = $(selector);

// Example functionality to demonstrate a value feedback
// and change the output's value.
function valueOutput(element) {
    var value = element.value;
    var output = element.parentNode.getElementsByTagName('output')[0];

    output.innerHTML = value;
    console.log(value);
    changeMsg.opt = value;
    //改变地图的透明度----------------------------------------------------------------
}

// Initial value output
for (var i = $inputRange.length - 1; i >= 0; i--) {
    valueOutput($inputRange[i]);
}
;

// Update value output
$document.on('input', selector, function (e) {
    valueOutput(e.target);
});

// Initialize the elements
$inputRange.rangeslider({
    polyfill: false
});

// Example functionality to demonstrate programmatic value changes
$document.on('click', '#js-example-change-value button', function (e) {
    var $inputRange = $('input[type="range"]', e.target.parentNode);
    var value = $('input[type="number"]', e.target.parentNode)[0].value;

    $inputRange
        .val(value)
        .change();
});

// Example functionality to demonstrate programmatic attribute changes
$document.on('click', '#js-example-change-attributes button', function (e) {
    var $inputRange = $('input[type="range"]', e.target.parentNode);
    var attributes = {
        min: $('input[name="min"]', e.target.parentNode)[0].value,
        max: $('input[name="max"]', e.target.parentNode)[0].value,
        step: $('input[name="step"]', e.target.parentNode)[0].value
    };

    $inputRange
        .attr(attributes)
        .rangeslider('update', true);
});

// Example functionality to demonstrate destroy functionality
$document.on('click', '#js-example-destroy button[data-behaviour="destroy"]', function (e) {
    $('input[type="range"]', e.target.parentNode).rangeslider('destroy');
}).on('click', '#js-example-destroy button[data-behaviour="initialize"]', function (e) {
    $('input[type="range"]', e.target.parentNode).rangeslider({polyfill: false});
});
/*------------------------滑动条js------------------------------------------------------------------------*/





require(
    [
        "esri/map", "esri/layers/GraphicsLayer", "esri/geometry/Extent", "tdlib/gaodeLayer", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
        "esri/symbols/PictureFillSymbol", "esri/layers/MapImageLayer", "esri/layers/MapImage", "dojo/domReady!"
    ],
    function (Map,   GraphicsLayer, Extent, gaodeLayer, SpatialReference, PictureMarkerSymbol, 
    		PictureFillSymbol, MapImageLayer, MapImage) {

    	dong.GraphicsLayer = GraphicsLayer;//画布层，用于加点，标线等等
    	dong.Extent = Extent;//空间范围
        dong.gaodeLayer = gaodeLayer;//高德地图
        dong.SpatialReference = SpatialReference;//投影坐标
        dong.PictureMarkerSymbol = PictureMarkerSymbol;//图片点样式
        dong.PictureFillSymbol = PictureFillSymbol;//
        dong.MapImageLayer = MapImageLayer;//
        dong.MapImage = MapImage;//

        //arcgis代理，在GP服务时使用
//      esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
//      esri.config.defaults.io.alwaysUseProxy = false;

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

        app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
        app.map.addLayer(app.gLyr1);
        
        app.mapimagelayer = new dong.MapImageLayer({"id":"myil"});
        app.map.addLayer(app.mapimagelayer);
        app.mapimagelayer.setOpacity(opacity);


        app.map.on("load", initialize);//启动后立即执行获取数据

        //地图显示区域改变后触发事件
        app.map.on("extent-change", function (event) {
            updata();//排放数据地图
            updataWind();//风场数据地图
        });

        initialize();//初始化函数
    });


/*初始化函数*/
/**
 * 添加或修改物种选择框，同时关联计算最大最小日期并初始化日期控件
 */
function initialize() {
    $('#species').empty();//清空物种分辨率，根据默认参数，添加物种分辨率的内容
    for (var i = 0; i < speciesArr[changeMsg.rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[changeMsg.rms][i] + '</option>'))
    }
    requestDate();//请求可选日期范围
    
    //当时间获取完成，开始请求数据
    $.when(dps_Date, dps_YBd).done(function () {
        window.setTimeout(function () {
            updata();
        },50);
    });
}

/*请求可选日期范围*/
function requestDate() {
    var url = '/Air/get_time';
    dps_Date = ajaxPost(url, {
        userId: userId
    }).success(function (res) {

        if (res.status == 0) {
          /*这里要初始化时间*/
            changeMsg.minDate = res.data.mintime;
            changeMsg.maxDate = res.data.maxtime;

            if (!(moment(res.data.maxtime).add(-7, 'd').isBefore(moment(res.data.mintime)))) {
                changeMsg.startD = moment(res.data.maxtime).add(-7, 'd').format('YYYY-MM-DD')
            } else {
                changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
            }
            changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
            changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');

          /*测试使用*/
            changeMsg.minDate = '2017-04-28';
//            changeMsg.maxDate = '2017-04-16';
//            changeMsg.startD = '2017-04-14';
//            changeMsg.endD = '2017-04-16';
//            changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');
          /*测试使用 end*/
            initSPDate(changeMsg.minDate, changeMsg.maxDate, changeMsg.startD, changeMsg.endD);
            dps_YBd = setYBdate();
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
            }

            changeMsg.YBDate = $('#sTime-d').val();

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
//	zmblockUI("#map_in", "start");
	
    var parameter = {
        calcType: 'show',
        showType: 'concn',
        userId: userId,
        layer: changeMsg.layer,
        rows: changeMsg.rows,
        cols: changeMsg.cols,
        domainId: changeMsg.domainId,
        domain: changeMsg.domain,
        missionId: changeMsg.missionId,
        scenarioId1: changeMsg.qj1Id,
        species: (function () {
            var arr = [];
            for (var i = 0; i < changeMsg.species.length; i++) {
                arr.push(speciesObj[changeMsg.species[i]])
            }
            return arr;
        })(),
        timePoint: changeMsg.rms,
        borderType: "0"
    };
    var p1 = $.extend({}, parameter);

    if (changeMsg.rms == 'd') {
        p1.day = moment(changeMsg.YBDate).format('YYYY-MM-DD');
    } else if (changeMsg.rms == 'h') {
        p1.day = moment(changeMsg.YBDate).format('YYYY-MM-DD');
        p1.hour = changeMsg.YBHour;
    }
    p1.GPserver_type = [];
    for (var i = 0; i < changeMsg.species.length; i++) {
        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
    }
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
    
    console.log(par);
    ajaxPost('/extract/png', par).success(function (data) {
    	console.log(JSON.stringify(data));
    	
        if(data.status == 0){
			app.mapimagelayer.removeAllImages();//删除全部的图片图层
			
//			var imageURL = "http://192.168.1.148:8082/ampc/img/ceshi/now.png";//定义图片路径，这个图片是动态生成的
			var imageURL = "http://192.168.1.148:8091/Java/"+data.data.imagePath.substring(data.data.imagePath.indexOf("imageFilePath"))+"?t="+Math.random();
//			var imageURL = "http://166.111.42.85:8300/ampc/"+data.data.imagePath.substring(data.data.imagePath.indexOf("imageFilePath"))+"?t="+Math.random();
//			var imageURL = "/imagePath/d-2016-11-17-0-0-PM25-507-1495248228006.png";
//			console.log(imageURL);
			
			var initE = new dong.Extent({ 'xmin': par.xmin, 'ymin': par.ymin, 'xmax': par.xmax, 'ymax': par.ymax, 'spatialReference': { 'wkid': 3857 }});
            var mapImage = new dong.MapImage({
                'extent': initE,
                'href': imageURL
            });
            
            app.mapimagelayer.addImage(mapImage);//将新的图片图层添加到地图
            
            $('#colorBar').html("<img src='img/cb/"+par.species[0]+".png' width='75%' height='75px' />");//添加图例
            zmblockUI("#map_in", "end");//打开锁屏控制
            console.log((new Date().getTime() - v1) + "处理完成");//记录处理时间
	
		}else{
			zmblockUI("#map_in", "end");//打开锁屏控制
		}


    }).error(function (res) {
        zmblockUI("#map_in", "end");
        swal('抽数，内部错误', '', 'error');
    });
}



/**
 * 风场数据更新并加载
 */
function updataWind() {
    var parameter = {
        calcType: 'show',
        showType: 'wind',
        userId: userId,
        layer: changeMsg.layer,
        rows: changeMsg.rows,
        cols: changeMsg.cols,
        domainId: changeMsg.domainId,
        domain: changeMsg.domain,
        missionId: changeMsg.missionId,
        scenarioId1: changeMsg.qj1Id,
        species: ['WSPD', 'WDIR'],
        timePoint: changeMsg.rms,
        borderType: "0"
    };
    var p1 = $.extend({}, parameter);
    if (changeMsg.rms == 'd') {
        p1.day = changeMsg.YBDate;
    } else if (changeMsg.rms == 'h') {
        p1.day = changeMsg.YBDate;
        p1.hour = changeMsg.YBHour;
    }
    p1.GPserver_type = [];
    for (var i = 0; i < changeMsg.species.length; i++) {
        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
    }
//    load_gis(p1);

}







/**
 * 更新标题行显示的查询条件
 */
function showTitleFun() {
    $('#showTitle span').empty();
    var sData=moment($("#SPDate").val(),"YYYYMMDD").format("YYYY-MM-DD");
    var dataT=moment($("#sTime-d").val(),"YYYYMMDD").format("YYYY-MM-DD");
    var dataState=moment($("#sTime-d").val()+$("#sTime-h").val(),"YYYYMMDDH").format("YYYY-MM-DD H");
    $("#showTitle .rangeName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;地域范围：</span>"+($('input[name=domain]:checked').val()=="1"?"中国":($('input[name=domain]:checked').val()==2?"中国东部":"河北省"))).css({"margin-right":"40px"});
    $("#showTitle .rmsName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;时间分辨率：</span>"+($('input[name=rms]:checked').val()=="h"?"逐小时":"逐日")).css({"margin-right":"40px"});
    $("#showTitle .speciesName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;物种分辨率：</span>"+$("#species").val()).css({"margin-right":"40px"});
    $("#showTitle .sDateName").html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;起报日期：</span>"+sData).css({"margin-right":"40px"});

    if($('input[name=rms]:checked').val()=='d'){
        $('#showTitle .dateName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+dataT).css({"margin-right":"40px"});
    }else{
        $('#showTitle .dateName').html("<span  class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+dataState).css({"margin-right":"40px"});
    }
}