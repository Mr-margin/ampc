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
		domainData=res.data;
		var _temp=domainData.slice(0,10);
		$.each(_temp,function(key,value){
			findPull(value);
		});
		status();
		$('#pp').pagination({
		    total:res.data.length,
		    pageSize:10,
		    onSelectPage:function(pageNumber, pageSize){
		    	$(".domain_box tbody").empty();
		    	if(pageNumber*pageSize<domainData.length){
		    		var _temp=domainData.slice((pageNumber-1)*pageSize);
		    		$.each(_temp,function(key,value){
		    			findPull(value);
		    			status();
		    		});
		    	}else{
		    		var _temp=domainData.slice((pageNumber-1)*pageSize,pageNumber*pageSize);
		    		$.each(_temp,function(key,value){
		    			findPull(value);
		    			status();
		    		});
		    	}
		    	
		    }
		});
    });
}  

function findPull(value){
	$('.todo,.default,.b_del').show();
	var disposeStatus = value.disposeStatus;
	var employStatus = value.employStatus; 
	var domain_id = value.domainId;
	var world = '';
	var domain_work = '';
	if(employStatus == '1'){
		domain_work = '使用过';
	} 
	if(employStatus == '2'){
		domain_work = '未使用'; 
	}
	if(disposeStatus == '1'){
		world = '待编辑';
	}
	if(disposeStatus == '2'){
		world = '处理中'; 
	} 
	if(disposeStatus == '3'){
		world = '处理成功'; 
	} 
	if(disposeStatus == '4'){
		world = '处理失败'; 
	}

	var dom = '';
	dom += '<tr class="tr_'+value.domainId+'">';
	dom += '<td class="col-3">'+value.domainName+'</td>';
	dom += '<td class="col-4">'+value.domainDoc+'</td>';
	dom += '<td class="col-1 status_domain">'+domain_work+'</td>';
	dom += '<td class="col-1 do_domain">'+world+'</td>';
	dom += '<td class="col-3 buttonclick">';
	dom += '<button class="btn-blue look" onclick="examine(this)" domain_id="'+value.domainId+'">查看</button>';
	dom += '<button class="btn-green todo" onclick="update(this)" domain_id="'+value.domainId+'">编辑</button> ';
	dom += '<button class="btn-yellow default" onclick="defaultSelect(this)" domain_id="'+value.domainId+'">开启</button> ';
	dom += '<button class="btn-red b_del" onclick="delDomain(this)" domain_id="'+value.domainId+'">删除</button>';
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
	var url1 = '/Domain/nameLike';
	if(domainName == ''){
		swal("请填写名称");
	}else if(domainDoc == ''){
		swal("请填写备注");
	}else{
		ajaxPost(url1,{
			"domainName": domainName,
 			"userId":userId
		}).success(function(res){
			console.log(res);
			if(res.data == '名称重复'){
				swal("名称重复");
			}else{
				ajaxPost(url,{
					'userId':userId,
					'domainName':domainName,
					'domainDoc':domainDoc
				}).success(function(){
					$("#creat_domain").window('close');
					swal("创建成功");
					$('tbody tr').remove();
					getInfo();
				});
			}
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

function defaultSelect(th){
	var url = '/Domain/Valid';
	var domain_id = $(th).attr('domain_id');
	ajaxPost(url,{
		'userId':userId,
		'domainId':domain_id
	}).success(function(){
		if($(th).text() == '开启'){
			$('.default').text('开启')
			$(th).text('停用');
			$('.default').css('background-color','#f0ad4e');
			$(th).css('background-color','#ff8002');
			swal("启用成功");
		}else{
			$(th).text('开启');
			$(th).css('background-color','#f0ad4e');
		}
	});
}

function status(){
	var domainStatus = '';
	var domainDo = '';
	$('tbody tr').each(function(key,value){
		domainStatus = $(value).children('.status_domain').text();
		domainDo = $(value).children('.do_domain').text();
		if(domainStatus == '已使用'){
			$(value).children().children('.todo,.b_del').hide();
		}
		if(domainDo == '处理中'){
			$(value).children().children('.todo,.default,.b_del').hide();
		}
		if(domainDo == '处理失败'){
			$(value).children().children('.default').hide();
		}
		if(domainDo == '待编辑'){
			$(value).children().children('.default').hide();
		}
		console.log(domainStatus);
	});
}
