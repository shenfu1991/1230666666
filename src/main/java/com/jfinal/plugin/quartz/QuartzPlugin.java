package com.jfinal.plugin.quartz;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuartzPlugin implements IPlugin {
    private boolean isStarted = false;

    private static Log log = Log.getLog(QuartzPlugin.class);

    private ConcurrentHashMap<String, JFinalJob> jobMap = new ConcurrentHashMap<String, JFinalJob>();

    public void addJob(JFinalJob job) {
        if (jobMap.containsKey(job.getClass().getSimpleName())) {
            log.error("There is a same job,name is :" + job.getClass().getSimpleName());
            return;
        }
        log.debug("Add a new job,name is :" + job.getClass().getSimpleName());
        jobMap.put(job.getClass().getSimpleName(), job);
    }

    public boolean start() {
        if (isStarted) return true;
        //
        Set<Map.Entry<String, JFinalJob>> entries = jobMap.entrySet();
        if (jobMap.size() == 0) {
            log.debug("No job has been started");
            return true;
        }
        for (Map.Entry entry : entries) {
            JFinalJob value = (JFinalJob) entry.getValue();
            value.start();
        }
        log.info("JFinal job has been started.");
        isStarted = true;
        return true;
    }

    public boolean stop() {
        if (!isStarted) return true;
        //
        Set<Map.Entry<String, JFinalJob>> entries = jobMap.entrySet();
        for (Map.Entry entry : entries) {
            JFinalJob value = (JFinalJob) entry.getValue();
            value.shutdown();
        }
        log.info("JFinal job has been stopped.");
        isStarted = false;
        return true;
    }
}
