/**
 * Created by shanhaichushi on 2017/5/19.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">本地清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatTemp()">新建</button><button class="qdEdit" onclick="editTemp()">编辑</button><button class="qdDelet" onclick=innitdata("delete_nativeTp")>删除</button></span>');

// 表单生成
innitdata("find_natives")
function innitdata(active){
    if(active=="find_natives"){
        // $("#localqd").treegrid({
        //     method:'post', //ajax 请求远程数据方法
        //     url: "/ampc/NativeAndNation/doPost", //请求数据
        //     idField:'esNativeTpId',
        //     treeField:'esNativeTpName',
        //     dataType: "json",
        //     columns:[[  //表头
        //         {field:"ck",checkbox:true},
        //         {field:"esNativeTpName",title:"全国清单",width:200},
        //         {field:"esNativeTpYear",title:"年份"},
        //         {field:"esUploadTpTime",title:"创建时间",formatter:function(value,row,index){
        //             moment(value).format("YYYY-MM-DD")//格式化带日期格式
        //             return  moment(value).format("YYYY-MM-DD");
        //         }},
        //         {field:"esComment",title:"备注",width:400},
        //         {field:"isEffective",title:"操作",},//新建（打开校验按钮）   正常（校验成功） 错误（校验错误）
        //         //是否使用 如果使用 不许删除 未使用可以删除
        //         //        	             {field:"qgqdConfig",title:"操作"}//校验清单
        //     ]],
        //     loadFilter:function (data) { //过滤数据，转换成符合格式的数据
        //         return data.data.data;
        //     },
        //     selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        //     singleSelect: true,//设置True 将禁止多选
        //     checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        //     fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        //     clickToSelect: true,// 点击选中行
        //     pagination: true, // 在表格底部显示分页工具栏
        //     pageSize:10,  //页面里面显示数据的行数
        //     pageNumber: 1, // 页数
        //     pageList: [10, 15,20], //页面可以进行选择的数据行数
        //     height:'100%',
        //     striped: false, // 使表格带有条纹
        //     silent: true, // 刷新事件必须设置
        //     contentType: "application/json",
        //     queryParams:function (params) { //ajax 传递的参数  分页
        //         var data = {};
        //         data.userId = userId;
        //         data.method="active";
        //         // data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
        //         // data.pageNumber=params.pageNumber  //初始化页面的页码
        //        // return {"token": "", "data": data};
        //         return data;
        //     },
        // })
        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":active,
        }).success(function(data){
            $("#localqd").treegrid({
                idField:'esNativeTpId',
                treeField:'esNativeTpName',
                data:data.data.data,
                lines:false,
                columns:[[  //表头
                    {field:"ck",checkbox:true},
                    {field:"esNativeTpName",title:"清单模板名称",width:200},
                    {field:"esNativeTpYear",title:"年份"},
                    {field:"esUploadTpTime",title:"创建时间",formatter:function(value,row,index){
                        moment(value).format("YYYY-MM-DD")//格式化带日期格式
                        return  moment(value).format("YYYY-MM-DD");
                    },sortable :true},
                    {field:"esComment",title:"备注",width:300},
                    {field:"isEffective",title:"状态",width:100,formatter:function(value,row,index){
                        if(value==1){
                            return "<span style='color: #009943'>已校验</span>"
                        }else{
                            return "<span style='color: #dc3f35'>未校验</span>"
                        }
                    }},
                    {field:"actor",title:"操作",width:100,align:'center',formatter:function(value,row,index){
                        if(row.isEffective==1){
                            return "<button id='addQdBtn' onclick='adgQdBtn()' style='cursor:pointer;width:76px;height:20px;background-color: #0fa35a;border:1px solid #00622d;color: white;border-radius:2px;box-sizing:border-box'>添加数据</button>"
                        }else{
                            return "<button style='cursor:pointer;width:76px;height:20px;background-color: #febb00;border:1px solid #00662f;color: white;border-radius:2px;box-sizing:border-box'>校验数据</button>"
                        }
                    }}
                ]],
                selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
                singleSelect: true,//设置True 将禁止多选
                checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
                fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
                clickToSelect: true,// 点击选中行
                pagination: true, // 在表格底部显示分页工具栏
            })
        })
    }else if(active=="add_nativeTp"){
        var param={};
        param.userId=userId;
        param.method="add_nativeTp";
        param.nativeTpName =$("#esNationName").val(); //清单名字
        param.nativeTpYear = $("#esNationYear").val(); //清单年份
        param.nativeTpRemark = $("#esNationMark").val();//清单备注
        //判断新建清单的年份是否在1990-2100之间
        var myYear=$("#esNationYear").val()
        if(myYear>=1990&&myYear<2100){    //判断年份
            $("#formQd").submit(
                ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                    if(res.status==0){
                        $("#localqd").treegrid('insert',{ //在表格中插入新建模板
                            before: 0,	// 索引从0开始
                            data: {
                                esNativeTpName: param.nativeTpName, //新建模板的名字
                                esNativeTpYear: param.nativeTpYear,//新建模板的年份
                                esComment:param.nativeTpRemark //新建模板的备注
                            }
                        })
                    }else{
                        swal('参数错误', '', 'error');
                    }

                })
            )
            $("#creatTemp").window('close');
        }else{
            swal('清单年份获取错误', '', 'error');
        }
    }else if(active=="updata_nativeTp"){
        var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
        var rowIndex = row.id;//获取选中行的行数索引
        console.log(rowIndex)
        var tempName=$("#esLocalEditName").val();//获取编辑后的数据 清单名称
        var tempYear=$("#esLocalEditYear").val();//年份
        var tempMark=$("#esLocalEditMark").val();//备注
        var param={};//设置接口参数
        param.userId=userId;
        param.method="updata_nativeTp";
        param.nativeTpName =tempName;
        param.nativeTpYear=tempYear;
        param.nativeTpId  = row.esNativeTpId;
        param.nativeTpRemark = tempMark;
        var myYear=$("#esLocalEditYear").val()
        if(myYear>=1990&&myYear<2100){//判断年份是否符合要求 符合提交编辑后数据
            $("#formQd").submit(
                ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                    if(res.status==0){
                        $("#localqd").treegrid('update',{//更新清单列表编辑后的数据
                            id: rowIndex+1,
                            row: {
                                esNativeTpName: param.nativeTpName,
                                esNativeTpYear:tempYear,
                                esComment:tempMark
                            }

                        })
                    }else{
                        swal('参数错误', '', 'error');
                    }
                })
            )
            $("#editTemp").window('close');
        }else{
            swal('年份错误', '', 'error');
        }
    }else if(active=="delete_nativeTp"){
        var obj_node = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
        console.log(obj_node);
        ajaxPost('/NativeAndNation/doPost',{"nativeTpId":obj_node.esNativeTpId,"method":"delete_nativeTp"}).success(function(res){
            if(res.status==0){
                var obj_node_id=obj_node.id+1;
                console.log(obj_node_id);
                $("#localqd").treegrid("remove",obj_node_id)
            }else{
                swal('参数错误', '', 'error');
            }
        })
    }
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
// 编辑模板模态框
//创建模板窗口
$("#editTemp").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'编辑模板',
    border:false,
    closed:true,
    cls:"cloudui"
})
function editTemp() {
    var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editTempName,editTempYear,editMark,editId;
        editTempName=row.esNativeTpName,editTempYear=row.esNativeTpYear,editMark=row.esComment;
        document.getElementById("esLocalEditName").value=editTempName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("esLocalEditYear").value=editTempYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("esLocalEditMark").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
        $("#editTemp").window('open');
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
//点击按钮创建模板下面的清单
function adgQdBtn(){

}