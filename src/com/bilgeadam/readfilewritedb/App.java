package com.bilgeadam.readfilewritedb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class App {
	
	
	private final static String user = "postgres";
	private final static String password = "root";
	private final static String url = "jdbc:postgresql://localhost/peopledb";
	
	
	
	public static void main(String[] args) {
		
	try {
		insertPeople(convertStringToPerson());
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public static Connection connect() {
		
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to PostgresQL server.");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return conn;
	}
	
	public static List<String> readFileAndCreateAStringPersonList() {
		List<String>  personStringList = new ArrayList<>();
		
	try(BufferedReader br = new BufferedReader(new FileReader("Person.csv"))){
		String line;
	while((line=br.readLine()) != null) {
		personStringList.add(line);
	}
		personStringList.remove(0);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return personStringList;
	}
	
	
	public static List<Person> convertStringToPerson() throws ParseException {
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		
	   List<String> tempList =	readFileAndCreateAStringPersonList();
	   
	   StringTokenizer st;
	   List<Person> personList = new ArrayList<>();
	   
	   for (String line : tempList) {
		Person person = new Person();
		   st = new StringTokenizer(line,",");
		   person.setId(Integer.parseInt(st.nextToken()));
		   person.setFirstName(st.nextToken());
		   person.setLastName(st.nextToken());
		   person.setEmail(st.nextToken());
		   person.setGender(st.nextToken());
		   person.setBirthday(new Timestamp(df.parse(st.nextToken()).getTime()));
		   personList.add(person);
	   }
		return personList;
	}
	public static void insertPeople(List<Person> list) {
		String sql = "Insert into persona(id,first_name,last_name,email,gender,birthday) VALUES (?,?,?,?,?,?) ";
		try(Connection conn = connect();){
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			for (Person person : list) {
				stmt.setInt(1, person.getId());
				stmt.setString(2, person.getFirstName());
				stmt.setString(3, person.getLastName());
				stmt.setString(4, person.getEmail());
				stmt.setString(5, person.getGender());
				stmt.setTimestamp(6, person.getBirthday());
				
				stmt.addBatch();			
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
