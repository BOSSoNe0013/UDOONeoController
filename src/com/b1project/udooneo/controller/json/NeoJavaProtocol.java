package com.b1project.udooneo.controller.json;

/**
 * Copyright (C) 2015 Cyril Bosselut <bossone0013@gmail.com>
 * <p>
 * This file is part of NeoJava examples for UDOO
 * <p>
 * NeoJava examples for UDOO is free software: you can redistribute it and/or modify
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
public class NeoJavaProtocol {
    
    public final static String COMMAND_HELP = "help";
    public final static String COMMAND_VERSION = "version";
    public final static String COMMAND_QUIT = "quit";
    public final static String COMMAND_LCD_CLEAR = "lcd/clear";
    public final static String COMMAND_LCD_PRINT = "lcd/print";
    public final static String COMMAND_TEMP_REQUEST = "sensors/temperature";
    public final static String COMMAND_EXPORTED_GPIOS = "gpios/exported";
    public final static String COMMAND_SET_GPIO_MODE = "gpios/mode";
    public final static String COMMAND_SET_GPIO_STATE = "gpios/state";
    public final static String COMMAND_RELEASE_GPIO = "gpios/release";

}
