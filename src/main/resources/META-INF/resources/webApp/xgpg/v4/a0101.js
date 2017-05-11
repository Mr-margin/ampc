var opacity = 0.8;//默认的图层透明度
var ls, sceneInitialization, qjMsg;
var videoPlayScale = [];
var changeMsg = {
    borderType:1,
    showWind: '-1',
    showType: ['concn'],//"emis"代表排放、"concn"代表浓度、"wind"代表风场
    calcType: 'show',//"show"当前情景，"diff"差值，"ratio"比例
    species: ['PM₂.₅'],//物种
    missionId: '',//任务ID
    domain: 1,//模拟范围
    domainId: '',//模拟范围ID
    rms: 'd',//时间分辨率
    qj1Id: '',//左边情景ID
    qj2Id: '',//右边情景ID
    sTimeD: '',//开始日期
    sTimeH: 0,//开始时间
    eTime: '',//结束日期
    GPserver_type: [],//
    dates: [],
    layer: 1,
    rows: 40,
    cols: 40
};
var playDay = '',playHour = '',play = false;
var speciesArr = {
    d: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄²¯', 'NO₃¯', 'NH₄⁺', 'BC', 'OM', 'PMFINE'],
    a: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄²¯', 'NO₃¯', 'NH₄⁺', 'BC', 'OM', 'PMFINE'],
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
    a: {
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
        'SO2': 'so2_hourly',
        'NO2': 'no2_hourly',
        'CO': 'co_hourly',
        'SO₄²¯': 'PM25',
        'NO₃¯': 'PM25',
        'NH₄⁺': 'PM25',
        'BC': 'PM25',
        'OM': 'PM25',
        'PMFINE': 'PM25'
    }
};

