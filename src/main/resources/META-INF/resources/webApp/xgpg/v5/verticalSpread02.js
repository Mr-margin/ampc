$(function(){
	
	//	创建ECharts图表
	var myChart1 = echarts.init(document.getElementById('main1'));
	var myChart2 = echarts.init(document.getElementById('main2'));
	var myChart3 = echarts.init(document.getElementById('main3'));
	var myChart4 = echarts.init(document.getElementById('main4'));
	var myChart5 = echarts.init(document.getElementById('main5'));
	var myChart6 = echarts.init(document.getElementById('main6'));
	var myChart7 = echarts.init(document.getElementById('main7'));
	var myChart8 = echarts.init(document.getElementById('main8'));
    //指定图表的配置项和数据	
	  var option = {
			  	title: {
			  		text :'PM2.5',
			  		x:'center',
			  		y:'top'
			  	},
	            tooltip : {
	                trigger: 'axis',
	                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
	                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
	                }
	            },
	            legend: {
	            	x:'right',
	            	y:'top',
	                //data:['基准','长三角管控'],
	            },
	            grid: {
	                left: '30%',
	                right: '15%',
	                bottom: '18%',
	                //containLabel: true
	            },
	           // calculable : true,
	            xAxis : [
	                {
	                	name : '地面',
	                	nameLocation: 'start',
	                	nameGap : 20,
	                    type : 'category',
	                    data : ['50', '35', '75', '80', '100', '200', '300', '222', '500', '400']
	                },
	                {
	                	type: 'category',
	                	xAxisIndex: 1,
	                	interval:10
	                	//data : ['50', '35', '75', '80', '100', '200', '300', '222', '500', '400']
	                }
	            ],
	            yAxis : [
	                {
	                         type: 'category',
	                         name: '高度（米）',
	                         position: 'left',
	                         //max:500,
	                         data:['0','50','100','200','300','400','500','700','1000','1500','2000','3000'],
	                        
	                },
	                
	                {
	                	yAxisIndex: 1
	                }
	            ],
	            series : [
	                {
	                    name:'基准',
	                    type:'line',
	                    symbolSize: 8,
	                    hoverAnimation: false,
	                    yAxis: 1, 
	                    data:[100, 52, 200, 198, 266, 500, 752,333,452,910]
	                },
	                {
	                    name:'长三角管控',
	                    type:'line',
	                    symbolSize: 8,
	                    hoverAnimation: false,
	                    yAxis: 1, 
	                    //itemStyle:{normal:{color:'#d14a63'}},
	                    data:[11, 54, 20, 198, 66, 203, 157,233,33,444]
	                },
	           
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
	
	//myChart.hideLoding();
	//getChartData(); //与java后台交互

});


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
			myChart.hideloding();
		}

	});
}














