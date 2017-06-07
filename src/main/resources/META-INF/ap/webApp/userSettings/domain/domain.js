/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight"><button class="qdCreat" onclick="update();">编辑</button></span><span class="navRight qdnavRight"><button class="qdCreat" onclick="creat();">新建</button></span>');


$(document).ready(function(){
	$('.d02').hide();
	$('.d03').hide(); 
	$('.d04').hide();
	$('.add_two').click(function(){
		$('.d02').show();
		$('.box-body').attr('max_dom','1');
		$('.fa-times').show();
	});
	$('.hade').click(function(){
		$('.d02').hide();
		$('.d03').hide();
		$('.d04').hide();
	});
	$('.add_two2').click(function(){
		$('.d03').show();
		$('.box-body').attr('max_dom','2');
		$('.fa-times').show();
	});
	$('.hade2').click(function(){
		$('.d03').hide();
		$('.d04').hide();
		$('.box-body').attr('max_dom','1');
	});
	$('.add_two3').click(function(){ 
		$('.d04').show();
		$('.box-body').attr('max_dom','3');
		$('.fa-times').show();
	}); 
	$('.hade3').click(function(){
		$('.d04').hide();
		$('.box-body').attr('max_dom','2');
	});
	$("select").change(function(){
		resolution();
		submitSave();
	});
	$('input').change(function(){
		submitSave();
	});
	getInfo();
	resolution();
});


/**创建**/
function creat(){
	$("#creat_Domain").window('open');

	if($('.domainName').val() == ''){
		$('#creat_Domain button').attr("disabled", true);
	}else{
		$('#creat_Domain button').attr('disabled',false);
	}
}
$("#creat_Domain input").change(function(){
	if($('.domainName').val() == ''){
		$('#creat_Domain button').attr('disabled',true);
	}else{
		$('#creat_Domain button').attr('disabled',false);
	}
});

/**编辑**/
function update(){
	$("#update_Domain").window('open');
	$('#update_Domain table tr').remove();
	var  url = '/Domain/findAll';
	ajaxPost(url, {
		userId: userId
	}).success(function (res) {
		$.each(res.data,function(key,value){
			updatePull(value);
		});
        
    }) 

}

/**删除**/
function deleteDomain(){
	var url = '/Domain/deleteDomain';
	var domain_id = $('.del_domain').attr('domain_id');
	$('.box-body').hide();
	$('box-body input').val('');
	$('select').find("option[value='0']").attr('selected','selected');
	ajaxPost(url,{
		'userId':userId,
		'domainId':domain_id
	}).success(function(res){
	});
}

/**点击编辑弹出框**/
function updatePull(value){
	var dom = '';
	dom += '<tr class="tr'+value.domainId+'">';
	dom +='<td><input class="choose" domain_id="'+value.domainId+'" type="radio" name="update"></td>';
	dom += '<td class="name d_name_'+value.domainId+' col-4">'+value.domainName+'</td>';
	dom += '<td class="doc d_doc_'+value.domainId+' col-4">'+value.domainDoc+'</td>';
	dom += '<td class="u_d_n btn-blue" onclick="updateDomainName(this);">修改</td>';
	dom += '</tr>';
	$('#update_Domain table').append(dom);
	if(value.haveMission == '已使用'){
		$('.tr'+value.domainId).remove();
	}
}

/**修改domain名称**/
function updateDomainName(val){
	$("#update_Domain_name").window('open');
	var domain_id = $(val).siblings().children('.choose').attr('domain_id');
	var domainDoc = $(val).siblings('.name').text();
	var domainName = $(val).siblings('.doc').text();
	$('.u_domainDoc').val(domainDoc);
	$('.u_domainName').val(domainDoc);
	$('.u_domainName').attr('domain_id',domain_id);
}

/**编辑内容选择确定按钮**/
function updateChoose(){
	$("#update_Domain").window('close');
	var domain_id = $('#update_Domain input[name="update"]:checked ').attr('domain_id');
	$('.box-body').show();
	$('.box-body').attr('domain_id',domain_id);
	$('.box-body input').attr('readonly',false);
	$('.i_parent_start1').attr('readonly',true);
	$('.j_parent_start1').attr('readonly',true);
	$('.dx1').attr('readonly',true);
	$('.dx2').attr('readonly',true);
	$('.i_parent_start1').val('1');
	$('.j_parent_start1').val('1');
	$('.fa-times').hide();
	var  url = '/Domain/findAll';
	ajaxPost(url, {
		userId: userId
	}).success(function (res) {
		$('.fa-times').show();
		$.each(res.data,function(key,value){
			if(value.domainId == domain_id){
				pullPage(value);
				$('.add_two,.add_two2,.add_two3').show();
				$('.del_domain').show();
			}
		});
    });
}

