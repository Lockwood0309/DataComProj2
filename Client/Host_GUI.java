package Client;

// Create GUI according to project spec:
// Connection Fields: Server Hostname, Port, Username, Hostname, Speed

// Search Fields: Keyword, Connection points 

// FTP Command Line Fields: Command Line, Output Log

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class Host_GUI {

    public Host_GUI() {
        JFrame frame = new JFrame();
        frame.setSize(1240,3000);
        frame.setTitle("Project2");

        JPanel centralPanel = new JPanel();
        centralPanel.setBorder(new TitledBorder("Connection"));
        centralPanel.setLayout(new GridLayout(5,5));

        JPanel connectionPanel = new JPanel();
        connectionPanel.setBorder(new TitledBorder("Connection1"));
        connectionPanel.setLayout(new GridLayout(1,2));

        JLabel serverHostNameLabel = new JLabel("Server Hostname: ");
        JTextField serverHostNameTextField = new JTextField();
        JLabel portLabel = new JLabel("Port: ");
        JTextField portTextField = new JTextField();

        JLabel usernameLabel = new JLabel("Username: ");
        JTextField usernameTextField = new JTextField();

        JLabel hostNameLabel = new JLabel("Hostname: ");
        JTextField hostNameTextField = new JTextField();

        String[] internetSpeeds = new String[] {"Ethernet", "placeholder1", "placeholder2"};
        JLabel speedLabel = new JLabel("Speed: ");
        JComboBox<String> speedDropDownBox = new JComboBox<>(internetSpeeds);
        connectionPanel.add(serverHostNameLabel);
        connectionPanel.add(serverHostNameTextField);
        connectionPanel.add(portLabel);
        connectionPanel.add(portTextField);
        connectionPanel.add(usernameLabel);
        connectionPanel.add(usernameTextField);
        connectionPanel.add(hostNameLabel);
        connectionPanel.add(hostNameTextField);
        connectionPanel.add(speedLabel);
        connectionPanel.add(speedDropDownBox);


        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(new TitledBorder("Search"));
        searchPanel.setLayout(new GridLayout(1,3));

        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchTextField = new JTextField();
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);


        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(new TitledBorder("Table"));
        tablePanel.setLayout(new GridLayout(1,1));
        
        
        Dictionary<Integer,String> data = new Hashtable<Integer,String>();
        data.put(0,"data1");
        data.put(1,"data2");

        //String[] cols = {
        //    "ID", "data"
        //};

        //String[][] rowData = new String[data.size()][1];
        //for(int i = 0; i<data.size(); i++){
        //    rowData[i][0] = data.get(i); 
        //}
        // String[][] rowData = {
        //     for(int i = 0; i<data.size(); i++){
        //         rowData[i][0] = data.get(i); 

        //    }
        // };

        //JTable resultTable = new JTable(rowData,cols);

        JTable resultTable = new JTable();

        tablePanel.add(resultTable);

        centralPanel.add(connectionPanel);
        centralPanel.add(searchPanel);
        centralPanel.add(tablePanel);

        frame.add(centralPanel);
        frame.setVisible(true);
    }

    public static void main(String args[]){
        Host_GUI gui = new Host_GUI();
    }
}
