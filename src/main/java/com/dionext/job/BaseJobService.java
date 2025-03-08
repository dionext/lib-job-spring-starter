package com.dionext.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
abstract public class BaseJobService {

    @Autowired
    protected JobManager jobManager;

    /*
    protected Collection<String> getItemList(){
        return Collections.emptyList();
    }
    protected Collection<String> processItem(String item){
        return Collections.emptyList();
    }
     */

}
