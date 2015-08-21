/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author Pavel
 */
public class Bus implements Runnable {

    private int id; //Идентификатор
    private List<Passenger> passengers; //Список пассажиров
    private int passengerCapacity; //Пассажировместимость (max пассажиров)
    private Route route; //Маршрут автобуса
    private Lock busLock; //Замок автобуса
    private Conductor conductor; //Объект класса Conductor

    private volatile boolean onRoute = true; //Переменная, отвечающая за остановку

    private static int ID_COUNTER = 0; //Вспомогательная переменная, для генерации id
    private static final int DEFAULT_PASSENGER_CAPACITY = 45; //Пассажировместимость по умолчанию
    private static final int WAITING_TIME = 1000; //Время ожидания до повторной попытки встать у остановки
    
    private static final Logger LOG = Logger.getLogger(Bus.class);

    public Bus(Route route) {
        this.id = ID_COUNTER++;
        this.route = route; 
        this.passengers = new ArrayList<>();
        this.passengerCapacity = DEFAULT_PASSENGER_CAPACITY;
        this.busLock = new ReentrantLock();
        this.conductor = new Conductor();
    }

    public Bus(int passengerCapacity, Route route) {
        this.id = ID_COUNTER++;
        this.passengerCapacity = passengerCapacity;
        this.route = route;
        this.busLock = new ReentrantLock();
        this.conductor = new Conductor();
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getPassengersCount() {
        return passengers.size();
    }

    public boolean addPassenger(Passenger e) { //Если число пассажиров меньше пассажировместимости,
        if (passengers.size() < passengerCapacity) { //добавить пассажира
            return passengers.add(e);
        } else {
            LOG.info("Bus " + this.getId() + " is too full "); //Иначе возвращает false
            return false;
        }
    }

    public boolean removePassenger(Passenger p) {
        return passengers.remove(p);
    }

    public Passenger getPassenger(int index) {
        return passengers.get(index);
    }

    public boolean isOnRoute() {
        return onRoute;
    }

    public void setOnRoute(boolean onRoute) {
        this.onRoute = onRoute;
    }

    public Lock getBusLock() {
        return busLock;
    }
    
    public boolean hasPlaces(){
        return passengers.size()<passengerCapacity;
    }

    
    @Override
    public void run() {
        try {
            BusStop nextStop = route.getNextStop(); //Получаем следующую остановку из маршрута
            boolean stopTaken = false;
            while (onRoute) {
                try {
                    stopTaken = nextStop.takePlace(this);
                    if (stopTaken) { //Если удалось занять место у остановки
                        LOG.info("Bus " + this.getId() + " came to stop "
                                + nextStop.getId());
                        conductor.unloadAndLoad(this, nextStop); //Начать высадку и посадку

                    } else {
                        LOG.info("Bus " + this.getId() + " is still waiting for stop "
                                + nextStop.getId());
                        Thread.sleep(WAITING_TIME); //Если место занять не удалось, ждём
                    }
                } finally {
                    if (stopTaken) {
                        LOG.info("Bus " + this.getId() + " leaving stop "
                                + nextStop.getId());
                        nextStop.freePlace(this); //Освободить место
                        
                        nextStop = route.getNextStop(); //Получить следующую остановку
                    }
                }
            }
        } catch (InterruptedException ex) {
            LOG.error("Bus was terminated");
        } catch (EmptyRouteException ex) {
            LOG.error("Bus had an empty route");
        }
    }

}
