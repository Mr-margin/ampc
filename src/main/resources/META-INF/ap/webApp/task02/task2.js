/**
 * Created by lvcheng on 2017/5/25.
 */
var qjMsg;
/*向路由保存信息对象结构*/
var msg = {
    'id': 'yaMessage',
    'content': {
        rwId: '',
        rwName: '',
        qjId: '',
        qjName: '',
        qjStartDate: '',
        qjEndDate: '',
        areaName: '',
        areaId: '',
        timeId: ''
    }
};
var jpztSetTimeOut;//减排计算之后查看计算的进度，做了一个轮训查看状态，有问题暂时放弃
var plancharts;//作为存储时间线绘制的echarts实例
var allData = [];//保存所有区域时段信息
var allData1 = null;  //临时存储区域、时段的临时变量
var cnArea = false;//判断是否超过区域的最大数量
var showCode = [{}, {}, {}];//保存所选的地区
var editTimeDateObj = {};//作为编辑时段时存储时间段的变量
var scenarino; //存储promise的变量
var selectCopyPlan; //当进入情景编辑的时候，可以选择复制预案，存储的变量
var areaIndex, timeIndex;//全局变量用于存储选中区域的序号和时段的序号
/*tree数配置*/
var zTreeSetting = {
    check: {
        enable: true,
//				autoCheckTrigger:true,
        chkboxType: {"Y": "ps", "N": "ps"}, //设置父子联动
        chkDisabledInherit: true //是否沿用disabled
    },
    data: {
        simpleData: {
            enable: true,     //简单数据模式
            idKey: "adcode",
            pIdKey: "padcode" //子父级关系对照
        },
        key: {
            name: 'name',
        }
    },
    callback: {
        onCheck: function (e, t, tr) {
            var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
            //selectNode(tr);
            if (tr.checked) {
//    	  setExtent(tr);
                if (tr.level == 0) {
                    level0(tr)
                } else {
                    level12(tr)
                }
            } else {
                if (tr.level == 0) {
                    delNode0(tr)
                } else {
                    delNode12(tr)
                }
            }
            //updataCodeList();
        }
    }
};
//在这里做初始化操作
(function () {
//        页面布局的渲染
    $('#task2content').layout();
//        选择情景的弹框渲染
    defaultwindowoption.title = '选择情景';
    $('#selectCreateQj').window(defaultwindowoption);
//        页面初始化
    initialize();
//        情景编辑的弹窗渲染
    $('#settingqjbox').window($.extend({}, defaultwindowoption, {
        title: '情景编辑'
    }));
//  时段编辑的标签页渲染
    $('#timeorplan').tabs({
        tabPosition: 'left',
        width: '100%',
        height: 400,
        pill:true,
        plain:true,
        border:false,
        onSelect: function (t, i) {
            if (((!areaIndex) && (areaIndex != 0))||(allData.length==0))return;
            if (t == '时段操作') {
                if (allData[areaIndex].timeItems.length <= 1) {
                    $('#timepanel').tabs('disableTab', '时段删除');
                    $('#timepanel').tabs('disableTab', '时段编辑');
                }
            } else if (t == '预案操作') {
                if (allData[areaIndex].timeItems[timeIndex].planId == -1) {
                    $('#planpanel').tabs('disableTab', '编辑现预案');
                    // $('#planpanel').tabs('disableTab', '删除现预案');
                    $('#planpanel').tabs('select', '添加新预案');
                } else {
                    $('#planpanel').tabs('disableTab', '添加新预案');
                    $('#planpanel').tabs('disableTab', '复制旧预案');
                    $('#planpanel').tabs('select', '编辑现预案');
                }
            }
        }
    });
//        时段编辑的窗口渲染
    $('#timePlan').window($.extend({}, defaultwindowoption, {
        title: '时段编辑',
        top: 10,
        onOpen: function () {
            areaIndex = selectedTimes.index;
            timeIndex = selectedTimes.indexNum;
            $('#timepanel').tabs({
                width: '100%',
                height: '100%',
                fit: true,
                plain:true, //样式的属性
                narrow:true  //样式的属性
            });
            $('#planpanel').tabs({
                width: '100%',
                height: '100%',
                fit: true,
                plain:true,
                narrow:true,
                onSelect:function (title,index) {
                    if(title=='复制旧预案'){
                        console.log(selectedTimes);
                        if (selectedTimes.planId == -1) {                           
                            setTimeout(copyPlan,500);
                        }
                    }
                }
            });
            //当当前区域只有一个时段的时候，时段不能删除和进行编辑
            if (allData[areaIndex].timeItems.length <= 1) {
                $('#timepanel').tabs('disableTab', '时段删除');
                $('#timepanel').tabs('disableTab', '时段编辑');
            }else {
                $('#timepanel').tabs('enableTab', '时段删除');
                $('#timepanel').tabs('enableTab', '时段编辑');
            }

            if (allData[areaIndex].timeItems[timeIndex].planId == -1) {
                $('#planpanel').tabs('disableTab', '编辑现预案');
                // $('#planpanel').tabs('disableTab', '删除现预案');
                $('#planpanel').tabs('enableTab', '复制旧预案').tabs('enableTab', '添加新预案');
                $('#planpanel').tabs('select', '添加新预案');

            } else {
                $('#planpanel').tabs('disableTab', '添加新预案');
                $('#planpanel').tabs('disableTab', '复制旧预案');
                $('#planpanel').tabs('enableTab', '编辑现预案');
                $('#planpanel').tabs('select', '编辑现预案');
            }
            
            $('#timeorplan').tabs('select','预案操作');
//                全局变量变量的存储

            var timeStart = moment(selectedTimes.startTime);
            var timeEnd = moment(selectedTimes.endTime);
            /*最小间隔一小时*/
            var timeEnd1 = moment(selectedTimes.endTime).add(-1, 'h');
            msg.content.areaId = allData[areaIndex].areaId;
            msg.content.areaName = allData[areaIndex].areaName;
            msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
            msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
            msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
            msg.content.cityCodes = allData[areaIndex].cityCodes;
            msg.content.countyCodes = allData[areaIndex].countyCodes;
            msg.content.provinceCodes = allData[areaIndex].provinceCodes;

            /*添加时段 start*/
            initDate(timeStart.add(2, 'h'), timeEnd1);
            editHtml('addTime1');

            /*删除时段 start*/
            editHtml('delTime');
            
            if (allData[areaIndex].timeItems.length > 1) {
                $('.delTimeLi').removeClass('disNone');
                $('#timepanel').tabs('enableTab', '时段删除');
                $('.delTimeDiv').find('.delSelect').empty();


                var redio = $('.radio').clone().show();
                if (timeIndex == 0) {
                    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
                    redio.find('input').val('down');
                } else if (timeIndex == (allData[areaIndex].timeItems.length - 1)) {
                    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
                    redio.find('input').val('up');
                } else {
                    var redio2 = $('.radio').clone().show();
                    redio2.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
                    redio2.find('input').val('up');
                    redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
                    redio.find('input').val('down');
                    $('.delTimeDiv').find('.delSelect').append(redio2);
                }
                console.log(redio);
                $('.delTimeDiv').find('.delSelect').append(redio);
                redio.find('input').attr('checked', 'checked');


            }
            /* else {
             // $('.delTimeLi').addClass('disNone');
             $('.delTimeLi').removeClass('active');
             $('.delTimeDiv').removeClass('active');
             $('#timepanel').tabs('disableTab','时段删除');
             }*/
            /*删除时段 end*/

            /**************************************************************************************/
            /*编辑时段 start*/
            if (allData[areaIndex].timeItems.length > 1) {
                // $('.editTimeLi').removeClass('disNone');

                clearTimeDate();
                updatetimeSow();
                editTimeDateObj.type = $('#selectEditPoint').val();
            } else {
                // $('.editTimeLi').addClass('disNone');
            }

            /*编辑时段 end*/

            /**************************************************************************************/
            /*添加预案 start*/
            // $('.delPlanLi').addClass('disNone');//暂无删除预案接口支持

            /*添加预案 end*/
        }
    }));
//        新增区域的窗口渲染，新增区域和编辑区域是一个window弹框
    $('#editArea').window($.extend({}, defaultwindowoption, {
        title: '定义区域范围',
        width: 900,
        onOpen: function () {
            $('#settingqjbox').window('close');
            /*初始化tree树相关信息*/
            $.fn.zTree.init($("#adcodeTree"), zTreeSetting, zTreeData);//zTreeData是从home页面传过来的数据
            var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
            var nodes = treeObj.getNodesByParam("level", 1);//获取地市一级的节点
            var code = qjMsg.esCodeRange; //获取当前
//                对应的任务范围中市级下可显示县一级
            for (var i = 0; i < nodes.length; i++) {
                var adcode = nodes[i].adcode;
                if ((code.indexOf(adcode.substr(0, 2)) == -1) && (code.indexOf(adcode.substr(0, 4)) == -1)) {
//                        在这里将非当前任务范围中的县级给隐藏起来
                    treeObj.hideNodes(nodes[i].children);
                }
            }

//                var button = $(event.relatedTarget);
//                areaIndex = $('.areaTitle_con').index($(button).parents('.areaTitle_con'));
            //var create = button.data('new');
            var create = cnArea;
            var areaId, findUrl;
            var allUrl = '/area/find_areaAll';
            $('#adcodeListBox').empty();
            if (create) {
                $('#areaName').val('').removeAttr('data-id');
                showCode = [{}, {}, {}];
//                    $('.adcodeList.mt20').empty();
                    app.gLyr.clear();
            } else {
                findUrl = '/area/get_areaList';
                areaId = $('#settingqjbox .step1>button').attr('data-qjid');//获取当前区域id
                ajaxPost(findUrl, {
                    areaId: areaId,
                    userId: userId
                }).success(function (res) {
                    if (res.data) {
                        /*设置显示已选code*/
                        setShowCode(res.data);
                            addLayer(showCode);
                    }
                    updataCodeList();
                });
                $('#areaName').attr('data-id', areaId);
                $.each(allData, function (i, n) {
                    if (n.areaId == areaId) {
                        $('#areaName').val(n.areaName);
                    }
                });
            }
            cnArea = false;
            // $('#editArea').window('close');
            ajaxPost(allUrl, {
                scenarinoId: qjMsg.qjId,
                userId: userId,
                areaId: areaId
            }).success(function (res) {
                setDisabled(res.data);
            });
        }
    }));
//    状态查看的窗口渲染
    $('#jpzt').window($.extend({},defaultwindowoption,{
        title:'减排计算状态',
        onOpen:function () {
            window.clearTimeout(jpztSetTimeOut);
            jpztckBtn(3000);
        },
        onClose:function(){
            window.clearTimeout(jpztSetTimeOut);
            jpztckBtn(60000);
        }
    }));

    // window.setTimeout(function () {
    //     $('#timepanel').tabs({
    //         width:'100%',
    //         height:'100%',
    //         fit: true
    //     });
    //     $('#planpanel').tabs({
    //         width:'100%',
    //         height:'100%',
    //         fit: true
    //     });
    // },100);
})()
/*该页面初始化函数*/
function initialize() {

    /**
     * 设置导航条菜单
     */
    $("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景编辑</a>');
    previous();

    var ls = window.sessionStorage;
    qjMsg = vipspa.getMessage('qjMessage').content;
    if (!qjMsg) {
        qjMsg = JSON.parse(ls.getItem('qjMsg'));
        
    } else {
        ls.setItem('qjMsg', JSON.stringify(qjMsg));
        scenarinoType(qjMsg.scenarinoStatus)
    }
    ajaxPost_async_false('/scenarino/find_Scenarino_status', {
        userId: userId,
        scenarinoId: qjMsg.qjId
    }).success(function (res) {
        qjMsg.scenarinoStatus = res.data.scenarinoStatus;
        scenarinoType(qjMsg.scenarinoStatus);
    })
    $('.qyCon').removeClass('disNone');
    $('.qyCon .nowRw span').html(qjMsg.rwName);
    $('.qyCon .nowQj span').html(qjMsg.qjName);
    $('.qyCon .seDate span').html(moment(qjMsg.qjStartDate).format('YYYY-MM-DD') + '至' + moment(qjMsg.qjEndDate).format('YYYY-MM-DD'));

    msg.content.rwId = qjMsg.rwId;
    msg.content.rwName = qjMsg.rwName;
    msg.content.qjId = qjMsg.qjId;
    msg.content.qjName = qjMsg.qjName;
    msg.content.qjStartDate = qjMsg.qjStartDate;
    msg.content.qjEndDate = qjMsg.qjEndDate;
    msg.content.esCouplingId = qjMsg.esCouplingId;
    msg.content.esCouplingName = qjMsg.esCouplingName;

    $('.footerShow .rw span').html(qjMsg.rwName);
    $('.footerShow .qj span').html(qjMsg.qjName);

    var url = '/area/get_areaAndTimeList';
    var scenarino = ajaxPost(url, {
        scenarinoId: qjMsg.qjId,
        userId: userId
    });
    //initDate();

    scenarino.then(function (res) {

        //            allData1 = res.data.slice(0, -1);

        allData1 = res.data.slice(0, -1);
        if (qjMsg.scenarinoStatus == 2) {
            for (var i = 0; i < allData1.length; i++) {
                for (var m = 0; m < allData1[i].timeItems.length; m++) {
                    if (allData1[i].timeItems[m].planId != -1) {
                        $('.jpjs').removeAttr('disabled');
                        break;
                    }
                }
            }
        }
        if (res.data[res.data.length - 1].isnew) {
            $('#selectCreateQj').window('open');
        } else {
            selectCopy(false);
        }

    });

}
/**
 * 选择复制情景时操作
 * @param t
 */
function selectCopy(t) {
    if (t) {

        $('#selectCreateQj .selectCQJbtn').hide();
        $('#selectCreateQj .selectCopyQj').show();
        $('#selectCreateQj .modal-footer').show();
        initCoptTable();
    } else {
        allData = allData1;
        allData1 = null;
        for (var i = 0; i < allData.length; i++) {
            allData[i].timeFrame = [];
            var timeItems = allData[i].timeItems;
            var tLength = timeItems.length;
            //$('.areaMsg').append(area);
            for (var item = 0; item < tLength; item++) {

                if (item > 0) {
                    var sD = timeItems[item].timeStartDate;
                    allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
                }
            }

        }
        showTimeline(allData);
        $('#selectCreateQj').window('close');
        app2();
    }

}
var selectCopyQJ, statusRW = '';
/*初始化复制情景table*/
function initCoptTable() {
    $('#copyQJ').datagrid($.extend({}, defaultdatagridoption, {
        url: '/ampc/scenarino/get_CopyScenarinoList',
        dataType: "json",
        singleSelect: true,
        contentType: "application/json", // 请求远程数据的内容类型。
        queryParams: function (m) {
            var json = {
                "token": "",
                "data": {
                    "scenarinoId": qjMsg.qjId,
                    "queryName": m.searchText || '',
                    "missionStatus": statusRW,
                    "pageNum": m.pageNumber,
                    "pageSize": m.pageSize,
                    "sort": '',
                    "userId": 1
                }
            };
            return json;
        },
        loadFilter: function (res) {
            return res.data.rows
        },
        columns: [[{
            checkbox: true,
            field: 'cb'
        }, {
            field: 'missionName',
            title: '任务名称',
            width: 150
        }, {
            field: 'missionStatus',
            title: '任务类型',
            width: 80,
            formatter: function (v) {
                return v == 1 ? '实时预报' : v == 2 ? '预评估' : '后评估'
            }
        }, {
            field: 'scenarinoName',
            title: '情景名称',
            width: 120
        }]]

    }))
    /*        $('#copyQJ').bootstrapTable({
     method: 'POST',
     url: '/ampc/scenarino/get_CopyScenarinoList',
     dataType: "json",
     contentType: "application/json", // 请求远程数据的内容类型。
     toobar: '#rwToolbar',
     iconSize: "outline",
     search: false,
     searchAlign: 'right',
     height: 453,
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
     return {classes: 'info'}
     }
     return {};
     },
     queryParams: function (m) {
     var json = {
     "token": "",
     "data": {
     "scenarinoId": qjMsg.qjId,
     "queryName": m.searchText || '',
     "missionStatus": statusRW,
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
     $('.info').removeClass('info');
     $($element).addClass('info');
     selectCopyQJ = row;
     },
     onLoadSuccess: function (data) {
     selectCopyQJ = data.rows[0];
     }
     });*/
}
/*选择创建情景时模态框上一步操作*/
function previous() {
    $('#selectCreateQj .step1').show();
    $('#selectCreateQj .step2').hide();
    //    $('#selectCreateQj').window('open');
}
/**
 * 情景计算状态
 * 功能栏中的按钮
 * @param typeNum
 */
function scenarinoType(typeNum) {
    $('.toolShow').removeAttr('disabled');
    $('.addNewArea').removeAttr('disabled');
    switch (typeNum) {
        case 1:
            $('.jpjs').attr('disabled', true);
            $('.jpjs').removeClass('disNone');
            $('.jpztck').addClass('disNone');
            $('.jpfx').attr('disabled', true);
            break;
        case 2:
            $('.jpjs').attr('disabled', true);
            $('.jpjs').removeClass('disNone');
            $('.jpztck').addClass('disNone');
            $('.jpfx').attr('disabled', true);
            break;
        case 3:
            $('.toolShow').attr('disabled', true);
            $('.addNewArea').attr('disabled', true);
        case 4:
            $('.jpjs').addClass('disNone');
            $('.jpztck').removeClass('disNone');
            $('.jpfx').attr('disabled', true);
            break;
        case 5:
            $('.jpjs').removeAttr('disabled');
            $('.jpjs').removeClass('disNone');
            $('.jpztck').addClass('disNone');
            $('.jpfx').removeAttr('disabled');
            break;
        case 6:
        case 7:
        case 8:
            $('.jpjs').attr('disabled', true);
            $('.jpjs').removeClass('disNone');
            $('.jpztck').addClass('disNone');
            $('.jpfx').removeAttr('disabled');
            break;
    }
}
function showTimeline(data) {
    var _temp = {};
    var _tempdate;//临时时间作为循环判断用
    _temp.yAxisLabel = [];//存储y轴标签
    _temp.seriesData = [];//用于存储绘制点的坐标
    _temp.seriesLabel = [];//存储数据的标签
    _temp.seriesName = [];//存储情景的id
    var _temparr = [];
    var _temparr1 = [];
    for (var i = 0; i < data.length; i++) {
        /*        var obj = {
         id:data[i].areaId.toString(),
         index:i
         };*/
        _temp.yAxisLabel.push(data[i].areaName + '1', data[i].areaId.toString(), data[i].areaName + '2');
        // _temp.yAxisLabel.push(data[i].areaName + '1', JSON.stringify(obj), data[i].areaName + '2');
        _temp.seriesData.push([]);
        for (var j = 0; j < data[i].timeItems.length; j++) {
            _temparr = [];
            _temparr1 = [];
            _tempdate = moment(data[i].timeItems[j].timeStartDate);
            _temp.seriesLabel.push([echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', _tempdate.valueOf()), data[i].areaId.toString(), data[i].timeItems[j].planName]);
            _temp.seriesName.push(data[i].timeItems[j].planId);
            _temparr1.unshift([echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', _tempdate.valueOf()), data[i].areaName + '1']);
            do {
                _temparr.push([echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', _tempdate.valueOf()), data[i].areaName + '1']);
                _temparr1.unshift([echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', _tempdate.valueOf()), data[i].areaName + '2']);
                _tempdate.add(1, 'hours')
            } while (!_tempdate.isAfter(moment(data[i].timeItems[j].timeEndDate).add(1, 'seconds')))
            _temp.seriesData[_temp.seriesData.length - 1].push(_temparr.concat(_temparr1));
        }
    }
    console.log(_temp.seriesLabel);
    plancharts = echarts.init(document.getElementById('plancharts'));
    var _option = {
//            dataZoom: [{
//                type: 'inside',
//                throttle: 50
//            }],
        xAxis: {
            position: 'top',
            type: 'time',
            min: echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', qjMsg.qjStartDate),
            max: echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', moment(qjMsg.qjEndDate).add(1, 'seconds').valueOf())
        },
        yAxis: {
            type: 'category',
            data: _temp.yAxisLabel,
            inverse:true,
            axisLabel: {
                interval: function (index, value) {
                    if (index % 3 == 1) {
                        return true
                    }
                    return false;
                },
                formatter: function (value, index) {
                    if (index % 3 == 1) {
                        var obj = JSON.parse(value);
                        for (var i = 0; i < allData.length; i++) {
                            if (value == allData[i].areaId) {
                                // if (obj.id == allData[i].areaId) {
                                return allData[i].areaName;
                                // }

                            }
                        }
                        return value;
                    }
                }
            },
            axisTick: {
                show: false
            },
            triggerEvent: true
        },
        series: (function () {
            var _arr = [];
            for (var i = 0; i < _temp.seriesData.length; i++) {
                for (var j = 0; j < _temp.seriesData[i].length; j++) {
                    _arr.push({
                        type: 'line',
                        lineStyle: {
                            normal: {
                                color: '#2F4554'
                            }
                        },
                        silent: true,
                        //                            label: {
                        //                                normal: {
                        //                                    show: true,
                        //                                    formatter: 'hahhah'
                        //                                }
                        //                            },
                        areaStyle: {
                            normal: {
                                color: allData[i].timeItems[j].planId==-1?'#5BC0DE':'#337AB7'
                            }
                        },
                        showSymbol: false,
                        data: _temp.seriesData[i][j]
                    });
                }
            }
            for (var i = 0; i < _temp.seriesLabel.length; i++) {
                _arr.push({
                    type: 'scatter',
                    symbolSize: 8,
                    label: {
                        normal: {
                            show: true,
                            formatter: _temp.seriesLabel[i][2],
                            position: 'right',
                            textStyle: {
                                color: '#fff',
                                fontWeight: 'bold',
                                fontSize: 16
                            }
                        }
                    },
                    name: _temp.seriesName[i],
                    silent: false,
                    data: [[_temp.seriesLabel[i][0], _temp.seriesLabel[i][1]]]
                })
            }
            return _arr
        })(),
        useUTC: false,
        animation:false,
        animationDuration:0,
        progressive:0
    };
    plancharts.setOption(_option);
    plancharts.on('click', function (params) {
        /*            if (isMouseDrag = 1) {
         isMouseDrag = 3;
         }
         if (isMouseDrag = 2) {
         isMouseDrag = 3;
         }*/
        if (params.componentType == 'yAxis') {
            console.log(params);
            // areaIndex = obj.index;
            for(var i=0;i<allData.length;i++){
                if(params.value==allData[i].areaId.toString()){
                    areaIndex=i;
                    break
                }
            }
            $('#settingqjbox').window('open').window('center').find('.step1 button').attr('data-qjid', allData[i].areaId);
        }
        if (params.seriesType == 'scatter') {
            if (params.seriesName == -1) {
                return
            } else {
                out:
                    for (var i = 0; i < allData.length; i++) {
                        console.log(allData);
                        for (var j = 0; j < allData[i].timeItems.length; j++) {
                            if (allData[i].timeItems[j].planId == params.seriesName) {
                                break out;
                            }
                        }
                    }
                selectedTimes = {
                    areaId: allData[i].areaId,
                    areaName: allData[i].areaName,
                    cityCodes: allData[i].cityCodes,
                    countyCodes: allData[i].countyCodes,
                    endTime: allData[i].timeItems[j].timeEndDate,
                    index: i,
                    indexNum: j,
                    planId: allData[i].timeItems[j].planId,
                    planName: allData[i].timeItems[j].planName,
                    provinceCodes: allData[i].provinceCodes,
                    startTime: allData[i].timeItems[j].timeStartDate,
                    timeId: allData[i].timeItems[j].timeId
                };
                console.log(selectedTimes);
                areaIndex = selectedTimes.index;
                timeIndex = selectedTimes.indexNum;

                msg.content.areaId = allData[i].areaId;
                msg.content.areaName = allData[i].areaName;
                msg.content.timeId = allData[i].timeItems[j].timeId;
                //msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
                msg.content.timeEndDate = selectedTimes.endTime
                //msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
                msg.content.timeStartDate = selectedTimes.startTime;
                msg.content.planId = allData[i].timeItems[j].planId;
                msg.content.planName = allData[i].timeItems[timeIndex].planName;
                msg.content.cityCodes = allData[i].cityCodes;
                msg.content.countyCodes = allData[i].countyCodes;
                msg.content.provinceCodes = allData[i].provinceCodes;

                vipspa.setMessage(msg);
                location.hash = '#/csbj';
            }
            ;
        }
//            _onmouseover();
        console.log(params);
    });
    var zr = plancharts.getZr();
    zr.on('click', function (params) {
        console.log(params);
        if (typeof params.target !== 'undefined') {
            return
        }
        var pointInPixel = [params.offsetX, params.offsetY];
        var pointInGrid = plancharts.convertFromPixel('grid', pointInPixel);
        var labelIndex = Math.floor(pointInGrid[1] / 3);
        if (allData[labelIndex].cityCodes.length == 0 && allData[labelIndex].countyCodes.length == 0 && allData[labelIndex].provinceCodes.length == 0) {
            areaIndex=labelIndex;
            $('#settingqjbox').window('open').window('center').find('.step1 button').attr('data-qjid', allData[labelIndex].areaId);
            return
        }
        if (qjMsg.scenarinoStatus == 3)return;
        if (qjMsg.scenarinoStatus == 4)return;
        if (qjMsg.scenarinoStatus == 6)return;
        if (qjMsg.scenarinoStatus == 7)return;
        if (qjMsg.scenarinoStatus == 8)return;
        if (qjMsg.scenarinoStatus == 9)return;
        for (var i = 0; i < allData[labelIndex].timeItems.length; i++) {
//                if(Math.floor(pointInGrid[1]/3))
            console.log((i-1<0?allData[labelIndex].timeItems[i].timeEndDate:allData[labelIndex].timeItems[i-1].timeStartDate) < pointInGrid[0] && allData[labelIndex].timeItems[i].timeEndDate > pointInGrid[0]);
            if (allData[labelIndex].timeItems[i].timeStartDate < pointInGrid[0]  && allData[labelIndex].timeItems[i].timeEndDate > pointInGrid[0]) {
                selectedTimes = {
                    areaId: allData[labelIndex].areaId,
                    areaName: allData[labelIndex].areaName,
                    cityCodes: allData[labelIndex].cityCodes,
                    countyCodes: allData[labelIndex].countyCodes,
                    endTime: allData[labelIndex].timeItems[i].timeEndDate,
                    index: labelIndex,
                    indexNum: i,
                    planId: allData[labelIndex].timeItems[i].planId,
                    planName: allData[labelIndex].timeItems[i].planName,
                    provinceCodes: allData[labelIndex].provinceCodes,
                    startTime: allData[labelIndex].timeItems[i].timeStartDate,
                    timeId: allData[labelIndex].timeItems[i].timeId
                };
                ontTimes({
                    areaId: allData[labelIndex].areaId,
                    areaName: allData[labelIndex].areaName,
                    cityCodes: allData[labelIndex].cityCodes,
                    countyCodes: allData[labelIndex].countyCodes,
                    endTime: allData[labelIndex].timeItems[i].timeEndDate,
                    index: labelIndex,
                    indexNum: i,
                    planId: allData[labelIndex].timeItems[i].planId,
                    planName: allData[labelIndex].timeItems[i].planName,
                    provinceCodes: allData[labelIndex].provinceCodes,
                    startTime: allData[labelIndex].timeItems[i].timeStartDate,
                    timeId: allData[labelIndex].timeItems[i].timeId
                })
            }
        }
    });
    zr.on('mousemove',function (params) {
        var pointInPixel = [params.offsetX, params.offsetY];
        var pointInGrid = plancharts.convertFromPixel('grid', pointInPixel);
        var labelIndex = Math.floor(pointInGrid[1] / 3);
        console.log(pointInGrid[0]>qjMsg.qjEndDate||pointInGrid[0]<qjMsg.qjStartDate||pointInGrid[1]<0||pointInGrid[1]>allData.length*3-1);
        if(pointInGrid[0]>qjMsg.qjEndDate||pointInGrid[0]<qjMsg.qjStartDate||pointInGrid[1]<0||pointInGrid[1]>allData.length*3-1){
        	var _series=plancharts.getOption().series;
        	var _line=[];
        	for(var i=0;i<allData.length;i++){
        		for(var j=0;j<allData[i].timeItems.length;j++){
        			_line.push(allData[i].timeItems[j]);
        		}
        	}
        	for(var i=0;i<_line.length;i++){
        		if(_line[i].planId==-1){
        			_series[i].areaStyle.normal.color='#5bc0de';
        		}else{
        			_series[i].areaStyle.normal.color='#337ab7';
        		}
        	}
        	plancharts.setOption({series:_series});
        	return
        }else{
        	$('#plancharts>div').css('cursor','pointer');
        	var _series=plancharts.getOption().series;
        	var _line=[];
        	var _temp=0;
        	for(var i=0;i<allData.length;i++){
        		for(var j=0;j<allData[i].timeItems.length;j++){
        			_line.push(allData[i].timeItems[j]);
        			if(plancharts.getOption().yAxis[0].data[labelIndex*3+1]==allData[i].areaId&&pointInGrid[0]>allData[i].timeItems[j].timeStartDate&&pointInGrid[0]<allData[i].timeItems[j].timeEndDate){
        				_temp=_line.length-1;
        			}
        		}
        	}
        	for(var i=0;i<_line.length;i++){
        		if(_line[i].planId==-1){
        			_series[i].areaStyle.normal.color='#5bc0de';
        		}else{
        			_series[i].areaStyle.normal.color='#337ab7';
        		}
        		if(i==_temp){
        			_series[i].areaStyle.normal.color='#626C91';
        		}
        	}
        	plancharts.setOption({series:_series});
        }

    });
    /*console.log(data);
     var _temp = {};
     _temp.yAxisLabel = [];//存储y轴标签
     _temp.seriesData = [];//用于存储绘制点的坐标
     _templength=0;//存储共有类目组的长度最大是多少
     for (var i = 0; i < data.length; i++) {
     for (var j = 0; j < data[i].timeItems.length; j++) {
     if(j>_templength){
     _templength=j;
     }
     }
     }
     for(var i=0;i<=_templength;i++){
     _temp.seriesData.push([]);
     }
     for (var i = 0; i < data.length; i++) {
     _temp.yAxisLabel.push(data[i].areaName);
     for (var j = 0; j < data[i].timeItems.length; j++) {
     //                _temp.seriesData.push([data[i].timeItems[j].timeEndDate,data[i].areaName]);
     //                _temp.seriesData.push([0,data[i].areaName]);
     //                _temp.seriesData.push([echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', data[i].timeItems[j].timeEndDate),data[i].areaName]);
     //                _temp.seriesData.push([data[i].timeItems[j].timeEndDate,i]);
     //                _temp.seriesData.push([data[i].areaName,data[i].timeItems[j].timeEndDate]);
     }
     }
     plancharts = echarts.init(document.getElementById('plancharts'));
     var _option = {
     xAxis: {
     position: 'top',
     type: 'time',
     min: echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', qjMsg.qjStartDate),
     max: echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', moment(qjMsg.qjEndDate).add(1, 'seconds').valueOf())
     },
     tooltip: {
     trigger: 'item'
     },
     yAxis: {
     type: 'category',
     data: _temp.yAxisLabel
     },
     series: (function () {
     var _arr = [];
     for (var i = 0; i < _temp.seriesData.length; i++) {
     _arr.push({
     type: 'bar',
     //                        stack: _temp.seriesData[i][1],
     stack: 1,
     //                        lineStyle: {
     //                            normal: {
     //                                color: '#2F4554'
     //                            }
     //                        },
     //                        silent: true,
     //                            label: {
     //                                normal: {
     //                                    show: true,
     //                                    formatter: 'hahhah'
     //                                }
     //                            },
     areaStyle: {
     normal: {
     color: '#2F4554'
     }
     },
     tooltip: {
     formatter: 'hahah'
     },
     //                            showSymbol: false,
     data:[{
     //                            name:_temp.yAxisLabel[i],
     value:_temp.seriesData[i]
     }]
     });
     }
     return _arr
     })(),
     useUTC: false
     };
     plancharts.setOption(_option);
     plancharts.on('click', function (param) {
     console.log(param);
     })*/
}
/*删除区域*/
function delArea(e) {
    console.log(e);
    for (var i = 0; i < allData.length; i++) {
        if ($(e).attr('data-qjid') == allData[i].areaId) {
            var areaIndex = i;
            console.log(i);
            break;
        }
    }
//        var areaIndex = $('.areaTitle_con').index($(e).parents('.areaTitle_con'));
    var url = '/area/delete_area';
    //var areaIds = [allData[areaIndex].areaId.toString()];
    var params = {
        userId: userId,
        //areaIds: allData[areaIndex].areaId.toString()
        areaIds: $(e).attr('data-qjid'),
        scenarinoStatus: qjMsg.scenarinoStatus,
        scenarinoId: qjMsg.qjId
    };
    swal({
            title: "确定要删除区域-" + allData[areaIndex].areaName,
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "删除",
            cancelButtonText: "取消",
            closeOnConfirm: false
        },
        function () {
            console.log(params);
            ajaxPost(url, params).success(function (res) {
                $('#settingqjbox').window('close');
                if (res.status == 0) {
                    allData.splice(areaIndex, 1);
                    $('.areaTitle_con').eq(areaIndex).remove();
                    showTimeline(allData);

                    for (var i = 0; i < allData.length; i++) {
                        for (var ii = 0; ii < allData[i].timeItems.length; ii++) {
                            if (allData[i].timeItems[ii].planId == -1) {//对左侧菜单栏中按钮操作
                                $('.jpjs').removeClass('disNone');
                                $('.jpjs').attr('disabled', true);
                                $('.jpztck').addClass('disNone');
                            } else {
                                $('.jpjs').removeAttr('disNone');
                                $('.jpjs').removeClass('disNone');
                                $('.jpztck').addClass('disNone');
                                break;
                            }
                        }
                    }

                    //$('.areaTitle_con').eq(areaIndex).remove();
                    swal({
                        title: '已删除!',
                        type: 'success',
                        timer: 1000,
                        showConfirmButton: false
                    });
                } else {
                    swal({
                        title: '删除失败!',
                        type: 'error',
                        timer: 1000,
                        showConfirmButton: false
                    });
                }
            }).error(function () {
                $('#settingqjbox').window('close');
                swal({
                    title: '删除失败!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            })

        });

}

/*编辑预案*/
function editPlan(t) {
    if (!t) {
        t = selectedTimes;
    } else {
        selectedTimes = t;
    }
    areaIndex = t.index;
    timeIndex = t.indexNum;

    msg.content.areaId = allData[areaIndex].areaId;
    msg.content.areaName = allData[areaIndex].areaName;
    msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
    //msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
    msg.content.timeEndDate = t.endTime;
    //msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
    msg.content.timeStartDate = t.startTime;
    msg.content.planId = allData[areaIndex].timeItems[timeIndex].planId;
    msg.content.planName = allData[areaIndex].timeItems[timeIndex].planName;
    msg.content.cityCodes = allData[areaIndex].cityCodes;
    msg.content.countyCodes = allData[areaIndex].countyCodes;
    msg.content.provinceCodes = allData[areaIndex].provinceCodes;

    vipspa.setMessage(msg);

    if (msg.content.planId == -1) {
        $('#timePlan').window('open').window('center');
        $('a[href="#plan"]').eq(0).click();

        return
    }
    ;

    createNewPlan();
}

/*创建新预案*/
function createNewPlan(e) {
    $('#timePlan').window('close');
    window.setTimeout(function () {
        location.hash = '#/csbj';
    }, 500)
}

/*当前选中的时段*/
var selectedTimes;

function ontTimes(data) {
    selectedTimes = data;
    console.log(selectedTimes);
    //if (data.planId != -1) {
    //  $('.yacz').attr('disabled', true);
    //} else {
    //  $('.yacz').removeAttr('disabled');
    //}

    if (data.planId != -1) {
        $('.addNewPlanBtn').attr('disabled', true);
        $('.addCopyPlanBtn').attr('disabled', true);
        $('.editPlanBtn').removeAttr('disabled');

    } else {
        $('.addNewPlanBtn').removeAttr('disabled');
        $('.addCopyPlanBtn').removeAttr('disabled');
        $('.editPlanBtn').attr('disabled', true);
    }


    //$('#editTime').modal('show')
    // $('#timePlan').window('open').window('center');
    $('#timePlan').window('open').window('center');
}

//通用属性
var stat = {};
//中心点坐标
stat.cPointx = 106;
stat.cPointy = 35;
var app = {};
var dong = {};

//颜色数组
var sz_corlor = [[0, 114, 190, 0.65], [218, 83, 25, 0.65], [238, 178, 51, 0.65], [126, 47, 142, 0.65], [119, 173, 48, 0.65], [77, 191, 239, 0.65], [163, 20, 47, 0.65], [64, 90, 90, 0.65], [78, 0, 205, 0.65], [1, 123, 26, 0.65]];
var dojoConfig = {
    async: true,
    parseOnLoad: true,
    packages: [{
        name: 'tdlib',
        location: "/js/tdlib"
    }]
};

require(
    [
        "esri/map",
        "esri/layers/FeatureLayer",
        "esri/layers/GraphicsLayer",
        "esri/symbols/SimpleFillSymbol",
        "esri/symbols/SimpleLineSymbol",
        "esri/symbols/SimpleMarkerSymbol",
        "esri/renderers/ClassBreaksRenderer",
        "esri/geometry/Point",
        "esri/geometry/Extent",
        "esri/renderers/SimpleRenderer",
        "esri/graphic",
        "dojo/_base/Color",
        "dojo/dom-style",
        "esri/tasks/FeatureSet",
        "esri/SpatialReference",
        "tdlib/gaodeLayer",
        "esri/InfoTemplate",
        "esri/renderers/UniqueValueRenderer",
        "esri/tasks/query",
        "esri/geometry/Extent",
        "dojo/domReady!"

    ],
    function (Map, FeatureLayer, GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, ClassBreaksRenderer, Point, Extent, SimpleRenderer,
              Graphic, Color, style, FeatureSet, SpatialReference, gaodeLayer, InfoTemplate, UniqueValueRenderer, query, Extent) {
        dong.gaodeLayer = gaodeLayer;
        dong.Graphic = Graphic;
        dong.Point = Point;
        dong.GraphicsLayer = GraphicsLayer;
        dong.SpatialReference = SpatialReference;
        dong.SimpleLineSymbol = SimpleLineSymbol;
        dong.FeatureLayer = FeatureLayer;
        dong.SimpleRenderer = SimpleRenderer;
        dong.SimpleFillSymbol = SimpleFillSymbol;
        dong.Color = Color;
        dong.ClassBreaksRenderer = ClassBreaksRenderer;
        dong.UniqueValueRenderer = UniqueValueRenderer;
        dong.InfoTemplate = InfoTemplate;
        dong.Query = query;
        dong.Extent = Extent;

        app.outline = new dong.SimpleLineSymbol("solid", new dong.Color("#444"), 1);
        app.symbol = new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([221, 160, 221, 0.65]));
        app.featureLayer1 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/2", {//添加省的图层
            mode: dong.FeatureLayer.MODE_ONDEMAND
        });
        app.featureLayer2 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/1", {//市的图层
            mode: dong.FeatureLayer.MODE_ONDEMAND
        });
        app.featureLayer3 = new dong.FeatureLayer(ArcGisServerUrl + "/arcgis/rest/services/ampc/cms/MapServer/0", {//区县的图层
            mode: dong.FeatureLayer.MODE_ONDEMAND
        });

        /*********************本底外面的**************************/
        app.map1 = new Map("mapDiv1", {
            logo: false,
            center: [stat.cPointx, stat.cPointy],
            minZoom: 3,
            maxZoom: 13,
            zoom: 3
        });
        app.baselayerList1 = new dong.gaodeLayer();
        app.stlayerList1 = new dong.gaodeLayer({layertype: "st1"});
        app.labellayerList1 = new dong.gaodeLayer({layertype: "label1"});
        app.map1.addLayer(app.baselayerList1);//添加高德地图到map容器
        app.map1.addLayers([app.baselayerList1]);//添加高德地图到map容器
        app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
        app.map1.addLayer(app.gLyr1);
        app.map1.on("loaded", app2())

        /**********************************模态窗口地图部分*****************************************************/
        app.map = new Map("mapDiv", {
            logo: false,
            center: [stat.cPointx, stat.cPointy],
            minZoom: 3,
            maxZoom: 13,
            zoom: 3
        });
        app.baselayerList = new dong.gaodeLayer();
        app.stlayerList = new dong.gaodeLayer({layertype: "st"});
        app.labellayerList = new dong.gaodeLayer({layertype: "label"});
        app.map.addLayer(app.baselayerList);//添加高德地图到map容器
        app.map.addLayers([app.baselayerList]);//添加高德地图到map容器
        app.gLyr = new dong.GraphicsLayer({"id": "gLyr"});
        app.map.addLayer(app.gLyr);
    });
var extent_n;
var tthg_n;

/**
 * 模态窗行政区划渲染
 */
function addLayer(data) {
    app.gLyr.clear();
    extent_n = new dong.Extent();
    tthg_n = true;
    var query = new dong.Query();
    for (var i = 0; i < 3; i++) {
        var t1 = "";
        if (i == 0) {
            $.each(data[i], function (k, vol) {
                t1 += "'" + k + "',";
            });
        } else {
            $.each(data[i], function (k, vol) {
                $.each(vol, function (m, col) {
                    t1 += "'" + m + "',";
                });
            });
        }
        if (t1 != "") {
            query.where = "ADMINCODE IN (" + t1.substring(0, t1.length - 1) + ")";
            if (i == "0") {
                app.featureLayer1.queryFeatures(query, modal_Result);
            } else if (i == "1") {
                app.featureLayer2.queryFeatures(query, modal_Result);
            } else if (i == "2") {
                app.featureLayer3.queryFeatures(query, modal_Result);
            }
        }
    }
}

/**
 * 模态窗口添加图层并定位
 * @param featureSet：返回的形状
 */
function modal_Result(featureSet) {
    for (var i = 0, il = featureSet.features.length; i < il; i++) {
        var graphic = featureSet.features[i];
        if (tthg_n) {
            extent_n = graphic.geometry.getExtent();
            tthg_n = false;
        } else {
            extent_n = extent_n.union(graphic.geometry.getExtent());
        }
        app.gLyr.add(new dong.Graphic(graphic.geometry, app.symbol));
    }
    app.map.setExtent(extent_n.expand(1.5));
}

/**********************************任务管理进来地图*****************************************************/
var qnumber = 0;//记录有效的需要查询的次数
//根据区域在地图上显示
function app2() {
    if (app.map1 == "" || app.map1 == null || app.map1 == undefined) {
        return;
    }
    if (allData != "" && allData != null && allData != undefined) {
        app.gLyr1.clear();
        app.extent = new dong.Extent();
        var tthg = true;

        var query = new dong.Query();
        
        var qwhere = [];//转换查询条件，将行政区划转换为查询的编码
        $.each(allData, function (k, item) {
        	var obj = {};
        	obj.symbol = new dong.SimpleFillSymbol("solid", app.outline, new dong.Color(sz_corlor[k]));
        	
        	obj.t1 = "", obj.t2 = "", obj.t3 = "";
            if (item.provinceCodes != "" && item.provinceCodes != null && item.provinceCodes != undefined) {
                if (item.provinceCodes.length > 0) {//省
                    $.each(item.provinceCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                        	obj.t1 += "'" + m + "',";
                        });
                    });
                }
            }
            if (item.cityCodes != "" && item.cityCodes != null && item.cityCodes != undefined) {//市
                if (item.cityCodes.length > 0) {
                    $.each(item.cityCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                        	obj.t2 += "'" + m + "',";
                        });
                    });
                }
            }
            if (item.countyCodes != "" && item.countyCodes != null && item.countyCodes != undefined) {//区县
                if (item.countyCodes.length > 0) {
                    $.each(item.countyCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                        	obj.t3 += "'" + m + "',";
                        });
                    });
                }
            }
            if (obj.t1 != "") {
            	qnumber++;
            }
            if (obj.t2 != "") {
            	qnumber++;
            }
            if (obj.t3 != "") {
            	qnumber++;
            }
            qwhere.push(obj);
        });
        
        
        var hkj = 0;//记录每个有效的查询
        $.each(qwhere, function (k, item) {
            if (item.t1 != "") {
                query.where = "ADMINCODE IN (" + item.t1.substring(0, item.t1.length - 1) + ")";
                app.featureLayer1.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                        	app.extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                        	app.extent = app.extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, item.symbol));
                    }
                    hkj++;
                    setExtent_z(hkj);
                });
            }

            if (item.t2 != "") {
                query.where = "ADMINCODE IN (" + item.t2.substring(0, item.t2.length - 1) + ")";
                app.featureLayer2.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                        	app.extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                        	app.extent = app.extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, item.symbol));
                    }
                    hkj++;
                    setExtent_z(hkj);
                });
            }

            if (item.t3 != "") {
                query.where = "ADMINCODE IN (" + item.t3.substring(0, item.t3.length - 1) + ")";
                app.featureLayer3.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                        	app.extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                        	app.extent = app.extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, item.symbol));
                    }
                    hkj++;
                    setExtent_z(hkj);
                });
            }

        });
    }
}

