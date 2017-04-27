$(function () {
  /**
   *设置导航条信息
   */
  $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span>>><span style="padding-left: 15px;padding-right: 15px;">垂直廓线</span>');
});
/*存储全局改变量*/
var dps_codeStation,dps_station,dps_Date;
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
  grid:{
    x:50
  },
  title: {
    bottom: 10,
    left: 'center',
    text: '模拟数据'  //可变，存储每个污染物
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
    nameGap: 0	,
    boundaryGap: ['0%', '8%'],
  },
  yAxis: {
    //name: 'm',
    nameGap: 5,
    type: 'value',
    axisLabel : {
      formatter: '{value}m'
    },
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
  setTime();
  $.when(dps_Date,dps_station,dps_codeStation).then(function () {
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
function setTime() {
  var url = '/Air/get_time';
  dps_Date = ajaxPost(url,{
    userId:userId
  }).success(function(res){

    if(res.status == 0){
      /*这里要初始化时间*/

      changeMsg.minDate = res.data.mintime;
      changeMsg.maxDate = res.data.maxtime;

      if(!(moment(res.data.maxtime).add(-7,'d').isBefore(moment(res.data.mintime)))){
        changeMsg.startD = moment(res.data.maxtime).add(-7,'d').format('YYYY-MM-DD')
      }else{
        changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
      }
      changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
      changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');


      /*测试使用*/
      changeMsg.minDate = '2017-04-14';
      changeMsg.maxDate = '2017-04-16';
      changeMsg.startD = '2017-04-14';
      changeMsg.endD = '2017-04-16';
      changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');
      /*测试使用 end*/
      initCZDate(changeMsg.minDate,changeMsg.maxDate,changeMsg.startD,changeMsg.endD);
    }

  })
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

    //option.legend.data = ['模拟数据'];
    if (species[i] != 'CO') {
      //option.xAxis.name = 'μg/m³';
      option.title.text = species[i]+'（μg/m³）';
    } else {
      //option.xAxis.name = 'mg/m³';
      option.title.text = species[i]+'（mg/m³）';
    }
    option.series = [];



      option.series.push({
        name: '模拟数据', //可变，存储情景名称
        type: 'line',
        smooth: true,
        symbolSize: 5,
        data: data[speciesObj[species[i]]].slice(0, $('#height').val())  //可变，存储情景数据
      })
    es.setOption(option);
  }
}

/**
 * 初始化时间范围
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initCZDate(s, e, start, end) {
  $('#CZDate').daterangepicker({
    "parentEl": ".toolAll",
    singleDatePicker: true,  //显示单个日历
    timePicker: changeMsg.rms == 'day'?false:true,  //允许选择时间
    timePicker24Hour: true, //时间24小时制
    minDate: s,//最早可选日期
    maxDate: e,//最大可选日期
    locale: {
      format: changeMsg.rms == 'day'?"YYYY-MM-DD":"YYYY-MM-DD HH",
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
    "opens": "right"
  }, function (start, end, label) {

    changeMsg.time = start.format('YYYY-MM-DD HH');
    updata(true);
  })
  var d = $('#CZDate').data('daterangepicker');
  d.element.off();
}

/*按钮打开日期*/
function showDate(type) {
  var d = $('#CZDate').data('daterangepicker');
  if(!d){
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


$('input[name=rms]').on('change', function (e) { //时间分辨率选择
  var rms = $(e.target).val();
  changeMsg.rms = rms;
  console.log(rms);
  $('#species').empty();
  for (var i = 0; i < speciesArr[rms].length; i++) {
    $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
  }

  initCZDate(changeMsg.minDate,changeMsg.maxDate,changeMsg.startD,changeMsg.endD);

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
    var url = '/Air/find_vertical';
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





