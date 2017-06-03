
/*左侧菜单栏显示*/
(function () {
    $('.cryak').show();
    $('.wc').show();
    $('.nowCS').hide();
    $('.nowHY').hide();
})()
$("#show").click(function () {
    if ($("#slider_wrap").css("display") == "none") {
        $("#slider_wrap").show();
        $("#custom").css("background-position", "right -18px");
    } else {
        $("#slider_wrap").hide();
        $("#custom").css("background-position", "right 8px");
    }
});
var wrwSelect;
var ls = window.sessionStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;
if (!qjMsg) {
    qjMsg = JSON.parse(ls.getItem('yaMsg'));
} else {
    ls.setItem('yaMsg', JSON.stringify(qjMsg));
}
var checkeded;//选中行
var msg = {
    'id': 'csMessage',
    'content': qjMsg
};
var pointColor = {};
//console.log(JSON.stringify(qjMsg));

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
//    console.log(JSON.stringify(res));
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
            swal('连接错误/plan/iscopy_plan' + JSON.stringify(res), '', 'error');
        }
    });
}

/**
 * 模态窗口的地图切换全屏
 */
function m_gis_q() {
    if ($("#left_div").is(":hidden")) {
        $("#right_top_div").css("height", "50%");
        $("#right_div").attr("class", "col-6");
        $("#left_div").show();//左侧两部分显示
        $("#right_bottom_div").show();//右侧底部显示
    } else {
        $("#left_div").hide();//左侧两部分显示
        $("#right_bottom_div").hide();//右侧底部显示
        $("#right_top_div").css("height", "100%");
        $("#right_div").attr("class", "col-12");
    }
}

/**
 * 设置导航条菜单
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a><i class="en-arrow-right7" style="font-size:16px;"></i><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景编辑</a><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;font-size:12px;">措施编辑</span>');

$(function () {

})

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

/**
 * 时间戳转成日期格式
 * @param nS
 * @returns
 */
function getLocalTime(nS) {
    return moment(nS).format('YYYY-MM-DD HH');
}


/*手风琴初始化处理*/
(function () {
    $('.menuCD_con').css('height', '0');

    $('#accordion').on('click','.menuCD_title',function (e) {
        $('.openAccordion').find('.en-arrow-up7').css('transform','rotate(0deg)')
        $('.openAccordion').removeClass('openAccordion');
        var len = $(e.target).parents('.menuCD').attr('data-cslen');
        if ($(e.target).parents('.menuCD').find('.menuCD_con').css('height') != '0px') {
            $(e.target).parents('.menuCD').find('.menuCD_con').css('height', '0');
        } else {
            $(e.target).parents('.menuCD').addClass('openAccordion');
            $(e.target).parents('.menuCD').find('.en-arrow-up7').css('transform','rotate(180deg)');
            $('.menuCD_con').css('height', '0');
            $(e.target).parents('.menuCD').find('.menuCD_con').css('height', (Math.ceil(len/2)*45 + 10)+'px')

            var name = $(e.target).parents('.menuCD').find('.menuCD_title').attr('val_name');
            $("#hangyedetype").children().each(function () {
                if ($(this).is('.active')) {
                    hangyede_type = $(this).attr("val_name");
                }
            });
            if($("#hangyedetype").find('.active').attr('val_name') == 'dq'){
                metTable_hj_info(name);
            }
        }
    })
})();


/**
 * 获取用户的行业与措施，页面初始化赋值
 */
