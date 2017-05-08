/**
 * Created by zhangmiao on 2017/4/27.
 */
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
var datagridcolumnwidthrem=0;
function clacdatagridcolumnwidth() {
  datagridcolumnwidthrem=(parseFloat(getComputedStyle(document.getElementById('Route_conter')).width)-17)/100;
}