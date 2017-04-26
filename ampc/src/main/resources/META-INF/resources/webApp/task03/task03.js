/**
 * 白色区域的收缩效果
 */
//$('.collapse-link').click(function () {
//    var ibox = $(this).closest('div.ibox');
//    var button = $(this).find('i');
//    var content = ibox.find('div.ibox-content');
//    content.slideToggle(200);
//    button.toggleClass('fa-chevron-up').toggleClass('fa-chevron-down');
//    ibox.toggleClass('').toggleClass('border-bottom');
//    setTimeout(function () {
//        ibox.resize();
//        ibox.find('[id^=map-]').resize();
//    }, 50);
//});

$("#show").click(function () {
  if ($("#slider_wrap").css("display") == "none") {
    $("#slider_wrap").show();
    $("#custom").css("background-position", "right -18px");
  } else {
    $("#slider_wrap").hide();
    $("#custom").css("background-position", "right 8px");
  }
});

var ls = window.sessionStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;

if (!qjMsg) {
  qjMsg = JSON.parse(ls.getItem('yaMsg'));
} else {
  ls.setItem('yaMsg', JSON.stringify(qjMsg));
}
console.log(JSON.stringify(qjMsg));

/**
 * 完成按钮
 */
function complete_Button() {
//	var msg1 = {
//	'id': 'jpfxMessage',
//	'content': {}
//	};
//	msg1.content.rwId = qjMsg.rwId;
//	msg1.content.rwName = qjMsg.rwName;
//	msg1.content.qjId = qjMsg.qjId;
//	msg1.content.qjName = qjMsg.qjName;
//	vipspa.setMessage(msg);
  var a = document.createElement('a');
  a.href = '#/yabj';
  a.click();
}

/**
 * 存入预案库
 */
function Deposit_Reserve_plan() {
  var paramsName = {};
  paramsName.userId = userId;
  paramsName.planId = qjMsg.planId;

  ajaxPost('/plan/iscopy_plan', paramsName).success(function (res) {//设置可复用预案
    console.log(JSON.stringify(res));
    if (res.status == 0) {
      swal({
        title: "存入预案库成功",
        text: "是否继续编辑预案！",
        type: "success",//"warning", "error", "success" and "info".
        showCancelButton: true,//如果设置为真,一个“取消”按钮将显示,用户可以点击将模态。
        confirmButtonColor: "#00A4EF",
        confirmButtonText: "继续编辑",
        cancelButtonText: "完成编辑",
        closeOnConfirm: false
      }, function (isConfirm) {
        if (isConfirm) {
          window.location.reload();
        } else {
          var a = document.createElement('a');
          a.href = '#/yabj';
          a.click();
        }
      });

    } else {
      swal('连接错误/plan/get_planInfo' + JSON.stringify(res), '', 'error');
    }
  });
}

/**
 * 模态窗口的地图切换全屏
 */
function m_gis_q() {
  if ($("#left_div").is(":hidden")) {
    $("#right_top_div").css("height", "50%");
    $("#right_div").attr("class", "col-md-6");
    $("#left_div").show();//左侧两部分显示
    $("#right_bottom_div").show();//右侧底部显示
  } else {
    $("#left_div").hide();//左侧两部分显示
    $("#right_bottom_div").hide();//右侧底部显示
    $("#right_top_div").css("height", "100%");
    $("#right_div").attr("class", "col-md-12");
  }
}

/**
 * 设置导航条菜单
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景编辑</a>>><span style="padding-left: 15px;padding-right: 15px;">措施编辑</span>');


$('.csCon').removeClass('disNone');
$('.csCon .nowRw span').html(qjMsg.rwName);
$('.csCon .nowQj span').html(qjMsg.qjName);
$('.csCon .nowArea span').html(qjMsg.areaName);
$('.csCon .nowTime span').html(qjMsg.planName);
$('.csCon .seDate span').html(moment(qjMsg.timeStartDate).format('YYYY-MM-DD HH') + '<br>至<br>' + moment(qjMsg.timeEndDate).format('YYYY-MM-DD HH'));

var Codes = [];
$.each(qjMsg.cityCodes, function (i, col) {
  $.each(col, function (k, vol) {
    Codes.push(k);
  });
});
$.each(qjMsg.countyCodes, function (k, col) {
  $.each(col, function (k, vol) {
    Codes.push(k);
  });
});
$.each(qjMsg.provinceCodes, function (k, col) {
  $.each(col, function (k, vol) {
    Codes.push(k);
  });
});


hyc();
metTable_hj_info();

/**
 * 时间戳转成日期格式
 * @param nS
 * @returns
 */
function getLocalTime(nS) {
  return moment(nS).format('YYYY-MM-DD HH');
}

/**
 * 获取用户的行业与措施，页面初始化赋值
 */
function hyc() {
  var paramsName = {};
  paramsName.userId = userId;
  paramsName.planId = qjMsg.planId;
  paramsName.timeId = qjMsg.timeId;
  paramsName.timeStartTime = qjMsg.timeStartDate;
  paramsName.timeEndTime = qjMsg.timeEndDate;

  $("#dangqianrenwu").html("当前任务：" + qjMsg.rwName);
  $("#dangqianqingjing").html("当前情景：" + qjMsg.qjName);
  $("#dangqianquyu").html("当前区域：" + qjMsg.areaName);
  $("#dangqianshiduan").html("当前时段：" + getLocalTime(qjMsg.timeStartDate) + " 时 至 " + getLocalTime(qjMsg.timeEndDate) + " 时");

  m_planId = qjMsg.planId;
  $("#accordion").html("");
  ajaxPost('/plan/get_planInfo', paramsName).success(function (res) {
//		console.log(JSON.stringify(res));
    var accordion_html = "";
    if (res.status == 0) {
      $.each(res.data, function (i, col) {

//      var inn = i == 0 ? "in" : "";//第一个手风琴页签打开
    	var inn = "";
    	if(stname){
    		inn = stname == col.sectorsName ? "in" : "";
    	}
    	
        accordion_html += '<div class="panel panel-default" val_name="' + col.sectorsName + '"><div class="panel-heading" style="background-color: #FFF;">';
        accordion_html += '<a data-toggle="collapse" data-parent="#accordion" style="font-weight: 700;" href="#collapse' + i + '"><h5 onclick="metTable_hj_info(\'' + col.sectorsName + '\');" class="panel-title">' + col.sectorsName + '';
        if (col.count != "0") {
          accordion_html += '<code class="pull-right">已使用&nbsp;' + col.count + '&nbsp;条措施</code>';
        }
        accordion_html += '</h5></a></div><div id="collapse' + i + '" class="panel-collapse collapse ' + inn + '" style="background-color: #EDF7FF;"><div class="panel-body" style="border: 0px;">';

        $.each(col.measureItems, function (j, vol) {
          accordion_html += '<div class="col-md-6 c6center">';
          var bggh = true;
          if (col.planMeasure.length > 0) {
            $.each(col.planMeasure, function (k, ool) {//循环已选中的措施
              if (ool.measureId == vol.mid) {//已选中的措施和措施列表的ID相同，标签需要更改效果
                accordion_html += '<a class="btn btn-success-cs-w" id="' + ool.planMeasureId + '" style="width:80%;" onclick="open_cs(\'' + col.sectorsName + '\',\'' + vol.measureame + '\',\'' + vol.mid + '\',\'' + ool.planMeasureId + '\');"><i class="fa fa-check"> </i>&nbsp;&nbsp;&nbsp;' + vol.measureame + '</a>';
                bggh = false;//如果已选中，在这里添加，否则需要添加未选中的标签
              }
            });
          }
          if (bggh) {
            accordion_html += '<a class="btn btn-success-cs btn-outline" style="width:80%;" onclick="open_cs(\'' + col.sectorsName + '\',\'' + vol.measureame + '\',\'' + vol.mid + '\',\'null\');"><i class="fa fa-ban"> </i>&nbsp;&nbsp;&nbsp;' + vol.measureame + '</a>';
          }
          accordion_html += '</div>';
        });
        accordion_html += '</div></div></div>';
      });
      $("#accordion").html(accordion_html);

    } else {
      swal('连接错误/plan/get_planInfo', '', 'error');
//			swal('添加成功', '', 'success');
    }
  }).error(function () {
    swal('校验失败', '', 'error')
  })
}

/**
 * 获取措施汇总
 */
function metTable_hj_info(pa_name) {

  $('#metTable_hj').bootstrapTable('destroy');

  $("#hz_de").hide();
  $("#hz_up").hide();
  $("#jianpaijisuan").hide();

  var hangye = "";//手风琴当前打开的内容

  if (typeof pa_name != "undefined") {
    hangye = pa_name;
  } else {
    //循环手风琴列表下所有的一级子节点，查找哪个正在打开
    $("#accordion").children().each(function () {
      var e = $(this);
      e.children().each(function () {//再循环一次，这次下面有两个div，一个标题，一个内容
        if ($(this).is('.in')) {
          hangye = e.attr("val_name");
        }
      });
    });
  }

  //行业的查询状态
  var hangyede_type = "";
  $("#hangyedetype").children().each(function () {
    if ($(this).is('.active')) {
      hangyede_type = $(this).attr("val_name");
    }
  });

  var columnsw = [];
  columnsw.push({field: 'state', title: '', align: 'center', checkbox: true});
  columnsw.push({field: 'sectorName', title: '行业', align: 'center'});
  columnsw.push({field: 'measureName', title: '措施', align: 'center'});
  columnsw.push({field: 'implementationScope', title: '点源实际范围', align: 'center'});
  columnsw.push({
    field: 'reduct', title: '涉及年化排放占比', align: 'center', formatter: function (value, row, index) {

      return value;
    }
  });
  columnsw.push({
    field: 'ratio', title: '年化减排比例', align: 'center', formatter: function (value, row, index) {

      return value;
    }
  });
  $('#metTable_hj').bootstrapTable({
    method: 'POST',
    url: "/ampc/measure/get_measureList",
    dataType: "json",
    columns: columnsw, //列
    clickToSelect: true,// 点击选中行
    pagination: false, // 在表格底部显示分页工具栏
    singleSelect: true,//设置True 将禁止多选
    striped: false, // 使表格带有条纹
    silent: true, // 刷新事件必须设置
    queryParams: function (params) {
      var data = {};
      data.planId = qjMsg.planId;
      data.userId = userId;
      data.stainType = $('#hz_wrw').val();
      if (hangyede_type == "dq") {
        data.sectorName = hangye;
      }
      console.log(JSON.stringify({"token": "", "data": data}));
      return JSON.stringify({"token": "", "data": data});
    },
    queryParamsType: "limit", //参数格式,发送标准的RESTFul类型的参数请求
    contentType: "application/json", // 请求远程数据的内容类型。
    responseHandler: function (res) {

      if (res.status == 0) {

        $.each(res.data.rows, function (i, col) {

          if (typeof res.data.rows[i].reduct != "undefined") {
            if (res.data.rows[i].reduct == "-9999.0") {
              res.data.rows[i].reduct = "-";
            } else {
              res.data.rows[i].reduct = res.data.rows[i].reduct + "%";
            }
          } else {
            res.data.rows[i].reduct = "-";
          }
          if (typeof res.data.rows[i].ratio != "undefined") {

            if (res.data.rows[i].ratio == "-9999.0") {
              res.data.rows[i].ratio = "-";
            } else {
              res.data.rows[i].ratio = res.data.rows[i].ratio + "%";
            }

          } else {
            res.data.rows[i].ratio = "-";
          }


        });

        return res.data.rows;

      } else if (res.status == 1000) {
        swal('/measure/get_measureList参数错误', '', 'error')
        return "";
      }


    },
    onClickRow: function (row, $element) {
//      $('.success').removeClass('success');
//      $($element).addClass('success');
    	if (row.state == true) {//如果被选中
            $("#hz_de").show();
            $("#hz_up").show();
          } else {
            $("#hz_de").hide();
            $("#hz_up").hide();
          }
    },
    onLoadSuccess: function (data) {
      if (data.length > 0) {
        $("#jianpaijisuan").show();
      }
    },
    onCheck: function (row){
    	$("#hz_de").show();
        $("#hz_up").show();
    },
    onUncheck: function (row){
    	$("#hz_de").hide();
        $("#hz_up").hide();
    }
  });

}