var mappingSpeciesBig = {
    'PM₂.₅': 'PM25',
    'PM₁₀': 'PM10',
    'O₃_8_max': 'O3_8_MAX',
    'O₃_1_max': 'O3_1_MAX',
    'O₃_avg': 'O3_AVG',
    'O₃': 'O3',
    'SO₂': 'SO2',
    'NO₂': 'NO2',
    'CO': 'CO',
    'SO₄²¯': 'SO4',
    'NO₃¯': 'NO3',
    'NH₄⁺': 'NH4',
    'BC': 'BC',
    'OM': 'OM',
    'PMFINE': 'PMFINE'
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
        "esri/renderers/SimpleRenderer", "esri/graphic", "esri/lang", "dojo/_base/Color", "dojo/_base/array", "dojo/number", "dojo/dom-style", "dijit/TooltipDialog",
        "dijit/popup", "dojox/widget/ColorPicker", "esri/layers/RasterLayer", "tdlib/gaodeLayer", "esri/tasks/FeatureSet", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
        "dojo/domReady!"
    ],
    function (Map, Geoprocessor, ImageParameters, DynamicLayerInfo, RasterDataSource, TableDataSource, LayerDataSource, FeatureLayer, GraphicsLayer, LayerDrawingOptions,
              SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint, Point, Extent, SimpleRenderer, Graphic, esriLang, Color, array, number, domStyle,
              TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference, PictureMarkerSymbol) {

        dong.gaodeLayer = gaodeLayer;
        dong.Geoprocessor = Geoprocessor;
        dong.Graphic = Graphic;
        dong.Point = Point;
        dong.FeatureSet = FeatureSet;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.PictureMarkerSymbol = PictureMarkerSymbol;

        esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
        esri.config.defaults.io.alwaysUseProxy = false;

        app.mapList = new Array();
        app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new Array();//加载卫星图
        app.labellayerList = new Array();//加载标注图

        for (var i = 0; i < 2; i++) {
            var map = new Map("mapDiv" + i, {
                logo: false,
                center: [stat.cPointx, stat.cPointy],
                minZoom: 4,
                maxZoom: 13,
                zoom: 4
            });

            app.mapList.push(map);
            app.baselayerList[i] = new dong.gaodeLayer();
            app.stlayerList[i] = new dong.gaodeLayer({layertype: "st"});
            app.labellayerList[i] = new dong.gaodeLayer({layertype: "label"});
            app.mapList[i].addLayer(app.baselayerList[i]);//添加高德地图到map容器
            app.mapList[i].addLayers([app.baselayerList[i]]);//添加高德地图到map容器
        }

        app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
        app.mapList[0].addLayer(app.gLyr1);
        
        app.gLyr2 = new dong.GraphicsLayer({"id": "gLyr2"});
        app.mapList[1].addLayer(app.gLyr2);

        app.shengx = 0;

        //多个地图互相联动效果
        var source = -1;
        app.mapList[0].on("extent-change", function (event) {
            if (source == -1) {
                source = 0;
                app.mapList[1].setExtent(event.extent);
                app.shengx = 0;//当前生效的地图位置
                //0
                /*这里清除风场图层*/
                app.gLyr1.clear();
                app.gLyr2.clear();
                /*这里清除风场图层 end*/
                updata();

            } else {
                source = -1;
            }
        });

        app.mapList[1].on("extent-change", function (event) {
            if (source == -1) {
                source = 0;
                app.mapList[0].setExtent(event.extent);
                app.shengx = 1;//当前生效的地图位置
                //1
                /*这里清除风场图层*/
                app.gLyr1.clear();
                app.gLyr2.clear();
                /*这里清除风场图层 end*/
                updata();

            } else {
                source = -1;
            }
        });

//		app.mapList[1].extent.xmax
//		app.mapList[1].extent.xmin
//		app.mapList[1].extent.ymax
//		app.mapList[1].extent.ymin
//		app.mapList[0].on("mouse-move", showCoordinates);
//		app.mapList[0].on("mouse-drag", showCoordinates);
//		event.extent.xmin;
//		event.extent.ymin;//左下
//		event.extent.xmax;
//		event.extent.ymax;//右上
//      var mp = esri.geometry.webMercatorToGeographic(evt.mapPoint);
//      dojo.byId("Point").innerHTML = event.mapPoint.x.toFixed(3) + ", " + event.mapPoint.y.toFixed(3);

        //bianji('1',0);
        //bianji('1',1);
        ls = window.sessionStorage;
        qjMsg = vipspa.getMessage('yaMessage').content;
        if (!qjMsg) {
            qjMsg = JSON.parse(ls.getItem('yaMsg'));
        } else {
            ls.setItem('yaMsg', JSON.stringify(qjMsg));
        }

        sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
        if (!sceneInitialization) {
            sceneInitialization = JSON.parse(ls.getItem('SI'));
        } else {
            ls.setItem('SI', JSON.stringify(sceneInitialization));
        }
//console.log(JSON.stringify(sceneInitialization));

        if (!sceneInitialization) {
            sceneInittion();
        } else {
            setQjSelectBtn(sceneInitialization.data);
        }
        /*这段代码需要在初始化中*/
        $('#species').empty();
        for (var i = 0; i < speciesArr.d.length; i++) {
            $('#species').append($('<option>' + speciesArr.d[i] + '</option>'))
        }
    });

/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">水平分布</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');

var allMission = {};
/**
 * 初始化模态框显示
 */
function sceneInittion() {
    $("#task").html("");
    var paramsName = {};
    paramsName.userId = userId;
    console.log(JSON.stringify(paramsName));
    ajaxPost('/mission/find_All_mission', paramsName).success(function (res) {
        console.log(JSON.stringify(res));
        if (res.status == 0) {
            if (res.data || res.data.length > 0) {
                var task = "";
                $.each(res.data, function (k, vol) {
                    allMission[vol.missionId] = vol;
                    if (sceneInitialization) {
                        if (sceneInitialization.taskID == vol.missionId) {
                            task += '<option value="' + vol.missionId + '" selected="selected">' + vol.missionName + '</option>';
                        } else {
                            task += '<option value="' + vol.missionId + '">' + vol.missionName + '</option>';
                        }
                    } else {
                        task += '<option value="' + vol.missionId + '">' + vol.missionName + '</option>';
                    }
                });
                $("#task").html(task);
                $("#Initialization").modal();//初始化模态框显示
                sceneTable();
            } else {
                swal('无可用任务', '', 'error')
            }
        } else {
            swal('接口故障', '', 'error')
        }
    }).error(function () {
        swal('服务未连接', '', 'error')
    });
}

