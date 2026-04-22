/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.resource;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.exception.SensorUnavailableException;
import com.mycompany.w2120084_coursework.model.Sensor;
import com.mycompany.w2120084_coursework.store.DataStore;
import com.mycompany.w2120084_coursework.model.ErrorMessage;
import com.mycompany.w2120084_coursework.model.SensorReading;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            ErrorMessage error = new ErrorMessage(
                    "Sensor not found.",
                    404,
                    "/api/v1"
            );
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<SensorReading> sensorReadings = DataStore.readings.get(sensorId);

        if (sensorReadings == null) {
            sensorReadings = new ArrayList<SensorReading>();
        }

        return Response.ok(sensorReadings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            ErrorMessage error = new ErrorMessage(
                    "Sensor not found.",
                    404,
                    "/api/v1"
            );
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is under maintenance and cannot accept readings."
            );
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        if (!DataStore.readings.containsKey(sensorId)) {
            DataStore.readings.put(sensorId, new ArrayList<SensorReading>());
        }

        DataStore.readings.get(sensorId).add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
