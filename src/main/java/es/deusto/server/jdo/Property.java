package es.deusto.server.jdo;

import java.io.Serializable;
import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable
public class Property implements Serializable {
	@PrimaryKey
	String address = null;
	String city = null;
	int capacity = 0;
	double cost = 0.0;
	@ForeignKey
	User host;
	
	public Property(String address, String city, int capacity, double cost, User host) {
		super();
		this.address = address;
		this.city = city;
		this.capacity = capacity;
		this.cost = cost;
		this.host = host;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return "Property [address=" + address + ", city=" + city + ", capacity=" + capacity + ", cost=" + cost
				+ ", host=" + host + "]";
	}

}
