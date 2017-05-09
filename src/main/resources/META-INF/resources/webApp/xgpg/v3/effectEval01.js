/**
 * 初始化
 */
$(function () {
  /**
   *设置导航条信息
   */
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">时间序列</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');

});

/**
 * 全局变量
 */
var	dps_codeStation,	//设置站点信息
	dps_station,		//查询站点
	echartsData,		//该任务下所选情景的所有数据
	standardData,		//基准和观测的所有数据
	allMission;			//放置站点信息
//默认显示柱状图
var show_type = "bar";
var changeMsg = {
		pro: '',			//站点选择
		proName: '',
		city: '',
		cityName: '',
		station: '',
		stationName: '',
		rms: 'day',			//时间分辨率
		scenarinoId: [],	//选择的情景Id数组
		scenarinoName: [],	//选择的情景名称数组
};
//逐小时显示 AQI PM25 ,SO4 NO3 NH4 BC OM PMFINE, PM10 O3 SO2 NO2 CO 
//逐日显示 AQI PM25 ,SO4 NO3 NH4 BC OM PMFINE, PM10 O3_8_max O3_1_max SO2 NO2 CO 
//物种选择
var speciesArr = {
		day: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3_8_MAX', 'O3_1_MAX', 'SO2', 'NO2', 'CO'],
//		day: ['PM25', 'PM10', 'O3_8_MAX', 'O3_1_MAX', 'O3_AVG', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE'],
		
//		hour: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3', 'SO2', 'NO2', 'CO']
		hour: ['PM25', 'PM10', 'O3', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE']
};
var scenarino={			//存放基准数据id和Name
		scenarinoId:'',
		scenarinoName:'',
};
var observation={		//存放观测数据id和Name
		scenarinoId:'',
		scenarinoName:''
};
//window.onresize=function () { //浏览器调整大小后，自动对所有的图进行调整
//	try{
//		if(es){
//			 es.resize();
//		}
//		
//		}catch(e){
//	}
//};

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
		    data: [],     	//改变量 存储所选情景name
	
		 },
		 grid: {
			show: true,
			left: '3%',
			right: '3%',
			bottom: '30%',
		 },
		 dataZoom:[
		           {
		        	  show:'true',
		        	  realtime:'true',
		        	  start:0,
		        	  end:100
		           },
		          ],
         calculable: false,  
      xAxis: [  
              	{  
	        	  show: true,  
	        	  type: 'category',  
	        	  data: []  	//改变量
              	}
              ],  
      yAxis: [  
	            {  
	               name:'',  	//改变量  污染物name
	               nameLocation:'end',
	               show: true,  
	               type: 'value',  
	               splitArea: {show: false},	//去除网格阴影
	               splitLine:{show: true,		//设置网格线颜色
		                lineStyle:{
		                    color :['#D3D3D3','#00E400', '#FFFF00','#FF7E00','#FF0000','#99004C','#7E0023']
		                }
		                
		            },//去除网格线
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

var sceneInitialization = vipspa.getMessage('sceneInitialization').content;	//从路由中取到情景范围
if (!sceneInitialization) {
	sceneInitialization = JSON.parse(ls.getItem('SI'));
} else {
	ls.setItem('SI', JSON.stringify(sceneInitialization));	//不为空往session中存入一份数据
}
//console.log(JSON.stringify(sceneInitialization));

if (!sceneInitialization) {	//为空时
	sceneInittion();		//调用弹出模态框方法
} else {
	set_sce();
	initNowSession();
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
	
	setStation(sceneInitialization.taskID);
	setTime(sceneInitialization.s, sceneInitialization.e);
	$.when(dps_codeStation,dps_station).then(function () {
//		console.log(changeMsg.station);
		getdata();
	});
	
}

//var allCode = {};	//用于存储所有的站点信息
/**
 * 设置站点信息
 */
function setStation(id) {
	  $('#proStation').empty();
	  $('#cityStation').empty();
	  $('#station').empty();
	  var url = '/Site/find_codes';
	  dps_codeStation = ajaxPost_sy(url, {
	    userId: userId,
	    missionId: id
	  }).success(function (res) {
	    if (res.status == 0) {
	    	allCode = res.data;
	      for (var pro in allCode) {
	        $('#proStation').append($('<option value="'+ pro + '">' + allCode[pro].name + '</option>'))
	      }
	      var cityStation = allCode[$('#proStation').val()].city;
	      for (var city in cityStation) {
	        $('#cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
	      }

	      //var station = cityStation[$('#cityStation').val()].station;
	      //for (var s in station) {
	      //  $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
	      //}

	      changeMsg.pro = $('#proStation').val();
	      changeMsg.proName = allCode[changeMsg.pro].name;
	      changeMsg.city = $('#cityStation').val();
	      changeMsg.cityName = allCode[changeMsg.pro].city[changeMsg.city];
	      findStation(changeMsg.city);
	      //changeMsg.station = $('#station').val();
	    } else {
//	      console.log('站点请求故障！！！')
	    }
	  });
	}

/**查询站点*/
function findStation(code){
	dps_station = ajaxPost_sy('/Site/find_Site',{
    userId:userId,
    siteCode:code.substr(0, 4)
  }).success(function(res){
    $('#station').empty();
    $('#station').append($('<option value="avg">平均</option>'));
    for(var i=0;i<res.data.length;i++){
      $('#station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
    }
    changeMsg.station = $('#station').val();
    changeMsg.stationName = '平均';
  })
}

/**设置时间*/
/**只限输入毫秒数*/
function setTime(s, e) {
  s = moment(s - 0);
  e = moment(e - 0);
  $('#sTime-d').empty();
  while (true) {
    $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
    if (e.format('YYYY-MM-DD') == 'Invalid date') {
      break;
    }
    if (e.isBefore(s.add(1, 'd'))) {
      break;
    }
  }
  changeMsg.time = $('#sTime-d').val() + ' 00';
}


/**
 * 动态添加div 填数据
 */
function initEcharts() {
	var ech=[];
	var aqi;
	
	var AQI;
	var PM25;
	var SO4;
	var NO3;
	var NH4;
	var BC;
	var OM;
	var PMFINE;
	var PM10;
	var O3_8_MAX;
	var O3_1_MAX;
	var SO2;
	var NO2;
	var CO;
	
	$("#initEcharts").empty();
	
	var datas='';	//存放echarts出图需要的数据
	var tname = []; 	//污染物name
	var sceneInitialization_arr	= [];	//情景id和name
	
	var species = speciesArr[changeMsg.rms];
	for(var s = 0;s<species.length;s++){
		tname.push(species[s]);
	}
	
	var compare=$("input[name='changes']").parents("label.active").text();
	
	if("绝对量比较"==compare){
		var echartsDatas = echartsData;
		var standardDatas=standardData;
		if(standardDatas==undefined){	
			standardDatas='';
			 datas= $.extend(echartsDatas,standardDatas);
		}else{
			 datas= $.extend(echartsDatas,standardDatas);	//合并json对象
		}
		
		sceneInitialization_arr=$.extend(true,[],sceneInitialization.data); //拷贝数据
		sceneInitialization_arr.unshift(scenarino);
		sceneInitialization_arr.unshift(observation);
	}else if("相对变化"==compare){
		sceneInitialization_arr=$.extend(true,[],sceneInitialization.data);
		datas=$.extend(true,{},xdScene);
		
	}
	
	var	proStation=$("#proStation option:selected").text();
	var	cityStation=$("#cityStation option:selected").text();
	var	station=$("#station option:selected").text();
	var	domain=$("input[name='domain']").parents('label.active').text();
	
	var mesage='';
	if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"==station){
		mesage+=cityStation+">>"+domain+">>";
	
	}else if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"!=station){
		mesage+=cityStation+">>"+station+">>"+domain+">>";
	}else if("省"==proStation.substr(-1)&&"平均"==station){
		mesage+=proStation+">>"+cityStation+">>"+domain+">>";
	}else {
		mesage+=proStation+">>"+cityStation+">>"+station+">>"+domain+">>";
	}
	
	for(var i = 0;i < tname.length;i++){
		if("PM25"==tname[i]){
			var div_bj=$('<div class="row" style="padding-left:15px;"><div class="col-sm-4"><div class="input-group m-b" style="margin-bottom: 0px"><div class="" style="margin-bottom:0px;padding-left:7px;"><div class="btn-group" data-toggle="buttons"><label name="collapse" class="btn btn-outline btn-success active"><input type="radio" name="spread" value="open" checked>组分展开</label><label name="collapse" class="btn btn-outline btn-success"><input type="radio" name="spread" value="close">组分收起</label></div></div></div></div></div>');
			var div = $('<div style="height:250px;"></div>');
			div.attr("id",tname[i]);
			div.addClass('echartsCZ');
			$("#initEcharts").append(div_bj);
			$("#initEcharts").append(div);
		}else{
			var div = $('<div style="height:250px;"></div>');
			div.attr("id",tname[i]);
			div.addClass('echartsCZ');
			$("#initEcharts").append(div);
		}
		
		var option = $.extend(true,{},optionAll); 	//拷贝echarts模板
		if(tname[i] == 'AQI'){
			option.title.text = mesage +tname[i];   //加不同单位
		}else if(tname[i] != 'CO'){
			if("PM25"==tname[i]){
				option.title.text = mesage +"PM₂.₅ "+('(μg/m³)');
			}else if("SO4"==tname[i]){
				option.title.text = mesage +"SO₄²¯ "+('(μg/m³)');
			}else if("NO3"==tname[i]){
				option.title.text = mesage +"NO₃¯ "+('(μg/m³)');
			}else if("NH4"==tname[i]){
				option.title.text = mesage +"NH₄⁺ "+('(μg/m³)');
			}else if("PM10"==tname[i]){
				option.title.text = mesage +"PM₁₀ "+('(μg/m³)');
			}else if("O3_8_MAX"==tname[i]){
				option.title.text = mesage +"O₃_8_max "+('(μg/m³)');
			}else if("O3_1_MAX"==tname[i]){
				option.title.text = mesage +"O₃_1_max "+('(μg/m³)');
			}else if("SO2"==tname[i]){
				option.title.text = mesage +"SO₂ "+('(μg/m³)');
			}else if("NO2"==tname[i]){
				option.title.text = mesage +"NO₂ "+('(μg/m³)');
			}else{
				option.title.text = mesage +tname[i]+(' (μg/m³)');
			}
		}else{
			option.title.text = mesage +tname[i]+(' (mg/m³)');
		}	
		option.legend.data = (function(){	//图例名称
		var lenArr = [];
		for(var i = 0;i<sceneInitialization_arr.length;i++){
		lenArr.push(sceneInitialization_arr[i].scenarinoName);
		}
		return lenArr;
		})();
		option.series = [];
		
		for(var j = 0;j< sceneInitialization_arr.length; j++){
			var id = sceneInitialization_arr[j].scenarinoId;
			var name = sceneInitialization_arr[j].scenarinoName;
			var ttime = [];		//x轴数据
			var ydata = [];		//y轴数据	
			var keys = [];
			var vals = [];
			if(changeMsg.rms == 'day'){
				for (var prop in datas) {  		//prop--情景id-507     datas--json对象
					if (datas.hasOwnProperty(prop)) {   
						if(prop == id){ 		//循环不同的情景id
							for ( var pr in datas[prop] ) {		//无规律循环该ID下的pr--物种
								if (datas[prop].hasOwnProperty(pr)) {
									if(pr == tname[i]){			//判断是否含有该物种
										var ss = datas[prop][pr];
										for( var s in ss ) {
											if(ss.hasOwnProperty(s)){
												keys.push(s);
//												vals.push(ss[s]);
											}
//											if(ss.hasOwnProperty(s)){  //循环数据 放数据
//												dd[s] = ss[s];	//值
//												ttime.push(s);	//键
//												ydata.push(dd[s]);	//值的集合
//											}
										}
										keys = keys.sort();		//.sort()函数重新排序
										for(var m=0; m<keys.length; m++){	//根据键取值
											ttime.push(keys[m]);
											ydata.push(ss[keys[m]]);
										}
									}
//									else{		//没有该物种开始
//										var notData=[];
//										var notDataobj='';
//										tname[i]  //没有的物种名称
//										for ( var pr in datas[prop] ) {		//循环该ID下的pr--物种
//											if (datas[prop].hasOwnProperty(pr)) {
//												for(var prkey in datas[prop][pr]){
//													notData.push(prkey);
//													break;
//												}
//											}
//										}
//										notData=notData.sort();		//重新排序数据
//										for(var n=0; n<notData.length; n++){
//											notDataobj(notData[n],"-");
//										}
//										datas[prop][tname[i]]=notDataobj;
//									}	//没有该物种结束
								}
							}	
						}
					}  
				}
			}else{
				var arr = Object.keys(datas);
				for (var prop in datas) {  
					if (datas.hasOwnProperty(prop)) {   
						if(prop == id){ 	//循环不同的情景id
							for ( var pr in datas[prop] ) {
								if (datas[prop].hasOwnProperty(pr)) {
									if(pr == tname[i]){		//一个物种开始
										var ss = datas[prop][pr];
										for(var h in ss){		//一个物种中的所有数据开始
											if(ss.hasOwnProperty(h)){
												keys.push(h);	//拼接X轴数据
											}
										}	//一个物种中的所有数据结束
										keys = keys.sort();
										for(var m=0; m<keys.length; m++){		//得到所有年份
											var arr = Object.keys(ss[keys[m]]);	//得到所有年份的键
											for(var z=0;z<arr.length;z++){
												ttime.push(keys[m]+' '+z);		//名称
												ydata.push(ss[keys[m]][z]);		//数据
											}
										}
									}//一个物种结束
								}
							}	
						}
					}  
				}
			}
//			console.log(ttime);
//			console.log(ydata);
		option.series.push({
			name : name, 				//情景名称  对应图例 exceptsjz
			type : show_type, 			//图表类型   已设全局变量 show_type
			smooth : true,
			data : ydata     			//可变情景数据 
		});
	}
    option.xAxis = [];
    option.xAxis.push({				    //x轴情景时间
    	data: ttime						//修改数据排序
    });
    
//    if("AQI"==tname[i]){
//    	 AQI = echarts.init(document.getElementById(tname[i]));
//    	AQI.setOption(option);
//    	$(window).resize(AQI.resize);
//    }else if("PM25"==tname[i]){
//    	 PM25 = echarts.init(document.getElementById(tname[i]));
//    	PM25.setOption(option);
//    	$(window).resize(PM25.resize);
//    }else if("SO4"==tname[i]){
//    	 SO4 = echarts.init(document.getElementById(tname[i]));
//    	SO4.setOption(option);
//    	$(window).resize(SO4.resize);
//    }else if("NO3"==tname[i]){
//    	 NO3 = echarts.init(document.getElementById(tname[i]));
//    	NO3.setOption(option);
//    	$(window).resize(NO3.resize);
//    }else if("NH4"==tname[i]){
//    	 NH4 = echarts.init(document.getElementById(tname[i]));
//    	NH4.setOption(option);
//    	$(window).resize(NH4.resize);
//    }else if("BC"==tname[i]){
//    	 BC = echarts.init(document.getElementById(tname[i]));
//    	BC.setOption(option);
//    	$(window).resize(BC.resize);
//    }else if("OM"==tname[i]){
//    	 OM = echarts.init(document.getElementById(tname[i]));
//    	OM.setOption(option);
//    	$(window).resize(OM.resize);
//    }else if("PMFINE"==tname[i]){
//    	 PMFINE = echarts.init(document.getElementById(tname[i]));
//    	PMFINE.setOption(option);
//    	$(window).resize(PMFINE.resize);
//    }else if("PM10"==tname[i]){
//    	 PM10 = echarts.init(document.getElementById(tname[i]));
//    	PM10.setOption(option);
//    	$(window).resize(PM10.resize);
//    }else if("O3_8_MAX"==tname[i]){
//    	 O3_8_MAX = echarts.init(document.getElementById(tname[i]));
//    	O3_8_MAX.setOption(option);
//    	$(window).resize(O3_8_MAX.resize);
//    }else if("O3_1_MAX"==tname[i]){
//    	 O3_1_MAX = echarts.init(document.getElementById(tname[i]));
//    	O3_1_MAX.setOption(option);
//    	$(window).resize(O3_1_MAX.resize);
//    }else if("SO2"==tname[i]){
//    	 SO2 = echarts.init(document.getElementById(tname[i]));
//    	SO2.setOption(option);
//    	$(window).resize(SO2.resize);
//    }else if("NO2"==tname[i]){
//    	 NO2 = echarts.init(document.getElementById(tname[i]));
//    	NO2.setOption(option);
//    	$(window).resize(NO2.resize);
//    }else if("CO"==tname[i]){
//    	 CO = echarts.init(document.getElementById(tname[i]));
//    	CO.setOption(option);
//    	$(window).resize(CO.resize);
//    }
    
    
    if("AQI"!=tname[i]){
    	var es = echarts.init(document.getElementById(tname[i]));
    	es.group = 'group1';
//    	ech.push(es);
    	es.on('datazoom', function (params) {
//    		ech=[];
    		group='';
    		echarts.disconnect(group);
   	    	
   	    });
    	es.setOption(option);
    	
    	$(window).resize(es.resize);
    	
    }else{
    	 aqi = echarts.init(document.getElementById(tname[i]));
    	aqi.group = 'group1';
//    	ech.push(aqi);
    	aqi.on('datazoom', function (params) {
//    		echarts.connect(ech);
    		echarts.connect('group1');
   	    });
    	aqi.setOption(option);
    	
    	$(window).resize(aqi.resize);
    }
    
//    var es = echarts.init(document.getElementById(tname[i]));
//    es.setOption(option);
//    $(window).resize(es.resize);
  }
//	echarts.connect(ech);
//	echarts.connect([PM25, SO4, NO3, NH4, BC, OM, PMFINE, PM10, O3_8_MAX, O3_1_MAX, SO2, NO2, CO]);
	
	//逐日显示 AQI PM25 ,< SO4 NO3 NH4 BC OM PMFINE >, PM10 O3_8_max O3_1_max SO2 NO2 CO 
	//组分展开==open  收起==close	
	$('input[name=spread]').on('change', function (e) {
		var spType = $(e.target).val();
		if (spType == 'close') {
			$("#SO4").hide();
			$("#NO3").hide();
			$("#NH4").hide();
			$("#BC").hide();
			$("#OM").hide();
			$("#PMFINE").hide();
//			$(e.target.parentNode).text("组分展开");
//			$(e.target).val('open');
		} else {
			$("#SO4").show();
			$("#NO3").show();
			$("#NH4").show();
			$("#BC").show();
			$("#OM").show();
			$("#PMFINE").show();
//			$(e.target.parentNode).text("组分收起");
//			$(e.target).val('close');
		}

	});
	
	
}

/**
 * 接收/更新数据
 */
function getdata() {
	find_standard();
	var url = '/Appraisal/find_appraisal';
	var ch_scenarinoId=changeMsg.scenarinoId.join(",");	
	
	var paramsName = {
	    "userId": "1",
	    "missionId": sceneInitialization.taskID,
	    "mode": changeMsg.station=='avg'?'city':'point',
	    "cityStation": changeMsg.station=='avg'?changeMsg.city:changeMsg.station,
//	    "scenarinoId": changeMsg.scenarinoId,
		"scenarinoId": ch_scenarinoId,
	    "datetype": changeMsg.rms
	  };
	ajaxPost(url, paramsName).success(function (res) {
	    if (res.status == 0) {
	    	echartsData = res.data;
	    	if (JSON.stringify(echartsData) == '{}' || echartsData == null) {
	    		swal('暂无数据', '', 'error')
	    	} else {
	
	    	}
	    	initEcharts();
	    } else {
	      swal(res.msg, '', 'error')
	    }

	});
}

function ajaxPost_sy(url, parameter) {
	  parameterPar.data = parameter;
	  var p = JSON.stringify(parameterPar);
	  return $.ajax('/ampc' + url, {
	    contentType: "application/json",
	    type: "POST",
//	    cache : true,		//缓存读取数据较慢
	    async: false,		//同步
	    dataType: 'JSON',
	    data: p
	  });
}

/**
 * 查询基准
 * */
function find_standard(){
	var missionId=$("#task").val();
	scenarino.scenarinoId='';
	scenarino.scenarinoName='';
	observation.scenarinoId='';
	observation.scenarinoName='';
	var url='/Appraisal/find_standard';
	var paramsName = {
			"userId": "1",
			"missionId":sceneInitialization.taskID,				//任务ID
			"mode":changeMsg.station=='avg'?'city':'point',		//检测站点
			"cityStation":changeMsg.station=='avg'?changeMsg.city:changeMsg.station,	//检测站点具体值
		    "domain":$('input[name=domain]:checked').val(),		
		    "changeType":$('input[name=changes]:checked').val(),			//变化状态
			"datetype":changeMsg.rms			//时间分辨率
		  };
	ajaxPost(url, paramsName).success(function (res) {
	    if (res.status == 0) {
	    	standardData = res.data.data;	//放置基准数据
	    	scenarino.scenarinoId=res.data.scenarinoId;
	    	scenarino.scenarinoName=res.data.scenarinoName;
	    	observation.scenarinoId=res.data.observationId;
	    	observation.scenarinoName=res.data.observationName;
	    	if (JSON.stringify(standardData) == '{}' || standardData == null||standardData==undefined||standardData=='') {
	    	  swal('暂无基准匹配数据', '', 'error')
	    	} else {
	    	  
	    	}
	    } else {
	    	swal(res.msg, '', 'error')
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
	ajaxPost('/mission/find_All_mission',paramsName).success(function(res){
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
			$("#Initialization").modal({backdrop: 'static', keyboard: false});	//初始化模态框显示
        sceneTable();
      } 
//		else {
//        swal('无可用任务', '', 'error')
//      }
//    } else {
//      swal('接口故障', '', 'error')
//    }
//  }).error(function () {
//    swal('服务未连接', '', 'error')
  });
}


/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable(){
	$("#sceneTableId").bootstrapTable('destroy');	//销毁现有表格数据
	
	$("#sceneTableId").bootstrapTable({
		method : 'POST',
		url : '/ampc/scenarino/find_All_scenarino',
		dataType : "json",
		iconSize : "outline",
		clickToSelect : true,	// 点击选中行
		pagination : false, 	// 在表格底部显示分页工具栏
		striped : true, 		// 使表格带有条纹
		queryParams : function(params) {
			var data = {};
			data.userId = userId;
			data.missionId = $("#task").val();
			return JSON.stringify({"token": "","data": data});
		},
		queryParamsType : "limit", 			// 参数格式,发送标准的RESTFul类型的参数请求
		silent : true, 						// 刷新事件必须设置
		contentType : "application/json", 	// 请求远程数据的内容类型。
		responseHandler: function (res) {
			if(res.status == 0&&res.data!=null&&res.data!=''&&res.data!=undefined){
				if(res.data.hasOwnProperty('rows')){
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
					}else{
						return res;
					}
				}else{
					return res;
				}
			}else if(res.status == 1000){
				swal(res.msg, '', 'error');
			}
			else{
				return res;
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
			
    },
    onLoadError: function () {
      swal('连接错误', '', 'error');
    }
  });
}

/**
 * 保存选择的情景
 */
function save_scene() {
	standardData = '';		//清空基准数据
	scenarino.scenarinoId='';
	scenarino.scenarinoName='';
	var row = $('#sceneTableId').bootstrapTable('getSelections');	//获取所有选中的情景数据
	if (row.length > 0) {
		var mag = {};
		mag.id = "sceneInitialization";
		mag.taskID = $("#task").val();
		mag.domainId = allMission[mag.taskID].domainId;
		mag.s = allMission[mag.taskID].missionStartDate;
		mag.e = allMission[mag.taskID].missionEndDate;
        mag.jzID = allMission[mag.taskID].jzqjid;
		var data = [];
		$.each(row, function (i, col) {
			data.push({"scenarinoId": col.scenarinoId, "scenarinoName": col.scenarinoName});
		});
		mag.data = data;
		vipspa.setMessage(mag);
		ls.setItem('SI', JSON.stringify(mag));
		sceneInitialization = jQuery.extend(true, {}, mag);		//复制数据
		var arrId = [];		//放入已选的情景id 
		for (i = 0; i < mag.data.length; i++) {
			arrId.push({"id": mag.data[i].scenarinoId});
		}
		$("#close_scene").click();
		set_sce();
//    	find_standard();
		initNowSession();
	} else {
		swal('暂无数据', '', 'error');
		$("#close_scene").click();
	}
}
//超链接显示 模态框
function exchangeModal() {
  sceneInittion();
  $("#Initialization").modal();
}


/**
 * 设置情景下拉框的内容
 */
function set_sce() {
  changeMsg.scenarinoId = [];
  //changeMsg.scenarinoId
}


/**
 * 站点 点击事件 省- 市- 站点
 */
$('#proStation').on('change', function (e) {
	  var pro = $(e.target).val();
	  changeMsg.pro = pro;
	  console.log(allCode);
	  changeMsg.proName = allCode[pro].name;
	  $('#cityStation').empty();
	  var cityStation = allCode[pro].city;
	  for (var city in cityStation) {
	    $('#cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
	  }
	  changeMsg.city = $('#cityStation').val();
	  changeMsg.cityName = allCode[changeMsg.pro].city[changeMsg.city];
	  findStation(changeMsg.city);
	  
//	  $("#provinces").empty();
//	  $("#citys").empty();
//	  $("#stations").empty();
//	  $("#interspaces").empty();
//	  
//	  $("#provinces").text($("#proStation option:selected").text());
//	  $("#citys").text($("#cityStation option:selected").text());
//	  $("#stations").text($("#station option:selected").text());
//	  $("#interspaces").parents('label').text($("input[name='domain']").parents('label.active').text());
	  getdata();
});

	$('#cityStation').on('change', function (e) {
	  var city = $(e.target).val();
	  changeMsg.city = city;
	  changeMsg.cityName = allCode[changeMsg.pro].city[city]; 
	  findStation(changeMsg.city);
	  getdata();
	});

	$('#station').on('change', function (e) {
	  var station = $(e.target).val();
	  changeMsg.station = station;
	  changeMsg.stationName = $(e.target)[0].selectedOptions[0].innerHTML;
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
//		console.log(rms);
		if(rms == 'hour'){
			show_type = "line";
			getdata();
		}else{
			show_type = "bar";
			getdata();
		}
		
});


//绝对量比较==1 相对变化==2
var changeType;
var xdScene={};		//总和
$('input[name=changes]').on('change', function (e) {
	changeType = $(e.target).val();		//获取到被选中的值
	if (changeType == '1') {	//绝对量变化
		initEcharts();
	
	}else if(changeType == '2'){	//相对变化开始
		
		var xd_echartsData=echartsData;		//该任务下所选情景的所有数据
		var xd_standardData=standardData;	
		if(xd_echartsData!=""&&xd_echartsData!=null&&xd_echartsData!=undefined&&xd_standardData!=""&&xd_standardData!=null&&xd_standardData!=undefined){	//存在数据
//			var xdScene={};		//总和
			var xdSpecies={};	//物种
			var xdYear={};		//年份
			
			var time=$("input[name='rms']").parents("label.active").text();
			
			if("逐日"==time){
				$.each(xd_standardData,function (k,sv){	//k 为基准数据和观测数据
					if("基准数据"==k){
						$.each(xd_echartsData,function (sk,qj){		//k 取情景数据
							if(sk!=k && "观测数据"!=sk){		//情景类型
								$.each(sv,function (svkey,svVal){	
									$.each(qj,function (qjkey,qjval){	
										if(svkey==qjkey){	//物种
											$.each(svVal,function (sykey,syval){
												$.each(qjval,function (qjykey,qjyval){
													if(sykey==qjykey){	//年份
														$(xdYear).attr(qjykey,(((qjyval-syval)/syval).toFixed(2))*100);	//赋值
													}
												});
											});
											$(xdSpecies).attr(svkey,xdYear);
										}
									});
								});
								$(xdScene).attr(sk,xdSpecies);
							}
						});
					}
				});
				initEcharts();
//				console.log(xdScene);
			}else if("逐小时"==time){
				var hours={};	//存放逐小时数据
				
				$.each(xd_standardData,function (k,sv){	//k 为基准数据和观测数据
					if("基准数据"==k){
						$.each(xd_echartsData,function (sk,qj){		//k 取情景数据
							if(sk!=k && "观测数据"!=sk){		//情景类型
								$.each(sv,function (svkey,svVal){	
									$.each(qj,function (qjkey,qjval){	
										if(svkey==qjkey){	//物种
											$.each(svVal,function (sykey,syval){
												$.each(qjval,function (qjykey,qjyval){
													if(sykey==qjykey){	//年份
														$.each(syval,function (syhkey,syhval){
															$.each(qjyval,function (qjhkey,qjhval){
																if(syhkey==qjhkey){
																	$(hours).attr(qjhkey,(((qjhval-syhval)/syhval).toFixed(2))*100);	//赋值
																}
															});
														});
														$(xdYear).attr(qjykey,hours);
													}
												});
											});
											$(xdSpecies).attr(svkey,xdYear);
										}
									});
								});
								$(xdScene).attr(sk,xdSpecies);
							}
						});
					}
				});
				initEcharts();
//				console.log(xdScene);
			}
			
		}else{	//无数据
			
		}
				
	}	//相对变化结束
});


//空间分布率
var domain;
$('input[name=domain]').on('change', function (e) {
	domain = $(e.target).val();
	getdata();
//	console.log(domain);
});



