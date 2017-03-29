
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
	timeBar();
    
	//全选复选框 旧版 已注释
	//initTableCheckbox();

});

//下拉选框
function selectQj(value){
	if (value == 'j1' || value == 'j2') {
		$("#tableId").css('display','block');
		
	} else {
		$("#tableId").css('display','none');
	}

}
//	创建ECharts图表
function timeBar(){
	
	var myChart1 = echarts.init(document.getElementById('mainDiv1'));
	var myChart2 = echarts.init(document.getElementById('mainDiv2'));
	var myChart3 = echarts.init(document.getElementById('mainDiv3'));
	var myChart4 = echarts.init(document.getElementById('mainDiv4'));
	var myChart5 = echarts.init(document.getElementById('mainDiv5'));
	var myChart6 = echarts.init(document.getElementById('mainDiv6'));
	var myChart7 = echarts.init(document.getElementById('mainDiv7'));
	var myChart8 = echarts.init(document.getElementById('mainDiv8'));
	var myChart9 = echarts.init(document.getElementById('mainDiv9'));
	var myChart10 = echarts.init(document.getElementById('mainDiv10'));
	var myChart11 = echarts.init(document.getElementById('mainDiv11'));
	var myChart12 = echarts.init(document.getElementById('mainDiv12'));
	var myChart13 = echarts.init(document.getElementById('mainDiv13'));
	var myChart14 = echarts.init(document.getElementById('mainDiv14'));
   
	//指定图表的配置项和数据	
    var option = {  
            //标题，每个图表最多仅有一个标题控件，每个标题控件可设主副标题  
            title: {  
                //主标题文本，'\n'指定换行  
                text: 'AQI指数数据图',  
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
                            {yAxis: 50, name: '基准线'}  
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
                            {yAxis: 100, name: '基准线'}  
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
                    }  
                  
                }, 
                {  
                    //系列名称，如果启用legend，该值将被legend.data索引相关  
                    name: '1069长三角管控',  
                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
                    type: 'bar',  
                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
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

//超链接显示 模态框
function exchangeModal(){
	$(".createRwModal").modal();
	
}
//全选复选框 旧版
/*function initTableCheckbox() {  
    var $thr = $('table thead tr');  
    var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');  
    将全选/反选复选框添加到表头最前，即增加一列  
    $thr.prepend($checkAllTh);  
    “全选/反选”复选框  
    var $checkAll = $thr.find('input');  
    $checkAll.click(function(event){  
        将所有行的选中状态设成全选框的选中状态  
        $tbr.find('input').prop('checked',$(this).prop('checked'));  
        并调整所有选中行的CSS样式  
        if ($(this).prop('checked')) {  
            $tbr.find('input').parent().parent().addClass('warning');  
        } else{  
            $tbr.find('input').parent().parent().removeClass('warning');  
        }  
        阻止向上冒泡，以防再次触发点击操作  
        event.stopPropagation();  
    });  
    点击全选框所在单元格时也触发全选框的点击操作  
    $checkAllTh.click(function(){  
        $(this).find('input').click();  
    });  
    var $tbr = $('table tbody tr');  
    var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');  
    每一行都在最前面插入一个选中复选框的单元格  
    $tbr.prepend($checkItemTd);  
    点击每一行的选中复选框时  
    $tbr.find('input').click(function(event){  
        调整选中行的CSS样式  
        $(this).parent().parent().toggleClass('warning');  
        如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态  
        $checkAll.prop('checked',$tbr.find('input:checked').length == $tbr.length ? true : false);  
        阻止向上冒泡，以防再次触发点击操作  
        event.stopPropagation();  
    });  
    点击每一行时也触发该行的选中操作  
    $tbr.click(function(){  
        $(this).find('input').click();  
    });  
} 
*/
//模态框 内的表格 全选复选框 新版
$("#timeTable").bootstrapTable({
	method:'POST',
	url:'',
	dataType: "json",
	cache: false,         //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性
	clickToSelect : true,// 点击选中行
	pagination : false, // 在表格底部显示分页工具栏
	singleSelect : false,//设置True 将禁止多选
	striped : true, // 使表格带有条纹
	silent : true, // 刷新事件必须设置
    columns: [{
    	checkbox:true
    },{
        field: 'qjName',
        title: '情景名称',
        align: 'center',
        
    }, {
        field: 'qjDis',
        title: '情景内容',
        align: 'center'
    }, {
        field: 'qjTime',
        title: '时间',
        align: 'center'
    }],
    data:[{
    	qjName: '情景一',
    	qjDis: '情景描述',
    	qjTime: '2017-03-19',
    },{
    	qjName: '情景二',
    	qjDis: '情景描述',
    	qjTime: '2017-03-20',
    },{
    	qjName: '情景三',
    	qjDis: '情景描述',
    	qjTime: '2017-03-20',
    }]
});









