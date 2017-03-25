var columns = [{"field":"xzArea","title":"行政区","align":"center"},{"field":"PM25name","title":"PM2.5","align":"center"},{"field":"PM10name","title":"PM10","align":"center"},{"field":"SO2name","title":"SO2","align":"center"},{"field":"NOXname","title":"NOX","align":"center"},{"field":"VOCname","title":"VOC","align":"center"},{"field":"COname","title":"CO","align":"center"},{"field":"NH3name","title":"NH3","align":"center"},{"field":"BCname","title":"BC","align":"center"},{"field":"OCname","title":"OC","align":"center"},{"field":"PMFINEname","title":"PMFINE","align":"center"},{"field":"PMCname","title":"PMC","align":"center"}];
var data = [{"xzArea":"杭州市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"嘉兴市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"湖州市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"},{"xzArea":"宁波市","PM25name":"76","PM10name":"80","SO2name":"85","NOXname":"78","VOCname":"77","COname":"75","NH3name":"76","BCname":"75","OCname":"71","PMFINEname":"76","PMCname":"73"}];


var ls = window.localStorage;
var qjMsg = vipspa.getMessage('yaMessage').content;

if(!qjMsg){
  qjMsg = JSON.parse(ls.getItem('yaMsg'));
}else{
  ls.setItem('yaMsg',JSON.stringify(qjMsg));
}
//console.log(JSON.stringify(qjMsg));

/**
 *设置导航条信息
 */
$("#crumb").html('<a href="#/rwgl" style="padding-left: 15px;padding-right: 15px;">任务管理</a>>><a href="#/yabj" style="padding-left: 15px;padding-right: 15px;">情景管理</a>>><span style="padding-left: 15px;padding-right: 15px;">减排分析</span>');
var gis_paramsName = {};//地图请求的参数，第一次加载地图时初始化，每次更改地图比例尺时修改codeLevel

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
require(["esri/map", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/symbols/SimpleFillSymbol", "esri/symbols/SimpleLineSymbol",  "esri/symbols/PictureMarkerSymbol",
         "esri/renderers/ClassBreaksRenderer","esri/symbols/SimpleMarkerSymbol","esri/dijit/PopupTemplate", "esri/geometry/Point", "esri/geometry/Extent", 
         "esri/renderers/SimpleRenderer", "esri/graphic", "dojo/_base/Color", "dojo/dom-style",'dojo/query', "esri/tasks/FeatureSet", "esri/SpatialReference", 
         'extras/ClusterLayer',"tdlib/gaodeLayer", "dojo/dom-construct", "esri/dijit/Legend", "dojo/dom", "extras/Tip", "dojo/domReady!"], 
	function(Map, FeatureLayer,GraphicsLayer, SimpleFillSymbol, SimpleLineSymbol,PictureMarkerSymbol, ClassBreaksRenderer, SimpleMarkerSymbol,PopupTemplate, Point,Extent,SimpleRenderer, Graphic,
	        Color, domStyle,query, FeatureSet, SpatialReference,ClusterLayer, gaodeLayer, domConstruct, Legend, dom, Tip) {
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
		dong.SimpleFillSymbol = SimpleFillSymbol;
		dong.domConstruct = domConstruct;
		dong.Legend = Legend;
		dong.dom = dom;
		dong.Tip = Tip;

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
		app.outline = new dong.SimpleLineSymbol("solid", new dong.Color("#444"), 1);
		
});

/**
 * 启动后加载数据，
 */
function shoe_data_start(evn){
	gis_paramsName.userId = userId;
	gis_paramsName.pollutant = $('#hz_wrw').val();//物种
	//获取地图当前比例尺，决定数据请求省市县的哪一级
	if (evn.map.getZoom() <= 6) {
		gis_paramsName.codeLevel = 1;//省市区层级（1省，2市，3区）
	}else if (evn.map.getZoom() == 7){
		gis_paramsName.codeLevel = 2;//省市区层级（1省，2市，3区）
	}else if (evn.map.getZoom() >= 8){
		gis_paramsName.codeLevel = 3;//省市区层级（1省，2市，3区）
	}
//	gis_paramsName.scenarinoId = qjMsg.qjId;//情景id
	gis_paramsName.scenarinoId = 3;//情景id
	baizhu_jianpai(gis_paramsName);
}

/**
 * 标注地图的减排计算
 */
