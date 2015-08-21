/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

import com.epam.tc.busstop.entity.name.FirstName;
import com.epam.tc.busstop.entity.name.LastName;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Pavel
 */
public class Passenger {

    private int id;
    private String name; //Имя, генерируется псевдослучайно
    private static final Random RANDOM = new Random();
    private static int ID_COUNTER = 0;

    private enum PassengerAction {

        GET_OUT,
        STAY
    }

    public Passenger() {
        id = ID_COUNTER++;
        StringBuilder builder = new StringBuilder();
        int index = RANDOM.nextInt(FirstName.values().length); //Получаем псевдослучайное целое значение
        builder.append(FirstName.values()[index]); //Получаем имя
        builder.append(" ");
        index = RANDOM.nextInt(LastName.values().length); //Получаем псевдослучайное целое значение
        builder.append(LastName.values()[index]); //Получаем фамилию

        this.name = builder.toString();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isExiting(Bus bus, BusStop stop) { //выполнить действие (onBoard - в автобусе ли человек)
        switch (PassengerAction.values()[RANDOM.nextInt(2000) % 2]) { //Случайным образом выбирается
            case GET_OUT: //выйти или войти в автобус
                return true; //Высадка
            case STAY: //Или остаться
                return false;
        }
        return false;
    }

    public Route chooseRoute(BusStop busStop) { //Выбрать маршрут, в очередь на который встанет пассажир
        Set<Route> routeSet = busStop.getRoutesSet(); //Узнать какие маршруты проходят через остановку
        int rand = RANDOM.nextInt(routeSet.size()); //получаем случайное число, соответствующее номеру маршрута в routeSet
        Iterator iterator = routeSet.iterator();
        for (int i = 0; i < rand; i++) { //С помощью итератора доходим до значения, определённого random
            if (iterator.hasNext()) {
                iterator.next();
            }
        }
        return (Route) iterator.next(); //Возвращаем нужную остановку
    }

}
