/**
 * 初始化
 */
$(function(){
	/**
	 *设置导航条信息
	 */
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">时间序列</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');

});

/**
 * 全局变量
 */
//默认显示柱状图
var show_type="bar";
var changeMsg = {
		pro: '',//站点选择
		city: '',
		station: '',
		rms: 'day',//时间分辨率
		scenarinoId : [],//选择的情景Id数组
		scenarinoName : [],//选择的情景名称数组
};
//逐小时显示 AQI PM25 ,SO4 NO3 NH4 BC OM PMFINE, PM10 O3 SO2 NO2 CO 
//逐日显示 AQI PM25 ,SO4 NO3 NH4 BC OM PMFINE, PM10 O3_8_max O3_1_max SO2 NO2 CO 
//物种选择
var speciesArr = {
		day: ['AQI', 'PM25','SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3_8_MAX', 'O3_1_MAX','SO2','NO2', 'CO',],
		hour: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE','PM10','O3', 'SO2', 'NO2', 'CO' ]
};

/**
 * 设置柱状图 模板
 */
var optionAll = {  
		title: {  
			text: '',  //改变量 放污染物
			x: 'left',  
			y: 'top'  
		},  
		tooltip: {  
			trigger: 'axis'  
		},  
		legend: {  
			show: true,  
			x: 'center',  
			y: 'top',  
			//legend的data: 用于设置图例，data内的字符串数组需要与sereis数组内每一个series的name值对应  
			data: [],     //改变量 存储所选情景name

		},
		grid: {
			show: true,
			left: '3%',
			right: '3%',
			bottom: '15%',
		},
		dataZoom:[
		          {
		        	  show:'true',
		        	  realtime:'true',
		        	  start:0,
		        	  end:100

		          }
		          ],
		          calculable: false,  
		          xAxis: [  
		                  {  
		                	  show: true,  
		                	  type: 'category',  
		                	  data: []  //改变量
		                  }
		                  ],  
		          yAxis: [  
		                  {  
		                   name:'',  //改变量  污染物name
		                   nameLocation:'end',
		                   show: true,  
		                   type: 'value',  
		                   splitArea: {show: true}
		                   }
		                    ],
		            //颜色色卡
		          color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
		           //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
		          series:[]   //改变量  观测数据单独写一个接口  其它的放在一起
}; 




var ls = window.sessionStorage;
//var qjMsg = vipspa.getMessage('yaMessage').content;
//if(!qjMsg){
//	qjMsg = JSON.parse(ls.getItem('yaMsg'));
//}else{
//	ls.setItem('yaMsg',JSON.stringify(qjMsg));
//}

var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if(!sceneInitialization){
	sceneInitialization = JSON.parse(ls.getItem('SI'));
}else{
	ls.setItem('SI',JSON.stringify(sceneInitialization));//不为空往session中存入一份数据
}
console.log(JSON.stringify(sceneInitialization));

if(!sceneInitialization){//为空时
	sceneInittion();//调用弹出模态框方法
}else{
	set_sce();
	initNowSession()
}

/**
 * 初始化获取页面数据
 */
function initNowSession(){
	changeMsg.scenarinoId = [];
	changeMsg.scenarinoName = [];
	for(var i = 0;i< sceneInitialization.data.length; i++){
		changeMsg.scenarinoId.push(sceneInitialization.data[i].scenarinoId);
		changeMsg.scenarinoName.push(sceneInitialization.data[i].scenarinoName);
	}
	getdata();
}
//放置站点信息
var allStation = {};
/**
 * 设置站点信息
 */
function stationInfo(){
	$("#proStation").empty();
	$("#cityStation").empty();
	$("#station").empty();
	
	var paramsName = {};
	url = '';
	ajaxPost(url,paramsName).success(function(res){
		if(res.status == 0){
			
			allStation = res.data;
			for(var pro in allStation){
				$('#proStation').append($('<option value="' + allStation[pro].code + '">' + allStation[pro].name + '</option>'))
			}
			
			var cityStation = allStation[$('#proStation').val()].station;
			for(var c in cityStation){
				$('#cityStation').append($('<option value="' + allStation[c].code + '">' + allStation[c].name + '</option>'))
			}
			
			var station = allStation[$('#cityStation').val()].station;
			for(var s in allStation){
				$('#station').append($('<option value="' + allStation[s].code + '">' + allStation[s].name + '</option>'))
			}
			
			//存入全局变量
			changeMsg.pro=$('#proStation').val();
			changeMsg.city=$('#cityStation').val();
			changeMsg.station=$('#station').val();
		}else{
			console.log("站点请求失败！")
		}
	});
}

