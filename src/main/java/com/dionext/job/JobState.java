package com.dionext.job;



public enum JobState {
        PLANNED,
        /**
         * The task has not completed.
         */
        RUNNING,
        /**
         * The task completed with a result.
         */
        SUCCESS,
        /**
         * The task completed with an exception.
         */
        FAILED,
        /**
         * The task was cancelled.
         */
        CANCELLED

}
