var app = {};
var dong = {};
var ip = "192.168.1.132";
var url = "http://"+ip+":6080/arcgis/rest/services/china_ampc/MapServer";//基础底图
dojoConfig = {  
        parseOnLoad: true,  
        packages: [{  
            name: 'tdlib',  
            location: "/js/tdlib"  
        }]  
    }; 

require(["esri/map",
         "esri/tasks/Geoprocessor",
	     "tdlib/TDTLayer",  
	    "tdlib/TDTAnnoLayer",  
	    "esri/layers/FeatureLayer",  
	    "esri/geometry/Point",  
	    "esri/symbols/SimpleFillSymbol",  
	    "esri/symbols/SimpleLineSymbol",  
	    "dojo/_base/Color",  
	    "dojo/domReady!"],  
function (Map,  
		Geoprocessor,
     TDTLayer,  
     TDTAnnoLayer,  
     FeatureLayer,  
     Point,  
     SimpleFillSymbol,  
     SimpleLineSymbol,  
     Color  
){  
	app.map = new Map("mapDiv1", { logo: false });  
	var basemap = new TDTLayer(); 
	app.map.addLayer(basemap); 
		
	var annolayer = new TDTAnnoLayer();
	app.map.addLayer(annolayer);
		
//		var layer = new esri.layers.ArcGISDynamicMapServiceLayer(url);//创建动态地图
//		map.addLayer(layer);//加载底图
  
//		var pro = new FeatureLayer("http://localhost:6080/arcgis/rest/services/chinawgs84/MapServer/1");  
//		map.addLayer(pro);  
  
	var pt = new Point(103.847, 36.0473);  
	app.map.centerAndZoom(pt, 3);  
});


function bianji1(){
	var myDate = new Date();
	var v1 = myDate.getTime();
	
	app.gp = new esri.tasks.Geoprocessor("http://"+ip+":6080/arcgis/rest/services/ampcnc/GPServer/ampc");
	var parms = {
//			"input_file" : "{'url': 'http://192.168.1.154:8082/ampc/ncfile/meic_China36_2016255_new.nc'}",
			"input_file" : "{'url': 'http://192.168.1.132:8080/nc/Hourly.Combine.Surf.Column.2017052'}",
			"variable" : "NO2",
			"x_dimension" : "COL",
			"y_dimension" : "ROW",
			"out_la" : "raster_Layer"//raster_Layer
		};
	app.gp.submitJob(parms, function(jobInfo){
		app.gpResultLayer = app.gp.getResultImageLayer(jobInfo.jobId, "out_la");//这里的名字是跟着返回图层的变量名走的，不一样的话是不出图的
		//需要判断一下是否已经添加过图层，先移除，再添加
		var out_raster_layer = app.map.getLayer('out_raster_layer');
	    if(out_raster_layer){
	    	app.map.removeLayer(out_raster_layer);
	    }
		app.map.addLayer(app.gpResultLayer);
		
		var myDate2 = new Date();
		var v2 = myDate2.getTime();
		console.log(v2-v1);
		
	}, gpJobStatus, gpJobFailed);
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