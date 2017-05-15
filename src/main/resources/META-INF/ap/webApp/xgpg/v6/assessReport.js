$(function () {
    /**
     *设置导航条信息
     */
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">评估报告</span><a onclick="exchangeModal()" class="nav_right" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
    //全选复选框
    //initTableCheckbox();
});
/*存储全局改变量*/
var dps_codeStation, dps_station;
var ooo = [['-', 0], ['-', 50], ['-', 100], ['-', 200], ['-', 300], ['-', 400], ['-', 500], ['-', 700], ['-', 1000], ['-', 1500], ['-', 2000], ['-', 3000]];
var heightArr = [0, 50, 100, 200, 300, 400, 500, 700, 1000, 1500, 2000, 3000];
var changeMsg = {
    pro: '',//站点选择
    proName: '',
    city: '',
    cityName: '',
    station: '',
    stationName: '',
    height: 9,//高度选择
    rms: 'day',//时间分辨率
    time: '',//时间选择
    scenarinoId: [],//选择的情景Id数组
    scenarinoName: [],//选择的情景名称数组
};
var zhiCity = ['11', '12', '31', '50'];
var ls = window.sessionStorage;
var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if (!sceneInitialization) {
    sceneInitialization = JSON.parse(ls.getItem('SI'));
} else {
    ls.setItem('SI', JSON.stringify(sceneInitialization));
}

if (!sceneInitialization) {
    sceneInittion();
} else {
    //set_sce1_sce2();
    initialize();
}


/*初始化页面数据,在缓存中有数据的情况下*/
function initialize() {
    console.log("加载数据")
    changeMsg.scenarinoId = [];
    changeMsg.scenarinoName = [];
    for (var i = 0; i < sceneInitialization.data.length; i++) {
        changeMsg.scenarinoId.push(sceneInitialization.data[i].scenarinoId);
        changeMsg.scenarinoName.push(sceneInitialization.data[i].scenarinoName);
    }
    //changeMsg.scenarinoId.unshift('-1');
    //changeMsg.scenarinoName.unshift('基准');

    setStation(sceneInitialization.taskID);
    setTime(sceneInitialization.s, sceneInitialization.e);
    $.when(dps_codeStation, dps_station).then(function () {
        updata();
    });
    //updata();
    //initEcharts();

}

