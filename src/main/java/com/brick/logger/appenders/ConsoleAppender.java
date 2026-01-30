package com.brick.logger.appenders;

import com.brick.logger.utility.Message;

/*
    Summary : Prints Log Messages to Console
 */
public class ConsoleAppender implements LogAppender {

    /*
        Description: Prints Log Message to Console
                     Lock Ensure thread safety
     */
    @Override
    public synchronized void appendLog(Message message) {
    	System.out.println(message.toString());
    }
}
