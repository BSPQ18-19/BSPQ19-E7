package es.deusto.server.jdo;

import java.io.Serializable;
import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.PersistenceCapable;

@SuppressWarnings("serial")
@PersistenceCapable
public class Occupancy implements Serializable{
	@ForeignKey
	Property property;
	String startDate;
	String endDate;
	
	public Occupancy(Property property, String startDate, String endDate) {
		super();
		this.property = property;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Property getProperty() {
		return property;
	}
	
	public void setProperty(Property property) {
		this.property = property;
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
		return "Occupancy [property=" + property + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

}
