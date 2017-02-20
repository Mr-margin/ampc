var ip = "192.168.1.142";
var url = "http://"+ip+":6080/arcgis/rest/services/china_j/MapServer";//基础底图
var gp_ampc = "http://"+ip+":6080/arcgis/rest/services/ampcNC/GPServer/ampcNC";

//通用属性
var stat = {};
//地图范围
stat.maxScale=8017.530719061126;
stat.minScale=16419902.912621401;
stat.scale=4104975.7281553536;
//中心点坐标
stat.cPointx=115.955;
stat.cPointy=39.107;

var mod5={};
//画高亮矩形样式
mod5.rect_symbol={
		color:[0, 112,255, 0.5],
		linecolor:[255, 255, 0],
		linewidth:3
}
//选中区域样式
mod5.forcusarea={
		color:[0, 112,255, 0.0],
		linecolor:[25,8,155],
		linewidth:3
};
//默认市级区域样式
mod5.mrarea={
		color:[0, 0,0, 0.0],
		linecolor:[25,8,155],
		linewidth:1
};
mod5.scale=8209951.456310134;


var app = {};
var dong = {};
require(
	[
	 	"esri/map", 
	 	"esri/tasks/Geoprocessor",
	 	"esri/layers/ImageParameters",
	 	"esri/layers/DynamicLayerInfo",
	 	"esri/layers/RasterDataSource",
	 	"esri/layers/TableDataSource",
	 	"esri/layers/LayerDataSource",
	 	"esri/layers/FeatureLayer",
	 	"esri/layers/GraphicsLayer",
	 	"esri/layers/LayerDrawingOptions",
	 	"esri/symbols/SimpleFillSymbol", 
	 	"esri/symbols/SimpleLineSymbol", 
	 	"esri/symbols/SimpleMarkerSymbol", 
	 	"esri/geometry/Multipoint", 
	 	"esri/geometry/Point", 
        "esri/renderers/SimpleRenderer", 
        "esri/graphic", 
        "esri/lang",
        "dojo/_base/Color", 
        "dojo/_base/array", 
        "dojo/number", 
        "dojo/dom-style", 
        "dijit/TooltipDialog", 
        "dijit/popup", 
        "dojox/widget/ColorPicker", 
        "dojo/domReady!"
	], 
	function(Map, Geoprocessor,ImageParameters,DynamicLayerInfo,RasterDataSource,TableDataSource
			,LayerDataSource,FeatureLayer,GraphicsLayer,LayerDrawingOptions,SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint,Point,SimpleRenderer, Graphic, esriLang,
	        Color, array, number, domStyle, TooltipDialog, dijitPopup, ColorPicker) {
		dong.Graphic = Graphic;
		dong.Geoprocessor = Geoprocessor;
		dong.ImageParameters = ImageParameters;
		
		app.map1 = new Map("mapDiv1",{slider: false, logo:false, scale:mod5.scale, center:[stat.cPointx,stat.cPointy], maxScale:stat.maxScale,minScale:stat.minScale});
		app.layer = new esri.layers.ArcGISDynamicMapServiceLayer(url);//创建动态地图
		app.map1.addLayer(app.layer);//加载底图
		app.map1.on("load", function() {
//		    app.map1.disablePan();//需要鼠标平移
//		    app.map1.disableRubberBandZoom();//启用时,用户可以画一个边界框的放大或缩小地图使用鼠标
//		    app.map1.disableScrollWheelZoom();//取消滚轮缩放
//		    app.map1.disableDoubleClickZoom();//取消双击放大
//		    app.map1.disableShiftDoubleClickZoom();//shift+双击或者拉框操作
//		    app.map1.disableKeyboardNavigation();//键盘操作
//		    app.gp = new dong.Geoprocessor("http://192.168.1.38:6080/arcgis/rest/services/MakeNetCDFRasterLayer/GPServer/创建 NetCDF 栅格图层");
		    app.gp = new dong.Geoprocessor(gp_ampc);//
		    
//		    app.fl1 = new FeatureLayer(hebei_shi_url, {
//				//maxAllowableOffset: app.map1.extent.getWidth() / app.map1.width,
//		        mode: FeatureLayer.MODE_SNAPSHOT,
//		        outFields: ["NAME"],
//		        visible: true
//			});
//			app.fl1.setRenderer(new SimpleRenderer(app.touming));
//			app.map1.addLayer(app.fl1);
//			
//			app.fl1.on("click", function(evt) {
//				app.map1.graphics.clear();
//				var xzGraphic1 = new Graphic(evt.graphic.geometry,app.symbol);
//				app.map1.graphics.add(xzGraphic1);
//				
//				app.map2.graphics.clear();
//				var xzGraphic2 = new Graphic(evt.graphic.geometry,app.symbol);
//				app.map2.graphics.add(xzGraphic2);
//			});
			app.map1.on("mouse-move", showCoordinates);
  			app.map1.on("mouse-drag", showCoordinates);

		});
        
        // 鼠标选中后图层二次高亮的样式
		app.highlightSymbol = new SimpleFillSymbol(
			SimpleFillSymbol.STYLE_SOLID,
			new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(mod5.rect_symbol.linecolor),mod5.rect_symbol.linewidth), 
			new Color(mod5.rect_symbol.color)
		);
		
		app.symbol = new SimpleFillSymbol(
			SimpleFillSymbol.STYLE_SOLID,
			new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(mod5.forcusarea.linecolor), mod5.forcusarea.linewidth), 
			new Color(mod5.forcusarea.color)
		);
		
        app.touming = new SimpleFillSymbol(
			SimpleFillSymbol.STYLE_SOLID,
			new SimpleLineSymbol(SimpleLineSymbol.STYLE_SOLID, new Color(mod5.mrarea.linecolor),mod5.mrarea.linewidth ), 
			new Color(mod5.mrarea.color)
		);
		
		function showCoordinates(evt) {
	        var mp = esri.geometry.webMercatorToGeographic(evt.mapPoint);
	        dojo.byId("info").innerHTML = evt.mapPoint.x.toFixed(3) + ", " + evt.mapPoint.y.toFixed(3);
	    }

});