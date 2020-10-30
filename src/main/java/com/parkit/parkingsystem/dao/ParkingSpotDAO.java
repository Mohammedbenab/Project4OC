package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public int getNextAvailableSpot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		String query = DBConstants.GET_NEXT_PARKING_SPOT;
		try {
			con = dataBaseConfig.getConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		try {
			if (query != null) {
				ps = con.prepareStatement(query);
				ps.setString(1, parkingType.toString());
				rs = ps.executeQuery();
			}
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception ex) {
			result = -1;
			logger.error("Error fetching next available spot", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return result;
	}

	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability for that parking spot
		Connection con = null;
		PreparedStatement ps = null;
		String query = null;
		int updateRowCount = 0;
		try {
			con = dataBaseConfig.getConnection();
			query = DBConstants.UPDATE_PARKING_SPOT;
			try {
				ps = con.prepareStatement(query);
				ps.setBoolean(1, parkingSpot.isAvailable());
				ps.setInt(2, parkingSpot.getId());
				updateRowCount = ps.executeUpdate();
			} catch (SQLException exp) {
				exp.printStackTrace();
			} finally {
				if (ps != null) {
					ps.close();
				}
			}
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
			try {
				if (con != null) {
					con.close();
				}

			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		}
	}

}
