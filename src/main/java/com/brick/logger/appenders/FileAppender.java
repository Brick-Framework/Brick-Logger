package com.brick.logger.appenders;

import com.brick.logger.utility.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
    Summary : Appends Log to Files on Disk
    Description: Create a PriorityBlockingQueue which determines priority
                 based on timestamp, A worker thread continually polls this queue for messages
                 when a message is received by worker thread it prints the message to file
                 Blocking nature of queue ensure thread safety
 */
public class FileAppender implements LogAppender, Closeable {
    private final PrintWriter writer;
    private final BlockingQueue<Message> queue;
    private final Thread worker;
    private volatile boolean running;

    public FileAppender(String filePath, boolean clearExistingLogs) throws IOException {
        createFileIfNotExist(filePath,clearExistingLogs);
        FileWriter fw = new FileWriter(filePath, true);
        this.writer = new PrintWriter(fw, true);
        this.queue = new PriorityBlockingQueue<>(
                10,
                (m1, m2) -> {
                    ZoneId systemZoneId = ZoneId.systemDefault();
                    ZoneRules rules = systemZoneId.getRules();
                    Instant now = Instant.now();
                    ZoneOffset offset = rules.getOffset(now);
                    return Long.compare(

                            m1.getTimestamp().toEpochSecond(offset),
                            m2.getTimestamp().toEpochSecond(offset)
                    );
                }

        );
        this.running = true;

        this.worker = new Thread(()->{
           try{
               while( running || !queue.isEmpty() ){
                   Message message = queue.poll(100, TimeUnit.MILLISECONDS);
                   if( message != null ){
                       writer.println(message);
                   }
               }
           } catch (InterruptedException e) {
        	   Thread.currentThread().interrupt();
               e.printStackTrace();
           }
        });
        this.worker.setName("appender-"+filePath+"-thread");

        worker.start();
    }

    /*
        Description: Appends Message Queue for Processing
    */
    @Override
    public void appendLog(Message message) {
        if( this.running ){
        	queue.add(message);
        }
    }

    /*
         Description: Closes Open Resources
     */
    @Override
    public void close() {
        this.running = false;
        try{
            worker.join();
        }catch (InterruptedException e){
        	Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        writer.close();
    }

    /*
        Description: Create a New File on a given path, if the clearExistingLogs flag is set
                     then it clears any existing logs at that path
     */
    private static void createFileIfNotExist(String filePath, boolean clearExistingLogs){
        Path path = Paths.get(filePath);
        try{
            Files.createDirectories(path.getParent());

            if (Files.exists(path) && clearExistingLogs) {
                Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING).close();
            }else if( !Files.exists(path) ){
                Files.createFile(path);
            }

            Files.notExists(path);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
