package vn.edu.hcmus.student.sv19127640.chatroom.client;


import vn.edu.hcmus.student.sv19127640.chatroom.auth.LoginScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.client
 * Created by ADMIN
 * Date 1/2/2022 - 10:30 AM
 * Description: ...
 */


public class ClientSide extends JFrame implements ActionListener {
    private JLabel header;
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLable;
    private JTextField portField;
    private JButton connectBtn;
    private JButton logoutBtn;
    private JTextPane msgtextPane;
    private JList userList;
    private JButton startChatBtn;
    private JButton endChatBtn;
    private JLabel messageLabel;
    private JTextArea inputMsg;
    private String username;
    //    private JLabel nameLabel;
//    private JTextField nameText;
    private JButton sendBtn;
    private JButton sendFileBtn;
    private JButton refreshListBtn;
    private JLabel userToChatLabel;
    private JTabbedPane tabbedPane;
    private Socket socket;
    private String host;
    private String port;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private ClientService clientService;

    public ClientSide(String username, Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream, String host, String port) {
        Container container = this.getContentPane();
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

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JLabel("Online users (click to chat): "), BorderLayout.PAGE_START);
        userList = new JList();
        userList.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPaneInfo = new JScrollPane(userList);
        startChatBtn = new JButton("Start Chat");
        startChatBtn.addActionListener(this);
        refreshListBtn = new JButton("Refresh Online Users List");
        refreshListBtn.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        JPanel footerContent = new JPanel();
        footerContent.setLayout(new BoxLayout(footerContent, BoxLayout.X_AXIS));
        footerContent.add(startChatBtn);
        footerContent.add(Box.createRigidArea(new Dimension(20,0)));
        footerContent.add(refreshListBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(footerContent);

//        startChatBtn.setEnabled(false);
        panel1.add(scrollPaneInfo, BorderLayout.CENTER);
        panel1.add(buttonPanel, BorderLayout.PAGE_END);


        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Setting", null, panel1, "click to show setting");
//        tabbedPane.addTab("Chat", null, chatPanel, "click to show chat");
        mainPanel.add(headerPanel, BorderLayout.PAGE_START);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        container.add(mainPanel);
        this.setTitle("Chat Box");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(new Dimension(600, 500));
        // update online users list for new user
        try {
            if (dataInputStream.readUTF().equals("!updateonlineuser")) {
                String[] content = dataInputStream.readUTF().split("\\|");
                Vector<String> onlineUsers = new Vector<>();
                for (int i = 0; i < content.length; i++) {
                    if (!content[i].equals(username))
                        onlineUsers.add(content[i]);
                }
                userList.setListData(onlineUsers);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startChatBtn) {
            if (userList.getSelectedIndex() == -1){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Error: Please choose one user to chat!");
                return;
            }
            String selectedUser = (String) userList.getSelectedValue();
            tabbedPane.addTab(selectedUser, null, createNewTab());
            try {
                clientService = new ClientService(username, socket, msgtextPane, sendFileBtn, sendBtn, inputMsg, messageLabel, endChatBtn, tabbedPane, userList);
//                msgtextPane.setText(msgtextPane.getText() + "\nConnected to server");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == logoutBtn) {
            JComponent comp = (JComponent) e.getSource();
            Window win = SwingUtilities.getWindowAncestor(comp);
            win.dispose();
            LoginScreen loginScreen = new LoginScreen();
        } else if (e.getSource() == refreshListBtn){
            try {
                if (dataInputStream.readUTF().equals("!updateonlineuser")){
                    String[] content = dataInputStream.readUTF().split("\\|");
                    Vector<String> onlineUsers = new Vector<>();
                    for (int i = 0; i < content.length; i++){
                        if (!content[i].equals(username))
                            onlineUsers.add(content[i]);
                    }
                    userList.setListData(onlineUsers);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public JPanel createNewTab() {
        JPanel chatPanel = new JPanel();
        chatPanel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        messageLabel = new JLabel("Message: ");

        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatPanel.add(messageLabel);
        msgtextPane = new JTextPane();
        msgtextPane.setPreferredSize(new Dimension(500, 300));
        JScrollPane scrollPaneMsg = new JScrollPane(msgtextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(scrollPaneMsg);

        JPanel sendFilePanel = new JPanel();
        sendFilePanel.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        sendFilePanel.setLayout(new BoxLayout(sendFilePanel, BoxLayout.X_AXIS));

        JLabel noticeLabel = new JLabel("Input your message here");
        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        sendFilePanel.add(noticeLabel);
        sendFilePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        sendFileBtn = new JButton("Send File");
        sendFilePanel.add(sendFileBtn);

        endChatBtn = new JButton("End Chat");
        endChatBtn.addActionListener(this);
        endChatBtn.setBackground(Color.red);
        sendFilePanel.add(Box.createRigidArea(new Dimension(210, 0)));
        sendFilePanel.add(endChatBtn);

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
        sendMsgPanel.add(sendBtn);


        chatPanel.add(sendMsgPanel);
        return chatPanel;
    }

}
