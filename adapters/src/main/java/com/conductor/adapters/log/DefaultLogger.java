package com.conductor.adapters.log;

import com.conductor.ports.log.Logger;
import com.conductor.ports.log.model.Message;

import org.springframework.stereotype.Component;

@Component
public class DefaultLogger implements Logger {
    @Override
    public void log(Message msg){
        System.out.print(msg.msg);
    }
}