/**
 * 更换行业类型查询查询
 */
function hangye_type_info() {
  setTimeout(function () {
    metTable_hj_info();
  }, 100);
}

/**
 * 汇总的修改按钮
 */

function hz_up() {
  var row = $('#metTable_hj').bootstrapTable('getSelections');
  if (row.length > 0) {
    open_cs(row[0].sectorName, row[0].measureName, row[0].measureId, row[0].planMeasureId);
  }
}

/**
 * 汇总的删除按钮
 */
function hz_de() {
  var row = $('#metTable_hj').bootstrapTable('getSelections');
  if (row.length > 0) {
    var paramsName = {};
    paramsName.planMeasureId = row[0].planMeasureId;
    paramsName.sectorName = row[0].sectorName;
    paramsName.planId = row[0].planId;
    paramsName.userId = userId;
    paramsName.scenarinoId = qjMsg.qjId;

    ajaxPost('/measure/delete_measure', paramsName).success(function (res) {
      metTable_hj_info();
      hyc();
    });
  }
}

/**
 * 减排计算按钮
 */
function jianpaijisuan() {
  var row = $('#metTable_hj').bootstrapTable('getData');//获取所有的数据
  var planMeasureIds = "";
  $.each(row, function (i, col) {
    planMeasureIds += col.planMeasureId + ",";
  });
  zmblockUI("#jianpaijisuanbox", 'start');
  console.log(1);
  // $("#zhezhao").show();//计算中
  // $("#zhezhao_title").show();
  ajaxPost('/jp/pmjp', {"planMeasureIds": planMeasureIds.substring(0, planMeasureIds.length - 1)}).success(function (res) {
//		console.log(JSON.stringify(res));
    metTable_hj_info();
    // $("#zhezhao").hide();//计算中
    // $("#zhezhao_title").hide();
    zmblockUI("#jianpaijisuanbox", 'end');
  }).error(function (res) {
    // $("#zhezhao").hide();//计算中
    // $("#zhezhao_title").hide();
    zmblockUI("#jianpaijisuanbox", 'end');
    swal('连接错误', '', 'error');
  });
}


var sc_val = {};//存储最终的自措施条件
var sc_v1 = {};
var temp_val = {};//单次查询的缓存
var ty = "";
var temp_val_v1 = {};//单次查询的缓存
var xishu_temp = [];//需要填写的内容
var xishu_temp_ch = [];//需要填写的内容中文
var xishu_temp_v = [];//需要填写的内容默认值
//var show_lenght =[];//要显示的列长度
var measureame_temp = "";//措施名称
var measureame_temp_en = "";//措施名称英文
var query = [];//返回的条件内容
var zongdianyaun = [];//当前行业措施下的总的点源数据

var columns = [];//模态框内表格显示的表头
var point_z = [];//点源总数

var m_mid, m_planId, m_sectorName, m_planMeasureId;
var stname;//当前选择的行业
/**
 * 打开措施的窗口，数据初始化
 * sectorsName:行业名称
 * measureame:措施名称
 * mid:措施ID
 * planMeasureId: 已经选中的措施ID，如果为null，证明是新建，否则为修改
 */
