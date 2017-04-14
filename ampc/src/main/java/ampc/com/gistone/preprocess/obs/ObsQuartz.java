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
    	
    	String[] cities = {"1101","1201","1301","1302","1303","1304","1305","1306","1307","1308","1309","1310","1311"};
    	String[] stations = {"1001A","1002A","1003A","1004A","1005A","1006A","1007A","1008A","1009A","1010A","1011A","1012A","1014A","1015A","1016A","1017A","1018A","1019A","1021A","1023A","1024A","1025A","1026A","1027A","1029A","1030A","1031A","1032A","1033A","1034A","1035A","1036A","1037A","1038A","1039A","1040A","1041A","1042A","1043A","1044A","1045A","1046A","1047A","1048A","1049A","1050A","1077A","1078A","1079A","1080A","1051A","1052A","1053A","1054A","1055A","1056A","1057A","1058A","1059A","1060A","1061A","1062A","1063A","1064A","1065A","1066A","1071A","1072A","1073A","1067A","1068A","1069A","1070A","1074A","1075A","1076A"};
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
