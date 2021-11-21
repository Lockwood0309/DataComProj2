package Client;

import java.io.*;
import java.net.*;
import java.util.*;

// Handles keyword search with centralized server

// Handles:
// Connection with centralized Server
// keyword search with centralized server
// Get Command with another ftpServer


public class ftpClient {

    Socket ControlSocket;
    int port = 1200;

    int connPort;
    String ip;

    DataOutputStream outToServer;
    DataInputStream inFromServer;

    Dictionary<Integer,ArrayList<Object>> keyword_search_table;


    public Dictionary<Integer,ArrayList<Object>> get_file_table(){
        return keyword_search_table;
    }

    public void connect_to_server(String ip, int port, String username, String hostname, String connSpeed){
        try{
            connPort = port;
            this.ip = ip;
            ControlSocket = new Socket(ip, port);
            // Display connection to output window

            outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            inFromServer = new DataInputStream(ControlSocket.getInputStream());
            //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(ControlSocket.getInputStream()));

            // Set host creds
            System.out.println(username+ " " + hostname + " " + connSpeed);
            outToServer.writeUTF(username+ " " + hostname + " " + connSpeed);


            File dir = new File(System.getProperty("user.dir"));
            String[] files = dir.list();

            if(files == null){
                outToServer.writeUTF("eof");
            }else{
                for(String file : files){
                    outToServer.writeUTF(file);
                }
                outToServer.writeUTF("eof");
            }
        }catch(Exception e){
            // Display error to output window
        }
    }

    public void keyword_search(String keyword){
        try{
            System.out.println("key " + keyword);

            port += 2;
            ServerSocket welcomeData = new ServerSocket(port);
            outToServer.writeBytes(port + " keyword " + keyword);

            System.out.println("key: " + keyword);

            Socket dataSocket = welcomeData.accept();

            DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

            keyword_search_table =  new Hashtable<Integer,ArrayList<Object>>();

            int count = 0;
            while(true){
                String sentence = inData.readUTF();
                if(sentence.equals("eof")){
                    break;
                }
                StringTokenizer tokens = new StringTokenizer(sentence);

                String hostname = tokens.nextToken();
                int port = Integer.parseInt(tokens.nextToken());
                String filename = tokens.nextToken();
                String connSpeed = tokens.nextToken();

                keyword_search_table.put(count,new ArrayList<>(){{
                    add(hostname);
                    add(port);
                    add(filename);
                    add(connSpeed);
                }});
                count++;
            }
            welcomeData.close();
        }catch(Exception e){
            // Display error to output window
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void connect_to_peer(String cmd){

    }

    public void retr_from_peer(String cmd){

    }

    public void close_server_connection(){

    }

}