function open_cs(sectorsName, measureame, mid, planMeasureId) {
  $("#measureame").html("措施：" + measureame);//发开弹出窗，设置标题、行业、措施等内容
  $("#sectorsName").html("行业：" + sectorsName);
  stname = sectorsName;
  $("#dianyaunzushu").html("");//清空点源与占比
  $("#xiangxizhibiao").html("");

  measureame_temp = measureame;
  m_mid = mid;
  m_sectorName = sectorsName;
  m_planMeasureId = planMeasureId;

  var paramsName = {};
  paramsName.userId = userId;
  paramsName.sectorName = sectorsName;
  paramsName.measureId = mid;
  paramsName.planId = qjMsg.planId;
  paramsName.planMeasureId = planMeasureId;

  sc_val = {};//初始化缓存
  sc_val.bigIndex = qjMsg.esCouplingId;
  sc_val.smallIndex = sectorsName;//记录行业
  sc_val.filters = [];
  sc_val.summary = {};
  sc_val.regionIds = Codes;

  columns = [];
  point_z = [];//点源总数

  //地图窗口正常化显示
  $("#right_top_div").css("height", "50%");
  $("#right_div").attr("class", "col-md-6");
  $("#left_div").show();//左侧两部分显示
  $("#right_bottom_div").show();//右侧底部显示

  var b_data = [];

  ajaxPost('/measure/get_measureQuery', paramsName).success(function (res) {
//		console.log(JSON.stringify(res));
    $("#sc_conter").html("");//按属性筛选条件
    $("#qiye_name").val("");//按名称筛选条件
    $("#xishuMO").html("");//控制系数div
    $("#xishuMO").hide();//控制系数div
    $("#xishuMOB").hide();//控制系数按钮
    $("#mic").hide();//筛选结果div
    $("#mic_name").hide();//按名称筛选结果div隐藏

    $("#shaixuan_num").html("");//筛选点源
    $("#shaixuan_num").hide();

    boolean_delete_sc_name = false;//记录是否删除过按名称筛选的结果
    poi_name_or_pub = null;//记录当前的筛选条件是按照属性来，还是按照名称来

    if (res.status == 0) {//返回状态正常
      $("#createModal").modal();

      if (res.data.query.length > 0) {//返回筛选条件
        query = jQuery.extend(true, {}, res.data.query);//保存一份返回的条件结果集
        var sc_conter = '';//页面的标签
        var name_length = 0;//记录标题的长度，用于设置每个条件的标题的宽度
        $.each(res.data.query, function (i, col) {//循环每个条件，第一次循环实现页面标签实体化

          var type = col.queryOptiontype.indexOf("复选") >= 0 ? "checkbox" : "radio";//根据类型区别复选或单选
          var queryShowquery = col.queryShowquery == null ? '' : 'style="display:none;"';//如果有出现条件，默认隐藏
          //获取单位之外的标题长度，根据最长的标题的字数决定宽度，决定标题与单位的分割是英文的括号
          var temp_name = col.queryName.indexOf("(") > 0 ? col.queryName.substring(0, col.queryName.indexOf("(")) : col.queryName;
          name_length = temp_name.length > name_length ? temp_name.length : name_length;//记录字数最多的标题

          sc_conter += '<div class="btn-group tiaojian" ' + queryShowquery + ' sc_su="' + col.queryShowqueryen + '" sc_su_val="' + col.queryValue + '">';
          sc_conter += '<span class="font-bold tiaojian-title" style="width: 90px;">' + col.queryName.replace("(", "<br>(") + '</span>';
          sc_conter += '<div class="btn-group btn-group-circle" data-toggle="buttons" id="' + col.queryEtitle + '" onclick="val_handle($(this),\'' + col.queryEtitle + '\',\'' + col.queryName + '\',\'' + type + '\',\'onclick\');">';
          if (col.queryOptiontype.indexOf("值域") >= 0) {//所有的值域需要自定义修改
            sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="' + type + '" class="toggle">不限</label>';

            for (var k = 1; k < 6; k++) {//循环五个指标项，
              if (col["queryOption" + k] == null) {//指标项为空
                if (k > 1) {//不是第一个指标项，增加最后一个第六项，大于最大值
                  sc_conter += '<label class="btn btn-d1" val_name="' + col["queryOption" + (k - 1)] + '~"><input type="' + type + '" class="toggle">≥' + col["queryOption" + (k - 1)] + '</label>';
                  break;
                }
              } else {
                if (k == 1) {//指标项有内容的情况下，第一个指标从零开始
                  sc_conter += '<label class="btn btn-d1" val_name="0~' + (col.queryOption1 - 1) + '"><input type="' + type + '" class="toggle">0-' + (col.queryOption1 - 1) + '</label>';
                } else {//其余的指标均是上一段到本段减一
                  sc_conter += '<label class="btn btn-d1" val_name="' + (col["queryOption" + (k - 1)] - 0) + '~' + (col["queryOption" + k] - 1) + '"><input type="' + type + '" class="toggle">' + (col["queryOption" + (k - 1)] - 0) + '-' + (col["queryOption" + k] - 1) + '</label>';
                }
                if (k == 5) {//到最后一个指标项的时候，增加第六项
                  sc_conter += '<label class="btn btn-d1" val_name="' + (col["queryOption" + k] - 0) + '~"><input type="' + type + '" class="toggle">≥' + (col["queryOption" + k] - 0) + '</label>';
                  break;
                }
              }
            }
            sc_conter += '</div>';
            sc_conter += '<form role="form" class="form-inline"><div class="form-group" style="padding-left:10px;">';
            sc_conter += '<input type="text" id="' + col.queryEtitle + '_1" onchange="val_handle($(this),\'' + col.queryEtitle + '\',\'' + col.queryName + '\',\'' + type + '\',\'onchange\');" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
            sc_conter += '</div>-<div class="form-group">';
            sc_conter += '<input type="text" id="' + col.queryEtitle + '_2" onchange="val_handle($(this),\'' + col.queryEtitle + '\',\'' + col.queryName + '\',\'' + type + '\',\'onchange\');" class="form-control" style="width:50px;height: 30px;padding: 6px 3px;">';
            sc_conter += '</div></form>';
          } else {//单选或者复选项直接判断
            sc_conter += '<label class="btn btn-d1 active" val_name="不限"><input type="' + type + '" class="toggle">不限</label>';
            sc_conter += col.queryOption1 == null ? '' : '<label class="btn btn-d1" val_name="' + col.queryOption1 + '"><input type="' + type + '" class="toggle">' + col.queryOption1 + '</label>';
            sc_conter += col.queryOption2 == null ? '' : '<label class="btn btn-d1" val_name="' + col.queryOption2 + '"><input type="' + type + '" class="toggle">' + col.queryOption2 + '</label>';
            sc_conter += col.queryOption3 == null ? '' : '<label class="btn btn-d1" val_name="' + col.queryOption3 + '"><input type="' + type + '" class="toggle">' + col.queryOption3 + '</label>';
            sc_conter += col.queryOption4 == null ? '' : '<label class="btn btn-d1" val_name="' + col.queryOption4 + '"><input type="' + type + '" class="toggle">' + col.queryOption4 + '</label>';
            sc_conter += col.queryOption5 == null ? '' : '<label class="btn btn-d1" val_name="' + col.queryOption5 + '"><input type="' + type + '" class="toggle">' + col.queryOption5 + '</label>';
            sc_conter += '</div>';
          }

          sc_conter += '</div>';
        });
        sc_conter += '<div id="se_bu" style="position: absolute;right: 20px; bottom: 20px;">';//padding-right: 20px;text-align: right;
        sc_conter += '<button class="btn btn-success" onclick="search_button();" type="button">';
        sc_conter += '<i class="fa fa-search"></i>&nbsp;&nbsp;<span class="bold">筛选</span>';
        sc_conter += '</button></div>';

        re = new RegExp("90px;", "g");
        sc_conter = sc_conter.replace(re, (name_length * 16) + "px;");
        $("#sc_conter").html(sc_conter);

        //初始化条件结构，将前置条件与后置条件分离，前置条件中的复选框做数组处理
        temp_val = {};
        $.each(res.data.query, function (i, col) {//组织条件的样例结构
          if (col.queryShowquery == null) {//是一级节点
            if (col.queryOptiontype.indexOf("复选") >= 0) {//是复选框
              temp_val[col.queryEtitle] = [];
              if (col.queryOption1 != null) {
                temp_val[col.queryEtitle].push(col.queryOption1);
              }
              if (col.queryOption2 != null) {
                temp_val[col.queryEtitle].push(col.queryOption2);
              }
              if (col.queryOption3 != null) {
                temp_val[col.queryEtitle].push(col.queryOption3);
              }
              if (col.queryOption4 != null) {
                temp_val[col.queryEtitle].push(col.queryOption4);
              }
              if (col.queryOption5 != null) {
                temp_val[col.queryEtitle].push(col.queryOption5);
              }
            } else {//是单选
              temp_val[col.queryEtitle] = "";
            }
          }
        });
        ty = "";
        $.each(res.data.query, function (i, col) {//组织条件的样例结构
          if (col.queryShowquery != null) {//是二级节点
            if (ty.indexOf(col.queryShowquery) == -1) {//记录前置条件的同时，判断每个前置条件只处理一次
              if (jQuery.isArray(temp_val[col.queryShowqueryen])) {//判断前置条件是否是数组，数组意味着复选框
                var temp = [];
                $.each(temp_val[col.queryShowqueryen], function (k, vol) {//循环这个数组，每个元素转换为对象
                  var tt = {};
                  tt[col.queryShowqueryen] = vol;
                  temp.push(tt);//将复选框条件的值增加到数据元素的对象中
                });
                temp_val[col.queryShowqueryen] = temp;
                ty += col.queryShowquery + ",";
              }
            }

          }
        });
        temp_val_v1 = jQuery.extend(true, {}, temp_val);//保留空白结构的备份，下一个条件还要继续使用
//				console.log(JSON.stringify(temp_val));
        //这是有属性条件的措施
      } else {
        //没有属性的措施，默认显示按名称查询，按属性查询关闭
        $("#sc_conter").hide();//属性查询
        $("#sc_conter_name").show();//条件查询
        $("#chaxuntype").children().each(function () {
          if ($(this).attr("val_name") == "shuxing") {//按属性查
            $(this).removeClass('active');
          } else if ($(this).attr("val_name") == "mingcheng") {//按名称查
            $(this).addClass('active');
          }
        });
      }

      //这里是名称条件的页面处理，默认显示按属性，这部分隐藏


      //要显示的列
      if (res.data.measureColumn.length > 0) {
        var sum = [];
        $.each(res.data.measureColumn, function (i, col) {
          sum.push(col.sectordocEtitle);//记录英文名称
        });
        sc_val.summary.sum = sum;//将英文名称添加到计算需求的模板中
      }

      xishu_temp = [];//需要填写的内容
      xishu_temp_ch = [];//需要填写的内容中文
      xishu_temp_v = [];

      var xishuMO = "";//修改的表单
      //要修改的列
      if (res.data.measureList.length > 0) {
        $.each(res.data.measureList, function (i, col) {

          xishu_temp.push(col.nameen);
          xishu_temp_ch.push(col.namech);
          xishu_temp_v.push(col.value);

          xishuMO += '<div class="col-md-6" style="margin-top: 10px;">';
          xishuMO += '<div class="input-group m-b" style="margin-bottom: 0px">';
          xishuMO += '<span class="input-group-addon" style="border: 1px solid #E5E6E7;">' + col.namech + '</span>';

          var step = "";
          if (col.value.toString().indexOf(".") >= 0) {
            var tthj = "";
            for (var i = 0; i < col.value.toString().split(".")[1].length - 1; i++) {
              tthj += "0";
            }
            step = 'step="0.' + tthj + '1"';
          }

          xishuMO += '<input type="number" class="form-control" min="' + col.minValue + '" ' + step + ' max="' + col.maxValue + '" id="' + col.nameen + '" name="' + col.nameen + '" value="' + col.value + '">';
          xishuMO += '</div></div>';
        });
        $("#xishuMO").html(xishuMO);
      }

      zongdianyaun = [];

      //if(res.data.query.length>0){//返回筛选条件，说明有点源可以筛选
      ajaxPost_w(jianpaiUrl + '/search/companyCount', {
        "bigIndex": qjMsg.esCouplingId, "smallIndex": sectorsName, "regionIds": Codes
      }).success(function (res) {
//					console.log(JSON.stringify(res));
        if (res.status == 'success') {
          $("#dianyaunzushu").html("点源总数：" + res.data.count);
          $("#xiangxizhibiao").html("点源排放占比:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SO<sub>2</sub>:" + res.data.rate.SO2 + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NO<sub>x</sub>:" + res.data.rate.NOx + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PM<sub>2.5</sub>:" + res.data.rate.PM25 + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;VOCs:" + res.data.rate.VOC + "%");
          add_point(res.data.company);

          point_z = res.data.company;//记录点源总数

          zongdianyaun = res.data.company;//复制总点源数据以后备用
        } else {
          swal('连接错误search/companyCount', '', 'error');
        }
      });
      //}

      //子措施列表的表头
      columns.push({
        field: 'state', title: '', align: 'center', checkbox: true, formatter: function (value, row, index) {
          if (index === 0) {
            return {
              disabled: true
            }
          }
          return value;
        }
      });
      columns.push({field: 'f1', title: '措施', align: 'center'});
      columns.push({field: 'f2', title: '点源实施范围', align: 'center'});
      $.each(res.data.measureColumn, function (i, vol) {
        columns.push({field: vol.sectordocEtitle, title: vol.sectordocCtitle, align: 'center'});
      });
      $.each(res.data.measureList, function (i, col) {
        columns.push({field: "psl_" + col.nameen, title: col.namech, align: 'center'});
      });


      var measureContent = {};
      if (typeof res.data.measureContent != "undefined") {
        measureContent = JSON.parse(res.data.measureContent);
        b_data.push(measureContent.table[0]);

        for (var pl = measureContent.table1.length - 1; pl >= 0; pl--) {
          delete measureContent.table1[pl].oopp;
          b_data.push(measureContent.table1[pl]);
        }

        delete measureContent.table[1].oopp;
        delete measureContent.table[2].oopp;
        b_data.push(measureContent.table[1]);
        b_data.push(measureContent.table[2]);
//				console.log(JSON.stringify(measureContent));

        delete measureContent.table;
        delete measureContent.table1;
        //初始化的时候，固定条件将来自于数据库返回值，复制一份保存使用
        sc_v1 = jQuery.extend(true, {}, measureContent);

        setTimeout(function () {
          show_zicuoshi_table(columns, b_data);
        }, 200);
      } else {
        //添加区域4的结果表格
//				console.log(JSON.stringify({"bigIndex":qjMsg.esCouplingId,"smallIndex":sectorsName,"summary":sc_val.summary,"regionIds":Codes}));
        ajaxPost_w(jianpaiUrl + '/search/summarySource', {
          "bigIndex": qjMsg.esCouplingId, "smallIndex": sectorsName, "summary": sc_val.summary, "regionIds": Codes
        }).success(function (da) {
//					console.log(JSON.stringify(da));
          if (da.status == 'success') {
            var zz = {"f1": "汇总", "f2": "0/" + da.data.P[0].count}, pp = {"f1": "剩余点源", "f2": da.data.P[0].count},
              ss = {"f1": "面源", "f2": "0"};
            $.each(res.data.measureColumn, function (i, vol) {
              zz[vol.sectordocEtitle] = "0/" + (Math.round(da.data.P[0][vol.sectordocEtitle]) + Math.round(da.data.S[0][vol.sectordocEtitle]));
              pp[vol.sectordocEtitle] = Math.round(da.data.P[0][vol.sectordocEtitle]);
              ss[vol.sectordocEtitle] = Math.round(da.data.S[0][vol.sectordocEtitle]);
            });
            $.each(res.data.measureList, function (i, col) {
              zz["psl_" + col.nameen] = "";
              pp["psl_" + col.nameen] = "";
              ss["psl_" + col.nameen] = "";
            });
            b_data.push(zz);
            b_data.push(pp);
            b_data.push(ss);
//						console.log(JSON.stringify(b_data));
            setTimeout(function () {
              show_zicuoshi_table(columns, b_data);
            }, 200);
          } else {
            swal('连接错误search/summarySource', '', 'error');
          }
        });

        //初始化的时候，将固定条件组织好，复制一份保存使用
        sc_v1 = jQuery.extend(true, {}, sc_val);
      }


    } else {
      swal('连接错误get_measureQuery', '', 'error');
    }
  }).error(function () {
    swal('校验失败', '', 'error')
  });
}

/**
 * 查询方式的改变，切换页面显示
 */
function chaxun_type_info() {
  setTimeout(function () {
    $("#chaxuntype").children().each(function () {
      if ($(this).is('.active')) {
        if ($(this).attr("val_name") == "shuxing") {//按属性查

          $("#sc_conter").show();//属性查询
          $("#sc_conter_name").hide();//条件查询
          $("#mic_name").hide();//按名称筛选结果div隐藏
          $("#mic_name").hide();//按名称筛选结果div
          $("#metTable_name_tools").hide();//保存子措施按钮

          if (poi_name_or_pub == "pub") {//名称查询
            var row = $('#metTable_point').bootstrapTable('getData');
            if (typeof row != "undefined") {
              if (row.length > 0) {
                $("#mic").show();//筛选结果div显示
                $("#metTable_tools").show();//保存子措施按钮
              } else {
                $("#mic").hide();//筛选结果div显示
                $("#metTable_tools").hide();//保存子措施按钮
              }
            }
          }

        } else if ($(this).attr("val_name") == "mingcheng") {//按名称查

          $("#sc_conter").hide();//属性查询
          $("#mic").hide();//筛选结果div显示
          $("#sc_conter_name").show();//条件查询
          $("#mic").hide();//筛选结果div显示
          $("#metTable_tools").hide();//保存子措施按钮

          if (poi_name_or_pub == "name") {//属性查询
            var row = $('#metTable_name_point').bootstrapTable('getData');
            if (typeof row != "undefined") {
              if (row.length > 0) {
                $("#mic_name").show();//按名称筛选结果div
                $("#metTable_name_tools").show();//保存子措施按钮
              } else {
                $("#mic_name").hide();//按名称筛选结果div
                $("#metTable_name_tools").hide();//保存子措施按钮
              }
            }
          }

        }
      }
    });
  }, 100);
}

/**
 * 显示子措施表格的列表
 */
function show_zicuoshi_table(columns, b_data) {
  $('#show_zicuoshi_table').bootstrapTable('destroy');
  $('#show_zicuoshi_table').bootstrapTable({
    columns: columns,
    data: b_data,
    height: $("#show_zicuoshi_table_div").height(),
    clickToSelect: false,// 点击选中行
    detailView: true,//显示详细页面模式
    pagination: false, // 在表格底部显示分页工具栏
    clickToSelect: true,//设置true 将在点击行时，自动选择rediobox 和 checkbox
    singleSelect: true,//设置True 将禁止多选
    striped: false, // 使表格带有条纹
    silent: true, // 刷新事件必须设置
    detailFormatter: function (index, row) {
      if (row.f1 == "汇总" || row.f1 == "剩余点源" || row.f1 == "面源") {

      } else {
        return row.tiaojian;
      }
    },
    onClickRow: function (row, $element) {
//      $('.success').removeClass('success');
//      $($element).addClass('success');
    	if (row.state == true) {//如果被选中
    		if (row.f1 != "剩余点源" && row.f1 != "面源") {
    			$("#zicuoshi_tools_de").show();
    			$("#zicuoshi_tools_up").show();
    		} else {
    			$("#zicuoshi_tools_de").hide();
    			$("#zicuoshi_tools_up").show();
    		}
    	} else {
    		$("#zicuoshi_tools_de").hide();
    		$("#zicuoshi_tools_up").hide();
    	}
    },
    onCheck: function (row){
    	if (row.f1 != "剩余点源" && row.f1 != "面源") {
			$("#zicuoshi_tools_de").show();
			$("#zicuoshi_tools_up").show();
		} else {
			$("#zicuoshi_tools_de").hide();
			$("#zicuoshi_tools_up").show();
		}
    },
    onUncheck: function (row){
    	$("#zicuoshi_tools_de").hide();
		$("#zicuoshi_tools_up").hide();
    },
    onLoadSuccess: function (data) {
//			alert('b');
    },
    onLoadError: function () {
      swal('连接错误', '', 'error');
    }
  });
  $.each(b_data, function (i, vol) {
    $('#show_zicuoshi_table').bootstrapTable('expandRow', i);
  });
}


/**
 * 筛选条件的值处理
 * e: 当前操作的条件的外层DIV对象
 * queryEtitle: 点击的条件的en名称
 * queryName: 点击的条件的cn名称
 * type: 点击的条件的类型，用于区分复选单选
 */
function val_handle(e, queryEtitle, queryName, type, sourceType) {

  var xuanze_val = "";//
  setTimeout(function () {//等待1/10秒，待css改变后再获取选择的值
    var queryShowqueryen = e.parent().attr("sc_su");//前置条件的名称
    var queryValue = e.parent().attr("sc_su_val");//前置条件的值

    var kk = [];//复选框的值记录
    var pp = "";//单选框记录的值

    if (sourceType == "onchange") {//操作来源是文本框的值改变
      pp = $("#" + queryEtitle + "_1").val() + "~" + $("#" + queryEtitle + "_2").val();
    } else {
      e.children().each(function () {//循环当前条件下所有的可选项
        var val_name = $(this).attr("val_name");
        if (val_name != "不限") {
          if (type == "radio") {
            if ($(this).is('.active')) {
              pp = val_name;
              xuanze_val += val_name + ",";
              if (val_name.indexOf("~") >= 0) {//如果值得中间有波折号，说明是值域，为文本框赋值
                var s = val_name.split("~");
                $("#" + queryEtitle + "_1").val(s[0]);
                $("#" + queryEtitle + "_2").val(s[1] == "" ? "" : s[1]);
              }
            } else {
              $("#sc_conter").children().each(function () {//为单选的前置条件设置，清空后置条件的显隐设置
                if ($(this).attr("sc_su") != "null") {
                  if ($(this).attr("sc_su") == queryEtitle) {//条件的名字与其他条件的前置条件名相同
                    if ($(this).attr("sc_su_val") == val_name) {//值包含
                      $(this).children("div").each(function () {
                        $(this).children().each(function () {
                          $(this).removeClass("active");
                          if ($(this).attr("val_name") == "不限") {
                            $(this).addClass("active");
                          }
                        });
                      });
                    }
                  }
                }
              });
            }
          } else {//复选框
            if (ty.indexOf(queryName) >= 0) {//当前的条件属于前置条件
              if ($(this).is('.active')) {
                xuanze_val += val_name + ",";
              } else {
                $.each(temp_val_v1[queryEtitle], function (i, col) {
                  if (col[queryEtitle] == val_name) {
                    $.each(col, function (k, vol) {
                      if (k != queryEtitle) {
                        delete col[k];
                      }
                    });
                  }
                });

                $("#sc_conter").children().each(function () {
                  if ($(this).attr("sc_su") != "null") {
                    if ($(this).attr("sc_su") == queryEtitle) {//条件的名字与其他条件的前置条件名相同
                      if ($(this).attr("sc_su_val") == val_name) {//值包含
                        $(this).children("div").each(function () {
                          $(this).children().each(function () {
                            $(this).removeClass("active");
                            if ($(this).attr("val_name") == "不限") {
                              $(this).addClass("active");
                            }
                          });
                        });
                      }
                    }
                  }
                });
              }
            } else {
              if ($(this).is('.active')) {
                kk.push(val_name);
                xuanze_val += val_name + ",";
              }
            }
          }
        } else {
          if ($(this).is('.active')) {
            if (type == "radio") {
              $("#" + queryEtitle + "_1").val("");
              $("#" + queryEtitle + "_2").val("");
            }
          }
        }
      });
    }

    if (queryShowqueryen == "null") {//没有前置条件
      if (type == "checkbox") {
        if (ty.indexOf(queryName) >= 0) {

        } else {
          temp_val_v1[queryEtitle] = kk;
        }
      } else {
        temp_val_v1[queryEtitle] = pp;
      }
    } else {//有前置条件，需要进一步增加层级
      if (jQuery.isArray(temp_val_v1[queryShowqueryen])) {
        $.each(temp_val_v1[queryShowqueryen], function (i, col) {
          if (col[queryShowqueryen] == queryValue) {
            if (type == "checkbox") {
              temp_val_v1[queryShowqueryen][i][queryEtitle] = kk;
            } else {
              temp_val_v1[queryShowqueryen][i][queryEtitle] = pp;
            }
          }
        });
      } else {
        temp_val_v1[queryEtitle] = pp;
      }
    }
    $("#sc_conter").children().each(function () {//循环所有的条件
      if ($(this).attr("sc_su") != "null") {
        if ($(this).attr("sc_su") == queryEtitle) {//条件的名字与其他条件的前置条件名相同
          if (xuanze_val.indexOf($(this).attr("sc_su_val")) >= 0) {//值包含
            $(this).show();
          } else {
            $(this).hide();
          }
        }
      }
    });
  }, 100);
}


/**
 * 筛选按钮
 */
function search_button() {

  var ttlk = true;//点源的编辑状态
  var row = $('#show_zicuoshi_table').bootstrapTable('getData');//已有子措施的所有的记录
  $.each(row, function (i, col) {
    if (col.f1 == '剩余点源') {
      $.each(col, function (k, vol) {//循环剩余点源的所有列
        if (k.indexOf("psl_") == 0) {
          if (typeof vol != "undefined" && vol != "") {
            if (parseFloat(vol) > 0) {
              ttlk = false;
            }
          }
        }
      });
    }
  });


  setTimeout(function () {
    //条件缓存中的空值删除
    $.each(temp_val_v1, function (key, col) {
      if (jQuery.isArray(col)) {//判断值是否数组

        var ttp = [];//有效数组
        var bool = false;//记录是否进行了处理
        $.each(col, function (i, vol) {//循环数组
//					if((typeof vol=='string') && vol.constructor==String){//字符串不需处理
//
//					}else{
//						bool = true;
//						$("#"+key).children().each(function(){//循环数组的页面，查看现有的元素是否选中
//							if($(this).attr("val_name") == vol[key]){//如果当前的标签的值与数组中唯一元素的值相同，同时数组中唯一元素的key与顶层key一致，说明找到标签
//								if($(this).is(".active")){//判断这个标签是否被选中，如果选中说明正常，未选中需要再数组中删除这个元素
//									ttp.push(vol);//删除操作就是将有效数组放入到新数组中，循环结束一次性覆盖
//								}
//							}
//						});
//					}
          bool = true;
          $("#" + key).children().each(function () {//循环数组的页面，查看现有的元素是否选中
            if ($(this).attr("val_name") == vol) {//如果当前的标签的值与数组中唯一元素的值相同，同时数组中唯一元素的key与顶层key一致，说明找到标签
              if ($(this).is(".active")) {//判断这个标签是否被选中，如果选中说明正常，未选中需要再数组中删除这个元素
                ttp.push(vol);//删除操作就是将有效数组放入到新数组中，循环结束一次性覆盖
              }
            }
          });
        });

        if (bool) {
          temp_val_v1[key] = ttp;
        }

      } else {
        if (col == "") {//不是数组，同时为空值，删除
          delete temp_val_v1[key];
        }
      }
    });

    //删除空数组
    $.each(temp_val_v1, function (key, col) {
      if (!col.length > 0) {
        delete temp_val_v1[key];
      }
    });


    //先将备份的内容复制到查询条件中，只有保存子措施后，备份内容才添加
    sc_val = jQuery.extend(true, {}, sc_v1);

    //将本次查询的缓存加入到总条件中
//		console.log(JSON.stringify(temp_val_v1));
    if (JSON.stringify(temp_val_v1) == "{}") {
      //没有条件，就不加了
      swal({
        title: "请选择筛选条件",
        text: "筛选条件至少选择一项，全部点源请对剩余点源进行操作.",
        timer: 2000,
        showConfirmButton: false
      });
    } else {
      if (ttlk) {
        sc_val.filters.push(temp_val_v1);
        console.log(JSON.stringify(sc_val));
        temp_val_v1 = jQuery.extend(true, {}, temp_val);//赋值模板到操作缓存
        //根据筛选条件获取点源，准备填写空值系数
        point_table();
      } else {
        swal({
          title: "剩余点源已设置，无法进行筛选",
          text: "筛选条件至少选择一项，全部点源请对剩余点源进行操作.",
          timer: 2000,
          showConfirmButton: false
        });
      }
    }


    //所有查询条件初始化
    $("#sc_conter").children().each(function () {//循环一级标签
      if ($(this).attr("sc_su") != "null") {//没有前置条件显示，有前置条件隐藏
        $(this).hide();
      }
      $("#se_bu").show();//筛选按钮打开

      //循环下级的div标签，div内的子项为单选和复选，全部初始化为不限
      $(this).children("div").each(function () {
        $(this).children().each(function () {
          $(this).removeClass("active");
          if ($(this).attr("val_name") == "不限") {
            $(this).addClass("active");
          }
        });
      });

      //循环下级的文本框，文本框清空
      $(this).find("input").each(function () {
        $(this).val("");
      });
    });
    //获取点源相应的减排占比等内容，数据加入到表格显示，刷新显示区域
  }, 200);
}

/**
 * 按企业名称的筛选
 */
function search_name_button() {
  //先将备份的内容复制到查询条件中，只有保存子措施后，备份内容才添加
  if ($("#qiye_name").val() != "") {
    sc_val = jQuery.extend(true, {}, sc_v1);
    var ttqr = {};
    ttqr.companyname = "%" + $("#qiye_name").val() + "%";
    sc_val.filters.push(ttqr);
    point_name_info = [];
    //根据筛选条件获取点源，准备填写空值系数
    point_name_table();

    $("#qiye_name").removeClass("erroe_input");//删除红色边框
  } else {
    $("#qiye_name").addClass("erroe_input");//加红色边框
  }
}

var poi_name_or_pub;//记录当前的筛选条件是按照属性来，还是按照名称来

//筛选点源列表---按属性查询
function point_table() {
  poi_name_or_pub = "pub";//记录当前的筛选条件是按照属性来，还是按照名称来
  $('#metTable_point').bootstrapTable('destroy');//销毁现有表格数据
  $("#shaixuan_num").html("");//筛选点源
  $("#shaixuan_num").hide();
  $("#mic").show();//筛选结果div显示
  $("#mic_name").hide();//按名称筛选结果div隐藏
  $("#xishuMO").hide();//控制系数隐藏
  $("#xishuMOB").hide();//控制系数按钮隐藏
  $('#metTable_point').bootstrapTable({
    method: 'POST',
    url: jianpaiUrl + '/search/sourceList',
    dataType: "json",
    iconSize: "outline",
    clickToSelect: true,// 点击选中行
    pagination: true, // 在表格底部显示分页工具栏
    pageSize: 5, // 页面大小
    pageNumber: 1, // 页数
    pageList: [5, 10, 20],
    striped: true, // 使表格带有条纹
    sidePagination: "server",// 表格分页的位置 client||server
    queryParams: function (params) {
      var temp_sc_val = jQuery.extend(true, {}, sc_val);
      delete temp_sc_val.summary;
      temp_sc_val.regionIds = Codes;
      temp_sc_val.pageHelper = {};
      temp_sc_val.pageHelper.pageSize = params.limit;
      temp_sc_val.pageHelper.pageNumber = params.offset;
      return JSON.stringify(temp_sc_val);
    },
    responseHandler: function (res) {

      if (res.status == 'success') {
        if (res.data.rows.length > 0) {
          $("#shaixuan_num").html("筛选点源：" + res.data.append.sourceTotalCount);
          if (typeof res.data.append.total != "undefined") {
            if (res.data.append.total.length > 0) {//加地图坐标
              add_point(res.data.append.total);
            }
          }
          $("#shaixuan_num").show();
          var tablejieguo = "";
          if (sc_val.filters.length == 0) {
            //没有条件，直接筛选
            tablejieguo = "全部,";
          } else {
            $.each(sc_val.filters[sc_val.filters.length - 1], function (k, vol) {//循环最后一个条件，这个条件是要添加显示的条件
              $.each(query, function (i, col) {//循环提前记录的条件结果集，将英文名称换为中文名称
                if (col.queryEtitle == k) {
                  tablejieguo += col.queryName + "：" + vol + ",";
                }
              });
            });
          }

          $("#shaixuan_num").attr("title", tablejieguo.substring(0, tablejieguo.length - 1));
        }

        return res.data;
      } else if (res.status == '') {

        return "";
      }
    },
    queryParamsType: "limit", // 参数格式,发送标准的RESTFul类型的参数请求
    silent: true, // 刷新事件必须设置
    contentType: "application/json", // 请求远程数据的内容类型。
    onClickRow: function (row, $element) {
      $('.success').removeClass('success');
      $($element).addClass('success');
    },
    icons: {
      refresh: "glyphicon-repeat",
      toggle: "glyphicon-list-alt",
      columns: "glyphicon-list"
    },
    onLoadSuccess: function (data) {
//			console.log(data);
      data.total > 0 ? $("#metTable_tools").show() : $("#metTable_tools").hide();//保存子措施按钮
    },
    onLoadError: function () {
      swal('连接错误', '', 'error');
    }
  });
}

var point_name_info = [];//按名称查询时候的坐标

//筛选点源列表---按名称查询
function point_name_table() {
  poi_name_or_pub = "name";//记录当前的筛选条件是按照属性来，还是按照名称来
  $('#metTable_name_point').bootstrapTable('destroy');//销毁现有表格数据
  $("#shaixuan_num").html("");//筛选点源
  $("#shaixuan_num").hide();
  $("#mic").hide();//筛选结果div隐藏
  $("#mic_name").show();//按名称筛选结果div显示
  $("#xishuMO").hide();//控制系数隐藏
  $("#xishuMOB").hide();//控制系数按钮隐藏
  $('#metTable_name_point').bootstrapTable({
    method: 'POST',
    url: jianpaiUrl + '/search/sourceList',
    dataType: "json",
    iconSize: "outline",
    clickToSelect: true,// 点击选中行
    pagination: false, // 在表格底部显示分页工具栏
    striped: true, // 使表格带有条纹
    queryParams: function (params) {
      var temp_sc_val = jQuery.extend(true, {}, sc_val);
      delete temp_sc_val.summary;
      temp_sc_val.regionIds = Codes;
//			console.log(JSON.stringify(temp_sc_val));
      return JSON.stringify(temp_sc_val);
    },
    queryParamsType: "limit", // 参数格式,发送标准的RESTFul类型的参数请求
    silent: true, // 刷新事件必须设置
    contentType: "application/json", // 请求远程数据的内容类型。
    responseHandler: function (res) {
      if (res.status == 'success') {
//				console.log(JSON.stringify(res));
        if (typeof res.data.append.total != "undefined") {
          if (res.data.append.total.length > 0) {//加地图坐标
            add_point(res.data.append.total);
            point_name_info = res.data.append.total;
          }
        }
        $.each(res.data.rows, function (k, vol) {
          vol.caozuo = '<a onClick="delete_sc_name(\'' + vol.id + '\',\'' + vol.companyId + '\');">删除</a>';
        });
        return res.data.rows;
      } else if (res.status == 'fail' && res.error == '查询数据超过50条') {//筛选结果大于50条
        swal('查询结果超过50个设备，请精确输入企业名称', '', 'error');
        return "";
      }
    },
    onClickRow: function (row, $element) {
      $('.success').removeClass('success');
      $($element).addClass('success');
    },
    icons: {
      refresh: "glyphicon-repeat",
      toggle: "glyphicon-list-alt",
      columns: "glyphicon-list"
    },
    onLoadSuccess: function (data) {
//			console.log(data);
      data.length > 0 ? $("#metTable_name_tools").show() : $("#metTable_name_tools").hide();//保存子措施按钮
    },
    onLoadError: function () {
      swal('连接错误', '', 'error');
    }
  });
}


var boolean_delete_sc_name = false;//记录是否删除过按名称筛选的结果
/**
 * 删除按名称筛选的结果
 * vval:id的值
 */
function delete_sc_name(vval, cid) {
  $('#metTable_name_point').bootstrapTable('remove', {field: 'id', values: [vval]});

  var row = $('#metTable_name_point').bootstrapTable('getData')
  var ttgu = true;
  $.each(row, function (i, col) {//循环表格现有的数据，判断删除的企业是否还在表格内存在，多个设备的问题
    if (col.companyId == cid) {
      ttgu = false;//同一个企业还要没删除干净的
    }
  });

  if (ttgu) {//同一个企业全部都删除完了
    var xin = [];
    $.each(point_name_info, function (i, col) {//在保存的店坐标中，将没有出现的企业保存为新的记录
      if (col.companyId != cid) {
        xin.push(col);
      }
    });
    add_point(xin);
    point_name_info = xin;
  }

  boolean_delete_sc_name = true;
}


/**
 * 保存子措施，打开控制系数页面
 */
function xishuMo() {
  $("#mic").hide();//筛选结果div隐藏
  $("#mic_name").hide();//按名称筛选结果div隐藏
  $("#metTable_tools").hide();//保存子措施按钮
  $("#metTable_name_tools").hide();//保存子措施按钮
  $("#xishuMO").show();//控制系数div显示
  $("#xishuMOB").show();//控制系数按钮显示
  $("#shaixuan_num").hide();
}

/**
 * 保存子措施，打开控制系数页面，记录id号，按企业名称查询专用
 */
function xishuMo_name() {
  if (poi_name_or_pub == "name") {//记录当前的筛选条件是按照属性来，还是按照名称来
    var row = $('#metTable_name_point').bootstrapTable("getData");
    if (row.length > 0) {
      var ttdf = [];
      var ttat = [];
      var ttvt = "";
      $.each(row, function (k, vol) {
        //先判断是否删除过筛选结果
        if (boolean_delete_sc_name) {
          ttdf.push(vol.id);
        }
        if (ttvt != vol.companyname) {//重复的企业名称排除
          ttat.push(vol.companyname);//记录所有的企业名称
          ttvt = vol.companyname;
        }
      });
      if (ttat.length == 1) {//只有一个企业的时候，才可以继续
        if (boolean_delete_sc_name) {//先判断是否删除过筛选结果
          //替换缓存中的filters中的companyname
          delete sc_val.filters[sc_val.filters.length - 1].companyname;
          sc_val.filters[sc_val.filters.length - 1].id = ttdf;
        }
        xishuMo();
      } else {
        swal('只能对单个企业设置控制措施', '', 'error');
      }
    }
  }
}

var zicuoshi_up_index;//修改或者删除的时候记录的子措施的行号

/**
 * 系数表单保存
 */
function xishu_save() {

  var ttwr = true;
  //值域校验
  $.each(xishu_temp, function (i, vol) {
    var min = parseFloat($("#" + vol).attr("min"));
    var max = parseFloat($("#" + vol).attr("max"));
    var vv = parseFloat($("#" + vol).val());
    if (vv < min || vv > max) {
      ttwr = false;
      $("#" + vol).addClass("erroe_input");//加红色边框
    }
  });

  if (ttwr) {

    if (zicuoshi_up_index == null) {//添加新的子措施
//			console.log(JSON.stringify(sc_val));
      ajaxPost_w(jianpaiUrl + '/search/summary', sc_val).success(function (res) {
//				console.log(JSON.stringify(res));
        if (res.status == 'success') {

          var ttr = {};

//					var re1 = new RegExp("{","g");
//					var re2 = new RegExp("}","g");
//					var re3 = new RegExp("\"","g");

          var tablejieguo = "";
          var row = $('#metTable_name_point').bootstrapTable("getData");

          if (sc_val.filters.length >= 1) {//大于1，说明有条件
            $.each(sc_val.filters[sc_val.filters.length - 1], function (k, vol) {//循环最后一个条件，这个条件是要添加显示的条件
              if (k == "companyname") {
//								var re4 = new RegExp("%","g");
                tablejieguo += "企业名称：" + row[0].companyname + ",";
              } else if (k == "id") {
                tablejieguo += "企业名称：" + row[0].companyname + ",";
              } else {
                $.each(query, function (i, col) {//循环提前记录的条件结果集，将英文名称换为中文名称
                  if (col.queryEtitle == k) {
                    tablejieguo += col.queryName + "：" + vol + ",";
                  }
                });
              }
            });
          } else {//等于0，说明条件是全部
            tablejieguo = "全部点源,";
          }

          ttr.tiaojian = tablejieguo.substring(0, tablejieguo.length - 1);
          ttr.f1 = measureame_temp;
          ttr.f2 = res.data.count;

          $.each(sc_val.summary.sum, function (i, vol) {
            ttr[vol] = Math.round(res.data[vol]);
          });
          $.each(xishu_temp, function (i, vol) {
            ttr["psl_" + vol] = $("#" + vol).val();
          });

          $('#show_zicuoshi_table').bootstrapTable('insertRow', {index: 1, row: ttr});
          restion_table();

          //计算完成，将结果保存为上一级的缓存
          sc_v1 = jQuery.extend(true, {}, sc_val);
//					console.log(JSON.stringify(sc_v1));

          //输入框初始化
          $.each(xishu_temp, function (i, vol) {
            $("#" + vol).removeClass("erroe_input");//删除红色边框
            $("#" + vol).val(xishu_temp_v[i]);
          });
          $("#qiye_name").val("");//按名称筛选条件
          $('#metTable_point').bootstrapTable('destroy');//销毁现有表格数据
          $('#metTable_name_point').bootstrapTable('destroy');//销毁现有表格数据

          if (point_z.length > 0) {
            add_point(point_z);//地图初始化
          }
        }
      });
    } else {//修改已有子措施

      var up_row = {};
      $.each(xishu_temp, function (i, vol) {
        up_row["psl_" + vol] = $("#" + vol).val();
      });
      $('#show_zicuoshi_table').bootstrapTable('updateRow', {index: zicuoshi_up_index, row: up_row});
      restion_table();
      zicuoshi_up_index = null;
      //输入框初始化
      $.each(xishu_temp, function (i, vol) {
        $("#" + vol).removeClass("erroe_input");//删除红色边框
        $("#" + vol).val(xishu_temp_v[i]);
      });

    }

    boolean_delete_sc_name = false;//记录是否删除过按名称筛选的结果
    poi_name_or_pub = null;//记录当前的筛选条件是按照属性来，还是按照名称来

    $("#mic").hide();//筛选结果div
    $("#mic_name").hide();//按名称筛选结果div隐藏
    $("#metTable_tools").hide();//保存子措施按钮
    $("#metTable_name_tools").hide();//保存子措施按钮
    $("#xishuMO").hide();//控制系数div
    $("#xishuMOB").hide();//控制系数按钮
    $("#shaixuan_num").html("");//筛选点源
    $("#shaixuan_num").hide();
  }
}


/**
 * 子措施的修改
 */
function zicuoshi_up() {
  var row = $('#show_zicuoshi_table').bootstrapTable('getData');//所有的记录
  zicuoshi_up_index = null;

  $.each(row, function (i, col) {
    if (col.state == true) {//如果被选中

      $.each(col, function (k, vol) {
        if (k.indexOf("psl_") == 0) {
          if (vol == "") {
            $.each(xishu_temp, function (w, txt) {
              if (txt == k.substring(4, k.length)) {//找到要填写的内容的名称
                $("#" + k.substring(4, k.length)).val(xishu_temp_v[w]);
              }
            });
          } else {
            $("#" + k.substring(4, k.length)).val(vol);
          }
        }
      });
      //打开控制系数填写页面
      $("#mic").hide();//筛选结果div
      $("#mic_name").hide();//按名称筛选结果div隐藏
      $("#metTable_tools").hide();//保存子措施按钮
      $("#metTable_name_tools").hide();//保存子措施按钮
      $("#shaixuan_num").html("");//筛选点源
      $("#shaixuan_num").hide();
      $("#xishuMO").show();//控制系数div
      $("#xishuMOB").show();//控制系数按钮
      zicuoshi_up_index = i;//当前选中的行是要修改的子措施
    }
  });
}

/**
 * 子措施的删除
 */
function zicuoshi_de() {

  var row = $('#show_zicuoshi_table').bootstrapTable('getData');//所有的记录
  zicuoshi_up_index = null;

  var kk = -1;
  $.each(row, function (i, col) {
    if (col.state) {//如果被选中
      kk = i;
    }
  });

  if (kk > 0) {
    var rowq = $('#show_zicuoshi_table').bootstrapTable('getSelections');
    SKUNo = $.map(rowq, function (rowww) {
      return rowww.state;
    });
    $('#show_zicuoshi_table').bootstrapTable('remove', {
      field: 'state',
      values: SKUNo
    });

    if (kk > 1) {

//			console.log(JSON.stringify(sc_v1));
      sc_v1.filters.splice(sc_v1.filters.length - kk, 1);
//			console.log(JSON.stringify(sc_v1));

      ajaxPost_w(jianpaiUrl + '/search/summaryLevel', sc_v1).success(function (res) {
//				console.log(JSON.stringify(res));
        if (res.status == 'success') {

          $.each(res.data, function (k, col) {//循环返回的数据
            var ttr = {};
            $.each(sc_val.summary.sum, function (i, vol) {
              ttr[vol] = Math.round(col[vol]);
            });
            ttr.f2 = col["count"];
            $('#show_zicuoshi_table').bootstrapTable('updateRow', {index: (res.data.length - k), row: ttr});
            restion_table();
          });
        }
      });

    } else {//删除的是第一条，不用重新计算，只要把条件的最后一条删除即可
      sc_v1.filters.pop();
      restion_table();
    }
    $("#zicuoshi_tools_de").hide();
    $("#zicuoshi_tools_up").hide();
  }
}

/**
 * 重新计算并打开表格
 */
function restion_table() {
  var row = $('#show_zicuoshi_table').bootstrapTable('getData');//添加后所有的数据

  var row_0_temp = jQuery.extend(true, {}, row[0]);
  var row_1_temp = jQuery.extend(true, {}, row[row.length - 1]);//面源
  var row_2_temp = jQuery.extend(true, {}, row[row.length - 2]);//剩余点源

  if (row.length > 3) {
    $.each(row, function (i, col) {//循环所有记录

      var ttwre = false;//是否计算，用于区分是否是汇总行、还是计算行
      var ttfg = true;//剩余点源和面源不需要做减法改变行内的值

      if (col.f1 == "汇总") {
        //汇总行不需要计算
        ttwre = false;
      } else {
        if (col.f1 == "剩余点源" || col.f1 == "面源") {
          //如果是剩余点源和面源，需要看是否填写了控制措施，填写控制措施的进入计算，否则不计算
          $.each(row_0_temp, function (k, vol) {//循环所有的列
            if (k.indexOf("psl_") == 0) {
              if (col[k] != "") {//只要有一个控制措施有值，就进入计算
                ttwre = true;
                ttfg = false;
              }
            }
          });
        } else {
          //直接计算
          ttwre = true;
        }
      }
      //开始计算
      if (ttwre) {
        if (i == 1) {//一个表格只要有一次初始化即可
          //汇总行和剩余点源行初始化
          $.each(row_0_temp, function (k, vol) {//循环汇总行
            if (k.indexOf("psl_") == 0 && k == "tiaojian" && k == "f1" && k == "state") {//第一个字母是下划线开头,中文条件字段，措施字段均不需要计算
              //不需要计算
            } else {
              if (vol.toString().indexOf("/") >= 0) {
                var yuyu = vol.split("/");//汇总行的数据
                row_0_temp[k] = "0/" + yuyu[1];
                row_2_temp[k] = parseInt(yuyu[1]) - parseInt(row_1_temp[k]);//剩余点源等于汇总减去面源（面源是不操作的，所以用面源做计算）
              }
            }
          });
        }

        $.each(row_0_temp, function (k, vol) {//循环汇总行
          if (k.indexOf("psl_") == 0 && k == "tiaojian" && k == "f1" && k == "state") {//第一个字母是下划线开头,中文条件字段，措施字段均不需要计算
            //不需要计算
          } else {
            if (vol.toString().indexOf("/") >= 0) {
              var yuyu = vol.split("/");//汇总行的数据
              var ttemp = parseInt(yuyu[0]);//汇总行的当前值

              if (col.f1 == "剩余点源") {
                row_0_temp[k] = (parseInt(yuyu[0]) + parseInt(row_2_temp[k])) + "/" + yuyu[1];
              } else {
                row_0_temp[k] = (parseInt(yuyu[0]) + parseInt(col[k])) + "/" + yuyu[1];
              }

              if (ttfg) {//只有普通的子措施时剩余点源才参与计算，剩余点源自己和面源参加到子措施时不能减少剩余点源的值
                var ttui = row_2_temp[k];//剩余点源的数据
                row_2_temp[k] = parseInt(ttui) - parseInt(col[k]);
              }


            }
          }
        });
      }

    });
  } else {//就剩三行了，回复初始化

    var ttshengyud = false;//剩余点源是否计算
    var ttmiany = false;//面源是否计算

    $.each(row_0_temp, function (k, vol) {//循环汇总行
      if (k.indexOf("psl_") == 0 && k == "tiaojian" && k == "f1" && k == "state") {//第一个字母是下划线开头,中文条件字段，措施字段均不需要计算
        //不需要计算
      } else {
        if (vol.toString().indexOf("/") >= 0) {
          var yuyu = vol.split("/");//汇总行的数据

          row_0_temp[k] = "0/" + yuyu[1];
          row_2_temp[k] = parseInt(yuyu[1]) - parseInt(row_1_temp[k]);//剩余点源等于汇总减去面源（面源是不操作的，所以用面源做计算）
        }
      }

      if (k.indexOf("psl_") == 0) {
        if (row_2_temp[k] != "") {//剩余点源只要有一个控制措施有值，就进入计算
          ttshengyud = true;
        }
        if (row_1_temp[k] != "") {//面源只要有一个控制措施有值，就进入计算
          ttmiany = true;
        }
      }

    });

    $.each(row_0_temp, function (k, vol) {
      if (k.indexOf("psl_") == 0 && k == "tiaojian" && k == "f1" && k == "state") {//第一个字母是下划线开头,中文条件字段，措施字段均不需要计算
        //不需要计算
      } else {
        if (vol.toString().indexOf("/") >= 0) {
          var yuyu = vol.split("/");//汇总行的数据
          var ttemp = parseInt(yuyu[0]);//汇总行的当前值
          if (ttshengyud) {
            //如果剩余点源有计算，将剩余点源的数值加到汇总行
            ttemp += parseInt(row_2_temp[k]);
          }
          if (ttmiany) {
            //如果面源有计算，将面源数值加到会总行
            ttemp += parseInt(row_1_temp[k]);
          }
          row_0_temp[k] = ttemp + "/" + yuyu[1];
        }
      }
    });

  }

  $('#show_zicuoshi_table').bootstrapTable('updateRow', {index: 0, row: row_0_temp});
  $('#show_zicuoshi_table').bootstrapTable('updateRow', {index: row.length - 2, row: row_2_temp});


  $.each(row, function (i, vol) {
    $('#show_zicuoshi_table').bootstrapTable('expandRow', i);
  });
}


/**
 * 系数表单不保存关闭
 */
function xishu_close() {
  if (zicuoshi_up_index == null) {
    if (poi_name_or_pub == "pub") {//记录当前的筛选条件是按照属性来，还是按照名称来
      $("#mic").show();//筛选结果div
      $("#shaixuan_num").show();
      $("#metTable_tools").show();//保存子措施按钮
      $("#mic_name").hide();//按名称筛选结果div隐藏
    } else if (poi_name_or_pub == "name") {
      $("#mic").hide();//筛选结果div
      $("#metTable_name_tools").show();//保存子措施按钮
      $("#mic_name").show();//按名称筛选结果div隐藏
    }
    $("#xishuMO").hide();//控制系数div
    $("#xishuMOB").hide();//控制系数按钮

//		if(point_z.length > 0){
//			add_point(point_z);//地图初始化
//		}
  } else {
    if (poi_name_or_pub == "pub") {//记录当前的筛选条件是按照属性来，还是按照名称来
      $("#mic").hide();//筛选结果div
      $("#shaixuan_num").hide();
      $("#metTable_tools").hide();//保存子措施按钮
      $("#mic_name").hide();//按名称筛选结果div隐藏
    } else if (poi_name_or_pub == "name") {
      $("#mic").hide();//筛选结果div
      $("#metTable_name_tools").hide();//保存子措施按钮
      $("#mic_name").hide();//按名称筛选结果div隐藏
    }
    $("#xishuMO").hide();//控制系数div
    $("#xishuMOB").hide();//控制系数按钮
    zicuoshi_up_index = null;
  }
}

/**
 * 子措施保存
 */
var zicuoshi_bool = true;
function create() {
//	delete sc_v1.summary;

  if (zicuoshi_bool) {
    zicuoshi_bool = false;

    var row = $('#show_zicuoshi_table').bootstrapTable('getData');

    //删除空数组
//		$.each(sc_v1.filters, function(k, vol) {
//			if(!vol.fuelType.length>0){
//				delete sc_v1.filters[k].fuelType;
//			}
//		});

    sc_v1.table = [];
    sc_v1.table.push(row[0]);

    //判断剩余点源与面源是否需要向后传送
//		var tt1 = false;//剩余点源是否填写
//		var tt2 = false;//面源是否填写
//		$.each(row[row.length-2], function(k, vol) {//循环所有的列
//			if(k.indexOf("psl_")==0){
//				if(vol != ""){//只要有一个控制措施有值，就进入计算
//					tt1 = true;
//				}
//			}
//		});
//		if(tt1){
    var temm = {};
    $.each(row[row.length - 2], function (k, vol) {
      if (k.indexOf("psl_") == 0) {//第一个字母是下划线开头
        temm[k.substring(4, k.length)] = vol;
        //delete row[row.length-2][k];
      }
      if (k == "state") {
//					if(k == "tiaojian" || k == "state"){
        delete row[row.length - 2][k];
      }
    });
    row[row.length - 2].oopp = temm;
    sc_v1.table.push(row[row.length - 2]);
//		}
    //
//		$.each(row[row.length-1], function(k, vol) {//循环所有的列
//			if(k.indexOf("psl_")==0){
//				if(vol != ""){//只要有一个控制措施有值，就进入计算
//					tt2 = true;
//				}
//			}
//		});
//		if(tt2){
    var temm = {};
    $.each(row[row.length - 1], function (k, vol) {
      if (k.indexOf("psl_") == 0) {//第一个字母是下划线开头
        temm[k.substring(4, k.length)] = vol;
        //delete row[row.length-1][k];
      }
      if (k == "state") {
//					if(k == "tiaojian" || k == "state"){
        delete row[row.length - 1][k];
      }
    });
    row[row.length - 1].oopp = temm;
    sc_v1.table.push(row[row.length - 1]);
//		}

    sc_v1.table1 = [];
    for (var i = row.length - 3; i > 0; i--) {

      var temm = {};
      $.each(row[i], function (k, vol) {
        if (k.indexOf("psl_") == 0) {//第一个字母是下划线开头
          temm[k.substring(4, k.length)] = vol;
          //delete row[i][k];
        }
        if (k == "state") {
//					if(k == "tiaojian" || k == "state"){
          delete row[i][k];
        }
      });
      row[i].oopp = temm;
      sc_v1.table1.push(row[i]);
    }

    var paramsName = {};
    paramsName.userId = userId;
    paramsName.sectorName = m_sectorName;
    paramsName.measureId = m_mid;
    paramsName.planId = m_planId;
    paramsName.planMeasureId = m_planMeasureId;
    paramsName.measureContent = JSON.stringify(sc_v1);
    paramsName.scenarinoId = qjMsg.qjId;

    console.log(JSON.stringify(sc_v1));
    ajaxPost('/measure/addOrUpdate_measure', paramsName).success(function (res) {
//			console.log(JSON.stringify(res));
      if (res.status == 0) {
        hyc();
        metTable_hj_info();
        $("#zicuoshi_close").click();
        zicuoshi_bool = true;
      } else {
        swal('连接错误', '', 'error');
        zicuoshi_bool = true;
      }
    });

  }
}


/**
 * json元素的个数
 */
function JSONLength(obj) {
  var size = 0, key;
  for (key in obj) {
    if (obj.hasOwnProperty(key)) size++;
  }
  return size;
};

function Trim(str) {
  return str.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 操作地图显示
 */
var stat = {cPointx: 106, cPointy: 35}, app = {}, dong = {};
var dojoConfig = {
  async: true,
  parseOnLoad: true,
  packages: [{
    name: 'tdlib',
    location: "/js/tdlib"
  }],
  paths: {
    extras: location.pathname.replace(/\/[^/]+$/, '') + "/js/extras"
  }
};
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol", 'esri/symbols/PictureMarkerSymbol',
    'esri/renderers/ClassBreaksRenderer', "esri/symbols/SimpleMarkerSymbol", 'esri/dijit/PopupTemplate', "esri/geometry/Point", "esri/geometry/Extent", "esri/renderers/SimpleRenderer", "esri/graphic",
    "dojo/_base/Color", "dojo/dom-style", 'dojo/query', "esri/tasks/FeatureSet", "esri/SpatialReference", 'extras/ClusterLayer', "tdlib/gaodeLayer", "dojo/domReady!"],
  function (Map, FeatureLayer, GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, PictureMarkerSymbol, ClassBreaksRenderer, SimpleMarkerSymbol, PopupTemplate, Point, Extent, SimpleRenderer, Graphic,
            Color, domStyle, query, FeatureSet, SpatialReference, ClusterLayer, gaodeLayer) {
    dong.gaodeLayer = gaodeLayer;
    dong.Graphic = Graphic;
    dong.Point = Point;
    dong.GraphicsLayer = GraphicsLayer;
    dong.SpatialReference = SpatialReference;
    dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
    dong.Extent = Extent;
    dong.SimpleLineSymbol = SimpleLineSymbol;
    dong.Color = Color;
    dong.PopupTemplate = PopupTemplate;
    dong.ClusterLayer = ClusterLayer;
    dong.PictureMarkerSymbol = PictureMarkerSymbol;
    dong.ClassBreaksRenderer = ClassBreaksRenderer;
    dong.domStyle = domStyle;
    dong.query = query;
    app.mapList = new Array();
    app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
    app.stlayerList = new Array();//加载卫星图
    app.labellayerList = new Array();//加载标注图

    for (var i = 0; i < 2; i++) {
      var map = new Map("mapDiv" + i, {
        logo: false,
        center: [stat.cPointx, stat.cPointy],
        minZoom: 3,
        maxZoom: 13,
        zoom: 3
      });

      app.mapList.push(map);
      app.baselayerList[i] = new dong.gaodeLayer();
      app.stlayerList[i] = new dong.gaodeLayer({layertype: "st"});
      app.labellayerList[i] = new dong.gaodeLayer({layertype: "label"});
      app.mapList[i].addLayer(app.baselayerList[i]);//添加高德地图到map容器
      app.mapList[i].addLayers([app.baselayerList[i]]);//添加高德地图到map容器
    }
    app.gLyr = new dong.GraphicsLayer({"id": "gLyr"});
    app.mapList[0].addLayer(app.gLyr);

//		app.pint = new dong.GraphicsLayer({"id":"pint"});
//		app.mapList[1].addLayer(app.pint);

//		app.layer = new esri.layers.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer");//创建动态地图
//		app.mapList[0].addLayer(app.layer);

  });


