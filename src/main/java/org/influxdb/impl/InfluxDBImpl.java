package org.influxdb.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.ContinuousQuery;
import org.influxdb.dto.Database;
import org.influxdb.dto.Pong;
import org.influxdb.dto.ScheduledDelete;
import org.influxdb.dto.Serie;
import org.influxdb.dto.User;

import retrofit.RestAdapter;

public class InfluxDBImpl implements InfluxDB {
	private final String username;
	private final String password;
	private final RestAdapter restAdapter;
	private final InfluxDBService influxDBService;

	public InfluxDBImpl(final String url, final String username, final String password) {
		super();
		this.username = username;
		this.password = password;

		this.restAdapter = new RestAdapter.Builder()
				.setEndpoint(url)
				.setErrorHandler(new InfluxDBErrorHandler())
				.build();
		this.influxDBService = this.restAdapter.create(InfluxDBService.class);
	}

	@Override
	public InfluxDB setLogLevel(final LogLevel logLevel) {
		switch (logLevel) {
		case NONE:
			this.restAdapter.setLogLevel(retrofit.RestAdapter.LogLevel.NONE);
			break;
		case BASIC:
			this.restAdapter.setLogLevel(retrofit.RestAdapter.LogLevel.BASIC);
			break;
		case HEADERS:
			this.restAdapter.setLogLevel(retrofit.RestAdapter.LogLevel.HEADERS);
			break;
		case FULL:
			this.restAdapter.setLogLevel(retrofit.RestAdapter.LogLevel.FULL);
			break;
		default:
			break;
		}
		return this;
	}

	@Override
	public Pong ping() {
		return this.influxDBService.ping();
	}

	@Override
	public void write(final String database, final Serie[] series, final TimeUnit precision) {
		this.influxDBService.write(database, series, this.username, this.password, toTimePrecision(precision));
	}

	@Override
	public List<Serie> Query(final String database, final String query, final TimeUnit precision) {
		return this.influxDBService.query(database, query, this.username, this.password, toTimePrecision(precision));
	}

	@Override
	public void createDatabase(final String name, final int replicationFactor) {
		Database db = new Database(name, replicationFactor);
		String response = this.influxDBService.createDatabase(db, this.username, this.password);
	}

	@Override
	public void deleteDatabase(final String name) {
		String response = this.influxDBService.deleteDatabase(name, this.username, this.password);
	}

	@Override
	public List<Database> describeDatabases() {
		return this.influxDBService.describeDatabases(this.username, this.password);
	}

	@Override
	public void createClusterAdmin(final String name, final String password) {
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		this.influxDBService.createClusterAdmin(user, this.username, this.password);
	}

	@Override
	public void deleteClusterAdmin(final String name) {
		this.influxDBService.deleteClusterAdmin(name, this.username, this.password);
	}

	@Override
	public List<User> describeClusterAdmins() {
		return this.influxDBService.describeClusterAdmins(this.username, this.password);
	}

	@Override
	public void updateClusterAdmin(final String name, final String password) {
		User user = new User();
		user.setPassword(password);
		this.influxDBService.updateClusterAdmin(user, name, this.username, this.password);
	}

	@Override
	public void createDatabaseUser(final String database, final String name, final String password,
			final String... permissions) {
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		user.setPermissions(permissions);
		this.influxDBService.createDatabaseUser(database, user, this.username, this.password);
	}

	@Override
	public void deleteDatabaseUser(final String database, final String name) {
		this.influxDBService.deleteDatabaseUser(database, name, this.username, this.password);
	}

	@Override
	public List<User> describeDatabaseUsers(final String database) {
		return this.influxDBService.describeDatabaseUsers(database, this.username, this.password);
	}

	@Override
	public void updateDatabaseUser(final String database, final String name, final String password,
			final String... permissions) {
		User user = new User();
		user.setPassword(password);
		user.setPermissions(permissions);
		this.influxDBService.updateDatabaseUser(database, user, name, this.username, this.password);
	}

	@Override
	public void alterDatabasePrivilege(final String database, final String name, final boolean isAdmin,
			final String... permissions) {
		User user = new User();
		user.setAdmin(isAdmin);
		user.setPermissions(permissions);
		this.influxDBService.updateDatabaseUser(database, user, name, this.username, this.password);
	}

	@Override
	public void authenticateDatabaseUser(final String database, final String username, final String password) {
		this.influxDBService.authenticateDatabaseUser(database, username, password);
	}

	@Override
	public List<ContinuousQuery> getContinuousQueries(final String database) {
		return this.influxDBService.getContinuousQueries(database, this.username, this.password);
	}

	@Override
	public void deleteContinuousQuery(final String database, final int id) {
		this.influxDBService.deleteContinuousQuery(database, id, this.username, this.password);
	}

	@Override
	public void deletePoints(final String database, final String serieName) {
		// TODO implement
		throw new IllegalArgumentException();
	}

	@Override
	public void createScheduledDelete(final String database, final ScheduledDelete delete) {
		// TODO implement
		throw new IllegalArgumentException();
	}

	@Override
	public List<ScheduledDelete> describeScheduledDeletes(final String database) {
		// TODO implement
		throw new IllegalArgumentException();
	}

	@Override
	public void removeScheduledDelete(final String database, final int id) {
		// TODO implement
		throw new IllegalArgumentException();
	}

	private static String toTimePrecision(final TimeUnit t) {
		switch (t) {
		case SECONDS:
			return "s";
		case MILLISECONDS:
			return "m";
		case MICROSECONDS:
			return "u";
		default:
			throw new IllegalArgumentException("time precision should be SECONDS or MILLISECONDS or MICROSECONDS");
		}
	}

}
