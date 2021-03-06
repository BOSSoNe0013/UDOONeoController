package com.b1project.udooneo.controller.json;

/**
 * Copyright (C) 2015 Cyril Bosselut <bossone0013@gmail.com>
 * <p>
 * This file is part of UDOO Neo Controller
 * <p>
 * UDOO Neo Controller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PinObject {
    public Integer id;
    public String state = "LOW";
    public String mode = "OUTPUT";

    public PinObject(Integer id, String state, String mode){
        this.id = id;
        this.state = state;
        this.mode = mode;
    }
}
