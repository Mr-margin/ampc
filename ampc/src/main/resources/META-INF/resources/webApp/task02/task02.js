/**
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
    chkboxType: {"Y": "ps", "N": "ps"}, //设置父子联动
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
      //selectNode(tr);
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
      //updataCodeList();
    }
  }
};

function showMap() {
  addLayer(showCode);
}

function addP(adcode, name, level) {
  return $('<p class="col-md-3"><i class="im-close" style="cursor: pointer" onclick="delAdcode(' + adcode + ',' + level + ')"></i>&nbsp;&nbsp;' + name + ' </p>')
}
function updataCodeList() {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  $('.adcodeList').empty();
  for (var i = 0; i < 3; i++) {
    for (var ad in showCode[i]) {
      if (i == 0) {
        $('.adcodeList').append(addP(ad, showCode[i][ad], i))
      } else {
        for (var add in showCode[i][ad]) {
          $('.adcodeList').append(addP(add, showCode[i][ad][add], i))
        }
      }

    }
  }
  /*  proNum = Object.keys(showCode[0]).length;
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
   })();*/

  proNum = treeObj.getNodesByFilter(function (node) {
    return (node.checked && (node.level == 0))
  }).length;
  cityNum = treeObj.getNodesByFilter(function (node) {
    return (node.checked && (node.level == 1))
  }).length;
  countyNum = treeObj.getNodesByFilter(function (node) {
    return (node.checked && (node.level == 2))
  }).length;

  $('.proNumber span').html(proNum);
  $('.cityNumber span').html(cityNum);
  $('.countyNumber span').html(countyNum);

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


/*情景计算状态*/
function scenarinoType(typeNum) {
  $('.toolShow').removeAttr('disabled');
  $('.addNewArea').removeAttr('disabled');
  switch (typeNum) {
    case 1:
      $('.jpjs').attr('disabled', true);
      $('.jpjs').removeClass('disNone');
      $('.jpztck').addClass('disNone');
      $('.jpfx').attr('disabled', true);
      break;
    case 2:
      $('.jpjs').attr('disabled', true);
      $('.jpjs').removeClass('disNone');
      $('.jpztck').addClass('disNone');
      $('.jpfx').attr('disabled', true);
      break;
    case 3:
      $('.toolShow').attr('disabled', true);
      $('.addNewArea').attr('disabled', true);
    case 4:
      $('.jpjs').addClass('disNone');
      $('.jpztck').removeClass('disNone');
      $('.jpfx').attr('disabled', true);
      break;
    case 5:
      $('.jpjs').removeAttr('disabled');
      $('.jpjs').removeClass('disNone');
      $('.jpztck').addClass('disNone');
      $('.jpfx').removeAttr('disabled');
      break;
    case 6:
    case 7:
    case 8:
      $('.jpjs').attr('disabled', true);
      $('.jpjs').removeClass('disNone');
      $('.jpztck').addClass('disNone');
      $('.jpfx').removeAttr('disabled');
      break;
  }
}


initialize();


var zTreeData;
var allData1;//临时变量
function initialize() {

  /**
   * 设置导航条菜单
   */
  $("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">时段区域编辑</a>');
  previous();

  var ls = window.localStorage;
  qjMsg = vipspa.getMessage('qjMessage').content;

  if (!qjMsg) {
    var url = '/scenarino/find_Scenarino_status';
    qjMsg = JSON.parse(ls.getItem('qjMsg'));
    ajaxPost(url, {
      userId: userId,
      scenarinoId: qjMsg.qjId
    }).success(function (res) {
      qjMsg.scenarinoStatus = res.data.scenarinoStatus;
      scenarinoType(qjMsg.scenarinoStatus);
    })
  } else {
    ls.setItem('qjMsg', JSON.stringify(qjMsg));
    scenarinoType(qjMsg.scenarinoStatus)
  }
  $('.qyCon').removeClass('disNone');
  $('.qyCon .nowRw span').html(qjMsg.rwName);
  $('.qyCon .nowQj span').html(qjMsg.qjName);
  $('.qyCon .seDate span').html(moment(qjMsg.qjStartDate).format('YYYY-MM-DD') + '至' + moment(qjMsg.qjEndDate).format('YYYY-MM-DD'));

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

  var url = '/area/get_areaAndTimeList';
  var scenarino = ajaxPost(url, {
    scenarinoId: qjMsg.qjId,
    userId: userId
  });
  //initDate();

  scenarino.then(function (res) {

    allData1 = res.data.slice(0, -1);
    if (qjMsg.scenarinoStatus == 2) {
      for (var i = 0; i < allData1.length; i++) {
        for (var m = 0; m < allData1[i].timeItems.length; m++) {
          if (allData1[i].timeItems[m].planId != -1) {
            $('.jpjs').removeAttr('disabled');
            break;
          }
        }
      }
    }
    if (res.data[res.data.length - 1].isnew) {
      $('#selectCreateQj').modal('show')
    } else {
      selectCopy(false);
    }

    //$('#selectCreateQj').modal('show');
    //for (var i = 0; i < res.data.length; i++) {
    //  allData[i].timeFrame = [];
    //  var timeItems = res.data[i].timeItems;
    //  var tLength = timeItems.length;
    //  //$('.areaMsg').append(area);
    //  for (var item = 0; item < tLength; item++) {
    //
    //    if (item > 0) {
    //      var sD = timeItems[item].timeStartDate;
    //      allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
    //    }
    //  }
    //
    //}
    //
    //
    //showTimeline(allData);
    //app2();
  });

  initZTree();  //初始化zTree数据
}

/*初始化zTree数据*/
function initZTree() {
  var url = '/area/find_areas_new';
  ajaxPost(url, {
    userId: userId
  }).success(function (res) {
    zTreeData = res.data;
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
    allData = res.data.slice(0, -1);
    for (var i = 0; i < allData.length; i++) {
      allData[i].timeFrame = [];
      var timeItems = allData[i].timeItems;
      var tLength = timeItems.length;
      for (var item = 0; item < tLength; item++) {
        if (item > 0) {
          var sD = timeItems[item].timeStartDate;
          allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
        }
      }
    }
    showTimeline(allData);
  });
}


/*删除区域*/
function delArea(e) {
  var areaIndex = $('.areaTitle_con').index($(e).parents('.areaTitle_con'));
  var url = '/area/delete_area';
  //var areaIds = [allData[areaIndex].areaId.toString()];
  var params = {
    userId: userId,
    //areaIds: allData[areaIndex].areaId.toString()
    areaIds: $(e).parents('.areaTitle_con').attr('id'),
    scenarinoStatus: qjMsg.scenarinoStatus,
    scenarinoId: qjMsg.qjId
  };
  swal({
      title: "确定要删除区域-" + allData[areaIndex].areaName,
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
          allData.splice(areaIndex, 1);
          $('.areaTitle_con').eq(areaIndex).remove();
          showTimeline(allData);
          //$('.areaTitle_con').eq(areaIndex).remove();
          swal("已删除!", "", "success");
        } else {
          swal("删除失败!", "", "error");
        }
      }).error(function () {
        swal("删除失败!", "", "error");
      })

    });

}


