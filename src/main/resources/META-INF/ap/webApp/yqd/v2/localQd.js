/**
 * Created by shanhaichushi on 2017/5/19.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">本地清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatTemp()">新建</button><button class="qdEdit" onclick="editTemp()">编辑</button><button class="qdDelet" onclick="delectTemp()">删除</button></span>');

// 表单生成
innitdata()
function innitdata(){
    ajaxPost('/NativeAndNation/find_natives',{
        "userId":userId
    }).success(function(data){
        console.log("数据加载成功了")
        $("#localqd").treegrid({

        })
    })
    // $("#localqd").treegrid({
    //     method:'post',
    //     url: "/ampc/NativeAndNation/find_nation",
    //     dataType: "json",
    //     columns:[[  //表头
    //         {field:"ck",checkbox:true},
    //         {field:"esNationName",title:"全国清单"},
    //         {field:"esNationYear",title:"年份"},
    //         {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
    //             moment(value).format("YYYY-MM-DD")//格式化带日期格式
    //             return  moment(value).format("YYYY-MM-DD");
    //         },sortable :true},
    //         {field:"nationRemark",title:"备注"},
    //         {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
    //         //是否使用 如果使用 不许删除 未使用可以删除
    //         {field:"qgqdConfig",title:"操作"}//校验清单
    //     ]],
    //     loadFilter:function (data) { //过滤数据，转换成符合格式的数据
    //         return data.data;
    //     },
    //     checkOnSelect:true,
    //     selectOnCheck:true,
    //     clickToSelect: true,// 点击选中行
    //     pagination: true, // 在表格底部显示分页工具栏
    //     pageSize:20,  //页面里面显示数据的行数
    //     pageNumber: 1, // 页数
    //     pageList: [20, 30,40], //页面可以进行选择的数据行数
    //     height:'100%',
    //     singleSelect: true,//设置True 将禁止多选
    //     striped: false, // 使表格带有条纹
    //     silent: true, // 刷新事件必须设置
    //     contentType: "application/json",
    //     queryParams:function (params) { //ajax 传递的参数  分页
    //         console.log("分页")
    //         console.log(params)
    //         var data = {};
    //         data.userId = userId;
    //         data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
    //         data.pageNumber=params.pageNumber  //初始化页面的页码
    //         return {"token": "", "data": data};
    //     },
    // })

}
//创建模板窗口
$("#creatTemp").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'新建模板',
    border:false,
    closed:true,
    cls:"cloudui"
})
function creatTemp(){
    $("#creatTemp #formQd").form("clear");
    $("#creatTemp").window("open");
}
//清空弹窗输入框内容
function claearTemp() {
    $("#creatTemp #formQd").form("clear");
}