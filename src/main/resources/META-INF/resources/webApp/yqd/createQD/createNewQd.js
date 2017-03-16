/**
 * Created by lvcheng on 2017/3/16.
 */
function initQgqdListTable() {
  $('#qgqdList').bootstrapTable({
    method: 'GET',
      url: 'webApp/yqd/createQD/qgqd.json',
    //url: 'qgqd.json',
//      url : BackstageIP+'/mission/get_mission_list',
//      url : '/ampc/mission/get_mission_list',
    dataType: "json",
    contentType: "application/json",
    toobar: '#qgqdToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:llqHeight/2 -160,
    maintainSelected: true,
    clickToSelect: false,
    pagination: true,
    pageSize: 20,
    pageNumber: 1,
    pageList: [20],
    striped: true,
    sidePagination: "server",
    rowStyle: function (row, index) {
      return {};
    },
    queryParams: function formPm(m) {
      console.log(m);
      var json = {
        "token": "",
        "data": {

        }
      };

//        return JSON.stringify(json);
      return '';
    },
    responseHandler: function (res) {
      return res.data
    },
    queryParamsType: "undefined",
    silent: true,
    onClickRow: function (row, $element) {
      $('.info').removeClass('info');
      $($element).addClass('info');
    },

    onLoadSuccess:function(data){

    },

    contextMenu: '#RWcontext-menu',//右键菜单ID
    onContextMenuItem: function (row, $el) {
//        if ($el.data("item") == "rename") {
//          rename('rw', row.missionId);
//        }
    }
  });
}


function initBdqdListTable() {
  $('#bdqdList').bootstrapTable({
    method: 'GET',
      url: 'webApp/yqd/createQD/bdqd.json',
    //url: 'bdqd.json',
//      url : BackstageIP+'/mission/get_mission_list',
//      url : '/ampc/mission/get_mission_list',
    dataType: "json",
    contentType: "application/json",
    toobar: '#bdqdToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:llqHeight/2 -160,
    maintainSelected: true,
    clickToSelect: false,
    pagination: true,
    pageSize: 20,
    pageNumber: 1,
    pageList: [20],
    striped: true,
    sidePagination: "server",
    rowStyle: function (row, index) {
      return {};
    },
    queryParams: function formPm(m) {
      console.log(m);
      var json = {
        "token": "",
        "data": {

        }
      };

//        return JSON.stringify(json);
      return '';
    },
    responseHandler: function (res) {
      return res.data
    },
    queryParamsType: "undefined",
    silent: true,
    onClickRow: function (row, $element) {
      $('.info').removeClass('info');
      $($element).addClass('info');
    },

    onLoadSuccess:function(data){

    },

    contextMenu: '#RWcontext-menu',//右键菜单ID
    onContextMenuItem: function (row, $el) {
//        if ($el.data("item") == "rename") {
//          rename('rw', row.missionId);
//        }
    }
  });
}

/*获取所有年份*/
function getAllYear(){
  var url = '';
  var params = {
//      userId:userId
  };
//    ajaxPost(url,params).success(function(res){
//
//      $('#qgqdnf').empty();
//      $('#qgqdnf').append($('<option value=""></option>'));
//      for(var i=0;i<res.data.length;i++){
//        $('#qgqdnf').append($('<option value="'+ res.data[i] +'">'+ res.data[i] +'</option>'))
//      }
//    });
  var res = [2017,2016,2015];
  $('#qgqdnf').empty();
  $('#qgqdnf').append($('<option value=""></option>'));
  for(var i=0;i<res.length;i++){
    $('#qgqdnf').append($('<option value="'+ res[i] +'">'+ res[i] +'</option>'))
  }
}

/*选择全国清单年份事件*/
function selectQGQDyear(){
  var year = $('#qgqdnf').val();
  var params = $('#qgqdList').bootstrapTable('getOptions');
  params.queryParams = function (params) {
    var json;
    json = {
      "token": "",
      "data": {
      }
    };
    json.data.year = year;
//    params = JSON.stringify(json);
    params = '';
    //console.info(params);
    return params;
  };
  $('#qgqdList').bootstrapTable('refresh', params);
}

initialize();
function initialize(){
  /**
   * 设置导航条菜单
   */
  $("#crumb").html('<a href="#/yqd" style="padding-left: 15px;padding-right: 15px;">源清单</a>>><a href="#/newQd" style="padding-left: 15px;padding-right: 15px;">创建清单</a>');
  getAllYear();
  initQgqdListTable();
  initBdqdListTable();
}