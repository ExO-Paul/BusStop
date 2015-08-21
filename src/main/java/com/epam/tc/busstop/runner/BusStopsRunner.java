/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.runner;

import com.epam.tc.busstop.entity.Bus;
import com.epam.tc.busstop.entity.Passenger;
import com.epam.tc.busstop.entity.BusStop;
import com.epam.tc.busstop.entity.EmptyRouteException;
import com.epam.tc.busstop.entity.Route;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Pavel
 */
public class BusStopsRunner {

    private static final int STOP_QUANTITY = 10;
    private static final int STOP_PLACES = 3;
    private static final int BUS_QUANTITY = 20;
    private static final int PASSENGERS_QUANTITY = 100;

    private static final Logger LOG = Logger.getLogger(BusStopsRunner.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<BusStop> stops1 = makeStops(STOP_QUANTITY, STOP_PLACES); //Создаём остановки
        ArrayList<BusStop> stops2 = shuffleStops(stops1); //Получаем второй список с перемешанными остановками

        Set<ArrayList<BusStop>> stopLists = new HashSet<>();
        stopLists.add(stops2);
        stopLists.add(stops1);

        Bus[] buses = createBuses(BUS_QUANTITY, stopLists); //Создаём набор автобусов
        fillWithPassengers(buses[0].getRoute(), PASSENGERS_QUANTITY);

        startAllBuses(buses);
        try {
            Thread.sleep(2000); //Ждём
        } catch (InterruptedException ex) {

        }
        stopAllBuses(buses);
}

private static ArrayList<BusStop> makeStops(int quantity, int places) {
        ArrayList<BusStop> stops1 = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            stops1.add(new BusStop(places)); //Создаём и наполняем список остановок
        }
        return stops1;
    }

    private static ArrayList<BusStop> shuffleStops(ArrayList<BusStop> stops1) {
        ArrayList<BusStop> stops2 = (ArrayList<BusStop>) stops1.clone(); //Клонируем первый список
        Collections.shuffle(stops2); //Перемешиваем новый список
        return stops2;
    }

    private static void fillWithPassengers(Route route, int passengersQuantity) {
        try {
            BusStop stop = null;
            while (route.hasNextStop()) { //Наполняем каждую остановку 5 пассажирами
                stop = route.getNextStop();
                for (int i = 0; i < passengersQuantity; i++) {
                    stop.addPassenger(new Passenger());
                }
            }
        } catch (InterruptedException ex) {
            LOG.error("The process of adding passengers was interrupted", ex);
        } catch (EmptyRouteException ex) {
            LOG.error("The route you tried to fill was empty", ex);
        }
    }

    private static Bus[] createBuses(int busQuantity, Set<ArrayList<BusStop>> stopLists) {
        Bus[] buses = new Bus[busQuantity * stopLists.size()];
        Iterator iterator = null;
        for (int i = 0; i < buses.length; i += stopLists.size()) {
            iterator = stopLists.iterator();
            for (int j = 0; j < stopLists.size(); j++) {
                buses[i + j] = new Bus(new Route((ArrayList<BusStop>) iterator.next()));
            }
        }
        return buses;
    }

    private static void startAllBuses(Bus[] buses) {
        for (Bus b : buses) {
            new Thread(b).start();//Запускаем автобусы
        }
    }

    private static void stopAllBuses(Bus[] buses) {
        for (Bus b : buses) {
            b.setOnRoute(false); //Останавливаем автобусы
        }
    }

}
