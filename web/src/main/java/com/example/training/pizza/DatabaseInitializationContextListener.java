package com.example.training.pizza;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.example.training.util.NihDatabaseMigration;

public class DatabaseInitializationContextListener implements ServletContextListener {

	@Resource(name = "jdbc/MyDataSource")
	private DataSource ds;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		new NihDatabaseMigration(ds).applyMigrations(List.of(
				"/schema.sql",
				"/data.sql"
		));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		shutdownH2Database(ds);
		deregisterAllJdbcDrivers(sce);
	}

	@SuppressWarnings({"PMD.EmptyCatchBlock"})
	private static void shutdownH2Database(DataSource ds) {
		try {
			Connection con = ds.getConnection();
			try (Statement st = con.createStatement()) {
				st.execute("SHUTDOWN");
			}
			// con.close() throws exception
			if (con.isWrapperFor(org.h2.jdbc.JdbcConnection.class)) {
				con.unwrap(org.h2.jdbc.JdbcConnection.class).close();
			}
		} catch (SQLException e) {
			// Continue
		}
	}

	@SuppressWarnings({"PMD.UseProperClassLoader", "PMD.EmptyCatchBlock", "PMD.CompareObjectsWithEquals"})
	private static void deregisterAllJdbcDrivers(ServletContextEvent sce) {
		for (Driver driver : Collections.list(DriverManager.getDrivers())) {
			if (driver.getClass().getClassLoader() == sce.getServletContext().getClassLoader()) {
				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException ex) {
					// Continue
				}
			}
		}
	}
}
