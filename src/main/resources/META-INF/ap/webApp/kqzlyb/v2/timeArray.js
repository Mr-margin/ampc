$(function(){
    /**
     *设置导航条信息
     **/
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">时间序列</span>');

});	//初始化结束



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
var zhiCity = ['11', '12', '31', '50'];
var speciesArr = {
		day: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3_8_MAX', 'O3_1_MAX', 'SO2', 'NO2', 'CO'],
		hour: ['AQI', 'PM25', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE', 'PM10', 'O3', 'SO2', 'NO2', 'CO']
};

var meteorArr = {
//    day: ["TEMP","PRSFC","PT","RH","WSPD"],
//    hour: ["TEMP","PRSFC","PT","RH","WSPD"]
		day: ["气压","温度","湿度","降水","风速"],
		hour: ["气压","温度","湿度","降水","风速"]
	    
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
	    'O₃':'O3'
};

/**
 * 设置柱状图 模板
 */
var show_type = "bar";
var optionAll = {
		backgroundColor:'rgb(220,220,220)',
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
	            splitLine:{show: false,		//设置网格线颜色
	                lineStyle:{
	
	                }
	            },//去除网格线
	        }
	    ],
	    //颜色色卡
	    color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
	    series:[]   //改变量  观测数据单独写一个接口  其它的放在一起
};

initialize();

