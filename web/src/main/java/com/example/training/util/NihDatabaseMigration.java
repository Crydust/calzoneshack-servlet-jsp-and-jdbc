package com.example.training.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * I'd recommend using Flyway, Liquibase or something like it if you need something more robust.
 * This implementation will not allow comments in sql for example.
 */
public class NihDatabaseMigration {

	private static final Logger LOGGER = Logger.getLogger(NihDatabaseMigration.class.toString());

	private final DataSource ds;

	public NihDatabaseMigration(DataSource ds) {
		this.ds = ds;
	}

	private static void applySqlStatements(DataSource ds, String sqls) throws SQLException {
		try (Connection con = ds.getConnection();
			 Statement st = con.createStatement()) {
			for (String sql : sqls.split(";")) {
				if (!sql.isBlank()) {
					st.executeUpdate(sql);
				}
			}
		}
	}

	private static boolean failedMigrationsExist(DataSource ds) throws SQLException {
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select null from DBMIGRATIONS where state = ?")) {
			ps.setString(1, MigrationState.FAILED.name());
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	private static boolean unknownMigrationsExist(DataSource ds, Collection<String> migrations) throws SQLException {
		if (migrations.isEmpty()) {
			return false;
		}

		final String questionMarks = migrations.stream()
				.map(it -> "?")
				.collect(joining(", "));
		final String sql = "select null from DBMIGRATIONS where filename not in (" +
				questionMarks +
				") fetch first 1 rows only";

		//noinspection SqlSourceToSinkFlow
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			int parameterIndex = 1;
			for (String migration : migrations) {
				ps.setString(parameterIndex, migration);
				parameterIndex++;
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	private static MigrationState determineMigrationState(DataSource ds, String migration, String currentHash) throws SQLException {
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select hash from DBMIGRATIONS where filename = ?")) {
			ps.setString(1, migration);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return MigrationState.NEW;
				}
				if (currentHash.equals(rs.getString(1))) {
					return MigrationState.EXISTING_ALREADY_APPLIED;
				}
				return MigrationState.EXISTING_DIFFERENT_HASH;
			}
		}
	}

	private static boolean dbMigrationsTableExists(DataSource ds) throws SQLException {
		try (Connection con = ds.getConnection();
			 ResultSet tables = con.getMetaData().getTables(null, null, "DBMIGRATIONS", null)) {
			return tables.next();
		}
	}

	private static void insertMigrationState(String sqlFile, String currentHash, MigrationState state, DataSource ds) throws SQLException {
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("insert into DBMIGRATIONS (filename, hash, state) values (?, ?, ?)")) {
			ps.setString(1, sqlFile);
			ps.setString(2, currentHash);
			ps.setString(3, state.name());
			ps.executeUpdate();
		}
	}

	private static void createDbMigrationsTable(DataSource ds) throws SQLException {
		try (Connection con = ds.getConnection();
			 Statement st = con.createStatement()) {
			st.execute("""
					create table DBMIGRATIONS (
						filename varchar(255),
						hash varchar(64),
						state varchar(32)
					)
					""");
		}
	}

	public void applyMigrations(List<String> migrations) {
		try {
			if (!dbMigrationsTableExists(ds)) {
				LOGGER.fine("Creating table 'DBMIGRATIONS'");
				createDbMigrationsTable(ds);
			}
			if (failedMigrationsExist(ds)) {
				throw new RuntimeException("Previously failed migrations found.");
			}
			if (unknownMigrationsExist(ds, migrations)) {
				throw new RuntimeException("Unknown migrations are applied to the database.");
			}
			for (String migration : migrations) {
				byte[] bytes = readFileFromClasspath(migration);
				String currentHash = NihDigestUtils.sha256Hex(bytes);
				LOGGER.fine("Checking state of migration '%s' with hash '%s'.".formatted(migration, currentHash));
				MigrationState state = determineMigrationState(ds, migration, currentHash);
				if (state == MigrationState.EXISTING_ALREADY_APPLIED) {
					LOGGER.fine("Skipping migration '%s' because it is already applied".formatted(migration));
				} else if (state == MigrationState.EXISTING_DIFFERENT_HASH) {
					throw new RuntimeException("Migration '%s' has hash '%s', but previously had another hash"
							.formatted(migration, currentHash));
				} else {
					try {
						LOGGER.fine("Applying migration '%s'".formatted(migration));
						applySqlStatements(ds, new String(bytes, UTF_8));
						state = MigrationState.SUCCEEDED;
					} catch (SQLException e) {
						LOGGER.severe("Could not apply migration '%s'".formatted(migration));
						state = MigrationState.FAILED;
						throw new RuntimeException(e);
					} finally {
						insertMigrationState(migration, currentHash, state, ds);
					}
				}
			}
		} catch (IOException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] readFileFromClasspath(String sqlFile) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(sqlFile)) {
			return requireNonNull(in).readAllBytes();
		}
	}

	private enum MigrationState {
		NEW, EXISTING_ALREADY_APPLIED, EXISTING_DIFFERENT_HASH, FAILED, SUCCEEDED
	}

}
