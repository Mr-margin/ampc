﻿/**
 * Created by lvcheng on 2017/2/21.
 */

/*全局变量*/
var totalWidth, totalDate, startDate, qjMsg;
var index, indexPar, handle, minLeft, maxLeft, selfLeft, startX, leftWidth, rightWidth;
var allData = [];//保存所有区域时段信息
var areaIndex, timeIndex;//保存选择区域和时段的索引
var showCode = [{}, {}, {}];//保存所选的地区
var proNum, cityNum, countyNum;//保存所选地区数量
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

function showMap() {
    addLayer(showCode);
}

/**
 * 创建区域模态框中中间显示地区部分
 * @param adcode 地区code
 * @param name 地区名称
 * @param level 地区等级（省市县 对应 012）
 * @returns {jQuery|Mixed|HTMLElement}
 */
function addP(adcode, name, level) {
    return $('<p class="col-md-3"><i class="im-close" style="cursor: pointer" onclick="delAdcode(' + adcode + ',' + level + ')"></i>&nbsp;&nbsp;' + name + ' </p>')
}

/**
 * 更新保存显示的地区code信息
 */
function updataCodeList() {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    $('.adcodeList').empty();
    for (var i = 0; i < 3; i++) {
        for (var ad in showCode[i]) {
            if (i == 0) {
                $('.adcodeList').append(addP(ad, showCode[i][ad], i))
            } else {
                for (var add in showCode[i][ad]) {
                    $('.adcodeList').append(addP(add, showCode[i][ad][add], i))
                }
            }

        }
    }
    /*  proNum = Object.keys(showCode[0]).length;
     cityNum = (function () {
     var n = 0;
     for (var ad in showCode[1]) {
     n += Object.keys(showCode[1][ad]).length;
     }
     return n;
     })();
     countyNum = (function () {
     var n = 0;
     for (var ad in showCode[2]) {
     n += Object.keys(showCode[2][ad]).length;
     }
     return n;
     })();*/

    // proNum = treeObj.getNodesByFilter(function (node) {
    //   return (node.checked && (node.level == 0))
    // }).length;
    // cityNum = treeObj.getNodesByFilter(function (node) {
    //   return (node.checked && (node.level == 1))
    // }).length;
    // countyNum = treeObj.getNodesByFilter(function (node) {
    //   return (node.checked && (node.level == 2))
    // }).length;

    /*  $('.proNumber span').html(proNum);
     $('.cityNumber span').html(cityNum);
     $('.countyNumber span').html(countyNum);*/

}

/**
 * 删除所选地区
 * @param adcode 地区code
 * @param level 地区等级（省市县对应012）
 */
function delAdcode(adcode, level) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var node = treeObj.getNodeByParam("adcode", adcode, null);
    treeObj.checkNode(node, false, true);
    switch (level) {
        case 0:
            delete showCode[level][adcode];
            break;
        case 1:
            delete showCode[level][adcode.toString().substr(0, 2) + "0000"][adcode];
            break;
        case 2:
            delete showCode[level][adcode.toString().substr(0, 4) + '00'][adcode];
            break;
    }
    addLayer(showCode);
    updataCodeList();

}

/**
 * 删除所有地区
 */
function clearAllArea() {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    treeObj.checkAllNodes(false);
    showCode = [{}, {}, {}];
    addLayer(showCode);
    updataCodeList();
}


