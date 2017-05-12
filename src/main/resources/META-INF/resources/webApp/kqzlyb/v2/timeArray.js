$(function(){
/**
*设置导航条信息
**/
	$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span>>><span style="padding-left: 15px;padding-right: 15px;">时间序列</span>');
	
});	//初始化结束

//格式化日期插件	
//	$("#datetimeStart").datetimepicker({
//		format: 'yyyy/mm/dd',
//		minView: 'month',
//		startView: 'month',
//		language: 'zh-CN',
//		autoclose: true,
//		todayBtn: true
//	})
//	.on('changeDate', function(ev){
//		var date = moment(ev.date).format('YYYY-MM-DD');
//		$('#datetimeEnd').datetimepicker('setStartDate', date);
//		$('#datetimeStart').datetimepicker('setEndDate', null);
//	});
//	$("#datetimeEnd").datetimepicker({
//		format: 'yyyy/mm/dd',
//		minView: 'month',
//		startView: 'month',
//		language: 'zh-CN',
//		autoclose: true,
//		todayBtn: true
//	})
//	.on('changeDate', function(ev){
//		var date = moment(ev.date).format('YYYY-MM-DD');
//		$('#datetimeStart').datetimepicker('setEndDate', date);
//		$('#datetimeEnd').datetimepicker('setStartDate', null);
//	});
////	创建ECharts图表
//	var myChart = echarts.init(document.getElementById('main'));
//	var myChartTwo = echarts.init(document.getElementById('mainTwo'));
////	指定图表的配置项和数据	
//    var option = {  
//            //标题，每个图表最多仅有一个标题控件，每个标题控件可设主副标题  
//            title: {  
//                //主标题文本，'\n'指定换行  
//                text: '',  
//                //主标题文本超链接  
//                link: '',  
//                //副标题文本，'\n'指定换行  
//                subtext: '',  
//                //副标题文本超链接  
//                sublink: '',  
//                //水平安放位置，默认为左侧，可选为：'center' | 'left' | 'right' | {number}（x坐标，单位px）  
//                x: 'left',  
//                //垂直安放位置，默认为全图顶端，可选为：'top' | 'bottom' | 'center' | {number}（y坐标，单位px）  
//                y: 'top'  
//            },  
//            //提示框，鼠标悬浮交互时的信息提示  
//            tooltip: {  
//                //触发类型，默认（'item'）数据触发，可选为：'item' | 'axis'  
//                trigger: 'axis'  
//            },  
//            //图例，每个图表最多仅有一个图例  
//            legend: {  
//                //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
//                show: true,  
//                //水平安放位置，默认为全图居中，可选为：'center' | 'left' | 'right' | {number}（x坐标，单位px）  
//                x: 'center',  
//                //垂直安放位置，默认为全图顶端，可选为：'top' | 'bottom' | 'center' | {number}（y坐标，单位px）  
//                y: 'top',  
//                //legend的data: 用于设置图例，data内的字符串数组需要与sereis数组内每一个series的name值对应  
//                data: ['观测数据','1073基数','1068杭州管控','1069长三角管控']  
//            },
//            dataZoom:[
//                      {
//                    	  show:'true',
//                    	  realtime:'true',
//                    	  start:20,
//                    	  end:30
//                    	  
//                      },
//                      {
//                    	  type:'inside',
//                    	  realtime:'true',
//                    	  start:20,
//                    	  end:30
//                      }
//                      
//                      ],
//            //工具箱，每个图表最多仅有一个工具箱  
//            toolbox: {  
//                //显示策略，可选为：true（显示） | false（隐藏），默认值为false  
//                show: true,  
//                //启用功能，目前支持feature，工具箱自定义功能回调处理  
//                feature: {  
//                    //辅助线标志  
//                    mark: {show: true},  
//                    //dataZoom，框选区域缩放，自动与存在的dataZoom控件同步，分别是启用，缩放后退  
//                    dataZoom: {  
//                        show: true,  
//                         title: {  
//                            dataZoom: '区域缩放',  
//                            dataZoomReset: '区域缩放后退'  
//                        }  
//                    },  
//                    //数据视图，打开数据视图，可设置更多属性,readOnly 默认数据视图为只读(即值为true)，可指定readOnly为false打开编辑功能  
//                    dataView: {show: true, readOnly: true},  
//                    //magicType，动态类型切换，支持直角系下的折线图、柱状图、堆积、平铺转换  
//                    magicType: {show: true, type: ['line', 'bar']},  
//                    //restore，还原，复位原始图表  
//                    restore: {show: true},  
//                    //saveAsImage，保存图片（IE8-不支持）,图片类型默认为'png'  
//                    saveAsImage: {show: true}  
//                }  
//            },  
//            //是否启用拖拽重计算特性，默认关闭(即值为false)  
//            calculable: true,  
//            //直角坐标系中横轴数组，数组中每一项代表一条横轴坐标轴，仅有一条时可省略数值  
//            //横轴通常为类目型，但条形图时则横轴为数值型，散点图时则横纵均为数值型  
//            xAxis: [  
//                {  
//                    //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
//                    show: true,  
//                    //坐标轴类型，横轴默认为类目型'category'  
//                    type: 'category',  
//                    //类目型坐标轴文本标签数组，指定label内容。 数组项通常为文本，'\n'指定换行  
//                    data: ['2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23','2016-08-23']  
//                }  
//            ],  
//            //直角坐标系中纵轴数组，数组中每一项代表一条纵轴坐标轴，仅有一条时可省略数值  
//            //纵轴通常为数值型，但条形图时则纵轴为类目型  
//            yAxis: [  
//                {  
//                	name:'AQI',
//                	nameLocation:'end',
//                    //显示策略，可选为：true（显示） | false（隐藏），默认值为true  
//                    show: true,  
//                    //坐标轴类型，纵轴默认为数值型'value'  
//                    type: 'value',  
//                    //分隔区域，默认不显示  
//                    splitArea: {show: true}  
//                }  
//            ],  
//              
//            //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象  
//            series: [  
//                {  
//                    //系列名称，如果启用legend，该值将被legend.data索引相关  
//                    name: '观测数据',  
//                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
//                    type: 'bar',  
//                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
//                    data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],  
//                    //系列中的数据标注内容  
//                    markPoint: {  
//                        data: [  
//                            {type: 'max', name: '最大值'},  
//                            {type: 'min', name: '最小值'}  
//                        ]  
//                    },  
//                    //系列中的数据标线内容  
//                    markLine: {  
//                        data: [  
//                            {type: 'average', name: '平均值'}  
//                        ]  
//                    }  
//                },  
//                {  
//                    //系列名称，如果启用legend，该值将被legend.data索引相关  
//                    name: '1073基数',  
//                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
//                    type: 'bar',  
//                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
//                    data: [6, 11, 12, 26.4, 28.7, 80.7, 175.6, 282.2, 148.7,78.8, 66.0, 66],  
//                    //系列中的数据标注内容  
//                    markPoint: {  
//                        data: [  
//                            {type: 'max', name: '最大值'},  
//                            {type: 'min', name: '最小值'}  
//                        ]  
//                    },  
//                    //系列中的数据标线内容  
//                    markLine: {  
//                        data: [  
//                            {type: 'average', name: '平均值'}  
//                        ]  
//                    }  
//                },
//                {  
//                    //系列名称，如果启用legend，该值将被legend.data索引相关  
//                    name: '1068杭州管控',  
//                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
//                    type: 'bar',  
//                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
//                    data: [6, 9, 24, 26.4, 28.7, 70.7, 188, 222, 333, 144, 55, 33],  
//                    //系列中的数据标注内容  
//                    markPoint: {  
//                        data: [  
//                            {type: 'max', name: '最大值'},  
//                            {type: 'min', name: '最小值'}  
//                        ]  
//                    },  
//                    //系列中的数据标线内容  
//                    markLine: {  
//                        data: [  
//                            {type: 'average', name: '平均值'}  
//                        ]  
//                    }  
//                }, 
//                {  
//                    //系列名称，如果启用legend，该值将被legend.data索引相关  
//                    name: '1069长三角管控',  
//                    //图表类型，必要参数！如为空或不支持类型，则该系列数据不被显示。  
//                    type: 'bar',  
//                    //系列中的数据内容数组，折线图以及柱状图时数组长度等于所使用类目轴文本标签数组axis.data的长度，并且他们间是一一对应的。数组项通常为数值  
//                    data: [5, 9, 24, 26, 28, 70, 175, 182, 48, 33.8, 22, 8],  
//                    //系列中的数据标注内容  
//                    markPoint: {  
//                        data: [  
//                            {type: 'max', name: '最大值'},  
//                            {type: 'min', name: '最小值'}  
//                        ]  
//                    },  
//                    //系列中的数据标线内容  
//                    markLine: {  
//                        data: [  
//                            {type: 'average', name: '平均值'}  
//                        ]  
//                    }  
//                }
//                
//            ]  
//        }; 
//
//	myChart.setOption(option);
//	myChartTwo.setOption(option);
//	图表结束	


