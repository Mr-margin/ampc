/**
 * Created by shanhaichushi on 2017/5/19.
 */
$("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">用户设置</span><i class="en-arrow-right7" style="font-size:16px;"></i><span style="padding-left: 15px;padding-right: 15px;">domain设置</span><span class="navRight qdnavRight"><span class="navRight qdnavRight"><button class="qdCreat" onclick="creatQd()">新建</button><button class="qdEdit" onclick="editQd()">编辑</button></span>');


$(document).ready(function(){
	getInfo();
});
     

/**查询接口**/
function getInfo(){
	var  url = '/Domain/findAll';
	ajaxPost(url,{
		'userId': userId
	}).success(function (res) {
		$.each(res.data,function(key,value){
			console.log(value);
			findPull(value);
		});
    });
}  

function findPull(value){
	var dom = '';
	dom += '<tr>';
	dom += '<td>'+value.domainName+'</td>';
	dom += '<td>'+value.domainDoc+'</td>';
	dom += '<td>'+value.haveMission+'</td>';
	dom += '<td>';
	dom += '<button class="btn-blue" onclick="examine(this)" domain_id="'+value.domainId+'">查看</button>';
	dom += '<button class="btn-green">编辑</button> ';
	dom += '<button class="btn-yellow">开启</button> ';
	dom += '<button class="btn-red">删除</button>';
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
	console.log(msg);
	vipspa.setMessage(msg);
	location.hash = '#/domain';
 }