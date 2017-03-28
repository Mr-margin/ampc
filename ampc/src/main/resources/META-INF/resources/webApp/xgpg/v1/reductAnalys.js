
/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
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
//	bar();
//	pie();
	//行业 措施联动
	$("#tradeId").change(function(){
		pie();
	});
	$("#measureId").change(function(){
		pie();
	});
	
    //地图展示切换
    $("#mapId").change(function(){
    	$("#map_showId").show();
    	$("#listModal").hide();
    	
    })
    //列表展示切换
    $("#listShow").change(function(){
    	$("#listModal").show();
    	$("#map_showId").hide();

    })
});

/****************************************************柱状图*************************************************************************/
function bar () {
	var paramsName = {"scenarinoId":233,"code":130123,"addressLevle":3,"stainType":"SO2"};
		ajaxPost('/echarts/get_barInfo',paramsName).success(function(res){
		
		var myPfChart = echarts.init(document.getElementById('pfDiv1'));
		
		var option = {
			    title : {
			        text: '减排图表',
			    },
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        },
			        formatter: function (params){
			            return params[0].name + '<br/>'
			                   + params[0].seriesName + ' : ' + params[0].value + '<br/>'
			                   + params[1].seriesName + ' : ' +  params[1].value;       //把 + params[0].value 去掉了
			        }
			    },
			    legend: {
			    	//不触动
			        selectedMode:false,
			        data:['实际排放量', '减排量']
			    },
			    grid:{
			    		show:true
			    },
	            dataZoom:[
	                      {
	                    	  show:'true',
	                    	  realtime:'true',
	                    	  start:20,
	                    	  end:80
	                    	  
	                      },
	                      {
	                    	  type:'inside',
	                    	  realtime:'true',
	                    	  start:60,
	                    	  end:80
	                      }
	                      ],
			    calculable : true,
			    xAxis : [
			        {
			        	show: true, 
			            type : 'category',
			            axisLabel : {
			            	formatter: function(category)
			            	{
			            		return category.substring(0,10);          //截取字符串
			            	}
			            },
			            data:res.data.dateResult
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value',
			            name : '吨',
			            boundaryGap: [0, 0.1],
			            splitArea : {show : true},
			            show:true
			        }
			    ],
			    series : [
			        {
			            name:'实际排放量',
			            type:'bar',
			            stack: 'sum',
			            barCategoryGap: '50%',
			            itemStyle: {
			                normal: {
			                    color: 'tomato',
			                    barBorderColor: 'tomato',
			                    barBorderWidth: 4,
			                    barBorderRadius:0,
			                   /* label : {
			                        show: true, position: 'insideTop'
			                    }*/
			                }
			            },
			            //data:res.data.pflResult
			            data:res.data.jplResult
			        },
			        {
			            name:'减排量',
			            type:'bar',
			            stack: 'sum',
			            itemStyle: {
			                normal: {
			                    color: '#fff',
			                    barBorderColor: 'tomato',
			                    barBorderWidth: 4,
			                    barBorderRadius:0,
			                    label : {
			                        /*show: true, 
			                        position: 'top',*/
			                        formatter: function (params) {
			                            for (var i = 0, l = option.xAxis[0].data.length; i < l; i++) {
			                                if (option.xAxis[0].data[i] == params.name) {
			                                    return option.series[0].data[i] + params.value;
			                                }
			                            }
			                        },
			                        textStyle: {
			                            color: 'tomato'
			                        }
			                    }
			                }
			            },
			            //data:res.data.jplResult
			            data:res.data.pflResult
			        }
			    ]
			};
			console.log(res);
			//减排量echarts
			myPfChart.setOption(option);
		
			//自适应屏幕大小变化
			window.addEventListener("resize",function(){
				 myPfChart.resize();
			 });
		
			//点击联动饼图
			myPfChart.on('datazoom', function (params){
			console.log(params);
			
			pie();
			});

	});
}
/****************************************************行业措施饼状图*************************************************************************/
function  pie () {
	var nameVal;
	var valueVal;
	var paramsName = {"scenarinoId":233,"code":130000,"addressLevle":1,"stainType":"SO2","startDate":"2017-03-04","endDate":"2017-03-29","type":1};
	ajaxPost('/echarts/get_pieInfo',paramsName).success(function(result){
		console.log(result)
		for(i=0;i<result.data.length;i++){
			nameVal = result.data[i].name;
			valueVal = result.data[i].value;
		}
	var myhycsChart = echarts.init(document.getElementById('hycsDiv1'));
	var optionPie = {
		    title : {
		        text: '饼状图',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data:[{name:nameVal}]
		    },
		    series : [
		        {
		            name: '数据比例',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:[{value:valueVal,name:nameVal}],
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
			//行业措施分担饼状图
			myhycsChart.setOption(optionPie);
			//自适应屏幕大小变化
			window.addEventListener("resize",function(){
				myhycsChart.resize();
			 });
	});		

}

