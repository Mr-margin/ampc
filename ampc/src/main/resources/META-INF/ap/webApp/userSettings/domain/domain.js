/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight">');

var Storage = localStorage;
$(document).ready(function(){
	$('.d03').hide(); 
	$('.d04').hide();
	
	//增加第二层
	$('.add_two2').click(function(){
		$('.d03').show();
		$('.box-body').attr('max_dom','2');
		$('.fa-times').show();
	});
	
	//删除第二层
	$('.hade2').click(function(){
		$('.d03').hide();
		$('.d03 input').val('');
		$('.d04').hide();
		$('.d04 input').val('');
		$('.box-body').attr('max_dom','1');
	});
	
	//增加第三层
	$('.add_two3').click(function(){ 
		$('.d04').show();
		$('.box-body').attr('max_dom','3');
		$('.fa-times').show();
	}); 
	
	//删除第三层
	$('.hade3').click(function(){
		$('.d04').hide();
		$('.d04 input').val('');
		$('.box-body').attr('max_dom','2');
	});
	
	//分辨率发生变化
	$("select").change(function(){
		resolution();//分辨率选择，改变多层分辨率
	});
	
	
	$('input').change(function(){
		resolution();
		$('.save').attr('disabled',false);
	});
});

/**
 * 输入数据验证
 */
$('.ref_lon').blur(function(){
	var Central_Meridian = $(this).val();
	if(Central_Meridian){
		Central_Meridian = parseFloat(Central_Meridian).toFixed(6)*1;
		$(this).val(Central_Meridian);
	}
	if(Central_Meridian < 75 || Central_Meridian > 150){
 		swal("请输入75~150之间的数字");
 		$('.ref_lon').val('');
 	}
});
$('.stand_lat1').blur(function(){
	var Standard_Parallel_1 = $(this).val();
	if(Standard_Parallel_1){
		Standard_Parallel_1 = parseFloat(Standard_Parallel_1).toFixed(6)*1;
		$(this).val(Standard_Parallel_1);
	}
	if(Standard_Parallel_1 < 0 || Standard_Parallel_1 > 90){
 		swal("请输入0~90之间的数字");
 		$('.stand_lat1').val('');
 	}
});
$('.stand_lat2').blur(function(){
	var Standard_Parallel_2 = $(this).val();
	if(Standard_Parallel_2){
		Standard_Parallel_2 = parseFloat(Standard_Parallel_2).toFixed(6)*1;
		$(this).val(Standard_Parallel_2);
	}
	if(Standard_Parallel_2 < 0 || Standard_Parallel_2 > 90){
 		swal("请输入0~90之间的数字");
 		$('.stand_lat2').val('');
 	}
});
$('.ref_lat').blur(function(){
	var Latitude_Of_Origin = $(this).val();
	if(Latitude_Of_Origin){
		numbers = parseFloat(Latitude_Of_Origin).toFixed(6)*1;
		$(this).val(Latitude_Of_Origin);
	}
	if(Latitude_Of_Origin < 15 || Latitude_Of_Origin > 55){
 		swal("请输入15~55之间的数字");
 		$('.ref_lat').val('');
 	}
});
$('.e_reolace').blur(function(){
	var num = $(this).val();
	if((num-1)%3 !== 0){
		swal("请输入3的倍数+1的数字");
		$(this).val('');
	}
});

/**
 * 查询已有数据
 */
function getInfo(){
	var  url = '/Domain/findAll';
	var domain_id = Storage.getItem('domain_id_up');
	ajaxPost(url,{
		'userId': userId
	}).success(function (res) {
		$.each(res.data,function(key,value){
			if(value.domainId == domain_id){
				console.log(value);
				pullPage(value);
			}
		});
		resolution();//分辨率选择，改变多层分辨率
		setMapExtent(submitSave());
    });
}  

/**
 * 打开编辑页面的时候初始化数据
 * @param value:数据库获取到的domain数据
 */
