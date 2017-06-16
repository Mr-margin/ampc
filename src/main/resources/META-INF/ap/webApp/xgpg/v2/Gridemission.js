/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">网格排放</span><a onclick="exchangeModal()" class="nav_right" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a><span style="padding-left: 15px;padding-right: 15px;float:right;" id="missionName"></span>');

var opacity = 0.8;//默认的图层透明度
var ls, sceneInitialization, qjMsg;
var changeMsg = {
    borderType:1,
    showWind: '-1',
    showType: ['emis'],//"emis"代表排放、"concn"代表浓度、"wind"代表风场
    calcType: 'show',//"show"当前情景，"diff"差值，"ratio"比例
    species: ['PM₂.₅'],//物种
    missionId: '',//任务ID
    domain: 3,//模拟范围
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
var speciesArr = {
    d: ['PM₂.₅', 'PM₁₀', 'SO₂', 'NOx', 'CO', 'NH₃', 'BC', 'OC', 'PMFINE','PMcoarse'],
    a: ['PM₂.₅', 'PM₁₀', 'SO₂', 'NOx', 'CO', 'NH₃', 'BC', 'OC', 'PMFINE','PMcoarse'],
    h: ['PM₂.₅', 'PM₁₀', 'SO₂', 'NOx', 'CO', 'NH₃', 'BC', 'OC', 'PMFINE','PMcoarse']
};

var mappingSpecies = {
    d: {
        'PM₂.₅': 'PM25',
        'PM₁₀': 'PM10',
        'SO₂': 'so2_daily',
        'NOx': 'nox_daily',
        'CO': 'co_daily',
        'NH₃': 'PM25',
        'BC': 'PM25',
        'OC': 'PM25',
        'PMFINE': 'PM25',
        'PMcoarse':'PM25'
    },
    a: {
        'PM₂.₅': 'PM25',
        'PM₁₀': 'PM10',
        'SO₂': 'so2_daily',
        'NOx': 'nox_daily',
        'CO': 'co_daily',
        'NH₃': 'PM25',
        'BC': 'PM25',
        'OC': 'PM25',
        'PMFINE': 'PM25',
        'PMcoarse':'PM25'
    },
    h: {
        'PM₂.₅': 'PM25',
        'PM₁₀': 'PM10',
        'SO₂': 'so2_daily',
        'NOx': 'nox_daily',
        'CO': 'co_daily',
        'NH₃': 'PM25',
        'BC': 'PM25',
        'OC': 'PM25',
        'PMFINE': 'PM25',
        'PMcoarse':'PM25'
    }
};

var mappingSpeciesBig = {
    'PM₂.₅': 'PM25',
    'PM₁₀': 'PM10',
    'SO₂': 'SO2',
    'NOx': 'NOX',
    'CO': 'CO',
    'NH₃': 'NH3',
    'BC': 'BC',
    'OC': 'OC',
    'PMFINE': 'PMFINE',
    'PMcoarse': 'PMcoarse'
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
        "esri/renderers/SimpleRenderer", "esri/graphic", "dojo/_base/array", "dojo/number", "dojo/dom-style", "dijit/TooltipDialog",
        "dijit/popup", "dojox/widget/ColorPicker", "esri/layers/RasterLayer", "tdlib/gaodeLayer", "esri/tasks/FeatureSet", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
        "esri/geometry/Polygon", "esri/symbols/PictureFillSymbol", "esri/Color", "esri/layers/ArcGISDynamicMapServiceLayer", "esri/tasks/RasterData", "esri/layers/MapImageLayer", 
        "esri/layers/MapImage", "dojo/domReady!"
    ],
    function (Map, Geoprocessor, ImageParameters, DynamicLayerInfo, RasterDataSource, TableDataSource, LayerDataSource, FeatureLayer, GraphicsLayer, LayerDrawingOptions,
              SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint, Point, Extent, SimpleRenderer, Graphic,  array, number, domStyle,
              TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference, PictureMarkerSymbol, Polygon, PictureFillSymbol, Color,
              ArcGISDynamicMapServiceLayer, RasterData, MapImageLayer, MapImage) {

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
        dong.MapImageLayer = MapImageLayer;//
        dong.Extent = Extent;//
        dong.MapImage = MapImage;//

        esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
        esri.config.defaults.io.alwaysUseProxy = false;

        app.mapList = new Array();
        app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new Array();//加载卫星图
        app.labellayerList = new Array();//加载标注图
        app.mapimagelayer = new Array();//图片图层
        app.dynamicData = new Array();//栅格图层

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
            
            app.mapimagelayer[i] = new dong.MapImageLayer({"id":"myil"+i});
            app.mapList[i].addLayer(app.mapimagelayer[i]);
            
            app.dynamicData[i] = new dong.ArcGISDynamicMapServiceLayer(ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer");
            app.mapList[i].addLayer(app.dynamicData[i]);
            
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

                app.tuodong = false;

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

                app.tuodong = false;

                updata();

            } else {
                source = -1;
            }
        });

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
        if (!sceneInitialization) {
            sceneInittion();
        } else {
        	$("#missionName").empty();
        	$("#missionName").text(sceneInitialization.missionName);
            setQjSelectBtn(sceneInitialization.data);
        }
        /*这段代码需要在初始化中*/
        $('#species').empty();
        for (var i = 0; i < speciesArr.d.length; i++) {
            $('#species').append($('<option>' + speciesArr.d[i] + '</option>'))
        }
    });