/*初始化日期插件*/
function initEditTimeDate(s, e) {
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

/*编辑时段时间*/
function sunEditTimeDate() {
  var url = '/time/update_time';
  var after, before, date;
  if (editTimeDateObj.type == 'start') {
    date = moment(editTimeDateObj.s).format('YYYY-MM-DD HH:mm:ss');
    before = allData[areaIndex].timeItems[timeIndex - 1].timeId;
    after = allData[areaIndex].timeItems[timeIndex].timeId
  } else {
    date = moment(editTimeDateObj.e).add(1, 'h').format('YYYY-MM-DD HH:mm:ss');
    after = allData[areaIndex].timeItems[timeIndex + 1].timeId;
    before = allData[areaIndex].timeItems[timeIndex].timeId
  }
  ajaxPost(url, {
    userId: userId,
    updateDate: date,
    beforeTimeId: before,
    afterTimeId: after,
    scenarinoStatus: qjMsg.scenarinoStatus,
    scenarinoId: qjMsg.qjId
  }).success(function (res) {
    if (res.status == 0) {
      if (editTimeDateObj.type == 'start') {
        allData[areaIndex].timeItems[timeIndex].timeStartDate = moment(date).format('x') - 0;
        allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = moment(editTimeDateObj.beforeE).format('x') - 0;
      } else {
        allData[areaIndex].timeItems[timeIndex].timeEndDate = moment(editTimeDateObj.e).format('x') - 0;
        allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = moment(editTimeDateObj.afterS).format('x') - 0;
      }
      showTimeline(allData);
    }
  })
}

/*添加时间段*/
function addTimes() {
  addTimePoint = $('#qyTimePoint').val();
  var timePoint = moment(addTimePoint).format('YYYY-MM-DD HH:mm:ss');
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
    addTimeDate: timePoint,
    scenarinoStatus: qjMsg.scenarinoStatus
  }).success(function (res) {

    getAreaAndTime();


  }).error(function () {
    timeFrame.splice(index, 1);
    swal('添加失败！！！', '', 'error')
  });
}

/*添加时段按钮事件*/
//function openAddTimes() {
//  $('#qyTime').modal('show');
//}

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
//function openDelTimes() {
//  $('#delTime').modal('show');
//}

/*删除时间段*/
function delTimes() {
  var url = '/time/delete_time';
  var mId;
  var ub = $('.delSelect input:checked').val();
  var delTime;
  if (ub == 'up') {
    mId = allData[areaIndex].timeItems[timeIndex - 1].timeId;
    delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate)).format('YYYY-MM-DD HH');
  } else {
    mId = allData[areaIndex].timeItems[timeIndex + 1].timeId;
    delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate)).format('YYYY-MM-DD HH');
  }

  ajaxPost(url, {
    deleteTimeId: allData[areaIndex].timeItems[timeIndex].timeId,
    startDate: moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate)).format('YYYY-MM-DD HH:mm:ss'),
    endDate: moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate)).format('YYYY-MM-DD HH:mm:ss'),
    mergeTimeId: mId,
    userId: userId,
    status: ub,
    scenarinoStatus: qjMsg.scenarinoStatus,
    scenarinoId: qjMsg.qjId
  }).success(function () {
    var index = allData[areaIndex].timeFrame.indexOf(delTime);
    if (ub == 'up') {
      allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
    } else {
      allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
    }
    //delTimes.remove();
    allData[areaIndex].timeFrame.splice(index, 1);
    allData[areaIndex].timeItems.splice(timeIndex, 1);
    showTimeline(allData);
  })

}

/*当前选中的时段*/
var selectedTimes;
function ontTimes(data) {
  selectedTimes = data;
  console.log(selectedTimes);
  //if (data.planId != -1) {
  //  $('.yacz').attr('disabled', true);
  //} else {
  //  $('.yacz').removeAttr('disabled');
  //}

  if (data.planId != -1) {
    $('.addNewPlanBtn').attr('disabled', true);
    $('.addCopyPlanBtn').attr('disabled', true);
    $('.editPlanBtn').removeAttr('disabled');

  } else {
    $('.addNewPlanBtn').removeAttr('disabled');
    $('.addCopyPlanBtn').removeAttr('disabled');
    $('.editPlanBtn').attr('disabled', true);
  }


  //$('#editTime').modal('show')
  $('#timePlan').modal('show')
}


var addTimePoint;         //添加的时间点
/*timePlan 打开时需的准备工作*/
$('#timePlan').on('show.bs.modal', function (event) {

  $('#time .active').removeClass('active');
  $('#plan .active').removeClass('active');
  $('.addTimeLi').addClass('active');
  $('.addTimeDiv').addClass('active');
  areaIndex = selectedTimes.index;
  timeIndex = selectedTimes.indexNum;
  var timeStart = moment(selectedTimes.startTime);
  var timeEnd = moment(selectedTimes.endTime);
  /*最小间隔一小时*/
  var timeEnd1 = moment(selectedTimes.endTime).add(-1, 'h');
  msg.content.areaId = allData[areaIndex].areaId;
  msg.content.areaName = allData[areaIndex].areaName;
  msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
  msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
  msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
  msg.content.cityCodes = allData[areaIndex].cityCodes;
  msg.content.countyCodes = allData[areaIndex].countyCodes;
  msg.content.provinceCodes = allData[areaIndex].provinceCodes;

  /*添加时段 start*/
  initDate(timeStart.add(1,'h'),timeEnd1);
  editHtml('addTime1');
  /*滑块*/
  /*var timeArr = [];
   while (timeStart.isBefore(timeEnd1, 'h')) {
   timeArr.push(timeStart.add(1, 'h').format('YYYY-MM-DD HH'));
   }
   $('.addTimeHk').eq(0).empty();
   $('.addTimeHk').eq(0).append($('<h3 style="margin-bottom:40px;">编辑插入此时间段的时间点：</h3>'));
   $('.addTimeHk').eq(0).append($('<input type="hidden" class="qyTimePoint" id="qyTimePoint" />'));
   $('#qyTimePoint').jRange({/!*初始化滑块控件*!/
   from: 0,
   to: timeArr.length,
   step: 1,
   scale: [timeArr[0].substr(5), timeArr[Math.floor(timeArr.length/2)].substr(5), timeArr[timeArr.length - 1].substr(5)],
   format: function (s) {
   addTimePoint = timeArr[s];
   return timeArr[s];
   },
   width: 550,
   showLabels: true,
   showScale: true,
   theme: 'theme-blue'
   });
   $('#qyTimePoint').jRange('setValue', Math.floor(timeArr.length/2));*/
  /*添加时段 end*/

  /**************************************************************************************/
  /*删除时段 start*/
  editHtml('delTime');
  if(allData[areaIndex].timeItems.length > 1){
    $('.delTimeLi').removeClass('disNone');
    $('.delTimeDiv').find('.delSelect').empty();


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
      $('.delTimeDiv').find('.delSelect').append(redio2);
    }
    $('.delTimeDiv').find('.delSelect').append(redio);
    redio.find('input').attr('checked', 'checked');


  }else{
    $('.delTimeLi').addClass('disNone');
    $('.delTimeLi').removeClass('active');
    $('.delTimeDiv').removeClass('active');
  }
  /*删除时段 end*/

  /**************************************************************************************/
  /*编辑时段 start*/
  if(allData[areaIndex].timeItems.length > 1){
    $('.editTimeLi').removeClass('disNone');

    clearTimeDate();
    updatetimeSow();
    editTimeDateObj.type = $('#selectEditPoint').val();
  }else{
    $('.editTimeLi').addClass('disNone');
  }

  /*编辑时段 end*/

  /**************************************************************************************/
  /*添加预案 start*/
  $('.delPlanLi').addClass('disNone');//暂无删除预案接口支持
  if(selectedTimes.planId == -1){
    copyPlan();
    $('.addPlanLi').removeClass('disNone');
    $('.copyPlanLi').removeClass('disNone');
    $('.editPlanLi').addClass('disNone');

    $('.addPlanLi').addClass('active');
    $('.addPlanDiv').addClass('active');

  }else{
    $('.addPlanLi').addClass('disNone');
    $('.copyPlanLi').addClass('disNone');
    $('.editPlanLi').removeClass('disNone');

    $('.editPlanLi').addClass('active');
    $('.editPlanDiv').addClass('active');
  }
  /*添加预案 end*/

});

