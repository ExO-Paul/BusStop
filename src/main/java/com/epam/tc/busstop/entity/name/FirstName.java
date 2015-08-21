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
public enum FirstName {
    
    CHARLES("Charles"),
    JAMES("James"),
    PETER("Peter"),
    GEORGE("George"),
    JANE("Jane"),
    MARGARETH("Margareth"),
    WILLIAM("William"),
    ELIZABETH("Elizabeth");
    
    private String name;

    private FirstName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
