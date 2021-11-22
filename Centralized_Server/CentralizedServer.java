package Centralized_Server;

import java.net.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.*;
import java.util.concurrent.locks.*;

import javax.management.loading.PrivateClassLoader;
import javax.sound.sampled.Port;

import java.io.*;

// Centralized Server Specs:
// Maintain a table for user, hostname, connection speed (I think we should use dictionaries for this => Dictionary of Type (ID,List))
// Maintain a table for user's files & descriptors (I think we should use dictionaries for this => Dictionary of Type (ID,List))

// Commands
// Connect: Establish connection w/ host. Once init connection has been made, tell host ready to recieve filenames & descriptors 
// KeywordSearch: Search the file table for the keyword. Return the user, hostname, & connection speed to host (Returns the all locations of the file/descriptor)

public class CentralizedServer{
    private Dictionary<Integer,ArrayList<String>> creds = new Hashtable<>();
    private Dictionary<Integer,ArrayList<String>> file_info = new Hashtable<>();

    private Lock creds_lock = new ReentrantLock();
    private Lock file_info_lock = new ReentrantLock();

    int client_count = 0;

    // Wait for new connection. Create new thread to handle connection once established
    private void newConnection(Integer ID, ServerSocket serverSocket) throws Exception{
        host_handler new_host_handler = new host_handler(ID,serverSocket.accept());
        client_count += 1;
        System.out.println("connected");
        Thread hostHandler = new Thread(new_host_handler);
        hostHandler.start();
    }

    // Main Centralized server loop. Creates a new connection by calling newConnection and then increments the ID counter after a connection has been made
    public void start_server(){
        try{
            Integer ID = 0;
            ServerSocket serverSocket = new ServerSocket(1200);

            while(true){
                newConnection(ID,serverSocket);
                ID += 1;
            }

        }catch(Exception e){
            System.err.println("Could not listen on port: 1200. Error: " + e.getMessage());
            System.exit(-1);
        }
    }

    // Start Centralized Server
    public static void main(String[] args){
        CentralizedServer main_server = new CentralizedServer();
        main_server.start_server();
    }

    // Inner Thread class to handle diff host connections while still being able to access global shared vars in the outer class
    public class host_handler extends Thread{
        int port;
        Integer my_ID; // The hosts id associate w/ the connection
        
        Socket connSocket;
        DataOutputStream outToHost;
        DataInputStream inFromHost;


        host_handler(Integer ID, Socket connSocket){
            try{
                this.my_ID = ID;
                this.connSocket = connSocket;
                outToHost = new DataOutputStream(connSocket.getOutputStream());
                inFromHost = new DataInputStream(connSocket.getInputStream());
            }catch(Exception e){
                System.err.println("Could not connect wih " + ID.toString() + ": " + connSocket.getInetAddress().toString() + ". Error: " + e.getMessage());

            }
        }

        // Establish Connection with host, let host known connection has been made, retireve filenames and descriptor,
        // wait for commands (keyword, close Connection) from host
        public void run(){
            try{

                set_host_creds(get_host_creds());
                set_host_file_info(get_host_file_info());
                host_sends_command();
            }catch(Exception e){
                System.err.println("Could not get the host credentials. Error: " + e.getMessage());
                // Error has occured and the thread will now end with the host. The output statment has been handled by the function that threw it.
            }
        }

        // Gets the host creds and returns them as an ArrayList
        private ArrayList<String> get_host_creds() throws Exception{
            System.out.println("host_creds");
            ArrayList<String> host_cred = new ArrayList<String>(){{
            try{
                outToHost.writeUTF(String.valueOf(client_count));

                String line = " ";
                System.out.println("Line: " + line);
                StringTokenizer tokens = new StringTokenizer(line = inFromHost.readUTF());
                System.out.println("Line: " + line);
                add(tokens.nextToken()); // username
                add(tokens.nextToken()); // hostname
                add(tokens.nextToken()); // connection speed
                add(tokens.nextToken()); // port
                System.out.println("Line: " + line);
            }catch(Exception e){
                System.err.println("Could not get the host credentials. Error: " + e.getMessage());
                throw new Exception();
            }
            };};


            System.out.println(host_cred.get(0));

            System.out.println(host_cred.get(1));

            System.out.println(host_cred.get(2));

            return host_cred;
        }