/**创建提交按钮**/
function domainPost(){
	var url = '/Domain/save_domain';
	var domainDoc = $('.domainDoc').val();
	var domainName = $('.domainName').val()
	ajaxPost(url,{
		userId:userId,
		domainDoc:domainDoc,
		domainName:domainName
	}).success(function(res){
		var domain_id = res.data.DomainId;
		$("#creat_Domain").window('close');
		$('.panel-title').text(domainName);
		$('.box-body').attr('domain_id',domain_id);
		$('.box-body').show();
		$('.box-body input').val('');
		$('.add_two,.add_two2,.add_two3').show();
		$('select').find("option[value='0']").attr('selected','selected');
		$('.del_domain').show();
		$('.box-body input').attr('readonly',false);
		$('.i_parent_start1').attr('readonly',true);
		$('.j_parent_start1').attr('readonly',true);
		$('.dx1').attr('readonly',true);
		$('.dx2').attr('readonly',true);
		$('.i_parent_start1').val('1');
		$('.j_parent_start1').val('1');
		$('.d02').hide();
		$('.d03').hide(); 
		$('.d04').hide();
	});
}

/**创建清除按钮**/
function domainClear(){
	$('#creat_Domain input').val('');
	$('#creat_Domain textarea').val('');
	$('#update_Domain_name input').val('');
	$('#update_Domain_name textarea').val('');
}

$("#creat_Domain").window({  //创建全国清单窗口
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'新建Domain',
    border:false,
    closed:true,
    cls:"cloudui"
})

$("#update_Domain").window({  //创建全国清单窗口
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'新建Domain',
    border:false,
    closed:true,
    cls:"cloudui"
})

$("#update_Domain_name").window({  //创建全国清单窗口
    width:600,  //easyui 窗口宽度
    collapsible:false, //easyui 自带的折叠按钮
    maximizable:false,//easyui 自带的最大按钮
    minimizable:false,//easyui 自带的最小按钮
    modal:true,
    shadow:false,
    title:'新建Domain',
    border:false,
    closed:true,
    cls:"cloudui"
})



/**查询接口**/
function getInfo(){
	var  url = '/Domain/findAll';
	ajaxPost(url,{
		'userId': userId
	}).success(function (res) {
		$.each(res.data,function(key,value){
			if(value.haveMission == '已使用'){
				$('.box-body input').attr('readonly',true);
				$('.fa-times').hide();
				$('.add_two,.add_two2,.add_two3').hide();
				$('.del_domain').hide();
				pullPage(value);
			}
		});
        
    });
}  

/**数据导入页面**/
function pullPage(value){
	console.log(value);
	var domain_id = value.domainId;
	if(JSON.stringify(value.domainInfo) == "{}"){
		$('.panel-title').text(value.domainName);
		$('.box-body input').val('');
		$('.d02').hide();
		$('.d03').hide(); 
		$('.d04').hide();
		$('select').find("option[value='0']").attr('selected','selected');
		$('.box-body').attr('domain_id',domain_id);
		$('.del_domain').attr('domain_id',domain_id);
	}else{
		var arr_we = value.domainInfo.wrf.e_we.split(',');
		var arr_sn = value.domainInfo.wrf.e_sn.split(',');
		var arr_dx = value.domainInfo.common.dx.split(',');
		var arr_i_parent_start = value.domainInfo.wrf.i_parent_start.split(',');
		var arr_j_parent_start = value.domainInfo.wrf.j_parent_start.split(',');
		$('.panel-title').text(value.domainName);
		$('.ref_lat').val(value.domainInfo.common.ref_lat);
		$('.ref_lon').val(value.domainInfo.common.ref_lon);
		$('.stand_lat1').val(value.domainInfo.common.stand_lat1);
		$('.stand_lat2').val(value.domainInfo.common.stand_lat2);
		$('.stand_lon').val(value.domainInfo.common.stand_lon);
		$('.e_we1').val(arr_we[0]);
		$('.e_sn1').val(arr_sn[0]);
		$('.e_we2').val(arr_we[1]);
		$('.e_sn2').val(arr_sn[1]);
		$('.e_we3').val(arr_we[2]);
		$('.e_sn3').val(arr_sn[2]);
		$('.btrim').val(value.domainInfo.mcip.btrim);
		$('.i_parent_start1').val(arr_i_parent_start[0]);
		$('.i_parent_start2').val(arr_i_parent_start[1]);
		$('.i_parent_start3').val(arr_i_parent_start[2]);
		$('.j_parent_start1').val(arr_j_parent_start[0]);
		$('.j_parent_start2').val(arr_j_parent_start[1]);
		$('.j_parent_start3').val(arr_j_parent_start[2]);
		$('.box-body').attr('domain_id',domain_id);
		$('.del_domain').attr('domain_id',domain_id);
		if(arr_dx[0] == '27000'){
			$('select').find("option[value='2']").attr('selected','selected');
		}else{
			$('select').find("option[value='3']").attr('selected','selected');
		}
		$('.dx1').val(arr_dx[1]);
		$('.dx2').val(arr_dx[2]);
		if(value.domainInfo.common.max_dom =='3'){
			$('.d02').show();
			$('.d03').show(); 
			$('.d04').show();
		}else if(value.domainInfo.common.max_dom =='2'){
			$('.d02').show();
			$('.d03').show(); 
		}else if(value.domainInfo.common.max_dom =='1'){
			$('.d02').show();
		}
	}
}

