package ampc.com.gistone.extract.vertical;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:specie.properties")
public class SpecieIndexList {

	@Value("${hourly_PM10}")
	private String hourly_PM10;
	@Value("${hourly_PM25}")
	private String hourly_PM25;
	@Value("${hourly_O3}")
	private String hourly_O3;
	@Value("${hourly_SO2}")
	private String hourly_SO2;
	@Value("${hourly_NO2}")
	private String hourly_NO2;
	@Value("${hourly_CO}")
	private String hourly_CO;

	@Value("${daily_PM10}")
	private String daily_PM10;
	@Value("${daily_PM25}")
	private String daily_PM25;
	@Value("${daily_O3_8_max}")
	private String daily_O3_8_max;
	@Value("${daily_O3_1_max}")
	private String daily_O3_1_max;
	@Value("${daily_O3_avg}")
	private String daily_O3_avg;
	@Value("${daily_NO2}")
	private String daily_NO2;
	@Value("${daily_SO2}")
	private String daily_SO2;
	@Value("${daily_CO}")
	private String daily_CO;

	@Value("${diff_PM25}")
	private String diff_PM25;
	@Value("${diff_PM10}")
	private String diff_PM10;
	@Value("${diff_O3}")
	private String diff_O3;
	@Value("${diff_SO2}")
	private String diff_SO2;
	@Value("${diff_NO2}")
	private String diff_NO2;
	@Value("${diff_CO}")
	private String diff_CO;

	@Value("${ratio}")
	private String ratio;

	public String getHourly_PM10() {
		return hourly_PM10;
	}

	public String getHourly_PM25() {
		return hourly_PM25;
	}

	public String getHourly_O3() {
		return hourly_O3;
	}

	public String getHourly_SO2() {
		return hourly_SO2;
	}

	public String getHourly_NO2() {
		return hourly_NO2;
	}

	public String getHourly_CO() {
		return hourly_CO;
	}

	public String getDaily_PM10() {
		return daily_PM10;
	}

	public String getDaily_PM25() {
		return daily_PM25;
	}

	public String getDaily_O3_8_max() {
		return daily_O3_8_max;
	}

	public String getDaily_O3_1_max() {
		return daily_O3_1_max;
	}

	public String getDaily_O3_avg() {
		return daily_O3_avg;
	}

	public String getDaily_NO2() {
		return daily_NO2;
	}

	public String getDaily_SO2() {
		return daily_SO2;
	}

	public String getDaily_CO() {
		return daily_CO;
	}

	public String getDiff_PM25() {
		return diff_PM25;
	}

	public String getDiff_PM10() {
		return diff_PM10;
	}

	public String getDiff_O3() {
		return diff_O3;
	}

	public String getDiff_SO2() {
		return diff_SO2;
	}

	public String getDiff_NO2() {
		return diff_NO2;
	}

	public String getDiff_CO() {
		return diff_CO;
	}

	public String getRatio() {
		return ratio;
	}

}
