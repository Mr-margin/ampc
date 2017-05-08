var app = {};
var dong = {};
var ip = "192.168.1.132";
var url = "http://"+ip+":6080/arcgis/rest/services/china_ampc/MapServer";//基础底图

var dojoConfig = {
	async: true,
    parseOnLoad: true,  
    packages: [{  
        name: 'tdlib',  
        location: "/js/tdlib"  
    }]  
}; 

require(["esri/map", "tdlib/gaodeLayer", "dojo/domReady!"],  function (Map){  
	app.map = new Map("mapDiv1", { 
        center: [116, 28],
        zoom: 5
	});
	
	app.baselayer = new gaodeLayer();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
    //var baselayer = new gaodeLayer({layertype: "st"});//加载卫星图
    //var baselayer = new gaodeLayer({layertype: "label"});//加载标注图
	app.map.addLayer(app.baselayer);//添加高德地图到map容器
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




function bianji(){
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
}