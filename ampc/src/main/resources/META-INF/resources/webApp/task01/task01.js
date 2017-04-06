/**
 * Created by lvcheng on 2017/2/21.
 */
//var userId = 1;
var formCreate;
var selectRW = {};
var statusRW = '';
var delRWid = {}, delQJid = {};
var rwStartDate;
var rwEndDate;
var rwSelectType;
var basisArr;
var qjType;
var selectEndDate;
var __dsp = {};//用于存储请求信息，做Promise存储
//var parameterPar = {total: '', data: {}};
var msg = {
  'id': 'qjMessage',
  'content': {
    rwId: '',
    rwName: '',
    qjId: '',
    qjName: '',
    qjStartDate: '',
    qjEndDate: '',
    esCouplingId: '',
    esCouplingName: ''
  }
};

$(window).resize(function (e) {
  if ($(window).width() < 1370) {
    $('.cjsc').removeClass('col-md-4').addClass('col-md-6');
    $('.smallP').css('display', 'block');
    $('.bigP').css('display', 'none');
  } else {
    $('.smallP').css('display', 'none');
    $('.bigP').css('display', 'block');
    $('.cjsc').removeClass('col-md-6').addClass('col-md-4')

  }
});

function formVerify() {

  $.validator.addMethod("dateV", function (value, element) {
    var startD = value.substring(0, value.indexOf('至'));
    var endD = value.substring(value.indexOf('至') + 1);

    if(rwTypeV == 'y'){
      var s = moment(startD).isBefore(moment().add(-1,'d'));
      var e = moment(endD).isAfter(moment());
      return s && e;
    }else{
      return true
    }
  }, "请输入正确日期范围");




  $("#rwForm").validate({
    debug: true,
    rules: {
      rwName: {
        required: true,
        maxlength: 15
      },
      rwStartDate: {
        required: true,
        dateV: true
      },
    },
    messages: {
      rwName: {
        required: '请填写任务名称',
        maxlength: '不可超过15个字符'
      },
      rwStartDate: {
        required: '请填写日期'
      },
    }

  });

  $("#yqjForm").validate({
    debug: true,
    rules: {
      yName: {
        required: true,
        maxlength: 15
      }
    },
    messages: {
      yName: {
        required: '请填写情景名称',
        maxlength: '不可超过15个字符'
      }
    }

  });

  $("#hqjForm").validate({
    debug: true,
    rules: {
      hName: {
        required: true,
        maxlength: 15
      }
    },
    messages: {
      hName: {
        required: '请填写情景名称',
        maxlength: '不可超过15个字符'
      }
    }

  });
}


$(document).ready(function () {
  initialize();
  //initDate();
  //initRwDate();
  formVerify();
});

