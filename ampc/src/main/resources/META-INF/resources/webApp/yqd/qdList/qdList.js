/**
 * Created by lvcheng on 2017/3/16.
 */

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

    },
    onUncheck: function (row) {

    },
    onCheckAll: function (rows) {

    },
    onUncheckAll: function (rows) {

    },
    onLoadSuccess:function(data){

    },
    
    contextMenu: '#RWcontext-menu',
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
