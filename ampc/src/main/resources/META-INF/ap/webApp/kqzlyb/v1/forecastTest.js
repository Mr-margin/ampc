/**
 * Created by lvcheng on 2017/4/21.
 */

$(function () {
  /**
   *设置导航条信息
   */
  $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span>>><span style="padding-left: 15px;padding-right: 15px;">预报检验</span>');

  initialize();
});

/*定义全局变量*/
var dps_Date,dps_City;
var dps_Station = {};
var allCode;
var changeMsg = {
  type:'wrw',
  startD:'',
  endD:'',
  rms:'day',
  pro:'',
  city:''
};
var speciesAll = {
  wrw:{
    day:['PM₂₅', 'PM₁₀', 'O₃_8_MAX','SO₂', 'NO₂', 'CO'],
    hour:['PM₂₅', 'PM₁₀', 'O₃','SO₂', 'NO₂', 'CO']
  },
  qxys:{
    day:[],
    hour:[]
  }
}
var speciesObj = {
  'PM₂₅':'PM25',
  'PM₁₀':'PM10',
  'O₃_8_MAX':'O3_8_MAX',
  'O₃_1_MAX':'O3_1_MAX',
  'O₃_AVG':'O3_AVG',
  'SO₂':'SO2',
  'NO₂':'NO2',
  'CO':'CO',
  'O₃':'O3'
};


/*初始化函数*/
function initialize(){
  dps_City = requestRegion();
  dps_Date = requestDate();

  $.when(dps_Date,dps_City).then(function(){
//    console.log('initialize  updata');
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
    "opens": "right"
  }, function (start, end, label) {
    changeMsg.startD = start.format('YYYY-MM-DD');
    changeMsg.endD = end.format('YYYY-MM-DD');
    updata(true);
  })
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
    "opens": "right"
  }, function (start, end, label) {
    changeMsg.startD = start.format('YYYY-MM-DD');
    changeMsg.endD = end.format('YYYY-MM-DD');
    updata(true);
  })
  var d = $('#qxysDate').data('daterangepicker');
  d.element.off();
}

/*按钮打开日期*/
function showRwDate(type) {
  var d = $('#'+type+'Date').data('daterangepicker');
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

/*设置污染物/气象要素站点
 * @parameter regionId 城市regionId
 * @parameter type 类型
 * */
function setStation(regionId,type){
  var url = '/Site/find_Site';
  return ajaxPost(url,{
    userId:userId,
    siteCode:regionId.substr(0,4)
  });
}

/*请求省市区code*/
function requestRegion(){
  var url = '/Site/find_codes';
  return ajaxPost(url,{
    userId:userId
  }).success(function(res){
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
      //changeMsg.station = $('#station').val();
      if(!dps_Station[changeMsg.city+changeMsg.type]){
        dps_Station[changeMsg.city+changeMsg.type] = setStation(changeMsg.city,changeMsg.type);
      }
    } else {
      console.log('站点请求故障！！！')
    }

  })
}

/*请求可选日期范围*/
function requestDate(){
  var url = '/Air/get_time';
  return ajaxPost(url,{
    userId:userId
  }).success(function(res){

    if(res.status == 0){
      /*这里要初始化时间*/

      if(!(moment(res.data.maxtime).add(-7,'d').isBefore(moment(res.data.mintime)))){
        changeMsg.startD = moment(res.data.maxtime).add(-7,'d').format('YYYY-MM-DD')
      }else{
        changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
      }

      changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
      initWrwDate(moment(res.data.mintime).format('YYYY-MM-DD'),moment(res.data.maxtime).format('YYYY-MM-DD'),changeMsg.startD,changeMsg.endD);
      initQxysDate(moment(res.data.mintime).format('YYYY-MM-DD'),moment(res.data.maxtime).format('YYYY-MM-DD'),changeMsg.startD,changeMsg.endD);
    }

  })
}

