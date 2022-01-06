package vn.edu.hcmus.student.sv19127640.chatroom.client;


import vn.edu.hcmus.student.sv19127640.chatroom.auth.LoginScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.client
 * Created by ADMIN
 * Date 1/2/2022 - 10:30 AM
 * Description: ...
 */


public class ClientSide extends JFrame implements ActionListener, ItemListener, WindowListener {
    private JLabel header;
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLable;
    private JTextField portField;
    private JButton connectBtn;
    private JButton logoutBtn;
    private JTextPane msgtextPane;
    private JScrollPane scrollPane;
    private JLabel messageLabel;
    private JTextArea inputMsg;
    private String username;
    private JButton sendBtn;
    private JButton sendFileBtn;
    private Socket socket;
    private String host;
    private String port;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ArrayList<String> chattingList;
    private JLabel onlineUserLabel;
    private JComboBox<String> onlineUserList;
    private HashMap<String, JTextPane> messagePaneMap;

    class receiveMessage implements Runnable {
        private DataInputStream dataInputStream;

        public receiveMessage(DataInputStream dataInputStream) {
            this.dataInputStream = dataInputStream;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String signal = this.dataInputStream.readUTF();
                    if (signal.equals("!publicmessage")) {
                        String sender = this.dataInputStream.readUTF();
                        String content = this.dataInputStream.readUTF();
                        msgtextPane = messagePaneMap.get("All");
                        if (!sender.equals(username)) {
                            msgtextPane.setText(msgtextPane.getText() + "\n" + sender + ": " + content);
                            System.out.println(username + " receive " + content + " from " + sender);
                        }
                    } else if (signal.equals("!privatemessage")) {
                        String sender = this.dataInputStream.readUTF();
                        String content = this.dataInputStream.readUTF();
                        msgtextPane = messagePaneMap.get(sender);
                        msgtextPane.setText(msgtextPane.getText() + "\n" + sender + ": " + content);
                        System.out.println(username + " receive " + content + " from " + sender);
                    } else if (signal.equals("!updateonlineuser")) {
                        String content = this.dataInputStream.readUTF();
                        System.out.println(content);
                        String[] usernames = content.split("\\|");
                        String previousSelection = String.valueOf(onlineUserList.getSelectedItem());
                        onlineUserList.removeAllItems();
                        onlineUserList.addItem("All");
                        for (String user : usernames) {
                            if (!user.equals(username)) { // chi lay ra nhung online users khac chinh minh
                                onlineUserList.addItem(user);
                                System.out.println("added " + user);
                                if (messagePaneMap.get(user) == null) {
                                    // create new text pane
                                    JTextPane textPane = new JTextPane();
                                    textPane.setPreferredSize(new Dimension(500, 300));
                                    textPane.setEditable(false); // prevent editting
                                    messagePaneMap.put(user, textPane);
                                }
                            }
                        }
                        // set to the previous option
                        if (isExistInList(previousSelection)) {
                            onlineUserList.setSelectedItem(previousSelection);
                        } else {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "This user is currently offline! You will be redirect to Group Chat");
                            onlineUserList.setSelectedItem("All");
                        }
                    } else if (signal.equals("!privatefile")) {
                        int fileNameLength = 0;
                        try {
                            String sender = dataInputStream.readUTF();
                            fileNameLength = dataInputStream.readInt();
//                            System.out.println("file length = " + fileNameLength);
                            // If the file exists
                            if (fileNameLength > 0) {
                                // Byte array to hold name of file.
                                byte[] fileNameBytes = new byte[fileNameLength];
                                // Read from the input stream into the byte array.
                                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                                // Create the file name from the byte array.
                                String fileName = new String(fileNameBytes);
                                // Read how much data to expect for the actual content of the file.
                                int fileContentLength = dataInputStream.readInt();
                                // If the file exists.
                                if (fileContentLength > 0) {
                                    // Array to hold the file data.
                                    byte[] fileContentBytes = new byte[fileContentLength];
                                    // Read from the input stream into the fileContentBytes array.
                                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                                    // save file to folder
//                                    System.out.println("file name = "  + fileName);
                                    System.out.println(username + " receive file " + fileName + " from " + sender);
                                    File fileToDownload = new File(fileName);
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                    // Write the actual file data to the file.
                                    fileOutputStream.write(fileContentBytes);
                                    // Close the stream.
                                    fileOutputStream.close();
                                    msgtextPane = messagePaneMap.get(sender);
                                    msgtextPane.setText(msgtextPane.getText() + "\n" + sender + ": " + fileName + " (click to download)");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (signal.equals("!publicfile")) {
                        int fileNameLength = 0;
                        try {
                            String sender = dataInputStream.readUTF();
                            fileNameLength = dataInputStream.readInt();
//                            System.out.println("file length = " + fileNameLength);
                            // If the file exists
                            if (fileNameLength > 0) {
                                // Byte array to hold name of file.
                                byte[] fileNameBytes = new byte[fileNameLength];
                                // Read from the input stream into the byte array.
                                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                                // Create the file name from the byte array.
                                String fileName = new String(fileNameBytes);
                                // Read how much data to expect for the actual content of the file.
                                int fileContentLength = dataInputStream.readInt();
                                // If the file exists.
                                if (fileContentLength > 0) {
                                    // Array to hold the file data.
                                    byte[] fileContentBytes = new byte[fileContentLength];
                                    // Read from the input stream into the fileContentBytes array.
                                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                                    // save file to folder
//                                    System.out.println("file name = "  + fileName);
                                    System.out.println(username + " receive file " + fileName + " from " + sender);
                                    File fileToDownload = new File(fileName);
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                    // Write the actual file data to the file.
                                    fileOutputStream.write(fileContentBytes);
                                    // Close the stream.
                                    fileOutputStream.close();
                                    msgtextPane = messagePaneMap.get("All");
                                    if (!sender.equals(username)) {
                                        msgtextPane.setText(msgtextPane.getText() + "\n" + sender + ": " + fileName + " (click to download)");
                                        System.out.println(username + " receive file " + fileName + " from " + sender);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (signal.equals("!leave")) {
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        dispose();
                        LoginScreen loginScreen = new LoginScreen();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        this.dataInputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public ClientSide(String username, Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, String host, String port) {
        Container container = this.getContentPane();
        chattingList = new ArrayList<>();
        messagePaneMap = new HashMap<>();
        onlineUserList = new JComboBox<>();
        onlineUserList.addItem("All");
        onlineUserList.addItemListener(this);


        this.username = username;
        this.socket = socket;
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        this.host = host;
        this.port = port;
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        header = new JLabel("Hello " + username, SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(20.0f));
        header.setForeground(Color.blue);
        hostLabel = new JLabel("Host address: ");
        hostField = new JTextField(10);
        portLable = new JLabel("Port: ");

        portField = new JTextField(10);
        connectBtn = new JButton("Connected");
        logoutBtn = new JButton("Log out");


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        headerPanel.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;

        headerPanel.add(hostLabel, gbc);
        gbc.gridx = 1;
        hostField.setText(host);
        hostField.setEditable(false);
        headerPanel.add(hostField, gbc);
        gbc.gridx = 2;
        headerPanel.add(portLable, gbc);
        gbc.gridx = 3;
        portField.setText(port);
        portField.setEditable(false);
        headerPanel.add(portField, gbc);

        gbc.gridx = 4;
        connectBtn.addActionListener(this);
        connectBtn.setBackground(Color.green);
        connectBtn.setEnabled(false);
        headerPanel.add(connectBtn, gbc);
        gbc.gridx = 5;
        logoutBtn.addActionListener(this);
        headerPanel.add(logoutBtn, gbc);
        JPanel chatPanel = new JPanel();
        chatPanel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        gbc.gridy = 2;
        gbc.gridx = 0;
        onlineUserLabel = new JLabel("Choose user: ");
        headerPanel.add(onlineUserLabel, gbc);
        gbc.gridx = 1;
        headerPanel.add(onlineUserList, gbc);
        gbc.gridx = 3;
        messageLabel = new JLabel("HISTORY");
        headerPanel.add(messageLabel, gbc);

        msgtextPane = new JTextPane();
        msgtextPane.setPreferredSize(new Dimension(500, 300));
        msgtextPane.setEditable(false);
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(msgtextPane);
        scrollPane.validate();
        messagePaneMap.put("All", msgtextPane); // chat gr
        chatPanel.add(scrollPane);

        JPanel sendFilePanel = new JPanel();
        sendFilePanel.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        sendFilePanel.setLayout(new BoxLayout(sendFilePanel, BoxLayout.X_AXIS));
        sendFilePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel noticeLabel = new JLabel("Input your message here");
        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        sendFilePanel.add(noticeLabel);
        sendFilePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        sendFileBtn = new JButton("Send File");
        sendFileBtn.addActionListener(this);
        sendFilePanel.add(sendFileBtn);

        chatPanel.add(sendFilePanel);

        JPanel sendMsgPanel = new JPanel();
        sendMsgPanel.setLayout(new BoxLayout(sendMsgPanel, BoxLayout.X_AXIS));
        inputMsg = new JTextArea(5, 10);
        inputMsg.setLineWrap(true);
        inputMsg.setWrapStyleWord(true);
        inputMsg.setBorder(new LineBorder(Color.black));
        sendMsgPanel.add(inputMsg);
        sendMsgPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        sendBtn = new JButton("Send");
        sendBtn.addActionListener(this);
        sendMsgPanel.add(sendBtn);
        chatPanel.add(sendMsgPanel);

        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        container.add(mainPanel);
        this.setTitle("Chat Box");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(new Dimension(600, 500));
        this.addWindowListener(this);
        Thread receiveMessageThread = new Thread(new receiveMessage(dataInputStream));
        receiveMessageThread.start();
    }


    public void sendPrivateMsg(String message, String receiver) {
        try {
            dataOutputStream.writeUTF("!privatemessage");
            dataOutputStream.writeUTF(receiver);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            String current = msgtextPane.getText();
            msgtextPane.setText(current + "\nYou: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPublicMsg(String message) {
        try {
            dataOutputStream.writeUTF("!publicmessage");
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            String current = msgtextPane.getText();
            msgtextPane.setText(current + "\nYou: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateFile(String receiver, File file) {
        try {
            // Create an input stream into the file you want to send.
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());


            // Get the name of the file you want to send and store it in filename.
            String fileName = file.getName();
            // Convert the name of the file into an array of bytes to be sent to the server.
            byte[] fileNameBytes = fileName.getBytes();
            // Create a byte array the size of the file so don't send too little or too much data to the server.
            byte[] fileBytes = new byte[(int) file.length()];
            // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
            fileInputStream.read(fileBytes);
            dataOutputStream.writeUTF("!privatefile");
            dataOutputStream.writeUTF(receiver);
            // Send the length of the name of the file so server knows when to stop reading.
            dataOutputStream.writeInt(fileNameBytes.length);
            // Send the file name.
            dataOutputStream.write(fileNameBytes);
            // Send the length of the byte array so the server knows when to stop reading.
            dataOutputStream.writeInt(fileBytes.length);
            // Send the actual file.
            dataOutputStream.write(fileBytes);
            dataOutputStream.flush();
            msgtextPane.setText(msgtextPane.getText() + "\nYou: " + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void sendPublicFile(File file) {
        try {
            // Create an input stream into the file you want to send.
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            // Get the name of the file you want to send and store it in filename.
            String fileName = file.getName();
            // Convert the name of the file into an array of bytes to be sent to the server.
            byte[] fileNameBytes = fileName.getBytes();
            // Create a byte array the size of the file so don't send too little or too much data to the server.
            byte[] fileBytes = new byte[(int) file.length()];
            // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
            fileInputStream.read(fileBytes);
            dataOutputStream.writeUTF("!publicfile");
            // Send the length of the name of the file so server knows when to stop reading.
            dataOutputStream.writeInt(fileNameBytes.length);
            // Send the file name.
            dataOutputStream.write(fileNameBytes);
            // Send the length of the byte array so the server knows when to stop reading.
            dataOutputStream.writeInt(fileBytes.length);
            // Send the actual file.
            dataOutputStream.write(fileBytes);
            dataOutputStream.flush();
            msgtextPane.setText(msgtextPane.getText() + "\nYou: " + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendLogoutRequest(String username) {
        try {
            dataOutputStream.writeUTF("!logout");
            dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isExistInList(String username) {
        for (int i = 0; i < onlineUserList.getItemCount(); i++) {
            if (onlineUserList.getItemAt(i).equals(username))
                return true;
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendBtn) {
            String message = inputMsg.getText();
            if (message.length() == 0) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Invalid message!");
                return;
            }
            String receiver = String.valueOf(onlineUserList.getSelectedItem());
            if (receiver.equals("All")) {
                this.sendPublicMsg(message);
            } else {
                this.sendPrivateMsg(message, receiver);
            }

            this.inputMsg.setText(""); // clear message after send
            System.out.println(username + " Sent " + message + " to " + receiver);
        } else if (e.getSource() == sendFileBtn) {

            // Create a file chooser to open the dialog to choose a file.
            JFileChooser jFileChooser = new JFileChooser();
            // Set the title of the dialog.
            jFileChooser.setDialogTitle("Choose a file to send.");
            // Show the dialog and if a file is chosen from the file chooser execute the following statements.
            if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // Get the selected file.
                File fileToSend = jFileChooser.getSelectedFile();
                String receiver = String.valueOf(onlineUserList.getSelectedItem());
                if (receiver.equals("All")) {
                    this.sendPublicFile(fileToSend);
                } else {
                    this.sendPrivateFile(receiver, fileToSend);
                }
            }
        } else if (e.getSource() == logoutBtn) {
            this.sendLogoutRequest(username);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String receiver = String.valueOf(onlineUserList.getSelectedItem());
            System.out.println("clicked");
            System.out.println(receiver);
            msgtextPane = messagePaneMap.get(receiver); // assign to the right one
            System.out.println("change to " + receiver);
            scrollPane.setViewportView(msgtextPane);
            scrollPane.validate();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.sendLogoutRequest(username);
//        System.out.println("Window closing");
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}
