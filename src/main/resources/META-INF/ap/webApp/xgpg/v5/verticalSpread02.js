$(function () {
    /**
     *设置导航条信息
     */
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">垂直廓线</span><a onclick="exchangeModal()" class="nav_right" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a><div style="width:250px;height:17px;float:right;"><span style="padding-left: 15px;padding-right: 15px;float:left;" id="missionName"></span></div>');
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
$('.day').css('display', 'block');
$('.hour').css('display', 'none');
var speciesArr = {
    day: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE'],
    hour: ['PM₂.₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE']
};
var speciesObj = {
    'PM₂.₅': 'PM25',
    'PM₁₀': 'PM10',
    'O₃_8_max': 'O3_8_MAX',
    'O₃_1_max': 'O3_1_MAX',
    'O₃_avg': 'O3_AVG',
    'SO₂': 'SO2',
    'NO₂': 'NO2',
    'CO': 'CO',
    'SO₄': 'SO4',
    'NO₃': 'NO3',
    'NH₄': 'NH4',
    'BC': 'BC',
    'OM': 'OM',
    'PMFINE': 'PMFINE',
    //'O₃':'O3'
    'O₃': 'O3'
};
/*echarts 配置*/
var optionAll = {
    grid: {
        x: 50
    },
    title: {
        bottom: 10,
        left: "center",
        text: ''  //可变，存储每个污染物
    },
    legend: {
        data: [] //可变，存储所有已选情景
    },
    toolbox: {
        show: true,
        feature: {
            mark: {show: true}
        }
    },
    calculable: true,
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'line',
            axis: 'y',
            snap: true,
        },
        formatter: function (params, ticket, callback) {

            var hm = params[0].axisValue + 'm<br />';
            for (var i = 0; i < params.length; i++) {
                if (i == params.length - 1) {
                    hm = hm + '<i style="width:10px;height:10px;display:inline-block;border-radius:100%;background-color: ' + params[i].color + ';"></i> ' + params[i].seriesName + ' : ' + params[i].data[0]
                } else {
                    hm = hm + '<i style="width:10px;height:10px;display:inline-block;border-radius:100%;background-color: ' + params[i].color + ';"></i> ' + params[i].seriesName + ' : ' + params[i].data[0] + '<br />'
                }
            }

            return hm;
        }
    },
    xAxis: {
        type: 'value',
        //name: '/μg/m³',  //可变，CO为mg/m³其他的为'μg/m³
        nameGap: 0,
        boundaryGap: ['0%', '8%'],
    },
    yAxis: {
        //name: 'm',
        nameGap: 5,
        type: 'value',
        axisLabel: {
            formatter: '{value}m'
        },
//    interval: 100,
        axisLine: {onZero: false},
    },
    color: ["#e6b600", "#0098d9", "#2b821d", "#005eaa", "#339ca8", "#cda819", "#32a487"],
    series: [  //可变，存储所有情景数据

    ],
    animation: false
};

var ls = window.sessionStorage;
var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if (!sceneInitialization) {
    sceneInitialization = JSON.parse(ls.getItem('SI'));
} else {
    ls.setItem('SI', JSON.stringify(sceneInitialization));
}

