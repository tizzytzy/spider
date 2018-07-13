package com.qingguatang.spider.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class PhantomjsService {

    private static final Logger logger = LoggerFactory.getLogger(PhantomjsService.class);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @PostConstruct
    public void init(){
        ProcessBuilder pb = new ProcessBuilder("phantomjs","webserver.js","8081");
        pb.redirectErrorStream(true);
        try{
            Process process = pb.start();
            ResultStreamHandler inputStreamHandler = new ResultStreamHandler(process.getInputStream());
            ResultStreamHandler errorStreamHandler = new ResultStreamHandler(process.getErrorStream());
            threadPool.execute(inputStreamHandler);
            threadPool.execute(errorStreamHandler);
        }catch (IOException e){
            logger.error("",e);
        }
    }

    class ResultStreamHandler implements Runnable{

        private InputStream in;

        ResultStreamHandler(InputStream in){
            this.in = in;
        }
        @Override
        public void run(){
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = bufferedReader.readLine()) != null){
                    logger.error("phantomjs:" + line);
                }
            }catch (Throwable t){
                logger.error("",t);
            }finally {
                try {
                    bufferedReader.close();
                }catch (IOException e){
                    logger.error("",e);
                }
            }
        }
    }

}

