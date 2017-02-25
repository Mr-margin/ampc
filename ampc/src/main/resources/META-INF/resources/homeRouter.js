/**
 * Created by lvcheng on 2017/2/24.
 */
vipspa.start({
  view: '.ui-view',// 页面路由的div
  router: {
    '/sy': {        //首页
      templateUrl: 'a.html',
      controller: ''
    },
    '/yqd':{       //源清单
      templateUrl: 'a.html',
      controller: ''
    },
    '/ypg': {      //预评估
      templateUrl: 'webApp/task01/task01.html',
      controller: 'webApp/task01/task01.js'
    },
    '/hpg': {      //后评估
        templateUrl: 'a.html',
        controller: ''
    },
    '/kqzlyb': {      //空气质量预报
      templateUrl: 'a.html',
      controller: ''
    },
    '/sjkj': {      //数据空间
      templateUrl: 'a.html',
      controller: ''
    },
    '/hsz': {        //首页回收站
      templateUrl: 'a.html',
      controller: ''
    },
    '/test': {
      templateUrl: 'a.html',
      controller: ''
    },
    'defaults': '/ypg'     //默认显示页面
  },
  errorTemplateId: '#error'//错误显示页面
});