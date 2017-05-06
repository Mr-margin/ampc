/**
 * Created by lvcheng on 2017/4/25.
 */
$(function () {
  /**
   *设置导航条信息
   */
  $("#crumb").html('<span style="padding-left: 15px;padding-right: 15px;">空气质量预报</span>>><span style="padding-left: 15px;padding-right: 15px;">水平分布</span>');
});
var dps_Date;
var changeMsg = {
  species:'PM25',
  rms:'day',
  time:'',
  domain:2,
  minDate:'',
  maxDate:'',
  startD:'',
  endD:'',
  speed:'',
  field:'',
  opt:0.5
}

var speciesArr = {
  day: ['PM₂.₅', 'PM₁₀', 'O₃_8_max', 'O₃_1_max', 'O₃_avg', 'SO₂', 'NO₂', 'CO', 'SO₄²¯', 'NO₃¯', 'NH₄⁺', 'BC', 'OM', 'PMFINE'],
  hour: ['PM₂.₅', 'PM₁₀', 'O₃', 'SO₂', 'NO₂', 'CO', 'SO₄', 'NO₃', 'NH₄', 'BC', 'OM', 'PMFINE']
};

var speciesObj = {
  'PM₂.₅':'PM25',
  'PM₁₀':'PM10',
  'O₃_8_max':'O3_8_MAX',
  'O₃_1_max':'O3_1_MAX',
  'O₃_avg':'O3_AVG',
  'SO₂':'SO2',
  'NO₂':'NO2',
  'CO':'CO',
  'SO₄²¯':'SO4',
  'NO₃¯':'NO3',
  'NH₄⁺':'NH4',
  'BC':'BC',
  'OM':'OM',
  'PMFINE':'PMFINE',
  //'O₃':'O3'
  'O₃':'O3'
};


initialize();

/*初始化函数*/
function initialize(){
  $('#species').empty();
  for(var i=0;i<speciesArr[changeMsg.rms].length;i++){
    $('#species').append($('<option>'+ speciesArr[changeMsg.rms][i] +'</option>'))
  }
  requestDate();

  $.when(dps_Date).done(function(){

  })
}

/*请求可选日期范围*/
function requestDate(){
  var url = '/Air/get_time';
  dps_Date = ajaxPost(url,{
    userId:userId
  }).success(function(res){

    if(res.status == 0){
      /*这里要初始化时间*/

      changeMsg.minDate = res.data.mintime;
      changeMsg.maxDate = res.data.maxtime;

      if(!(moment(res.data.maxtime).add(-7,'d').isBefore(moment(res.data.mintime)))){
        changeMsg.startD = moment(res.data.maxtime).add(-7,'d').format('YYYY-MM-DD')
      }else{
        changeMsg.startD = moment(res.data.mintime).format('YYYY-MM-DD')
      }
      changeMsg.endD = moment(res.data.maxtime).format('YYYY-MM-DD');
      changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');


      /*测试使用*/
      changeMsg.minDate = '2017-04-14';
      changeMsg.maxDate = '2017-04-16';
      changeMsg.startD = '2017-04-14';
      changeMsg.endD = '2017-04-16';
      changeMsg.time = moment(changeMsg.startD).format('YYYY-MM-DD HH');
      /*测试使用 end*/
      initSPDate(changeMsg.minDate,changeMsg.maxDate,changeMsg.startD,changeMsg.endD);
    }

  })
}

/**
 * 初始化时间范围
 * @param s 最大开始时间
 * @param e 最大结束时间
 * @param start 默认开始时间
 * @param end 默认结束时间
 */
