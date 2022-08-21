package com.example.training.pizza;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.example.training.util.NihDatabaseMigration;

public class TemporaryDatabaseExtension implements BeforeEachCallback, AfterEachCallback {

	private static final AtomicInteger SEQNO = new AtomicInteger(0);
	private DataSource ds;

	public DataSource get() {
		return ds;
	}

	@Override
	public void beforeEach(ExtensionContext extensionContext) {
		ds = createDataSource();
	}

	@Override
	public void afterEach(ExtensionContext extensionContext) {
		closeDataSource(ds);
	}

	private static DataSource createDataSource() {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:testdb" + SEQNO.getAndIncrement() + ";DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		h2.setPassword("sa");
		new NihDatabaseMigration(h2).applyMigrations(List.of(
			"/schema.sql",
			"/data.sql"
		));
		return h2;
	}

	private static void closeDataSource(DataSource ds) {
		try (Connection con = ds.getConnection();
			 Statement st = con.createStatement()) {
			st.execute("SHUTDOWN");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
