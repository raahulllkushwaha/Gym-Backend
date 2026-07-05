package com.gym.scheduler;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail membershipExpiryReminderJobDetail() throws Exception {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(MembershipExpiryReminderJob.class);
        factoryBean.setDurability(true);
        factoryBean.setName("membershipExpiryReminderJob");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /** Runs every day at 08:00 AM server time. */
    @Bean
    public org.quartz.Trigger membershipExpiryReminderTrigger(JobDetail membershipExpiryReminderJobDetail) throws Exception {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(membershipExpiryReminderJobDetail);
        factoryBean.setCronExpression("0 0 8 * * ?");
        factoryBean.setName("membershipExpiryReminderTrigger");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}