function baizhu_jianpai(gis_paramsName){
//	console.log(JSON.stringify(gis_paramsName));
	if (typeof app.paifang != "undefined") {
		app.map.removeLayer(app.paifang);
	}
	ajaxPost('/find_reduceEmission',gis_paramsName).success(function(res){
//		console.log(JSON.stringify(res));
		if(res.status == 0){
			var data_id = "";
			$.each(res.data, function(k, col) {
				data_id += "'"+k+"',";
			});
			
			var paifang_url = "";
			if(gis_paramsName.codeLevel == 1){
				paifang_url = ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer/2";
			}else if(gis_paramsName.codeLevel == 2){
				paifang_url = ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer/1";
			}else if(gis_paramsName.codeLevel == 3){
				paifang_url = ArcGisServerUrl+"/arcgis/rest/services/cms/MapServer/0";
			}
			
			app.paifang = new dong.FeatureLayer(paifang_url, {
				mode: dong.FeatureLayer.MODE_ONDEMAND,
				outFields: ["*"],
				id: "paifang"
			});
			
			var template = "<strong>${NAME}:  ${DATAVALU}</strong>";
	        app.tip = new dong.Tip({
	          "format": template,
	          "node": "legend",
	          "res": res.data
	        });
			
			//渲染器，根据行政区划code获取外部获取的面对应的数据
			var br = new dong.ClassBreaksRenderer(null, function(graphic){
				var state = graphic.attributes.ADMINCODE;
	     		return res.data[state];
	        });
			
			br.setMaxInclusive(true);
			var breaks = calcBreaks(res.data);
			br.addBreak(breaks[0], breaks[1], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([72, 165, 251, 0.65])));
			br.addBreak(breaks[1], breaks[2], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([126, 251, 251, 0.65])));
			br.addBreak(breaks[2], breaks[3], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([122, 251, 159, 0.65])));
			br.addBreak(breaks[3], breaks[4], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([147, 251, 72, 0.65])));
			br.addBreak(breaks[4], breaks[5], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([229, 251, 72, 0.65])));
			br.addBreak(breaks[5], breaks[6], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 237, 72, 0.65])));
			br.addBreak(breaks[6], breaks[7], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 171, 72, 0.65])));
			br.addBreak(breaks[7], breaks[8], new dong.SimpleFillSymbol("solid", app.outline, new dong.Color([251, 100, 72, 0.65])));
			
			app.paifang.setRenderer(br);
			app.paifang.redraw();
			app.map.addLayer(app.paifang);
			createLegend(gis_paramsName.codeLevel);
			
            dojo.connect(app.paifang, "onClick", optionclick);//点击
            dojo.connect(app.paifang, "onMouseOver", app.tip.showInfo);
            dojo.connect(app.paifang, "onMouseOut", app.tip.hideInfo);
            
			if(gis_paramsName.codeLevel == 1){
	            var query = new dong.query();
				query.where = "ADMINCODE in ("+data_id.substring(0, data_id.length-1)+")";
				app.paifang.queryFeatures(query, function(featureSet) {
					var extent = new dong.Extent();
					for (var i = 0, il = featureSet.features.length; i < il; i++) {
						var graphic = featureSet.features[i];
						if(i == 0){
							extent = graphic.geometry.getExtent();
						}else{
							extent = extent.union(graphic.geometry.getExtent());
						}
					}
					app.map.setExtent(extent.expand(1.5));
				});
			}
		}else{
			swal('减排数据获取失败', '', 'error');
		}
	});
}

/**
 * 创建图例
 */
function createLegend(level) {
	//如果存在的话，删除之前的图例
  	if ( app.hasOwnProperty("legend") ) {
    	app.legend.destroy();
    	dong.domConstruct.destroy(dojo.byId("legend"));
  	}
  	//创建一个新的div图例
  	var legendDiv = dong.domConstruct.create("div", {
  		id: "legend"
  	}, dong.dom.byId("legendWrapper"));

  	var title = "";
  	if(level == 1){
  		title = "各省 "+$('#hz_wrw').val()+" 减排量（吨）";
  	}else if(level == 2){
  		title = "各市 "+$('#hz_wrw').val()+" 减排量（吨）";
  	}else if(level == 3){
  		title = "各县 "+$('#hz_wrw').val()+" 减排量（吨）";
  	}
  	app.legend = new dong.Legend({
    	map : app.map,
    	respectCurrentMapScale : false,//当真正的图例会更新每个规模变化和只显示层和子层中可见当前地图比例尺。当假的,图例不更新在每个规模变化和所有层和子层将显示出来。默认值是正确的。
    	layerInfos : [{
      		layer : app.paifang,
      		title : title
    	}]
  	}, legendDiv);
  	app.legend.startup();
}

/**
 * 计算分段
 * @param data
 */
function calcBreaks(data){
	var breaks = [];
	var valMax = 0;//数据中的最大值
	$.each(data, function(k, col) {
		if(col > valMax){
			valMax = col;
		}
	});
	var i = 1;
	while(true){
		var ttrt = valMax/i;//最大值按比例减少，第一次不变，以后每次减一个零
		if(ttrt < 640){//最大值除以i，如果结果小于640，可以进入循环
			switch (true) {
				case ttrt < 80 :
					breaks = [0,(10*i),(20*i),(30*i),(40*i),(50*i),(60*i),(70*i),(80*i)];
					break;
				case ttrt < 96 :
					breaks = [0,(12*i),(24*i),(36*i),(48*i),(60*i),(72*i),(84*i),(96*i)];
					break;
				case ttrt < 120 :
					breaks = [0,(15*i),(30*i),(45*i),(60*i),(75*i),(90*i),(105*i),(120*i)];
					break;
				case ttrt < 160 :
					breaks = [0,(20*i),(40*i),(60*i),(80*i),(100*i),(120*i),(140*i),(160*i)];
					break;
				case ttrt < 240 :
					breaks = [0,(30*i),(60*i),(90*i),(120*i),(150*i),(180*i),(210*i),(240*i)];
					break;
				case ttrt < 320 :
					breaks = [0,(40*i),(80*i),(120*i),(160*i),(200*i),(240*i),(280*i),(320*i)];
					break;
				case ttrt < 400 :
					breaks = [0,(50*i),(100*i),(150*i),(200*i),(250*i),(300*i),(350*i),(400*i)];
					break;
				default :
					breaks = [0,(80*i),(160*i),(240*i),(320*i),(400*i),(480*i),(560*i),(640*i)];
			}
			break;
		}else{
			i = i*10;
		}
	}
	return breaks;
}

