package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAOTest {

    private ParkingSpotDAO parkingSpotDAO;

    @Mock
    private DataBaseConfig dataBaseConfig;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    }

    @Test
    void getNextAvailableSlotTest() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(5);

        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(5, result);
    }

    @Test
    void updateParkingTest() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(result);
    }
    
    
    
    
    
    
    @Test
    void getParkingSpotTest() throws Exception {
        int number = 1;
        

        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("SELECT * FROM parking WHERE PARKING_NUMBER = ?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("TYPE")).thenReturn(ParkingType.CAR.toString());
        when(resultSet.getBoolean("AVAILABLE")).thenReturn(true);

        ParkingSpot result = parkingSpotDAO.getParkingSpot(number);
        assertNotNull(result);
        assertEquals(number, result.getId());
    }
}