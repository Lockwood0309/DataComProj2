package Client;

// Host encapsulates ftpClient & ftpServer along with init host GUI
// Basically will serve as a medium between the two
// If hostB machine connects to HostA to get a file, spawn a new server thread to handle this interaction so that the HostA can still perform 
// keyword searches and get calls 

public class Host {

    public static void main(String args[]){
        // Init GUI, ftpClient, ftpServer (main Thread)
        // Listen for commands from GUI
        // If keyword search or connection send command to Client
        // If it's a ftp command, send it the Server
    }

}
