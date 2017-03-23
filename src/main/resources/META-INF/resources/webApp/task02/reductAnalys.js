var columns = [{"field":"xzArea","title":"行政区","align":"center"},{"field":"PM25name","title":"PM2.5","align":"center"},{"field":"PM10name","title":"PM10","align":"center"},{"field":"SO2name","title":"SO2","align":"center"},{"field":"NOXname","title":"NOX","align":"center"},{"field":"VOCname","title":"VOC","align":"center"},{"field":"COname","title":"CO","align":"center"},{"field":"NH3name","title":"NH3","align":"center"},{"field":"BCname","title":"BC","align":"center"},{"field":"OCname","title":"OC","align":"center"},{"field":"PMFINEname","title":"PMFINE","align":"center"},{"field":"PMCname","title":"PMC","align":"center"}];
var data = [{"xzArea":"杭州市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"嘉兴市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"湖州市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"宁波市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"}];


var ls = window.localStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;

if(!qjMsg){
  qjMsg = JSON.parse(ls.getItem('yaMsg'));
}else{
  ls.setItem('yaMsg',JSON.stringify(qjMsg));
}
console.log(JSON.stringify(qjMsg));

/**
 *设置导航条信息
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景管理</a>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span>');


//渲染器样式
//mod2.xrclass={
//		linecolor:"#444",//边框颜色
//		linewidth:1,//边框
//		class1:[255, 255, 178, 0.75],
//		class2:[254, 204, 92, 0.75],
//		class3:[253, 141, 60, 0.75],
//		class4:[227, 26, 28, 0.75],
//};


/**
 * 时间戳转成日期格式
 * @param nS
 * @returns
 */
function getLocalTime(nS) {     
    return moment(nS).format('YYYY-MM-DD');
}


/**
 * 操作地图显示
 */
var stat = {cPointx : 106, cPointy : 35}, app = {}, dong = {};
var dojoConfig = {
		async: true,
	    parseOnLoad: true,  
	    packages: [{  
	        name: 'tdlib',  
	        location: "/js/tdlib"  
	    }],
	    paths: {
	    	extras: location.pathname.replace(/\/[^/]+$/, '') + "/js/extras"  
	    }
	};
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol",  'esri/symbols/PictureMarkerSymbol',
         'esri/renderers/ClassBreaksRenderer',"esri/symbols/SimpleMarkerSymbol",'esri/dijit/PopupTemplate', "esri/geometry/Point", "esri/geometry/Extent", "esri/renderers/SimpleRenderer", "esri/graphic", 
        "dojo/_base/Color", "dojo/dom-style",'dojo/query', "esri/tasks/FeatureSet", "esri/SpatialReference", 'extras/ClusterLayer',"tdlib/gaodeLayer", "dojo/domReady!"], 
	function(Map, FeatureLayer,GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol,PictureMarkerSymbol, ClassBreaksRenderer, SimpleMarkerSymbol,PopupTemplate, Point,Extent,SimpleRenderer, Graphic,
	        Color, domStyle,query, FeatureSet, SpatialReference,ClusterLayer, gaodeLayer) {
		dong.gaodeLayer = gaodeLayer;
		dong.Graphic = Graphic;
		dong.Point = Point;
		dong.GraphicsLayer = GraphicsLayer;
		dong.SpatialReference = SpatialReference;
		dong.SimpleMarkerSymbol = SimpleMarkerSymbol;
		dong.Extent = Extent;
		dong.SimpleLineSymbol =SimpleLineSymbol;
		dong.Color = Color;
		dong.PopupTemplate = PopupTemplate;
		dong.ClusterLayer = ClusterLayer ;
		dong.PictureMarkerSymbol = PictureMarkerSymbol;
		dong.ClassBreaksRenderer = ClassBreaksRenderer ;
		dong.domStyle = domStyle ;
		dong.query = query;
		dong.FeatureLayer = FeatureLayer;
		
//		esri.config.defaults.io.proxyUrl = "http://192.168.4.214:8091/Java/proxy.jsp";
//    	esri.config.defaults.io.alwaysUseProxy = false;

		app.map = new Map("map_showId", {
			logo:false,
	        center: [stat.cPointx, stat.cPointy],
	        minZoom:3,
	        maxZoom:13,
	        zoom: 4
		});
		
		
		app.baselayerList = new dong.gaodeLayer();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
		app.stlayerList = new dong.gaodeLayer({layertype: "st"});//加载卫星图
		app.labellayerList = new dong.gaodeLayer({layertype: "label"});//加载标注图
		
		app.map.on("load", shoe_data_start);//启动后立即执行获取数据
		
		app.map.addLayer(app.baselayerList);//添加高德地图到map容器
		app.map.addLayers([app.baselayerList]);//添加高德地图到map容器
		
		app.gLyr = new dong.GraphicsLayer({"id":"gLyr"});
		app.map.addLayer(app.gLyr);
		
		dojo.connect(app.map, "onZoomEnd", resizess);//缩放
//		dojo.connect(app.map, "onload", resizess);//加载
		
		/****************************添加省*********************************************/
//		app.sheng = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer/2", {//添加省的图层
//			mode: dong.FeatureLayer.MODE_ONDEMAND,
//			outFields: ["*"]
//		});
//		app.map.addLayer(app.sheng);
//		dojo.connect(app.sheng, "onClick", optionclick);//点击
//		/*******************************添加市***************************************/
//		app.shi = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/1", {//市的图层
//			mode: dong.FeatureLayer.MODE_ONDEMAND,
//			outFields: ["*"],
//		});
//		app.map.addLayer(app.shi);
//		dojo.connect(app.shi, "onClick", optionclick);
//		/*******************************添加县***************************************/
//		app.xian = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/china_x/MapServer/0", {//县的图层
//			mode: dong.MODE_ONDEMAND,
//			outFields: ["*"],
//		});
//		app.map.addLayer(app.xian);
//		dojo.connect(app.xian, "onClick", optionclick);
		
		//定义专题图的默认样式
//	    app.symbol = new dong.SimpleFillSymbol(
//			dong.SimpleFillSymbol.STYLE_SOLID,
//			new dong.SimpleLineSymbol(
//				dong.SimpleLineSymbol.STYLE_SOLID, 
//				new dong.Color([255, 255, 255, 0.35]), 1
//			), 
//			new dong.Color([163, 163, 163, 0.7])
//		);
	    
	    
});

