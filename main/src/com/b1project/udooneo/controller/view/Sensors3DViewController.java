package com.b1project.udooneo.controller.view;

import com.b1project.udooneo.controller.MainApp;
import com.b1project.udooneo.controller.listener.AccelerometerListener;
import com.b1project.udooneo.controller.listener.GyroscopeListener;
import com.b1project.udooneo.controller.listener.MagnetometerListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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
public class Sensors3DViewController implements Initializable, GyroscopeListener, MagnetometerListener, AccelerometerListener {

    @FXML
    private Pane rootPane;

    @FXML
    private PerspectiveCamera camera;

    @FXML
    private Label accX;
    @FXML
    private Label accY;
    @FXML
    private Label accZ;

    @FXML
    private Label magnX;
    @FXML
    private Label magnY;
    @FXML
    private Label magnZ;

    @FXML
    private Label gyroX;
    @FXML
    private Label gyroY;
    @FXML
    private Label gyroZ;

    @FXML
    private Label rotX;
    @FXML
    private Label rotY;
    @FXML
    private Label rotZ;

    private MainApp mainApp;

    public Sensors3DViewController() {

    }

    private Box board;
    //private Sphere world;

    private float originX = 0.0f;
    private float originY = 0.0f;
    private float originZ = 0.0f;
    protected final String GYROSCOPE_REQUEST = "{\"method\":\"sensors/gyroscope\"}";
    protected final String MAGNETOMETER_REQUEST = "{\"method\":\"sensors/magnetometer\"}";
    protected final String ACCELEROMETER_REQUEST = "{\"method\":\"sensors/accelerometer\"}";
    private Thread gyroscopeThread;
    private Thread accelerometerThread;
    private Thread magnetometerThread;
    private Thread rotationThread;
    private static boolean gyroscopeThreadShoudlStop = false;
    private static boolean magnetometerThreadShoudlStop = false;
    private static boolean accelerometerThreadShoudlStop = false;
    private static boolean rotationThreadShoudlStop = false;