function hyc() {
    var paramsName = {};
    paramsName.userId = userId;
    paramsName.planId = qjMsg.planId;
    paramsName.timeId = qjMsg.timeId;
    paramsName.timeStartTime = moment(qjMsg.timeStartDate).format('YYYY-MM-DD HH:ss');
    paramsName.timeEndTime = moment(qjMsg.timeEndDate).format('YYYY-MM-DD HH:ss');

    $("#dangqianrenwu").html("当前任务：" + qjMsg.rwName);
    $("#dangqianqingjing").html("当前情景：" + qjMsg.qjName);
    $("#dangqianquyu").html("当前区域：" + qjMsg.areaName);
    $("#dangqianshiduan").html("当前时段：" + getLocalTime(qjMsg.timeStartDate) + " 时 至 " + getLocalTime(qjMsg.timeEndDate) + " 时");

    m_planId = qjMsg.planId;
    $("#accordion").html("");
    ajaxPost('/plan/get_planInfo', paramsName).success(function (res) {

        if(res.status == 0){
            var accordion = $("#accordion");
            pointColor = {};
            var len = res.data.length;
            $.each(res.data,function (i, col) {

                pointColor[col.sectorsName] = colorArr[Math.floor(colorArr.length/len * i)];

                //      var inn = i == 0 ? "in" : "";//第一个手风琴页签打开
                var inn = "";
                if (stname) {
                    inn = stname == col.sectorsName ? "in" : "";
                }
                var caidan = $('<div class="menuCD" data-cslen="'+ col.measureItems.length +'"></div>');
                var caidan_title = $('<div class="menuCD_title" title="'+ col.sectorsName +'" val_name="' + col.sectorsName + '"  ></div>');
                caidan_title.append($('<span>'+ col.sectorsName +'</span>'));
                //添加箭头
                caidan_title.append($('<i class="en-arrow-up7" style="float: right;line-height:45px;"></i>'))
                if (col.planMeasure.length > 0) {
                    caidan_title.append($('<span class="measureNum" style="float:right;line-height:45px;">已使用 '+ col.planMeasure.length +' 条措施</span>'));
                }
                var caidan_con = $('<div class="menuCD_con"></div>');

                $.each(col.measureItems,function (m, vol) {
                    var caidan_k;
                    var bggh = true;

                    if (col.planMeasure.length > 0) {
                        $.each(col.planMeasure,function (n, ool) {
                            if (ool.measureId == vol.mid) {
                                caidan_k = $('<div class="col-6 c6center"><a class="btnG btn btn-success-cs-w" style="width:80%;" onclick="open_csbjSon(\''+col.sectorsName+'\',\''+vol.measureame+'\',\''+vol.mid+'\',\''+ ool.planMeasureId +'\')";><i class="fa fa-check"> </i> '+ vol.measureame +'</a></div>')
                                bggh = false;
                            }
                        })
                    }

                    if(bggh){
                        caidan_k = $('<div class="col-6 c6center"><a class="btnG btn btn-success-cs" style="width:80%;" onclick="open_csbjSon(\''+col.sectorsName+'\',\''+vol.measureame+'\',\''+vol.mid+'\',\'null\')";><i class="fa fa-ban"> </i> '+ vol.measureame +'</a></div>')
                    }

                    caidan_con.append(caidan_k);
                });
                caidan.append(caidan_title);
                caidan.append(caidan_con);
                accordion.append(caidan);

            })
            $(".measureNum").css({"margin-right":"20px","color":"rgb(210, 68, 64)"}).parents('.menuCD_title').find('.en-arrow-up7').css('color','rgb(210, 68, 64)')
        } else {
            swal('连接错误/plan/get_planInfo', '', 'error');
//			swal('添加成功', '', 'success');
        }

return;


    }).error(function () {
        swal('校验失败', '', 'error')
    })
}


/**
 * 获取措施汇总
 */
