package es.deusto.server.jdo;

import java.io.Serializable;

import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Reservation implements Serializable {
	@ForeignKey
	Property property;
	@ForeignKey
	User guest;
	String date = null;
	int duration = 0;
	
	public Reservation(Property property, User guest, String date, int duration) {
		super();
		this.property = property;
		this.guest = guest;
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
		return "Reservation [property=" + property + ", guest=" + guest + ", date=" + date + ", duration=" + duration
				+ "]";
	}
	
}
