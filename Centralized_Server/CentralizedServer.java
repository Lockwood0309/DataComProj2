package Centralized_Server;

import java.net.*;
import java.io.*;

// Centralized Server Specs:
// Maintain a table for user, hostname, connection speed (I think we should use dictionaries for this => Dictionary of Type (ID,List))
// Maintain a table for user's files & descriptors (I think we should use dictionaries for this => Dictionary of Type (ID,List))

// Commands
// Connect: Establish connection w/ host. Once init connection has been made, tell host ready to recieve filenames & descriptors 
// KeywordSearch: Search the file table for the keyword. Return the user, hostname, & connection speed to host (Returns the all locations of the file/descriptor)

public class CentralizedServer extends Thread {

    private Socket connSocket;

    public CentralizedServer(Socket connSocket){
        this.connSocket = connSocket;
    }

    // Establish Connection with host, let host known connection has been made, retireve filenames and descriptor,
    // wait for commands (keyword, close Connection) from host
    public void run(){
        
    }

    // Wait for new connection. Create new thread to handle connection once established
    private static void newConnection(ServerSocket serverSocket){
            CentralizedServer server = new CentralizedServer(serverSocket.accept());
            Thread clientHandler = new Thread(server);
            clientHandler.start();
    }

    // Start Centralized Server
    public static void main(String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(1200);
            
            while(true){
                newConnection(serverSocket);
            }
        }catch(IOException e){
            System.err.println("Could not listen on port: 1200.");
            System.exit(-1);
        }
}
