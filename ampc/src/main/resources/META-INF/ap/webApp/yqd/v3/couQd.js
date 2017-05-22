/**
 * Created by shanhaichushi on 2017/5/21.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">耦合清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet">删除</button></span>');
//初始化生成表格
var preams = {
    "userId": userId,
};
innitdata()
function innitdata(){
    ajaxPost('/escoupling/get_couplingInfo',preams).success(function(data){
        console.log("数据")
        console.log(data);
        $("#couqd").datagrid({
            checkOnSelect:true,
            selectOnCheck:true,
            clickToSelect: true,// 点击选中行
            data:data.data,
            columns:[[
                {field:"ck",checkbox:true},
                {field:"esNationName",title:"全国清单"},
                {field:"esNationYear",title:"年份"},
                {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                    moment(value).format("YYYY-MM-DD")
                    console.log("日期");
                    console.log( moment(value).format("YYYY-MM-DD"));
                    return  moment(value).format("YYYY-MM-DD");
                },sortable :true},
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
// 耦合设置
$(".coupSet").layout();