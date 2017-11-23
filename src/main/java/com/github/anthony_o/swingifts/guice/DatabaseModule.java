package com.github.anthony_o.swingifts.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseModule extends AbstractModule {

    public static final String DB_PATH_PROPERTY_NAME = "swingifts.dbH2Path";
    private Server server;

    @Override
    protected void configure() {

    }

    @Provides @Singleton
    public JdbcConnectionPool createJdbcConnectionPool() throws ClassNotFoundException, SQLException, LiquibaseException {
        Class.forName("org.h2.Driver");
        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(getJdbcUrl(), "sa", "");
        Connection connection = connectionPool.getConnection();
        Liquibase liquibase = new Liquibase("db_schema/db.changelog-master.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        liquibase.update(new Contexts());
        // start the server, one can remotely connect on it with the following uri: jdbc:h2:tcp://<host>:9092/<swingifts.dbH2Path property or /var/local/swingifts/db> . Typically jdbc:h2:tcp://localhost:9092//var/local/swingifts/db when using the Docker installation.
        server = Server.createTcpServer("-tcpAllowOthers");
        server.start();
        return connectionPool;
    }

    @Provides @Singleton
    @Inject
    public DataSource createDataSource(JdbcConnectionPool jdbcConnectionPool) {
        return jdbcConnectionPool;
    }

    protected String getJdbcUrl() {
        return "jdbc:h2:" + System.getProperty(DB_PATH_PROPERTY_NAME, "/var/local/swingifts/db");
    }

    public static void setDbPath(Path path) {
        System.setProperty(DB_PATH_PROPERTY_NAME, path.toAbsolutePath().toString());
    }

    @Provides
    @Inject
    public Connection getOrCreateConnection(DataSource dataSource) throws ClassNotFoundException, SQLException, LiquibaseException {
        return dataSource.getConnection();
    }

    @Provides @Singleton
    @Inject
    public DBI getOrCreateDBI(DataSource dataSource) {
        return new DBI(dataSource);
    }

    @Provides
    public Server getServer() {
        return server;
    }
}
