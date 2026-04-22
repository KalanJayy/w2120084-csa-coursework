/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.resource;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.model.Sensor;
import com.mycompany.w2120084_coursework.model.Room;
import com.mycompany.w2120084_coursework.model.ErrorMessage;
import com.mycompany.w2120084_coursework.store.DataStore;
import com.mycompany.w2120084_coursework.exception.LinkedResourceNotFoundException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @Context
    private UriInfo uriInfo;

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<Sensor>(DataStore.sensors.values());

        if (type == null || type.trim().isEmpty()) {
            return sensorList;
        }

        List<Sensor> filtered = new ArrayList<Sensor>();

        for (Sensor sensor : sensorList) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                filtered.add(sensor);
            }
        }

        return filtered;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            ErrorMessage error = new ErrorMessage(
                    "Sensor id is required.",
                    400,
                    "/api/v1"
            );
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "The roomId '" + sensor.getRoomId() + "' does not exist."
            );
        }

        if (DataStore.sensors.containsKey(sensor.getId())) {
            ErrorMessage error = new ErrorMessage(
                    "Sensor with this id already exists.",
                    409,
                    "/api/v1"
            );
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        DataStore.sensors.put(sensor.getId(), sensor);

        Room room = DataStore.rooms.get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        URI uri = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();

        return Response.created(uri).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            ErrorMessage error = new ErrorMessage(
                    "Sensor not found.",
                    404,
                    "/api/v1"
            );
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
