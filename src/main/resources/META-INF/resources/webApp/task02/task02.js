﻿/**
 * Created by lvcheng on 2017/2/21.
 */

/*全局变量*/
var totalWidth, totalDate, startDate, qjMsg;
var index, indexPar, handle, minLeft, maxLeft, selfLeft, startX, leftWidth, rightWidth;
var allData = [];
var areaIndex, timeIndex;
var showCode = [{}, {}, {}];
var proNum, cityNum, countyNum;
var msg = {
  'id': 'yaMessage',
  'content': {
    rwId: '',
    rwName: '',
    qjId: '',
    qjName: '',
    qjStartDate: '',
    qjEndDate: '',
    areaName: '',
    areaId: '',
    timeId: ''
  }
};

var zTreeSetting = {
  check: {
    enable: true,
//				autoCheckTrigger:true,
    chkboxType: {"Y": "s", "N": "s"}, //子父级联动控制，仅子级联动
    chkDisabledInherit: true //是否沿用disabled
  },
  data: {
    simpleData: {
      enable: true,     //简单数据模式
      idKey: "adcode",
      pIdKey: "padcode" //子父级关系对照
    },
    key: {
      name: 'name',
    }
  },
  callback: {
    onCheck: function (e, t, tr) {
      var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
      selectNode(tr);
      if (tr.checked) {
//    	  setExtent(tr);
        if (tr.level == 0) {
          level0(tr)
        } else {
          level12(tr)
        }
      } else {
        if (tr.level == 0) {
          delNode0(tr)
        } else {
          delNode12(tr)
        }
      }
      updataCodeList();
    }
  }
};

function showMap(){
  addLayer(showCode);
}

function addP(adcode, name, level) {
  return $('<p class="col-md-3"><i class="im-close" style="cursor: pointer" onclick="delAdcode(' + adcode + ',' + level + ')"></i>&nbsp;&nbsp;' + name + ' </p>')
}
function updataCodeList() {
  $('.adcodeList').empty();
  for (var i = 0; i < 3; i++) {
    for (var ad in showCode[i]) {
      if (i == 0) {
        $('.adcodeList').append(addP(ad, showCode[i][ad], i))
        console.log(showCode)
      } else {
        for (var add in showCode[i][ad]) {
          $('.adcodeList').append(addP(add, showCode[i][ad][add], i))
        }
      }

    }
  }
  proNum = Object.keys(showCode[0]).length;
  cityNum = (function () {
    var n = 0;
    for (var ad in showCode[1]) {
      n += Object.keys(showCode[1][ad]).length;
    }
    return n;
  })();
  countyNum = (function () {
    var n = 0;
    for (var ad in showCode[2]) {
      n += Object.keys(showCode[2][ad]).length;
    }
    return n;
  })();

  $('.proNumber span').html(proNum);
  $('.cityNumber span').html(cityNum);
  $('.countyNumber span').html(countyNum);

  console.log(proNum, cityNum, countyNum);
}

/*删除所选地区*/
function delAdcode(adcode, level) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  var node = treeObj.getNodeByParam("adcode", adcode, null);
  treeObj.checkNode(node, false, true);
  switch (level) {
    case 0:
      delete showCode[level][adcode];
      break;
    case 1:
      delete showCode[level][adcode.toString().substr(0, 2) + "0000"][adcode];
      break;
    case 2:
      delete showCode[level][adcode.toString().substr(0, 4) + '00'][adcode];
      break;
  }
  addLayer(showCode);
  updataCodeList();

}

/*删除所有所选地区*/
function clearAllArea() {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  treeObj.checkAllNodes(false);
  showCode = [{}, {}, {}];
  addLayer(showCode);
  updataCodeList();
}

/*test使用*/
var parameterPar = {total: '', data: {}};
function ajaxPost1(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  //return $.ajax(BackstageIP + url, {
  return $.ajax(url, {
    contentType: "application/json",
    type: "GET",
    async: true,
    dataType: 'JSON',
    data: p
  })
}

initialize();


function initialize() {
  var ls = window.localStorage;
  qjMsg = vipspa.getMessage('qjMessage').content;

  if (!qjMsg) {
    qjMsg = JSON.parse(ls.getItem('qjMsg'));
  } else {
    ls.setItem('qjMsg', JSON.stringify(qjMsg));
  }

  msg.content.rwId = qjMsg.rwId;
  msg.content.rwName = qjMsg.rwName;
  msg.content.qjId = qjMsg.qjId;
  msg.content.qjName = qjMsg.qjName;
  msg.content.qjStartDate = qjMsg.qjStartDate;
  msg.content.qjEndDate = qjMsg.qjEndDate;
  msg.content.esCouplingId = qjMsg.esCouplingId;
  msg.content.esCouplingName = qjMsg.esCouplingName;

  $('.footerShow .rw span').html(qjMsg.rwName);
  $('.footerShow .qj span').html(qjMsg.qjName);

  /*总时长*/
  totalDate = qjMsg.qjEndDate - qjMsg.qjStartDate;
  startDate = qjMsg.qjStartDate;

  var url = '/area/get_areaAndTimeList';
  var scenarino = ajaxPost(url, {
    scenarinoId: qjMsg.qjId,
    userId: userId
  });
  initDate();

  scenarino.then(function (res) {

    allData = res.data;
    for (var i = 0; i < res.data.length; i++) {
      allData[i].timeFrame = [];
      /*克隆区域进行添加*/
      //var area = $('.area.disNone').clone().removeClass('disNone');
      //area.find('.front>span').html(res.data[i].areaName);
      var timeItems = res.data[i].timeItems;
      var tLength = timeItems.length;
      //$('.areaMsg').append(area);
      for (var item = 0; item < tLength; item++) {
        //var totalWidth = $('.period').width();
        //
        ///*克隆时段进行添加*/
        //var times = $('.time.disNone').clone().removeClass('disNone');
        //area.find('.showLine').before(times);
        //
        //if(timeItems[item].planId == -1){
        //  times.find('.timeToolDiv .btn').eq(1).attr('disabled','disabled');
        //}else{
        //  times.find('.timeToolDiv .btn').eq(0).attr('disabled','disabled');
        //  times.find('h4').html(timeItems[item].planName);
        //}
        //if(tLength == 1){
        //  times.find('.timeToolDiv .btn').eq(2).attr('disabled','disabled');
        //}

        if (item > 0) {
          var sD = timeItems[item].timeStartDate;
          allData[i].timeFrame[item - 1] = moment(sD).format('YYYY/MM/DD HH');

          /*克隆滑块进行添加*/
          //var hk = $('.hk.disNone').clone().removeClass('disNone');
          //hk.find('.showTips').html(moment(sD).format('YYYY/MM/DD HH'));
          //var left = ((sD - startDate)/totalDate);
          //area.find('.showLine').append(hk);
          //hk.css('left',left*100+'%');
        }
        //var tw;

        //tw = ((timeItems[item].timeEndDate - timeItems[item].timeStartDate)/totalDate)*totalWidth - 1;
        //times.css('width',(tw/totalWidth)*100 + '%');

      }

    }


    showTimeline(allData);
    console.log(allData)
    app2();
  });


}
var scenarino;
/*请求区域及时段*/
function getAreaAndTime() {
  var url = '/area/get_areaAndTimeList';
  scenarino = ajaxPost(url, {
    scenarinoId: qjMsg.qjId,
    userId: userId
  });

  scenarino.then(function (res) {
    allData = res.data;
    for (var i = 0; i < res.data.length; i++) {
      allData[i].timeFrame = [];
      var timeItems = res.data[i].timeItems;
      var tLength = timeItems.length;
      for (var item = 0; item < tLength; item++) {
        if (item > 0) {
          var sD = timeItems[item].timeStartDate;
          allData[i].timeFrame[item - 1] = moment(sD).format('YYYY/MM/DD HH');
        }
      }
    }
    showTimeline(allData);
    console.log(allData)
  });
}

