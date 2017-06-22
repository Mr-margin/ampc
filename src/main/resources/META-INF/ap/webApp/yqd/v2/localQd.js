/**
 * Created by shanhaichushi on 2017/5/19.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">本地清单</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatTemp()">新建模板</button><button class="qdEdit" onclick="editTemp()">编辑模板</button><button class="qdDelet" onclick=innitdata("delete_nativeTp")>删除模板</button></span>');
// 表单生成
var tmp=1;
innitdata("find_natives");
$('#rwpagination').pagination({
    total:10,
    pageSize: 10,
    pageNumber:1,
    onSelectPage:function(pageNumber, pageSize){
        tmp=pageNumber;
        innitdata("find_natives");
    }
});
//点击显示路径
function showPath(value) {
    var e = e || window.event;
    e.stopPropagation();//防止出现下拉
    if(value!=undefined||value!=null||value!=""){
        swal("请您到以下地址上传清单：", value);
    }else{
        swal('信息创建失败', '', 'error');
    }
}
function innitdata(active){
    if(active=="find_natives"){

        // $("#localqd").treegrid({
        //     method:'post', //ajax 请求远程数据方法
        //     url: "/ampc/NativeAndNation/doPost", //请求数据
        //     idField:'id',//通过id区分子节点父节点
        //     treeField:'esNativeTpName',//树形结构分支节点
        //     dataType: "json",
        //     columns:[[  //表头
        //         {field:"ck",checkbox:true},
        //         {field:"esNativeTpName",title:"清单模板名称",width:160,formatter: function (value) {
        //             return "<span title='" + value + "'>" + value + "</span>";}},
        //         {field:"esNativeTpYear",title:"年份",width:80},
        //         {field:"updateTime",title:"创建时间",formatter:function(value,row,index){
        //             return  moment(value).format("YYYY-MM-DD");
        //         },width :100,},
        //         {field:"filePath",title:"路径",width:120,formatter: function (value) {
        //     return "<span style='width:120px;' class='localPath' title='" + value + "'>" + value + "</span>";}},
        //         {field:"esComment",title:"备注",width:200},
        //         {field:"his",title:"使用状态",width:100},
        //         {field:"isVerify",title:"状态",width:100,formatter:function(value,row,index){
        //             if(value=="1"){
        //                 return "<span style='color: #009943'>已校验</span>"
        //             }else if(value=="0"){
        //                 return "<span style='color: #dc3f35'>未校验</span>"
        //             }else{
        //                 return value;
        //             }
        //         }},
        //         {field:"actor",title:"操作",width:100,align:'center',formatter:function(value,row,index){
        //             if(row.isVerify==1){
        //                 if(row.id.indexOf("mb")==0){
        //                     var addNativeDiv="<button id='addQdBtn'  style='cursor:pointer;width:76px;height:20px;background-color: #0fa35a;border:1px solid #00622d;color: white;border-radius:2px;box-sizing:border-box' onclick='adgQdBtn(\""+row.id+"\")'>添加数据</button>"
        //                     return addNativeDiv;
        //                 }else{
        //                     return "<span>校验成功</span>"
        //                 }
        //             }else if(row.isVerify==0){
        //                 var checkDiv="<button style='cursor:pointer;width:76px;height:20px;background-color: #febb00;border:1px solid #cd8c00;color: white;border-radius:2px;box-sizing:border-box' onclick='checkData(\""+row.id+"\")'>校验</button>"
        //                 return checkDiv
        //             }else {
        //                 return value;
        //             }
        //         }}
        //     ]],
        //     loadFilter:function (data) { //过滤数据，转换成符合格式的数据
        //         return data.data.data;
        //     },
        //     fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        //     pagination: true, // 在表格底部显示分页工具栏
        //     pageSize:10,  //页面里面显示数据的行数
        //     pageNumber: 1, // 页数
        //     pageList: [10, 15,20], //页面可以进行选择的数据行数
        //     height:'100%',
        //     contentType: "application/json",
        //     queryParams:function (params) { //ajax 传递的参数  分页
        //         var data = {};
        //         data.userId = userId;
        //         data.method="find_natives";
        //         data.pageSize=tmp; //初始化页面上面表单的数据行数
        //         data.pageNumber=params.pageSize  //初始化页面的页码
        //        // return {"token": "", "data": data};
        //         return data;
        //     },
        //     onClickRow:function (row) {
        //         if(row.children!=undefined&&row.children!=""&&row.children!=null){
        //             if(row.children.length>1){
        //                 var rowId=row.id
        //                 if ($('[node-id="' + rowId + '"]').hasClass('datagrid-row-clicked')) {
        //                     $('[node-id="' + rowId + '"]').removeClass('datagrid-row-clicked');
        //                     $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
        //                     $('#localqd').treegrid('toggle',rowId);
        //                 } else {
        //                     $('[node-id="' + rowId + '"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
        //                     $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
        //                     $('#localqd').treegrid('collapseAll').treegrid('expand',rowId);
        //                 }
        //             }
        //         }
        //     },
        //     onLoadSuccess:function (row, data) {
        //         $('#rwpagination').pagination({
        //             total:data.data.data.total
        //         });
        //     }
        // })

        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":"find_natives",
            "pageNum": tmp,
            "pageSize": 10,
        }).success(function(data){
            //给每个子节点添加标题
            var rowDate=data.data.data.rows;
            for(var i=0;i<rowDate.length;i++){
                rowDate[i].children.unshift({esNativeTpName:"清单名称",esNativeTpYear:"年份",addTime:"创建时间",esUploadTpTime:"上传时间",filePath:"路径",esComment:"备注",his:"使用状态",isVerify:"状态",actor:"操作",id:rowDate[i].id+"title"})
            }
            $("#localqd").treegrid({
                idField:'id',//通过id区分子节点父节点
                treeField:'esNativeTpName',//树形结构分支节点
                data:rowDate,
                lines:false,
                showHeader: false,
                animate:true,
                // checkbox:true,
                columns:[[  //表头
                    // {field:"ck",checkbox:true},
                    {field:"esNativeTpName",title:"清单模板名称",width:160,formatter: function (value) {
                        return "<span title='" + value + "'>" + value + "</span>";}},
                    {field:"esNativeTpYear",title:"年份",width:50},
                    {field:"addTime",title:"创建时间",formatter:function(value,row,index){
                        if(isNaN(value)){
                            return "<span>创建时间</span>";
                        }else{
                            var val=moment(value).format('YYYY-MM-DD HH:mm:ss');
                            return "<div title=\'"+val+"\'>"+val+"</div>";
                        }
                    },width :100},
                    {field:"esUploadTpTime",title:"上传时间",formatter:function(value,row,index){
                        if(isNaN(value)){
                            return "<span>上传时间</span>";
                        }else{
                            return  moment(value).format("YYYY-MM-DD");
                        }
                    },width :100,},
                    {field:"filePath",title:"路径",width:120,formatter: function (value) {
                        if(value=="路径"){
                            return "<span>路径</span>"
                        }else{
                            var swalDiv="<span class='localPath' style='text-decoration: underline' onclick=showPath(\'"+value+"\')>查看路径</span>"
                            return swalDiv;
                        }
                     }},
                    {field:"esComment",title:"备注",width:200},
                    {field:"isVerify",title:"状态",width:80,formatter:function(value,row,index){
                        if(value=="1"){
                            return "<span style='color: #009943'>已校验</span>"
                        }else if(value=="0"){
                            return "<span style='color: #dc3f35'>未校验</span>"
                        }else{
                            return value;
                        }
                    }},
                    {field:"actor",title:"操作",width:100,align:'center',formatter:function(value,row,index){
                        var addNativeDiv="<a id='addQdBtn'  style='cursor:pointer;' onclick='adgQdBtn(\""+row.id+"\")'>添加数据</a>"
                        var addNativeDivEdit="<a id='addQdBtn'  style='cursor:pointer;' onclick='editQdBtn(\""+row.id+"\")'>编辑</a>"
                        var addNativeDivDelet="<a id='addQdBtn'  style='cursor:pointer;' onclick='deleteQdBtn(\""+row.id+"\")'>删除</a>"
                        var checkDiv="<a style='cursor:pointer' onclick='$(this).attr(\"disabled\",\"disabled\");checkData(\""+row.id+"\");'>校验</a>";
                        if(Boolean(value)){
                            return value;
                        }else{
                            if(row.id.indexOf("mb")==0){
                                if(row.isVerify==1){
                                    return addNativeDiv;
                                }else if(row.isVerify==0){
                                    return checkDiv;
                                }
                            }else if(row.id.indexOf("qd")==0){
                                if(row.isVerify==0){
                                    return "<div class='allQdBtn'><a href='javascript:' class='localQdMenu'><i class='fa fa-caret-down' aria-hidden='true'></i></a><ul class='qdBtn'><li>"+checkDiv+"</li><li>"+addNativeDivEdit+"</li><li>"+addNativeDivDelet+"</li></ul></div>"
                                    // return checkDiv+"<br>"+addNativeDivEdit+"<br>"+addNativeDivDelet;
                                }else if(row.isVerify==1){
                                    return "<div class='allQdBtn'><a href='javascript:' class='localQdMenu'><i class='fa fa-caret-down' aria-hidden='true'></i></a><ul class='qdBtn'><li>"+addNativeDivEdit+"</li><li>"+addNativeDivDelet+"</li></ul></div>"

                                    // return addNativeDivEdit+"<br>"+addNativeDivDelet;
                                }
                            }
                        }

                        // if(row.isVerify==1){
                        //     if(row.id.indexOf("mb")==0){
                        //         return addNativeDiv;
                        //     }else{
                        //         return "<span>校验成功</span>"
                        //     }
                        // }else if(row.isVerify==0){
                        //     return checkDiv
                        // }else {
                        //     return value;
                        // }
                    }}
                ]],
                onClickRow:function (row) {
                    if(row.children!=undefined&&row.children!=""&&row.children!=null){
                        if(row.children.length>1){
                            var rowId=row.id
                            if ($('[node-id="' + rowId + '"]').hasClass('datagrid-row-clicked')) {
                                $('[node-id="' + rowId + '"]').removeClass('datagrid-row-clicked');
                                $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
                                $('#localqd').treegrid('toggle',rowId);
                            } else {
                                $('[node-id="' + rowId + '"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
                                $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
                                $('#localqd').treegrid('collapseAll').treegrid('expand',rowId);
                            }
                        }else if(row.children.length==1){
                            var rowId=row.id
                            if ($('[node-id="' + rowId + '"]').hasClass('datagrid-row-clicked')) {
                                $('[node-id="' + rowId + '"]').removeClass('datagrid-row-clicked');
                                $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
                            } else {
                                $('[node-id="' + rowId + '"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
                                $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
                            }
                        }
                    }
                },
                // selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
                // singleSelect: true,//设置True 将禁止多选
                // checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
                // fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
            })
            $('#rwpagination').pagination({
                total:data.data.data.total
            });
        })
    }else if(active=="add_nativeTp"){
        var param={};
        param.userId=userId;
        param.method="add_nativeTp";
        param.nativeTpName =$("#creatTemp #esNationName").val(); //清单名字
        param.nativeTpYear = $("#creatTemp #esNationYear").val(); //清单年份
        param.nativeTpRemark = $("#creatTemp #esNationMark").val();//清单备注
        //判断新建清单的年份是否在1990-2100之间
        var myYear=$("#creatTemp #esNationYear").val()
        var myName=$("#creatTemp #esNationName").val()
        if(myName.length>0 && myName.length<=20 && myName!="不可超过15个字符（必填）"){
            if(myYear>=1990&&myYear<=2100){    //判断年份
                $("#formQd").submit(
                    ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                        if(res.status==0){
                            innitdata("find_natives");
                        }else{
                            swal('参数错误', '', 'error');
                        }
                    })
                )
                $("#creatTemp").window('close');
            }else{
                swal('清单年份获取错误', '', 'error');
                // $("#creatTemp .tipYearRepeat span").remove();
                // $("#creatTemp .tipYearRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的名称</span>");
            }
        }else{
            swal('请输入符合要求的名称', '', 'error');
            // $("#creatTemp .tipNameRepeat span").remove();
            // $("#creatTemp .tipNameRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的名称</span>");
        }
    }else if(active=="updata_nativeTp"){
        var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
        var rowIndex = row.id;//获取选中行的行数索引
        var tempName=$("#esLocalEditName").val();//获取编辑后的数据 清单名称
        var tempYear=$("#esLocalEditYear").val();//年份
        var tempMark=$("#esLocalEditMark").val();//备注
        var param={};//设置接口参数
        param.userId=userId;
        param.method="updata_nativeTp";
        param.newNativeTpName =tempName;
        param.nativeTpName =tempNameOld;
        param.nativeTpYear=tempYear;
        param.nativeTpId  = row.esNativeTpId;
        param.nativeTpRemark = tempMark;
        param.filePath = row.filePath;
        param.nativeTpoutPath=row.esNativeTpOutPath;
        var myYear=$("#esLocalEditYear").val()
        var myName=$("#editTemp #esLocalEditName").val()
        if(myName.length>0 && myName.length<=20 &&  myName!="不可超过15个字符（必填）"){
            if(myYear>=1990&&myYear<=2100){//判断年份是否符合要求 符合提交编辑后数据
                $("#formQd").submit(
                    ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                        if(res.status==0){
                            $("#localqd").treegrid('update',{//更新清单列表编辑后的数据
                                id: rowIndex,
                                row: {
                                    esNativeTpName: param.nativeTpName,
                                    esNativeTpYear:tempYear,
                                    esComment:tempMark
                                }

                            })
                            innitdata("find_natives")
                        }else{
                            swal('参数错误', '', 'error');
                        }
                    })
                )
                $("#editTemp").window('close');
            }else{
                swal('年份错误', '', 'error');
            }
        }else{
            swal('请输入符合要求的名称', '', 'error');
        }

    }else if(active=="delete_nativeTp"){
        var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
        swal({
            title: "确定要删除?",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "删除",
            cancelButtonText: "取消",
            closeOnConfirm: false
        }, function() {
            ajaxPost('/NativeAndNation/doPost',{"nativeTpId":row.esNativeTpId,"method":"delete_nativeTp"}).success(function(res){
                if(res.status==0){
                    var row_id=row.id;
                    $("#localqd").treegrid("remove",row_id);
                    innitdata("find_natives")
                    swal({
                        title: '已删除!',
                        type: 'success',
                        timer: 1000,
                        showConfirmButton: false
                    });
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        });
    }else if(active=="add_native"){
        var rowDiv=$("#localqd").treegrid('find',creatQd);
        var qdName=$("#esLocalQdName").val();
        var qdYear=$("#esLocalQdYear").val();
        var qdRemark=$("#esLocalQdMark").val();
        if(rowDiv){
            if(qdName.length>0 && qdName.length<=20 && qdName!="不可超过15个字符（必填）"){
                if(qdYear>=1990&&qdYear<=2100){
                    ajaxPost('/NativeAndNation/doPost',{"userId":userId,"method":"add_native","nativeName":qdName,"nativeYear":qdYear,"nativeRemark":qdRemark,"nativeTpId":rowDiv.esNativeTpId,"nativeTpName":rowDiv.esNativeTpName}).success(function(res){
                        if(res.status==0){
                            innitdata("find_natives");
                            console.log("123");
                            $('[node-id="'+creatQd+'"]').addClass('datagrid-row-clicked').siblings().removeClass('datagrid-row-clicked');
                            $(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree .datagrid-row").removeClass('datagrid-row-clicked');
                            $('#localqd').treegrid('collapseAll').treegrid('expand',creatQd);
                            $("#editTempQd").window('close');
                        }else{
                            swal('参数错误', '', 'error');
                        }
                    })
                }else{
                    swal('年份错误', '', 'error');
                    // $("#editTempQd .tipYearRepeat span").remove();
                    // $("#editTempQd .tipYearRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的年份</span>");
                }
            }else{
                swal('请输入符合要求的年份', '', 'error');
                // $("#editTempQd .tipNameRepeat span").remove();
                // $("#editTempQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>请输入符合要求的名称</span>");
            }

        }
    }else if(active=="update_native"){
        var rowNow=$("#localqd").treegrid('find',editQd)
        var param={};//设置接口参数
        param.userId=userId;
        param.nativeName=$("#editLocalQdName").val();
        param.nativeId=rowNow.esNativeId;
        param.nativeRemark=$("#editLocalQdMark").val();
        param.method="update_native";
        param.nativeYear=$("#editLocalQdYear").val();


        var myYear_=$("#editLocalQdYear").val()
        var myName_=$("#editLocalQdYear").val()
        if(myName_.length>0 && myName_.length<=20 &&  myName_!="不可超过15个字符（必填）"){
            if(myYear_>=1990&&myYear_<=2100){//判断年份是否符合要求 符合提交编辑后数据
                $("#formLocalQd").submit(
                    ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                        if(res.status==0){
                            $("#localqd").treegrid('update',{//更新清单列表编辑后的数据
                                id: editQd,
                                row: {
                                    esNativeTpName: param.nativeName,
                                    esNativeTpYear:param.nativeYear,
                                    esComment:param.nativeRemark
                                }

                            })
                            innitdata("find_natives")
                        }else{
                            swal('参数错误', '', 'error');
                        }
                    })
                )
                $("#editLocalQd").window('close');
            }else{
                swal('年份错误', '', 'error');
            }
        }else{
            swal('请输入符合要求的名称', '', 'error');
        }
    }
}

//easyui布局
$(".treeTable").layout()
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
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("1990-2100（必填）").css({"color":"#757575"});
    // $("#creatTemp .tipNameRepeat span").remove();
    // $("#creatTemp .tipYearRepeat span").remove();
    var labelDiv=$("#creatTemp label");
    for(var i=0;i<labelDiv.length;i++){
        if($("#creatTemp label").eq(i).attr("id")){
            $("#creatTemp label").eq(i).remove();
        }
    }
    $("#creatTemp").window("open").window('center');;
}
//清空弹窗输入框内容
function claearTemp() {
    $("#creatTemp #formQd").form("clear");
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("1990-2100（必填）").css({"color":"#757575"});
}
//清空新建清单弹窗输入框内容
function claearTempQd() {
    $("#creatTemp #formTempQd").form("clear");
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("1990-2100（必填）").css({"color":"#757575"});
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
var tempNameOld;
function editTemp() {
    var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editTempName,editTempYear,editMark,editId;
        editTempName=row.esNativeTpName,editTempYear=row.esNativeTpYear;
        if(Boolean(row.esComment!=undefined)){
            editMark=row.esComment;
        }else{
            editMark="";
        }
        tempNameOld=editTempName;
        document.getElementById("esLocalEditName").value=editTempName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("esLocalEditYear").value=editTempYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("esLocalEditMark").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
        $("#editTemp input").css({"color":"black"});
        // $("#editTemp .tipNameRepeat span").remove();
        // $("#editTemp .tipYearRepeat span").remove();
        var labelDiv=$("#editTemp label");
        for(var i=0;i<labelDiv.length;i++){
            if($("#editTemp label").eq(i).attr("id")){
                $("#editTemp label").eq(i).remove();
            }
        }
        $("#editTemp").window('open').window('center');;
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
//创建模板清单窗口
$("#editTempQd").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'添加清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
var creatQd;
var editQd;
var deleteQd;
//删除清单
function deleteQdBtn(rowID){
    deleteQd=rowID;
    // var row = $('#localqd').treegrid('getSelected');//获取所有选中的清单数据
    var rowDelete=$("#localqd").treegrid('find',deleteQd)
    swal({
        title: "确定要删除?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        cancelButtonText: "取消",
        closeOnConfirm: false
    }, function() {
        ajaxPost('/NativeAndNation/doPost',{"nativeId":rowDelete.esNativeId,"method":"delete_native","userId":userId}).success(function(res){
            if(res.status==0){
                $("#localqd").treegrid("remove",deleteQd);
                innitdata("find_natives")
                swal({
                    title: '已删除!',
                    type: 'success',
                    timer: 1000,
                    showConfirmButton: false
                });
            }else{
                swal('参数错误', '', 'error');
            }
        })
    });

}
//点击按钮创建模板下面的清单
function adgQdBtn(rowId){
    creatQd=rowId
    var e = e || window.event;
    e.stopPropagation();//防止出现下拉
    $("#formTempQd").form("clear");
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("1990-2100（必填）").css({"color":"#757575"});
    // $("#editTempQd .tipYearRepeat span").remove();
    // $("#editTempQd .tipNameRepeat span").remove();
    var labelDiv=$("#editTempQd label");
    for(var i=0;i<labelDiv.length;i++){
        if($("#editTempQd label").eq(i).attr("id")){
            $("#editTempQd label").eq(i).remove();
        }
    }
    $("#editTempQd").window("open").window('center');;
}
//编辑清单窗口
$("#editLocalQd").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'编辑清单',
    border:false,
    closed:true,
    cls:"cloudui"
})
//点击按钮编辑模板下面的清单
function editQdBtn(rowId){
    editQd=rowId;
    var rowDiv=$("#localqd").treegrid('find',rowId);
    console.log(rowDiv);
    var e = e || window.event;
    e.stopPropagation();//防止出现下拉
    document.getElementById("editLocalQdName").value=rowDiv.esNativeTpName; //编辑窗口打开后 名称输入框显示所选数据的名称
    document.getElementById("editLocalQdYear").value=rowDiv.esNativeTpYear;//编辑窗口打开后 年份输入框显示所选数据的名称
    document.getElementById("editLocalQdMark").value=rowDiv.esComment;//编辑窗口打开后 备注输入框显示所选数据的名称
    var labelDiv=$("#editTempQd label");
    for(var i=0;i<labelDiv.length;i++){
        if($("#editTempQd label").eq(i).attr("id")){
            $("#editTempQd label").eq(i).remove();
        }
    }
    $("#editLocalQd").window("open").window('center');;
}
//防止树形表单子节点点击出现下拉效果
$(".cloudui .treeTable .datagrid-btable .treegrid-tr-tree tr").click(function(){
    var e = e || window.event;
    e.stopPropagation();//防止出现下拉
})
//校验
function checkData(rowId) {
    var rowDiv=$("#localqd").treegrid('find',rowId);
    var parentId=rowDiv._parentId;
    var parentRowDiv=$("#localqd").treegrid('find',parentId);
    swal({
        title: "校验中，请勿进行其他操作！",
        type: "warning",
        showConfirmButton: false,
        showCancelButton:false,
    })
        if(rowId.indexOf("mb")==0){
            ajaxPost('/NativeAndNation/doPost',{
                "userId":userId,
                "method":"checkNativeTp",
                "nativeTpId":rowDiv.esNativeTpId,
                "nativeTpName":rowDiv.esNativeTpName,
                "esNativeTpOutPath":rowDiv.esNativeTpOutPath
            }).success(function (res) {
                swal.close();
                if(res.status==0){
                    if(rowDiv.isVerify==0){
                        if(res.data.data.msg==true){
                            swal({
                                title: '校验成功!',
                                type: 'success',
                                timer: 500,
                                showConfirmButton: false
                            });
                            innitdata("find_natives");
                        }else if(res.data.data.errorMsg){
                            var hisCon=res.data.data.errorMsg[0];
                            $('[node-id="' + rowId + '"]').children("td[field=his]").html("<div style='width:200px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;color:#333;' title='"+hisCon+"'>"+hisCon+"</div>");
                            swal(hisCon, '', 'error');
                            innitdata("find_natives");
                        }
                    }
                }else{
                    swal('接口错误', '', 'error');
                }
            }).error(function () {
                swal("接口错误","","error")
            })
        }else  if(rowId.indexOf("qd")==0){
            ajaxPost('/NativeAndNation/doPost',{
                "userId":userId,
                "method":"checkNative",
                "nativeTpId":parentRowDiv.esNativeTpId,
                "nativeId":rowDiv.esNativeId,
                "nativeName":rowDiv.esNativeTpName,
                "nativeTpName":parentRowDiv.esNativeTpName,
                "esNativeTpOutPath":parentRowDiv.esNativeTpOutPath
            }).success(function (res) {
                swal.close()
                if(res.status==0){
                    if(rowDiv.isVerify==0){
                        if(res.data.data.errorMsg){
                            var hisConQd=res.data.data.errorMsg;
                            var re;
                            for(var i=0;i<hisConQd.length;i++){
                                re+=hisConQd[i];
                            }
                            $('[node-id="' + rowId + '"]').children("td[field=his]").html("<div style='width:200px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;color:#333;' title='"+re+"'>"+re+"</div>");
                            swal(re, '', 'error');
                            innitdata("find_natives");
                        }else if(res.data.data.msg==true){
                            swal({
                                title: '校验成功!',
                                type: 'success',
                                timer: 500,
                                showConfirmButton: false
                            });
                            innitdata("find_natives");
                        }
                    }
                }else{
                    swal('参数错误', '', 'error');
                }
            }).error(function () {
                swal("接口错误","","error")
            })
        }
    


}
// 创建 输入框获得焦点
$(".cloudui .rwCon .qdContent .qdName").focus(function () {//名称获取焦点
    if($(this).val()=="不可超过15个字符（必填）"){
        $(this).val("");
        $(this).css({"color":"black"})
    }
})
$(".cloudui .rwCon .qdContent .qdYear").focus(function () {//年份获取焦点
    if($(this).val()=="1990-2100（必填）"){
        $(this).val("");
        $(this).css({"color":"black"})
    }
})

//创建 输入框失去焦点
$(".cloudui .rwCon .qdContent .qdName").blur(function () {//名称失去焦点
    if($(this).val()==""){
        $(this).val("不可超过15个字符（必填）")
        $(this).css({"color":"#757575"})
    }

})
$(".cloudui .rwCon .qdContent .qdYear").blur(function () {//年份失去焦点
    if($(this).val()==""){
        $(this).val("1990-2100之间的年份")
        $(this).css({"color":"#757575"})
    }

})
//本地清单模板名字校验
$("#creatTemp #esNationName").blur(
    function () {
        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":'verifyByNativeTpName',
            "nativeTpName":$("#creatTemp #esNationName").val()
        }).success(function (res) {
            if(res.data.data.msg==true){
                swal('名称重复，请再次输入名称', '', 'error');
                // $("#creatTemp .tipNameRepeat span").remove();
                // $("#creatTemp .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>该名称已被使用</span>");
            }
        })
    }
)
//本地清单名称校验
$("#editTempQd #esLocalQdName").blur(
    function () {
        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":'verifyByNativeName',
            "nativeName":$("#editTempQd #esLocalQdName").val()
        }).success(function (res) {
            if(res.data.data.msg==true){
                swal('名称重复，请再次输入名称', '', 'error');
                // $("#editTempQd .tipNameRepeat span").remove();
                // $("#editTempQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>该名称已被使用</span>");
            }

        })
    }
)

    $("#creatTemp #formQd").validate({
        rules: {
            esNationName: {
                required: true,
                maxlength: 15,
                minlength: 1
            },
            esNationYear: {
                number:true,
                range:[1990,2100]
            },
        },
        messages: {
            esNationName: {
                required: '请填写清单名称',
                maxlength: '不可超过15个字符'
            },
            esNationYear: {
                number: '请填写年份',
            },
        }

    });
    $("#formEditTemp").validate({
        rules: {
            esNationName: {
                required: true,
                maxlength: 15,
                minlength: 1
            },
            esNationYear: {
                number:true,
                range:[1990,2100]
            },
        },
        messages: {
            esNationName: {
                required: '请填写清单名称',
                maxlength: '不可超过15个字符'
            },
            esNationYear: {
                number: '请填写年份',
            },
        }

    });
    $("#formTempQd").validate({
        rules: {
            esNationName: {
                required: true,
                maxlength: 15,
                minlength: 1
            },
            esNationYear: {
                number:true,
                range:[1990,2100]
            },
        },
        messages: {
            esNationName: {
                required: '请填写清单名称',
                maxlength: '不可超过15个字符'
            },
            esNationYear: {
                number: '请填写年份',
            },
        }

    });

//树形结构操作
//
/*任务列表中设置的下拉框*/
// $(".localQdMenu").click(function () {
//     console.log("进来1");
//     $(this).siblings().show();
//     console.log("进来");
// })
// $("#localqd").on('click', '.allQdBtn>.localQdMenu', function (e) {
//     console.log("出现1")
//     $(this).siblings().show();
//     console.log("出现")
// });
// $("#localqd").on('mouseleave', '.allQdBtn', function (e) {
//     $(this).find('ul').hide();
//     console.log("隐藏")
// });
$('.tableBox').on('click','.localQdMenu',function (e) {
    $(this).parent().find('.qdBtn').show();
})
$(".tableBox").on('mouseleave', '.allQdBtn', function (e) {
    $(this).find('ul').hide();
});