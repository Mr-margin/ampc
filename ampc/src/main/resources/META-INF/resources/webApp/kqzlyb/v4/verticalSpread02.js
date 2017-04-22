$(function () {
  /**
   *设置导航条信息
   */
  $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">效果评估</span>>><span style="padding-left: 15px;padding-right: 15px;">垂直廓线</span><a onclick="exchangeModal()" style="padding-left: 15px;padding-right: 15px;float:right;">切换情景范围</a>');
  //全选复选框
  //initTableCheckbox();


});
/*存储全局改变量*/
var dps_codeStation,dps_station;
var ooo = [['-',0],['-',50],['-',100],['-',200],['-',300],['-',400],['-',500],['-',700],['-',1000],['-',1500],['-',2000],['-',3000]]
var changeMsg = {
  pro: '',//站点选择
  city: '',
  station: '',
  height: 9,//高度选择
  rms: 'day',//时间分辨率
  time: '',//时间选择
  scenarinoId: [],//选择的情景Id数组
  scenarinoName: [],//选择的情景名称数组
};
$('.day').css('display','block');
$('.hour').css('display','none');
var speciesArr = {
  day: ['PM₂₅', 'PM₁₀', 'O₃_8_MAX', 'O₃_1_MAX', 'O₃_AVG', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE'],
  hour: ['PM₂₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE']
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
/*echarts 配置*/
var optionAll = {
  title: {
    bottom: 10,
    left: "40%",
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
    name: '/μg/m³',  //可变，CO为mg/m³其他的为'μg/m³
    nameGap: 0	,
    boundaryGap: ['0%', '8%'],
  },
  yAxis: {
    name: 'm',
    nameGap: 5,
    type: 'value',
//    interval: 100,
    axisLine: {onZero: false},
  },
  color:["#c12e34","#e6b600", "#0098d9", "#2b821d","#005eaa","#339ca8","#cda819","#32a487"],
  series: [  //可变，存储所有情景数据

  ],
  animation:false
};

  initialize();


/*初始化页面数据,在缓存中有数据的情况下*/
function initialize() {

  setStation();
  setTime(sceneInitialization.s, sceneInitialization.e);
  $.when(dps_codeStation,dps_station).then(function () {
    updata();
  });

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
    //missionId: id
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
      changeMsg.city = $('#cityStation').val();
      findStation(changeMsg.city);
      //changeMsg.station = $('#station').val();
    } else {
      console.log('站点请求故障！！！')
    }
  })
}
/*查询站点*/
function findStation(code){
  dps_station = ajaxPost('/Site/find_Site',{
    userId:userId,
    siteCode:code
  }).success(function(res){
    $('#station').empty();
    $('#station').append($('<option value="avg">平均</option>'));
    for(var i=0;i<res.data.length;i++){
      $('#station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
    }
    changeMsg.station = $('#station').val();
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
  if(changeMsg.rms == 'day'){
    $('.hour').css('display','none');
    $('.day').css('display','block');
  }else{
    $('.day').css('display','none');
    $('.hour').css('display','block');
  }
  var data = czData;
  var species = speciesArr[changeMsg.rms];
  for (var i = 0; i < species.length; i++) {
    echarts.dispose(document.getElementById(species[i]));
    var es = echarts.init(document.getElementById(species[i]));
    var option = $.extend(true, {}, optionAll);
    option.title.text = species[i];
    option.legend.data = (function () {
      var arr = [];
      for (var a = 0; a < sceneInitialization.data.length; a++) {
        arr.push(sceneInitialization.data[a].scenarinoName)
      };


      arr.unshift('基准');
      return arr;
    })();
    if (species[i] != 'CO') {
      option.xAxis.name = 'μg/m³';
    } else {
      option.xAxis.name = 'mg/m³';
    }
    option.series = [];


    for (var sp = 0; sp < sceneInitialization.data.length; sp++) {
      var id = sceneInitialization.data[sp].scenarinoId;
      var name = sceneInitialization.data[sp].scenarinoName;


      option.series.push({
        name: name, //可变，存储情景名称
        type: 'line',
        smooth: true,
        symbolSize: 5,
        data: data[id][speciesObj[species[i]]].slice(0, $('#height').val())  //可变，存储情景数据
      })
    }
    option.series.unshift({
      name: '基准', //可变，存储情景名称
      type: 'line',
      smooth: true,
      symbolSize: 5,
      data: data['-1'][speciesObj[species[i]]].slice(0, $('#height').val())  //可变，存储情景数据
    });
    es.setOption(option);
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

  updata();
});

/*站点改变事件*/
$('#proStation').on('change', function (e) {
  var pro = $(e.target).val();
  changeMsg.pro = pro;
  $('#cityStation').empty();
  var cityStation = allCode[pro].city;
  for (var city in cityStation) {
    $('#cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
  }
  changeMsg.city = $('#cityStation').val();
  findStation(changeMsg.city);

  updata();
});

$('#cityStation').on('change', function (e) {
  var city = $(e.target).val();
  changeMsg.city = city;
  findStation(changeMsg.city);

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
  initEcharts();
  //updata();
});

var czData;
/*设置echarts图表*/
function updata() {
  $.when(dps_station).then(function () {
    var url = '/Appraisal/find_vertical';
    ajaxPost(url, {
      userId: userId,
      mode:changeMsg.station=='avg'?'city':'point',
      time:changeMsg.time,
      cityStation:changeMsg.station=='avg'?changeMsg.city.substr(0,4):changeMsg.station,
      datetype:changeMsg.rms
    }).success(function (res) {
      if ((res.status == 0)) {
          if(!res.data){
            res.data = {};
            for(var y in speciesObj){
              res.data[speciesObj[y]] = ooo;
            }
          }else{
            for(var y in speciesObj){
              if((!res.data[speciesObj[y]])||(res.data[speciesObj[y]].length == 0)){
                res.data[speciesObj[y]] = ooo;
              }
            }
          }
        var obj = {};
        $.extend(obj , res.data);
        czData =  obj;
        initEcharts();
      } else {
        console.log(res.msg)
      }
    }, function () {
      console.log('接口故障！！！')
    })
  });
}