/*时段预案操作模态框选择 start*/
//function selectOperate(type) {
//  if (type == 'sTime') {
//    $('.btnSelect').css({
//      'right': '-100%',
//      'opacity': 0
//    });
//    $('.' + type).css({
//      left: 0,
//      'opacity': 1
//    })
//
//    if (allData[selectedTimes.index].timeItems.length <= 1) {
//      $('.delTimeBtn').attr('disabled', true);
//      $('.editTimeBtn').attr('disabled', true);
//    } else {
//      $('.delTimeBtn').removeAttr('disabled');
//      $('.editTimeBtn').removeAttr('disabled');
//    }
//
//  } else {
//    $('.btnSelect').css({
//      'left': '-100%',
//      'opacity': 0
//    });
//    $('.' + type).css({
//      right: 0,
//      'opacity': 1
//    })
//  }
//}

//$('#editTime').on('show.bs.modal', function (event) {
//  $('.btnSelect').css({
//    'right': 0,
//    'left': 0,
//    'opacity': 1
//  });
//  $('.sTime').css({
//    left: '-100%',
//    'opacity': 0
//  });
//  $('.sPlan').css({
//    right: '-100%',
//    'opacity': 0
//  })
//});

/*打开预案编辑*/
//function openAddYA() {
//  $('#addYA .selectAdd').removeClass('disNone');
//  $('#addYA .addCopyPlan').addClass('disNone');
//  $('#addYA .addNewPlan').addClass('disNone');
//  $('#addYA .modal-footer').addClass('disNone');
//
//  window.setTimeout(function () {
//    $('#addYA').modal('show')
//  }, 350)
//}

/*时段预案操作模态框选择 end*/
var newPlan;
/*添加预案*/
function addPlan(e) {
  newPlan = e;
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
      planName: $('#yaName').val(),
      scenarinoStatus: qjMsg.scenarinoStatus,
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
      timeId: allData[areaIndex].timeItems[timeIndex].timeId,
      scenarinoStatus: qjMsg.scenarinoStatus,
      scenarinoId: qjMsg.qjId
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
  }, 500)
}

/*添加新预案按钮*/
//function addNewPlan(e) {
//  $(e).parents('#addYA').find('.modal-footer').removeClass('disNone');
//  $(e).parents('#addYA').find('.addNewPlan').removeClass('disNone');
//  $(e).parents('#addYA').find('.addCopyPlan').addClass('disNone');
//  $(e).parents('.selectAdd').addClass('disNone');
//  newPlan = true;
//}

/*选择已有预案按钮*/
function copyPlan() {
  var url = '/plan/copy_plan_list';
  ajaxPost(url, {
    userId: userId
  }).success(function (res) {
    for (var i = 0; i < res.data.length; i++) {
      $('<option value="' + res.data[i].planId + '">' + res.data[i].planName + '</option>').appendTo('#copyPlan')
    }
  })
}

/*编辑预案*/
function editPlan(t) {
  if(!t){
     t = selectedTimes;
  }
  areaIndex = t.index;
  timeIndex = t.indexNum;

  msg.content.areaId = allData[areaIndex].areaId;
  msg.content.areaName = allData[areaIndex].areaName;
  msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
  msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
  msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
  msg.content.planId = allData[areaIndex].timeItems[timeIndex].planId;
  msg.content.planName = allData[areaIndex].timeItems[timeIndex].planName;
  msg.content.cityCodes = allData[areaIndex].cityCodes;
  msg.content.countyCodes = allData[areaIndex].countyCodes;
  msg.content.provinceCodes = allData[areaIndex].provinceCodes;

  vipspa.setMessage(msg);

  if (msg.content.planId == -1)return;

  createNewPlan();
}

