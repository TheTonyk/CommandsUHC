package com.thetonyk.UHC.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;

public class DatabaseUtils {

	private static final String USER = Main.uhc.getConfig().getString("SQLuser");
	private static final String PASS = Main.uhc.getConfig().getString("SQLpass");

    private static Connection connection;
	private static Map<String, List<Map<String, Object>>> cache = new HashMap<>();
    
    private static Connection getConnection() throws SQLException {
    	
    	if (DatabaseUtils.connection != null) {

    		if (DatabaseUtils.connection.isValid(1)) return DatabaseUtils.connection;

			DatabaseUtils.connection.close();

		}

		DatabaseUtils.connection = DriverManager.getConnection("jdbc:mysql://localhost/commandspvp", USER, PASS);
    	return DatabaseUtils.connection;
    	
    }

    private static List<Map<String, Object>> getCache(String table) {

		if (!DatabaseUtils.cache.containsKey(table) || DatabaseUtils.cache.get(table) == null || DatabaseUtils.cache.get(table).isEmpty()) return null;

		return DatabaseUtils.cache.get(table);

	}

	public static List<Map<String, Object>> sqlQuery (String table, Map<String, String> where) {

		List<Map<String, Object>> result = getCache(table);

		if (result == null) {

			result = new ArrayList<>();

			try {

				String query = "SELECT * FROM " + table + ";";
				ResultSet resultSet = DatabaseUtils.getConnection().createStatement().executeQuery(query);
				String[] labels = new String[resultSet.getMetaData().getColumnCount()];

				for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

					labels[i] = resultSet.getMetaData().getColumnLabel(i + 1);

				}

				while (resultSet.next()) {

					Map<String, Object> row = new HashMap<>();

					for (String label : labels) {

						row.put(label, resultSet.getObject(label));

					}

					result.add(row);

				}

				resultSet.close();

			} catch (SQLException exception) {

				exception.printStackTrace();
				return null;

			}

			DatabaseUtils.cache.put(table, result);

		}

		List<Map<String, Object>> whereResult = new ArrayList<>();

		for (Map<String, Object> row : result) {

			Boolean valid = true;

			for (Map.Entry<String, Object> entry : row.entrySet()) {

				if (!where.containsKey(entry.getKey())) continue;

				if (where.get(entry.getKey()).equalsIgnoreCase(entry.getValue().toString())) continue;

				valid = false;
				break;

			}

			if (!valid) continue;

			whereResult.add(row);

		}

		return whereResult;

	}
    
    public static ResultSet sqlQuery (String request) {
    	
    	ResultSet result;
    	
    	try {
    		
    		result = DatabaseUtils.getConnection().createStatement().executeQuery(request);
    		
    	} catch (SQLException exception) {
    		
    		exception.printStackTrace();
    		return null;
    		
    	}
    	
    	return result;
    	
    }
    
    public static void sqlInsert (String request) {
    	
    	new BukkitRunnable() {
    		
    		public void run() {
    			
		    	try {
		    		
		    		DatabaseUtils.getConnection().createStatement().executeUpdate(request);
		    		
		    	} catch (SQLException exception) {
		    		
		    		return;
		    		
		    	}
		    	
    		}
    	
    	}.runTaskAsynchronously(Main.uhc);
    	
    }

}


