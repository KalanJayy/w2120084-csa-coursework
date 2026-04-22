/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.w2120084_coursework;

/**
 *
 * @author kalanajayaweera
 */
import com.mycompany.w2120084_coursework.resource.Discovery;
import com.mycompany.w2120084_coursework.resource.SensorRoom;
import com.mycompany.w2120084_coursework.resource.SensorResource;
import com.mycompany.w2120084_coursework.mapper.RoomNotEmptyExceptionMapper;
import com.mycompany.w2120084_coursework.mapper.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.w2120084_coursework.mapper.SensorUnavailableExceptionMapper;
import com.mycompany.w2120084_coursework.mapper.GlobalExceptionMapper;
import com.mycompany.w2120084_coursework.filter.LoggingFilter;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        classes.add(Discovery.class);
        classes.add(SensorRoom.class);
        classes.add(SensorResource.class);

        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        classes.add(LoggingFilter.class);

        return classes;
    }
}