/*编辑时段时间使用*/
var editTimeDateObj = {};
function clearTimeDate() {
  editTimeDateObj = {};
  editTimeDateObj.s = moment(selectedTimes.startTime).format('YYYY-MM-DD HH');
  editTimeDateObj.e = moment(selectedTimes.endTime).format('YYYY-MM-DD HH');
  if (timeIndex == 0) {
    editTimeDateObj.afterS = moment(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.afterE = moment(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate).format('YYYY-MM-DD HH');
  } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
    editTimeDateObj.beforeS = moment(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeE = moment(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate).format('YYYY-MM-DD HH');
  } else {
    editTimeDateObj.afterS = moment(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.afterE = moment(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeS = moment(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate).format('YYYY-MM-DD HH');
    editTimeDateObj.beforeE = moment(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate).format('YYYY-MM-DD HH');
  }
}

//$('#editTimeDate').on('show.bs.modal', function (event) {
//  areaIndex = selectedTimes.index;
//  timeIndex = selectedTimes.indexNum;
//  clearTimeDate();
//  updatetimeSow();
//  editTimeDateObj.type = $('#selectEditPoint').val();
//});

/*编辑时段时间html*/
function updatetimeSow() {
  $('#editTime1 .showTimes .col-md-4 p').eq(0).empty();
  $('#editTime1 .showTimes .col-md-4 p').eq(1).empty();
  $('#editTime1 .showTimes .col-md-4 p').eq(2).empty();
  $('#selectEditPoint').empty();
  var s, e;
  s = editTimeDateObj.s;
  e = editTimeDateObj.e;
  if (timeIndex == 0) {
    e = editTimeDateObj.afterE;
    $('#editTime1 .showTimes .col-md-4 p').eq(0)
      .html('<h4>无时段</h4>');
    $('#editTime1 .showTimes .col-md-4 p').eq(2)
      .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
    $('#selectEditPoint').append($('<option value="end">结束时间</option>'))
    //editTimeDateObj.type = 'end'
  } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
    s = editTimeDateObj.beforeS;
    $('#editTime1 .showTimes .col-md-4 p').eq(2)
      .html('<h4>无时段</h4>');
    $('#editTime1 .showTimes .col-md-4 p').eq(0)
      .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
    $('#selectEditPoint').append($('<option value="start">开始时间</option>'))
    //editTimeDateObj.type = 'start'
  } else {
    s = editTimeDateObj.beforeS;
    $('#selectEditPoint').append($('<option value="start">开始时间</option>'));
    $('#selectEditPoint').append($('<option value="end">结束时间</option>'));
    $('#editTime1 .showTimes .col-md-4 p').eq(0)
      .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
    $('#editTime1 .showTimes .col-md-4 p').eq(2)
      .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
    //editTimeDateObj.type = 'start'
  }
  $('#editTime1 .showTimes .col-md-4 p').eq(1).html(editTimeDateObj.s + '<br />至<br/>' + editTimeDateObj.e);

  initEditTimeDate(s, e);
  initEditTimeDate(s, e);
}

function editHtml(id){
  $('#'+ id +' .showTimes .col-md-4 p').eq(0).empty();
  $('#'+ id +' .showTimes .col-md-4 p').eq(1).empty();
  $('#'+ id +' .showTimes .col-md-4 p').eq(2).empty();
  $('#selectEditPoint').empty();
  if (timeIndex == 0) {
    $('#'+ id +' .showTimes .col-md-4 p').eq(0)
      .html('<h4>无时段</h4>');
    if(allData[areaIndex].timeItems.length == 1){
      $('#'+ id +' .showTimes .col-md-4 p').eq(2)
        .html('<h4>无时段</h4>');
    }else{
      $('#'+ id +' .showTimes .col-md-4 p').eq(2)
        .html(momentDate(allData[areaIndex].timeItems[timeIndex+1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex+1].timeEndDate));
    }
  } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
    $('#'+ id +' .showTimes .col-md-4 p').eq(2)
      .html('<h4>无时段</h4>');
    if(allData[areaIndex].timeItems.length == 1){
      $('#'+ id +' .showTimes .col-md-4 p').eq(0)
        .html('<h4>无时段</h4>');
    }else{
      $('#'+ id +' .showTimes .col-md-4 p').eq(0)
        .html(momentDate(allData[areaIndex].timeItems[timeIndex-1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex-1].timeEndDate));
    }
  } else {
    $('#'+ id +' .showTimes .col-md-4 p').eq(0)
      .html(momentDate(allData[areaIndex].timeItems[timeIndex-1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex-1].timeEndDate));
    $('#'+ id +' .showTimes .col-md-4 p').eq(2)
      .html(momentDate(allData[areaIndex].timeItems[timeIndex+1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex+1].timeEndDate));
    //editTimeDateObj.type = 'start'
  }
  $('#'+ id +' .showTimes .col-md-4 p').eq(1)
    .html(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate));
}

/*选择修改时段开始时间或结束时间*/
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

  /*初始化方法执行两遍，否则有问题*/
  initEditTimeDate(s, e);
  initEditTimeDate(s, e);
}

//function editTimeDate() {
//  $('#editTimeDate').modal('show');
//  window.setTimeout(function () {
//  }, 350)
//}

$('#editArea').on('show.bs.modal', function (event) {
  $.fn.zTree.init($("#adcodeTree"), zTreeSetting, zTreeData);
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  var nodes = treeObj.getNodesByParam("level", 1);
  //var a = ['11','12','1301'];
  var code = qjMsg.esCodeRange;
  for(var i=0;i<nodes.length;i++){
    var adcode = nodes[i].adcode;
    if((code.indexOf(adcode.substr(0,2)) == -1) && (code.indexOf(adcode.substr(0,4)) == -1)){
      treeObj.hideNodes(nodes[i].children);
    }
  }

  var button = $(event.relatedTarget);
  var create = button.data('new');
  var areaId, findUrl;
  var allUrl = '/area/find_areaAll';
  $('.adcodeList.mt20').empty();
  if (create) {
    $('#areaName').val('').removeAttr('data-id');
    showCode = [{}, {}, {}];
    $('.adcodeList.mt20').empty();
  } else {
    findUrl = '/area/get_areaList';
    areaId = button.parents('.areaTitle_con').attr('id');
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

  ajaxPost(allUrl, {
    scenarinoId: qjMsg.qjId,
    userId: userId,
    areaId: areaId
  }).success(function (res) {
    setDisabled(res.data);
  });

});

/*创建/编辑区域*/
function createEditArea() {
  var url = '/area/saveorupdate_area';
  var areaName = $('#areaName').val();
  if (!areaName) {
    alert('kong')
    return;
  }

  var obj = {
    missionId: qjMsg.rwId,
    scenarinoId: qjMsg.qjId,
    areaName: areaName,
    userId: userId,
    scenarinoStartDate: moment(momentDate(qjMsg.qjStartDate)).format('YYYY-MM-DD HH:mm:ss'),
    scenarinoEndDate: moment(momentDate(qjMsg.qjEndDate)).format('YYYY-MM-DD HH:mm:ss'),
    areaId: $('#areaName').attr('data-id') || '',
    provinceCodes: '',
    cityCodes: '',
    countyCodes: '',
    scenarinoStatus: qjMsg.scenarinoStatus
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

  if (pArr.length == 0 && ctArr.length == 0 && crArr.length == 0) {
    alert("请选择范围");
    return;
  }
  $('#editArea').modal('hide');
  $('#' + $('#areaName').attr('data-id')).find('button.btn-flash').removeClass('btn-flash');
  ajaxPost(url, obj).success(function (res) {

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
      obj.provinceCodes = pArr;
      obj.cityCodes = ctArr;
      obj.countyCodes = crArr;
      allData.push(obj);
      showTimeline(allData);
    } else {
      $('#' + $('#areaName').attr('data-id')).find('b').html(areaName);
    }
  })
}

/*显示已选择code,并进行checked*/
function setShowCode(data) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  proNum = data.provinceCodes.length;
  cityNum = data.cityCodes.length;
  countyNum = data.countyCodes.length;

  if (proNum == 0) {
    showCode[0] = {};
  } else {
    for (var i = 0; i < proNum; i++) {
      $.extend(showCode[0], data.provinceCodes[i]);

      var node = treeObj.getNodeByParam("adcode", Object.keys(data.provinceCodes[i]), null);
      treeObj.checkNode(node, true, true, false);
    }
  }
  if (cityNum == 0) {
    showCode[1] = {};
  } else {
    for (var ii = 0; ii < cityNum; ii++) {
      var adcode1 = Object.keys(data.cityCodes[ii])[0];
      if (!showCode[1][adcode1.substr(0, 2) + '0000'])showCode[1][adcode1.substr(0, 2) + '0000'] = {};
      $.extend(showCode[1][adcode1.substr(0, 2) + '0000'], data.cityCodes[ii]);

      var node = treeObj.getNodeByParam("adcode", Object.keys(data.cityCodes[ii]), null);
      treeObj.checkNode(node, true, true, false);
    }
  }
  if (countyNum == 0) {
    showCode[2] = {};
  } else {
    for (var iii = 0; iii < countyNum; iii++) {
      var adcode2 = Object.keys(data.countyCodes[iii])[0];
      if (!showCode[2][adcode2.substr(0, 4) + '00'])showCode[2][adcode2.substr(0, 4) + '00'] = {};
      $.extend(showCode[2][adcode2.substr(0, 4) + '00'], data.countyCodes[iii]);

      var node = treeObj.getNodeByParam("adcode", Object.keys(data.countyCodes[iii]), null);
      treeObj.checkNode(node, true, true, false);
    }
  }
}