function pullPage(value){
	console.log(value);
	var domain_id = value.domainId;
	if(JSON.stringify(value.domainInfo) == "{}"){
		$('.panel-title a').text(value.domainName);
		$('.box-body input').val('');
		$('.d03').hide(); 
		$('.d04').hide();
		$('.domain_select').find("option[value='0']").attr('selected','selected');
		$('.box-body').attr('domain_id',domain_id);
		//$('.box-body').attr('max_dom',value.domainInfo.common.max_dom);
		$('.del_domain').attr('domain_id',domain_id);
		//$('.i_parent_start1').text('1');
		//$('.j_parent_start1').text('1');
	}else{
		var arr_we = value.domainInfo.wrf.e_we.split(',');
		var arr_sn = value.domainInfo.wrf.e_sn.split(',');
		var arr_dx = value.domainInfo.common.dx.split(',');
		var arr_i_parent_start = value.domainInfo.wrf.i_parent_start.split(',');
		var arr_j_parent_start = value.domainInfo.wrf.j_parent_start.split(',');
		var btrim = value.domainInfo.mcip.btrim;
		$('.panel-title a').text(value.domainName);
		$('.ref_lat').val(value.domainInfo.common.ref_lat);
		$('.ref_lon').val(value.domainInfo.common.ref_lon);
		$('.stand_lat1').val(value.domainInfo.common.stand_lat1);
		$('.stand_lat2').val(value.domainInfo.common.stand_lat2);
		$('.stand_lon').val(value.domainInfo.common.ref_lon);
		$('.e_we1').val(arr_we[0]);
		$('.e_sn1').val(arr_sn[0]);
		$('.e_we2').val(arr_we[1]);
		$('.e_sn2').val(arr_sn[1]);
		$('.e_we3').val(arr_we[2]);
		$('.e_sn3').val(arr_sn[2]);
		//$('.i_parent_start1').text(arr_i_parent_start[0]);
		$('.i_parent_start2').val(arr_i_parent_start[1]);
		$('.i_parent_start3').val(arr_i_parent_start[2]);
		//$('.j_parent_start1').text(arr_j_parent_start[0]);
		$('.j_parent_start2').val(arr_j_parent_start[1]);
		$('.j_parent_start3').val(arr_j_parent_start[2]);
		$('.box-body').attr('domain_id',domain_id);
		$('.box-body').attr('max_dom',value.domainInfo.common.max_dom);
		$('.del_domain').attr('domain_id',domain_id);
		$('.btrim_select').find("option[value="+btrim+"]").attr('selected','selected');
		if(arr_dx[0] == '27000'){
			$('.domain_select').find("option[value='2']").attr('selected','selected');
		}else{
			$('.domain_select').find("option[value='3']").attr('selected','selected');
		}
		$('.dx1').text(arr_dx[1]);
		$('.dx2').text(arr_dx[2]);
		if(value.domainInfo.common.max_dom =='3'){
			$('.d02').show();
			$('.d03').show(); 
			$('.d04').show();
		}else if(value.domainInfo.common.max_dom =='2'){
			$('.d02').show();
			$('.d03').show(); 
		}else if(value.domainInfo.common.max_dom =='1'){
			$('.d02').show();
		}
	}
}



/**
 * 返回上一界面
 */
