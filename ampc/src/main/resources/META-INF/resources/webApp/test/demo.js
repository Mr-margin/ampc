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
        "esri/layers/TableDataSource",
        "esri/layers/LayerDataSource",
        "esri/layers/RasterDataSource",
        "esri/layers/DynamicLayerInfo",
        
        "esri/layers/LayerDrawingOptions",
        
        "esri/renderers/UniqueValueRenderer",
        "dojo/domReady!"

	],
	function(Map,FeatureLayer,GraphicsLayer,ArcGISDynamicMapServiceLayer,SimpleFillSymbol,SimpleLineSymbol,SimpleMarkerSymbol,ClassBreaksRenderer,Point,Extent,SimpleRenderer,
			Graphic,Color,style,FeatureSet,SpatialReference,gaodeLayer,InfoTemplate,query,number,arr,domConstruct,dom,Legend,OverviewMap,TableDataSource,LayerDataSource,
			DynamicLayerInfo,LayerDrawingOptions,RasterDataSource,UniqueValueRenderer) {
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
		dong.TableDataSource = TableDataSource;
		dong.LayerDataSource = LayerDataSource;
		dong.RasterDataSource = RasterDataSource;
		dong.DynamicLayerInfo = DynamicLayerInfo;
		dong.LayerDrawingOptions = LayerDrawingOptions;
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
	    app.dynamicData = new dong.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/rs/MapServer");
		app.map.addLayer(app.dynamicData);
	    app.dynamicData.hide();
	    
	    // 添加鹰眼 
        var overviewMapDijit = new esri.dijit.OverviewMap({  
          map: app.map,   // 必要的  
          visible: true,  // 初始化可见，默认为false  
          baseLayer:new esri.layers.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer"),
          attachTo: "top-left",   // 默认右上角  
          width: 150, // 默认值是地图高度的 1/4th  
          height: 150, // 默认值是地图高度的 1/4th   
          opacity: .40,    // 透明度 默认0.5  
          maximizeButton: true,   // 最大化,最小化按钮，默认false  
          expandFactor: 3,    //概览地图和总览图上显示的程度矩形的大小之间的比例。默认值是2，这意味着概览地图将至少是两倍的大小的程度矩形。  
          color: "red"    // 默认颜色为#000000  
        });  
        overviewMapDijit.startup();   // 开启
});

function shape () {
	
	if ($('input[name="shape"]:checked').val() == "1") {
		
	} else if ($('input[name="shape"]:checked').val() == "2") {
		
	} else if ($('input[name="shape"]:checked').val() == "3") {
		grid();
		$("#show_shape").modal('hide');
	}
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
	dataSource.workspaceId = "MyRasterWorkspaceID";
	dataSource.dataSourceName = "reuslt.tiff";
	var layerSource = new dong.LayerDataSource();
	layerSource.dataSource = dataSource;
	dynamicLayerInfo.source = layerSource;
	dynamicLayerInfos.push(dynamicLayerInfo);
	app.dynamicData.setDynamicLayerInfos(dynamicLayerInfos);
	app.dynamicData.setVisibleLayers([996]);
}

