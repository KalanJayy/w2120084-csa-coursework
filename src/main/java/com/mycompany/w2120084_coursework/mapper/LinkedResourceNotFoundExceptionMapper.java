/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework.mapper;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.exception.LinkedResourceNotFoundException;
import com.mycompany.w2120084_coursework.model.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                422,
                "/api/v1"
        );

        return Response.status(422)
                .entity(error)
                .build();
    }
}