/**
 * 每次查询都执行一下定位，只有最后一次查询才开始真的定位
 * @param ttpye
 */
function setExtent_z(ttpye){
	if(ttpye == qnumber){
		app.map1.setExtent(app.extent.expand(1.5));
	}
}

/*---------------------------------------------------以上是地图部分----------------------------------------------------------*/
/*检测是否超过最大区域数量*/
function createNewAreaBtn() {
    if (allData.length >= maxAreaNum) {
        cnArea = false;
        swal({
            title: '已达最大区域数量!',
            type: 'warning',
            timer: 1000,
            showConfirmButton: false
        });
    } else {
        cnArea = true;
        $('#editArea').window('open');
    }
}

/*前端设置disabled*/
function setDisabled(data) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    for (var i = 0; i < data.length; i++) {
        var name = '(' + data[i].areaName + ')';
//            检索省份列表
        for (var pro = 0; pro < data[i].provinceCodes.length; pro++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].provinceCodes[pro]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }
//            检索市级列表
        for (var ci = 0; ci < data[i].cityCodes.length; ci++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].cityCodes[ci]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }
//          检索县区级列表
        for (var ct = 0; ct < data[i].countyCodes.length; ct++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].countyCodes[ct]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }
    }
}

/**
 * 更新保存显示的地区code信息
 */
