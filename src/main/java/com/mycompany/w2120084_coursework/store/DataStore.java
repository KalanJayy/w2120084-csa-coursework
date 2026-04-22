/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.store;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.model.Room;
import com.mycompany.w2120084_coursework.model.Sensor;
import com.mycompany.w2120084_coursework.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static final Map<String, Room> rooms = new HashMap<String, Room>();
    public static final Map<String, Sensor> sensors = new HashMap<String, Sensor>();
    public static final Map<String, List<SensorReading>> readings = new HashMap<String, List<SensorReading>>();

    private DataStore() {
    }
}
