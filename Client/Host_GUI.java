package Client;

// Create GUI according to project spec:
// Connection Fields: Server Hostname, Port, Username, Hostname, Speed

// Search Fields: Keyword, Connection points

// FTP Command Line Fields: Command Line, Output Log

import javax.swing.*;
// import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;
// import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.*;

public class Host_GUI extends JFrame{

   private ftpClient client;

    private final DimensionUIResource scrn_size = new DimensionUIResource(675,700);

    final private JLabel lb_server_hostname = new JLabel(){{
        setText("Server Hostname:");
    }};

    final private JLabel lb_connection_port = new JLabel(){{
        setText("Port:");
    }};

    final private JLabel lb_username = new JLabel(){{
        setText("Username:");
    }};

    final private JLabel lb_hostname = new JLabel(){{
        setText("Hostname:");
    }};

    final private JLabel lb_speed = new JLabel(){{
        setText("Speed:");
    }};

    final private JLabel lb_keyword = new JLabel(){{
        setText("Keyword:");
    }};

    final private JLabel lb_cmd = new JLabel(){{
        setText("Enter Command: ");
    }};

    final private JTextField tb_server_host = new JTextField(){{
        setPreferredSize(new DimensionUIResource(200, 25));
    }};

    final private JTextField tb_port = new JTextField(){{
        setPreferredSize(new DimensionUIResource(50, 25));
    }};

    final private JTextField tb_username = new JTextField(){{
        setPreferredSize(new DimensionUIResource(100, 25));
    }};

    final private JTextField tb_hostname = new JTextField(){{
        setPreferredSize(new DimensionUIResource(150, 25));
    }};

    final private JTextField tb_keyword = new JTextField(){{
        setPreferredSize(new DimensionUIResource(200 - 10, 25));
    }};

    final private JTextField tb_command = new JTextField(){{
        setPreferredSize(new DimensionUIResource(475,25));
    }};

    final private JButton btn_connect = new JButton(){{
        setPreferredSize(new DimensionUIResource(200,25));
        setText("Connect");
    }};

    final private JButton btn_search = new JButton(){{
        setPreferredSize(new DimensionUIResource(100,25));
        setText("Search");
    }};

    final private JButton btn_go = new JButton(){{
        setPreferredSize(new DimensionUIResource(50,25));
        setText("Go");
    }};

    final private JTable file_table = new JTable(){{
        setPreferredSize(new DimensionUIResource(scrn_size.width-20,200));
    }};

    final private JTextArea output_text = new JTextArea(){{
        setPreferredSize(new DimensionUIResource(scrn_size.width -20, 200));
    }};

    final private JComboBox<String> cbx_speed = new JComboBox<String>(){{
        addItem("Ethernet");
        addItem("T1");
        addItem("T2");
    }};

    private final JPanel connection_panel = new JPanel(){{
            setBorder(BorderFactory.createTitledBorder("connection"));
            setBounds(5, 5, scrn_size.width-10, 100);

            add(lb_server_hostname);
            add(tb_server_host);

            add(lb_connection_port);
            add(tb_port);

            add(btn_connect);

            add(lb_username);
            add(tb_username);

            add(lb_hostname);
            add(tb_hostname);

            add(lb_speed);
            add(cbx_speed);
    }};

    private final JPanel search_panel = new JPanel(){{
        setBorder(BorderFactory.createTitledBorder("Search"));
        setBounds(5, 5+100, scrn_size.width-5, 275);

        add(lb_keyword);
        add(tb_keyword);

        add(btn_search);
        add(file_table);
    }};

    private final JPanel cmd_panel = new JPanel(){{
        setBorder(BorderFactory.createTitledBorder("FTP"));
        setBounds(5, 5+375, scrn_size.width-5, 275);


        add(lb_cmd);
        add(tb_command);

        add(btn_go);
        add(output_text);
    }};

    private final JPanel base_panel = new JPanel(){{
        setBounds(0, 0, scrn_size.width, scrn_size.height);
        setLayout(null);                        // abs. pos.
        add(connection_panel);
        add(search_panel);
        add(cmd_panel);
    }};

    public Host_GUI(ftpClient client){
        this.client = client;


        this.setSize(scrn_size);
        this.setTitle("Data Communication P2");
        this.setResizable(false);                   // No resizing for absolute pos.

        this.add(base_panel);

        init_handlers();
    }

    private void init_handlers(){
        // Closing window event handler
        this.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent){
                client.close_server_connection();
                System.exit(0);
            }
        });

         // Establish connection w/ centralixed server
        btn_connect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    client.connect_to_server(tb_server_host.getText(), Integer.parseInt(tb_port.getText()), tb_username.getText(), tb_hostname.getText(), cbx_speed.getSelectedItem().toString());
                }catch(Exception ex){
                    // User entered a non int port#
                }
            }
        });

        // Make a keyword serach with the centralized server
        btn_search.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println(tb_keyword.getText());
                client.keyword_search(tb_keyword.getText());
            }
        });

        btn_go.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String cmd = tb_command.getText();
                if(cmd.contains("connect")){
                    client.connect_to_peer(cmd);
                }else if(cmd.contains("retr")){
                    client.retr_from_peer(cmd);
                }else{
                    // Not valid input
                }
            }
        });
    }
}