/*以下为新添加内容，注意作用域问题，提升变量*/
/*定义全局变量，用于存储改变值*/
var dps_City,dps_Date ;
var dps_Station = {};
var changeMsg = {
    type: 'wrw',
    startD: '',		//开始时间
    endD: '',		//结束时间
    rms: 'day',		//时间分辨率
    pro: '',		//站点选择
    city: ''		
};

$('.day').css('display','block');
$('.hour').css('display','none');

var speciesArr = {
//		day: ['PM₂₅', 'PM₁₀', 'O₃_8_MAX', 'O₃_1_MAX', 'O₃_AVG', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE'],
		day: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3_8_MAX', 'O3_1_MAX', 'SO2', 'NO2', 'CO'],
//		day: ['PM25', 'PM10', 'O3_8_MAX', 'O3_1_MAX', 'O3_AVG', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE'],
		hour: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3', 'SO2', 'NO2', 'CO']
//		hour: ['PM25', 'PM10', 'O3', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE']
//		hour: ['PM₂₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE']
};

var meteorArr = {
		day: ["TEMP","PRSFC","PT","RH","WSPD"],
		hour: ["TEMP","PRSFC","PT","RH","WSPD"]
};


var speciesObj = {
  'PM₂₅':'PM25',
  'PM₁₀':'PM10',
  'O₃_8_MAX':'O3_8_MAX',
  'O₃_1_MAX':'O3_1_MAX',
  'O₃_AVG':'O3_AVG',
  'SO₂':'SO2',
  'NO₂':'NO2',
  'CO':'CO',
  'SO₄':'SO4',
  'NO₃':'NO3',
  'NH₄':'NH4',
  'BC':'BC',
  'OM':'OM',
  'PMFINE':'PMFINE',
  //'O₃':'O3'
  'O₃':'O3'
};

