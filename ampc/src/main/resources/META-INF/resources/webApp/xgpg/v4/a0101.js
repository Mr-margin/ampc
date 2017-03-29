var gp32 = "http://192.168.1.132:6080/arcgis/rest/services/FactorToResult/GPServer/FTR";
var gp33 = "http://192.168.1.133:6080/arcgis/rest/services/ceshi/GPServer/ceshi1";


/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">水平分布</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');

var ls = window.sessionStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;
if(!qjMsg){
	qjMsg = JSON.parse(ls.getItem('yaMsg'));
}else{
	ls.setItem('yaMsg',JSON.stringify(qjMsg));
}

var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if(!sceneInitialization){
	sceneInitialization = JSON.parse(ls.getItem('SI'));
}else{
	ls.setItem('SI',JSON.stringify(sceneInitialization));
}
console.log(JSON.stringify(sceneInitialization));

if(!sceneInitialization){
	sceneInittion();
}else{
	set_sce1_sce2();
}

/**
 * 初始化模态框显示
 */
function sceneInittion(){
	$("#task").html("");
	var paramsName = {};
	paramsName.userId = userId;
	console.log(JSON.stringify(paramsName));
	ajaxPost('/mission/find_All_mission',paramsName).success(function(res){
		console.log(JSON.stringify(res));
		if(res.status == 0){
			var task = "";
			$.each(res.data, function(k, vol) {
				
				if(sceneInitialization){
					if(sceneInitialization.taskID == vol.missionId){
						task += '<option value="'+vol.missionId+'" selected="selected">'+vol.missionName+'</option>';
					}else{
						task += '<option value="'+vol.missionId+'">'+vol.missionName+'</option>';
					}
				}else{
					task += '<option value="'+vol.missionId+'">'+vol.missionName+'</option>';
				}
			});
			$("#task").html(task);
			$("#Initialization").modal();//初始化模态框显示
			sceneTable();
		}
	});
}

/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable(){
	$("#sceneTableId").bootstrapTable('destroy');//销毁现有表格数据
	
	$("#sceneTableId").bootstrapTable({
		method : 'POST',
		url : '/ampc/scenarino/find_All_scenarino',
		dataType : "json",
		iconSize : "outline",
		clickToSelect : true,// 点击选中行
		pagination : false, // 在表格底部显示分页工具栏
		striped : true, // 使表格带有条纹
		queryParams : function(params) {
			var data = {};
			data.userId = userId;
			data.missionId = $("#task").val();
			return JSON.stringify({"token": "","data": data});
		},
		queryParamsType : "limit", // 参数格式,发送标准的RESTFul类型的参数请求
		silent : true, // 刷新事件必须设置
		contentType : "application/json", // 请求远程数据的内容类型。
		responseHandler: function (res) {
			if(res.status == 0){
				if(res.data.rows.length>0){
					
					if(sceneInitialization){
						if(sceneInitialization.data.length>0){
							
							$.each(res.data.rows, function(i, col) {
								$.each(sceneInitialization.data, function(k, vol) {
									if(col.scenarinoId == vol.scenarinoId){
										res.data.rows[i].state = true;
									}
								});
							});
						}
					}
					
					return res.data.rows;
				}
			}else if(res.status == 1000){
				swal(res.msg, '', 'error');
			}
		},
		onClickRow : function(row, $element) {
			$('.success').removeClass('success');
			$($element).addClass('success');
		},
		icons : {
			refresh : "glyphicon-repeat",
			toggle : "glyphicon-list-alt",
			columns : "glyphicon-list"
		},
		onLoadSuccess : function(data){
//			console.log(data);
		},
		onLoadError : function(){
			swal('连接错误', '', 'error');
		}
	});
}

/**
 * 保存选择的情景
 */
function save_scene(){
	var row = $('#sceneTableId').bootstrapTable('getSelections');//获取所有选中的情景数据
	if(row.length>0){
		var mag = {};
		mag.id = "sceneInitialization";
		mag.taskID = $("#task").val();
		var data = [];
		$.each(row, function(i, col) {
			data.push({"scenarinoId":col.scenarinoId,"scenarinoName":col.scenarinoName,"scenarinoStartDate":col.scenarinoStartDate,"scenarinoEndDate":col.scenarinoEndDate});
		});
		mag.data = data;
		vipspa.setMessage(mag);
		ls.setItem('SI',JSON.stringify(mag));
		console.log(data);
		setQjSelectBtn(data);
		sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
		$("#close_scene").click();
		//set_sce1_sce2();
	}
}
//超链接显示 模态框
function exchangeModal(){
	sceneInittion();
	$("#Initialization").modal();
}