function initialize() {
  $('.qyCon').addClass('disNone');
  /**
   * 设置导航条菜单
   */
  $("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>');


  initRwTable();
  getMnfw();
  getQD();
//  var param = vipspa.getMessage('home_msg');
}

var QJheight;
function initRwTable() {
  $('#rwTable').bootstrapTable({
    method: 'POST',
    url: '/ampc/mission/get_mission_list',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#rwToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    //height:453,
    maintainSelected: true,
    clickToSelect: false,// 点击选中行
    pagination: true, // 在表格底部显示分页工具栏
    pageSize: 9, // 页面大小
    pageNumber: 1, // 页数
    pageList: [9],
    striped: true, // 使表格带有条纹
    sidePagination: "server",// 表格分页的位置 client||server
    rowStyle: function (row, index) {
      if (index == 0) {
        return {
          classes: 'info'
        };
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
      $('.qj').val('');
      selectRW = row;
      $('#qjTable').bootstrapTable('destroy');
      initQjTable();
      $('.info').removeClass('info');
      $($element).addClass('info');
    },
    /*复选框设置*/
    onCheck: function (row) {
      $('.rwDel').attr('disabled', false);
      delRWid[row.missionId] = 'true';
    },
    onUncheck: function (row) {
      delete delRWid[row.missionId];
      if ($.isEmptyObject(delRWid)) {
        $('.rwDel').attr('disabled', true)
      }
    },
    onCheckAll: function (rows) {
      $('.rwDel').attr('disabled', false);
      for (var i = 0; i < rows.length; i++) {
        delRWid[rows[i].missionId] = 'true';
      }
    },
    onUncheckAll: function (rows) {
      delRWid = {};
      $('.rwDel').attr('disabled', true);
    },
    onLoadSuccess: function (data) {
      selectRW = data.rows[0];
      QJheight = data.rows.length * 69 + 98;
      $('.qjtableDiv').css('background-color', '#d9edf7');
      //QJheight = $('.rwtableDiv').height();

      if (QJheight < 600) {
        QJheight = 600;
        $('#rwTable').bootstrapTable('resetView', {height: 600})
      } else {
        $('#rwTable').bootstrapTable('resetView', {height: QJheight})
      }

      //QJheight = QJheight>400?QJheight:400;
      if (data.rows.length == 0) {
        $('#qjTable').bootstrapTable('resetView', {height: 600});
        return;
      }
      $('#qjTable').bootstrapTable('destroy');
      initQjTable();
    },
    /*右键菜单*/
    contextMenu: '#RWcontext-menu', //右键菜单ID
    onContextMenuItem: function (row, $el) {
      if ($el.data("item") == "rename") {
        rename('rw', row.missionId);
      }
    }
  });
}

/*筛选*/
function statusRWfun(status, t) {
	$('.btn-success.btn-danger').removeClass('btn-danger');
  $(t).addClass('btn-danger');
  $('.seeName').html($(t).children('a').html());
  statusRW = status;
  search('rw');
}

/*搜索事件*/
function search(type) {
  var params = $('#' + type + 'Table').bootstrapTable('getOptions');
  params.queryParams = function (params) {
    var json;
    if (type == 'rw') {
      json = {
        "token": "",
        "data": {
          "queryName": params.searchText || '',
          "pageNum": params.pageNumber,
          "pageSize": params.pageSize,
          "missionStatus": statusRW,
          "sort": '',
          "userId": 1
        }
      };
      json.data.queryName = $('.rw').val();
    } else {
      json = {
        "token": "",
        "data": {
          "missionId": selectRW.missionId,
          "queryName": params.searchText || '',
//            "pageNum": params.pageNumber,
//            "pageSize": params.pageSize,
          "sort": '',
          "userId": 1
        }
      };
      json.data.queryName = $('.qj').val();
    }
//      json.data.queryName = $('.' + type).val();
    params = JSON.stringify(json);
    //console.info(params);
    return params;
  };
  $('#' + type + 'Table').bootstrapTable('refresh', params);
}


function initQjTable() {
  $('#qjTable').bootstrapTable({
    method: 'POST',
    url: '/ampc/scenarino/get_scenarinoListBymissionId',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#qjToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height: QJheight - 57,
    maintainSelected: true,
    clickToSelect: false,// 点击选中行
    pagination: false, // 在表格底部显示分页工具栏
    onlyInfoPagination: true,
    pageSize: 9, // 页面大小
    pageNumber: 1, // 页数
    pageList: [9],
    striped: false, // 使表格带有条纹
    sidePagination: "server",// 表格分页的位置 client||server
    rowStyle: function (row, index) {
      return {
        classes: 'info'
      };
    },
    queryParams: function formPm(m) {
      var json = {
        "token": "",
        "data": {
          "missionId": selectRW.missionId,
          "queryName": '',
//            "pageNum": m.pageNumber,
//            "pageSize": m.pageSize,
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
      msg.content.rwId = selectRW.missionId;
      msg.content.rwName = selectRW.missionName;
      msg.content.rwType = selectRW.missionStatus;
      msg.content.qjName = row.scenarinoName;
      msg.content.qjId = row.scenarinoId;
      msg.content.qjStartDate = row.scenarinoStartDate;
      msg.content.qjEndDate = row.scenarinoEndDate;
      msg.content.esCouplingId = selectRW.esCouplingId;
      msg.content.esCouplingName = selectRW.esCouplingName;
      msg.content.esCodeRange = selectRW.esCodeRange.split(',');
      msg.content.scenarinoStatus = row.scenarinoStatus;
      msg.content.scenarinoStatuName = row.scenarinoStatuName;
      msg.content.SCEN_TYPE = row.SCEN_TYPE;
      vipspa.setMessage(msg);
      console.log(row)
    },
    /*复选框设置*/
    onCheck: function (row) {
      $('.qjDel').attr('disabled', false);
      delQJid[row.scenarinoId] = 'true';
    },
    onUncheck: function (row) {
      delete delQJid[row.scenarinoId];
      if ($.isEmptyObject(delQJid)) {
        $('.qjDel').attr('disabled', true)
      }
    },
    onCheckAll: function (rows) {
      $('.qjDel').attr('disabled', false);
      for (var i = 0; i < rows.length; i++) {
        delQJid[rows[i].scenarinoId] = 'true';
      }
    },
    onUncheckAll: function (rows) {
      delQJid = {};
      $('.qjDel').attr('disabled', true);
    },


    /*右键菜单*/
    contextMenu: '#QJcontext-menu',
    onContextMenuItem: function (row, $el) {
      if ($el.data("item") == "rename") {
        rename('qj', row.scenarinoId);
      }
    }
  });
}

/*format 函数*/
function rwName(v, row, i) {
  return '<h3><a href="javascript:">' + row.missionName + '</a></h3><a style="font-size:12px; color:#a1a1a1;">创建时间：' + moment(row.missionAddTime).format('YYYY-MM-DD HH') + '</a>'
}

function rwType(v, row, i) {
  var type;
  switch(row.missionStatus){
    case '1':
      type = '预评估';
      break;
    case '2':
      type = '后评估';
      break;
  }
  return type
}

function rwDomain(v, row, i) {
  return row.v1 + '<br><a style="font-size:12px; color:#a1a1a1;">起止日期：' + moment(row.missionStartDate).format('YYYY-MM-DD') + ' - ' + moment(row.missionEndDate).format('YYYY-MM-DD') + '</a>'
}

function qjName(v, row, i) {
  var name = row.SCEN_TYPE == 3? '<h3><a>' + row.scenarinoName + '</a></h3>':'<h3><a href="#/yabj">' + row.scenarinoName + '</a></h3>';

  return  name+
    '<a style="font-size:12px; color:#a1a1a1;">创建时间：' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH') + '</a><br/>' +
    '<a style="font-size:12px; color:#a1a1a1;">起止日期：' + moment(row.scenarinoStartDate).format('YYYY-MM-DD') + ' 至 ' + moment(row.scenarinoEndDate).format('YYYY-MM-DD') + '</a>'
}

function qjType(v, row, i) {
  var type;
  switch(row.SCEN_TYPE){
    case '1':
      type = '预评估情景';
      break;
    case '2':
      type = '后评估情景';
      break;
    case '3':
      type = '新基准情景';
      break;
  }
  return type
}

function qjStatus(v, row, i){
  if(row.scenarinoStatus == 3 || row.scenarinoStatus == 6){
    return '<a href="javascript:" class="statusType">'+ row.scenarinoStatuName +'</a>'
  }else{
    return row.scenarinoStatuName
  }
}

function qjOrder(v, row, i) {
  return '<button class="btn btn-success mb10 mr10" data-toggle="modal" data-target="#startUp">启动</button>' +
    '<button class="btn btn-success mb10">终止</button>' +
    '<br/>' +
    '<button class="btn btn-success mr10">续跑</button>' +
    '<button class="btn btn-success">暂停</button>'
}

//function qjEffectEvaluation(v, row, i) {
//  return '<button class="btn btn-success mb10 mr10">效果评估</button>'
//}


/*delete 函数*/
function deleteFun(type) {
  var params = {userId: userId};
  var delList = '', url;
  if (type == 'rw') {
    for (var i in delRWid) {
      delList += i + ',';
    }
    delList = delList.substr(0, delList.length - 1);
    params.missionIds = delList;
    url = '/mission/delete_mission';
  } else {
    for (var ii in delQJid) {
      delList += ii + ',';
    }
    delList = delList.substr(0, delList.length - 1);
    params.scenarinoIds = delList;
    url = '/scenarino/delete_scenarino'
  }

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
        if (res.status == 0) {
          if (type == 'rw') {
            $('#rwTable').bootstrapTable('destroy');
            initRwTable();
          } else {
            $('#qjTable').bootstrapTable('destroy');
            initQjTable();
          }
          swal("已删除!", "", "success");
        } else {
          swal("删除失败!", "", "error");
        }
      }).error(function () {
        swal("删除失败!", "", "error");
      })

    });
}


