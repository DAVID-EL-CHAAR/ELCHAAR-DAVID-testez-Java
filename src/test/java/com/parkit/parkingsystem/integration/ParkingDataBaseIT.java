package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import com.parkit.parkingsystem.util.DateForParkingApp;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    
    @Mock
    private static DateForParkingApp dateForParkingApp;

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar() throws Exception {
     //GIVEN - ARRANGE
     when(inputReaderUtil.readSelection()).thenReturn(1);
     String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
     when(vehicleRegNumber).thenReturn("ABCDEF");
     dataBasePrepareService.clearDataBaseEntries();
     Date date = new Date();
     when(dateForParkingApp.getDateForParkingApp()).thenReturn(date);
     ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp);
     int previousSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
     //WHEN - ACT
     parkingService.processIncomingVehicle();
     //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
     Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);//DNAS LA DB TICKET je vais chch mon ticket
     //THEN - ASSERT
     assertNotNull(ticket, "The ticket should not be null");
     ParkingSpot parkSpot = ticket.getParkingSpot();
     assertEquals(previousSlot, parkSpot.getId(), "The parking spot id should match the previous available slot");
     boolean avail = parkSpot.isAvailable();
     assertFalse(avail, "The parking spot should not be available after parking a car");
    }
 
    @Test
    //ajout de throws dans une méthode de test...
    public void testParkingLotExitCAR() throws Exception {


        testParkingACar();
        System.out.println(ticketDAO.getTicket("ABCDEF").getOutTime());
        System.out.println(ticketDAO.getTicket("ABCDEF").getPrice());
        long timeIn = ticketDAO.getTicket("ABCDEF").getInTime().getTime();
        long timeOut = timeIn + (60 * 60 * 1000);
        System.out.println("voici timeout "+ timeOut);

       Date dateOut = new Date(timeOut);

        when(dateForParkingApp.getDateForParkingApp()).thenReturn(dateOut);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, dateForParkingApp); //parce que je vais avoir besoin d'un

        parkingService.processExitingVehicle();

        String encodedRegistration = inputReaderUtil.readVehicleRegistrationNumber();

        //TODO: check that the fare generated and out time are populated (remplis) correctly in the database

        assertTrue(ticketDAO.getTicket(encodedRegistration).getPrice() > 0);
        assertNotNull(ticketDAO.getTicket(encodedRegistration).getOutTime());
    }
    
    
}

