$(function(){
	//初始化模态框显示
	$(".createRwModal").modal();
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
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
	//全选复选框
    initTableCheckbox(); 
    //地图展示切换
    $("#mapId").change(function(){
    	$("#map_showId").show();
    	$("#listModal").hide();
    	
    })
    $("#listShow").change(function(){
    	$("#listModal").show();
    	$("#map_showId").hide();

    })
    //构建列表展示table  
    $("#table_listShow").bootstrapTable({
    	method:'POST',
    	url:'',
    	dataType: "json",
    	clickToSelect : true,// 点击选中行
    	pagination : false, // 在表格底部显示分页工具栏
    	singleSelect : true,//设置True 将禁止多选
    	striped : false, // 使表格带有条纹
    	silent : true, // 刷新事件必须设置
        columns: [{
            checkbox: true
        }, {
            field: 'xzArea',
            title: '行政区'
        }, {
            field: 'PM2.5',
            title: 'PM2.5'
        }, {
            field: 'PM10',
            title: 'PM10'
        }, {
            field: 'SO2',
            title: 'SO2'
        }, {
            field: 'NOX',
            title: 'NOX'
        }, {
            field: 'VOC',
            title: 'VOC'
        }, {
            field: 'CO',
            title: 'CO'
        }, {
            field: 'NH3',
            title: 'NH3'
        }, {
            field: 'BC',
            title: 'BC'
        }, {
            field: 'OC',
            title: 'OC'
        }, {
            field: 'PMFINE',
            title: 'PMFINE'
        }, {
            field: 'PMC',
            title: 'PMC'
        }]
    	
    })
    	
  	
});
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



