﻿/**
 * Created by lvcheng on 2017/2/21.
 */

/*全局变量*/
var totalWidth, totalDate, startDate, qjMsg;
var index, indexPar, handle, minLeft, maxLeft, selfLeft, startX, leftWidth, rightWidth;
var allData = [];//用于存储所有区域时段和预案信息，展示和数据处理
var areaIndex, timeIndex;
var showCode = [{}, {}, {}];
var proNum, cityNum, countyNum;
var msg = {//路由传递信息参数
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
};//zTree 设置option对象

function showMap() {
    addLayer(showCode);
}

/*创建编辑区域模态框中间部分显示区域名称*/
function addP(adcode, name, level) {
    return $('<p class="col-md-3"><i class="im-close" style="cursor: pointer" onclick="delAdcode(' + adcode + ',' + level + ')"></i>&nbsp;&nbsp;' + name + ' </p>')
}

/*创建编辑区域中间部分的adcode更新处理*/
function updataCodeList() {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");//获取zTree对象
    $('.adcodeList').empty();
    for (var i = 0; i < 3; i++) {
        for (var ad in showCode[i]) {
            if (i == 0) {//根据showCode的数据结构，分为两部分进行处理i==0为省级
                $('.adcodeList').append(addP(ad, showCode[i][ad], i))//将设着好的地区名称标签添加至模态框中间显示部分
            } else {
                for (var add in showCode[i][ad]) {
                    $('.adcodeList').append(addP(add, showCode[i][ad][add], i))//将设着好的地区名称标签添加至模态框中间显示部分
                }
            }

        }
    }

    proNum = treeObj.getNodesByFilter(function (node) {
        return (node.checked && (node.level == 0))
    }).length;//获取已选省数量
    cityNum = treeObj.getNodesByFilter(function (node) {
        return (node.checked && (node.level == 1))
    }).length;//获取已选市数量
    countyNum = treeObj.getNodesByFilter(function (node) {
        return (node.checked && (node.level == 2))
    }).length;//获取已选县数量

    //将三个数进行展示（暂未使用，标签display：none）
    $('.proNumber span').html(proNum);
    $('.cityNumber span').html(cityNum);
    $('.countyNumber span').html(countyNum);

}

/*删除所选地区*/
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

/*删除所有所选地区*/
function clearAllArea() {
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    treeObj.checkAllNodes(false);
    showCode = [{}, {}, {}];
    addLayer(showCode);
    updataCodeList();
}


/**
 * 情景计算状态
 * 情景在不同状态下减排计算、状态查看、减排分析三个按钮的功能操作
 * 1：创建中，2：编辑中，3：减排计算中，4：减排计算失败，5：可执行，6：模式执行中，7：暂停，8：模式执行完毕，9,10暂且不用
 * @param typeNum 情景状态
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

/*执行初始化函数*/
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
        ajaxPost(url, {
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
    $('.qyCon .nowRw span').html(qjMsg.rwName);
    $('.qyCon .nowQj span').html(qjMsg.qjName);
    $('.qyCon .seDate span').html(moment(qjMsg.qjStartDate).format('YYYY-MM-DD') + '至' + moment(qjMsg.qjEndDate).format('YYYY-MM-DD'));
    //向路由对象中添加数据
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

    /*请求所有区域及时段预案信息*/
    var url = '/area/get_areaAndTimeList';
    var scenarino = ajaxPost(url, {
        scenarinoId: qjMsg.qjId,
        userId: userId
    });
    //initDate();

    scenarino.then(function (res) {

        /*使用临时变量存储，后续有可能因为复制情景不使用此数据*/
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
        //判断isNew参数，确定当前情景是不是新创建的，用以判断是否是继续新建还是复制情景
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
        showTimeline(allData);
    });
}


/*删除区域*/
function delArea(e) {
    //当前区域的索引位置，后续需要根据此索引位置到allData中查找数据
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

                    //删除区域之后，判断剩余区域是否还有预案，判断减排计算等按钮的使用与否
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
        }
    })
}

/**
 * 添加时间段
 */
