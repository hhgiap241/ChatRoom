package vn.edu.hcmus.student.sv19127640.chatroom.client;


import vn.edu.hcmus.student.sv19127640.chatroom.auth.LoginScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.client
 * Created by ADMIN
 * Date 1/2/2022 - 10:30 AM
 * Description: ...
 */


public class ClientSide extends JFrame implements ActionListener, ItemListener {
    private JLabel header;
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLable;
    private JTextField portField;
    private JButton connectBtn;
    private JButton logoutBtn;
    private JTextPane msgtextPane;
    private JScrollPane scrollPane;
//    private JButton endChatBtn;
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
//                        System.out.println(signal);
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
//                        messagePaneMap.clear();

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
                        if (isExistInList(previousSelection)){
                            onlineUserList.setSelectedItem(previousSelection);
                        } else{
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "This user is currently offline! You will be redirect to Group Chat");
                            onlineUserList.setSelectedItem("All");
                        }
                    }else if (signal.equals("!leave")){
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
//        setUPGUI();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel headerPanel = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        header = new JLabel("Hello " + username, SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(20.0f));
        header.setForeground(Color.blue);
//        nameLabel = new JLabel("Input your name: ");
//        nameText = new JTextField(10);
        hostLabel = new JLabel("Host address: ");
        hostField = new JTextField(10);
        portLable = new JLabel("Port: ");

        portField = new JTextField(10);
        connectBtn = new JButton("Connected");
        logoutBtn = new JButton("Log out");
//        msgtextPane = new JTextPane();
//        msgtextPane.setPreferredSize(new Dimension(500,300));
//        infotextPane = new JTextPane();
//        infotextPane.setPreferredSize(new Dimension(500,300));
//        JScrollPane scrollPaneInfo = new JScrollPane(infotextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        messageLabel = new JLabel("Message: ");
//        inputMsg = new JTextArea(3, 70);
//        sendBtn = new JButton("Send");
//        sendFileBtn = new JButton("Send File");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        headerPanel.add(header, gbc);

        gbc.gridwidth = 1;
//        gbc.gridy = 1;
//        headerPanel.add(nameLabel, gbc);
//        gbc.gridwidth = 4;
//        gbc.gridx = 1;
//        headerPanel.add(nameText, gbc);

//        gbc.gridwidth = 1;
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
//        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        chatPanel.add(messageLabel);
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

        JLabel noticeLabel = new JLabel("Input your message here");
        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        sendFilePanel.add(noticeLabel);
        sendFilePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        sendFileBtn = new JButton("Send File");
        sendFilePanel.add(sendFileBtn);

//        endChatBtn = new JButton("End Chat");
//        endChatBtn.addActionListener(this);
//        endChatBtn.setBackground(Color.red);
//        sendFilePanel.add(Box.createRigidArea(new Dimension(210, 0)));
//        sendFilePanel.add(endChatBtn);

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


//        tabbedPane = new JTabbedPane();
//        tabbedPane.addTab("Setting", null, panel1, "click to show setting");
//        tabbedPane.addTab("Chat", null, chatPanel, "click to show chat");
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        container.add(mainPanel);
        this.setTitle("Chat Box");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(new Dimension(600, 500));
        Thread receiveMessageThread = new Thread(new receiveMessage(dataInputStream));
        receiveMessageThread.start();
    }

    /**
     * check if current user is chatting with specific user or not
     *
     * @param username String
     * @return boolean
     */
    public boolean isChattingWith(String username) {
        for (int i = 0; i < chattingList.size(); i++) {
            if (chattingList.get(i).equals(username))
                return true;
        }
        return false;
    }

    public void sendPrivate(String message, String receiver) {
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

    public void sendPublic(String message) {
        try {
            dataOutputStream.writeUTF("!publicmessage");
//            dataOutputStream.writeUTF(receiver);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            String current = msgtextPane.getText();
            msgtextPane.setText(current + "\nYou: " + message);
        } catch (IOException e) {
            e.printStackTrace();
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
    public void sendLeaveChatRequest(String username){
        try {
            dataOutputStream.writeUTF("!exitchat");
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
                this.sendPublic(message);
            } else {
                this.sendPrivate(message, receiver);
            }

            this.inputMsg.setText(""); // clear message after send
            System.out.println(username + " Sent " + message + " to " + receiver);
        } else if (e.getSource() == logoutBtn){
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
//            System.out.println(messagePaneMap.get(receiver));
            // check if current text pane is chatting with this user or not
//            if (msgtextPane != messagePaneMap.get(receiver)){
//                msgtextPane = messagePaneMap.get(receiver); // assign to the right one
//                System.out.println("change to " + receiver);
////                inputMsg.setText(""); // clear message
//                scrollPane.setViewportView(msgtextPane);
//                scrollPane.validate();
//            }
        }
    }


}