console.log(JSON.stringify(sceneInitialization));
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
    $("#missionName").text("任务:"+sceneInitialization.missionName);
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
    if (changeMsg.rms == 'day') {
        $('.hour').css('display', 'none');
        $('.day').css('display', 'block');
    } else {
        $('.day').css('display', 'none');
        $('.hour').css('display', 'block');
    }
    var data = czData;
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
                    option.title.text = species[i] + "²⁻ (μg/m³)";
                    break;
                case 'NO₃':
                    option.title.text = species[i] + "⁻ (μg/m³)";
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
function sceneInittion() {
    $("#task").html("");
    var paramsName = {};
    paramsName.userId = userId;
    //console.log(JSON.stringify(paramsName));
    ajaxPost('/mission/find_All_mission', paramsName).success(function (res) {
      //  console.log(JSON.stringify(res));
        if (res.status == 0) {
            if (res.data || res.data.length > 0) {
                //if(false){
                var task = "";


              /*测试数据*/
                //res.data = [
                //  {
                //    missionEndDate: 1480258800000,
                //    missionId: 393,
                //    missionName: "测试任务",
                //    missionStartDate: 1479571200000,
                //  }
                //]
              /*测试数据 end*/


                $.each(res.data, function (k, vol) {
                    allMission[vol.missionId] = vol;
                    if (sceneInitialization) {
                        if (sceneInitialization.taskID == vol.missionId) {
                            task += '<option value="' + vol.missionId + '" selected="selected">' + vol.missionName + '</option>';
                        } else {
                            task += '<option value="' + vol.missionId + '">' + vol.missionName + '</option>';
                        }
                    } else {
                        task += '<option value="' + vol.missionId + '">' + vol.missionName + '</option>';
                    }
                });
                $("#task").html(task);

                if(!$.isEmptyObject(allMission)){
                    var id = $("#task").val();
                    if(allMission[id].missionStatus == '2'){
                        $('#pathDdiv').css('display','block');
                        $('#pathD').html('');

                        for(var ids in allMission[id].pathdates){
                            if(ids == sceneInitialization.pathdate){
                                $('#pathD').append($('<option selected="selected" value="'+ ids +'">'+ (ids==-1?'无':moment(allMission[id].pathdates[ids].pathDate).format('YYYY-MM-DD')) +'</option>'))
                            }else{
                                $('#pathD').append($('<option value="'+ ids +'">'+ (ids==-1?'无':moment(allMission[id].pathdates[ids].pathDate).format('YYYY-MM-DD')) +'</option>'))
                            }

                        }
                    }else{
                        $('#pathDdiv').css('display','none');
                        $('#pathD').html('');
                    }
                }

//      $("#Initialization").modal();//初始化模态框显示
               /*$("#Initialization").modal({backdrop: 'static', keyboard: false});*/
                $("#Initialization").window('open');
                sceneTable();
            } else {
                swal('无可用任务', '', 'error')
            }
        } else {
            swal('接口故障', '', 'error')
        }
    }).error(function () {
        swal('服务未连接', '', 'error')
    });
}

/**
 * 选择任务时候判断是否为预评估任务进行pathDate选择
 */
function selectRwId() {
    var id = $("#task").val();
    if(allMission[id].missionStatus == '2'){
        $('#pathDdiv').css('display','block');
        $('#pathD').empty();

        for(var ids in allMission[id].pathdates){
            $('#pathD').append($('<option value="'+ ids +'">'+ (ids == -1?"无":moment(allMission[id].pathdates[ids].pathDate).format('YYYY-MM-DD')) +'</option>'))
        }
    }else{
        $('#pathDdiv').css('display','none');
        $('#pathD').empty();
    }
    sceneTable();
}