/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable() {
    $("#sceneTableId").bootstrapTable('destroy');//销毁现有表格数据

    $("#sceneTableId").bootstrapTable({
        method: 'POST',
        url: '/ampc/scenarino/find_All_scenarino',
        dataType: "json",
        iconSize: "outline",
        clickToSelect: true,// 点击选中行
        pagination: false, // 在表格底部显示分页工具栏
        striped: true, // 使表格带有条纹
        queryParams: function (params) {
            var data = {};
            data.userId = userId;
            data.missionId = $("#task").val();
            return JSON.stringify({"token": "", "data": data});
        },
        queryParamsType: "limit", // 参数格式,发送标准的RESTFul类型的参数请求
        silent: true, // 刷新事件必须设置
        contentType: "application/json", // 请求远程数据的内容类型。
        responseHandler: function (res) {
            if (res.status == 0) {
                if (!res.data.rows) {
                    res.data.rows = [];
                } else if (res.data.rows.length > 0) {
                    if (sceneInitialization) {
                        if (sceneInitialization.data.length > 0) {

                            $.each(res.data.rows, function (i, col) {
                                $.each(sceneInitialization.data, function (k, vol) {
                                    if (col.scenarinoId == vol.scenarinoId) {
                                        res.data.rows[i].state = true;
                                    }
                                });
                            });
                        }
                    }
                }
                return res.data.rows;
            } else if (res.status == 1000) {
                swal(res.msg, '', 'error');
            }
        },
        onClickRow: function (row, $element) {
            $('.success').removeClass('success');
            $($element).addClass('success');
        },
        icons: {
            refresh: "glyphicon-repeat",
            toggle: "glyphicon-list-alt",
            columns: "glyphicon-list"
        },
        onLoadSuccess: function (data) {
//			console.log(data);
        },
        onLoadError: function () {
            swal('连接错误', '', 'error');
        }
    });
}

/**
 * 保存选择的情景
 */
function save_scene() {
    var row = $('#sceneTableId').bootstrapTable('getSelections');//获取所有选中的情景数据
    if (row.length > 0) {
        var mag = {};
        mag.id = "sceneInitialization";
        mag.taskID = $("#task").val();
        mag.domainId = allMission[mag.taskID].domainId;
        mag.s = allMission[mag.taskID].missionStartDate;
        mag.e = allMission[mag.taskID].missionEndDate;
        mag.jzID = allMission[mag.taskID].jzqjid;
        var data = [];
        $.each(row, function (i, col) {
            data.push({
                "scenarinoId": col.scenarinoId,
                "scenarinoName": col.scenarinoName,
                "scenarinoStartDate": col.scenarinoStartDate,
                "scenarinoEndDate": col.scenarinoEndDate
            });
        });
        mag.data = data;
        vipspa.setMessage(mag);
        ls.setItem('SI', JSON.stringify(mag));
        console.log(data);
        sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
        setQjSelectBtn(data);
        $("#close_scene").click();

    }
}
//超链接显示 模态框
function exchangeModal() {
    sceneInittion();
    $("#Initialization").modal();
}



