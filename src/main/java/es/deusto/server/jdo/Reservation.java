package es.deusto.server.jdo;

import java.io.Serializable;
import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.PersistenceCapable;

@SuppressWarnings("serial")
@PersistenceCapable
public class Reservation implements Serializable {
	@ForeignKey
	Property property;
	@ForeignKey
	User guest;
	String startDate;
	String endDate;
	
	public Reservation(Property property, User guest, String startDate, String endDate) {
		super();
		this.property = property;
		this.guest = guest;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public User getGuest() {
		return guest;
	}

	public void setGuest(User guest) {
		this.guest = guest;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Reserv.[ Location: "+property.address+", "+property.city+". From:"+startDate+" To:"+endDate+" by "+guest.getName()+"]";
	}

}
