package com.jfinal.plugin.quartz;

import com.jfinal.log.Log;
import org.quartz.*;
import org.quartz.Job;
import org.quartz.impl.StdSchedulerFactory;


public abstract class JFinalJob implements Job {
    private SchedulerFactory schedulerfactory;
    private Scheduler scheduler;
    private final String name = this.getClass().getName();
    private static Log log = Log.getLog(QuartzPlugin.class);
    private String cron;

    public JFinalJob() {
    }

    public JFinalJob cron(String cron) {
        this.cron = cron;
        return this;
    }


    public void start() {
        //通过schedulerFactory获取一个调度器
        try {
            schedulerfactory = new StdSchedulerFactory();
            scheduler = null;
            //通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();
            //创建jobDetail实例，绑定Job实现类
            //指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = JobBuilder.newJob(this.getClass()).withIdentity(JobConstant.JOB_NAME + name, JobConstant.JOB_GROUP + name).build();
            // 定义调度触发规则
            //使用cornTrigger规则  每天10点42分
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(JobConstant.TRIGGER_NAME + name, JobConstant.TRIGGER_GROUP + name)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .startNow().build();
            //把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
            //启动调度
            scheduler.start();
            log.debug("job: " + name + "is started.");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            scheduler.shutdown();
            log.debug("job: " + name + "is stopped.");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute(JobExecutionContext context) throws JobExecutionException;
}
