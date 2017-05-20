/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">全国清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit">编辑</button><button class="qdDelet">删除</button></span>');
//全国清单
var preams = {
	"userId": userId,
    "missionId":$("#task").val()
};
ajaxPost('/NativeAndNation/find_nation',preams).success(function(data){
    $("#qgqd").datagrid({
        data:data.data,
        columns:[[
            {field:"ck",checkbox:true},
            {field:"esNationName",title:"全国清单"},
            {field:"esNationYear",title:"年份"},
            {field:"publishTime",title:"创建时间"},
            {field:"qgqdCheck",title:"上传/校验"},
            {field:"qgqdConfig",title:"配置"}
        ]],
        pagination:'true',
        fit:'true'
    });
});
$(".yqdTable").layout();

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
    title:'创建情景',
    border:false,
    closed:false,
    cls:"cloudui"
})
function submitQd(){
    $("#formQd").form('submit',{
        url:'/NativeAndNation/add_nation',
        success:function (data) {
            console.log("成功");
            console.log(data);
        }
        // onLoadSuccess:function(){
        //     console.log("提交成功");
    })
}
function claearQd(){
    $("#formQd").form('clear');
}