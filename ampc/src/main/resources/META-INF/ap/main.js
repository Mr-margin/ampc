// 导航
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">首页</span>');

//任务列表
$("#renwu").datagrid({
    url:"/ampc/mission/get_mission_list",
    method:'post', //ajax 请求远程数据方法
    dataType: "json",
    columns:[[  //表头
        {field:"missionId",title:"ID",align:'cneter'},
        {field:"missionName",title:"任务名称",align:'cneter'},
        {field:"domainName",title:"模拟范围",align:'cneter'},
        {field:"esCouplingName",title:"清单",align:'cneter'},
        // {field:"missionStatus",title:"类型",align:'cneter'},
    ]],
    loadFilter:function (data) { //过滤数据，转换成符合格式的数据
        console.log(data);
        return data.data.rows;
    },
    singleSelect: true,//设置True 将禁止多选
    height:'100%',
    contentType: "application/json",
    queryParams:function (params) { //ajax 传递的参数  分页
        var data = {};
        data.userId = userId;
        data.queryName="";
        data.sort="addTime desc";
        data.missionStatus="";
        data.pageSize=params.pageSize; //初始化页面上面表单的数据行数
        data.pageNum=params.pageNumber  //初始化页面的页码
        return {"token": "", "data": data};
    },
})
//情景列表
ajaxPost('/NativeAndNation/doPost',{
    "userId":userId,
    "method":"find_natives",
    "pageNum": 1,
    "pageSize": 10,
}).success(function(data){
    //给每个子节点添加标题
    var rowDate=data.data.data.rows;
    for(var i=0;i<rowDate.length;i++){
        rowDate[i].children.unshift({esNativeTpName:"清单名称",esNativeTpYear:"年份",addTime:"创建时间",esUploadTpTime:"上传时间",filePath:"路径",esComment:"备注",his:"使用状态",isVerify:"状态",actor:"操作",id:rowDate[i].id+"title"})
    }
    $("#qingdan").treegrid({
        idField:'id',//通过id区分子节点父节点
        treeField:'esNativeTpName',//树形结构分支节点
        data:rowDate,
        lines:false,
        showHeader: false,
        animate:true,
        columns:[[  //表头
            {field:"esNativeTpName",title:"清单模板名称",formatter: function (value) {
                return "<span title='" + value + "'>" + value + "</span>";}},
            {field:"esNativeTpYear",title:"年份"},
            {field:"addTime",title:"创建时间",formatter:function(value,row,index){
                if(isNaN(value)){
                    return "<span>创建时间</span>";
                }else{
                    return  moment(value).format("YYYY-MM-DD");
                }
            }},
            {field:"esUploadTpTime",title:"上传时间",formatter:function(value,row,index){
                if(isNaN(value)){
                    return "<span>上传时间</span>";
                }else{
                    return  moment(value).format("YYYY-MM-DD");
                }
            }},
            {field:"his",title:"使用状态",width:100},
            {field:"isVerify",title:"状态",width:100,formatter:function(value,row,index){
                if(value=="1"){
                    return "<span style='color: #009943'>已校验</span>"
                }else if(value=="0"){
                    return "<span style='color: #dc3f35'>未校验</span>"
                }else{
                    return value;
                }
            }},
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
    })
})