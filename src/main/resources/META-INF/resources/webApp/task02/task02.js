/**
 * Created by lvcheng on 2017/2/21.
 */

/*全局变量*/
var totalWidth,totalDate,startDate,qjMsg;
var index,indexPar,handle,minLeft,maxLeft,selfLeft,startX,leftWidth,rightWidth;
var allData = [];
var addTimeIndex;

/*test使用*/
var parameterPar = {total: '', data: {}};
function ajaxPost1(url, parameter) {
 parameterPar.data = parameter;
 var p = JSON.stringify(parameterPar);
 //return $.ajax(BackstageIP + url, {
 return $.ajax( url, {
 contentType: "application/json",
 type: "GET",
 async: true,
 dataType: 'JSON',
 data: p
 })
 }

initialize();


function initialize(){
  qjMsg = vipspa.getMessage('qjMessage').content;
  qjMsg = {
    qjEndDate: 1486137600000,
    qjId: 19,
    qjName: "dfgerg",
    qjStartDate: 1485792000000,
    rwId: 65,
    rwName: "gjfhdxghxdf"
  };

  /*总时长*/
  totalDate = qjMsg.qjEndDate - qjMsg.qjStartDate;
  startDate = qjMsg.qjStartDate;

  var url = '/area/get_areaAndTimeList';
  var scenarino = ajaxPost(url,{
    scenarinoId:qjMsg.qjId,
    userId:userId
  });

  initDate();
  /*test使用*/
  var scenarino1 = ajaxPost1('webApp/task02/qy.json',{});

  scenarino1.then(function(res){
    allData = res.data;
    for(var i=0;i<res.data.length;i++){
      allData[i].timeFrame = [];
      /*克隆区域进行添加*/
      var area = $('.area.disNone').clone().removeClass('disNone');
      area.find('.front>span').html(res.data[i].areaName);
      var timeItems = res.data[i].timeItems;
      $('.areaMsg').append(area);
      for(var item=0;item<timeItems.length;item++){
        var totalWidth = $('.period').width();

        /*克隆时段进行添加*/
        var times = $('.time.disNone').clone().removeClass('disNone');
        area.find('.showLine').before(times);
        times.find('h4').html(timeItems[item].planName);
        if(item>0){
          var sD = timeItems[item].timeStartDate ;
          allData[i].timeFrame[item-1] = moment(sD).format('YYYY/MM/DD HH');

          /*克隆滑块进行添加*/
          var hk = $('.hk.disNone').clone().removeClass('disNone');
          hk.find('.showTips').html(moment(sD).format('YYYY/MM/DD HH'));
          var left = ((sD - startDate)/totalDate);
          area.find('.showLine').append(hk);
          hk.css('left',left*100+'%');
        }
        var tw;

        tw = ((timeItems[item].timeEndDate - timeItems[item].timeStartDate)/totalDate)*totalWidth - 1;
        times.css('width',(tw/totalWidth)*100 + '%');

      }

    }
    console.log(allData)
  })

}

function sub(){
  data.push($('input[type=text]').val());

}

$('.areaMsg').on('mouseenter','.hk',function(e){
//    index = $('.hk').index(this);
//    startX = e.pageX;
  $(this).find('.showTips').css('display','block');
});
$('.areaMsg').on('mouseleave','.hk',function(e){
//    index = $('.hk').index(this);
  $(this).find('.showTips').css('display','none');
});