/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable() {
    $("#sceneTableId").bootstrapTable('destroy');//销毁现有表格数据
    //表格交互 easyui
    ajaxPost('/scenarino/find_All_scenarino',{
        "pathDate":(function () {
            if(allMission[$('#task').val()].missionStatus == '2'){
                if($('#pathD').val() != -1){
                    return moment(allMission[$('#task').val()].pathdates[$('#pathD').val()].pathDate).format('YYYY-MM-DD')
                }else{
                    return ''
                }
            }else{
                return ''
            }
        })(),
        "userId": userId,
        "missionId":$("#task").val()
    }).success(function(data){
        $("#sceneTableId").datagrid({
            data:data.data.rows,
            columns:[[
                {field:"ck",checkbox:true},
                {field:"scenarinoName",title:"情景名称"},
                {field:"scenType",title:"情景描述"},
                {field:"scenarinoStartDate",title:"时间",formatter:function(value,row,index){
                    return  moment(value).format("YYYY年MM月DD日");
                },align:'center'},
                {field:"scenarinoEndDate",title:"时间",formatter:function(value,row,index){
                    return  moment(value).format("YYYY年MM月DD日");
                },align:'center'},
            ]],
            clickToSelect: true,// 点击选中行
            pagination: false, // 在表格底部显示分页工具栏
            striped: true, // 使表格带有条纹
            queryParams: function (params) {
                var data = {};
                data.userId = userId;
                data.missionId = $("#task").val();
                return JSON.stringify({"token": "", "data": data});
            },
            onLoadSuccess:function(data){
                if(sceneInitialization!=null&&sceneInitialization!=""&&sceneInitialization!=undefined){
                    var truedData=sceneInitialization.data;
                    for(var i=0;i<truedData.length;i++){
                        if(data){
                            $.each(data.rows, function(index, item){
                                if(truedData[i].scenarinoId==item.scenarinoId){
                                    $('#sceneTableId').datagrid('checkRow', index);
                                }
                            });
                        }
                    }
                }
            }
        })
    })
/*
    $("#sceneTableId").bootstrapTable({
        method: 'POST',
        url: '/ampc/scenarino/find_All_scenarino',
        dataType: "json",
        iconSize: "outline",
        clickToSelect: true,// 点击选中行
        pagination: false, // 在表格底部显示分页工具栏
        striped: true, // 使表格带有条纹
        queryParams: function (params) {
            var data = {};
            data.userId = userId;
            data.missionId = $("#task").val();
            return JSON.stringify({"token": "", "data": data});
        },
        queryParamsType: "limit", // 参数格式,发送标准的RESTFul类型的参数请求
        silent: true, // 刷新事件必须设置
        contentType: "application/json", // 请求远程数据的内容类型。
        responseHandler: function (res) {
            if (res.status == 0) {
                if (!res.data.rows) {
                    res.data.rows = [];
                } else if (res.data.rows.length > 0) {
                    if (sceneInitialization) {
                        if (sceneInitialization.data.length > 0) {

                            $.each(res.data.rows, function (i, col) {
                                $.each(sceneInitialization.data, function (k, vol) {
                                    if (col.scenarinoId == vol.scenarinoId) {
                                        res.data.rows[i].state = true;
                                    }
                                });
                            });
                        }
                    }
                }
                return res.data.rows;
            } else if (res.status == 1000) {
                swal(res.msg, '', 'error');
            }


          /*测试使用 start*/
            //var data = {
            //  rows:[]
            //}
            //data.rows = [
            //  {
            //    "scenarinoId": 456,
            //    "scenarinoName": "情景1",
            //    "scenType": "1",
            //    "scenarinoStartDate": 1479571200000,
            //    "scenarinoEndDate": 1480258800000
            //  },
            //  {
            //    "scenarinoId": 458,
            //    "scenarinoName": "情景2",
            //    "scenType": "1",
            //    "scenarinoStartDate": 1479571200000,
            //    "scenarinoEndDate": 1480258800000
            //  },
            //  {
            //    "scenarinoId": 466,
            //    "scenarinoName": "情景3",
            //    "scenType": "1",
            //    "scenarinoStartDate": 1479571200000,
            //    "scenarinoEndDate": 1480258800000
            //  },
            //];
            //return data.rows

          /*测试使用 end*/
          /*
        },
        onClickRow: function (row, $element) {
            $('.success').removeClass('success');
            $($element).addClass('success');
        },
        icons: {
            refresh: "glyphicon-repeat",
            toggle: "glyphicon-list-alt",
            columns: "glyphicon-list"
        },
        onLoadSuccess: function (data) {
//			console.log(data);
        },
        onLoadError: function () {
            swal('连接错误', '', 'error');
        }
    });*/
}
/**
 * 保存选择的情景
 */