        // Accesses gloabl outer: lock cred var to insure no data collusion
        private void set_host_creds(ArrayList<String> cred){
            creds_lock.lock();
            creds.put(my_ID, cred);
            creds_lock.unlock();
        }

        // Gets the host file info and returns them as an ArrayList
        private ArrayList<String> get_host_file_info() throws Exception{
            ArrayList<String> host_file_info = new ArrayList<String>(){{
                try {
                    String data;
                    while(!(data = inFromHost.readUTF()).equals("eof") ){
                        add(data);
                    }
                } catch (Exception e) {
                    System.err.println("Could not get the hosts file info. Error: " + e.getMessage());
                    throw new Exception();
                }
            };};
            
            for(int i=0; i<host_file_info.size(); i++){
                System.out.println(host_file_info.get(i));
            }

            return host_file_info;
        }

        private void set_host_file_info(ArrayList<String> filenames) throws Exception{
            file_info_lock.lock();
            file_info.put(my_ID, filenames);
            file_info_lock.unlock();
        }

        private void host_sends_command() throws Exception{
            try{
                while(true){
                    String line;
                    if(!(line = inFromHost.readUTF()).equals("eof")){
                        String cmd;
                        StringTokenizer tokens = new StringTokenizer(line);
                        cmd = tokens.nextToken();
                        //System.out.println("cmd: "+cmd);
                        if(cmd.equals("close")){
                            close_connection();
                        }else if(cmd.equals("keyword")){                                          // cmd must equal keyword search (Check for input error on Client end)
                            port = Integer.parseInt(tokens.nextToken());
                            keyword_search(tokens.nextToken());
                        }else{
                            System.out.println("wtf");
                        }
                    }
                }
            }catch(Exception e){
                System.err.println("Could resolve host command. Error: " + e.getMessage());
                throw new Exception();
            }
        }

        // Searches the outer classes file_info dictionary for any file_info that matches keyword
        private void keyword_search(String keyword) throws Exception{
            print_tables();
            try {
                Socket dataSocket = new Socket(connSocket.getInetAddress(), port);
                DataOutputStream outData = new DataOutputStream(dataSocket.getOutputStream());
                
                // Search For files in dictionary => send them back to the host
                for(int i = 0; i < file_info.size(); i++){
                    for(String info : file_info.get(i)){
                        if(info.contains(keyword)){
                            // return to client hostname port filename connSpeed

                            String hostname = creds.get(i).get(1); // creds.get(i).get(1) => hostname
                            String port = creds.get(i).get(3);
                            String filename = info; // file_info.get(i).get(0) => filename
                            String connSpeed = creds.get(i).get(2); // creds.get(i).get(2) => connSpeed

                            System.out.println(hostname + " " + port + " " + filename + " " + connSpeed);

                            outData.writeUTF(hostname + " " + port + " " + filename + " " + connSpeed);
                        }
                    }
                }
                outData.writeUTF("eof");;
            } catch (Exception e) {
                // Do nothing
                System.out.println("Error " + e.getMessage());
            }
        }

        // Closes all connections with the host assciated with my_ID
        private void close_connection(){
        
        }

        private void print_tables(){
            System.out.println("wtf2");
            for(int i =0; i<creds.size(); i++){
                for(String cred : creds.get(i)){
                    System.out.println("creds["+i+"]: " + cred);
                }
            }

            for(int i =0; i<file_info.size(); i++){
                for(String info : file_info.get(i)){
                    System.out.println("fileinfo["+i+"]: " + info);
                }
            }
        }

    }
}
