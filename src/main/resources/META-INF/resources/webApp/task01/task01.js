/**
 * Created by lvcheng on 2017/2/21.
 */
//  console.log(BackstageIP);
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
    rwId:'',
    rwName:'',
    qjId:'',
    qjName:'',
    qjStartDate:'',
    qjEndDate:'',
    esCouplingId:'',
    esCouplingName:''
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

  });

    //$("#formCreate").validate({
    //  onfocusout: function(element){
    //    $(element).valid();
    //  }
    //});
  }


$(document).ready(function () {
  initialize();
  //initDate();
  //initRwDate();
  formVerify();
});

function initialize() {
  initRwTable();
  getMnfw();
  getQD();
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
      msg.content.esCouplingId = selectRW.esCouplingId;
      msg.content.esCouplingName = selectRW.esCouplingName;
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

/*function newQj(){
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

}*/

/*var oldType = '';
function selectOldType(e){
  $('.selectOldQj .active').removeClass('active');
  $(e).addClass('active');
  oldType = $(e).attr('data-type');

  $('#oldQJ').bootstrapTable('destroy');
  initOldQj();
}*/

var qjId = '';
/*function findOldQj(){
  $('#createModal .modal-dialog').addClass('w80');
  $('.selectOldQj').css('display','block');
  $('.createQj').parent().addClass('col-md-6');

  initOldQj();

}*/

/*function oldSearch(){
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
}*/

/*function initOldQj(){
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
}*/

/*function oldQJ_rw(v,row,i){
  return '<a href="javascript:">'+ row.missionName + '</a><br /><span> 模拟范围：'+ row.v1 +'</span><br /><span>清单：'+ row.esCouplingName +'</span>'
}*/


/*/!*任务、情景创建*!/
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
}*/




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


/*返回上一步操作*/
function returnLeft(type){
  if(type == 'rw'){
    $('.rwType').css('display','block');
    $('.rwCon').css('display','none');
    $('.createRwBtn').css('display','none');
    $('.return_S_rw').css('display','none');
    $('.rwTitle').html('创建任务')
  }else if(type == 'yqj'){
    $('.ypgQJType').css('display','block');
    $('.ypgQJCon').css('display','none');
    $('.createQjBtn').css('display','none');
    $('.return_S_qj').css('display','none');
  }else if(type == 'hqj'){
    $('.hpgQJType').css('display','block');
    $('.hpgQJCon').css('display','none');
    $('.createQjBtn').css('display','none');
    $('.return_S_qj').css('display','none');
  }
}



/*创建任务选择预评估或后评估*/
function selectType(type){
  var startDate;
  var endDate;
  if(type == 'y'){
    startDate = moment().subtract(2, 'w').format('YYYY-MM-DD');
    endDate = moment().add(1,'y').format('YYYY-MM-DD');
    rwSelectType = '预评估';
    $('.rwTitle').html('创建预评估任务')
  }else if(type == 'h'){
    startDate = '2007-01-01';
    endDate = moment().subtract(2, 'd').format('YYYY-MM-DD');
    rwSelectType = '后评估';
    $('.rwTitle').html('创建后评估任务')
  }

  $('.rwType').css('display','none');
  $('.rwCon').css('display','block');

  $('.createRwBtn').css('display','inline-block');
  $('.return_S_rw').css('display','inline-block');
  rwStartDate = moment().subtract(2,'w').format('YYYY-MM-DD');
  rwEndDate = moment().subtract(2,'d').format('YYYY-MM-DD');
  initRwDate(startDate,endDate);
}

/*创建情景选择类型*/
function selectQJtype(type){

  var url = '/scenarino/find_endTime';
  //var url = 'end.json';
  var params = {
    userId:userId
  };

  __dsp['jcqj'+selectRW.missionId].then(function(res){
    basisArr = res.data;
    if(basisArr)

    //reverse  倒序
    switch(type){
      case 'yy':
        $('.ypgQJType').css('display','none');
        $('.ypgQJCon').css('display','block');
        $('.dbqj').css('display','none');
        $('.createQjBtn').css('display','inline-block');
        $('.return_S_qj').css('display','inline-block');
        qjType = 1;
        params.scenType = qjType;

        setOption('#jcqj',basisArr);
        var dateArr = setSelectDate(basisArr[0].scenarinoStartDate,basisArr[0].scenarinoEndDate,basisArr[0].pathDate);
        $('#jcdate').empty();
        for(var i=0;i<dateArr.length;i++){
          $('#jcdate').append($('<option value="'+ dateArr[i] +'">'+ dateArr[i] +'</option>'))
        }
        var startD = moment(moment(dateArr[0])).add(1,'d').format('YYYY-MM-DD');
        $('#yStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));
        ajaxPost(url,params).success(function(res){
          selectEndDate = res.data.endTime;
          selectEndDate = moment(selectRW.missionEndDate).isBefore(selectEndDate)?moment(selectRW.missionEndDate).format('YYYY-MM-DD'):selectEndDate;
          var endDateArr = setSelectDate($('#yStartDate').val(),selectEndDate);
          $('#yEndDate').empty();
          for(var i=0;i<endDateArr.length;i++){
            $('#yEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
          }
        });

        break;
      case 'yh':
        $('.ypgQJType').css('display','none');
        $('.ypgQJCon').css('display','block');
        $('.dbqj').css('display','block');
        $('.createQjBtn').css('display','inline-block');
        $('.return_S_qj').css('display','inline-block');
        $('#dbqj').attr('disabled',true);
        $('#jcqj').removeAttr('disabled');
        $('#jcdate').removeAttr('disabled');
        $('#yEndDate').removeAttr('disabled');
        qjType = 2;
        params.scenType = qjType;

        setOption('#dbqj',basisArr);
        setOption('#jcqj',basisArr);

        var dateArr = setSelectDate(basisArr[0].scenarinoStartDate,basisArr[0].scenarinoEndDate,basisArr[0].pathDate);
        dateArr = dateArr.reverse();
        $('#jcdate').empty();
        for(var i=0;i<dateArr.length;i++){
          $('#jcdate').append($('<option value="'+ dateArr[i] +'">'+ dateArr[i] +'</option>'))
        }
        var startD = moment(moment(dateArr[0])).add(1,'d').format('YYYY-MM-DD');
        $('#yStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));

        ajaxPost(url,params).success(function(res){
          selectEndDate = res.data.endTime;
          selectEndDate = moment(selectRW.missionEndDate).isBefore(selectEndDate)?moment(selectRW.missionEndDate).format('YYYY-MM-DD'):selectEndDate;
          var endDateArr = setSelectDate($('#yStartDate').val(),selectEndDate);
          $('#yEndDate').empty();
          for(var i=0;i<endDateArr.length;i++){
            $('#yEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
          }
        });



        break;
      case 'hh':
        $('.hpgQJType').css('display','none');
        $('.hpgQJCon').css('display','block');
        $('.createQjBtn').css('display','inline-block');
        $('.return_S_qj').css('display','inline-block');
        $('.diffNo').css('display','block');
        $('.spinup').css('display','none');
        $('#dbqj1').attr('disabled',true);
        $('#jcqj1').removeAttr('disabled');
        $('#jcdate1').removeAttr('disabled');
        $('#hEndDate').removeAttr('disabled');
        qjType = 2;

        setOption('#dbqj1',basisArr);
        setOption('#jcqj1',basisArr);
        var dateArr = setSelectDate(basisArr[0].scenarinoStartDate,moment(selectRW.missionEndDate).subtract(2,'d').format('YYYY-MM-DD'));
        dateArr = dateArr.reverse();
        $('#jcdate1').empty();
        for(var i=0;i<dateArr.length;i++){
          $('#jcdate1').append($('<option value="'+ dateArr[i] +'">'+ dateArr[i] +'</option>'))
        }
        var startD = moment(moment(dateArr[0])).add(1,'d').format('YYYY-MM-DD');
        $('#hStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));


        var endDateArr = setSelectDate($('#hStartDate').val(),moment(selectRW.missionEndDate));
        $('#hEndDate').empty();
        for(var i=0;i<endDateArr.length;i++){
          $('#hEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
        }
        break;
      case 'hj':
        $('.hpgQJType').css('display','none');
        $('.hpgQJCon').css('display','block');
        $('.createQjBtn').css('display','inline-block');
        $('.return_S_qj').css('display','inline-block');
        $('.diffNo').css('display','none');
        $('.spinup').css('display','block');
        $('#hEndDate').attr('disabled',true);
        qjType = 3;
        $('#hStartDate').empty().append($('<option value="'+ moment(selectRW.missionStartDate).format('YYYY-MM-DD') +'">'+ moment(selectRW.missionStartDate).format('YYYY-MM-DD') +'</option>'));
        $('#hEndDate').empty().append($('<option value="'+ moment(selectRW.missionEndDate).format('YYYY-MM-DD') +'">'+ moment(selectRW.missionEndDate).format('YYYY-MM-DD') +'</option>'));



        break;
    }

  },function(){
    console.log('基础情景获取失败！！！！！');
    if(type == 'hj'){
      $('.hpgQJType').css('display','none');
      $('.hpgQJCon').css('display','block');
      $('.createQjBtn').css('display','inline-block');
      $('.return_S_qj').css('display','inline-block');
      $('.diffNo').css('display','none');
      $('.spinup').css('display','block');
      $('#hEndDate').attr('disabled',true);
      qjType = 3;
      $('#hStartDate').empty().append($('<option value="'+ moment(selectRW.missionStartDate).format('YYYY-MM-DD') +'">'+ moment(selectRW.missionStartDate).format('YYYY-MM-DD') +'</option>'));
      $('#hEndDate').empty().append($('<option value="'+ moment(selectRW.missionEndDate).format('YYYY-MM-DD') +'">'+ moment(selectRW.missionEndDate).format('YYYY-MM-DD') +'</option>'));
    }
  })




}

function checkedDB(t){
  console.log(t);
  if($(t)[0].checked){
    $('#dbqj').removeAttr('disabled');
    $('#jcqj').attr('disabled',true);
    $('#jcdate').attr('disabled',true);
    $('#yEndDate').attr('disabled',true);

    $('#dbqj1').removeAttr('disabled');
    $('#jcqj1').attr('disabled',true);
    $('#jcdate1').attr('disabled',true);
    $('#hEndDate').attr('disabled',true);
  }else{
    $('#dbqj').attr('disabled',true);
    $('#jcqj').removeAttr('disabled');
    $('#jcdate').removeAttr('disabled');
    $('#yEndDate').removeAttr('disabled');

    $('#dbqj1').attr('disabled',true);
    $('#jcqj1').removeAttr('disabled');
    $('#jcdate1').removeAttr('disabled');
    $('#hEndDate').removeAttr('disabled');
  }
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
  params.missionStartDate = rwStartDate;
  params.missionEndDate = rwEndDate;
  params.userId = paramsName.userId = userId;
  params.missionStauts = rwSelectType;

  ajaxPost(urlName,paramsName).success(function(res){
    if(res.data){
      ajaxPost(url, params).success(function (res) {
        if(res.status == 0){
          $('#rwTable').bootstrapTable('destroy');
          initRwTable();
          $('#createRwModal').modal('hide');
          $('#rwName').val('');
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

/*初始化日期插件*/
function initRwDate(s,e){
  $('#rwDate').daterangepicker({
    singleDatePicker: false,  //显示单个日历
    timePicker:false,  //允许选择时间
    timePicker24Hour:true, //时间24小时制
    minDate:s,//最早可选日期
    maxDate:e,//最大可选日期
    locale: {
      format: "YYYY-MM-DD",
      separator: " 至 ",
      applyLabel: "确定", //按钮文字
      cancelLabel: "取消",//按钮文字
      weekLabel: "W",
      daysOfWeek: [
        "日","一","二","三","四","五","六"
      ],
      monthNames: [
        "一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"
      ],
      firstDay: 1
    },
    "startDate": moment().subtract(2,'w'),
    "endDate": moment().subtract(2,'d'),
    "opens": "right"
  },function(start, end, label) {
    rwStartDate = start.format('YYYY-MM-DD');
    rwEndDate = end.format('YYYY-MM-DD');
    console.log(rwStartDate, rwEndDate, label);
  })
}

/*创建情景时选择模态框*/
function createQJselect(){
  console.log(selectRW);
  var oldQJUrl = '/scenarino/find_scenarino_time';
  //var oldQJUrl = 'date.json';
  var oldQJparams = {
    userId:userId,
    missionId:selectRW.missionId
  };
  if((!__dsp['jcqj'+selectRW.missionId]) || (basisArr.length ==0)){
    __dsp['jcqj'+selectRW.missionId] = ajaxPost(oldQJUrl,oldQJparams);
  }

  if(selectRW.missionStatus == "预评估"){
    $('#createYpQjModal').modal('show');
    returnLeft('yqj');
  }else if(selectRW.missionStatus == "后评估"){
    $('#createHpQjModal').modal('show');
    returnLeft('hqj');
  }
}

/*配置日期*/
function setSelectDate(qjS,qjE,pathD){
  var dateArr = [];
  if(pathD){
    var p = moment(pathD);
    while(p.format('YYYY-MM-DD') > qjS){
      dateArr.push(p.subtract(1,'d').format('YYYY-MM-DD'));
      console.log(p.format('YYYY-MM-DD'))
    }
  }else{
    var e = moment(qjE);
    while(e.format('YYYY-MM-DD') > qjS){
      dateArr.push(e.subtract(1,'d').format('YYYY-MM-DD'));
      console.log(e.format('YYYY-MM-DD'))
    }
  }
  return dateArr
}

/*基础情景改变的时候，基础日期随之改变*/
function changeJcqj(t){
  var index = $(t).val();
  var selectJcqj = basisArr[index];
  var dateArr = setSelectDate(selectJcqj.scenarinoStartDate,selectJcqj.scenarinoEndDate,selectJcqj.pathDate);
  if(qjType == 2){
    dateArr = dateArr.reverse();
  }
  $('#jcdate').empty();
  for(var i=0;i<dateArr.length;i++){
    $('#jcdate').append($('<option value="'+ dateArr[i] +'">'+ dateArr[i] +'</option>'))
  }
  var startD = moment(moment(dateArr[0])).add(1,'d').format('YYYY-MM-DD');
  $('#yStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));

  var endDateArr = setSelectDate($('#yStartDate').val(),selectEndDate);
  $('#yEndDate').empty();
  for(var i=0;i<endDateArr.length;i++){
    $('#yEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
  }
}

function changeJcDate(t){
  var date = $(t).val();
  var startD = moment(moment(date)).add(1,'d').format('YYYY-MM-DD');
  $('#yStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));

  var endDateArr = setSelectDate($('#yStartDate').val(),selectEndDate);
  $('#yEndDate').empty();
  for(var i=0;i<endDateArr.length;i++){
    $('#yEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
  }
}

/*基础情景改变的时候，基础日期随之改变*/
function changeJcqj1(t){
  var index = $(t).val();
  var selectJcqj = basisArr[index];
  var dateArr = setSelectDate(selectJcqj.scenarinoStartDate,moment(selectRW.missionEndDate).subtract(2,'d').format('YYYY-MM-DD'));
  dateArr = dateArr.reverse();
  $('#jcdate1').empty();
  for(var i=0;i<dateArr.length;i++){
    $('#jcdate1').append($('<option value="'+ dateArr[i] +'">'+ dateArr[i] +'</option>'))
  }
  var startD = moment(moment(dateArr[0])).add(1,'d').format('YYYY-MM-DD');
  $('#hStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));

  var endDateArr = setSelectDate($('#hStartDate').val(),moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
  $('#hEndDate').empty();
  for(var i=0;i<endDateArr.length;i++){
    $('#hEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
  }
}

function changeJcDate1(t){
  var date = $(t).val();
  var startD = moment(moment(date)).add(1,'d').format('YYYY-MM-DD');
  $('#hStartDate').empty().append($('<option value="'+ startD +'">'+ startD +'</option>'));

  var endDateArr = setSelectDate($('#hStartDate').val(),moment(selectRW.missionEndDate).format('YYYY-MM-DD'));
  $('#hEndDate').empty();
  for(var i=0;i<endDateArr.length;i++){
    $('#hEndDate').append($('<option value="'+ endDateArr[i] +'">'+ endDateArr[i] +'</option>'))
  }
}


/*添加option*/
function setOption(ele,res){
  $(ele).empty();
  for(var i=0;i<res.length;i++){
    if(((ele == '#dbqj')||(ele == '#dbqj1')) && (res[i].scenarinoId == -1))continue;
    $(ele).append($('<option value="'+ i +'">'+ res[i].scenarinoName +'</option>'))
  }
}

/*新改情景创建*/
function createQj(type){
  var url,urlName;
  var params,paramsName;
  url = '/scenarino/save_scenarino';
  urlName = '/scenarino/check_scenarinoname';
  if(type == 'ypg' ){
    params = {};
    paramsName = {};
    params.scenarinoName = paramsName.scenarinoName = $('#yName').val();
    params.userId = paramsName.userId = userId;
    params.missionId = paramsName.missionId = selectRW.missionId;
    params.missionType = selectRW.missionStatus;
    params.scenarinoStartDate = $('#yStartDate').val();
    params.scenarinoEndDate = $('#yEndDate').val();
    params.scenType = qjType;
    if(qjType == 1){
      params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
      params.basisTime = $('#jcdate').val();
    }else if(qjType == 2){
      if($('.dbqj input[type=checkbox]').checked){
        params.basisScenarinoId = basisArr[$('#dbqj').val()].scenarinoId;
      }else{
        params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
        params.basisTime = $('#jcdate').val();
      }
    }
    /*
     * 这里应该还会有几个参数
     * */


    /*
    * 预评估任务，后评估情景
    * 判断$('.changeDB')是否有被checked
    * 然后考虑组织参数
    * */
  }else{
    params = {};
    paramsName = {};
    params.scenarinoName = paramsName.scenarinoName = $('#hName').val();
    params.missionId = paramsName.missionId = selectRW.missionId;
    params.scenarinoStartDate = $('#qjStartDate').val();
    params.scenarinoEndDate = $('#qjEndDate').val();
    params.userId = paramsName.userId = userId;
    params.scenarinoId = qjId;
    /*
     * 这里应该还会有几个参数
     * */


    /*
     * 预评估任务，后评估情景
     * 判断$('.changeDB')是否有被checked
     * 然后考虑组织参数
     * */
  }

  ajaxPost(urlName,paramsName).success(function(res){
    if(res.data){
      ajaxPost(url, params).success(function () {
        //console.log('success');

        $('#createModal').modal('hide');
        swal('添加成功', '', 'success')
      }).error(function () {
        swal('添加失败', '', 'error')
      })
    }else{
      swal('名称重复', '', 'error')
    }
  }).error(function(){
    swal('校验失败', '', 'error')
  })


}

/*获取模拟范围*/
function getMnfw(){
  var url = '';
  var params = {
    userId:userId
  };
  ajaxPost(url,params).success(function(res){

    for(var i=0;i<res.data;i++){
      $('#mnfw').append($('<option value=""></option>'))
    }

  }).error(function(){
    console.log('模拟范围未获取到！！！！')
  })
  $('#mnfw').append($('<option value="1">范围1</option>'))
}

/*获取清单*/
function getQD(){
  var url = '';
  var params = {
    userId:userId
  };
  ajaxPost(url,params).success(function(res){

    for(var i=0;i<res.data;i++){
      $('#qd').append($('<option value=""></option>'))
    }

  }).error(function(){
    console.log('清单未获取到！！！！')
  })
  $('#qd').append($('<option value="1">清单1</option>'))
}




function ajaxPost1(url, parameter) {
  parameterPar.data = parameter;
  var p = JSON.stringify(parameterPar);
  //return $.ajax(BackstageIP + url, {
  return $.ajax('webApp/task01/'+ url, {
    contentType: "application/json",
    type: "GET",
    async: true,
    dataType: 'JSON',
    //data: p
  })
}






