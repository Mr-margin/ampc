/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight">');

var Storage = localStorage;
$(document).ready(function(){

	$('.d03').hide(); 
	$('.d04').hide();
	$('.add_two').click(function(){
		$('.d02').show();
		$('.box-body').attr('max_dom','1');
		$('.fa-times').show();
	});
	$('.hade').click(function(){
		$('.d02 input').val('');
		$('.d03').hide();
		$('.d03 input').val('');
		$('.d04').hide();
		$('.d04 input').val('');
	});
	$('.add_two2').click(function(){
		$('.d03').show();
		$('.box-body').attr('max_dom','2');
		$('.fa-times').show();
	});
	$('.hade2').click(function(){
		$('.d03').hide();
		$('.d03 input').val('');
		$('.d04').hide();
		$('.d04 input').val('');
		$('.box-body').attr('max_dom','1');
	});
	$('.add_two3').click(function(){ 
		$('.d04').show();
		$('.box-body').attr('max_dom','3');
		$('.fa-times').show();
	}); 
	$('.hade3').click(function(){
		$('.d04').hide();
		$('.d04 input').val('');
		$('.box-body').attr('max_dom','2');
	});
	$("select").change(function(){
		resolution();
		submitSave();
	});
	$('input').change(function(){
		submitSave();
		resolution();
	});
	getInfo();
	resolution();
});
     

/**查询接口**/
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
        
    });
}  