function updataCodeList() {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");//获取ztree对象
    $('#adcodeListBox').empty();//清空原有显示内容
    for (var i = 0; i < 3; i++) {  //遍历省市县三级地区
        switch (i) {
            case 0:
                $('#adcodeListBox').append("<p class='adcodeListBox-title'>选中的省份：</p><div class='adcodeListBox-province'></div>");
                break;
            case 1:
                $('#adcodeListBox').append("<p class='adcodeListBox-title'>选中的地市：</p><div class='adcodeListBox-city'></div>");
                break;
            case 2:
                $('#adcodeListBox').append("<p class='adcodeListBox-title'>选中的区县：</p><div class='adcodeListBox-county'></div>");
                break;
            default:
                break;
        }

        for (var ad in showCode[i]) {
            if (i == 0) {
                $('#adcodeListBox .adcodeListBox-province').append('<span class="alb-pn">' + showCode[i][ad] + '</span>');
            } else if (i == 1) {
                for (var j = 0; j < zTreeData.length; j++) {
                    if (zTreeData[j].code == ad) {
                        break;
                    }
                }
                $('#adcodeListBox .adcodeListBox-city').append('<p>' + zTreeData[j].name + '</p><div class="province-city city' + zTreeData[j].code + '"></div>');
                for (var add in showCode[i][ad]) {
                    $('#adcodeListBox .city' + zTreeData[j].code).append('<span class="alb-cn">' + showCode[i][ad][add] + '</span>')
                }
            } else if (i = 2) {
                for (var j = 0; j < zTreeData.length; j++) {
                    if (zTreeData[j].code == ad) {
                        break;
                    }
                }
                $('#adcodeListBox .adcodeListBox-county').append('<p>' + zTreeData[j].name + '</p><div class="city-county county' + zTreeData[j].code + '"></div>');
                for (var add in showCode[i][ad]) {
                    $('#adcodeListBox .county' + zTreeData[j].code).append('<span class="alb-cn">' + showCode[i][ad][add] + '</span>')
                }
            }
        }
    }
    addLayer(showCode);
    
    $('.codeTree').hide();
    $('.adcodeList').show();
}