function addTimes() {
    addTimePoint = $('#qyTimePoint').val();
    var timePoint = moment(addTimePoint).format('YYYY-MM-DD HH:mm:ss');
    //通过areaIndex确定我是编辑的哪一个区域中的
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

        //重新请求一遍区域时段和预案信息
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

var selectedTimes;
/**
 * 当前选中的时段
 * 点击某时段，拿到该时段上保存的信息
 * @param data 时段上保存的信息
 */
function ontTimes(data) {
    selectedTimes = data;
    console.log(selectedTimes);

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
/**
 * timePlan 打开时需的准备工作
 */
$('#timePlan').on('show.bs.modal', function (event) {

    $('#time .active').removeClass('active');
    $('#plan .active').removeClass('active');
    $('.addTimeLi').addClass('active');
    $('.addTimeDiv').addClass('active');
    //拿到区域和时段的索引
    areaIndex = selectedTimes.index;//区域索引
    timeIndex = selectedTimes.indexNum;//时段索引
    var timeStart = moment(selectedTimes.startTime);//时段开始时间
    var timeEnd = moment(selectedTimes.endTime);//时段结束时间
    /*最小间隔一小时*/
    var timeEnd1 = moment(selectedTimes.endTime).add(-1, 'h');
    //向路由对象中存放数据
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

    /*打开模态框后，需要展示的前一时段，当前时段，后一时段*/
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

/*时段预案操作模态框选择 end*/
var newPlan;
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

    //点击编辑预案后需要跳转页面，此时向路由中存放数据
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

/**
 * 编辑时段时间使用
 * 用于保存前一时段后一时段和当前时段的时间
 * @type {{}}
 */
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

/**
 * 编辑时段时间html （更新使用）
 * 时段编辑时显示的前一时段后一时段当前时段的信息
 */
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

    //此处执行两遍不是错误，只执行一遍会出问题
    initEditTimeDate(s, e);
    initEditTimeDate(s, e);
}

/**
 * 编辑时段时间html （编辑某一时段使用）
 * 时段编辑时显示的前一时段后一时段当前时段的信息
 */
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
 * 区域编辑模态框打开时处理
 */
$('#editArea').on('show.bs.modal', function (event) {
    //初始化Tree树数据
    $.fn.zTree.init($("#adcodeTree"), zTreeSetting, zTreeData);
    var treeObj = $.fn.zTree.getZTreeObj("adcodeTree");
    var nodes = treeObj.getNodesByParam("level", 1);//选择所有市级节点，0：省，1：市，2：县
    //var a = ['11','12','1301'];
    var code = qjMsg.esCodeRange;//需要展示到县级的行政区划
    for (var i = 0; i < nodes.length; i++) {
        var adcode = nodes[i].adcode;
        if ((code.indexOf(adcode.substr(0, 2)) == -1) && (code.indexOf(adcode.substr(0, 4)) == -1)) {
            treeObj.hideNodes(nodes[i].children);//如果不需要展示到县级，则隐藏县级
        }
    }

    var button = $(event.relatedTarget);
    //确定点击的某个区域的索引
    areaIndex = $('.areaTitle_con').index($(button).parents('.areaTitle_con'));
    //var create = button.data('new');
    //判断是创建还是编辑
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
                setShowCode(res.data);
                addLayer(showCode);
            }
            updataCodeList();
        });
        $('#areaName').val(button.parents('.areaTitle_con').find('b').html()).attr('data-id', areaId);
    }
    cnArea = false;

    //请求区域地区信息，进行disabled处理
    ajaxPost(allUrl, {
        scenarinoId: qjMsg.qjId,
        userId: userId,
        areaId: areaId
    }).success(function (res) {
        setDisabled(res.data);
    });

});

/**
 * 创建/编辑区域
 */
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
 * 前端设置disabled
 * @param data
 */
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
 * 选择复制情景或者新建情景
 * @param t true：复制情景，false：使用新建情景
 */
function selectCopy(t) {
    if (t) {
        initCoptTable();
        $('#selectCreateQj .selectCQJbtn').addClass('disNone');
        $('#selectCreateQj .selectCopyQj').removeClass('disNone');
        $('#selectCreateQj .modal-footer').removeClass('disNone');
    } else {
        allData = allData1;//当确定使用新建情景时，将临时变量值传递给allData
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

function previous() {
    $('#selectCreateQj .selectCQJbtn').removeClass('disNone');
    $('#selectCreateQj .selectCopyQj').addClass('disNone');
    $('#selectCreateQj .modal-footer').addClass('disNone');
}

/**
 * 选择copy情景，处理copy之后的事件
 */
function subCopyQJ() {
    console.log(selectCopyQJ);

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
                    //jpztckBtn(60000);
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

/**
 * 减排状态打开关闭时处理
 */
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
        url: '/ampc/plan/copy_plan_list',
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
            console.log(data);
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
