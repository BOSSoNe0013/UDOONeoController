package com.b1project.udooneo.controller;

import com.b1project.udooneo.controller.json.*;
import com.b1project.udooneo.controller.json.messages.Message;
import com.b1project.udooneo.controller.json.messages.ResponseMessage;
import com.b1project.udooneo.controller.json.messages.response.ResponseExportGpios;
import com.b1project.udooneo.controller.json.messages.response.ResponseSensorData;
import com.b1project.udooneo.controller.json.messages.response.ResponseSetPinMode;
import com.b1project.udooneo.controller.json.messages.response.ResponseSetPinState;
import com.b1project.udooneo.controller.listener.AccelerometerListener;
import com.b1project.udooneo.controller.listener.GyroscopeListener;
import com.b1project.udooneo.controller.listener.MagnetometerListener;
import com.b1project.udooneo.controller.model.Pin;
import com.b1project.udooneo.controller.view.MainViewController;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

public class MainApp extends Application {

    private final static String OUTPUT_STATE_CHANGED = "StateChanged";
    private final static String OUTPUT_MODE_CHANGED = "ModeChanged";
    private final static String ERROR = "ERROR";

    private Preferences userPreferences;
    public final static String PREF_SERVER_URI = "server_uri";
    public final static String PREF_SERVER_PORT = "server_port";
    public final static String PREF_REQUEST_HISTORY = "request_history";

    private final static String DEFAULT_HOST_ADDRESS = "192.168.7.2";
    private final static int DEFAULT_HOST_PORT = 45045;

    private ObservableList<Pin> exportedGpios = FXCollections.observableArrayList();
    public static final List<Pin> allGpios = new ArrayList<>();
    static {
        //external bank
        allGpios.add(new Pin(106, false, "OUTPUT"));
        allGpios.add(new Pin(107, false, "OUTPUT"));
        allGpios.add(new Pin(180, false, "OUTPUT"));
        allGpios.add(new Pin(181, false, "OUTPUT"));
        allGpios.add(new Pin(172, false, "OUTPUT"));
        allGpios.add(new Pin(173, false, "OUTPUT"));
        allGpios.add(new Pin(182, false, "OUTPUT"));
        allGpios.add(new Pin(24, false, "OUTPUT"));
        allGpios.add(new Pin(25, false, "OUTPUT"));
        allGpios.add(new Pin(22, false, "OUTPUT"));
        allGpios.add(new Pin(14, false, "OUTPUT"));
        allGpios.add(new Pin(15, false, "OUTPUT"));
        allGpios.add(new Pin(16, false, "OUTPUT"));
        allGpios.add(new Pin(17, false, "OUTPUT"));
        allGpios.add(new Pin(18, false, "OUTPUT"));
        allGpios.add(new Pin(19, false, "OUTPUT"));
        allGpios.add(new Pin(20, false, "OUTPUT"));
        allGpios.add(new Pin(21, false, "OUTPUT"));
        allGpios.add(new Pin(203, false, "OUTPUT"));
        allGpios.add(new Pin(202, false, "OUTPUT"));
        allGpios.add(new Pin(177, false, "OUTPUT"));
        allGpios.add(new Pin(176, false, "OUTPUT"));
        allGpios.add(new Pin(175, false, "OUTPUT"));
        allGpios.add(new Pin(174, false, "OUTPUT"));
        allGpios.add(new Pin(119, false, "OUTPUT"));
        allGpios.add(new Pin(124, false, "OUTPUT"));
        allGpios.add(new Pin(127, false, "OUTPUT"));
        allGpios.add(new Pin(116, false, "OUTPUT"));
        allGpios.add(new Pin(7, false, "OUTPUT"));
        allGpios.add(new Pin(6, false, "OUTPUT"));
        allGpios.add(new Pin(5, false, "OUTPUT"));
        allGpios.add(new Pin(4, false, "OUTPUT"));
        //internal bank
        allGpios.add(new Pin(178, false, "OUTPUT"));
        allGpios.add(new Pin(179, false, "OUTPUT"));
        allGpios.add(new Pin(104, false, "OUTPUT"));
        allGpios.add(new Pin(143, false, "OUTPUT"));
        allGpios.add(new Pin(142, false, "OUTPUT"));
        allGpios.add(new Pin(141, false, "OUTPUT"));
        allGpios.add(new Pin(140, false, "OUTPUT"));
        allGpios.add(new Pin(149, false, "OUTPUT"));
        allGpios.add(new Pin(105, false, "OUTPUT"));
        allGpios.add(new Pin(148, false, "OUTPUT"));
        allGpios.add(new Pin(146, false, "OUTPUT"));
        allGpios.add(new Pin(147, false, "OUTPUT"));
        allGpios.add(new Pin(100, false, "OUTPUT"));
        allGpios.add(new Pin(102, false, "OUTPUT"));
    }

