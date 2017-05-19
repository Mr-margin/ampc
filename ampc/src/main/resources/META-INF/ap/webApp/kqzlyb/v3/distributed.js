/**
 * Created by lvcheng on 2017/4/25.
 */
$(function () {
    /**
     *设置导航条信息
     */
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">水平分布</span>');
});
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
    rows: 40,
    cols: 40
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
require(
    [
        "esri/map", "esri/tasks/Geoprocessor", "esri/layers/ImageParameters", "esri/layers/DynamicLayerInfo", "esri/layers/RasterDataSource", "esri/layers/TableDataSource",
        "esri/layers/LayerDataSource", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/layers/LayerDrawingOptions", "esri/symbols/SimpleFillSymbol",
        "esri/symbols/SimpleLineSymbol", "esri/symbols/SimpleMarkerSymbol", "esri/geometry/Multipoint", "esri/geometry/Point", "esri/geometry/Extent",
        "esri/renderers/SimpleRenderer", "esri/graphic", "esri/lang", "dojo/_base/array", "dojo/number", "dojo/dom-style", "dijit/TooltipDialog",
        "dijit/popup", "dojox/widget/ColorPicker", "esri/layers/RasterLayer", "tdlib/gaodeLayer", "esri/tasks/FeatureSet", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
        "esri/geometry/Polygon", "esri/symbols/PictureFillSymbol", "esri/Color", "esri/layers/ArcGISDynamicMapServiceLayer", "esri/tasks/RasterData", "dojo/domReady!"
    ],
    function (Map, Geoprocessor, ImageParameters, DynamicLayerInfo, RasterDataSource, TableDataSource, LayerDataSource, FeatureLayer, GraphicsLayer, LayerDrawingOptions,
              SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint, Point, Extent, SimpleRenderer, Graphic, esriLang, array, number, domStyle,
              TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference, PictureMarkerSymbol, Polygon, PictureFillSymbol, Color,
              ArcGISDynamicMapServiceLayer, RasterData) {

        dong.gaodeLayer = gaodeLayer;
        dong.DynamicLayerInfo = DynamicLayerInfo;
        dong.RasterDataSource = RasterDataSource;
        dong.LayerDataSource = LayerDataSource;

        dong.Geoprocessor = Geoprocessor;
        dong.Graphic = Graphic;
        dong.Point = Point;
        dong.FeatureSet = FeatureSet;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.PictureMarkerSymbol = PictureMarkerSymbol;//图片点样式
        dong.Polygon = Polygon;//多边形
        dong.PictureFillSymbol = PictureFillSymbol;//
        dong.SimpleLineSymbol = SimpleLineSymbol;//
        dong.Color = Color;//
        dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;//
        dong.RasterData = RasterData;//

        esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
        esri.config.defaults.io.alwaysUseProxy = false;

        app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new Array();//加载卫星图
        app.labellayerList = new Array();//加载标注图

        app.map = new Map("map_in", {
            logo: false,
            center: [stat.cPointx, stat.cPointy],
            minZoom: 4,
            maxZoom: 13,
            zoom: 5
        });

        app.baselayerList = new dong.gaodeLayer();
        app.stlayerList = new dong.gaodeLayer({layertype: "st"});
        app.labellayerList = new dong.gaodeLayer({layertype: "label"});
        app.map.addLayer(app.baselayerList);//添加高德地图到map容器
        app.map.addLayers([app.baselayerList]);//添加高德地图到map容器

        app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
        app.map.addLayer(app.gLyr1);


        app.map.on("load", initialize);//启动后立即执行获取数据

        //地图显示区域改变后触发事件
        app.map.on("extent-change", function (event) {
            updata();
            updataWind();
        });

//			app.map.on("mouse-move", function(e){
//				  console.log(e.mapPoint.x);
//			});

        initialize();

    });


/*初始化函数*/
/**
 * 添加或修改物种选择框，同时关联计算最大最小日期并初始化日期控件
 */
function initialize() {
    $('#species').empty();
    for (var i = 0; i < speciesArr[changeMsg.rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[changeMsg.rms][i] + '</option>'))
    }
    requestDate();

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
            changeMsg.minDate = '2017-04-14';
            changeMsg.maxDate = '2017-04-16';
            changeMsg.startD = '2017-04-14';
            changeMsg.endD = '2017-04-16';
            changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');
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