function back(){
	var url = '/Domain/updateRangeAndCode';
	var data = submitSave();
	if($('.save').attr('disabled') == 'disabled'){
		history.back(-1);
	}else{
		swal({
			title: "是否保存?",
			text: "请您确定数据已经保存!",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			cancelButtonText:'取消',
			confirmButtonText: "确定",
			closeOnConfirm: false
		},
		function(isConfirm){
			if (isConfirm) {
		  		if(data.domainInfo.common.ref_lat == ''){
					swal("请输入Latitude_Of_Origin");
				}else if(data.domainInfo.common.ref_lon == ''){
					swal("请输入Central_Meridian");
				}else if(data.domainInfo.common.stand_lat1 == ''){
					swal("请输入Standard_Parallel_1");
				}else if(data.domainInfo.common.stand_lat2 == ''){
					swal("请输入Standard_Parallel_2");
				}else if($('.e_we1').val() == ''){
					swal("请输入x方向网格边数");
				}else if($('.e_sn1').val() == ''){
					swal("请输入y方向网格边数");
				}else if($('.btrim_select').find("option:selected").val() == '0'){
					swal("请选择x,y向裁剪网格数");
				}else if($('.domain_select').find("option:selected").text() == '请选择'){
					swal("请选择x,y向分辨率(m)");
				}else{
					ajaxPost(url,{
						'userId':data.userId,
						'domainInfo':data.domainInfo,
						'domainRange':data.domainRange,
						'domainId':data.domainId,
					}).success(function(res){
						console.log(res);
						if(res.msg == 'success'){
							swal("保存成功");
							$('.sweet-overlay').hide();
							$('.visible').hide();
						    history.back(-1);
							$('.save').attr('disabled',true);
						}else{
							swal("保存失败");
						}
					});
				}
			} else {
		  		$('.sweet-overlay').hide();
				$('.visible').hide();
			    history.back(-1);
			}
		});
	}
}

/**
 * 分辨率选择，改变多层分辨率
 */
function resolution(){
	var checkValue=$(".domain_select").val();
	var btrim = $('.btrim_select').val();
	var stand_lon = $('.ref_lon').val();
	$('.btrims').text(btrim);
//	$('.ref_lon').val(stand_lon);
	if(checkValue == '2'){
		$('.dx1').text('9000');
		$('.dx2').text('3000');
	}else if(checkValue == '3'){
		$('.dx1').text('12000');
		$('.dx2').text('4000');
	}else if(checkValue == '0'){
		$('.dx1').text('');
		$('.dx2').text('');

	}
}

/**修改页面数据整理数据结构**/
function submitSave(){
	var arr_we = [];
	var arr_sn = [];
	var arr_dx = [];
	var arr_i_parent_start = [];
	var arr_j_parent_start = [];
	var e_we1 = $('.e_we1').val(); 
	var e_we2 = $('.e_we2').val(); 
	var e_we3 = $('.e_we3').val(); 
	var e_sn1 = $('.e_sn1').val(); 
	var e_sn2 = $('.e_sn2').val(); 
	var e_sn3 = $('.e_sn3').val(); 
	var i_parent_start1 = 1;
	var i_parent_start2 = $('.i_parent_start2').val();
	var i_parent_start3 = $('.i_parent_start3').val();
	var j_parent_start1 = 1;
	var j_parent_start2 = $('.j_parent_start2').val();
	var j_parent_start3 = $('.j_parent_start3').val();
	var dx = $(".domain_select").find("option:selected").text();

	arr_we.push(e_we1,e_we2,e_we3);
	arr_sn.push(e_sn1,e_sn2,e_sn3);
	arr_j_parent_start.push(j_parent_start1,j_parent_start2,j_parent_start3);
	arr_i_parent_start.push(i_parent_start1,i_parent_start2,i_parent_start3);

	var ref_lat = $('.ref_lat').val();
	var ref_lon = $('.ref_lon').val();
	var stand_lat1 = $('.stand_lat1').val();
	var stand_lat2 = $('.stand_lat2').val();
	var max_dom = $('.box-body').attr('max_dom');
	var btrim = $('.btrim_select').find("option:selected").val();
	var i_parent_start = arr_i_parent_start.join(',');
	var j_parent_start = arr_j_parent_start.join(',');
	var e_we = arr_we.join(',');
	var e_sn = arr_sn.join(',');
	var domainInfo = {
		'common':{
			'ref_lat':ref_lat,
			'ref_lon':ref_lon,
			'stand_lat1':stand_lat1,
			'stand_lat2':stand_lat2,
			'max_dom':max_dom,
			'dx':dx,
			'dy':dx,
		},
		'wrf':{
			'i_parent_start':i_parent_start,
			'j_parent_start':j_parent_start,
			'e_we':e_we,
			'e_sn':e_sn,
		},
		'mcip':{
			'btrim':btrim,
		}
	};
	var domainRange = dx;
	var domainId = $('.box-body').attr('domain_id');
	var data = {
		'userId':userId,
		'domainInfo':domainInfo,
		'domainRange':domainRange,
		'domainId':domainId
	}
	return data;
}