/*初始化zTree*/
function initZTree(data) {
  $.fn.zTree.init($("#adcodeTree"), zTreeSetting, data);
}

function sub() {
  data.push($('input[type=text]').val());

}

$('.areaMsg').on('mouseenter', '.hk', function (e) {
//    index = $('.hk').index(this);
//    startX = e.pageX;
  $(this).find('.showTips').css('display', 'block');
});
$('.areaMsg').on('mouseleave', '.hk', function (e) {
//    index = $('.hk').index(this);
  $(this).find('.showTips').css('display', 'none');
});

$('.areaMsg').on('mousedown', '.hk', function (e) {
  var widthP = $('.period').width();
  indexPar = $('.area').index($(this).parents('.area'));
  index = $('.area').eq(indexPar).find('.hk').index(this);
  leftWidth = $('.area').eq(indexPar).children('.period').children('.time').eq(index).width();
  rightWidth = $('.area').eq(indexPar).children('.period').children('.time').eq(index + 1).width();
  handle = true;
  startX = e.pageX;
  $(this).find('.showTips').css('display', 'block');
  selfLeft = parseInt($(this).css('left'));
  minLeft = parseInt($('.area').eq(indexPar).find('.hk').eq(index - 1).css('left'));
  maxLeft = parseInt($('.area').eq(indexPar).find('.hk').eq(index + 1).css('left'));
  if (selfLeft > (widthP / 2)) {
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsLeft').addClass('showTipsRight');
  } else {
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsRight').addClass('showTipsLeft');
  }
  console.log(allData)
  //data[index] = selfLeft;
});
$('.areaMsg').on('mouseup', '.area', function (e) {
  //index = $('.hk').index(this);
  if (!handle)return;
  allData[indexPar].timeFrame[index] = allData[indexPar].timeItems[index].timeEndDate;

  ajaxPost('/time/update_time', {
    userId: userId,
    updateDate: allData[indexPar].timeItems[index].timeEndDate,
    beforeTimeId: allData[indexPar].timeItems[index].timeId,
    afterTimeId: allData[indexPar].timeItems[index + 1].timeId
  }).success(function (res) {
    console.log(res)
  })

  console.log(allData);
  handle = false;
  $(this).find('.showTips').css('display', 'none');
});

$('.areaMsg').on('mousemove', '.period', function (e) {
  e.stopPropagation();
  if (handle) {
    var widthP = $(this).width();
    var moveX = e.pageX;
    if (!maxLeft) {
      maxLeft = widthP - 4;
    }
    var newLeft = moveX - startX + selfLeft;
    if (newLeft > (maxLeft - 4))newLeft = maxLeft - 4;
    if (index == 0) {
      if (newLeft < 4)newLeft = 4;

    } else {
      if (newLeft < (minLeft + 4))newLeft = minLeft + 4;
    }
    //data[index] = newLeft;
    $('.area').eq(indexPar).find('.hk').eq(index).css('left', (newLeft / widthP) * 100 + '%');

    if (newLeft > (widthP / 2)) {
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsLeft').addClass('showTipsRight');
    } else {
      $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').removeClass('showTipsRight').addClass('showTipsLeft');
    }

    $(this).children('.time').eq(index).css('width', ((newLeft - selfLeft + leftWidth) / widthP) * 100 + '%');
    $(this).children('.time').eq(index + 1).css('width', ((+rightWidth - (newLeft - selfLeft)) / widthP) * 100 + '%');

    var showDate = moment((newLeft / widthP * totalDate) + startDate).format('YYYY/MM/DD HH');
    $('.area').eq(indexPar).find('.hk').eq(index).find('.showTips').html(showDate);

    allData[indexPar].timeItems[index].timeEndDate = showDate;
    allData[indexPar].timeItems[index + 1].timeStartDate = showDate;
    //console.log(moment((newLeft/widthP*totalDate)+startDate).format('YYYY-MM-DD HH'))
  }
//    console.log(widthP,startX,moveX,selfLeft)
});

/*删除区域*/
function delArea(e) {
  console.log(e);
  var indexPar = $('.areaTitle_con').index($(e).parents('.areaTitle_con'));
  var url = '/area/delete_area';
  //var areaIds = [allData[indexPar].areaId.toString()];
  var params = {
    userId: userId,
    //areaIds: allData[indexPar].areaId.toString()
    areaIds: $(e).parents('.areaTitle_con').attr('id')
  };
  swal({
      title: "确定要删除区域-" + allData[indexPar].areaName,
      type: "warning",
      showCancelButton: true,
      confirmButtonColor: "#DD6B55",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      closeOnConfirm: false
    },
    function () {
      ajaxPost(url, params).success(function (res) {
        if (res.status == 0) {
          allData.splice(indexPar, 1);
          $('.areaTitle_con').eq(indexPar).remove();
          showTimeline(allData);
          //$('.areaTitle_con').eq(indexPar).remove();
          swal("已删除!", "", "success");
        } else {
          swal("删除失败!", "", "error");
        }
      }).error(function () {
        swal("删除失败!", "", "error");
      })

    });

}

/*点击时段编辑信息*/
function editTimes() {

}

