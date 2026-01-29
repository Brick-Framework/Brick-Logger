package com.brick.logger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoggerTest {

    private static String readFileAsString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static boolean isWithinFiveMinutes(LocalDateTime reference) {
        // Calculate the duration between the two
        long minutesDifference = Duration.between(reference, LocalDateTime.now()).toMinutes();
        // Check if the difference is within 5 minutes (both past and future)
        return Math.abs(minutesDifference) <= 5;
    }
    
    @Test
    @Order(1)
    public void Logger_trace_test() throws IOException {
    	Logger.trace("TraceLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
    	
        String fileData = readFileAsString("target/trace-logfile.log");
        String[] logData = fileData.split("\r")[0].split(" ");
        LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("TRACE",logData[1]);
        assertTrue(logData[3].startsWith("log-message:TraceLoggerMessage"));
        
        
        //All File
        fileData = readFileAsString("target/all-logfile1.log");
        logData = fileData.split("\r")[0].split(" ");
        logDateTime = LocalDateTime.parse(logData[0]);
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("TRACE",logData[1]);
        assertTrue(logData[3].startsWith("log-message:TraceLoggerMessage"));
    }
    
    @Test
    @Order(2)
    public void Logger_debug_test() throws IOException {
    	Logger.debug("DebugLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);

    	String fileData = readFileAsString("target/debug-logfile.log");
    	String[] logData = fileData.split("\r")[0].split(" ");
    	LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        logData = fileData.split("\r")[0].split(" ");
        logDateTime = LocalDateTime.parse(logData[0]);
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("DEBUG",logData[1]);
        assertTrue(logData[3].startsWith("log-message:DebugLoggerMessage"));
    }
    
    @Test
    @Order(3)
    public void Logger_info_test() throws IOException {
    	Logger.info("InfoLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);

    	String fileData = readFileAsString("target/info-logfile1.log");
    	String[] logData = fileData.split("\r")[0].split(" ");
    	LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        logData = fileData.split("\r")[0].split(" ");
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("INFO",logData[1]);
        assertTrue(logData[3].startsWith("log-message:InfoLoggerMessage"));
    }
    
    @Test
    @Order(4)
    public void Logger_warn_test() throws IOException {
    	Logger.warn("WarnLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);

    	String fileData = readFileAsString("target/warn-logfile.log");
    	String[] logData = fileData.split("\r")[0].split(" ");
    	LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        logData = fileData.split("\r")[0].split(" ");
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("WARN",logData[1]);
        assertTrue(logData[3].startsWith("log-message:WarnLoggerMessage"));
    }
    
    @Test
    @Order(5)
    public void Logger_error_test() throws IOException {
    	Logger.error("ErrorLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);

    	String fileData = readFileAsString("target/error-logfile.log");
    	String[] logData = fileData.split("\r")[0].split(" ");
    	LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        logData = fileData.split("\r")[0].split(" ");
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("ERROR",logData[1]);
        assertTrue(logData[3].startsWith("log-message:ErrorLoggerMessage"));
    }
    
    @Test
    @Order(6)
    public void Logger_fatal_test() throws IOException {
    	Logger.fatal("FatalLoggerMessage");
    	await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);

    	String fileData = readFileAsString("target/fatal-logfile.log");
    	String[] logData = fileData.split("\r")[0].split(" ");
    	LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        logData = fileData.split("\r")[0].split(" ");
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("FATAL",logData[1]);
        assertTrue(logData[3].startsWith("log-message:FatalLoggerMessage"));
    }
    
    @Test
    @Order(7)
    public void Logger_exception_error_test() throws IOException {
    	//Exception Test
        Exception exception = new Exception("ExceptionMessage");
        Logger.logException(exception);
        await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        String fileData = readFileAsString("target/warn-logfile.log");
        String[] logData = fileData.split("\\R")[2].split(" ");
        LocalDateTime logDateTime = LocalDateTime.parse(logData[0]);
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("WARN",logData[1]);
        
        //Error Test
        Error error = new Error("ErrorMessage");
        Logger.logError(error);
        await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        fileData = readFileAsString("target/error-logfile.log");
        logData = fileData.split("\\R")[2].split(" ");
        logDateTime = LocalDateTime.parse(logData[0]);
        assertTrue(isWithinFiveMinutes(logDateTime));
        assertEquals("ERROR",logData[1]);
    }
}