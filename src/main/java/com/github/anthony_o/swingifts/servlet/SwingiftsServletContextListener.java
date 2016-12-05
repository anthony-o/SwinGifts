package com.github.anthony_o.swingifts.servlet;

import com.github.anthony_o.swingifts.util.InjectUtils;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class SwingiftsServletContextListener implements ServletContextListener {
    private final static Logger LOG = LoggerFactory.getLogger(SwingiftsServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.info("Swingifts started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Swingifts stoping");
        InjectUtils.getInstance(Server.class).stop();
        try {
            InjectUtils.getInstance(Connection.class).createStatement().execute("SHUTDOWN"); // close all the H2 connections thanks to http://stackoverflow.com/a/40762283/535203
        } catch (SQLException e) {
            LOG.error("Problem while shutting down H2", e);
        }
        LOG.info("Swingifts stopped");
    }
}
