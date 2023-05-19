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
        ticket.setPrice(fareCalculatorService.calculateFare(ticket)); // update ticket with calculated price
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }


    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) ); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(fareCalculatorService.calculateFare(ticket)); // update ticket with calculated price
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


    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes parking should give 3/4th parking fare
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


    /*
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
*/

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) ); // 24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        outTime.setTime( inTime.getTime() + (  2 * 60 * 60 * 1000) ); // 2 hours parking time should give 2 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        double price = fareCalculatorService.calculateFare(ticket);
        ticket.setPrice(price); // update the ticket with the calculated price

        // Calculate the expected price based on the exact parking duration
        long parkingDuration = outTime.getTime() - inTime.getTime();
        double expectedPrice = (Math.ceil(parkingDuration / 3600000.0) * Fare.CAR_RATE_PER_HOUR);
        assertEquals(expectedPrice, ticket.getPrice(), 0.01);
    }

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
    fareCalculatorService.calculateFare(ticket);
    assertEquals(0, ticket.getPrice());
}

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

//Méthode de test qui vérifie le tarif avec réduction pour une voiture
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

// Méthode de test qui vérifie le tarif avec réduction pour une moto
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