/*在选中地区的面板进行修改选中的区域时的函数，重新跳出ztree*/
function revise() {
    $('.adcodeList').hide();
    $('.codeTree').show();
}

/*选择省级*/
function level0(node) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var nodesDis = treeObj.getNodesByParam('chkDisabled', true, node);
    if (nodesDis.length == 0) {
        showCode[node.level][node.adcode] = node.name;
        delete showCode[node.level + 1][node.adcode];
        for (var i = 1; i < showCode.length; i++) {
            for (var a in showCode[i]) {
                if (a.toString().substr(0, 2) == node.adcode.toString().substr(0, 2)) {
                    delete showCode[i][a];
                }
            }
        }
    } else {
        //treeObj.checkNode(node, false, false, false);
        var child = node.children;
        for (var ch = 0; ch < child.length; ch++) {
            if (!child[ch].chkDisabled) {
                level12(child[ch])
            }
        }
    }
}

/*选择市县级*/
function level12(node) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var nodesDis = treeObj.getNodesByParam('chkDisabled', true, node);
    var parNode = node.getParentNode();

    if (nodesDis.length == 0) {
        if (!showCode[node.level][parNode.adcode]) {
            showCode[node.level][parNode.adcode] = {}
        }
        showCode[node.level][parNode.adcode][node.adcode] = node.name;
        if (parNode.children.length == Object.keys(showCode[node.level][parNode.adcode]).length) {
            delete showCode[node.level][parNode.adcode];
            if (node.level == 1) {
                level0(parNode);
            } else {
                level12(parNode);
            }
        }
        if (node.level == 1) {
            delete showCode[node.level + 1][node.adcode];
        }
    } else {
//    delete showCode[node.level][parNode.adcode];
//    treeObj.checkNode(node, false, false, false);
        var child = node.children;
        if (!showCode[node.level + 1][node.adcode]) {
            showCode[node.level + 1][node.adcode] = {};
        }
        for (var ch = 0; ch < child.length; ch++) {
            if (!child[ch].chkDisabled) {
                showCode[node.level + 1][node.adcode][child[ch].adcode] = child[ch].name;
            }
        }
    }

}

