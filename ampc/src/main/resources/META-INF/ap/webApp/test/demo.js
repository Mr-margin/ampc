//通用属性
var stat = {};
//中心点坐标
stat.cPointx=108;
stat.cPointy=35;
var app = {};
var dong = {};
require(
	[
	 	"esri/map",
	 	"esri/layers/FeatureLayer",
	 	"esri/layers/GraphicsLayer",
	 	"esri/layers/ArcGISDynamicMapServiceLayer",
	 	"esri/symbols/SimpleFillSymbol",
	 	"esri/symbols/SimpleLineSymbol",
	 	"esri/symbols/SimpleMarkerSymbol",
	 	"esri/renderers/ClassBreaksRenderer",
	 	"esri/geometry/Point",
	 	"esri/geometry/Extent",
        "esri/renderers/SimpleRenderer",
        "esri/graphic",
        "dojo/_base/Color",
        "dojo/dom-style",
        "esri/tasks/FeatureSet",
        "esri/SpatialReference",
        "tdlib/gaodeLayer",
        "esri/InfoTemplate",
        "esri/tasks/query",
        "dojo/number",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom", 
        "esri/dijit/Legend", 
        "esri/dijit/OverviewMap", 
        "esri/layers/DynamicLayerInfo",
        "esri/layers/LayerDataSource",
        "esri/layers/RasterDataSource",
        "esri/renderers/UniqueValueRenderer",
        "esri/tasks/Geoprocessor",
        "esri/tasks/RasterData",
        "dojo/domReady!"
	],
	function(Map,FeatureLayer,GraphicsLayer,ArcGISDynamicMapServiceLayer,SimpleFillSymbol,SimpleLineSymbol,SimpleMarkerSymbol,ClassBreaksRenderer,Point,Extent,SimpleRenderer,
			Graphic,Color,style,FeatureSet,SpatialReference,gaodeLayer,InfoTemplate,query,number,arr,domConstruct,dom,Legend,OverviewMap,DynamicLayerInfo,
			LayerDataSource,RasterDataSource,UniqueValueRenderer, Geoprocessor, RasterData) {
		dong.gaodeLayer = gaodeLayer;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		dong.SimpleLineSymbol = SimpleLineSymbol ;
		dong.FeatureLayer = FeatureLayer ;
		dong.SimpleRenderer = SimpleRenderer ;
		dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;
		dong.SimpleFillSymbol = SimpleFillSymbol;
		dong.Color = Color;
		dong.ClassBreaksRenderer = ClassBreaksRenderer;
		dong.UniqueValueRenderer = UniqueValueRenderer ;
		dong.InfoTemplate = InfoTemplate;
		dong.Query = query;
		dong.Extent = Extent;
		dong.number = number;
		dong.arr = arr;
		dong.domConstruct = domConstruct;
		dong.dom = dom;
		dong.Legend = Legend;
		dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
		dong.OverviewMap = OverviewMap;
		dong.LayerDataSource = LayerDataSource;
		dong.RasterDataSource = RasterDataSource;
		dong.DynamicLayerInfo = DynamicLayerInfo;
		dong.Geoprocessor = Geoprocessor;
		dong.RasterData = RasterData;
		/*****************************************************/
		app.map = new Map("mapDiv", {
			logo:false,
			basemap: "osm",//osm   topo
	        center: [stat.cPointx, stat.cPointy],
	        minZoom:4,
	        maxZoom:13,
	        zoom: 4,
	        sliderPosition: 'bottom-right',
	        showAttribution:false,//右下的gisNeu (logo左侧)
		});
		app.baselayerList = new dong.gaodeLayer();
		app.stlayerList = new dong.gaodeLayer({layertype: "st"});
		app.labellayerList = new dong.gaodeLayer({layertype: "label"});
		app.map.addLayer(app.baselayerList);//添加高德地图到map容器
		
		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
		app.map.addLayer(app.gLyr);
	    /*************************动态服务**********************************/
	    //app.dynamicData = new dong.ArcGISDynamicMapServiceLayer("http://192.168.1.132:6080/arcgis/rest/services/polist/rs/MapServer");
		//app.map.addLayer(app.dynamicData);
		
//		app.dynamicData.on("load",grid);
		app.map.on("load",gp_server);
});

function shape () {
	
	if ($('input[name="shape"]:checked').val() == "1") {
		
	} else if ($('input[name="shape"]:checked').val() == "2") {
		
	} else if ($('input[name="shape"]:checked').val() == "3") {
		grid();
		$("#show_shape").modal('hide');
	}
}

/**
 * GP服务调用，两个栅格文件想加，返回栅格数据集
 */
function gp_server(){
	var myDate = new Date();
	var v1 = myDate.getTime();
	
	app.gp = new dong.Geoprocessor("http://192.168.1.132:6080/arcgis/rest/services/add/GPServer/add");
	
	var a1 = new dong.RasterData();
	a1.format = "tiff";
	a1.url = "http://192.168.1.132:8080/arcgis_js_api/reuslt1.tiff";
	
	var a2 = new dong.RasterData();
	a2.format = "tiff";
	a2.url = "http://192.168.1.132:8080/arcgis_js_api/reuslt.tiff";
	
	var parms = {
			"a1" : a1,
			"a2" : a2,
			"f1" : "out_raster_layer"
		};
	app.gp.submitJob(parms, function(jobInfo){
		app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "f1");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
		
		//需要判断一下是否已经添加过图层，先移除，再添加
		var out_raster_layer = app.map.getLayer('out_raster_layer');
	    if(out_raster_layer){
	    	app.map.removeLayer(out_raster_layer);
	    }
	    app.map.addLayer(app.gpResultLayer);
		
		var myDate2 = new Date();
		var v2 = myDate2.getTime();
		console.log(v2-v1);
		
	}, function(jobinfo){
		var jobstatus = '';
	    switch (jobinfo.jobStatus) {
	      case 'esriJobSubmitted':
	        jobstatus = '图一正在提交...';
	        break;
	      case 'esriJobExecuting':
	        jobstatus = '图一处理中...';
	        break;
	      case 'esriJobSucceeded':
	    	jobstatus = '图一处理完成...';
	        break;
	    }
	    console.log(jobstatus);
	}, function(error){
		console.log("图一"+error);
	});
}




//非栅格
function grid(){
	app.dynamicData.show();
	var dynamicLayerInfos=app.dynamicData.createDynamicLayerInfosFromLayerInfos();
	var dynamicLayerInfo = new dong.DynamicLayerInfo();
	dynamicLayerInfo.id = 996;
	dynamicLayerInfo.defaultVisibility = false;
	dynamicLayerInfo.name = "reuslt";
	var dataSource = new dong.RasterDataSource();
	dataSource.workspaceId = "workspace";
	dataSource.dataSourceName = "reuslt.tiff";
	var layerSource = new dong.LayerDataSource();
	layerSource.dataSource = dataSource;
	dynamicLayerInfo.source = layerSource;
	dynamicLayerInfos.push(dynamicLayerInfo);
	app.dynamicData.setDynamicLayerInfos(dynamicLayerInfos);
	app.dynamicData.setVisibleLayers([996]);
}

