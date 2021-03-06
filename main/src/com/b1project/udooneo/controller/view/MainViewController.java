package com.b1project.udooneo.controller.view;
import com.b1project.udooneo.controller.MainApp;
import com.b1project.udooneo.controller.json.NeoJavaProtocol;
import com.b1project.udooneo.controller.model.Pin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private Menu menuDevice;
    @FXML
    private MenuItem menuItemConnect;
    @FXML
    private MenuItem menuItemPreferences;
    @FXML
    private MenuItem menuItem3DSensors;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private MenuItem menuItemStopService;
    @FXML
    private MenuItem menuItemReboot;
    @FXML
    private TableView<Pin> pinTable;
    @FXML
    private TableColumn<Pin, String> idColumn;
    @FXML
    private TableColumn<Pin, Boolean> stateColumn;
    @FXML
    private TableColumn<Pin, String> modeColumn;
    @FXML
    private TextArea consoleTextArea;
    @FXML
    private ImageView gpio_21;
    @FXML
    private ImageView gpio_20;
    @FXML
    private ImageView gpio_19;
    @FXML
    private ImageView gpio_18;
    @FXML
    private ImageView gpio_17;
    @FXML
    private ImageView gpio_16;
    @FXML
    private ImageView gpio_15;
    @FXML
    private ImageView gpio_14;
    @FXML
    private ImageView gpio_22;
    @FXML
    private ImageView gpio_25;
    @FXML
    private ImageView gpio_24;
    @FXML
    private ImageView gpio_182;
    @FXML
    private ImageView gpio_173;
    @FXML
    private ImageView gpio_172;
    @FXML
    private ImageView gpio_181;
    @FXML
    private ImageView gpio_180;
    @FXML
    private ImageView gpio_107;
    @FXML
    private ImageView gpio_106;
    @FXML
    private ImageView gpio_174;
    @FXML
    private ImageView gpio_175;
    @FXML
    private ImageView gpio_176;
    @FXML
    private ImageView gpio_177;
    @FXML
    private ImageView gpio_202;
    @FXML
    private ImageView gpio_203;
    @FXML
    private ImageView gpio_4;
    @FXML
    private ImageView gpio_5;
    @FXML
    private ImageView gpio_6;
    @FXML
    private ImageView gpio_7;
    @FXML
    private ImageView gpio_116;
    @FXML
    private ImageView gpio_127;
    @FXML
    private ImageView gpio_124;
    @FXML
    private ImageView gpio_119;
    @FXML
    private ComboBox<String> inputComboBox;
    @FXML
    private ContextMenu exportMenu;
    @FXML
    private Button releasePinButton;
    @FXML
    private Button exportButton;

    private MainApp mainApp;

    public MainViewController(){

    }

    private HashMap<Integer, ImageView> pinImageViewMap = new HashMap<>();
    private final static ObservableList<String> requestHistory = FXCollections.observableArrayList();


    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle){
        pinTable.setEditable(true);
        pinTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, oldValue, newValue) -> releasePinButton.setDisable(newValue == null));
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asString());
        modeColumn.setCellValueFactory(cellData -> cellData.getValue().modeProperty());
        modeColumn.setEditable(true);
        modeColumn.setCellFactory(column -> new TableCell<Pin, String>(){
            private ComboBox<String> comboBox;
            private final ObservableList<String> options = FXCollections.observableArrayList("OUTPUT","INPUT");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if(!empty) {
                    if (isEditing()) {
                        if (comboBox != null) {
                            comboBox.getEditor().setText(item);
                        }
                        setGraphic(comboBox);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        setText(item);
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                }
                else {
                    setGraphic(null);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if(comboBox == null){
                    createComboBox();
                }
                comboBox.getSelectionModel().select(getItem());
                setGraphic(comboBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }

            private void createComboBox(){
                comboBox = new ComboBox<>();
                comboBox.setItems(options);
                comboBox.setEditable(false);
                comboBox.setOnAction(this::handleChangeModeAction);
            }

            @FXML
            private void handleChangeModeAction(ActionEvent event) {
                System.out.println(event);
                commitEdit(comboBox.getSelectionModel().getSelectedItem());
                Pin pin = getTableRow().getItem();
                String request = String.format(
                        "{\"method\":\"%s\",\"pinId\":%s,\"mode\":\"%s\"}",
                        NeoJavaProtocol.REQ_GPIO_SET_MODE,
                        pin.getId(),
                        getItem()
                );
                mainApp.sendRequest(request);
            }
        });

        stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        stateColumn.setCellFactory(column -> new TableCell<Pin, Boolean>(){
            private ComboBox<String> comboBox;
            private final ObservableList<String> options = FXCollections.observableArrayList("LOW","HIGH");
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setTextAlignment(TextAlignment.CENTER);
                setAlignment(Pos.CENTER);
                if(!empty) {
                    String text = "LOW";
                    if(item){
                        text = "HIGH";
                    }
                    Pin pin = getTableRow().getItem();
                    if (pin != null && pin.getMode().equals("OUTPUT") && isEditing()) {
                        if (comboBox != null) {
                            comboBox.getEditor().setText(text);
                        }
                        setGraphic(comboBox);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        setText(text);
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                }
                else {
                    setGraphic(null);
                }
            }
            @Override
            public void startEdit() {
                super.startEdit();
                Pin pin = getTableRow().getItem();
                if (pin.getMode().equals("OUTPUT")) {
                    if (comboBox == null) {
                        createComboBox();
                    }
                    comboBox.getSelectionModel().select(getItem() ? "HIGH" : "LOW");
                    setGraphic(comboBox);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem()?"HIGH":"LOW");
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }

            private void createComboBox(){
                comboBox = new ComboBox<>();
                comboBox.setItems(options);
                comboBox.setEditable(false);
                comboBox.setOnAction(this::handleChangeStateAction);
                comboBox.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
                    if(comboBox != null && !newValue){
                        commitEdit(getItem());
                    }
                });
            }

            @FXML
            private void handleChangeStateAction(ActionEvent event) {
                System.out.println(event);
                commitEdit(comboBox.getSelectionModel().getSelectedItem().equals("HIGH"));
                Pin pin = getTableRow().getItem();
                String request = String.format(
                        "{\"method\":\"%s\",\"pinId\":%s,\"state\":\"%s\"}",
                        NeoJavaProtocol.REQ_GPIO_SET_STATE,
                        pin.getId(),
                        getItem()?"HIGH":"LOW"
                );
                mainApp.sendRequest(request);
            }
        });

        consoleTextArea.setEditable(false);
        MenuItem clearConsoleMenuItem = new MenuItem();
        clearConsoleMenuItem.setText("Clear console");
        clearConsoleMenuItem.setOnAction(this::handleClearConsoleAction);

        MenuItem copyMenuItem = new MenuItem();
        copyMenuItem.setText("Copy");
        copyMenuItem.setDisable(true);
        copyMenuItem.setOnAction(this::handleCopyConsoleTextAction);

        /*MenuItem cutMenuItem = new MenuItem();
        cutMenuItem.setText("Cut");
        cutMenuItem.setDisable(true);
        cutMenuItem.setOnAction(this::handleCutConsoleTextAction);*/

        MenuItem selectAllMenuItem = new MenuItem();
        selectAllMenuItem.setText("Select all");
        selectAllMenuItem.setOnAction(this::handleSelectAllConsoleTextAction);

        final ContextMenu contextMenu = new ContextMenu();
        consoleTextArea.setContextMenu(contextMenu);
        consoleTextArea.getContextMenu().getItems().add(clearConsoleMenuItem);
        consoleTextArea.getContextMenu().getItems().add(copyMenuItem);
        //consoleTextArea.getContextMenu().getItems().add(cutMenuItem);
        consoleTextArea.getContextMenu().getItems().add(selectAllMenuItem);

        consoleTextArea.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()){
                copyMenuItem.setDisable(true);
                //cutMenuItem.setDisable(true);
            }
            else{
                copyMenuItem.setDisable(false);
                //cutMenuItem.setDisable(false);
            }
        });

        pinImageViewMap.put(21, gpio_21);
        pinImageViewMap.put(20, gpio_20);
        pinImageViewMap.put(19, gpio_19);
        pinImageViewMap.put(18, gpio_18);
        pinImageViewMap.put(17, gpio_17);
        pinImageViewMap.put(16, gpio_16);
        pinImageViewMap.put(15, gpio_15);
        pinImageViewMap.put(14, gpio_14);
        pinImageViewMap.put(22, gpio_22);
        pinImageViewMap.put(25, gpio_25);
        pinImageViewMap.put(24, gpio_24);
        pinImageViewMap.put(182, gpio_182);
        pinImageViewMap.put(173, gpio_173);
        pinImageViewMap.put(172, gpio_172);
        pinImageViewMap.put(181, gpio_181);
        pinImageViewMap.put(180, gpio_180);
        pinImageViewMap.put(107, gpio_107);
        pinImageViewMap.put(106, gpio_106);
        pinImageViewMap.put(174, gpio_174);
        pinImageViewMap.put(175, gpio_175);
        pinImageViewMap.put(176, gpio_176);
        pinImageViewMap.put(177, gpio_177);
        pinImageViewMap.put(202, gpio_202);
        pinImageViewMap.put(203, gpio_203);
        pinImageViewMap.put(4, gpio_4);
        pinImageViewMap.put(5, gpio_5);
        pinImageViewMap.put(6, gpio_6);
        pinImageViewMap.put(7, gpio_7);
        pinImageViewMap.put(116, gpio_116);
        pinImageViewMap.put(127, gpio_127);
        pinImageViewMap.put(124, gpio_124);
        pinImageViewMap.put(119, gpio_119);

        inputComboBox.setItems(requestHistory);
        inputComboBox.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER && !inputComboBox.getEditor().getText().isEmpty()){
                mainApp.sendRequest(inputComboBox.getEditor().getText());
                if(!requestHistory.contains(inputComboBox.getEditor().getText())) {
                    requestHistory.add(inputComboBox.getEditor().getText());
                }
                inputComboBox.getEditor().clear();
            }
        });

        menuItemConnect.setOnAction(this::handleConnectAction);
        menuItemPreferences.setOnAction(this::handlePreferencesAction);
        menuItem3DSensors.setOnAction(this::handle3DSensorsAction);
        menuItemStopService.setOnAction(this::handleStopServiceAction);
        menuItemReboot.setOnAction(this::handleRebootDeviceAction);
        menuItemClose.setOnAction(this::handleExitAction);
        menuItemAbout.setOnAction(this::handleAboutAction);

        exportButton.setDisable(true);
        exportButton.setOnMouseClicked(this::handleExportButtonAction);

        releasePinButton.setOnAction(this::handleReleasePinAction);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleStopServiceAction(ActionEvent event) {
        String request = String.format(
                "{\"method\":\"%s\"}",
                NeoJavaProtocol.REQ_QUIT
        );
        System.out.println(request);
        mainApp.sendRequest(request);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleRebootDeviceAction(ActionEvent event) {
        String request = String.format(
                "{\"method\":\"%s\"}",
                NeoJavaProtocol.REQ_BOARD_REBOOT
        );
        System.out.println(request);
        mainApp.sendRequest(request);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleExitAction(ActionEvent event) {
        Platform.exit();
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleClearConsoleAction(ActionEvent event){
        clearConsole();
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleCopyConsoleTextAction(ActionEvent event){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(consoleTextArea.selectedTextProperty().get());
        clipboard.setContent(content);
    }

    /*@FXML
    private void handleCutConsoleTextAction(ActionEvent event){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(consoleTextArea.selectedTextProperty().get());
        clipboard.setContent(content);

        IndexRange range = consoleTextArea.getSelection();
        String origText = consoleTextArea.getText();
        String firstPart = origText.substring(0, range.getStart());
        String lastPart = origText.substring(range.getEnd(), origText.length());
        consoleTextArea.setText( firstPart + lastPart );

        consoleTextArea.positionCaret( range.getStart() );
    }*/

    @SuppressWarnings("unused")
    @FXML
    private void handleSelectAllConsoleTextAction(ActionEvent event){
        consoleTextArea.selectAll();
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleConnectAction(ActionEvent event) {
        mainApp.openSocket();
        exportButton.setDisable(false);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleDisconnectAction(ActionEvent event) {
        exportButton.setDisable(true);
        mainApp.closeSocket();
    }

    @SuppressWarnings("unused")
    @FXML
    private void handle3DSensorsAction(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/3dsensors.fxml"));
        try {
            Parent root = loader.load();
            Sensors3DViewController sensors3DViewController = loader.getController();
            sensors3DViewController.setMainApp(mainApp);
            stage.setScene(new Scene(root));
            stage.setTitle("3D Sensors");
            stage.initModality(Modality.NONE);
            stage.initOwner(pinTable.getScene().getWindow());
            sensors3DViewController.setStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void handlePreferencesAction(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/preferences.fxml"));
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Preferences");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(pinTable.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleAboutAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("UDOO Neo Controller - About");
        alert.setHeaderText("© 2015, Cyril Bosselut - The B1 Project");
        alert.setGraphic(new ImageView(MainApp.class.getResource("/images/udoo_icon_64.png").toString()));
        alert.setContentText("License: GNU General Public License Version 2");
        alert.show();
    }

    @FXML
    private void handleExportButtonAction(MouseEvent event){
        if(!exportMenu.isShowing() && mainApp.connectedToBoard()){
            exportMenu.getItems().clear();
            for(Pin p: MainApp.allGpios){
                boolean found = false;
                for(Pin pe :mainApp.getExportedGpios()){
                    if(p.getId() == pe.getId()){
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    MenuItem item = new MenuItem("GPIO " + p.getId(), null);
                    item.setUserData(p);
                    item.setOnAction(actionEvent -> {
                        Pin pin = (Pin) ((MenuItem)actionEvent.getSource()).getUserData();
                        String request = String.format(
                                "{\"method\":\"%s\",\"pinId\":%s,\"mode\":\"OUTPUT\"}",
                                NeoJavaProtocol.REQ_GPIO_SET_MODE,
                                pin.getId()
                        );
                        mainApp.sendRequest(request);
                    });
                    exportMenu.getItems().add(item);
                }
            }
            exportMenu.show(exportButton, event.getScreenX(), event.getScreenY());
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleReleasePinAction(ActionEvent event) {
        Pin pin = pinTable.getSelectionModel().getSelectedItem();
        int pinId = pin.getId();
        String request = String.format(
                "{\"method\":\"%s\",\"pinId\":%s}",
                NeoJavaProtocol.REQ_GPIO_RELEASE,
                pinId
        );
        mainApp.sendRequest(request);
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        pinTable.setItems(mainApp.getExportedGpios());
    }

    public void setRequestHistory(List<String> history){
        requestHistory.clear();
        requestHistory.addAll(history);
    }

    public ObservableList<String> getRequestHistory(){
        return requestHistory;
    }

    public void clearConsole(){
        consoleTextArea.clear();
    }

    public void appendTextToConsole(String text){
        consoleTextArea.appendText(text);
    }

    public void updatePinStatus(){
        try {
            BufferedImage pinBufferedImageHigh = ImageIO.read(MainApp.class.getResource("/images/pin_state_high.png"));
            WritableImage pinImageHigh = null;
            if (pinBufferedImageHigh != null) {
                pinImageHigh = new WritableImage(pinBufferedImageHigh.getWidth(), pinBufferedImageHigh.getHeight());
                PixelWriter pwHigh = pinImageHigh.getPixelWriter();
                for (int x = 0; x < pinBufferedImageHigh.getWidth(); x++) {
                    for (int y = 0; y < pinBufferedImageHigh.getHeight(); y++) {
                        pwHigh.setArgb(x, y, pinBufferedImageHigh.getRGB(x, y));
                    }
                }
            }
            BufferedImage pinBufferedImageLow = ImageIO.read(MainApp.class.getResource("/images/pin_state_low.png"));
            WritableImage pinImageLow = null;
            if (pinBufferedImageLow != null) {
                pinImageLow = new WritableImage(pinBufferedImageLow.getWidth(), pinBufferedImageLow.getHeight());
                PixelWriter pwLow = pinImageLow.getPixelWriter();
                for (int x = 0; x < pinBufferedImageLow.getWidth(); x++) {
                    for (int y = 0; y < pinBufferedImageLow.getHeight(); y++) {
                        pwLow.setArgb(x, y, pinBufferedImageLow.getRGB(x, y));
                    }
                }
            }
            BufferedImage pinBufferedImageOff = ImageIO.read(MainApp.class.getResource("/images/pin_state_off.png"));
            WritableImage pinImageOff = null;
            if (pinBufferedImageOff != null) {
                pinImageOff = new WritableImage(pinBufferedImageOff.getWidth(), pinBufferedImageOff.getHeight());
                PixelWriter pwOff = pinImageOff.getPixelWriter();
                for (int x = 0; x < pinBufferedImageOff.getWidth(); x++) {
                    for (int y = 0; y < pinBufferedImageOff.getHeight(); y++) {
                        pwOff.setArgb(x, y, pinBufferedImageOff.getRGB(x, y));
                    }
                }
            }
            for(Map.Entry<Integer, ImageView> entry: pinImageViewMap.entrySet()){
                ImageView imageView = entry.getValue();
                if(imageView != null) {
                    imageView.setImage(pinImageOff);
                    imageView.setEffect(null);
                }
            }
            for(Pin pin: mainApp.getExportedGpios()){
                ImageView pinImageView = pinImageViewMap.get(pin.getId());
                if(pinImageView != null) {
                    if (pin.getState()) {
                        pinImageView.setImage(pinImageHigh);
                    } else {
                        pinImageView.setImage(pinImageLow);
                    }
                    pinImageView.setEffect(new Glow(0.7));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateConnectionMenuItem(boolean connected){
        System.out.println("updateConnectionMenuItem: " + connected);
        menuDevice.setDisable(!connected);
        if(connected){
            menuItemConnect.setText("Disconnect");
            menuItemConnect.setOnAction(this::handleDisconnectAction);
        }
        else {
            menuItemConnect.setText("Connect");
            menuItemConnect.setOnAction(this::handleConnectAction);
        }
    }

    public void updatePinStatus(Integer pinId){
        try{
            Pin pin = null;
            for(Pin p: mainApp.getExportedGpios()) {
                if (p.getId() == pinId) {
                    pin = p;
                    break;
                }
            }
            ImageView pinImageView = pinImageViewMap.get(pinId);
            if(pin != null && pinImageView != null){
                System.out.println("Pin #" + pin.getId() + ": " + pin.getState());
                if (pin.getState()) {
                    BufferedImage pinBufferedImageHigh = ImageIO.read(MainApp.class.getResource("/images/pin_state_high.png"));
                    WritableImage pinImageHigh = null;
                    if (pinBufferedImageHigh != null) {
                        pinImageHigh = new WritableImage(pinBufferedImageHigh.getWidth(), pinBufferedImageHigh.getHeight());
                        PixelWriter pwHigh = pinImageHigh.getPixelWriter();
                        for (int x = 0; x < pinBufferedImageHigh.getWidth(); x++) {
                            for (int y = 0; y < pinBufferedImageHigh.getHeight(); y++) {
                                pwHigh.setArgb(x, y, pinBufferedImageHigh.getRGB(x, y));
                            }
                        }
                    }
                    pinImageView.setImage(pinImageHigh);
                } else {
                    BufferedImage pinBufferedImageLow = ImageIO.read(MainApp.class.getResource("/images/pin_state_low.png"));
                    WritableImage pinImageLow = null;
                    if (pinBufferedImageLow != null) {
                        pinImageLow = new WritableImage(pinBufferedImageLow.getWidth(), pinBufferedImageLow.getHeight());
                        PixelWriter pwLow = pinImageLow.getPixelWriter();
                        for (int x = 0; x < pinBufferedImageLow.getWidth(); x++) {
                            for (int y = 0; y < pinBufferedImageLow.getHeight(); y++) {
                                pwLow.setArgb(x, y, pinBufferedImageLow.getRGB(x, y));
                            }
                        }
                    }
                    pinImageView.setImage(pinImageLow);
                }
                pinImageView.setEffect(new Glow(0.7));
            }
            else if(pinImageView != null){
                BufferedImage pinBufferedImageOff = ImageIO.read(MainApp.class.getResource("/images/pin_state_off.png"));
                WritableImage pinImageOff = null;
                if (pinBufferedImageOff != null) {
                    pinImageOff = new WritableImage(pinBufferedImageOff.getWidth(), pinBufferedImageOff.getHeight());
                    PixelWriter pwOff = pinImageOff.getPixelWriter();
                    for (int x = 0; x < pinBufferedImageOff.getWidth(); x++) {
                        for (int y = 0; y < pinBufferedImageOff.getHeight(); y++) {
                            pwOff.setArgb(x, y, pinBufferedImageOff.getRGB(x, y));
                        }
                    }
                    pinImageView.setImage(pinImageOff);
                }
                pinImageView.setImage(pinImageOff);
                pinImageView.setEffect(null);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error: updatePinStatus pin #" + pinId);
            e.printStackTrace();
        }
    }
}

