/**
 * 初始化
 */
$(function(){
	/**
	 *设置导航条信息
	 */
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">时间序列</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');

	//柱状图
	//timeBar();

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
		time: '',//时间选择
		scenarinoId : [],//选择的情景Id数组
		scenarinoName : [],//选择的情景名称数组
};
//逐小时显示 AQI PM25 PM10 O3 SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
//逐日显示 AQI PM25 PM10 O3_8_max O3_1_max SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
//物种选择
var speciesArr = {
		day: ['AQI', 'PM25', 'PM10', 'O3_8_max', 'O3_1_max', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE'],
		hour: [ 'AQI', 'PM25', 'PM10', 'O3', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE']
};

/**
 * 设置图表 模板
 */
var optionAll = {  

		title: {  
			text: 'AQI指数数据图',  //改变量放污染物
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
			data: ['观测数据','1073基数','1068杭州管控','1069长三角管控'],     //改变量 存储所选情景name

		},
		grid: {
			show: true,
			left: '3%',
			right: '3%',
			bottom: '20%',
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
		                	  data: ['2017-02-01','2016-02-03','2016-02-05','2016-02-07','2016-02-09','2016-02-11','2016-02-13','2016-02-15','2016-02-17','2016-02-19','2016-02-21','2016-02-23']  //改变量
		                  }
		                  ],  
		                  yAxis: [  
		                          {  
		                        	  name:'AQI',  //改变量  污染物name
		                        	  nameLocation:'end',
		                        	  show: true,  
		                        	  type: 'value',  
		                        	  splitArea: {show: true}
		                          }
		                          ],
		                          //颜色色卡
		                          color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
		                          //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
		                          series: [    //改变量  观测数据单独写一个接口  其它的放在一起
		                                       {  
		                                    	   name: '观测数据',  //改变量
		                                    	   //图表类型，动态参数，必要参数！如为空或不支持类型，则该系列数据不被显示。  
		                                    	   type: show_type,  
		                                    	   data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],  //改变量
		                                    	   symbol:'none',
		                                    	   smooth:true,
		                                    	   //系列中的数据标线内容  
		                                    	   markLine: {  
		                                    		   data: [  
		                                    		          {
		                                    		        	  yAxis: 50, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#00CD00'
		                                    		        		  }
		                                    		        	  }
		                                    		          },  
		                                    		          {
		                                    		        	  yAxis: 100, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#FFFF00'
		                                    		        		  }
		                                    		        	  }
		                                    		          },
		                                    		          {
		                                    		        	  yAxis: 150, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#FF8C00'
		                                    		        		  }
		                                    		        	  }
		                                    		          },
		                                    		          {
		                                    		        	  yAxis: 200, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#FF0000'
		                                    		        		  }
		                                    		        	  }
		                                    		          },
		                                    		          {
		                                    		        	  yAxis: 400, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#800080'
		                                    		        		  }
		                                    		        	  }
		                                    		          },
		                                    		          {
		                                    		        	  yAxis: 500, 
		                                    		        	  name: '基准线',
		                                    		        	  lineStyle:{
		                                    		        		  normal:{
		                                    		        			  color:'#8B4513' //褐红8E236B 重褐色8B4513
		                                    		        		  }
		                                    		        	  }
		                                    		          }
		                                    		          ]  
		                                    	   }
		                                       },  
		                                       {  
		                                    	   name: '1073基数',  //改变量
		                                    	   type: show_type,  
		                                    	   data: [6, 11, 12, 26.4, 28.7, 80.7, 175.6, 546.2, 148.7,78.8, 66.0, 66],  //改变量
		                                    	   smooth:true,

		                                       }
		                                       ]  
}; 




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
		changeMsg.scenarinoId.push(sceneInitialization.data.scenarinoId);
		changeMsg.scenarinoName.push(sceneInitialization.data.scenarinoName);
	}
	
}
//放置站点信息
var allStation = {};
/**
 * 设置站点信息
 */
function stationInfo(){
	
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
			changeMsg.city=$('#proStation').val();
			changeMsg.station=$('#proStation').val();
		}else{
			console.log("站点请求失败！")
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
		mag.s = allMission[mag.taskID].missionStartDate;
		mag.e = allMission[mag.taskID].missionEndDate;
		var data = [];
		$.each(row, function(i, col) {
			data.push({"scenarinoId":col.scenarinoId,"scenarinoName":col.scenarinoName});
		});
		mag.data = data;
		vipspa.setMessage(mag);
		ls.setItem('SI',JSON.stringify(mag));
		console.log(JSON.stringify(mag));
		sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
		var arrId = [];//放入已选的情景id 传传
		for(i=0; i<mag.data.length;i++){
			arrId.push({"id":mag.data[i].scenarinoId});
		}
		console.log(JSON.stringify(arrId))
		$("#close_scene").click();
		set_sce();
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
	changeMsg.scenarinoId
}
