function save_scene() {
    var row = $('#sceneTableId').datagrid('getSelections');//获取所有选中的情景数据
    if (row.length > 0) {
        var mag = {};
        mag.id = "sceneInitialization";
        mag.taskID = $("#task").val();
        mag.missionName = $("#task :selected").text();
        mag.pathdate = $('#pathD').val();
        mag.pathMission = (function () {
            return JSON.stringify(allMission[mag.taskID].pathmission);
            /*if(allMission[mag.taskID].missionStatus == '2'){
             if(allMission[mag.taskID].pathmission[mag.jzID] == '-1'){
             return '-1'
             }else{
             return allMission[mag.taskID].pathmission[mag.jzID]
             }
             }else{
             if(allMission[mag.taskID].jzqjid){
             return mag.taskID
             }else{
             return -1
             }
             }*/
        })();
        mag.domainId = allMission[mag.taskID].domainId;
        mag.s = allMission[mag.taskID].missionStartDate;
        mag.e = allMission[mag.taskID].missionEndDate;
        mag.jzID = allMission[mag.taskID].missionStatus == '2'?$('#pathD').val():allMission[mag.taskID].jzqjid;
        mag.jzS = allMission[mag.taskID].pathdates[mag.jzID]?allMission[mag.taskID].pathdates[mag.jzID].startDate:mag.s;
        mag.jzE = allMission[mag.taskID].pathdates[mag.jzID]?allMission[mag.taskID].pathdates[mag.jzID].endDate:mag.e;
        var data = [];
        $.each(row, function (i, col) {
            data.push({
                "scenarinoId": col.scenarinoId,
                "scenarinoName": col.scenarinoName,
                "scenarinoStartDate": col.scenarinoStartDate,
                "scenarinoEndDate": col.scenarinoEndDate,
                "scenType":col.scenType
            });
        });
        mag.data = data;
        vipspa.setMessage(mag);
        ls.setItem('SI', JSON.stringify(mag));
        console.log(data);
        sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
        $("#close_scene").click();
        //添加任务名称
		$("#missionName").text("任务:"+sceneInitialization.missionName);
        var url='/Appraisal/show_Times';
        var paramsName = {
            "missionId":sceneInitialization.taskID,				//任务ID
        };
        ajaxPost(url, paramsName).success(function (res) {
            if (res.status == 0) {
                showTime=res;
                qjStartTime=showTime.data.startTime;
                qjEndTime=showTime.data.endTime;
            } else {
                swal(res.msg, '', 'error');
            }

        });
      /*数据准备完毕，进行初始化页面*/
        initialize();
    } else {
        swal('暂无数据', '', 'error');
        $("#close_scene").click();
    }
}


$('input[name=rms]').on('change', function (e) { //时间分辨率选择
    var rms = $(e.target).val();
    changeMsg.rms = rms;
    console.log(rms);
    $('#species').empty();
    for (var i = 0; i < speciesArr[rms].length; i++) {
        $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
    }
    $('#sTime-d').css('width', '100%');
    if (rms == 'day') {
        $('#sTime-h').addClass('disNone');
        $('#eTimeP').addClass('disNone');
    } else if (rms == 'hour') {
        $('#sTime-d').css('width', '65%');
        $('#sTime-h').removeClass('disNone');
        $('#eTimeP').addClass('disNone');
    }

    updata();
});

