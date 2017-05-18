/**
 * Created by lvcheng on 2017/4/21.
 */

$(function () {
    /**
     *设置导航条信息
     */
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">预报检验</span>');

    initialize();
});

/*定义全局变量*/
var dps_Date, dps_City;
var dps_Station = {};
var allCode;
var changeMsg = {
    stationName:'',
    type: 'wrw',
    startD: '',
    endD: '',
    rms: 'day',
    pro: '',
    city: ''
};
var zhiCity = ['11', '12', '31', '50'];
var speciesAll = {
    wrw: {
        day: ['AQI','PM₂.₅', 'PM₁₀', 'O₃_8_max', 'SO₂', 'NO₂', 'CO'],
        hour: ['AQI','PM₂.₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO']
    },
    qxys: {
        day: [],
        hour: []
    }
};
var speciesObj = {
    'AQI':'AQI',
    'PM₂.₅': 'PM25',
    'PM₁₀': 'PM10',
    'O₃_8_max': 'O3_8_MAX',
    'O₃_1_max': 'O3_1_MAX',
    'O₃_AVG': 'O3_AVG',
    'SO₂': 'SO2',
    'NO₂': 'NO2',
    'CO': 'CO',
    'O₃': 'O3'
};


/*初始化函数*/
function initialize() {
    dps_City = requestRegion();
    dps_Date = requestDate();

    $.when(dps_Date, dps_City).then(function () {
        updata();
    })
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
    console.log($('#' + type + 'Date').data());
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

/*请求省市区code*/
var cityStation
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
            cityStation = allCode[$('.proStation').val()].city;
            for (var city in cityStation) {
                $('.cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
            }

            changeMsg.pro = $('.proStation').val();
            changeMsg.city = $('.cityStation').val();
            //changeMsg.station = $('#station').val();
            if (!dps_Station[changeMsg.city + changeMsg.type]) {
                dps_Station[changeMsg.city + changeMsg.type] = setStation(changeMsg.city, changeMsg.type);
            }
        } else {
            console.log('站点请求故障！！！')
        }

    })
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
            // initWrwDate(moment(res.data.mintime).format('YYYY-MM-DD'), moment(res.data.maxtime).format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);

            changeMsg.startD = '2017-04-27';
            changeMsg.endD = '2017-05-03';
            initWrwDate('2017-04-27','2017-05-03',changeMsg.startD,changeMsg.endD);
            initQxysDate(moment(res.data.mintime).format('YYYY-MM-DD'), moment(res.data.maxtime).format('YYYY-MM-DD'), changeMsg.startD, changeMsg.endD);
        }
    })
}

