/**
 * Created by lvcheng on 2017/2/21.
 */
//  console.log(BackstageIP);
var userId = 1;
var formCreate;
var selectRW;
var delRWid = {}, delQJid = {};
var parameterPar = {total: '', data: {}};
function refreshIcheck() {
  var callbacks_list = $('.rwTable ul');
  $('body input').on('ifCreated ifClicked ifChanged ifChecked ifUnchecked ifDisabled ifEnabled ifDestroyed', function (event) {
    callbacks_list.prepend('<li><span>#' + this.id + '</span> is ' + event.type.replace('if', '').toLowerCase() + '</li>');
  }).iCheck({
    checkboxClass: 'icheckbox_flat-aero',
    radioClass: 'iradio_flat-aero',
    increaseArea: '20%'
  });
}

function formVerify(){
  formCreate = $("#formCreate").validate({
    debug:true,
    rules:{
      rwName:{
        required: true,
        maxlength: 15
      },
      rwStartDate:{
        required: true
      },
      rwEndDate:{
        required: true
      },
      qjName:{
        required: true,
        maxlength: 15
      },
      qjStartDate:{
        required: true
      },
      qjEndDate:{
        required: true
      }
    },
    messages: {
      rwName:{
        required: '请填写任务名称',
        maxlength: '不可超过15个字符'
      },
      rwStartDate:{
        required: '请填写日期'
      },
      rwEndDate:{
        required: '请填写日期'
      },
      qjName:{
        required: '请填写情景名称',
        maxlength: '不可超过15个字符'
      },
      qjStartDate:{
        required: '请填写日期'
      },
      qjEndDate:{
        required: '请填写日期'
      }
    }

  })
}


/*看名字*/
function ajaxPost(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  //return $.ajax(BackstageIP + url, {
  return $.ajax('/ampc' + url, {
    contentType: "application/json",
    type: "POST",
    async: true,
    dataType: 'JSON',
    data: p
  })
}

$(document).ready(function () {
  initialize();
  initDate();
  formVerify();
});

function initialize() {
  initRwTable();
}

var QJheight;
function initRwTable() {
  $('#rwTable').bootstrapTable({
    method: 'POST',
    //url: 'rw.json',
//      url : BackstageIP+'/mission/get_mission_list',
      url : '/ampc/mission/get_mission_list',
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
    pageSize: 10, // 页面大小
    pageNumber: 1, // 页数
    pageList: [10],
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
          "missionName": m.searchText || '',
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
    onLoadSuccess:function(data){
      //console.log(data);
      selectRW = data.rows[0];
      QJheight = data.rows.length *57 + 98;
      $('.qjtableDiv').css('background-color','#d9edf7');
      //QJheight = $('.rwtableDiv').height();

      if(QJheight<600){
        QJheight = 600;
        $('#rwTable').bootstrapTable('resetView',{height:600})
      }else{
        $('#rwTable').bootstrapTable('resetView',{height:QJheight})
      }

      //QJheight = QJheight>400?QJheight:400;
      $('#qjTable').bootstrapTable('destroy');
      initQjTable();
    },
    /*右键菜单*/
    contextMenu: '#RWcontext-menu',
    onContextMenuItem: function (row, $el) {
      if ($el.data("item") == "rename") {
        rename('rw', row.missionId);
      }
    }
  });
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
          "missionName": params.searchText || '',
          "pageNum": params.pageNumber,
          "pageSize": params.pageSize,
          "sort": '',
          "userId": 1
        }
      };
      json.data.missionName = $('.rw').val();
    } else {
      json = {
        "token": "",
        "data": {
          "missionId": selectRW.missionId,
          "scrnarinoName": params.searchText || '',
//            "pageNum": params.pageNumber,
//            "pageSize": params.pageSize,
          "sort": '',
          "userId": 1
        }
      };
      json.data.scrnarinoName = $('.qj').val();
    }
//      json.data.missionName = $('.' + type).val();
    params = JSON.stringify(json);
    console.info(params);
    return params;
  };
  $('#' + type + 'Table').bootstrapTable('refresh', params);
}


