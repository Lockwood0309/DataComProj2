package Client;

import java.net.*;
import java.io.*;
import java.util.*;

// Handles get commands from an ftpclient

public class ftpserver extends Thread {

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(1200);

            while(true){
                server_handler ftp_server_handler = new server_handler(serverSocket.accept());
                Thread clientHandler = new Thread(ftp_server_handler);
                clientHandler.start();
            }

        }catch(IOException e){
            System.err.println("Could not listen on port: 1200.");
            System.exit(-1);
        }
    }

    public class server_handler extends Thread{

        int port;

        Socket connectionSocket;

        public server_handler(Socket connSocket ){
            this.connectionSocket = connSocket;
        }

        public void run(){
            try{
                processRequest();
            }catch(Exception e){
                System.out.println("Error Occured: " + e.getMessage());
            }
        }

        public void processRequest() throws Exception{  
            DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        while(true){
            String commandFromClient = inFromClient.readLine();
            StringTokenizer tokens = new StringTokenizer(commandFromClient);
            String frstln = tokens.nextToken();
            if(frstln.equals("close")){
                //clientCommand = frstln;
            }else{
                port = Integer.parseInt(frstln);
                //clientCommand = tokens.nextToken();
            }
            
        }
        }

    }
}
