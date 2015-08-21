/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epam.tc.busstop.entity.name;

/**
 *
 * @author Pavel
 */
public enum LastName {
     
    JOHNSON("Johnson"),
    GRAYFORD("Grayford"),
    MARLOW("Marlow"),
    WILLSON("Willson"),
    STROAN("Stroan"),
    CANTERBURY("Canterbury"),
    DERRINGER("Derringer"),
    DRAKE("Drake");
    
    private String name;

    private LastName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