$('#qyTime').on('show.bs.modal', function (event) {
  var button = $(event.relatedTarget);
  if (button.length == 0) {
    areaIndex = selectedTimes.timeIndex;
    return
  }
  ;
  areaIndex = $('.areaTitle_con').index(button.parents('.areaTitle_con')) || selectedTimes.index;
});

$('#addYA').on('show.bs.modal', function (event) {


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
  msg.content.cityCodes = allData[areaIndex].cityCodes;
  msg.content.countyCodes = allData[areaIndex].countyCodes;
  msg.content.provinceCodes = allData[areaIndex].provinceCodes;
});

$('#delTime').on('show.bs.modal', function (event) {
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
});

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
    //treeObj.checkNode(node, false, false, false);
    var child = node.children;
    for (var ch = 0; ch < child.length; ch++) {
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
//    treeObj.checkNode(node, false, false, false);
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
  delete showCode[node.level][node.adcode];
  delete showCode[node.level + 1][node.adcode];
  var ad = node.adcode.substr(0, 2);
  var show = showCode[2];
  for (var i in show) {
    if (i.substr(0, 2) == ad) {
      delete showCode[2][i];
    }
  }
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
      if (!child[n].chkDisabled) {
        showCode[node.level][parNode.adcode][child[n].adcode] = child[n].name;
      }
    }
  }
  delete showCode[node.level][parNode.adcode][node.adcode];
  if ($.isEmptyObject(showCode[node.level][parNode.adcode])) {
    delete showCode[node.level][parNode.adcode];
  }
}

function initDate(s,e,start) {
  $("#qyTimePoint").daterangepicker({


    timePicker: true,
    format: 'YYYY-MM-DD HH',
    todayHighlight: false,
    singleDatePicker: true,
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true,
    timePicker24Hour:true,
    startDate: s.format('YYYY-MM-DD HH'),
    //endDate: e.format('YYYY-MM-DD HH'),
    minDate:s.format('YYYY-MM-DD HH'),//最早可选日期
    maxDate: e.format('YYYY-MM-DD HH'),//最大可选日期
    locale: {
      format: "YYYY-MM-DD HH",
      //weekLabel: "W",
      applyLabel: "确定", //按钮文字
      cancelLabel: "取消",//按钮文字
      daysOfWeek: [
        "日", "一", "二", "三", "四", "五", "六"
      ],
      monthNames: [
        "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
      ],
      //firstDay: 1
    },
  })
    .on('changeDate', function (ev) {
      var date = moment(ev.date).format('YYYY-MM-DD HH');
    });
}

/*前端设置disabled*/
function setDisabled(data) {
  var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
  for (var i = 0; i < data.length; i++) {
    var name = '(' + data[i].areaName + ')';
    for (var pro = 0; pro < data[i].provinceCodes.length; pro++) {
      var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].provinceCodes[pro]), null);
      node.name += name;
      //node.chkDisabled = true;
      treeObj.setChkDisabled(node, true, false, true);
      treeObj.updateNode(node);
    }

    for (var ci = 0; ci < data[i].cityCodes.length; ci++) {
      var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].cityCodes[ci]), null);
      node.name += name;
      //node.chkDisabled = true;
      treeObj.setChkDisabled(node, true, false, true);
      treeObj.updateNode(node);
    }

    for (var ct = 0; ct < data[i].countyCodes.length; ct++) {
      var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].countyCodes[ct]), null);
      node.name += name;
      //node.chkDisabled = true;
      treeObj.setChkDisabled(node, true, false, true);
      treeObj.updateNode(node);
    }
  }
}

var selectCopyQJ, statusRW = '';
/*初始化复制情景table*/
function initCoptTable() {
  $('#copyQJ').bootstrapTable({
    method: 'POST',
    //url: 'webApp/task01/rw.json',
//      url : BackstageIP+'/mission/get_mission_list',
    url: '/ampc/scenarino/get_CopyScenarinoList',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#rwToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height: 453,
    maintainSelected: true,
    clickToSelect: false,// 点击选中行
    pagination: true, // 在表格底部显示分页工具栏
    pageSize: 10, // 页面大小
    pageNumber: 1, // 页数
    pageList: [10],
    striped: true, // 使表格带有条纹
    sidePagination: "server",// 表格分页的位置 client||server
    rowStyle: function (row, index) {
      if (index == 0) {
        return {classes: 'info'}
      }
      return {};
    },
    queryParams: function formPm(m) {
      var json = {
        "token": "",
        "data": {
          "queryName": m.searchText || '',
          "missionStatus": statusRW,
          "pageNum": m.pageNumber,
          "pageSize": m.pageSize,
          "sort": '',
          "userId": 1
        }
      };

      return JSON.stringify(json);
    },
    responseHandler: function (res) {
      return res.data
    },
    queryParamsType: "undefined", // 参数格式,发送标准的RESTFul类型的参数请求
    silent: true, // 刷新事件必须设置
    onClickRow: function (row, $element) {
      $('.info').removeClass('info');
      $($element).addClass('info');
      selectCopyQJ = row;
    },
    onLoadSuccess: function (data) {
      selectCopyQJ = data.rows[0];
    }
  });
}