//逐小时显示 AQI PM25 PM10 O3 SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
//逐日显示 AQI PM25 PM10 O3_8_max O3_1_max SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
$('input[name=zhuTime]').on('change',function(e){
	var zhuType = $(e.target).val();
	console.log(zhuType);
	
	if(zhuType == 'd'){
		show_type = "bar";
		$("#divAQI").show();
		$("#divPM25").show();
		$("#divPM10").show();
		$("#divO38").show();
		$("#divO31").show();
		$("#divSO2").show();
		$("#divNO2").show();
		$("#divCO").show();
		$("#divSO4").show();
		$("#divNO3").show();
		$("#divNH4").show();
		$("#divBC").show();
		$("#divOM").show();
		$("#divPMFINE").show();
		$("#divO3").hide();
	}else{
		show_type = "line";
		$("#mapDivAQI").show();
		$("#mapDivPM25").show();
		$("#mapDivPM10").show();
		$("#mapDivO3").show();
		$("#mapDivSO2").show();
		$("#mapDivNO2").show();
		$("#mapDivCO").show();
		$("#mapDivSO4").show();
		$("#mapDivNO3").show();
		$("#mapDivNH4").show();
		$("#mapDivBC").show();
		$("#mapDivOM").show();
		$("#mapDivPMFINE").show();
		$("#mapDivO38").hide();
		$("#mapDivO31").hide();
	}
	timeBar();	
	
});

//绝对量比较 绝对变化
$('input[name=changes]').on('change',function(e){
	var changeType = $(e.target).val();
	console.log(changeType)
	if(changeType == '1'){
		timeBar();
	}else{
		timeBar();
	}
});

//组分展开收起
$('input[name=spread]').on('change',function(e){
	var spType = $(e.target).val();
	if(spType == 'open'){
		$("#divAQI").show();
		$("#divPM25").show();
		$("#divPM10").show();
		$("#divO3").show();
	}else{
		$("#divPM25").show();
		$("#divPM10").show();
		$("#divO3").hide();
		$("#divO38").hide();
		$("#divO31").hide();
		$("#divSO2").hide();
		$("#divNO2").hide();
		$("#divCO").hide();
		$("#divSO4").hide();
		$("#divNO3").hide();
		$("#divNH4").hide();
		$("#divBC").hide();
		$("#divOM").hide();
		$("#divPMFINE").hide();
	}
	
});

//空间分布率
$('input[name=domain]').on('change',function(e){
	var domain = $(e.target).val();
	console.log(domain);
});