$('#createRwModal').on('show.bs.modal', function (event) {
  $('.rwDateTip').addClass('disNone');
})

/*模态框打开处理*/
$('#createModal').on('show.bs.modal', function (event) {
  if (!event.relatedTarget)return;
  var button = $(event.relatedTarget);
  var recipient = button.data('whatever');
  var type = button.data('type');
  var modal = $(this);
  modal.find('.modal-title').text(recipient);
  modal.find('.create').attr('data-type', type);
  modal.find('.createRun').attr('data-type', type);


  /*不同类型出现内容不同*/
  if (type == 'rw') {
    modal.find('.createRun').text('创建并执行基准模拟');
    modal.find('.createRun').attr('data-href', '');
    $('.createRw').css('display', 'block');
    $('.createQj').css('display', 'none');
    $('.createRw').parent().removeClass('col-md-6');
    $('.selectOldQj').css('display', 'none');
    $('#createModal .modal-dialog').removeClass('w80');

  } else {
    modal.find('.createRun').text('创建并开始编辑情景');
    modal.find('.createRun').attr('data-href', 'xxxxxxx.html');

    $('.createRw').css('display', 'none');
    $('.createQj').css('display', 'block');
    $('.createRw').parent().removeClass('col-md-6');
    $('.selectOldQj').css('display', 'none');
    $('#createModal .modal-dialog').removeClass('w80');

    $('.createQj-in').css('display', 'none');
    $('.qjSelectWay').css('display', 'block');
    $('.new-foot').css('display', 'none');
    $('.old-foot').css('display', 'block');

  }

});


