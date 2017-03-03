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
    '/rwgl': {      //任务管理
    	templateUrl: 'webApp/task01/task01.html',
    	controller: 'webApp/task01/task01.js'
    },
    '/yabj': {      //预评估
    	templateUrl: 'webApp/task02/task02.html',
    	controller: 'webApp/task02/task02.js'
    },
    '/xgpg': {      //效果评估
        templateUrl: 'webApp/Analysis02/a0201.html',
        controller: 'webApp/Analysis02/a0201.js'
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
    '/csbj':{
      templateUrl: 'webApp/task03/task03.html',
      controller: 'webApp/task03/task03.js'
    },
    '/test': {
    	templateUrl: 'a.html',
    	controller: ''
    },
    'defaults': '/rwgl'     //默认显示页面
  },
  errorTemplateId: '#error'//错误显示页面
});