var allMission = {};
/**
 * 初始化模态框显示
 */
function sceneInittion() {
    $("#task").html("");
    var paramsName = {};
    paramsName.userId = userId;
   // console.log(JSON.stringify(paramsName));
    ajaxPost('/mission/find_All_mission', paramsName).success(function (res) {
     //   console.log(JSON.stringify(res));
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

                if(!$.isEmptyObject(allMission)){
                    var id = $("#task").val();
                    if(allMission[id].missionStatus == '2'){
                        $('#task').css('width','60%');
                        $('#pathD').css('display','block');
                        $('#pathD').html('');

                        for(var ids in allMission[id].pathdates){
                            if(ids == sceneInitialization.pathdate){
                                $('#pathD').append($('<option selected="selected" value="'+ ids +'">'+ (ids==-1?'无':moment(allMission[id].pathdates[ids]).format('YYYY-MM-DD')) +'</option>'))
                            }else{
                                $('#pathD').append($('<option value="'+ ids +'">'+ (ids==-1?'无':moment(allMission[id].pathdates[ids]).format('YYYY-MM-DD')) +'</option>'))
                            }

                        }
                    }else{
                        $('#task').css('width','100%');
                        $('#pathD').html('');
                    }
                }

                //$("#Initialization").modal();//初始化模态框显示
                $("#Initialization").window('open')
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
 * 选择任务时候判断是否为预评估任务进行pathDate选择
 */
function selectRwId() {
    var id = $("#task").val();
    if(allMission[id].missionStatus == '2'){
        $('#task').css('width','60%');
        $('#pathD').css('display','block');
        $('#pathD').empty();

        for(var ids in allMission[id].pathdates){
            $('#pathD').append($('<option value="'+ ids +'">'+ (ids == -1?"无":moment(allMission[id].pathdates[ids]).format('YYYY-MM-DD')) +'</option>'))
        }
    }else{
        $('#task').css('width','100%');
        $('#pathD').empty();
    }
    sceneTable();
}

/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable() {
    $("#sceneTableId").bootstrapTable('destroy');//销毁现有表格数据
    //表格交互 easyui
    ajaxPost('/scenarino/find_All_scenarino',{
        "pathDate":(function () {
            if(allMission[$('#task').val()].missionStatus == '2'){
                if($('#pathD').val() != -1){
                    return moment(allMission[$('#task').val()].pathdates[$('#pathD').val()]).format('YYYY-MM-DD')
                }else{
                    return ''
                }
            }else{
                return ''
            }
        })(),
        "userId": userId,
        "missionId":$("#task").val()
    }).success(function(data){
        $("#sceneTableId").datagrid({
            data:data.data.rows,
            columns:[[
                {field:"ck",checkbox:true},
                {field:"scenarinoName",title:"情景名称"},
                {field:"scenType",title:"情景描述"},
                {field:"scenarinoStartDate",title:"时间",formatter:function(value,row,index){
                    return  moment(value).format("YYYY年MM月DD日");
                },align:'center'},
                {field:"scenarinoEndDate",title:"时间",formatter:function(value,row,index){
                    return  moment(value).format("YYYY年MM月DD日");
                },align:'center'},
            ]],
            clickToSelect: true,// 点击选中行
            pagination: false, // 在表格底部显示分页工具栏
            striped: true, // 使表格带有条纹
            queryParams: function (params) {
                var data = {};
                data.userId = userId;
                data.missionId = $("#task").val();
                return JSON.stringify({"token": "", "data": data});
            },
            onLoadSuccess:function(data){
                if(sceneInitialization!=null&&sceneInitialization!=""&&sceneInitialization!=undefined){
                    var truedData=sceneInitialization.data;
                    for(var i=0;i<truedData.length;i++){
                        if(data){
                            $.each(data.rows, function(index, item){
                                if(truedData[i].scenarinoId==item.scenarinoId){
                                    $('#sceneTableId').datagrid('checkRow', index);
                                }
                            });
                        }
                    }
                }
            }
        })
    })
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
        mag.missionName = $("#task :selected").text();
        mag.pathdate = $('#pathD').val();
        mag.domainId = allMission[mag.taskID].domainId;
        mag.s = allMission[mag.taskID].missionStartDate;
        mag.e = allMission[mag.taskID].missionEndDate;
        mag.jzID = allMission[mag.taskID].missionStatus == '2'?$('#pathD').val():allMission[mag.taskID].jzqjid;
        var data = [];
        $.each(row, function (i, col) {
            data.push({
                "scenarinoId": col.scenarinoId,
                "scenarinoName": col.scenarinoName,
                "scenarinoStartDate": col.scenarinoStartDate,
                "scenarinoEndDate": col.scenarinoEndDate,
                "scenType":col.scenType
            });
        });
        mag.data = data;
        vipspa.setMessage(mag);
        ls.setItem('SI', JSON.stringify(mag));
        sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
        setQjSelectBtn(data);
        $("#close_scene").click();
        $("#missionName").text(sceneInitialization.missionName);
        //查询任务的开始时间和结束时间
        var url='/Appraisal/show_Times';
        var paramsName = {
            "missionId":sceneInitialization.taskID,				//任务ID
        };
        ajaxPost(url, paramsName).success(function (res) {
            if (res.status == 0) {
                showTime=res;
                qjStartTime=showTime.data.startTime;
                qjEndTime=showTime.data.endTime;
            } else {
                swal(res.msg, '', 'error');
            }

        });

    }
}
//超链接显示 模态框
function exchangeModal() {
    sceneInittion();
    //$("#Initialization").modal();
    //$("#Initialization").window("open")
}

/**
 * 网格排放出图
 * @param type
 * @param g_num
 * @param p
 * @param wind
 */
function bianji_wanggepafang(type, g_num, p , wind){
    for(var sp=0;sp<changeMsg.species.length;sp++){
        if(!wind){
            wind = -1
        }
        var par = p;
        var v1 = new Date().getTime();
        
//        ajaxPost_w('http://166.111.42.85:8300/ampc/extract/tiff', par).success(function (data) {
        ajaxPost('/extract/tiff', par).success(function (data) {

    		if(data.status == 0){
    			
    			app.dynamicData[g_num].show();
    			var dynamicLayerInfos = app.dynamicData[g_num].createDynamicLayerInfosFromLayerInfos();
    			var dynamicLayerInfo = new dong.DynamicLayerInfo();
    			dynamicLayerInfo.id = 996;
    			dynamicLayerInfo.defaultVisibility = false;
    			dynamicLayerInfo.name = "reuslt";
    			var dataSource = new dong.RasterDataSource();
    			dataSource.workspaceId = "tiffPath";
    			
//    			dataSource.dataSourceName = "tiffPath/2017-06-05/1/1/372/3/emis/show/d-2016-11-17-0-0-PM25-509.tiff";
    			dataSource.dataSourceName = data.data;
    			
    			var layerSource = new dong.LayerDataSource();
    			layerSource.dataSource = dataSource;
    			dynamicLayerInfo.source = layerSource;
    			dynamicLayerInfos.push(dynamicLayerInfo);
    			app.dynamicData[g_num].setDynamicLayerInfos(dynamicLayerInfos);
    			app.dynamicData[g_num].setVisibleLayers([996]);
    			
    			$('#colorBar'+g_num).html("<img src='img/cb/"+par.species[0]+".png' width='75%' height='75px' />");//添加图例
    			zmblockUI1("#mapDiv"+g_num, "end");//打开锁屏控制
    			console.log((new Date().getTime() - v1) + "处理完成");//记录处理时间
    		}else{
    			zmblockUI1("#mapDiv"+g_num, "end");//打开锁屏控制
    		}
    	});
        
        
        
        
    }
}



/**
 * 风场出图
 * @param type
 * @param g_num
 * @param p
 * @param wind
 */
function bianji(type, g_num, p , wind) {
    for(var sp=0;sp<changeMsg.species.length;sp++){

        if(!wind){
            wind = -1
        }
        var par = p;
        var v1 = new Date().getTime();

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

        par.xmax = app.mapList[app.shengx].extent.xmax;
        par.xmin = app.mapList[app.shengx].extent.xmin;
        par.ymax = app.mapList[app.shengx].extent.ymax;
        par.ymin = app.mapList[app.shengx].extent.ymin;


        if(wind == -1){//无风场

        }else{
            par.species = ['WSPD','WDIR'];
            app.gLyr1.clear();
            app.gLyr2.clear();
            
//            console.log(JSON.stringify(par));
            
            ajaxPost('/extract/data', par).success(function (data) {
                if (!data.data) {
                    // console.log("data.data-null");
                    swal('获取当前范围风场数据失败', '', 'error');
                    return;
                }
                if (data.data.length == 0) {
                    // console.log("length-null");
                    swal('当前范围缺少风场数据', '', 'error');
                    return;
                }
                $.each(data.data, function (i, col) {

                    if (typeof col.x == "undefined") {
                        // console.log("x-null");
                        return;
                    }
                    if (typeof col.y == "undefined") {
                        // console.log("y-null");
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
                // console.log((new Date().getTime() - v1) + "num:" +g_num);
            }).error(function (res) {
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
    setDate(s1, e1, s2, e2, rmsType);
    app.tuodong = true;
    if(changeMsg.qj1Id == -1){
        ajaxPost('/scenarino/findby_pathdate',{
            userId:userId,
            pathDate:$('#sTime-d').val()
        }).success(function (res) {
            if(res.status == 0){
                changeMsg.qj1Id = res.data.scenarinoId;
                updata();
            }else{
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }).error(function () {
            swal({
                title: '接口故障',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        })
    }else{
        updata();
    }
}

var rmsType = 'd';
var startTime, endTime, nowTime;//存储moment对象
/*设置日期下拉框*/
/*传入毫秒数*/
function setDate(s1, e1, s2, e2, type) {

    $('#sTime-d').empty();
    $('#eTime').empty();
    if (!s2 || !e2) {
        //$('input[name=rms].p').parent().attr('disabled',true);
        s1 = moment(s1 - 0);
        e1 = moment(e1 - 0);


        if (type == 'h') {
            if (e1.isBefore(s1, 'h')) {
                return;
            }
            startTime = s1;
            endTime = e1;
            nowTime = moment(startTime);
            var s = moment(startTime);
            var e = moment(endTime);
            while (true) {
                //$('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
                //console.log(s.format('YYYY-MM-DD'));
                if (e.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if (e.isBefore(s.add(1, 'd'))) {
                    break;
                }
            }
            s = moment(startTime);
            while (true) {
                $('#sTime-h').append($('<option>' + s.format('HH') + '</option>'));
                if (s.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if ('23' == s.add(1, 'h').format('HH')) {
                    break;
                }
            }

        } else {
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
                //console.log(s.format('YYYY-MM-DD'));
                if (s.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if (e.isBefore(s.add(1, 'd'))) {
                    break;
                }
            }
        }
        changeMsg.sTimeD = moment($('#sTime-d').val()).format('YYYY-MM-DD');
        changeMsg.eTime = moment($('#eTime').val()).format('YYYY-MM-DD');
    } else {
        //$('input[name=rms].p').parent().removeAttr('disabled');
        s1 = moment(s1 - 0);
        s2 = moment(s2 - 0);
        e1 = moment(e1 - 0);
        e2 = moment(e2 - 0);
        if (type == 'h') {
            if (e1.isBefore(s2, 'h') || e2.isBefore(s1, 'h')) {
                return;
            }

            if (s1.isBefore(s2, 'h')) {
                startTime = s2;
            } else {
                startTime = s1;
            }

            if (e1.isBefore(e2, 'h')) {
                endTime = e1;
            } else {
                endTime = e2;
            }
            nowTime = moment(startTime);
            var s = moment(startTime);
            var e = moment(endTime);
            while (true) {
                $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
                //console.log(s.format('YYYY-MM-DD'));
                if (e.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if (e.isBefore(s.add(1, 'd'))) {
                    break;
                }
            }
            s = moment(startTime);
            while (true) {
                $('#sTime-h').append($('<option>' + s.format('HH') + '</option>'));
                if (s.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if ('23' == s.add(1, 'h').format('HH')) {
                    break;
                }
            }
        } else {
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
                //console.log(s.format('YYYY-MM-DD'));
                if (s.format('YYYY-MM-DD') == 'Invalid date') {
                    break;
                }
                if (e.isBefore(s.add(1, 'd'))) {
                    break;
                }
            }
        }
        changeMsg.sTimeD = moment($('#sTime-d').val()).format('YYYY-MM-DD');
        changeMsg.eTime = moment($('#eTime').val()).format('YYYY-MM-DD');
    }
}

/*顶部选择事件*/
$('#species').on('change', function (e) {  //物种选择
    var species = $(e.target).val();
    changeMsg.species = [];
    changeMsg.species.push(species);
   // console.log(species);
    app.tuodong = true;
    updata();
});

$('input[name=domain]').on('change', function (e) { //domaon选择
    var domain = $(e.target).val();
    changeMsg.domain = domain;
    //console.log(domain);
    app.tuodong = true;
    updata();
});

$('input[name=rms]').on('change', function (e) { //时间分辨率选择
    var rms = $(e.target).val();
    changeMsg.dates = [];
    rmsType = rms;
   // console.log(rms);
    $('#species').empty();
    for (var i = 0; i < speciesArr[rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
    }
    $('#sTime-d').css('width', '100%');
    if (rms == 'd') {
        $('#sTime-h').addClass('disNone');
        $('#eTimeP').addClass('disNone');
    } else if (rms == 'h') {
        $('#sTime-d').css('width', '65%');
        $('#sTime-h').removeClass('disNone');
        $('#eTimeP').addClass('disNone');
    } else {
        $('#sTime-h').addClass('disNone');
        $('#eTimeP').removeClass('disNone');

        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYY-MM-DD'));
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
    app.tuodong = true;

    updata();
});

$('input[name=calcType]').on('change', function (e) { //地图图片类型
    var type = $(e.target).val();
    changeMsg.calcType = type;
   // console.log(type);
    app.tuodong = true;
    updata(true);
});

$('input[name=showWind]').on('change', function (e) { //地图风场类型
    var type = $(e.target).val();
    changeMsg.showType = ['emis'];
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

    //console.log(type);
    app.tuodong = true;
    updata('wind');
});

$('#qjBtn1').on('change', 'input', function (e) {//改变左侧情景
    var qjId = $(e.target).val();
   // console.log(qjId)
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
    changeMsg.qj2Id = $('#qjBtn2 label.active').find('input').val();
    setDate(s1, e1, s2, e2, rmsType);
    app.tuodong = true;
    if(qjId == -1){
        ajaxPost('/scenarino/findby_pathdate',{
            userId:userId,
            pathDate:$('#sTime-d').val()
        }).success(function (res) {
            if(res.status == 0){
                qjId = res.data.scenarinoId
                changeMsg.qj1Id = qjId;
                updata();
            }else{
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }).error(function () {
            swal({
                title: '接口故障',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        })
    }else{
        changeMsg.qj1Id = qjId;
        updata();
    }
});

$('#qjBtn2').on('change', 'input', function (e) {//改变右侧情景
    var qjId = $(e.target).val();

    var s2 = $(e.target).attr('data-sDate');
    var e2 = $(e.target).attr('data-eDate');

    var s1 = $('#qjBtn1 label.active input').attr('data-sDate');
    var e1 = $('#qjBtn1 label.active input').attr('data-eDate');
    setDate(s1, e1, s2, e2, rmsType);
    app.tuodong = true;
    if(qjId == -1){
        ajaxPost('/scenarino/findby_pathdate',{
            userId:userId,
            pathDate:$('#sTime-d').val()
        }).success(function (res) {
            if(res.status == 0){
                qjId = res.data.scenarinoId
                changeMsg.qj2Id = qjId;
                updata();
            }else{
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }).error(function () {
            swal({
                title: '接口故障',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        })
    }else{
        changeMsg.qj2Id = qjId;
        updata(true);
    }
});

$('#sTime-d').on('change', function (e) {//选择日期
    var date = $(e.target).val();
    changeMsg.sTimeD = moment(date).format('YYYY-MM-DD');
    if (changeMsg.rms == 'a') {
        changeMsg.dates = []
        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYY-MM-DD'));
            if (s.add(1, 'd').isAfter(e)) {
                break;
            }
        }
    }
   // console.log(date);
    app.tuodong = true;
    updata();
});

$('#sTime-h').on('change', function (e) {//选择时间
    var date = $(e.target).val();
    changeMsg.sTimeH = date - 0;
    //console.log(date)
    app.tuodong = true;
    updata();
});

$('#eTime').on('change', function (e) {//选择平均后的时间
    var date = $(e.target).val();
    changeMsg.eTime = moment(date).format('YYYY-MM-DD');
    if (changeMsg.rms == 'a') {
        changeMsg.dates = [];
        var s = moment($('#sTime-d').val());
        var e = moment($('#eTime').val());
        while (true) {
            changeMsg.dates.push(s.format('YYYY-MM-DD'));
            if (s.add(1, 'd').isAfter(e)) {
                break;
            }
        }
    }
   // console.log(date);
    app.tuodong = true;
    updata();
});

/*数据更新*/
function updata(t) {
    showTitleFun();
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

    if(changeMsg.qj1Id == -1){
        ajaxPost('/scenarino/findby_pathdate',{
            userId:userId,
            pathDate:$('#sTime-d').val()
        }).success(function (res) {
            if(res.status == 0){
                changeMsg.qj1Id = res.data.scenarinoId;
                ajaxPar();
            }else{
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }).error(function () {
            swal({
                title: '接口故障',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        })
    }else if(changeMsg.qj2Id == -1){
        ajaxPost('/scenarino/findby_pathdate',{
            userId:userId,
            pathDate:$('#sTime-d').val()
        }).success(function (res) {
            if(res.status == 0){
                changeMsg.qj2Id = res.data.scenarinoId;
                ajaxPar();
            }else{
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }).error(function () {
            swal({
                title: '接口故障',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        })
    }else{
        ajaxPar();
    }

    function ajaxPar() {
        var p1 = $.extend({
            scenarioId1: changeMsg.qj1Id,
            calcType: 'show'
        }, parameter);

        var p2 = $.extend({
            scenarioId1: changeMsg.qj2Id
        }, parameter);

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
        for (var i = 0; i < changeMsg.species.length; i++) {
            p1.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
            p2.GPserver_type.push(mappingSpecies[changeMsg.rms][changeMsg.species[i]]);
        }


        if (t == 'wind') {
            p1.showType = t;

            /*执行方法，进行左图添加*/
            bianji("1", 0, p1, changeMsg.showWind);
            /*执行方法，进行左图添加 end*/

            p2.calcType = changeMsg.calcType;
            p2.showType = t;
            //console.log('p2',$.extend({},p2),p2.showType);

            /*执行方法，进行右图添加*/
            bianji("1", 1, p2, changeMsg.showWind);
            /*执行方法，进行右图添加 end*/
        } else if (t) {
            for (var x = 0; x < changeMsg.showType.length; x++) {
                p2.calcType = changeMsg.calcType;
                p2.showType = changeMsg.showType[x];
                //console.log('p2',$.extend({},p2),p2.showType);

                /*执行方法，进行右图添加*/
                zmblockUI("#mapDiv1", "start");
                bianji("1", 1, p2);
                bianji_wanggepafang("1", 1, p2);
                /*执行方法，进行右图添加 end*/

            }
        } else {
            for (var i = 0; i < changeMsg.showType.length; i++) {
                p1.showType = changeMsg.showType[i];


                p2.calcType = changeMsg.calcType;
                p2.showType = changeMsg.showType[i];
                //console.log('p2',$.extend({},p2),p2.showType);

                if (i == 0) {
                    /*执行方法，进行左图添加*/

                    bianji("1", 0, p1);
                    if (app.tuodong) {
                        zmblockUI("#mapDiv0", "start");
                        bianji_wanggepafang("1", 0, p1);
                    }

                    /*执行方法，进行左图添加 end*/

                    /*执行方法，进行右图添加*/
                    bianji("1", 1, p2);

                    if (app.tuodong) {
                        zmblockUI("#mapDiv1", "start");
                        bianji_wanggepafang("1", 1, p2);
                    }
                    /*执行方法，进行右图添加 end*/
                } else {
                    /*执行方法，进行左图添加*/
                    bianji("1", 0, p1, changeMsg.showWind);

                    if (app.tuodong) {
                        zmblockUI("#mapDiv0", "start");
                        bianji_wanggepafang("1", 0, p1, changeMsg.showWind);
                    }
                    /*执行方法，进行左图添加 end*/

                    /*执行方法，进行右图添加*/
                    bianji("1", 1, p2, changeMsg.showWind);

                    if (app.tuodong) {
                        zmblockUI("#mapDiv1", "start");
                        bianji_wanggepafang("1", 1, p2, changeMsg.showWind);
                    }
                    /*执行方法，进行右图添加 end*/
                }

            }
        }
    }
}

function showTitleFun() {
    $('#showTitle span').empty();
    $('#showTitle span').css({"margin-right":"0px"});
    var timeStartFor=moment(changeMsg.sTimeD,"YYYY-MM-DD").format("YYYY-MM-DD");
    var stateFor=moment(changeMsg.sTimeD+changeMsg.sTimeH,"YYYY-MM-DDH").format("YYYY-MM-DD HH");
    var timeTwoFor=moment(changeMsg.sTimeD).format("YYYY-MM-DD")+"至"+moment(changeMsg.eTime).format("YYYY-MM-DD");

    // var timeTwoFor=moment(changeMsg.sTimeD+"-"+changeMsg.eTime,"YYYY-MM-DD-YYYY-MM-DD").format("YYYY-MM-DD-YYYY-MM-DD");
    $('#showTitle .specieName').html("<span class='titleTab'><i class='en-layout' style='font-size: 16px;'></i>"+"&nbsp;物种：</span>"+changeMsg.species).css({"margin-right":"40px"});
    $('#showTitle .spaceName').html("<span class='titleTab'><i class='en-flow-parallel' style='font-size: 16px;'></i>"+"&nbsp;空间分辨率：</span>"+(changeMsg.domain=='1'?'3KM':(changeMsg.domain=='2'?'9KM':'27km'))).css({"margin-right":"40px"});
    if(changeMsg.rms=='d'){
        $('#showTitle .timeName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+'逐日').css({"margin-right":"40px"});
        $('#showTitle .dateStartName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+timeStartFor).css({"margin-right":"40px"});
    }else  if(changeMsg.rms=='h'){
        $('#showTitle .timeName').html("<span  class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+'逐时').css({"margin-right":"40px"});
        $('#showTitle .dateStartName').html("<span  class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+stateFor).css({"margin-right":"40px"});
    }else if(changeMsg.rms=='a'){
        $('#showTitle .timeName').html("<span  class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+'平均').css({"margin-right":"40px"});
        $('#showTitle .dateEndName').html("<span  class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+timeTwoFor).css({"margin-right":"40px"});
    }
}
//easyui 添加
$(".upDownBtn").append("<i class='en-arrow-up7'></i>")
$(".toolAll").hide();
$(".upDownBtn").click(function(){
    if($(".upDownBtn").text()=="收起"){
        $(".upDownBtn").text("更多搜索条件");
        $(".toolAll").hide();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-down8'></i>")
        $(".upDownBtn i").attr("class","en-arrow-down8")
        //headerH=$(".cloudui .searchT").height();
        //$(".charContent").css({"top":headerH+"px"})
    }else{
        $(".upDownBtn").text("收起");
        $(".toolAll").show();
        //headerH=$(".cloudui .searchT").height();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-up7'></i>");
        //$(".charContent").css({"top":headerH+"px"});
    }
})
$(".cloudui .verticalCon .ibox-content .searchT .upDown").hover(function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #0275D8"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #0275D8"});
},function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #d9d9d9"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #d9d9d9"});
})
// $(".charContent").slimScroll({
//     height: '100%'
// })