/**
 * 动态添加div 填数据
 */
function initEcharts(){
	var datas = echartsData;
	var dd = {};
	var ds = {};
	var tname = []; //污染物name

	var species = speciesArr[changeMsg.rms];
	for(var s = 0;s<species.length;s++){
		tname.push(species[s]);
	}
	$("#initEcharts").empty();
	for(var i = 0;i < tname.length;i++){
		var div = $('<div></div>');
		div.attr("id",tname[i]);
		div.addClass('echartsCZ');
		$("#initEcharts").append(div);
		var option = $.extend(true,{},optionAll); //复制echarts模板
		if(tname[i] == 'AQI'){
			option.title.text = tname[i];         //加不同单位
		}else if(tname[i] != 'CO'){
			option.title.text = tname[i]+('(μg/m³)');
		}else{
			option.title.text = tname[i]+('(mg/m³)');
		}	
		option.legend.data = (function(){
		var lenArr = [];
		for(var i = 0;i<sceneInitialization.data.length;i++){
		lenArr.push(sceneInitialization.data[i].scenarinoName);
		}
		return lenArr;
		})();
		option.series = [];
		for(var j = 0;j< sceneInitialization.data.length; j++){
			var id = sceneInitialization.data[j].scenarinoId;
			var name = sceneInitialization.data[j].scenarinoName;
			var ttime = [];   //x轴数据
			var ydata = [];	 //y数据	
			if(changeMsg.rms == 'day'){
				for (var prop in datas) {  
					if (datas.hasOwnProperty(prop)) {   
						if(prop == id){ //循环不同的情景id
							for ( var pr in datas[prop] ) {
								if (datas[prop].hasOwnProperty(pr)) {
									if(pr == tname[i]){
										var ss = datas[prop][pr];
										for( var s in ss ) {
											if(ss.hasOwnProperty(s)){  //循环数据 放数据
												dd[s] = ss[s];
												ttime.push(s);	
												ydata.push(dd[s]);
											}
										}
									}
								}
							}	
						}
					}  
				}
			}else{
				for (var prop in datas) {  
					if (datas.hasOwnProperty(prop)) {   
						if(prop == id){ //循环不同的情景id
							for ( var pr in datas[prop] ) {
								if (datas[prop].hasOwnProperty(pr)) {
									if(pr == tname[i]){
										var ss = datas[prop][pr];
										for(var h in ss){
											if(ss.hasOwnProperty(h)){
												for(var y in ss[h]){
													if(ss[h].hasOwnProperty(y)){
														var tt = [];
														tt = ss[h][y];
														ttime.push(h+' '+y);
														ydata.push(ss[h][y]);
													}
												}
											}
										}
									}
								}
							}	
						}
					}  
				}
			}
			option.series.push({
				name : name, 				//情景名称  对应图例 exceptsjz
				type : show_type, 			//图表类型   已设全局变量 show_type
				smooth : true,
				data : ydata     			//可变情景数据  

			});
		}
		option.xAxis = [];
		option.xAxis.push({				    //x轴情景时间
			data:ttime
		});
		var es = echarts.init(document.getElementById(tname[i]));
		es.setOption(option);
	}
}

var echartsData;
/**
 * 接收/更新数据
 */
function getdata(){
	
	var url = '/Appraisal/find_appraisal';
//	var paramsName = {"userId":"1","missionId":"393","mode":"point","time":"2016-11-27 13","cityStation":"1002A","scenarinoId":changeMsg.scenarinoId,"datetype":changeMsg.rms};
	var paramsName = {"userId":"1","missionId":"393","mode":"point","time":"2016-11-27 13","cityStation":"1002A","scenarinoId":[466,458,456],"datetype":"day"};
	
	ajaxPost(url,paramsName).success(function(res){
		if(res.status == 0){
			echartsData = res.data;
			console.log(echartsData)
			if(JSON.stringify(echartsData) == '{}'||echartsData==null){	
				swal('暂无数据', '', 'error')
			}else{
			
			}
			initEcharts();
		}else{
			swal(res.msg, '' , 'error')
		}
		
	});
}

