package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.parkit.parkingsystem.util.DateForParkingApp;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService;

    @Mock
    private static DateForParkingApp dateForParkingApp;

    @InjectMocks
    private ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    /**
     * Cette méthode est exécutée une seule fois avant tous les tests.
     */
    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    /**
     * Cette méthode est exécutée avant chaque test.
     */
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
 
        dataBasePrepareService.clearDataBaseEntries();
      
    }

 

    /**
     * Ce test vérifie si une voiture est correctement garée.
     */
    @Test
    public void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

      
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());

       
        ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
        assertFalse(parkingSpot.isAvailable());
    }

    /**
     * Ce test vérifie si la sortie du parking se déroule correctement.
     */
    @Test
    public void testParkingLotExit() throws Exception{
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        Thread.sleep(500);
        parkingService.processExitingVehicle();

        
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

       
        assertNotNull(ticket.getPrice(), "Le tarif doit être renseigné");

        
        assertNotNull(ticket.getOutTime(), "Le temps de sortie doit être renseigné");
    }

    /**
     * Ce test vérifie si un utilisateur récurrent est correctement traité lors de la sortie du parking.
     */
    @Test
    public void testParkingLotExitRecurringUser() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        String vehicleRegNumber = "ABCDEF";

        
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        parkingService.processIncomingVehicle();

        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        
        parkingService.processExitingVehicle();

      
        Ticket firstSavedTicket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotNull(firstSavedTicket, "Le ticket n'a pas été enregistré correctement");

        double firstSavedTicketPrice = firstSavedTicket.getPrice();

     
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        
        parkingService.processIncomingVehicle();

       
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        
        parkingService.processExitingVehicle();

        
        Ticket secondSavedTicket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotNull(secondSavedTicket, "Le ticket n'a pas été mis à jour correctement");
        assertNotNull(secondSavedTicket.getOutTime(), "L'heure de sortie n'a pas été définie");

       
        assertEquals(vehicleRegNumber, secondSavedTicket.getVehicleRegNumber());
        double expectedPrice = firstSavedTicketPrice * 0.95; // 5% discount
        assertEquals(expectedPrice, secondSavedTicket.getPrice(), 0.01, "Le prix du billet n'a pas été calculé correctement pour un utilisateur récurrent");
    }
}