var qjId = '';


/*修改名称*/
function rename(type, id) {
//    var url = 'rw.json';
  var url, urlName;
  var params = {userId: userId};
  var paramsName = {userId: userId};
  if (type == 'rw') {
    params.missionId = id;
    url = '/mission/update_mission';
    urlName = '/mission/check_missioname';
  } else {
    params.scenarinoId = id;
    paramsName.missionId = selectRW.missionId;
    params.state = -1;
    url = '/scenarino/updat_scenarino';
    urlName = '/scenarino/check_scenarinoname';
  }

  swal({
      title: "名称修改",
//          text: "请输入新名称:",
      type: "input",
      showCancelButton: true,
      closeOnConfirm: false,
      confirmButtonText: "修改",
      cancelButtonText: "取消",
      animation: "slide-from-top",
      inputPlaceholder: "请输入新名称:",
      showLoaderOnConfirm: true
    },
    function (inputValue) {
      if (inputValue === false) return false;

      if (inputValue === "") {
        swal.showInputError("请输入内容!");
        return false
      }

      if (type == 'rw') {
        params.missionName = inputValue;
      } else {
        params.scenarinoName = inputValue;
        paramsName.scenarinoName = inputValue;
      }
      ajaxPost(urlName, type == 'rw' ? params : paramsName).success(function (res) {
        if (res.data) {
          ajaxPost(url, params).success(function () {
            if (type == 'rw') {
              $('#rwTable').bootstrapTable('destroy');
              initRwTable();
            } else {
              $('#qjTable').bootstrapTable('destroy');
              initQjTable();
            }
            swal("已修改!", "修改名称为：" + inputValue, "success");
          }).error(function () {
            swal("修改失败!", "名称未修改为：" + inputValue, "error");
          }).error(function () {
            $('#createModal').modal('hide');
            swal('添加失败', '', 'error')
          })
        } else {
          swal('名称重复', '', 'error')
        }
      }).error(function () {
        swal('校验失败', '', 'error')
      })
    });

}


/*返回上一步操作*/
function returnLeft(type) {
  if (type == 'rw') {
    $('.rwType').css('display', 'block');
    $('.rwCon').css('display', 'none');
    $('.createRwBtn').css('display', 'none');
    $('.return_S_rw').css('display', 'none');
    $('.rwTitle').html('创建任务')
  } else if (type == 'yqj') {
    $('.ypgQJType').css('display', 'block');
    $('.ypgQJCon').css('display', 'none');
    $('.createQjBtn').css('display', 'none');
    $('.return_S_qj').css('display', 'none');
  } else if (type == 'hqj') {
    $('.hpgQJType').css('display', 'block');
    $('.hpgQJCon').css('display', 'none');
    $('.createQjBtn').css('display', 'none');
    $('.return_S_qj').css('display', 'none');
  }
}


var rwTypeV;
/*创建任务选择预评估或后评估*/
function selectType(type) {
  rwTypeV = type;
  var startDate;
  var endDate;
  var start,end;
  if (type == 'y') {
    startDate = moment().subtract(14, 'd').format('YYYY-MM-DD');
    endDate = moment().add(365, 'd').format('YYYY-MM-DD');
    start = moment().subtract(2, 'd').format('YYYY-MM-DD');
    end = moment().add(14, 'd').format('YYYY-MM-DD');
    rwSelectType = '1';
    $('.rwTitle').html('创建预评估任务');
    rwEndDate = moment().add(2, 'd').format('YYYY-MM-DD');
    initRwDate(startDate, endDate,start, end);
  } else if (type == 'h') {
    startDate = '2007-01-01';
    endDate = moment().subtract(2, 'd').format('YYYY-MM-DD');
    start = moment().subtract(32, 'd').format('YYYY-MM-DD');
    end = moment().subtract(2, 'd').format('YYYY-MM-DD');
    rwSelectType = '2';
    $('.rwTitle').html('创建后评估任务');
    rwEndDate = moment().subtract(2, 'd').format('YYYY-MM-DD');
    initRwDate(startDate, endDate,start, end);
  }

  $('.rwType').css('display', 'none');
  $('.rwCon').css('display', 'block');

  $('.createRwBtn').css('display', 'inline-block');
  $('.return_S_rw').css('display', 'inline-block');
  rwStartDate = moment().subtract(2, 'w').format('YYYY-MM-DD');


}