/**
 * 地图改变比例尺事件
 * @param event
 */
function resizess(event){
	//获取地图当前比例尺，如果比例尺要请求的数据与初始化的请求级别不一致，重新请求
	var level = 0;
	if (app.map.getZoom() <= 6) {
		level = 1;//省市区层级（1省，2市，3区）
	}else if (app.map.getZoom() == 7){
		level = 2;//省市区层级（1省，2市，3区）
	}else if (app.map.getZoom() >= 8){
		level = 3;//省市区层级（1省，2市，3区）
	}
	if(level != gis_paramsName.codeLevel){
		gis_paramsName.codeLevel = level;
		baizhu_jianpai(gis_paramsName);
	}
}

var admincode = "";//当前统计图显示数据的行政区划

/**
 * 点击地图事件，调用统计图方法
 * @param event
 */
function optionclick(event){
	admincode = event.graphic.attributes.ADMINCODE;
	var name = event.graphic.attributes.NAME;
	
	//更新统计图
	var wztype = $('#hz_wrw').val();
	bar(admincode,name,wztype);
	pie(admincode,name,wztype);
	
}


/**
 * 污染物选择，更新地图
 */
function gis_paifang_show(){
	gis_paramsName.pollutant = $('#hz_wrw').val();//物种
	baizhu_jianpai(gis_paramsName);
	//更新统计图
//	admincode //已选择的行政区划，如果为空，则还未选择
	
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
/****************************************************柱状图*************************************************************************/
function bar (admincode,name,wztype) {
	/*var wztype;
	nameArea = name;*/
	var paramsName = {"scenarinoId":"136","code":admincode,"addressLevle":3,"stainType":wztype};
		ajaxPost('/echarts/get_barInfo',paramsName).success(function(res){
		
		var myPfChart = echarts.init(document.getElementById('pfDiv1'));
		
		var option = {
			    title : {
			        text: name + '的减排图表',
			    },
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        },
			        formatter: function (params){
			            return params[0].name + '<br/>'
			                   + params[0].seriesName + ' : ' + params[0].value + '<br/>'
			                   + params[1].seriesName + ' : ' +  params[1].value;       //把 + params[0].value 去掉了
			        }
			    },
			    legend: {
			    	//不触动
			        selectedMode:false,
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
			            axisLabel : {
			            	formatter: function(category)
			            	{
			            		return category.substring(0,10);          //截取字符串
			            	}
			            },
			            data:res.data.dateResult
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
			                   /* label : {
			                        show: true, position: 'insideTop'
			                    }*/
			                }
			            },
			            data:res.data.pflResult
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
			                        /*show: true, 
			                        position: 'top',*/
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
			            data:res.data.jplResult
			        }
			    ]
			};
			console.log(res);
			//减排量echarts
			myPfChart.setOption(option);
		
			//自适应屏幕大小变化
			window.addEventListener("resize",function(){
				 myPfChart.resize();
			 });
		
			//点击联动饼图
			myPfChart.on('datazoom', function (params){
			console.log(params);
			
			pie();
			});

	});
}
/****************************************************行业措施饼状图*************************************************************************/
function  pie(admincode,name,wztype){
	var nameVal;
	var valueVal ;
	var paramsName = {"scenarinoId":"136","code":admincode,"addressLevle":2,"stainType":wztype,"startDate":"2017-03-04","endDate":"2017-03-09","type":1};
	ajaxPost('/echarts/get_pieInfo',paramsName).success(function(result){
		console.log(result.length)
			if(result == null){
				for(i=0;i<result.data.length;i++){
					nameVal = result.data[i].name;
					valueVal = result.data[i].value;
				}
			}/*else{
				swal('饼图暂无数据', '', 'error')
			}*/
			
	var myhycsChart = echarts.init(document.getElementById('hycsDiv1'));
	var optionPie = {
		    title : {
		        text: name+'的行业措施饼状图',
		        x:'center'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        left: 'left',
		        data:[{name:nameVal}]
		    },
		    series : [
		        {
		            name: '数据比例',
		            type: 'pie',
		            radius : '55%',
		            center: ['50%', '60%'],
		            data:[{value:valueVal,name:nameVal}],
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
			myhycsChart.setOption(optionPie);
			//自适应屏幕大小变化
			window.addEventListener("resize",function(){
				myhycsChart.resize();
			 });
	}).error(function () {
      swal('暂无数据', '', 'error')
    })	

}
