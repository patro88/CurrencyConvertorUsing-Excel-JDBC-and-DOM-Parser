package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataAccess {
	/**
	 * @param fileName
	 * @return countryMap
	 * 
	 */	
	public Map<String, Map<Integer, Double>> excelRead(String filename){

		Connection c = null;
		Statement stmnt = null;
		Map<String, Map<Integer, Double>> countryMap = new LinkedHashMap<String, Map<Integer,Double>>();
		try
		{
			Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
			//using DSN-less connection
			c = DriverManager.getConnection( "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)}"
					+ ";DBQ="+ filename);

			stmnt = c.createStatement();
			String query = "select * from [Table$];";
			ResultSet rs = stmnt.executeQuery( query );

			//list of all the years 
			List<Integer> givenYear = new ArrayList<Integer>(); 
			while( rs.next() )
			{				
				//for the first row containing years 
				if(rs.getRow()==4){
					for(int i=2;i<=rs.getMetaData().getColumnCount();i++){
						givenYear.add(rs.getInt(i));						
					}					
				}
				// for country wise rows 
				else if(rs.getRow()>4 && rs.getRow() < 46){					
					Map<Integer, Double> exchangeMap = new LinkedHashMap<Integer, Double>();
					for(int i=2;i<=rs.getMetaData().getColumnCount();i++){
						Integer key = givenYear.get(i-2); 
						exchangeMap.put(key, rs.getDouble(i));
					}
					countryMap.put(rs.getString(1), exchangeMap);					
				}
			}						
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.err.println( "Exception in reading from file :  " + e.getStackTrace() );
		}
		finally
		{
			try
			{
				stmnt.close();
				c.close();
			}
			catch( Exception e )
			{
				System.err.println( "Error while closing the statement :" + e );
			}
		}
		return countryMap;
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @return currencyCode
	 */
	public Map<String, String> parseCSV(String fileName){
		
		Map<String, String> currencyCode = new HashMap<String, String>();
		//read the csv file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String  thisLine = null;
			while((thisLine = br.readLine()) != null){
				String[] currencyInfo = thisLine.split("\\|");
				currencyCode.put(currencyInfo[0], currencyInfo[1]);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Exception in reading file : " + e);
		} finally{
			try {
				br.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		return currencyCode;
	}
}
