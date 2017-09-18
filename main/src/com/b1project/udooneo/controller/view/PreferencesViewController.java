package com.b1project.udooneo.controller.view;

import com.b1project.udooneo.controller.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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
@SuppressWarnings("unused")
public class PreferencesViewController implements Initializable {
    @FXML
    private ListView<GridPane> menuListView;
    @FXML
    private GridPane boardMenuItem;
    @FXML
    private AnchorPane boardPane;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private Button saveHostButton;

    private Preferences preferences;
    private String ipAddress = "";
    private String port = "";
    private boolean willConsume = false;

    public PreferencesViewController(){

    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle){
        menuListView.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, oldItem, newItem) -> System.out.println("item selected " + newItem.getId()));
        menuListView.getSelectionModel().select(0);
        preferences = Preferences.userNodeForPackage(MainApp.class);

        ipAddress = preferences.get(MainApp.PREF_SERVER_URI, "");
        ipTextField.setText(ipAddress);
        ipTextField.setOnKeyPressed(keyEvent -> saveHostButton.setDisable(ipTextField.getText().equals(ipAddress)));

        port = preferences.get(MainApp.PREF_SERVER_PORT, "");
        portTextField.setText(port);

        KeyCode[] keyCodes = new KeyCode[]{KeyCode.BACK_SPACE, KeyCode.DELETE, KeyCode.NUMPAD0, KeyCode.NUMPAD1,
                KeyCode.NUMPAD2, KeyCode.NUMPAD3, KeyCode.NUMPAD4, KeyCode.NUMPAD5, KeyCode.NUMPAD6, KeyCode.NUMPAD7,
                KeyCode.NUMPAD8, KeyCode.NUMPAD9, KeyCode.ENTER};
        List<KeyCode> keyCodesList = Arrays.asList(keyCodes);

        portTextField.setOnKeyPressed(keyEvent -> {
            if(!keyCodesList.contains(keyEvent.getCode())){
                willConsume = true;
            }
            else {
                saveHostButton.setDisable(ipTextField.getText().equals(ipAddress));
            }
        });
        portTextField.setOnKeyReleased(keyEvent -> {
            if(willConsume){
                keyEvent.consume();
            }
            willConsume = false;
        });

        saveHostButton.setOnAction(this::handleSaveHost);
    }

    @FXML
    private void handleSaveHost(ActionEvent event){
        ipAddress = ipTextField.getText();
        port = portTextField.getText();
        if(ipAddress.equals("")){
            preferences.remove(MainApp.PREF_SERVER_URI);
        }
        else {
            preferences.put(MainApp.PREF_SERVER_URI, ipTextField.getText());
        }
        if(port.equals("")){
            preferences.remove(MainApp.PREF_SERVER_PORT);
        }
        else {
            preferences.putInt(MainApp.PREF_SERVER_PORT, Integer.parseInt(portTextField.getText()));
        }
        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        finally {
            saveHostButton.setDisable(true);
        }
    }

}