    private Socket socket;
    private PrintWriter outPrintWriter;
    private MainViewController mainViewController;
    private GyroscopeListener gyroscopeListener;
    private MagnetometerListener magnetometerListener;
    private AccelerometerListener accelerometerListener;

    @Override
    public void start(Stage primaryStage) throws Exception{
        userPreferences = Preferences.userNodeForPackage(MainApp.class);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/main.fxml"));
        Parent root = loader.load();
        mainViewController = loader.getController();
        mainViewController.setMainApp(this);
        primaryStage.setTitle("UDOO Neo Controller");
        primaryStage.getIcons().add(new Image("/images/udoo_icon_128.png"));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(root, 960, 640));
        primaryStage.show();

        setRequestHistory();

        mainViewController.updatePinStatus();
    }

    @Override
    public void stop(){
        storeRequestHistory();
        closeSocket();
    }

    public void setGyroscopeListener(GyroscopeListener listener){
        this.gyroscopeListener = listener;
    }

    public void setMagnetometerListener(MagnetometerListener listener){
        this.magnetometerListener = listener;
    }

    public void setAccelerometerListener(AccelerometerListener listener){
        this.accelerometerListener = listener;
    }

    private void setRequestHistory(){
        String historyString = userPreferences.get(PREF_REQUEST_HISTORY, "[]");
        System.out.println(historyString);
        Gson gson = new Gson();
        List<String> history = new ArrayList<>();
        String[] historyArray = gson.fromJson(historyString, String[].class);
        Collections.addAll(history, historyArray);
        mainViewController.setRequestHistory(history);
    }

    private void storeRequestHistory(){
        List<String> history = mainViewController.getRequestHistory();
        Gson gson = new Gson();
        userPreferences.put(PREF_REQUEST_HISTORY, gson.toJson(history.toArray()));
    }

    private Thread inputStreamReaderThread;

    public boolean connectedToBoard(){
        return socket != null && socket.isConnected();
    }

    public void openSocket(){
        String serverUri = userPreferences.get(PREF_SERVER_URI, DEFAULT_HOST_ADDRESS);
        int serverPort = userPreferences.getInt(PREF_SERVER_PORT, DEFAULT_HOST_PORT);
        if(serverUri.equals("")){
            serverUri = DEFAULT_HOST_ADDRESS;
        }
        if(serverPort == 0){
            serverPort = DEFAULT_HOST_PORT;
        }

        try {
            if(socket == null || socket.isConnected()){
                this.socket = new Socket(serverUri, serverPort);
            }

            mainViewController.appendTextToConsole("Opening connection with UDOO Neo\n");
            mainViewController.appendTextToConsole("host: " + serverUri + ":" + serverPort + "\n");
            inputStreamReaderThread = new Thread(){
                @Override
                public void run(){
                    try {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String inputLine;
                        while (!isInterrupted() && (inputLine = in.readLine()) != null){
                            processInput(inputLine);
                        }
                    }
                    catch (SocketException e) {
                        mainViewController.updateConnectionMenuItem(false);
                        System.err.println("Socket closed");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            inputStreamReaderThread.start();
            mainViewController.updateConnectionMenuItem(true);
            Thread.sleep(2);
            outPrintWriter = new PrintWriter(socket.getOutputStream(), true);
            sendRequest("{\"method\":\"" + NeoJavaProtocol.REQ_GPIOS_EXPORT + "\",\"content\":\"\"}");
        }
        catch (ConnectException e){
            mainViewController.updateConnectionMenuItem(false);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Error");
            alert.setContentText("Connection failed, retry ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                openSocket();
            }
        }
        catch (Exception e) {
            mainViewController.updateConnectionMenuItem(false);
            System.err.println(e.getMessage());
        }
    }

    public void closeSocket(){
        if(socket != null && socket.isConnected()){
            try {
                inputStreamReaderThread.interrupt();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mainViewController.updateConnectionMenuItem(false);
        exportedGpios.clear();
        mainViewController.clearConsole();
        mainViewController.updatePinStatus();
    }

    public void sendRequest(String request){
        if(socket != null && outPrintWriter != null){
            outPrintWriter.println(request);
        }
    }

    private void processInput(String inputLine){
        try {
            Gson gson = new Gson();
            ResponseMessage message = gson.fromJson(inputLine, ResponseMessage.class);
            Pin pin = null;
            boolean logToConsole = true;
            switch (message.method){
                case NeoJavaProtocol.RESP_HELP:
                    break;
                case NeoJavaProtocol.RESP_VERSION:
                    break;
                case NeoJavaProtocol.RESP_LCD_CLEAR:
                    break;
                case NeoJavaProtocol.RESP_QUIT:
                    break;
                case NeoJavaProtocol.RESP_LCD_PRINT:
                    break;
                case NeoJavaProtocol.RESP_TEMPERATURE:
                    break;
                case NeoJavaProtocol.REQ_SERVER_ACTION:
                    if(message.info.equals(NeoJavaProtocol.ServerMessages.SHUTDOWN)){
                        closeSocket();
                    }
                    break;
                case NeoJavaProtocol.RESP_ACCELEROMETER:
                    if(accelerometerListener != null){
                        logToConsole = false;
                        if(!message.info.equals("Reading accelerometer data")) {
                            ResponseSensorData responseSensorData = gson.fromJson(inputLine, ResponseSensorData.class);
                            SensorData sensorData = responseSensorData.content;
                            if (sensorData != null) {
                                accelerometerListener.onAccelerometerDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.RESP_MAGNETOMETER:
                    if(magnetometerListener != null){
                        logToConsole = false;
                        if(!message.info.equals("Reading magnetometer data")) {
                            ResponseSensorData responseSensorData = gson.fromJson(inputLine, ResponseSensorData.class);
                            SensorData sensorData = responseSensorData.content;
                            if (sensorData != null) {
                                magnetometerListener.onMagnetometerDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.RESP_GYROSCOPE:
                    if(gyroscopeListener != null){
                        logToConsole = false;
                        if(!message.info.equals("Reading gyroscope data")) {
                            ResponseSensorData responseSensorData = gson.fromJson(inputLine, ResponseSensorData.class);
                            SensorData sensorData = responseSensorData.content;
                            if (sensorData != null) {
                                gyroscopeListener.onGyroscopeDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.RESP_GPIOS_EXPORT:
                    Platform.runLater(() -> exportedGpios.clear());
                    System.out.printf("length: %d\n", exportedGpios.size());
                    ResponseExportGpios responseExportGpios = gson.fromJson(inputLine, ResponseExportGpios.class);
                    List<PinObject> pins = responseExportGpios.content;
                    for(PinObject po: pins) {
                        if(po != null && po.state != null && po.mode != null) {
                            Pin p = new Pin(po.id, po.state.equals("HIGH"), po.mode);
                            Platform.runLater(() -> exportedGpios.add(p));
                        }
                    }
                    Platform.runLater(() -> Collections.sort(exportedGpios));
                    System.out.printf("length: %d\n", exportedGpios.size());
                    mainViewController.updatePinStatus();
                    break;
                case NeoJavaProtocol.RESP_SET_PIN_STATE:
                case OUTPUT_STATE_CHANGED:
                    ResponseSetPinState responseSetPinState = gson.fromJson(inputLine, ResponseSetPinState.class);
                    PinObject pinState = responseSetPinState.pin;
                    for(Pin p: exportedGpios) {
                        if (p.getId() == pinState.id) {
                            pin = p;
                            break;
                        }
                    }
                    if(pin != null) {
                        System.out.println("Pin: " + pin.toString());
                        pin.setState(pinState.state != null &&
                                pinState.state.equals("HIGH"));
                        Thread.sleep(30);
                        mainViewController.updatePinStatus(pinState.id);
                    }
                    break;
                case NeoJavaProtocol.RESP_SET_PIN_MODE:
                case OUTPUT_MODE_CHANGED:
                    ResponseSetPinMode responseSetPinMode = gson.fromJson(inputLine, ResponseSetPinMode.class);
                    PinObject pinMode = responseSetPinMode.pin;
                    for(Pin p: exportedGpios) {
                        if (p.getId() == pinMode.id) {
                            pin = p;
                            break;
                        }
                    }
                    if(pin == null){
                        final Pin newPin = new Pin(pinMode.id, false, pinMode.mode);
                        Platform.runLater(() -> exportedGpios.add(newPin));
                        Platform.runLater(() -> Collections.sort(exportedGpios));
                        pin = newPin;
                    }
                    //System.out.println("Pin: " + pin.toString());
                    pin.setMode(pinMode.mode);
                    Thread.sleep(30);
                    mainViewController.updatePinStatus(pinMode.id);
                    break;
                case ERROR:
                    break;
            }
            if (logToConsole) {
                System.out.println("Method: " + message.method);
                System.out.println("Output: " + message.info);
                System.out.println("RAW: " + inputLine);
                mainViewController.appendTextToConsole(message.method + ": " + message.info + "\n");
            }
        }
        catch (Exception e){
            System.err.println("error: invalid message \"" + inputLine + "\"");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<Pin> getExportedGpios(){
        return exportedGpios;
    }
}