//**************************************************************************************************************************************
/**
 * 主窗口地图显示,加点专题图
 */
function main_gis_point() {

  //获取所有行业控制的点源坐标
  ajaxPost_w(jianpaiUrl + '/search/', {"bigIndex": qjMsg.esCouplingId, "planId": qjMsg.planId}).success(function (res) {
//		console.log(JSON.stringify(res));
    if (res.status == 'success') {

    } else {
      swal('连接错误search/', '', 'error');
    }
  });

}


//**************************************************************************************************************************************
var point_message = "";
var clusterLayer_ttft = "";
/**
 * 模态窗口地图加点
 */
function add_point(col) {
//	console.log(JSON.stringify(col));

  if (clusterLayer_ttft != "") {
    app.mapList[1].removeLayer(clusterLayer_ttft);//清空已有的点位信息
    app.mapList[1].infoWindow.hide();
  }
  var photoInfo = {};
  var point_sz = [];
  var xmax = 0, xmin = 0, ymax = 0, ymin = 0;

  var k = 0;
  $.each(col, function (i, vol) {
    if (typeof vol.lon != "undefined" && vol.lon != "") {
      if (typeof vol.lat != "undefined" && vol.lat != "") {
        //中国范围内
        if (parseFloat(vol.lon) > 70 && parseFloat(vol.lon) < 135 && parseFloat(vol.lat) > 15 && parseFloat(vol.lat) < 55) {
          var x = handle_x(vol.lon);
          var y = handle_y(vol.lat);
          if (k == 0) {
            xmax = x;
            xmin = x;
            ymax = y;
            ymin = y;
          } else {
            xmin = x < xmin ? x : xmin;
            xmax = x > xmax ? x : xmax;
            ymin = y < ymin ? y : ymin;
            ymax = y > ymax ? y : ymax;
          }
          var attributes = {
            "企业名称": "<a id=\"companyId_h\" onClick=\"companyInfo('" + vol.companyId + "');\">" + vol.companyname + "</a>"
          };
          point_sz[k] = {"x": x, "y": y, "attributes": attributes};
          k++;
        }
      }
    }
  });

  if (point_sz.length > 0) {
    photoInfo.data = point_sz;
    clusterLayer = new dong.ClusterLayer({
      "data": photoInfo.data,
      "distance": 150,
      "id": "clusters",
      "labelColor": "#fff",
      "labelOffset": -4,
      "resolution": app.mapList[1].extent.getWidth() / app.mapList[1].width,
      "singleColor": "#888",
      "maxSingles": 3000,
      "showSingles": false

    });

    var defaultSym = new dong.SimpleMarkerSymbol().setSize(4);
    var renderer = new dong.ClassBreaksRenderer(defaultSym, "clusterCount");
    var style1 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([72, 165, 251]), 1), new dong.Color([218, 83, 25, 0.9]));
    var style2 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([122, 251, 159]), 1), new dong.Color([218, 83, 25, 0.9]));
    var style3 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 25, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([229, 251, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
    var style4 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 30, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 171, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
    var style5 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 35, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 100, 72]), 1), new dong.Color([218, 83, 25, 0.9]));

    renderer.addBreak(0, 10, style1);
    renderer.addBreak(10, 100, style2);
    renderer.addBreak(100, 1000, style3);
    renderer.addBreak(1000, 10000, style4);
    renderer.addBreak(10000, 99999999, style5);

    clusterLayer.setRenderer(renderer);
    app.mapList[1].addLayer(clusterLayer);
    clusterLayer_ttft = clusterLayer;

    setTimeout(function () {
      var extent = new dong.Extent(xmin, ymin, xmax, ymax, new dong.SpatialReference({wkid: 3857}));
      app.mapList[1].setExtent(extent.expand(1.5));
    }, 100);
  }
}

