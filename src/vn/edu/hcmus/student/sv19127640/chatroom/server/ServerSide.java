package vn.edu.hcmus.student.sv19127640.chatroom.server;


import vn.edu.hcmus.student.sv19127640.chatroom.auth.Account;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.server
 * Created by ADMIN
 * Date 1/2/2022 - 10:32 AM
 * Description: ...
 */

public class ServerSide extends JPanel implements ActionListener {
    private JLabel header;
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLable;
    private JTextField portField;
    private JButton startBtn;
    private JButton stopBtn;
    static JTextPane textPane;
    private JLabel statusLabel;
    private JLabel statusText;
    private JLabel numOfUserLabel;
    static JLabel numOfUserText;
    private int numberOfUser;
    static ArrayList<ServerService> userList; // danh sach cac user online
    private ServerSocket serverSocket;
    Thread thread;



    public ServerSide() {
        setUPGUI();
    }

    private static void showGUI() {

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Chat Box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ServerSide serverSide = new ServerSide();
        serverSide.setOpaque(true);
        frame.setContentPane(serverSide);
        frame.pack();
        frame.setSize(new Dimension(700, 500));
        frame.setVisible(true);
    }

    private void setUPGUI() {
        this.userList = new ArrayList<>();
        numberOfUser = 0;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        header = new JLabel("SERVER MANAGEMENT");
        header.setFont(new Font("Arial", Font.BOLD, 25));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setForeground(Color.blue);
        add(header);
        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        hostLabel = new JLabel("IP Address: ");
        hostField = new JTextField(30);
        hostField.setText("127.0.0.1");
        portLable = new JLabel("Port: ");
        portField = new JTextField(30);
        portField.setText("3000");
        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(hostLabel, gbc);
        gbc.gridx = 1;
        configPanel.add(hostField, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        configPanel.add(portLable, gbc);
        gbc.gridx = 1;
        configPanel.add(portField, gbc);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        statusLabel = new JLabel("Status: ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusText = new JLabel("OFF");
        statusText.setFont(new Font("Arial", Font.BOLD, 20));
        statusText.setForeground(Color.red);
        numOfUserLabel = new JLabel("Number of Users: ");
        numOfUserLabel.setFont(new Font("Arial", Font.BOLD, 20));
        numOfUserText = new JLabel(String.valueOf(numberOfUser));
        numOfUserText.setFont(new Font("Arial", Font.BOLD, 20));
        GridBagConstraints gbc_1 = new GridBagConstraints();
        gbc_1.insets = new Insets(5, 5, 5, 5);
        gbc_1.anchor = GridBagConstraints.EAST;
        gbc_1.gridx = 0;
        gbc_1.gridy = 0;
        infoPanel.add(statusLabel, gbc_1);
        gbc_1.gridx = 1;
        infoPanel.add(statusText, gbc_1);
        gbc_1.gridy = 1;
        gbc_1.gridx = 0;
        infoPanel.add(numOfUserLabel, gbc_1);
        gbc_1.gridx = 1;
        infoPanel.add(numOfUserText, gbc_1);
//        infoPanel.setBackground(Color.darkGray);
        infoPanel.setBorder(new TitledBorder("Server Info"));

        JPanel headerPanel = new JPanel();
        Dimension dim = new Dimension(700, 200);
        headerPanel.setSize(dim);
        headerPanel.setMinimumSize(dim);
        headerPanel.setMaximumSize(dim);
        headerPanel.setPreferredSize(dim);
        headerPanel.setLayout((new BoxLayout(headerPanel, BoxLayout.X_AXIS)));
        headerPanel.add(configPanel);
        headerPanel.add(infoPanel);
        add(headerPanel);

        JPanel manualPanel = new JPanel();
        dim = new Dimension(600, 50);
        manualPanel.setSize(dim);
        manualPanel.setMinimumSize(dim);
        manualPanel.setMaximumSize(dim);
        manualPanel.setPreferredSize(dim);
        manualPanel.setLayout(new BoxLayout(manualPanel, BoxLayout.PAGE_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        startBtn = new JButton("Start Server");
        stopBtn = new JButton("Stop Server");
        buttonPanel.add(startBtn);
        startBtn.addActionListener(this);
        buttonPanel.add(Box.createRigidArea(new Dimension(30, 10)));
        buttonPanel.add(stopBtn);
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(this);

        manualPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualPanel.setBorder(new TitledBorder("Server Options"));
        manualPanel.add(buttonPanel);
        add(manualPanel);
        textPane = new JTextPane();
        textPane.setForeground(Color.black);
        JScrollPane scrollPane = new JScrollPane(textPane);
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.add(scrollPane, BorderLayout.CENTER);
        footerPanel.setBorder(new TitledBorder("User Connections"));
        add(footerPanel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                showGUI();
            }
        });
    }

    public static void removeFromUserList(String username){
        for (ServerService service : ServerSide.userList) {
            if (service.getUsername().equals(username)){
                userList.remove(service);
                textPane.setText(textPane.getText() + "\nUser " + username + " is disconnected!");
                break;
            }
        }
    }
    /**
     * update online users to another users
     */
    public static void updateOnlineUser() {
        // create a string that contains all online user and each user separated by |
        String message = "";
        for (ServerService service : ServerSide.userList) {
            message += service.getUsername() + "|";
        }

        // send this message to every online users
        for (ServerService service: ServerSide.userList){
            try {
                service.getDataOutputStream().writeUTF("!updateonlineuser");
                service.getDataOutputStream().writeUTF(message);
                service.getDataOutputStream().flush();
                textPane.setText(textPane.getText() + "\n" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            try {
                int port = Integer.parseInt(portField.getText());
                serverSocket = new ServerSocket(port);
                thread = new Thread() {
                    public void run() {
                        try {
                            hostField.setEditable(false);
                            portField.setEditable(false);
                            startBtn.setEnabled(false);
                            stopBtn.setEnabled(true);
                            textPane.setText(textPane.getText() + "\nListening on port " + port + "...");
                            statusText.setText("ON");
                            statusText.setForeground(Color.GREEN);
                            while (true) {
                                Socket socket = serverSocket.accept();
                                numOfUserText.setText(String.valueOf(numberOfUser));
                                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                textPane.setText(textPane.getText() + "\nConnected to " + socket.getPort());

                                String requestFromUser = dataInputStream.readUTF();

                                if (requestFromUser.equals("!login")) {
                                    String username = dataInputStream.readUTF();
                                    String password = dataInputStream.readUTF();
                                    Account account = new Account(username, password);
                                    // check username and password
                                    if (account.isValidAccount()) {
                                        dataOutputStream.writeUTF("!successlogin");
                                        dataOutputStream.flush();
                                        ServerService service = new ServerService(username, password, socket);
                                        userList.add(service);
                                        // update cac user online
                                        ServerSide.updateOnlineUser();
                                        numOfUserText.setText(String.valueOf(userList.size()));
                                        Thread thread = new Thread(service);
                                        thread.start();
                                    } else {
                                        dataOutputStream.writeUTF("!faillogin");
                                        dataOutputStream.flush();
                                    }
                                } else if (requestFromUser.equals("!signup")) {
                                    String username = dataInputStream.readUTF();
                                    String password1 = dataInputStream.readUTF();
                                    String password2 = dataInputStream.readUTF();
                                    Account account = new Account(username, password1);
                                    if(!account.isExistUsername()){ // kiem tra username da ton tai hay chua
                                        // kiem tra 2 password co trung nhau k
                                        if (password1.equals(password2)){
                                            // dang ky thanh cong
                                            account.saveToFile();
                                            dataOutputStream.writeUTF("!successsignup");
                                            dataOutputStream.flush();
                                        }else{
                                            // 2 password khong trung nhau
                                            dataOutputStream.writeUTF("!passdontmatch");
                                            dataOutputStream.flush();
                                        }
                                    } else {
                                        // username da ton tai trong he thong
                                        dataOutputStream.writeUTF("!existsusername");
                                        dataOutputStream.flush();
                                    }
                                }
                            }
                        } catch (IOException ex) {
                        }
                    }
                };
                thread.start();
                System.out.println("Start " + thread);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Error: " + ioException.getMessage());
                ioException.printStackTrace();
            }
        } else if (e.getSource() == stopBtn) {
            try {
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                statusText.setText("OFF");
                statusText.setForeground(Color.red);
                hostField.setEditable(true);
                portField.setEditable(true);
                textPane.setText(textPane.getText() + "\nConnection is closed!!!");
                numOfUserText.setText("0");
                serverSocket.close();
                System.out.println("Stop" + thread);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

