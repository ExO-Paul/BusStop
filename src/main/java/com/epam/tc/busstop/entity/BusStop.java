/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Pavel
 */
public class BusStop {

    private int id;
    Semaphore semaphore; //Семафор, отслеживающий свободные места на остановке
    private ConcurrentMap<Route, ConcurrentLinkedQueue<Passenger>> queues; //Список очередей на остановке (по маршруту нахотдится очередь пассажиров)

    private static final int DEFAULT_BUS_CAPACITY = 3; //Число автобусов, которые могут остановиться, по умолчанию
    private static int ID_COUNTER = 0;
    private static final int WAITING_TIME = 15;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    public BusStop() {
        this.id = ID_COUNTER++;
        this.semaphore = new Semaphore(DEFAULT_BUS_CAPACITY);
        this.queues = new ConcurrentHashMap<>();
    }

    public BusStop(int capacity) {
        this.id = ID_COUNTER++;
        this.semaphore = new Semaphore(capacity);
        this.queues = new ConcurrentHashMap<>();
    }

    public void addToRoute(Route route) { //Записать, что маршрут проходит черех остановку (добавить очередь на этот маршрут)
        queues.put(route, new ConcurrentLinkedQueue<Passenger>());
    }

    public void removeFromRoute(Route route) { //Убрать маршрут с остановки
        queues.remove(route);
    }

    public int getId() {
        return id;
    }

    public Set<Route> getRoutesSet() {
        return queues.keySet();
    }

    public Passenger pollPassenger(Route route) throws InterruptedException {
        return queues.get(route).poll();
    }

    public boolean addPassenger(Passenger e) throws InterruptedException {
        return queues.get(e.chooseRoute(this)).add(e);
    }

    public int getPassengersCountForRoute(Route route) {
        return queues.get(route).size();
    }

    public int getTotalPassengersCount() {
        int result = 0;
        for (ConcurrentLinkedQueue<Passenger> q : queues.values()) {
            result += q.size();
        }
        return result;
    }

    public boolean takePlace(Bus bus) throws InterruptedException { //Занять место автобусом - если есть место у остановки, встать
        return semaphore.tryAcquire(WAITING_TIME, TIME_UNIT);
    }

    public void freePlace(Bus bus) throws InterruptedException { //Освободить место у остановки
        semaphore.release();
    }
}
