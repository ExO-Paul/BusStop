/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity;

/**
 *
 * @author Pavel
 */
public class EmptyRouteException extends Exception {

    public EmptyRouteException() {
    }

    public EmptyRouteException(String message) {
        super(message);
    }

    public EmptyRouteException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