/*初始化日期插件*/
function initEditTimeDate(s, e) {
  console.log(s, e);
  $('#editDate').daterangepicker({
    singleDatePicker: true,  //显示单个日历
    timePicker: true,  //允许选择时间
    timePicker24Hour: true, //时间24小时制
    minDate: s,//最早可选日期
    maxDate: e,//最大可选日期
    locale: {
      format: "YYYY-MM-DD HH",
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
    //"startDate": moment().subtract(2,'w'),
    //"endDate": end,
    "opens": "right"
  }, function (start, end, label) {

    if (editTimeDateObj.type == 'start') {
      if (editTimeDateObj.beforeS >= moment(start).subtract(1, 'h').format('YYYY-MM-DD HH')) {
        console.log('时间不合理请重新选择！！！');
        return
      }
      editTimeDateObj.s = moment(start).format('YYYY-MM-DD HH');
      editTimeDateObj.beforeE = moment(start).subtract(1, 'h').format('YYYY-MM-DD HH')
    } else {
      if (moment(start).add(1, 'h').format('YYYY-MM-DD HH') >= editTimeDateObj.afterE) {
        console.log('时间不合理请重新选择！！！');
        return
      }
      editTimeDateObj.e = moment(start).format('YYYY-MM-DD HH');
      editTimeDateObj.afterS = moment(start).add(1, 'h').format('YYYY-MM-DD HH')
    }
    updatetimeSow();
  })
}

function sunEditTimeDate() {
  var url = '/time/update_time';
  var after, before, date;
  if (editTimeDateObj.type == 'start') {
    date = editTimeDateObj.s;
    before = allData[indexPar].timeItems[index - 1].timeId;
    after = allData[indexPar].timeItems[index].timeId
  } else {
    date = editTimeDateObj.e;
    after = allData[indexPar].timeItems[index + 1].timeId;
    before = allData[indexPar].timeItems[index].timeId
  }
  ajaxPost(url, {
    userId: userId,
    updateDate: moment(date).add(1, 'h').format('YYYY-MM-DD HH'),
    beforeTimeId: before,
    afterTimeId: after
  }).success(function (res) {
    if (res.status == 0) {
      if (editTimeDateObj.type == 'start') {
        allData[indexPar].timeItems[index].timeStartDate = moment(date).format('x') - 0;
        allData[indexPar].timeItems[index - 1].timeEndDate = moment(editTimeDateObj.beforeE).format('x') - 0;
      } else {
        allData[indexPar].timeItems[index].timeEndDate = moment(date).format('x') - 0;
        allData[indexPar].timeItems[index + 1].timeStartDate = moment(editTimeDateObj.afterS).format('x') - 0;
      }
      showTimeline(allData);
    }
  })
}

/*添加时间段*/
function addTimes() {
  console.log(123);
  var timePoint = $('#qyTimePoint').val();
  var timeFrame = allData[areaIndex].timeFrame;
  timeFrame.push(timePoint);
  timeFrame.sort();
  var index = timeFrame.indexOf(timePoint);

  var url = '/time/save_time';
  ajaxPost(url, {
    missionId: qjMsg.rwId,
    scenarinoId: qjMsg.qjId,
    userId: userId,
    areaId: allData[areaIndex].areaId,
    selectTimeId: allData[areaIndex].timeItems[index].timeId,
    addTimeDate: timePoint
  }).success(function (res) {
    console.log(res);

    getAreaAndTime();


  }).error(function () {
    timeFrame.splice(index, 1);
    swal('添加失败！！！', '', 'error')
  });

}

/*返回YYYY-MM-DD HH格式*/
function momentDate(d) {
  var n = Number(d);
  if (!isNaN(n)) {
    return moment(d).format('YYYY-MM-DD HH')
  } else {
    return moment(d, 'YYYY-MM-DD HH').format('YYYY-MM-DD HH')
  }
}

/*打开删除时段模态框*/
function openDelTimes() {
  $('#delTime').modal('show');
}
/*删除时间段*/
function delTimes() {
  var url = '/time/delete_time';
  var mId;
  var ub = $('.delSelect input:checked').val();
  var delTime;
  if (ub == 'up') {
    mId = allData[areaIndex].timeItems[timeIndex - 1].timeId;
    delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate)).format('YYYY/MM/DD HH');
  } else {
    mId = allData[areaIndex].timeItems[timeIndex + 1].timeId;
    delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate)).format('YYYY/MM/DD HH');
  }

  ajaxPost(url, {
    deleteTimeId: allData[areaIndex].timeItems[timeIndex].timeId,
    startDate: moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate)).format('YYYY/MM/DD HH'),
    endDate: moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate)).format('YYYY/MM/DD HH'),
    mergeTimeId: mId,
    userId: userId,
    status: ub
  }).success(function () {
    var index = allData[areaIndex].timeFrame.indexOf(delTime);
    var totalWidth = $('.area').eq(areaIndex).find('.period').width();
    var delTimes = $('.area').eq(areaIndex).find('.time').eq(timeIndex);
    var delWidth = delTimes.width();
    if (ub == 'up') {
      allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
    } else {
      allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
    }
    delTimes.remove();
    allData[areaIndex].timeFrame.splice(index, 1);
    allData[areaIndex].timeItems.splice(timeIndex, 1);
    showTimeline(allData);
  })

}

/*当前选中的时段*/
var selectedTimes;
function ontTimes(data) {
  selectedTimes = data;
  if (data.planId != -1) {
    $('.yacz').attr('disabled', true);
  } else {
    $('.yacz').removeAttr('disabled');
  }
  $('#editTime').modal('show')
}

/*时段预案操作模态框选择 start*/
function selectOperate(type) {
  if (type == 'sTime') {
    $('.btnSelect').css({
      'right': '-100%',
      'opacity': 0
    });
    $('.' + type).css({
      left: 0,
      'opacity': 1
    })

    if (allData[selectedTimes.index].timeItems.length <= 1) {
      $('.delTimeBtn').attr('disabled', true);
      $('.editTimeBtn').attr('disabled', true);
    } else {
      $('.delTimeBtn').removeAttr('disabled');
      $('.editTimeBtn').removeAttr('disabled');
    }

  } else {
    $('.btnSelect').css({
      'left': '-100%',
      'opacity': 0
    });
    $('.' + type).css({
      right: 0,
      'opacity': 1
    })
  }
}

$('#editTime').on('show.bs.modal', function (event) {
  $('.btnSelect').css({
    'right': 0,
    'left': 0,
    'opacity': 1
  });
  $('.sTime').css({
    left: '-100%',
    'opacity': 0
  });
  $('.sPlan').css({
    right: '-100%',
    'opacity': 0
  })
});

/*打开预案编辑*/
function openAddYA() {
  $('#addYA .selectAdd').removeClass('disNone');
  $('#addYA .addCopyPlan').addClass('disNone');
  $('#addYA .addNewPlan').addClass('disNone');
  $('#addYA .modal-footer').addClass('disNone');

  window.setTimeout(function () {
    $('#addYA').modal('show')
  }, 350)
}

/*时段预案操作模态框选择 end*/