var allCode = {};//用于存储所有的站点信息
/*设置站点*/
function setStation(id) {
    $('#proStation').empty();
    $('#cityStation').empty();
    $('#station').empty();
    var url = '/Site/find_codes';
    dps_codeStation = ajaxPost(url, {
        userId: userId,
        MissionId: id
    }).success(function (res) {
        if (res.status == 0) {
            allCode = res.data;
            for (var pro in allCode) {
                $('#proStation').append($('<option value="' + pro + '">' + allCode[pro].name + '</option>'))
            }
            var cityStation = allCode[$('#proStation').val()].city;
            for (var city in cityStation) {
                $('#cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
            }

            //var station = cityStation[$('#cityStation').val()].station;
            //for (var s in station) {
            //  $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
            //}

            changeMsg.pro = $('#proStation').val();
            changeMsg.proName = allCode[changeMsg.pro].name;
            changeMsg.city = $('#cityStation').val();
            changeMsg.cityName = allCode[changeMsg.pro].city[changeMsg.city];
            findStation(changeMsg.city);
            //changeMsg.station = $('#station').val();
        } else {
            console.log('站点请求故障！！！')
        }
    })
}
/*查询站点*/
function findStation(code) {
    dps_station = ajaxPost('/Site/find_Site', {
        userId: userId,
        siteCode: code
    }).success(function (res) {
        $('#station').empty();
        $('#station').append($('<option value="avg">平均</option>'));
        for (var i = 0; i < res.data.length; i++) {
            $('#station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
        }
        changeMsg.station = $('#station').val();
        changeMsg.stationName = '平均';
    })
}

/*设置时间*/
/*只限输入毫秒数*/
function setTime(s, e) {
    s = moment(s - 0);
    e = moment(e - 0);
    $('#sTime-d').empty();
    while (true) {
        $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
        if (e.format('YYYY-MM-DD') == 'Invalid date') {
            break;
        }
        if (e.isBefore(s.add(1, 'd'))) {
            break;
        }
    }
    changeMsg.time = $('#sTime-d').val() + ' 00'
}


function initEcharts() {
    showTitleFun();
    //console.log("初始化表单")
    if (changeMsg.rms == 'day') {
        $('.hour').css('display', 'none');
        $('.day').css('display', 'block');
    } else {
        $('.day').css('display', 'none');
        $('.hour').css('display', 'block');
    }
    var data = czData;
    //$('#initEcharts').empty();
    var species = speciesArr[changeMsg.rms];
    for (var i = 0; i < species.length; i++) {
        echarts.dispose(document.getElementById(species[i]));
        var es = echarts.init(document.getElementById(species[i]));
        //es.clear();
        //var div = $('<div></div>');
        //div.attr('id', species[i]);
        //div.addClass('echartsCZ');
        //div.addClass('col-md-3');
        //$('#initEcharts').append(div);
        var option = $.extend(true, {}, optionAll);

        option.legend.data = (function () {
            var arr = [];
            for (var a = 0; a < sceneInitialization.data.length; a++) {
                arr.push(sceneInitialization.data[a].scenarinoName)
            }
            ;

            //for (var a = 0; a < 3; a++) {
            //  arr.push('a'+a+1)
            //};

            arr.unshift('基准');
            return arr;
        })();
        //option.legend.data = (function () {
        //  var arr = [];
        //  for (var a = 0; a < 5; a++) {
        //    arr.push('aaaaaaaaaa'+a)
        //  }
        //  return arr;
        //})();
        if (species[i] != 'CO') {

            switch (species[i]) {
                case 'SO₄':
                    option.title.text = species[i] + "²¯ (μg/m³)";
                    break;
                case 'NO₃':
                    option.title.text = species[i] + "¯ (μg/m³)";
                    break;
                case 'NH₄':
                    option.title.text = species[i] + "⁺ (μg/m³)";
                    break;
                default:
                    option.title.text = species[i] + " (μg/m³)";
            }

        } else {
            option.title.text = species[i] + " (mg/m³)";
        }
        option.series = [];
        //var arrrr = [466,458,456];
        //for (var sp = 0; sp < 3; sp++) {
        //  //for (var sp = 0; sp < 5; sp++) {
        //  var id = arrrr[sp];
        //  var name = 'a'+sp+1;


        for (var sp = 0; sp < sceneInitialization.data.length; sp++) {
            //for (var sp = 0; sp < 5; sp++) {
            var id = sceneInitialization.data[sp].scenarinoId;
            var name = sceneInitialization.data[sp].scenarinoName;


            option.series.push({
                name: name, //可变，存储情景名称
                //name: 'aaaaaaaaaa'+sp, //可变，存储情景名称
                type: 'line',
                smooth: true,
                symbolSize: 5,
                data: data[id][speciesObj[species[i]]].slice(0, $('#height').val())  //可变，存储情景数据
                //data: data['191']['CO']  //可变，存储情景数据
            })
        }
        option.series.unshift({
            name: '基准', //可变，存储情景名称
            //name: 'aaaaaaaaaa'+sp, //可变，存储情景名称
            type: 'line',
            smooth: true,
            symbolSize: 5,
            data: data['-1'][speciesObj[species[i]]].slice(0, $('#height').val())  //可变，存储情景数据
            //data: data['191']['CO']  //可变，存储情景数据
        });
        es.setOption(option);

        //自适应屏幕大小变化
        $(window).resize(es.resize);
        //自适应结束

    }
}

//超链接显示 模态框
function exchangeModal() {
    sceneInittion();
   // $("#Initialization").modal();
    //$("#Initialization").window("open")
}
var allMission = {};
/**
 * 初始化模态框显示
 */