$('.areaMsg').on('mousedown','.hk',function(e){
  var widthP = $('.period').width();
  indexPar = $('.area').index($(this).parents('.area'));
  index = $('.area').eq(indexPar).find('.hk').index(this);
  leftWidth = $('.area').eq(indexPar).children('.period').children('.time').eq(index).width();
  rightWidth = $('.area').eq(indexPar).children('.period').children('.time').eq(index+1).width();
  handle = true;
  startX = e.pageX;
  $(this).find('.showTips').css('display','block');
  selfLeft = parseInt($(this).css('left'));
  minLeft = parseInt($('.area').eq(indexPar).find('.hk').eq(index-1).css('left'));
  maxLeft = parseInt($('.area').eq(indexPar).find('.hk').eq(index+1).css('left'));
  if(selfLeft > (widthP/2)){
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsLeft').addClass('showTipsRight');
  }else{
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsRight').addClass('showTipsLeft');
  }
  console.log(allData)
  //data[index] = selfLeft;
});
$('.areaMsg').on('mouseup','.area',function(e){
  //index = $('.hk').index(this);
  if(!handle)return;
  allData[indexPar].timeFrame[index] = allData[indexPar].timeItems[index].timeEndDate;

  ajaxPost('/time/update_time',{
    userId:userId,
    updateDate:allData[indexPar].timeItems[index].timeEndDate,
    beforeTimeId:allData[indexPar].timeItems[index].timeId,
    afterTimeId:allData[indexPar].timeItems[index+1].timeId
  }).success(function(res){
    console.log(res)
  })

  console.log(allData);
  handle = false;
  $(this).find('.showTips').css('display','none');
});

$('.areaMsg').on('mousemove','.period',function(e){
  e.stopPropagation();
  if(handle){
    var widthP = $(this).width();
    var moveX = e.pageX;
    if(!maxLeft){
      maxLeft = widthP-4;
    }
    var newLeft = moveX - startX + selfLeft;
    if(newLeft>(maxLeft-4))newLeft = maxLeft-4;
    if(index == 0){
      if(newLeft<4)newLeft = 4;

    }else{
      if(newLeft<(minLeft+4))newLeft = minLeft+4;
    }
    //data[index] = newLeft;
    $('.area').eq(indexPar).find('.hk').eq(index).css('left',(newLeft/widthP)*100+'%');

    if(newLeft > (widthP/2)){
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsLeft').addClass('showTipsRight');
    }else{
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsRight').addClass('showTipsLeft');
    }

    $(this).children('.time').eq(index).css('width',((newLeft - selfLeft+leftWidth)/widthP)*100+'%');
    $(this).children('.time').eq(index+1).css('width',((+rightWidth - (newLeft - selfLeft))/widthP)*100+'%');

    var showDate = moment((newLeft/widthP*totalDate)+startDate).format('YYYY/MM/DD HH');
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').html(showDate);

    allData[indexPar].timeItems[index].timeEndDate = showDate;
    allData[indexPar].timeItems[index+1].timeStartDate = showDate;
    //console.log(moment((newLeft/widthP*totalDate)+startDate).format('YYYY-MM-DD HH'))
  }
//    console.log(widthP,startX,moveX,selfLeft)
});

/*编辑区域*/
function editArea(e){}

/*删除区域*/
function delArea(e){
  console.log(e);
  var indexPar = $('.area').index($(e).parents('.area'));
  var url = '/area/delete_area';
  var areaIds = [allData[indexPar].areaId.toString()]
  var params = {
    userId:userId,
    areaIds:areaIds
  };
  swal({
      title: "确定要删除?",
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#DD6B55",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      closeOnConfirm: false
    },
    function () {
//          window.setTimeout(function () {
//            swal("删除失败!", "", "error");
//          }, 2000);
      ajaxPost(url, params).success(function (res) {
        if(res.status == 0){
          $('.area').eq(indexPar).remove();
          $('.area').eq(indexPar).remove();
          swal("已删除!", "", "success");
        }else{
          swal("删除失败!", "", "error");
        }
      }).error(function () {
        swal("删除失败!", "", "error");
      })

    });

}