/**
 * 情景计算状态
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


initialize();


var zTreeData;
var allData1;//临时变量
function initialize() {

    /**
     * 设置导航条菜单
     */
    $("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景编辑</a>');
    previous();

    var ls = window.sessionStorage;
    qjMsg = vipspa.getMessage('qjMessage').content;

    if (!qjMsg) {
        var url = '/scenarino/find_Scenarino_status';
        qjMsg = JSON.parse(ls.getItem('qjMsg'));
        ajaxPost(url, {                   //请求情景状态，根据情景状态，对区域时段进行操作控制
            userId: userId,
            scenarinoId: qjMsg.qjId
        }).success(function (res) {
            qjMsg.scenarinoStatus = res.data.scenarinoStatus;
            scenarinoType(qjMsg.scenarinoStatus);
        })
    } else {
        ls.setItem('qjMsg', JSON.stringify(qjMsg));
        scenarinoType(qjMsg.scenarinoStatus)
    }
    $('.qyCon').removeClass('disNone');

    /*显示左侧菜单栏中信息*/
    $('.qyCon .nowRw span').html(qjMsg.rwName);
    $('.qyCon .nowQj span').html(qjMsg.qjName);
    $('.qyCon .seDate span').html(moment(qjMsg.qjStartDate).format('YYYY-MM-DD') + '至' + moment(qjMsg.qjEndDate).format('YYYY-MM-DD'));


    /*向路由中存放信息，以备措施页面使用*/
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
    var scenarino = ajaxPost(url, {   //请求所有区域及时段
        scenarinoId: qjMsg.qjId,
        userId: userId
    });
    //initDate();

    scenarino.then(function (res) {

        allData1 = res.data.slice(0, -1);//保存除最后一条以外的数据
        if (qjMsg.scenarinoStatus == 2) {//根据情景状态及预案信息（有无缘），判断减排计算按钮的使用控制
            for (var i = 0; i < allData1.length; i++) {
                for (var m = 0; m < allData1[i].timeItems.length; m++) {
                    if (allData1[i].timeItems[m].planId != -1) {
                        $('.jpjs').removeAttr('disabled');
                        break;
                    }
                }
            }
        }
        //根据返回值最后一条数据isnew判断当前情景是否为新创建，新创建情景弹窗提示是新建情景还是复制情景
        if (res.data[res.data.length - 1].isnew) {
            $('#selectCreateQj').modal('show')
        } else {
            selectCopy(false);
        }

        //$('#selectCreateQj').modal('show');
        //for (var i = 0; i < res.data.length; i++) {
        //  allData[i].timeFrame = [];
        //  var timeItems = res.data[i].timeItems;
        //  var tLength = timeItems.length;
        //  //$('.areaMsg').append(area);
        //  for (var item = 0; item < tLength; item++) {
        //
        //    if (item > 0) {
        //      var sD = timeItems[item].timeStartDate;
        //      allData[i].timeFrame[item - 1] = moment(sD).format('YYYY-MM-DD HH');
        //    }
        //  }
        //
        //}
        //
        //
        //showTimeline(allData);
        //app2();
    });

    //initZTree();  //初始化zTree数据
}


var scenarino;
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


