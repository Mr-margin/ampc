/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight">');
getInfo();
/**查询接口**/
function getInfo(){
	var  url = '/Domain/findAll';
	var domain = vipspa.getMessage('domain_update');
	var domain_id = domain.content.domain_id;
	var domainId =  $.session.get('domain_id');
	console.log(domainId);
	ajaxPost(url,{
		'userId': userId
	}).success(function (res) {
		$.each(res.data,function(key,value){
			if(value.domainId == domain_id){
				console.log(value);
				pullPage(value);
			}
		});
        
    });
}  

/**数据导入页面**/
function pullPage(value){
	var domain_id = value.domainId;
	if(JSON.stringify(value.domainInfo) == "{}"){
		$('.title').text(value.domainName);
	}else{
		var arr_we = value.domainInfo.wrf.e_we.split(',');
		var arr_sn = value.domainInfo.wrf.e_sn.split(',');
		var arr_dx = value.domainInfo.common.dx.split(',');
		var arr_i_parent_start = value.domainInfo.wrf.i_parent_start.split(',');
		var arr_j_parent_start = value.domainInfo.wrf.j_parent_start.split(',');
		$('.title').text(value.domainName);
		$('.ref_lat').text(value.domainInfo.common.ref_lat);
		$('.ref_lon').text(value.domainInfo.common.ref_lon);
		$('.stand_lat1').text(value.domainInfo.common.stand_lat1);
		$('.stand_lat2').text(value.domainInfo.common.stand_lat2);
		$('.stand_lon').text(value.domainInfo.common.stand_lon);
		$('.e_we1').text(arr_we[0]);
		$('.e_sn1').text(arr_sn[0]);
		$('.e_we2').text(arr_we[1]);
		$('.e_sn2').text(arr_sn[1]);
		$('.e_we3').text(arr_we[2]);
		$('.e_sn3').text(arr_sn[2]);
		$('.btrim').text(value.domainInfo.mcip.btrim);
		$('.btrims').text(value.domainInfo.mcip.btrim);
		$('.i_parent_start1').text(arr_i_parent_start[0]);
		$('.i_parent_start2').text(arr_i_parent_start[1]);
		$('.i_parent_start3').text(arr_i_parent_start[2]);
		$('.j_parent_start1').text(arr_j_parent_start[0]);
		$('.j_parent_start2').text(arr_j_parent_start[1]);
		$('.j_parent_start3').text(arr_j_parent_start[2]);
		$('.box-body').attr('domain_id',domain_id);
		$('.del_domain').attr('domain_id',domain_id);
		$('.dx').text(arr_dx[0]);
		$('.dx1').text(arr_dx[1]);
		$('.dx2').text(arr_dx[2]);
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