/**
 * 设置情景一和情景二的下拉框内容
 */
function set_sce1_sce2(){
	if(sceneInitialization){
		$("#scene1").html("");
		$("#scene2").html("");
		
		var scene = "";
		$.each(sceneInitialization.data, function(i, col) {
			scene += '<option value="'+col.scenarinoId+'">'+col.scenarinoName+'</option>';
		});
		$("#scene1").html(scene);
		$("#scene2").html(scene);
	}
}









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
//	        dojo.byId("Point").innerHTML = event.mapPoint.x.toFixed(3) + ", " + event.mapPoint.y.toFixed(3);
//	    	$("#scale").html("scale="+app.mapList[0].getScale());
//	    	$("#Zoom").html("Zoom="+app.mapList[0].getZoom());
//	    	$("#Level").html("Level="+app.mapList[0].getLevel());
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


var speciesArr = {
	d:['PM25','PM10','O3_8_max','O3_1_max','O3_avg','SO2','NO2','CO','SO4','NO3','NH4','BC','OM','PMFINE'],
	p:['PM25','PM10','O3_8_max','O3_1_max','O3_avg','SO2','NO2','CO','SO4','NO3','NH4','BC','OM','PMFINE'],
	h:['PM25','PM10','O3','SO2','NO2','CO','SO4','NO3','NH4','BC','OM','PMFINE']
};


/*这段代码需要在初始化中*/
$('#species').empty();
for(var i=0;i<speciesArr.d.length;i++){
	$('#species').append($('<option>'+ speciesArr.d[i] +'</option>'))
}

/*添加情景选择按钮*/
function setQjSelectBtn(data){
	var s1,s2,e1,e2;
	$('#qjBtn1 .btn-group').empty();
	$('#qjBtn2 .btn-group').empty();
	for(var i=0;i<data.length;i++){
		var btn1 = $('<label class="btn btn-outline btn-success bgw"><input type="radio" name="qjBtn1"><span></span></label><br/>');
		var btn2 = $('<label class="btn btn-outline btn-success bgw"><input type="radio" name="qjBtn2"><span></span></label><br/>');
		btn1.attr('title',data[i].scenarinoName).find('input').attr('value',data[i].scenarinoId).attr('data-sDate',data[i].scenarinoStartDate).attr('data-eDate',data[i].scenarinoEndDate);
		btn1.find('span').html(data[i].scenarinoName);
		btn2.attr('title',data[i].scenarinoName).find('input').attr('value',data[i].scenarinoId).attr('data-sDate',data[i].scenarinoStartDate).attr('data-eDate',data[i].scenarinoEndDate);
		btn2.find('span').html(data[i].scenarinoName);
		if(i==0){
			btn1.addClass('active').find('input').attr('checked',true);
			btn2.addClass('disabled');
			s1 = data[i].scenarinoStartDate;
			e1 = data[i].scenarinoEndDate;
		}
		if(i==1){
			btn2.addClass('active').find('input').attr('checked',true);
			s2 = data[i].scenarinoStartDate;
			e2 = data[i].scenarinoEndDate;
		}
		$('#qjBtn1 .btn-group').append(btn1);
		$('#qjBtn2 .btn-group').append(btn2);
	}
	setDate(s1,e1,s2,e2,rmsType);
}