/**
 * 设置柱状图 模板
 */
var show_type = "bar";
var optionAll = {
		title: {
		    text: '',  //改变量 放污染物
		    x: 'left',
		    y: 'top'
		 },
	  tooltip: {
		  	trigger: 'axis'
	  },
	  legend: {
		    show: true,
		    x: 'center',
		    y: 'top',
		    //legend的data: 用于设置图例，data内的字符串数组需要与sereis数组内每一个series的name值对应
		    data: [],     //改变量 存储所选情景name

	  	},
		grid: {
			show: true,
			left: '3%',
			right: '3%',
			bottom: '30%',
		},
		dataZoom:[
		          {
		        	  show:'true',
		        	  realtime:'true',
		        	  start:0,
		        	  end:100
		          },
		     ],
      calculable: false,
      xAxis: [
              	{
	        	  show: true,
	        	  type: 'category',
	        	  data: []  //改变量
              	}
              ],
      yAxis: [
	            {
	            	name:'',  //改变量  污染物name
	               	nameLocation:'end',
	               	show: true,
	               	type: 'value',
	               	splitArea: {show: false},
	            	
	             }
               ],
	   //颜色色卡
      color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
       //sereis的数据: 用于设置图表数据之用。series是一个对象嵌套的结构；对象内包含对象
      series:[]   //改变量  观测数据单独写一个接口  其它的放在一起
};

