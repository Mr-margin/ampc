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
	var paramsName = {};
	paramsName.userId = 1;
	
	ajaxPost(urlName,paramsName).success(function(res){
		
		var accordion_html = "";
		if(res.status == 0){
			$.each(res.data, function(i, col) {
				
				var inn = i == 0 ? "in" : "";
				
				accordion_html += '<div class="panel panel-default"><div class="panel-heading" style="background-color: #FFF;">';
				accordion_html += '<a data-toggle="collapse" data-parent="#accordion" style="font-weight: 700;" href="#collapse'+i+'"><h5 class="panel-title">'+col.sectorsName+'';
				accordion_html += '<code class="pull-right">已使用&nbsp;4&nbsp;条措施</code></h5></a></div>';
				accordion_html += '<div id="collapse'+i+'" class="panel-collapse collapse '+inn+'" style="background-color: #EDF7FF;"><div class="panel-body" style="border: 0px;">';
				
				$.each(col.measureItems, function(j, vol) {
					accordion_html += '<div class="col-md-6 c6center">';
//					accordion_html += '<button type="button" onclick="open_cs('+vol.measureame+');" class="btn btn-default '+vol.colorname+'" style="background-color: #'+vol.colorcode+';width:80%;">'+vol.measureame+'</button>';
					accordion_html += '<a class="btn btn-success-cs btn-outline" style="width:80%;" onclick="open_cs(\''+col.sectorsName+'\',\''+vol.measureame+'\',\''+vol.mid+'\');"><i class="fa fa-ban"> </i>&nbsp;&nbsp;&nbsp;'+vol.measureame+'</a>';//fa fa-check-circle-o
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
 * 获取措施汇总
 */
function metTable_hj_info(){
	$('#metTable_hj').bootstrapTable({
		method: 'POST',
		url: url,
		dataType: "json",
		columns: columns, //列
		iconSize: "outline",
		clickToSelect: true,//点击选中行
		pagination: false,	//在表格底部显示分页工具栏
		icons: {
			refresh: "glyphicon-repeat",
			toggle: "glyphicon-list-alt",
			columns: "glyphicon-list"
		},
//		pageSize: tablepageSize,	//页面大小
//		pageNumber: 1,	//页数
		striped: true,	 //使表格带有条纹
//		sidePagination: "server",//表格分页的位置 client||server
		queryParams: queryParams, //参数
		queryParamsType: "limit", //参数格式,发送标准的RESTFul类型的参数请求
		silent: true,  //刷新事件必须设置
		contentType: "application/x-www-form-urlencoded",	//请求远程数据的内容类型。
		onClickRow: function (row, $element) {
			$('.success').removeClass('success');
			$($element).addClass('success');
//			row_sheetname = sheet;
		}
	});
}

var sc_val = {};//存储最终的自措施条件

/**
 * 打开措施的窗口，数据初始化
 * sectorsName:行业名称
 * measureame:措施名称
 * mid:措施ID
 */
function open_cs(sectorsName,measureame,mid){
	$("#measureame").html("措施："+measureame);
	$("#sectorsName").html("行业："+sectorsName);

	var urlName = '/measure/get_measureQuery';
	var paramsName = {};
	paramsName.userId = 1;
	paramsName.sectorName = sectorsName;
	paramsName.measureId = mid;
	paramsName.planId = 1;
	ajaxPost(urlName,paramsName).success(function(res){
		
		if(res.status == 0){//返回状态正常
			if(res.data.query.length>0){//返回筛选条件
				var sc_conter = '';
				var name_length = 0;
				$.each(res.data.query, function(i, col) {
					
					var type = col.queryOptiontype.indexOf("复选")>=0 ? "checkbox" : "radio";//根据类型区别复选或单选
					var queryShowquery = col.queryShowquery == null ? '' : 'style="display:none;"';//如果有出现条件，默认隐藏
					sc_val[col.queryEtitle] = queryShowquery == '' ? '不限' : '';//存储最终的自措施条件，,根据是否有条件，决定默认不限还是默认为空
					
//					var temp_name = col.queryName.replace("(","").replace(")","").replace("/","");
					var temp_name = col.queryName.indexOf("(")>0 ? col.queryName.substring(0,col.queryName.indexOf("(")) : col.queryName;
					name_length = temp_name.length > name_length ? temp_name.length : name_length;//记录字数最多的标题
					
					sc_conter += '<div class="btn-group tiaojian" '+queryShowquery+' sc_su="'+col.queryShowquery+'" sc_su_val="'+col.queryValue+'">';
					sc_conter += '<span class="font-bold tiaojian-title" style="width: 90px;">'+col.queryName.replace("(","<br>(")+'</span>';
					sc_conter += '<div class="btn-group btn-group-circle" data-toggle="buttons" onclick="val_handle($(this),\''+col.queryEtitle+'\',\''+col.queryName+'\');">';
					if(col.queryOptiontype.indexOf("值域")>=0){//所有的值域需要自定义修改
						sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="'+type+'" class="toggle">不限</label>';
						
						for(var k = 1;k<6;k++){
							if(col["queryOption"+k] == null){
								if(k>1){
									sc_conter += '<label class="btn btn-d1" val_name=">='+col["queryOption"+(k-1)]+'"><input type="'+type+'" class="toggle">≥'+col["queryOption"+(k-1)]+'</label>';
									break;
								}
							}else{
								if(k == 1){
									sc_conter += '<label class="btn btn-d1" val_name="0-'+(col.queryOption1-1)+'"><input type="'+type+'" class="toggle">0-'+(col.queryOption1-1)+'</label>';
								}else{
									sc_conter += '<label class="btn btn-d1" val_name="'+(col["queryOption"+(k-1)]-0)+'-'+(col["queryOption"+k]-1)+'"><input type="'+type+'" class="toggle">'+(col["queryOption"+(k-1)]-0)+'-'+(col["queryOption"+k]-1)+'</label>';
								}
								if(k == 5){
									sc_conter += '<label class="btn btn-d1" val_name=">='+(col["queryOption"+k]-0)+'"><input type="'+type+'" class="toggle">≥'+(col["queryOption"+k]-0)+'</label>';
									break;
								}
							}
						}
						sc_conter += '</div>';
						sc_conter += '<form role="form" class="form-inline"><div class="form-group" style="padding-left:10px;">';
						sc_conter += '<input type="name" id="" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
						sc_conter += '</div>-<div class="form-group">';
						sc_conter += '<input type="name" id="" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
						sc_conter += '</div></form>';
					}else{
						sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="'+type+'" class="toggle">不限</label>';
						sc_conter +=  col.queryOption1 == null ? '' : '<label class="btn btn-d1" val_name="'+col.queryOption1+'"><input type="'+type+'" class="toggle">'+col.queryOption1+'</label>';
						sc_conter +=  col.queryOption2 == null ? '' : '<label class="btn btn-d1" val_name="'+col.queryOption2+'"><input type="'+type+'" class="toggle">'+col.queryOption2+'</label>';
						sc_conter +=  col.queryOption3 == null ? '' : '<label class="btn btn-d1" val_name="'+col.queryOption3+'"><input type="'+type+'" class="toggle">'+col.queryOption3+'</label>';
						sc_conter +=  col.queryOption4 == null ? '' : '<label class="btn btn-d1" val_name="'+col.queryOption4+'"><input type="'+type+'" class="toggle">'+col.queryOption4+'</label>';
						sc_conter +=  col.queryOption5 == null ? '' : '<label class="btn btn-d1" val_name="'+col.queryOption5+'"><input type="'+type+'" class="toggle">'+col.queryOption5+'</label>';
						sc_conter += '</div>';
					}
					
					sc_conter += '</div>';
					sc_conter += '';
					sc_conter += '';
				});
				re = new RegExp("90px;","g");
				sc_conter = sc_conter.replace(re, (name_length*16)+"px;");
				$("#sc_conter").html(sc_conter);
			}
		}else{
			swal('连接错误', '', 'error');
		}
	}).error(function(){
		swal('校验失败', '', 'error')
	})
	
	
	$("#createModal").modal();
}
console.log(sc_val);

/**
 * 筛选条件的值处理
 * e:当前操作的条件的外层DIV对象
 */
function val_handle(e, queryEtitle, queryName){
	var xuanze_val = "";
	setTimeout(function(){//等待1/10秒，待css改变后再获取选择的值
		e.children().each(function(){//循环当前条件下所有的可选项
			if($(this).is('.active')){
				sc_val[queryEtitle] = $(this).attr("val_name");
				xuanze_val = $(this).attr("val_name");
			}
		});
		$("#sc_conter").children().each(function(){//循环所有的条件
			if($(this).attr("sc_su")!=null){
				if($(this).attr("sc_su")==queryName){//条件的名字与其他条件的前置条件名相同
					if($(this).attr("sc_su_val")==xuanze_val){//值相同
						$(this).show();
					}else{
						$(this).hide();
					}
				}
			}
		});
		
	},100);
	console.log(sc_val);
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
