package es.deusto.server.jdo;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable
public class Property implements Serializable {
	@PrimaryKey
	String address = null;
	String city = null;
	int capacity = 0;
	String ocupancy = null; //?
	double cost = 0.0;
	
	public Property(String address, String city, int capacity, String ocupancy, double cost) {
		super();
		this.address = address;
		this.city = city;
		this.capacity = capacity;
		this.ocupancy = ocupancy;
		this.cost = cost;
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

	public String getOcupancy() {
		return ocupancy;
	}

	public void setOcupancy(String ocupancy) {
		this.ocupancy = ocupancy;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Property [address=" + address + ", capacity=" + capacity + ", ocupancy=" + ocupancy + ", cost=" + cost + "]";
	}		
}
