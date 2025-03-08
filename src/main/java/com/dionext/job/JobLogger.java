package com.dionext.job;



import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JobLogger {
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    private ConcurrentLinkedQueue< JobLoggerLine> logLines;

    public JobLogger() {
        this.logLines = new ConcurrentLinkedQueue<>();
    }

    public void add( JobLoggerLine line) {
        logLines.add(line);
    }

    public Queue< JobLoggerLine> getLogLines() {
        return logLines;
    }

    public void debug(String debugMessage) {
        logLines.add(new JobLoggerLine(Level.DEBUG, debugMessage));
    }

    public void info(String infoMessage) {
        logLines.add(new JobLoggerLine(JobLogger.Level.INFO, infoMessage));
    }

    public void warn(String warnMessage) {
        logLines.add(new JobLoggerLine(Level.WARN, warnMessage));
    }

    public void error(String errorMessage) {
        logLines.add(new JobLoggerLine(Level.ERROR, errorMessage));
    }


}
