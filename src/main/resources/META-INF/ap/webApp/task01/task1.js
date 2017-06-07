var transformdata = [];//因为获取任务和获取情景是两个接口，此变量用于存储两个接口转换成一个数据的变量
var subBtn = true;//用于防止重复提交的flag
var rwSelectType = ''; //存储选中情景所对应的任务
var statusRW = '';//任务类型选择使用的变量，废弃中
var selectRW = {};//存储被选中的任务的数据
var rwStartDate;
var rwEndDate;
var basisArr, qjType;
var qjType = 0;
var __dsp = {};//用于存储请求信息，做Promise存储
var modelstatusechart = echarts.init(document.getElementById("modelstatusechart"));
var msg = {
    'id': 'qjMessage',
    'content': {
        rwId: '',
        rwName: '',
        qjId: '',
        qjName: '',
        qjStartDate: '',
        qjEndDate: '',
        esCouplingId: '',
        esCouplingName: ''
    }
};//用于存储情景信息
(function () {
    /*修改面包屑中的显示内容*/
    $("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>');
    /*生成页面底部的分页器*/
    $('#rwpagination').pagination({
        pageSize: 10,
        showPageList:false,
        onSelectPage:function(pageNumber, pageSize){
        	$('#rwgltable').treegrid({
        		queryParams:{
        			"page":pageNumber,
        			"rows":10,
        			"queryName": '',
        			"missionStatus": '',
        			"sort": '',
        			"userId": userId
        		}	
        	})
        }
    });
    /*生成相关创建任务的模态框*/
    $("#addRW").window({
        width: 600,
        collapsible: false,
        maximizable: false,
        minimizable: false,
        modal: true,
        shadow: false,
        title: '新增任务',
        border: false,
        closed: true,
        cls: 'cloudui'
    });
    /*生成相关创建情景的模态框*/
    $("#addYQJ").window({
        width: 600,
        collapsible: false,
        maximizable: false,
        minimizable: false,
        modal: true,
        shadow: false,
        title: '新增情景',
        border: false,
        closed: true,
        cls: 'cloudui'
    });
//    生成后评估的模态框
    $("#addHQJ").window({
        width: 600,
        collapsible: false,
        maximizable: false,
        minimizable: false,
        modal: true,
        shadow: false,
        title: '新增情景',
        border: false,
        closed: true,
        cls: 'cloudui'
    });
//    启动模式的模态框
    $("#startUp").window($.extend({},defaultwindowoption,{
    	title:'启动模式',
    	onOpen:function(){
    		var num = Math.round((msg.content.qjEndDate - msg.content.qjStartDate) / 1000 / 60 / 60 / 24);
    		var url = '/getCores/spentTimes';

    		  ajaxPost(url, {
    		    userId: userId,
    		    missionId: msg.content.rwId,
    		    missionType: msg.content.rwType,
    		    scenarinoType: msg.content.SCEN_TYPE
    		  }).success(function (res) {
    		    if (res.status == 0) {
    		      $('.cpu span').empty();
    		      $('.baseTime').empty();
    		      $('.allTime').empty();
    		      for (var i = 0; i < res.data.length; i++) {
    		        $('.cpu span').eq(i).html(res.data[i].cores);
    		        $('.cpu input').eq(i).val(res.data[i].cores);
    		        $('.baseTime').eq(i).html((res.data[i].avgTime - 0) * num + 'h');
    		        $('.allTime').eq(i).html((res.data[i].avgTime - 0) * num * (res.data[i].cores - 0));
    		      }
    		    } else {
    		      console.log('接口故障！！！')
    		    }
    		  }).error(function () {
    		    console.log('接口未成功发送')
    		  })
    	}
    }));
//  状态查看的窗口渲染
    $('#jpzt1').window($.extend({},defaultwindowoption,{
        title:'减排计算状态',
        onOpen:function () {
            jpztckBtn1();
        }
    }));
    /*模式运行状态查看弹框渲染*/
    $('#modelstatus').window($.extend({},defaultwindowoption,{
    	title:'模式运行状态',
    	onOpen:function(){
    		ajaxPost("/ModelExecuteStatus",{
    			missionId:msg.content.rwId,
    		   	userId:userId,
    		    scenarinoType:msg.content.SCEN_TYPE,
    		    missionType:msg.content.rwType,
    		    scenarinoId:msg.content.qjId
    			/*测试数据*/
/*    			    "missionId":353,
    			   	"userId":1,
    			    "scenarinoType":4,
    			    "missionType":1,
    			    "scenarinoId":852*/
    		}).success(function(res){
    			var data=res.data;
    			console.log(data);
    			if(data.execModel==1){
    				moduleSimulationScheduleHorizontal(data.startTime, data.endTime, data.stopTime, data.stopData, data.moduleType, data.stopMessage, data.stopType, data.excutionMessage)
    			}else if(data.execModel==2){
    				moduleSimulationScheduleVertical(data.startTime, data.endTime, data.stopTime, data.stopData, data.moduleType, data.stopMessage, data.stopType, data.excutionMessage);
    			}
    		})
    	}
    }))
    /*页面中间的任务列表部分的滚动条*/
    $("#rwgltablebox").slimScroll({
        height: '100%',
        alwaysVisible:true  //滚动条的常显
    });
    /*事件绑定*/
    /*任务列表中设置的下拉框*/
    $("#rwgltablebox").on('click', '.dropdownmenu>a', function (e) {
        $(this).siblings().show();
    });
    $("#rwgltablebox").on('mouseleave', '.dropdownmenu', function (e) {
        $(this).find('ul').hide();
    });

    var columnsRW = [[]];
    columnsRW[0].push({field: 'missionName', title: '任务名称', formatter: missionNameFormatter, width: 200});
    columnsRW[0].push({field: 'missionId', title: 'ID', formatter: missionIdFormatter, width: 80});
    columnsRW[0].push({field: 'domainName', title: '模拟范围', formatter: domainNameFormatter, width: 200});
    columnsRW[0].push({field: 'esCouplingName', title: '清单', width: 200, formatter: esCouplingNameFormatter});
    columnsRW[0].push({field: 'missionAddTime', title: '创建时间', width: 250, formatter: missionAddTimeFormatter});
    columnsRW[0].push({field: 'missionStartDate', title: '开始日期', width: 250, formatter: missionDateFormatter});
    columnsRW[0].push({field: 'missionStatus', title: '类型', width: 90, formatter: missionStatusFormatter});
    columnsRW[0].push({field: 'missionEndDate', title: '操作', width: 80, formatter: missionmanage});
    //页面布局的渲染
    $('#rwglpanle').layout();
    $('#rwgltable').treegrid({
    	url: '/ampc/new_mission/get_mission_list',
        idField: 'id',
        showHeader: false,
        checkbox: true,
        animate: true,
        border: false,
        checkOnSelect: false,
        fitColumns: true,
        rowStyler:function(obj){
        	if(typeof obj=='object'&&typeof obj.expand4!='undefined'){
        		return {class:'haserror'};
        	}
        },
        queryParams:{
        	"page":1,
        	"rows":10,
        	"queryName": '',
            "missionStatus": '',
            "sort": '',
            "userId": userId
        },
        loadFilter:function(data){
        	if(data.data.errorCount==0){
        		$('#showErrorBtn').hide();
        	}else{
        		$('#showErrorBtnNum').html(data.data.errorCount);
        		$('#showErrorBtn').show();
        	}
        	$('#rwpagination').pagination('refresh',{
        		total:data.data.total
        	});
        	return data.data;
        },
        treeField: 'missionName',
        columns: columnsRW,
        onClickRow: function (row) {
        	console.log(row);
            if (typeof row.children === 'undefined') {
                msg.content.qjName = row.scenarinoName;
                msg.content.qjId = row.scenarinoId;
                msg.content.qjStartDate = row.scenarinoStartDate;
                msg.content.qjEndDate = row.scenarinoEndDate;
                msg.content.scenarinoStatus = row.scenarinoStatus;
                msg.content.scenarinoStatuName = row.scenarinoStatuName;
                msg.content.SCEN_TYPE = row.scenType;
                storageqjmsg();
                return
            }else{
            	selectRW=row;
            }
            if ($('[node-id="' + row.id + '"]').hasClass('datagrid-row-clicked')) {
                $('[node-id="' + row.id + '"]').removeClass('datagrid-row-clicked');
                $('#rwgltable').treegrid('toggle', row.id);
            } else {
                $('[node-id="' + row.id + '"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
                $('#rwgltable').treegrid('collapseAll').treegrid('expand', row.id);
            }
            msg.content.rwId = row.missionId;
            msg.content.rwName = row.missionName;
            msg.content.rwType = row.missionStatus;
            msg.content.domainId = row.missionDomainId;
            msg.content.esCouplingId = row.esCouplingId;
            msg.content.esCouplingName = row.esCouplingName;
            msg.content.esCodeRange = row.esCodeRange.split(',');
        }
    });
    //将页面进行重新resize
    $("#rwgltableboxpanel").panel({
    	onResize:function(){
    		$('#rwgltable').treegrid('resize')
    	}
    })
    //获取清单数据
    getQD();
    /*$.ajax({
        url: '/ampc/mission/get_mission_list',
        contentType: 'application/json',
        method: 'post',
        dataType: 'JSON',
        data: JSON.stringify({
            "token": "",
            "data": {
                "queryName": '',
                "missionStatus": '',
                "pageNum": 1,
                "pageSize": 10,
                "sort": '',
                "userId": 1
            }
        }),
//      data: {
//        "token": "",
//        "data": {
//          "queryName": '',
//          "missionStatus": '',
//          "pageNum": 1,
//          "pageSize": 10,
//          "sort": '',
//          "userId": 1
//        }
//      },
        success: function (data) {
            requestQJData(data.data);
            $('#rwgltable').treegrid({
                data: transformdata,
                idField: 'id',
                showHeader: false,
                checkbox: true,
                animate: true,
                border: false,
                checkOnSelect: false,
                fitColumns: true,
                treeField: 'missionName',
                columns: columnsRW,
                onClickRow: function (row) {
                    if (typeof row.children === 'undefined') {
                        msg.content.qjName = row.scenarinoName;
                        msg.content.qjId = row.scenarinoId;
                        msg.content.qjStartDate = row.scenarinoStartDate;
                        msg.content.qjEndDate = row.scenarinoEndDate;
                        msg.content.scenarinoStatus = row.scenarinoStatus;
                        msg.content.scenarinoStatuName = row.scenarinoStatuName;
                        msg.content.SCEN_TYPE = row.SCEN_TYPE;
                        storageqjmsg();
                        return
                    }
                    if ($('[node-id="' + row.id + '"]').hasClass('datagrid-row-clicked')) {
                        $('[node-id="' + row.id + '"]').removeClass('datagrid-row-clicked');
                        $('#rwgltable').treegrid('toggle', row.id);
                    } else {
                        $('[node-id="' + row.id + '"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
                        $('#rwgltable').treegrid('collapseAll').treegrid('expand', row.id);
                    }
                    msg.content.rwId = row.missionId;
                    msg.content.rwName = row.missionName;
                    msg.content.rwType = row.missionStatus;
                    msg.content.domainId = row.missionDomainId;
                    msg.content.esCouplingId = row.esCouplingId;
                    msg.content.esCouplingName = row.esCouplingName;
                    msg.content.esCodeRange = row.esCodeRange.split(',');
                }
            });
            $('#rwpagination').pagination({total:data.data.total});
        }
    });*/
//    启动表单验证
    formVerify();
})();

function requestQJData(res) {
    transformdata=[];
    for (var i = 0; i < res.rows.length; i++) {
        transformdata.push($.extend({}, res.rows[i], {id: 'rw' + res.rows[i].missionId, state: 'closed'}));
        $.ajax({
            url: '/ampc/scenarino/get_scenarinoListBymissionId',
            contentType: 'application/json',
            method: 'post',
            async: false,
            dataType: 'JSON',
            data: JSON.stringify({
                "token": "",
                "data": {
                    "missionId": res.rows[i].missionId,
                    "queryName": '',
                    "sort": '',
                    "userId": 1
                }
            }),
            success: function (data) {
//          if(data.data.rows.length==0){
//            return;
//          }
                transformdata[transformdata.length - 1].children = [];
                transformdata[transformdata.length - 1].children.push($.extend({}, {
                    missionNameTitle: '情景名称', missionIdTitle: 'ID', domainNameTitle: '操作', esCouplingNameTitle: '管理',
                    missionAddTimeTitle: '情景状态', missionStartDateTitle: '执行日期', missionEndDateTitle: '结束日期',
                    missionStatusTitle: '类型'
                }, {id: 'qjtitle' + res.rows[i].missionId}))
                for (var j = 0; j < data.data.rows.length; j++) {
                    transformdata[transformdata.length - 1].children.push($.extend({}, data.data.rows[j], {id: 'qj' + data.data.rows[j].scenarinoId}));
                }
            }
        });
    }
}
//格式化missionName列的显示，如果此行是任务就显示为任务的名称，如果此行是虚拟标题行则显示标题，如果是慈航石情景，则显示情景名称
function missionNameFormatter(value, row, index) {
    if (typeof row.missionName === 'undefined') {
        if (typeof row.scenarinoNameTitle === 'undefined') {
        	if(row.expand4){
        		return row.scenType == 3 ? '<h3 title="创建时间：' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH:mm:ss') + '">' + row.scenarinoName + ' <i class="fa fa-exclamation-triangle" aria-hidden="true" style="color:#FF0000"></i></h3>' : '<h3  title="' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH:mm:ss') + '"><a href="#/yabj" style="text-decoration: underline">' + row.scenarinoName + ' <i class="fa fa-exclamation-triangle" aria-hidden="true" style="color:#FF0000"></i></a></h3>';
        	}else{
        		return row.scenType == 3 ? '<h3 title="创建时间：' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH:mm:ss') + '">' + row.scenarinoName + '</h3>' : '<h3  title="' + moment(row.scenarinoAddTime).format('YYYY-MM-DD HH:mm:ss') + '"><a href="#/yabj" style="text-decoration: underline">' + row.scenarinoName + '</a></h3>';
        	}
        }
        return row.scenarinoNameTitle;
    }
    if(row.missionErrorCount>0){
    	return row.missionName+' <i class="fa fa-exclamation-triangle" aria-hidden="true" style="color:#FF0000"></i>';
    }else{
    	return row.missionName;
    }
    
}
/*存储情景msg信息*/
function storageqjmsg() {
    vipspa.setMessage(msg);
}
//格式化missionID的显示，如果此行是任务就显示任务的Id，如果此行是虚拟标题行则显示标题，如果是情景则显示情景ID
function missionIdFormatter(value, row, index) {
    if (typeof row.missionId === 'undefined') {
        if (typeof row.scenarinoIdTitle === 'undefined') {
            return row.scenarinoId;
        }
        return row.scenarinoIdTitle;
    }
    return 'No:' + row.missionId
}
//格式化domainName的显示，如果此行是任务就显示任务的模拟范围，如果此行是虚拟标题行则显示标题，如果是情景则显示情景的操作按钮
function domainNameFormatter(value, row, index) {
    if (typeof row.domainName === 'undefined') {
        if (typeof row.operationTitle === 'undefined') {
            if (row.scenarinoStatus == 5) {
                return "<a href='javascript:' onclick='startBtn()' style='color: #9CC8F7'><i class='im-play2'> 启动</i></a>";
            } else if (row.scenarinoStatus == 6) {
                return "<a href='javascript:pauseBtn()'  style='color: #9CC8F7'><i class='im-pause'> 暂停</i></a>";
            } else if (row.scenarinoStatus == 7) {
                return "<a href='javascript:continueBtn()' style='color: #9CC8F7'><i class='im-play2'> 续跑</i></a>";
            }
        }
        return row.operationTitle;
    }
    return '<div class="rwdetail">模拟范围：' + row.domainName + '</div>'
}
//格式化esCouplingName的显示，如果此行是任务则显示使用的清单名称，如果此行是虚拟标题行则显示为标题，如果此行是情景则为终止按钮
function esCouplingNameFormatter(value, row, index) {
    if (typeof row.esCouplingName === 'undefined') {
        if (typeof row.adminTitle === 'undefined') {
            if (row.scenarinoStatus == 6 || row.scenarinoStatus == 7) {
                return "<a href='javascript:stopBtn()' style='color: #9CC8F7'><i class='im-stop'> 终止</i></a>"
            } else {
                return "<i class='im-stop'style='color: #ccc'> 终止</i>";
            }
        }
        return row.adminTitle;
    }
    return '<div class="rwdetail">' + row.esCouplingName + '</div>'
}
//格式化missionAddTime的显示，如果此行是任务则显示创建时间，如果是标题行则显示标题，如果是情景行则显示情景的执行状态名称
function missionAddTimeFormatter(value, row, index) {
    if (typeof row.missionAddTime === 'undefined') {
        if (typeof row.scenarinoStatusTitle === 'undefined') {
            if (row.scenarinoStatus == 3) {
                return '<a href="javascript:" class="statusType">' + row.scenarinoStatuName + '</a>'
            } else if(row.scenarinoStatus == 6){
            	return '<a href="javascript:showModelStatusWindow()" class="statusType">' + row.scenarinoStatuName + '</a>'
            }else {
            	if(row.expand4){
            		return row.scenarinoStatuName+'<span style="color:red">('+row.expand4+')</span>';
            	}else{
            		return row.scenarinoStatuName;
            	}
                
            }
        }
        return row.scenarinoStatusTitle;
    }
    return '<div class="rwdetail">创建于&nbsp;' + moment(row.missionAddTime).format('YYYY-M-D H:MM:ss') + '</div>';
}
//格式化missionDate的显示，如果此行是任务则显示任务的执行时间，如果是标题则显示标题，如果是情景则显示情景的执行时间
function missionDateFormatter(value, row, index) {
    if (typeof row.missionStartDate === 'undefined') {
        if (typeof row.runTimeTitle === 'undefined') {
            return moment(row.scenarinoStartDate).format('YYYY-MM-DD') + '&nbsp;--&nbsp;' + moment(row.scenarinoEndDate).format('YYYY-MM-DD');
        }
        return row.runTimeTitle;
    }
    return '<div class="missionstatus"><div class="rwdetail">' + moment(row.missionStartDate).format('YYYY-M-D') + '&nbsp;--&nbsp;' + moment(row.missionEndDate).format('YYYY-M-D') + '</div></div><div class="missiondetail">情景：'+row.scnum+'，正在执行：'+row.zxscnum+'</div>';
}
//  function missionStartDateFormatter(value, row, index) {
//    if (typeof row.missionStartDate=== 'undefined') {
//      if(typeof row.missionStartDateTitle ==='undefined'){
//        return moment(row.scenarinoStartDate).format('YYYY-MM-DD');
//      }
//      return row.missionStartDateTitle;
//    }
//    return '<div class="rwdetail">模拟从&nbsp;'+moment(row.missionStartDate).format('YYYY-M-D')+'</div>';
//  }
//  function missionEndDateFormatter(value, row, index) {
//    if (typeof row.missionEndDate=== 'undefined') {
//      if(typeof row.missionEndDateTitle ==='undefined'){
//        return moment(row.scenarinoEndDate).format('YYYY-MM-DD');
//      }
//      return row.missionEndDateTitle;
//    }
//    return '<div class="rwdetail">到&nbsp;'+moment(row.missionEndDate).format('YYYY-M-D')+'</div>';
//  }
//显示此行类型
function missionStatusFormatter(value, row, index) {
    if (typeof row.missionStatus === 'undefined') {
        if (typeof row.missionStatusTitle === 'undefined') {
            var type;
            switch (row.scenType) {
                case '1':
                    type = '预评估情景';
                    break;
                case '2':
                    type = '后评估情景';
                    break;
                case '3':
                    type = '新基准情景';
                    break;
            }
            return type
        }
        return row.missionStatusTitle;
    }
    var type;
    switch (row.missionStatus) {
        case '2':
            type = '预评估';
            break;
        case '3':
            type = '后评估';
            break;
    }
    return type;
//    return '<div class="missionstatus">' + type + '</div><div class="missiondetail">情景：n，正在执行：m</div>'
}
function missionmanage(value, row, index) {
    if (typeof row.missionStatus === 'undefined') {
        if (typeof row.settingTitle === 'undefined') {
            return '<div class="dropdownmenu"><a href="javascript:"><i class="fa fa-caret-down" aria-hidden="true"></i></a><ul style="display: none"><li><a href="javascript:">修改情景名</a></li><li><a href="javascript:">删除</a></li></ul></div>'
        }
        return '设置'
    }
//    return '<a href="#"><i class="fa fa-plus" aria-hidden="true" style="color: #000"></i></a>'
    return '<a href="javascript:" onclick="stop(this)" data-missionId="' + row.id + '" style="font-size: 12px">[新增]</a>'
}
function stop(t) {
    var e = e || window.event;
    e.stopPropagation();
    createQJselect($(t).attr('data-missionId'));
}
/*初始化日期插件*/
function initRwDate(s, e, start, end) {
    $('#rwDate').daterangepicker({
        singleDatePicker: false,  //显示单个日历
        timePicker: false,  //允许选择时间
        timePicker24Hour: true, //时间24小时制
        minDate: s,//最早可选日期
        maxDate: e,//最大可选日期
        locale: {
            format: "YYYY-MM-DD",
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
        "startDate": start,
        "endDate": end,
        "opens": "right"
    }, function (start, end, label) {
        rwStartDate = start.format('YYYY-MM-DD');
        rwEndDate = end.format('YYYY-MM-DD');
    })
    var d = $('#rwDate').data('daterangepicker');
    d.element.off();
}
/*按钮打开日期*/
function showRwDate() {
    var d = $('#rwDate').data('daterangepicker');
    d.toggle();
}
/*创建任务选择预评估或后评估*/
function selectType(type) {
    rwTypeV = type;
    var startDate;
    var endDate;
    var start, end;
    if (type == 'y') {
        startDate = moment().subtract(14, 'd').format('YYYY-MM-DD');
        endDate = moment().add(365, 'd').format('YYYY-MM-DD');
        start = moment().subtract(2, 'd').format('YYYY-MM-DD');
        end = moment().add(14, 'd').format('YYYY-MM-DD');
        rwSelectType = '2';
//      $('.rwTitle').html('创建预评估任务');
        $("#addRW").panel({title: '创建预评估任务'});
        //rwEndDate = moment().add(2, 'd').format('YYYY-MM-DD');
        initRwDate(startDate, endDate, start, end);
    } else if (type == 'h') {
        startDate = '2007-01-01';
        endDate = moment().subtract(2, 'd').format('YYYY-MM-DD');
        start = moment().subtract(32, 'd').format('YYYY-MM-DD');
        end = moment().subtract(2, 'd').format('YYYY-MM-DD');
        rwSelectType = '3';
        $("#addRW").panel({title: '创建后估任务'});
        //rwEndDate = moment().subtract(2, 'd').format('YYYY-MM-DD');
        initRwDate(startDate, endDate, start, end);
    }

    $('#addRW .step1').css('display', 'none');
    $('#addRW .step2').css('display', 'block');
    $("#addRW").window("center");
//    $('.createRwBtn').css('display', 'inline-block');
//    $('.return_S_rw').css('display', 'inline-block');
    //rwStartDate = moment().subtract(2, 'w').format('YYYY-MM-DD');
    rwStartDate = start;
    rwEndDate = end;
}
/*创建任务的window方法*/
function addRWWindow() {
    $("#addRW").window("open").window('center');
    $('#addRW .step1').css('display', 'flex').siblings().css('display', 'none');
}
/*新改任务创建*/
function createRw() {
    if ($('#rwForm').valid()) {
        if (!subBtn)return;
        subBtn = false;
        var url = '/mission/save_mission';
        var urlName = '/mission/check_missioname';
        var params = {};
        var paramsName = {};
        params.missionName = paramsName.missionName = $('#rwName').val();
        params.missionDomainId = $('#mnfw').val();
        params.esCouplingId = $('#qd').val();
        params.missionStartDate = moment(rwStartDate).format('YYYY-MM-DD HH:mm:ss');
        params.missionEndDate = moment(rwEndDate).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
        params.userId = paramsName.userId = userId;
        params.missionStauts = rwSelectType;
        if (rwSelectType == '2') {
            if (rwEndDate < moment().format('YYYY-MM-DD')) {
                $('.rwDateTip').show();
                subBtn = true;
                return;
            } else {
                $('.rwDateTip').hide();
            }
        }

        ajaxPost(urlName, paramsName).success(function (res) {
            if (res.data) {
                ajaxPost(url, params).success(function (res) {
                    if (res.status == 0) {
                        $("#addRW").window("close");
                        reloadTreegrid();
                        $('#rwName').val('');
                        swal({
                            title: '添加成功!',
                            type: 'success',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        $('#rwName').val('');
                        subBtn = true;
                    } else {
                        $("#addRW").window("close");
                        swal({
                            title: '添加失败!',
                            type: 'error',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        subBtn = true;
                    }
                }).error(function () {

                    $("#addRW").window("close");
                    swal({
                        title: '添加失败!',
                        type: 'error',
                        timer: 1000,
                        showConfirmButton: false
                    });
                    //swal('添加失败', '', 'error')
                    subBtn = true;
                })
            } else {
                swal({
                    title: '名称重复!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
                //swal('名称重复', '', 'error')
                subBtn = true;
            }
        }).error(function () {
            swal({
                title: '校验失败!',
                type: 'error',
                timer: 1000,
                showConfirmButton: false
            });
            //swal('校验失败', '', 'error')
            subBtn = true;
        })
    }

}
//  表单验证有关函数
function formVerify() {

    $.validator.addMethod("dateV", function (value, element) {
        var startD = value.substring(0, value.indexOf('至'));
        var endD = value.substring(value.indexOf('至') + 1);

        if (rwTypeV == 'y') {
            var s = moment(startD).isBefore(moment().add(-1, 'd'));
            var e = moment(endD).isAfter(moment());
            return s && e;
        } else {
            return true
        }
    }, "请输入正确日期范围");


    $("#rwForm").validate({
        debug: true,
        rules: {
            rwName: {
                required: true,
                maxlength: 15
            },
            rwStartDate: {
                required: true,
                dateV: true
            },
        },
        messages: {
            rwName: {
                required: '请填写任务名称',
                maxlength: '不可超过15个字符'
            },
            rwStartDate: {
                required: '请填写日期'
            },
        }

    });

    $("#yqjForm").validate({
        debug: true,
        rules: {
            yName: {
                required: true,
                maxlength: 15
            }
        },
        messages: {
            yName: {
                required: '请填写情景名称',
                maxlength: '不可超过15个字符'
            }
        }

    });

    $("#hqjForm").validate({
        debug: true,
        rules: {
            hName: {
                required: true,
                maxlength: 15
            }
        },
        messages: {
            hName: {
                required: '请填写情景名称',
                maxlength: '不可超过15个字符'
            }
        }

    });
}
/*刷新datagrid的数据*/
function reloadTreegrid() {
	$('#rwgltable').treegrid({
		queryParams:{
			"page":1,
			"rows":10,
			"queryName": $('#searchqd').searchbox('getValue'),
			"missionStatus": '',
			"sort": '',
			"userId": userId
		}	
	})
}
/*delete 函数*/
function deleteFun(type) {
    var params = {userId: userId};
    var params1 = {userId: userId};
    var delList = '', url;
    var delRWid = {}, delQJid = {};
    var data = $('#rwgltable').treegrid('getData');
    for (var i = 0; i < data.length; i++) {
        if (data[i].checkState == 'checked') {
            delRWid[data[i].missionId] = true;
        } else if (data[i].checkState == 'indeterminate') {
            for (var j = 1; j < data[i].children.length; j++) {
                if (data[i].children[j].checkState == "checked") {
                    delQJid[data[i].children[j].scenarinoId] = true;
                }
            }
        }
    }
//    if (type == 'rw') {
    for (var i in delRWid) {
        delList += i + ',';
    }
    delList = delList.substr(0, delList.length - 1);
    params.missionIds = delList;
    url = '/mission/delete_mission';
//    } else {
    delList = '';
    for (var ii in delQJid) {
        delList += ii + ',';
    }
    delList = delList.substr(0, delList.length - 1);
    params1.scenarinoIds = delList;
    url1 = '/scenarino/delete_scenarino'
//    }

    swal({
        title: "确定要删除?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        cancelButtonText: "取消",
        closeOnConfirm: false
    }, function () {


        $.when(ajaxPost(url, params), ajaxPost(url1, params1))
            .done(function () {
                swal({
                    title: '已删除!',
                    type: 'success',
                    timer: 1000,
                    showConfirmButton: false
                });
                reloadTreegrid();
            })
            .fail(function () {
                swal({
                    title: '删除失败!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            });
    })
}

/*筛选*/
function statusRWfun(t, status) {
    $(t).addClass('selected').siblings().removeClass('selected');
    $("#selectTypeBtn").find('.l-btn-text').html($(t).find('span').html());
    statusRW = status;
    search('rw');
}
/*搜索事件*/
function search(type) {
	$('#rwgltable').treegrid({
		queryParams:{
			"page":1,
			"rows":10,
			"queryName": '',
			"missionStatus": statusRW,
			"sort": '',
			"userId": userId
		}
	})
    /*var queryName = '',
        pageNum = 1,
        pageSize = 10,
        sort = '';
    ajaxPost('/mission/get_mission_list', {
        queryName: queryName, pageNum: pageNum, pageSize: pageSize, missionStatus: statusRW, sort: sort, userId: userId
    })
        .success(function (data) {
            transformdata = [];
            requestQJData(data.data);
            $('#rwgltable').treegrid({data: transformdata});
        })*/
}


/*创建情景时选择模态框*/
function createQJselect(id) {
    var oldQJUrl = '/scenarino/find_scenarino_time';
    //var oldQJUrl = 'date.json';
    var row = $('#rwgltable').treegrid('find', id);
    selectRW = $.extend({}, row);
    var oldQJparams = {
        userId: userId,
        missionId: row.missionId
    };
    //if((!__dsp['jcqj'+selectRW.missionId]) || (basisArr.length ==0)){
    __dsp['jcqj' + row.missionId] = ajaxPost(oldQJUrl, oldQJparams);
    //}

    if (row.missionStatus == "2") {
        if (moment(row.missionEndDate).isBefore(moment(), 'day')) {
            $('.disYQJ').attr('disabled', true);
        } else {
            $('.disYQJ').removeAttr('disabled');
        }
        $("#addYQJ").window('open');
        $("#addYQJ .step1").show().siblings().hide();
        $("#addYQJ").window("center");
    } else if (selectRW.missionStatus == "3") {
//      $('#createHpQjModal h4.modal-title').html('创建情景');
        __dsp['jcqj' + row.missionId].success(function (res) {
            if (res.status == 0) {
                if (res.data.length == 0) {
                    $('.cjhpgqj').eq(0).attr('disabled', true);
                    $('.cjxjzqj').eq(0).removeAttr('disabled');
                } else if (res.data[0].ScenType == '3') {
                    $('.cjxjzqj').eq(0).attr('disabled', true);
                    $('.cjhpgqj').eq(0).removeAttr('disabled');
                } else {
                    $('.cjhpgqj').eq(0).removeAttr('disabled');
                    $('.cjxjzqj').eq(0).removeAttr('disabled');
                }
            } else {
                $('.cjhpgqj').eq(0).attr('disabled', true);
                $('.cjxjzqj').eq(0).removeAttr('disabled');
            }
        })
//      $('#createHpQjModal').modal('show');
        $("#addHQJ").window('open');
        $("#addHQJ .step1").show().siblings().hide();
        $("#addHQJ").window("center");
//      returnLeft('hqj');
    }
}
/*创建情景选择类型*/
function selectQJtype(type) {

    var url = '/scenarino/find_endTime';
    //var url = 'end.json';
    var params = {
        userId: userId
    };

    __dsp['jcqj' + selectRW.missionId].then(function (res) {
        basisArr = res.data;
        if ((type == 'hj') && !basisArr) {
//        $('.hpgQJType').css('display', 'none');
//        $('.hpgQJCon').css('display', 'block');
//        $('.createQjBtn').css('display', 'inline-block');
//        $('.return_S_qj').css('display', 'inline-block');
            $('#addHQJ').find('.step1').hide().end().find('.step2').show().end().window('close').window('open');

            $('.diffNo').css('display', 'none');
            $('.spinup').css('display', 'block');
            $('#hEndDate').attr('disabled', true);
            qjType = 3;
            $('#hStartDate').empty().append($('<option value="' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '</option>'));
            $('#hEndDate').empty().append($('<option value="' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '</option>'));
        }
        if (basisArr) {

            //reverse  倒序
            switch (type) {
                case 'yy':
                    $("#addYQJ").panel({title: '创建情景（预评估情景）'});
                    $("#addYQJ .step1").hide().siblings().show();
                    $("#addYQJ").window("center");
                    $('.dbqj').css('display', 'none');
                    qjType = 1;
                    params.scenType = qjType;

                    setOption('#jcqj', basisArr);
                    var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, basisArr[0].scenarinoEndDate, !(basisArr[0].pathDate) ? moment().add(-1, 'd') : basisArr[0].pathDate);
                    $('#jcdate').empty();
                    for (var i = 0; i < dateArr.length; i++) {
                        $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
                    }
                    var startD = dateArr[0] ? moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD') : '';
                    $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));
                    ajaxPost(url, params).success(function (res) {
                        selectEndDate = res.data.endTime;
                        selectEndDate = moment(selectRW.missionEndDate).add(1, 'd').isBefore(selectEndDate) ? moment(selectRW.missionEndDate).format('YYYY-MM-DD') : selectEndDate;
                        if ($('#yStartDate').val() == '') {
                            $('#yEndDate').empty();
                            return;
                        }
                        var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
                        $('#yEndDate').empty();
                        for (var i = 0; i < endDateArr.length; i++) {
                            if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
                            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
                        }
                    }).fail(function () {
                    });

                    break;
                case 'yh':
                    $("#addYQJ").panel({title: '创建情景（后评估情景）'});
                    $("#addYQJ .step1").hide().siblings().show();
                    $("#addYQJ").window("center");
//            $('#createYpQjModal h4.modal-title').html('创建情景（后评估情景）');
//            $('.ypgQJType').css('display', 'none');
//            $('.ypgQJCon').css('display', 'block');
                    $('.dbqj').css('display', 'block');
//            $('.createQjBtn').css('display', 'inline-block');
//            $('.return_S_qj').css('display', 'inline-block');
                    $('#dbqj').attr('disabled', true);
                    $('#jcqj').removeAttr('disabled');
                    $('#jcdate').removeAttr('disabled');
                    $('#yEndDate').removeAttr('disabled');
                    qjType = 2;
                    params.scenType = qjType;

                    if (basisArr.length <= 1) {
                        $('.chengeDB').attr('disabled', true)
                    } else {
                        $('.chengeDB').removeAttr('disabled')
                    }
                    setOption('#dbqj', basisArr);
                    setOption('#jcqj', basisArr);

                    var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, basisArr[0].scenarinoEndDate, basisArr[0].pathDate);
                    dateArr = dateArr.reverse();
                    $('#jcdate').empty();
                    for (var i = 0; i < dateArr.length; i++) {
                        $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
                    }
                    var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
                    $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

                    ajaxPost(url, params).success(function (res) {
                        selectEndDate = res.data.endTime;
                        selectEndDate = moment(selectRW.missionEndDate).add(1, 'd').isBefore(selectEndDate) ? moment(selectRW.missionEndDate).format('YYYY-MM-DD') : selectEndDate;
                        var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
                        $('#yEndDate').empty();
                        for (var i = 0; i < endDateArr.length; i++) {
                            if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
                            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
                        }
                    });


                    break;
                case 'hh':
                    $('#createHpQjModal h4.modal-title').html('创建情景（后评估情景）');
//            $('.hpgQJType').css('display', 'none');
//            $('.hpgQJCon').css('display', 'block');
//            $('.createQjBtn').css('display', 'inline-block');
//            $('.return_S_qj').css('display', 'inline-block');
                    $("#addHQJ").panel({title: '创建情景（后评估情景）'});
                    $("#addHQJ .step1").hide().siblings().show();
                    $("#addHQJ").window("center");
                    $('.diffNo').css('display', 'block');
                    $('.spinup').css('display', 'none');
                    $('#dbqj1').attr('disabled', true);
                    $('#jcqj1').removeAttr('disabled');
                    $('#jcdate1').removeAttr('disabled');
                    $('#hEndDate').removeAttr('disabled');
                    qjType = 2;

                    if (basisArr.length <= 1) {
                        $('.chengeDB').attr('disabled', true)
                    } else {
                        $('.chengeDB').removeAttr('disabled')
                    }
                    setOption('#dbqj1', basisArr);
                    setOption('#jcqj1', basisArr);
                    var dateArr = setSelectDate(basisArr[0].scenarinoStartDate, moment(selectRW.missionEndDate).subtract(1, 'd').format('YYYY-MM-DD'));
                    dateArr = dateArr.reverse();
                    $('#jcdate1').empty();
                    for (var i = 0; i < dateArr.length; i++) {
                        $('#jcdate1').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
                    }
                    var startD = moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD');
                    $('#hStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));


                    var endDateArr = setSelectDate($('#hStartDate').val(), moment(selectRW.missionEndDate));
                    $('#hEndDate').empty();
                    for (var i = 0; i < endDateArr.length; i++) {
                        $('#hEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
                    }
                    break;
                case 'hj':
                    $('#createHpQjModal h4.modal-title').html('创建情景（新基准情景）');
//            $('.hpgQJType').css('display', 'none');
//            $('.hpgQJCon').css('display', 'block');
//            $('.createQjBtn').css('display', 'inline-block');
//            $('.return_S_qj').css('display', 'inline-block');
                    $('.diffNo').css('display', 'none');
                    $('.spinup').css('display', 'block');
                    $('#hEndDate').attr('disabled', true);
                    qjType = 3;
                    $('#hStartDate').empty().append($('<option value="' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '</option>'));
                    $('#hEndDate').empty().append($('<option value="' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '</option>'));
                    break;
            }
        } else {
            if (type != 'hj') {
                swal({
                    title: res.msg,
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
            }
        }
    }, function () {
        if (type == 'hj') {
//        $('.hpgQJType').css('display', 'none');
//        $('.hpgQJCon').css('display', 'block');
//        $('.createQjBtn').css('display', 'inline-block');
//        $('.return_S_qj').css('display', 'inline-block');
            $('#addHQJ').find('.step1').hide().end().find('.step2').show().end().window('close').window('open');
            $('.diffNo').css('display', 'none');
            $('.spinup').css('display', 'block');
            $('#hEndDate').attr('disabled', true);
            qjType = 3;
            $('#hStartDate').empty().append($('<option value="' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionStartDate).format('YYYY-MM-DD') + '</option>'));
            $('#hEndDate').empty().append($('<option value="' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '">' + moment(selectRW.missionEndDate).format('YYYY-MM-DD') + '</option>'));
        }
    })
}
/*配置日期*/
function setSelectDate(qjS, qjE, pathD) {
    var dateArr = [];
    if (pathD) {
        var p = moment(pathD);
        //while (!(p.isBefore(moment(qjS),'d'))) {
        while (moment(qjS).isBefore(p, 'd')) {
            dateArr.push(p.subtract(1, 'd').format('YYYY-MM-DD'));
        }
    } else {
        var e = moment(qjE);
        while (!(e.isBefore(moment(qjS), 'd'))) {
            dateArr.push(e.format('YYYY-MM-DD'));
            e.subtract(1, 'd');
        }
    }
    return dateArr
}
/*添加option*/
function setOption(ele, res) {
    $(ele).empty();
    for (var i = 0; i < res.length; i++) {
        if (((ele == '#dbqj') || (ele == '#dbqj1')) && (res[i].scenarinoId == -1))continue;
        $(ele).append($('<option value="' + i + '">' + res[i].scenarinoName + '</option>'))
    }
}

function checkedDB(t) {
    if ($(t)[0].checked) {
        $('#dbqj').removeAttr('disabled').removeClass('disabled');
        $('#jcqj').attr('disabled', true).addClass('disabled');
        $('#jcdate').attr('disabled', true).addClass('disabled');
        $('#yStartDate').attr('disabled', true).addClass('disabled');
        $('#yEndDate').attr('disabled', true).addClass('disabled');

        $('#dbqj1').removeAttr('disabled').removeClass('disabled');
        $('#jcqj1').attr('disabled', true).addClass('disabled');
        $('#jcdate1').attr('disabled', true).addClass('disabled');
        $('#hStartDate').attr('disabled', true).addClass('disabled');
        $('#hEndDate').attr('disabled', true).addClass('disabled');
    } else {
        $('#dbqj').attr('disabled', true).addClass('disabled');
        $('#jcqj').removeAttr('disabled').removeClass('disabled');
        $('#jcdate').removeAttr('disabled').removeClass('disabled');
        $('#yStartDate').removeAttr('disabled').removeClass('disabled');
        $('#yEndDate').removeAttr('disabled').removeClass('disabled');

        $('#dbqj1').attr('disabled', true).addClass('disabled');
        $('#jcqj1').removeAttr('disabled').removeClass('disabled');
        $('#jcdate1').removeAttr('disabled').removeClass('disabled');
        $('#hStartDate').removeAttr('disabled').removeClass('disabled');
        $('#hEndDate').removeAttr('disabled').removeClass('disabled');
    }
}

/*基础情景改变的时候，基础日期随之改变*/
function changeJcqj(t) {
    var index = $(t).val();
    var selectJcqj = basisArr[index];
    var pt = qjType == 1 ? (selectJcqj.pathDate ? selectJcqj.pathDate : moment().add(-1, 'd')) : selectJcqj.pathDate;
    var dateArr = setSelectDate(selectJcqj.scenarinoStartDate, selectJcqj.scenarinoEndDate, pt);
    if (qjType == 2) {
        dateArr = dateArr.reverse();
    }
    $('#jcdate').empty();
    for (var i = 0; i < dateArr.length; i++) {
        $('#jcdate').append($('<option value="' + dateArr[i] + '">' + dateArr[i] + '</option>'))
    }
    var startD = dateArr[0] ? moment(moment(dateArr[0])).add(1, 'd').format('YYYY-MM-DD') : '';
    $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

    if ($('#yStartDate').val() == '') {
        $('#yEndDate').empty();
        return;
    }
    var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
    $('#yEndDate').empty();
    for (var i = 0; i < endDateArr.length; i++) {
        if (qjType == 1) {
            if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
        } else if (qjType == 2) {
            if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
        }
    }
}
function changeJcDate(t) {
    var date = $(t).val();
    var startD = moment(moment(date)).add(1, 'd').format('YYYY-MM-DD');
    $('#yStartDate').empty().append($('<option value="' + startD + '">' + startD + '</option>'));

    var endDateArr = setSelectDate($('#yStartDate').val(), selectEndDate);
    $('#yEndDate').empty();
    for (var i = 0; i < endDateArr.length; i++) {
        if (qjType == 1) {
            if (endDateArr[i] < moment().format('YYYY-MM-DD'))continue;
            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
        } else if (qjType == 2) {
            if (endDateArr[i] < moment(startD).format('YYYY-MM-DD'))continue;
            $('#yEndDate').append($('<option value="' + endDateArr[i] + '">' + endDateArr[i] + '</option>'))
        }
    }
}
/*新改情景创建*/
function createQj(type) {
    if (!subBtn)return;
    subBtn = false;
    var url, urlName;
    var params, paramsName;
    url = '/scenarino/save_scenarino';
    urlName = '/scenarino/check_scenarinoname';
    if (type == 'ypg') {

        if ($('#yqjForm').valid()) {
            params = {};
            paramsName = {};
            params.scenarinoName = paramsName.scenarinoName = $('#yName').val();
            params.userId = paramsName.userId = userId;
            params.missionId = paramsName.missionId = selectRW.missionId;
            params.missionType = selectRW.missionStatus;
            params.scenType = qjType;
            params.scenarinoStartDate = '';
            params.scenarinoEndDate = '';
            params.basisScenarinoId = '';
            params.basisTime = '';
            params.controstScenarinoId = '';
            params.spinUp = '';

            if (qjType == 1) {
                params.scenarinoStartDate = moment($('#yStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
                params.scenarinoEndDate = moment($('#yEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
                params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
                params.basisTime = moment($('#jcdate').val()).format('YYYY-MM-DD HH:mm:ss');
            } else if (qjType == 2) {
                if ($('.dbqj input[type=checkbox]')[0].checked) {
                    params.controstScenarinoId = basisArr[$('#dbqj').val()].scenarinoId;
                } else {
                    params.scenarinoStartDate = moment($('#yStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
                    params.scenarinoEndDate = moment($('#yEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
                    params.basisScenarinoId = basisArr[$('#jcqj').val()].scenarinoId;
                    params.basisTime = moment($('#jcdate').val()).format('YYYY-MM-DD HH:mm:ss');
                }
            }

            ajaxPost(urlName, paramsName).success(function (res) {
                if (res.data) {
                    ajaxPost(url, params).success(function () {
                        reloadTreegrid();
                        subBtn = true;
                        $("#addYQJ").window('close');
                        swal({
                            title: '添加成功!',
                            type: 'success',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        $('#yName').val('');
                        //swal('添加成功', '', 'success')
                    }).error(function () {
                        swal({
                            title: '添加失败!',
                            type: 'error',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        //swal('添加失败', '', 'error')
                    })
                } else {
                    swal({
                        title: '名称重复!',
                        type: 'error',
                        timer: 1000,
                        showConfirmButton: false
                    });
                    //swal('名称重复', '', 'error')
                    subBtn = true;
                }
            }).error(function () {
                swal({
                    title: '校验失败!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
                //swal('校验失败', '', 'error')
                subBtn = true;
            })
        }


    } else {

        if ($('#hqjForm').valid()) {
            params = {};
            paramsName = {};
            params.scenarinoName = paramsName.scenarinoName = $('#hName').val();
            params.missionId = paramsName.missionId = selectRW.missionId;
            params.missionType = selectRW.missionStatus;
            params.userId = paramsName.userId = userId;
            params.scenType = qjType;
            params.scenarinoStartDate = '';
            params.scenarinoEndDate = '';
            params.basisScenarinoId = '';
            params.basisTime = '';
            params.controstScenarinoId = '';
            params.spinUp = '';
            if (qjType == 2) {
                if ($('.dbqj1 input[type=checkbox]')[0].checked) {
                    params.controstScenarinoId = basisArr[$('#dbqj1').val()].scenarinoId;
                } else {
                    params.scenarinoStartDate = moment($('#hStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
                    params.scenarinoEndDate = moment($('#hEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
                    params.basisScenarinoId = basisArr[$('#jcqj1').val()].scenarinoId;
                    params.basisTime = moment($('#jcdate1').val()).format('YYYY-MM-DD HH:mm:ss');
                }
            } else if (qjType == 3) {
                params.scenarinoStartDate = moment($('#hStartDate').val()).format('YYYY-MM-DD HH:mm:ss');
                params.scenarinoEndDate = moment($('#hEndDate').val()).add(1, 'd').subtract(1, 's').format('YYYY-MM-DD HH:mm:ss');
                params.spinUp = $('#spinup').val();
            }

            ajaxPost(urlName, paramsName).success(function (res) {
                if (res.data) {
                    ajaxPost(url, params).success(function () {
                        // initQjTable();
                        // $('#createHpQjModal').modal('hide');
                        // $('#createYpQjModal').modal('hide');
                        reloadTreegrid();
                        subBtn = true;
                        $("#addHQJ").window('close');
                        swal({
                            title: '添加成功!',
                            type: 'success',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        //swal('添加成功', '', 'success')
                        $('#hName').val('')
                    }).error(function () {
                        swal({
                            title: '添加失败!',
                            type: 'error',
                            timer: 1000,
                            showConfirmButton: false
                        });
                        //swal('添加失败', '', 'error')
                    })
                } else {
                    swal({
                        title: '名称重复!',
                        type: 'error',
                        timer: 1000,
                        showConfirmButton: false
                    });
                    //swal('名称重复', '', 'error')
                    subBtn = true;
                }
            }).error(function () {
                swal({
                    title: '校验失败!',
                    type: 'error',
                    timer: 1000,
                    showConfirmButton: false
                });
                //swal('校验失败', '', 'error')
                subBtn = true;
            })
        }

    }
    subBtn=true;
}
/*打开模式启动模态框*/
function startBtn(){
	console.log(msg);
	setTimeout(function(){
		$("#startUp").window('open').window('center');
	},1)	
}
/*选择计算核心后开始情景 */
function subStartUp() {
	  if (!subBtn)return;
	  subBtn = false;
	  var url = '/ModelType/startModel';
	  ajaxPost(url, {
	    userId: userId,
	    scenarinoId: msg.content.qjId,
	    missionId: msg.content.rwId,
	    missionType: msg.content.rwType,
	    scenarinoType: msg.content.SCEN_TYPE,
	    cores: $('input[name=cpuNum]:checked').val(),
	    bigIndex:selectRW.esCouplingId
	  }).success(function (res) {
	    if (res.status == 0) {
	    	$('#rwgltable').treegrid({
        		queryParams:{
        			"page":1,
        			"rows":10,
        			"queryName": '',
        			"missionStatus": '',
        			"sort": '',
        			"userId": userId,
        			
        		}	
        	})
	      $('#startUp').window('close');
	      swal({
	        title: '启动成功!',
	        type: 'success',
	        timer: 1000,
	        showConfirmButton: false
	      });
	    } else {
	      swal({
	        title: '启动失败!',
	        type: 'error',
	        timer: 1000,
	        showConfirmButton: false
	      });
	    }

	  }).error(function () {
	    swal({
	      title: '启动失败!',
	      type: 'error',
	      timer: 1000,
	      showConfirmButton: false
	    });
	  })
	}
/*终止情景*/
function pauseBtn(){
	var param={
		    userId:userId,
		    domainId:selectRW.missionDomainId,
		    scenarinoId:msg.content.qjId,
		    missionId:msg.content.rwId,
		    flag:1,
		    missionType:selectRW.missionStatus,
		    scenarinoType:msg.content.SCEN_TYPE
		  };
		  ajaxPost('/ModelType/sendstopModel', param).success(function(res){
		    if(res.status===0){
		      swal({
		        title: res.msg,
		        type: 'success',
		        timer: 1000,
		        showConfirmButton: false
		      });
		      $('#rwgltable').treegrid({
		  		queryParams:{
		  			"page":1,
		  			"rows":10,
		  			"queryName": '',
		  			"missionStatus": '',
		  			"sort": '',
		  			"userId": userId
		  		}	
		  	})
		    }else{
		      swal({
		        title: res.msg,
		        type: 'error',
		        timer: 1000,
		        showConfirmButton: false
		      });
		    }
		  })
  
}
/*终止情景*/
function stopBtn(){
  var param={
    userId:userId,
    domainId:selectRW.missionDomainId,
    scenarinoId:msg.content.qjId,
    missionId:msg.content.rwId,
    flag:0,
    missionType:selectRW.missionStatus,
    scenarinoType:msg.content.SCEN_TYPE
  };
  ajaxPost('/ModelType/sendstopModel', param).success(function(res){
    if(res.status===0){
      swal({
        title: res.msg,
        type: 'success',
        timer: 1000,
        showConfirmButton: false
      });
      reloadTreegrid()
    }else{
      swal({
        title: res.msg,
        type: 'error',
        timer: 1000,
        showConfirmButton: false
      });
    }
  })
}
/*续跑按钮*/
function continueBtn(){
	var param={
			userId:userId,
			missionType:selectRW.missionStatus,
			scenarinoType:msg.content.SCEN_TYPE,
			scenarinoId:msg.content.qjId,
			missionId:selectRW.missionId,
			bigIndex:selectRW.esCouplingId
	},
	url='/ModelType/continueModel';
	ajaxPost(url,params).success(function(res){
		if(res.status==0){
			swal({
		        title: res.msg,
		        type: 'success',
		        timer: 1000,
		        showConfirmButton: false
		      });
		      reloadTreegrid()
		}else{
			swal({
		        title: res.msg,
		        type: 'error',
		        timer: 1000,
		        showConfirmButton: false
		      });
		}
	})
}
/*获取清单*/
function getQD() {
    var url = '/escoupling/get_couplingInfo';
    var params = {
        userId: userId
    };
    $('#qd').empty();
    ajaxPost(url, params).success(function (res) {
        for (var i = 0; i < res.data.length; i++) {
            $('#qd').append($('<option value="' + res.data[i].esCouplingId + '">' + res.data[i].esCouplingName + '</option>'))
        }

    }).error(function () {
        console.log('清单未获取到！！！！')
    })
    //$('#qd').append($('<option value="1">jjj</option>'))
}
/*查看有运行出错的任务*/
function showErrorMission(){
	//运行出错的查看需要更换treegrid接口
	var url='new_mission/get_errormission_list';
	//存储正常查看treegrid的接口
	var url1='new_mission/get_mission_list';
	//判断是否点击过
	if($("#showErrorBtn").hasClass('active')){
		//判断为已经查看了错误的情景，此时需要返回正常查看
		$('#rwgltable').treegrid({
			url:url1,
			queryParams:{
	        	"page":1,
	        	"rows":10,
	        	"queryName": '',
	            "missionStatus": '',
	            "sort": '',
	            "userId": userId
	        }
		});
		//将按钮内容修改为查看全部
		$("#showErrorBtn .l-btn-text").html('<i class="fa fa-exclamation-triangle" aria-hidden="true"></i> 运行出错 (<span id="showErrorBtnNum">0</span>)');
		$("#showErrorBtn").removeClass('active');
		$('#selectTypeBtn').menubutton('enable');
		$('#searchqd').searchbox('enable');
		
	}else{
		$("#showErrorBtn .l-btn-text").html('<i class="fa fa-eye" aria-hidden="true"></i> 查看全部');
		$('#rwgltable').treegrid({
			url:url,
			queryParams:{
	        	"page":1,
	        	"rows":10,
	            "sort": '',
	            "userId": userId
	        }
		});		
		$("#showErrorBtn").addClass('active');
		$('#selectTypeBtn').menubutton('disable');
		$('#searchqd').searchbox('disable');
	}
	
}
/*定义一个用于绘画出逐列执行的函数
 * parameter:startTime、endTime、stopTime、stopData、xAxisData、stopMessage
 * startTime:时间戳，模拟进度开始的日期
 * endTime:时间戳，模拟进度结束的日期
 * stopTime:时间戳，模拟进的的停止时间
 * stopData:字符串，模拟进度的停止时的类型
 * xAxixData:数组，模拟进度的类型集合
 * stopMessage:字符串，模拟进度的停止信息
 * stopType:数字，停止原因的类型码
 * excutionMessage:数组，已经执行过的信息
 * */
function moduleSimulationScheduleVertical(startTime, endTime, stopTime, stopData, xAxisData, stopMessage, stopType, excutionMessage) {

//  默认的echarts配置项，并且作为返回的对象
  var _option = {},
    _temp = {},//作为一个临时存储对象
    _tempTime = moment(startTime, 'x'), //作为一个用于生成时间轴坐标的操作用时间戳
    _stopFlag = false;

  _temp.yAxislabel = []; //用于存储时间轴坐标刻度
  _temp.xAxislabel = []; //用于存储模块轴坐标刻度
  _temp.straightLineSuccess = []; //用于存储成功的直线
  _temp.straightLineFail = []; //用于存储未执行的虚线
  _temp.successPoint = [];  //模式成功的点的集合
  _temp.failPoint = [];  //未执行模式的点的集合
  _temp.stopPoint = []; //停止点的坐标
  _temp.dashLine = []; //虚线点的集合
  _temp.dashLineEndPoint = [];  //虚线末尾的箭头点集合
  _temp.straightLineSuccessArrow = []; //执行完成后实线的箭头
  _temp.straightLineFailArrow = [];  //未执行实线的箭头

//  先计算出两条坐标轴的刻度

//  计算Y轴坐标刻度
  do {
    _temp.yAxislabel.push(_tempTime.format("YYYY-M-D"));
    _tempTime.add(1, 'd');
  } while (!_tempTime.isAfter(moment(endTime, 'x'), 'day'));

//  计算X轴坐标刻度
  $.each(xAxisData, function (i, n) {
    _temp.xAxislabel.push(n)
    if (i < xAxisData.length - 1) {
      _temp.xAxislabel.push(n + "1")
    }
  });

//  计算从开始到执行停止的实线和到结束的实线
//  计算执行过的点、停止的点和结束的点
  for (var i = 0; i < _temp.xAxislabel.length; i += 2) {
    if (_stopFlag) {
      _temp.straightLineFail.push([]);
    } else {
      _temp.straightLineSuccess.push([]);
    }
    for (var j = 0; j < _temp.yAxislabel.length; j++) {
      if (moment(stopTime, 'x').isSame(moment(_temp.yAxislabel[j], "YYYY-M-D"), 'day') && _temp.xAxislabel[i] == stopData) {
        _stopFlag = true;
        _temp.straightLineSuccess[_temp.straightLineSuccess.length - 1].push([_temp.xAxislabel[i], _temp.yAxislabel[j]]);
        _temp.straightLineFail.push([]);
      }
      if (_stopFlag) {
        _temp.straightLineFail[_temp.straightLineFail.length - 1].push([_temp.xAxislabel[i], _temp.yAxislabel[j]]);
        if (_temp.stopPoint.length > 0) {
          _temp.failPoint.push([_temp.xAxislabel[i], _temp.yAxislabel[j]]);
        } else {
          _temp.stopPoint = [_temp.xAxislabel[i], _temp.yAxislabel[j]];
        }

      } else {
        _temp.straightLineSuccess[_temp.straightLineSuccess.length - 1].push([_temp.xAxislabel[i], _temp.yAxislabel[j]]);
        _temp.successPoint.push([_temp.xAxislabel[i], _temp.yAxislabel[j]]);
      }
    }
    if (_stopFlag) {
      _temp.straightLineFailArrow.push([_temp.xAxislabel[i], _temp.yAxislabel[j - 1]]);
    } else {
      _temp.straightLineSuccessArrow.push([_temp.xAxislabel[i], _temp.yAxislabel[j - 1]]);
    }

  }
  console.log(_temp.stopPoint);
//  计算表示流程进行的虚线和箭头
  for (var i = 0; i < xAxisData.length - 1; i++) {
    _temp.dashLineEndPoint.push([_temp.xAxislabel[i * 2 + 2], _temp.yAxislabel[0]]);
    _temp.dashLine.push([[_temp.xAxislabel[i * 2 + 2], _temp.yAxislabel[0]]]);
    for (var j = 0; j < _temp.yAxislabel.length; j++) {
      _temp.dashLine[i].push([_temp.xAxislabel[i * 2 + 1], _temp.yAxislabel[j]]);
    }
    _temp.dashLine[i].push([_temp.xAxislabel[i * 2], _temp.yAxislabel[j - 1]]);

  }

//  修改_option
  _option = {
    xAxis: {
      position: 'top',
      axisLine: {
        show: false
      },
      data: _temp.xAxislabel,
      axisLabel: {
        interval: function (index, value) {
          if (index % 2 == 0) {
            return true
          }
          return false
        }
      },
      boundaryGap: true,
      axisTick: {
        show: false,
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'category',
      axisLine: {
        show: false
      },
      boundaryGap: false,
      inverse: true,
      data: _temp.yAxislabel,
      axisTick: {
        show: false
      }
    },
    dataZoom: [{
      type: 'inside',
      yAxisIndex: [0],
      startValue: 0,
      endValue: 6
    }, {
      type: 'slider',
      yAxisIndex: 0,
      startValue: 0,
      endValue: 6,
    }],
    tooltip: {
      trigger: 'item'
    },
    series: (function () {
      var _seriesData = [];
      for (var i = 0; i < _temp.straightLineSuccess.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#000033'
            }
          },
          data: _temp.straightLineSuccess[i]
        })
      }
      for (var i = 0; i < _temp.straightLineFail.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#dddddd',
              type: 'solid'
            }
          },
          data: _temp.straightLineFail[i]
        })
      }
      for (var i = 0; i < _temp.dashLine.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#FFC513',
              type: 'dashed'
            }
          },
          data: _temp.dashLine[i]
        });
      }
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: 180,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [0, -7],
        itemStyle: {
          normal: {
            color: '#2F4554'
          }
        },
        coordinateSystem: 'cartesian2d',
        data: _temp.straightLineSuccessArrow
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: 180,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [0, -7],
        itemStyle: {
          normal: {
            color: '#dddddd'
          }
        },
        coordinateSystem: 'cartesian2d',
        data: _temp.straightLineFailArrow
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: -90,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [-7, 0],
        itemStyle: {
          normal: {
            color: '#FFC513'
          }
        },
        data: _temp.dashLineEndPoint,
        coordinateSystem: 'cartesian2d',
        hoverAnimation: false,
        legendHoverLink: false,
        tooltip: {
          backgroundColor: 'transparent',
          formatter: " "
        }
      });
      _seriesData.push({
        type: 'scatter',
        coordinateSystem: 'cartesian2d',
        symbolSize: 14,
        itemStyle: {
          normal: {
            color: "#00CC33",
            opacity: 1
          }
        },
        data: _temp.successPoint,
        tooltip: {
          formatter: function (obj) {
            var x = $.inArray(obj.data[0], xAxisData);
            var y = $.inArray(obj.data[1], _temp.yAxislabel);
            return excutionMessage[x][y]
          }
        }
      });
      _seriesData.push({
        type: 'scatter',
        symbol: (function () {
          return stopType == 1 ? 'triangle' : stopType == 2 ? "roundRect" : "roundRect"
        })(),
        symbolSize: 14,
        symbolRotate: 180,
        coordinateSystem: 'cartesian2d',
        itemStyle: {
          normal: {
            color: function () {
              return stopType == 1 ? '#0099FF' : stopType == 2 ? "#666699" : "#FF0000"
            },
            opacity: 1
          }
        },
        data: [_temp.stopPoint],
        tooltip: {
          formatter: function () {
            return stopMessage;
          }
        }
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'emptyCircle',
        symbolSize: 14,
        coordinateSystem: 'cartesian2d',
        itemStyle: {
          normal: {
            color: '#666666',
            opacity: 1
          }
        },
        data: _temp.failPoint,
        hoverAnimation: false,
        legendHoverLink: false,
        tooltip: {
          backgroundColor: 'transparent',
          formatter: " "
        }
      });
      return _seriesData
    })()
  };

  modelstatusechart.setOption(_option);
}
/*定义一个用于绘画出逐列执行的函数
 * parameter:startTime、endTime、stopTime、stopData、xAxisData、stopMessage
 * startTime:时间戳，模拟进度开始的日期
 * endTime:时间戳，模拟进度结束的日期
 * stopTime:时间戳，模拟进的的停止时间
 * stopData:字符串，模拟进度的停止时的类型
 * xAxixData:数组，模拟进度的类型集合
 * stopMessage:字符串，模拟进度的停止信息
 * stopType:数字，停止原因的类型码
 * excutionMessage:数组，已经执行过的信息
 * */
function moduleSimulationScheduleHorizontal(startTime, endTime, stopTime, stopData, xAxisData, stopMessage, stopType, excutionMessage) {

  //    默认的echarts配置项，并且作为返回的对象
  var _option = {},
    _temp = {},//作为一个临时存储对象
    _tempTime = moment(startTime, 'x'), //作为一个用于生成时间轴坐标的操作用时间戳
    _stopFlag = false;

  _temp.yAxislabel = []; //用于存储时间轴坐标刻度
  _temp.xAxislabel = []; //用于存储模块轴坐标刻度
  _temp.straightLineSuccess = []; //用于存储成功的直线
  _temp.straightLineFail = []; //用于存储未执行的虚线
  _temp.successPoint = [];  //模式成功的点的集合
  _temp.failPoint = [];  //未执行模式的点的集合
  _temp.stopPoint = []; //停止点的坐标
  _temp.dashLine = []; //虚线点的集合
  _temp.dashLineEndPoint = [];  //虚线末尾的箭头点集合
  _temp.straightLineSuccessArrow = []; //执行完成后实线的箭头
  _temp.straightLineFailArrow = [];  //未执行实线的箭头

//  先计算出两条坐标轴的刻度

//  X轴坐标刻度
  _temp.xAxislabel = xAxisData;

//  计算Y轴坐标刻度
//  先计算出每一天，因为Y轴是类目项要在每两天之间增加一个虚拟天，用于绘制虚线
  do {
    _temp.yAxislabel.push(_tempTime.format("YYYY-M-D"));
    _temp.yAxislabel.push(_tempTime.format("YYYY-M-D") + " ");
    _tempTime.add(1, 'd');
  } while (_tempTime.isBefore(moment(endTime, 'x'), "day"));
  _temp.yAxislabel.push(_tempTime.format("YYYY-M-D"));

//  计算从开始到执行停止的实线和到结束的实线
//  计算执行过的点、停止的点和结束的点
  for (var i = 0; i < _temp.yAxislabel.length; i++) {
    if (_stopFlag) {
      _temp.straightLineFail.push([]);
    } else {
      _temp.straightLineSuccess.push([]);
    }

    for (var j = 0; j < _temp.xAxislabel.length; j++) {
      if (i % 2 == 0) {
//      用于计算表示流程方向的实线和箭头，执行完成的点和未执行的点，停止的点
        if (moment(_temp.yAxislabel[i], "YYYY-M-D").isSame(moment(stopTime, 'x'), 'day') && _temp.xAxislabel[j] == stopData) {
          _stopFlag = true;
          _temp.straightLineSuccess[_temp.straightLineSuccess.length - 1].push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          _temp.straightLineFail.push([]);
        }
        if (_stopFlag) {
          _temp.straightLineFail[_temp.straightLineFail.length - 1].push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          if (_temp.stopPoint.length > 0) {
            _temp.failPoint.push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          } else {
            _temp.stopPoint = [_temp.xAxislabel[j], _temp.yAxislabel[i]];
          }
        } else {
          _temp.straightLineSuccess[_temp.straightLineSuccess.length - 1].push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          _temp.successPoint.push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
        }
        if (j == _temp.xAxislabel.length - 1) {
          if (_stopFlag) {
            _temp.straightLineFailArrow.push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          } else {
            _temp.straightLineSuccessArrow.push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
          }
        }
      } else {
//      用于计算表示流程方向的虚线和箭头
        if (j == 0) {
          _temp.dashLine.push([[_temp.xAxislabel[j], _temp.yAxislabel[i + 1]]]);
          _temp.dashLineEndPoint.push([_temp.xAxislabel[j], _temp.yAxislabel[i + 1]]);
        }
        _temp.dashLine[_temp.dashLine.length - 1].push([_temp.xAxislabel[j], _temp.yAxislabel[i]]);
        if (j == _temp.xAxislabel.length - 1) {
          _temp.dashLine[_temp.dashLine.length - 1].push([_temp.xAxislabel[j], _temp.yAxislabel[i - 1]]);
        }
      }

    }


  }

//  修改_option
  _option = {
    xAxis: {
      position: 'top',
      axisLine: {
        show: false
      },
      data: _temp.xAxislabel,
      boundaryGap: true,
      axisTick: {
        show: false,
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'category',
      axisLine: {
        show: false
      },
      boundaryGap: false,
      inverse: true,
      data: _temp.yAxislabel,
      axisLabel: {
        interval: function (index, value) {
          if (index % 2 == 0) {
            return true
          }
          return false
        }
      },
      axisTick: {
        show: false
      }
    },
    dataZoom: [{
      type: 'inside',
      yAxisIndex: [0],
      startValue: 0,
      endValue: 6
    }, {
      type: 'slider',
      yAxisIndex: 0,
      startValue: 0,
      endValue: 6,
    }],
    tooltip: {
      trigger: 'item'
    },
    series: (function () {
      var _seriesData = [];
      for (var i = 0; i < _temp.straightLineSuccess.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#000033',
              width:10
            }
          },
          data: _temp.straightLineSuccess[i]
        })
      }
      for (var i = 0; i < _temp.straightLineFail.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#dddddd',
              type: 'solid'
            }
          },
          data: _temp.straightLineFail[i]
        })
      }
      for (var i = 0; i < _temp.dashLine.length; i++) {
        _seriesData.push({
          type: 'line',
          symbol: 'none',
          lineStyle: {
            normal: {
              color: '#FFC513',
              type: 'dashed'
            }
          },
          data: _temp.dashLine[i]
        });
      }
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: -90,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [-7, 0],
        itemStyle: {
          normal: {
            color: '#2F4554'
          }
        },
        coordinateSystem: 'cartesian2d',
        data: _temp.straightLineSuccessArrow
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: -90,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [-7, 0],
        itemStyle: {
          normal: {
            color: '#dddddd'
          }
        },
        coordinateSystem: 'cartesian2d',
        data: _temp.straightLineFailArrow
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'arrow',
        symbolRotate: 180,
        legendHoverLink: false,
        hoverAnimation: false,
        symbolOffset: [0, -7],
        itemStyle: {
          normal: {
            color: '#FFC513'
          }
        },
        data: _temp.dashLineEndPoint,
        coordinateSystem: 'cartesian2d',
        hoverAnimation: false,
        legendHoverLink: false,
        tooltip: {
          backgroundColor: 'transparent',
          formatter: " "
        }
      });
      _seriesData.push({
        type: 'scatter',
        coordinateSystem: 'cartesian2d',
        symbolSize: 14,
        itemStyle: {
          normal: {
            color: "#00CC33",
            opacity: 1
          }
        },
        data: _temp.successPoint,
        tooltip: {
          formatter: function (obj) {
            var y = $.inArray(obj.data[0], xAxisData);
            var x = $.inArray(obj.data[1], _temp.yAxislabel)/2;
            return excutionMessage[x][y]
          }
        }
      });
      _seriesData.push({
        type: 'scatter',
        symbol: (function () {
          return stopType == 1 ? 'triangle' : stopType == 2 ? "roundRect" : "roundRect"
        })(),
        symbolSize: 14,
        symbolRotate: 180,
        coordinateSystem: 'cartesian2d',
        itemStyle: {
          normal: {
            color: function () {
              return stopType == 1 ? '#0099FF' : stopType == 2 ? "#666699" : "#FF0000"
            },
            opacity: 1
          }
        },
        data: [_temp.stopPoint],
        tooltip: {
          formatter: function () {
            return stopMessage;
          }
        }
      });
      _seriesData.push({
        type: 'scatter',
        symbol: 'emptyCircle',
        symbolSize: 14,
        coordinateSystem: 'cartesian2d',
        itemStyle: {
          normal: {
            color: '#666666',
            opacity: 1
          }
        },
        data: _temp.failPoint,
        hoverAnimation: false,
        legendHoverLink: false,
        tooltip: {
          backgroundColor: 'transparent',
          formatter: " "
        }
      });
      return _seriesData
    })()
  };
  modelstatusechart.setOption(_option);
}
/*模式查看打开框函数*/
function showModelStatusWindow(){
	$("#modelstatus").window('open');
}
/*减排状态查看*/
function jpztckBtn(t) {
    var url = '/jp/areaStatusJp';
    var params = {
        bigIndex:selectRW.esCouplingId,
        scenarinoId: msg.qjId,
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

            } else if (res.data.type == 1) {
                $('#jpzt1').window('close');
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
                $('#jpzt1').window('close');
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