/*添加时间段*/
function addTimes(){
  console.log(123);
  var timePoint = $('#qyTimePoint').val();
  var timeFrame = allData[addTimeIndex].timeFrame;
  timeFrame.push(timePoint);
  timeFrame.sort();
  var index = timeFrame.indexOf(timePoint);

  var url = '/time/save_time';
  ajaxPost(url,{
    missionId:qjMsg.rwId,
    scenarinoId:qjMsg.qjId,
    userId:userId,
    areaId:allData[addTimeIndex].areaId,
    selectTimeId:allData[addTimeIndex].timeItems[index].timeId,
    addTimeDate:timePoint
  }).success(function(res){
    console.log(res);

    allData[addTimeIndex].timeItems.splice(index+1,0,{
      timeStartDate:timePoint,
      timeId:res.data.timeId,
      timeEndDate:allData[addTimeIndex].timeItems[index].timeEndDate
    });
    allData[addTimeIndex].timeItems[index].timeEndDate = timePoint;

    var area = $('.area').eq(addTimeIndex);
    var hk = $('.hk.disNone').clone().removeClass('disNone');

    if(timeFrame.length > 1){

      if(index == 0){
        area.find('.hk').eq(index).before(hk);
      }else{
        area.find('.hk').eq(index-1).after(hk);
      }
    }else{
      area.find('.showLine').append(hk);
    }
    hk.find('.showTips').html(timePoint);
    var left = (moment(momentDate(timePoint)) - moment(momentDate(startDate)))/totalDate*100;
    hk.css('left',left+'%');

    var times = $('.time.disNone').clone().removeClass('disNone');
    var totalWidth = area.find('.period').width();
    var indextime = area.find('.time').eq(index);
    indextime.after(times);

    var beforeL = 0;
    var afterL = totalWidth-20;
    if(index != 0){
      beforeL =  parseInt(area.find('.hk').eq(index-1).css('left'))
    }
    if(index < allData[addTimeIndex].timeFrame.length-1){
      afterL = parseInt(area.find('.hk').eq(index+1).css('left'))
    }

    var timeWidth = left - ((beforeL/totalWidth)*100);
    var newTimeWidth = ((afterL/totalWidth)*100) - left;



    //var timeWidth = (moment(momentDate(allData[addTimeIndex].timeItems[index].timeEndDate))-moment(momentDate(allData[addTimeIndex].timeItems[index].timeStartDate)))/totalDate*100 ;
    indextime.css('width',timeWidth+'%');
    //var newTimeWidth = (moment(momentDate(allData[addTimeIndex].timeItems[index+1].timeEndDate))-moment(momentDate(allData[addTimeIndex].timeItems[index+1].timeStartDate)))/totalDate*100;
    times.css('width',newTimeWidth+'%');

  }).error(function(){
    timeFrame.splice(index,1);
    swal('添加失败！！！','','error')
  });

}

/*返回YYYY-MM-DD HH格式*/
function momentDate(d){
  var n = Number(d);
  if (!isNaN(n)){
    return moment(d).format('YYYY-MM-DD HH')
  }else{
    return moment(d,'YYYY-MM-DD HH').format('YYYY-MM-DD HH')
  }
}

/*删除时间段*/
function delTimes(e){}

/*添加预案*/
function addPlan(e){}

/*删除预案*/
function delPlan(e){}

$('#qyTime').on('show.bs.modal', function (event) {
	//console.log(event);
  var button = $(event.relatedTarget);
  if(button.length == 0)return;
  addTimeIndex = $('.area').index(button.parents('.area'));
  console.log(addTimeIndex)
});


function initDate(){
  $("#qyTimePoint").datetimepicker({
    format: 'yyyy/mm/dd hh',
    todayHighlight:false,
    minView: 'day',
    startView: 'month',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true,
    startDate:moment(qjMsg.qjStartDate).format('YYYY-MM-DD HH'),
    endDate:moment(qjMsg.qjEndDate).format('YYYY-MM-DD HH')
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY/MM/DD HH');
      //$('#rwEndDate').datetimepicker('setStartDate', date);
      //$('#rwStartDate').datetimepicker('setEndDate', null);
    });

}
