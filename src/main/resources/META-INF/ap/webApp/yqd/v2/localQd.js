/**
 * Created by shanhaichushi on 2017/5/19.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">本地清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet">删除</button></span>');

// 表单生成
innitdata()
function innitdata(){
    var preams = {
        "userId": userId,
    };
    ajaxPost('/NativeAndNation/find_natives',preams).success(function(data){
        console.log(data);
        $("#localqd").treegrid({
            idField:'id',
            treeField:'name',
            data:data.data,
            columns:[[
                {field:"ck",checkbox:true},
                {field:"esNativeName",title:"清单模板名称"},
                {field:"esUploadTime",title:"创建时间"},
                {field:"isEffective",title:"共有数据"},
                {field:"nationRemark",title:"备注"},
                {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
                //是否使用 如果使用 不许删除 未使用可以删除
                {field:"qgqdConfig",title:"操作"}//校验清单
            ]],
            pagination:'true',
            silent: true, // 刷新事件必须设置
            pageSize: 10, // 页面大小
            pageNumber: 1, // 页数
            pageList: [5, 10],
            height:'100%',
            pagePosition:'bottom',
            fit:'true',
            sortOrder:'desc',
            singleSelect:'true'
        });

    });
}