/*更新数据*/
function updata(opt){
  $.when(dps_Station[changeMsg.city+changeMsg.type]).done(function(res){
    if(!opt){
      res = dps_Station[changeMsg.city+changeMsg.type].responseJSON;
      $('#'+changeMsg.type+' .station').empty();
      $('#'+changeMsg.type+' .station').append($('<option value="avg">平均</option>'));
      for(var i=0;i<res.data.length;i++){
        $('#'+changeMsg.type+' .station').append($('<option value="' + res.data[i].stationId + '">' + res.data[i].stationName + '</option>'))
      }
      changeMsg.station = $('#'+changeMsg.type+' .station').val();
    }

    $('.showTable').empty()
    for(var i=0;i<speciesAll[changeMsg.type][changeMsg.rms].length;i++){
      var div = $('.mnDataShow.disNone').clone();
      div.removeClass('disNone');
      div.find('.mnName').html(speciesAll[changeMsg.type][changeMsg.rms][i]+'模拟数据');
      div.find('.12').html('12'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.13').html('13'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.14').html('14'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.22').html('22'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.23').html('23'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.24').html('24'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.32').html('32'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.33').html('33'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.34').html('34'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.42').html('42'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.43').html('43'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);
      div.find('.44').html('44'+speciesAll[changeMsg.type][changeMsg.rms][i]+changeMsg.station);

      $('.showTable').append(div);
    }

    var url = '';
    ajaxPost(url,{
      userId:userId
    }).success(function(res){

      for(var i=0;i<speciesAll[changeMsg.type][changeMsg.rms].length;i++){
        var div = $('.mnDataShow.disNone').clone();
        div.removeClass('disNone');
        div.find('.12').html('12'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.13').html('13'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.14').html('14'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.22').html('22'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.23').html('23'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.24').html('24'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.32').html('32'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.33').html('33'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.34').html('34'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.42').html('42'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.43').html('43'+speciesAll[changeMsg.type][changeMsg.rms][i]);
        div.find('.44').html('44'+speciesAll[changeMsg.type][changeMsg.rms][i]);

        $('.showTable').append(div);
      }




    })
  })
}


/*change 改变事件*/
$('.proStation').on('change',function(e){
  var pro = $(e.target).val();
  $('.proStation').val(pro);
  changeMsg.pro = pro;
  $('.cityStation').empty();
  var cityStation = allCode[pro].city;
  for (var city in cityStation) {
    $('.cityStation').append($('<option value="' + city + '">' + cityStation[city] + '</option>'))
  }
  changeMsg.city = $('.cityStation').val();
  if(!dps_Station[changeMsg.city+changeMsg.type]){
    dps_Station[changeMsg.city+changeMsg.type] = setStation(changeMsg.city,changeMsg.type);
  }
  updata();
});


$('.cityStation').on('change',function(e){
  var city = $(e.target).val();
  $('.cityStation').val(city);
  changeMsg.city = city;
  if(!dps_Station[changeMsg.city+changeMsg.type]){
    dps_Station[changeMsg.city+changeMsg.type] = setStation(changeMsg.city,changeMsg.type);
  }
  updata();
});

$('.station').on('change',function(e){
  var station = $(e.target).val();
  changeMsg.station = station;
  updata(true);
});


var stint = true;
$('input[name=rmsWrw]').on('change', function (e) { //时间分辨率选择
  var rms = $(e.target).val();
  if(stint){
    stint = false;
    $('#qxys input[value='+rms+']').parent().click();
    changeRms(rms)
  }
});

$('input[name=rmsQxys]').on('change', function (e) { //时间分辨率选择
  var rms = $(e.target).val();
  if(stint){
    stint = false;
    $('#wrw input[value='+rms+']').parent().click();
    changeRms(rms)
  }
});

function changeRms(rms){
  stint = true;
  changeMsg.rms = rms;
//  console.log(rms);
  updata(true);
}

function changeType(type){

  if(changeMsg.type != type){
    changeMsg.type = type;
    if(!dps_Station[changeMsg.city+changeMsg.type]){
      dps_Station[changeMsg.city+changeMsg.type] = setStation(changeMsg.city,changeMsg.type);
    }
    updata();
  }

}