/**
 * 点击make点事件，显示企业排放信息
 * @param companyId
 */
function companyInfo(companyId) {

  if (typeof companyId != "undefined" && companyId != "") {
//		console.log(JSON.stringify(sc_val.summary));
    ajaxPost_w(jianpaiUrl + '/search/companyInfo', {
      "bigIndex": qjMsg.esCouplingId, "smallIndex": stname, "regionIds": Codes, "summary": sc_val.summary,
      "append": {"companyId": companyId}
    }).success(function (res) {
//			console.log(JSON.stringify(res));
      if (res.status == 'success') {

        var info_html = '<table class="table table-bordered table-striped"><thead><tr><td>指标项</td><td>排放量</td><td>控制量</td></tr></thead><tbody>';

        $.each(res.data.total, function (i, vol) {
          info_html += "<tr>";

          $.each(columns, function (k, col) {
            if (col.field == i && i != "count") {
              info_html += '<td>' + col.title + '</td>';

              info_html += '<td>' + liangtoFixed(vol) + '</td>';

              info_html += '<td></td>';
            }
          });
          info_html += "</tr>";
        });
        info_html += '</tbody></table>';

        var ys = $(info_html);
        $("#companyId_h").parent().after(ys);

      } else {
        swal('连接错误search/companyCount', '', 'error');
      }
    });


  }

}


