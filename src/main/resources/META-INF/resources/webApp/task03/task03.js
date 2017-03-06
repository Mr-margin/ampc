//$(function(){  
//    $("#checkall").click(function(){   
//        //第一种方法 全选全不选  
//        if(this.checked){   
//            $("input[name='check1']:checkbox").attr('checked',true);   
//        }else{   
//            $("input[name='check1']:checkbox").attr('checked',false);    
//        }  
//        //第二种方法 全选全不选   
//        //$('[name=check1]:checkbox').attr('checked',this.checked);//checked为true时为默认显示的状态   
//    });  
//    $("#checkrev").click(function(){  
//        //实现反选功能  
//        $('[name=check1]:checkbox').each(function(){  
//            this.checked=!this.checked;  
//        });  
//    });   
//}); 

//通用属性
var stat = {};
//中心点坐标
stat.cPointx=116;
stat.cPointy=28;
var app = {};
var dong = {};

var dojoConfig = {
	async: true,
    parseOnLoad: true,  
    packages: [{  
        name: 'tdlib',  
        location: "/js/tdlib"  
    }]
};
require(
	[
	 	"esri/map", 
	 	"esri/layers/FeatureLayer",
	 	"esri/layers/GraphicsLayer",
	 	"esri/symbols/SimpleFillSymbol", 
	 	"esri/symbols/SimpleLineSymbol", 
	 	"esri/symbols/SimpleMarkerSymbol", 
	 	"esri/geometry/Point", 
	 	"esri/geometry/Extent",
        "esri/renderers/SimpleRenderer", 
        "esri/graphic", 
        "dojo/_base/Color", 
        "dojo/dom-style", 
        "esri/tasks/FeatureSet",
        "esri/SpatialReference",
        "tdlib/gaodeLayer",
        "dojo/domReady!"
	], 
	function(Map, FeatureLayer,GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Point,Extent,SimpleRenderer, Graphic,
	        Color, domStyle, FeatureSet, SpatialReference, gaodeLayer) {
		dong.gaodeLayer = gaodeLayer;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		
		app.map = new Map("mapDiv", {
			logo:false,
	        center: [stat.cPointx, stat.cPointy],
	        minZoom:4,
	        maxZoom:13,
	        zoom: 4
		});
		app.baselayerList = new dong.gaodeLayer();
		app.stlayerList = new dong.gaodeLayer({layertype: "st"});
		app.labellayerList = new dong.gaodeLayer({layertype: "label"});
		app.map.addLayer(app.baselayerList);//添加高德地图到map容器
		app.map.addLayers([app.baselayerList]);//添加高德地图到map容器
		
		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
		app.map.addLayer(app.gLyr);
});




////通用属性
//var stat = {};
////地图范围
//stat.maxScale=36978595.474472;
//stat.minScale=72223.819286;
////中心点坐标
//stat.cPointx=116;
//stat.cPointy=28;
//
//
//var app = {};
//var dong = {};
//
//var dojoConfig = {
//	async: true,
//    parseOnLoad: true,  
//    packages: [{  
//        name: 'tdlib',  
//        location: "/js/tdlib"  
//    }]
//};
//require(
//	[
//	 	"esri/map", 
//	 	"esri/tasks/Geoprocessor",
//	 	"esri/layers/ImageParameters",
//	 	"esri/layers/DynamicLayerInfo",
//	 	"esri/layers/RasterDataSource",
//	 	"esri/layers/TableDataSource",
//	 	"esri/layers/LayerDataSource",
//	 	"esri/layers/FeatureLayer",
//	 	"esri/layers/GraphicsLayer",
//	 	"esri/layers/LayerDrawingOptions",
//	 	"esri/symbols/SimpleFillSymbol", 
//	 	"esri/symbols/SimpleLineSymbol", 
//	 	"esri/symbols/SimpleMarkerSymbol", 
//	 	"esri/geometry/Multipoint", 
//	 	"esri/geometry/Point", 
//	 	"esri/geometry/Extent",
//        "esri/renderers/SimpleRenderer", 
//        "esri/graphic", 
//        "esri/lang",
//        "dojo/_base/Color", 
//        "dojo/_base/array", 
//        "dojo/number", 
//        "dojo/dom-style", 
//        "dijit/TooltipDialog", 
//        "dijit/popup", 
//        "dojox/widget/ColorPicker", 
//        "esri/layers/RasterLayer",
//        "tdlib/gaodeLayer",
//        "esri/tasks/FeatureSet",
//        "esri/SpatialReference",
//        "dojo/domReady!"
//	], 
//	function(Map, Geoprocessor,ImageParameters,DynamicLayerInfo,RasterDataSource,TableDataSource
//			,LayerDataSource,FeatureLayer,GraphicsLayer,LayerDrawingOptions,SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint,Point,Extent,SimpleRenderer, Graphic, esriLang,
//	        Color, array, number, domStyle, TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference) {
//		
//		dong.gaodeLayer = gaodeLayer;
//		dong.Geoprocessor = Geoprocessor;
//		dong.Graphic = Graphic;
//		dong.Point = Point;
//		dong.FeatureSet = FeatureSet;
//		dong.GraphicsLayer = GraphicsLayer;
//		dong.SpatialReference = SpatialReference;
//		
////		esri.config.defaults.io.proxyUrl = "http://192.168.1.147:8091/Java/proxy.jsp";
////    	esri.config.defaults.io.alwaysUseProxy = false;
//		
//		app.mapList = new Array();
//		app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
//		app.stlayerList = new Array();//加载卫星图
//		app.labellayerList = new Array();//加载标注图
//		
//		for(var i = 0;i<1;i++){
//			var map = new Map("mapDiv", {
////				extent:app.mapExtent,
//				logo:false,
//		        center: [stat.cPointx, stat.cPointy],
//		        minZoom:4,
//		        maxZoom:13,
////		        minScale:stat.minScale,
////		        maxScale:stat.maxScale,
//		        zoom: 4
//			});
////			map.setExtent(app.mapExtent);
//			
//			app.mapList.push(map);
//			app.baselayerList[i] = new dong.gaodeLayer();
//			app.stlayerList[i] = new dong.gaodeLayer({layertype: "st"});
//			app.labellayerList[i] = new dong.gaodeLayer({layertype: "label"});
//			app.mapList[i].addLayer(app.baselayerList[i]);//添加高德地图到map容器
//			app.mapList[i].addLayers([app.baselayerList[i]]);//添加高德地图到map容器
//		}
//		
//		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
//		app.mapList[0].addLayer(app.gLyr);
//		
//});