/**分辨率选择**/
function resolution(){
	var checkValue=$("select").val();
	if(checkValue == '2'){
		$('.dx1').val('9000');
		$('.dx2').val('3000');
	}else if(checkValue == '3'){
		$('.dx1').val('12000');
		$('.dx2').val('4000');
	}else if(checkValue == '0'){
		$('.dx1').val('');
		$('.dx2').val('');
	}
}

/**修改页面数据整理数据结构**/
function submitSave(){
	var arr_we = [];
	var arr_sn = [];
	var arr_dx = [];
	var arr_i_parent_start = [];
	var arr_j_parent_start = [];
	var e_we1 = $('.e_we1').val(); 
	var e_we2 = $('.e_we2').val(); 
	var e_we3 = $('.e_we3').val(); 
	var e_sn1 = $('.e_sn1').val(); 
	var e_sn2 = $('.e_sn2').val(); 
	var e_sn3 = $('.e_sn3').val(); 
	var i_parent_start1 = $('.i_parent_start1').val();
	var i_parent_start2 = $('.i_parent_start2').val();
	var i_parent_start3 = $('.i_parent_start3').val();
	var j_parent_start1 = $('.j_parent_start1').val();
	var j_parent_start2 = $('.j_parent_start2').val();
	var j_parent_start3 = $('.j_parent_start3').val();
	var dx = $("select").find("option:selected").text();

	arr_we.push(e_we1,e_we2,e_we3);
	arr_sn.push(e_sn1,e_sn2,e_sn3);
	arr_j_parent_start.push(j_parent_start1,j_parent_start2,j_parent_start3);
	arr_i_parent_start.push(i_parent_start1,i_parent_start2,i_parent_start3);

	var ref_lat = $('.ref_lat').val();
	var ref_lon = $('.ref_lon').val();
	var stand_lat1 = $('.stand_lat1').val();
	var stand_lat2 = $('.stand_lat2').val();
	var max_dom = $('.box-body').attr('max_dom');
	var btrim = $('.btrim').val();
	var i_parent_start = arr_i_parent_start.join(',');
	var j_parent_start = arr_j_parent_start.join(',');
	var e_we = arr_we.join(',');
	var e_sn = arr_sn.join(',');
	var domainInfo = {
		'common':{
			'ref_lat':ref_lat,
			'ref_lon':ref_lon,
			'stand_lat1':stand_lat1,
			'stand_lat2':stand_lat2,
			'max_dom':max_dom,
			'dx':dx,
			'dy':dx,
		},
		'wrf':{
			'i_parent_start':i_parent_start,
			'j_parent_start':j_parent_start,
			'e_we':e_we,
			'e_sn':e_sn,
		},
		'mcip':{
			'btrim':btrim,
		}
	};
	var domainRange = dx;
	var domainId = $('.box-body').attr('domain_id');
	var data = {
		'userId':userId,
		'domainInfo':domainInfo,
		'domainyu':domainRange,
		'domainId':domainId
	}
	return data;
}

/**数据修改保存提交**/
function postSubmit(){
	var url = '/Domain/updateRangeAndCode';
	var data = submitSave();
	ajaxPost(url,{
		'userId':data.userId,
		'domainInfo':data.domainInfo,
		'domainyu':data.domainyu,
		'domainId':data.domainId,
	}).success(function(res){
	});
}

/**修改domain名字 post**/
function namePost(){
	$("#update_Domain_name").window('close');
	var url = '/Domain/updateNameAndDoc';
	var domainName = $('.u_domainName').val();
	var domainDoc = $('.u_domainDoc').val();
	var domain_id = $('.u_domainName').attr('domain_id');
	ajaxPost(url,{
		'userId':userId,
		'domainDoc':domainDoc,
		'domainName':domainName,
		'domainId':domain_id
	}).success(function(res){
		$('.d_name_'+res.data.domainId).text(res.data.domainName);
		$('.d_doc_'+res.data.domainId).text(res.data.domainDoc);
	});
}

