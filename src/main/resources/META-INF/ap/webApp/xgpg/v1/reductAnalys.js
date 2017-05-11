var ls = window.sessionStorage;
var qjid_dq;//当前的情景ID
var qjname_dq;//当前情景的name


var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if (!sceneInitialization) {
    sceneInitialization = JSON.parse(ls.getItem('SI'));
} else {
    ls.setItem('SI', JSON.stringify(sceneInitialization));
}
console.log(JSON.stringify(sceneInitialization));

if (!sceneInitialization) {
    sceneInittion();
} else {
    setQjSelectBtn(sceneInitialization.data);//加载地图上的情景按钮
}
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
                //$("#Initialization").modal();//初始化模态框显示
                //$("#Initialization").window();
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
    console.log()
    //表格交互 easyui

    $.ajax({
        url: '/ampc/scenarino/find_All_scenarino',
        contentType: 'application/json',
        method: 'post',
        dataType: 'JSON',
        data: JSON.stringify({
            "token": "",
            "data": {
                "userId": 1,
                "missionId":$("#task").val()
            }
        }),
        success:function (data) {

            $("#sceneTableId").datagrid({
                data:data.data.rows,
                columns:[[
                    {field:"ck",checkbox:true},
                    {field:"scenarinoName",title:"情景名称"},
                    {field:"scenType",title:"情景描述"},
                    {field:"scenarinoStartDate",title:"时间"},
                    {field:"scenarinoEndDate",title:"时间"},
                ]],
                clickToSelect: true,// 点击选中行
                pagination: false, // 在表格底部显示分页工具栏
                striped: true, // 使表格带有条纹
                queryParams: function (params) {
                    var data = {};
                    data.userId = userId;
                    data.missionId = $("#task").val();
                    return JSON.stringify({"token": "", "data": data});
                }
            })
        }
    })
/*
    $("#sceneTableId").bootstrapTable({
        method: 'POST',
        url: localhttp+'/ampc/scenarino/find_All_scenarino',
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
    });*/
}

/**
 * 保存选择的情景
 */
function save_scene() {
   // var row = $('#sceneTableId').bootstrapTable('getSelections');//获取所有选中的情景数据
    var row = $('#sceneTableId').datagrid('getSelections');//获取所有选中的情景数据

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
        //console.log("数据")
        setQjSelectBtn(data);//添加情景选择按钮
        sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
        $("#close_scene").click();
        shoe_data_start();
    }
}
//超链接显示 模态框
function exchangeModal() {
    sceneInittion();
    //$("#Initialization").modal();
    $("#Initialization").window("open")
}


/*添加情景选择按钮*/
function setQjSelectBtn(data) {
    $('#qjBtn1 .btn-group').empty();
    for (var i = 0; i < data.length; i++) {
       // var btn1 = $('<label class="btn btn-outline btn-success bgw" style="z-index: 2;position:relative"><input type="radio"  name="qjBtn1"><span></span></label><br/>');
        var btn1 = $('<label class="btn btn-outline btn-success bgw" style="z-index: 2;position:relative"><input type="radio"  name="qjBtn1"><span></span></label>');
        btn1.attr('title', data[i].scenarinoName).find('input').attr('value', data[i].scenarinoId);
        btn1.find('span').html(data[i].scenarinoName);
        if (i == 0) {
            btn1.addClass('active').find('input').attr('checked', true);
            qjid_dq = data[i].scenarinoId;
            qjname_dq = data[i].scenarinoName;
        }
        $('#qjBtn1 .btn-group').append(btn1);
    }
    // shoe_data_start();
}

/*切换情景事件*/
$('#qjBtn1').on('change', 'input', function (e) {
    console.log($(e.target).val());
    qjid_dq = $(e.target).val();
    shoe_data_start()
})


/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">减排分析</span><a onclick="exchangeModal()" class="nav_right" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
var gis_paramsName = {};//地图请求的参数，第一次加载地图时初始化，每次更改地图比例尺时修改codeLevel

var tj_paramsName = {};//统计图用的参数
tj_paramsName.wz = $('#hz_wrw').val();//默认的物种
tj_paramsName.code = "0";//code默认为0
// tj_paramsName.name = qjname_dq;//name默认为情景名称
tj_paramsName.name = "全部区域";//name默认为情景名称
tj_paramsName.codeLevel = 1;



/**
 * 时间戳转成日期格式
 * @param nS
 * @returns
 */
