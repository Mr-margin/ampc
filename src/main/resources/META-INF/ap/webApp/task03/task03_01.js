/**
 * Created by lvcheng on 2017/5/12.
 */

/**
 * 设置导航条菜单
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景编辑</a>>><a href="#/csbj" style="padding-left: 15px;padding-right: 15px;">措施编辑</a>>><span style="padding-left: 15px;padding-right: 15px;">子措施编辑</span>');

/*设置变量*/
var sc_val = {};//存储最终的自措施条件
var columns = [];//模态框内表格显示的表头
var point_z = [];//点源总数


var ls = window.sessionStorage;
var csMsg = vipspa.getMessage('csMessage').content;
if (!csMsg) {
    csMsg = JSON.parse(ls.getItem('csMsg'));
} else {
    ls.setItem('csMsg', JSON.stringify(csMsg));
}

$('.csCon').removeClass('disNone');
$('.csCon .nowRw span').html(csMsg.rwName);
$('.csCon .nowQj span').html(csMsg.qjName);
$('.csCon .nowArea span').html(csMsg.areaName);
$('.csCon .nowTime span').html(csMsg.planName);
$('.csCon .seDate span').html(moment(csMsg.timeStartDate).format('YYYY-MM-DD HH') + '<br>至<br>' + moment(csMsg.timeEndDate).format('YYYY-MM-DD HH'));

var Codes = [];
$.each(csMsg.cityCodes, function (i, col) {
    $.each(col, function (k, vol) {
        Codes.push(k);
    });
});
$.each(csMsg.countyCodes, function (k, col) {
    $.each(col, function (k, vol) {
        Codes.push(k);
    });
});
$.each(csMsg.provinceCodes, function (k, col) {
    $.each(col, function (k, vol) {
        Codes.push(k);
    });
});

/*初始化第三个手风琴菜单打开*/
$('.menuCD:nth-child(3)').css('width','calc(100% - 120px)');
$('.menuCD:nth-child(3) .menuCD_con').css('opacity',1);


/*手风琴点击事件*/
$('.menuCD_title').click(function (e) {
     $('.menuCD').css('width','45px');
     $('.menuCD_con').css('opacity',0);
     $(e.target).parents('.menuCD').css('width','calc(100% - 120px)');
     $(e.target).parents('.menuCD').find('.menuCD_con').css('opacity',1);
});

