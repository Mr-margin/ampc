//默认显示柱状图
var show_type="bar";
/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">时间序列</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');



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
//		console.log(JSON.stringify(mag));
		sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
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
	
}






$(function(){
	//柱状图
	timeBar();

	
	
});

//逐小时显示 AQI PM25 PM10 O3 SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
$("#zhuHour").change(function(){
	show_type="line";
	timeBar();
});
//逐日显示 AQI PM25 PM10 O3_8_max O3_1_max SO2 NO2 CO SO4 NO3 NH4 BC OM PMFINE
$("#zhuDay").change(function(){
	show_type="bar";
	timeBar();
});

//绝对量比较
$("#absoluChange").change(function(){
	timeBar();
});
//相对变化
$("#relativeChange").change(function(){
	timeBar();
});

//组分展开
$("#zufenSpread").change(function(){
	$("#mapDivAQI").show();
	$("#mapDivPM25").show();
});
//组分收起
$("#zufenUp").change(function(){
	$("#mapDivPM25").hide();
	$("#mapDivPM10").hide();
	$("#mapDivO3").hide();
	$("#mapDivO38").hide();
	$("#mapDivO31").hide();
	$("#mapDivSO2").hide();
	$("#mapDivNO2").hide();
	$("#mapDivCO").hide();
	$("#mapDivSO4").hide();
	$("#mapDivNO3").hide();
	$("#mapDivNH4").hide();
	$("#mapDivBC").hide();
	$("#mapDivOM").hide();
	$("#mapDivPMFINE").hide();
});



//	创建ECharts图表
function timeBar(){
	
	var myChart1 = echarts.init(document.getElementById('mapDivAQI'));
	var myChart2 = echarts.init(document.getElementById('mapDivPM25'));
	var myChart3 = echarts.init(document.getElementById('mapDivPM10'));
	var myChart4 = echarts.init(document.getElementById('mapDivO3'));
	var myChart5 = echarts.init(document.getElementById('mapDivO38'));
	var myChart6 = echarts.init(document.getElementById('mapDivO31'));
	var myChart7 = echarts.init(document.getElementById('mapDivSO2'));
	var myChart8 = echarts.init(document.getElementById('mapDivNO2'));
	var myChart9 = echarts.init(document.getElementById('mapDivCO'));
	var myChart10 = echarts.init(document.getElementById('mapDivSO4'));
	var myChart11 = echarts.init(document.getElementById('mapDivNO3'));
	var myChart12 = echarts.init(document.getElementById('mapDivNH4'));
	var myChart13 = echarts.init(document.getElementById('mapDivBC'));
	var myChart14 = echarts.init(document.getElementById('mapDivOM'));
	var myChart15 = echarts.init(document.getElementById('mapDivPMFINE'));
	//指定图表的配置项和数据	
    var option = {  
            //标题，每个图表最多仅有一个标题控件，每个标题控件可设主副标题  
            title: {  
                //主标题文本，'\n'指定换行  
                text: 'AQI指数数据图',  
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
                data: ['观测数据','1073基数','1068杭州管控','1069长三角管控']  
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
                    data: ['2017-02-01','2016-02-03','2016-02-05','2016-02-07','2016-02-09','2016-02-11','2016-02-13','2016-02-15','2016-02-17','2016-02-19','2016-02-21','2016-02-23']  
                }  
            ],  
            //直角坐标系中纵轴数组，数组中每一项代表一条纵轴坐标轴，仅有一条时可省略数值  
            //纵轴通常为数值型，但条形图时则纵轴为类目型  
            yAxis: [  
                {  
                	name:'AQI',
                	nameLocation:'end',
                    show: true,  
                    type: 'value',  
                    //分隔区域，默认不显示  
                    splitArea: {show: true},
                    
                }  
            ],  
              
            //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
            series: [  
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '观测数据',  
                    //图表类型，动态参数，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: show_type,  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],  
                    //设置成曲线
                    smooth:true,
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {yAxis: 50, name: '基准线'}  
                        ]  
                    }  
                },  
                {  
                    name: '1073基数',  
                    type: show_type,  
                    data: [6, 11, 12, 26.4, 28.7, 80.7, 175.6, 282.2, 148.7,78.8, 66.0, 66],  
                    smooth:true,
                    markLine: {  
                        data: [  
                            {yAxis: 100, name: '基准线'}  
                        ]  
                    }  
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
                    data: [5, 9, 24, 26, 28, 70, 175, 182, 48, 33.8, 22, 8],  
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
    
	
}


//echarts 请求数据  还没写
function getChartData(){
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
}