/**数据修改保存提交**/
function postSubmit(){
	var url = '/Domain/updateRangeAndCode';
	var data = submitSave();
	if(data.domainInfo.common.ref_lat == ''){
		swal("请输入Latitude_Of_Origin");
	}else if(data.domainInfo.common.ref_lon == ''){
		swal("请输入Central_Meridian");
	}else if(data.domainInfo.common.stand_lat1 == ''){
		swal("请输入Standard_Parallel_1");
	}else if(data.domainInfo.common.stand_lat2 == ''){
		swal("请输入Standard_Parallel_2");
	}else if($('.e_we1').val() == ''){
		swal("请输入x方向网格边数");
	}else if($('.e_sn1').val() == ''){
		swal("请输入y方向网格边数");
	}else if($('.btrim_select').find("option:selected").val() == '0'){
		swal("请选择x,y向裁剪网格数");
	}else if($('.domain_select').find("option:selected").text() == '请选择'){
		swal("请选择x,y向分辨率(m)");
	}else{
		ajaxPost(url,{
			'userId':data.userId,
			'domainInfo':data.domainInfo,
			'domainRange':data.domainRange,
			'domainId':data.domainId,
		}).success(function(res){
			console.log(res);
			if(res.msg == 'success'){
				swal("保存成功");
				$('.save').attr('disabled',true);
			}else{
				swal("保存失败");
			}
		});
	}
}



var stat = {cPointx: 106, cPointy: 35}, app = {}, dong = {};
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
        "esri/map",
        "esri/Color",
        "esri/tasks/Geoprocessor", 
        "esri/layers/DynamicLayerInfo", 
        "esri/layers/TableDataSource",
        "esri/layers/LayerDataSource", 
        "esri/layers/FeatureLayer", 
        "esri/layers/GraphicsLayer", 
        "esri/layers/LayerDrawingOptions", 
        "esri/geometry/Extent",
        "esri/renderers/SimpleRenderer", 
        "esri/graphic", 
        "tdlib/gaodeLayer", 
        "esri/tasks/FeatureSet", 
        "esri/SpatialReference", 
        "esri/symbols/PictureMarkerSymbol",
        "esri/layers/MapImageLayer", 
        "esri/layers/MapImage", 
        "esri/layers/ArcGISDynamicMapServiceLayer",
        "esri/symbols/SimpleLineSymbol",
        "dojo/domReady!"
    ],
    function (Map, 
    		Color,
    		Geoprocessor, 
    		DynamicLayerInfo, 
    		TableDataSource, 
    		LayerDataSource, 
    		FeatureLayer, 
    		GraphicsLayer, 
    		LayerDrawingOptions,
            Extent, 
            SimpleRenderer, 
            Graphic, 
            gaodeLayer, 
            FeatureSet, 
            SpatialReference, 
            PictureMarkerSymbol, 
            MapImageLayer, 
            MapImage,
            ArcGISDynamicMapServiceLayer,
            SimpleLineSymbol) {

    	dong.Map = Map;
    	dong.Color = Color;
    	
        dong.gaodeLayer = gaodeLayer;
        dong.Geoprocessor = Geoprocessor;
        dong.Graphic = Graphic;
        dong.FeatureSet = FeatureSet;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.Extent = Extent;//
        dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;//
        dong.SimpleLineSymbol = SimpleLineSymbol;//
        
        app.gp = new dong.Geoprocessor(ArcGisServerUrl+"/arcgis/rest/services/ampc/FishingNets/GPServer/FishingNets");
        
        getInfo();//数据初始化
    });