/*删除区域*/
function delArea(e) {
    var areaIndex = $('.areaTitle_con').index($(e).parents('.areaTitle_con'));
    var url = '/area/delete_area';
    //var areaIds = [allData[areaIndex].areaId.toString()];
    var params = {
        userId: userId,
        //areaIds: allData[areaIndex].areaId.toString()
        areaIds: $(e).parents('.areaTitle_con').attr('id'),
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
            ajaxPost(url, params).success(function (res) {
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
                swal({
                    title: '删除失败!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            })

        });

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

/*编辑时段时间*/
function sunEditTimeDate() {
    var url = '/time/update_time';
    var after, before, date;
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
            if (editTimeDateObj.type == 'start') {
                allData[areaIndex].timeItems[timeIndex].timeStartDate = moment(date).format('x') - 0;
                allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = moment(editTimeDateObj.beforeE).format('x') - 0;
            } else {
                allData[areaIndex].timeItems[timeIndex].timeEndDate = moment(editTimeDateObj.e).format('x') - 0;
                allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = moment(editTimeDateObj.afterS).format('x') - 0;
            }
            showTimeline(allData);
        }
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


    }).error(function () {
        timeFrame.splice(index, 1);
        swal({
            title: '添加失败!',
            type: 'error',
            timer: 1000,
            showConfirmButton: false
        });
    });
}

/*添加时段按钮事件*/
//function openAddTimes() {
//  $('#qyTime').modal('show');
//}

/*返回YYYY-MM-DD HH格式*/
function momentDate(d) {
    var n = Number(d);
    if (!isNaN(n)) {
        return moment(d).format('YYYY-MM-DD HH')
    } else {
        return moment(d, 'YYYY-MM-DD HH').format('YYYY-MM-DD HH')
    }
}

/*打开删除时段模态框*/
//function openDelTimes() {
//  $('#delTime').modal('show');
//}

/*删除时间段*/
function delTimes() {
    var url = '/time/delete_time';
    var mId;
    var ub = $('.delSelect input:checked').val();
    var delTime;
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
        if (ub == 'up') {
            allData[areaIndex].timeItems[timeIndex - 1].timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
        } else {
            allData[areaIndex].timeItems[timeIndex + 1].timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
        }
        //delTimes.remove();
        allData[areaIndex].timeFrame.splice(index, 1);
        allData[areaIndex].timeItems.splice(timeIndex, 1);
        showTimeline(allData);
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

/*当前选中的时段*/
var selectedTimes;
function ontTimes(data) {
    selectedTimes = data;
    //console.log(selectedTimes);
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
    $('#timePlan').modal('show')
}


var addTimePoint;         //添加的时间点
/*timePlan 打开时需的准备工作*/
$('#timePlan').on('show.bs.modal', function (event) {

    $('#time .active').removeClass('active');
    $('#plan .active').removeClass('active');
    $('.addTimeLi').addClass('active');
    $('.addTimeDiv').addClass('active');
    areaIndex = selectedTimes.index;
    timeIndex = selectedTimes.indexNum;
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
    /*滑块*/
    /*var timeArr = [];
     while (timeStart.isBefore(timeEnd1, 'h')) {
     timeArr.push(timeStart.add(1, 'h').format('YYYY-MM-DD HH'));
     }
     $('.addTimeHk').eq(0).empty();
     $('.addTimeHk').eq(0).append($('<h3 style="margin-bottom:40px;">编辑插入此时间段的时间点：</h3>'));
     $('.addTimeHk').eq(0).append($('<input type="hidden" class="qyTimePoint" id="qyTimePoint" />'));
     $('#qyTimePoint').jRange({/!*初始化滑块控件*!/
     from: 0,
     to: timeArr.length,
     step: 1,
     scale: [timeArr[0].substr(5), timeArr[Math.floor(timeArr.length/2)].substr(5), timeArr[timeArr.length - 1].substr(5)],
     format: function (s) {
     addTimePoint = timeArr[s];
     return timeArr[s];
     },
     width: 550,
     showLabels: true,
     showScale: true,
     theme: 'theme-blue'
     });
     $('#qyTimePoint').jRange('setValue', Math.floor(timeArr.length/2));*/
    /*添加时段 end*/

    /**************************************************************************************/
    /*删除时段 start*/
    editHtml('delTime');
    if (allData[areaIndex].timeItems.length > 1) {
        $('.delTimeLi').removeClass('disNone');
        $('.delTimeDiv').find('.delSelect').empty();


        var redio = $('.radio.disNone').clone().removeClass('disNone');
        if (timeIndex == 0) {
            redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
            redio.find('input').val('down');
        } else if (timeIndex == (allData[areaIndex].timeItems.length - 1)) {
            redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
            redio.find('input').val('up');
        } else {
            var redio2 = $('.radio.disNone').clone().removeClass('disNone');
            redio2.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
            redio2.find('input').val('up');
            redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
            redio.find('input').val('down');
            $('.delTimeDiv').find('.delSelect').append(redio2);
        }
        $('.delTimeDiv').find('.delSelect').append(redio);
        redio.find('input').attr('checked', 'checked');


    } else {
        $('.delTimeLi').addClass('disNone');
        $('.delTimeLi').removeClass('active');
        $('.delTimeDiv').removeClass('active');
    }
    /*删除时段 end*/

    /**************************************************************************************/
    /*编辑时段 start*/
    if (allData[areaIndex].timeItems.length > 1) {
        $('.editTimeLi').removeClass('disNone');

        clearTimeDate();
        updatetimeSow();
        editTimeDateObj.type = $('#selectEditPoint').val();
    } else {
        $('.editTimeLi').addClass('disNone');
    }

    /*编辑时段 end*/

    /**************************************************************************************/
    /*添加预案 start*/
    $('.delPlanLi').addClass('disNone');//暂无删除预案接口支持
    if (selectedTimes.planId == -1) {
        copyPlan();
        $('.addPlanLi').removeClass('disNone');
        $('.copyPlanLi').removeClass('disNone');
        $('.editPlanLi').addClass('disNone');

        $('.addPlanLi').addClass('active');
        $('.addPlanDiv').addClass('active');

    } else {
        $('.addPlanLi').addClass('disNone');
        $('.copyPlanLi').addClass('disNone');
        $('.editPlanLi').removeClass('disNone');

        $('.editPlanLi').addClass('active');
        $('.editPlanDiv').addClass('active');
    }
    /*添加预案 end*/

});

/*打开预案编辑*/
//function openAddYA() {
//  $('#addYA .selectAdd').removeClass('disNone');
//  $('#addYA .addCopyPlan').addClass('disNone');
//  $('#addYA .addNewPlan').addClass('disNone');
//  $('#addYA .modal-footer').addClass('disNone');
//
//  window.setTimeout(function () {
//    $('#addYA').modal('show')
//  }, 350)
//}

/*时段预案操作模态框选择 end*/
var newPlan;
/*添加预案*/
function addPlan(e) {
    newPlan = e;
    if (newPlan) {//判断是添加预案，还是复制预案
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
            allData[areaIndex].timeItems[timeIndex].planId = res.data;
            allData[areaIndex].timeItems[timeIndex].planName = selectCopyPlan.planReuseName;
            showTimeline(allData);
        })
    }
}

/*创建新预案*/
function createNewPlan(e) {
    window.setTimeout(function () {
        $('#addCSBJ')[0].click();
    }, 500)
}

/*选择已有预案按钮*/
function copyPlan() {
    initCopyPlanTable();
    //var url = '/plan/copy_plan_list';
    //ajaxPost(url, {
    //  userId: userId
    //}).success(function (res) {
    //  for (var i = 0; i < res.data.length; i++) {
    //    $('<option value="' + res.data[i].planId + '">' + res.data[i].planName + '</option>').appendTo('#copyPlan')
    //  }
    //})
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
    msg.content.timeEndDate = t.endTime.getTime();
    //msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
    msg.content.timeStartDate = t.startTime.getTime();
    msg.content.planId = allData[areaIndex].timeItems[timeIndex].planId;
    msg.content.planName = allData[areaIndex].timeItems[timeIndex].planName;
    msg.content.cityCodes = allData[areaIndex].cityCodes;
    msg.content.countyCodes = allData[areaIndex].countyCodes;
    msg.content.provinceCodes = allData[areaIndex].provinceCodes;

    vipspa.setMessage(msg);

    if (msg.content.planId == -1) {
        $('#timePlan').modal('show');
        $('a[href="#plan"]').eq(0).click();

        return
    }
    ;

    createNewPlan();
}

