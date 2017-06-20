$(function () {
    /**
     *设置导航条信息
     */
    $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">评估报告</span><a onclick="exchangeModal()" class="nav_right" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a><div style="width:250px;height:17px;float:right;"><span style="padding-left: 15px;padding-right: 15px;float:left;" id="missionName"></span></div>');
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
    rms: 'day',//时间分辨率
    time: '',//时间选择
    scenarinoId: [],//选择的情景Id数组
    scenarinoName: [],//选择的情景名称数组
    sTimeD: '',//开始日期
    eTime: ''//结束日期
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
// var showTime="";
// var qjStartTime;
// var qjEndTime;

var ls = window.sessionStorage;
var sceneInitialization = vipspa.getMessage('sceneInitialization').content;//从路由中取到情景范围
if (!sceneInitialization) {
    sceneInitialization = JSON.parse(ls.getItem('SI'));
} else {
    ls.setItem('SI', JSON.stringify(sceneInitialization));
}
//
// console.log(JSON.stringify(sceneInitialization));
if (!sceneInitialization) {
    sceneInittion();
} else {
    setQjSelectBtn(sceneInitialization.data);//加载地图上的情景按钮
    initialize();
}


/*初始化页面数据,在缓存中有数据的情况下*/
function initialize() {

    changeMsg.scenarinoId = [];
    changeMsg.scenarinoName = [];

    var qjNum=$("#qjbtnCon").val();
    changeMsg.qjId=sceneInitialization.data[qjNum].scenarinoId;
    changeMsg.qjName=sceneInitialization.data[qjNum].scenarinoName;
    // changeMsg.sTimeD=$("#sTime-d").val();
    // changeMsg.eTime=$("#eTime").val();
    // console.log("时间")
    // console.log($("#sTime-d").text());
    // console.log($("#eTime").text())

    for (var i = 0; i < sceneInitialization.data.length; i++) {
        //console.log("加载数据")
        //console.log(sceneInitialization.data[i].scenarinoId=sceneInitialization.data[i].scenarinoName);
        changeMsg.scenarinoId.push(sceneInitialization.data[i].scenarinoId);
        changeMsg.scenarinoName.push(sceneInitialization.data[i].scenarinoName);
    }
    //changeMsg.scenarinoId.unshift('-1');
    //changeMsg.scenarinoName.unshift('基准');
    dps_station = setStation(sceneInitialization.taskID);
    $("#missionName").text("任务:"+sceneInitialization.missionName);
    setTime(sceneInitialization.data[$("#qjbtnCon").val()].scenarinoStartDate,sceneInitialization.data[$("#qjbtnCon").val()].scenarinoEndDate);
    console.log(sceneInitialization.data[$("#qjbtnCon").val()].scenarinoStartDate)
    $.when(dps_codeStation, dps_station).done(function () {
        window.setTimeout(function () {
            updata();
        },200)
    });
    //updata();
    //initEcharts();

}
/*添加情景选择按钮*/
function setQjSelectBtn(data) {
    $('.cloudui #qjbtnCon').empty();
    for (var i = 0; i < data.length; i++) {
        var btn1 = $('<option value="'+i+'">'+data[i].scenarinoName+'</option>');
        $('.cloudui #qjbtnCon').append(btn1);

    }
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
    $('#eTime').empty();
    while (true) {
        $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
        $('#eTime').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
        if (e.format('YYYY-MM-DD') == 'Invalid date') {
            break;
        }
        if (e.isBefore(s.add(1, 'd'))) {
            break;
        }
    }
    changeMsg.sTimeD = $('#sTime-d').val()
    changeMsg.eTime = $('#eTime').val()

}

//超链接显示 模态框
function exchangeModal() {
    sceneInittion();
}
var allMission = {};
/**
 * 初始化模态框显示
 */
function sceneInittion() {
    $("#task").html("");
    var paramsName = {};
    paramsName.userId = userId;
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
    $.ajax({
        url: '/ampc/scenarino/find_All_scenarino',
        contentType: 'application/json',
        method: 'post',
        dataType: 'JSON',
        data: JSON.stringify({
            "token": "",
            "data": {
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
                "userId": 1,
                "missionId":$("#task").val()
            }
        }),
        success:function (data) {
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
                    if(sceneInitialization!=""&&sceneInitialization!=null&&sceneInitialization!=undefined){
                        var truedData=sceneInitialization.data;
                        for(var i=0;i<truedData.length;i++){
                            if(data){
                                $.each(data.rows, function(index, item){
                                    // console.log(index);
                                    // console.log(item);
                                    if(truedData[i].scenarinoId==item.scenarinoId){
                                        $('#sceneTableId').datagrid('checkRow', index);
                                    }
                                });
                            }
                        }
                    }

                }
            })
        }
    })
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
        sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
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

        $("#close_scene").click();
        $("#missionName").text("任务:"+sceneInitialization.missionName);
        /*数据准备完毕，进行初始化页面*/
        setQjSelectBtn(data);//添加情景选择按钮
        initialize();
    } else {
        swal('暂无数据', '', 'error');
        $("#close_scene").click();
    }
}

