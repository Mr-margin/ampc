package ampc.com.gistone.preprocess.obs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class TaskSchedulerObsConfig {
	
	private final static Logger logger = LoggerFactory.getLogger(TaskSchedulerObsConfig.class);
	@Autowired
	private ObsQuartz obsQuartz;

	private boolean debug;
	private static String obsDailyExpression;
	private static String obsHourlyExpression;
	public static String OBS_URL;
	public static int timeout;
	
	@PostConstruct
	public void init() {
		getObsConfig("/obsConfig.properties");
	}

	private void getObsConfig(String config) {
		
		InputStream ins = getClass().getResourceAsStream(config);
		Properties pro = new Properties();
		try {
			pro.load(ins);
			debug = Boolean.valueOf((String) pro.get("debug"));
			obsDailyExpression = (String) pro.get("obsDailyExpression");
			obsHourlyExpression = (String) pro.get("obsHourlyExpression");
			OBS_URL = (String) pro.get("obsUrl");
			timeout = Integer.valueOf((String) pro.get("timeout"));
		} catch (FileNotFoundException e) {
			logger.error(config + " file does not exits!", e);
		} catch (IOException e) {
			logger.error("load " + config + " file error!", e);
		}
		
		try {
			if(ins != null) ins.close();
		} catch (IOException e) {
			logger.error("close " + config + "file error!", e);
		}
	}
	
	//设置方法调用
    @Bean
    public MethodInvokingJobDetailFactoryBean obsDateTask(){

        MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();

        methodInvokingJobDetailFactoryBean.setTargetObject(obsQuartz);

        methodInvokingJobDetailFactoryBean.setTargetMethod("doDayTask");

        return methodInvokingJobDetailFactoryBean;
    }
    
    //设置方法调用
    @Bean
    public MethodInvokingJobDetailFactoryBean obsHourTask(){

        MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();

        methodInvokingJobDetailFactoryBean.setTargetObject(obsQuartz);

        methodInvokingJobDetailFactoryBean.setTargetMethod("doHourTask");

        return methodInvokingJobDetailFactoryBean;
    }
    
    //设置定时器表达式
    @Bean
    public CronTriggerFactoryBean obsDateCronTrigger() {

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();

        bean.setCronExpression(obsDailyExpression);

        bean.setJobDetail(obsDateTask().getObject());

        return bean;
    }

    @Bean
    public CronTriggerFactoryBean obsHourCronTrigger() {

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();

        bean.setCronExpression(obsHourlyExpression); //每天整点运行

        bean.setJobDetail(obsHourTask().getObject());

        bean.setPriority(100);//设置优先级

        return bean;
    }
    
    //调度工厂
    @Bean
    public SchedulerFactoryBean SchedulerFactoryBean(){

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        schedulerFactoryBean.setQuartzProperties(quartzProperties());

        List<Trigger> triggerList = new ArrayList<Trigger>();

        triggerList.add(obsDateCronTrigger().getObject());

        triggerList.add(obsHourCronTrigger().getObject());

        schedulerFactoryBean.setTriggers(triggerList.toArray(new Trigger[0]));

        schedulerFactoryBean.setAutoStartup(debug);

        return schedulerFactoryBean;
    }
    
    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties properties = new Properties();
        try {
            propertiesFactoryBean.afterPropertiesSet();

           properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            logger.error("quartzProperties error", e);
        }
        return properties;
    }
}