function metTable_hj_info(pa_name) {

    // $('#metTable_hj').datagrid('destroy');
    
    $("#hz_de").hide();
    $("#hz_up").hide();
    $("#jianpaijisuan").hide();

    var hangye = "";//手风琴当前打开的内容

    if (typeof pa_name != "undefined") {
        hangye = pa_name;
    } else {
        hangye = $("#accordion .openAccordion .menuCD_title").attr("val_name");
    }

    //行业的查询状态
    var hangyede_type = "";
    $("#hangyedetype").children().each(function () {
        if ($(this).is('.active')) {
            hangyede_type = $(this).attr("val_name");
        }
    });

    var columnsw = [[]];
    columnsw[0].push({field: 's1', title: '', align: 'center', checkbox: true});
    columnsw[0].push({field: 'sectorName', title: '行业', align: 'center'});
    columnsw[0].push({field: 'measureName', title: '措施', align: 'center'});
    columnsw[0].push({field: 'implementationScope', title: '点源实际范围', align: 'center'});
    columnsw[0].push({
        field: 'reduct', title: '涉及年化排放占比', align: 'center', formatter: function (value, row, index) {

            return value;
        }
    });
    columnsw[0].push({
        field: 'ratio', title: '年化减排比例', align: 'center', formatter: function (value, row, index) {

            return value;
        }
    });

    var data = {};
    data.planId = qjMsg.planId;
    data.userId = userId;
    data.stainType = $('#hz_wrw').val();
    if (hangyede_type == "dq") {
        data.sectorName = hangye;
    }
    wrwSelect=data.sectorName;
    getMapPoint(data.sectorName);
    // ajaxPost('/measure/get_measureList',data).success(function (res) {
      
        $('#metTable_hj').datagrid({
            method: 'POST',
            url: "/ampc/measure/get_measureList",
            dataType: "json",
            // data:res,
            columns: columnsw, //列
            checkOnSelect:true,
            selectOnCheck:true,
            clickToSelect: true,// 点击选中行
            pagination: false, // 在表格底部显示分页工具栏
            singleSelect: true,//设置True 将禁止多选
            striped: false, // 使表格带有条纹
            silent: true, // 刷新事件必须设置
            contentType: "application/json",
            queryParams: function (params) {
                var data = {};
                data.planId = qjMsg.planId;
                data.userId = userId;
                data.stainType = $('#hz_wrw').val();
                if (hangyede_type == "dq") {
                    data.sectorName = hangye;
                }
//			console.log(JSON.stringify({"token": "", "data": data}));
//                 return JSON.stringify({"token": "", "data": data});
                return {"token": "", "data": data};
            },
            queryParamsType: "limit", //参数格式,发送标准的RESTFul类型的参数请求
            loadFilter: function (res) {

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
            onLoadSuccess: function (data) {
                if (data.rows.length > 0) {
                    $("#jianpaijisuan").show();
                }
            },
            onCheck: function (index,row) {
                if (row.planMeasureId != checkeded) {//如果被选中
                    checkeded = row.planMeasureId;
                    $("#hz_de").show();
                    $("#hz_up").show();
                } else {
                    checkeded = '-1';
                    $('#metTable_hj').datagrid('clearChecked');
                    $("#hz_de").hide();
                    $("#hz_up").hide();
                }
            },
        });
      
    // })
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
 * 获取地图上的点信息
 * @param sector 所请求的行业
 */
function getMapPoint(sector) {
    var parameter = {
        userId:userId,
        planId:qjMsg.planId,
        species:$('#hz_wrw').val(),
        sector:sector||'',
    }

    ajaxPost_w(jianpaiUrl+'/search/companyPoint',parameter).success(function (res) {
        if(res.code == '0'){
            if(res.data.total){
                var total = res.data.total;
                var pointArr = [];
                for(var i in total){
                    if(total[i].length>0){
                        pointArr = pointArr.concat(total[i]);

                    }
                }
                if(pointArr.length == 0){
                    app.mapList[0].removeLayer(clusterLayer_ttft);//清空已有的点位信息
                }else{
                    add_point(pointArr);
                }

                // if(res.data.total.length>0){
                //     add_point(res.data.total);
                // }else if(res.data.total.length==0){
                //     app.mapList[0].removeLayer(clusterLayer_ttft);//清空已有的点位信息
                // }
            }else{
                console.log('/search/companyPoint返回数据格式错误')
            }
        }else{
            swal({
                title: "地图点源信息查询有误",
                type:'error',
                timer: 2000,
                showConfirmButton: false
            });
        }

    }).error(function () {
        swal({
            title: "/search/companyPoint 接口错误",
            type:'error',
            timer: 2000,
            showConfirmButton: false
        });
    })

}

/**
 * 汇总的修改按钮
 */

function hz_up() {
    var row = $('#metTable_hj').datagrid('getSelections');
    if (row.length > 0) {
        open_csbjSon(row[0].sectorName, row[0].measureName, row[0].measureId, row[0].planMeasureId);
        // open_cs(row[0].sectorName, row[0].measureName, row[0].measureId, row[0].planMeasureId);
    }
}

/**
 * 汇总的删除按钮
 */
function hz_de() {
    var row = $('#metTable_hj').datagrid('getSelections');
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
    var row = $('#metTable_hj').datagrid('getData');//获取所有的数据
    var planMeasureIds = "";
    $.each(row.rows, function (i, col) {
        planMeasureIds += col.planMeasureId + ",";
    });
    zmblockUI("#jianpaijisuanbox", 'start');
//  console.log(1);
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

var poi_name_or_pub;//记录当前的筛选条件是按照属性来，还是按照名称来



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

        for (var i = 0; i < 1; i++) {
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

      //定义绘制图层的点击事件，此模块内绘制图层只有点，所以直接是marker点的单击事件
    	dojo.connect(app.gLyr, "onClick", capitalclick);
    	
//		app.pint = new dong.GraphicsLayer({"id":"pint"});
//		app.mapList[0].addLayer(app.pint);

//		app.layer = new esri.layers.ArcGISDynamicMapServiceLayer(ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer");//创建动态地图
//		app.mapList[0].addLayer(app.layer);

        hyc();

        metTable_hj_info();

    });


//**************************************************************************************************************************************
/**
 * 主窗口地图显示,加点专题图
 */
function main_gis_point() {

    //获取所有行业控制的点源坐标
    ajaxPost_w(jianpaiUrl + '/search/', {
        "bigIndex": qjMsg.esCouplingId,
        "planId": qjMsg.planId
    }).success(function (res) {
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
 * 主窗口地图加点
 */
function add_point(col) {
//	console.log(JSON.stringify(col));

    if (clusterLayer_ttft != "") {
        app.mapList[0].removeLayer(clusterLayer_ttft);//清空已有的点位信息
        app.mapList[0].infoWindow.hide();
    }
    
//    var photoInfo = {};
//    var point_sz = [];
    
    
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
                    
                    
                    
//                    var attributes = {
//                        "企业名称": "<a id=\"companyId_h\" onClick=\"companyInfo('" + vol.companyId + "');\">" + vol.companyname + "</a>"
//                    };
//                    point_sz[k] = {"x": x, "y": y, "attributes": attributes};
//                    k++;
                    
                    var point = new dong.Point(x, y, new dong.SpatialReference({wkid: 3857}));
                    point.companyId = vol.companyId;
                    point.companyname = vol.companyname;
                    point.smallIndex = vol.smallIndex;
                    
                    var graphic = new dong.Graphic(point, createSymbol(vol.smallIndex));
                    app.gLyr.add(graphic);
                    
                }
            }
        }
    });

//    if (point_sz.length > 0) {
//        photoInfo.data = point_sz;
//        clusterLayer = new dong.ClusterLayer({
//            "data": photoInfo.data,
//            "distance": 150,
//            "id": "clusters",
//            "labelColor": "#fff",
//            "labelOffset": -4,
//            "resolution": app.mapList[0].extent.getWidth() / app.mapList[0].width,
//            "singleColor": "#888",
//            "maxSingles": 3000,
//            "showSingles": false
//
//        });
//
//        var defaultSym = new dong.SimpleMarkerSymbol().setSize(4);
//        var renderer = new dong.ClassBreaksRenderer(defaultSym, "clusterCount");
//        var style1 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([72, 165, 251]), 1), new dong.Color([218, 83, 25, 0.9]));
//        var style2 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([122, 251, 159]), 1), new dong.Color([218, 83, 25, 0.9]));
//        var style3 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 25, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([229, 251, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
//        var style4 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 30, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 171, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
//        var style5 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 35, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 100, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
//
//        renderer.addBreak(0, 10, style1);
//        renderer.addBreak(10, 100, style2);
//        renderer.addBreak(100, 1000, style3);
//        renderer.addBreak(1000, 10000, style4);
//        renderer.addBreak(10000, 99999999, style5);
//
//        clusterLayer.setRenderer(renderer);
//        app.mapList[0].addLayer(clusterLayer);
//        clusterLayer_ttft = clusterLayer;
//
//        setTimeout(function () {
//            var extent = new dong.Extent(xmin, ymin, xmax, ymax, new dong.SpatialReference({wkid: 3857}));
//            app.mapList[0].setExtent(extent.expand(1.5));
//        }, 100);
//    }
}