/**
 * 启动后加载数据，
 */
function shoe_data_start(){
	var paramsName = {};
	paramsName.userId = userId;
//	paramsName.emissionDate = getLocalTime(qjMsg.qjStartDate);//时间
	paramsName.emissionDate = "2017-03-11";//时间
	paramsName.pollutant = $('#hz_wrw').val();//物种
	paramsName.codeLevel = 2;//省市区层级（1省，2市，3区）
	paramsName.scenarinoId = qjMsg.qjId;//情景id
	
	console.log(JSON.stringify(paramsName));
	ajaxPost('/find_reduceEmission',paramsName).success(function(res){
		console.log(JSON.stringify(res));
		
		if(res.status == 0){
			var data_id = "";
			$.each(res.data, function(i, col) {
				
			});
		}
		
		var query = new dong.query();
		query.where = "ADMINCODE in ('"+parent.dataBase.Message_map.REGION+"')";
		
		app.sheng = new dong.FeatureLayer(ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer/2", {outFields: ["*"]});//添加省的图层
		app.sheng.queryFeatures(query, function(featureSet) {
			for (var i = 0, il = featureSet.features.length; i < il; i++) {
				var graphic = featureSet.features[i];
				if(i == 0){
					xmax = graphic.geometry.getExtent().xmax;
					xmin = graphic.geometry.getExtent().xmin;
					ymax = graphic.geometry.getExtent().ymax;
					ymin = graphic.geometry.getExtent().ymin;
				}else{
					xmin = graphic.geometry.getExtent().xmin < xmin ? graphic.geometry.getExtent().xmin : xmin;
					xmax = graphic.geometry.getExtent().xmax > xmax ? graphic.geometry.getExtent().xmax : xmax;
					ymin = graphic.geometry.getExtent().ymin < ymin ? graphic.geometry.getExtent().ymin : ymin;
					ymax = graphic.geometry.getExtent().ymax > ymax ? graphic.geometry.getExtent().ymax : ymax;
				}
			}
			var extent = new dong.Extent(xmin,ymin,xmax,ymax, new dong.SpatialReference({ wkid:3857 }));
			app.map.setExtent(extent.expand(1.5));
		});
		
		
	});
	
}

function resizess(event){
	alert('c');
}

function optionclick(event){
	
	
	alert('b');
}

























$(function(){

	//初始化模态框显示
	$(".createRwModal").modal();
	
	//柱状图
	bar();
	pie();
	
	//行业 措施联动
	$("#tradeId").change(function(){
		pie();
	});
	$("#measureId").change(function(){
		pie();
	});
	
    //地图展示切换
    $("#mapId").change(function(){
    	$("#map_showId").show();
    	$("#listModal").hide();
    	
    })
    //列表展示切换
    $("#listShow").change(function(){
    	$("#listModal").show();
    	$("#map_showId").hide();

    })
    
    
    
    //构建列表展示table  
    $("#table_listShow").bootstrapTable({
    	method:'POST',
    	url:'',
    	dataType: "json",
    	clickToSelect : true,// 点击选中行
    	pagination : false, // 在表格底部显示分页工具栏
    	singleSelect : true,//设置True 将禁止多选
    	striped : true, // 使表格带有条纹
    	pagination: true, //是否启用分页
    	sidePagination: "client",//分页方式：client客户端分页，server服务端分页（*）
    	pageNumber:1,   //初始化加载第一页，默认第一页
    	pageSize: 10,   //每页的记录行数
    	pageList: [10, 25, 50, 100],//可供选择的每页的行数
    	silent : true, // 刷新事件必须设置
    	detailView: true,//是否显示父子表
        columns: columns,
        data:data,
      //注册加载子表的事件。注意下这里的三个参数！
        onExpandRow: function (index, row, $detail) {
            InitSubTable(index, row, $detail);
        }
    });
    
    
});
//初始化子表格(无线循环)
InitSubTable = function (index, row, $detail) {
    var cur_table = $detail.html('<table></table>').find('table');
    $(cur_table).bootstrapTable({
        url: '',
        method: 'get',
        clickToSelect: true,
        detailView: false,//父子表
        columns: [{
            field: 'xzArea',
            align: 'center'
        }, {
            field: 'PM25name',
            align: 'center'
        }, {
            field: 'PM10name',
            align: 'center'
        }, {
            field: 'SO2name',
            align: 'center'
        }, {
            field: 'NOXname',
            align: 'center'
        }, {
            field: 'VOCname',
            align: 'center'
        }, {
            field: 'COname',
            align: 'center'
        }, {
            field: 'NH3name',
            align: 'center'
        }, {
            field: 'BCname',
            align: 'center'
        }, {
            field: 'OCname',
            align: 'center'
        }, {
            field: 'PMFINEname',
            align: 'center'
        }, {
            field: 'PMCname',
            align: 'center'
        }],
        data:[{
        	xzArea: '上城区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '下城区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },{
        	xzArea: '江干区',
        	PM25name: '76',
        	PM10name: '80',
        	SO2name: '85',
        	NOXname: '78',
        	VOCname: '77',
        	COname: '75',
        	NH3name: '76',
        	BCname: '75',
        	OCname: '71',
        	PMFINEname: '76',
        	PMCname: '73'
        },]

    });
};


//下拉选框
function selectQj(value){
	if (value == 'j1' || value == 'j2') {
		$("#tableId").css('display','block');
		
	} else {
		$("#tableId").css('display','none');
	}
}
//柱状图
function bar () {
	var myPfChart = echarts.init(document.getElementById('pfDiv1'));
	var option = {
		    title : {
		        text: '减排图表',
		        //subtext: 'From ExcelHome',
		        //sublink: 'http://e.weibo.com/1341556070/AizJXrAEa'
		    },
		    tooltip : {
		        trigger: 'axis',
		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		        },
		        formatter: function (params){
		            return params[0].name + '<br/>'
		                   + params[0].seriesName + ' : ' + params[0].value + '<br/>'
		                   + params[1].seriesName + ' : ' + (params[1].value + params[0].value);
		        }
		    },
		    legend: {
		    	//不触动
		        selectedMode:true,
		        data:['实际排放量', '减排量']
		    },
		    grid:{
		    		show:true
		    },
            dataZoom:[
                      {
                    	  show:'true',
                    	  realtime:'true',
                    	  start:20,
                    	  end:80
                    	  
                      },
                      {
                    	  type:'inside',
                    	  realtime:'true',
                    	  start:60,
                    	  end:80
                      }
                      ],
		    calculable : true,
		    xAxis : [
		        {
		        	show: true, 
		            type : 'category',
		            data : ['2016-11-17','2016-11-22','2016-11-27','2016-12-02','2016-12-07','2016-11-10']
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value',
		            name : '吨',
		            boundaryGap: [0, 0.1],
		            splitArea : {show : true},
		            show:true
		        }
		    ],
		    series : [
		        {
		            name:'实际排放量',
		            type:'bar',
		            stack: 'sum',
		            barCategoryGap: '50%',
		            itemStyle: {
		                normal: {
		                    color: 'tomato',
		                    barBorderColor: 'tomato',
		                    barBorderWidth: 4,
		                    barBorderRadius:0,
		                    label : {
		                        show: true, position: 'insideTop'
		                    }
		                }
		            },
		            data:[260, 200, 220, 120, 100, 80]
		        },
		        {
		            name:'减排量',
		            type:'bar',
		            stack: 'sum',
		            itemStyle: {
		                normal: {
		                    color: '#fff',
		                    barBorderColor: 'tomato',
		                    barBorderWidth: 4,
		                    barBorderRadius:0,
		                    label : {
		                        show: true, 
		                        position: 'top',
		                        formatter: function (params) {
		                            for (var i = 0, l = option.xAxis[0].data.length; i < l; i++) {
		                                if (option.xAxis[0].data[i] == params.name) {
		                                    return option.series[0].data[i] + params.value;
		                                }
		                            }
		                        },
		                        textStyle: {
		                            color: 'tomato'
		                        }
		                    }
		                }
		            },
		            data:[40, 80, 50, 80,80, 70]
		        }
		    ]
		};
		myPfChart.on('click', function (params) {
//		console.log(params);
		pie();
		
	});
		//减排量echarts
		myPfChart.setOption(option);
		//自适应屏幕大小变化
		window.addEventListener("resize",function(){
			 myPfChart.resize();

		 });

}
//行业措施饼状图
function  pie () {
	var myhycsChart = echarts.init(document.getElementById('hycsDiv1'));
	var option2 = {
		    title : {
		        text: '行业措施饼状图',
		        //subtext: '纯属虚构',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data: ['钢铁','玻璃','水泥','独立焦化','其他工业企业','非道移动源','工业锅炉','储存运输','电力','废弃物处理源','其它','生物质燃烧源'],
		    },
		    series : [
		        {
		            name: '访问来源',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:[
		                {value:335, name:'其他工业企业'},
		                {value:310, name:'独立焦化'},
		                {value:234, name:'水泥'},
		                {value:135, name:'玻璃'},
		                {value:1203, name:'钢铁'},
		                {value:90, name:'非道移动源'},
		                {value:25, name:'工业锅炉'},
		                {value:15, name:'储存运输'},
		                {value:231, name:'电力'},
		                {value:68, name:'废弃物处理源'},
		                {value:8, name:'其它'},
		                {value:6, name:'生物质燃烧源'},
		            ],
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};
			//行业措施分担饼状图
			myhycsChart.setOption(option2);
			window.onresize = myhycsChart.resize;	
}