function bianji(type, g_num, p , wind) {

    for(var sp=0;sp<changeMsg.species.length;sp++){


        if(!wind){
            wind = -1
        }
        var par = p;
        var v1 = new Date().getTime();
        par.xmax = app.mapList[app.shengx].extent.xmax;
        par.xmin = app.mapList[app.shengx].extent.xmin;
        par.ymax = app.mapList[app.shengx].extent.ymax;
        par.ymin = app.mapList[app.shengx].extent.ymin;

        if(wind == -1){//无风场

            var GPserver_type = par.GPserver_type[sp];
            var GPserver_url = ArcGisServerUrl + "/arcgis/rest/services/ampc_zrly/" + GPserver_type + "/GPServer/" + GPserver_type;
//	    $.get('data6.json', function (data) {
//	    console.log(par);

            var pftype = par.species[sp];

            ajaxPost('/extract/data', par).success(function (data) {

                if (!data.data) {
                    console.log("data.data-null");
                    
                    zmblockUI("#mapDiv"+g_num, "end");
                    swal('获取当前范围数据失败', '', 'error');
                    return;
                }
                if (data.data.length == 0) {
                    console.log("length-null");
                    zmblockUI("#mapDiv"+g_num, "end");
                    swal('当前范围缺少数据', '', 'error');
                    return;
                }
                console.log(g_num + '~~~' + data.data.length);
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

//	        console.log(JSON.stringify(featureset));
//	        console.log((new Date().getTime() - v1) + "生成数据");
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
                    
                    var out_raster_layer = app.mapList[g_num].getLayer('out_raster_layer');
                    if (out_raster_layer) {
                        app.mapList[g_num].removeLayer(out_raster_layer);
                    }
                    app.mapList[g_num].addLayer(gpResultLayer);
                    
                    //添加图例
                    $('#colorBar'+g_num).html("<img src='img/cb/"+par.species[0]+".png' width='75%' height='75px' />");
                    
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
                            judgmentObj.push(g_num);
                            judgment();
                            jobstatus = '--' + g_num + '--处理完成...';
                            console.log((new Date().getTime() - v1) + jobstatus);
                            zmblockUI("#mapDiv"+g_num, "end");
                            break;
                    }
                }, function (error) {
                    console.log(error);
                    zmblockUI("#mapDiv"+g_num, "end");
                    swal('GIS，内部错误', '', 'error');
                });

            }).error(function (res) {
            	zmblockUI("#mapDiv"+g_num, "end");
                swal('抽数，内部错误', '', 'error');
            });
        }else{//风场
        	var lujing = "";
        	var fxOpacity = 0;
        	if(wind == 1){
        		lujing = "fxj";
        		par.windSymbol = 0;
        		fxOpacity = 0.5;
        	}else if(wind == 2){
        		lujing = "fx";
        		par.windSymbol = 1;//1代表F风，F风最大到20级，箭头风最大到12级
        		fxOpacity = 1;
        	}
        	par.rows = 20;
        	par.cols = 20;
        	
            par.species = ['WSPD','WDIR'];
            app.gLyr1.clear();
            app.gLyr2.clear();
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
                    if(g_num == 0){
                        app.gLyr1.add(graphic);
                        app.gLyr1.setOpacity(fxOpacity);
                    }else if(g_num == 1){
                        app.gLyr2.add(graphic);
                        app.gLyr2.setOpacity(fxOpacity);
                    }


                });
//                zmblockUI("#mapDiv0", "end");
//                zmblockUI("#mapDiv1", "end");
                console.log((new Date().getTime() - v1) + "num:" +g_num);
            }).error(function (res) {
//            	zmblockUI("#mapDiv"+g_num, "end");
                swal('风场抽数，内部错误', '', 'error');
            });

        }


    }



	
	
	
    

}


/*添加情景选择按钮*/
function setQjSelectBtn(data) {
    changeMsg.missionId = sceneInitialization.taskID;
    changeMsg.domainId = sceneInitialization.domainId;
    changeMsg.qj1Id = sceneInitialization.jzID;
    if (data.length > 0) {
        changeMsg.qj2Id = data[0].scenarinoId;
    } else {
        changeMsg.qj2Id = '';
    }
    var s1, s2, e1, e2;
    $('#qjBtn1 .btn-group').empty();
    $('#qjBtn2 .btn-group').empty();
    for (var i = 0; i <= data.length; i++) {
        var btn1 = $('<label class="btn btn-outline btn-success bgw"><input type="radio" name="qjBtn1"><span></span></label><br/>');
        var btn2 = $('<label class="btn btn-outline btn-success bgw"><input type="radio" name="qjBtn2"><span></span></label><br/>');
        if(i==0){
            btn1.attr('title', '基准').find('input').attr('value', sceneInitialization.jzID).attr('data-sDate', sceneInitialization.s).attr('data-eDate', sceneInitialization.e);
            btn1.find('span').html('基准');
            btn2.attr('title', '基准').find('input').attr('value', sceneInitialization.jzID).attr('data-sDate', sceneInitialization.s).attr('data-eDate', sceneInitialization.e);
            btn2.find('span').html('基准');
            btn1.addClass('active').find('input').attr('checked', true);
            btn2.addClass('disabled');
            s1 = sceneInitialization.s;
            e1 = sceneInitialization.e;

        }else{
            btn1.attr('title', data[i-1].scenarinoName).find('input').attr('value', data[i-1].scenarinoId).attr('data-sDate', data[i-1].scenarinoStartDate).attr('data-eDate', data[i-1].scenarinoEndDate);
            btn1.find('span').html(data[i-1].scenarinoName);
            btn2.attr('title', data[i-1].scenarinoName).find('input').attr('value', data[i-1].scenarinoId).attr('data-sDate', data[i-1].scenarinoStartDate).attr('data-eDate', data[i-1].scenarinoEndDate);
            btn2.find('span').html(data[i-1].scenarinoName);
        }

        if (i == 1) {
            btn2.addClass('active').find('input').attr('checked', true);
            s2 = data[i-1].scenarinoStartDate;
            e2 = data[i-1].scenarinoEndDate;
        }
        $('#qjBtn1 .btn-group').append(btn1);
        $('#qjBtn2 .btn-group').append(btn2);
    }
    setDate(s1, e1, s2, e2);
    updata();
}

