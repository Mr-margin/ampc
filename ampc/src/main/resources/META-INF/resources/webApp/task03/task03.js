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

var ls = window.localStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;

if(!qjMsg){
  qjMsg = JSON.parse(ls.getItem('yaMsg'));
}else{
  ls.setItem('yaMsg',JSON.stringify(qjMsg));
}

hyc();

/**
 * 获取用户的行业与措施，页面初始化赋值
 */
function hyc(){
	var paramsName = {};
	paramsName.userId = userId;
	paramsName.planId = qjMsg.planId;
	paramsName.timeId=qjMsg.timeId;
	paramsName.timeStartTime=qjMsg.timeStartDate;
	paramsName.timeEndTime=qjMsg.timeEndDate;
	
	ajaxPost('/plan/get_planInfo',paramsName).success(function(res){
//		console.log(JSON.stringify(res));
		var accordion_html = "";
		if(res.status == 0){
			$.each(res.data, function(i, col) {
				
				var inn = i == 0 ? "in" : "";//第一个手风琴页签打开
				accordion_html += '<div class="panel panel-default"><div class="panel-heading" style="background-color: #FFF;">';
				accordion_html += '<a data-toggle="collapse" data-parent="#accordion" style="font-weight: 700;" href="#collapse'+i+'"><h5 class="panel-title">'+col.sectorsName+'';
				if(col.count != "0"){
					accordion_html += '<code class="pull-right">已使用&nbsp;'+col.count+'&nbsp;条措施</code>';
				}
				accordion_html += '</h5></a></div><div id="collapse'+i+'" class="panel-collapse collapse '+inn+'" style="background-color: #EDF7FF;"><div class="panel-body" style="border: 0px;">';
				
				$.each(col.measureItems, function(j, vol) {
					accordion_html += '<div class="col-md-6 c6center">';
					var bggh = true;
					if(col.planMeasure.length > 0){
						$.each(col.planMeasure, function(k, ool) {//循环已选中的措施
							if(ool.measureId == vol.mid){//已选中的措施和措施列表的ID相同，标签需要更改效果
								accordion_html += '<a class="btn btn-success-cs-w" style="width:80%;" onclick="open_cs(\''+col.sectorsName+'\',\''+vol.measureame+'\',\''+vol.mid+'\',\''+ool.planMeasureId+'\');"><i class="fa fa-check"> </i>&nbsp;&nbsp;&nbsp;'+vol.measureame+'</a>';
								bggh = false;//如果已选中，在这里添加，否则需要添加未选中的标签
							}
						});
					}
					if(bggh){
						accordion_html += '<a class="btn btn-success-cs btn-outline" style="width:80%;" onclick="open_cs(\''+col.sectorsName+'\',\''+vol.measureame+'\',\''+vol.mid+'\',\'null\');"><i class="fa fa-ban"> </i>&nbsp;&nbsp;&nbsp;'+vol.measureame+'</a>';
					}
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
 * planMeasureId: 已经选中的措施ID，如果为null，证明是新建，否则为修改
 */
function open_cs(sectorsName, measureame, mid, planMeasureId){
	$("#measureame").html("措施："+measureame);//发开弹出窗，设置标题、行业、措施等内容
	$("#sectorsName").html("行业："+sectorsName);

	var paramsName = {};
	paramsName.userId = userId;
	paramsName.sectorName = sectorsName;
	paramsName.measureId = mid;
	paramsName.planId = qjMsg.planId;
	paramsName.planMeasureId = planMeasureId;
	
	sc_val = {};//初始化缓存
	sc_val.bigIndex = "应急系统新_1描述文件.xlsx";
	sc_val.smallIndex = sectorsName;//记录行业
	sc_val.filters = [];
	sc_val.summary = {};
	
	ajaxPost('/measure/get_measureQuery',paramsName).success(function(res){
//		console.log(JSON.stringify(res));
		if(res.status == 0){//返回状态正常
			if(res.data.query.length>0){//返回筛选条件
				var sc_conter = '';//页面的标签
				var name_length = 0;//记录标题的长度，用于设置每个条件的标题的宽度
				$.each(res.data.query, function(i, col) {//循环每个条件，第一次循环实现页面标签实体化
					
					var type = col.queryOptiontype.indexOf("复选")>=0 ? "checkbox" : "radio";//根据类型区别复选或单选
					var queryShowquery = col.queryShowquery == null ? '' : 'style="display:none;"';//如果有出现条件，默认隐藏
					//获取单位之外的标题长度，根据最长的标题的字数决定宽度，决定标题与单位的分割是英文的括号
					var temp_name = col.queryName.indexOf("(")>0 ? col.queryName.substring(0,col.queryName.indexOf("(")) : col.queryName;
					name_length = temp_name.length > name_length ? temp_name.length : name_length;//记录字数最多的标题
					
					sc_conter += '<div class="btn-group tiaojian" '+queryShowquery+' sc_su="'+col.queryShowqueryen+'" sc_su_val="'+col.queryValue+'">';
					sc_conter += '<span class="font-bold tiaojian-title" style="width: 90px;">'+col.queryName.replace("(","<br>(")+'</span>';
					sc_conter += '<div class="btn-group btn-group-circle" data-toggle="buttons" id="'+col.queryEtitle+'" onclick="val_handle($(this),\''+col.queryEtitle+'\',\''+col.queryName+'\',\''+type+'\',\'onclick\');">';
					if(col.queryOptiontype.indexOf("值域")>=0){//所有的值域需要自定义修改
						sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="'+type+'" class="toggle">不限</label>';
						
						for(var k = 1;k<6;k++){//循环五个指标项，
							if(col["queryOption"+k] == null){//指标项为空
								if(k>1){//不是第一个指标项，增加最后一个第六项，大于最大值
									sc_conter += '<label class="btn btn-d1" val_name="'+col["queryOption"+(k-1)]+'~"><input type="'+type+'" class="toggle">≥'+col["queryOption"+(k-1)]+'</label>';
									break;
								}
							}else{
								if(k == 1){//指标项有内容的情况下，第一个指标从零开始
									sc_conter += '<label class="btn btn-d1" val_name="0~'+(col.queryOption1-1)+'"><input type="'+type+'" class="toggle">0-'+(col.queryOption1-1)+'</label>';
								}else{//其余的指标均是上一段到本段减一
									sc_conter += '<label class="btn btn-d1" val_name="'+(col["queryOption"+(k-1)]-0)+'~'+(col["queryOption"+k]-1)+'"><input type="'+type+'" class="toggle">'+(col["queryOption"+(k-1)]-0)+'-'+(col["queryOption"+k]-1)+'</label>';
								}
								if(k == 5){//到最后一个指标项的时候，增加第六项
									sc_conter += '<label class="btn btn-d1" val_name="'+(col["queryOption"+k]-0)+'~"><input type="'+type+'" class="toggle">≥'+(col["queryOption"+k]-0)+'</label>';
									break;
								}
							}
						}
						sc_conter += '</div>';
						sc_conter += '<form role="form" class="form-inline"><div class="form-group" style="padding-left:10px;">';
						sc_conter += '<input type="name" id="'+col.queryEtitle+'_1" onchange="val_handle($(this),\''+col.queryEtitle+'\',\''+col.queryName+'\',\''+type+'\',\'onchange\');" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
						sc_conter += '</div>-<div class="form-group">';
						sc_conter += '<input type="name" id="'+col.queryEtitle+'_2" onchange="val_handle($(this),\''+col.queryEtitle+'\',\''+col.queryName+'\',\''+type+'\',\'onchange\');" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
						sc_conter += '</div></form>';
					}else{//单选或者复选项直接判断
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
				sc_conter += '<div id="se_bu" style="text-align: right;padding-right: 20px;">';
				sc_conter += '<button class="btn btn-success" onclick="search_button();" type="button">';
				sc_conter += '<i class="fa fa-search"></i>&nbsp;&nbsp;<span class="bold">筛选</span>';
				sc_conter += '</button></div>';
				
				re = new RegExp("90px;","g");
				sc_conter = sc_conter.replace(re, (name_length*16)+"px;");
				$("#sc_conter").html(sc_conter);
				
				//初始化条件结构，将前置条件与后置条件分离，前置条件中的复选框做数组处理
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
						if(ty.indexOf(col.queryShowquery) == -1){//记录前置条件的同时，判断每个前置条件只处理一次
							if(jQuery.isArray(temp_val[col.queryShowqueryen])){//判断前置条件是否是数组，数组意味着复选框
								var temp = [];
								$.each(temp_val[col.queryShowqueryen], function(k, vol) {//循环这个数组，每个元素转换为对象
									var tt = {};
									tt[col.queryShowqueryen] = vol;
									temp.push(tt);//将复选框条件的值增加到数据元素的对象中
								});
								temp_val[col.queryShowqueryen] = temp;
								ty += col.queryShowquery+",";
							}
						}
						
					}
				});
				temp_val_v1 = jQuery.extend(true, {}, temp_val);//保留空白结构的备份，下一个条件还要继续使用
//				console.log(JSON.stringify(temp_val));
			}
			
			//要显示的列
			var show_zicuoshi = "<tr><th>措施</th><th>点源实施范围</th>";
			var z_num = 8;
			if(res.data.measureColumn.length>0){
				var sum = [];
				$.each(res.data.measureColumn, function(i, col) {
					sum.push(col.sectordocEtitle);//记录英文名称
					show_zicuoshi += "<th>"+col.sectordocCtitle+"</th>"
					z_num += col.sectordocCtitle.length;
				});
				sc_val.summary.sum = sum;//将英文名称添加到计算需求的模板中
			}
			
			//要修改的列
			if(res.data.measureList.length>0){
				$.each(res.data.measureList, function(i, col) {
					show_zicuoshi += "<th>"+col.name+"</th>"
					z_num += col.name.length;
				});
			}
			show_zicuoshi += "</tr>";
			$("#show_zicuoshi_table").css("width",(z_num*20)+"px");
			$("#show_zicuoshi").html(show_zicuoshi);
			
			ajaxPost_w('http://192.168.1.116:8089/search/companyCount',{"bigIndex":"应急系统新_1描述文件.xlsx","smallIndex":sectorsName}).success(function(res){
				console.log(JSON.stringify(res));
				$("#dianyaunzushu").html("点源总数："+res.data.length);
				
				add_point(res.data);
			});
			
		}else{
			swal('连接错误', '', 'error');
		}
	}).error(function(){
		swal('校验失败', '', 'error')
	});
	
	$("#createModal").modal();
}


/**
 * 筛选条件的值处理
 * e: 当前操作的条件的外层DIV对象
 * queryEtitle: 点击的条件的en名称
 * queryName: 点击的条件的cn名称
 * type: 点击的条件的类型，用于区分复选单选
 */
function val_handle(e, queryEtitle, queryName, type, sourceType){
	var xuanze_val = "";//
	setTimeout(function(){//等待1/10秒，待css改变后再获取选择的值
		
		var queryShowqueryen = e.parent().attr("sc_su");//前置条件的名称
		var queryValue = e.parent().attr("sc_su_val");//前置条件的值
		
		var kk = [];//复选框的值记录
		var pp = "";//单选框记录的值
		
		if (sourceType == "onchange") {//操作来源是文本框的值改变
			pp = $("#"+queryEtitle+"_1").val()+"~"+$("#"+queryEtitle+"_2").val();
		}else{
			e.children().each(function(){//循环当前条件下所有的可选项
				var val_name = $(this).attr("val_name");
				if(val_name != "不限"){
					if(type == "radio"){
						if($(this).is('.active')){
							pp = val_name;
							xuanze_val += val_name+",";
							if(val_name.indexOf("~")>=0){//如果值得中间有波折号，说明是值域，为文本框赋值
								var s = val_name.split("~");
								$("#"+queryEtitle+"_1").val(s[0]);
								$("#"+queryEtitle+"_2").val(s[1]==""?999999:s[1]);
							}
						}else{
							$("#sc_conter").children().each(function(){//为单选的前置条件设置，清空后置条件的显隐设置
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
					}else{//复选框
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
		}
		
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
	
	temp_val_v1 = jQuery.extend(true, {}, temp_val);//赋值模板到操作缓存
	
	//所有查询条件初始化
	$("#sc_conter").children().each(function(){//循环一级标签
		if($(this).attr("sc_su")!="null"){//没有前置条件显示，有前置条件隐藏
			$(this).hide();
		}
		$("#se_bu").show();//筛选按钮打开
		
		//循环下级的div标签，div内的子项为单选和复选，全部初始化为不限
		$(this).children("div").each(function(){
			$(this).children().each(function(){
				$(this).removeClass("active");
				if($(this).attr("val_name") == "不限"){
					$(this).addClass("active");
				}
			});
		});
		
		//循环下级的文本框，文本框清空
		$(this).find("input").each(function(){
			$(this).val("");
		});
	});
	
	
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

function Trim(str){ 
	return str.replace(/(^\s*)|(\s*$)/g, ""); 
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
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol",  'esri/symbols/PictureMarkerSymbol',
         'esri/renderers/ClassBreaksRenderer',"esri/symbols/SimpleMarkerSymbol",'esri/dijit/PopupTemplate', "esri/geometry/Point", "esri/geometry/Extent", "esri/renderers/SimpleRenderer", "esri/graphic", 
        "dojo/_base/Color", "dojo/dom-style",'dojo/query', "esri/tasks/FeatureSet", "esri/SpatialReference", 'extras/ClusterFeatureLayer',"tdlib/gaodeLayer", "dojo/domReady!"], 
	function(Map, FeatureLayer,GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol,PictureMarkerSymbol, ClassBreaksRenderer, SimpleMarkerSymbol,PopupTemplate, Point,Extent,SimpleRenderer, Graphic,
	        Color, domStyle,query, FeatureSet, SpatialReference,ClusterFeatureLayer, gaodeLayer) {
		dong.gaodeLayer = gaodeLayer;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
		dong.Extent = Extent;
		dong.SimpleLineSymbol =SimpleLineSymbol;
		dong.Color = Color;
		dong.PopupTemplate = PopupTemplate;
		dong.ClusterFeatureLayer = ClusterFeatureLayer ;
		dong.PictureMarkerSymbol = PictureMarkerSymbol;
		dong.ClassBreaksRenderer = ClassBreaksRenderer ;
		dong.domStyle = domStyle ;
		dong.query = query;
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
		
		app.pint = new dong.GraphicsLayer({"id":"pint"});
		app.mapList[1].addLayer(app.pint);
		
		app.layer = new esri.layers.ArcGISDynamicMapServiceLayer("http://192.168.1.132:6080/arcgis/rest/services/china_gd/MapServer");//创建动态地图
		app.mapList[0].addLayer(app.layer);
		
		app.str = {
	            'markerSymbol': new SimpleMarkerSymbol('circle', 20, null, new Color([0, 0, 0, 0.25])),
	            'marginLeft': '20',
	            'marginTop': '20'
	          };
});

var point_message = "";
//地图加点
function add_point(col){
	dong.domStyle.set(dong.query('a.action.zoomTo')[0], 'display', 'none');
	point_message = col;
	var point_sz = [];
	var xmax = 0,xmin = 0,ymax = 0,ymin = 0;
	$.each(col, function(i, vol) {
		
		var x = handle_x(vol.lon);
		var y = handle_y(vol.lat);
		
		if(i == 0){
			xmax = x;
			xmin = x;
			ymax = y;
			ymin = y;
		}else{
			xmin = x < xmin ? x : xmin;
			xmax = x > xmax ? x : xmax;
			ymin = y < ymin ? y : ymin;
			ymax = y > ymax ? y : ymax;
		}
		app.str = new dong.SimpleMarkerSymbol('circle', 30,
                new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([148,0,211,0.25]), 15),
                new dong.Color([148,0,211,0.5]));
		
		var point = new dong.Point(x, y, new dong.SpatialReference({ wkid: 3857 }));
		
		point_sz[i]= {"x":x,"y":y,"attributes":3857}
	});
	
	
//	 var popupTemplate = dong.PopupTemplate({
//         'title': '',
//         'fieldInfos': [{
//           'fieldName': 'ADMINCODE',
//           'label': 'Tract: ',
//           visible: true
//         }, {
//           'fieldName': 'NAME',
//           'label': 'Name: ',
//           visible: true
//         }]
//       });
	clusterLayer = new dong.ClusterFeatureLayer({
//		   'url': 'http://services.arcgis.com/V6ZHFr6zdgNZuVG0/ArcGIS/rest/services/CT2010_pts/FeatureServer/0',
		 "data": point_sz,
         "distance": 100,
         "id": "clusters",
         "labelColor": "#fff",
         "labelOffset": 10,
//         "resolution": map.extent.getWidth() / map.width,
         "singleColor": "#888",
//         "singleTemplate": popupTemplate
      });
	
	
	
	 var picBaseUrl = 'http://static.arcgis.com/images/Symbols/Shapes/';
     var defaultSym = new dong.PictureMarkerSymbol(picBaseUrl + 'BluePin1LargeB.png', 32, 32).setOffset(0, 15);
     var renderer = new dong.ClassBreaksRenderer(defaultSym, 'clusterCount');
     var small = new dong.SimpleMarkerSymbol('circle', 30,
             new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([148,0,211,0.25]), 15),
             new dong.Color([148,0,211,0.5]));
    renderer.addBreak(0, Infinity, small);
    
    clusterLayer.setRenderer(renderer);
    app.mapList[1].addLayer(clusterLayer);
    app.mapList[1].on('click', cleanUp());
	app.mapList[1].on('key-down', function(e) {
         if (e.keyCode === 27) {
           cleanUp();
         }
       });
	console.log(point_sz)
//	dojo.connect(app.pint, "onClick", optionclick);
	var extent = new dong.Extent(xmin,ymin,xmax,ymax, new dong.SpatialReference({ wkid:3857 }));
	app.mapList[1].setExtent(extent);
	/***********************************************************/
}
function cleanUp() {
	app.mapList[1].infoWindow.hide();
    //clusterLayer.clearSingles();
  }
function error(err) {
    console.log('something failed: ', err);
  }
window.showExtents = function() {
    var extents = map.getLayer('clusterExtents');
    if ( extents ) {
      map.removeLayer(extents);
    }
    extents = new GraphicsLayer({ id: 'clusterExtents' });
    var sym = new SimpleFillSymbol().setColor(new Color([205, 193, 197, 0.5]));

    arrayUtils.forEach(clusterLayer._clusters, function(c, idx) {
      var e = c.attributes.extent;
      extents.add(new Graphic(new Extent(e[0], e[1], e[2], e[3], map.spatialReference), sym));
    }, this);
    map.addLayer(extents, 0);
  }

//点击点的事件
function optionclick(event) {
	app.mapList[0].infoWindow.hide();
	var companyId = "";
	$.each(point_message,function(i,item){
		console.log(event.graphic._extent.xmax);
		if(handle_x(item.lon) == event.graphic._extent.xmax && handle_y(item.lat) == event.graphic._extent.ymax){
			companyId = item.companyId;
		}
	})
	var ert = {};
	ert.bigIndex = "应急系统新_1描述文件.xlsx";
	ert.smallIndex = sc_val.smallIndex;
	ert.filters = [{"companyId":companyId}];
	ert.summary = sc_val.summary;
	
	ajaxPost_w('http://192.168.1.116:8089/search/companyInfo',ert).success(function(res){
		console.log(res);
		if ( res.status == "success" ) {
			
		}
//		 var content =  "22222<br><b>Order</b>: erer<br><b>SubOrder</b>: 23<br><b>Description</b>: ssss<br><b>Drainage</b>:graphic.attributes.DrainageCl " + 
//	       + "<br><a href='#' onclick=\"infoWindowMobileHandler(event)\" class='info-window-mobile-button ui-btn'>Close</a> ";
//	     app.mapList[1].infoWindow.setTitle("");
//	     app.mapList[1].infoWindow.setContent(content);
//	     app.mapList[1].infoWindow.show(event.mapPoint);
	});
}
//筛选点源列表
function point_table () {
	$('#metTable_point').bootstrapTable({
		method : 'POST',
		url : '192.168.1.116:8089/search/companyList',
		dataType : "json",
		iconSize : "outline",
		clickToSelect : true,// 点击选中行
		pagination : true, // 在表格底部显示分页工具栏
		pageSize : 10, // 页面大小
		pageNumber : 1, // 页数
		pageList : [ 10, 20, 50, 100 ],
		striped : true, // 使表格带有条纹
		sidePagination : "server",// 表格分页的位置 client||server
		queryParams : function(params) {
			return {
				pageSize : params.limit,
				pageNumber : params.offset,
			}
		},
		queryParamsType : "limit", // 参数格式,发送标准的RESTFul类型的参数请求
		silent : true, // 刷新事件必须设置
		contentType : "application/x-www-form-urlencoded", // 请求远程数据的内容类型。
		onClickRow : function(row, $element) {
			$('.success').removeClass('success');
			$($element).addClass('success');
		},
		icons : {
			refresh : "glyphicon-repeat",
			toggle : "glyphicon-list-alt",
			columns : "glyphicon-list"
		}
	});
//	 $('#renwu_select').bootstrapTable('hideColumn', 'taskId');
}