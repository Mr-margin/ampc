$(function () {
  /**
   *设置导航条信息
   */
  $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
  //全选复选框
  //initTableCheckbox();


});
/*存储全局改变量*/
var dps_station;
var changeMsg = {
  pro: '',//站点选择
  city: '',
  station: '',
  height:9,//高度选择
  rms: 'day',//时间分辨率
  time: '',//时间选择
  scenarinoId : [],//选择的情景Id数组
  scenarinoName : [],//选择的情景名称数组
};
var speciesArr = {
  day: ['PM25', 'PM10', 'O3_8_max', 'O3_1_max', 'O3_avg', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE'],
  hour: ['PM25', 'PM10', 'O3', 'SO2', 'NO2', 'CO', 'SO4', 'NO3', 'NH4', 'BC', 'OM', 'PMFINE']
};
/*echarts 配置*/
var optionAll = {
  title: {
	bottom:10,
	left:"40%",
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
      for(var i=0;i<params.length;i++){
        if(i == params.length-1){
          hm = hm+'<i style="width:10px;height:10px;display:inline-block;border-radius:100%;background-color: ' + params[i].color + ';"></i> ' + params[i].seriesName + ' : ' + params[i].data[0]
        }else{
          hm = hm+'<i style="width:10px;height:10px;display:inline-block;border-radius:100%;background-color: ' + params[i].color + ';"></i> ' + params[i].seriesName + ' : ' + params[i].data[0] + '<br />'
        }
      }

      return hm;
    }
  },
  xAxis: {
    type: 'value',
    name: '/μg/m³',  //可变，CO为mg/m³其他的为'μg/m³
    nameGap: -3,
    boundaryGap: ['0%', '8%'],
  },
  yAxis: {
    name: '/m',
    nameGap:5,
    type: 'value',
    interval: 500,
    axisLine: {onZero: false},
  },
  series: [  //可变，存储所有情景数据

  ]
}

var ls = window.sessionStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;
if (!qjMsg) {
  qjMsg = JSON.parse(ls.getItem('yaMsg'));
} else {
  ls.setItem('yaMsg', JSON.stringify(qjMsg));
}
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

  changeMsg.scenarinoId = [];
  changeMsg.scenarinoName = [];
  for(var i=0;i<sceneInitialization.data.length;i++){
    changeMsg.scenarinoId.push(sceneInitialization.data.scenarinoId);
    changeMsg.scenarinoName.push(sceneInitialization.data.scenarinoName);
  }

  setStation(sceneInitialization.taskID);
  setTime(sceneInitialization.s, sceneInitialization.e);
  $.when(dps_station).then(function(){
    updata();
  });
  //initEcharts();

}

var allStation = {};//用于存储所有的站点信息
/*设置站点*/
function setStation(id) {
  $('#proStation').empty();
  $('#cityStation').empty();
  $('#station').empty();
  var url = '';
  dps_station = ajaxPost(url, {
    userId: userId,
    missionId: id
  }).success(function (res) {
    if(res.status == 0){
      allStation = res.data;
      for (var pro in allStation) {
        $('#proStation').append($('<option value="' + allStation[pro].code + '">' + allStation[pro].name + '</option>'))
      }
      var cityStation = allStation[$('#proStation').val()].station;
      for (var city in cityStation) {
        $('#cityStation').append($('<option value="' + cityStation[city].code + '">' + cityStation[city].name + '</option>'))
      }
      var station = cityStation[$('#cityStation').val()].station;
      for (var s in station) {
        $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
      }

      changeMsg.pro = $('#proStation').val();
      changeMsg.city = $('#cityStation').val();
      changeMsg.station = $('#station').val();
    }else{
      console.log('站点请求故障！！！')
    }
  })
}

/*设置时间*/
/*只限输入毫秒数*/
function setTime(s, e) {
  s = moment(s - 0);
  e = moment(e - 0);
  $('#sTime-d').empty();
  while(true) {
    $('#sTime-d').append($('<option>' + s.format('YYYY-MM-DD') + '</option>'));
    if(e.format('YYYY-MM-DD')=='Invalid date'){
      return;
    }
    if (e.isBefore(s.add(1, 'd'))) {
      return;
    }
  }
  changeMsg.time = $('#sTime-d').val()+' 00'
}


