package com.b1project.udooneo.controller;

import com.b1project.udooneo.controller.json.*;
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
        allGpios.add(new Pin(106, false, "out"));
        allGpios.add(new Pin(107, false, "out"));
        allGpios.add(new Pin(180, false, "out"));
        allGpios.add(new Pin(181, false, "out"));
        allGpios.add(new Pin(172, false, "out"));
        allGpios.add(new Pin(173, false, "out"));
        allGpios.add(new Pin(182, false, "out"));
        allGpios.add(new Pin(24, false, "out"));
        allGpios.add(new Pin(25, false, "out"));
        allGpios.add(new Pin(22, false, "out"));
        allGpios.add(new Pin(14, false, "out"));
        allGpios.add(new Pin(15, false, "out"));
        allGpios.add(new Pin(16, false, "out"));
        allGpios.add(new Pin(17, false, "out"));
        allGpios.add(new Pin(18, false, "out"));
        allGpios.add(new Pin(19, false, "out"));
        allGpios.add(new Pin(20, false, "out"));
        allGpios.add(new Pin(21, false, "out"));
        allGpios.add(new Pin(203, false, "out"));
        allGpios.add(new Pin(202, false, "out"));
        allGpios.add(new Pin(177, false, "out"));
        allGpios.add(new Pin(176, false, "out"));
        allGpios.add(new Pin(175, false, "out"));
        allGpios.add(new Pin(174, false, "out"));
        allGpios.add(new Pin(119, false, "out"));
        allGpios.add(new Pin(124, false, "out"));
        allGpios.add(new Pin(127, false, "out"));
        allGpios.add(new Pin(116, false, "out"));
        allGpios.add(new Pin(7, false, "out"));
        allGpios.add(new Pin(6, false, "out"));
        allGpios.add(new Pin(5, false, "out"));
        allGpios.add(new Pin(4, false, "out"));
        //internal bank
        allGpios.add(new Pin(178, false, "out"));
        allGpios.add(new Pin(179, false, "out"));
        allGpios.add(new Pin(104, false, "out"));
        allGpios.add(new Pin(143, false, "out"));
        allGpios.add(new Pin(142, false, "out"));
        allGpios.add(new Pin(141, false, "out"));
        allGpios.add(new Pin(140, false, "out"));
        allGpios.add(new Pin(149, false, "out"));
        allGpios.add(new Pin(105, false, "out"));
        allGpios.add(new Pin(148, false, "out"));
        allGpios.add(new Pin(146, false, "out"));
        allGpios.add(new Pin(147, false, "out"));
        allGpios.add(new Pin(100, false, "out"));
        allGpios.add(new Pin(102, false, "out"));
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
            sendRequest("{\"method\":\"" + NeoJavaProtocol.COMMAND_EXPORTED_GPIOS + "\",\"content\":\"\"}");
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
            Message message = gson.fromJson(inputLine, Message.class);
            Pin pin = null;
            boolean logToConsole = true;
            switch (message.method){
                case NeoJavaProtocol.COMMAND_HELP:
                    break;
                case NeoJavaProtocol.COMMAND_VERSION:
                    break;
                case NeoJavaProtocol.COMMAND_LCD_CLEAR:
                    break;
                case NeoJavaProtocol.COMMAND_QUIT:
                    break;
                case NeoJavaProtocol.COMMAND_LCD_PRINT:
                    break;
                case NeoJavaProtocol.COMMAND_TEMP_REQUEST:
                    break;
                case NeoJavaProtocol.COMMAND_ACCELEROMETER_REQUEST:
                    if(accelerometerListener != null){
                        logToConsole = false;
                        if(!message.output.equals("Reading accelerometer data")) {
                            SensorData sensorData = gson.fromJson(message.output, SensorData.class);
                            if (sensorData != null) {
                                accelerometerListener.onAccelerometerDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.COMMAND_MAGNETOMETER_REQUEST:
                    if(magnetometerListener != null){
                        logToConsole = false;
                        if(!message.output.equals("Reading magnetometer data")) {
                            SensorData sensorData = gson.fromJson(message.output, SensorData.class);
                            if (sensorData != null) {
                                magnetometerListener.onMagnetometerDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.COMMAND_GYROSCOPE_REQUEST:
                    if(gyroscopeListener != null){
                        logToConsole = false;
                        if(!message.output.equals("Reading gyroscope data")) {
                            SensorData sensorData = gson.fromJson(message.output, SensorData.class);
                            if (sensorData != null) {
                                gyroscopeListener.onGyroscopeDataReceived(sensorData.data);
                            }
                        }
                    }
                    break;
                case NeoJavaProtocol.COMMAND_EXPORTED_GPIOS:
                    Platform.runLater(() -> exportedGpios.clear());
                    System.out.printf("length: %d", exportedGpios.size());
                    PinObject[] pins = gson.fromJson(message.output, PinObject[].class);
                    for(PinObject po: pins) {
                        Pin p = new Pin(po.id, po.state == 1, po.mode);
                        Platform.runLater(() -> exportedGpios.add(p));
                    }
                    System.out.printf("length: %d", exportedGpios.size());
                    mainViewController.updatePinStatus();
                    break;
                case OUTPUT_STATE_CHANGED:
                    PinState pinState = gson.fromJson(message.output, PinState.class);
                    for(Pin p: exportedGpios) {
                        if (p.getId() == pinState.pin) {
                            pin = p;
                            break;
                        }
                    }
                    if(pin != null) {
                        //System.out.println("Pin: " + pin.toString());
                        pin.setState(pinState.state == 1);
                        Thread.sleep(30);
                        mainViewController.updatePinStatus(pinState.pin);
                    }
                    break;
                case OUTPUT_MODE_CHANGED:
                    PinMode pinMode = gson.fromJson(message.output, PinMode.class);
                    for(Pin p: exportedGpios) {
                        if (p.getId() == pinMode.pin) {
                            pin = p;
                            break;
                        }
                    }
                    if(pin == null){
                        final Pin newPin = new Pin(pinMode.pin, false, pinMode.mode);
                        Platform.runLater(() -> exportedGpios.add(newPin));
                        pin = newPin;
                    }
                    //System.out.println("Pin: " + pin.toString());
                    pin.setMode(pinMode.mode);
                    Thread.sleep(30);
                    mainViewController.updatePinStatus(pinMode.pin);
                    break;
                case ERROR:
                    break;
            }
            if (logToConsole) {
                System.out.println("Method: " + message.method);
                System.out.println("Output: " + message.output);
                mainViewController.appendTextToConsole(message.method + ": " + message.output + "\n");
            }
        }
        catch (Exception e){
            System.err.println("error: invalid message \"" + inputLine + "\"");
            //e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<Pin> getExportedGpios(){
        return exportedGpios;
    }
}
