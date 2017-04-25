package ampc.com.gistone.preprocess.obs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class ObsQuartz {

	private Logger logger = LoggerFactory.getLogger(ObsQuartz.class);
	
	@Autowired
    private ObsDateService obsDateService;

    @Autowired
    private ObsHourService obsHourService;

    private List<String> cityList;
    private List<String> stationList;
    
    @PostConstruct
    public void init(){
    	
    	//TODO update

//        cityList = getshowCity();
//
//        stationList= getShowStation();
    	
    	String[] cities = {"1101","1201","1301","1302","1303","1304","1305","1306","1307","1308","1309","1310","1311","1402","1403","1404","1407","1409","1504","1509","3701","3703","3705","3707","3709","3712","3714","3715","3716","4105", "4106", "4109"};
    	String[] stations = {"2862A","2865A","2876A","2877A","2878A","965A","966A","967A","968A","969A","970A","971A","972A","973A","974A","975A","976A","977A","978A","979A","980A","981A","982A","983A","984A","985A","？964A","2845A","2858A","2859A","2860A","2392A","2393A","2394A","2395A","2385A","2386A","2387A","2171A","2172A","2173A","2174A","2180A","2181A","2182A","2197A","2198A","1959A","1960A","1961A","1962A","1741A","1742A","1743A","1744A","1745A","1746A","1747A","1818A","1819A","1820A","1821A","1822A","1617A","1622A","1623A","1624A","1625A","1626A","1627A","1628A","1629A","1630A","1631A","1632A","1633A","1634A","1635A","1636A","1648A","1649A","1650A","1651A","1652A","1656A","1657A","1658A","1665A","1666A","1667A","1668A","1721A","1722A","1723A","1724A","1725A","1726A","1727A","1728A","1729A","1730A","1731A","1738A","1739A","1740A","1615A","1616A","1299A","1300A","1301A","1302A","1303A","1304A","1305A","1306A","1001A","1002A","1003A","1004A","1005A","1006A","1007A","1008A","1009A","1010A","1011A","1012A","1013A","1014A","1015A","1016A","1017A","1018A","1019A","1020A","1021A","1022A","1023A","1024A","1025A","1026A","1027A","1029A","1030A","1031A","1032A","1033A","1034A","1035A","1036A","1037A","1038A","1039A","1040A","1041A","1042A","1043A","1044A","1045A","1046A","1047A","1048A","1049A","1050A","1051A","1052A","1053A","1054A","1055A","1056A","1057A","1058A","1059A","1060A","1061A","1062A","1063A","1064A","1065A","1066A","1067A","1068A","1069A","1070A","1071A","1072A","1073A","1074A","1075A","1076A","1077A","1078A","1079A","1080A"};
//    	String[] stations = {"965A","966A","967A","968A","969A","970A","971A","972A","973A","974A","975A","976A","977A","978A","979A","980A","981A","982A","983A","984A","985A","964A","1001A","1002A","1003A","1004A","1005A","1006A","1007A","1008A","1009A","1010A","1011A","1012A","2858A","2859A","2860A","1013A","1014A","1015A","1016A","1017A","1018A","1019A","1020A","1021A","1022A","1023A","1024A","1025A","1026A","1027A","2862A","1028A","1029A","1030A","1031A","1032A","1033A","1034A","1035A"};
    	cityList = Arrays.asList(cities);
    	stationList = Arrays.asList(stations);
    }
    
    public void doDayTask() throws JsonProcessingException{

        //定时任务的开始时间
        LocalDateTime now = LocalDateTime.now();

        LocalDate endTime = now.toLocalDate();

        LocalDate minusDay = endTime.minus(1, ChronoUnit.DAYS);

        LocalDate startDateTime = minusDay.minus(3,ChronoUnit.DAYS);

        obsDateService.preCollectDayDataInCitys(startDateTime,endTime,cityList);

        obsDateService.preCollectDayDataInStations(startDateTime,endTime,stationList);

    }
	
    public void doHourTask(){

        //定时任务的开始时间
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime endTime = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),now.getHour(),0,0,0);

        LocalDateTime startDateTime = endTime.minus(24, ChronoUnit.HOURS);

        obsHourService.preCollectHourDataInCitys(startDateTime,endTime,cityList);

        obsHourService.preCollectHourDataInStations(startDateTime,endTime,stationList);

    }
}