/*移除省级*/
function delNode0(node) {
    delete showCode[node.level][node.adcode];
    delete showCode[node.level + 1][node.adcode];
    var ad = node.adcode.substr(0, 2);
    var show = showCode[2];
    for (var i in show) {
        if (i.substr(0, 2) == ad) {
            delete showCode[2][i];
        }
    }
}

/*移除市县级*/
function delNode12(node) {
    var parNode = node.getParentNode();
    var parTrue;
    if (!showCode[node.level][parNode.adcode]) {
        if (node.level == 1) {
            if ($.isEmptyObject(showCode[node.level - 1])) {
                parTrue = true;
            } else {
                parTrue = false;
            }
            delNode0(parNode);
        } else {
            delNode12(parNode);
        }
        if (node.level == 1) {
            if (!parTrue) {
                showCode[node.level][parNode.adcode] = {};
                var child = parNode.children;
                for (var n = 0; n < child.length; n++) {
                    if (!child[n].chkDisabled) {
                        showCode[node.level][parNode.adcode][child[n].adcode] = child[n].name;
                    }
                }
            }
        } else {
            showCode[node.level][parNode.adcode] = {};
            var child = parNode.children;
            for (var n = 0; n < child.length; n++) {
                if (!child[n].chkDisabled) {
                    showCode[node.level][parNode.adcode][child[n].adcode] = child[n].name;
                }
            }
        }

    }
    delete showCode[node.level][parNode.adcode][node.adcode];
    if ($.isEmptyObject(showCode[node.level][parNode.adcode])) {
        delete showCode[node.level][parNode.adcode];
    }
    if (node.level == 1) {
        delete showCode[node.level + 1][node.adcode];
    }
}

