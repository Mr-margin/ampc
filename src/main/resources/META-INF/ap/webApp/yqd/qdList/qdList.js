/**
 * Created by lvcheng on 2017/3/16.
 */

// var delQDMap = {};
// initialize();
// function initialize(){
//   /**
//    * 设置导航条菜单
//    */
//   $("#crumb").html('<a href="#/yqd" style="padding-left: 15px;padding-right: 15px;">源清单</a>');
//   initQdListTable();
// }
// function initQdListTable() {
//   $('#qdList').bootstrapTable({
//     method: 'GET',
//       url: 'webApp/yqd/qdList/qd.json',
//     dataType: "json",
//     contentType: "application/json",
//     toobar: '#qdToolbar',
//     iconSize: "outline",
//     search: false,
//     searchAlign: 'right',
//     height:llqHeight-200,
//     maintainSelected: true,
//     clickToSelect: false,
//     pagination: true,
//     pageSize: 20,
//     pageNumber: 1,
//     pageList: [20],
//     striped: true,
//     sidePagination: "server",
//     rowStyle: function (row, index) {
//       return {};
//     },
//       queryParams: function formPm(m) {
//         console.log(m);
//         var json = {
//           "token": "",
//           "data": {
//
//           }
//         };
//
// //        return JSON.stringify(json);
//         return '';
//       },
//     responseHandler: function (res) {
//       return res.data
//     },
//     queryParamsType: "undefined",
//     silent: true,
//     onClickRow: function (row, $element) {
// //        $('.qj').val('');
// //        selectRW = row;
// //        $('#qjTable').bootstrapTable('destroy');
// //        initQjTable();
//       $('.info').removeClass('info');
//       $($element).addClass('info');
//     },
//
//     onCheck: function (row) {
//       $('.delQD').attr('disabled', false);
//       delQDMap[row.qdId] = 'true';
//     },
//     onUncheck: function (row) {
//       delete delQDMap[row.qdId];
//       if ($.isEmptyObject(delQDMap)) {
//         $('.delQD').attr('disabled', true)
//       }
//     },
//     onCheckAll: function (rows) {
//       $('.delQD').attr('disabled', false);
//       for (var i = 0; i < rows.length; i++) {
//         delQDMap[rows[i].qdId] = 'true';
//       }
//     },
//     onUncheckAll: function (rows) {
//       delQDMap = {};
//       $('.delQD').attr('disabled', true);
//     },
//     onLoadSuccess:function(data){
//
//     },
//
//     contextMenu: '#RWcontext-menu',//右键菜单ID
//     onContextMenuItem: function (row, $el) {
// //        if ($el.data("item") == "rename") {
// //          rename('rw', row.missionId);
// //        }
//     }
//   });
// }
//
// /*搜索事件*/
// function search(type) {
//   var params = $('#qdList').bootstrapTable('getOptions');
//   params.queryParams = function (params) {
//     var json;
//       json = {
//         "token": "",
//         "data": {
//         }
//       };
//       json.data.queryName = $('.qd').val();
// //    params = JSON.stringify(json);
//     params = '';
//     //console.info(params);
//     return params;
//   };
//   $('#qdList').bootstrapTable('refresh', params);
// }
//
/*创建新清单*/
function createNewQd(){
	var a = document.createElement('a');
	a.href = '#/newQd';
	a.click();
}
//点击创建新清单按钮跳转
$("#addnewqd").click(function(){
	$("#addnewqd").attr("href","#/newQd");
	$("#addnewqd").click();
});
//
// function delQD(){
//   console.log(delQDMap);
//   /*这里将有一个请求，处理清单删除*/
// }

/**
 *设置导航条信息
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span>');
defaultdatagridoption.method='get';
defaultdatagridoption.url='/ampc/webApp/yqd/qdList/qd.json';
clacdatagridcolumnwidth();
defaultdatagridoption.columns=[[
  {field:'qdmc',title:'清单名称',width:30*datagridcolumnwidthrem},
  {field:'qdms',title:'清单',width:10*datagridcolumnwidthrem},
  {field:'bdqd',title:'清单描述',width:10*datagridcolumnwidthrem},
  {field:'qgqd',title:'本地清单',width:10*datagridcolumnwidthrem},
  {field:'qgqd',title:'全国清单',width:10*datagridcolumnwidthrem},
  {field:'nf',title:'年份',width:10*datagridcolumnwidthrem},
  {field:'cjsj',title:'创建时间',width:10*datagridcolumnwidthrem},
  {field:'syls',title:'使用历史',width:10*datagridcolumnwidthrem},
]];
// defaultdatagridoption.columns=[[
//   {field:'qdmc',title:'清单名称'},
//   {field:'qdms',title:'清单'},
//   {field:'bdqd',title:'清单描述'},
//   {field:'qgqd',title:'本地清单'},
//   {field:'qgqd',title:'全国清单'},
//   {field:'nf',title:'年份'},
//   {field:'cjsj',title:'创建时间'},
//   {field:'syls',title:'使用历史'},
// ]];
// defaultdatagridoption.columns=[[
//   {field:'qdmc',title:'清单名称',width:3},
//   {field:'qdms',title:'清单',width:1},
//   {field:'bdqd',title:'清单描述',width:1},
//   {field:'qgqd',title:'本地清单',width:1},
//   {field:'qgqd',title:'全国清单',width:1},
//   {field:'nf',title:'年份',width:1},
//   {field:'cjsj',title:'创建时间',width:1},
//   {field:'syls',title:'使用历史',width:1}
// ]];
//groupFormatter:function(value, rows){
//	return value + ' - ' + rows.length + ' Item(s)';
//    }
$('#dg').datagrid(
  defaultdatagridoption
)