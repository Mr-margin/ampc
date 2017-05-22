/**
 * Created by shanhaichushi on 2017/5/21.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">耦合清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet">删除</button></span>');

$(".coupSet").layout();// 耦合设置面板
innitdata()
function innitdata(){  //全国清单的初始化
    $("#qgqd").datagrid({
        method:'post',
        url: "/ampc/NativeAndNation/find_nation",
        dataType: "json",
        columns:[[
            {field:"ck",checkbox:true},
            {field:"esNationName",title:"全国清单"},
            {field:"esNationYear",title:"年份"},
            {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                moment(value).format("YYYY-MM-DD")
                return  moment(value).format("YYYY-MM-DD");
            },sortable :true},
            {field:"nationRemark",title:"备注"},
        ]],
        loadFilter:function (data) {
            return data.data;
        },
        checkOnSelect:true,
        selectOnCheck:true,
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,
        pageNumber: 1, // 页数
        pageList: [20, 30,40],
        height:'100%',
        singleSelect: true,//设置True 将禁止多选
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) {
            var data = {};
            data.userId = userId;
            data.pageSize=params.pageSize;
            data.pageNumber=params.pageNumber
            return {"token": "", "data": data};
        },
    })
}
coupQd() //清单耦合步骤初始化
function coupQd(){
    innitdata();    //生成全国清单
    $("#prevCoup").hide();//隐藏上一步按钮
    $(".coudui .coupSetCon").eq(1).hide();//隐藏其他步骤
    $(".coudui .coupSetCon").eq(2).hide();
}
function nextCoup(){//点击下一步按钮
    var conText=$(".coupSetTitle .coupSetTitleList .active div").text()
    if(conText=="第一步"){
        $(".cloudui .coupSetTitleList").children("li").eq(0).removeClass("active");
        $(".cloudui .coupSetTitleList").children("li").eq(1).addClass("active");
        $("#prevCoup").show();//上一步按钮
        $(".coupSetCon").eq(0).hide();//隐藏其他步骤
        $(".coupSetCon").eq(1).show();
        $(".coupSetCon").eq(2).hide();
    }else if(conText=="第二步"){
        $(".cloudui .coupSetTitleList").children("li").eq(1).removeClass("active");
        $(".cloudui .coupSetTitleList").children("li").eq(2).addClass("active");
        $("#prevCoup").show();//最后一步 上一步按钮
        $("#nextCoup").hide();//最后一步 隐藏下一步按钮
        $(".coupSetCon").eq(0).hide();//隐藏其他步骤
        $(".coupSetCon").eq(1).hide();
        $(".coupSetCon").eq(2).show();
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