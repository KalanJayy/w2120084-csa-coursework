/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.resource;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.model.ErrorMessage;
import com.mycompany.w2120084_coursework.model.Room;
import com.mycompany.w2120084_coursework.store.DataStore;
import com.mycompany.w2120084_coursework.exception.RoomNotEmptyException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoom {

    @Context
    private UriInfo uriInfo;

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<Room>(DataStore.rooms.values());
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            ErrorMessage error = new ErrorMessage(
                    "Room id is required.",
                    400,
                    "/api/v1"
            );
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (DataStore.rooms.containsKey(room.getId())) {
            ErrorMessage error = new ErrorMessage(
                    "Room with this id already exists.",
                    409,
                    "/api/v1"
            );
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        DataStore.rooms.put(room.getId(), room);

        URI uri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();

        return Response.created(uri).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            ErrorMessage error = new ErrorMessage(
                    "Room not found.",
                    404,
                    "/api/v1"
            );
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            ErrorMessage error = new ErrorMessage(
                    "Room not found.",
                    404,
                    "/api/v1"
            );
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " still has sensors assigned.");
        }

        DataStore.rooms.remove(roomId);

        return Response.ok(room).build();
    }
}
