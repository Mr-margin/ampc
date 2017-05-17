var stat = {cPointx : 106, cPointy : 35}, app = {}, dong = {};
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
        "dijit/popup", "dojox/widget/ColorPicker", "esri/layers/RasterLayer", "tdlib/gaodeLayer", "esri/tasks/FeatureSet", "esri/SpatialReference", "dojo/domReady!"
	], 
	function(Map, Geoprocessor,ImageParameters,DynamicLayerInfo,RasterDataSource,TableDataSource,LayerDataSource,FeatureLayer,GraphicsLayer,LayerDrawingOptions,
			SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint,Point,Extent,SimpleRenderer, Graphic, esriLang,Color, array, number, domStyle, 
			TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference) {
		
		dong.gaodeLayer = gaodeLayer;
		dong.Geoprocessor = Geoprocessor;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.FeatureSet = FeatureSet;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		
		esri.config.defaults.io.proxyUrl = ArcGisUrl+"/Java/proxy.jsp";
//		esri.config.defaults.io.proxyUrl = "webApp/xgpg/v4/proxy.jsp";
//		esri.config.defaults.io.proxyUrl = "proxy.jsp";
//		esri.config.defaults.io.proxyUrl = "http://192.168.4.215:8082/ampc/proxy.jsp";
    	esri.config.defaults.io.alwaysUseProxy = false;
		
		app.mapList = new Array();
		app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
		app.stlayerList = new Array();//加载卫星图
		app.labellayerList = new Array();//加载标注图
		
		for(var i = 0;i<2;i++){
			var map = new Map("mapDiv"+i, {
				logo:false,
		        center: [stat.cPointx, stat.cPointy],
		        minZoom:4,
		        maxZoom:13,
		        zoom: 4
			});
			
			app.mapList.push(map);
			app.baselayerList[i] = new dong.gaodeLayer();
			app.stlayerList[i] = new dong.gaodeLayer({layertype: "st"});
			app.labellayerList[i] = new dong.gaodeLayer({layertype: "label"});
			app.mapList[i].addLayer(app.baselayerList[i]);//添加高德地图到map容器
			app.mapList[i].addLayers([app.baselayerList[i]]);//添加高德地图到map容器
		}
		
		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
		app.mapList[0].addLayer(app.gLyr);
		
		//多个地图互相联动效果
		var source = -1;
		app.mapList[0].on("extent-change",function(event){
			if(source == -1){
				source = 0;
				app.mapList[1].setExtent(event.extent);
			}else{
				source = -1;
			}
		});
		
		app.mapList[1].on("extent-change",function(event){
			if(source == -1){
				source = 0;
				app.mapList[0].setExtent(event.extent);
			}else{
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
		
		bianji("1",0);
});



function bianji(type, g_num){
	var v1 = new Date().getTime();
	
	var GPserver_type = "co_daily";
	
	GPserver_type = "PM25";
	
	
	var GPserver_url = ArcGisServerUrl+"/arcgis/rest/services/ampc/"+GPserver_type+"/GPServer/"+GPserver_type;
	
	
	
	$.get('http://192.168.4.215:8082/ampc/data-d01.json', function (data) {
		
		var features = [];
		$.each(data, function(i, col) {
			var point = new dong.Point(col.x, col.y, new dong.SpatialReference({ wkid: 3857 }));
			var attr = {};
	        attr["FID"] = i;
	        attr["dqvalue"] = col.v;
			var graphic = new dong.Graphic(point);
			graphic.setAttributes(attr);
			features.push(graphic);
		});
		
		var featureset = new dong.FeatureSet();
		featureset.fields = [{
			"name": "dqvalue",
			"type": "esriFieldTypeSingle",
			"alias": "dqvalue"
		},{
			"name": "FID",
			"type": "esriFieldTypeOID",
			"alias": "FID"
		}];
		featureset.fieldAliases = {"dqvalue" : "dqvalue","FID":"FID"};
		featureset.spatialReference =  new dong.SpatialReference({ wkid: 3857 });
	    featureset.features = features;
	    featureset.exceededTransferLimit = false;
	    
		app.gp = new esri.tasks.Geoprocessor(GPserver_url);
		var parms = {
				"imp" : featureset,
				"out" : "out_raster_layer"
			};
		app.gp.submitJob(parms, function(jobInfo){
			var gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "out");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
			//需要判断一下是否已经添加过图层，先移除，再添加
			var out_raster_layer = app.mapList[g_num].getLayer('out_raster_layer');
		    if(out_raster_layer){
		    	app.mapList[g_num].removeLayer(out_raster_layer);
		    }
		    gpResultLayer.setOpacity(opacity);
		    app.mapList[g_num].addLayer(gpResultLayer);
			
			console.log(new Date().getTime()-v1);
		}, function(jobinfo){
			var jobstatus = '';
		    switch (jobinfo.jobStatus) {
		      case 'esriJobSubmitted':
		        jobstatus = type+'正在提交...';
		        break;
		      case 'esriJobExecuting':
		        jobstatus = type+'处理中...';
		        break;
		      case 'esriJobSucceeded':
		    	jobstatus = type+'处理完成...';
		        break;
		    }
		    console.log((new Date().getTime()-v1)+jobstatus);
		}, function(error){
			console.log(error);
		});
		
	});
	
}




////通用属性
//var stat = {};
////中心点坐标
//stat.cPointx=108;
//stat.cPointy=35;
//var app = {};
//var dong = {};
//require(
//	[
//	 	"esri/map",
//	 	"esri/layers/FeatureLayer",
//	 	"esri/layers/GraphicsLayer",
//	 	"esri/layers/ArcGISDynamicMapServiceLayer",
//	 	"esri/symbols/SimpleFillSymbol",
//	 	"esri/symbols/SimpleLineSymbol",
//	 	"esri/symbols/SimpleMarkerSymbol",
//	 	"esri/renderers/ClassBreaksRenderer",
//	 	"esri/geometry/Point",
//	 	"esri/geometry/Extent",
//        "esri/renderers/SimpleRenderer",
//        "esri/graphic",
//        "dojo/_base/Color",
//        "dojo/dom-style",
//        "esri/tasks/FeatureSet",
//        "esri/SpatialReference",
//        "tdlib/gaodeLayer",
//        "esri/InfoTemplate",
//        "esri/tasks/query",
//        "dojo/number",
//        "dojo/_base/array",
//        "dojo/dom-construct",
//        "dojo/dom", 
//        "esri/dijit/Legend", 
//        "esri/dijit/OverviewMap", 
//        "esri/layers/DynamicLayerInfo",
//        "esri/layers/LayerDataSource",
//        "esri/layers/RasterDataSource",
//        "esri/renderers/UniqueValueRenderer",
//        "esri/tasks/Geoprocessor",
//        "esri/tasks/RasterData",
//        "dojo/domReady!"
//	],
//	function(Map,FeatureLayer,GraphicsLayer,ArcGISDynamicMapServiceLayer,SimpleFillSymbol,SimpleLineSymbol,SimpleMarkerSymbol,ClassBreaksRenderer,Point,Extent,SimpleRenderer,
//			Graphic,Color,style,FeatureSet,SpatialReference,gaodeLayer,InfoTemplate,query,number,arr,domConstruct,dom,Legend,OverviewMap,DynamicLayerInfo,
//			LayerDataSource,RasterDataSource,UniqueValueRenderer, Geoprocessor, RasterData) {
//		dong.gaodeLayer = gaodeLayer;
//		dong.Graphic = Graphic;
//		dong.Point = Point;
//		dong.GraphicsLayer = GraphicsLayer;
//		dong.SpatialReference = SpatialReference;
//		dong.SimpleLineSymbol = SimpleLineSymbol ;
//		dong.FeatureLayer = FeatureLayer ;
//		dong.SimpleRenderer = SimpleRenderer ;
//		dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;
//		dong.SimpleFillSymbol = SimpleFillSymbol;
//		dong.Color = Color;
//		dong.ClassBreaksRenderer = ClassBreaksRenderer;
//		dong.UniqueValueRenderer = UniqueValueRenderer ;
//		dong.InfoTemplate = InfoTemplate;
//		dong.Query = query;
//		dong.Extent = Extent;
//		dong.number = number;
//		dong.arr = arr;
//		dong.domConstruct = domConstruct;
//		dong.dom = dom;
//		dong.Legend = Legend;
//		dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
//		dong.OverviewMap = OverviewMap;
//		dong.LayerDataSource = LayerDataSource;
//		dong.RasterDataSource = RasterDataSource;
//		dong.DynamicLayerInfo = DynamicLayerInfo;
//		dong.Geoprocessor = Geoprocessor;
//		dong.RasterData = RasterData;
//		/*****************************************************/
//		app.map = new Map("mapDiv", {
//			logo:false,
//			basemap: "osm",//osm   topo
//	        center: [stat.cPointx, stat.cPointy],
//	        minZoom:4,
//	        maxZoom:13,
//	        zoom: 4,
//	        sliderPosition: 'bottom-right',
//	        showAttribution:false,//右下的gisNeu (logo左侧)
//		});
//		app.baselayerList = new dong.gaodeLayer();
//		app.stlayerList = new dong.gaodeLayer({layertype: "st"});
//		app.labellayerList = new dong.gaodeLayer({layertype: "label"});
//		app.map.addLayer(app.baselayerList);//添加高德地图到map容器
//		
//		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
//		app.map.addLayer(app.gLyr);
//	    /*************************动态服务**********************************/
//	    //app.dynamicData = new dong.ArcGISDynamicMapServiceLayer("http://192.168.1.132:6080/arcgis/rest/services/polist/rs/MapServer");
//		//app.map.addLayer(app.dynamicData);
//		
////		app.dynamicData.on("load",grid);
//		app.map.on("load",gp_server);
//});
//
//function shape () {
//	
//	if ($('input[name="shape"]:checked').val() == "1") {
//		
//	} else if ($('input[name="shape"]:checked').val() == "2") {
//		
//	} else if ($('input[name="shape"]:checked').val() == "3") {
//		grid();
//		$("#show_shape").modal('hide');
//	}
//}
//
///**
// * GP服务调用，两个栅格文件想加，返回栅格数据集
// */
//function gp_server(){
//	var myDate = new Date();
//	var v1 = myDate.getTime();
//	
//	app.gp = new dong.Geoprocessor("http://192.168.1.132:6080/arcgis/rest/services/add/GPServer/add");
//	
//	var a1 = new dong.RasterData();
//	a1.format = "tiff";
//	a1.url = "http://192.168.1.132:8080/arcgis_js_api/reuslt1.tiff";
//	
//	var a2 = new dong.RasterData();
//	a2.format = "tiff";
//	a2.url = "http://192.168.1.132:8080/arcgis_js_api/reuslt.tiff";
//	
//	var parms = {
//			"a1" : a1,
//			"a2" : a2,
//			"f1" : "out_raster_layer"
//		};
//	app.gp.submitJob(parms, function(jobInfo){
//		app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "f1");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
//		
//		//需要判断一下是否已经添加过图层，先移除，再添加
//		var out_raster_layer = app.map.getLayer('out_raster_layer');
//	    if(out_raster_layer){
//	    	app.map.removeLayer(out_raster_layer);
//	    }
//	    app.map.addLayer(app.gpResultLayer);
//		
//		var myDate2 = new Date();
//		var v2 = myDate2.getTime();
//		console.log(v2-v1);
//		
//	}, function(jobinfo){
//		var jobstatus = '';
//	    switch (jobinfo.jobStatus) {
//	      case 'esriJobSubmitted':
//	        jobstatus = '图一正在提交...';
//	        break;
//	      case 'esriJobExecuting':
//	        jobstatus = '图一处理中...';
//	        break;
//	      case 'esriJobSucceeded':
//	    	jobstatus = '图一处理完成...';
//	        break;
//	    }
//	    console.log(jobstatus);
//	}, function(error){
//		console.log("图一"+error);
//	});
//}
//
//
//
//
////非栅格
//function grid(){
//	app.dynamicData.show();
//	var dynamicLayerInfos=app.dynamicData.createDynamicLayerInfosFromLayerInfos();
//	var dynamicLayerInfo = new dong.DynamicLayerInfo();
//	dynamicLayerInfo.id = 996;
//	dynamicLayerInfo.defaultVisibility = false;
//	dynamicLayerInfo.name = "reuslt";
//	var dataSource = new dong.RasterDataSource();
//	dataSource.workspaceId = "workspace";
//	dataSource.dataSourceName = "reuslt.tiff";
//	var layerSource = new dong.LayerDataSource();
//	layerSource.dataSource = dataSource;
//	dynamicLayerInfo.source = layerSource;
//	dynamicLayerInfos.push(dynamicLayerInfo);
//	app.dynamicData.setDynamicLayerInfos(dynamicLayerInfos);
//	app.dynamicData.setVisibleLayers([996]);
//}
//
