package com.b1project.udooneo.controller.model;

import javafx.beans.property.*;

/**
 * Copyright (C) 2015 Cyril Bosselut <bossone0013@gmail.com>
 * <p/>
 * This file is part of UDOO Neo Controller
 * <p/>
 * UDOO Neo Controller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Pin implements Comparable<Pin> {
    private final IntegerProperty id;
    private final BooleanProperty state;
    private final StringProperty mode;

    public Pin(){
        this(null, null, null);
    }

    public Pin(Integer id, Boolean state, String mode) {
        this.id = new SimpleIntegerProperty(id);
        this.state = new SimpleBooleanProperty(state);
        this.mode = new SimpleStringProperty(mode);
    }

    public int getId(){
        return id.get();
    }

    public void setId(Integer id){
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public boolean getState(){
        return state.get();
    }

    public void setState(boolean state){
        this.state.set(state);
    }

    public BooleanProperty stateProperty() {
        return state;
    }

    public String getMode(){
        return this.mode.get();
    }

    public void setMode(String mode) {
        this.mode.set(mode);
    }

    public StringProperty modeProperty() {
        return mode;
    }

    @Override
    public int compareTo(Pin another) {
        if(getId() > another.getId()){
            return 1;
        }
        else if(getId() < another.getId()){
            return -1;
        }
        return 0;
    }
}
