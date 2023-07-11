package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import junit.framework.Assert;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	
	
	private static final String VEHICLE_REG_NUMBER = "12345";
	
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    
    @Captor
    private ArgumentCaptor<ParkingSpot> parkingSpotCaptor;
     

    @Mock
    private FareCalculatorService fareCalculatorService;
  
    

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    /**
     * Cette méthode d'initialisation est exécutée avant chaque test.
     * Elle initialise les objets simulés nécessaires pour les tests.
     */
    
    @SuppressWarnings("deprecation")
	@BeforeEach
    private void setUpPerTest() {
    	   MockitoAnnotations.initMocks(this);
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            System.setOut(new PrintStream(outputStreamCaptor));

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Échec de la configuration des objets fictifs de test");
        }
    }
    
    
    /**
     * Cette méthode est exécutée après chaque test.
     * Elle rétablit le flux de sortie système par défaut.
     */
    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }
    
      
    /**
     * Ce test vérifie que la méthode processExitingVehicle du service de parking fonctionne correctement.
     */
    
    @Test
    public void processExitingVehicleTest() {
        try {
            
            String vehicleRegNumber = "ABCDEF";
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            Ticket ticket = spy(new Ticket()); 
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(vehicleRegNumber);
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.getNbTicket(anyString())).thenReturn(1);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            

            
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            parkingService.processExitingVehicle();

            verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(1)).getTicket(vehicleRegNumber);
            verify(ticketDAO, Mockito.times(1)).getNbTicket(vehicleRegNumber);
            verify(ticket, Mockito.times(1)).setOutTime(any(Date.class));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Echec de l'installation des objets mock");
        }
    }

    
    /**
     * Ce test vérifie que la méthode processIncomingVehicle du service de parking fonctionne correctement.
     */
  
    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // Arrange
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_REG_NUMBER);
        when(inputReaderUtil.readSelection()).thenReturn(1); 

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any())).thenReturn(true);

        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Act
        parkingService.processIncomingVehicle();

        // Assert
        verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
        verify(ticketDAO, times(1)).saveTicket(any());

        ParkingSpot capturedParkingSpot = parkingSpotCaptor.getValue();
        assertEquals(expectedParkingSpot.getId(), capturedParkingSpot.getId());
        assertEquals(expectedParkingSpot.getParkingType(), capturedParkingSpot.getParkingType());
        assertEquals(expectedParkingSpot.isAvailable(), capturedParkingSpot.isAvailable());

        System.out.println("Expected parking spot: " + expectedParkingSpot.toString());
        System.out.println("Captured parking spot: " + capturedParkingSpot.toString());
    }
    
    /**
     * Ce test vérifie que la méthode getNextParkingNumberIfAvailable renvoie le bon numéro de parking si une place est disponible.
     */
    @Test
    public void testGetNextParkingNumberIfAvailable() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); 
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertEquals(1, parkingSpot.getId());
        assertEquals(true, parkingSpot.isAvailable());
    }
    
    /**
     * Ce test vérifie que l'exception est correctement levée lorsque la mise à jour du ticket échoue.
     */
    @Test
    public void processExitingVehicleTestUpdateFails() {
        // Arrange
    	Ticket mockTicket = mock(Ticket.class);
        
        lenient().when(ticketDAO.getTicket(anyString())).thenReturn(mockTicket);
        lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);  
        
        
        
        // Act and Assert
        assertThrows(Exception.class, () -> parkingService.processExitingVehicle());
    }

    
    /**
     * Ce test vérifie que la méthode getNextParkingNumberIfAvailable retourne null si aucune place de parking n'est disponible.
     */
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); 
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); 

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertNull(parkingSpot);
    }
    
    
    /**
     * Ce test vérifie que la méthode getNextParkingNumberIfAvailable retourne null si une option non valide est sélectionnée.
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(3); 

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertNull(parkingSpot);
    }
    
    
    
    
}
    
 
    


