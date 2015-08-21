/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.apache.log4j.Logger;

/**
 *
 * @author Pavel
 */
public class Conductor { //Класс, отвечающий за синхронизацию

    private static final int WAITING_TIME = 15; //Время ожидания взятия замка

    private static final Logger LOG = Logger.getLogger(Conductor.class);

    public boolean unloadAndLoad(Bus bus, BusStop stop) throws InterruptedException {
        boolean busLocked = false; //Флаг того, удалось ли заблокировать пассажиров автобуса
        Lock busLock = bus.getBusLock(); //Получение замка автобуса
        try {
            busLocked = busLock.tryLock(WAITING_TIME, TimeUnit.MINUTES); // Попытка блокировать замок на автобусе

            if (busLocked) { // Если удалось заблокировать автобус
                unloadPassengers(bus, stop); //Выгружаем пассажиров
                loadPassengers(bus, stop); //Загружаем пассажиров
            }

            return true; //Возврат true, если операция удалась
        } finally {
            if (busLocked) {
                busLock.unlock(); //Если замок был взят, снимаем его
                busLocked = false;
            }
        }
    }

    private void unloadPassengers(Bus bus, BusStop stop) throws InterruptedException {

        int passengersBefore = bus.getPassengersCount(); //Получение числа пассажиров в автобусе до операции
        Passenger passenger;
        for (int i = 0; i < bus.getPassengersCount(); i++) {
            passenger = bus.getPassenger(i); //Каждого пассажира автобуса опрашиваем
            if (passenger.isExiting(bus, stop)) { //Выходит ли он на этой остановке - если да:
                if (bus.removePassenger(passenger)) { //Если пассажир вышел из автобуса
                    stop.addPassenger(passenger); //Поместить его на остановку
                }
            }
        }

        LOG.info("Bus " + bus.getId() + " has unloaded "
                + (passengersBefore - bus.getPassengersCount())
                + " passengers, " + bus.getPassengersCount()
                + " on board "
        );

    }

    private void loadPassengers(Bus bus, BusStop stop) throws InterruptedException {
        int passengersBefore = stop.getTotalPassengersCount(); //Получение общего количества пассажиров на остановке до операции
        Passenger passenger;
        while (bus.hasPlaces() && //Пока в автобусе есть места
                (passenger = stop.pollPassenger(bus.getRoute())) != null) { //И мы получаем из очереди не нулевые значения 
            //(в очереди людей, ожидавших автобуса этого маршрута, есть люди)
            bus.addPassenger(passenger); //Добавить полученного пассажира из очереди в автобус
        }
        LOG.info("Bus " + bus.getId() + " has loaded "
                + (passengersBefore - stop.getTotalPassengersCount())
                + " passengers, " + bus.getPassengersCount()
                + " on board "
        );
    }

}