function initEcharts() {
  var data = czData;
  //var data = {
  //  '191': {
  //    CO: [[12, 0], [20, 50], [25, 100],[15,200],[43,300]],
  //    CO2: [[12, 0], [20, 50], [25, 100]]
  //  },
  //  '192': {
  //    CO: [[12, 0], [20, 50], [25, 100]],
  //    CO2: [[12, 0], [20, 50], [25, 100]]
  //  }
  //};
  $('#initEcharts').empty();
  var species = speciesArr[changeMsg.rms];
  for (var i = 0; i < species.length; i++) {
    var div = $('<div></div>');
    div.attr('id', species[i]);
    div.addClass('echartsCZ');
    div.addClass('col-md-3');
    $('#initEcharts').append(div);
    var option = $.extend(true, {}, optionAll);
    option.title.text = species[i];
    option.legend.data = (function () {
      var arr = [];
      for (var a = 0; a < sceneInitialization.data.length; a++) {
        arr.push(sceneInitialization.data[a].scenarinoName)
      }
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
      option.xAxis.name = '/μg/m³';
    } else {
      option.xAxis.name = '/mg/m³';
    }
    option.series = [];
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
        data: data[id][species[i]].slice(0, $('#height').val())  //可变，存储情景数据
        //data: data['191']['CO']  //可变，存储情景数据
      })
    }
    var es = echarts.init(document.getElementById(species[i]));
    es.setOption(option);
  }
}

//超链接显示 模态框
function exchangeModal() {
  sceneInittion();
  $("#Initialization").modal();
}
var allMission = {};
/**
 * 初始化模态框显示
 */
function sceneInittion() {
  $("#task").html("");
  var paramsName = {};
  paramsName.userId = userId;
  console.log(JSON.stringify(paramsName));
  ajaxPost('/mission/find_All_mission', paramsName).success(function (res) {
    console.log(JSON.stringify(res));
    if (res.status == 0) {
      var task = "";
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
      $("#Initialization").modal();//初始化模态框显示
      sceneTable();
    }
  });
}
/**
 * 根据任务ID，获取情景列表用于选择情景范围
 */
function sceneTable() {
  $("#sceneTableId").bootstrapTable('destroy');//销毁现有表格数据

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
        if (res.data.rows.length > 0) {

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

          return res.data.rows;
        }
      } else if (res.status == 1000) {
        swal(res.msg, '', 'error');
      }
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
  });
}
/**
 * 保存选择的情景
 */
function save_scene() {
  var row = $('#sceneTableId').bootstrapTable('getSelections');//获取所有选中的情景数据
  if (row.length > 0) {
    var mag = {};
    mag.id = "sceneInitialization";
    mag.taskID = $("#task").val();
    mag.s = allMission[mag.taskID].missionStartDate;
    mag.e = allMission[mag.taskID].missionEndDate;
    var data = [];
    $.each(row, function (i, col) {
      data.push({
        "scenarinoId": col.scenarinoId,
        "scenarinoName": col.scenarinoName,
        "scenarinoStartDate": col.scenarinoStartDate,
        "scenarinoEndDate": col.scenarinoEndDate
      });
    });
    mag.data = data;
    vipspa.setMessage(mag);
    ls.setItem('SI', JSON.stringify(mag));
    console.log(data);
    sceneInitialization = jQuery.extend(true, {}, mag);//复制数据
    $("#close_scene").click();
    /*数据准备完毕，进行初始化页面*/
    initialize();
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
  if (rms == 'day') {
    $('#sTime-h').addClass('disNone');
    $('#eTimeP').addClass('disNone');
  } else if (rms == 'hour') {
    $('#sTime-h').removeClass('disNone');
    $('#eTimeP').addClass('disNone');
  }
});

/*站点改变事件*/
$('#proStation').on('change', function (e) {
  var pro = $(e.target).val();
  changeMsg.pro = pro;
  var cityStation = allStation[pro].station;
  for (var city in cityStation) {
    $('#cityStation').append($('<option value="' + cityStation[city].code + '">' + cityStation[city].name + '</option>'))
  }
  changeMsg.city = $('#cityStation').val();
  var station = cityStation[changeMsg.city].station;
  for(var s in station){
    $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
  }
  changeMsg.station = $('#station').val();

  updata();
});

$('#cityStation').on('change', function (e) {
  var city = $(e.target).val();
  changeMsg.city = city;
  var station = allStation[changeMsg.pro].station[city].station;
  for (var s in station) {
    $('#station').append($('<option value="' + station[s].code + '">' + station[s].name + '</option>'))
  }
  changeMsg.station = $('#station').val();

  updata();
});

$('#station').on('change', function (e) {
  var station = $(e.target).val();
  changeMsg.station = station;

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
  updata();
});

var czData;
/*设置echarts图表*/
function updata() {
  var url = '';
  var echartsData = ajaxPost(url,{
    userId:userId,
  });

  $.when(echartsData).then(function(res){
    if(res.status == 0){
      czData = res.data;
      initEcharts();
    }else{
      console.log(res.msg)
    }
  },function(){
    console.log('接口故障！！！')
  })
}






