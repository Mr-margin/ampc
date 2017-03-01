/**
 * Created by lvcheng on 2017/2/21.
 */

/*全局变量*/
var totalWidth,totalDate,startDate,qjMsg;
var index,indexPar,handle,minLeft,maxLeft,selfLeft,startX,leftWidth,rightWidth;
var allData = [];

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

  /*test使用*/
  var scenarino1 = ajaxPost1('webApp/task02/qy.json',{});

  scenarino1.then(function(res){
    allData = res.data;
    for(var i=0;i<res.data.length;i++){
      allData[i].timeFrame = [];
      var area = $('.area.disNone').clone().removeClass('disNone');
      area.find('.front>span').html(res.data[i].areaName);
      var timeItems = res.data[i].timeItems;
      $('.areaMsg').append(area);
      for(var item=0;item<timeItems.length;item++){
        var totalWidth = $('.period').width();
        var times = $('.time.disNone').clone().removeClass('disNone');
        area.find('.showLine').before(times);
        times.find('h4').html(timeItems[item].planName);
        if(item>0){
          var sD = timeItems[item].timeStartDate ;
          allData[i].timeFrame[item-1] = moment(sD).format('YYYY-MM-DD HH');

          var hk = $('.hk.disNone').clone().removeClass('disNone');
          hk.find('.showTips').html(moment(sD).format('YYYY-MM-DD HH'));
          var left = ((sD - startDate)/totalDate) * totalWidth;
          area.find('.showLine').append(hk);
          hk.css('left',left+'px');
        }

        var tw = ((timeItems[item].timeEndDate - timeItems[item].timeStartDate)/totalDate)*totalWidth - 1;
        times.css('width',tw + 'px');

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
  console.log(allData)
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
    $('.area').eq(indexPar).find('.hk').eq(index).css('left',newLeft+'px');

    if(newLeft > (widthP/2)){
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsLeft').addClass('showTipsRight');
    }else{
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsRight').addClass('showTipsLeft');
    }


    $(this).children('.time').eq(index).css('width',(newLeft - selfLeft+leftWidth)+'px');
    $(this).children('.time').eq(index+1).css('width',(+rightWidth - (newLeft - selfLeft))+'px');

    var showDate = moment((newLeft/widthP*totalDate)+startDate).format('YYYY-MM-DD HH');
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').html(showDate);

    allData[indexPar].timeItems[index].timeEndDate = showDate;
    allData[indexPar].timeItems[index+1].timeStartDate = showDate;
    //console.log(moment((newLeft/widthP*totalDate)+startDate).format('YYYY-MM-DD HH'))
  }
//    console.log(widthP,startX,moveX,selfLeft)
});

/*编辑区域*/
function editArea(){}

/*删除区域*/
function delArea(){}

/*添加时间段*/
function addTimes(){}

/*删除时间段*/
function delTimes(){}

/*添加预案*/
function addPlan(){}

/*删除预案*/
function delPlan(){}

/*添加区域处工具*/
function addAreaTool(){

  var div = '<div class="areaToolDiv text-center">'+
              '<button type="button" onclick="editArea()" class="btn btn-default btn-xs toolShow" title="编辑区域"><i class="ec-pencil"></i><span>编辑区域</span></button>'+
              '<button type="button" onclick="delArea()" class="btn btn-default btn-xs toolShow" title="删除区域"><i class="ec-trashcan"></i><span>删除区域</span></button>'+
              '<button type="button" onclick="addTimes()" class="btn btn-default btn-xs toolShow" title="添加时段"><i class="im-plus"></i><span>添加时段</span></button>'+
            '</div>'

}

/*添加时间段处工具*/
function addTimesTool(){

  var div = '<div class="timeToolDiv text-center">'+
              '<button type="button" onclick="addPlan()" class="btn btn-default btn-xs toolShow" title="添加预案"><i class="im-google-plus"></i><span>添加预案</span></button>'+
              '<button type="button" onclick="delPlan()" class="btn btn-default btn-xs toolShow" title="删除预案"><i class="ec-trashcan"></i><span>删除预案</span></button>'+
              '<button type="button" onclick="delTimes()" class="btn btn-default btn-xs toolShow" title="删除时段"><i class="im-close"></i><span>删除时段</span></button>'+
            '</div>'

}