var rmsType = 'd';
var startTime,endTime,nowTime;//存储moment对象
/*设置日期下拉框*/
/*传入毫秒数*/
function setDate(s1,e1,s2,e2,type){
	$('#sTime-d').empty();
	$('#eTime').empty();
	s1 = moment(s1-0);
	s2 = moment(s2-0);
	e1 = moment(e1-0);
	e2 = moment(e2-0);
	if(type == 'h'){
		if(e1.isBefore(s2,'h')||e2.isBefore(s1,'h')){
			return;
		}

		if(s1.isBefore(s2,'h')){
			startTime = s2;
		}else{
			startTime = s1;
		}

		if(e1.isBefore(e2,'h')){
			endTime = e1;
		}else{
			endTime = e2;
		}
		nowTime = moment(startTime);
		var s = moment(startTime);
		var e = moment(endTime);
		while(true){
			$('#sTime-d').append($('<option>' +s.format('YYYY-MM-DD')+ '</option>'));
			console.log(s.format('YYYY-MM-DD'));
			if(e.isBefore(s.add(1,'d'))){
				return;
			}
		}
		s = moment(startTime);
		while(true){
			$('#sTime-h').append($('<option>' +s.format('HH')+ '</option>'));
			if('23' ==s.add(1,'h').format('HH')){
				return;
			}
		}
	}else{
		if(e1.isBefore(s2,'d')||e2.isBefore(s1,'d')){
			return;
		}

		if(s1.isBefore(s2,'d')){
			startTime = s2;
		}else{
			startTime = s1;
		}

		if(e1.isBefore(e2,'d')){
			endTime = e1;
		}else{
			endTime = e2;
		}

		nowTime = moment(startTime);
		var s = moment(startTime);
		var e = moment(endTime);
		while(true){
			$('#sTime-d').append($('<option>' +s.format('YYYY-MM-DD')+ '</option>'));
			$('#eTime').append($('<option>' +s.format('YYYY-MM-DD')+ '</option>'));
			console.log(s.format('YYYY-MM-DD'));
			if(e.isBefore(s.add(1,'d'))){
				return;
			}
		}
	}
}

/*顶部选择事件*/
$('#species').on('change',function(e){  //物种选择
	var species = $(e.target).val();
	console.log(species);
});

$('input[name=domain]').on('change',function(e){ //domaon选择
	var domain = $(e.target).val();
	console.log(domain);
});

$('input[name=rms]').on('change',function(e){ //时间分辨率选择
	var rms = $(e.target).val();
	rmsType = rms;
	console.log(rms);
	$('#species').empty();
	for(var i=0;i<speciesArr[rms].length;i++){
		$('#species').append($('<option>'+ speciesArr[rms][i] +'</option>'))
	}
	if(rms == 'd'){
		$('#sTime-h').addClass('disNone');
		$('#eTimeP').addClass('disNone');
	}else if(rms == 'h'){
		$('#sTime-h').removeClass('disNone');
		$('#eTimeP').addClass('disNone');
	}else{
		$('#sTime-h').addClass('disNone');
		$('#eTimeP').removeClass('disNone');
	}
});

$('input[name=showType]').on('change',function(e){ //地图图片类型
	var type = $(e.target).val();
	console.log(type);
})

$('#qjBtn1').on('change','input',function(e){//改变左侧情景
	var qjId = $(e.target).val();
	console.log(qjId)
	var index = $('input[name=qjBtn1]').index($(e.target));
	$('#qjBtn2 .disabled').removeClass('disabled');
	$('#qjBtn2 .active').removeClass('active');
	$('input[name=qjBtn2]').eq(index).parents('label').addClass('disabled');
	if(index == 0){
		$('#qjBtn2 label').eq(1).addClass('active').find('input').attr('checked',true)
	}else{
		$('#qjBtn2 label').eq(0).addClass('active').find('input').attr('checked',true)
	}

	var s1 = $(e.target).attr('data-sDate');
	var e1 = $(e.target).attr('data-eDate');

	var s2 = $('#qjBtn2 input:checked').attr('data-sDate');
	var e2 = $('#qjBtn2 input:checked').attr('data-eDate');



	setDate(s1,e1,s2,e2,rmsType);
});

$('#qjBtn2').on('change','input',function(e){//改变右侧情景
	var qjId = $(e.target).val();
	console.log(qjId)

	var s2 = $(e.target).attr('data-sDate');
	var e2 = $(e.target).attr('data-eDate');

	var s1 = $('#qjBtn1 input:checked').attr('data-sDate');
	var e1 = $('#qjBtn1 input:checked').attr('data-eDate');



	setDate(s1,e1,s2,e2,rmsType);
});

$('#sTime-d').on('change',function(e){//选择日期
	var date = $(e.target).val();
	console.log(date);
});

$('#sTime-h').on('change',function(e){//选择时间
	var date = $(e.target).val();
	console.log(date)
});

$('#eTime').on('change',function(e){//选择平均后的时间
	var date = $(e.target).val();
	console.log(date)
});
