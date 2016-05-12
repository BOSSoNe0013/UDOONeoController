package com.b1project.udooneo.controller.json.messages.response;

import com.b1project.udooneo.controller.json.PinObject;
import com.b1project.udooneo.controller.json.messages.ResponseMessage;

import java.util.List;

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
public class ResponseExportGpios extends ResponseMessage {
    public List<PinObject> content;

    public ResponseExportGpios(String method, String info, List<PinObject> gpios) {
        super(method, info);
        this.content = gpios;
    }
}
