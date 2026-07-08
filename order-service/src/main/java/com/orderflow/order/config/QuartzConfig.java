package com.orderflow.order.config;

import com.orderflow.order.scheduler.OrderTimeoutJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(new AutowiringSpringBeanJobFactory(applicationContext));
        factory.setJobDetails(orderTimeoutJobDetail());
        factory.setTriggers(orderTimeoutTrigger());
        return factory;
    }


    @Bean
    public JobDetail orderTimeoutJobDetail() {
        return JobBuilder.newJob(OrderTimeoutJob.class)
                .withIdentity("orderTimeoutJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger orderTimeoutTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(orderTimeoutJobDetail())
                .withIdentity("orderTimeoutTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * * * ?"))
                .build();
    }
}