initialize();

/*初始化函数*/
function initialize() {
    dps_City = requestRegion();
    dps_Date = requestDate();

    $.when(dps_Date, dps_City).then(function () {
//        console.log('initialize  updata');
        updata();
    })
}

/**
 * 标签页替换
 * @param type
 */
function changeType(type) {
    if (changeMsg.type != type) {

        if (type == 'wrw') {
            $('.toolAll.qxysTool').addClass('disNone');
            $('.toolAll.wrwTool').removeClass('disNone');
        } else {
            $('.toolAll.wrwTool').addClass('disNone');
            $('.toolAll.qxysTool').removeClass('disNone');
        }


        changeMsg.type = type;
        if (!dps_Station[changeMsg.city + changeMsg.type]) {
            dps_Station[changeMsg.city + changeMsg.type] = setStation(changeMsg.city, changeMsg.type);
        }
        updata();
    }

}

/**
 * 初始化污染物时间范围
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initWrwDate(s, e, start, end) {
    $('#wrwDate').daterangepicker({
//    "parentEl": ".toolAll",
        "autoApply": true,
        singleDatePicker: false,  //显示单个日历
        timePicker: false,  //允许选择时间
        timePicker24Hour: true, //时间24小时制
        minDate: s,//最早可选日期
        maxDate: e,//最大可选日期
        locale: {
            format: "YYYY-MM-DD",
            separator: " 至 ",
            applyLabel: "确定", //按钮文字
            cancelLabel: "取消",//按钮文字
            weekLabel: "W",
            daysOfWeek: [
                "日", "一", "二", "三", "四", "五", "六"
            ],
            monthNames: [
                "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
            ],
            firstDay: 1
        },
        "startDate": start,
        "endDate": end,
        "opens": "left"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        updata(true);
    });
    var d = $('#wrwDate').data('daterangepicker');
    d.element.off();
}

/**
 * 初始化气象要素时间范围
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initQxysDate(s, e, start, end) {
    $('#qxysDate').daterangepicker({
//    "parentEl": ".toolAll",
        "autoApply": true,
        singleDatePicker: false,  //显示单个日历
        timePicker: false,  //允许选择时间
        timePicker24Hour: true, //时间24小时制
        minDate: s,//最早可选日期
        maxDate: e,//最大可选日期
        locale: {
            format: "YYYY-MM-DD",
            separator: " 至 ",
            applyLabel: "确定", //按钮文字
            cancelLabel: "取消",//按钮文字
            weekLabel: "W",
            daysOfWeek: [
                "日", "一", "二", "三", "四", "五", "六"
            ],
            monthNames: [
                "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
            ],
            firstDay: 1
        },
        "startDate": start,
        "endDate": end,
        "opens": "left"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        updata(true);
    })
    var d = $('#qxysDate').data('daterangepicker');
    d.element.off();
}

/*按钮打开日期*/
function showDate(type) {
    var d = $('#' + type + 'Date').data('daterangepicker');
    if (!d) {
        swal({
            title: '无可选日期!',
            type: 'error',
            timer: 1000,
            showConfirmButton: false
        });
        return
    }
    d.toggle();
}

