/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.mapper;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.exception.SensorUnavailableException;
import com.mycompany.w2120084_coursework.model.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                403,
                "/api/v1"
        );

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .build();
    }
}