function getLocalTime(nS) {
    return moment(nS).format('YYYY-MM-DD');
}


/**
 * 获取当前选择的行业措施的内容
 */
function getchaxuntype() {
    $("#chaxuntype").children().each(function () {
        if ($(this).is('.active')) {
            tj_paramsName.type = $(this).attr("val_name");
        }
    });
}


/**
 * 操作地图显示
 */
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
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol", "esri/symbols/PictureMarkerSymbol",
        "esri/renderers/ClassBreaksRenderer", "esri/symbols/SimpleMarkerSymbol", "esri/dijit/PopupTemplate", "esri/geometry/Point", "esri/geometry/Extent",
        "esri/renderers/SimpleRenderer", "esri/graphic", "dojo/_base/Color", "dojo/dom-style", 'dojo/query', "esri/tasks/FeatureSet", "esri/SpatialReference",
        'extras/ClusterLayer', "tdlib/gaodeLayer", "dojo/dom-construct", "esri/dijit/Legend", "dojo/dom", "extras/Tip", "dojo/domReady!"],
    function (Map, FeatureLayer, GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, PictureMarkerSymbol, ClassBreaksRenderer, SimpleMarkerSymbol, PopupTemplate, Point, Extent, SimpleRenderer, Graphic,
              Color, domStyle, query, FeatureSet, SpatialReference, ClusterLayer, gaodeLayer, domConstruct, Legend, dom, Tip) {
        dong.gaodeLayer = gaodeLayer;
        dong.Graphic = Graphic;
        dong.Point = Point;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
        dong.Extent = Extent;
        dong.SimpleLineSymbol = SimpleLineSymbol;
        dong.Color = Color;
        dong.PopupTemplate = PopupTemplate;
        dong.ClusterLayer = ClusterLayer;
        dong.PictureMarkerSymbol = PictureMarkerSymbol;
        dong.ClassBreaksRenderer = ClassBreaksRenderer;
        dong.domStyle = domStyle;
        dong.query = query;
        dong.FeatureLayer = FeatureLayer;
        dong.SimpleFillSymbol = SimpleFillSymbol;
        dong.domConstruct = domConstruct;
        dong.Legend = Legend;
        dong.dom = dom;
        dong.Tip = Tip;

        app.map = new Map("map_showId", {
            logo: false,
            center: [stat.cPointx, stat.cPointy],
            minZoom: 3,
            maxZoom: 13,
            zoom: 4
        });


        app.baselayerList = new dong.gaodeLayer();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new dong.gaodeLayer({layertype: "st"});//加载卫星图
        app.labellayerList = new dong.gaodeLayer({layertype: "label"});//加载标注图

        app.map.on("load", shoe_data_start);//启动后立即执行获取数据

        app.map.addLayer(app.baselayerList);//添加高德地图到map容器
        app.map.addLayers([app.baselayerList]);//添加高德地图到map容器

        app.map.graphics.clear();

        dojo.connect(app.map, "onClick", optionclick);//点击

        dojo.connect(app.map, "onZoomEnd", resizess);//缩放
        app.outline = new dong.SimpleLineSymbol("solid", new dong.Color("#444"), 1);
        app.selectline = new dong.SimpleLineSymbol("solid", new dong.Color("#5D1F68"), 3);
    });

/**
 * 启动后加载数据，
 */
function shoe_data_start(evn) {
    gis_paramsName.userId = userId;
    gis_paramsName.pollutant = $('#hz_wrw').val();//物种
    //获取地图当前比例尺，决定数据请求省市县的哪一级
//	if (evn.map.getZoom() <= 6) {

    gis_paramsName.codeLevel = 1;//省市区层级（1省，2市，3区）
    tj_paramsName.codeLevel = 1;

//	}else if (evn.map.getZoom() == 7){
//
//		gis_paramsName.codeLevel = 2;//省市区层级（1省，2市，3区）
//		tj_paramsName.codeLevel = 2;
//
//	}else if (evn.map.getZoom() >= 8){
//
//		gis_paramsName.codeLevel = 3;//省市区层级（1省，2市，3区）
//		tj_paramsName.codeLevel = 3;
//	}
    gis_paramsName.scenarinoId = qjid_dq;//情景id
    baizhu_jianpai(gis_paramsName, "1");
}

var gghjl = "";

/**
 * 标注地图的减排计算
 */