/*筛选*/
function statusRWfun(status, t) {
  statusRW = status;
  search('rw');
}

/*搜索事件*/
function search() {
  var params = $('#copyQJ').bootstrapTable('getOptions');
  params.queryParams = function (params) {
    var json;
    json = {
      "token": "",
      "data": {
        "queryName": params.searchText || '',
        "pageNum": 1,
        "pageSize": params.pageSize,
        "missionStatus": statusRW,
        "sort": '',
        "userId": userId
      }
    };
    json.data.queryName = $('.copyQjsearch').val();

    params = JSON.stringify(json);
    return params;
  };
  $('#copyQJ').bootstrapTable('refresh', params);
}

function rwType(v, row, i) {
  var type;
  switch (row.missionStatus) {
    case '1':
      type = '预评估';
      break;
    case '2':
      type = '后评估';
      break;
  }
  return type
}

function selectCopy(t) {
  if (t) {
    initCoptTable();
    $('#selectCreateQj .selectCQJbtn').addClass('disNone');
    $('#selectCreateQj .selectCopyQj').removeClass('disNone');
    $('#selectCreateQj .modal-footer').removeClass('disNone');
  } else {
    allData = allData1;
    allData1 = null;
    for (var i = 0; i < allData.length; i++) {
      allData[i].timeFrame = [];
      var timeItems = allData[i].timeItems;
      var tLength = timeItems.length;
      //$('.areaMsg').append(area);
      for (var item = 0; item < tLength; item++) {

        if (item > 0) {
          var sD = timeItems[item].timeStartDate;
          allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
        }
      }

    }
    showTimeline(allData);
    app2();
  }

}

function previous() {
  $('#selectCreateQj .selectCQJbtn').removeClass('disNone');
  $('#selectCreateQj .selectCopyQj').addClass('disNone');
  $('#selectCreateQj .modal-footer').addClass('disNone');
}

function subCopyQJ() {
  console.log(selectCopyQJ);

  var copyUrl = '/scenarino/copy_Scenarino';
  ajaxPost(copyUrl, {
    scenarinoId: qjMsg.qjId,
    userId: userId,
    copyscenarinoId: selectCopyQJ.scenarinoId
  }).success(function (res) {
    if (res.status == 0) {
      var url = '/area/get_areaAndTimeList';
      ajaxPost(url, {
        scenarinoId: qjMsg.qjId,
        userId: userId
      }).success(function (res) {
        if (res.status == 0) {
          allData = res.data.slice(0, -1);
          for (var i = 0; i < allData.length; i++) {
            allData[i].timeFrame = [];
            var timeItems = allData[i].timeItems;
            var tLength = timeItems.length;
            //$('.areaMsg').append(area);
            for (var item = 0; item < tLength; item++) {

              if (item > 0) {
                var sD = timeItems[item].timeStartDate;
                allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
              }
            }
          }
          showTimeline(allData);
          app2();
        } else {
          console.log('get_areaAndTimeList 接口异常')
        }

      });
    } else {
      console.log('copy_Scenarino 接口异常')
    }
  })
}

/*显示所选code及地图展示*/
function showAllCode() {
  updataCodeList();
  showMap();
}

/*减排计算按钮*/
function jpjsBtn() {
  var url = '/jp/areajp';
  var params = {
    scenarinoId: qjMsg.qjId,
    areaAndPlanIds: {},
    userId: userId
  }
  for (var i = 0; i < allData.length; i++) {
    var planArr = [];
    var times = allData[i].timeItems;
    for (var p = 0; p < times.length; p++) {
      if (times[p].planId != -1) {
        planArr.push(times[p].planId)
      }
    }
    if (planArr.length > 0) {
      params.areaAndPlanIds[allData[i].areaId] = planArr
    }
  }

  if (Object.keys(params.areaAndPlanIds).length > 0) {
    ajaxPost(url, params).success(function (res) {
      console.log(res);
      if (res.status == 0) {
        if (res.data == 1) {
          $('.jpjs').addClass('disNone');
          $('.jpztck.disNone').removeClass('disNone');

          scenarinoType(3);
          qjMsg.scenarinoStatus = 3;
        } else {
          console.log('计算异常')
        }

        /*这里控制所有禁止操作*/

      } else {
        console.log('接口异常')
      }
    })
  }
}

/*减排状态查看*/
function jpztckBtn() {
  var url = '/jp/areaStatusJp';
  var params = {
    scenarinoId: qjMsg.qjId,
    areaAndPlanIds: {},
    userId: userId
  }
  for (var i = 0; i < allData.length; i++) {
    var planArr = [];
    var times = allData[i].timeItems;
    for (var p = 0; p < times.length; p++) {
      if (times[p].planId != -1) {
        planArr.push(times[p].planId)
      }
    }
    if (planArr.length > 0) {
      params.areaAndPlanIds[allData[i].areaId] = planArr
    }
  }
  if (Object.keys(params.areaAndPlanIds).length > 0) {
    ajaxPost(url, params).success(function (res) {

      if (res.status == 0) {
        if (res.data.type == 0) {
          var jsjd = res.data.percent * 100 + '%';
          var yys = moment(res.data.time * 1000).subtract(8, 'h').format('HH时mm分ss秒');
          var sysj = moment((res.data.time / res.data.percent - res.data.time) * 1000).subtract(8, 'h').format('HH时mm分ss秒');

          $('.jsjd').empty().html(jsjd);
          $('.yys').empty().html(yys);
          $('.sysj').empty().html(sysj);

          if (res.data.percent == 1) {
            var url = '/scenarino/find_Scenarino_status';
            ajaxPost(url, {
              userId: userId,
              scenarinoId: qjMsg.qjId
            }).success(function (res) {
              qjMsg.scenarinoStatus = res.data.scenarinoStatus;
              scenarinoType(qjMsg.scenarinoStatus);
            })
          }
        } else if (res.data.type == 1) {
          console.log('重新计算中！！！')
        } else {
          console.log('计算接口异常')
        }
      } else {
        console.log('接口故障')
      }

    })
  }
}

$('#jpzt').on('show.bs.modal', function (event) {
  jpztckBtn();
})