/**
 * 保留两位有效数字
 * @param jk
 * @returns
 */
function liangtoFixed(jk) {
  if (jk >= 1 || jk == 0) {
    return jk.toFixed(2);
  } else {
    var k = 2;//默认小数点保留两位
    var tted;
    do {
      if (k < jk.length - 2) {
        tted = jk.toFixed(k);
        k++;
      } else {
        return tted;
      }
    } while (tted.substring(tted.length - 2, tted.length - 1) == "0" || tted.substring(tted.length - 1, tted.length) == "0");
    return tted;
  }
}


function cleanUp() {
  app.mapList[1].infoWindow.hide();
  //clusterLayer.clearSingles();
}


//点击点的事件
function optionclick(event) {
  app.mapList[0].infoWindow.hide();
  var companyId = "";
  $.each(point_message, function (i, item) {
//		console.log(event.graphic._extent.xmax);
    if (handle_x(item.lon) == event.graphic._extent.xmax && handle_y(item.lat) == event.graphic._extent.ymax) {
      companyId = item.companyId;
    }
  })
  var ert = {};
  ert.bigIndex = qjMsg.esCouplingId;
  ert.smallIndex = sc_val.smallIndex;
  ert.filters = [{"companyId": companyId}];
  ert.summary = sc_val.summary;

  ajaxPost_w(jianpaiUrl + '/search/companyInfo', ert).success(function (res) {
//		console.log(res);
    if (res.status == "success") {

    }
//		 var content =  "22222<br><b>Order</b>: erer<br><b>SubOrder</b>: 23<br><b>Description</b>: ssss<br><b>Drainage</b>:graphic.attributes.DrainageCl " +
//	       + "<br><a href='#' onclick=\"infoWindowMobileHandler(event)\" class='info-window-mobile-button ui-btn'>Close</a> ";
//	     app.mapList[1].infoWindow.setTitle("");
//	     app.mapList[1].infoWindow.setContent(content);
//	     app.mapList[1].infoWindow.show(event.mapPoint);
  });
}