/*编辑时段时间使用*/
var editTimeDateObj = {};
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
    $('#editTime1 .showTimes .col-md-4 p').eq(0).empty();
    $('#editTime1 .showTimes .col-md-4 p').eq(1).empty();
    $('#editTime1 .showTimes .col-md-4 p').eq(2).empty();
    $('#selectEditPoint').empty();
    var s, e;
    s = editTimeDateObj.s;
    e = editTimeDateObj.e;
    if (timeIndex == 0) {
        e = editTimeDateObj.afterE;
        $('#editTime1 .showTimes .col-md-4 p').eq(0)
            .html('<h4>无时段</h4>');
        $('#editTime1 .showTimes .col-md-4 p').eq(2)
            .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
        $('#selectEditPoint').append($('<option value="end">结束时间</option>'))
        //editTimeDateObj.type = 'end'
    } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
        s = editTimeDateObj.beforeS;
        $('#editTime1 .showTimes .col-md-4 p').eq(2)
            .html('<h4>无时段</h4>');
        $('#editTime1 .showTimes .col-md-4 p').eq(0)
            .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
        $('#selectEditPoint').append($('<option value="start">开始时间</option>'))
        //editTimeDateObj.type = 'start'
    } else {
        s = editTimeDateObj.beforeS;
        $('#selectEditPoint').append($('<option value="start">开始时间</option>'));
        $('#selectEditPoint').append($('<option value="end">结束时间</option>'));
        $('#editTime1 .showTimes .col-md-4 p').eq(0)
            .html(editTimeDateObj.beforeS + '<br />至<br/>' + editTimeDateObj.beforeE);
        $('#editTime1 .showTimes .col-md-4 p').eq(2)
            .html(editTimeDateObj.afterS + '<br />至<br/>' + editTimeDateObj.afterE);
        //editTimeDateObj.type = 'start'
    }
    $('#editTime1 .showTimes .col-md-4 p').eq(1).html(editTimeDateObj.s + '<br />至<br/>' + editTimeDateObj.e);

    initEditTimeDate(s, e);
    initEditTimeDate(s, e);
}

