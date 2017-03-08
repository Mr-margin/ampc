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


/**
 * 白色区域的收缩效果
 */
//$('.collapse-link').click(function () {
//    var ibox = $(this).closest('div.ibox');
//    var button = $(this).find('i');
//    var content = ibox.find('div.ibox-content');
//    content.slideToggle(200);
//    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
//    ibox.toggleClass('').toggleClass('border-bottom');
//    setTimeout(function () {
//        ibox.resize();
//        ibox.find('[id^=map-]').resize();
//    }, 50);
//});

$("#show").click(function(){
	if($("#slider_wrap").css("display")=="none"){
		$("#slider_wrap").show();
	    $("#custom").css("background-position","right -18px");
	}else{
		$("#slider_wrap").hide();
	    $("#custom").css("background-position","right 8px");
	}
});

/**
 * 设置导航条菜单
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">添加预案</a>>><span style="padding-left: 15px;padding-right: 15px;">措施编辑</span>');

var userid = "1";
hyc();

/**
 * 获取用户的行业与措施
 */
function hyc(){
	var urlName = '/ms/get_msInfo';
	var paramsName = {userId:1};
	
	ajaxPost(urlName,paramsName).success(function(res){
		
		var accordion_html = "";
		if(res.status == 0){
			$.each(res.data, function(i, col) {
				
				accordion_html += '<div class="panel panel-default"><div class="panel-heading" style="background-color: #FFF;"><h5 class="panel-title">';
				accordion_html += '<a data-toggle="collapse" data-parent="#accordion" href="#collapse'+i+'">'+col.sectorsName+'&nbsp;&nbsp;&nbsp;~~&nbsp;已使用';
				accordion_html += '<code>&nbsp;4&nbsp;</code>条措施&nbsp;~~</a></h5></div>';
				accordion_html += '<div id="collapse'+i+'" class="panel-collapse collapse" style="background-color: #EDF7FF;"><div class="panel-body" style="border: 0px;">';
				
				$.each(col.measureItems, function(j, vol) {
					accordion_html += '<div class="col-md-6 c6center">';
					accordion_html += '<button type="button" class="btn btn-default btn-cuoshi-qian" style="background-color: #f0f8ff;width:80%;">'+vol.measureame+'</button>';
					accordion_html += '</div>';
				});
				accordion_html += '</div></div></div>';
			});
			$("#accordion").html(accordion_html);
		}else{
			swal('连接错误', '', 'error');
//			swal('添加成功', '', 'success');
		}
	}).error(function(){
		swal('校验失败', '', 'error')
	})
}







/**
 * 操作地图显示
 */
var stat = {cPointx : 116, cPointy : 35}, app = {}, dong = {};
var dojoConfig = {
	async: true,
    parseOnLoad: true,  
    packages: [{  
        name: 'tdlib',  
        location: "/js/tdlib"  
    }]
};
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol", 
	 	"esri/symbols/SimpleMarkerSymbol", "esri/geometry/Point", "esri/geometry/Extent", "esri/renderers/SimpleRenderer", "esri/graphic", 
        "dojo/_base/Color", "dojo/dom-style", "esri/tasks/FeatureSet", "esri/SpatialReference", "tdlib/gaodeLayer", "dojo/domReady!"], 
	function(Map, FeatureLayer,GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Point,Extent,SimpleRenderer, Graphic,
	        Color, domStyle, FeatureSet, SpatialReference, gaodeLayer) {
		dong.gaodeLayer = gaodeLayer;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		
		app.mapList = new Array();
		app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
		app.stlayerList = new Array();//加载卫星图
		app.labellayerList = new Array();//加载标注图
		
		for(var i = 0;i<2;i++){
			var map = new Map("mapDiv"+i, {
				logo:false,
		        center: [stat.cPointx, stat.cPointy],
		        minZoom:3,
		        maxZoom:13,
		        zoom: 3
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
		
		app.layer = new esri.layers.ArcGISDynamicMapServiceLayer("http://192.168.1.132:6080/arcgis/rest/services/china_gd/MapServer");//创建动态地图
		app.mapList[0].addLayer(app.layer);
		
		
//		var point = new dong.Point(12367606.298176643, 4092838.5819190354, new dong.SpatialReference({ wkid: 3857 }));
//		app.str = new SimpleMarkerSymbol('esriSMSCircle','20','','red');
//		var graphic = new dong.Graphic(point, app.str);
//		app.gLyr.add(graphic);
});
