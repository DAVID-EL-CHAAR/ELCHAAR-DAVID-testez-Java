package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

    @InjectMocks
    private TicketDAO ticketDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private Ticket ticket;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataBaseConfig.getConnection()).thenReturn(connection);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(0.0);
        ticket.setInTime(new Timestamp(System.currentTimeMillis()));
        ticket.setOutTime(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void saveTicketTest() throws Exception {
        // Arrange
        Ticket ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(10.0);
        ticket.setInTime(new Timestamp(System.currentTimeMillis()));
        ticket.setOutTime(new Timestamp(System.currentTimeMillis()));

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatement);

        // Act
        boolean result = ticketDAO.saveTicket(ticket);

        // Assert
        verify(preparedStatement).setInt(1, parkingSpot.getId());
        verify(preparedStatement).setString(2, ticket.getVehicleRegNumber());
        verify(preparedStatement).setDouble(3, ticket.getPrice());
        verify(preparedStatement).setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
        verify(preparedStatement).setTimestamp(5, new Timestamp(ticket.getOutTime().getTime()));
        assertTrue(result); 
    }

    @Test
    void getTicketTest() throws Exception {
        when(connection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        Ticket result = ticketDAO.getTicket("ABC123");

        assertNotNull(result, "L'objet du ticket ne doit pas être nul");
    }

    @Test
    void updateTicketTest() throws Exception {
        when(connection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);

        boolean result = ticketDAO.updateTicket(ticket);
        assertTrue(result, "Le ticket de mise à jour doit renvoyer vrai");
    }
    

    @Test
    void getNbTicketTest() throws Exception {
        when(connection.prepareStatement(DBConstants.GET_NB_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("nbTicket")).thenReturn(2);

        int result = ticketDAO.getNbTicket("ABC123");
        assertEquals(2, result, "Le nombre de billets doit être de 2");
    }

    @Test
    void getNumberOfTicketsTest() throws Exception {
        when(connection.prepareStatement(DBConstants.GET_NB_TICKET)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);

        int result = ticketDAO.getNumberOfTickets("ABC123");
        assertEquals(2, result, "Le nombre de billets doit être de 2");
    }
}