function baizhu_jianpai(gis_paramsName, sh_type) {
//	console.log(JSON.stringify(gis_paramsName));
    if (typeof app.paifang != "undefined") {
        app.map.removeLayer(app.paifang);
    }
    ajaxPost('/find_reduceEmission', gis_paramsName).success(function (res) {
//		console.log(JSON.stringify(res));
        if (res.status == 0) {

            var valMax = 0;//数据中的最大值
            $.each(res.data, function (k, col) {
                if (col > valMax) {
                    valMax = col;
                }
            });
            if (parseInt(valMax) <= 0) {//如果最大值小于等于0，地图不显示
                if (app.hasOwnProperty("legend")) {
                    app.legend.destroy();
                    dong.domConstruct.destroy(dojo.byId("legend"));
                    $("#legendWrapper").hide();
                }
                return;
            }

            var data_id = "";
            var tegd = "";

            $.each(res.data, function (k, col) {
                data_id += "'" + k + "',";
                tegd += k + ",";
//				tj_paramsName.code = k;//获取最后一个行政区划
            });

            if (tj_paramsName.code == "0" && sh_type == "1") {//第一次打开页面，记录省级或者最高级的行政区划
                tj_paramsName.code = tegd.substring(0, tegd.length - 1);
                gghjl = tegd.substring(0, tegd.length - 1);
                if (sh_type == "1") {
                    bar();
                }
            }

            var paifang_url = "";
            if (gis_paramsName.codeLevel == 1) {
                paifang_url = ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/2";
            } else if (gis_paramsName.codeLevel == 2) {
                paifang_url = ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/1";
            } else if (gis_paramsName.codeLevel == 3) {
                paifang_url = ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/0";
            }

            app.paifang = new dong.FeatureLayer(paifang_url, {
                mode: dong.FeatureLayer.MODE_ONDEMAND,
                outFields: ["*"],
                id: "paifang"
            });

            var template = "<strong>${NAME}:  ${DATAVALU}</strong>";
            app.tip = new dong.Tip({
                "format": template,
                "node": "legend",
                "res": res.data
            });

            //渲染器，根据行政区划code获取外部获取的面对应的数据
            var br = new dong.ClassBreaksRenderer(null, function (graphic) {
                var state = graphic.attributes.ADMINCODE;
                return res.data[state];
            });

            br.setMaxInclusive(true);
            var breaks = calcBreaks(res.data);
            br.addBreak(breaks[0], breaks[1], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([72, 165, 251, 0.65])));
            br.addBreak(breaks[1], breaks[2], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([126, 251, 251, 0.65])));
            br.addBreak(breaks[2], breaks[3], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([122, 251, 159, 0.65])));
            br.addBreak(breaks[3], breaks[4], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([147, 251, 72, 0.65])));
            br.addBreak(breaks[4], breaks[5], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([229, 251, 72, 0.65])));
            br.addBreak(breaks[5], breaks[6], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 237, 72, 0.65])));
            br.addBreak(breaks[6], breaks[7], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 171, 72, 0.65])));
            br.addBreak(breaks[7], breaks[8], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 100, 72, 0.65])));

            app.paifang.setRenderer(br);
            app.paifang.redraw();
            app.map.addLayer(app.paifang);
            createLegend(gis_paramsName.codeLevel);

            dojo.connect(app.paifang, "onMouseOver", app.tip.showInfo);
            dojo.connect(app.paifang, "onMouseOut", app.tip.hideInfo);

            if (gis_paramsName.codeLevel == 1) {
                var query = new dong.query();
                query.where = "ADMINCODE in (" + data_id.substring(0, data_id.length - 1) + ")";
                app.paifang.queryFeatures(query, function (featureSet) {
                    var extent = new dong.Extent();
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (i == 0) {
                            extent = graphic.geometry.getExtent();
                        } else {
                            extent = extent.union(graphic.geometry.getExtent());
                        }
                    }
                    app.map.setExtent(extent.expand(1.5));
                });
            }
            if (sh_type == "1") {
                bar();
            }
        } else {
            swal('减排数据获取失败', '', 'error');
        }
    });
}

/**
 * 创建图例
 */
