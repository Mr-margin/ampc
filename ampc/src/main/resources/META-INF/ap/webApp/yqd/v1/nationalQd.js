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
ajaxPost('/NativeAndNation/find_nation',preams).success(function(data){
    $("#qgqd").datagrid({
        data:data.data,
        columns:[[
            {field:"ck",checkbox:true},
            {field:"esNationName",title:"全国清单"},
            {field:"esNationYear",title:"年份"},
            {field:"publishTime",title:"创建时间"},
            {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
            //是否使用 如果使用 不许删除 未使用可以删除
            {field:"qgqdConfig",title:"操作"}//校验清单
        ]],
        pagination:'true',
        fit:'true',
        pageSize:1,
        pageNumber:1,
        pageList:[1]
    });
});
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
$(".yqdTable").layout();
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
function submitQd(){
    // $('#formQd').submit();
    // $("#formQd").form({
    //     url:'/NativeAndNation/add_nation',
    //     onSubmit: function(param){
    //         param.esNationName =$("#esNationName").val();
    //         param.esNationYear = $("#esNationYear").val();
    //         param.esNationMark = $("#esNationMark").val();
    //         console.log(param)
    //     },
    //     success:function (data) {
    //
    //     }
    // })

    var param={};
    param.userId=userId;
    param.nationName =$("#esNationName").val();
    param.nationYear = $("#esNationYear").val();
    // param.esNationMark = $("#esNationMark").val();

    $("#formQd").submit(
        ajaxPost('/NativeAndNation/add_nation',param).success(function(data){
            console.log(data)
        })
    )


    $("#creatQd").window('close');

}
function claearQd(){
    $("#formQd").form('clear');
}
// 编辑清单
function editQd(){
    $("#creatQd").window('open');
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