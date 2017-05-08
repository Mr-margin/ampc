var ip = "192.168.1.132";
var url = "http://"+ip+":6080/arcgis/rest/services/china_ampc/MapServer";//基础底图
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
        "esri/layers/RasterLayer",
        "dojo/domReady!"
	], 
	function(Map, Geoprocessor,ImageParameters,DynamicLayerInfo,RasterDataSource,TableDataSource
			,LayerDataSource,FeatureLayer,GraphicsLayer,LayerDrawingOptions,SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint,Point,SimpleRenderer, Graphic, esriLang,
	        Color, array, number, domStyle, TooltipDialog, dijitPopup, ColorPicker, RasterLayer) {
		dong.Graphic = Graphic;
//		dong.Geoprocessor = Geoprocessor;
		dong.ImageParameters = ImageParameters;
		dong.RasterLayer = RasterLayer;
//		app.gp = new dong.Geoprocessor(gp_ampc);
//		app.gp1 = new dong.Geoprocessor(NetCDF);
		
//		app.map1 = new Map("mapDiv1",{slider: false, logo:false, scale:mod5.scale, center:[stat.cPointx,stat.cPointy], maxScale:stat.maxScale,minScale:stat.minScale});
		app.map1 = new Map("mapDiv1");
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
		    //
		    
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
	        dojo.byId("Point").innerHTML = evt.mapPoint.x.toFixed(3) + ", " + evt.mapPoint.y.toFixed(3);
	    	
	    	$("#scale").html("scale="+app.map1.getScale());
	    	$("#Zoom").html("Zoom="+app.map1.getZoom());
	    	$("#Level").html("Level="+app.map1.getLevel());
	    }

});

function bianji1(){
	var myDate = new Date();
	var v1 = myDate.getTime();
	
	app.gp = new esri.tasks.Geoprocessor("http://"+ip+":6080/arcgis/rest/services/ampcnc/GPServer/ampc");
	var parms = {
//			"input_file" : "{'url': 'http://192.168.1.154:8082/ampc/ncfile/meic_China36_2016255_new.nc'}",
//			"input_file" : "{'url': 'http://192.168.1.104:8082/ampc/ncfile/Hourly.Combine.Surf.Column.2017052'}",
			"input_file" : "{'url': 'http://192.168.1.132:8080/nc/Hourly.Combine.Surf.Column.2017052'}",
//			"input_file" : "D:/dynamic/meic_China36_2016255_new.nc",
			"variable" : "NO2",
			"x_dimension" : "COL",
			"y_dimension" : "ROW",
			"out_la" : "raster_Layer"//raster_Layer
		};
	app.gp.submitJob(parms, function(jobInfo){
		app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "out_la");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
		//需要判断一下是否已经添加过图层，先移除，再添加
		var out_raster_layer = app.map1.getLayer('out_raster_layer');
	    if(out_raster_layer){
	    	app.map2.removeLayer(out_raster_layer);
	    }
		app.map1.addLayer(app.gpResultLayer);
		
		var myDate2 = new Date();
		var v2 = myDate2.getTime();
		console.log(v2-v1);
		
	}, gpJobStatus, gpJobFailed);
	
	
	
//	app.gp.execute(parms, function(results, messages){
//		FileRasterSource rasterSource =null;
//		rasterSource = new FileRasterSource(rasterPath);
//		RasterLayer rasterLayer = new RasterLayer(rasterSource);
//        this.mapView.addLayer(rasterLayer);
//		var dataFile = new esri.tasks.DataFile();
//		dataFile.url = results[0].value.url;
		
//		var isRasterLayer = new dong.RasterLayer(results[0].value.url, {
//			  opacity: 1
//			 // pixelFilter: pixelFilter.hillshade,  //Class that draws hillshades  using a frequent-shader
//			 // drawMode: false,
//			 // drawType: "experimental-webgl"
//			});
		
//		var mi = new MapImage({
//		    'href': results[0].value.url
//		  });
//		esri/layers/MapImageLayer();
//		addImage(mapImage)
        
//		app.gpResultLayer = new esri.layers.ArcGISImageServiceLayer(results[0].value.url);
//		alert(results[0].value.url);
//		var isRasterLayer = new RasterLayer(results[0].value.url);
//		var hotspotLayer = new esri.layers.ArcGISImageServiceLayer(results[0].value.url);  
			
//		app.gpResultLayer = app.gp.getResultImageLayer("", "out_la");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
//		//需要判断一下是否已经添加过图层，先移除，再添加
////		var out_raster_layer = app.map1.getLayer('out_raster_layer');
////	    if(out_raster_layer){
////	    	app.map2.removeLayer(out_raster_layer);
////	    }
//		app.map1.addLayer(app.gpResultLayer);
//		
//		var myDate2 = new Date();
//		var v2 = myDate2.getTime();
//		console.log(v2-v1);
//		
//	}, gpJobFailed);
	
	
	
	
	
	
//	app.gp = new esri.tasks.Geoprocessor("http://"+ip+":6080/arcgis/rest/services/NetCDF1/GPServer/NetCDF");
//	var parms = {
//			"in_netCDF_file" : "{'url': 'http://192.168.1.154:8082/ampc/ncfile/Hourly.Combine.Surf.Column.2017052'}",
//			"variable" : "NO2",
//			"x_dimension" : "COL",
//			"y_dimension" : "ROW",
//			"out_raster_layer" : "NO2_Layer"//raster_Layer
//		};
//	app.gp.submitJob(parms, completeCallback, gpJobStatus, gpJobFailed);
	
	
//	app.gp = new esri.tasks.Geoprocessor("http://"+ip+":6080/arcgis/rest/services/dp/GPServer/NetCDF");
//	var parms = {
//			"in_netCDF_file" : "{'url': 'http://192.168.1.154:8082/ampc/ncfile/cngdp2010_31.nc'}",
//			"variable" : "dp",
//			"x_dimension" : "x",
//			"y_dimension" : "y",
//			"out_raster_layer" : "D:\dynamic\dp_Layer"
//		};
//	app.gp.submitJob(parms, completeCallback, gpJobStatus, gpJobFailed);
}
//放回正常方法
function completeCallback(jobInfo){
//	var jobId = jobInfo.jobId;//获取返回的临时空间ID
//	//创建栅格图层
	
	
//	var imageParameters = new esri.layers.ImageParameters();
//	imageParameters.format = "jpeg";
//	app.gp1.getResultImageLayer(jobInfo.jobId, "out_raster_layer",imageParameters, function(gpLayer){
//		  gpLayer.setOpacity(0.5);
//		  app.map1.addLayer(gpLayer);
//	});
//	dojo.byId("info").innerHTML = "";
}

function gpJobStatus(jobinfo){
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
    dojo.byId("info").innerHTML = jobstatus;
}
//错误执行方法
function gpJobFailed(error) {    
	dojo.byId("info").innerHTML = "图一"+error;
}