/*改变情景*/
$("#qjbtnCon").on('change',function(e){
    var index=$(e.target).val();
    changeMsg.qjId=sceneInitialization.data[index].scenarinoId;
    changeMsg.qjName=sceneInitialization.data[index].scenarinoName;
    updata();

})


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
    if(moment(time_d).isAfter(moment($("#eTime").val()))){
        $("#eTime").val(time_d);
        changeMsg.eTime = time_d;
    }
    changeMsg.sTimeD=time_d;
    updata();
});
$('#eTime').on('change', function (e) {
    var time_d = $(e.target).val();
    if(moment(time_d).isBefore(moment($("#sTime-d").val()))){
        $("#sTime-d").val(time_d);
        changeMsg.sTimeD = time_d;
    }
    changeMsg.eTime=time_d;
    updata();
});


var czData;
/*设置echarts图表*/
var allData;
function updata() {

    showTitleFun();
    shownPgbg()
    ajaxPost('/Appraisal/report',{"mode":changeMsg.station == 'avg' ? 'city' : 'point',
                     "startdate":changeMsg.sTimeD+" 00",
                     "enddate":changeMsg.eTime+" 00",
                     "userId":userId,
                     "cityStation":changeMsg.station == 'avg' ? changeMsg.city.substr(0, 4) : changeMsg.station,
                     "scenarinoId":changeMsg.qjId,
                     "missionId":sceneInitialization.taskID}).success(function(res){
        if(res.status==0){
                        allData=res.data;
                        $("#PM25_jp").html(allData.PM25_jp==null?"暂无":allData.PM25_jp)
                        $("#SO2_jp").html(allData.SO2_jp==null?"暂无":allData.SO2_jp)
                        $("#NOx_jp").html(allData.NOx_jp==null?"暂无":allData.NOx_jp)
                        $("#VOC_jp").html(allData.VOC_jp==null?"暂无":allData.VOC_jp)
                        $("#PM25").html(allData.PM25);
                        $("#SO4").html(allData.SO4);
                        $("#NH4").html(allData.NH4);
                        $("#NO3").html(allData.NO3)
                        $("#CO").html(allData.CO);
                        $("#NO2").html(allData.NO2)
                        $("#SO2").html(allData.SO2)
                        $("#O3_8_MAX").html(allData.O3_8_MAX)
                        $("#PM10").html(allData.PM10)
                    } else {
                        swal(res.msg, '', 'error');
                        $("#PM25_jp").html("暂无")
                        $("#SO2_jp").html("暂无")
                        $("#NOx_jp").html("暂无")
                        $("#VOC_jp").html("暂无")
                        $("#PM25").html("暂无");
                        $("#SO4").html("暂无");
                        $("#NH4").html("暂无");
                        $("#NO3").html("暂无")
                        $("#CO").html("暂无");
                        $("#NO2").html("暂无")
                        $("#SO2").html("暂无")
                        $("#O3_8_MAX").html("暂无")
                        $("#PM10").html("暂无")
                    }
    })
}