/*减排分析按钮*/
function jumpJpfx() {
  var msg1 = {
    'id': 'jpfxMessage',
    'content': {}
  };
  msg1.content.rwId = qjMsg.rwId;
  msg1.content.rwName = qjMsg.rwName;
  msg1.content.qjId = qjMsg.qjId;
  msg1.content.qjName = qjMsg.qjName;
  vipspa.setMessage(msg);

  var a = document.createElement('a');
  a.href = '#/rwgl_reductAnalys';
  a.click();
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
    "esri/tasks/query",
    "esri/geometry/Extent",
    "dojo/domReady!"

  ],
  function (Map, FeatureLayer, GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, ClassBreaksRenderer, Point, Extent, SimpleRenderer,
            Graphic, Color, style, FeatureSet, SpatialReference, gaodeLayer, InfoTemplate, UniqueValueRenderer, query, Extent) {
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
    dong.Query = query;
    dong.Extent = Extent;
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
  app.featureLayer1 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/2", {
//	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
    mode: dong.FeatureLayer.MODE_ONDEMAND,
    outFields: ["NAME"]
  });
  app.featureLayer2 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/1", {
//	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
    mode: dong.FeatureLayer.MODE_ONDEMAND,
    outFields: ["NAME"]
  });
  app.featureLayer3 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/0", {
//	 infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
    mode: dong.FeatureLayer.MODE_ONDEMAND,
    outFields: ["NAME"]
  });
  app.map.addLayer(app.featureLayer1);
  app.map.addLayer(app.featureLayer2);
  app.map.addLayer(app.featureLayer3);

  area(data);
}
//行政区划渲染
function area(data) {
  var defaultSymbol = new dong.SimpleFillSymbol().setStyle(dong.SimpleFillSymbol.STYLE_NULL);
  defaultSymbol.outline.setStyle(dong.SimpleLineSymbol.STYLE_NULL);
  for (var i = 0; i < 3; i++) {
    var t1 = "";
    var str = "ADMINCODE";
    if (i == 1) {
      str = "CITYCODE";
    }
    var renderer = new dong.UniqueValueRenderer(defaultSymbol, str);
    if (i == 0) {
      for (var prop in data[i]) {
        if (data[i].hasOwnProperty(prop)) {
          renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color([221, 160, 221, 0.5])));
          t1 += "'" + prop + "',";
        }
      }

    } else {
      for (var prop in data[i]) {
        if (data[i].hasOwnProperty(prop)) {
          for (var prop1 in data[i][prop]) {
            renderer.addValue(prop1, new dong.SimpleFillSymbol().setColor(new dong.Color([221, 160, 221, 0.5])));
            t1 += "'" + prop + "',";
          }
        }
      }
    }
    if (i == "0") {
      app.featureLayer1.setRenderer(renderer);
      app.map.addLayer(app.featureLayer1);
      dingwei1(t1, "1");
    } else if (i == "1") {
      app.featureLayer2.setRenderer(renderer);
      app.map.addLayer(app.featureLayer2);
      dingwei1(t1, "2");
    } else if (i == "2") {
      app.featureLayer3.setRenderer(renderer);
      app.map.addLayer(app.featureLayer3);
      dingwei1(t1, "3");
    }
  }
}

var xmax1 = 0, xmin1 = 0, ymax1 = 0, ymin1 = 0;
var jj = 0;
//模态框中地图定位
function dingwei1(tiaojian, type) {
  if (tiaojian == "" || tiaojian == null || tiaojian == undefined) {
    return;
  }
  var query = new dong.Query();
  if (type == "1") {
    query.where = "ADMINCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";
    app.featureLayer1.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (jj == 0) {
          xmax1 = graphic.geometry.getExtent().xmax;
          xmin1 = graphic.geometry.getExtent().xmin;
          ymax1 = graphic.geometry.getExtent().ymax;
          ymin1 = graphic.geometry.getExtent().ymin;
          jj++;
        } else {
          xmin1 = graphic.geometry.getExtent().xmin < xmin1 ? graphic.geometry.getExtent().xmin : xmin1;
          xmax1 = graphic.geometry.getExtent().xmax > xmax1 ? graphic.geometry.getExtent().xmax : xmax1;
          ymin1 = graphic.geometry.getExtent().ymin < ymin1 ? graphic.geometry.getExtent().ymin : ymin1;
          ymax1 = graphic.geometry.getExtent().ymax > ymax1 ? graphic.geometry.getExtent().ymax : ymax1;
        }
      }
      var extent = new dong.Extent(xmin1, ymin1, xmax1, ymax1, new dong.SpatialReference({wkid: 3857}));
      app.map.setExtent(extent.expand(1.5));
    });
  } else if (type == "2") {
    query.where = "CITYCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";
    app.featureLayer2.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (jj == 0) {
          xmax1 = graphic.geometry.getExtent().xmax;
          xmin1 = graphic.geometry.getExtent().xmin;
          ymax1 = graphic.geometry.getExtent().ymax;
          ymin1 = graphic.geometry.getExtent().ymin;
          jj++;
        } else {
          xmin1 = graphic.geometry.getExtent().xmin < xmin1 ? graphic.geometry.getExtent().xmin : xmin1;
          xmax1 = graphic.geometry.getExtent().xmax > xmax1 ? graphic.geometry.getExtent().xmax : xmax1;
          ymin1 = graphic.geometry.getExtent().ymin < ymin1 ? graphic.geometry.getExtent().ymin : ymin1;
          ymax1 = graphic.geometry.getExtent().ymax > ymax1 ? graphic.geometry.getExtent().ymax : ymax1;
        }
      }
      var extent = new dong.Extent(xmin1, ymin1, xmax1, ymax1, new dong.SpatialReference({wkid: 3857}));
      app.map.setExtent(extent.expand(1.5));
    });
  } else if (type == "3") {
    query.where = "ADMINCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";
    app.featureLayer3.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (jj == 0) {
          xmax1 = graphic.geometry.getExtent().xmax;
          xmin1 = graphic.geometry.getExtent().xmin;
          ymax1 = graphic.geometry.getExtent().ymax;
          ymin1 = graphic.geometry.getExtent().ymin;
          jj++;
        } else {
          xmin1 = graphic.geometry.getExtent().xmin < xmin1 ? graphic.geometry.getExtent().xmin : xmin1;
          xmax1 = graphic.geometry.getExtent().xmax > xmax1 ? graphic.geometry.getExtent().xmax : xmax1;
          ymin1 = graphic.geometry.getExtent().ymin < ymin1 ? graphic.geometry.getExtent().ymin : ymin1;
          ymax1 = graphic.geometry.getExtent().ymax > ymax1 ? graphic.geometry.getExtent().ymax : ymax1;
        }
      }
      var extent = new dong.Extent(xmin1, ymin1, xmax1, ymax1, new dong.SpatialReference({wkid: 3857}));
      app.map.setExtent(extent.expand(1.5));
    });
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

  $('.jpjs').attr('disabled', true);
  if (qjMsg.scenarinoStatus == 2) {
    for (var i = 0; i < data.length; i++) {
      for (var m = 0; m < data[i].timeItems.length; m++) {
        if (data[i].timeItems[m].planId != -1) {
          $('.jpjs').removeAttr('disabled');
          break;
        }
      }
    }
  }
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
  if (app.map1 == "" || app.map1 == null || app.map1 == undefined) {
    return;
  }
  if (allData != "" && allData != null && allData != undefined) {
    var defaultSymbol = new dong.SimpleFillSymbol().setStyle(dong.SimpleFillSymbol.STYLE_NULL);
    defaultSymbol.outline.setStyle(dong.SimpleLineSymbol.STYLE_NULL);

    app.fea1 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/2", {//添加省的图层
//			infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
      mode: dong.FeatureLayer.MODE_ONDEMAND,
      outFields: ["NAME"]
    });
    app.fea2 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/1", {//市的图层
