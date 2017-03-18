$(function(){
	//初始化模态框显示
	$("#createRwModal").modal();
	//柱状图
	bar();
	pie();
	//行业 措施联动
	$("#tradeId").change(function(){
		
		pie();
	});
	$("#measureId").change(function(){
		
		pie();
	});
	
	/**
	 *设置导航条信息
	 */
	$("#crumb").html('<a href="" style="padding-left: 15px;padding-right: 15px;"><span class="col-md-2 col-md-offset-9">切换情景范围</span></a>');
	
	
})

//下拉选框
function selectQj(value){
	if (value == 'j1' || value == 'j2') {
		$("#tableId").css('display','block');
		
	} else {
		$("#tableId").css('display','none');
	}

}
//柱状图
function bar () {
	var myPfChart = echarts.init(document.getElementById('pfDiv1'));
	var option = {
		    title : {
		        text: '减排图表',
		        //subtext: 'From ExcelHome',
		        //sublink: 'http://e.weibo.com/1341556070/AizJXrAEa'
		    },
		    tooltip : {
		        trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        },
		        formatter: function (params){
		            return params[0].name + '<br/>'
		                   + params[0].seriesName + ' : ' + params[0].value + '<br/>'
		                   + params[1].seriesName + ' : ' + (params[1].value + params[0].value);
		        }
		    },
		    legend: {
		    	//不触动
		        selectedMode:true,
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
		            data : ['2016-11-17','2016-11-22','2016-11-27','2016-12-02','2016-12-07','2016-11-10']
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
		                    label : {
		                        show: true, position: 'insideTop'
		                    }
		                }
		            },
		            data:[260, 200, 220, 120, 100, 80]
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
		                        show: true, 
		                        position: 'top',
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
		            data:[40, 80, 50, 80,80, 70]
		        }
		    ]
		};
		myPfChart.on('click', function (params) {
		console.log(params);
		pie();
		
	});
		//减排量echarts
		myPfChart.setOption(option);
		//自适应屏幕大小变化
		window.addEventListener("resize",function(){

			 myPfChart.resize();

		 });



}

//行业措施饼状图
function  pie () {

	var myhycsChart = echarts.init(document.getElementById('hycsDiv1'));
	var option2 = {
		    title : {
		        text: '行业措施饼状图',
		        //subtext: '纯属虚构',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data: ['钢铁','玻璃','水泥','独立焦化','其他工业企业','非道移动源','工业锅炉','储存运输','电力','废弃物处理源','其它','生物质燃烧源'],
		    },
		    series : [
		        {
		            name: '访问来源',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:[
		                {value:335, name:'其他工业企业'},
		                {value:310, name:'独立焦化'},
		                {value:234, name:'水泥'},
		                {value:135, name:'玻璃'},
		                {value:1203, name:'钢铁'},
		                {value:90, name:'非道移动源'},
		                {value:25, name:'工业锅炉'},
		                {value:15, name:'储存运输'},
		                {value:231, name:'电力'},
		                {value:68, name:'废弃物处理源'},
		                {value:8, name:'其它'},
		                {value:6, name:'生物质燃烧源'},
		            ],
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
			myhycsChart.setOption(option2);
			window.onresize = myhycsChart.resize;	


}








