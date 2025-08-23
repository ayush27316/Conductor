package com.conductor.ports.log;

import com.conductor.ports.log.model.Message;

public interface Logger {
    public void log(Message msg);
}