/*编辑时段弹窗中显示的时段信息，分别为前一时段、当前时段和后一时段*/
function editHtml(id) {
    $('#' + id + ' .showTimes .col-md-4 p').eq(0).empty();
    $('#' + id + ' .showTimes .col-md-4 p').eq(1).empty();
    $('#' + id + ' .showTimes .col-md-4 p').eq(2).empty();
    $('#selectEditPoint').empty();
    if (timeIndex == 0) {
        $('#' + id + ' .showTimes .col-md-4 p').eq(0)
            .html('<h4>无时段</h4>');
        if (allData[areaIndex].timeItems.length == 1) {
            $('#' + id + ' .showTimes .col-md-4 p').eq(2)
                .html('<h4>无时段</h4>');
        } else {
            $('#' + id + ' .showTimes .col-md-4 p').eq(2)
                .html(momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        }
    } else if (timeIndex == allData[areaIndex].timeItems.length - 1) {
        $('#' + id + ' .showTimes .col-md-4 p').eq(2)
            .html('<h4>无时段</h4>');
        if (allData[areaIndex].timeItems.length == 1) {
            $('#' + id + ' .showTimes .col-md-4 p').eq(0)
                .html('<h4>无时段</h4>');
        } else {
            $('#' + id + ' .showTimes .col-md-4 p').eq(0)
                .html(momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        }
    } else {
        $('#' + id + ' .showTimes .col-md-4 p').eq(0)
            .html(momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        $('#' + id + ' .showTimes .col-md-4 p').eq(2)
            .html(momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        //editTimeDateObj.type = 'start'
    }
    $('#' + id + ' .showTimes .col-md-4 p').eq(1)
        .html(momentDate(allData[areaIndex].timeItems[timeIndex].timeStartDate) + '<br />至<br/>' + momentDate(allData[areaIndex].timeItems[timeIndex].timeEndDate));
}

/*选择修改时段开始时间或结束时间*/
function selectEditPoint(t) {
    var s, e;
    if ($(t).val() == 'start') {
        s = editTimeDateObj.beforeS;
        e = editTimeDateObj.e;
        editTimeDateObj.type = 'start'
    } else {
        s = editTimeDateObj.s;
        e = editTimeDateObj.afterE;
        editTimeDateObj.type = 'end'
    }

    /*初始化方法执行两遍，否则有问题*/
    initEditTimeDate(s, e);
    initEditTimeDate(s, e);
}


/**
 * 打开区域创建编辑模态框
 */
$('#editArea').on('show.bs.modal', function (event) {
    /*初始化tree树相关信息*/
    $.fn.zTree.init($("#adcodeTree"), zTreeSetting, zTreeData);
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var nodes = treeObj.getNodesByParam("level", 1);
    //var a = ['11','12','1301'];
    var code = qjMsg.esCodeRange;
    for (var i = 0; i < nodes.length; i++) {
        var adcode = nodes[i].adcode;
        if ((code.indexOf(adcode.substr(0, 2)) == -1) && (code.indexOf(adcode.substr(0, 4)) == -1)) {
            treeObj.hideNodes(nodes[i].children);
        }
    }

    var button = $(event.relatedTarget);
    areaIndex = $('.areaTitle_con').index($(button).parents('.areaTitle_con'));
    //var create = button.data('new');
    var create = cnArea;
    var areaId, findUrl;
    var allUrl = '/area/find_areaAll';
    $('.adcodeList.mt20').empty();
    if (create) {
        $('#areaName').val('').removeAttr('data-id');
        showCode = [{}, {}, {}];
        $('.adcodeList.mt20').empty();
        app.gLyr.clear();
    } else {
        findUrl = '/area/get_areaList';
        areaId = button.parents('.areaTitle_con').attr('id');
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
        $('#areaName').val(button.parents('.areaTitle_con').find('b').html()).attr('data-id', areaId);
    }
    cnArea = false;

    ajaxPost(allUrl, {
        scenarinoId: qjMsg.qjId,
        userId: userId,
        areaId: areaId
    }).success(function (res) {
        setDisabled(res.data);
    });

});

/*创建/编辑区域*/
function createEditArea() {
    var url = '/area/saveorupdate_area';
    var checkUrl = '/area/check_areaname';
    var areaName = $('#areaName').val();
    if (!areaName) {
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
    };

    var pArr = [];
    for (var i in showCode[0]) {
        var proObj = {};
        proObj[i] = showCode[0][i];
        pArr.push(proObj);
    }
    obj.provinceCodes = JSON.stringify(pArr);
    var ctArr = [];
    for (var i in showCode[1]) {
        for (var ii in showCode[1][i]) {
            var cityObj = {};
            cityObj[ii] = showCode[1][i][ii];
            ctArr.push(cityObj)
        }
    }
    obj.cityCodes = JSON.stringify(ctArr);
    var crArr = [];
    for (var i in showCode[2]) {
        for (var ii in showCode[2][i]) {
            var countyObj = {};
            countyObj[ii] = showCode[2][i][ii];
            crArr.push(countyObj)
        }
    }
    obj.countyCodes = JSON.stringify(crArr);

    if (pArr.length == 0 && ctArr.length == 0 && crArr.length == 0) {
        alert("请选择范围");
        return;
    }

    if (!$('#areaName').attr('data-id')) {
        ajaxPost(checkUrl, {
            userId: userId,
            scenarinoId: qjMsg.qjId,
            areaName: areaName,
        }).success(function (res) {
            if (!res.data) {
                swal({
                    title: '名称重复!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            } else {
                $('#editArea').modal('hide');

                $('#' + $('#areaName').attr('data-id')).find('button.btn-flash').removeClass('btn-flash');
                ajaxPost(url, obj).success(function (res) {

                    var obj = {};
                    obj.areaId = res.data.areaId;
                    obj.areaName = areaName;
                    obj.timeFrame = [];
                    obj.timeItems = [{
                        planId: -1,
                        planName: '无',
                        timeId: res.data.timeId,
                        timeEndDate: qjMsg.qjEndDate,
                        timeStartDate: qjMsg.qjStartDate
                    }];
                    obj.provinceCodes = pArr;
                    obj.cityCodes = ctArr;
                    obj.countyCodes = crArr;
                    allData.push(obj);
                    showTimeline(allData);
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
            $('#' + $('#areaName').attr('data-id')).find('b').html(areaName);
            $('#editArea').modal('hide');
            app2();
            $('#' + $('#areaName').attr('data-id')).find('button.btn-flash').removeClass('btn-flash');
        })
    }
}

var cnArea = false;
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
        $('#editArea').modal('show');
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

$('#qyTime').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    if (button.length == 0) {
        areaIndex = selectedTimes.timeIndex;
        return
    }
    ;
    areaIndex = $('.areaTitle_con').index(button.parents('.areaTitle_con')) || selectedTimes.index;
});

$('#addYA').on('show.bs.modal', function (event) {


    areaIndex = selectedTimes.index;
    timeIndex = selectedTimes.indexNum;

    $(event.target).find('.modal-footer').addClass('disNone');
    $(event.target).find('.addCopyPlan').addClass('disNone');
    $(event.target).find('.addNewPlan').addClass('disNone');
    $(event.target).find('.selectAdd').removeClass('disNone');
    $(event.target).find('#copyPlan').empty();

    msg.content.areaId = allData[areaIndex].areaId;
    msg.content.areaName = allData[areaIndex].areaName;
    msg.content.timeId = allData[areaIndex].timeItems[timeIndex].timeId;
    msg.content.timeEndDate = allData[areaIndex].timeItems[timeIndex].timeEndDate;
    msg.content.timeStartDate = allData[areaIndex].timeItems[timeIndex].timeStartDate;
    msg.content.cityCodes = allData[areaIndex].cityCodes;
    msg.content.countyCodes = allData[areaIndex].countyCodes;
    msg.content.provinceCodes = allData[areaIndex].provinceCodes;
});

$('#delTime').on('show.bs.modal', function (event) {
    areaIndex = selectedTimes.index;
    timeIndex = selectedTimes.indexNum;
    $(event.target).find('.delSelect').empty();

    var redio = $('.radio.disNone').clone().removeClass('disNone');
    if (timeIndex == 0) {
        redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        redio.find('input').val('down');
    } else if (timeIndex == (allData[areaIndex].timeItems.length - 1)) {
        redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '<br />' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        redio.find('input').val('up');
    } else {
        var redio2 = $('.radio.disNone').clone().removeClass('disNone');
        redio2.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex - 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex - 1].timeEndDate));
        redio2.find('input').val('up');
        redio.find('span').html('时   段ID：' + allData[areaIndex].timeItems[timeIndex + 1].timeId + '<br />' + '开始时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeStartDate) + '&nbsp;&nbsp;&nbsp;&nbsp;' + '结束时间：' + momentDate(allData[areaIndex].timeItems[timeIndex + 1].timeEndDate));
        redio.find('input').val('down');
        $(event.target).find('.delSelect').append(redio2);
    }
    $(event.target).find('.delSelect').append(redio);
    redio.find('input').attr('checked', 'checked');
});

/*zTree相关方法*/
/*选择节点，控制勾选状态*/
function selectNode(node) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var parNode = node.getParentNode();

    if (parNode) {
        treeObj.checkNode(parNode, true, false, false);
        var child = parNode.children;
        for (var c = 0; c < child.length; c++) {
            if (!child[c].checked) {
                treeObj.checkNode(parNode, false, false, false);
                if (parNode.getParentNode()) {
                    treeObj.checkNode(parNode.getParentNode(), false, false, false);
                }
                break;
            }
        }
        if (parNode.checked) {
            var parparNode = parNode.getParentNode();
            if (parparNode) {
                treeObj.checkNode(parparNode, true, false, false);
                var proChild = parparNode.children;
                for (var c = 0; c < proChild.length; c++) {
                    if (!proChild[c].checked) {
                        treeObj.checkNode(parparNode, false, false, false);
                        break;
                    }
                }
            }
        }
    }
}

/*以下几个是点击tree树节点时，对code进行判断及操作*/
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

/*前端设置disabled*/
function setDisabled(data) {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    for (var i = 0; i < data.length; i++) {
        var name = '(' + data[i].areaName + ')';
        for (var pro = 0; pro < data[i].provinceCodes.length; pro++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].provinceCodes[pro]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }

        for (var ci = 0; ci < data[i].cityCodes.length; ci++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].cityCodes[ci]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }

        for (var ct = 0; ct < data[i].countyCodes.length; ct++) {
            var node = treeObj.getNodeByParam("adcode", Object.keys(data[i].countyCodes[ct]), null);
            node.name += name;
            //node.chkDisabled = true;
            treeObj.setChkDisabled(node, true, false, true);
            treeObj.updateNode(node);
        }
    }
}

var selectCopyQJ, statusRW = '';
/*初始化复制情景table*/
function initCoptTable() {
    $('#copyQJ').bootstrapTable({
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
        queryParams: function formPm(m) {
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
    });
}

/*筛选*/
function statusRWfun(status, t) {
    statusRW = status;
    search('rw');
}

/*搜索事件*/
function search() {
    var params = $('#copyQJ').bootstrapTable('getOptions');
    params.queryParams = function (params) {
        var json;
        json = {
            "token": "",
            "data": {
                "scenarinoId": qjMsg.qjId,
                "queryName": params.searchText || '',
                "pageNum": 1,
                "pageSize": params.pageSize,
                "missionStatus": statusRW,
                "sort": '',
                "userId": userId
            }
        };
        json.data.queryName = $('.copyQjsearch').val();

        params = JSON.stringify(json);
        return params;
    };
    $('#copyQJ').bootstrapTable('refresh', params);
}

function rwType(v, row, i) {
    var type;
    switch (row.missionStatus) {
        case '2':
            type = '预评估';
            break;
        case '3':
            type = '后评估';
            break;
    }
    return type
}

/**
 * 选择复制情景时操作
 * @param t
 */
function selectCopy(t) {
    if (t) {
        initCoptTable();
        $('#selectCreateQj .selectCQJbtn').addClass('disNone');
        $('#selectCreateQj .selectCopyQj').removeClass('disNone');
        $('#selectCreateQj .modal-footer').removeClass('disNone');
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
        app2();
    }

}

/*选择创建情景时模态框上一步操作*/
function previous() {
    $('#selectCreateQj .selectCQJbtn').removeClass('disNone');
    $('#selectCreateQj .selectCopyQj').addClass('disNone');
    $('#selectCreateQj .modal-footer').addClass('disNone');
}

/**
 * 复制情景请求
 */
function subCopyQJ() {
    //console.log(selectCopyQJ);

    var copyUrl = '/scenarino/copy_Scenarino';
    ajaxPost(copyUrl, {
        scenarinoId: qjMsg.qjId,
        userId: userId,
        copyscenarinoId: selectCopyQJ.scenarinoId
    }).success(function (res) {
        if (res.status == 0) {
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

/*显示所选code及地图展示*/
function showAllCode() {
    updataCodeList();
    showMap();
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
            //console.log(res);
            if (res.status == 0) {
                if (res.data == 1) {
                    $('.jpjs').addClass('disNone');
                    $('.jpztck.disNone').removeClass('disNone').click();

                    scenarinoType(3);
                    qjMsg.scenarinoStatus = 3;
                    window.clearTimeout(jpztSetTimeOut);
                    //jpztckBtn(60000);
                } else {
                    console.log('计算异常')
                }

                /*这里控制所有禁止操作*/

            } else {
                console.log('接口异常')
            }
        })
    }
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

var jpztSetTimeOut;

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
                    findQJstatus();
                } else {
                    jpztSetTimeOut = window.setTimeout(function () {
                        jpztckBtn(t)
                    }, t)
                }
            } else if (res.data.type == 1) {
                $('#jpzt').modal('hide');
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
                $('#jpzt').modal('hide');
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

/**
 * 查看情景状态
 */
function findQJstatus() {
    var url = '/scenarino/find_Scenarino_status';
    ajaxPost(url, {
        userId: userId,
        scenarinoId: qjMsg.qjId
    }).success(function (res) {
        qjMsg.scenarinoStatus = res.data.scenarinoStatus;
        if (qjMsg.scenarinoStatus != 5) {
            findQJstatus();
        } else {
            scenarinoType(qjMsg.scenarinoStatus);
        }
    })
}

var jpztSetInterval;
$('#jpzt').on('show.bs.modal', function (event) {
    window.clearTimeout(jpztSetTimeOut);
    jpztckBtn(3000);
});

$('#jpzt').on('hidden.bs.modal', function () {
    window.clearTimeout(jpztSetTimeOut);
    jpztckBtn(60000);
});

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

var selectCopyPlan;
/*初始化复制预案table*/
function initCopyPlanTable() {
    $('#copyPlanTable').bootstrapTable({
        method: 'POST',
        url: localhttp + '/ampc/plan/copy_plan_list',
        dataType: "json",
        contentType: "application/json", // 请求远程数据的内容类型。
        toobar: '#rwToolbar',
        iconSize: "outline",
        search: false,
        searchAlign: 'right',
        height: 453,
        maintainSelected: true,
        clickToSelect: true,// 点击选中行
        pagination: false, // 在表格底部显示分页工具栏
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
        queryParams: function formPm(m) {
            var json = {
                "token": "",
                "data": {
                    "userId": userId
                }
            };

            return JSON.stringify(json);
        },
        responseHandler: function (res) {
            var data = {
                rows: res.data
            };
            return data;
        },
        queryParamsType: "undefined", // 参数格式,发送标准的RESTFul类型的参数请求
        silent: true, // 刷新事件必须设置
        onClickRow: function (row, $element) {
            $('.info').removeClass('info');
            $($element).addClass('info');
            selectCopyPlan = row;
        },
        onCheck: function (row) {
            selectCopyPlan = row;
        },
        onLoadSuccess: function (data) {
            selectCopyPlan = data.rows[0];
            //console.log(data);
            if (selectCopyPlan) {
                $('#copyPlanTable').bootstrapTable('check', 0)
            }
        }
    });
}
/*format 函数*/
function copyPlanAddTime(v, row, i) {
    return moment(row.addTime).format('YYYY-MM-DD HH:mm:ss')
}

var timeline;
$().ready(function () {
    $('#autoAdjustTime').on('change', function (event) {
        var value = $(this).val();
        $('#startTime').attr('disabled', value === 'both' || value === 'start');
        $('#endTime').attr('disabled', value === 'both' || value === 'end');
    });

    $('#guides').on('change', function (event) {
        $('#showGuidesLabel').attr('disabled', $(this).val() === 'none');
    });

    $('#zoom').on('change', function (event) {
        if (timeline) {
            timeline.timeline('zoom', $(this).val());
        }
    });

    $('#refresh')
        .on('click', showTimeline);

});

function showTimeline(data) {
//	var testData = $('#testData').val();
//	var testData = '2';
    //$.getJSON(testData, function (data) {
    //    var options = {};
    //    options.startTime = $('#startTime').val();
    //    options.endTime = $('#endTime').val();
    //    options.showArrow = $('#showArrow')[0].checked;
    //    options.timeScalePosition = $('#timeScalePosition').val();
    //    options.autoAdjustTime = $('#autoAdjustTime').val();
    //    options.smoothScroll = $('#smoothScroll').val();
    //    options.guides = $('#guides').val();
    //    options.showGuidesLabel = $('#showGuidesLabel')[0].checked;
    //    options.showSlider = $('#showSlider')[0].checked;
    //    options.zoom = $('#zoom').val();
    //    options.data = data;
    //    timeline = $('#timeline').timeline(options);
    //});

    $('.jpjs').attr('disabled', true);
    if ((qjMsg.scenarinoStatus == 1) || (qjMsg.scenarinoStatus == 2) || (qjMsg.scenarinoStatus == 5)) {
        for (var i = 0; i < data.length; i++) {
            for (var m = 0; m < data[i].timeItems.length; m++) {
                if (data[i].timeItems[m].planId != -1) {
                    $('.jpjs').removeAttr('disabled');
                    break;
                }
            }
        }
    }
    var options = {};
    //options.startTime = $('#startTime').val();
    //options.endTime = $('#endTime').val();
    options.startTime = qjMsg.qjStartDate;
    options.endTime = qjMsg.qjEndDate;
//	options.showArrow = $('#showArrow')[0].checked;
//	options.timeScalePosition = $('#timeScalePosition').val();
//	options.autoAdjustTime = $('#autoAdjustTime').val();
//	options.smoothScroll = $('#smoothScroll').val();
//	options.guides = $('#guides').val();
//	options.showGuidesLabel = $('#showGuidesLabel')[0].checked;
//	options.showSlider = $('#showSlider')[0].checked;
//	options.zoom = $('#zoom').val();
    //options.data = testData === '1' ? testdata1 : testData === '2' ? testdata2 : testdata3;
    options.data = data;
    timeline = $('#timeline').timeline(options);
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

//根据区域在地图上显示
function app2() {
    if (app.map1 == "" || app.map1 == null || app.map1 == undefined) {
        return;
    }
    if (allData != "" && allData != null && allData != undefined) {
        app.gLyr1.clear();
        var extent = new dong.Extent();
        var tthg = true;

        var query = new dong.Query();
        $.each(allData, function (k, item) {
            var symbol = new dong.SimpleFillSymbol("solid", app.outline, new dong.Color(sz_corlor[k]));
            var t1 = "", t2 = "", t3 = "";
            if (item.provinceCodes != "" && item.provinceCodes != null && item.provinceCodes != undefined) {
                if (item.provinceCodes.length > 0) {//省
                    $.each(item.provinceCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                            t1 += "'" + m + "',";
                        });
                    });
                }
            }
            if (item.cityCodes != "" && item.cityCodes != null && item.cityCodes != undefined) {//市
                if (item.cityCodes.length > 0) {
                    $.each(item.cityCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                            t2 += "'" + m + "',";
                        });
                    });
                }
            }
            if (item.countyCodes != "" && item.countyCodes != null && item.countyCodes != undefined) {//区县
                if (item.countyCodes.length > 0) {
                    $.each(item.countyCodes, function (i, vol) {
                        $.each(vol, function (m, col) {
                            t3 += "'" + m + "',";
                        });
                    });
                }
            }
            if (t1 != "") {
                query.where = "ADMINCODE IN (" + t1.substring(0, t1.length - 1) + ")";
                app.featureLayer1.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                            extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                            extent = extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, symbol));
                    }
                    app.map1.setExtent(extent.expand(1.5));
                });
            }

            if (t2 != "") {
                query.where = "ADMINCODE IN (" + t2.substring(0, t2.length - 1) + ")";
                app.featureLayer2.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                            extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                            extent = extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, symbol));
                    }
                    app.map1.setExtent(extent.expand(1.5));
                });
            }

            if (t3 != "") {
                query.where = "ADMINCODE IN (" + t3.substring(0, t3.length - 1) + ")";
                app.featureLayer3.queryFeatures(query, function (featureSet) {
                    for (var i = 0, il = featureSet.features.length; i < il; i++) {
                        var graphic = featureSet.features[i];
                        if (tthg) {
                            extent = graphic.geometry.getExtent();
                            tthg = false;
                        } else {
                            extent = extent.union(graphic.geometry.getExtent());
                        }
                        app.gLyr1.add(new dong.Graphic(graphic.geometry, symbol));
                    }
                    app.map1.setExtent(extent.expand(1.5));
                });
            }

        });
    }
}