function initSPDate(s, e, start, end) {
  $('#SPDate').daterangepicker({
    singleDatePicker: true,  //显示单个日历
    timePicker: changeMsg.rms == 'day'?false:true,  //允许选择时间
    timePicker24Hour: true, //时间24小时制
    minDate: s,//最早可选日期
    maxDate: e,//最大可选日期
    locale: {
      format: changeMsg.rms == 'day'?"YYYY-MM-DD":"YYYY-MM-DD HH",
      separator: " 至 ",
      applyLabel: "确定", //按钮文字
      cancelLabel: "取消",//按钮文字
      weekLabel: "W",
      daysOfWeek: [
        "日", "一", "二", "三", "四", "五", "六"
      ],
      monthNames: [
        "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
      ],
      firstDay: 1
    },
    "startDate": start,
    "endDate": end,
    "opens": "right"
  }, function (start, end, label) {

    changeMsg.time = start.format('YYYY-MM-DD HH');
    updata(true);
  })
  var d = $('#SPDate').data('daterangepicker');
  d.element.off();
}


/*按钮打开日期*/
function showDate(type) {
  var d = $('#SPDate').data('daterangepicker');
  if(!d){
    swal({
      title: '无可选日期!',
      type: 'error',
      timer: 1000,
      showConfirmButton: false
    });
    return
  }
  d.toggle();
}

/*数据更新*/
function updata(t){

}


/*改变事件*/
$('input[name=rms]').on('change', function (e) { //时间分辨率选择
  var rms = $(e.target).val();
  changeMsg.rms = rms;
  console.log(rms);
  $('#species').empty();
  for (var i = 0; i < speciesArr[rms].length; i++) {
    $('#species').append($('<option>' + speciesArr[rms][i] + '</option>'))
  }

  initSPDate(changeMsg.minDate,changeMsg.maxDate,changeMsg.startD,changeMsg.endD);

  updata();
});

$('input[name=domain]').on('change', function (e) {
  var domain = $(e.target).val();
  changeMsg.domain = domain;
  console.log(domain);

  updata();
});

$('input[name=speed]').on('change', function (e) {
  var speed = $(e.target).val();
  changeMsg.speed = speed;
  console.log(speed);

  updata();
});

$('input[name=field]').on('change', function (e) {
  var field = $(e.target).val();
  changeMsg.field = field;
  console.log(field);

  updata();
});

$('#species').on('change',function(e){
  var species = $(e.target).val();
  changeMsg.species = species;
  console.log(species);
  updata();
});



/*滑动条js*/
var $document   = $(document);
var selector    = '[data-rangeslider]';
var $inputRange = $(selector);

// Example functionality to demonstrate a value feedback
// and change the output's value.
function valueOutput(element) {
  var value = element.value;
  var output = element.parentNode.getElementsByTagName('output')[0];

  output.innerHTML = value;
  console.log(value);
  changeMsg.opt = value;
}

// Initial value output
for (var i = $inputRange.length - 1; i >= 0; i--) {
  valueOutput($inputRange[i]);
};

// Update value output
$document.on('input', selector, function(e) {
  valueOutput(e.target);
});

// Initialize the elements
$inputRange.rangeslider({
  polyfill: false
});

// Example functionality to demonstrate programmatic value changes
$document.on('click', '#js-example-change-value button', function(e) {
  var $inputRange = $('input[type="range"]', e.target.parentNode);
  var value = $('input[type="number"]', e.target.parentNode)[0].value;

  $inputRange
    .val(value)
    .change();
});

// Example functionality to demonstrate programmatic attribute changes
$document.on('click', '#js-example-change-attributes button', function(e) {
  var $inputRange = $('input[type="range"]', e.target.parentNode);
  var attributes = {
    min: $('input[name="min"]', e.target.parentNode)[0].value,
    max: $('input[name="max"]', e.target.parentNode)[0].value,
    step: $('input[name="step"]', e.target.parentNode)[0].value
  };

  $inputRange
    .attr(attributes)
    .rangeslider('update', true);
});

// Example functionality to demonstrate destroy functionality
$document.on('click', '#js-example-destroy button[data-behaviour="destroy"]', function(e) {
	$('input[type="range"]', e.target.parentNode).rangeslider('destroy');
}).on('click', '#js-example-destroy button[data-behaviour="initialize"]', function(e) {
	$('input[type="range"]', e.target.parentNode).rangeslider({ polyfill: false });
});




