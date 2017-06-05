/**
 * Created by shanhaichushi on 2017/5/21.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><a href="#/yqd_v3" style="padding-left: 15px;padding-right: 15px;font-size:14px;color:#333;text-decoration: none" >耦合清单</a><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatCoupQd()">新建</button><button class="qdEdit" onclick="editCoupQd()">编辑</button><button class="qdDelet" onclick="coupDelete()">删除</button></span>');
var coupingQd,checkQgQd,localQd;
$(".coupSet").layout();// 耦合设置面板
function innitdata(){  //全国清单的初始化
    $("#couqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/doPost", //请求数据
        dataType: "json",
        columns:[[  //表头
            {field:"ck",checkbox:true},
            {field:"esCouplingName",title:"清单名称",width:100,align:'cneter'},
            {field:"esCouplingDesc",title:"清单描述",width:400,align:'cneter'},
            {field:"publishTime",title:"本地清单",width:100},
            {field:"nationRemark",title:"全国清单",width:100},
            {field:"esCouplingYear",title:"年份",width:80},
            {field:"addTime",title:"创建时间",formatter:function(value,row,index){
                return  moment(value).format("YYYY-MM-DD");
            },align:'cneter',width:80},
            {field:"historyCoupling",title:"使用历史",width:100},
            {field:"coupingSet",title:"配置",formatter:function(value,row,index){
                var coupId=row.esCouplingId
                var coupSetBtn="<button style='cursor:pointer;width:76px;height:20px;background-color: #febb00;border:1px solid #cd8c00;color: white;border-radius:2px;box-sizing:border-box' onclick='coupSetQd("+coupId+")'>耦合配置</button>"
                return  coupSetBtn;
            },align:'cneter',width:120}

        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data.data.rows;
        },
        selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        singleSelect: true,//设置True 将禁止多选
        checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.method="find_coupling";
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            return {"token": "", "data": data};
        },
    })
}
//全国清单生成
function qgqdTable() {
    $("#qgqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/find_nation", //请求数据
        dataType: "json",
        columns:[[  //表头
            {field:"ck",checkbox:true},
            {field:"esNationName",title:"全国清单",width:200,align:'cneter'},
            {field:"esNationYear",title:"年份",width:80,align:'cneter'},
            {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                return  moment(value).format("YYYY-MM-DD");
            },align:'cneter'},
            {field:"nationRemark",title:"备注",width:400,align:'cneter'},
            {field:"qgqdCheck",title:"状态",align:'cneter',width:100},//新建（校验按钮）   正常（校验成功） 错误（校验错误）
            //是否使用 如果使用 不许删除 未使用可以删除
            {field:"qgqdConfig",title:"操作",align:'cneter',width:100}//校验清单
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;
        },
        selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        singleSelect: true,//设置True 将禁止多选
        checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            return {"token": "", "data": data};
        },
    })
}
coupQd() //清单耦合步骤初始化
function coupQd(){
    innitdata();    //生成全国清单
    $(".coupSet").hide();
}
//打开新建清单窗口
$("#creatCoupQd").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'新建清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
//点击新建按钮 打开窗口
function creatCoupQd(){
    $("#formCoup").form("clear")
    $(".cloudui .rwCon .qdContent .qdName").val("请输入长度不超过20的名称");
    $(".cloudui .rwCon .qdContent .qdYear").val("请输入1990-2100之间的年份");
    $("#creatCoupQd").window("open");
}
//点击新建窗口提交按钮进行耦合清单创建
function submitCoup(){
    var param={};
    param.userId=userId;
    param.method="add_coupling";
    param.couplingName =$("#creatCoupQd #coupQdName").val(); //清单名字
    param.couplingYear = $("#creatCoupQd #coupQdYear").val(); //清单年份
    param.couplingDesc = $("#creatCoupQd #coupQdMark").val();//清单备注
    //判断新建清单的年份是否在1990-2100之间
    var myYear=$("#creatCoupQd #coupQdYear").val()
    if(myYear>=1990&&myYear<2100){    //判断年份
        $("#couqd").submit(
            ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                if(res.status==0){
                    $("#couqd").datagrid('insertRow',{ //在表格中插入新建清单
                        index: 0,	// 索引从0开始
                        row: {
                            esCouplingName: param.couplingName, //新建清单的名字
                            esCouplingYear: param.couplingYear,//新建清单的年份
                            esCouplingDesc:param.couplingDesc //新建清单的描述
                        }
                    })
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        )
        $("#creatCoupQd").window('close');
    }else{
        swal('清单年份获取错误', '', 'error');
    }
}
//打开编辑清单窗口
$("#editCoupQd").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'编辑清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
//编辑耦合清单
function editCoupQd(){
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editQdName,editQdYear,editMark,editId;
        editQdName=row.esCouplingName,editQdYear=row.esCouplingYear,editMark=row.esCouplingDesc,editId=row.esCouplingId;
        document.getElementById("coupEditQdName").value=editQdName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("coupEditQdYear").value=editQdYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("coupEditQdMark").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
        $("#editCoupQd").window('open');
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
//提交编辑后的清单
function submitEditCoup() {
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    var rowIndex = $('#couqd').datagrid('getRowIndex',row);//获取选中行的行数索引
    var qdName=$("#editCoupQd #coupEditQdName").val();//获取编辑后的数据 清单名称
    var qdYear=$("#editCoupQd #coupEditQdYear").val();//年份
    var qdMark=$("#editCoupQd #coupEditQdMark").val();//描述
    var param={};//设置接口参数
    param.userId=userId;
    param.couplingName=qdName;
    param.couplingId = row.esCouplingId;
    param.couplingDesc = qdMark;
    param.couplingYear = qdYear;
    param.method="update_coupling"
    var myYear=$("#editCoupQd #coupEditQdYear").val()
    if(myYear>=1990&&myYear<2100){//判断年份是否符合要求 符合提交编辑后数据
        $("#formQd").submit(
            ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                if(res.status==0){
                    $("#couqd").datagrid('updateRow',{//更新清单列表编辑后的数据
                        index: rowIndex,
                        row: {
                            esCouplingName:qdName,
                            esCouplingYear:qdYear,
                            esCouplingDesc:qdMark
                        }
                    })
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        )
        $("#editCoupQd").window('close');
    }else{
        swal('年份错误', '', 'error');
    }
}
//删除选中的耦合清单
function coupDelete(){
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    var rowIndex = $('#couqd').datagrid('getRowIndex', row);
    swal({
        title: "您确定要删除吗？",
        text: "您确定要删除这条数据？",
        type: "warning",
        animation:"slide-from-top",
        showCancelButton: true,
        closeOnConfirm: true,
        confirmButtonText: "是的，我要删除",
    }, function() {
        ajaxPost('/NativeAndNation/doPost',{"couplingId":row.esCouplingId,"method":"delete_coupling"}).success(function(res){
            if(res.status==0){
                $('#couqd').datagrid('deleteRow', rowIndex);
                $('#couqd').datagrid('reload');//删除后重新加载下
            }else{
                swal('参数错误', '', 'error');
            }
        })
    });
}
//点击耦合清单
function coupSetQd(coupId) {
    var rowsAll=$("#couqd").datagrid("getRows");
    var checkRow;
    if(coupId){
        for(var i=0;i<rowsAll.length;i++){
            if(rowsAll[i].esCouplingId==coupId){
                checkRow=rowsAll[i];
            }
        }
    }
    coupingQd=checkRow;
    $(".tableBox").hide();
    $(".coupSet").show();
    $(".coupSetCon").eq(0).show();//隐藏其他步骤
    $(".coupSetCon").eq(1).hide();
    $(".coupSetCon").eq(2).hide();
    window.setTimeout(qgqdTable(),100)
    $("#prevCoup").hide();//上一步按钮
    $(".navRight").hide();
}
function nextCoup(){//点击下一步按钮
    var conText=$(".coupSetTitle .coupSetTitleList .active div").text()
    if(conText=="第一步"){
        checkQgQd=$("#qgqd").datagrid("getSelected");
        if(checkQgQd!=''&&checkQgQd!=null&&checkQgQd!=undefined){
            $(".cloudui .coupSetTitleList").children("li").eq(0).removeClass("active");
            $(".cloudui .coupSetTitleList").children("li").eq(1).addClass("active");
            $("#prevCoup").show();//上一步按钮
            $(".coupSetCon").eq(0).hide();//隐藏其他步骤
            $(".coupSetCon").eq(1).show();
            $(".coupSetConSecond").layout()//耦合第二步进行渲染
            $(".coupSetCon").eq(2).hide();
            mbSelect();

        }else{
            swal('请先选择全国清单', '', 'error');
        }
    }else if(conText=="第二步"){
        localQd=$("#localTable").datagrid("getSelections");
        var localQdId=[];
        for(var i=0;i<localQd.length;i++){
            localQdId.push(localQd[i].esNativeId)
        }
        if(localQd!=''&&localQd!=null&&localQd!=undefined){
            $(".cloudui .coupSetTitleList").children("li").eq(1).removeClass("active");
            $(".cloudui .coupSetTitleList").children("li").eq(2).addClass("active");
            $("#prevCoup").show();//最后一步 上一步按钮
            $("#nextCoup").hide();//最后一步 隐藏下一步按钮
            $(".coupSetCon").eq(0).hide();//隐藏其他步骤
            $(".coupSetCon").eq(1).hide();
            $(".coupSetCon").eq(2).show();
            //获得选择全国清单个本地清单的ID
            ajaxPost('/NativeAndNation/doPost',{"userId":userId,"method":"findCityAndIndustryById","nationId":checkQgQd.esNationId,"nativesId":localQdId,"nativeTpId":mbArray[$(".cloudui .coupSetCon #coupSetMb").val()].esNativeTpId}).success(function (res) {
                if(res.status==0){
                    console.log(res);
                    coupCity()
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        }else{
            swal('请先选择本地清单', '', 'error');
        }
    }
}
function prevCoup(){
    var conText=$(".coupSetTitle .coupSetTitleList .active div").text()
    if(conText=="第三步"){
        $(".cloudui .coupSetTitleList").children("li").eq(2).removeClass("active");
        $(".cloudui .coupSetTitleList").children("li").eq(1).addClass("active");
        $("#prevCoup").show();//上一步按钮
        $("#nextCoup").show();//最后一步 隐藏下一步按钮
        $(".coupSetCon").eq(0).hide();//隐藏其他步骤
        $(".coupSetCon").eq(1).show();
        $(".coupSetCon").eq(2).hide();
        // $(".coupSetConSecond").layout()//耦合第二步进行渲染
        // mbSelect()

    }else if(conText=="第二步"){
        $(".cloudui .coupSetTitleList").children("li").eq(1).removeClass("active");
        $(".cloudui .coupSetTitleList").children("li").eq(0).addClass("active");
        $("#prevCoup").hide();//上一步按钮
        $("#nextCoup").show();//最后一步 隐藏下一步按钮
        $(".coupSetCon").eq(0).show();//隐藏其他步骤
        $(".coupSetCon").eq(1).hide();
        $(".coupSetCon").eq(2).hide();
    }
}

//耦合措施第二步
var mbArray
function mbSelect() {
    ajaxPost("/NativeAndNation/doPost",{"userId":userId,"method":"find_couplingNativeTp"}).success(function (res) {
        mbArray=res.data.data.rows;
        if(res.status==0){
            for(var i=0;i<mbArray.length;i++){
                var mbDiv= $('<option value="'+i+'">'+mbArray[i].esNativeTpName+'</option>');
                $(".cloudui .coupSetCon .coupSetMb").append(mbDiv);
            }
        }else{
            swal('参数故障', '', 'error')
        }
        var mbIndex=$(".cloudui .coupSetCon #coupSetMb").val();
        window.setTimeout(localTable(mbIndex),100);
    })
}
//本地模板选择变化时
$(".cloudui #coupSetMb").on("change",function (e) {
    var index=$(e.target).val();
    localTable(index);
})
//根据清单模板的ID 生成本地清单表单
function  localTable(value) {
   var mbQd= mbArray[value];
    $("#localTable").datagrid({
        url:'/ampc/NativeAndNation/doPost',
        method:'post', //ajax 请求远程数据方法
        dataType: "json",
        columns:[[  //表头
            {field:"ck",checkbox:true},
            {field:"esNativeName",title:"清单名称",width:200,align:'cneter'},
            {field:"esComment",title:"清单备注",width:80,align:'cneter'},
            {field:"esNativeYear",title:"年份"},
            {field:"addTime",title:"创建时间",formatter:function(value,row,index){
                return  moment(value).format("YYYY-MM-DD");
            },align:'cneter'},
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data.data.rows;
        },
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.method = 'find_couplingNatives';
            data.nativeTpId =mbQd.esNativeTpId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            return {"token": "", "data": data};
        }
    })
}
// 创建 输入框获得焦点
$(".cloudui .rwCon .qdContent .qdName").focus(function () {//名称获取焦点
    if($(this).val()=="请输入长度不超过20的名称"){
        $(this).val("");
        $(this).css({"color":"black"})
    }
})
$(".cloudui .rwCon .qdContent .qdYear").focus(function () {//年份获取焦点
    if($(this).val()=="请输入1990-2100之间的年份"){
        $(this).val("");
        $(this).css({"color":"black"})
    }
})

//创建 输入框失去焦点
$(".cloudui .rwCon .qdContent .qdName").blur(function () {//名称失去焦点
    if($(this).val()==""){
        $(this).val("请输入长度不超过20的名称")
        $(this).css({"color":"#a9a9a9"})
    }
})
$(".cloudui .rwCon .qdContent .qdYear").blur(function () {//年份失去焦点
    if($(this).val()==""){
        $(this).val("请输入1990-2100之间的年份")
        $(this).css({"color":"#a9a9a9"})
    }
})
//耦合第三步
function coupCity() {
    var city=["北京","天津"]
    var industry=[["钢铁","工业","火力","煤炭"],["钢铁","水力","煤炭清洗"]]
    var coupFirst="";
    for(var m=0;m<city.length;m++){
        coupFirst+="<tr><td class='cityName' rowspan=\'"+industry[m].length+"\'>"+city[m]+"</td><td class='industryName'>"+industry[m][0]+"</td><td></td></tr>"
        for(var i=1;i<industry[m].length;i++){
            var coupOther="<tr><td class='industryName'>"+industry[m][i]+"</td><td></td></tr>";
            coupFirst+=coupOther;
        }
    }
    $("#coupCityTable").append(coupFirst);
}