/*站点改变事件*/
$('#proStation').on('change', function (e) {
    var pro = $(e.target).val();
    changeMsg.pro = pro;
    changeMsg.proName = allCode[pro].name;
    $('#cityStation').empty();
    var cityStation = allCode[pro].city;
    for (var city in cityStation) {
        $('#cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
    }
    changeMsg.city = $('#cityStation').val();
    changeMsg.cityName = allCode[changeMsg.pro].city[changeMsg.city];
    findStation(changeMsg.city);
    //var station = cityStation[changeMsg.city].station;
    //for (var s in station) {
    //  $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
    //}
    //changeMsg.station = $('#station').val();

    updata();
});

$('#cityStation').on('change', function (e) {
    var city = $(e.target).val();
    changeMsg.city = city;
    changeMsg.cityName = allCode[changeMsg.pro].city[city];
    findStation(changeMsg.city);
    //var station = allCode[changeMsg.pro].station[city].station;
    //for (var s in station) {
    //  $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
    //}
    //changeMsg.station = $('#station').val();

    updata();
});

$('#station').on('change', function (e) {
    var station = $(e.target).val();
    changeMsg.station = station;
    changeMsg.stationName = $(e.target)[0].selectedOptions[0].innerHTML;

    updata();
});

$('#sTime-d').on('change', function (e) {
    var time_d = $(e.target).val();
    var time_h = $('#sTime-h').val('00').val();

    changeMsg.time = time_d + ' ' + time_h;
    updata();
});

$('#sTime-h').on('change', function (e) {
    var time_h = $(e.target).val();
    var time_d = $('#sTime-d').val();

    changeMsg.time = time_d + ' ' + time_h;
    updata();
});

$('#height').on('change', function (e) {
    var height = $(e.target).val();
    changeMsg.height = height;

    initEcharts();
    //updata();
});

var czData;
/*设置echarts图表*/
function updata() {
    showTitleFun()
    $.when(dps_station).then(function () {
        var url = '/Appraisal/find_vertical';
        var urlJZ = '/Appraisal/find_basevertical';
        var echartsData = ajaxPost(url, {
            userId: userId,
            missionId: sceneInitialization.taskID,
            mode: changeMsg.station == 'avg' ? 'city' : 'point',
            time: changeMsg.time,
            cityStation: changeMsg.station == 'avg' ? changeMsg.city.substr(0, 4) : changeMsg.station,
            scenarinoId: changeMsg.scenarinoId,
            datetype: changeMsg.rms


            //missionId:sceneInitialization.taskID,
            //mode:changeMsg.station=='avg'?'city':'point',
            //time:changeMsg.time,
            //"userId":"1",
            //cityStation:changeMsg.station=='avg'?changeMsg.city:changeMsg.station,
            //"scenarinoId":changeMsg.scenarinoId,
            ////"scenarinoId":[466,458,456],
            //datetype:changeMsg.rms
        });
        var echartsJZData = ajaxPost(urlJZ, {
            userId: userId,
            missionId: sceneInitialization.taskID,
            mode: changeMsg.station == 'avg' ? 'city' : 'point',
            time: changeMsg.time,
            cityStation: changeMsg.station == 'avg' ? changeMsg.city.substr(0, 4) : changeMsg.station,
            //scenarinoId:changeMsg.scenarinoId,
            datetype: changeMsg.rms


            //missionId:sceneInitialization.taskID,
            //mode:changeMsg.station=='avg'?'city':'point',
            //time:changeMsg.time,
            //"userId":"1",
            //cityStation:changeMsg.station=='avg'?changeMsg.city:changeMsg.station,
            ////"scenarinoId":changeMsg.scenarinoId,
            //datetype:changeMsg.rms
        });

        $.when(echartsData, echartsJZData).then(function (res, resJZ) {
            if ((res[0].status == 0) && (resJZ[0].status == 0)) {
                for (var i = 0; i < changeMsg.scenarinoId.length; i++) {
                    if (!res[0].data[changeMsg.scenarinoId[i]]) {
                        res[0].data[changeMsg.scenarinoId[i]] = {};
                        for (var y in speciesObj) {
                            res[0].data[changeMsg.scenarinoId[i]][speciesObj[y]] = ooo;
                        }
                    } else {
                        for (var y in speciesObj) {
                            if ((!res[0].data[changeMsg.scenarinoId[i]][speciesObj[y]]) || (res[0].data[changeMsg.scenarinoId[i]][speciesObj[y]].length == 0)) {
                                res[0].data[changeMsg.scenarinoId[i]][speciesObj[y]] = ooo;
                            }
                        }
                    }
                }
                if ($.isEmptyObject(resJZ[0].data)) {
                    resJZ[0].data['-1'] = {};
                    for (var i in speciesObj) {
                        resJZ[0].data['-1'][speciesObj[i]] = ooo;
                    }
                }
                var obj = {};
                $.extend(obj, resJZ[0].data, res[0].data);
                czData = obj;


                initEcharts();
            } else {
                console.log(res.msg)
            }
        }, function () {
            console.log('接口故障！！！')
        })
    });
}

function showTitleFun() {
    $('#showTitle span').empty();
    $('#showTitle span').css({"margin-right":"0px"});
    if (zhiCity.indexOf(changeMsg.pro) == -1) {
        if (changeMsg.station == 'avg') {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.proName);
            $('#showTitle .cityName').html(changeMsg.cityName).css({"margin-right":"40px"});
            $('#showTitle .timeName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+(changeMsg.rms == 'day' ? changeMsg.time.substr(0, 10) : changeMsg.time)).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
            $('#showTitle .heightName').html("<span class='titleTab'><i class='fa-sort-by-attributes-alt'></i>"+"&nbsp;高度：</span>"+heightArr[changeMsg.height - 1] + 'm').css({"margin-right":"40px"});
        } else {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.proName + '<i class="en-arrow-right7" style="font-size:16px;"></i>');
            $('#showTitle .cityName').html(changeMsg.cityName);
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            $('#showTitle .timeName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+(changeMsg.rms == 'day' ? changeMsg.time.substr(0, 10) : changeMsg.time)).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
            $('#showTitle .heightName').html("<span class='titleTab'><i class='fa-sort-by-attributes-alt'></i>"+"&nbsp;高度：</span>"+heightArr[changeMsg.height - 1] + 'm').css({"margin-right":"40px"});
        }
    } else {
        if (changeMsg.station == 'avg') {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.cityName).css({"margin-right":"40px"});
            $('#showTitle .timeName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+(changeMsg.rms == 'day' ? changeMsg.time.substr(0, 10) : changeMsg.time)).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时')).css({"margin-right":"40px"});
            $('#showTitle .heightName').html("<span class='titleTab'><i class='fa-sort-by-attributes-alt'></i>"+"&nbsp;高度：</span>"+heightArr[changeMsg.height - 1] + 'm').css({"margin-right":"40px"});
        } else {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.cityName);
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            $('#showTitle .timeName').html("<span class='titleTab'><i class='br-calendar' style='font-size: 16px;'></i>"+"&nbsp;日期：</span>"+(changeMsg.rms == 'day' ? changeMsg.time.substr(0, 10) : changeMsg.time)).css({"margin-right":"40px"});
            $('#showTitle .rmsName').html("<span class='titleTab'><i class='im-clock2' style='font-size: 16px;'></i>"+"&nbsp;时间分辨率：</span>"+(changeMsg.rms == 'day' ? '逐日' : '逐小时') ).css({"margin-right":"40px"});
            $('#showTitle .heightName').html("<span class='titleTab'><i class='fa-sort-by-attributes-alt'></i>"+"&nbsp;高度：</span>"+heightArr[changeMsg.height - 1] + 'm').css({"margin-right":"40px"});
        }
    }
}

//easyui 添加
$(".toolAll").hide();
$(".upDownBtn").append("<i class='en-arrow-up7'></i>")
var headerH = $(".cloudui .searchT").height();
$(".verticalChar").css({"top": headerH + "px"});
$(".upDownBtn").click(function(){
    console.log($(".upDownBtn").val());
    if($(".upDownBtn").text()=="收起"){
        $(".upDownBtn").text("更多搜索条件");
        $(".toolAll").hide();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-down8'></i>")
        $(".upDownBtn i").attr("class","en-arrow-down8")
        headerH=$(".cloudui .searchT").height();
       $(".verticalChar").css({"top":headerH+"px"})
    }else{
        $(".upDownBtn").text("收起");
        $(".toolAll").show();
       headerH=$(".cloudui .searchT").height();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-up7'></i>");
        $(".verticalChar").css({"top":headerH+"px"});
    }
})
$(".cloudui .verticalCon .ibox-content .searchT .upDown").hover(function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #0275D8"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #0275D8"});
},function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #d9d9d9"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #d9d9d9"});
})
// $(".verticalChar").slimScroll({
//     height: '100%'
// })



