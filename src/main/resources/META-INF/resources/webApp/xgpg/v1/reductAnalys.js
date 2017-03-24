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
	//初始化模态表
	initQdListTable();
	//获取任务name 有数据用
	//getRwName();
	
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
/****************************************************构建列表展示table*************************************************************************/    
    $("#table_listShow").bootstrapTable({
    	method:'POST',
    	url:'',
    	dataType: "json",
    	clickToSelect : true,// 点击选中行
    	pagination : false, // 在表格底部显示分页工具栏
    	singleSelect : true,//设置True 将禁止多选
    	striped : true, // 使表格带有条纹
    	pagination: true, //是否启用分页
    	sidePagination: "client",//分页方式：client客户端分页，server服务端分页（*）
    	pageNumber:1,   //初始化加载第一页，默认第一页
    	pageSize: 10,   //每页的记录行数
    	pageList: [10, 25, 50, 100],//可供选择的每页的行数
    	silent : true, // 刷新事件必须设置
    	detailView: true,//是否显示父子表
        columns: [{
            field: 'xzArea',
            title: '行政区',
            align: 'center',
            
        }, {
            field: 'PM25name',
            title: 'PM2.5',
            align: 'center'
        }, {
            field: 'PM10name',
            title: 'PM10',
            align: 'center'
        }, {
            field: 'SO2name',
            title: 'SO2',
            align: 'center'
        }, {
            field: 'NOXname',
            title: 'NOX',
            align: 'center'
        }, {
            field: 'VOCname',
            title: 'VOC',
            align: 'center'
            	
        }, {
            field: 'COname',
            title: 'CO',
            align: 'center'
        }, {
            field: 'NH3name',
            title: 'NH3',
            align: 'center'
        }, {
            field: 'BCname',
            title: 'BC',
            align: 'center'
        }, {
            field: 'OCname',
            title: 'OC',
            align: 'center'
        }, {
            field: 'PMFINEname',
            title: 'PMFINE',
            align: 'center'
        }, {
            field: 'PMCname',
            title: 'PMC',
            align: 'center'
        }],
        data:[{
        	xzArea: '杭州市',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '嘉兴市',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '湖州市',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '宁波市',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },],
      //注册加载子表的事件。注意下这里的三个参数！
        onExpandRow: function (index, row, $detail) {
            InitSubTable(index, row, $detail);
        }
    });
});
//初始化子表格(无线循环)
InitSubTable = function (index, row, $detail) {
    var cur_table = $detail.html('<table></table>').find('table');
    $(cur_table).bootstrapTable({
        url: '',
        method: 'get',
        clickToSelect: true,
        detailView: false,//父子表
        columns: [{
            field: 'xzArea',
            align: 'center'
        }, {
            field: 'PM25name',
            align: 'center'
        }, {
            field: 'PM10name',
            align: 'center'
        }, {
            field: 'SO2name',
            align: 'center'
        }, {
            field: 'NOXname',
            align: 'center'
        }, {
            field: 'VOCname',
            align: 'center'
        }, {
            field: 'COname',
            align: 'center'
        }, {
            field: 'NH3name',
            align: 'center'
        }, {
            field: 'BCname',
            align: 'center'
        }, {
            field: 'OCname',
            align: 'center'
        }, {
            field: 'PMFINEname',
            align: 'center'
        }, {
            field: 'PMCname',
            align: 'center'
        }],
        data:[{
        	xzArea: '上城区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '下城区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '江干区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },]

    });
};

//获取任务名称
function getRwName() {
	  var url = '';
	  var params = {
	    userId: userId
	  };
	  ajaxPost(url, params).success(function (res) {

	    for (var i = 0; i < res.data; i++) {
	      $('#rwName').append($('<option value=""></option>'))
	    }

	  }).error(function () {
	    console.log('模拟范围未获取到！！！！')
	  })
	  $('#rwName').append($('<option value="1">范围1</option>'))
	}
//下拉选框
function selectQj(value){
	if (value == 'j1' || value == 'j2') {
		$("#tableId").css('display','block');
		
	} else {
		$("#tableId").css('display','none');
	}
}
/****************************************************柱状图*************************************************************************/
function bar () {
	var paramsName = {"scenarinoId":"136","code":130123,"addressLevle":3,"stainType":"NOx"};
		ajaxPost('/scenarino/get_radioInfo',paramsName).success(function(res){
		
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
			            data:res.data.pflResult
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
			            data:res.data.jplResult
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
	var myhycsChart = echarts.init(document.getElementById('hycsDiv1'));
	var optionPie = {
		    title : {
		        text: '行业措施饼状图',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data: ['钢铁','玻璃','水泥','独立焦化','其他工业企业','非道移动源','工业锅炉','储存运输','电力','废弃物处理源','其它','生物质燃烧源','农业源','化工化纤','扬尘源','民用源','道路机动车','溶剂使用'],
		    },
		    series : [
		        {
		            name: '访问来源',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:[
		                {value:1000.12, name:'钢铁'},
		                {value:135, name:'玻璃'},
		                {value:234, name:'水泥'},
		                {value:310, name:'独立焦化'},
		                {value:335, name:'其他工业企业'},
		                {value:90, name:'非道移动源'},
		                {value:25, name:'工业锅炉'},
		                {value:15, name:'储存运输'},
		                {value:231, name:'电力'},
		                {value:68, name:'废弃物处理源'},
		                {value:8, name:'其它'},
		                {value:6, name:'生物质燃烧源'},
		                {value:10, name:'农业源'},
		                {value:10, name:'化工化纤'},
		                {value:10, name:'扬尘源'},
		                {value:10, name:'民用源'},
		                {value:10, name:'道路机动车'},
		                {value:10, name:'溶剂使用'},
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
			myhycsChart.setOption(optionPie);
			//自适应屏幕大小变化
			window.addEventListener("resize",function(){
				myhycsChart.resize();
			 });	
}
//超链接显示 模态框
function exchangeModal(){
	$(".createRwModal").modal();
	
}
//初始化模态框 内的表格 全选复选框 新版
function initQdListTable() {
	  $('#testTableId').bootstrapTable({
	    method: 'GET',
	      url: 'webApp/xgpg/v1/qjdata.json',
	      //url:'webApp/yqd/qdList/qd.json',
//	      url : BackstageIP+'/mission/get_mission_list',
//	      url : '/ampc/mission/get_mission_list',
	    dataType: "json",
	    contentType: "application/json",
	    //toobar: '#',
	    iconSize: "outline",
	    search: false,
	    //searchAlign: 'right',
	    //height:llqHeight-200,
	    maintainSelected: true,
	    clickToSelect: false,
	    //pagination: true,
	   // pageSize: 20,
	    //pageNumber: 1,
	    //pageList: [20],
	    striped: true,
	    sidePagination: "server",
	    rowStyle: function (row, index) {
	      return {};
	    },
	      queryParams: function formPm(m) {
	        console.log(m);
	        var json = {
	          "token": "",
	          "data": {

	          }
	        };
//	        return JSON.stringify(json);
	        return '';
	      },
	    responseHandler: function (res) {
	    	console.log(res);
	      return res.data
	    },
	    queryParamsType: "undefined",
	    silent: true,
	   /* onClickRow: function (row, $element) {

	      $('.info').removeClass('info');
	      $($element).addClass('info');
	    },*/

	    /*onCheck: function (row) {
	      $('.delQD').attr('disabled', false);
	      delQDMap[row.qdId] = 'true';
	    },
	    onUncheck: function (row) {
	      delete delQDMap[row.qdId];
	      if ($.isEmptyObject(delQDMap)) {
	        $('.delQD').attr('disabled', true)
	      }
	    },
	    onCheckAll: function (rows) {
	      $('.delQD').attr('disabled', false);
	      for (var i = 0; i < rows.length; i++) {
	        delQDMap[rows[i].qdId] = 'true';
	      }
	    },
	    onUncheckAll: function (rows) {
	      delQDMap = {};
	      $('.delQD').attr('disabled', true);
	    },*/
	    onLoadSuccess:function(data){
	    	console.log(data)
	    },
	    
	    onContextMenuItem: function (row, $el) {

	    }
	  });
	}