function showTitleFun() {
    $('#showTitle span').empty();
    $('#showTitle span').css({"margin-right":"0px"});
    $('#showTitle .qjName').html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;情景：</span>"+changeMsg.qjName).css({"margin-right":"40px"});
    var timeTwoFor=moment(changeMsg.sTimeD+"-"+changeMsg.eTime,"YYYYMMDD-YYYYMMDD").format("YYYY年MM月DD日-YYYY年MM月DD日");
    $('#showTitle .dateStartName').html("<span class='titleTab'><i class='br-calendar'></i>"+"&nbsp;开始时间：</span>"+changeMsg.sTimeD+"至"+changeMsg.eTime).css({"margin-right":"40px"});
    if (zhiCity.indexOf(changeMsg.pro) == -1) {
        if (changeMsg.station == 'avg') {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.proName);
            $('#showTitle .cityName').html(changeMsg.cityName).css({"margin-right":"40px"});
        } else {
            $('#showTitle .proName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.proName);
            $('#showTitle .cityName').html(changeMsg.cityName);
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            }
    } else {
        if (changeMsg.station == 'avg') {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.cityName).css({"margin-right":"40px"});
        } else {
            $('#showTitle .cityName').html("<span class='titleTab'><i class='im-office'></i>"+"&nbsp;城市：</span>"+changeMsg.cityName);
            $('#showTitle .stationName').html(changeMsg.stationName).css({"margin-right":"40px"});
            }
    }
}
function shownPgbg(){
    $('.pgbgContent span').empty();
    $(".pgbgContent #stationName").empty();
    $(".pgbgContent #pgbaStart").html(moment(changeMsg.sTimeD,"YYYYMMDD").format("YYYY年MM月DD日"));
    $(".pgbgContent #pgbgEnd").html(moment(changeMsg.eTime,"YYYYMMDD").format("YYYY年MM月DD日"));
    $(".pgbgContent #pgbgQjName").html(changeMsg.qjName);

    if (zhiCity.indexOf(changeMsg.pro) == -1) {
            $('.pgbgContent #proName').html(changeMsg.proName);
            $('.pgbgContent #cityName').html(changeMsg.cityName);
            $('.pgbgContent #stationName').html(changeMsg.stationName);
    } else {
            $('.pgbgContent #cityName').html(changeMsg.cityName);
            $('.pgbgContent #stationName').html(changeMsg.stationName);
    }
}
//easyui 添加
$(".toolAll").hide();
$(".upDownBtn").append("<i class='en-arrow-up7'></i>");
var headerH = $(".cloudui .searchT").height();
$(".verticalChar").css({"top": headerH + "px"});
$(".upDownBtn").click(function(){
    if($(".upDownBtn").text()=="收起"){
        $(".upDownBtn").text("更多搜索条件");
        $(".toolAll").hide();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-down8'></i>")
        $(".upDownBtn i").attr("class","en-arrow-down8");
        headerH=$(".cloudui .searchT").height();
        $(".verticalChar").css({"top":headerH+"px"})
    }else{
        $(".upDownBtn").text("收起");
        $(".toolAll").show();
        $(".upDownBtn i").remove();
        $(".upDownBtn").append("<i class='en-arrow-up7'></i>");
        headerH=$(".cloudui .searchT").height();
        $(".verticalChar").css({"top":headerH+"px"})
    }
})
$(".cloudui .verticalCon .ibox-content .searchT .upDown").hover(function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #0275D8"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #0275D8"});
},function(){
    $(".cloudui .verticalCon .ibox-content .searchT .upDown").css({"border-top":"1px solid #d9d9d9"});
    $(".cloudui .verticalCon .ibox-content .searchT .upDown .upDownBtn").css({"border":"1px solid #d9d9d9"});
})

$("#close_scene").click(function () {
    $("#Initialization").window("close")
})

//报告展示
var pWidth=$(".cloudui .verticalCon .verticalChar .pgbgContent").width();
var pHeight=$(".cloudui .verticalCon .verticalChar .pgbgContent").height();
var cWidth=$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").width();
var cHeight=$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").height();
$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").css({"left":"50%","margin-left":(-cWidth/2)+"px"})
$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").css({"top":"40%","margin-top":(-cHeight/2)+"px"})
window.addEventListener("resize", function () {
    setTimeout(function () {
        pWidth=$(".cloudui .verticalCon .verticalChar .pgbgContent").width();
        pHeight=$(".cloudui .verticalCon .verticalChar .pgbgContent").height();
        cWidth=$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").width();
        cHeight=$(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").height();
        $(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").css({"left":"50%","margin-left":(-cWidth/2)+"px"});
        $(".cloudui .verticalCon .verticalChar .pgbgContent .pgbgCon").css({"top":"50%","margin-top":(-cHeight/2)+"px"});
        console.log(pWidth);
        console.log(pHeight)
    },50)}
    )




