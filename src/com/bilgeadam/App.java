package com.bilgeadam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
	
	
	private final String user = "postgres";
	private final String password = "root";
	private final String url = "jdbc:postgresql://localhost/dvdrental";
	
	public Connection connect() {
		
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
	
	public int getActorCount() {
		int count = 0;
		String sql = "SELECT COUNT(*) FROM ACTOR;";
		//try with resources
		try(Connection conn = connect();
				Statement stmt =conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)		
				){
			rs.next();
			count = rs.getInt("count");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public void getActors() {
		
		String sql = "SELECT actor_id,first_name,last_name from actor;";
				try (Connection conn = connect();
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(sql)){
					displayActors(rs);
				} catch (Exception e) {
					// TODO: handle exception
				}		
	}	
	
	private void displayActors(ResultSet rs) throws SQLException {
		while(rs.next()) {
		System.out.println(rs.getString("actor_id")+"-"+"\t"
		+rs.getString("first_name")+"\t\t"
		+rs.getString("Last_name"));	;
		}
	}
	
	public void findActorById(int actorId) {
		
		String sql = "SELECT actor_id,first_name,last_name from actor WHERE actor_id = ?";
		
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				){
			pstmt.setInt(1, actorId);
			ResultSet rs =pstmt.executeQuery();
			displayActors(rs);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	public int insertActor(Actor actor) {
	int id=0;
		String sql = "insert into actor(first_name, last_name) VALUES (?,?)";
		
		try (Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
			pstmt.setString(1, actor.getFirstname());
			pstmt.setString(2, actor.getLastname());
			int affectedRows = pstmt.executeUpdate();
			
			if(affectedRows>0) {
				try(ResultSet rs =pstmt.getGeneratedKeys()){
					if(rs.next()) {
					id=rs.getInt(1);
					}	;
			}
		}
			} catch (Exception e) {
			// TODO: handle exception
		}
		return id;
	}
	
	public void insertActors(List<Actor> list) throws SQLException {
		
		String sql = "insert into actor(first_name, last_name) VALUES (?,?)";
		
		try(Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql);){
		int count = 0;
			for(Actor actor : list) {
				pstmt.setString(1, actor.getFirstname());
				pstmt.setString(2, actor.getLastname());
				
				pstmt.addBatch();
				count++;
				if(count%100 == 0 || count == list.size()) {
					pstmt.executeBatch();
				}
			}
		}	
	}
	
	public int updateLastName(int id, String lastname) {

		String sql = "UPDATE actor SET last_name = ? WHERE actor_id = ?";
		
		int affectedRows = 0;
		
		try(Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			
			pstmt.setString(1, lastname);
			pstmt.setInt(2, id);
			
			affectedRows = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return affectedRows;
	}
	
	public void deleteActor(int id) {
		String sql = "DELETE FROM actor WHERE actor_id = ?";
		
		try(Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			System.out.println(id+ "li aktör silindi.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		
		App app = new App();
	
	//	app.connect();
	//	System.out.println(app.getActorCount());
		
	//	app.getActors();
	//	app.findActorById(100);
		List<Actor> actorList = new ArrayList<>();
		Actor actor = new Actor("Cagri","Trkmen");
		Actor actor2 = new Actor("Tarık","Akan");
		Actor actor3 = new Actor("Hulya","Kcygt");
		actorList.add(actor3);
		actorList.add(actor2);
		actorList.add(actor);


	//System.out.println(app.insertActor(actor));	;
		try {
			app.insertActors(actorList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//	app.updateLastName(201, "Turkmen");
			app.deleteActor(201);
	}

}
