package es.deusto.server.jdo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Property {
	@PrimaryKey
	String id = null;
	String address = null;
	String city = null;
	int capacity = 0;
	String ocupancy = null; //?
	double cost = 0.0;
	
	public Property(String id, String address, int capacity, String ocupancy, double cost) {
		super();
		this.id = id;
		this.address = address;
		this.capacity = capacity;
		this.ocupancy = ocupancy;
		this.cost = cost;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
		return "Property [id=" + id + ", address=" + address + ", capacity=" + capacity + ", ocupancy=" + ocupancy
				+ ", cost=" + cost + "]";
	}
	
		
}
