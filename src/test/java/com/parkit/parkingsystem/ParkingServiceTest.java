package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        // mock the call to getNbTicket()
        when(ticketDAO.getNbTicket(toString())).thenReturn(1);

        parkingService.processExitingVehicle();

        // verify that updateParking() was called once
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // créez les objets nécessaires pour le test
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        when(parkingService.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
        when(parkingService.getVehichleRegNumber()).thenReturn("ABC123");

        // appelez la méthode à tester
        parkingService.processIncomingVehicle();

        // vérifiez que les méthodes attendues ont été appelées avec les bons arguments
        verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }
    
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
        // créez les objets nécessaires pour le test
        Ticket ticket = new Ticket();
        ticket.setPrice(0);
        ticket.setInTime(new Date());
        when(parkingService.getVehichleRegNumber()).thenReturn("ABC123");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        // appelez la méthode à tester
        parkingService.processExitingVehicle();

        // vérifiez que les méthodes attendues ont été appelées avec les bons arguments
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    }
    
    
    @Test
    public void testGetNextParkingNumberIfAvailable() {
        // créez les objets nécessaires pour le test
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // appelez la méthode à tester
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // vérifiez les résultats
        assertNotNull(parkingSpot);
        assertEquals(1, parkingSpot.getId());
        assertTrue(parkingSpot.isAvailable());
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        // créez les objets nécessaires pour le test
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

        // appelez la méthode à tester
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // vérifiez les résultats
        assertNull(parkingSpot);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // créez les objets nécessaires pour le test
    	when(inputReaderUtil.readSelection()).thenReturn(1);

        // appelez la méthode à tester
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // vérifiez les résultats
        assertNull(parkingSpot);
    }
}
