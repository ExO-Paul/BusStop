/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Pavel
 */
public class Route {

    private List<BusStop> route; //Маршрут - список остановок
    private Iterator<BusStop> iterator;
    private boolean changed = false; //Флаг - был ли изменён маршрут после создания последнего итератора
    
    private static final String EMPTY_ROUTE_MESSAGE = "The route you tried to "
            + "navigate is empty";

    public Route() {
        route = new LinkedList<BusStop>();
        changed = true;
    }

    public Route(List<BusStop> route) {
        this.route = route;
        for(BusStop stop: route){ //Добавляем на каждую остановку запись о проходящем через неё маршруте
            stop.addToRoute(Route.this);
        }
        changed = true;
    }

    public int getStopsCount() {
        return route.size();
    }

    public boolean addStop(BusStop e) {
        changed = route.add(e);
        if (changed){
            e.addToRoute(this); //Записываем в расписание остановки информацию о маршруте
        }
        return changed;
    }

    public boolean removeStop(BusStop e) {
        changed = route.remove(e);
        if (changed){
            e.removeFromRoute(this); //удаляем из расписания остановки информацию о маршруте
        }
        return changed;
    }

    public void clearRoute() {
        for (BusStop s: route){
            s.removeFromRoute(this); //Удаляем из каждой остановки запись о проходящем через неё маршруте
        }
        route.clear();
        changed = true;
    }

    public BusStop getStopByIndex(int index) {
        return route.get(index);
    }

    public void removeStopByIndex(int index) {
        changed = true;
        route.remove(index).removeFromRoute(this); //Удаляем остановку из маршрута и сдаляем с неё запись о маршруте
    }

    public boolean hasNextStop() { //Проверка есть ли в маршруте следующая остановка
        if (changed) { //Если мрашрут изменялся, 
            iterator = route.listIterator(); //Получаем новый итератор
            changed=false;
        }
        return iterator.hasNext(); //Получаем у итератора есть ли дальше остановки
    }

    public BusStop getNextStop() throws EmptyRouteException {
        if (!this.hasNextStop()) { //Если в маршруте нет следующей остановки
            if (!route.isEmpty()) { //И если маршрут не пустой
                iterator = route.listIterator(); //Обновляем итератор, чтобы вернуть 1 остановку следующей
            } else {
                throw new EmptyRouteException(EMPTY_ROUTE_MESSAGE); //Если маршрут пустой - выбрасываем исключение
            }
        }
        return iterator.next(); //Возвращаем следующую остановку из итератора
    }
}