var rmsType = 'd';
var startTime, endTime, nowTime;//存储moment对象
/*设置日期下拉框*/
/*传入毫秒数*/
function setDate(s1, e1, s2, e2) {
    console.log('shezhishijian')
    $('#sTime-d').empty();
    $('#eTime').empty();
    videoPlayScale = [];
    if (!s2 || !e2) {
        s1 = moment(s1 - 0);
        e1 = moment(e1 - 0);
        if (e1.isBefore(s1, 'd')) {
            return;
        }
        startTime = s1;
        endTime = e1;
        nowTime = moment(startTime);
        var s = moment(startTime);
        var e = moment(endTime);
        while (true) {
            $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
            $('#eTime').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
            videoPlayScale.push(s.format('YYYY-MM-DD'));
            //console.log(s.format('YYYY-MM-DD'));
            if (s.format('YYYY-MM-DD') == 'Invalid date') {
                break;
            }
            if (e.isBefore(s.add(1, 'd'))) {
                break;
            }
        }
        changeMsg.sTimeD = moment($('#sTime-d').val()).format('YYYYMMDD');
        changeMsg.eTime = moment($('#eTime').val()).format('YYYYMMDD');
    } else {
        s1 = moment(s1 - 0);
        s2 = moment(s2 - 0);
        e1 = moment(e1 - 0);
        e2 = moment(e2 - 0);
        if (e1.isBefore(s2, 'd') || e2.isBefore(s1, 'd')) {
            return;
        }

        if (s1.isBefore(s2, 'd')) {
            startTime = s2;
        } else {
            startTime = s1;
        }

        if (e1.isBefore(e2, 'd')) {
            endTime = e1;
        } else {
            endTime = e2;
        }

        nowTime = moment(startTime);
        var s = moment(startTime);
        var e = moment(endTime);
        while (true) {
            $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
            $('#eTime').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
            videoPlayScale.push(s.format('YYYY-MM-DD'));
            //console.log(s.format('YYYY-MM-DD'));
            if (s.format('YYYY-MM-DD') == 'Invalid date') {
                break;
            }
            if (e.isBefore(s.add(1, 'd'))) {
                break;
            }
        }
        changeMsg.sTimeD = moment($('#sTime-d').val()).format('YYYYMMDD');
        changeMsg.eTime = moment($('#eTime').val()).format('YYYYMMDD');
    }
    initVideoPlay();
}

/*顶部选择事件*/
$('#species').on('change', function (e) {  //物种选择
    var species = $(e.target).val();
    changeMsg.species = [];
    changeMsg.species.push(species);
    console.log(species);
    updata();
});

$('input[name=domain]').on('change', function (e) { //domaon选择
    var domain = $(e.target).val();
    changeMsg.domain = domain;
    console.log(domain);
    updata();
});

$('input[name=rms]').on('change', function (e) { //时间分辨率选择
    var rms = $(e.target).val();
    changeMsg.dates = [];
    rmsType = rms;
    console.log(rms);
    $('#species').empty();
    for (var i = 0; i < speciesArr[rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
    }
    $('#sTime-d').css('width', '100%');
    if (rms == 'd') {
        $('#sTime-h').addClass('disNone');
        $('#eTimeP').addClass('disNone');
    } else if (rms == 'h') {
        $('#sTime-d').css('width', '70%');
        $('#sTime-h').removeClass('disNone');
        $('#eTimeP').addClass('disNone');
    } else {
        $('#sTime-h').addClass('disNone');
        $('#eTimeP').removeClass('disNone');

        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYYMMDD'));
            if (s.add(1, 'd').isAfter(e)) {
                break;
            }
        }

        // $('#showType .active input').prop('checked', false);
        // $('#showType .active').removeClass('active');
        // $('#showType label').attr('disabled', true);
        // $('#showType input[value=concn]').prop('checked', true).parent().addClass('active').removeAttr('disabled');
        // changeMsg.showType = ['concn'];
    }
    changeMsg.rms = rms;
    updata();
});