function initQjTable() {
  $('#qjTable').bootstrapTable({
    method: 'GET',
    url: 'webApp/task01/qj.json',
//      url : BackstageIP+'/scenarino/scenarinoListBymissionId',
//      url : '/ampc/scenarino/get_scenarinoListBymissionId',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#qjToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:QJheight-59,
    maintainSelected: true,
    clickToSelect: false,// 点击选中行
    pagination: false, // 在表格底部显示分页工具栏
    onlyInfoPagination: true,
    pageSize: 10, // 页面大小
    pageNumber: 1, // 页数
    pageList: [10],
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
          "scrnarinoName": '',
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
//        $('.success').addClass('info').removeClass('success');
//        $($element).removeClass('info').addClass('success');
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
  return '<a href="#">' + row.missionName + '</a><br><a style="font-size:12px; color:#a1a1a1;">创建时间：' + moment(row.missionAddTime).format('YYYY-MM-DD HH') + '</a>'
}

function rwDomain(v, row, i) {
  return row.v1 + '<br><a style="font-size:12px; color:#a1a1a1;">起止日期：' + moment(row.missionStartDate).format('YYYY-MM-DD HH') + ' - ' + moment(row.missionEndDate).format('YYYY-MM-DD HH') + '</a>'
}

function qjName(v, row, i) {
  return '<a href="#">' + row.scenarinoName + '</a><br>' +
    '<a style="font-size:12px; color:#a1a1a1;">创建时间：' + row.addTime + '</a><br/>' +
    '<a style="font-size:12px; color:#a1a1a1;">起止日期：' + row.startDate + ' - ' + row.endDate + '</a>'
}

function qjOrder(v, row, i) {
  return '<button class="btn btn-primary mb10 mr10">启动</button>' +
    '<button class="btn btn-primary mb10">终止</button>' +
    '<br/>' +
    '<button class="btn btn-primary mr10">续跑</button>' +
    '<button class="btn btn-primary">暂停</button>'
}

function qjEffectEvaluation(v, row, i) {
  return '<button class="btn btn-primary mb10 mr10">效果评估</button>'
}


/*delete 函数*/
function deleteFun(type) {
  var params = {userId: userId};
  var delList = '', url;
  if (type == 'rw') {
    for (var i in delRWid) {
      delList+=i+',';
    }
    delList = delList.substr(0,delList.length-1);
    params.missionIds = delList;
    url = '/mission/delete_mission';
  } else {
    for (var ii in delQJid) {
      delList+=ii+',';
    }
    delList = delList.substr(0,delList.length-1);
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
        if(res.status == 0){
          if (type == 'rw') {
            $('#rwTable').bootstrapTable('destroy');
            initRwTable();
          } else {
            $('#qjTable').bootstrapTable('destroy');
            initQjTable();
          }
          swal("已删除!", "", "success");
        }else{
          swal("删除失败!", "", "error");
        }
      }).error(function () {
        swal("删除失败!", "", "error");
      })

    });
}


/*模态框打开处理*/
$('#createModal').on('show.bs.modal', function (event) {
  if(!event.relatedTarget)return;
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
    modal.find('.createRun').attr('data-href','');
    $('.createRw').css('display','block');
    $('.createQj').css('display','none');
  } else {
    modal.find('.createRun').text('创建并开始编辑情景');
    modal.find('.createRun').attr('data-href','xxxxxxx.html');
    $('.createRw').css('display','none');
    $('.createQj').css('display','block');
  }

});

/*任务、情景创建*/
function create(e,run) {

  $('.createSubmit').click();
  if(formCreate.form()){
    var url;
    var params;
    var type = $(e).attr('data-type');
    if(type == 'rw'){
      url = '/mission/save_mission';
      params = {};
      params.missionName = $('#rwName').val();
      params.missionDomainId = $('#mnfw').val();
      params.esCouplingId = $('#qd').val();
      params.missionStartDate = $('#rwStartDate').val();
      params.missionEndDate = $('#rwEndDate').val();
      params.userId = userId;
      if(run){
        params.createType = 2;
      }else{
        params.createType = 1;
      }
    }else{

      url = '/scenarino/save_scenarino';
      params = {};
      params.scenarinoName = $('#qjName').val();
      params.missionId = selectRW.missionId;
      params.scenarinoStartDate = $('#qjStartDate').val();
      params.scenarinoEndDate = $('#qjEndDate').val();
      params.userId = userId;
      params.scenarinoId = '';
      if(run){
        params.createType = 2;
      }else{
        params.createType = 1;
      }
    }

    ajaxPost(url, params).success(function () {
      //console.log('success');
      if (type == 'rw') {
        $('#rwTable').bootstrapTable('destroy');
        initRwTable();
      } else {
        $('#qjTable').bootstrapTable('destroy');
        initQjTable();
      }
      $('#createModal').modal('hide')
    }).error(function () {
//        console.log('error');
      $('#createModal').modal('hide')
      swal('添加失败', '', 'error')
    })
  }
}


/*初始化日期插件*/
function initDate() {
  $("#rwStartDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'year',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      $('#rwEndDate').datetimepicker('setStartDate', date);
      $('#rwStartDate').datetimepicker('setEndDate', null);
    });
  $("#rwEndDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'year',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true,
    startDate:'2017-02-24'
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      $('#rwStartDate').datetimepicker('setEndDate', date);
      $('#rwEndDate').datetimepicker('setStartDate', null);
    });
  $("#qjStartDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'year',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      $('#qjEndDate').datetimepicker('setStartDate', date);
      $('#qjStartDate').datetimepicker('setStartDate', null);
    });
  $("#qjEndDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'year',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      $('#qjStartDate').datetimepicker('setStartDate', date);
      $('#qjEndDate').datetimepicker('setEndDate', null);
    });
}

/*修改名称*/
function rename(type, id) {
//    var url = 'rw.json';
  var url;
  var params = {userId: userId};
  if (type == 'rw') {
    params.missionId = id;
    url = '/mission/update_mission';
  } else {
    params.scenarinoId = id;
    params.state = -1;
    url = '/scenarino/updat_scenarino';
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
      }

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
      });

    });

}