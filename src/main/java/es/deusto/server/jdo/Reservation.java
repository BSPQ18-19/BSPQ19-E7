package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Reservation {
	@PrimaryKey
	String id = null;
	Property property;
	Guest client;
	Host host;
	String date = null;
	int duration = 0;
	
	public Reservation(String id, Property property, Guest client, Host host, String date, int duration) {
		super();
		this.id = id;
		this.property = property;
		this.client = client;
		this.host = host;
		this.date = date;
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Guest getClient() {
		return client;
	}

	public void setClient(Guest client) {
		this.client = client;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Reservation [id=" + id + ", property=" + property + ", client=" + client + ", host=" + host + ", date="
				+ date + ", duration=" + duration + "]";
	}
	
}