/*更新数据*/
var proStation,stationName;
var stationVal
function updata(opt) {
    $.when(dps_Station[changeMsg.city + changeMsg.type]).done(function (res) {
        if (!opt) {
            res = dps_Station[changeMsg.city + changeMsg.type].responseJSON;
            //var stationVal
            stationVal=res.data;
            $('#' + changeMsg.type + ' .station').empty();
            $('#' + changeMsg.type + ' .station').append($('<option value="avg">平均</option>'));
            for (var i = 0; i < res.data.length; i++) {
                $('#' + changeMsg.type + ' .station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
                //stationName=stationVal[$(".station").val()].stationName;
            }


            changeMsg.station = $('#' + changeMsg.type + ' .station').val();
        }

        $('.showTable').empty();
        var url = '/Air/checkout';
        ajaxPost(url, {
            userId: userId,
            mode: changeMsg.station == 'avg' ? 'city' : 'point',
            starttime:changeMsg.startD,
            endtime:changeMsg.endD,
            // starttime: '2017-05-01 00',
            // endtime: '2017-05-03 00',
            cityStation: changeMsg.station == 'avg' ? changeMsg.city : changeMsg.station,
            datetype: changeMsg.rms,
            tabType:changeMsg.type
        }).success(function (res) {
            $('.showTable').empty();
            for (var i = 0; i < speciesAll[changeMsg.type][changeMsg.rms].length; i++) {
                var div = $('.mnDataShow.disNone').clone();
                div.removeClass('disNone');
                div.addClass(speciesObj[speciesAll[changeMsg.type][changeMsg.rms][i]]);
                div.find('.mnName').html('<i class="im-tags"></i>'+speciesAll[changeMsg.type][changeMsg.rms][i]);
                $('.showTable').append(div);
            }

            var sp = speciesAll[changeMsg.type][changeMsg.rms];
            for (var n in res.data) {

                for (var s = 0; s < sp.length; s++) {
                    var spObj = res.data[n][speciesObj[sp[s]]];
                    if (!spObj) {
                        $('.' + speciesObj[sp[s]]).find('.' + n + 'bias').html('-');
                        $('.' + speciesObj[sp[s]]).find('.' + n + 'coefficient').html('-');
                        $('.' + speciesObj[sp[s]]).find('.' + n + 'rate').html('-');
                    } else {
                        for (var d in spObj) {
                            var num = res.data[n][speciesObj[sp[s]]][d];
                            if (num == undefined) {
                                $('.' + speciesObj[sp[s]]).find('.' + n + d).html('-');
                            } else {
                                $('.' + speciesObj[sp[s]]).find('.' + n + d).html(num);
                            }
                        }
                    }

                }

                // for(var s in res.data[n]){
                //     for(var d in res.data[n][s]){
                //         $('.'+s).find('.'+n+d).html(res.data[n][s][d]);
                //     }
                // }
            }

        })
    })
    proStation=allCode[$(".proStation").val()].name
    cityStation=allCode[$('.proStation').val()].city[$(".cityStation").val()]
    var tf=($(".statin").val()!='avg');

    showTitleFun();
}


/*change 改变事件*/
$('.proStation').on('change', function (e) {
    var pro = $(e.target).val();
    $('.proStation').val(pro);
    changeMsg.pro = pro;
    $('.cityStation').empty();
    cityStation= allCode[pro].city;
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
$('input[name=rmsWrw]').on('change', function (e) { //时间分辨率选择
    var rms = $(e.target).val();
    if (stint) {
        stint = false;
        $('#qxys input[value=' + rms + ']').parent().click();
        changeRms(rms)
    }
});

$('input[name=rmsQxys]').on('change', function (e) { //时间分辨率选择
    var rms = $(e.target).val();
    if (stint) {
        stint = false;
        $('#wrw input[value=' + rms + ']').parent().click();
        changeRms(rms)
    }
});

function changeRms(rms) {
    stint = true;
    changeMsg.rms = rms;
    updata(true);
}

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
//easyui 增加
var headerH=$(".cloudui .searchT").height();
$(".verticalChar").css({"top":headerH+"px"});
$(".toolAll").hide();
$(".upDownBtn").text("更多搜索条件");
$(".upDownBtn").append("<i class='en-arrow-up7'></i>")
$(".upDownBtn").click(function(){
    if($(".upDownBtn").text()=="收起"){
        $(".upDownBtn").text("更多搜索条件");
        $(".toolAll").hide();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-down8'></i>")
        $(".upDownBtn i").attr("class","en-arrow-down8")
        headerH=$(".cloudui .searchT").height();
        $(".charContent").css({"top":headerH+"px"})
    }else{
        $(".upDownBtn").text("收起");
        $(".toolAll").show();
        headerH=$(".cloudui .searchT").height();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-up7'></i>");
        $(".charContent").css({"top":headerH+"px"});
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
    if (zhiCity.indexOf(changeMsg.pro) == -1) {
        if (changeMsg.station == 'avg') {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+proStation);
            $('#showTitle .cityName').html(cityStation);
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时'));
        } else {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+proStation);
            $('#showTitle .cityName').html(cityStation );
            $('#showTitle .stationName').html(changeMsg.stationName);
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时'));
        }
    } else {
        if (changeMsg.station == 'avg') {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+cityStation );
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时'));
        } else {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+cityStation );
            $('#showTitle .stationName').html(changeMsg.stationName );
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时'));
        }
    }
    var timeTwoFor=moment(changeMsg.startD+"-"+changeMsg.endD,"YYYYMMDD-YYYYMMDD").format("YYYY年MM月DD日-YYYY年MM月DD日");
    $('#showTitle .dateName').html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;日期：</span>"+changeMsg.startD+"至"+changeMsg.endD);
}