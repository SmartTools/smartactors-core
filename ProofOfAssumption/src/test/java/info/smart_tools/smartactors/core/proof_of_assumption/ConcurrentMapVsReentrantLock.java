package info.smart_tools.smartactors.core.proof_of_assumption;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Compares performance of ConcurrentHashMap vs ReentrantLock
 */

public class ConcurrentMapVsReentrantLock {

    private final int SIZE = 1000000;
    private final int COUNT = 100;

//    @Test
    public void performanceHashMap() {
        long startTime;
        long writeTime;
        long endTime;
        long average = 0;
        long averageW = 0;
        long averageR = 0;
        System.out.printf("Size - %s \n", SIZE);

        for( int n = 0; n < COUNT; n++ ){
            Map<String, Integer> noneThreadsafeCollection = new HashMap<String, Integer>();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                noneThreadsafeCollection.put(Integer.toString(i), i);
            }
            writeTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                noneThreadsafeCollection.get(Integer.toString(i));
            }
            endTime = System.currentTimeMillis();
            noneThreadsafeCollection.clear();
//            System.out.printf("None threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", (writeTime - startTime), (endTime - writeTime), (endTime - startTime));
            average += (endTime - startTime);
            averageR += (endTime - writeTime);
            averageW += (writeTime - startTime);
        }
        System.out.printf("None threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", averageW / COUNT, averageR / COUNT, average / COUNT);

    }

//    @Test
    public void performanceConcurrentHashMap() {
        long startTime;
        long writeTime;
        long endTime;
        long average = 0;
        long averageW = 0;
        long averageR = 0;

        System.out.printf("Size - %s \n", SIZE);
        for( int n = 0; n < COUNT; n++ ){
            Map<String, Integer> threadsafeCollection = new ConcurrentHashMap<String, Integer>();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                threadsafeCollection.put(Integer.toString(i), i);
            }
            writeTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                threadsafeCollection.get(Integer.toString(i));
            }
            endTime = System.currentTimeMillis();
            threadsafeCollection.clear();
            //System.out.printf("Threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", (writeTime - startTime), (endTime - writeTime), (endTime - startTime));
            average += (endTime - startTime);
            averageR += (endTime - writeTime);
            averageW += (writeTime - startTime);
        }
        System.out.printf("None threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", averageW / COUNT, averageR / COUNT, average / COUNT);
    }

//    @Test
    public void performanceHashMapWithLock() {
        long startTime;
        long writeTime;
        long endTime;
        long average = 0;
        long averageW = 0;
        long averageR = 0;

        System.out.printf("Size - %s \n", SIZE);
        for( int n = 0; n < COUNT; n++ ){
            Map<String, Integer> noneThreadsafeLCollection = new HashMap<String, Integer>();
            Lock l = new ReentrantLock();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                l.lock();
                try {
                    noneThreadsafeLCollection.put(Integer.toString(i), i);
                } finally {
                    l.unlock();
                }
            }
            writeTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                l.lock();
                try {
                    Integer value = noneThreadsafeLCollection.get(Integer.toString(i));
                } finally {
                    l.unlock();
                }
            }
            endTime = System.currentTimeMillis();
            noneThreadsafeLCollection.clear();
            //System.out.printf("None Threadsafe w Lock\nWrite time: %s\nReadTime: %s\nAll time: %s\n", (writeTime - startTime), (endTime - writeTime), (endTime - startTime));
            average += (endTime - startTime);
            averageR += (endTime - writeTime);
            averageW += (writeTime - startTime);
        }
        System.out.printf("None threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", averageW / COUNT, averageR / COUNT, average / COUNT);
    }

//    @Test
    public void performanceHashMapWithReadWriteLock() {
        long startTime;
        long writeTime;
        long endTime;
        long average = 0;
        long averageW = 0;
        long averageR = 0;

        System.out.printf("Size - %s \n", SIZE);
        for( int n = 0; n < COUNT; n++ ){
            Map<String, Integer> noneThreadsafeRWLCollection = new HashMap<String, Integer>();
            ReadWriteLock rw = new ReentrantReadWriteLock();
            Lock rl = rw.readLock();
            Lock wl = rw.writeLock();
            startTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                wl.lock();
                try {
                    noneThreadsafeRWLCollection.put(Integer.toString(i), i);
                } finally {
                    wl.unlock();
                }
            }
            writeTime = System.currentTimeMillis();
            for (int i = 0; i < SIZE; ++i) {
                rl.lock();
                try {
                    Integer value = noneThreadsafeRWLCollection.get(Integer.toString(i));
                } finally {
                    rl.unlock();
                }
            }
            endTime = System.currentTimeMillis();
            noneThreadsafeRWLCollection.clear();
            //System.out.printf("None Threadsafe w ReadWriteLock\nWrite time: %s\nReadTime: %s\nAll time: %s\n", (writeTime - startTime), (endTime - writeTime), (endTime - startTime));
            average += (endTime - startTime);
            averageR += (endTime - writeTime);
            averageW += (writeTime - startTime);
        }
        System.out.printf("None threadsafe\nWrite time: %s\nReadTime: %s\nAll time: %s\n", averageW / COUNT, averageR / COUNT, average / COUNT);
    }
}
