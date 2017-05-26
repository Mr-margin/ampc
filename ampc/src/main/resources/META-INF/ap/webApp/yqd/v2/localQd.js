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
        	idField:'esNativeTpId',
            treeField:'esNativeTpName',
        	data:data.data,
        	columns:[[  //表头
        	             {field:"ck",checkbox:true},
        	             {field:"esNativeTpName",title:"全国清单"},
        	             {field:"esNativeTpYear",title:"年份"},
        	             {field:"esUploadTpTime",title:"创建时间",formatter:function(value,row,index){
        	                 moment(value).format("YYYY-MM-DD")//格式化带日期格式
        	                 return  moment(value).format("YYYY-MM-DD");
        	             },sortable :true},
        	             {field:"esComment",title:"备注"},
        	             {field:"isEffective",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
        	             //是否使用 如果使用 不许删除 未使用可以删除
//        	             {field:"qgqdConfig",title:"操作"}//校验清单
        	         ]],
            selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
            singleSelect: true,//设置True 将禁止多选
            checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
            fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
            clickToSelect: true,// 点击选中行
            pagination: true, // 在表格底部显示分页工具栏
        })
    })
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
//点击新建窗口的提交按钮
// function submitTemp() {
//     var param={};
//     param.userId=userId;
//     param.nationName =$("#esNationName").val(); //清单名字
//     param.nationYear = $("#esNationYear").val(); //清单年份
//     param.nationRemark = $("#esNationMark").val();//清单备注
//     //判断新建清单的年份是否在1990-2100之间
//     var myYear=$("#esNationYear").val()
//     if(myYear>=1990&&myYear<2100){    //判断年份
//         $("#formQd").submit(
//             ajaxPost('/NativeAndNation/doPost',param).success(function(res){
//                 if(res.status==0){
//                     $("#qgqd").datagrid('insertRow',{ //在表格中插入新建清单
//                         index: 0,	// 索引从0开始
//                         row: {
//                             esNationName: param.nationName, //新建清单的名字
//                             esNationYear: param.nationYear,//新建清单的年份
//                             nationRemark:param.nationRemark //新建清单的备注
//                         }
//                     })
//                 }else{
//                     swal('参数错误', '', 'error');
//                 }
//
//             })
//         )
//         $("#creatQd").window('close');
//     }else{
//         swal('清单年份获取错误', '', 'error');
//     }
// }
function submitTemp() {
    var param = {};
    param.userId = userId;
    param.nationName = $("#esNationName").val(); //清单名字
    param.nationYear = $("#esNationYear").val(); //清单年份
    param.nationRemark = $("#esNationMark").val();//清单备注
    param.method = "find_natives";
    //判断新建清单的年份是否在1990-2100之间
    var myYear = $("#esNationYear").val()
    if (myYear >= 1990 && myYear < 2100) {    //判断年份
        $("#formQd").submit(
            ajaxPost('/NativeAndNation/doPost', param).success(function (res) {
                if (res.status == 0) {
                    $("#qgqd").datagrid('insertRow', { //在表格中插入新建清单
                        index: 0,	// 索引从0开始
                        row: {
                            esNationName: param.nationName, //新建清单的名字
                            esNationYear: param.nationYear,//新建清单的年份
                            nationRemark: param.nationRemark //新建清单的备注
                        }
                    })
                } else {
                    swal('参数错误', '', 'error');
                }

            })
        )
        $("#creatQd").window('close');
    } else {
        swal('清单年份获取错误', '', 'error');
    }
}