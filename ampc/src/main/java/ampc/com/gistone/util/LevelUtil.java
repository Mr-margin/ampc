package ampc.com.gistone.util;

import java.math.BigDecimal;

public class LevelUtil {
public static String Level(BigDecimal num){
	double b=num.doubleValue();
	int s=(int) b;
	if(b<50&&b>0){
		return "1";
	}
	if(b<100&&b>51){
		return "2";
	}
	if(b<150&&b>101){
		return "3";
	}
	if(b<200&&b>151){
		return "4";
	}
	if(b<300&&b>201){
		return "5";
	}
	return "6";
}
}
