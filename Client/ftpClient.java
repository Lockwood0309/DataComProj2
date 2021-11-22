package Client;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.sound.midi.SysexMessage;

// Handles keyword search with centralized server

// Handles:
// Connection with centralized Server
// keyword search with centralized server
// Get Command with another ftpServer


public class ftpClient {

    Socket ControlSocket;
    Socket PeerControlSocket;
    int port = 1200;

    int port1 = 1221;

    int connPort;
    String ip;

    DataOutputStream outToServer;
    DataInputStream inFromServer;

    Dictionary<Integer,ArrayList<Object>> keyword_search_table;

    Host my_host;
    Host_GUI my_GUI;

    public ftpClient(Host host){
        my_host = host;
    }

    public void set_GUI(Host_GUI GUI){
        my_GUI = GUI;
    }

    public Dictionary<Integer,ArrayList<Object>> get_file_table(){
        return keyword_search_table;
    }

    public void connect_to_server(String ip, int port, String username, String hostname, String connSpeed){
        try{
            this.ip = ip;
            ControlSocket = new Socket(ip, port);
            // Display connection to output window

            outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            inFromServer = new DataInputStream(ControlSocket.getInputStream());
            //BufferedReader inFromClient = new BufferedReader(new InputStreamReader(ControlSocket.getInputStream()));

            int client_count = Integer.parseInt(inFromServer.readUTF());

            connPort = port1 + (2*client_count);

            // Set host creds
            System.out.println(username+ " " + hostname + " " + connSpeed);
            outToServer.writeUTF(username+ " " + hostname + " " + connSpeed + " " + connPort);


            File dir = new File(System.getProperty("user.dir"));
            File[] files = dir.listFiles();

            if(files == null){
                outToServer.writeUTF("eof");
            }else{
                for(File file : files){
                    outToServer.writeUTF(file.getName());
                }
                outToServer.writeUTF("eof");
            }

            my_host.start_ftpserver();
        }catch(Exception e){
            // Display error to output window
        }
    }

    public void keyword_search(String keyword){
        try{
            //System.out.println("key " + keyword);

            port += 2;
            ServerSocket welcomeData = new ServerSocket(port);
            outToServer.writeUTF("keyword " + port + " " + keyword);

            //System.out.println("key: " + keyword);

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

            System.out.println(keyword_search_table.size());
            my_GUI.display_file_table(keyword_search_table);
            
            welcomeData.close();
        }catch(Exception e){
            // Display error to output window
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void connect_to_peer(String cmd){
        try{
            StringTokenizer tokens = new StringTokenizer(cmd);
            String my_cmd = tokens.nextToken();
            String connIP = tokens.nextToken();
            String connP = tokens.nextToken();

            System.out.println("\n" + cmd);

            System.out.println(my_cmd);
            System.out.println(connIP);
            System.out.println(connP);

            PeerControlSocket = new Socket(connIP, Integer.parseInt(connP));

        }catch(Exception e){
            System.out.println("Error p2p: " + e.getMessage());
        }    
    }

    public void retr_from_peer(String cmd){
        try{
            int tempConnPort = connPort - 1;
            ServerSocket welcomeData = new ServerSocket(tempConnPort);


            DataOutputStream P2PoutToServer = new DataOutputStream(PeerControlSocket.getOutputStream());
            DataInputStream P2PinFromServer = new DataInputStream(PeerControlSocket.getInputStream());

            
            P2PoutToServer.writeUTF(String.valueOf(tempConnPort) + " " + cmd);

            Socket dataSocket = welcomeData.accept();

            // retr file
        }catch(Exception e){
            
        }
    }

    public void close_server_connection(){

    }

}
