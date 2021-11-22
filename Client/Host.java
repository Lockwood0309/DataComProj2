package Client;

// Host encapsulates ftpClient & ftpServer along with init host GUI
// Basically will serve as a medium between the two
// If hostB machine connects to HostA to get a file, spawn a new server thread to handle this interaction so that the HostA can still perform
// keyword searches and get calls


// 1.) Set up GUI
// 2.) Spawn a client and server thread
// 3.) pass the GUI to the client
// 4.) wait for server and client threads to terminate.

public class Host {

    ftpClient host_client;
    ftpserver host_server;
    Host_GUI host_GUI;

    public void run(){
        host_client = new ftpClient(this);
        host_server = new ftpserver(host_client);

        host_GUI = new Host_GUI(host_client);
        host_client.set_GUI(host_GUI);

        host_GUI.setVisible(true);
    }

    public void start_ftpserver(){
        Thread ftp_server_thread = new Thread(host_server);
        ftp_server_thread.start();
    }
    public static void main(String args[]){
        // Init GUI, ftpClient, ftpServer (main Thread)
        // Listen for commands from GUI
        // If keyword search or connection send command to Client
        // If it's a ftp command, send it the Server
        Host host = new Host();
        host.run();
    }

}