/**
 * 生成渔网按钮
 */
function getFishingNets(){
	var data = submitSave();//获取到当前的值
//	console.log(data);
	if(data.domainInfo.common.ref_lat == ''){
		swal("请输入Latitude_Of_Origin");
	}else if(data.domainInfo.common.ref_lon == ''){
		swal("请输入Central_Meridian");
	}else if(data.domainInfo.common.stand_lat1 == ''){
		swal("请输入Standard_Parallel_1");
	}else if(data.domainInfo.common.stand_lat2 == ''){
		swal("请输入Standard_Parallel_2");
	}else if($('.e_we1').val() == ''){
		swal("请输入x方向网格边数");
	}else if($('.e_sn1').val() == ''){
		swal("请输入y方向网格边数");
	}else if($('.btrim_select').find("option:selected").val() == '0'){
		swal("请选择x,y向裁剪网格数");
	}else if($('.domain_select').find("option:selected").text() == '请选择'){
		swal("请选择x,y向分辨率(m)");
	}else{
		setMapExtent(data);
	}
	
}



/**
 * 设置地图的底图，根据参数改变地图投影
 * @param Central_Meridian：中央经线
 * @param Standard_Parallel_1：标准纬线1
 * @param Standard_Parallel_2：标准纬线2
 * @param Latitude_Of_Origin：起始原点
 */
function setMapExtent(data){
	var Central_Meridian = data.domainInfo.common.ref_lon;
	var Standard_Parallel_1 = data.domainInfo.common.stand_lat1;
	var Standard_Parallel_2 = data.domainInfo.common.stand_lat2;
	var Latitude_Of_Origin = data.domainInfo.common.ref_lat;
	
	if(app.map){
    	app.map.destroy();
    }
	app.spatialReference = new dong.SpatialReference({
		"wkt" : 'PROJCS["Lambert_Conformal_Conic_China",GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137.0,298.257223563]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Lambert_Conformal_Conic"],PARAMETER["False_Easting",0.0],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",'+Central_Meridian+'],PARAMETER["Standard_Parallel_1",'+Standard_Parallel_1+'],PARAMETER["Standard_Parallel_2",'+Standard_Parallel_2+'],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",'+Latitude_Of_Origin+'],UNIT["Meter",1.0]]'
	});
	app.customExtentAndSR = new dong.Extent(-3095608.299999997, -1761039.3040000014, 1901303.5966000035, 2272060.6185000017, app.spatialReference);
	app.map = new dong.Map("mapDiv", {
        logo: false,
        center: [Central_Meridian, Latitude_Of_Origin],
        minZoom: 4,
        maxZoom: 13,
        sliderPosition: 'top-right',
        extent: app.customExtentAndSR,
        zoom: 3
    });
	app.layer = new dong.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/ampc/la_cms/MapServer");// 创建动态地图
	app.map.addLayer(app.layer);
	generateFishingNets(data);
}


var jisuanNUM = {};//存储计算用的多层坐标
/**
 * 生成渔网
 */