var newPlan;
/*添加预案*/
function addPlan() {

  if (newPlan) {
    var url = '/plan/add_plan';
    var params = {
      timeId: msg.content.timeId,
      userId: userId,
      missionId: msg.content.rwId,
      scenarioId: msg.content.qjId,
      areaId: msg.content.areaId,
      timeStartTime: moment(momentDate(msg.content.timeStartDate)).format('YYYY-MM-DD HH'),
      timeEndTime: moment(momentDate(msg.content.timeEndDate)).format('YYYY-MM-DD HH'),
      planName: $('#yaName').val()
    };
    ajaxPost(url, params).success(function (res) {
      msg.content.planId = res.data;
      msg.content.planName = $('#yaName').val();
      vipspa.setMessage(msg);
      createNewPlan();
      $('#yaName').val('');
    });
  } else {
    var planId = $('#copyPlan').val();
    var planName = $('#copyPlan').find("option:selected").text();
    var url = '/plan/copy_plan';

    ajaxPost(url, {
      userId: userId,
      planId: planId,
      timeId: allData[areaIndex].timeItems[timeIndex].timeId
    }).success(function () {
      allData[areaIndex].timeItems[timeIndex].planId = planId;
      allData[areaIndex].timeItems[timeIndex].planName = planName;
      showTimeline(allData);
    })
  }
}

/*创建新预案*/
function createNewPlan(e) {
  window.setTimeout(function () {
    $('#addCSBJ')[0].click();
    console.log(123)
  }, 500)
}

/*添加新预案按钮*/
function addNewPlan(e) {
  $(e).parents('#addYA').find('.modal-footer').removeClass('disNone');
  $(e).parents('#addYA').find('.addNewPlan').removeClass('disNone');
  $(e).parents('#addYA').find('.addCopyPlan').addClass('disNone');
  $(e).parents('.selectAdd').addClass('disNone');
  newPlan = true;
}

/*选择已有预案*/
function copyPlan(e) {
  $(e).parents('#addYA').find('.modal-footer').removeClass('disNone');
  $(e).parents('#addYA').find('.addCopyPlan').removeClass('disNone');
  $(e).parents('#addYA').find('.addNewPlan').addClass('disNone');
  $(e).parents('.selectAdd').addClass('disNone');
  newPlan = false;
  var url = '/plan/copy_plan_list';
  ajaxPost(url, {
    userId: userId
  }).success(function (res) {
    $(e).parents('#addYA').find('.modal-footer').removeClass('disNone');
    $(e).parents('#addYA').find('.addCopyPlan').removeClass('disNone');
    $(e).parents('.selectAdd').addClass('disNone');

    for (var i = 0; i < res.data.length; i++) {
      $('<option value="' + res.data[i].planId + '">' + res.data[i].planName + '</option>').appendTo('#copyPlan')
    }
  })
}

/*编辑预案*/
function editPlan(t) {
  //areaIndex = $('.areaTitle_con').index($(t).parents('.areaTitle_con'));
  //timeIndex = $(t).parents('.area').find('.time').index($(t).parents('.time'));
  areaIndex = t.index;
  timeIndex = t.indexNum;

  msg.content.areaId = allData[areaIndex].areaId;
  msg.content.areaName = allData[areaIndex].areaName;
  msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
  msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
  msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
  msg.content.planId = allData[areaIndex].timeItems[timeIndex].planId;
  msg.content.planName = allData[areaIndex].timeItems[timeIndex].planName;
  vipspa.setMessage(msg);

  if (msg.content.planId == -1)return;

  createNewPlan();
}

///*删除预案*/
//function delPlan(e){}