/**数据导入页面**/
function pullPage(value){
	var domain_id = value.domainId;
	if(JSON.stringify(value.domainInfo) == "{}"){
		$('.panel-title').text(value.domainName);
		$('.box-body input').val('');
		$('.d03').hide(); 
		$('.d04').hide();
		$('.domain_select').find("option[value='0']").attr('selected','selected');
		$('.box-body').attr('domain_id',domain_id);
		$('.del_domain').attr('domain_id',domain_id);
		$('.i_parent_start1').val('1');
		$('.j_parent_start1').val('1');
	}else{
		var arr_we = value.domainInfo.wrf.e_we.split(',');
		var arr_sn = value.domainInfo.wrf.e_sn.split(',');
		var arr_dx = value.domainInfo.common.dx.split(',');
		var arr_i_parent_start = value.domainInfo.wrf.i_parent_start.split(',');
		var arr_j_parent_start = value.domainInfo.wrf.j_parent_start.split(',');
		$('.panel-title').text(value.domainName);
		$('.ref_lat').val(value.domainInfo.common.ref_lat);
		$('.ref_lon').val(value.domainInfo.common.ref_lon);
		$('.stand_lat1').val(value.domainInfo.common.stand_lat1);
		$('.stand_lat2').val(value.domainInfo.common.stand_lat2);
		$('.stand_lon').val(value.domainInfo.common.stand_lon);
		$('.e_we1').val(arr_we[0]);
		$('.e_sn1').val(arr_sn[0]);
		$('.e_we2').val(arr_we[1]);
		$('.e_sn2').val(arr_sn[1]);
		$('.e_we3').val(arr_we[2]);
		$('.e_sn3').val(arr_sn[2]);
		$('.btrim').val(value.domainInfo.mcip.btrim);
		$('.btrims').val(value.domainInfo.mcip.btrim);
		$('.i_parent_start1').val(arr_i_parent_start[0]);
		$('.i_parent_start2').val(arr_i_parent_start[1]);
		$('.i_parent_start3').val(arr_i_parent_start[2]);
		$('.j_parent_start1').val(arr_j_parent_start[0]);
		$('.j_parent_start2').val(arr_j_parent_start[1]);
		$('.j_parent_start3').val(arr_j_parent_start[2]);
		$('.box-body').attr('domain_id',domain_id);
		$('.del_domain').attr('domain_id',domain_id);

		if(arr_dx[0] == '27000'){
			$('.domain_select').find("option[value='2']").attr('selected','selected');
		}else{
			$('.domain_select').find("option[value='3']").attr('selected','selected');
		}
		$('.dx1').val(arr_dx[1]);
		$('.dx2').val(arr_dx[2]);
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

/**分辨率选择**/
function resolution(){
	var checkValue=$("select").val();
	var btrim = $('.btrim').val();
	var stand_lon = $('.stand_lon').val();
	$('.btrims').val(btrim);
	$('.ref_lon').val(stand_lon);
	if(checkValue == '2'){
		$('.dx1').val('9000');
		$('.dx2').val('3000');
	}else if(checkValue == '3'){
		$('.dx1').val('12000');
		$('.dx2').val('4000');
	}else if(checkValue == '0'){
		$('.dx1').val('');
		$('.dx2').val('');

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
	var i_parent_start1 = $('.i_parent_start1').val();
	var i_parent_start2 = $('.i_parent_start2').val();
	var i_parent_start3 = $('.i_parent_start3').val();
	var j_parent_start1 = $('.j_parent_start1').val();
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
	var btrim = $('.btrim').val();
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
	ajaxPost(url,{
		'userId':data.userId,
		'domainInfo':data.domainInfo,
		'domainRange':data.domainRange,
		'domainId':data.domainId,
	}).success(function(res){
		if(res.msg == 'success'){
			swal("保存成功");
		}else{
			swal("保存失败");
		}
	});
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
        "dojo/domReady!"
    ],
    function (Map, 
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
            ArcGISDynamicMapServiceLayer) {

    	dong.Map = Map;
    	
        dong.gaodeLayer = gaodeLayer;
        dong.Geoprocessor = Geoprocessor;
        dong.Graphic = Graphic;
        dong.FeatureSet = FeatureSet;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.Extent = Extent;//
        dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;//

        app.baselayerList = new dong.gaodeLayer();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
        app.stlayerList = new dong.gaodeLayer({layertype: "st"});//加载卫星图
        app.labellayerList = new dong.gaodeLayer({layertype: "label"});//加载标注图
        


        
        
//        app.map = new Map("mapDiv", {
//            logo: false,
//            center: [stat.cPointx, stat.cPointy],
//            minZoom: 4,
//            maxZoom: 13,
//            sliderPosition: 'bottom-right',
//            zoom: 4
//        });
//
//        app.map.addLayer(app.baselayerList);//添加高德地图到map容器
//        app.map.addLayers([app.baselayerList]);//添加高德地图到map容器
        
        setMapExtent(110,25,40,34);
    });


/**
 * 设置地图的底图，根据参数改变地图投影
 * @param Central_Meridian：中央经线
 * @param Standard_Parallel_1：标准纬线1
 * @param Standard_Parallel_2：标准纬线2
 * @param Latitude_Of_Origin：起始原点
 */
function setMapExtent(Central_Meridian, Standard_Parallel_1, Standard_Parallel_2, Latitude_Of_Origin){
	
	if(app.map){
    	app.map.destroy();
    }
	
	app.spatialReference = new esri.SpatialReference({
		"wkt" : 'PROJCS["Lambert_Conformal_Conic_China",GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137.0,298.257223563]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Lambert_Conformal_Conic"],PARAMETER["False_Easting",0.0],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",'+Central_Meridian+'],PARAMETER["Standard_Parallel_1",'+Standard_Parallel_1+'],PARAMETER["Standard_Parallel_2",'+Standard_Parallel_2+'],PARAMETER["Scale_Factor",1.0],PARAMETER["Latitude_Of_Origin",'+Latitude_Of_Origin+'],UNIT["Meter",1.0]]'
	});

	app.map = new dong.Map("mapDiv", {
        logo: false,
        center: [stat.cPointx, stat.cPointy],
        minZoom: 4,
        maxZoom: 13,
        sliderPosition: 'top-right',
        zoom: 4
    });
	app.map.spatialReference = app.spatialReference;
	app.layer = new dong.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/ampc/la_cms/MapServer");// 创建动态地图
	app.layer.spatialReference = app.spatialReference;
	
	app.map.addLayer(app.layer);
	
}












