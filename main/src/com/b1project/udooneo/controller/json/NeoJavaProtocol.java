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
@SuppressWarnings("unused")
public class NeoJavaProtocol {

    public final static String REQ_HELP = "help";
    public final static String REQ_VERSION = "version";
    public final static String REQ_QUIT = "quit";
    public static final String REQ_SERVER_ACTION = "message/server";
    public final static String REQ_SENSORS_TEMPERATURE = "sensors/temperature";
    public final static String REQ_SENSORS_MAGNETOMETER = "sensors/magnetometer";
    public final static String REQ_SENSORS_ACCELEROMETER = "sensors/accelerometer";
    public final static String REQ_SENSORS_GYROSCOPE = "sensors/gyroscope";
    public final static String REQ_GPIOS_EXPORT = "gpios/exported";
    public final static String REQ_GPIO_SET_MODE = "gpios/mode";
    public final static String REQ_GPIO_SET_STATE = "gpios/state";
    public final static String REQ_GPIO_RELEASE = "gpios/release";
    public final static String REQ_LCD_CLEAR = "lcd/clear";
    public final static String REQ_LCD_PRINT = "lcd/print";
    public static final String REQ_BOARD_ID = "board/id";
    public static final String REQ_BOARD_MODEL = "board/model";
    public static final String REQ_BOARD_NAME = "board/name";

    public static final String RESP_HELP = "resp/"+REQ_HELP;
    public static final String RESP_VERSION = "resp/"+REQ_VERSION;
    public final static String RESP_QUIT = "resp/"+REQ_QUIT;
    public static final String RESP_GPIOS_EXPORT = "resp/"+REQ_GPIOS_EXPORT;
    public static final String RESP_SET_PIN_MODE = "resp/"+REQ_GPIO_SET_MODE;
    public static final String RESP_SET_PIN_STATE = "resp/"+REQ_GPIO_SET_STATE;
    public static final String RESP_TEMPERATURE = "resp/"+REQ_SENSORS_TEMPERATURE;
    public static final String RESP_GYROSCOPE = "resp/"+REQ_SENSORS_GYROSCOPE;
    public static final String RESP_ACCELEROMETER = "resp/"+REQ_SENSORS_ACCELEROMETER;
    public static final String RESP_MAGNETOMETER = "resp/"+REQ_SENSORS_MAGNETOMETER;
    public static final String RESP_LCD_CLEAR = "resp/"+REQ_LCD_CLEAR;
    public static final String RESP_LCD_PRINT = "resp/"+REQ_LCD_PRINT;
    public static final String RESP_BOARD_ID = "resp/"+REQ_BOARD_ID;
    public static final String RESP_BOARD_MODEL = "resp/"+REQ_BOARD_MODEL;
    public static final String RESP_BOARD_NAME = "resp/"+REQ_BOARD_NAME;

    public final static class ServerMessages{
        public final static String SHUTDOWN = "shutdown";

    }
}
