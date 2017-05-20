/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">全国清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet">删除</button></span>');
//全国清单
var preams = {
	"userId": userId,
    "missionId":$("#task").val()
};
// 表单生成
innitdata()
function innitdata(){
    ajaxPost('/NativeAndNation/find_nation',preams).success(function(data){
        $("#qgqd").datagrid({
            checkOnSelect:true,
            selectOnCheck:true,
            clickToSelect: true,// 点击选中行
            data:data.data,
            columns:[[
                {field:"ck",checkbox:true},
                {field:"esNationName",title:"全国清单"},
                {field:"esNationYear",title:"年份"},
                {field:"publishTime",title:"创建时间"},
                {field:"nationRemark",title:"备注"},
                {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
                //是否使用 如果使用 不许删除 未使用可以删除
                {field:"qgqdConfig",title:"操作"}//校验清单
            ]],
            pagination:'true',
            pagePosition:'bottom',
            pageNumber:1,
            pageSize:5,
            pageList:[5,10,15,20],
            fit:'true',
            pageSize:1,
            pageNumber:1,
            pageList:[1],
            sortOrder:'desc'
        });
    });
}

// $("#qgqd").datagrid({
//     url:'/ampc/NativeAndNation/find_nation',
//     method: 'POST',
//     dataType: "json",
//     columns:[[
//             {field:"ck",checkbox:true},
//             {field:"esNationName",title:"全国清单"},
//             {field:"esNationYear",title:"年份"},
//             {field:"publishTime",title:"创建时间"},
//             {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
//             //是否使用 如果使用 不许删除 未使用可以删除
//             {field:"qgqdConfig",title:"操作"}//校验清单
//         ]],
//     contentType:'application/Json',
//     pagination:'true',
//     pageNumber:1,
//     pageSize:1,
//     pageList:[1],
//     queryParams:function(params){
//         var json={
//             "token":"",
//             "data":{
//                 "userId": userId,
//                 "missionId":$("#task").val()
//                 }
//         }
//
//         return json;
//     }
// })
// 创建清单
function creatQd(){
    $("#creatQd").window('open');
}
$("#creatQd").window({
    width:600,
    collapsible:false,
    maximizable:false,
    minimizable:false,
    modal:true,
    shadow:false,
    title:'新建清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
//创建清单

function submitQd(){
    $("#formQd .easyui-validatebox").empty();
    var param={};
    param.userId=userId;
    param.nationName =$("#esNationName").val();
    param.nationYear = $("#esNationYear").val();
    param.nationRemark = $("#esNationMark").val();

    //判断年份
    var nData=new Date();
    var nYear=nData.getYear();
    var myYear=$("#esNationYear").val()
    if(myYear>nYear&&myYear<2000){
        swal('年份错误', '', 'error');
    }else{
        $("#formQd").submit(
            ajaxPost('/NativeAndNation/add_nation',param).success(function(data){
                $("#qgqd").datagrid('insertRow',{
                    index: 0,	// 索引从0开始
                    row: {
                        esNationName: param.nationName,
                        esNationYear: param.nationYear,
                        reamrk:param.nationRemark
                        // note: '新消息'
                    }
                })
            })
        )
        $("#creatQd").window('close');
    }
}
function claearQd(){
    $("#formQd").form('clear');
}

// 编辑清单
function editQd(){
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){
        var editQdName,editQdYear,editMark,editId;
        editQdName=row.esNationName,editQdYear=row.esNationYear,editMark=row.nationRemark,editId=row.esNationId;
        document.getElementById("esNationName_edit").value=editQdName;
        document.getElementById("esNationYear_edit").value=editQdYear;
        document.getElementById("esNationMark_edit").value=editMark;
        $("#editQd").window('open');
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
$("#editQd").window({
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
function editSubmitQd(){
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    console.log(row);
    // var editQdName,editQdYear,editMark,editId;
    // editQdName=row.esNationName,editQdYear=row.esNationYear,editMark=row.nationRemark,editId=row.esNationId;
    var param={};
    param.userId=userId;
    param.nationName =row.esNationName;
    param.nationId = row.esNationId;
    param.nationRemark = row.nationRemark;

    //判断年份
    var nData=new Date();
    var nYear=nData.getYear();
    var myYear=$("#esNationYear").val()
    if(myYear>nYear&&myYear<2000){
        swal('年份错误', '', 'error');
    }else{
        $("#formQd").submit(
            ajaxPost('/NativeAndNation/update_nation',param).success(function(data){
                console.log("加载成功")
                $("#qgqd").datagrid('insert',{
                    index: 0,	// 索引从0开始
                    row: {
                        esNationName: param.nationName,
                        esNationYear: param.nationYear,
                        reamrk:param.nationRemark
                        // note: '新消息'
                    }
                })
            })
        )
        $("#editQd").window('close');
    }
}