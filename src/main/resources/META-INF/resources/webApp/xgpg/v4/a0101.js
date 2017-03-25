var gp32 = "http://192.168.1.132:6080/arcgis/rest/services/FactorToResult/GPServer/FTR";
var gp33 = "http://192.168.1.133:6080/arcgis/rest/services/ceshi/GPServer/ceshi1";


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
	function(Map, Geoprocessor,ImageParameters,DynamicLayerInfo,RasterDataSource,TableDataSource
			,LayerDataSource,FeatureLayer,GraphicsLayer,LayerDrawingOptions,SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint,Point,Extent,SimpleRenderer, Graphic, esriLang,
	        Color, array, number, domStyle, TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference) {
		
		dong.gaodeLayer = gaodeLayer;
		dong.Geoprocessor = Geoprocessor;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.FeatureSet = FeatureSet;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		
		esri.config.defaults.io.proxyUrl = ArcGisUrl+"/Java/proxy.jsp";
    	esri.config.defaults.io.alwaysUseProxy = false;
		
		app.mapList = new Array();
		app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
		app.stlayerList = new Array();//加载卫星图
		app.labellayerList = new Array();//加载标注图
		
//		app.mapExtent = new esri.geometry.Extent({ 
//	           "xmin":7184564.28421679, 
//	           "ymin":-234108.71524707237, 
//	           "xmax":16547794.50103525, 
//	           "ymax":7964832.6867318945, 
//	           "spatialReference":{"wkid":3857}
//	         });
		
		for(var i = 0;i<2;i++){
			var map = new Map("mapDiv"+i, {
//				extent:app.mapExtent,
				logo:false,
		        center: [stat.cPointx, stat.cPointy],
		        minZoom:4,
		        maxZoom:13,
		        zoom: 4
			});
//			map.setExtent(app.mapExtent);
			
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
				for(var i = 0;i<app.mapList.length;i++){
					if(i != source){
						app.mapList[i].setExtent(event.extent);
					}
				}
				$("#info").html("xmax="+event.extent.xmax+"  xmin="+event.extent.xmin+"  ymax="+event.extent.ymax+"  ymin="+event.extent.ymin);
			}else{
				source = -1;
			}
		});
		app.mapList[1].on("extent-change",function(event){
			if(source == -1){
				source = 1;
				for(var i = 0;i<app.mapList.length;i++){
					if(i != source){
						app.mapList[i].setExtent(event.extent);
					}
				}
				
			}else{
				source = -1;
			}
		});
		
		app.mapList[0].on("mouse-move", showCoordinates);
		app.mapList[0].on("mouse-drag", showCoordinates);
		
//		$("#info").html("xmax="+event.extent.xmax+"  xmin="+event.extent.xmin+"  ymax="+event.extent.ymax+"  ymin="+event.extent.ymin);
//		
//		event.extent.xmin;
//		event.extent.ymin;//左下
//		
//		event.extent.xmax;
//		event.extent.ymax;//右上
		
		
		function showCoordinates(event) {
//	        var mp = esri.geometry.webMercatorToGeographic(evt.mapPoint);
	        dojo.byId("Point").innerHTML = event.mapPoint.x.toFixed(3) + ", " + event.mapPoint.y.toFixed(3);
	    	
	    	$("#scale").html("scale="+app.mapList[0].getScale());
	    	$("#Zoom").html("Zoom="+app.mapList[0].getZoom());
	    	$("#Level").html("Level="+app.mapList[0].getLevel());
	    }
});

function bianji(){
	var myDate = new Date();
	var v1 = myDate.getTime();
	var features = [];
	
	
	$.get('data2.json', function (data) {
		$.each(data, function(i, col) {
			var point = new dong.Point(col.x, col.y, new dong.SpatialReference({ wkid: 3857 }));
			var attr = {};
	        attr["FID"] = i;
	        attr["dqvalue"] = col.v;
//	        console.log(col.x+"---"+col.y);
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
	    
		app.gp = new esri.tasks.Geoprocessor(gp33);
		var parms = {
				"p1" : featureset,
				"p2" : "dqvalue",
				"p3" : "Spherical #",
				"p4" : "3000",
				"p5" : "VARIABLE 12",
				"o1" : "out_raster_layer"//raster_Layer
			};
		app.gp.submitJob(parms, function(jobInfo){
			app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "o1");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
			//需要判断一下是否已经添加过图层，先移除，再添加
			var out_raster_layer = app.mapList[0].getLayer('out_raster_layer');
		    if(out_raster_layer){
		    	app.mapList[0].removeLayer(out_raster_layer);
		    }
		    app.mapList[0].addLayer(app.gpResultLayer);
			
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
		    dojo.byId("info").innerHTML = jobstatus;
		}, function(error){
			dojo.byId("info").innerHTML = "图一"+error;//错误执行方法
		});
		
	});
	
//	console.log(JSON.stringify(cha_json));
//	console.log(JSON.stringify(xyv));
	
	
	
//	var xmax=13292188.592313899;
//	var xmin=12121784.815211596;
//	var ymax=5108533.813772183;
//	var ymin=4252439.096978439;
//	var hangjiege = (xmax-xmin)/20;
//	var liejiege = (ymax-ymin)/20;
//	var k = 1;
//	var yy = ymin;
//	for(var i=1;i<=21;i++){
//		var xx = xmin;
//		for(var j=1;j<=21;j++){
//			
//			var point = new dong.Point(xx, yy, new dong.SpatialReference({ wkid: 3857 }));
//			k++;
//			var attr = {};
//	        attr["FID"] = k;
//	        attr["dqvalue"] = Math.random()*300.123456;
//	          
//			var graphic = new dong.Graphic(point);
//			graphic.setAttributes(attr);
//			features.push(graphic);
//			xx = xmin+(hangjiege*j);
//		}
//		yy = ymin+(liejiege*i);
//	}
//	var featureset = new dong.FeatureSet();
//	featureset.fields = [{
//		"name": "dqvalue",
//		"type": "esriFieldTypeSingle",
//		"alias": "dqvalue"
//	},{
//		"name": "FID",
//		"type": "esriFieldTypeOID",
//		"alias": "FID"
//	}];
//	featureset.fieldAliases = {"dqvalue" : "dqvalue","FID":"FID"};
//	featureset.spatialReference =  new dong.SpatialReference({ wkid: 3857 });
//    featureset.features = features;
//    featureset.exceededTransferLimit = false;
//    
//	app.gp = new esri.tasks.Geoprocessor(gp32);
//	var parms = {
//			"p1" : featureset,
//			"p2" : "dqvalue",
//			"p3" : "Spherical #",
//			"p4" : "3000",
//			"p5" : "VARIABLE 12",
//			"o1" : "out_raster_layer"//raster_Layer
//		};
//	app.gp.submitJob(parms, function(jobInfo){
//		app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "o1");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
//		//需要判断一下是否已经添加过图层，先移除，再添加
//		var out_raster_layer = app.mapList[0].getLayer('out_raster_layer');
//	    if(out_raster_layer){
//	    	app.mapList[0].removeLayer(out_raster_layer);
//	    }
//	    app.mapList[0].addLayer(app.gpResultLayer);
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
//	    dojo.byId("info").innerHTML = jobstatus;
//	}, function(error){
//		dojo.byId("info").innerHTML = "图一"+error;//错误执行方法
//	});
}