/*创建情景选择类型*/
function selectQJtype(type) {

  var url = '/scenarino/find_endTime';
  //var url = 'end.json';
  var params = {
    userId: userId
  };

  __dsp['jcqj' + selectRW.missionId].then(function (res) {
    basisArr = res.data;
    if (basisArr)

    //reverse  倒序
      switch (type) {
        case 'yy':
          $('.ypgQJType').css('display', 'none');
          $('.ypgQJCon').css('display', 'block');
          $('.dbqj').css('display', 'none');
          $('.createQjBtn').css('display', 'inline-block');
          $('.return_S_qj').css('display', 'inline-block');
          qjType = 1;
          params.scenType = qjType;

          setOption('#jcqj', basisArr);
          var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, basisArr[0].scenarinoEndDate, basisArr[0].pathDate);
          $('#jcdate').empty();
          for (var i = 0; i < dateArr.length; i++) {
            $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
          }
          var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
          $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));
          ajaxPost(url, params).success(function (res) {
            selectEndDate = res.data.endTime;
            selectEndDate = moment(selectRW.missionEndDate).add(1, 'd').isBefore(selectEndDate) ? moment(selectRW.missionEndDate).format('YYYY-MM-DD') : selectEndDate;
            var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
            $('#yEndDate').empty();
            for (var i = 0; i < endDateArr.length; i++) {
              if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
              $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
            }
          });

          break;
        case 'yh':
          $('.ypgQJType').css('display', 'none');
          $('.ypgQJCon').css('display', 'block');
          $('.dbqj').css('display', 'block');
          $('.createQjBtn').css('display', 'inline-block');
          $('.return_S_qj').css('display', 'inline-block');
          $('#dbqj').attr('disabled', true);
          $('#jcqj').removeAttr('disabled');
          $('#jcdate').removeAttr('disabled');
          $('#yEndDate').removeAttr('disabled');
          qjType = 2;
          params.scenType = qjType;

          if (basisArr.length <= 1) {
            $('.chengeDB').attr('disabled', true)
          } else {
            $('.chengeDB').removeAttr('disabled')
          }
          setOption('#dbqj', basisArr);
          setOption('#jcqj', basisArr);

          var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, basisArr[0].scenarinoEndDate, basisArr[0].pathDate);
          dateArr = dateArr.reverse();
          $('#jcdate').empty();
          for (var i = 0; i < dateArr.length; i++) {
            $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
          }
          var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
          $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

          ajaxPost(url, params).success(function (res) {
            selectEndDate = res.data.endTime;
            selectEndDate = moment(selectRW.missionEndDate).add(1, 'd').isBefore(selectEndDate) ? moment(selectRW.missionEndDate).format('YYYY-MM-DD') : selectEndDate;
            var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
            $('#yEndDate').empty();
            for (var i = 0; i < endDateArr.length; i++) {
              if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
              $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
            }
          });


          break;
        case 'hh':
          $('.hpgQJType').css('display', 'none');
          $('.hpgQJCon').css('display', 'block');
          $('.createQjBtn').css('display', 'inline-block');
          $('.return_S_qj').css('display', 'inline-block');
          $('.diffNo').css('display', 'block');
          $('.spinup').css('display', 'none');
          $('#dbqj1').attr('disabled', true);
          $('#jcqj1').removeAttr('disabled');
          $('#jcdate1').removeAttr('disabled');
          $('#hEndDate').removeAttr('disabled');
          qjType = 2;

          if (basisArr.length <= 1) {
            $('.chengeDB').attr('disabled', true)
          } else {
            $('.chengeDB').removeAttr('disabled')
          }
          setOption('#dbqj1', basisArr);
          setOption('#jcqj1', basisArr);
          var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, moment(selectRW.missionEndDate).subtract(2, 'd').format('YYYY-MM-DD'));
          dateArr = dateArr.reverse();
          $('#jcdate1').empty();
          for (var i = 0; i < dateArr.length; i++) {
            $('#jcdate1').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
          }
          var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
          $('#hStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));


          var endDateArr = setSelectDate($('#hStartDate').val(), moment(selectRW.missionEndDate));
          $('#hEndDate').empty();
          for (var i = 0; i < endDateArr.length; i++) {
            $('#hEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
          }
          break;
        case 'hj':
          $('.hpgQJType').css('display', 'none');
          $('.hpgQJCon').css('display', 'block');
          $('.createQjBtn').css('display', 'inline-block');
          $('.return_S_qj').css('display', 'inline-block');
          $('.diffNo').css('display', 'none');
          $('.spinup').css('display', 'block');
          $('#hEndDate').attr('disabled', true);
          qjType = 3;
          $('#hStartDate').empty().append($('<option value="' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '</option>'));
          $('#hEndDate').empty().append($('<option value="' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '</option>'));


          break;
      }

  }, function () {
    console.log('基础情景获取失败！！！！！');
    if (type == 'hj') {
      $('.hpgQJType').css('display', 'none');
      $('.hpgQJCon').css('display', 'block');
      $('.createQjBtn').css('display', 'inline-block');
      $('.return_S_qj').css('display', 'inline-block');
      $('.diffNo').css('display', 'none');
      $('.spinup').css('display', 'block');
      $('#hEndDate').attr('disabled', true);
      qjType = 3;
      $('#hStartDate').empty().append($('<option value="' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '</option>'));
      $('#hEndDate').empty().append($('<option value="' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '</option>'));
    }
  })


}

