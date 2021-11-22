package Client;

import java.net.*;
import java.io.*;
import java.util.*;

// Handles get commands from an ftpclient

public class ftpserver extends Thread {

    ftpClient my_client;

    public ftpserver(ftpClient client){
        my_client = client;
    }

    public void run(){
        try{
            ServerSocket serverSocket = new ServerSocket(my_client.connPort);
            System.out.println(my_client.connPort);
            while(true){
                server_handler ftp_server_handler = new server_handler(serverSocket.accept());
                System.out.println("Client has connected");
                Thread clientHandler = new Thread(ftp_server_handler);
                clientHandler.start();
            }

        }catch(IOException e){
            System.err.println("Could not listen on port: 1200." + e.getMessage());
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
            DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
            while(true){
                String commandFromClient = inFromClient.readUTF();

                System.out.println(commandFromClient);

                if(commandFromClient.equals("close")){
                    close_peer_connection();
                }else{
                    StringTokenizer tokens = new StringTokenizer(commandFromClient);
                    int port = Integer.parseInt(tokens.nextToken());
                    String cmd = tokens.nextToken();

                    if(cmd.equals("retr")){
                        String filename = tokens.nextToken();
                        System.out.println(String.valueOf(port) + " " + filename);
                        retr_file(port,filename);
                    }else{
                        System.out.println("Error: "+ commandFromClient);
                    }
                }



            }
        }

        private void retr_file(int port, String filename){
            try{
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataOutputStream  outData = new DataOutputStream(dataSocket.getOutputStream());
                File file = new File(filename);

                File dir = new File(System.getProperty("user.dir"));
                String[] files = dir.list();
                for(String f : files){
                    System.out.println("Filename: "+f);
                }


                if (file.exists()) {
                    outData.writeUTF("200");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while(true){
                        if((line = reader.readLine()) == null){
                            outData.writeUTF("eof");
                            break;
                        }else{
                            outData.writeUTF(line);
                        }
                    }
                    reader.close();
                }else{
                    outData.writeUTF("550");
                }
                dataSocket.close();
            }catch(Exception e){

            }
        }

        private void close_peer_connection(){
            try{
                connectionSocket.close();
            }catch(Exception e){
                System.out.print("Error when closing " + e.getMessage());
            }finally{
                System.out.print("Closed connection with client");
            }
        }

    }
}