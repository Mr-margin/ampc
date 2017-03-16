/**
 * Created by lvcheng on 2017/3/16.
 */

var delQDMap = {};

initQdListTable();
function initQdListTable() {
  $('#qdList').bootstrapTable({
    method: 'GET',
      url: 'webApp/yqd/qdList/qd.json',
    //url: 'qd.json',
//      url : BackstageIP+'/mission/get_mission_list',
//      url : '/ampc/mission/get_mission_list',
    dataType: "json",
    contentType: "application/json",
    toobar: '#qdToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    //height:453,
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
//        $('.qj').val('');
//        selectRW = row;
//        $('#qjTable').bootstrapTable('destroy');
//        initQjTable();
      $('.info').removeClass('info');
      $($element).addClass('info');
    },

    onCheck: function (row) {
      $('.delQD').attr('disabled', false);
      delQDMap[row.qdId] = 'true';
    },
    onUncheck: function (row) {
      delete delQDMap[row.qdId];
      if ($.isEmptyObject(delQDMap)) {
        $('.delQD').attr('disabled', true)
      }
    },
    onCheckAll: function (rows) {
      $('.delQD').attr('disabled', false);
      for (var i = 0; i < rows.length; i++) {
        delQDMap[rows[i].qdId] = 'true';
      }
    },
    onUncheckAll: function (rows) {
      delQDMap = {};
      $('.delQD').attr('disabled', true);
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

/*搜索事件*/
function search(type) {
  var params = $('#qdList').bootstrapTable('getOptions');
  params.queryParams = function (params) {
    var json;
      json = {
        "token": "",
        "data": {
        }
      };
      json.data.queryName = $('.qd').val();
//    params = JSON.stringify(json);
    params = '';
    //console.info(params);
    return params;
  };
  $('#qdList').bootstrapTable('refresh', params);
}

/*创建新清单*/
function createNewQd(){
  var a = document.createElement('a');
  a.href = '#/newQd';
  a.click();
}

function delQD(){
  console.log(delQDMap);
  /*这里将有一个请求，处理清单删除*/
}
