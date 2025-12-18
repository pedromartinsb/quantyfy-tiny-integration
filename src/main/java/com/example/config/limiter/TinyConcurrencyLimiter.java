package com.example.config.limiter;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class TinyConcurrencyLimiter {

    private final Semaphore semaphore = new Semaphore(10); // máx chamadas simultâneas

    public void execute(Runnable task) {
        try {
            semaphore.acquire();
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }
}

