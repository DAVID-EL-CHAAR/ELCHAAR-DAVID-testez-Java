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
     
    private FareCalculatorService fareCalculatorService;
  
    

   

  /*  @BeforeEach
    private void setUpPerTest() {
    	
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }
  */
    
    @BeforeEach
    private void setUpPerTest() {
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

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }
    
    
    
    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).getNbTicket(anyString()); //verifying the call of getNbTicket() method
    }
    
   /*
    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // arrange
    	
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        String vehicleRegNumber = "12345";

       
           
		   
           // when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(parkingService.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
            when(parkingService.getVehichleRegNumber()).thenReturn(vehicleRegNumber);


        // act
          
                parkingService.processIncomingVehicle();
           
        // assert
       
            
            
            verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        

    }
    */
    /*
    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // arrange
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        // Mock the behaviour of inputReaderUtil's methods
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_REG_NUMBER);
        when(inputReaderUtil.readSelection()).thenReturn(1); // or any other integer value

        // Mock the behaviour of DAO methods
        when(parkingSpotDAO.updateParking(eq(parkingSpot))).thenReturn(true);
        when(ticketDAO.saveTicket(any())).thenReturn(true);

        // act
        parkingService.processIncomingVehicle();

        // assert
        verify(parkingSpotDAO, times(1)).updateParking(eq(parkingSpot));
        verify(ticketDAO, times(1)).saveTicket(any());
    }
   */
    
  
    
    @Captor ArgumentCaptor<ParkingSpot> parkingSpotCaptor;

    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // arrange
        // Initialize the ParkingService instance with the mock objects
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Mock the behaviour of inputReaderUtil's methods
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_REG_NUMBER);
        when(inputReaderUtil.readSelection()).thenReturn(1); // or any other integer value

        // Mock the behaviour of DAO methods
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); // or any other integer value
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any())).thenReturn(true);

        // Create a parkingSpot with the expected slot number and type, and available set to false
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Create a captor to capture the argument passed to updateParking()
        ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);

        // act
        parkingService.processIncomingVehicle();

        // assert
        verify(parkingSpotDAO, times(1)).updateParking(parkingSpotCaptor.capture());
        verify(ticketDAO, times(1)).saveTicket(any());

        // Check if the captured argument is equal to the expected parkingSpot
        ParkingSpot capturedParkingSpot = parkingSpotCaptor.getValue();
        assertEquals(parkingSpot.getId(), capturedParkingSpot.getId());
        assertEquals(parkingSpot.getParkingType(), capturedParkingSpot.getParkingType());
        assertEquals(parkingSpot.isAvailable(), capturedParkingSpot.isAvailable());

        // Print the values of the two objects to compare them
        System.out.println("Expected parking spot: " + parkingSpot.toString());
        System.out.println("Captured parking spot: " + capturedParkingSpot.toString());
    }
    

    @Test
    public void processExitingVehicleTestUnableUpdate() {
        // arrange
        String vehicleRegNumber = "ABC123";
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(10.0);
        ticket.setOutTime(new Date());

        // stub the methods of the mocks
        
    

        // act
        parkingService.processExitingVehicle();

        // assert
        assertEquals(10.0, ticket.getPrice()); // vérifier si le prix du ticket n'a pas changé
    }
    
    
    @Test
    public void testGetNextParkingNumberIfAvailable() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // simulates user selecting CAR
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertEquals(1, parkingSpot.getId());
        assertEquals(true, parkingSpot.isAvailable());
    }
    
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(1); // simulates user selecting CAR
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); // indicates no spot available

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertNull(parkingSpot);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() throws Exception {
        // arrange
        when(inputReaderUtil.readSelection()).thenReturn(3); // simulates user selecting an invalid option

        // act
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        // assert
        assertNull(parkingSpot);
    }
    
    
    
}
    
 
    