var allMission = {};
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
				allMission[vol.missionId] = vol;
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
//			$("#Initialization").modal();//初始化模态框显示
			$("#Initialization").modal({backdrop: 'static',keyboard: false});
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
			console.log(res.data);
			if(res.status == 0&&res.data.length>0){
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
			else{
				return res;
//				swal(res.msg, '未找到匹配数据', '');
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
		mag.s = allMission[mag.taskID].missionStartDate;
		mag.e = allMission[mag.taskID].missionEndDate;
		var data = [];
		$.each(row, function(i, col) {
			data.push({"scenarinoId":col.scenarinoId,"scenarinoName":col.scenarinoName});
		});
		mag.data = data;
		vipspa.setMessage(mag);
		ls.setItem('SI',JSON.stringify(mag));
		console.log(data)
		sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
		var arrId = [];//放入已选的情景id 传传
		for(i=0; i<mag.data.length;i++){
			arrId.push({"id":mag.data[i].scenarinoId});
		}
		console.log(JSON.stringify(arrId))
		$("#close_scene").click();
		set_sce();
		initNowSession();
	}else{
		swal('暂无数据', '', 'error');
//		$("#close_scene").click();
	}
}
//超链接显示 模态框
function exchangeModal(){
	sceneInittion();
	$("#Initialization").modal();
}

/**
 * 设置情景下拉框的内容
 */
function set_sce(){
	changeMsg.scenarinoId = [];
	//changeMsg.scenarinoId
}





/**
 * 站点 点击事件 省- 市- 站点
 */
$("#proStation").on('change',function(e){
	var pro = $(e.target).val();
	changeMsg.pro = pro;
	var cityStation = allStation[pro].station;
	for(var city in cityStation){
		$("#cityStation").append('<option value="'+ cityStation[city].code +'">'+ cityStation[city].name +'</option>')
	}
	changeMsg.city = $("#cityStation").val(); //放入全局变量
	var station = cityStation[changeMsg.city].station;
	for(var st in station){
		$("#station").append('<option value="'+ station[st].code +'">'+ station[st].name +'</option>')
	}
	changeMsg.station = $("#station").val();

	getdata();
});
$("#cityStation").on('change',function(e){
	var city = $(e.target).val();
	changeMsg.city = city;
	var station=  allStation[changeMsg.pro].station[city].station;
	for(var sta in station){
		$("#station").append('<option value="'+ station[sta].code +'">'+ station[sta].name +'</option>')
	}
	changeMsg.station = $("#station").val();
	
	getdata();
});
$("#station").on('change',function(e){
	var station = $(e.target).val();
	changeMsg.station = station;
	getdata();
});

/**
 * 逐日
 */
//逐小时显示 AQI PM25 PM10 O3 SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
//逐日显示 AQI PM25 PM10 O3_8_max O3_1_max SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
$('input[name=rms]').on('change',function(e){
	var rms = $(e.target).val();
	changeMsg.rms= rms
	console.log(rms);
	if(rms == 'hour'){
		show_type = "line";
		getdata();
		
	}else{
		show_type = "bar";
		getdata();
	}
	
	
});

//绝对量比较 绝对变化
$('input[name=changes]').on('change',function(e){
	var changeType = $(e.target).val();
	console.log(changeType)
	if(changeType == '1'){
		
	}else{
		
	}
});
//逐日显示 AQI PM25 ,< SO4 NO3 NH4 BC OM PMFINE >, PM10 O3_8_max O3_1_max SO2 NO2 CO 
//组分展开收起
$('input[name=spread]').on('change',function(e){
	var spType = $(e.target).val();
	console.log(spType)
	if(spType == 'close'){
		$("#SO4").hide();
		$("#NO3").hide();
		$("#NH4").hide();
		$("#BC").hide();
		$("#OM").hide();
		$("#PMFINE").hide();
	}else{
		$("#SO4").show();
		$("#NO3").show();
		$("#NH4").show();
		$("#BC").show();
		$("#OM").show();
		$("#PMFINE").show();
	}
	
});

//空间分布率
$('input[name=domain]').on('change',function(e){
	var domain = $(e.target).val();
	console.log(domain);
});



