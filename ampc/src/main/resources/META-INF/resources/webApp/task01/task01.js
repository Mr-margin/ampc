/**
 * Created by lvcheng on 2017/2/21.
 */
//  console.log(BackstageIP);
//var userId = 1;
var formCreate;
var selectRW = {};
var statusRW = '';
var delRWid = {}, delQJid = {};
//var parameterPar = {total: '', data: {}};
var msg = {
  'id': 'qjMessage',
  'content': {
    rwId:'',
    rwName:'',
    qjId:'',
    qjName:'',
    qjStartDate:'',
    qjEndDate:''

  }
};

$(window).resize(function(e) {
  if($(window).width()<1370){
    $('.cjsc').removeClass('col-md-4').addClass('col-md-6');
    $('.smallP').css('display','block');
    $('.bigP').css('display','none');
  }else{
    $('.smallP').css('display','none');
    $('.bigP').css('display','block');
    $('.cjsc').removeClass('col-md-6').addClass('col-md-4')

  }
});

function formVerify(){
  //$.validator.setDefaults({
  //  highlight: function(e) {
  //    $(e).closest(".form-group").removeClass("has-success").addClass("has-error")
  //  },
  //  success: function(e) {
  //    e.closest(".form-group").removeClass("has-error").addClass("has-success")
  //  },
  //  errorElement: "span",
  //  errorPlacement: function(e, r) {
  //    e.appendTo(r.is(":radio") || r.is(":checkbox") ? r.parent().parent().parent() : r.parent())
  //  },
  //  errorClass: "help-block m-b-none error",
  //  validClass: "help-block m-b-none error"
  //});
  //$.validator.addMethod("chinese", function(value, element) {
  //  var chinese = /^[\u4e00-\u9fa5]+$/;
  //  return this.optional(element) || (chinese.test(value));
  //}, "只能输入中文");
  //$.validator.addMethod("qingxuanze", function(value, element) {
  //  var valtext = '请选择';
  //  var valtext = value != '请选择';
  //  return this.optional(element) || (valtext);
  //}, "必选选择");





  formCreate = $("#formCreate").validate({
    debug:true,
    onfocusout:function(element){
      $(element).valid();
    },
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

    //$("#formCreate").validate({
    //  onfocusout: function(element){
    //    $(element).valid();
    //  }
    //});
  }


/*看名字*/
/*function ajaxPost(url, parameter) {
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
}*/

//$(document).ready(function () {
  initialize();
  initDate();
  formVerify();
//});

function initialize() {
  initRwTable();
//  var param = vipspa.getMessage('home_msg');
//  console.log(param);
}

var QJheight;
function initRwTable() {
  $('#rwTable').bootstrapTable({
    method: 'POST',
    //url: 'webApp/task01/rw.json',
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
          "queryName": m.searchText || '',
          "missionStatus":statusRW,
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


      $('#qjStartDate').datetimepicker('setStartDate', moment(selectRW.missionStartDate).format('YYYY-MM-DD')).datetimepicker('setEndDate', moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
      $('#qjEndDate').datetimepicker('setStartDate', moment(selectRW.missionStartDate).format('YYYY-MM-DD')).datetimepicker('setStartDate', moment(selectRW.missionEndDate).format('YYYY-MM-DD'));

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
      if(data.rows.length == 0){
        $('#qjTable').bootstrapTable('resetView',{height:600});
        return;
      }
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

/*筛选*/
function statusRWfun(status,t){
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
          "missionStatus":statusRW,
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
    //url: 'webApp/task01/qy.json',
//      url : BackstageIP+'/scenarino/scenarinoListBymissionId',
      url : '/ampc/scenarino/get_scenarinoListBymissionId',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#qjToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:QJheight-57,
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
      msg.content.rwId=selectRW.missionId;
      msg.content.rwName=selectRW.missionName;
      msg.content.qjName = row.scenarinoName;
      msg.content.qjId = row.scenarinoId;
      msg.content.qjStartDate = row.scenarinoStartDate;
      msg.content.qjEndDate = row.scenarinoEndDate;
      vipspa.setMessage(msg);
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
  return '<a href="javascript:">' + row.missionName + '</a><br><a style="font-size:12px; color:#a1a1a1;">创建时间：' + moment(row.missionAddTime).format('YYYY-MM-DD HH') + '</a>'
}

function rwDomain(v, row, i) {
  return row.v1 + '<br><a style="font-size:12px; color:#a1a1a1;">起止日期：' + moment(row.missionStartDate).format('YYYY-MM-DD HH') + ' - ' + moment(row.missionEndDate).format('YYYY-MM-DD HH') + '</a>'
}

function qjName(v, row, i) {
  return '<a href="#/yabj">' + row.scenarinoName + '</a><br>' +
    '<a style="font-size:12px; color:#a1a1a1;">创建时间：' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH') + '</a><br/>' +
    '<a style="font-size:12px; color:#a1a1a1;">起止日期：' +moment(row.scenarinoStartDate).format('YYYY-MM-DD HH') + ' - ' + moment(row.scenarinoEndDate).format('YYYY-MM-DD HH') + '</a>'
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
    $('.createRw').parent().removeClass('col-md-6');
    $('.selectOldQj').css('display','none');
    $('#createModal .modal-dialog').removeClass('w80');

  } else {
    modal.find('.createRun').text('创建并开始编辑情景');
    modal.find('.createRun').attr('data-href','xxxxxxx.html');

    $('.createRw').css('display','none');
    $('.createQj').css('display','block');
    $('.createRw').parent().removeClass('col-md-6');
    $('.selectOldQj').css('display','none');
    $('#createModal .modal-dialog').removeClass('w80');

    $('.createQj-in').css('display','none');
    $('.qjSelectWay').css('display','block');
    $('.new-foot').css('display','none');
    $('.old-foot').css('display','block');

  }

});

function newQj(){
  $('.qjSelectWay').css('display','none');
  $('.findQjDiv').css('display','none');
  $('.createQj-in').css('display','block');

  $('.new-foot').css('display','block');
  $('.old-foot').css('display','none');

}

function oldQj(){
  $('.qjSelectWay').css('display','none');
  $('.findQjDiv').css('display','block');
  $('.createQj-in').css('display','block');

  $('.new-foot').css('display','block');
  $('.old-foot').css('display','none');

}

var oldType = '';
function selectOldType(e){
  $('.selectOldQj .active').removeClass('active');
  $(e).addClass('active');
  oldType = $(e).attr('data-type');

  $('#oldQJ').bootstrapTable('destroy');
  initOldQj();
}

var qjId = '';
function findOldQj(){
  $('#createModal .modal-dialog').addClass('w80');
  $('.selectOldQj').css('display','block');
  $('.createQj').parent().addClass('col-md-6');

  initOldQj();

}

function oldSearch(){
  var params = $('#oldQJ').bootstrapTable('getOptions');
  params.queryParams = function (params) {
    var json;
    json = {
      "token": "",
      "data": {
        "queryName": params.searchText || '',
        "missionStatus":oldType,
        "pageNum": params.pageNumber,
        "pageSize": params.pageSize,
        "sort": '',
        "userId": userId
      }
    };
    json.data.queryName = $('.oldQjText').val();

//      json.data.missionName = $('.' + type).val();
    params = JSON.stringify(json);
    //console.info(params);
    return params;
  };
  $('#oldQJ').bootstrapTable('refresh', params);
}

function initOldQj(){
  $('#oldQJ').bootstrapTable({
    method: 'POST',
    //url: 'webApp/task01/testQJ.json',
//      url : BackstageIP+'/mission/get_mission_list',
    url : '/ampc/scenarino/get_CopyScenarinoList',
    dataType: "json",
    contentType: "application/json", // 请求远程数据的内容类型。
    toobar: '#rwToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:500,
    maintainSelected: true,
    clickToSelect: true,// 点击选中行
    pagination: true, // 在表格底部显示分页工具栏
    pageSize: 5, // 页面大小
    pageNumber: 1, // 页数
    pageList: [10],
    striped: true, // 使表格带有条纹
    sidePagination: "server",// 表格分页的位置 client||server
//    rowStyle: function (row, index) {
//      //if (index == 0) {
//      //  return {
//      //    classes: 'info'
//      //  };
//      //}
//      //return {};
//    },
    queryParams: function formPm(m) {
      var json = {
        "token": "",
        "data": {
          "queryName": m.searchText || '',
          "missionStatus":oldType,
          "pageNum": m.pageNumber,
          "pageSize": m.pageSize,
          "sort": '',
          "userId": userId
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
      qjId = row.scenarinoId;
      $('.selectOldQj .info').removeClass('info');
      $element.addClass('info');
    }

  });
}

function oldQJ_rw(v,row,i){
  return '<a href="javascript:">'+ row.missionName + '</a><br /><span> 模拟范围：'+ row.v1 +'</span><br /><span>清单：'+ row.esCouplingName +'</span>'
}


//
//function findRW(){
//  var url = '/mission/get_mission_list';
//  ajaxPost(url,{
//    "missionName": "",
//    "pageNum": 1,
//    "pageSize": 5,
//    "sort": "addTime desc",
//    "userId": 1 ,
//    "missionStatus":  "预评估"
//  })
//}
//
//function findQJ(){
//
//}




/*任务、情景创建*/
function create(e,run) {

  //$('.createSubmit').click();
  if(formCreate.form()){
    var url,urlName;
    var params,paramsName;
    var type = $(e).attr('data-type');
    if(type == 'rw'){
      url = '/mission/save_mission';
      urlName = '/mission/check_missioname';
      params = {};
      paramsName = {};
      params.missionName = paramsName.missionName = $('#rwName').val();
      params.missionDomainId = $('#mnfw').val();
      params.esCouplingId = $('#qd').val();
      params.missionStartDate = $('#rwStartDate').val();
      params.missionEndDate = $('#rwEndDate').val();
      params.userId = paramsName.userId = userId;
      if(run){
        params.createType = 2;
      }else{
        params.createType = 1;
      }
    }else{

      url = '/scenarino/save_scenarino';
      urlName = '/scenarino/check_scenarinoname';
      params = {};
      paramsName = {};
      params.scenarinoName = paramsName.scenarinoName = $('#qjName').val();
      params.missionId = paramsName.missionId = selectRW.missionId;
      params.scenarinoStartDate = $('#qjStartDate').val();
      params.scenarinoEndDate = $('#qjEndDate').val();
      params.userId = paramsName.userId = userId;
      params.scenarinoId = qjId;
      if(run){
        params.createType = 2;
      }else{
        params.createType = 1;
      }
    }

    ajaxPost(urlName,paramsName).success(function(res){
      if(res.data){
        ajaxPost(url, params).success(function () {
          //console.log('success');
          if (type == 'rw') {
            $('#rwTable').bootstrapTable('destroy');
            initRwTable();
          } else {
            $('#qjTable').bootstrapTable('destroy');
            initQjTable();
          }
          $('#createModal').modal('hide');
          swal('添加成功', '', 'success')
        }).error(function () {
//        console.log('error');
          $('#createModal').modal('hide');
          swal('添加失败', '', 'error')
        })
      }else{
        swal('名称重复', '', 'error')
      }
    }).error(function(){
      swal('校验失败', '', 'error')
    })


  }
}


/*初始化日期插件*/
function initDate() {
  $("#rwStartDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'month',
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
    startView: 'month',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      $('#rwStartDate').datetimepicker('setEndDate', date);
      $('#rwEndDate').datetimepicker('setStartDate', null);
    });


  $("#qjStartDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'month',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      if(date>moment(selectRW.missionStartDate).format('YYYY-MM-DD')){
        $('#qjEndDate').datetimepicker('setStartDate', date);
      }else{

      }

      $('#qjStartDate').datetimepicker('setEndDate', moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
    });
  $("#qjEndDate").datetimepicker({
    format: 'yyyy/mm/dd',
    minView: 'month',
    startView: 'month',
    language: 'zh-CN',
    autoclose: true,
    todayBtn: true
  })
    .on('changeDate', function(ev){
      var date = moment(ev.date).format('YYYY-MM-DD');
      if(date < moment(selectRW.missionEndDate).format('YYYY-MM-DD')){
        $('#qjStartDate').datetimepicker('setEndDate', date);
      }else{
        //$('#qjStartDate').datetimepicker('setEndDate', moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
      }


      $('#qjEndDate').datetimepicker('setStartDate', moment(selectRW.missionStartDate).format('YYYY-MM-DD'));
    });
}

/*修改名称*/
function rename(type, id) {
//    var url = 'rw.json';
  var url,urlName;
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
      ajaxPost(urlName,type == 'rw'?params:paramsName).success(function(res){
        if(res.data){
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
//        console.log('error');
            $('#createModal').modal('hide');
            swal('添加失败', '', 'error')
          })
        }else{
          swal('名称重复', '', 'error')
        }
      }).error(function(){
        swal('校验失败', '', 'error')
      })
    });

}




/*新改任务创建*/
function createRw(){
  var url = '/mission/save_mission';
  var urlName = '/mission/check_missioname';
  var params = {};
  var paramsName = {};
  params.missionName = paramsName.missionName = $('#rwName').val();
  params.missionDomainId = $('#mnfw').val();
  params.esCouplingId = $('#qd').val();
  params.missionStartDate = $('#rwStartDate').val();
  params.missionEndDate = $('#rwEndDate').val();
  params.userId = paramsName.userId = userId;
  params.missionStauts = $('#rwType').val();


  ajaxPost(urlName,paramsName).success(function(res){
    if(res.data){
      ajaxPost(url, params).success(function (res) {
        if(res.status == 0){
          $('#rwTable').bootstrapTable('destroy');
          initRwTable();
          $('#createRwModal').modal('hide');
          swal('添加成功', '', 'success')
        }else{
          $('#createRwModal').modal('hide');
          swal('添加失败', '', 'error')
        }
      }).error(function () {
        $('#createRwModal').modal('hide');
        swal('添加失败', '', 'error')
      })
    }else{
      swal('名称重复', '', 'error')
    }
  }).error(function(){
    swal('校验失败', '', 'error')
  })
}


/*新改情景创建*/
function createQj(){

}










