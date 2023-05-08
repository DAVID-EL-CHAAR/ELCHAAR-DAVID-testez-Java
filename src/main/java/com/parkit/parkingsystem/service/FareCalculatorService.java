package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

   public void calculateFare(Ticket ticket){
    if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
        throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
    }

    long inTimeMillis = ticket.getInTime().getTime();
    long outTimeMillis = ticket.getOutTime().getTime();
    long parkingDurationMillis = outTimeMillis - inTimeMillis;
    int parkingDurationMinutes = (int) (parkingDurationMillis / (60 * 1000));

    if (parkingDurationMinutes <= 30) {
        ticket.setPrice(0);
    } else {
        int durationInHours = (int) Math.ceil(parkingDurationMillis / (1000.0 * 60.0 * 60.0));
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            case BIKE:
                ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}

}