/*初始化函数*/
function initialize() {
    dps_City = requestRegion();//请求省市区code
//    dps_Date = requestDate();//请求可选日期范围

    //当时间和code全部完成时，开始组织查询条件，进行第一次查询
    $.when(dps_City).then(function () {
    	window.setTimeout(function () {
//            updata();
            requestDate();
        },50);
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
        requestDate();
//        updata();
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

    $('#wrwStartDate').daterangepicker({
//    "parentEl": ".toolAll",
        autoApply: true,
        singleDatePicker: true,  //显示单个日历
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
        startDate: start,
        endDate: start,
        opens: "right"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        // changeMsg.endD = end.format('YYYY-MM-DD');
        if(moment(changeMsg.startD).isAfter(moment($("#wrwEndDate").val()))){
//            $("#wrwEndDate").val(changeMsg.startD);
        	$('#wrwEndDate').data('daterangepicker').setStartDate(changeMsg.startD);
            $('#wrwEndDate').data('daterangepicker').setEndDate(changeMsg.startD);
            changeMsg.endD=changeMsg.startD;
        }
//        changeMsg.endD=moment($("#wrwEndDate").val()).format('YYYY-MM-DD');
        updata(true);
    });
    var ds = $('#wrwStartDate').data('daterangepicker');
    ds.element.off();


    $('#wrwEndDate').daterangepicker({
//    "parentEl": ".toolAll",
        autoApply: true,
        singleDatePicker: true,  //显示单个日历
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
        startDate: end,
        endDate: end,
        opens: "right"
    }, function (start, end, label) {
        // changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        if(moment(changeMsg.endD).isBefore(moment($("#wrwStartDate").val()))){
//            $("#wrwStartDate").val(changeMsg.endD);
            $('#wrwStartDate').data('daterangepicker').setStartDate(changeMsg.endD);
            $('#wrwStartDate').data('daterangepicker').setEndDate(changeMsg.endD);
            changeMsg.startD= changeMsg.endD;
        }
        updata(true);
    });
    var de = $('#wrwEndDate').data('daterangepicker');
    de.element.off();




    $('#wrwDate').daterangepicker({
        autoApply: true,
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
        startDate: start,
        endDate: end,
        opens: "left"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        updata(true);
    });
    var d = $('#wrwDate').data('daterangepicker');
    // d.element.off();
}

/**
 * 初始化气象要素时间范围
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initQxysDate(s, e, start, end) {
    $('#qxysStartDate').daterangepicker({
//    "parentEl": ".toolAll",
        autoApply: true,
        singleDatePicker: true,  //显示单个日历
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
        startDate: start,
        endDate: start,
        opens: "right"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        // changeMsg.endD = end.format('YYYY-MM-DD');
        if(moment(changeMsg.startD).isAfter(moment($("#qxysEndDate").val()))){
//            $("#qxysEndDate").val(changeMsg.startD);
        	$('#qxysEndDate').data('daterangepicker').setStartDate(changeMsg.startD);
            $('#qxysEndDate').data('daterangepicker').setEndDate(changeMsg.startD);
            changeMsg.endD=changeMsg.startD;
        }
//        changeMsg.endD=moment($("#qxysEndDate").val()).format('YYYY-MM-DD');
        updata(true);
    });
    var ds = $('#qxysStartDate').data('daterangepicker');
    ds.element.off();


    $('#qxysEndDate').daterangepicker({
//    "parentEl": ".toolAll",
        autoApply: true,
        singleDatePicker: true,  //显示单个日历
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
        startDate: end,
        endDate: end,
        opens: "right"
    }, function (start, end, label) {
        // changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        
        if(moment(changeMsg.endD).isBefore(moment($("#qxysStartDate").val()))){
//            $("#qxysStartDate").val(changeMsg.endD);
        	$('#qxysStartDate').data('daterangepicker').setStartDate(changeMsg.endD);
            $('#qxysStartDate').data('daterangepicker').setEndDate(changeMsg.endD);
            changeMsg.startD= changeMsg.endD;
        }
        updata(true);
    });
    var de = $('#qxysEndDate').data('daterangepicker');
    de.element.off();






    $('#qxysDate').daterangepicker({
//    "parentEl": ".toolAll",
        autoApply: true,
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
        startDate: start,
        endDate: end,
        opens: "left"
    }, function (start, end, label) {
        changeMsg.startD = start.format('YYYY-MM-DD');
        changeMsg.endD = end.format('YYYY-MM-DD');
        updata(true);
    })
    var d = $('#qxysDate').data('daterangepicker');
    // d.element.off();
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
        	if(true==res.data.msg){
        		
				/*这里要初始化时间*/
	        	if(JSON.stringify(res.data) == "{}"){
	//        		//如果返回值状态成功，但是时间数据没有，需要为时间控件设置一个默认值
	//        		changeMsg.startD = "2017-04-28";
	//    			changeMsg.endD = "2017-05-23";
	//    			if(changeMsg.type=="qxys"){
	//    				initQxysDate("2017-04-28", changeMsg.endD, changeMsg.startD,'2017-05-04');
	//    				showTitleFun();
	//    			}else{
	//    				initWrwDate("2017-04-28", changeMsg.endD, changeMsg.startD, '2017-05-04');
	//    			}
	        		swal('FNL时间查询错误', '', 'error');
	        	}else{
	        		//后台返回时间
	        		//1、判断最大时间与最小时间的天数
	        		//超过7天：（取最大时间-7天为开始时间，最大时间为结束时间，显示7天的数据）
	        		//小于等于7天：（最大时间最小时间就是开始结束时间）
	        		var maxtime = moment(res.data.maxtime);//最大时间
	        		var mintime = moment(res.data.mintime);//最小时间
	        		
	        		if(maxtime.diff(mintime, 'days') > 7){//事件间隔大于7天
	        			changeMsg.startD = moment(res.data.maxtime).add(-7, 'd').format('YYYY-MM-DD');
	        			changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
	        			
	        			if(changeMsg.type=="qxys"){
	        				initQxysDate(mintime.format('YYYY-MM-DD'), maxtime.format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
	        			}else{
	        				initWrwDate(mintime.format('YYYY-MM-DD'), maxtime.format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
	        			}
	        			//			最大可选时间     最小可选时间		默认开始时间		  默认结束时间
	        		}else{//小于等于7天
	        			changeMsg.startD = moment(res.data.maxtime).format('YYYY-MM-DD');
	        			changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
	        			if(changeMsg.type=="qxys"){
	        				initQxysDate(mintime.format('YYYY-MM-DD'), maxtime.format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
	        			}else{
	        				initWrwDate(mintime.format('YYYY-MM-DD'), maxtime.format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
	        			}
	        			
	        		}
	        	}
	        	//更新数据
	        	updata();
        	}else{
        		swal(res.data.msgContent, '', 'error');
        	}
        	
        }else{
        	swal('内部错误，时间查询失败', '', 'error');
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
    changeMsg.stationName = $(e.target)[0].selectedOptions[0].innerHTML;
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
            changeType:changeMsg.type		//选择的页签
        }).success(function(res){
//        	if(res.data==null||res.data==''||res.data=='{}'){
//        		swal({
//                    title: '暂无数据!',
//                    type: 'error',
//                    timer: 1000,
//                    showConfirmButton: false
//                });
//        	}else{
        		ecatherData='';
                ecatherData=res.data;
                initEcharts();
//        	}
            
        }
//        ,function(){
//        	console.log('接口故障！！！');
//          }
        )
    })
    proStation=allCode[$(".proStation").val()].name;
    cityStation= allCode[$('.proStation').val()].city[$(".cityStation").val()];
    showTitleFun();
}
/**
 * 加载echarts图形数据
 */