$('input[name=calcType]').on('change', function (e) { //地图图片类型
    var type = $(e.target).val();
    changeMsg.calcType = type;
    console.log(type);
    updata(true);
});

$('input[name=showWind]').on('change', function (e) { //地图风场类型
    var type = $(e.target).val();
    changeMsg.showType = ['concn'];
    changeMsg.showWind = type;

    if (type == -1) {

        /*这里清除风场图层*/
    	app.gLyr1.clear();
        app.gLyr2.clear();
        return;
        /*这里清除风场图层 end*/

    } else {
        changeMsg.showType.push('wind');
    }

    console.log(type);
    updata('wind');
});

$('#qjBtn1').on('change', 'input', function (e) {//改变左侧情景
    var qjId = $(e.target).val();
    console.log(qjId)
    var index = $('input[name=qjBtn1]').index($(e.target));
    $('#qjBtn2 .disabled').removeClass('disabled');
    $('#qjBtn2 .active').removeClass('active');
    $('input[name=qjBtn2]').eq(index).parents('label').addClass('disabled');
    $('input[name=qjBtn2]').removeAttr('checked');
    if (index == 0) {
        $('#qjBtn2 label').eq(1).addClass('active').find('input').attr('checked', true)
    } else {
        $('#qjBtn2 label').eq(0).addClass('active').find('input').attr('checked', true)
    }

    var s1 = $(e.target).attr('data-sDate');
    var e1 = $(e.target).attr('data-eDate');

    var s2 = $('#qjBtn2 label.active input').attr('data-sDate');
    var e2 = $('#qjBtn2 label.active input').attr('data-eDate');


    changeMsg.qj1Id = qjId;
    changeMsg.qj2Id = $('#qjBtn2 label.active').find('input').val();
    //changeMsg.qj2Id = $('input[name=qjBtn2]').val();
    setDate(s1, e1, s2, e2);
    updata();
});

$('#qjBtn2').on('change', 'input', function (e) {//改变右侧情景
    var qjId = $(e.target).val();
    console.log(qjId);

    var s2 = $(e.target).attr('data-sDate');
    var e2 = $(e.target).attr('data-eDate');

    var s1 = $('#qjBtn1 label.active input').attr('data-sDate');
    var e1 = $('#qjBtn1 label.active input').attr('data-eDate');

    changeMsg.qj2Id = qjId;
    setDate(s1, e1, s2, e2);
    updata(true);
});

$('#sTime-d').on('change', function (e) {//选择日期
    var date = $(e.target).val();
    changeMsg.sTimeD = moment(date).format('YYYYMMDD');
    if (changeMsg.rms == 'a') {
        changeMsg.dates = []
        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYYMMDD'));
            if (s.add(1, 'd').isAfter(e)) {
                break;
            }
        }
    }
    console.log(date);
    updata();
});

$('#sTime-h').on('change', function (e) {//选择时间
    var date = $(e.target).val();
    changeMsg.sTimeH = date - 0;
    console.log(date)
    updata();
});

$('#eTime').on('change', function (e) {//选择平均后的时间
    var date = $(e.target).val();
    changeMsg.eTime = moment(date).format('YYYYMMDD');
    if (changeMsg.rms == 'a') {
        changeMsg.dates = [];
        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYYMMDD'));
            if (s.add(1, 'd').isAfter(e)) {
                break;
            }
        }
    }
    console.log(date);
    updata();
});