/*请求可选日期范围*/
function requestDate() {
    var url = '/Air/get_time';
    return ajaxPost(url, {
        userId: userId
    }).success(function (res) {

        if (res.status == 0) {
            /*这里要初始化时间*/

            if (!(moment(res.data.maxtime).add(-7, 'd').isBefore(moment(res.data.mintime)))) {
                changeMsg.startD = moment(res.data.maxtime).add(-7, 'd').format('YYYY-MM-DD')
            } else {
                changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
            }

            changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
            initWrwDate(moment(res.data.mintime).format('YYYY-MM-DD'), moment(res.data.maxtime).format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
            initQxysDate(moment(res.data.mintime).format('YYYY-MM-DD'), moment(res.data.maxtime).format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
        }
    })
}

/*请求省市区code*/
function requestRegion() {
    var url = '/Site/find_codes';
    return ajaxPost(url, {
        userId: userId
    }).success(function (res) {
        $('.proStation').empty();
        $('.cityStation').empty();
        $('.station').empty();

        if (res.status == 0) {
            allCode = res.data;
            for (var pro in allCode) {
                $('.proStation').append($('<option value="' + pro + '">' + allCode[pro].name + '</option>'))
            }
            var cityStation = allCode[$('.proStation').val()].city;
            for (var city in cityStation) {
                $('.cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
            }

            changeMsg.pro = $('.proStation').val();
            changeMsg.city = $('.cityStation').val();
            if (!dps_Station[changeMsg.city + changeMsg.type]) {
                dps_Station[changeMsg.city + changeMsg.type] = setStation(changeMsg.city, changeMsg.type);
            }
        } else {
            console.log('站点请求故障！！！');
        }

    })
}

/*设置污染物/气象要素站点
 * @parameter regionId 城市regionId
 * @parameter type 类型
 * */
function setStation(regionId, type) {
    var url = '/Site/find_Site';
    return ajaxPost(url, {
        userId: userId,
        siteCode: regionId.substr(0, 4)
    });
}

/*change 改变事件*/
$('.proStation').on('change', function (e) {
    var pro = $(e.target).val();
    $('.proStation').val(pro);
    changeMsg.pro = pro;
    $('.cityStation').empty();
    var cityStation = allCode[pro].city;
    for (var city in cityStation) {
        $('.cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
    }
    changeMsg.city = $('.cityStation').val();
    if (!dps_Station[changeMsg.city + changeMsg.type]) {
        dps_Station[changeMsg.city + changeMsg.type] = setStation(changeMsg.city, changeMsg.type);
    }
    updata();
});


$('.cityStation').on('change', function (e) {
    var city = $(e.target).val();
    $('.cityStation').val(city);
    changeMsg.city = city;
    if (!dps_Station[changeMsg.city + changeMsg.type]) {
        dps_Station[changeMsg.city + changeMsg.type] = setStation(changeMsg.city, changeMsg.type);
    }
    updata();
});

$('.station').on('change', function (e) {
    var station = $(e.target).val();
    changeMsg.station = station;
    updata(true);
});


var stint = true;
$('input[name=rmsWrw]').on('change', function (e) { //污染物时间分辨率逐日或逐小时
    var rms = $(e.target).val();
    if (stint) {
        stint = false;
        $('#qxys input[value=' + rms + ']').parent().click();
        if(rms == 'hour'){
			show_type = "line";
		}else{
			show_type = "bar";
		}
        changeRms(rms);
    }
});

$('input[name=rmsQxys]').on('change', function (e) { //气象要素时间分辨率逐日或逐小时
    var rms = $(e.target).val();
    if (stint) {
        stint = false;
        $('#wrw input[value=' + rms + ']').parent().click();
        if(rms == 'hour'){
			show_type = "line";
		}else{
			show_type = "bar";
		}
        changeRms(rms);
    }
});

//空间分布率
$('input[name=domain]').on('change', function (e) {
	domain = $(e.target).val();
	updata(true);
});

function changeRms(rms) {	//参数为逐日或逐小时
    stint = true;
    changeMsg.rms = rms;
    updata(true);
}


/**
 * 数据更新使用
 * @param opt
 */
var ecatherData='';
function updata(opt) {
    $.when(dps_Station[changeMsg.city + changeMsg.type]).done(function (res) {
        if (!opt) {
            res = dps_Station[changeMsg.city + changeMsg.type].responseJSON;
            $('#' + changeMsg.type + ' .station').empty();
            $('#' + changeMsg.type + ' .station').append($('<option value="avg">平均</option>'));
            for (var i = 0; i < res.data.length; i++) {
                $('#' + changeMsg.type + ' .station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
            }
            changeMsg.station = $('#' + changeMsg.type + ' .station').val();
        }
        var url='/Air/findAllTimeSeries';
        ajaxPost(url,{
        	userId: userId,					//用户ID
            mode:changeMsg.station=='avg'?'city':'point',	//站点是否为平均
            startDate : changeMsg.startD,	//开始日期
            endDate :changeMsg.endD,		//结束日期
            cityStation:changeMsg.station=='avg'?changeMsg.city.substr(0,4):changeMsg.station,	//站点具体值
            datetype:changeMsg.rms,			//时间分辨率
            domain:$('input[name=domain]:checked').val(),	//空间分辨率
            changeType:changeMsg.type	//选择的页签
        }).success(function(res){
        	ecatherData='';
        	ecatherData=res.data;
        	initEcharts();
        }
//        ,function(){
//        	console.log('接口故障！！！');
//          }
        )
        
    })
    
}
/**
 * 加载echarts图形数据
 */
function initEcharts() {
	$("#initEcharts").empty();	//清空数据
	if(changeMsg.rms == 'day'){
		$('.hour').css('display','none');
	    $('.day').css('display','block');
	}else{
	    $('.day').css('display','none');
	    $('.hour').css('display','block');
	}
	var data = ecatherData;		//模拟和观测数据
	
	  
	var tname = []; 	//污染物name
	var species =[];
	if("wrw"==changeMsg.type){
		 species = speciesArr[changeMsg.rms];
		for (var j = 0; j < species.length; j++) {
			  tname.push(species[j]);
		}
	}else if("qxys"==changeMsg.type){
		 species = meteorArr[changeMsg.rms];
		for (var j = 0; j < species.length; j++) {
			  tname.push(species[j]);
		}
	}
	
	
	var	proStation=$("#proStation option:selected").text();
	var	cityStation=$("#cityStation option:selected").text();
	var	station=$("#station option:selected").text();
	var	domain=$("#wrw input[name='domain']").parents('label.active').text();
	var mesage='';
	if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"==station){
		mesage+=cityStation+">>"+domain+">>";
	}else if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"!=station){
		mesage+=cityStation+">>"+station+">>"+domain+">>";
	}else if("省"==proStation.substr(-1)&&"平均"==station){
		mesage+=proStation+">>"+cityStation+">>"+domain+">>";
	}else {
		mesage+=proStation+">>"+cityStation+">>"+station+">>"+domain+">>";
	}
	
	for (var i = 0; i < tname.length; i++) {	//循环物种开始
//		echarts.dispose(document.getElementById(species[i]));	//echarts内存释放
//	    var es = echarts.init(document.getElementById(species[i]));	
		
		//创建div存放echarts图表
		if("PM25"==tname[i]){
			var div_bj=$('<div class="row" style="padding-left:15px;"><div class="col-sm-4"><div class="input-group m-b" style="margin-bottom: 0px"><div class="" style="margin-bottom:0px;padding-left:7px;"><div class="btn-group" data-toggle="buttons"><label name="collapse" class="btn btn-outline btn-success active"><input type="radio" name="spread" value="open" checked>组分展开</label><label name="collapse" class="btn btn-outline btn-success"><input type="radio" name="spread" value="close">组分收起</label></div></div></div></div></div>');
			var div = $('<div style="height:250px;"></div>');
			div.attr("id",tname[i]);
			div.addClass('echartsCZ');
			$("#initEcharts").append(div_bj);
			$("#initEcharts").append(div);
		}else{
			var div = $('<div style="height:250px;"></div>');
			div.attr("id",tname[i]);
			div.addClass('echartsCZ');
			$("#initEcharts").append(div);
		}
		
		//拷贝echarts模板
		var option = $.extend(true, {}, optionAll);		 
		
		//设置标题名称
		if(tname[i] == 'AQI'){
			option.title.text = tname[i];         //加不同单位
		}else if(tname[i] != 'CO'){
			if("PM25"==tname[i]){
				option.title.text = mesage +"PM₂.₅"+('(μg/m³)');
			}else if("SO4"==tname[i]){
				option.title.text = mesage +"SO₄²¯"+('(μg/m³)');
			}else if("NO3"==tname[i]){
				option.title.text = mesage +"NO₃¯"+('(μg/m³)');
			}else if("NH4"==tname[i]){
				option.title.text = mesage +"NH₄⁺"+('(μg/m³)');
			}else if("PM10"==tname[i]){
				option.title.text = mesage +"PM₁₀"+('(μg/m³)');
			}else if("O3_8_MAX"==tname[i]){
				option.title.text = mesage +"O₃_8_max"+('(μg/m³)');
			}else if("O3_1_MAX"==tname[i]){
				option.title.text = mesage +"O₃_1_max"+('(μg/m³)');
			}else if("SO2"==tname[i]){
				option.title.text = mesage +"SO₂"+('(μg/m³)');
			}else if("NO2"==tname[i]){
				option.title.text = mesage +"NO₂"+('(μg/m³)');
			}else if("TEMP"==tname[i]){
				option.title.text = mesage +tname[i]+('(℃)');
			}else if("PRSFC"==tname[i]){
				option.title.text = mesage +tname[i]+('(hPa)');
			}else if("PT"==tname[i]){
				option.title.text = mesage +tname[i]+('(mm)');
			}else if("RH"==tname[i]){
				option.title.text = mesage +tname[i]+('(%)');
			}else if("WSPD"==tname[i]){
				option.title.text = mesage +tname[i]+('(m/s)');
			}
			else{
				option.title.text = mesage +tname[i]+('(μg/m³)');
			}
		}else{
			option.title.text = mesage +tname[i]+('(mg/m³)');
		}	
		
		//设置单个图表中包含的图例名称
		option.legend.data = (function(){	//图例名称
			var lenArr = [];	//图例的legend.data使用数组存放
			$.each(data,function(key,val){
				lenArr.push(key);
			});
			return lenArr;
		})();
		
		//存放单个图表的series数据
		option.series = [];
		if(changeMsg.rms == 'day'){	//逐日开始
			var xdataName =	[];
			$.each(data,function(key,val){
				var xdata = [];		//x轴数据
				var ydata = [];		//y轴数据
				var name = key;		//数据name
				
				if(val.hasOwnProperty(tname[i])){	//包含当前物种
					$.each(val,function(speKey,speVal){
						if(speKey==tname[i]){
							$.each(val[tname[i]],function(timeKey,timeVal){	//循环单个物种数据
								xdata.push(timeKey);
							});
							xdata = xdata.sort();
							for(var m=0;m<xdata.length;m++){
								ydata.push(val[tname[i]][xdata[m]]);
							}
							if("模拟数据"==key){		//去模拟
								for(var skey in val[tname[i]]){
									xdataName.push(skey);
									
								}
							}
						}
					});
					
				}
				
				option.series.push({
					name : name, 				//情景名称  对应图例 exceptsjz
					type : show_type, 			 //图表类型   已设全局变量 show_type
					smooth : true,
					data : ydata     			//可变情景数据 
				});	
			});
			
		}else{	//-----------------------逐日结束--逐小时开始--------------------//
			var xdataName =	[];
			$.each(data,function(key,val){	//键为模拟或观测
				var xdata = [];		//x轴数据
				var ydata = [];		//y轴数据
				var name = key;		//数据name
				var keys = [];
				
				if(val.hasOwnProperty(species[i])){	//包含当前物种
					for(var timesKey in val[species[i]]){
						xdata.push(timesKey);	//全部日期
					}
					xdata = xdata.sort();	//排序
					
						for(var n=0;n<xdata.length;n++){
							var arr = Object.keys(val[species[i]][xdata[n]]);
							for(var m=0;m<arr.length;m++){
								keys.push(xdata[n]+' '+m);
								ydata.push(val[species[i]][xdata[n]][m]);
							}
						
						}
					
					if("模拟数据"==key){		//去除模拟
						xdataName=keys;
					}
				}
				
				option.series.push({
					name : name, 				//情景名称  对应图例 exceptsjz
					type : show_type,			//图表类型   已设全局变量 show_type.
					smooth : true,
					data : ydata     			//可变情景数据 
				});	
			});
			
		}
		
		/**
		 * 逐日--根据不同物种添加相应的刻度标注线配色
		 * */
		//逐日和逐小时共用标注线颜色卡
		if("wrw"==changeMsg.type){
			
			var colors=['rgb(0, 228, 0)','rgb(255, 255, 0)','rgb(255, 126, 0)','rgb(255, 0, 0)','rgb(153, 0, 76)','rgb(126, 0, 35)','rgb(0, 0, 0)'];
			if(changeMsg.rms == 'day'){
				if("AQI"==tname[i]){	
					var markLineName=[50,100,150,200,300,400,500];
				}else if("PM25"==tname[i]){
					var markLineName=[35,75,115,150,250,350,500];
				}else if("PM10"==tname[i]){
					var markLineName=[50,150,250,350,420,500,600];
				}else if("O3_8_MAX"==tname[i]){
					var markLineName=[100,160,215,265,800];
				}else if("O3_1_MAX"==tname[i]){
					var markLineName=[160,200,300,400,800,1000,1200];
				}else if("SO2"==tname[i]){
					var markLineName=[50,150,475,800,1600,2100,2620];
				}else if("NO2"==tname[i]){
					var markLineName=[40,80,180,280,565,750,940];
				}else if("CO"==tname[i]){
					var markLineName=[2,4,14,24,36,48,60];
				}
				for(var w=0;w<markLineName.length;w++){
					var mkname=markLineName[w];
					var markLines={
							name:mkname ,
				            type:'line',
				            markLine : {
				                lineStyle: {
				                    normal: {
				                        type: 'dashed',
				                        color:colors[w]
				                    }
				                },
				                data : [
				                    {yAxis:mkname  }
				                ]
				            }
						};
					option.series.push(markLines);
				}
			}else{
				//逐小时--根据不同物种添加相应的刻度标注线配色
				if("AQI"==tname[i]){	
					var markLineName=[50,100,150,200,300,400,500];
				}else if("PM25"==tname[i]){
					var markLineName=[35,75,115,150,250,350,500];
				}else if("PM10"==tname[i]){
					var markLineName=[50,150,250,350,420,500,600];
				}else if("O3"==tname[i]){
					var markLineName=[160,200,300,400,800,1000,1200];
				}else if("SO2"==tname[i]){
					var markLineName=[150,500,650,800];
				}else if("NO2"==tname[i]){
					var markLineName=[100,200,700,1200,2340,3090,3840];
				}else if("CO"==tname[i]){
					var markLineName=[5,10,35,60,90,120,150];
				}
				for(var w=0;w<markLineName.length;w++){
					var mkname=markLineName[w];
					var markLines={
							name:mkname ,
				            type:'line',
				            markLine : {
				                lineStyle: {
				                    normal: {
				                        type: 'dashed',
				                        color:colors[w]
				                    }
				                },
				                data : [
				                    {yAxis:mkname  }
				                ]
				            }
						};
					option.series.push(markLines);
				}
			}	//标注线配色结束
		}
		option.xAxis = [];
		if(changeMsg.rms == 'day'){
			option.xAxis.push({				    //x轴情景时间
		    	data: xdataName.sort()			//修改数据排序
		    });
		}else{
			option.xAxis.push({				    //x轴情景时间
		    	data: xdataName					//修改数据排序
		    });
		}
	    
		var es = echarts.init(document.getElementById(tname[i]));
	    es.setOption(option);
	    $(window).resize(es.resize);
		
		
	 }	//循环物种结束

	//组分展开==open  收起==close	
$('input[name=spread]').on('change', function (e) {
	var spType = $(e.target).val();
	if (spType == 'close') {
		$("#SO4").hide();
		$("#NO3").hide();
		$("#NH4").hide();
		$("#BC").hide();
		$("#OM").hide();
		$("#PMFINE").hide();
//			$(e.target.parentNode).text("组分展开");
//			$(e.target).val('open');
	} else {
		$("#SO4").show();
		$("#NO3").show();
		$("#NH4").show();
		$("#BC").show();
		$("#OM").show();
		$("#PMFINE").show();
//			$(e.target.parentNode).text("组分收起");
//			$(e.target).val('close');
	}

});	
	
	
}