var	proStation,cityStation,station;
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


    proStation=$("#proStation option:selected").text();
    cityStation=$("#cityStation option:selected").text();
    station=$("#station option:selected").text();
    var	domain=$("#wrw input[name='domain']").parents('label.active').text();
//    var mesage='';
    // if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"==station){
    //     mesage+=cityStation+">>"+domain+">>";
    // }else if(proStation.substr(-1)!="省"&&cityStation.substr(-1)=="市"&&"平均"!=station){
    //     mesage+=cityStation+">>"+station+">>"+domain+">>";
    // }else if("省"==proStation.substr(-1)&&"平均"==station){
    //     mesage+=proStation+">>"+cityStation+">>"+domain+">>";
    // }else {
    //     mesage+=proStation+">>"+cityStation+">>"+station+">>"+domain+">>";
    // }

    for (var i = 0; i < tname.length; i++) {	//循环物种开始
//		echarts.dispose(document.getElementById(species[i]));	//echarts内存释放
//	    var es = echarts.init(document.getElementById(species[i]));

        //创建div存放echarts图表
        if("PM25"==tname[i]){
            //var div_bj=$('<div class="row" style="padding-left:15px;"><div class="col-sm-4"><div class="input-group m-b" style="margin-bottom: 0px"><div class="" style="margin-bottom:0px;padding-left:7px;"><div class="btn-group" data-toggle="buttons"><label name="collapse" class="btn btn-outline btn-success "><input type="radio" name="spread" value="open" >组分展开</label><label name="collapse" class="btn btn-outline btn-success active"><input type="radio" name="spread" value="close" checked>组分收起</label></div></div></div></div></div>');
            var div_bj=$('<div class="timeUpDown"><span class="timeUpDownBtn">组分展开</span></div>');
            var div = $('<div style="height:250px;"></div>');
            div.attr("id",tname[i]);
            div.addClass('echartsCZ');
            $("#initEcharts").append(div);
            $("#initEcharts").append(div_bj);
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
                option.title.text = "PM₂.₅"+('(μg/m³)');
            }else if("SO4"==tname[i]){
                option.title.text = "SO₄²¯"+('(μg/m³)');
            }else if("NO3"==tname[i]){
                option.title.text = "NO₃¯"+('(μg/m³)');
            }else if("NH4"==tname[i]){
                option.title.text = "NH₄⁺"+('(μg/m³)');
            }else if("PM10"==tname[i]){
                option.title.text = "PM₁₀"+('(μg/m³)');
            }else if("O3_8_MAX"==tname[i]){
                option.title.text = "O₃_8_max"+('(μg/m³)');
            }else if("O3_1_MAX"==tname[i]){
                option.title.text = "O₃_1_max"+('(μg/m³)');
            }else if("SO2"==tname[i]){
                option.title.text = "SO₂"+('(μg/m³)');
            }else if("NO2"==tname[i]){
                option.title.text = "NO₂"+('(μg/m³)');
            }else if("温度"==tname[i]){
                option.title.text = tname[i]+('(℃)');
            }else if("气压"==tname[i]){
                option.title.text = tname[i]+('(hPa)');
            }else if("降水"==tname[i]){
                option.title.text = tname[i]+('(mm)');
            }else if("湿度"==tname[i]){
                option.title.text = tname[i]+('(%)');
            }else if("风速"==tname[i]){
                option.title.text = tname[i]+('(m/s)');
            }
            else{
                option.title.text = tname[i]+('(μg/m³)');
            }
        }else{
            option.title.text = tname[i]+('(mg/m³)');
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
            var xAarr = [];
            $.each(data,function(key,val){	//键为模拟或观测
                var xdata = [];		//x轴数据
                var ydata = [];		//y轴数据
                var name = key;		//数据name
                var keys = [];

                if(val.hasOwnProperty(species[i])){	//包含当前物种
                    for(var timesKey in val[species[i]]){
                        xdata.push(timesKey);	//全部日期
                        //$.each循环不能复制到外部变量
                        xAarr.push(timesKey);
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
        	if(xAarr.length>=24){
		    	 option.xAxis = [];
				    option.xAxis.push({			
				    	axisLabel:{
				    		interval:71
				    	},
				    	data: xdataName						
				    });
			}else if(xAarr.length>=11){
			    		 option.xAxis = [];
					    option.xAxis.push({			
					    	axisLabel:{
					    		interval:47
					    	},
					    	data: xdataName						
					    });
			}else if(xAarr.length>5){
				 option.xAxis = [];
				    option.xAxis.push({			
				    	axisLabel:{
				    		interval:23
				    	},
				    	data: xdataName						
				    });
			}else if(xAarr.length<=5){
				 option.xAxis = [];
				    option.xAxis.push({			
				    	axisLabel:{
				    		interval:11
				    	},
				    	data: xdataName						
				    });
			}
        }
        
        //气象要素设置y轴坐标为自适应
        if("qxys"==changeMsg.type){
        	//气象要素逐小时
        	if(changeMsg.rms == 'hour'){
        		option.yAxis = [];
        		if("气温"==tname[i]){
        			 option.yAxis.push({
        				 	name:'',  //改变量  污染物name
				            nameLocation:'end',
				            show: true,
				            type: 'value',
				            boundaryGap: ['20%', '20%'],
				            splitArea: {show: false},
				            splitLine:{show: false,		//设置网格线颜色
				                lineStyle:{
				
				                }
				            },//去除网格线
        			 });
        		}else if("气压"==tname[i]){
        			option.yAxis.push({
    				 	name:'',  //改变量  污染物name
			            nameLocation:'end',
			            show: true,
			            type: 'value',
			            boundaryGap: ['20%', '20%'],
			            splitArea: {show: false},
			            splitLine:{show: false,		//设置网格线颜色
			                lineStyle:{
			
			                }
			            },//去除网格线
    			 });
        		}
        	}
        	
        }

        if("AQI"!=tname[i]){
            var es = echarts.init(document.getElementById(tname[i]));
            es.group = 'group1';
            es.on('mouseover', function (params) {
//	    		es.group='';
                echarts.disconnect('group1');
            });
            es.on('legendselectchanged', function (params) {
                //图表联动
                echarts.disconnect('group1');
            });
            es.setOption(option);
            $(window).resize(es.resize);
        }else{	//AQI
            var aqi = echarts.init(document.getElementById(tname[i]));
            aqi.group = 'group1';
            aqi.on('mouseover', function (params) {
                echarts.connect('group1');
            });
            aqi.on('legendselectchanged', function (params) {
                //图表联动
                echarts.connect('group1');
            });
            aqi.setOption(option);
            $(window).resize(aqi.resize);
        }

    }	//循环物种结束

    $("#SO4").hide();
    $("#NO3").hide();
    $("#NH4").hide();
    $("#BC").hide();
    $("#OM").hide();
    $("#PMFINE").hide();


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
    //组分展开收缩  添加
    $(".timeUpDown .timeUpDownBtn").click(function(){
        if($(".timeUpDown .timeUpDownBtn").text()=="组分展开"){
            $(".timeUpDown .timeUpDownBtn").text("组分收起");
            $("#SO4").show();
            $("#NO3").show();
            $("#NH4").show();
            $("#BC").show();
            $("#OM").show();
            $("#PMFINE").show();
        }else{
            $(".timeUpDown .timeUpDownBtn").text("组分展开");
            $("#SO4").hide();
            $("#NO3").hide();
            $("#NH4").hide();
            $("#BC").hide();
            $("#OM").hide();
            $("#PMFINE").hide();
        }
    });

}

//easyui 添加
$(".toolAll").hide();
$(".searchT .upDownBtn").text("更多搜索条件");
$(".searchT .upDownBtn").append("<i class='en-arrow-up7'></i>")
$(".searchT .upDownBtn").click(function(){
    if($(".searchT .upDownBtn").text()=="收起"){
        $(".searchT .upDownBtn").text("更多搜索条件");
        $(".toolAll").hide();
        $(".searchT .upDownBtn i").remove();
        $(".searchT .upDownBtn").append("<i class='en-arrow-down8'></i>")
        $(".searchT .upDownBtn i").attr("class","en-arrow-down8")
        // headerH=$(".cloudui .searchT").height();
        // $(".charContent").css({"top":headerH+"px"})
    }else{
        $(".searchT .upDownBtn").text("收起");
        $(".searchT .toolAll").show();
        // headerH=$(".cloudui .searchT").height();
        $(".searchT .upDownBtn i").remove();
        $(".searchT .upDownBtn").append("<i class='en-arrow-up7'></i>");
        // $(".charContent").css({"top":headerH+"px"});
    }
})
$(".cloudui .verticalCon .searchT .upDown").hover(function(){
    $(".cloudui .verticalCon .searchT .upDown").css({"border-top":"1px solid #0275D8"});
    $(".cloudui .verticalCon .searchT .upDown .upDownBtn").css({"border":"1px solid #0275D8"});
},function(){
    $(".cloudui .verticalCon .searchT .upDown").css({"border-top":"1px solid #d9d9d9"});
    $(".cloudui .verticalCon .searchT .upDown .upDownBtn").css({"border":"1px solid #d9d9d9"});
})

function showTitleFun() {
    $('#showTitle span').empty();
    $('#showTitle span').css({"margin-right":"0px"});
    if (zhiCity.indexOf(changeMsg.pro) == -1) {
        if (changeMsg.station == 'avg') {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+proStation);
            $('#showTitle .cityName').html(cityStation).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
        } else {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+proStation);
            $('#showTitle .cityName').html(cityStation );
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
        }
    } else {
        if (changeMsg.station == 'avg') {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+cityStation ).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
        } else {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+cityStation );
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
        }
    }
    var timeTwoFor=moment(changeMsg.startD+"-"+changeMsg.endD,"YYYYMMDD-YYYYMMDD").format("YYYY年MM月DD日-YYYY年MM月DD日");
    $('#showTitle .dateName').html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;日期：</span>"+changeMsg.startD+"至"+changeMsg.endD).css({"margin-right":"40px"});
    $("#showTitle .spaceName").html("<span class='titleTab'><i class='en-flow-parallel'></i>"+"&nbsp;空间分辨率：</span>"+($('input[name=domain]:checked').val()=="3"?"3km":($('input[name=domain]:checked').val()==2?"9km":"27km"))).css({"margin-right":"40px"});
}