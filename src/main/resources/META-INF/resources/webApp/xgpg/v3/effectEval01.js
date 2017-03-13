$(function(){
	//	创建ECharts图表
	var myChart = echarts.init(document.getElementById('main'));
	var myChartTwo = echarts.init(document.getElementById('mainTwo'));
    //指定图表的配置项和数据	
    var option = {  
            //标题，每个图表最多仅有一个标题控件，每个标题控件可设主副标题  
            title: {  
                //主标题文本，'\n'指定换行  
                text: '',  
                //主标题文本超链接  
                link: '',  
                //副标题文本，'\n'指定换行  
                subtext: '',  
                //副标题文本超链接  
                sublink: '',  
                //水平安放位置，默认为左侧，可选为：'center' | 'left' | 'right' | {number}（x坐标，单位px）  
                x: 'left',  
                //垂直安放位置，默认为全图顶端，可选为：'top' | 'bottom' | 'center' | {number}（y坐标，单位px）  
                y: 'top'  
            },  
            //提示框，鼠标悬浮交互时的信息提示  
            tooltip: {  
                //触发类型，默认（'item'）数据触发，可选为：'item' | 'axis'  
                trigger: 'axis'  
            },  
            //图例，每个图表最多仅有一个图例  
            legend: {  
                //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
                show: true,  
                //水平安放位置，默认为全图居中，可选为：'center' | 'left' | 'right' | {number}（x坐标，单位px）  
                x: 'center',  
                //垂直安放位置，默认为全图顶端，可选为：'top' | 'bottom' | 'center' | {number}（y坐标，单位px）  
                y: 'top',  
                //legend的data: 用于设置图例，data内的字符串数组需要与sereis数组内每一个series的name值对应  
                data: ['观测数据','1073基数','1068杭州管控','1069长三角管控']  
            },
            grid: {
                left: '30px',
                right: '30px',
                bottom: '40px',
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
            //工具箱，每个图表最多仅有一个工具箱  
            toolbox: {  
                /*//显示策略，可选为：true（显示） | false（隐藏），默认值为false  
                show: true,  
                //启用功能，目前支持feature，工具箱自定义功能回调处理  
                feature: {  
                    //辅助线标志  
                    mark: {show: true},  
                    //dataZoom，框选区域缩放，自动与存在的dataZoom控件同步，分别是启用，缩放后退  
                    dataZoom: {  
                        show: true,  
                         title: {  
                            dataZoom: '区域缩放',  
                            dataZoomReset: '区域缩放后退'  
                        }  
                    },  
                    //数据视图，打开数据视图，可设置更多属性,readOnly 默认数据视图为只读(即值为true)，可指定readOnly为false打开编辑功能  
                    dataView: {show: true, readOnly: true},  
                    //magicType，动态类型切换，支持直角系下的折线图、柱状图、堆积、平铺转换  
                    magicType: {show: true, type: ['line', 'bar']},  
                    //restore，还原，复位原始图表  
                    restore: {show: true},  
                    //saveAsImage，保存图片（IE8-不支持）,图片类型默认为'png'  
                    saveAsImage: {show: true}  
                }  */
            },  
            //是否启用拖拽重计算特性，默认关闭(即值为false)  
            calculable: true,  
            //直角坐标系中横轴数组，数组中每一项代表一条横轴坐标轴，仅有一条时可省略数值  
            //横轴通常为类目型，但条形图时则横轴为数值型，散点图时则横纵均为数值型  
            xAxis: [  
                {  
                    //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
                    show: true,  
                    //坐标轴类型，横轴默认为类目型'category'  
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
                    //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
                    show: true,  
                    //坐标轴类型，纵轴默认为数值型'value'  
                    type: 'value',  
                    //分隔区域，默认不显示  
                    splitArea: {show: true}  
                }  
            ],  
              
            //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
            series: [  
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '观测数据',  
                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: 'bar',  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],  
                    
                    //系列中的数据标注内容  
                    markPoint: {  
                        data: [  
                            {type: 'max', name: '最大值'},  
                            {type: 'min', name: '最小值'}  
                        ]  
                    },  
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {type: 'average', name: '平均值'}  
                        ]  
                    }  
                },  
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '1073基数',  
                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: 'bar',  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [6, 11, 12, 26.4, 28.7, 80.7, 175.6, 282.2, 148.7,78.8, 66.0, 66],  
                    //系列中的数据标注内容  
                    markPoint: {  
                        data: [  
                            {type: 'max', name: '最大值'},  
                            {type: 'min', name: '最小值'}  
                        ]  
                    },  
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {type: 'average', name: '平均值'}  
                        ]  
                    }  
                },
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '1068杭州管控',  
                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: 'bar',  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [6, 9, 24, 26.4, 28.7, 70.7, 188, 222, 333, 144, 55, 33],  
                    //系列中的数据标注内容  
                    markPoint: {  
                        data: [  
                            {type: 'max', name: '最大值'},  
                            {type: 'min', name: '最小值'}  
                        ]  
                    },  
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {type: 'average', name: '平均值'}  
                        ]  
                    }  
                }, 
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '1069长三角管控',  
                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: 'bar',  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
                    data: [5, 9, 24, 26, 28, 70, 175, 182, 48, 33.8, 22, 8],  
                    //系列中的数据标注内容  
                    markPoint: {  
                        data: [  
                            {type: 'max', name: '最大值'},  
                            {type: 'min', name: '最小值'}  
                        ]  
                    },  
                    //系列中的数据标线内容  
                    markLine: {  
                        data: [  
                            {type: 'average', name: '平均值'}  
                        ]  
                    }  
                }
                
            ]  
        }; 

	myChart.setOption(option);
	myChartTwo.setOption(option);
	
	//getChartData(); //与java后台交互

});


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
			myChart.hideloding();
		}

	});
}*/














