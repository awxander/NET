package ru.nsu.fit.tsibin;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {
        APIWorker apiWorker = new APIWorker();
        try {

            apiWorker.start();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
