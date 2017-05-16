/**
 * Created by zhangmiao on 2017/4/27.
 */
/*默认的datagrid配置项*/
var defaultdatagridoption={
  toolbar:'#toolbar',
  fit:true,
  border:false,
  pagination:true,
  fitColumns:true,
  loadFilter:function (res) {
    return res.data;
  },
  // onResize:function () {
  //   clacdatagridcolumnwidth();
  // }
};
/*默认的弹窗的window配置项*/
var defaultwindowoption={
  width: 600,
  collapsible: false,
  maximizable: false,
  minimizable: false,
  modal: true,
  shadow: false,
  border: false,
  closed: true,
  cls: 'cloudui'
};
var datagridcolumnwidthrem=0;
function clacdatagridcolumnwidth() {
  datagridcolumnwidthrem=(parseFloat(getComputedStyle(document.getElementById('Route_conter')).width)-17)/100;
}
/*封装一个包含在ajax中
* */
// function ajaxDatagrid(idname,url,param,option){
//   $.ajax()
// }