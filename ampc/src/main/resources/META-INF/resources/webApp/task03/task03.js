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
		console.log(JSON.stringify(res));
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
var temp_val = {};//单次查询的缓存
var ty = "";
var temp_val_v1 = {};//单次查询的缓存
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
	
	sc_val = {};//初始化缓存
	sc_val.bigIndex = "";
	sc_val.smallIndex = sectorsName;//记录行业
	sc_val.filters = [];
	sc_val.summary = {};
	
	ajaxPost(urlName,paramsName).success(function(res){
		console.log(JSON.stringify(res));
		if(res.status == 0){//返回状态正常
			if(res.data.query.length>0){//返回筛选条件
				var sc_conter = '';
				var name_length = 0;
				$.each(res.data.query, function(i, col) {
					
					var type = col.queryOptiontype.indexOf("复选")>=0 ? "checkbox" : "radio";//根据类型区别复选或单选
					var queryShowquery = col.queryShowquery == null ? '' : 'style="display:none;"';//如果有出现条件，默认隐藏
					var temp_name = col.queryName.indexOf("(")>0 ? col.queryName.substring(0,col.queryName.indexOf("(")) : col.queryName;
					name_length = temp_name.length > name_length ? temp_name.length : name_length;//记录字数最多的标题
					
					sc_conter += '<div class="btn-group tiaojian" '+queryShowquery+' sc_su="'+col.queryShowqueryen+'" sc_su_val="'+col.queryValue+'">';
					sc_conter += '<span class="font-bold tiaojian-title" style="width: 90px;">'+col.queryName.replace("(","<br>(")+'</span>';
					sc_conter += '<div class="btn-group btn-group-circle" data-toggle="buttons" id="'+col.queryEtitle+'" onclick="val_handle($(this),\''+col.queryEtitle+'\',\''+col.queryName+'\',\''+type+'\');">';
					if(col.queryOptiontype.indexOf("值域")>=0){//所有的值域需要自定义修改
						sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="'+type+'" class="toggle">不限</label>';
						
						for(var k = 1;k<6;k++){
							if(col["queryOption"+k] == null){
								if(k>1){
									sc_conter += '<label class="btn btn-d1" val_name="'+col["queryOption"+(k-1)]+'~"><input type="'+type+'" class="toggle">≥'+col["queryOption"+(k-1)]+'</label>';
									break;
								}
							}else{
								if(k == 1){
									sc_conter += '<label class="btn btn-d1" val_name="0~'+(col.queryOption1-1)+'"><input type="'+type+'" class="toggle">0-'+(col.queryOption1-1)+'</label>';
								}else{
									sc_conter += '<label class="btn btn-d1" val_name="'+(col["queryOption"+(k-1)]-0)+'~'+(col["queryOption"+k]-1)+'"><input type="'+type+'" class="toggle">'+(col["queryOption"+(k-1)]-0)+'-'+(col["queryOption"+k]-1)+'</label>';
								}
								if(k == 5){
									sc_conter += '<label class="btn btn-d1" val_name="'+(col["queryOption"+k]-0)+'~"><input type="'+type+'" class="toggle">≥'+(col["queryOption"+k]-0)+'</label>';
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
				});
				sc_conter += '<div style="text-align: right;padding-right: 20px;">';
				sc_conter += '<button class="btn btn-success" onclick="search_button();" type="button">';
				sc_conter += '<i class="fa fa-search"></i>&nbsp;&nbsp;<span class="bold">筛选</span>';
				sc_conter += '</button></div>';
				
				re = new RegExp("90px;","g");
				sc_conter = sc_conter.replace(re, (name_length*16)+"px;");
				$("#sc_conter").html(sc_conter);
				
				temp_val = {};
				$.each(res.data.query, function(i, col) {//组织条件的样例结构
					if(col.queryShowquery == null){//是一级节点
						if(col.queryOptiontype.indexOf("复选")>=0){//是复选框
							temp_val[col.queryEtitle] = [];
							if(col.queryOption1 != null){
								temp_val[col.queryEtitle].push(col.queryOption1);
							}
							if(col.queryOption2 != null){
								temp_val[col.queryEtitle].push(col.queryOption2);
							}
							if(col.queryOption3 != null){
								temp_val[col.queryEtitle].push(col.queryOption3);
							}
							if(col.queryOption4 != null){
								temp_val[col.queryEtitle].push(col.queryOption4);
							}
							if(col.queryOption5 != null){
								temp_val[col.queryEtitle].push(col.queryOption5);
							}
						}else{//是单选
							temp_val[col.queryEtitle] = "";
						}
					}
				});
				ty = "";
				$.each(res.data.query, function(i, col) {//组织条件的样例结构
					if(col.queryShowquery != null){//是二级节点
						if(ty.indexOf(col.queryShowquery) == -1){
							if(jQuery.isArray(temp_val[col.queryShowqueryen])){//判断前置条件是否是数组，数组意味着复选框
								var temp = [];
								$.each(temp_val[col.queryShowqueryen], function(k, vol) {//循环这个数组，每个元素转换为对象
									var tt = {};
									tt[col.queryShowqueryen] = vol;
									temp.push(tt);
								});
								temp_val[col.queryShowqueryen] = temp;
								ty += col.queryShowquery+",";
							}
						}
						
					}
				});
				temp_val_v1 = temp_val;
				
//				console.log(JSON.stringify(temp_val));
			}
		}else{
			swal('连接错误', '', 'error');
		}
	}).error(function(){
		swal('校验失败', '', 'error')
	})
	
	
	$("#createModal").modal();
}

/**
 * 组织初始化数据
 */
function info_data(){
	
}

/**
 * 筛选条件的值处理
 * e:当前操作的条件的外层DIV对象
 */
function val_handle(e, queryEtitle, queryName, type){
	var xuanze_val = "";//
	setTimeout(function(){//等待1/10秒，待css改变后再获取选择的值
		
		var queryShowqueryen = e.parent().attr("sc_su");
		var queryValue = e.parent().attr("sc_su_val");
		
		var kk = [];//复选框的值记录
		var pp = "";//单选框记录的值
		e.children().each(function(){//循环当前条件下所有的可选项
			var val_name = $(this).attr("val_name");
			if(val_name != "不限"){
				if(type == "radio"){
					if($(this).is('.active')){
						pp = val_name;
						xuanze_val += val_name+",";
					}else{
						$("#sc_conter").children().each(function(){
							if($(this).attr("sc_su")!="null"){
								if($(this).attr("sc_su")==queryEtitle){//条件的名字与其他条件的前置条件名相同
									if($(this).attr("sc_su_val") == val_name){//值包含
										$(this).children("div").each(function(){
											$(this).children().each(function(){
												$(this).removeClass("active");
												if($(this).attr("val_name") == "不限"){
													$(this).addClass("active");
												}
											});
										});
									}
								}
							}
						});
					}
				}else{
					if(ty.indexOf(queryName) >= 0){//当前的条件属于前置条件
						
						if($(this).is('.active')){
							xuanze_val += val_name+",";
						}else{
							$.each(temp_val_v1[queryEtitle], function(i, col) {
								if(col[queryEtitle] == val_name){
									$.each(col, function(k, vol) {
										if(k != queryEtitle){
											delete col[k];
										}
									});
								}
							});
							
							$("#sc_conter").children().each(function(){
								if($(this).attr("sc_su")!="null"){
									if($(this).attr("sc_su")==queryEtitle){//条件的名字与其他条件的前置条件名相同
										if($(this).attr("sc_su_val") == val_name){//值包含
											$(this).children("div").each(function(){
												$(this).children().each(function(){
													$(this).removeClass("active");
													if($(this).attr("val_name") == "不限"){
														$(this).addClass("active");
													}
												});
											});
										}
									}
								}
							});
							
						}
						
					}else{
						if($(this).is('.active')){
							kk.push(val_name);
							xuanze_val += val_name+",";
						}
					}
				}
			}
		});
		if(queryShowqueryen == "null"){//没有前置条件
			if(type == "checkbox"){
				if(ty.indexOf(queryName) >= 0){
					
				}else{
					temp_val_v1[queryEtitle] = kk;
				}
			}else{
				temp_val_v1[queryEtitle] = pp;
			}
		}else{//有前置条件，需要进一步增加层级
			if(jQuery.isArray(temp_val_v1[queryShowqueryen])){
				$.each(temp_val_v1[queryShowqueryen], function(i, col) {
					if(col[queryShowqueryen] == queryValue){
						if(type == "checkbox"){
							temp_val_v1[queryShowqueryen][i][queryEtitle] = kk;
						}else{
							temp_val_v1[queryShowqueryen][i][queryEtitle] = pp;
						}
					}
				});
			}else{
				temp_val_v1[queryEtitle] = pp;
			}
		}
		$("#sc_conter").children().each(function(){//循环所有的条件
			if($(this).attr("sc_su")!="null"){
				if($(this).attr("sc_su")==queryEtitle){//条件的名字与其他条件的前置条件名相同
					if(xuanze_val.indexOf($(this).attr("sc_su_val"))>=0){//值包含
						$(this).show();
					}else{
						$(this).hide();
					}
				}
			}
		});
	},100);
}


/**
 * 筛选按钮
 */
function search_button(){
	//条件缓存中的空值删除
	$.each(temp_val_v1, function(key, col) {
		if(jQuery.isArray(col)){//判断值是否数组
			
			var ttp = [];//有效数组
			var bool = false;//记录是否进行了处理
			$.each(col, function(i, vol) {//循环数组
				if((typeof vol=='string') && vol.constructor==String){//字符串不需处理
					
				}else{
					bool = true;
					$("#"+key).children().each(function(){//循环数组的页面，查看现有的元素是否选中
						if($(this).attr("val_name") == vol[key]){//如果当前的标签的值与数组中唯一元素的值相同，同时数组中唯一元素的key与顶层key一致，说明找到标签
							if($(this).is(".active")){//判断这个标签是否被选中，如果选中说明正常，未选中需要再数组中删除这个元素
								ttp.push(vol);//删除操作就是将有效数组放入到新数组中，循环结束一次性覆盖
							}
						}
					});
				}
			});
			
			if(bool){
				temp_val_v1[key] = ttp;
			}
			
		}else{
			if(col == ""){//不是数组，同时为空值，删除
				delete temp_val_v1[key];
			}
		}
	});

	//将本次查询的缓存加入到总条件中
	sc_val.filters.push(temp_val_v1);
	console.log(JSON.stringify(sc_val));
	
	temp_val_v1 = temp_val;
	//获取点源相应的减排占比等内容，数据加入到表格显示，刷新显示区域
	
}


/**
 * json元素的个数
 */
function JSONLength(obj) {
	var size = 0, key;
	for (key in obj) {
		if (obj.hasOwnProperty(key)) size++;
	}
	return size;
};

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
