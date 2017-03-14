$(function(){
	
	//	创建ECharts图表
	var myChart = echarts.init(document.getElementById('main'));
	var myChartTwo = echarts.init(document.getElementById('mainTwo'));
	var myCharThree = echarts.init(document.getElementById('mainThree'));
	var myCharFour = echarts.init(document.getElementById('mainFour'));
	
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
	                    data : ['9.15', '9.16', '9.17', '9.18', '9.19', '9.20', '9.21', '9.22', '9.23', '9.24']
	                }
	            ],
	            yAxis : [
	                {
	                         type: 'value',
	                         name: '高度（米）',
	                         position: 'left',
	                         max:500,
	                        
	                },
	                {
	                    type: 'value',
	                    position: 'right',
	                  
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
	                    data:[10, 52, 20, 98, 66, 200, 152,333,452,410]
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

	myChart.setOption(option);
	myChartTwo.setOption(option);
	myCharThree.setOption(option);
	myCharFour.setOption(option);
	
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














