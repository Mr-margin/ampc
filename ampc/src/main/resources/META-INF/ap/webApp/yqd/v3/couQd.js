/**
 * Created by shanhaichushi on 2017/5/21.
 */
// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">源清单</span><i class="en-arrow-right7" style="font-size:16px;"></i><a href="#/yqd_v3" style="padding-left: 15px;padding-right: 15px;color:#333;text-decoration: none" >耦合清单</a><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatCoupQd()">新建</button><button class="qdEdit" onclick="editCoupQd()">编辑</button><button class="qdDelet" onclick="coupDelete()">删除</button></span>');

var coupingQd,checkQgQd,localQd;
$(".coupSet").layout();// 耦合设置面板
function innitdata(){  //耦合清单的初始化
    $("#couqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/doPost", //请求数据
        dataType: "json",
        columns:[[  //表头
            // {field:"ck",checkbox:true},
            {field:"esCouplingName",title:"清单名称",width:160,align:'cneter'},
            {field:"esCouplingDesc",title:"清单描述",width:200,align:'cneter'},
            {field:"publishTime",title:"本地清单",width:100},
            {field:"nationRemark",title:"全国清单",width:100},
            {field:"esCouplingYear",title:"年份",width:80},
            {field:"addTime",title:"创建时间",formatter:function(value,row,index){
            	var val=moment(value).format('YYYY-MM-DD HH:mm:ss');
            	return "<span title=\'"+val+"\'>"+val+"</sapn>";
//                return  moment(value).format("YYYY-MM-DD hh:mm:ss");
            },align:'cneter',width:160},
            {field:"historyCoupling",title:"使用历史",width:100},
            {field:"employ",title:"配置",formatter:function(value,row,index){
                if(value==1){
                    return "<span>正在使用</span>"
                }else{
                    var coupId=row.esCouplingId
                    var coupSetBtn="<button style='cursor:pointer;width:76px;height:20px;background-color: #febb00;border:1px solid #cd8c00;color: white;border-radius:2px;box-sizing:border-box' onclick='coupSetQd("+coupId+")'>耦合配置</button>"
                    return  coupSetBtn;
                }
            },align:'cneter',width:120},
            {field:"viewDetail",title:"查看",formatter:function(value,row,index){
                var coupId=row.esCouplingId
                var coupSetBtn;
               if(row.employ==1){
                   var coupSetBtn="<button style='cursor:pointer;width:76px;height:20px;background-color: #0fa35a;border:1px solid #00622d;color: white;border-radius:2px;box-sizing:border-box' onclick='viewDetail("+coupId+")'>查看</button>"
                   return coupSetBtn;
               }else if(row.esCouplingStatus!=1){
                   var coupSetBtn="<button style='cursor:pointer;width:76px;height:20px;background-color: #0fa35a;border:1px solid #00622d;color: white;border-radius:2px;box-sizing:border-box' onclick='viewDetail("+coupId+")'>查看</button>"
                   return coupSetBtn;
               }else{
                   var coupSetBtn="<button style='cursor:pointer;width:76px;height:20px;background-color: #D9D9D9;border:1px solid #666;color: white;border-radius:2px;box-sizing:border-box'>查看</button>"
                   return coupSetBtn;
               }
            }}

        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data.data.rows;
        },
        // selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        toolbar: '#searchTool',
        singleSelect: true,//设置True 将禁止多选
        // checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        // fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        // clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        // silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.method="find_coupling";
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            data.queryName=$("#companyname").val();
            return {"token": "", "data": data};
        },
        onClickRow:function (index,row) {
            //用于作为单选行的操作，当点击一行后，其他行取消选中，在datagrid中需要把singleSelect取消
            var rowNum=$(this).datagrid('getRows').length;
            for(var i=0;i<rowNum;i++){
                if(i!=index){
                    $(this).datagrid('uncheckRow',i)
                }
            }
        }
    })
}
//全国清单生成
function qgqdTable() {
    $("#qgqd").datagrid({
        method:'post', //ajax 请求远程数据方法
        url: "/ampc/NativeAndNation/find_nation", //请求数据
        dataType: "json",
        columns:[[  //表头
            // {field:"ck",checkbox:true},
            {field:"esNationName",title:"全国清单",width:200,align:'cneter'},
            {field:"esNationYear",title:"年份",width:80,align:'cneter'},
            {field:"publishTime",title:"创建时间",formatter:function(value,row,index){
                return  moment(value).format("YYYY-MM-DD");
            },align:'cneter'},
            {field:"nationRemark",title:"备注",width:400,align:'cneter'},
            {field:"qgqdCheck",title:"状态",align:'cneter',width:100},//新建（校验按钮）   正常（校验成功） 错误（校验错误）
            //是否使用 如果使用 不许删除 未使用可以删除
            {field:"qgqdConfig",title:"操作",align:'cneter',width:100}//校验清单
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            return data.data;

        },
        selectOnCheck:true, //true，单击复选框将永远选择行 false，选择行将不选中复选框。
        singleSelect: true,//设置True 将禁止多选
        checkOnSelect:true,//true，当用户点击行的时候该复选框就会被选中或取消选中。false，当用户仅在点击该复选框的时候才会呗选中或取消。
        fitColumns:true,//真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。
        clickToSelect: true,// 点击选中行
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        height:'100%',
        striped: false, // 使表格带有条纹
        silent: true, // 刷新事件必须设置
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            data.queryName="";
            return {"token": "", "data": data};
        },
    })
}
coupQd() //清单耦合步骤初始化
function coupQd(){
    innitdata();    //生成全国清单
    $(".coupSet").hide();
}
//打开新建清单窗口
$("#creatCoupQd").window({
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
//点击新建按钮 打开窗口
function creatCoupQd(){
    $("#formCoup").form("clear")
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("请输入1990-2100之间的年份").css({"color":"#757575"});
    // $("#creatCoupQd .tipYearRepeat span").remove();
    // $("#creatCoupQd .tipNameRepeat span").remove();
    var labelDiv=$("#formCoup label");
    for(var i=0;i<labelDiv.length;i++){
        if($("#formCoup label").eq(i).attr("id")){
            $("#formCoup label").eq(i).remove();
        }
    }
    $("#creatCoupQd").window("open");
}
// 清除已经写入的数据
function clearCoup() {
    $("#formCoup").form("clear")
    $(".cloudui .rwCon .qdContent .qdName").val("不可超过15个字符（必填）").css({"color":"#757575"});
    $(".cloudui .rwCon .qdContent .qdYear").val("请输入1990-2100之间的年份").css({"color":"#757575"});
}
//点击新建窗口提交按钮进行耦合清单创建
function submitCoup(){
    var param={};
    param.userId=userId;
    param.method="add_coupling";
    param.couplingName =$("#creatCoupQd #coupQdName").val(); //清单名字
    param.couplingYear = $("#creatCoupQd #coupQdYear").val(); //清单年份
    param.couplingDesc = $("#creatCoupQd #coupQdMark").val();//清单备注
    //判断新建清单的年份是否在1990-2100之间
    var myYear=$("#creatCoupQd #coupQdYear").val();
    var myName=$("#creatCoupQd #coupQdName").val()
    if(myName.length>0 && myName.length<=20&&myName!="不可超过15个字符（必填）"){
        if(myYear>=1990&&myYear<=2100){    //判断年份
            $("#formCoup").submit(
                ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                    if(res.status==0){
                        $("#couqd").datagrid('insertRow',{ //在表格中插入新建清单
                            index: 0,	// 索引从0开始
                            row: {
                                esCouplingName: param.couplingName, //新建清单的名字
                                esCouplingYear: param.couplingYear,//新建清单的年份
                                esCouplingDesc:param.couplingDesc //新建清单的描述
                            }
                        })
                        innitdata();
                    }else{
                        swal('参数错误', '', 'error');
                    }
                })
            )
            $("#creatCoupQd").window('close');
        }else{
            swal('清单年份获取错误', '', 'error');
            // $("#creatCoupQd .tipYearRepeat span").remove();
            // $("#creatCoupQd .tipYearRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的年份</span>");
        }
    }else{
        swal('请输入符合要求的名称', '', 'error');
        // $("#creatCoupQd .tipNameRepeat span").remove();
        // $("#creatCoupQd .tipNameRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的名称</span>");
    }

}
//打开编辑清单窗口
$("#editCoupQd").window({
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
//编辑耦合清单
function editCoupQd(){
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    if(row!=''&&row!=null&&row!=undefined){ //所选数据不为空
        var editQdName,editQdYear,editMark,editId;
        editQdName=row.esCouplingName,editQdYear=row.esCouplingYear,editId=row.esCouplingId;
        if(Boolean(row.esCouplingDesc!=undefined)){
            editMark=row.esCouplingDesc;
        }else{
            editMark="";
        }
        document.getElementById("coupEditQdName").value=editQdName; //编辑窗口打开后 名称输入框显示所选数据的名称
        document.getElementById("coupEditQdYear").value=editQdYear;//编辑窗口打开后 年份输入框显示所选数据的名称
        document.getElementById("coupEditQdMark").value=editMark;//编辑窗口打开后 备注输入框显示所选数据的名称
        $("#editCoupQd input").css({"color":"black"});
        // $("#editCoupQd .tipYearRepeat span").remove();
        // $("#editCoupQd .tipNameRepeat span").remove();
        var labelDiv=$("#editCoupQd label");
        for(var i=0;i<labelDiv.length;i++){
            if($("#editCoupQd label").eq(i).attr("id")){
                $("#editCoupQd label").eq(i).remove();
            }
        }
        $("#editCoupQd").window('open');
    }else{
        swal('请先选择编辑清单', '', 'error');
    }
}
//提交编辑后的清单
function submitEditCoup() {
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    var rowIndex = $('#couqd').datagrid('getRowIndex',row);//获取选中行的行数索引
    var qdName=$("#editCoupQd #coupEditQdName").val();//获取编辑后的数据 清单名称
    var qdYear=$("#editCoupQd #coupEditQdYear").val();//年份
    var qdMark=$("#editCoupQd #coupEditQdMark").val();//描述
    var param={};//设置接口参数
    param.userId=userId;
    param.couplingName=qdName;
    param.couplingId = row.esCouplingId;
    param.couplingDesc = qdMark;
    param.couplingYear = qdYear;
    param.method="update_coupling";
    var myYear=$("#editCoupQd #coupEditQdYear").val()
    var myName=$("#editCoupQd #coupEditQdYear").val()
    if(myName.length>0 && myName.length<=20&&myName!="不可超过15个字符（必填）"){
        if(myYear>=1990&&myYear<=2100){//判断年份是否符合要求 符合提交编辑后数据
            $("#formQd").submit(
                ajaxPost('/NativeAndNation/doPost',param).success(function(res){
                    if(res.status==0){
                        $("#couqd").datagrid('updateRow',{//更新清单列表编辑后的数据
                            index: rowIndex,
                            row: {
                                esCouplingName:qdName,
                                esCouplingYear:qdYear,
                                esCouplingDesc:qdMark
                            }
                        })
                        innitdata()
                    }else{
                        swal('参数错误', '', 'error');
                    }
                })
            )
            $("#editCoupQd").window('close');
        }else{
            swal('年份错误', '', 'error');
            // $("#editCoupQd .tipYearRepeat span").remove();
            // $("#editCoupQd .tipYearRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的年份</span>");
        }
    }else{
        swal('请输入符合要求的名称', '', 'error');
        // $("#editCoupQd .tipNameRepeat span").remove();
        // $("#editCoupQd .tipNameRepeat").append("<span><i class='im-warning' style='color:red'></i>请输入符合要求的名称</span>");
    }
}
//删除选中的耦合清单
function coupDelete(){
    var row = $('#couqd').datagrid('getSelected');//获取所有选中的清单数据
    var rowIndex = $('#couqd').datagrid('getRowIndex', row);
    swal({
        title: "确定要删除?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        cancelButtonText: "取消",
        closeOnConfirm: false
    }, function() {
        ajaxPost('/NativeAndNation/doPost',{"couplingId":row.esCouplingId,"method":"delete_coupling"}).success(function(res){
            if(res.status==0){
                $('#couqd').datagrid('deleteRow', rowIndex);
                $('#couqd').datagrid('reload');//删除后重新加载下
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
//查看详情的窗口
$("#coupDetail").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'详细信息',
    border:false,
    closed:true,
    cls:"cloudui"
})
function closeDetail() {
    $("#coupDetail").window("close")
}
//点击查看耦合清单详细信息
function viewDetail(coupId) {
    $("#coupDetail input").innerText="";
    $("#coupDetail").window("open")
    var rowsAll=$("#couqd").datagrid("getRows");
    var checkRow;
    if(coupId){
        for(var i=0;i<rowsAll.length;i++){
            if(rowsAll[i].esCouplingId==coupId){
                checkRow=rowsAll[i];//获取选择的耦合清单的数据
            }
        }
    }
    var param={};//详细信息接口的参数
    param.userId=userId;
    param.method="lookByCouplingId";
    param.couplingId=checkRow.esCouplingId,
    param.nationId=checkRow.esCouplingNationId,
    param.nativesId=checkRow.esCouplingNativeId,
    param.nativeTpId=checkRow.esCouplingNativetpId,
    param.meicCityConfig='',
        ajaxPost('/NativeAndNation/doPost',param).success(function (res) {
            if(res.status==0){
                var data=res.data.data;
                //获取耦合清单的详细信息并且渲染在页面上面
                $("#coupName").val(data.esCouplingName);
                $("#coupName").attr('title',data.esCouplingName);
                $("#coupYear").val(data.esCouplingYear);
                $("#coupYear").attr('title',data.esCouplingYear);
                $("#coupDes").val(data.esCouplingDesc);
                $("#coupDate").val(moment(data.addTime).format("YYYY-MM-DD"));
                $("#coupDate").attr('title',moment(data.addTime).format("YYYY-MM-DD"));
                $("#coupLocal").val(data.nativeTpName);
                $("#coupLocal").attr('title',data.nativeTpName);
                $("#coupNation").val(data.nationName);
                $("#coupNation").attr('title',data.nationName);
            }else{
                swal("参数错误",'',"erro")
            }
        })
}

//点击耦合清单
function coupSetQd(coupId) {
    //点击耦合配置按钮，获取所选的耦合配置信息
    var rowsAll=$("#couqd").datagrid("getRows");
    var checkRow;
    if(coupId){
        for(var i=0;i<rowsAll.length;i++){
            if(rowsAll[i].esCouplingId==coupId){
                checkRow=rowsAll[i];
            }
        }
    }
    coupingQd=checkRow;
    //隐藏耦合列表 此案是耦合过程的第一步
    $(".tableBox").hide();
    $(".coupSet").show();
    $(".cloudui .coupSetTitleList").children("li").removeClass("active");
    $(".cloudui .coupSetTitleList").children("li").eq(0).addClass("active");
    $(".coupSetCon").eq(0).show();//隐藏其他步骤
    $(".coupSetCon").eq(1).hide();
    $(".coupSetCon").eq(2).hide();
    window.setTimeout(qgqdTable(),100)
    $("#prevCoup").hide();//上一步按钮
    $("#nextCoup").show();//下一步按钮
    $(".navRight").hide();
}
var checkCity=[];
var cityData=[];
var industryData=[];
var localQdId=[];
function nextCoup(){//点击下一步按钮
    var conText=$(".coupSetTitle .coupSetTitleList .active div").text()
    if(conText=="第一步"){
        checkQgQd=$("#qgqd").datagrid("getSelected");
        if(checkQgQd!=''&&checkQgQd!=null&&checkQgQd!=undefined){
            $(".cloudui .coupSetTitleList").children("li").eq(0).removeClass("active");
            $(".cloudui .coupSetTitleList").children("li").eq(1).addClass("active");
            $("#prevCoup").show();//上一步按钮
            $(".coupSetCon").eq(0).hide();//隐藏其他步骤
            $(".coupSetCon").eq(1).show();
            $(".coupSetConSecond").layout()//耦合第二步进行渲染
            $(".coupSetCon").eq(2).hide();
            mbSelect();

        }else{
            swal('请先选择全国清单', '', 'error');
        }
    }else if(conText=="第二步"){
        //获取所有选择的本地清单
        localQd=$("#localTable").datagrid("getSelections");
        for(var i=0;i<localQd.length;i++){
            localQdId.push(localQd[i].esNativeId);
        }
        if(cityData.length>0){
            cityData=[]
        }
        if(industryData.length>0){
            industryData=[]
        }
        globelCheckedQd=[];//点击下一步 进行第三步 所有数据清空
        allCity=[];
        globelCheckedCity=[];

        if(localQd!=''&&localQd!=null&&localQd!=undefined){
            //耦合过程中各个按钮的样式
            $(".cloudui .coupSetTitleList").children("li").eq(1).removeClass("active");
            $(".cloudui .coupSetTitleList").children("li").eq(2).addClass("active");
            $("#prevCoup").show();//最后一步 上一步按钮
            $("#nextCoup").hide();//最后一步 隐藏下一步按钮
            $(".coupSetCon").eq(0).hide();//隐藏其他步骤
            $(".coupSetCon").eq(1).hide();
            $(".coupSetCon").eq(2).show();
            //通过选择全国清单个本地清单的ID  获取耦合的城市和行业信息
            ajaxPost('/NativeAndNation/doPost',{"userId":userId,"method":"findCityAndIndustryById","nationId":checkQgQd.esNationId,"nativesId":JSON.stringify(localQdId),"nativeTpId":mbArray[$(".cloudui .coupSetCon #coupSetMb").val()].esNativeTpId}).success(function (res) {

                if(res.status==0){
                    if(res.data.status==0){
                        if(res.data.data.cityNames!=""&&res.data.data.cityNames!=null&&res.data.data.cityNames!=undefined){
                            var cityNames=res.data.data.cityNames;
                            // cityData=[{cityId:3,cityName:"城市一"},{cityId:1,cityName:"城市1"},{cityId:2,cityName:"城市2"}];

                            // 城市数据添加到数组中
                            $.each(cityNames, function (id, cityNames) {
                                cityData.push({                         //获取城市名称、编码
                                    "cityId":id,
                                    "cityName":cityNames,
                                });
                            });
                        }else{
                            swal('该清单下面无城市数据，请您重新选择本地清单', '', 'error');
                        }
                        if(res.data.data.industryNames!=""&&res.data.data.industryNames!=null&&res.data.data.industryNames!=undefined){
                            var industryNames=res.data.data.industryNames;
                            //行业数组添加到数组中
                            $.each(industryNames, function (id, industryNames) {
                                industryData.push({               //获取行业信息
                                    "industryNamesId":id,
                                    "industryNames":industryNames,
                                });
                            });
                        }else{
                            swal('该清单下面无行业数据，请您重新选择本地清单', '', 'error');
                        }
                        //城市数据添加到弹窗中
                        // $("#cityOption span").remove()
                        // var cityList="";
                        // for(var i=0;i<cityData.length;i++){
                        //     cityList+="<span><input type='radio' name='cityName' value=\'"+cityData[i].cityId+"\' /><label>"+cityData[i].cityName+"</label></span>";
                        // }
                        // $("#cityOption").append(cityList);
                        //打开城市选择窗口
                        $("#citySelect").window('open')
                        cityTable(cityData,0)//初始化耦合列表
                        // coupCity(cityData,industryData)
                    }else{
                        swal(res.data.msg, '', 'error');
                    }
                }else{
                    swal('参数错误', '', 'error');
                }
            })
        }else{
            swal('请先选择本地清单', '', 'error');
        }
    }
}
//选择城市进行提交
var cityCurren={};
function submitCity(){
    // $("input[name=cityName]").removeAttr("checked");
    var row=$("#cityOptionTable").datagrid("getSelected");
    //选择的城市添加到数组
    if(row){
        //获取选择的城市的信息
        checkCity.push({
            "cityId":row.cityId,
            "cityName":row.cityName
        })
        //当前选择的城市
        cityCurren.cityId=row.cityId;
        cityCurren.cityName=row.cityName;
    }else{
        swal("请选择城市","","error");
    }

    cityTable(cityData,row);//根据点击过的城市 再次生成城市表单信息
    coupCity(cityCurren,industryData)//根据选择的城市 行业生成耦合表格
    $("#citySelect").window("close");
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
        localQdId=[];
    }else if(conText=="第二步"){
        $(".cloudui .coupSetTitleList").children("li").eq(1).removeClass("active");
        $(".cloudui .coupSetTitleList").children("li").eq(0).addClass("active");
        $("#prevCoup").hide();//上一步按钮
        $("#nextCoup").show();//最后一步 隐藏下一步按钮
        $(".coupSetCon").eq(0).show();//隐藏其他步骤
        $(".coupSetCon").eq(1).hide();
        $(".coupSetCon").eq(2).hide();
        // checkQgQd={};
    }
}

//耦合措施第二步
var mbArray
function mbSelect() {
    ajaxPost("/NativeAndNation/doPost",{"userId":userId,"method":"find_couplingNativeTp"}).success(function (res) {
        mbArray=[];//每次点击下一步 都对本地模板进行初始化
        mbArray=res.data.data.rows;
        $(".cloudui .coupSetCon .coupSetMb option").remove()
        if(res.status==0){
            for(var i=0;i<mbArray.length;i++){
                var mbDiv;
                if(mbArray[i].isVerify==1){
                    mbDiv= $('<option value="'+i+'">'+mbArray[i].esNativeTpName+'</option>');
                    $(".cloudui .coupSetCon .coupSetMb").append(mbDiv);
                }
            }
        }else{
            swal('参数故障', '', 'error')
        }
        var mbIndex=$(".cloudui .coupSetCon #coupSetMb").val();
        //根据模板信息 生成本地清单信息
        window.setTimeout(localTable(mbIndex),100);
    })
}
//本地模板选择变化时
$(".cloudui #coupSetMb").on("change",function (e) {
    var index=$(e.target).val();
    localTable(index);
})
//根据清单模板的ID 生成本地清单表单
function  localTable(value) {
   var mbQd= mbArray[value];
    $("#localTable").datagrid({
        url:'/ampc/NativeAndNation/doPost',
        method:'post', //ajax 请求远程数据方法
        dataType: "json",
        columns:[[  //表头
            {field:"ck",checkbox:true},
            {field:"esNativeName",title:"清单名称",width:200,align:'cneter'},
            {field:"esComment",title:"清单备注",width:80,align:'cneter'},
            {field:"esNativeYear",title:"年份"},
            {field:"addTime",title:"创建时间",formatter:function(value,row,index){
                return  moment(value).format("YYYY-MM-DD");
            },align:'cneter'},
        ]],
        loadFilter:function (data) { //过滤数据，转换成符合格式的数据
            var valData=data.data.data.rows;
            var valDataAll=[];
            for(var i=0;i<valData.length;i++){
                if(valData[i].isVerify==1){
                    valDataAll.push(valData[i]);
                }
            }
            return valDataAll;
        },
        pagination: true, // 在表格底部显示分页工具栏
        pageSize:20,  //页面里面显示数据的行数
        pageNumber: 1, // 页数
        pageList: [20, 30,40], //页面可以进行选择的数据行数
        contentType: "application/json",
        queryParams:function (params) { //ajax 传递的参数  分页
            var data = {};
            data.userId = userId;
            data.method = 'find_couplingNatives';
            data.nativeTpId =mbQd.esNativeTpId;
            data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
            data.pageNumber=params.pageNumber  //初始化页面的页码
            return {"token": "", "data": data};
        }
    })
}
// 创建 输入框获得焦点
$(".cloudui .rwCon .qdContent .qdName").focus(function () {//名称获取焦点
    if($(this).val()=="不可超过15个字符（必填）"){
        $(this).val("");
        $(this).css({"color":"black"})
    }
})
$(".cloudui .rwCon .qdContent .qdYear").focus(function () {//年份获取焦点
    if($(this).val()=="请输入1990-2100之间的年份"){
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
        $(this).val("请输入1990-2100之间的年份")
        $(this).css({"color":"#757575"})
    }
})
//耦合第三步 参数是当前选择的城市和所有行业 根据这些生成表格
var checkQd=[];
function coupCity(cityCurren,industryData) {
    $("#coupCityTable").empty()//每次选择新的城市生成的表格  必须先清空以前的数据
    var coupFirst="";
    //根据城市信息 生成表单
    coupFirst+="<tr><td class='cityName' rowspan=\'"+industryData.length+"\'>"+cityCurren.cityName+"</td><td class='industryName'>"+industryData[0].industryNames+"</td><td class='industryQd'></td></tr>"
    if(industryData.length>1){
        for(var i=1;i<industryData.length;i++){
            var coupOther="<tr><td class='industryName'>"+industryData[i].industryNames+"</td><td class='industryQd'></td></tr>";
            coupFirst+=coupOther;
        }
    }

    $("#coupCityTable").append(coupFirst);
    // 添加下拉框数据
    var industrySelect="<select class='selectQd'>"
    industrySelect+="<option value='w_0'>无</option>"
    industrySelect+="<option value='qg_1'>"+checkQgQd.esNationName+"</option>"
    for(var m=0;m<localQd.length;m++){
        industrySelect+="<option value=\'"+localQd[m].esNativeId+"\'>"+localQd[m].esNativeName+"</option>"
    }
    industrySelect+="</select>";
    $(".industryQd").append(industrySelect);

    for(var a=0;a<industryData.length;a++){//初始化 所有下拉菜单默认无清单
        $(".selectQd").eq(a).val('w_0')
    }
    //根据选择的城市 如果当前城市未选择的 则初始化表单 如果当前选择的城市是已经选择过的 则根据以前选择的对应的行业 清单 对表单进行初始化
    for(var b=0;b<globelCheckedQd.length;b++){
        if(globelCheckedQd[b][0].regionId==cityCurren.cityId){//判断点击城市是否是已经选过的城市

            // //如果点击的是已经选择过得城市  把已经选择的结果整合在一起
            // checkQd=[];
            // for(var n=0;n<globelCheckedQd.length;n++){
            //     if((globelCheckedQd[n].regionId)==cityCurren.cityId){
            //         checkQd.push({
            //             industry:globelCheckedQd[n].sectorName,
            //             qd:globelCheckedQd[n].meicCityId
            //         })
            //
            //     }
            // }
            //根据前面多次的选择  对生成的表格清单进行数据初始化
            for(var q=0;q<industryData.length;q++){
                $(".selectQd").eq(q).val('qg_1')
                for(var l=0;l<globelCheckedQd[b].length;l++){
                    if((industryData[q].industryNames)==(globelCheckedQd[b][l].sectorName)){
                        if(globelCheckedQd[b][l].meicCityId===""){
                            $(".selectQd").eq(q).val('w_0')
                        }else{
                            $(".selectQd").eq(q).val(globelCheckedQd[b][l].meicCityId)
                        }
                    }
                }
            }
            break;
        }
    }

}
//城市选择窗口
$("#citySelect").window({
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'选择城市',
    border:false,
    closed:true,
    cls:"cloudui"
})
//数组去重
Array.prototype.unique = function(){
    var res = [];
    var json = {};
    for(var i = 0; i < this.length; i++){
        if(!json[this[i]]){
            res.push(this[i]);
            json[this[i]] = 1;
        }
    }
    return res;
}

//点击保存按钮保存耦合清单 全国清单 本地清单 企业 行业 等信息
var globelCheckedCity=[];//耦合后涉及的城市信息
var globelCheckedQd=[];//保存选择的 行政-行业-清单 一一对应
var allCity=[];//只要选择了清单 获取的所有城市的信息 包括重复的
var singleCheckCity=[];
var localQdId=[];
var checkCityName=[];
var meicCityConfig=[]
var globelCheckedQdCurr=[];
function saveAllId(){ //选好清单以后进行保存
    globelCheckedQdCurr=[];
    for(var i=0;i<industryData.length;i++){
        if($(".selectQd").eq(i).val()!="qg_1"){
            if($(".selectQd").eq(i).val()=="w_0"){
                globelCheckedQdCurr.push({
                    "meicCityId":'',
                    //"regionId":(cityCurren.cityId).substring(0,4) ,
                    "regionId":cityCurren.cityId,
                    "sectorName":industryData[i].industryNames
                })
            }else{
                globelCheckedQdCurr.push({
                    "meicCityId":$(".selectQd").eq(i).val(),
                    "regionId":cityCurren.cityId,
                    // "regionId":cityCurren.cityId,
                    "sectorName":industryData[i].industryNames
                })
            }
            allCity.push(cityCurren.cityId)//获取选择了清单所有的城市，包含重复的
        }
    }
    if(!globelCheckedQd[0]){
        globelCheckedQd.push(globelCheckedQdCurr);
    }else{
        for(var m=0;m<globelCheckedQd.length;m++){
            if(globelCheckedQd[m][0].regionId==cityCurren.cityId){
                globelCheckedQd[m]=globelCheckedQdCurr;
                break;
            }else{
                if(m==globelCheckedQd.length-1){
                    globelCheckedQd.push(globelCheckedQdCurr);
                }
            }
        }
    }

    globelCheckedCity=allCity.unique()//对所有耦合涉及的城市进行去重


    for(var nm=0;nm<checkCity.length;nm++){
        checkCityName.push(checkCity[nm].cityId);
    }
    singleCheckCity=checkCityName.unique()//去重
    $("#citySelect").window("open");
}
//城市表格生成
var allCheckRow=[]
function cityTable(cityData,checkRow) {
    allCheckRow.push(checkRow)
    //生成城市选择表格
    $("#cityOptionTable").datagrid({
        columns:[[
            {field:"ck",checkbox:true},
            {field:"cityName",title:"城市"},
            {field:"zt",title:"状态",formatter:function (value,row,index) {
                for(var i=0;i<allCheckRow.length;i++){
                    if(allCheckRow[i]==row){//根据选过的城市信息 初始化表格的状态栏
                        return "<span><i class='en-checkmark' style='color:red;'></i></span>";
                    }
                }
            },width:100}
        ]],
        data:cityData,
        singleSelect:'true'
    })
}
//点击数据进行提交
function submitCheckQd() {
    // meicCityConfig=globelCheckedQd;
    // var newArray=[]
    for(var i=0;i<globelCheckedQd.length;i++){
        for(var j=0;j<globelCheckedQd[i].length;j++){
            // var meicCityConfig=[]
            meicCityConfig.push(globelCheckedQd[i][j]);
        }
    }
    ajaxPost('/NativeAndNation/doPost',{"userId":userId,
        "method":'saveCoupling',
        "nationId":checkQgQd.esNationId, //第一步全国清单ID
        "nativesId":localQdId, //第二步本地清单ID
        "CouplingCity":globelCheckedCity, //耦合涉及的城市
        "nativeTpId":mbArray[$(".cloudui .coupSetCon #coupSetMb").val()].esNativeTpId,//第二步模板ID
        "couplingId":coupingQd.esCouplingId,//耦合清单的ID
        "meicCityConfig":JSON.stringify(meicCityConfig),}).success(function (res) {
        if(res.status==0){
            if(res.data.data){
                if(res.data.data.msg='true'){
                    swal('耦合数据提交成功', '', 'success');
                }else{
                    swal('耦合数据提交失败', '', 'error');
                }
            }
        }else{
            swal('参数错误', '', 'error');
        }
    })
    //点击提交以后页面回到初始化界面
    $("#citySelect").window("close")
    $(".tableBox").show();//点击提交以后页面返回首页
    $(".coupSet").hide();
    $(".navRight").show();
    innitdata();
}
//新建清单名字校验
$("#creatCoupQd #coupQdName").blur(
    function () {
        ajaxPost('/NativeAndNation/doPost',{
            "userId":userId,
            "method":'verifyByCouplingName',
            "couplingName":$("#creatCoupQd #coupQdName").val()
        }).success(function (res) {
            if(res.data.data.msg==true){
                swal('清单名称重复，请再次输入名称', '', 'error');
                // $("#creatCoupQd .tipNameRepeat span").remove();
                // $("#creatCoupQd .tipNameRepeat").append("<span><i class='im-warning' style='color: red'></i>该名称已被使用</span>");
            }
        })
    }
)

    $("#formCoup").validate({
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
    $("#formEditCoup").validate({
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