/*改变事件*/
$('input[name=rms]').on('change', function (e) { //时间分辨率选择

    var rms = $(e.target).val();
    changeMsg.rms = rms;
    $('#species').empty();
    for (var i = 0; i < speciesArr[rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
    }
    $('#sTime-d').css('width', '100%');
    if (rms == 'd') {
        $('#sTime-h').addClass('disNone');
    } else if (rms == 'h') {
        $('#sTime-d').css('width', '65%');
        $('#sTime-h').removeClass('disNone');
    }
    updata();

});

$('input[name=domain]').on('change', function (e) {
    var domain = $(e.target).val();
    changeMsg.domain = domain;
//  console.log(domain);
    updata();
});

$('input[name=speed]').on('change', function (e) {
    var speed = $(e.target).val();
    changeMsg.speed = speed;
//  console.log(speed);
    updata();
});

$('input[name=field]').on('change', function (e) {
    var field = $(e.target).val();
    changeMsg.field = field;
//  console.log(field);
    updataWind();
});

$('#species').on('change', function (e) {
    var species = $(e.target).val();
    changeMsg.species = [];
    changeMsg.species.push(species);
    updata();
});

$('#sTime-d').on('change', function (e) {//选择日期
    var date = $(e.target).val();
    changeMsg.YBDate = moment(date).format('YYYYMMDD');
    console.log(date);
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




/*数据更新*/
function updata(t) {
    // load_gis('');

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
        p1.day = moment(changeMsg.YBDate).format('YYYYMMDD');
    } else if (changeMsg.rms == 'h') {
        p1.day = moment(changeMsg.YBDate).format('YYYYMMDD');
        p1.hour = changeMsg.YBHour;
    }
    p1.GPserver_type = [];
    for (var i = 0; i < changeMsg.species.length; i++) {
        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
    }
    load_gis('0',p1);


    console.log(p1);
    showTitleFun();

}

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
    load_gis('0',p1);

}

/**
 * 加载预报数据
 */
function load_gis(g_num,p) {

    //获取屏幕显示的地图范围
    var par = p;
    par.xmax = app.map.extent.xmax;
    par.xmin = app.map.extent.xmin;
    par.ymax = app.map.extent.ymax;
    par.ymin = app.map.extent.ymin;

    var v1 = new Date().getTime();

    ajaxPost('/extract/data', par).success(function (data) {

        if (!data.data) {
            console.log("data.data-null");

            zmblockUI("#mapDiv" + g_num, "end");
            swal('获取当前范围数据失败', '', 'error');
            return;
        }
        if (data.data.length == 0) {
            console.log("length-null");
            zmblockUI("#mapDiv" + g_num, "end");
            swal('当前范围缺少数据', '', 'error');
            return;
        }
        console.log(g_num + '~~~' + data.data.length);

        //物种类型
        var pftype = par.species[0];
        //地图服务地址
        var GPserver_type = par.GPserver_type[0];
        var GPserver_url = ArcGisServerUrl + "/arcgis/rest/services/ampc_zrly/" + GPserver_type + "/GPServer/" + GPserver_type;

        var features = [];
        $.each(data.data, function (i, col) {
            if (typeof col.x == "undefined") {
                console.log("x-null");
                return;
            }
            if (typeof col.y == "undefined") {
                console.log("y-null");
                return;
            }
            var point = new dong.Point(col.x, col.y, new dong.SpatialReference({wkid: 3857}));
            var attr = {};
            attr["FID"] = i;
            attr["dqvalue"] = col[pftype];
            var graphic = new dong.Graphic(point);
            graphic.setAttributes(attr);
            features.push(graphic);
        });

        var featureset = new dong.FeatureSet();
        featureset.fields = [{
            "name": "dqvalue",
            "type": "esriFieldTypeSingle",
            "alias": "dqvalue"
        }, {
            "name": "FID",
            "type": "esriFieldTypeOID",
            "alias": "FID"
        }];
        featureset.fieldAliases = {"dqvalue": "dqvalue", "FID": "FID"};
        featureset.spatialReference = new dong.SpatialReference({wkid: 3857});
        featureset.features = features;
        featureset.exceededTransferLimit = false;

//    console.log(JSON.stringify(featureset));
        app.gp = new esri.tasks.Geoprocessor(GPserver_url);
        var parms = {
            "imp": featureset,
            "out": "out_raster_layer"
        };
        app.gp.submitJob(parms, function (jobInfo) {
            var gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "out");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
            //需要判断一下是否已经添加过图层，先移除，再添加
            gpResultLayer.id = "out_raster_layer";
            gpResultLayer.setOpacity(opacity);

            var out_raster_layer = app.map.getLayer('out_raster_layer');
            if (out_raster_layer) {
                app.map.removeLayer(out_raster_layer);
            }
            app.map.addLayer(gpResultLayer);

            //添加图例
            $('#colorBar' + g_num).html("<img src='img/cb/" + par.species[0] + ".png' width='75%' height='75px' />");

            //console.log(new Date().getTime() - v1);
        }, function (jobinfo) {
            var jobstatus = '';
            switch (jobinfo.jobStatus) {
                case 'esriJobSubmitted':
                    //jobstatus = type + '正在提交...';
                    break;
                case 'esriJobExecuting':
                    //jobstatus = type + '处理中...';
                    break;
                case 'esriJobSucceeded':
                    //视频播放处理
//                    judgmentObj.push(g_num);
//                    judgment();

                    jobstatus = '--' + g_num + '--处理完成...';
                    console.log((new Date().getTime() - v1) + jobstatus);
                    zmblockUI("#map_in", "end");
                    break;
            }
        }, function (error) {
            console.log(error);
            zmblockUI("#map_in", "end");
            swal('GIS，内部错误', '', 'error');
        });

    }).error(function (res) {
        zmblockUI("#map_in", "end");
        swal('抽数，内部错误', '', 'error');
    });


}

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
//修改easyui 下拉菜单按钮样式
$(".cloudui .distributed_map #dropdownMenu1 .l-btn-left span").removeClass("m-btn-downarrow")
$(".cloudui .distributed_map #dropdownMenu1").hover(function(){
    $(this).removeClass("m-btn-plain-active");
},function(){
    $(this).removeClass("m-btn-plain-active")
})