function checkedDB(t) {
  if ($(t)[0].checked) {
    $('#dbqj').removeAttr('disabled');
    $('#jcqj').attr('disabled', true);
    $('#jcdate').attr('disabled', true);
    $('#yEndDate').attr('disabled', true);

    $('#dbqj1').removeAttr('disabled');
    $('#jcqj1').attr('disabled', true);
    $('#jcdate1').attr('disabled', true);
    $('#hEndDate').attr('disabled', true);
  } else {
    $('#dbqj').attr('disabled', true);
    $('#jcqj').removeAttr('disabled');
    $('#jcdate').removeAttr('disabled');
    $('#yEndDate').removeAttr('disabled');

    $('#dbqj1').attr('disabled', true);
    $('#jcqj1').removeAttr('disabled');
    $('#jcdate1').removeAttr('disabled');
    $('#hEndDate').removeAttr('disabled');
  }
}


/*新改任务创建*/
function createRw() {
  if($('#rwForm').valid()){
    var url = '/mission/save_mission';
    var urlName = '/mission/check_missioname';
    var params = {};
    var paramsName = {};
    params.missionName = paramsName.missionName = $('#rwName').val();
    params.missionDomainId = $('#mnfw').val();
    params.esCouplingId = $('#qd').val();
    params.missionStartDate = moment(rwStartDate).format('YYYY-MM-DD HH:mm:ss');
    params.missionEndDate = moment(rwEndDate).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
    params.userId = paramsName.userId = userId;
    params.missionStauts = rwSelectType;

    if (rwSelectType == '1') {
      if (rwEndDate < moment().format('YYYY-MM-DD')) {
        $('.rwDateTip').removeClass('disNone');
        return;
      }
    }

    ajaxPost(urlName, paramsName).success(function (res) {
      if (res.data) {
        ajaxPost(url, params).success(function (res) {
          if (res.status == 0) {
            $('#rwTable').bootstrapTable('destroy');
            initRwTable();
            $('#createRwModal').modal('hide');
            $('#rwName').val('');
            swal('添加成功', '', 'success')
          } else {
            $('#createRwModal').modal('hide');
            swal('添加失败', '', 'error')
          }
        }).error(function () {
          $('#createRwModal').modal('hide');
          swal('添加失败', '', 'error')
        })
      } else {
        swal('名称重复', '', 'error')
      }
    }).error(function () {
      swal('校验失败', '', 'error')
    })
  }

}

/*初始化日期插件*/
function initRwDate(s, e,start, end) {
  $('#rwDate').daterangepicker({
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
    rwStartDate = start.format('YYYY-MM-DD');
    rwEndDate = end.format('YYYY-MM-DD');
  })
  var d = $('#rwDate').data('daterangepicker');
  d.element.off();
}

/*按钮打开日期*/
//($('#rwDate').data('daterangepicker')).element.on('click.daterangepicker',function(){});
function showRwDate(){
  var d = $('#rwDate').data('daterangepicker');
  d.toggle();
}

/*创建情景时选择模态框*/
function createQJselect() {
  var oldQJUrl = '/scenarino/find_scenarino_time';
  //var oldQJUrl = 'date.json';
  var oldQJparams = {
    userId: userId,
    missionId: selectRW.missionId
  };
  //if((!__dsp['jcqj'+selectRW.missionId]) || (basisArr.length ==0)){
  __dsp['jcqj' + selectRW.missionId] = ajaxPost(oldQJUrl, oldQJparams);
  //}

  if (selectRW.missionStatus == "1") {
    if (moment(selectRW.missionEndDate).isBefore(moment(), 'day')) {
      $('.disYQJ').attr('disabled', true);
    } else {
      $('.disYQJ').removeAttr('disabled');
    }
    $('#createYpQjModal').modal('show');
    returnLeft('yqj');
  } else if (selectRW.missionStatus == "2") {
    $('#createHpQjModal').modal('show');
    returnLeft('hqj');
  }
}

/*配置日期*/
function setSelectDate(qjS, qjE, pathD) {
  var dateArr = [];
  if (pathD) {
    var p = moment(pathD);
    while (!(p.isBefore(moment(qjS),'d'))) {
      dateArr.push(p.subtract(1, 'd').format('YYYY-MM-DD'));
    }
  } else {
    var e = moment(qjE);
    while (!(e.isBefore(moment(qjS),'d'))) {
      dateArr.push(e.format('YYYY-MM-DD'));
      e.subtract(1, 'd');
    }
  }
  return dateArr
}