function createLegend(level) {
    //如果存在的话，删除之前的图例
    if (app.hasOwnProperty("legend")) {
        app.legend.destroy();
        dong.domConstruct.destroy(dojo.byId("legend"));
        $("#legendWrapper").hide();
    }
    $("#legendWrapper").show();//预备可能第一次就没有图例。或者已经被隐藏，每次显示
    $("#legendWrapper").children(".tip").remove();//清空标题，否则会存留到下一个图例中
    //创建一个新的div图例
    var legendDiv = dong.domConstruct.create("div", {
        id: "legend"
    }, dong.dom.byId("legendWrapper"));

    var title = "";
    if (level == 1) {
        title = "各省 " + $('#hz_wrw').val() + " 减排量（吨）";
    } else if (level == 2) {
        title = "各市 " + $('#hz_wrw').val() + " 减排量（吨）";
    } else if (level == 3) {
        title = "各县 " + $('#hz_wrw').val() + " 减排量（吨）";
    }
    // tj_paramsName.codeLevel = level;
    app.legend = new dong.Legend({
        map: app.map,
        respectCurrentMapScale: false,//当真正的图例会更新每个规模变化和只显示层和子层中可见当前地图比例尺。当假的,图例不更新在每个规模变化和所有层和子层将显示出来。默认值是正确的。
        layerInfos: [{
            layer: app.paifang,
            title: title
        }]
    }, legendDiv);
    app.legend.startup();
}

/**
 * 计算分段
 * @param data
 */
function calcBreaks(data) {
    var breaks = [];
    var valMax = 0;//数据中的最大值
    $.each(data, function (k, col) {
        if (col > valMax) {
            valMax = col;
        }
    });


    if (valMax > 64 && valMax <= 640) {
        switch (true) {
            case valMax < 80 :
                breaks = [0, 10, 20, 30, 40, 50, 60, 70, 80];
                break;
            case valMax < 96 :
                breaks = [0, 12, 24, 36, 48, 60, 72, 84, 96];
                break;
            case valMax < 120 :
                breaks = [0, 15, 30, 45, 60, 75, 90, 105, 120];
                break;
            case valMax < 160 :
                breaks = [0, 20, 40, 60, 80, 100, 120, 140, 160];
                break;
            case valMax < 240 :
                breaks = [0, 30, 60, 90, 120, 150, 180, 210, 240];
                break;
            case valMax < 320 :
                breaks = [0, 40, 80, 120, 160, 200, 240, 280, 320];
                break;
            case valMax < 400 :
                breaks = [0, 50, 100, 150, 200, 250, 300, 350, 400];
                break;
            default :
                breaks = [0, 80, 160, 240, 320, 400, 480, 560, 640];
        }
    } else if (valMax <= 64) {

        var i = 10;
        while (true) {
            var ttrt = valMax * i;//最大值按比例减少，第一次不变，以后每次减一个零
            if (ttrt > 64) {
                switch (true) {
                    case ttrt < 80 :
                        breaks = [0, (10 / i), (20 / i), (30 / i), (40 / i), (50 / i), (60 / i), (70 / i), (80 / i)];
                        break;
                    case ttrt < 96 :
                        breaks = [0, (12 / i), (24 / i), (36 / i), (48 / i), (60 / i), (72 / i), (84 / i), (96 / i)];
                        break;
                    case ttrt < 120 :
                        breaks = [0, (15 / i), (30 / i), (45 / i), (60 / i), (75 / i), (90 / i), (105 / i), (120 / i)];
                        break;
                    case ttrt < 160 :
                        breaks = [0, (20 / i), (40 / i), (60 / i), (80 / i), (100 / i), (120 / i), (140 / i), (160 / i)];
                        break;
                    case ttrt < 240 :
                        breaks = [0, (30 / i), (60 / i), (90 / i), (120 / i), (150 / i), (180 / i), (210 / i), (240 / i)];
                        break;
                    case ttrt < 320 :
                        breaks = [0, (40 / i), (80 / i), (120 / i), (160 / i), (200 / i), (240 / i), (280 / i), (320 / i)];
                        break;
                    case ttrt < 400 :
                        breaks = [0, (50 / i), (100 / i), (150 / i), (200 / i), (250 / i), (300 / i), (350 / i), (400 / i)];
                        break;
                    default :
                        breaks = [0, (80 / i), (160 / i), (240 / i), (320 / i), (400 / i), (480 / i), (560 / i), (640 / i)];
                }
                break;
            } else {
                i = i * 10;
            }

        }

    } else if (valMax > 640) {

        var i = 10;
        while (true) {
            var ttrt = valMax / i;
            if (ttrt <= 640) {
                switch (true) {
                    case ttrt < 80 :
                        breaks = [0, (10 * i), (20 * i), (30 * i), (40 * i), (50 * i), (60 * i), (70 * i), (80 * i)];
                        break;
                    case ttrt < 96 :
                        breaks = [0, (12 * i), (24 * i), (36 * i), (48 * i), (60 * i), (72 * i), (84 * i), (96 * i)];
                        break;
                    case ttrt < 120 :
                        breaks = [0, (15 * i), (30 * i), (45 * i), (60 * i), (75 * i), (90 * i), (105 * i), (120 * i)];
                        break;
                    case ttrt < 160 :
                        breaks = [0, (20 * i), (40 * i), (60 * i), (80 * i), (100 * i), (120 * i), (140 * i), (160 * i)];
                        break;
                    case ttrt < 240 :
                        breaks = [0, (30 * i), (60 * i), (90 * i), (120 * i), (150 * i), (180 * i), (210 * i), (240 * i)];
                        break;
                    case ttrt < 320 :
                        breaks = [0, (40 * i), (80 * i), (120 * i), (160 * i), (200 * i), (240 * i), (280 * i), (320 * i)];
                        break;
                    case ttrt < 400 :
                        breaks = [0, (50 * i), (100 * i), (150 * i), (200 * i), (250 * i), (300 * i), (350 * i), (400 * i)];
                        break;
                    default :
                        breaks = [0, (80 * i), (160 * i), (240 * i), (320 * i), (400 * i), (480 * i), (560 * i), (640 * i)];
                }
                break;
            } else {
                i = i * 10;
            }
        }
    }
    return breaks;
}