//	创建ECharts图表
function timeBar(){
	var paramsName = {};
//	$.get('webApp/v3/echarts.json',paramsName).success(function(res){});
	
	var myChart1 = echarts.init(document.getElementById('divAQI'));
	var myChart2 = echarts.init(document.getElementById('divPM25'));
	var myChart3 = echarts.init(document.getElementById('divPM10'));
	var myChart4 = echarts.init(document.getElementById('divO3'));
	var myChart5 = echarts.init(document.getElementById('divO38'));
	var myChart6 = echarts.init(document.getElementById('divO31'));
	var myChart7 = echarts.init(document.getElementById('divSO2'));
	var myChart8 = echarts.init(document.getElementById('divNO2'));
	var myChart9 = echarts.init(document.getElementById('divCO'));
	var myChart10 = echarts.init(document.getElementById('divSO4'));
	var myChart11 = echarts.init(document.getElementById('divNO3'));
	var myChart12 = echarts.init(document.getElementById('divNH4'));
	var myChart13 = echarts.init(document.getElementById('divBC'));
	var myChart14 = echarts.init(document.getElementById('divOM'));
	var myChart15 = echarts.init(document.getElementById('divPMFINE'));
	
	//指定图表的配置项和数据	
    var option = {  
            
            title: {  
                text: 'AQI指数数据图',  //改变量
                x: 'left',  
                y: 'top'  
            },  
            tooltip: {  
                //触发类型，默认（'item'）数据触发，可选为：'item' | 'axis'  
                trigger: 'axis'  
            },  
            legend: {  
                //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
                show: true,  
                x: 'center',  
                y: 'top',  
                //legend的data: 用于设置图例，data内的字符串数组需要与sereis数组内每一个series的name值对应  
                data: ['观测数据','1073基数','1068杭州管控','1069长三角管控'],//改变量
                /*selected:{
                	'1073基数' : false
                	
                }*/
               
            },
            grid: {
            	//直角坐标系网格
            	show: true,
                left: '3%',
                right: '3%',
                bottom: '20%',
            },
            dataZoom:[
                      {
                    	  show:'true',
                    	  realtime:'true',
                    	  start:0,
                    	  end:100
                    	  
                      }
                      ],
            //是否启用拖拽重计算特性，默认关闭(即值为false)  
            calculable: false,  
            //直角坐标系中横轴数组，数组中每一项代表一条横轴坐标轴，仅有一条时可省略数值  
            //横轴通常为类目型，但条形图时则横轴为数值型，散点图时则横纵均为数值型  
            xAxis: [  
                {  
                    show: true,  
                    type: 'category',  
                    //类目型坐标轴文本标签数组，指定label内容。 数组项通常为文本，'\n'指定换行  
                    data: ['2017-02-01','2016-02-03','2016-02-05','2016-02-07','2016-02-09','2016-02-11','2016-02-13','2016-02-15','2016-02-17','2016-02-19','2016-02-21','2016-02-23']  //改变量
                }
            ],  
            //直角坐标系中纵轴数组，数组中每一项代表一条纵轴坐标轴，仅有一条时可省略数值  
            //纵轴通常为数值型，但条形图时则纵轴为类目型  
            yAxis: [  
                {  
                	name:'AQI',//改变量
                	nameLocation:'end',
                    show: true,  
                    type: 'value',  
                    //分隔区域，默认不显示  
                    splitArea: {show: true}
                }
            ],
            //颜色色卡
            color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
            //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
            series: [  //改变量
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '观测数据',  //改变量
                    //图表类型，动态参数，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: show_type,  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],  //改变量
                    symbol:'none',
                    //设置成曲线
                    smooth:true,
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {
                            yAxis: 50, 
                            name: '基准线',
                            lineStyle:{
                            	normal:{
                                	color:'#00CD00'
                                }
                            }
                            },  
                            {
                                yAxis: 100, 
                                name: '基准线',
                                lineStyle:{
                                	normal:{
                                    	color:'#FFFF00'
                                    }
                                }
                             },
                             {
                                 yAxis: 150, 
                                 name: '基准线',
                                 lineStyle:{
                                 	normal:{
                                     	color:'#FF8C00'
                                     }
                                 }
                              },
                              {
                                  yAxis: 200, 
                                  name: '基准线',
                                  lineStyle:{
                                  	normal:{
                                      	color:'#FF0000'
                                      }
                                  }
                              },
                              {
                                  yAxis: 400, 
                                  name: '基准线',
                                  lineStyle:{
                                  	normal:{
                                      	color:'#800080'
                                      }
                                  }
                              },
                              {
                                  yAxis: 500, 
                                  name: '基准线',
                                  lineStyle:{
                                  	normal:{
                                      	color:'#8B4513' //褐红8E236B 重褐色8B4513
                                      }
                                  }
                              }
                        ]  
                    }
                },  
                {  
                    name: '1073基数',  //改变量
                    type: show_type,  
                    data: [6, 11, 12, 26.4, 28.7, 80.7, 175.6, 546.2, 148.7,78.8, 66.0, 66],  //改变量
                    smooth:true,
                      
                },
                {  
                    name: '1068杭州管控',  
                    type: show_type, 
                    smooth:true,
                    data: [6, 9, 24, 26.4, 28.7, 70.7, 188, 222, 333, 144, 55, 33],  
                  
                }, 
                {  
                    name: '1069长三角管控',  
                    type: show_type,
                    smooth:true,
                    data: [5, 9, 24, 26, 28, 70, 175, 626, 48, 33.8, 22, 8],  
                }
            ]  
        }; 
    


    myChart1.setOption(option);
    myChart2.setOption(option);
    myChart3.setOption(option);
    myChart4.setOption(option);
    myChart5.setOption(option);
    myChart6.setOption(option);
    myChart7.setOption(option);
    myChart8.setOption(option);
    myChart9.setOption(option);
    myChart10.setOption(option);
    myChart11.setOption(option);
    myChart12.setOption(option);
    myChart13.setOption(option);
    myChart14.setOption(option);
    myChart15.setOption(option);
    
    //图标改成自适应大小
    window.addEventListener("resize",function(){
    	
    	myChart1.resize();
    	myChart2.resize();
    	myChart3.resize();
    	myChart4.resize();
    	myChart5.resize();
    	myChart6.resize();
    	myChart7.resize();
    	myChart8.resize();
    	myChart9.resize();
    	myChart10.resize();
    	myChart11.resize();
    	myChart12.resize();
    	myChart13.resize();
    	myChart14.resize();
    	myChart15.resize();
    	
    });
    //图表联动
	echarts.connect([myChart1,myChart2,myChart3,myChart4,myChart5,myChart6,myChart7,myChart8,myChart9,myChart10,myChart11,myChart12,myChart13,myChart14,myChart15]);

}





//echarts 请求数据  还没写
/*function getChartData(){
	var options = myChart.getOption();
	$.ajax({
		type:"post",
		async:false,
		url:"xxxx.do",
		data:{},
		dataType:"json",
		success:function(result){
			if (result) {
				//legend categrop data0 data1 data2 data3 后台定义的属性
				options.legend.data = result.legend;
				options.xAxis[0].data = result.timeData;
				options.series[0].data = result.series[0].data0;
				options.series[1].data = result.series[1].data1;
				options.series[2].data = result.series[2].data2;
				options.series[3].data = result.series[3].data3;
				
				myCharts.hideLoading();
				myCharts.setOption(options);
				
			} 
		},
		error:function(){
			alert("请求数据失败！！！");
		}

	});
}*/









