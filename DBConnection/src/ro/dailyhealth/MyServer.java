package ro.dailyhealth;

import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;


public class MyServer {
	static ArrayList<String> notifications = null;
	
	public static LinkedList<ClientThread> clients = null;
	
	public static void main(String[] args) throws IOException {
		int clientId = 0;
		
		notifications = new ArrayList<String>();
		
		clients = new LinkedList<ClientThread>();
		
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }
        
        if (DbConnection.con == null) {
        	try {
				DbConnection.openDBConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        System.out.println("step1.");
        Socket clientSocket = null;
        
        
        while (true) {
	        	System.out.println("listening...");
	            clientSocket = serverSocket.accept();
	            clientId++;
	            System.out.println("client " + clientId + " connected");
	            
	            // handle Client connection in separate Thread
	            ClientThread t = new ClientThread(clientSocket, clientId);
	            t.start();
	            
        }
        
    } //end main

}