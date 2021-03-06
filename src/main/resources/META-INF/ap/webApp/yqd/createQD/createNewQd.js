/**
 * Created by lvcheng on 2017/3/16.
 */
//initialize();
//function initialize(){
$(function(){
	
	
	/**
	 * 设置导航条菜单
	 */
	$("#crumb").html('<a href="#/yqd" style="padding-left: 15px;padding-right: 15px;">源清单</a>>><a href="#/newQd" style="padding-left: 15px;padding-right: 15px;">创建清单</a>');
	//查询清单年份
	getAllYear();
	//查询全国清单
	initQgqdListTable();
	//查询本地清单
	initBdqdListTable();
//}
});


//查询全国清单
function initQgqdListTable() {
	$('#qgqdList').bootstrapTable({
    method: 'post',
//    url: 'webApp/yqd/createQD/qgqd.json',
    url:'/ampc/NativeAndNation/find_nation',
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
    	var json = {
	        "token": "",
	        "data": {
	        	userId:userId,
	        }
    	};

//        return JSON.stringify(json);
    	return json;
    },
    responseHandler: function (res) {
      return res.data
    },
    queryParamsType: "limit",
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

//查询本地清单
function initBdqdListTable() {
  $('#bdqdList').bootstrapTable({
    method: 'post',
    url: '/ampc/NativeAndNation/find_natives',
    dataType: "json",
    contentType: "application/json",
//    contentType : "application/x-www-form-urlencoded",
    toobar: '#bdqdToolbar',
    iconSize: "outline",
    search: false,
    searchAlign: 'right',
    height:llqHeight/2 -160,
    maintainSelected: true,
    clickToSelect: false,
    pagination: true,
    pageSize: 10,
    pageNumber: 1,
    pageList: [20,50,100],
    striped: true,
    sidePagination: "server",
    rowStyle: function (row, index) {
      return {};
    },
    queryParams: function formPm(m) {
    	console.log(m);
    	var json = {
	        token: "",
	        data: {
	        	userId:userId,
	        }
	      };

//    	return JSON.stringify(json);
    	return json;
    },
    responseHandler: function (res) {
      return res.data
    },
    queryParamsType: "limit",
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