function generateFishingNets(data){
	jisuanNUM = {};//每次生成渔网的时候，清空上次计算的结果，计算结果只在循环内使用

	var rows = new Array();//行数
	rows = data.domainInfo.wrf.e_sn.split(",");
	var columns = new Array();//列数
	columns = data.domainInfo.wrf.e_we.split(",");
	
	var i_parent_start = new Array();
	var j_parent_start = new Array();
	
	i_parent_start = data.domainInfo.wrf.i_parent_start.split(",");
	j_parent_start = data.domainInfo.wrf.j_parent_start.split(",");
	
	var height = [];
	var width = []
	//判断分辨率，确定每一层domain的分辨率
	if(data.domainRange == 27000){
		height = [27000, 9000, 3000];
		width = [27000, 9000, 3000];
	}else if(data.domainRange == 36000){
		height = [36000, 12000, 4000];
		width = [36000, 12000, 4000];
	}
	
	//循环生成多层domain
	for(var i = 0; i<data.domainInfo.common.max_dom; i++){
		if(i == 0){
			
			var x1 = -((rows[i]-1)/2*width[i]);
			var x2 = (-((rows[i]-1)/2*width[i]))+10;
			var y1 = -((columns[i]-1)/2*height[i]);
			var y2 = -((columns[i]-1)/2*height[i]);
			
			jisuanNUM.x1 = x1;
			jisuanNUM.x2 = x2;
			jisuanNUM.y1 = y1;
			jisuanNUM.y2 = y2;
			
			gp_server(rows[i]-1, columns[i]-1, width[i], height[i], y1+" "+x1, y2+" "+x2, 0);
			
		}else if(i == 1){
			
			var x1 = jisuanNUM.x1 + ((i_parent_start[i]-1)*width[i-1]);
			var x2 = jisuanNUM.x2 + ((i_parent_start[i]-1)*width[i-1]);
			var y1 = jisuanNUM.y1 + ((j_parent_start[i]-1)*height[i-1]);
			var y2 = jisuanNUM.y2 + ((j_parent_start[i]-1)*height[i-1]);
			
			jisuanNUM.xx1 = x1;
			jisuanNUM.xx2 = x2;
			jisuanNUM.yy1 = y1;
			jisuanNUM.yy2 = y2;
			
			gp_server(rows[i]-1, columns[i]-1, width[i], height[i], y1+" "+x1, y2+" "+x2, 1);
			
		}else if(i == 2){
			
			var x1 = jisuanNUM.xx1 + ((i_parent_start[i]-1)*width[i-1]);
			var x2 = jisuanNUM.xx2 + ((i_parent_start[i]-1)*width[i-1]);
			var y1 = jisuanNUM.yy1 + ((j_parent_start[i]-1)*height[i-1]);
			var y2 = jisuanNUM.yy2 + ((j_parent_start[i]-1)*height[i-1]);
			
			jisuanNUM.xxx1 = x1;
			jisuanNUM.xxx2 = x2;
			jisuanNUM.yyy1 = y1;
			jisuanNUM.yyy2 = y2;
			
			gp_server(rows[i]-1, columns[i]-1, width[i], height[i], y1+" "+x1, y2+" "+x2, 2);
			
		}
	}
}

/**
 * GP服务调用，生成单层渔网
 */
function gp_server(rows, columns, width, height, zuobiao1, zuobiao12, type){
	var myDate = new Date();
	var v1 = myDate.getTime();
	
	//三层domain的颜色
	var colos = [[255,0,0],[255,255,0],[0,150,255]]
	
	var parms = {
			"1" : zuobiao1,
			"2" : zuobiao12,
			"width" : width,
			"height" : height,
			"rows" : rows,
			"columns" : columns,
			"out" : "out_raster_layer"
		};
	app.gp.submitJob(parms, function(jobInfo){
		var jobId = jobInfo.jobId;
		var status = jobInfo.jobStatus;
		if(status === esri.tasks.JobInfo.STATUS_SUCCEEDED) {
			app.gp.getResultData(jobId, "out", function(results){
				var features = results.value.features;
				for(var i = 0, length = features.length; i != length; ++i) {
					var feature = features[i];
					var polySymbolRed = new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([255,0,0]), 0.5);
					feature.setSymbol(polySymbolRed);
					app.map.graphics.add(feature);
				}
			});
		}
		
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
		console.log(error);
	});
}