//			infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
      mode: dong.FeatureLayer.MODE_ONDEMAND,
      outFields: ["NAME"]
    });
    app.fea3 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/cms/MapServer/0", {//区县的图层
//			infoTemplate: new dong.InfoTemplate("&nbsp;", "${NAME}"),
      mode: dong.FeatureLayer.MODE_ONDEMAND,
      outFields: ["NAME"]
    });
    var t1 = "", t2 = "", t3 = "";

    $.each(allData, function (k, item) {
      var str = sz_corlor[k];
      if (item.provinceCodes != "" && item.provinceCodes != null && item.provinceCodes != undefined) {
        if (item.provinceCodes.length > 0) {//市
          var renderer = new dong.UniqueValueRenderer(defaultSymbol, "ADMINCODE");
          for (var i = 0; i < item.provinceCodes.length; i++) {//省
            for (var prop in item.provinceCodes[i]) {
              if (item.provinceCodes[i].hasOwnProperty(prop)) {
                renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
                t1 += "'" + prop + "',";
              }
            }
            app.fea1.setRenderer(renderer);
            app.map1.addLayer(app.fea1);
          }
          dingwei(t1, "1");
        }
      }
      if (item.cityCodes != "" && item.cityCodes != null && item.cityCodes != undefined) {//市
        if (item.cityCodes.length > 0) {
          var renderer = new dong.UniqueValueRenderer(defaultSymbol, "ADMINCODE");
          for (var i = 0; i < item.cityCodes.length; i++) {
            for (var prop in item.cityCodes[i]) {
              if (item.cityCodes[i].hasOwnProperty(prop)) {
                renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
                t2 += "'" + prop + "',";
              }
            }
            app.fea2.setRenderer(renderer);
            app.map1.addLayer(app.fea2);
          }
          dingwei(t2, "2");
        }
      }
      if (item.countyCodes != "" && item.countyCodes != null && item.countyCodes != undefined) {//区县
        var tiaojian = "";
        if (item.countyCodes.length > 0) {
          var renderer = new dong.UniqueValueRenderer(defaultSymbol, "ADMINCODE");
          for (var i = 0; i < item.countyCodes.length; i++) {
            for (var prop in item.countyCodes[i]) {
              if (item.countyCodes[i].hasOwnProperty(prop)) {
                renderer.addValue(prop, new dong.SimpleFillSymbol().setColor(new dong.Color(str)));
                t3 += "'" + prop + "',";
              }
            }
            app.fea3.setRenderer(renderer);
            app.map1.addLayer(app.fea3);
          }
          dingwei(t3, "3");
        }
      }
    });
  }
}
var xmax = 0, xmin = 0, ymax = 0, ymin = 0;
var kk = 0;
function dingwei(tiaojian, type) {
  kk = 0;
  var query = new dong.Query();
  if (type == "1") {
    query.where = "ADMINCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";

    app.fea1.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (kk == 0) {
          xmax = graphic.geometry.getExtent().xmax;
          xmin = graphic.geometry.getExtent().xmin;
          ymax = graphic.geometry.getExtent().ymax;
          ymin = graphic.geometry.getExtent().ymin;
          kk++;
        } else {
          xmin = graphic.geometry.getExtent().xmin < xmin ? graphic.geometry.getExtent().xmin : xmin;
          xmax = graphic.geometry.getExtent().xmax > xmax ? graphic.geometry.getExtent().xmax : xmax;
          ymin = graphic.geometry.getExtent().ymin < ymin ? graphic.geometry.getExtent().ymin : ymin;
          ymax = graphic.geometry.getExtent().ymax > ymax ? graphic.geometry.getExtent().ymax : ymax;
        }
      }
      var extent = new dong.Extent(xmin, ymin, xmax, ymax, new dong.SpatialReference({wkid: 3857}));
      app.map1.setExtent(extent.expand(1.5));
    });
  } else if (type == "2") {
    query.where = "ADMINCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";
    app.fea2.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (kk == 0) {
          xmax = graphic.geometry.getExtent().xmax;
          xmin = graphic.geometry.getExtent().xmin;
          ymax = graphic.geometry.getExtent().ymax;
          ymin = graphic.geometry.getExtent().ymin;
          kk++;
        } else {
          xmin = graphic.geometry.getExtent().xmin < xmin ? graphic.geometry.getExtent().xmin : xmin;
          xmax = graphic.geometry.getExtent().xmax > xmax ? graphic.geometry.getExtent().xmax : xmax;
          ymin = graphic.geometry.getExtent().ymin < ymin ? graphic.geometry.getExtent().ymin : ymin;
          ymax = graphic.geometry.getExtent().ymax > ymax ? graphic.geometry.getExtent().ymax : ymax;
        }
      }
      var extent = new dong.Extent(xmin, ymin, xmax, ymax, new dong.SpatialReference({wkid: 3857}));
      app.map1.setExtent(extent.expand(1.5));
    });
  } else if (type == "3") {
    query.where = "ADMINCODE IN (" + tiaojian.substring(0, tiaojian.length - 1) + ")";
    app.fea3.queryFeatures(query, function (featureSet) {
      for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (kk == 0) {
          xmax = graphic.geometry.getExtent().xmax;
          xmin = graphic.geometry.getExtent().xmin;
          ymax = graphic.geometry.getExtent().ymax;
          ymin = graphic.geometry.getExtent().ymin;
          kk++;
        } else {
          xmin = graphic.geometry.getExtent().xmin < xmin ? graphic.geometry.getExtent().xmin : xmin;
          xmax = graphic.geometry.getExtent().xmax > xmax ? graphic.geometry.getExtent().xmax : xmax;
          ymin = graphic.geometry.getExtent().ymin < ymin ? graphic.geometry.getExtent().ymin : ymin;
          ymax = graphic.geometry.getExtent().ymax > ymax ? graphic.geometry.getExtent().ymax : ymax;
        }
      }
      var extent = new dong.Extent(xmin, ymin, xmax, ymax, new dong.SpatialReference({wkid: 3857}));
      app.map1.setExtent(extent.expand(1.5));
    });
  }
}