/**
 * 为marker点设置显示样式
 */
var styleType = [];
function createSymbol(smallIndex){

    if(smallIndex == '水泥'){
        console.log(1)
    }else if(smallIndex == '钢铁'){
        console.log(2)
    }else{
        console.log(3)
    }
	
  var style1 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([122, 251, 159]), 1), new dong.Color(pointColor[smallIndex]));
    // var style1 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([72, 165, 251]), 1), new dong.Color([218, 83, 25, 0.9]));
  var style2 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 20, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([122, 251, 159]), 1), new dong.Color([218, 83, 25, 0.9]));
  var style3 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 25, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([229, 251, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
  var style4 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 30, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 171, 72]), 1), new dong.Color([218, 83, 25, 0.9]));
  var style5 = new dong.SimpleMarkerSymbol(dong.SimpleMarkerSymbol.STYLE_CIRCLE, 35, new dong.SimpleLineSymbol(dong.SimpleLineSymbol.STYLE_SOLID, new dong.Color([251, 100, 72]), 1), new dong.Color([218, 83, 25, 0.9]));

    return style1;
}

/**
 * 企业点的事件
 */
function capitalclick(){
	
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
    app.mapList[0].infoWindow.hide();
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
//	     app.mapList[0].infoWindow.setTitle("");
//	     app.mapList[0].infoWindow.setContent(content);
//	     app.mapList[0].infoWindow.show(event.mapPoint);
    });
}


