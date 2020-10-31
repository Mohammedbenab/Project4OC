package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	@InjectMocks
	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@Test
	public void processExitingVehicleTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}

		when(inputReaderUtil.readSelection()).thenReturn(1);

		when(parkingSpotDAO.getNextAvailableSpot(any(ParkingType.class))).thenReturn(1);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}

	@Test
	public void getNextParkingNumberIfAvailableTrueTest() {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(any(ParkingType.class))).thenReturn(1);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
				ticketDAO);

		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
		assertNotNull(parkingSpot);
		verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSpot(any(ParkingType.class));
		verify(inputReaderUtil, Mockito.times(1)).readSelection();

	}

	@Test
	public void processIncomingVehicleWhenParkingSpotNull() {

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(any(ParkingType.class))).thenReturn(0);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
				ticketDAO);
		parkingService.processIncomingVehicle();

		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));

	}

	@Test
	public void processExitingVehicleTestInValable() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
	}
}