    private static float[] magnetometerVector = new float[3];
    private static float[] accelerometerVector = new float[3];
    private static float[] gyroscopeVector = new float[3];
    static {
        magnetometerVector[0] = 0.0f;
        magnetometerVector[1] = 0.0f;
        magnetometerVector[2] = 0.0f;

        accelerometerVector[0] = 0.0f;
        accelerometerVector[1] = 0.0f;
        accelerometerVector[2] = 0.0f;

        gyroscopeVector[0] = 0.0f;
        gyroscopeVector[1] = 0.0f;
        gyroscopeVector[2] = 0.0f;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gyroscopeThreadShoudlStop = false;
        magnetometerThreadShoudlStop = false;
        accelerometerThreadShoudlStop = false;
        rotationThreadShoudlStop = false;
        board = new Box(240.0, 8.0, 320.0);
        board.setId("board");
        board.setLayoutX(400);
        board.setLayoutY(300);
        board.setMaterial(new PhongMaterial(Color.GRAY));
        board.setDrawMode(DrawMode.FILL);
        /*world = new Sphere(120.0);
        world.setId("world");
        world.setLayoutX(400);
        world.setLayoutY(300);
        world.setMaterial(new PhongMaterial(Color.ORANGE));
        world.setDrawMode(DrawMode.FILL);*/
        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        originX = 0.0f;
        originY = 0.0f;
        originZ = 0.0f;

        rxBox.setAngle(originX);
        ryBox.setAngle(originY);
        rzBox.setAngle(originZ);
        board.getTransforms().addAll(rxBox, ryBox, rzBox);
        //world.getTransforms().addAll(rxBox, ryBox, rzBox);

        accX.setText("0.0");
        accY.setText("0.0");
        accZ.setText("0.0");
        magnX.setText("0.0");
        magnY.setText("0.0");
        magnZ.setText("0.0");
        gyroX.setText("0.0");
        gyroY.setText("0.0");
        gyroZ.setText("0.0");
        rotX.setText("0.0");
        rotY.setText("0.0");
        rotZ.setText("0.0");
        buildAxes();
        rootPane.getChildren().add(board);
        //rootPane.getChildren().add(world);
        gyroscopeThread = new Thread(){
            @Override
            public void run() {
                if(isInterrupted()){
                    return;
                }
                while (!isInterrupted() || !gyroscopeThreadShoudlStop){
                    if(mainApp != null) {
                        mainApp.sendRequest(GYROSCOPE_REQUEST);
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        gyroscopeThread.start();
        accelerometerThread = new Thread(){
            @Override
            public void run() {
                if(isInterrupted()){
                    return;
                }
                while (!isInterrupted() || !accelerometerThreadShoudlStop){
                    if(mainApp != null) {
                        mainApp.sendRequest(ACCELEROMETER_REQUEST);
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        accelerometerThread.start();
        magnetometerThread = new Thread(){
            @Override
            public void run() {
                if(isInterrupted()){
                    return;
                }
                while (!isInterrupted() || !magnetometerThreadShoudlStop){
                    if(mainApp != null) {
                        mainApp.sendRequest(MAGNETOMETER_REQUEST);
                    }
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        magnetometerThread.start();

        rotationThread = new Thread(){
            @Override
            public void run() {
                if(isInterrupted()){
                    return;
                }
                while (!isInterrupted() || rotationThreadShoudlStop){
                    try {
                        float[] rotationMatrix = new float[9];
                        float[] tiltMatrix = new float[9];
                        getRotationMatrix(rotationMatrix, tiltMatrix, accelerometerVector, magnetometerVector);

                        float[] rotation = new float[3];
                        getOrientation(rotationMatrix, rotation);
                        /*float rx = (float) Math.ceil(Math.toDegrees(rotation[0]));
                        float ry = (float) Math.ceil(Math.toDegrees(rotation[1]));
                        float rz = (float) Math.ceil(Math.toDegrees(rotation[2]));*/
                        float rx = (float) Math.toDegrees(rotation[0]);
                        float ry = (float) Math.toDegrees(rotation[1]);
                        float rz = (float) Math.toDegrees(rotation[2]);
                        /*float rx = rotation[0];
                        float ry = rotation[1];
                        float rz = rotation[2];*/

                        Platform.runLater(() -> rotX.setText(String.format("%f", rx)));
                        Platform.runLater(() -> rotY.setText(String.format("%f", ry)));
                        Platform.runLater(() -> rotZ.setText(String.format("%f", rz)));

                        rotate3Dto(board, rx, ry, rz);
                        rotate3D(axisGroup, rx, ry, rz);
                        //rotate3D(world, rx, ry, rz);
                        Thread.sleep(100L);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        };
        rotationThread.start();
    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        if(mainApp != null) {
            this.mainApp.setGyroscopeListener(this);
            this.mainApp.setMagnetometerListener(this);
            this.mainApp.setAccelerometerListener(this);
            this.mainApp.sendRequest(MAGNETOMETER_REQUEST);
        }
    }

    public void setStage(Stage stage){
        stage.setOnCloseRequest(windowEvent -> onClose());
    }

    protected void onClose() {
        System.out.println("finalize");
        rotationThreadShoudlStop = false;
        gyroscopeThreadShoudlStop = true;
        magnetometerThreadShoudlStop = true;
        accelerometerThreadShoudlStop = true;

        rotationThread.interrupt();
        rotationThread = null;
        gyroscopeThread.interrupt();
        gyroscopeThread = null;
        magnetometerThread.interrupt();
        magnetometerThread = null;
        accelerometerThread.interrupt();
        accelerometerThread = null;

        mainApp.setGyroscopeListener(null);
        mainApp.setMagnetometerListener(null);
        mainApp.setAccelerometerListener(null);
    }

    private boolean rotationInProgress = false;

    private void rotate3Dto(Shape3D shape, float tx, float ty, float tz){
        if (rotationInProgress){
            return;
        }
        //System.out.printf("rotate3Dto:%s %f | %f | %f\n", shape.getId(), tx, ty, tz);
        rotationInProgress = true;
        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        float rx = tx - originX;
        float ry = ty - originY;
        float rz = tz - originZ;

        rxBox.setAngle(rx);
        ryBox.setAngle(ry);
        rzBox.setAngle(rz);
        shape.getTransforms().addAll(rxBox, ryBox, rzBox);
        originX = tx;
        originY = ty;
        originZ = tz;
        rotationInProgress = false;
    }

    private void rotate3D(Group shape, float rx, float ry, float rz){
        if (rotationInProgress){
            return;
        }
        //System.out.printf("rotate3D:%s %f | %f | %f\n", shape.getId(), rx, ry, rz);
        rotationInProgress = true;
        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        rxBox.setAngle(rx);
        ryBox.setAngle(ry);
        rzBox.setAngle(rz);
        shape.getTransforms().addAll(rxBox, ryBox, rzBox);
        rotationInProgress = false;
    }

    private static final double AXIS_LENGTH = 128.0;
    final Xform axisGroup = new Xform();

    private void buildAxes() {
        axisGroup.setId("axis");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.BLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        axisGroup.setLayoutX(400);
        axisGroup.setLayoutY(300);
        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        Translate tzBox = new Translate(240.0, 0, 240.0);
        Double rx = 45.0;
        Double ry = -45.0;
        Double rz = 0.0;

        rxBox.setAngle(rx);
        ryBox.setAngle(ry);
        rzBox.setAngle(rz);
        axisGroup.getTransforms().addAll(tzBox, rxBox, ryBox, rzBox);
        rootPane.getChildren().addAll(axisGroup);
    }

    @Override
    public void onGyroscopeDataReceived(String data) {
        try {
            String[] values = data.split(",");
            gyroscopeVector[0] = (float) Double.parseDouble(values[0]);
            gyroscopeVector[1] = (float) Double.parseDouble(values[1]);
            gyroscopeVector[2] = (float) Double.parseDouble(values[2]);
            Platform.runLater(() -> gyroX.setText(values[0]));
            Platform.runLater(() -> gyroY.setText(values[1]));
            Platform.runLater(() -> gyroZ.setText(values[2]));
            //System.out.printf("Gyroscope: %f | %f | %f\n", gyroscopeVector[0], gyroscopeVector[1], gyroscopeVector[2]);
        }
        catch (Exception e){
            System.err.printf("Error: %s", e.getMessage());
        }
    }

    @Override
    public void onMagnetometerDataReceived(String data) {
        try {
            String[] values = data.split(",");
            magnetometerVector[0] = (float) Double.parseDouble(values[0]);
            magnetometerVector[1] = (float) Double.parseDouble(values[1]);
            magnetometerVector[2] = (float) Double.parseDouble(values[2]);
            Platform.runLater(() -> magnX.setText(values[0]));
            Platform.runLater(() -> magnY.setText(values[1]));
            Platform.runLater(() -> magnZ.setText(values[2]));
            //System.out.printf("Magnetometer: %f | %f | %f\n", magnetometerVector[0], magnetometerVector[1], magnetometerVector[2]);
        }
        catch (Exception e){
            System.err.printf("Error: %s", e.getMessage());
        }
    }

    @Override
    public void onAccelerometerDataReceived(String data) {
        try {
            String[] values = data.split(",");
            accelerometerVector[0] = (float) Double.parseDouble(values[0]);
            accelerometerVector[1] = (float) Double.parseDouble(values[1]);
            accelerometerVector[2] = (float) Double.parseDouble(values[2]);
            Platform.runLater(() -> accX.setText(values[0]));
            Platform.runLater(() -> accY.setText(values[1]));
            Platform.runLater(() -> accZ.setText(values[2]));
            //System.out.printf("Accelerometer: %f | %f | %f\n", accelerometerVector[0], accelerometerVector[1], accelerometerVector[2]);
        }
        catch (Exception e){
            System.err.printf("Error: %s", e.getMessage());
        }
    }
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];
        final float Ex = geomagnetic[0];
        final float Ey = geomagnetic[1];
        final float Ez = geomagnetic[2];

        // the the cross product of magnetic vector and gravity to derive a basis
        // vector pointing East.
        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;

        // Normalize & check the new basis vector
        final float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);
        if (normH < 0.1f) {
            // device is close to free fall (or in space?), or close to
            // magnetic north pole. Typical values are  > 100.
            return false;
        }
        final float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;

        // Now normalize the acceleration (assumed = gravity) vector
        final float invA = 1.0f / (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        Ax *= invA;
        Ay *= invA;
        Az *= invA;

        // A second cross product of the newly computed East and our measured gravity
        // vector gives a north vector in the horizontal plane.
        final float Mx = Ay * Hz - Az * Hy;
        final float My = Az * Hx - Ax * Hz;
        final float Mz = Ax * Hy - Ay * Hx;

        // Now create a rotation matrix based upon the newly computed basis vectors
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = Mx;
                R[4] = My;
                R[5] = Mz;
                R[6] = Ax;
                R[7] = Ay;
                R[8] = Az;
            } else if (R.length == 16) {
                R[0] = Hx;
                R[1] = Hy;
                R[2] = Hz;
                R[3] = 0;
                R[4] = Mx;
                R[5] = My;
                R[6] = Mz;
                R[7] = 0;
                R[8] = Ax;
                R[9] = Ay;
                R[10] = Az;
                R[11] = 0;
                R[12] = 0;
                R[13] = 0;
                R[14] = 0;
                R[15] = 1;
            }
        }
        if (I != null) {
            // compute the inclination matrix by projecting the geomagnetic
            // vector onto the Z (gravity) and X (horizontal component
            // of geomagnetic vector) axes.
            final float invE = 1.0f / (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            final float c = (Ex * Mx + Ey * My + Ez * Mz) * invE;
            final float s = (Ex * Ax + Ey * Ay + Ez * Az) * invE;
            if (I.length == 9) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[3] = 0;
                I[4] = c;
                I[5] = s;
                I[6] = 0;
                I[7] = -s;
                I[8] = c;
            } else if (I.length == 16) {
                I[0] = 1;
                I[1] = 0;
                I[2] = 0;
                I[4] = 0;
                I[5] = c;
                I[6] = s;
                I[8] = 0;
                I[9] = -s;
                I[10] = c;
                I[3] = I[7] = I[11] = I[12] = I[13] = I[14] = 0;
                I[15] = 1;
            }
        }
        return true;
    }

    public static float[] getOrientation(float[] R, float values[]) {
        /*
         * 4x4 (length=16) case:
         *   /  R[ 0]   R[ 1]   R[ 2]   0  \
         *   |  R[ 4]   R[ 5]   R[ 6]   0  |
         *   |  R[ 8]   R[ 9]   R[10]   0  |
         *   \      0       0       0   1  /
         *
         * 3x3 (length=9) case:
         *   /  R[ 0]   R[ 1]   R[ 2]  \
         *   |  R[ 3]   R[ 4]   R[ 5]  |
         *   \  R[ 6]   R[ 7]   R[ 8]  /
         *
         */
        if (R.length == 9) {
            values[0] = (float)Math.atan2(R[1], R[4]);
            values[1] = (float)Math.asin(-R[7]);
            values[2] = (float)Math.atan2(-R[6], R[8]);
        } else {
            values[0] = (float)Math.atan2(R[1], R[5]);
            values[1] = (float)Math.asin(-R[9]);
            values[2] = (float)Math.atan2(-R[8], R[10]);
        }
        return values;
    }

    public static float getInclination(float[] I) {
        if (I.length == 9) {
            return (float)Math.atan2(I[5], I[4]);
        } else {
            return (float)Math.atan2(I[6], I[5]);
        }
    }

}