/*打开子措施页面*/
/**
 *
 * @param sectorsName   行业名称
 * @param measureame    措施名称
 * @param mid   措施id
 * @param planMeasureId     已经选中的措施ID，如果为null，证明是新建，否则为修改
 */
function open_csbjSon(sectorsName, measureame, mid, planMeasureId) {
    msg.content.sectorsName = sectorsName;
    msg.content.measureame = measureame;
    msg.content.mid = mid;
    msg.content.planMeasureId = planMeasureId;

    vipspa.setMessage(msg);

    var a = document.createElement('a');
    a.href = '#/csbj_zi';
    a.click();
}
//显示详细信息模态框
$("#detalMsg").window({
    width:800,
    collapsible:false,
    maximizable:false,
    minimizable:false,
    modal:true,
    shadow:false,
    title:'详细信息',
    border:false,
    closed:true,
    cls:"cloudui"
})
// 通过企业名称搜索信息
function doSearch(){
    var searchCon=$("#companyname").val();
    $("#detalMsgTable").datagrid({
        url:jianpaiUrl+'/search/companyList',
        method:'post',
        dataType: "json",
        columns:[[  //表头
            {field:"regionName",title:"地区",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}
            },
            {field:"companyname",title:"企业名称",width:160,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"industrytype",title:"所属行业",formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"smallIndex",title:"控制行业",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"equipId",title:"设备编号",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"measure",title:"控制措施",width:200,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;
        },
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:15,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [10, 15,20], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        toolbar: '#searchTool',
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.planId = qjMsg.planId;
            data.species = $("#hz_wrw").val();
            data.sector = wrwSelect;//污染物 手风琴选择
            data.companyname=(searchCon=""?null:searchCon)
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=(params.pageNumber-1)*params.pageSize  //初始化页面的页码
            return data;
        },
    })
}
//生成详细信息表格
function initConpamyTable(){
    $("#detalMsgTable").datagrid({
        url:jianpaiUrl+'/search/companyList',
        method:'post',
        dataType: "json",
        columns:[[  //表头
            {field:"regionName",title:"地区",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}
            },
            {field:"companyname",title:"企业名称",width:160,formatter: function (value) {
        return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"industrytype",title:"所属行业",formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"smallIndex",title:"控制行业",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"equipId",title:"设备编号",width:100,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"measure",title:"控制措施",width:200,formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;
        },
        selectOnCheck:false, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        singleSelect: true,//设置True 将禁止多选
        checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:15,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [10, 15,20], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        toolbar: '#searchTool',
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.planId = qjMsg.planId;
            data.species = $("#hz_wrw").val();
            data.sector = wrwSelect;//污染物 手风琴选择
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=(params.pageNumber-1)*params.pageSize  //初始化页面的页码
            return data;
        },
    })
}
//详细信息弹窗
function showDetail(){
    var downLoadUrl="<a href='"+jianpaiUrl+"/search/exportCompany?userId="+userId+"&planId="+qjMsg.planId+"'>下载</a>"
    document.getElementById("detailDownload").innerHTML=downLoadUrl;
    $("#detalMsg").window("open")
    window.setTimeout(initConpamyTable(),100)

}
//点击详细信息关闭按钮
$("#close_scene").click(function(){
    $("#detalMsg").window("close")
})


var colorArr = [
    [0, 0, 255,0.9], [0, 21, 255,0.9], [0, 43, 255,0.9], [0, 64, 255,0.9], [0, 85, 255,0.9], [0, 106, 255,0.9], [0, 128, 255,0.9], [0, 149, 255,0.9], [0, 170, 255,0.9], [0, 191, 255,0.9], [0, 213, 255,0.9], [0, 234, 255,0.9], [
        0, 255, 255,0.9], [
        12, 255, 255,0.9], [
        24, 255, 255,0.9], [
        35, 255, 255,0.9], [
        47, 255, 255,0.9], [
        59, 255, 255,0.9], [
        71, 255, 255,0.9], [
        82, 255, 255,0.9], [
        94, 255, 255,0.9], [
        106, 255, 255,0.9], [
        118, 255, 255,0.9], [
        129, 255, 255,0.9], [
        141, 255, 255,0.9], [
        153, 255, 255,0.9], [
        140, 255, 234,0.9], [
        128, 255, 213,0.9], [
        115, 255, 191,0.9], [
        102, 255, 170,0.9], [
        89, 255, 149,0.9], [
        77, 255, 128,0.9], [
        64, 255, 106,0.9], [
        51, 255, 85,0.9], [
        38, 255, 64,0.9], [
        26, 255, 43,0.9], [
        13, 255, 21,0.9], [
        0, 255, 0,0.9], [
        16, 255, 0,0.9], [
        31, 255, 0,0.9], [
        47, 255, 0,0.9], [
        63, 255, 0,0.9], [
        78, 255, 0,0.9], [
        94, 255, 0,0.9], [
        110, 255, 0,0.9], [
        126, 255, 0,0.9], [
        141, 255, 0,0.9], [
        157, 255, 0,0.9], [
        173, 255, 0,0.9], [
        188, 255, 0,0.9], [
        204, 255, 0,0.9], [
        207, 255, 0,0.9], [
        210, 255, 0,0.9], [
        214, 255, 0,0.9], [
        217, 255, 0,0.9], [
        220, 255, 0,0.9], [
        223, 255, 0,0.9], [
        226, 255, 0,0.9], [
        230, 255, 0,0.9], [
        233, 255, 0,0.9], [
        236, 255, 0,0.9], [
        239, 255, 0,0.9], [
        242, 255, 0,0.9], [
        245, 255, 0,0.9], [
        249, 255, 0,0.9], [
        252, 255, 0,0.9], [
        255, 255, 0,0.9], [
        255, 248, 0,0.9], [
        255, 240, 0,0.9], [
        255, 233, 0,0.9], [
        255, 225, 0,0.9], [
        255, 218, 0,0.9], [
        255, 210, 0,0.9], [
        255, 203, 0,0.9], [
        255, 195, 0,0.9], [
        255, 188, 0,0.9], [
        255, 180, 0,0.9], [
        255, 173, 0,0.9], [
        255, 165, 0,0.9], [
        255, 158, 0,0.9], [
        255, 150, 0,0.9], [
        255, 143, 0,0.9], [
        255, 135, 0,0.9], [
        255, 128, 0,0.9], [
        255, 120, 0,0.9], [
        255, 112, 0,0.9], [
        255, 104, 0,0.9], [
        255, 96, 0,0.9], [
        255, 88, 0,0.9], [
        255, 80, 0,0.9], [
        255, 72, 0,0.9], [
        255, 64, 0,0.9], [
        255, 56, 0,0.9], [
        255, 48, 0,0.9], [
        255, 40, 0,0.9], [
        255, 32, 0,0.9], [
        255, 24, 0,0.9], [
        255, 16, 0,0.9], [
        255, 8, 0,0.9], [
        255, 0, 0,0.9]];