/**
 * 地图改变比例尺事件
 * @param event
 */
function resizess(event) {
    //获取地图当前比例尺，如果比例尺要请求的数据与初始化的请求级别不一致，重新请求
    var level = 0;
    if (app.map.getZoom() <= 6) {
        level = 1;//省市区层级（1省，2市，3区）
    } else if (app.map.getZoom() == 7) {
        level = 2;//省市区层级（1省，2市，3区）
    } else if (app.map.getZoom() >= 8) {
        level = 3;//省市区层级（1省，2市，3区）
    }
    if (level != gis_paramsName.codeLevel) {
        gis_paramsName.codeLevel = level;
        baizhu_jianpai(gis_paramsName, "2");
        app.map.graphics.clear();//清空图层
    }
}

/**
 * 点击地图事件，调用统计图方法.清空图层
 * @param event
 */
function optionclick(event) {

    if (typeof event.graphic != "undefined") {//点击选中了一个面对象
        tj_paramsName.code = event.graphic.attributes.ADMINCODE;
        tj_paramsName.name = event.graphic.attributes.NAME;
        tj_paramsName.codeLevel = gis_paramsName.codeLevel;
        bar();
        app.map.graphics.clear();
        app.map.graphics.add(new dong.Graphic(event.graphic.geometry, app.selectline));//添加选中的图层
    } else {
        if (app.map.graphics.graphics.length > 0) {//已经有选中的对象了
            tj_paramsName.code = gghjl;
            tj_paramsName.codeLevel = 1;
            tj_paramsName.name = "全部区域";//name默认为情景名称
            bar();
            app.map.graphics.clear();
        }
    }
}

/**
 * 污染物选择，更新地图
 */
function gis_paifang_show() {
    gis_paramsName.pollutant = $('#hz_wrw').val();//物种
    tj_paramsName.wz = $('#hz_wrw').val();//物种


    $("#showtype").children().each(function () {
        if ($(this).is('.active')) {

            if ($(this).attr("val_name") == "gis") {
                baizhu_jianpai(gis_paramsName, "1");
                bar();

            } else if ($(this).attr("val_name") == "table") {
                bar();
            }
        }
    });
}

/**
 * 行业措施切换
 */
function type_info() {
    setTimeout(function () {
        pie();
    }, 100);
}