/*数据更新*/
function updata(t) {
    
    var parameter = {
        userId: userId,
        layer: changeMsg.layer,
        rows: changeMsg.rows,
        cols: changeMsg.cols,
        domainId: changeMsg.domainId,
        domain: changeMsg.domain,
        missionId: changeMsg.missionId,
        species: (function() {
            var arr = [];
            for(var i=0;i<changeMsg.species.length;i++){
                arr.push(mappingSpeciesBig[changeMsg.species[i]])
            }
            return arr;
        })(),
        timePoint: changeMsg.rms,
        borderType: "0"
    };
    var p1 = $.extend({
        scenarioId1: changeMsg.qj1Id,
        calcType: 'show'
    }, parameter);

    if(changeMsg.calcType == 'show'){
        var p2 = $.extend({
            scenarioId1: changeMsg.qj2Id
        }, parameter);
    }else{
        var p2 = $.extend({
            scenarioId1: changeMsg.qj1Id,
            scenarioId2: changeMsg.qj2Id
        }, parameter);
    }


    if (changeMsg.rms == 'd') {
        p1.day = changeMsg.sTimeD;
        p2.day = changeMsg.sTimeD;

        //p1.day = '2016322';
        //p2.day = '2016322';
    } else if (changeMsg.rms == 'h') {
        p1.day = changeMsg.sTimeD;
        p2.day = changeMsg.sTimeD;
        p1.hour = changeMsg.sTimeH;
        p2.hour = changeMsg.sTimeH;
    } else {
        p1.dates = changeMsg.dates;
        p2.dates = changeMsg.dates;
    }
    p1.GPserver_type = [];
    p2.GPserver_type = [];
    for(var i=0;i<changeMsg.species.length;i++){
        p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
        p2.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
    }




    if(t == 'wind'){
        p1.showType = t;

        /*执行方法，进行左图添加*/
        bianji("1", 0, p1,changeMsg.showWind);
        /*执行方法，进行左图添加 end*/

        p2.calcType = changeMsg.calcType;
        p2.showType = t;
        //console.log('p2',$.extend({},p2),p2.showType);

        /*执行方法，进行右图添加*/
        bianji("1", 1, p2,changeMsg.showWind);
        /*执行方法，进行右图添加 end*/
    }else if (t) {
//    	zmblockUI("#mapDiv0", "start");
        
        
        for (var x = 0; x < changeMsg.showType.length; x++) {
            p2.calcType = changeMsg.calcType;
            p2.showType = changeMsg.showType[x];
            //console.log('p2',$.extend({},p2),p2.showType);

            zmblockUI("#mapDiv1", "start");
            /*执行方法，进行右图添加*/
            bianji("1", 1, p2);
            /*执行方法，进行右图添加 end*/

        }
    } else {
//    	zmblockUI("#mapDiv0", "start");
//        zmblockUI("#mapDiv1", "start");
        
        for (var i = 0; i < changeMsg.showType.length; i++) {
            p1.showType = changeMsg.showType[i];



            p2.calcType = changeMsg.calcType;
            p2.showType = changeMsg.showType[i];
            //console.log('p2',$.extend({},p2),p2.showType);



            if(i==0){
                /*执行方法，进行左图添加*/
            	zmblockUI("#mapDiv0", "start");
                bianji("1", 0, p1);
                /*执行方法，进行左图添加 end*/

                /*执行方法，进行右图添加*/
                zmblockUI("#mapDiv1", "start");
                bianji("1", 1, p2);
                /*执行方法，进行右图添加 end*/
            }else{
                /*执行方法，进行左图添加*/
            	zmblockUI("#mapDiv0", "start");
                bianji("1", 0, p1,changeMsg.showWind);
                /*执行方法，进行左图添加 end*/

                /*执行方法，进行右图添加*/
                zmblockUI("#mapDiv1", "start");
                bianji("1", 1, p2,changeMsg.showWind);
                /*执行方法，进行右图添加 end*/
            }
        }
    }

}


// /*视频播放*/
// var progress = $("#container").videodate({startTime: '2017-3-26', endTime: '2017-5-1', playbutton: fun1});
// function fun1() {
//     var ben = this;
//     console.log(ben);
//     setInterval(function () {
//         ben.add(1)
//     }, 2000);
// }


/*视频播放相关*/
(function () {

})()
var b;
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
    changeMsg.sTimeD = moment(playDay).format('YYYYMMDD');
    if(changeMsg.rms == 'h'){
        changeMsg.sTimeH = playHour;
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
            updata('wind');
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
    if(changeMsg.showWind == -1){
        if(judgmentObj.length == 2){
            window.setTimeout(function () {
                videoPlay();
            },2000)
        }
    }else{
        if(judgmentObj.length == 4){
            window.setTimeout(function () {
                videoPlay();
            },2000)
        }
    }
}