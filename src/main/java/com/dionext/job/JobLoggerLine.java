package com.dionext.job;


import java.time.Instant;

public class JobLoggerLine {
    private Instant logInstant;
    private String logMessage;

    protected JobLoggerLine() {
        // for json deserialization
    }

    public JobLoggerLine(JobLogger.Level level, String logMessage) {
        this.logInstant = Instant.now();
        this.logMessage = logMessage;
    }

    public Instant getLogInstant() {
        return logInstant;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