/**
 * 创建/编辑区域
 */
function createEditArea() {
    var url = '/area/saveorupdate_area';//新增和修改区域数据的接口
    var checkUrl = '/area/check_areaname';//新增区域时检查区域名称是否重复的
    var areaName = $('#areaName').val();//存储修改的区域名称
    if (!areaName) { //判断区域名称是否为空
        alert('kong')
        return;
    }

    var obj = {
        missionId: qjMsg.rwId,
        scenarinoId: qjMsg.qjId,
        areaName: areaName,
        userId: userId,
        scenarinoStartDate: moment(qjMsg.qjStartDate).format('YYYY-MM-DD HH:mm:ss'),
        scenarinoEndDate: moment(qjMsg.qjEndDate).format('YYYY-MM-DD HH:mm:ss'),
        areaId: $('#areaName').attr('data-id') || '',
        provinceCodes: '',
        cityCodes: '',
        countyCodes: '',
        scenarinoStatus: qjMsg.scenarinoStatus
    };  //存储此区域的信息

    var pArr = []; //临时的省份数组
    for (var i in showCode[0]) {
        var proObj = {};
        proObj[i] = showCode[0][i];
        pArr.push(proObj);
    }
    obj.provinceCodes = JSON.stringify(pArr);
    var ctArr = []; //临时的地市数组
    for (var i in showCode[1]) {
        for (var ii in showCode[1][i]) {
            var cityObj = {};
            cityObj[ii] = showCode[1][i][ii];
            ctArr.push(cityObj)
        }
    }
    obj.cityCodes = JSON.stringify(ctArr);
    var crArr = []; //临时的区县数组
    for (var i in showCode[2]) {
        for (var ii in showCode[2][i]) {
            var countyObj = {};
            countyObj[ii] = showCode[2][i][ii];
            crArr.push(countyObj)
        }
    }
    obj.countyCodes = JSON.stringify(crArr);

    if (pArr.length == 0 && ctArr.length == 0 && crArr.length == 0) {  //如果一个区域没有选择任何地区则不允许创建或修改
        alert("请选择范围");
        return;
    }

    if (!$('#areaName').attr('data-id')) {  //通过判断是否有data-id这个属性，判断是否为创建
        ajaxPost(checkUrl, {  //判断是否重名
            userId: userId,
            scenarinoId: qjMsg.qjId,
            areaName: areaName,
        }).success(function (res) {
            if (!res.data) {   //如果返回的数据为空说明，名称重复
                swal({
                    title: '名称重复!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {


                $('#' + $('#areaName').attr('data-id')).find('button.btn-flash').removeClass('btn-flash');
                ajaxPost(url, obj).success(function (res) {

                    var obj = {};  //创建一个临时的存储此区域的对象
                    obj.areaId = res.data.areaId;
                    obj.areaName = areaName;
                    obj.timeFrame = [];
                    obj.timeItems = [{
                        planId: -1,
                        planName: '无',
                        timeId: res.data.timeId,
                        timeEndDate: qjMsg.qjEndDate,
                        timeStartDate: qjMsg.qjStartDate
                    }];//给一个空的时段
                    obj.provinceCodes = pArr;
                    obj.cityCodes = ctArr;
                    obj.countyCodes = crArr;
                    allData.push(obj);  //将此条数据传入alldata
                    showTimeline(allData); //重新绘制时间线函数
                    $('#editArea').window('close'); //关闭编辑弹出框
                    app2();

                })
            }
        }).error(function () {
            swal({
                title: '名称校验失败!',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
        });
    } else {
        ajaxPost(url, obj).success(function (res) {

            allData[areaIndex].provinceCodes = pArr;
            allData[areaIndex].cityCodes = ctArr;
            allData[areaIndex].countyCodes = crArr;
//                $('#' + $('#areaName').attr('data-id')).find('b').html(areaName);
            $('#editArea').window('close'); //关闭编辑弹出框
            app2();
        })
    }
}

/*显示已选择code,并进行checked*/
function setShowCode(data) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    proNum = data.provinceCodes.length;
    cityNum = data.cityCodes.length;
    countyNum = data.countyCodes.length;
    showCode[0] = {};
    showCode[1] = {};
    showCode[2] = {};

    if (proNum == 0) {
        showCode[0] = {};
    } else {
        for (var i = 0; i < proNum; i++) {
            $.extend(showCode[0], data.provinceCodes[i]);

            var node = treeObj.getNodeByParam("adcode", Object.keys(data.provinceCodes[i]), null);
            treeObj.checkNode(node, true, true, false);
        }
    }
    if (cityNum == 0) {
        showCode[1] = {};
    } else {
        for (var ii = 0; ii < cityNum; ii++) {
            var adcode1 = Object.keys(data.cityCodes[ii])[0];
            if (!showCode[1][adcode1.substr(0, 2) + '0000']) showCode[1][adcode1.substr(0, 2) + '0000'] = {};
            $.extend(showCode[1][adcode1.substr(0, 2) + '0000'], data.cityCodes[ii]);

            var node = treeObj.getNodeByParam("adcode", Object.keys(data.cityCodes[ii]), null);
            treeObj.checkNode(node, true, true, false);
        }
    }
    if (countyNum == 0) {
        showCode[2] = {};
    } else {
        for (var iii = 0; iii < countyNum; iii++) {
            var adcode2 = Object.keys(data.countyCodes[iii])[0];
            if (!showCode[2][adcode2.substr(0, 4) + '00']) showCode[2][adcode2.substr(0, 4) + '00'] = {};
            $.extend(showCode[2][adcode2.substr(0, 4) + '00'], data.countyCodes[iii]);

            var node = treeObj.getNodeByParam("adcode", Object.keys(data.countyCodes[iii]), null);
            treeObj.checkNode(node, true, true, false);
        }
    }
}

function initDate(s, e, start) {
    $("#qyTimePoint").daterangepicker({


        timePicker: true,
        format: 'YYYY-MM-DD HH',
        todayHighlight: false,
        singleDatePicker: true,
        language: 'zh-CN',
        autoclose: true,
        todayBtn: true,
        timePicker24Hour: true,
        startDate: s.format('YYYY-MM-DD HH'),
        //endDate: e.format('YYYY-MM-DD HH'),
        minDate: s.format('YYYY-MM-DD HH'),//最早可选日期
        maxDate: e.format('YYYY-MM-DD HH'),//最大可选日期
        locale: {
            format: "YYYY-MM-DD HH",
            //weekLabel: "W",
            applyLabel: "确定", //按钮文字
            cancelLabel: "取消",//按钮文字
            daysOfWeek: [
                "日", "一", "二", "三", "四", "五", "六"
            ],
            monthNames: [
                "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
            ],
            //firstDay: 1
        },
    })
        .on('changeDate', function (ev) {
            var date = moment(ev.date).format('YYYY-MM-DD HH');
        });
}

/**
 * 编辑时段时间html （编辑某一时段使用）
 * 时段编辑时显示的前一时段后一时段当前时段的信息
 */
function editHtml(id) {
    $('#' + id + ' .showTimes .col-4 p').eq(0).empty();
    $('#' + id + ' .showTimes .col-4 p').eq(1).empty();
    $('#' + id + ' .showTimes .col-4 p').eq(2).empty();
    $('#selectEditPoint').empty();
    if (timeIndex == 0) {
        $('#' + id + ' .showTimes .col-4 p').eq(0)
            .html('<h4>无时段</h4>');
        if (allData[areaIndex].timeItems.length == 1) {
            $('#' + id + ' .showTimes .col-4 p').eq(2)
                .html('<h4>无时段</h4>');
        } else {
            $('#' + id + ' .showTimes .col-4 p').eq(2)
                .html(momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        }
    } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
        $('#' + id + ' .showTimes .col-4 p').eq(2)
            .html('<h4>无时段</h4>');
        if (allData[areaIndex].timeItems.length == 1) {
            $('#' + id + ' .showTimes .col-4 p').eq(0)
                .html('<h4>无时段</h4>');
        } else {
            $('#' + id + ' .showTimes .col-4 p').eq(0)
                .html(momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        }
    } else {
        $('#' + id + ' .showTimes .col-4 p').eq(0)
            .html(momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        $('#' + id + ' .showTimes .col-4 p').eq(2)
            .html(momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        //editTimeDateObj.type = 'start'
    }
    $('#' + id + ' .showTimes .col-4 p').eq(1)
        .html(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate));
}

/**
 * 选择copy情景，处理copy之后的事件
 */
function subCopyQJ() {
    selectCopyQJ = $('#copyQJ').datagrid('getSelected');

    var copyUrl = '/scenarino/copy_Scenarino';
    ajaxPost(copyUrl, {
        scenarinoId: qjMsg.qjId,
        userId: userId,
        copyscenarinoId: selectCopyQJ.scenarinoId
    }).success(function (res) {
        if (res.status == 0) {
            /*copy成功之后，重新请求区域时段及预案信息*/
            var url = '/area/get_areaAndTimeList';
            ajaxPost(url, {
                scenarinoId: qjMsg.qjId,
                userId: userId
            }).success(function (res) {
                if (res.status == 0) {
                    allData = res.data.slice(0, -1);
                    for (var i = 0; i < allData.length; i++) {
                        allData[i].timeFrame = [];
                        var timeItems = allData[i].timeItems;
                        var tLength = timeItems.length;
                        //$('.areaMsg').append(area);
                        for (var item = 0; item < tLength; item++) {

                            if (item > 0) {
                                var sD = timeItems[item].timeStartDate;
                                allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
                            }
                        }
                    }
                    showTimeline(allData);
                    $('#selectCreateQj').window('close');
                    app2();
                } else {
                    console.log('get_areaAndTimeList 接口异常')
                }

            });
        } else {
            console.log('copy_Scenarino 接口异常')
        }
    })
}

/*返回YYYY-MM-DD HH格式*/
function momentDate(d) {
    var n = Number(d);
    if (!isNaN(n)) {
        return moment(d).format('YYYY-MM-DD HH')
    } else {
        return moment(d, 'YYYY-MM-DD HH').format('YYYY-MM-DD HH')
    }
}

/*编辑时段时间使用*/

function clearTimeDate() {
    editTimeDateObj = {};
    editTimeDateObj.s = moment(selectedTimes.startTime).format('YYYY-MM-DD HH');
    editTimeDateObj.e = moment(selectedTimes.endTime).format('YYYY-MM-DD HH');
    if (timeIndex == 0) {
        editTimeDateObj.afterS = moment(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate).format('YYYY-MM-DD HH');
        editTimeDateObj.afterE = moment(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate).format('YYYY-MM-DD HH');
    } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
        editTimeDateObj.beforeS = moment(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate).format('YYYY-MM-DD HH');
        editTimeDateObj.beforeE = moment(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate).format('YYYY-MM-DD HH');
    } else {
        editTimeDateObj.afterS = moment(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate).format('YYYY-MM-DD HH');
        editTimeDateObj.afterE = moment(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate).format('YYYY-MM-DD HH');
        editTimeDateObj.beforeS = moment(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate).format('YYYY-MM-DD HH');
        editTimeDateObj.beforeE = moment(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate).format('YYYY-MM-DD HH');
    }
}

/*编辑时段时间html*/
function updatetimeSow() {
    $('#editTime1 .showTimes .col-4 p').eq(0).empty();
    $('#editTime1 .showTimes .col-4 p').eq(1).empty();
    $('#editTime1 .showTimes .col-4 p').eq(2).empty();
    $('#selectEditPoint').empty();
    var s, e;
    s = editTimeDateObj.s;
    e = editTimeDateObj.e;
    if (timeIndex == 0) {
        e = editTimeDateObj.afterE;
        $('#editTime1 .showTimes .col-4 p').eq(0)
            .html('<h4>无时段</h4>');
        $('#editTime1 .showTimes .col-4 p').eq(2)
            .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
        $('#selectEditPoint').append($('<option value="end">结束时间</option>'))
        //editTimeDateObj.type = 'end'
    } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
        s = editTimeDateObj.beforeS;
        $('#editTime1 .showTimes .col-4 p').eq(2)
            .html('<h4>无时段</h4>');
        $('#editTime1 .showTimes .col-4 p').eq(0)
            .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
        $('#selectEditPoint').append($('<option value="start">开始时间</option>'))
        //editTimeDateObj.type = 'start'
    } else {
        s = editTimeDateObj.beforeS;
        $('#selectEditPoint').append($('<option value="start">开始时间</option>'));
        $('#selectEditPoint').append($('<option value="end">结束时间</option>'));
        $('#editTime1 .showTimes .col-4 p').eq(0)
            .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
        $('#editTime1 .showTimes .col-4 p').eq(2)
            .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
        //editTimeDateObj.type = 'start'
    }
    $('#editTime1 .showTimes .col-4 p').eq(1).html(editTimeDateObj.s + '<br />至<br/>' + editTimeDateObj.e);

    initEditTimeDate(s, e);
    initEditTimeDate(s, e);
}

/*初始化日期插件*/
function initEditTimeDate(s, e) {
    $('#editDate').daterangepicker({
        singleDatePicker: true,  //显示单个日历
        timePicker: true,  //允许选择时间
        timePicker24Hour: true, //时间24小时制
        minDate: s,//最早可选日期
        maxDate: e,//最大可选日期
        locale: {
            format: "YYYY-MM-DD HH",
            separator: " 至 ",
            applyLabel: "确定", //按钮文字
            cancelLabel: "取消",//按钮文字
            weekLabel: "W",
            daysOfWeek: [
                "日", "一", "二", "三", "四", "五", "六"
            ],
            monthNames: [
                "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
            ],
            firstDay: 1
        },
        //"startDate": moment().subtract(2,'w'),
        //"endDate": end,
        "opens": "right"
    }, function (start, end, label) {
        //editTimeDateObj编辑时段的时候存储的前后时段时间对象
        if (editTimeDateObj.type == 'start') {
            if (editTimeDateObj.beforeS >= moment(start).subtract(1, 'h').format('YYYY-MM-DD HH')) {
                console.log('时间不合理请重新选择！！！');
                return
            }
            editTimeDateObj.s = moment(start).format('YYYY-MM-DD HH');
            editTimeDateObj.beforeE = moment(start).subtract(1, 'h').format('YYYY-MM-DD HH')
        } else {
            if (moment(start).add(1, 'h').format('YYYY-MM-DD HH') >= editTimeDateObj.afterE) {
                console.log('时间不合理请重新选择！！！');
                return
            }
            editTimeDateObj.e = moment(start).format('YYYY-MM-DD HH');
            editTimeDateObj.afterS = moment(start).add(1, 'h').format('YYYY-MM-DD HH')
        }
        updatetimeSow();
    })
}

/*添加时间段*/
function addTimes() {
    addTimePoint = $('#qyTimePoint').val();
    var timePoint = moment(addTimePoint).format('YYYY-MM-DD HH:mm:ss');
    var timeFrame = allData[areaIndex].timeFrame;
    timeFrame.push(timePoint);
    timeFrame.sort();
    var index = timeFrame.indexOf(timePoint);

    var url = '/time/save_time';
    ajaxPost(url, {
        missionId: qjMsg.rwId,
        scenarinoId: qjMsg.qjId,
        userId: userId,
        areaId: allData[areaIndex].areaId,
        selectTimeId: allData[areaIndex].timeItems[index].timeId,
        addTimeDate: timePoint,
        scenarinoStatus: qjMsg.scenarinoStatus
    }).success(function (res) {

        getAreaAndTime();
        $('#timePlan').window('close');

    }).error(function () {
        timeFrame.splice(index, 1);
        swal({
            title: '添加失败!',
            type: 'error',
            timer: 1000,
            showConfirmButton: false
        });
        $('#timePlan').window('close');
    });
}
/*请求区域及时段*/
function getAreaAndTime() {
    var url = '/area/get_areaAndTimeList';
    scenarino = ajaxPost(url, {
        scenarinoId: qjMsg.qjId,
        userId: userId
    });

    scenarino.then(function (res) {
        allData = res.data.slice(0, -1);
        for (var i = 0; i < allData.length; i++) {
            allData[i].timeFrame = [];
            var timeItems = allData[i].timeItems;
            var tLength = timeItems.length;
            for (var item = 0; item < tLength; item++) {
                if (item > 0) {
                    var sD = timeItems[item].timeStartDate;
                    allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
                }
            }
        }
        showTimeline(allData);//生成区域时段
    });
}
/*添加预案*/
function addPlan(e) {
    //添加预案时判断是新建的预案还是copy的预案
    newPlan = e;
    if (newPlan) {
        var url = '/plan/add_plan';
        var params = {
            timeId: msg.content.timeId,
            userId: userId,
            missionId: msg.content.rwId,
            scenarioId: msg.content.qjId,
            areaId: msg.content.areaId,
            timeStartTime: moment(msg.content.timeStartDate).format('YYYY-MM-DD HH'),
            timeEndTime: moment(msg.content.timeEndDate).format('YYYY-MM-DD HH'),
            planName: $('#yaName').val(),
            scenarinoStatus: qjMsg.scenarinoStatus,
        };
        ajaxPost(url, params).success(function (res) {
            msg.content.planId = res.data;
            msg.content.planName = $('#yaName').val();
            vipspa.setMessage(msg);
            createNewPlan();
            $('#yaName').val('');
        });
    } else {
        if (!selectCopyPlan) {
            swal({
                title: '无预案!',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
            return
        }
        ;
        var url = '/plan/copy_plan';

        ajaxPost(url, {
            userId: userId,
            timeId: allData[areaIndex].timeItems[timeIndex].timeId,
            scenarinoStatus: qjMsg.scenarinoStatus,
            scenarioId: qjMsg.qjId,
            missionId: qjMsg.rwId,
            areaId: allData[areaIndex].areaId,
            timeStartTime: moment(allData[areaIndex].timeItems[timeIndex].timeStartDate).format('YYYY-MM-DD HH'),
            timeEndTime: moment(allData[areaIndex].timeItems[timeIndex].timeEndDate).format('YYYY-MM-DD HH'),
            copyPlanId: selectCopyPlan.planReuseId
        }).success(function (res) {
            $('.jpjs.disNone').removeClass('disNone');
            $('.jpjs').removeAttr('disabled');
            $('.jpztck').addClass('disNone');
            //添加预案成功后更新allData数据
            allData[areaIndex].timeItems[timeIndex].planId = res.data;
            allData[areaIndex].timeItems[timeIndex].planName = selectCopyPlan.planReuseName;
            showTimeline(allData);
            $('#timePlan').window('close');
        })
    }
}
/*选择已有预案按钮*/
function copyPlan() {
    initCopyPlanTable();
}
/*初始化复制预案table*/
function initCopyPlanTable() {
    $('#copyPlanTable').datagrid({
        url: '/ampc/plan/copy_plan_list',
        dataType: "json",
        fit:true,
        singleSelect: true,
        contentType: "application/json", // 请求远程数据的内容类型。
        queryParams: function (m) {
            var json = {
                "token": "",
                "data": {
                    "userId": userId
                }
            };
            return json;
        },
        loadFilter: function (res) {
            return res.data
        },
        columns: [[{
            checkbox: true,
            field: 'cb'
        }, {
            field: 'planReuseName',
            title: '预案名称',
            width: 150
        }, {
            field: 'missionName',
            title: '任务名称',
            width: 80
        }, {
            field: 'scenarioName',
            title: '情景名称',
            width: 120
        }, {
            field: 'areaName',
            title: '区域名称',
            width: 120
        }, {
            field: 'areaName',
            title: '区域名称',
            width: 120,
            formatter:copyPlanAddTime
        }]],
        onCheck:function (index,row) {
            selectCopyPlan = row;

        }
    });

}
/*format 函数*/
function copyPlanAddTime(v, row, i) {
    return moment(row.addTime).format('YYYY-MM-DD HH:mm:ss')
}
/**
 * 删除时间段
 */
function delTimes() {
    var url = '/time/delete_time';
    var mId;
    var ub = $('.delSelect input:checked').val();
    var delTime;
    //判断我删除的时段是与前一时段合并还是与后一时段合并
    if (ub == 'up') {
        mId = allData[areaIndex].timeItems[timeIndex - 1].timeId;
        delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate)).format('YYYY-MM-DD HH');
    } else {
        mId = allData[areaIndex].timeItems[timeIndex + 1].timeId;
        delTime = moment(momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate)).format('YYYY-MM-DD HH');
    }

    ajaxPost(url, {
        deleteTimeId: allData[areaIndex].timeItems[timeIndex].timeId,
        startDate: moment(allData[areaIndex].timeItems[timeIndex].timeStartDate).format('YYYY-MM-DD HH:mm:ss'),
        endDate: moment(allData[areaIndex].timeItems[timeIndex].timeEndDate).format('YYYY-MM-DD HH:mm:ss'),
        mergeTimeId: mId,
        userId: userId,
        status: ub,
        scenarinoStatus: qjMsg.scenarinoStatus,
        scenarinoId: qjMsg.qjId
    }).success(function () {
        var index = allData[areaIndex].timeFrame.indexOf(delTime);
        //在前端进行数据处理
        if (ub == 'up') {
            allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
        } else {
            allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
        }
        //delTimes.remove();
        allData[areaIndex].timeFrame.splice(index, 1);
        allData[areaIndex].timeItems.splice(timeIndex, 1);
        showTimeline(allData);
        $('#timePlan').window('close');
        //循环判断数据信息，进行减排计算等按钮的使用操作
        for (var i = 0; i < allData.length; i++) {
            for (var ii = 0; ii < allData[i].timeItems.length; ii++) {
                if (allData[i].timeItems[ii].planId == -1) {
                    $('.jpjs').removeClass('disNone');
                    $('.jpjs').attr('disabled', true);
                    $('.jpztck').addClass('disNone');
                } else {
                    $('.jpjs').removeAttr('disNone');
                    $('.jpjs').removeClass('disNone');
                    $('.jpztck').addClass('disNone');
                    return;
                }
            }
        }
    })

}
/**
 * 提交编辑时段的时间
 */
function sunEditTimeDate() {
    var url = '/time/update_time';
    var after, before, date;
    //判断修改时段的时间是开始时间还是结束时间
    if (editTimeDateObj.type == 'start') {
        date = moment(editTimeDateObj.s).format('YYYY-MM-DD HH:mm:ss');
        before = allData[areaIndex].timeItems[timeIndex - 1].timeId;
        after = allData[areaIndex].timeItems[timeIndex].timeId
    } else {
        date = moment(editTimeDateObj.e).add(1, 'h').format('YYYY-MM-DD HH:mm:ss');
        after = allData[areaIndex].timeItems[timeIndex + 1].timeId;
        before = allData[areaIndex].timeItems[timeIndex].timeId
    }
    ajaxPost(url, {
        userId: userId,
        updateDate: date,
        beforeTimeId: before,
        afterTimeId: after,
        scenarinoStatus: qjMsg.scenarinoStatus,
        scenarinoId: qjMsg.qjId
    }).success(function (res) {
        if (res.status == 0) {
            //在前端将allData进行更新，省去在请求一遍areaAndTimeList接口
            if (editTimeDateObj.type == 'start') {
                allData[areaIndex].timeItems[timeIndex].timeStartDate = moment(date).format('x') - 0;
                allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = moment(editTimeDateObj.beforeE).format('x') - 0;
            } else {
                allData[areaIndex].timeItems[timeIndex].timeEndDate = moment(editTimeDateObj.e).format('x') - 0;
                allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = moment(editTimeDateObj.afterS).format('x') - 0;
            }
            showTimeline(allData);
            $('#timePlan').window('close');
        }
    })
}
/*减排计算按钮*/
function jpjsBtn() {
    var url = '/jp/areajp';
    var params = {
        bigIndex:qjMsg.esCouplingId,
        scenarinoId: qjMsg.qjId,
        areaAndPlanIds: {},
        userId: userId
    }
    for (var i = 0; i < allData.length; i++) {
        var planArr = [];
        var times = allData[i].timeItems;
        for (var p = 0; p < times.length; p++) {
            if (times[p].planId != -1) {
                planArr.push(times[p].planId)
            }
        }
        if (planArr.length > 0) {
            params.areaAndPlanIds[allData[i].areaId] = planArr
        }
    }

    if (Object.keys(params.areaAndPlanIds).length > 0) {
        ajaxPost(url, params).success(function (res) {
            if (res.status == 0) {
                if (res.data == 1) {
                    /*减排计算成功后关闭减排计算按钮，打开减排计算状态查看按钮*/
                    $('.jpjs').addClass('disNone');
                    $('.jpztck.disNone').removeClass('disNone').click();

                    scenarinoType(3);
                    qjMsg.scenarinoStatus = 3;
                    window.clearTimeout(jpztSetTimeOut);
                    jpztckBtn(60000);
                } else {
                    console.log('计算异常')
                }

                /*这里控制所有禁止操作*/

            } else {
                console.log(res.msg);
                console.log('接口异常')
            }
        })
    }
}
/*减排分析按钮*/
function jumpJpfx() {
    var msg1 = {
        'id': 'jpfxMessage',
        'content': {}
    };
    msg1.content.rwId = qjMsg.rwId;
    msg1.content.rwName = qjMsg.rwName;
    msg1.content.qjId = qjMsg.qjId;
    msg1.content.qjName = qjMsg.qjName;
    vipspa.setMessage(msg);

    var a = document.createElement('a');
    a.href = '#/rwgl_reductAnalys';
    a.click();
}
/*重置减排计算*/
function initJPJS() {
    var url = '/plan/update_Status';
    ajaxPost(url, {
        userId: userId,
        scenarinoId: qjMsg.qjId
    }).success(function (res) {
        if (res.status == 0) {
            window.clearTimeout(jpztSetTimeOut);
            jpztckBtn(3000);
        } else {
            console.log(url + '故障')
        }
    }).error(function () {
        console.log(url + '错误')
    })
}
/*减排状态查看*/
function jpztckBtn(t) {
    var url = '/jp/areaStatusJp';
    var params = {
        bigIndex:qjMsg.esCouplingId,
        scenarinoId: qjMsg.qjId,
        userId: userId,
        areaAndPlanIds: ''
    }
    ajaxPost(url, params).success(function (res) {

        if (res.status == 0) {
            if (res.data.type == 0) {
                var jsjd = (Math.round(res.data.percent * 10000)) / 100 + '%';
                var yys = moment(res.data.time * 1000).subtract(8, 'h').format('HH时mm分ss秒');
                var sysj = moment((res.data.time / res.data.percent - res.data.time) * 1000).subtract(8, 'h').format('HH时mm分ss秒');

                $('.jsjd').empty().html(jsjd);
                $('.yys').empty().html(yys);
                $('.sysj').empty().html(sysj);


                if (res.data.percent == 1) {
                    /*减排计算完成后，查看情景状态*/
                    findQJstatus();
                } else {
                    /*如果减排计算未完成，则递归函数，反复查看，直到减排计算完成，t为等候时间，模态框打开时为3s，模态框关闭时为1min*/
                    jpztSetTimeOut = window.setTimeout(function () {
                        jpztckBtn(t)
                    }, t)
                }
            } else if (res.data.type == 1) {
                $('#jpzt').window('close');
                console.log('重新计算中！！！');
                window.setTimeout(function () {
                    swal({
                        title: '重新计算中!',
                        type: 'warning',
                        timer: 1000,
                        showConfirmButton: false
                    });
                }, 50)
            } else if (res.data.type == 2) {
                $('#jpzt').window('close');
                console.log('计算排队中');
                window.setTimeout(function () {
                    swal({
                        title: '计算排队中!',
                        type: 'warning',
                        timer: 1000,
                        showConfirmButton: false
                    });
                }, 50)
            } else {
                console.log('计算接口异常')
            }
        } else {
            console.log('接口故障')
        }

    })
    //}
}
function findQJstatus() {
    var url = '/scenarino/find_Scenarino_status';
    ajaxPost(url, {
        userId: userId,
        scenarinoId: qjMsg.qjId
    }).success(function (res) {
        qjMsg.scenarinoStatus = res.data.scenarinoStatus;
        if (qjMsg.scenarinoStatus != 5) {
            /*如果情景状态未变成可执行，递归请求*/
            window.setTimeout(function () {
                findQJstatus();
            },1000);
        } else {
            scenarinoType(qjMsg.scenarinoStatus);
        }
    })
}