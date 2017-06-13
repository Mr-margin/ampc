/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight"><span class="navRight qdnavRight"><button class="qdCreat" onclick="creat()">新建</button></span>');

var Storage = localStorage;
$(document).ready(function(){
	getInfo();
});

var domainData=[]//用于 存储domain列表数据

/**查询接口**/
function getInfo(){
	var  url = '/Domain/findAll';
	ajaxPost(url,{
		'userId': userId
	}).success(function (res) {
		console.log(res);
		domainData=res.data;
		var _temp=domainData.slice(0,10);
		$.each(_temp,function(key,value){
			findPull(value);
		});
		$('#pp').pagination({
		    total:res.data.length,
		    pageSize:10,
		    onSelectPage:function(pageNumber, pageSize){
		    	$(".domain_box tbody").empty();
		    	if(pageNumber*pageSize<domainData.length){
		    		var _temp=domainData.slice((pageNumber-1)*pageSize);
		    		$.each(_temp,function(key,value){
		    			findPull(value);
		    		});
		    	}else{
		    		var _temp=domainData.slice((pageNumber-1)*pageSize,pageNumber*pageSize);
		    		$.each(_temp,function(key,value){
		    			findPull(value);
		    		});
		    	}
		    	
		    }
		});
    });
}  

function selectPage(){

}

function findPull(value){
	var employStatus = value.employStatus
	var world = '';
	if(employStatus == 1){
		world = '已使用';
	}else if(employStatus == 2){
		world = '未使用'; 
	}
	var dom = '';
	dom += '<tr class="tr_'+value.domainId+'">';
	dom += '<td class="col-3">'+value.domainName+'</td>';
	dom += '<td class="col-4">'+value.domainDoc+'</td>';
	dom += '<td class="col-2">'+world+'</td>';
	dom += '<td class="col-3">';
	dom += '<button class="btn-blue" onclick="examine(this)" domain_id="'+value.domainId+'">查看</button>';
	dom += '<button class="btn-green" onclick="update(this)" domain_id="'+value.domainId+'">编辑</button> ';
	dom += '<button class="btn-yellow" onclick="" domain_id="'+value.domainId+'">开启</button> ';
	dom += '<button class="btn-red" onclick="delDomain(this)" domain_id="'+value.domainId+'">删除</button>';
	dom += '</td>';
	dom += '</tr>';
	$('.domain_box table tbody').append(dom);
}
function examine(th){
	var domain_id = $(th).attr('domain_id');
	var msg = {
	    'id': 'domain',
	    'content': {
	    	'domain_id': domain_id,
	    }
	};
	vipspa.setMessage(msg);
	Storage.setItem('domain_id',domain_id);
	location.hash = '#/domain_details';
}

function update(th){
	var domain_id = $(th).attr('domain_id');
	var msg = {
	    'id': 'domain_update',
	    'content': {
	    	'domain_id': domain_id,
	    }
	};
	vipspa.setMessage(msg);
	Storage.setItem('domain_id_up',domain_id);
	location.hash = '#/domain';
}

function creat(){
    $("#creat_domain").window('open');
}

$("#creat_domain").window({
    width:600, 
    collapsible:false, 
    maximizable:false,
    minimizable:false,
    modal:true,
    shadow:false,
    title:'新建清单',
    border:false,
    closed:true,
    cls:"cloudui"
})

function submitCreat(){
	var domainName = $('.domain_name').val();
	var domainDoc = $('.domain_doc').val();
	var url = '/Domain/save_domain';
	if(domainName == ''){
		swal("请填写名称");
	}else if(domainDoc == ''){
		swal("请填写备注");
	}else{
		ajaxPost(url,{
			'userId':userId,
			'domainName':domainName,
			'domainDoc':domainDoc
		}).success(function(){
			$("#creat_domain").window('close');
			swal("创建成功");
		});
	}
}

function domainClaear(){
	$('.domain_name').val('');
	$('.domain_doc').val('');
}

function delDomain(th){
	var url = "/Domain/deleteDomain";
	var domain_id = $(th).attr('domain_id');
	ajaxPost(url,{
		'userId':userId,
		'domainId':domain_id
	}).success(function(){
		$('.tr_'+domain_id).remove();
		swal("删除成功");
	});
}

