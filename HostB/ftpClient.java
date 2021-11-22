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

    DataOutputStream P2PoutToServer;
    DataInputStream P2PinFromServer;

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
            my_GUI.output_text.append(">> Connect to Centralized Server"+"\n");
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
            my_GUI.output_text.append("\t Connected to Centralized Server"+"\n");
            my_host.start_ftpserver();
        }catch(Exception e){
            my_GUI.output_text.append("\t Error: " + e.getMessage());
        }
    }

    public void keyword_search(String keyword){
        try{
            //System.out.println("key " + keyword);

            port += 2;
            ServerSocket welcomeData = new ServerSocket(port);
            outToServer.writeUTF("keyword " + port + " " + keyword);

            my_GUI.output_text.append(">> Keyword " + keyword+"\n");
            my_GUI.output_text.append("\t keyword " + port + " " + keyword+"\n");
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
            my_GUI.output_text.append("\t Error: " + e.getMessage()+"\n");
        }
    }

    public void connect_to_peer(String cmd){
        try{
            StringTokenizer tokens = new StringTokenizer(cmd);
            String my_cmd = tokens.nextToken();
            String connIP = tokens.nextToken();
            String connP = tokens.nextToken();

            //System.out.println("\n" + cmd);

            System.out.println(my_cmd);
            System.out.println(connIP);
            System.out.println(connP);

            PeerControlSocket = new Socket(connIP, Integer.parseInt(connP));

            P2PoutToServer = new DataOutputStream(PeerControlSocket.getOutputStream());
            P2PinFromServer = new DataInputStream(PeerControlSocket.getInputStream());
            my_GUI.output_text.append("Connected with peer"+"\n");
        }catch(Exception e){
            my_GUI.output_text.append("\t Error: " + e.getMessage()+"\n");
        }
    }

    public void retr_from_peer(String cmd){
        try{
            int tempConnPort = connPort - 1;
            //System.out.println("before");
            ServerSocket welcomeData = new ServerSocket(tempConnPort);
            //System.out.println("after");

            P2PoutToServer.writeUTF(String.valueOf(tempConnPort) + " " + cmd);

            StringTokenizer tokens = new StringTokenizer(cmd);
            String my_cmd = tokens.nextToken();
            String my_filename = tokens.nextToken();

            Socket dataSocket = welcomeData.accept();
            //System.out.println("after2");
            File file = new File(my_filename);

            if(file.createNewFile()){
                FileWriter fileW = new FileWriter(file);
                DataInputStream inData = new DataInputStream(dataSocket.getInputStream());
                String line;
                if((line = inData.readUTF()).equals("200")){
                    while(true){
                        line = inData.readUTF();
                        if(line.equals("eof")){
                            fileW.close();
                            break;
                        }else{
                            fileW.write(line,0,line.length());
                        }
                    }
                    my_GUI.output_text.append("\t File has been retrieved"+"\n");
                }else{
                    my_GUI.output_text.append("Server Counldn't get " + my_filename + " " + line+"\n");
                    file.delete();
                }
            }
            dataSocket.close();
            welcomeData.close();
            // retr file
        }catch(Exception e){
            my_GUI.output_text.append("\t Error: " + e.getMessage()+"\n");
        }
    }

    public void close_peer_connection(){
        try{
            P2PoutToServer.writeUTF("close");
            // P2PinFromServer.close();
            // P2PoutToServer.close();
            my_GUI.output_text.append("\t Connection has been closed with peer"+"\n");
        }catch(Exception e){
            my_GUI.output_text.append("\t Error: " + e.getMessage()+"\n");
        }
    }

    // Socket ControlSocket;
    // Socket PeerControlSocket;
    // int port = 1200;

    // int port1 = 1221;

    // int connPort;
    // String ip;

    // DataOutputStream outToServer;
    // DataInputStream inFromServer;
    public void close_server_connection(){
        try{
            my_GUI.output_text.append(">> close");
            outToServer.writeUTF("close");
            my_GUI.output_text.append("\t Closing Connection with Server...");
            System.out.println("\t Closing Connection with Server...");

            if(PeerControlSocket != null && PeerControlSocket.isConnected()){
                close_peer_connection();
                PeerControlSocket.close();
            }
            outToServer.close();
            inFromServer.close();
            ControlSocket.close();
        }catch(Exception e){
            my_GUI.output_text.append("\t Error with disconection: " + e.getMessage()+"\n");
            System.out.println("\t Error with disconection: " + e.getMessage()+"\n");
        }


    }

}
