package org.vaadin.firitin.formbinder.hibernatecrossfieldexample;

import java.util.List;

@ValidPassengerCount(message = "There must be not more passengers than seats.")
public class Car {

	private int seatCount;

	private List<Person> passengers;

	public Car(int seatCount, List<Person> passengers) {
		this.seatCount = seatCount;
		this.passengers = passengers;
	}

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}

	public List<Person> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Person> passengers) {
		this.passengers = passengers;
	}
}