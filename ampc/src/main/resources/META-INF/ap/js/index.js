/**
 * Created by zhangmiao on 2017/4/27.
 */
(function (win) {
  require.config({
    shim:{
      'zhCN':['jquery'],
      'easyui':['jquery'],
      'director':{
        exports:'Router'
      }
    },
    paths:{
      jquery:'../easyui/jquery.min',
      easyui:'../easyui/jquery.easyui.min',
      zhCN:'../easyui/locale/easyui-lang-zh_CN',
      director:'plugin/director.min'
    }
  });
  require(['jquery','easyui','zhCN','director'],function ($,easyui,zhCN,Router){

  })
})(window)