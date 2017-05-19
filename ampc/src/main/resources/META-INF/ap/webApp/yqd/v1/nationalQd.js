/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">全国清单</span><span class="navRight qdnavRight"><button class="qdCreat">新建</button><button class="qdEdit">编辑</button><button class="qdDelet">删除</button></span>');
//全国清单
var preams={
    "userId": userId,
    "missionId":$("#task").val()
};
ajaxPost('/NativeAndNation/add_nation',preams).success(function(data){
    $("#qgqd").datagrid({
        columns:[[
            {field:"ck",checkbox:true},
            {field:"qgqdName",title:"全国清单"},
            {field:"qgqdYear",title:"年份"},
            {field:"qgqdCreatTime",title:"创建时间"},
            {field:"qgqdCheck",title:"上传/校验"},
            {field:"qgqdConfig",title:"配置"}
        ]],
    });
})