var editTimeDateObj = {};
function clearTimeDate() {
  editTimeDateObj = {};
  editTimeDateObj.s = moment(selectedTimes.startTime).format('YYYY-MM-DD HH');
  editTimeDateObj.e = moment(selectedTimes.endTime).format('YYYY-MM-DD HH');
  if (index == 0) {
    editTimeDateObj.afterS = moment(allData[indexPar].timeItems[index + 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.afterE = moment(allData[indexPar].timeItems[index + 1].timeEndDate).format('YYYY-MM-DD HH');
  } else if (index == allData[indexPar].timeItems.length - 1) {
    editTimeDateObj.beforeS = moment(allData[indexPar].timeItems[index - 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeE = moment(allData[indexPar].timeItems[index - 1].timeEndDate).format('YYYY-MM-DD HH');
  } else {
    editTimeDateObj.afterS = moment(allData[indexPar].timeItems[index + 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.afterE = moment(allData[indexPar].timeItems[index + 1].timeEndDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeS = moment(allData[indexPar].timeItems[index - 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeE = moment(allData[indexPar].timeItems[index - 1].timeEndDate).format('YYYY-MM-DD HH');
  }
}

$('#editTimeDate').on('show.bs.modal', function (event) {
  indexPar = selectedTimes.index;
  index = selectedTimes.indexNum;
  clearTimeDate();
  updatetimeSow();
  editTimeDateObj.type = $('#selectEditPoint').val();
});

function updatetimeSow() {
  $('.showTimes .col-md-4 p').eq(0).empty();
  $('.showTimes .col-md-4 p').eq(1).empty();
  $('.showTimes .col-md-4 p').eq(2).empty();
  $('#selectEditPoint').empty();
  var s, e;
  s = editTimeDateObj.s;
  e = editTimeDateObj.e;
  if (index == 0) {
    e = editTimeDateObj.afterE;
    $('.showTimes .col-md-4 p').eq(0)
      .html('<h4>无时段</h4>');
    $('.showTimes .col-md-4 p').eq(2)
      .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
    $('#selectEditPoint').append($('<option value="end">结束时间</option>'))
    //editTimeDateObj.type = 'end'
  } else if (index == allData[indexPar].timeItems.length - 1) {
    s = editTimeDateObj.beforeS;
    $('.showTimes .col-md-4 p').eq(2)
      .html('<h4>无时段</h4>');
    $('.showTimes .col-md-4 p').eq(0)
      .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
    $('#selectEditPoint').append($('<option value="start">开始时间</option>'))
    //editTimeDateObj.type = 'start'
  } else {
    s = editTimeDateObj.beforeS;
    $('#selectEditPoint').append($('<option value="start">开始时间</option>'));
    $('#selectEditPoint').append($('<option value="end">结束时间</option>'));
    $('.showTimes .col-md-4 p').eq(0)
      .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
    $('.showTimes .col-md-4 p').eq(2)
      .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
    //editTimeDateObj.type = 'start'
  }
  $('.showTimes .col-md-4 p').eq(1).html(editTimeDateObj.s + '<br />至<br/>' + editTimeDateObj.e);
  initEditTimeDate(s, e);
  initEditTimeDate(s, e);
}

function selectEditPoint(t) {
  var s, e;
  if ($(t).val() == 'start') {
    s = editTimeDateObj.beforeS;
    e = editTimeDateObj.e;
    editTimeDateObj.type = 'start'
  } else {
    s = editTimeDateObj.s;
    e = editTimeDateObj.afterE;
    editTimeDateObj.type = 'end'
  }

  initEditTimeDate(s, e);
  initEditTimeDate(s, e);
}

function editTimeDate() {
  $('#editTimeDate').modal('show');
  window.setTimeout(function () {
  }, 350)
}


function editArea() {
}

$('#editArea').on('show.bs.modal', function (event) {
  var button = $(event.relatedTarget);
  var create = button.data('new');
  var areaId, findUrl;
  var treeUrl = '/area/find_areas';
  $('.adcodeList.mt20').empty();
  if (create) {

    $('#areaName').val('').removeAttr('data-id');
    showCode = [{},{},{}];
    $('.adcodeList.mt20').empty();
  } else {
    findUrl = '/area/get_areaList';
    //indexPar = $('.area').index(button.parents('.area'));
    //areaId = allData[indexPar].areaId;
    areaId = button.parents('.areaTitle_con').attr('id');
    console.log(areaId);
    ajaxPost(findUrl, {
      areaId: areaId,
      userId: userId
    }).success(function (res) {
      if (res.data) {
        setShowCode(res.data);
        addLayer(showCode);
      }
      updataCodeList();
    });
    $('#areaName').val(button.parents('.areaTitle_con').find('b').html()).attr('data-id', areaId);
  }

  ajaxPost(treeUrl, {
    scenarinoId: qjMsg.qjId,
    userId: userId,
    areaId: areaId
  }).success(function (res) {
    initZTree(res.data);
    testDis();
  })
});

function createEditArea() {
  var url = '/area/saveorupdate_area';
  var areaName = $('#areaName').val();
  if (!areaName) {
    alert('kong')
  }

  var obj = {
    missionId: qjMsg.rwId,
    scenarinoId: qjMsg.qjId,
    areaName: areaName,
    userId: userId,
    scenarinoStartDate: moment(momentDate(qjMsg.qjStartDate)).format('YYYY/MM/DD HH'),
    scenarinoEndDate: moment(momentDate(qjMsg.qjEndDate)).format('YYYY/MM/DD HH'),
    areaId: $('#areaName').attr('data-id') || '',
    provinceCodes: '',
    cityCodes: '',
    countyCodes: ''
  };

  var pArr = [];
  for (var i in showCode[0]) {
    var proObj = {};
    proObj[i] = showCode[0][i];
    pArr.push(proObj);
  }
  obj.provinceCodes = JSON.stringify(pArr);
  var ctArr = [];
  for (var i in showCode[1]) {
    for (var ii in showCode[1][i]) {
      var cityObj = {};
      cityObj[ii] = showCode[1][i][ii];
      ctArr.push(cityObj)
    }
  }
  obj.cityCodes = JSON.stringify(ctArr);
  var crArr = [];
  for (var i in showCode[2]) {
    for (var ii in showCode[2][i]) {
      var countyObj = {};
      countyObj[ii] = showCode[2][i][ii];
      crArr.push(countyObj)
    }
  }
  obj.countyCodes = JSON.stringify(crArr);
  ajaxPost(url, obj).success(function (res) {
    console.log(res);

    if (!$('#areaName').attr('data-id')) {
      var obj = {};
      obj.areaId = res.data.areaId;
      obj.areaName = areaName;
      obj.timeFrame = [];
      obj.timeItems = [{
        planId: -1,
        planName: '无',
        timeId: res.data.timeId,
        timeEndDate: qjMsg.qjEndDate,
        timeStartDate: qjMsg.qjStartDate
      }];
      allData.push(obj);
      showTimeline(allData);
    }
  })
}

function setShowCode(data) {

  proNum = data.provinceCodes.length;
  cityNum = data.cityCodes.length;
  countyNum = data.countyCodes.length;

  if (proNum == 0) {
    showCode[0] = {};
  } else {
    for (var i = 0; i < proNum; i++) {
      $.extend(showCode[0], data.provinceCodes[i]);
    }
  }
  if (cityNum == 0) {
    showCode[1] = {};
  } else {
    for (var ii = 0; ii < cityNum; ii++) {
      var adcode1 = Object.keys(data.cityCodes[ii])[0];
      if (!showCode[1][adcode1.substr(0, 2)+'0000'])showCode[1][adcode1.substr(0, 2)+'0000'] = {};
      $.extend(showCode[1][adcode1.substr(0, 2)+'0000'], data.cityCodes[ii]);
    }
  }
  if (countyNum == 0) {
    showCode[2] = {};
  } else {
    for (var iii = 0; iii < countyNum; iii++) {
      var adcode2 = Object.keys(data.countyCodes[iii])[0];
      if (!showCode[2][adcode2.substr(0, 4)+'00'])showCode[2][adcode2.substr(0, 4)+'00'] = {};
      $.extend(showCode[2][adcode2.substr(0, 4)+'00'], data.countyCodes[iii]);
    }
  }

}


$('#qyTime').on('show.bs.modal', function (event) {
  //console.log(event);
  var button = $(event.relatedTarget);
  if (button.length == 0)return;
  areaIndex = $('.areaTitle_con').index(button.parents('.areaTitle_con'));
  console.log(areaIndex)
});

$('#addYA').on('show.bs.modal', function (event) {

  //var button = $(event.relatedTarget);
  //if (button.length == 0)return;
  //areaIndex = $('.areaTitle_con').index(button.parents('.areaTitle_con'));
  //timeIndex = button.parents('.area').find('.time').index(button.parents('.time'));

  console.log(selectedTimes);
  areaIndex = selectedTimes.index;
  timeIndex = selectedTimes.indexNum;

  $(event.target).find('.modal-footer').addClass('disNone');
  $(event.target).find('.addCopyPlan').addClass('disNone');
  $(event.target).find('.addNewPlan').addClass('disNone');
  $(event.target).find('.selectAdd').removeClass('disNone');
  $(event.target).find('#copyPlan').empty();

  msg.content.areaId = allData[areaIndex].areaId;
  msg.content.areaName = allData[areaIndex].areaName;
  msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
  msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
  msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
});

$('#delTime').on('show.bs.modal', function (event) {
  //console.log(event);
//  var button = $(event.relatedTarget);
//  if (button.length == 0)return;
  //areaIndex = $('.areaTitle_con').index(button.parents('.areaTitle_con'));
  //timeIndex = button.parents('.area').find('.time').index(button.parents('.time'));
  areaIndex = selectedTimes.index;
  timeIndex = selectedTimes.indexNum;
  $(event.target).find('.delSelect').empty();

  var redio = $('.radio.disNone').clone().removeClass('disNone');
  if (timeIndex == 0) {
    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
    redio.find('input').val('down');
  } else if (timeIndex == (allData[areaIndex].timeItems.length - 1)) {
    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
    redio.find('input').val('up');
  } else {
    var redio2 = $('.radio.disNone').clone().removeClass('disNone');
    redio2.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
    redio2.find('input').val('up');
    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
    redio.find('input').val('down');
    $(event.target).find('.delSelect').append(redio2);
  }
  $(event.target).find('.delSelect').append(redio);
  redio.find('input').attr('checked', 'checked');
  console.log(areaIndex, timeIndex)
});


/*初始化zTree*/
function initZtree() {

}

/*zTree相关方法*/
/*选择节点，控制勾选状态*/
function selectNode(node) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  var parNode = node.getParentNode();

  if (parNode) {
    treeObj.checkNode(parNode, true, false, false);
    var child = parNode.children;
    for (var c = 0; c < child.length; c++) {
      if (!child[c].checked) {
        treeObj.checkNode(parNode, false, false, false);
        if (parNode.getParentNode()) {
          treeObj.checkNode(parNode.getParentNode(), false, false, false);
        }
        break;
      }
    }
    if (parNode.checked) {
      var parparNode = parNode.getParentNode();
      if (parparNode) {
        treeObj.checkNode(parparNode, true, false, false);
        var proChild = parparNode.children;
        for (var c = 0; c < proChild.length; c++) {
          if (!proChild[c].checked) {
            treeObj.checkNode(parparNode, false, false, false);
            break;
          }
        }
      }
    }
  }
}

//function level0(node) {
//  showCode[node.level][node.adcode] = node.name;
//  delete showCode[node.level + 1][node.adcode];
//
//  for (var i = 1; i < showCode.length; i++) {
//    for (var a in showCode[i]) {
//      if (a.toString().substr(0, 2) == node.adcode.toString().substr(0, 2)) {
//        delete showCode[i][a];
//      }
//    }
//  }
//}


//function level12(node) {
//  var parNode = node.getParentNode();
//  if (!showCode[node.level][parNode.adcode]) {
//    showCode[node.level][parNode.adcode] = {}
//  }
//  showCode[node.level][parNode.adcode][node.adcode] = node.name;
//  if (parNode.children.length == Object.keys(showCode[node.level][parNode.adcode]).length) {
//    delete showCode[node.level][parNode.adcode];
//    if (node.level == 1) {
//      level0(parNode);
//    } else {
//      level12(parNode);
//    }
//  }
//
//  if (node.level == 1) {
//    delete showCode[node.level + 1][node.adcode];
//  }
//}

function level0(node) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  var nodesDis = treeObj.getNodesByParam('chkDisabled', true, node);
  if (nodesDis.length == 0) {
    showCode[node.level][node.adcode] = node.name;
    delete showCode[node.level + 1][node.adcode];
    for (var i = 1; i < showCode.length; i++) {
      for (var a in showCode[i]) {
        if (a.toString().substr(0, 2) == node.adcode.toString().substr(0, 2)) {
          delete showCode[i][a];
        }
      }
    }
  } else {
    treeObj.checkNode(node, false, false, false);
    var child = node.children;
    for(var ch=0;ch<child.length;ch++){
      if (!child[ch].chkDisabled) {
        level12(child[ch])
      }
    }
  }

}

function level12(node) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  var nodesDis = treeObj.getNodesByParam('chkDisabled', true, node);
  var parNode = node.getParentNode();

  if (nodesDis.length == 0) {
    if (!showCode[node.level][parNode.adcode]) {
      showCode[node.level][parNode.adcode] = {}
    }
    showCode[node.level][parNode.adcode][node.adcode] = node.name;
    if (parNode.children.length == Object.keys(showCode[node.level][parNode.adcode]).length) {
      delete showCode[node.level][parNode.adcode];
      if (node.level == 1) {
        level0(parNode);
      } else {
        level12(parNode);
      }
    }
    if (node.level == 1) {
      delete showCode[node.level + 1][node.adcode];
    }
  } else {
//    delete showCode[node.level][parNode.adcode];
    treeObj.checkNode(node, false, false, false);
    var child = node.children;
    if (!showCode[node.level + 1][node.adcode]) {
      showCode[node.level + 1][node.adcode] = {};
    }
    for (var ch = 0; ch < child.length; ch++) {
      if (!child[ch].chkDisabled) {
        showCode[node.level + 1][node.adcode][child[ch].adcode] = child[ch].name;
      }
    }
  }

}

function delNode0(node) {
  delete showCode[node.level][node.adcode]
}

function delNode12(node) {
  var parNode = node.getParentNode();
  if (!showCode[node.level][parNode.adcode]) {
    if (node.level == 1) {
      delNode0(parNode);
    } else {
      delNode12(parNode);
    }
    showCode[node.level][parNode.adcode] = {};
    var child = parNode.children;
    for (var n = 0; n < child.length; n++) {
      showCode[node.level][parNode.adcode][child[n].adcode] = child[n].name;
    }
  }
  delete showCode[node.level][parNode.adcode][node.adcode];
  if ($.isEmptyObject(showCode[node.level][parNode.adcode])) {
    delete showCode[node.level][parNode.adcode];
  }
}

function initDate() {
  $("#qyTimePoint").datetimepicker({
    format: 'yyyy/mm/dd hh',
    todayHighlight: false,
    minView: 'day',
    startView: 'month',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true,
    startDate: moment(qjMsg.qjStartDate).format('YYYY-MM-DD HH'),
    endDate: moment(qjMsg.qjEndDate).format('YYYY-MM-DD HH')
  })
    .on('changeDate', function (ev) {
      var date = moment(ev.date).format('YYYY/MM/DD HH');
      //$('#rwEndDate').datetimepicker('setStartDate', date);
      //$('#rwStartDate').datetimepicker('setEndDate', null);
    });
}


 /**********************************模态窗口地图部分*****************************************************/
 //通用属性
 var stat = {};
 //中心点坐标
 stat.cPointx = 116;
 stat.cPointy = 35;
 var app = {};
 var dong = {};

 var dojoConfig = {
 async: true,
 parseOnLoad: true,
 packages: [{
 name: 'tdlib',
 location: "/js/tdlib"
 }]
 };

 require(
 [
 "esri/map",
 "esri/layers/FeatureLayer",
 "esri/layers/GraphicsLayer",
 "esri/symbols/SimpleFillSymbol",
 "esri/symbols/SimpleLineSymbol",
 "esri/symbols/SimpleMarkerSymbol",
 "esri/renderers/ClassBreaksRenderer",
 "esri/geometry/Point",
 "esri/geometry/Extent",
 "esri/renderers/SimpleRenderer",
 "esri/graphic",
 "dojo/_base/Color",
 "dojo/dom-style",
 "esri/tasks/FeatureSet",
 "esri/SpatialReference",
 "tdlib/gaodeLayer",
 "esri/InfoTemplate",
 "esri/renderers/UniqueValueRenderer",
 "dojo/domReady!"

 ],
 function (Map, FeatureLayer, GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, ClassBreaksRenderer, Point, Extent, SimpleRenderer,
 Graphic, Color, style, FeatureSet, SpatialReference, gaodeLayer, InfoTemplate, UniqueValueRenderer) {
 dong.gaodeLayer = gaodeLayer;
 dong.Graphic = Graphic;
 dong.Point = Point;
 dong.GraphicsLayer = GraphicsLayer;
 dong.SpatialReference = SpatialReference;
 dong.SimpleLineSymbol = SimpleLineSymbol;
 dong.FeatureLayer = FeatureLayer;
 dong.SimpleRenderer = SimpleRenderer;
 dong.SimpleFillSymbol = SimpleFillSymbol;
 dong.Color = Color;
 dong.ClassBreaksRenderer = ClassBreaksRenderer;
 dong.UniqueValueRenderer = UniqueValueRenderer;
 dong.InfoTemplate = InfoTemplate;
 /*****************************************************/
 app.map1 = new Map("mapDiv1", {
 logo: false,
 center: [stat.cPointx, stat.cPointy],
 minZoom: 3,
 maxZoom: 13,
 zoom: 3
 });
 app.baselayerList1 = new dong.gaodeLayer();
 app.stlayerList1 = new dong.gaodeLayer({layertype: "st1"});
 app.labellayerList1 = new dong.gaodeLayer({layertype: "label1"});
 app.map1.addLayer(app.baselayerList1);//添加高德地图到map容器
 app.map1.addLayers([app.baselayerList1]);//添加高德地图到map容器
 app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
 app.map1.addLayer(app.gLyr1);
 app.map1.on("loaded", app2())
 /*************************************************************************/
 app.map = new Map("mapDiv", {
 logo: false,
 center: [stat.cPointx, stat.cPointy],
 minZoom: 3,
 maxZoom: 13,
 zoom: 3
 });
 app.baselayerList = new dong.gaodeLayer();
 app.stlayerList = new dong.gaodeLayer({layertype: "st"});
 app.labellayerList = new dong.gaodeLayer({layertype: "label"});
 app.map.addLayer(app.baselayerList);//添加高德地图到map容器
 app.map.addLayers([app.baselayerList]);//添加高德地图到map容器
 app.gLyr = new dong.GraphicsLayer({"id": "gLyr"});
 app.map.addLayer(app.gLyr);
 /******************************************/
 });
 //添加服务
 function addLayer(data) {
 shuju_clear();
 app.featureLayer1 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/china_x/MapServer/2", {
	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
	 mode: dong.FeatureLayer.MODE_ONDEMAND,
	 outFields: ["NAME"]
	 });
	 app.featureLayer2 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/china_x/MapServer/1", {
	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
	 mode: dong.FeatureLayer.MODE_ONDEMAND,
	 outFields: ["NAME"]
	 });
	 app.featureLayer3 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/china_x/MapServer/0", {
	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
	 mode: dong.FeatureLayer.MODE_ONDEMAND,
	 outFields: ["NAME"]
	 });
	 app.map.addLayer(app.featureLayer1);
	 app.map.addLayer(app.featureLayer2);
	 app.map.addLayer(app.featureLayer3);
 
 area(data);
 }
 //行政区划渲染
 function area (data) {
 console.log(data)
 var defaultSymbol = new dong.SimpleFillSymbol().setStyle(dong.SimpleFillSymbol.STYLE_NULL);
 defaultSymbol.outline.setStyle(dong.SimpleLineSymbol.STYLE_NULL);
 for ( var i = 0 ; i < 3; i ++ ) {
 var str = "ADMINCODE";
 if ( i == 1 ) {
 str = "CITYCODE" ;
 }
 var renderer = new  dong.UniqueValueRenderer(defaultSymbol,str);
 if(i == 0 ) {
 for (var prop in data[i]) {
 if (data[i].hasOwnProperty(prop)) {
 renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color([221, 160, 221, 0.5])));
 }
 }
 } else {
 for (var prop in data[i]) {
 if (data[i].hasOwnProperty(prop)) {
 for (var prop1 in data[i][prop]) {
 renderer.addValue(prop1, new dong.SimpleFillSymbol().setColor(new dong.Color([221, 160, 221, 0.5])));
 }
 }
 }
 }
