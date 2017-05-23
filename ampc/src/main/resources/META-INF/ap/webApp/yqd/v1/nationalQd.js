/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">全国清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button><button class="qdDelet" onclick="delectQd()">删除</button></span>');
innitdata()//全国清单表单初始化
function innitdata(){
    $("#qgqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/find_nation", //请求数据
        dataType: "json",
        columns:[[  //表头
                    {field:"ck",checkbox:true},
                    {field:"esNationName",title:"全国清单",width:200},
                    {field:"esNationYear",title:"年份",width:80},
                    {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                        moment(value).format("YYYY-MM-DD")//格式化带日期格式
                        return  moment(value).format("YYYY-MM-DD");
                    }},
                    {field:"nationRemark",title:"备注",width:400},
                    {field:"qgqdCheck",title:"状态"},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
                    //是否使用 如果使用 不许删除 未使用可以删除
                    {field:"qgqdConfig",title:"操作"}//校验清单
                ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;
        },
        selectOnCheck:false, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        singleSelect: true,//设置True 将禁止多选
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            return {"token": "", "data": data};
        },
    })
}
function creatQd(){ // 点击创建清单按钮 弹出创建窗口
    $("#formQd").form('clear');//打开窗口清空输入框数据
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
    param.nationName =$("#esNationName").val(); //清单名字
    param.nationYear = $("#esNationYear").val(); //清单年份
    param.nationRemark = $("#esNationMark").val();//清单备注
    //判断新建清单的年份是否在1990-2100之间
    var myYear=$("#esNationYear").val()
    if(myYear>=1990&&myYear<2100){    //判断年份
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
                    })
                }else{
                    swal('参数错误', '', 'error');
                }

            })
        )
        $("#creatQd").window('close');
    }else{
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
                    })
                }else{
                    swal('参数错误', '', 'error');
                }

            })
        )
        $("#creatQd").window('close');
    }
}
function claearQd(){ //点击窗口清空按钮 对表单输入框进行数据清空
    $("#formQd").form('clear');
}

function editQd(){ // 编辑全国清单
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editQdName,editQdYear,editMark,editId;
        editQdName=row.esNationName,editQdYear=row.esNationYear,editMark=row.nationRemark,editId=row.esNationId;
        document.getElementById("esNationName_edit").value=editQdName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("esNationYear_edit").value=editQdYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("esNationMark_edit").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
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
    var qdName=$("#esNationName_edit").val();//获取编辑后的数据 清单名称
    var qdYear=$("#esNationYear_edit").val();//年份
    var qdMark=$("#esNationMark_edit").val();//备注
    var param={};//设置接口参数
    param.userId=userId;
    param.nationName=qdName;
    param.nationId = row.esNationId;
    param.nationRemark = qdMark;
    param.nationYear = qdYear;
    var myYear=$("#esNationYear").val()
    if(myYear>=1990&&myYear<2100){//判断年份是否符合要求 符合提交编辑后数据
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
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        )
        $("#editQd").window('close');
    }else{
        swal('年份错误', '', 'error');
    }
}
// 删除清单
function delectQd(){
    var row = $('#qgqd').datagrid('getSelected');//获取所有选中的清单数据
    ajaxPost('/NativeAndNation/delete_nation',{"nationId":row.esNationId}).success(function(res){
        if(res.status==0){
            var rowIndex = $('#qgqd').datagrid('getRowIndex', row);
            $('#qgqd').datagrid('deleteRow', rowIndex);
            $('#qgqd').datagrid('reload');//删除后重新加载下
        }else{
            swal('参数错误', '', 'error');
        }
    })
}
