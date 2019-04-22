package es.deusto.server.jdo;

import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Reservation {
	@ForeignKey
	Property property;
	@ForeignKey
	User guest;
	@ForeignKey
	User host;
	String date = null;
	int duration = 0;
	
	public Reservation(Property property, User guest, User host, String date, int duration) {
		super();
		this.property = property;
		this.guest = guest;
		this.host = host;
		this.date = date;
		this.duration = duration;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public User getClient() {
		return guest;
	}

	public void setClient(User guest) {
		this.guest = guest;
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
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
		return "Reservation [property=" + property + ", client=" + guest + ", host=" + host + ", date="
				+ date + ", duration=" + duration + "]";
	}
	
}