// var str = "featureLayer"+i
// app.str = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/"+(2-i), {
// infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
// mode: dong.FeatureLayer.MODE_ONDEMAND,
// outFields: ["NAME"]
// });
 if ( i == "0" ) {
 app.featureLayer1.setRenderer(renderer);
 app.map.addLayer(app.featureLayer1);
 } else if ( i == "1" ) {
 app.featureLayer2.setRenderer(renderer);
 app.map.addLayer(app.featureLayer2);
 } else if ( i =="2" ) {
 app.featureLayer3.setRenderer(renderer);
 app.map.addLayer(app.featureLayer3);
 }
 }
 }
 //清空地图
 function shuju_clear() {
	 
 if (app.featureLayer1 != undefined && app.featureLayer1 != null && app.featureLayer1 != "") {
 app.map.removeLayer(app.featureLayer1);
// app.featureLayer1.clear();
 }
 if (app.featureLayer2 != undefined && app.featureLayer2 != null && app.featureLayer2 != "") {
 app.map.removeLayer(app.featureLayer2)
// app.featureLayer2.clear();
 }
 if (app.featureLayer3 != undefined && app.featureLayer3 != null && app.featureLayer3 != "") {
 //		app.featureLayer2.clear();
 app.map.removeLayer(app.featureLayer3)
// app.featureLayer3.clear();
 }
 }
 //对地图的定位
 function setExtent(data) {
 if (data != "" && data != undefined) {
 var code = data.adcode.substring(0, 2) + "0000";
 if (app.featureLayer1 != "" && app.featureLayer1 != null && app.featureLayer1 != undefined) {
 var ss = app.featureLayer1.graphics;
 if (data.checked == true) {
 $.each(app.featureLayer1.graphics, function (i, gra) {
 if (gra.attributes.ADMINCODE == code) {
 app.map.setExtent(app.featureLayer1.graphics[i].geometry.getExtent());
 }
 })
 }
 }
 }
 }
 //颜色数组
 var sz_corlor = ["#000080", "#6495ED", "#00FFFF", "#CD5C5C", "#FF00FF", "#9370DB", "#8B8682", "#EED5B7", "#FFF0F5", "	#9F79EE", "#A2B5CD", "#FFE1FF", "#B23AEE"];
 /**********************************任务管理进来地图*****************************************************/
