$(function(){
	//初始化模态框显示
	$(".createRwModal").modal();	
	//垂直分布
	czBar();
	/**
	 *设置导航条信息
	 */
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
	//全选复选框
	initTableCheckbox();
	
	
});
//下拉选框
function selectQj(value){
	if (value == 'j1' || value == 'j2') {
		$("#tableId").css('display','block');
		
	} else {
		$("#tableId").css('display','none');
	}

}

//垂直分布柱状图
function czBar(){
	
	var myChart1 = echarts.init(document.getElementById('main1'));
	var myChart2 = echarts.init(document.getElementById('main2'));
	var myChart3 = echarts.init(document.getElementById('main3'));
	var myChart4 = echarts.init(document.getElementById('main4'));
	var myChart5 = echarts.init(document.getElementById('main5'));
	var myChart6 = echarts.init(document.getElementById('main6'));
	var myChart7 = echarts.init(document.getElementById('main7'));
	var myChart8 = echarts.init(document.getElementById('main8'));
    //指定图表的配置项和数据	
	  /*var option = {
			  	title: {
			  		text :'PM2.5',
			  		x:'center',
			  		y:'top'
			  	},
	            tooltip : {
	                trigger: 'axis',
	                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
	                    type : 'line'          // 默认为直线，可选为：'line' | 'shadow'
	                }
	            },
	            legend: {
	            	x:'right',
	            	y:'top',
	                //data:['基准','长三角管控'],  //后面设成一行 一个基准 ，其他三个不要，标题放中间
	                borderWidth:0,             //表格宽度
	            },
	            grid: {
	                left: '30%',
	                right: '15%',
	                bottom: '18%',
	            },
	            xAxis : [
	                {
	                	name : '地面',
	                	nameLocation: 'start',
	                	nameGap : 20,
	                	axisTick:{inside:true},
	                    type : 'category',      //类目轴 要对应放数据
	                    data : ['0', '1', '1.5', '2', '2.5', '3', '4.5', '5', '5.5', '6']
	                },
	                {
	                	type: 'category',
	                	xAxisIndex: 1
	                	//interval:10
	                	//data : ['50', '35', '75', '80', '100', '200', '300', '222', '500', '400']
	                }
	            ],
	            yAxis : [
	                {
	                         type: 'value',
	                         name: '高度（米）',
	                         position: 'left',
	                         max:3000,
	                         data:['0','50','100','200','300','400','500','700','1000','1500','2000','3000'],
	                         axisTick:{inside:true},  //刻度朝内侧
	               
	                },
	                {
	                	axisTick:{inside:true},
	                	yAxisIndex: 1
	                }
	            ],
	            series : [
	                {
	                    name:'基准',
	                    type:'line',
	                    smooth:true,
	                    symbolSize: 3,
	                    hoverAnimation: false,
	                    yAxis: 1, 
	                    data:[212, 321, 400, 350, 400, 800, 862,950,1200,2322]
	                },
	                {
	                    name:'长三角管控',
	                    type:'line',
	                    smooth:true,
	                    symbolSize: 3,
	                    hoverAnimation: false,
	                    yAxis: 1, 
	                    data:[66, 105, 123, 124, 125, 126, 130,243,432,600]
	                },
	           
	            ]
	        };*/
	option = {
		    legend: {
		        data:['基准','长三角管控']
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            /*dataView : {show: true, readOnly: false},
		            magicType : {show: true, type: ['line', 'bar']},
		            restore : {show: true},
		            saveAsImage : {show: true}*/
		        }
		    },
		    calculable : true,
		    tooltip : {
		        trigger: 'axis',
		        //formatter: "Temperature : <br/>{b}km : {c}°C"
		    },
		    xAxis : [
		        {
		            type : 'value',
		            axisLabel : {
		                formatter: '{value} °C'
		            }
		        },
		        {
		        	type : 'value',
		        	xAxisIndex: 1
		        }
		    ],
		    yAxis : [
		        {
		            type : 'category',
		            axisLine : {onZero: false},
		            axisTick:{inside:true},  //刻度朝内侧
		            yAxis: 1, 
		            axisLabel : {
		                formatter: '{value}'
		            },
		            boundaryGap : false,
		            //min:50,
		            //max:3000
		            data :['0','50','100','200','300','400','500','700','1000','1500','2000','3000']
		        }
		    ],
		    series : [
		        {
		            name:'基准',
		            type:'line',
		            smooth:true,
		            itemStyle: {
		                normal: {
		                    lineStyle: {
		                        shadowColor : 'rgba(0,0,0,0.4)'
		                    }
		                }
		            },
		            data:[15, -50, -56.5, -46.5, -22.1, -2.5, -27.7, -55.7, -76.5,-15,-45,-58]
		        },
		        {
		            name:'长三角管控',
		            type:'line',
		            smooth:true,
		            itemStyle: {
		                normal: {
		                    lineStyle: {
		                        shadowColor : 'rgba(0,0,0,0.4)'
		                    }
		                }
		            },
		            data:[20, -40, -54.5, -41.5, -27.1, -7.5, -20.7, -54.7, -76.5,-57,-55,-61]
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
	  //屏幕自适应
	  window.addEventListener("resize",function(){
		  myChart1.resize();
		  myChart2.resize();
		  myChart3.resize();
		  myChart4.resize();
		  myChart5.resize();
		  myChart6.resize();
		  myChart7.resize();
		  myChart8.resize();
	  });
	
}
//获取数据
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


//超链接显示 模态框
function exchangeModal(){
	$(".createRwModal").modal();
	
}
//全选复选框
function initTableCheckbox() {  
    var $thr = $('table thead tr');  
    var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');  
    /*将全选/反选复选框添加到表头最前，即增加一列*/  
    $thr.prepend($checkAllTh);  
    /*“全选/反选”复选框*/  
    var $checkAll = $thr.find('input');  
    $checkAll.click(function(event){  
        /*将所有行的选中状态设成全选框的选中状态*/  
        $tbr.find('input').prop('checked',$(this).prop('checked'));  
        /*并调整所有选中行的CSS样式*/  
        if ($(this).prop('checked')) {  
            $tbr.find('input').parent().parent().addClass('warning');  
        } else{  
            $tbr.find('input').parent().parent().removeClass('warning');  
        }  
        /*阻止向上冒泡，以防再次触发点击操作*/  
        event.stopPropagation();  
    });  
    /*点击全选框所在单元格时也触发全选框的点击操作*/  
    $checkAllTh.click(function(){  
        $(this).find('input').click();  
    });  
    var $tbr = $('table tbody tr');  
    var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');  
    /*每一行都在最前面插入一个选中复选框的单元格*/  
    $tbr.prepend($checkItemTd);  
    /*点击每一行的选中复选框时*/  
    $tbr.find('input').click(function(event){  
        /*调整选中行的CSS样式*/  
        $(this).parent().parent().toggleClass('warning');  
        /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/  
        $checkAll.prop('checked',$tbr.find('input:checked').length == $tbr.length ? true : false);  
        /*阻止向上冒泡，以防再次触发点击操作*/  
        event.stopPropagation();  
    });  
    /*点击每一行时也触发该行的选中操作*/  
    $tbr.click(function(){  
        $(this).find('input').click();  
    });  
} 











