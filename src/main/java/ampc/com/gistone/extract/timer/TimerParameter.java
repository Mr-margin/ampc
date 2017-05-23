package ampc.com.gistone.extract.timer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:timer.properties")
public class TimerParameter {

	@Value("${dayDeleteFile}")
	private String dayDeleteFilePath;
	@Value("${manyDaysDeleteFile}")
	private String manyDaysDeleteFilePath;
	@Value("${manyDays}")
	private Long manyDays;

	public Long getManyDays() {
		return manyDays * 1000 * 60 * 60 * 24;
	}

	public String getDayDeleteFilePath() {
		return dayDeleteFilePath;
	}

	public String getManyDaysDeleteFilePath() {
		return manyDaysDeleteFilePath;
	}

}