/*初始化函数*/
function initialize() {
    open_cs(csMsg.sectorsName,csMsg.measureame,csMsg.mid,csMsg.planMeasureId)
}



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
    $("#dianyaunzushu").html("");//清空点源与占比
    $("#xiangxizhibiao").html("");

    var paramsName = {};
    paramsName.userId = userId;
    paramsName.sectorName = sectorsName;
    paramsName.measureId = mid;
    paramsName.planId = csMsg.planId;
    paramsName.planMeasureId = planMeasureId;

    sc_val = {};//初始化缓存
    sc_val.bigIndex = csMsg.esCouplingId;
    sc_val.smallIndex = sectorsName;//记录行业
    sc_val.filters = [];
    sc_val.summary = {};
    sc_val.regionIds = Codes;

    columns = [];
    point_z = [];//点源总数

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

                    sc_conter += '<div class="btn-group tiaojian col-6" ' + queryShowquery + ' sc_su="' + col.queryShowqueryen + '" sc_su_val="' + col.queryValue + '">';
                    sc_conter += '<span class="font-bold tiaojian-title" style="width: 90px;">' + col.queryName.replace("(", "<br>(") + '</span>';
                    sc_conter += '<div class="btn-group btn-group-circle" data-toggle="buttons" id="' + col.queryEtitle + '" onclick="val_handle($(this),\'' + col.queryEtitle + '\',\'' + col.queryName + '\',\'' + type + '\',\'onclick\');">';
                    if (col.queryOptiontype.indexOf("值域") >= 0) {//所有的值域需要自定义修改
                        sc_conter += '<label class="btn btn-success active" val_name="不限"><input type="' + type + '" class="toggle">不限</label>';

                        for (var k = 1; k < 6; k++) {//循环五个指标项，
                            if (col["queryOption" + k] == null) {//指标项为空
                                if (k > 1) {//不是第一个指标项，增加最后一个第六项，大于最大值
                                    sc_conter += '<label class="btn btn-success" val_name="' + col["queryOption" + (k - 1)] + '~"><input type="' + type + '" class="toggle">≥' + col["queryOption" + (k - 1)] + '</label>';
                                    break;
                                }
                            } else {
                                if (k == 1) {//指标项有内容的情况下，第一个指标从零开始
                                    sc_conter += '<label class="btn btn-success" val_name="0~' + (col.queryOption1 - 1) + '"><input type="' + type + '" class="toggle">0-' + (col.queryOption1 - 1) + '</label>';
                                } else {//其余的指标均是上一段到本段减一
                                    sc_conter += '<label class="btn btn-success" val_name="' + (col["queryOption" + (k - 1)] - 0) + '~' + (col["queryOption" + k] - 1) + '"><input type="' + type + '" class="toggle">' + (col["queryOption" + (k - 1)] - 0) + '-' + (col["queryOption" + k] - 1) + '</label>';
                                }
                                if (k == 5) {//到最后一个指标项的时候，增加第六项
                                    sc_conter += '<label class="btn btn-success" val_name="' + (col["queryOption" + k] - 0) + '~"><input type="' + type + '" class="toggle">≥' + (col["queryOption" + k] - 0) + '</label>';
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
                        sc_conter += '<label class="btn btn-success active" val_name="不限"><input type="' + type + '" class="toggle">不限</label>';
                        sc_conter += col.queryOption1 == null ? '' : '<label class="btn btn-success" val_name="' + col.queryOption1 + '"><input type="' + type + '" class="toggle">' + col.queryOption1 + '</label>';
                        sc_conter += col.queryOption2 == null ? '' : '<label class="btn btn-success" val_name="' + col.queryOption2 + '"><input type="' + type + '" class="toggle">' + col.queryOption2 + '</label>';
                        sc_conter += col.queryOption3 == null ? '' : '<label class="btn btn-success" val_name="' + col.queryOption3 + '"><input type="' + type + '" class="toggle">' + col.queryOption3 + '</label>';
                        sc_conter += col.queryOption4 == null ? '' : '<label class="btn btn-success" val_name="' + col.queryOption4 + '"><input type="' + type + '" class="toggle">' + col.queryOption4 + '</label>';
                        sc_conter += col.queryOption5 == null ? '' : '<label class="btn btn-success" val_name="' + col.queryOption5 + '"><input type="' + type + '" class="toggle">' + col.queryOption5 + '</label>';
                        sc_conter += '</div>';
                    }

                    sc_conter += '</div>';
                });
                // sc_conter += '<div id="se_bu" style="position: absolute;right: 20px; bottom: 20px;">';//padding-right: 20px;text-align: right;
                // sc_conter += '<button class="btn btn-success" onclick="search_button();" type="button">';
                // sc_conter += '<i class="fa fa-search"></i>&nbsp;&nbsp;<span class="bold">筛选</span>';
                // sc_conter += '</button></div>';

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

                    xishuMO += '<div class="col-6" style="margin-top: 10px;">';
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
                "bigIndex": csMsg.esCouplingId, "smallIndex": sectorsName, "regionIds": Codes
            }).success(function (res) {
//					console.log(JSON.stringify(res));
                if (res.status == 'success') {
                    $("#dianyaunzushu").html("点源总数：" + res.data.count);
                    if (res.data.count == 0) {
                        $("#se_bu").hide();//筛选按钮关闭
                        $("#se_bu_na").hide();//筛选按钮关闭
                        $("#se_name").hide();//企业名称输入关闭
                    } else {
                        $("#se_bu").show();
                        $("#se_bu_na").show();
                        $("#se_name").show();
                    }
                    $("#xiangxizhibiao").html("点源排放占比:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SO<sub>2</sub>:" + res.data.rate.SO2 + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NO<sub>x</sub>:" + res.data.rate.NOx + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PM<sub>2.5</sub>:" + res.data.rate.PM25 + "%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;VOCs:" + res.data.rate.VOC + "%");
                    //add_point(res.data.company);

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
//				console.log(JSON.stringify({"bigIndex":csMsg.esCouplingId,"smallIndex":sectorsName,"summary":sc_val.summary,"regionIds":Codes}));
                ajaxPost_w(jianpaiUrl + '/search/summarySource', {
                    "bigIndex": csMsg.esCouplingId,
                    "smallIndex": sectorsName,
                    "summary": sc_val.summary,
                    "regionIds": Codes
                }).success(function (da) {
//					console.log(JSON.stringify(da));
                    if (da.status == 'success') {
                        var zz = {"f1": "汇总", "f2": "0/" + da.data.P[0].count},
                            pp = {"f1": "剩余点源", "f2": da.data.P[0].count},
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
 * 显示子措施表格的列表
 */
function show_zicuoshi_table(columns, b_data) {
     var arr = [];
     arr.push(columns);
    // $('#show_zicuoshi_table').datagrid('destroy');
    $('#show_zicuoshi_table').datagrid({
        columns: arr,
        data: b_data,
        height: $("#show_zicuoshi_table_div").height(),
        // clickToSelect: false,// 点击选中行
        detailView: true,//显示详细页面模式
        pagination: false, // 在表格底部显示分页工具栏
        clickToSelect: true,//设置true 将在点击行时，自动选择rediobox 和 checkbox
        singleSelect: true,//设置True 将禁止多选
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        showGroup:true,
        scrollbarSize: 0,
//        title:"DataGrid - SubGrid",
    	fitColumns:"true",

        view: detailview,
        onExpandRow: function(index,row){
            $('#show_zicuoshi_table').datagrid('fixDetailRowHeight',index);
        },

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
        onCheck: function (row) {
            if (row.f1 != "剩余点源" && row.f1 != "面源") {
                $("#zicuoshi_tools_de").show();
                $("#zicuoshi_tools_up").show();
            } else {
                $("#zicuoshi_tools_de").hide();
                $("#zicuoshi_tools_up").show();
            }
        },
        onUncheck: function (row) {
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
    	 $('#show_zicuoshi_table').datagrid('fixDetailRowHeight',i);
//         $('#show_zicuoshi_table').datagrid('expandRow', i);
//         $('#show_zicuoshi_table').datagrid('appendRow',i);
     });
}

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
 * 筛选按钮
 */
function search_button() {

    var ttlk = true;//点源的编辑状态
    var row = $('#show_zicuoshi_table').datagrid('getData');//已有子措施的所有的记录
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
            //如果筛选为空 则清空下面企业列表中的数据
            $("#mic").hide();
            $("#shaixuan_num").html("");
            $("#metTable_tools").hide();//保存子措施按钮
            $("#metTable_name_tools").hide();//保存子措施按钮

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
//        console.log(JSON.stringify(sc_val));
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

/*初始化*/
initialize();