/****************************************************柱状图*************************************************************************/
function bar() {
    var paramsName = {
        "scenarinoId": gis_paramsName.scenarinoId,
        "code": tj_paramsName.code,
        "addressLevle": tj_paramsName.codeLevel,
        "stainType": tj_paramsName.wz
    };
    ajaxPost('/ampc/echarts/get_barInfo', paramsName).error(function () {
    }).success(function (res) {
        if (res.status == 0) {//返回成功

            if (res.data.dateResult.length > 0) {//有返回时间，说明可以显示柱状图

                tj_paramsName.new_arr = [];
                for (var i = 0; i < res.data.dateResult.length; i++) {
                    var items = res.data.dateResult[i];
                    tj_paramsName.new_arr.push(items.substring(0, 10));
                }
                //饼图
                pie();

                var myPfChart = echarts.init(document.getElementById('pfDiv1'),'shine');

                var option = {
                    title: {
                        text: tj_paramsName.name + "-" + tj_paramsName.wz + '-减排分析',
                        left:'50%',
                        top:'1%',
                        textAlign:'center'
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        },
                        formatter: function (params) {
                            return params[0].name + '<br/>'
                                + '基准排放量 : ' + (parseFloat(params[1].value) + parseFloat(params[0].value)).toFixed(2) + '<br/>'
                                + params[1].seriesName + ' : ' + params[1].value + '<br/>'
                                + params[0].seriesName + ' : ' + params[0].value;       //把 + params[0].value 去掉了
                        }
                    },
                    legend: {
                        //不触动
                        selectedMode: false,
                        top:'30px',
                        right: '10px;',
                        data: ['减排量', '实际排放量'],
                        backgroundColor:'#f0f0f0'
                    },
                    grid: {
                        show: true
                    },
                    dataZoom: [
                        {
                            show: 'true',
                            realtime: 'true',
                            start: 0,
                            end: 50
                            //startValue:

                        },
                        {
                            type: 'inside',
                            realtime: 'true',
                            start: 60,
                            end: 80
                            //	startValue:
                        }
                    ],
                    calculable: true,
                    xAxis: [
                        {
                            show: true,
                            type: 'category',
                            axisLabel: {
                                formatter: function (category) {
                                    newX.push(category.substring(0, 10));
                                    return category.substring(0, 10);          //截取字符串
                                }
                            },
                            data: res.data.dateResult
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            name: '吨',
                            boundaryGap: [0, 0.1],
                            splitArea: {show: true},
                            show: true
                        }
                    ],
                    series: [
                        {
                            name: '实际排放量',
                            type: 'bar',
                            stack: 'sum',
                            barCategoryGap: '50%',
                            itemStyle: {
                                normal: {
                                    color: 'tomato',
                                    barBorderColor: 'tomato',
                                    barBorderWidth: 4,
                                    barBorderRadius: 0
                                }
                            },
                            //data:res.data.pflResult
                            data: res.data.sjpflResult
                        },
                        {
                            name: '减排量',
                            type: 'bar',
                            stack: 'sum',
                            itemStyle: {
                                normal: {
                                    color: '#fff',
                                    barBorderColor: 'tomato',
                                    barBorderWidth: 4,
                                    barBorderRadius: 0,
                                    label: {
                                      /*show: true,
                                       position: 'top',*/
                                        formatter: function (params) {
                                            for (var i = 0, l = option.xAxis[0].data.length; i < l; i++) {
                                                if (option.xAxis[0].data[i] == params.name) {
                                                    return option.series[0].data[i] + params.value;
                                                }
                                            }
                                        },
                                        textStyle: {
                                            color: 'tomato'
                                        }
                                    }
                                }
                            },
                          /*markLine : {
                           data : [
                           {type : 'average', name: '平均值'}
                           ]
                           },*/
                            //data:res.data.jplResult
                            data: res.data.jplResult
                        }
                    ]
                };

                //减排量echarts
                myPfChart.setOption(option);
                //自适应屏幕大小变化
                $("#pfDiv1").panel({
                    onResize:function(){myPfChart.resize()}
                })

                window.addEventListener("resize", function () {

                  console.log(i);

                    setTimeout(function () {
                        myPfChart.resize();
                  },50);

                });




                //点击联动饼图
                myPfChart.on('datazoom', function (params) {
                    //alert(params.start + "||" + params.end + "||" + params.startValue + "||" + params.endValue);
                    //var aa = myPfChart.component.xAxis.option.xAxis[0].data;
                    if (newX.length == 0)return;
                    if (newX.length == oldX.length) {
                        if (newX[0] == oldX[0]) {
                            newX = [];
                            return;
                        }
                    }
                    oldX = [];
                    oldX = newX;
                    newX = [];

                    tj_paramsName.new_arr = [];
                    //删除时间重复的数据
                    var new_arr = [];
                    for (var i = 0; i < oldX.length; i++) {
                        var items = oldX[i];
                        //判断元素是否存在于new_arr中，如果不存在则插入到new_arr的最后
                        if ($.inArray(items, new_arr) == -1) {
                            tj_paramsName.new_arr.push(items);
                        }
                    }
                    pie();
                });
            } else {
                swal(tj_paramsName.name + '(' + tj_paramsName.code + ')缺少基准排放，无法计算', '', 'error');
            }
        } else {
            swal('/echarts/get_barInfo  连接错误', '', 'error');
        }
    });
}

