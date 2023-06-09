package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(fareCalculatorService.calculateFare(ticket)); 
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }


    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); 
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(fareCalculatorService.calculateFare(ticket)); 
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }


    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        // Création des objets nécessaires pour le test
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes de stationnement devraient donner 3/4 du tarif de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        Ticket ticket = new Ticket();

        // Attribution des valeurs aux propriétés du ticket
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Appel de la méthode à tester
        double calculatedPrice = fareCalculatorService.calculateFare(ticket);

        // Vérification de l'égalité entre le prix calculé et le prix attendu
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), calculatedPrice);
    }

    /**
     * Ce test vérifie que le tarif calculé pour une voiture garée moins d'une heure est correct
     */
    
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); 
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(fareCalculatorService.calculateFare(ticket)); //update ticket with calculated price

        double expectedPrice = 0.75 * Fare.CAR_RATE_PER_HOUR;
        expectedPrice = Math.round(expectedPrice * 100.0) / 100.0;

        assertEquals(expectedPrice, ticket.getPrice());
    }
    
    /**
     * Ce test vérifie que le tarif calculé pour une voiture garée plus d'une journée est correct
     */

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );
        Date outTime = new Date();
        outTime.setTime( inTime.getTime() + (  2 * 60 * 60 * 1000) ); 
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        double price = fareCalculatorService.calculateFare(ticket);
        ticket.setPrice(price); 

        
        long parkingDuration = outTime.getTime() - inTime.getTime();
        double expectedPrice = (Math.ceil(parkingDuration / 3600000.0) * Fare.CAR_RATE_PER_HOUR);
        assertEquals(expectedPrice, ticket.getPrice(), 0.01);
    }

    /**
     * Ce test vérifie que le tarif calculé pour une voiture garée moins de 30 minutes est correct
     */

    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime(){
    // Créer un objet de type ParkingSpot pour une voiture
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

    // Créer un objet de type Ticket avec une heure d'entrée il y a moins de 30 minutes
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); // 29 minutes
    Date outTime = new Date();
    outTime.setTime(System.currentTimeMillis());
    Ticket ticket = new Ticket();
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("ABCDEF");
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);

    // Appeler la méthode calculateFare avec le ticket et vérifier que le prix est égal à 0
    FareCalculatorService fareCalculatorService = new FareCalculatorService();
    double price = fareCalculatorService.calculateFare(ticket);
    ticket.setPrice(price);
    assertEquals(0, ticket.getPrice(), 0.0001);
}
    
    
    /**
     * Ce test vérifie que le tarif calculé pour une moto garée moins de 30 minutes est correct
     */

   
   @Test
   public void calculateFareBikeWithLessThan30minutesParkingTime(){
    // Créer un objet de type ParkingSpot pour une moto
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

    // Créer un objet de type Ticket avec une heure d'entrée il y a moins de 30 minutes
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); // 29 minutes
    Date outTime = new Date();
    outTime.setTime(System.currentTimeMillis());
    Ticket ticket = new Ticket();
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("ABCDEF");
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);

    // Appeler la méthode calculateFare avec le ticket et vérifier que le prix est égal à 0
    FareCalculatorService fareCalculatorService = new FareCalculatorService();
    fareCalculatorService.calculateFare(ticket);
    assertEquals(0, ticket.getPrice());
}
   
   
   /**
    * Ce test vérifie que le tarif calculé avec réduction pour une voiture est correct
    */

    @Test
    public void calculateFareCarWithDiscount() {
    // Création d'un ticket concernant une voiture avec une durée de 45 minutes
    Ticket ticket = new Ticket();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setParkingSpot(parkingSpot);
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);

    // Appel de la méthode à tester avec le paramètre discount à true
    double price = new FareCalculatorService().calculateFare(ticket, true);

     // Calcul du prix attendu avec réduction pour une voiture avec une durée de 45 minutes
     double expectedPrice = new FareCalculatorService().calculateFullFareCar(45) * 0.95;

     // Vérification que le prix calculé est égal au prix attendu
     assertEquals(expectedPrice, price, 0.01);
}
   
   /**
    * *
    * Ce test vérifie que le tarif calculé avec réduction pour une moto est correct
    */

   @Test
   public void calculateFareBikeWithDiscount() {
    // Création d'un ticket concernant une moto avec une durée de 45 minutes
    Ticket ticket = new Ticket();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setParkingSpot(parkingSpot);
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);

    // Appel de la méthode à tester avec le paramètre discount à true
    double price = new FareCalculatorService().calculateFare(ticket, true);

     // Calcul du prix attendu avec réduction pour une moto avec une durée de 45 minutes
     double expectedPrice = new FareCalculatorService().calculateFullFareBike(45) * 0.95;

     // Vérification que le prix calculé est égal au prix attendu
     assertEquals(expectedPrice, price, 0.01);
}


}