/*基础情景改变的时候，基础日期随之改变*/
function changeJcqj(t) {
  var index = $(t).val();
  var selectJcqj = basisArr[index];
  var dateArr = setSelectDate(selectJcqj.scenarinoStartDate, selectJcqj.scenarinoEndDate, selectJcqj.pathDate);
  if (qjType == 2) {
    dateArr = dateArr.reverse();
  }
  $('#jcdate').empty();
  for (var i = 0; i < dateArr.length; i++) {
    $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
  }
  var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
  $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

  var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
  $('#yEndDate').empty();
  for (var i = 0; i < endDateArr.length; i++) {
    if(qjType == 1){
      if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
      $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
    }else if(qjType == 2){
      if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
      $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
    }
  }
}

function changeJcDate(t) {
  var date = $(t).val();
  var startD = moment(moment(date)).add(1, 'd').format('YYYY-MM-DD');
  $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

  var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
  $('#yEndDate').empty();
  for (var i = 0; i < endDateArr.length; i++) {
    if(qjType == 1){
      if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
      $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
    }else if(qjType == 2){
      if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
      $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
    }
  }
}

/*基础情景改变的时候，基础日期随之改变*/
function changeJcqj1(t) {
  var index = $(t).val();
  var selectJcqj = basisArr[index];
  var dateArr = setSelectDate(selectJcqj.scenarinoStartDate, moment(selectRW.missionEndDate).subtract(2, 'd').format('YYYY-MM-DD'));
  dateArr = dateArr.reverse();
  $('#jcdate1').empty();
  for (var i = 0; i < dateArr.length; i++) {
    $('#jcdate1').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
  }
  var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
  $('#hStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

  var endDateArr = setSelectDate($('#hStartDate').val(), moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
  $('#hEndDate').empty();
  for (var i = 0; i < endDateArr.length; i++) {
    $('#hEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
  }
}

function changeJcDate1(t) {
  var date = $(t).val();
  var startD = moment(moment(date)).add(1, 'd').format('YYYY-MM-DD');
  $('#hStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

  var endDateArr = setSelectDate($('#hStartDate').val(), moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
  $('#hEndDate').empty();
  for (var i = 0; i < endDateArr.length; i++) {
    $('#hEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
  }
}


/*添加option*/
function setOption(ele, res) {
  $(ele).empty();
  for (var i = 0; i < res.length; i++) {
    if (((ele == '#dbqj') || (ele == '#dbqj1')) && (res[i].scenarinoId == -1))continue;
    $(ele).append($('<option value="' + i + '">' + res[i].scenarinoName + '</option>'))
  }
}

/*新改情景创建*/
function createQj(type) {
  var url, urlName;
  var params, paramsName;
  url = '/scenarino/save_scenarino';
  urlName = '/scenarino/check_scenarinoname';
  if (type == 'ypg') {

    if($('#yqjForm').valid()){
      params = {};
      paramsName = {};
      params.scenarinoName = paramsName.scenarinoName = $('#yName').val();
      params.userId = paramsName.userId = userId;
      params.missionId = paramsName.missionId = selectRW.missionId;
      params.missionType = selectRW.missionStatus;
      params.scenType = qjType;
      params.scenarinoStartDate = '';
      params.scenarinoEndDate = '';
      params.basisScenarinoId = '';
      params.basisTime = '';
      params.controstScenarinoId = '';
      params.spinUp = '';

      if (qjType == 1) {
        params.scenarinoStartDate = moment($('#yStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
        params.scenarinoEndDate = moment($('#yEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
        params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
        params.basisTime = moment($('#jcdate').val()).format('YYYY-MM-DD HH:mm:ss');
      } else if (qjType == 2) {
        if ($('.dbqj input[type=checkbox]')[0].checked) {
          params.controstScenarinoId = basisArr[$('#dbqj').val()].scenarinoId;
        } else {
          params.scenarinoStartDate = moment($('#yStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
          params.scenarinoEndDate = moment($('#yEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
          params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
          params.basisTime = moment($('#jcdate').val()).format('YYYY-MM-DD HH:mm:ss');
        }
      }

      ajaxPost(urlName, paramsName).success(function (res) {
        if (res.data) {
          ajaxPost(url, params).success(function () {
            $('#qjTable').bootstrapTable('destroy');
            initQjTable();
            $('#createHpQjModal').modal('hide');
            $('#createYpQjModal').modal('hide');
            swal('添加成功', '', 'success')
          }).error(function () {
            swal('添加失败', '', 'error')
          })
        } else {
          swal('名称重复', '', 'error')
        }
      }).error(function () {
        swal('校验失败', '', 'error')
      })
    }


  } else {

    if($('#hqjForm').valid()){
      params = {};
      paramsName = {};
      params.scenarinoName = paramsName.scenarinoName = $('#hName').val();
      params.missionId = paramsName.missionId = selectRW.missionId;
      params.missionType = selectRW.missionStatus;
      params.userId = paramsName.userId = userId;
      params.scenType = qjType;
      params.scenarinoStartDate = '';
      params.scenarinoEndDate = '';
      params.basisScenarinoId = '';
      params.basisTime = '';
      params.controstScenarinoId = '';
      params.spinUp = '';
      if (qjType == 2) {
        if ($('.dbqj1 input[type=checkbox]')[0].checked) {
          params.controstScenarinoId = basisArr[$('#dbqj1').val()].scenarinoId;
        } else {
          params.scenarinoStartDate = moment($('#hStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
          params.scenarinoEndDate = moment($('#hEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
          params.basisScenarinoId = basisArr[$('#jcqj1').val()].scenarinoId;
          params.basisTime = moment($('#jcdate1').val()).format('YYYY-MM-DD HH:mm:ss');
        }
      } else if (qjType == 3) {
        params.scenarinoStartDate = moment($('#hStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
        params.scenarinoEndDate = moment($('#hEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
        params.spinUp = $('#spinup').val();
      }

      ajaxPost(urlName, paramsName).success(function (res) {
        if (res.data) {
          ajaxPost(url, params).success(function () {
            $('#qjTable').bootstrapTable('destroy');
            initQjTable();
            $('#createHpQjModal').modal('hide');
            $('#createYpQjModal').modal('hide');
            swal('添加成功', '', 'success')
          }).error(function () {
            swal('添加失败', '', 'error')
          })
        } else {
          swal('名称重复', '', 'error')
        }
      }).error(function () {
        swal('校验失败', '', 'error')
      })
    }

  }

}

$('body').on('click','.statusType',function(){
  if(msg.content.scenarinoStatus == 3){
    $('#jpzt').modal('show');
  }else if(msg.content.scenarinoStatus == 6){
    switch(msg.content.SCEN_TYPE){
      case '1':
        return
      case '2':
        return
      case '3':
        return
    }
  }
})

$('#startUp').on('show.bs.modal', function (event) {
  var num = Math.round((msg.content.qjEndDate - msg.content.qjStartDate)/1000/60/60/24);
  var url = '/getCores/spentTimes';
  ajaxPost(url,{
    userId:userId,
    missionId:msg.content.rwId,
    missionType:msg.content.rwType,
    scenarinoType:msg.content.SCEN_TYPE
  }).success(function(res){
    if(res.status==0){
      $('.cpu span').empty();
      $('.baseTime').empty();
      $('.allTime').empty();
      for(var i=0;i<res.data.length;i++){
        $('.cpu span').eq(i).html(res.data[i].cores);
        $('.cpu input').eq(i).val(res.data[i].cores);
        $('.baseTime').eq(i).html((res.data[i].avgTime-0)*num + 'h');
        $('.allTime').eq(i).html((res.data[i].avgTime-0)*num*(res.data[i].cores-0));
      }
    }else{
      console.log('接口故障！！！')
    }
  }).error(function(){
    console.log('接口未成功发送')
  })
})

function subStartUp(){
  var url = 'aaaaaaaaaaaaaaaaaaa';
  ajaxPost(url,{
    userId:userId,
    scenarinoId:msg.content.qjId,
    missionId:msg.content.rwId,
    scenarinoType:msg.content.SCEN_TYPE,
    cores:$('input[name=cpuNum]:checked').val()
  }).success(function(res){

  })
}


/*减排状态查看*/
function jpztckBtn() {
  var url = '/jp/areaStatusJp';
  var params = {
    scenarinoId: msg.content.qjId,
    areaAndPlanIds: '',
    userId: userId
  };
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

$('#jpzt').on('show.bs.modal', function (event) {
  $('#jpzt .rw span').empty().html(selectRW.missionName);
  $('#jpzt .qj span').empty().html(msg.content.qjName);
  jpztckBtn();
})

/*获取模拟范围*/
function getMnfw() {
  var url = '';
  var params = {
    userId: userId
  };
  ajaxPost(url, params).success(function (res) {

    for (var i = 0; i < res.data; i++) {
      $('#mnfw').append($('<option value=""></option>'))
    }

  }).error(function () {
    console.log('模拟范围未获取到！！！！')
  })
  $('#mnfw').append($('<option value="1">范围1</option>'))
}

/*获取清单*/
function getQD() {
  var url = '/escoupling/get_couplingInfo';
  var params = {
    userId: userId
  };
  ajaxPost(url, params).success(function (res) {
    for (var i = 0; i < res.data.length; i++) {
      $('#qd').append($('<option value="'+ res.data[i].esCouplingId +'">'+ res.data[i].esCouplingName +'</option>'))
    }

  }).error(function () {
    console.log('清单未获取到！！！！')
  })
  //$('#qd').append($('<option value="1">jjj</option>'))
}