var newX = [], oldX = [];

/****************************************************行业措施饼状图*************************************************************************/
function pie() {
    var nameVal;
    var valueVal;
    getchaxuntype();

//	if(tj_paramsName.code == '0'){
//		tj_paramsName.codeLevel = 0;
//	}else{
//		tj_paramsName.codeLevel = gis_paramsName.codeLevel;
//	}

    var paramsName = {
        "scenarinoId": gis_paramsName.scenarinoId,
        "code": tj_paramsName.code,
        "addressLevle": tj_paramsName.codeLevel,
        "stainType": tj_paramsName.wz,
        "startDate": tj_paramsName.new_arr[0],
        "endDate": tj_paramsName.new_arr[tj_paramsName.new_arr.length - 1],
        "type": tj_paramsName.type
    };
    ajaxPost('/ampc/echarts/get_pieInfo', paramsName).success(function (result) {

        if (result.status == 0) {

            if (result.data.length != 0) {
                var data_value = [];//数据
                var legend_name = [];
                var sum_value = 0;
                for (i = 0; i < result.data.length; i++) {
                    var ttgk = {};
                    ttgk.name = result.data[i].name;
                    legend_name.push(result.data[i].name);
                    ttgk.value = result.data[i].value;
                    sum_value += result.data[i].value;
                    data_value.push(ttgk);
                }

                var myhycsChart = echarts.init(document.getElementById('hycsDiv1'),'shine');
                var optionPie = {
                    title: {
                        text: tj_paramsName.name + '-' + (tj_paramsName.type == "1" ? "分行业" : "分措施") + "-" + tj_paramsName.wz + '-减排分析',
                        subtext: '全部' + (tj_paramsName.type == "1" ? "行业" : "措施") + '合计减排：' + sum_value.toFixed(2),
                        left:'50%',
                        top:'1%',
                        textAlign:'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        selectedMode: false,//图标不触动
                        orient: 'vertical',
                        data: legend_name,
                        top:'30px',
                        right: '10px;',
                        backgroundColor:'#f0f0f0'
                    },
                    series: [
                        {
                            name: '数据比例',
                            type: 'pie',
                            radius: '55%',
                            center: ['50%', '60%'],
                            data: data_value,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };

                //行业措施分担饼状图
                myhycsChart.setOption(optionPie);
                //自适应屏幕大小变化
                $("#hycsDiv1").panel({
                    onResize:function(){myhycsChart.resize()}
                })

            } else {
                swal('饼图暂无数据', '', 'error')
            }
        } else {
            swal('/echarts/get_pieInfo  参数错误' + JSON.stringify(paramsName), '', 'error');
        }

    }).error(function () {
        swal('/echarts/get_pieInfo  连接错误', '', 'error')
    })

}


var query_code = "";
var query_Level = "";

var d_code = "";
var d_Level = "";

/**
 * 地图展示与列表展示切换
 */
function gis_switch_table() {
    setTimeout(function () {
        $("#showtype").children().each(function () {
            if ($(this).is('.active')) {

                if ($(this).attr("val_name") == "gis") {

                    $("#listModal").hide();
                    $("#map_showId").show();
                    $("#legendWrapper").show();
                    $("#gis_table_title").html("<i class='im-earth'></i>各地区减排");
                    $('#qjBtn1 .btn-group label').css({"z-index":"2"});
                    baizhu_jianpai(gis_paramsName, "1");
                    bar();

                } else if ($(this).attr("val_name") == "table") {

                    query_code = tj_paramsName.code;
                    query_Level = tj_paramsName.codeLevel;

                    table_show(tj_paramsName.code, tj_paramsName.codeLevel);

                    $("#gis_table_title").html("<i class='im-table'></i>各地区减排比例(%)");
                    $("#listModal").show();
                    $("#map_showId").hide();
                    $('#qjBtn1 .btn-group label').css({"z-index":"-1"});
                    $("#legendWrapper").hide();
                }
            }
        });
    }, 100);
}

/**
 * 返回上级
 */
$("#returnSuperior").click(function () {
    if (parseInt(d_Level) > 0) {

        if (d_Level == "1") {//当前是市，返回上级是全部省
            table_show(gghjl, parseInt(d_Level));
        } else {//当前是县，返回上一级是市
            table_show(d_code.substring(0, 2) + "0000", parseInt(d_Level) - 1);
        }

    }
});

/**
 * 刷新
 */
$("#Refresh").click(function () {
    table_show(query_code, query_Level);
});


/**
 * 表格展示
 */
function table_show(cod1, level1) {

    d_code = cod1;
    d_Level = level1;

    $('#listModal_table').bootstrapTable('destroy');

    $.ajax({
        url: '/ampc/echarts/get_radioList',
        contentType: 'application/json',
        method: 'post',
        dataType: 'JSON',
        data: JSON.stringify({
            "token": "",
            "data": {
                "code": cod1,
                "addressLevle":level1,
                "scenarinoId":qjid_dq
            }
        }),
        success:function (data) {

/*
            if (data.status == 0) {
                console.log("状态吗")
                console.log(data.status)
                if (data.data.length > 0) {
                    $.each(data.data, function (i, col) {
                        console.log("得到")
                        console.log(col)
                        $.each(col, function (k, vol) {
                            if (vol == '-9999') {
                                data.data[i][k] = "-";
                            }
                        });
                        if (col.type == "1") {
                            data.data[i].name = '<a style="color:red;" onClick="table_show(\'' + col.code + '\',\'' + (parseInt(d_Level) + 1) + '\');">' + col.name + '</a>';
                            console.log(data.data[i].name)
                        }
                    });
                    return data.data;
                }
            } else if (data.status == '') {
                return "";
            }
*/
            $("#listModal_table").datagrid({
                height: $("#listModal").height() - 36,
                striped:true,
                data:data.data,
                columns:[[
                    {field:"name",title:"行政区"},
                    {field:"pm25",title:"PM<sub>2.5</sub>"},
                    {field:"pm10",title:"PM<sub>10</sub>"},
                    {field:"so2",title:"SO<sub>2</sub>"},
                    {field:"nox",title:"NO<sub>x</sub>"},
                    {field:"voc",title:"VOC"},
                    {field:"co",title:"CO"},
                    {field:"nh3",title:"NH<sub>3</sub>"},
                    {field:"bc",title:"BC"},
                    {field:"oc",title:"OC"},
                    {field:"pmfine",title:"PMFINE"},
                    {field:"pmc",title:"PMC"},
                ]],
                clickToSelect: true,// 点击选中行
                pagination: false, // 在表格底部显示分页工具栏
                striped: true, // 使表格带有条纹
                queryParams: function (params) {
                    var data = {};
                    data.code = cod1;
                    data.addressLevle = level1;
                    data.scenarinoId = qjid_dq;//情景id

   //			console.log(JSON.stringify(data));
                    return JSON.stringify({"token": "", "data": data});
                },
                loadFilter:function(data){
                    console.log(data)
                     if (data.d){

                         return data.d;

                     } else {
                     return data;
                     }
                }
            })
        }
    })

/*
    $('#listModal_table').bootstrapTable({
        height: $("#listModal").height() - 51,
        method: 'POST',
        url: localhttp+'/ampc/echarts/get_radioList',
        dataType: "json",
        iconSize: "outline",
        clickToSelect: true,// 点击选中行
        pagination: false, // 在表格底部显示分页工具栏
        striped: true, // 使表格带有条纹
        queryParams: function (params) {
            var data = {};

            data.code = cod1;
            data.addressLevle = level1;
            data.scenarinoId = qjid_dq;//情景id

//			console.log(JSON.stringify(data));
            return JSON.stringify({"token": "", "data": data});
        },
        responseHandler: function (res) {
//			console.log(JSON.stringify(res));
            console.log(res)
            if (res.status == 0) {
                if (res.data.length > 0) {

                    $.each(res.data, function (i, col) {
                        $.each(col, function (k, vol) {
                            if (vol == '-9999') {
                                res.data[i][k] = "-";
                            }
                        });

                        if (col.type == "1") {
                            res.data[i].name = '<a onClick="table_show(\'' + col.code + '\',\'' + (parseInt(d_Level) + 1) + '\');">' + col.name + '</a>';

                        }

                    });
                    return res.data;
                }
            } else if (res.status == '') {
                return "";
            }

        },
        queryParamsType: "limit", // 参数格式,发送标准的RESTFul类型的参数请求
        silent: true, // 刷新事件必须设置
        contentType: "application/json", // 请求远程数据的内容类型。
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
    });*/
}

