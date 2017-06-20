/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">全国清单</span><span class="navRight qdnavRight qdnavRightNation"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet" onclick="delectQd()">删除</button></span>');

//普通用户无法看见全国清单 管理员可以看见全国清单
if(userId==2){
    $(".qdnavRightNation").css({"display":"none"})
}

innitdata()//全国清单表单初始化
function innitdata(){
    $("#qgqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/find_nation", //请求数据
        dataType: "json",
        columns:[[  //表头
                    // {field:"ck",checkbox:true},
                    {field:"esNationName",title:"全国清单",width:200,align:'cneter'},
                    {field:"esNationYear",title:"年份",width:80,align:'cneter'},
                    {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                        var val=moment(value).format('YYYY-MM-DD HH:mm:ss');
                        return "<div title=\'"+val+"\'>"+val+"</div>";
                    },align:'cneter'},
                    {field:"nationRemark",title:"备注",width:400,align:'cneter'},
                    {field:"qgqdCheck",title:"状态",align:'cneter',width:100},//新建（校验按钮）   正常（校验成功） 错误（校验错误）
                    //是否使用 如果使用 不许删除 未使用可以删除
                    {field:"qgqdConfig",title:"操作",align:'cneter',width:100}//校验清单
                ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;
        },
        toolbar: '#searchTool',
        // selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        singleSelect: true,//设置True 将禁止多选
        // checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        // fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        // clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        // silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            data.queryName=$("#companyname").val();
            return {"token": "", "data": data};
        },
        onClickRow:function (index,row) {
            //用于作为单选行的操作，当点击一行后，其他行取消选中，在datagrid中需要把singleSelect取消
            var rowNum=$(this).datagrid('getRows').length;
            for(var i=0;i<rowNum;i++){
                if(i!=index){
                    $(this).datagrid('uncheckRow',i)
                }
            }
        }
    })
}
function creatQd(){ // 点击创建清单按钮 弹出创建窗口
    $("#formQd").form('clear');//打开窗口清空输入框数据
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("请输入1990-2100之间的年份").css({"color":"#757575"});
    // $("#creatQd .tipYearRepeat span").remove();
    // $("#creatQd .tipNameRepeat span").remove();
    var labelDiv=$("#creatQd label");
    for(var i=0;i<labelDiv.length;i++){
        if($("#creatQd label").eq(i).attr("id")){
            $("#creatQd label").eq(i).remove();
        }
    }
    $("#creatQd").window('open');
}
$("#creatQd").window({  //创建全国清单窗口
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
function submitQd(){ //点击提交按钮进行新建清单数据的提交
    var param={};
    param.userId=userId;
    param.nationName =$("#creatQd #esNationName").val(); //清单名字
    param.nationYear = $("#creatQd #esNationYear").val(); //清单年份
    param.nationRemark = $("#creatQd #esNationMark").val();//清单备注
    //判断新建清单的年份是否在1990-2100之间
    var myYear=$("#creatQd #esNationYear").val()
    var myName=$("#creatQd #esNationName").val()
    if(myName.length>0 && myName.length<=20&&myName!="不可超过15个字符（必填）"){
        if(myYear>=1990&&myYear<=2100){    //判断年份
            $("#formQd").submit(
                ajaxPost('/NativeAndNation/add_nation',param).success(function(res){
                    if(res.status==0){
                        $("#qgqd").datagrid('insertRow',{ //在表格中插入新建清单
                            index: 0,	// 索引从0开始
                            row: {
                                esNationName: param.nationName, //新建清单的名字
                                esNationYear: param.nationYear,//新建清单的年份
                                nationRemark:param.nationRemark //新建清单的备注
                            }
                        });
                        innitdata()
                    }else{
                        swal('参数错误', '', 'error');
                    }

                })
            )
            $("#creatQd").window('close');
        }else{
            swal('清单年份获取错误', '', 'error');
            // $("#creatQd .tipYearRepeat span").remove();
            // $("#creatQd .tipYearRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的年份</span>");
        }
    }else{
        swal('请输入符合要求的名称', '', 'error');
        // $("#creatQd .tipNameRepeat span").remove();
        // $("#creatQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的名称</span>");
    }

}
function claearQd(){ //点击窗口清空按钮 对表单输入框进行数据清空
    $("#formQd").form('clear');
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("请输入1990-2100之间的年份").css({"color":"#757575"});
}

function editQd(){ // 编辑全国清单
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editQdName,editQdYear,editMark,editId;
        editQdName=row.esNationName,editQdYear=row.esNationYear,editId=row.esNationId;
        if(Boolean(row.nationRemark!=undefined)){
            editMark=row.nationRemark;
        }else{
            editMark="";
        }
        document.getElementById("esNationName_edit").value=editQdName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("esNationYear_edit").value=editQdYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("esNationMark_edit").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
        $("#editQd input").css({"color":"black"})
        // $("#editQd .tipYearRepeat span").remove();
        // $("#editQd .tipNameRepeat span").remove();
        var labelDiv=$("#editQd label");
        for(var i=0;i<labelDiv.length;i++){
            if($("#editQd label").eq(i).attr("id")){
                $("#editQd label").eq(i).remove();
            }
        }
        $("#editQd").window('open');
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
$("#editQd").window({ //编辑清单窗口
    width:600,
    collapsible:false,
    maximizable:false,
    minimizable:false,
    modal:true,
    shadow:false,
    title:'编辑清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
function editSubmitQd(){//点击提交按钮进行编辑数据提交
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    var rowIndex = $('#qgqd').datagrid('getRowIndex',row);//获取选中行的行数索引
    var qdName=$("#editQd #esNationName_edit").val();//获取编辑后的数据 清单名称
    var qdYear=$("#editQd #esNationYear_edit").val();//年份
    var qdMark=$("#editQd #esNationMark_edit").val();//备注
    var param={};//设置接口参数
    param.userId=userId;
    param.nationName=qdName;
    param.nationId = row.esNationId;
    param.nationRemark = qdMark;
    param.nationYear = qdYear;
    var myYear=$("#esNationYear_edit").val();
    var myName=$("#editQd #esNationName_edit").val()
    if(myName.length>0 && myName.length<=20){
        if(myYear>=1990&&myYear<=2100&&myName!="不可超过15个字符（必填）"){//判断年份是否符合要求 符合提交编辑后数据
            $("#formQd").submit(
                ajaxPost('/NativeAndNation/update_nation',param).success(function(res){
                    if(res.status==0){
                        $("#qgqd").datagrid('updateRow',{//更新清单列表编辑后的数据
                            index: rowIndex,
                            row: {
                                esNationName: qdName,
                                esNationYear:qdYear,
                                nationRemark:qdMark
                            }

                        })
                        innitdata()
                    }else{
                        swal('参数错误', '', 'error');
                    }
                })
            )
            $("#editQd").window('close');
        }else{
            swal('清单年份获取错误', '', 'error');
            // $("#editQd .tipYearRepeat span").remove();
            // $("#editQd .tipYearRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的年份</span>");
        }
    }else{
        swal('请输入符合要求的名称', '', 'error');
        // $("#editQd .tipNameRepeat span").remove();
        // $("#editQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的名称</span>");
    }

}
// 删除清单
function delectQd(){
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    swal({
        title: "确定要删除?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        cancelButtonText: "取消",
        closeOnConfirm: false
    }, function() {
        ajaxPost('/NativeAndNation/delete_nation',{"nationId":row.esNationId}).success(function(res){
            if(res.status==0){
                var rowIndex = $('#qgqd').datagrid('getRowIndex', row);
                $('#qgqd').datagrid('deleteRow', rowIndex);
                $('#qgqd').datagrid('reload');//删除后重新加载下
                swal({
                    title: '已删除!',
                    type: 'success',
                    timer: 1000,
                    showConfirmButton: false
                });
            }else{
                swal('参数错误', '', 'error');
            }
        })
    });
}
// 创建 输入框获得焦点
$(".cloudui .rwCon .qdContent .qdName").focus(function () {//名称获取焦点
    $(".tipRepeat span").remove();
    if($(this).val()=="不可超过15个字符（必填）"){
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
        $(this).val("不可超过15个字符（必填）")
        $(this).css({"color":"#757575"})
    }
})
$(".cloudui .rwCon .qdContent .qdYear").blur(function () {//年份失去焦点
    if($(this).val()==""){
        $(this).val("请输入1990-2100之间的年份")
        $(this).css({"color":"#757575"})
    }
})

//校验名字是否冲突
$("#creatQd #esNationName").blur(
    function () {
        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":'verifyByNationName',
            "nationName":$("#creatQd #esNationName").val()
        }).success(function (res) {
            if(res.data.data.msg==true){
                // $("#creatQd .tipNameRepeat span").remove();
                // $("#creatQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>该名称已被使用</span>");
                swal('清单名称重复，请再次输入名称', '', 'error');
            }
        })
    }
)


    $("#creatQd #formQd").validate({
        rules: {
            esNationName: {
                required: true,
                maxlength: 15,
                minlength: 1
            },
            esNationYear: {
                number:true,
                range:[1990,2100]
            },
        },
        messages: {
            esNationName: {
                required: '请填写清单名称',
                maxlength: '不可超过15个字符'
            },
            esNationYear: {
                number: '请填写年份',
            },
        }
    });
    $("#editQd #editQdForm").validate({
        rules: {
            esNationName: {
                required: true,
                maxlength: 15,
                minlength: 1
            },
            esNationYear: {
                number:true,
                range:[1990,2100]
            },
        },
        messages: {
            esNationName: {
                required: '请填写清单名称',
                maxlength: '不可超过15个字符'
            },
            esNationYear: {
                number: '请填写年份',
            },
        }

    });