var timeline;

$().ready(function () {

  $('#autoAdjustTime').on('change', function (event) {
    var value = $(this).val();
    $('#startTime').attr('disabled', value === 'both' || value === 'start');
    $('#endTime').attr('disabled', value === 'both' || value === 'end');
  });

  $('#guides').on('change', function (event) {
    $('#showGuidesLabel').attr('disabled', $(this).val() === 'none');
  });

  $('#zoom').on('change', function (event) {
    if (timeline) {
      timeline.timeline('zoom', $(this).val());
    }
  });

  $('#refresh')
    .on('click', showTimeline);


});

function showTimeline(data) {
//            var testData = $('#testData').val();
//  var testData = '2';
  //$.getJSON(testData, function (data) {
  //    var options = {};
  //    options.startTime = $('#startTime').val();
  //    options.endTime = $('#endTime').val();
  //    options.showArrow = $('#showArrow')[0].checked;
  //    options.timeScalePosition = $('#timeScalePosition').val();
  //    options.autoAdjustTime = $('#autoAdjustTime').val();
  //    options.smoothScroll = $('#smoothScroll').val();
  //    options.guides = $('#guides').val();
  //    options.showGuidesLabel = $('#showGuidesLabel')[0].checked;
  //    options.showSlider = $('#showSlider')[0].checked;
  //    options.zoom = $('#zoom').val();
  //    options.data = data;
  //    timeline = $('#timeline').timeline(options);
  //});

  var options = {};
  //options.startTime = $('#startTime').val();
  //options.endTime = $('#endTime').val();
  options.startTime = qjMsg.qjStartDate;
  options.endTime = qjMsg.qjEndDate;
//  options.showArrow = $('#showArrow')[0].checked;
//  options.timeScalePosition = $('#timeScalePosition').val();
//  options.autoAdjustTime = $('#autoAdjustTime').val();
//  options.smoothScroll = $('#smoothScroll').val();
//  options.guides = $('#guides').val();
//  options.showGuidesLabel = $('#showGuidesLabel')[0].checked;
//  options.showSlider = $('#showSlider')[0].checked;
//  options.zoom = $('#zoom').val();
  //options.data = testData === '1' ? testdata1 : testData === '2' ? testdata2 : testdata3;
  options.data = data;
  timeline = $('#timeline').timeline(options);
}
//根据区域在地图上显示
function app2() {
	var xmax = 0,xmin = 0,ymax = 0,ymin = 0;
	if ( app.map1 == "" || app.map1 == null || app.map1 == undefined ) {
		return;
	}
	if ( allData != "" && allData != null && allData != undefined ) {
		var defaultSymbol = new dong.SimpleFillSymbol().setStyle(dong.SimpleFillSymbol.STYLE_NULL);
		defaultSymbol.outline.setStyle(dong.SimpleLineSymbol.STYLE_NULL);
		$.each(allData,function(k,item){
			var str = sz_corlor[k];
			app.fea1 = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/2", {//添加省的图层
				 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
			        mode: dong.FeatureLayer.MODE_ONDEMAND,
			        outFields: ["NAME"]
			      });
			app.fea2 = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/1", {//市的图层
				 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
			        mode: dong.FeatureLayer.MODE_ONDEMAND,
			        outFields: ["NAME"]
			      });
			 app.fea3 = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/0", {//区县的图层
				 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
			        mode: dong.FeatureLayer.MODE_ONDEMAND,
			        outFields: ["NAME"]
			      });
			
			if(item.provinceCodes != "" && item.provinceCodes != null && item.provinceCodes != undefined ) {
				if(item.provinceCodes.length>0 ) {//市
					var renderer = new  dong.UniqueValueRenderer(defaultSymbol,"ADMINCODE");
					for ( var i = 0 ; i< item.provinceCodes.length; i++ ) {//省
						 for (var prop in item.provinceCodes[i]) {
							  if (item.provinceCodes[i].hasOwnProperty(prop)) { 
								  renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
							  } 
						}
						 app.fea1.setRenderer(renderer);
						 app.map1.addLayer(app.fea1);
					}
				} 
			}
			if ( item.cityCodes != "" && item.cityCodes != null && item.cityCodes != undefined ) {//市
				if( item.cityCodes.length>0) {
					var renderer = new  dong.UniqueValueRenderer(defaultSymbol,"CITYCODE");
					for(var i = 0 ; i < item.cityCodes.length; i++ ) {
						 for (var prop in item.cityCodes[i]) {
							  if (item.cityCodes[i].hasOwnProperty(prop)) { 
								  renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
							  } 
						}
						 app.fea2.setRenderer(renderer);
						 app.map1.addLayer(app.fea2);
					}
				}
			}
			if (item.countyCodes !="" && item.countyCodes != null && item.countyCodes != undefined ) {//区县
				if ( item.countyCodes.length > 0 ) {
					var renderer = new  dong.UniqueValueRenderer(defaultSymbol,"ADMINCODE");
					for ( var i = 0 ; i < item.countyCodes.length; i ++ ) {
						for (var prop in item.countyCodes[i]) {
							  if (item.countyCodes[i].hasOwnProperty(prop)) { 
								  renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
							  } 
						}
						 app.fea3.setRenderer(renderer);
						 app.map1.addLayer(app.fea3);
					} 
				}
			}
		})
	} 
	 
}

/*test 前端设置disabled*/
function testDis(data){
  var a = $.ajax('webApp/task02/test.json',{
    contentType: "application/json",
    type: "GET",
    async: true,
    dataType: 'JSON'
  })
  var disObj = [];
  $.when(a).then(function(res){
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    for(var i=0;i<res.length;i++){
      var name = '('+ res[i].areaName +')';
      for(var pro=0;pro<res[i].provinceCodes.length;pro++){
        var node = treeObj.getNodeByParam("adcode", Object.keys(res[i].provinceCodes[pro]), null);
        node.name += name;
        node.chkDisabled = true;
        treeObj.updateNode(node);
      }

      for(var ci=0;ci<res[i].cityCodes.length;ci++){
        var node = treeObj.getNodeByParam("adcode", Object.keys(res[i].cityCodes[ci]), null);
        node.name += name;
        node.chkDisabled = true;
        treeObj.updateNode(node);
      }

      for(var ct=0;ct<res[i].countyCodes.length;ct++){
        var node = treeObj.getNodeByParam("adcode", Object.keys(res[i].countyCodes[ct]), null);
        node.name += name;
        node.chkDisabled = true;
        treeObj.updateNode(node);
      }

    }
    console.log(res)
  })
}