var stat = {cPointx: 106, cPointy: 35}, app = {}, dong = {};
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
require(
		[
		 "esri/map", "esri/tasks/Geoprocessor", "esri/layers/ImageParameters", "esri/layers/DynamicLayerInfo", "esri/layers/RasterDataSource", "esri/layers/TableDataSource",
		 "esri/layers/LayerDataSource", "esri/layers/FeatureLayer", "esri/layers/GraphicsLayer", "esri/layers/LayerDrawingOptions", "esri/symbols/SimpleFillSymbol",
		 "esri/symbols/SimpleLineSymbol", "esri/symbols/SimpleMarkerSymbol", "esri/geometry/Multipoint", "esri/geometry/Point", "esri/geometry/Extent",
		 "esri/renderers/SimpleRenderer", "esri/graphic", "esri/lang", "dojo/_base/array", "dojo/number", "dojo/dom-style", "dijit/TooltipDialog",
		 "dijit/popup", "dojox/widget/ColorPicker", "esri/layers/RasterLayer", "tdlib/gaodeLayer", "esri/tasks/FeatureSet", "esri/SpatialReference", "esri/symbols/PictureMarkerSymbol",
		 "esri/geometry/Polygon", "esri/symbols/PictureFillSymbol", "esri/Color", "esri/layers/ArcGISDynamicMapServiceLayer", "esri/tasks/RasterData", "dojo/domReady!"
		 ],
		 function (Map, Geoprocessor, ImageParameters, DynamicLayerInfo, RasterDataSource, TableDataSource, LayerDataSource, FeatureLayer, GraphicsLayer, LayerDrawingOptions,
				 SimpleFillSymbol, SimpleLineSymbol, SimpleMarkerSymbol, Multipoint, Point, Extent, SimpleRenderer, Graphic, esriLang, array, number, domStyle,
				 TooltipDialog, dijitPopup, ColorPicker, RasterLayer, gaodeLayer, FeatureSet, SpatialReference, PictureMarkerSymbol, Polygon, PictureFillSymbol, Color, 
				 ArcGISDynamicMapServiceLayer, RasterData) {

			dong.gaodeLayer = gaodeLayer;
			dong.DynamicLayerInfo = DynamicLayerInfo;
			dong.RasterDataSource = RasterDataSource;
			dong.LayerDataSource = LayerDataSource;

			dong.Geoprocessor = Geoprocessor;
			dong.Graphic = Graphic;
			dong.Point = Point;
			dong.FeatureSet = FeatureSet;
			dong.GraphicsLayer = GraphicsLayer;
			dong.SpatialReference = SpatialReference;
			dong.PictureMarkerSymbol = PictureMarkerSymbol;//图片点样式
			dong.Polygon = Polygon;//多边形
			dong.PictureFillSymbol = PictureFillSymbol;//
			dong.SimpleLineSymbol = SimpleLineSymbol;//
			dong.Color = Color;//
			dong.ArcGISDynamicMapServiceLayer = ArcGISDynamicMapServiceLayer;//
			dong.RasterData = RasterData;//

			esri.config.defaults.io.proxyUrl = ArcGisUrl + "/Java/proxy.jsp";
			esri.config.defaults.io.alwaysUseProxy = false;

			app.baselayerList = new Array();//默认加载矢量 new gaodeLayer({layertype:"road"});也可以
			app.stlayerList = new Array();//加载卫星图
			app.labellayerList = new Array();//加载标注图

			app.map = new Map("map_in", {
				logo: false,
				center: [stat.cPointx, stat.cPointy],
				minZoom: 4,
				maxZoom: 13,
				zoom: 5
			});

			app.baselayerList = new dong.gaodeLayer();
			app.stlayerList = new dong.gaodeLayer({layertype: "st"});
			app.labellayerList = new dong.gaodeLayer({layertype: "label"});
			app.map.addLayer(app.baselayerList);//添加高德地图到map容器
			app.map.addLayers([app.baselayerList]);//添加高德地图到map容器

			app.gLyr1 = new dong.GraphicsLayer({"id": "gLyr1"});
			app.map.addLayer(app.gLyr1);

		});





