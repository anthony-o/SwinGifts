package com.github.anthony_o.swingifts.servlet;

import com.github.anthony_o.swingifts.util.InjectUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class SwingiftsServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Swingifts started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Swingifts stoping");
        //InjectUtils.getInstance(Server.class).shutdown();
        try {
            InjectUtils.getInstance(Connection.class).createStatement().execute("SHUTDOWN"); // close all the H2 connections thanks to http://stackoverflow.com/a/40762283/535203
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Swingifts stopped");
    }
}
