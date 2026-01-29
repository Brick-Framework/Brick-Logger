package com.brick.logger.appenders;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.brick.logger.utility.LogLevel;
import com.brick.logger.utility.Message;

import static org.awaitility.Awaitility.await;

public class FileAppenderTest {

    private static String readFileAsString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void test_fileOutput() throws IOException, InterruptedException {
        String filePath = "target/file-appender.log";
        FileAppender fileAppender = new FileAppender(filePath,true);

        Message logMessage = new Message();
        logMessage.setTimestamp(LocalDateTime.now());
        logMessage.setLevel(LogLevel.INFO);
        logMessage.setLogMessage("Hello World");

        fileAppender.appendLog(logMessage);

        await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        assertTrue(readFileAsString(filePath).split("\r")[0].startsWith(logMessage.toString()));
        fileAppender.close();
    }
    
    @Test
    public void threadInterruptException() throws IOException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	String filePath = "target/file-appender.log";
        FileAppender fileAppender = new FileAppender(filePath,true);

        Message logMessage = new Message();
        logMessage.setTimestamp(LocalDateTime.now());
        logMessage.setLevel(LogLevel.INFO);
        logMessage.setLogMessage("Hello World");

        fileAppender.appendLog(logMessage);

        // Example of accessing a private field named 'workerThread'
        Field field = FileAppender.class.getDeclaredField("worker");
        field.setAccessible(true);
        Thread thread = (Thread) field.get(fileAppender);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.err; // Save the original System.out

        System.setErr(new PrintStream(outputStream)); // Redirect System.out

        // Now you can interrupt it
        thread.interrupt();
        
        thread.join(1000);
        
        // Assert
        String output = outputStream.toString().split("\r")[0].trim();
        assertTrue(output.startsWith(InterruptedException.class.getName()));
        
        // Restore System.out
        System.setErr(originalOut);
    }
    
    @Test
    public void createFileIfNotExist_test() throws IOException {
    	String path1String = "target/test-file1.log";
    	String path2String = "target/test-file2.log";
    	
    	Path path1 = Paths.get(path1String);
    	Path path2 = Paths.get(path2String);
    	
    	Files.deleteIfExists(path1);
    	Files.deleteIfExists(path2);
    	
    	FileAppender fileAppender1 = new FileAppender(path1String,false);
    	FileAppender fileAppender2 = new FileAppender(path1String,true);
    	FileAppender fileAppender3 = new FileAppender(path2String,false);
    	
    	assertTrue(Files.exists(path1));
    	assertTrue(Files.exists(path2));
    	
    	fileAppender1.close();
    	fileAppender2.close();
    	fileAppender3.close();
    }
}
