package vn.edu.hcmus.student.sv19127640.chatroom.client;



import vn.edu.hcmus.student.sv19127640.chatroom.auth.LoginScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.client
 * Created by ADMIN
 * Date 1/2/2022 - 10:30 AM
 * Description: ...
 */


public class ClientSide extends JPanel implements ActionListener {
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
    private JLabel messageLabel;
    private JTextArea inputMsg;
    private JLabel nameLabel;
    private JTextField nameText;
    private JButton sendBtn;
    private JButton sendFileBtn;
    private JLabel userToChatLabel;
    private JTabbedPane tabbedPane;
    private Socket socket;
    private ClientService clientService;

    public ClientSide() {
        setUPGUI();
    }

    public void showGUI() {
        JFrame frame = new JFrame("Chat Box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClientSide clientSide = new ClientSide();
        clientSide.setOpaque(true);
        frame.setContentPane(clientSide);
        frame.pack();
        frame.setSize(new Dimension(600, 500));
        frame.setVisible(true);
    }
    public static void main(String[] args){
        ClientSide clientSide = new ClientSide();
        clientSide.showGUI();
    }
    private void setUPGUI() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        header = new JLabel("CLIENT CHAT", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(20.0f));
        header.setForeground(Color.blue);
        nameLabel = new JLabel("Input your name: ");
        nameText = new JTextField(10);
        hostLabel = new JLabel("Host address: ");
        hostField = new JTextField(10);
        portLable = new JLabel("Port: ");

        portField = new JTextField(10);
        connectBtn = new JButton("Connect");
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
        gbc.gridy = 1;
        headerPanel.add(nameLabel, gbc);
        gbc.gridwidth = 4;
        gbc.gridx = 1;
        headerPanel.add(nameText, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;

        headerPanel.add(hostLabel, gbc);
        gbc.gridx = 1;
        hostField.setText("127.0.0.1");
        headerPanel.add(hostField, gbc);
        gbc.gridx = 2;
        headerPanel.add(portLable, gbc);
        gbc.gridx = 3;
        portField.setText("3000");
        headerPanel.add(portField, gbc);

        gbc.gridx = 4;
        connectBtn.addActionListener(this);
        headerPanel.add(connectBtn, gbc);
        gbc.gridx = 5;
        logoutBtn.addActionListener(this);
        headerPanel.add(logoutBtn, gbc);

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JLabel("Online users (click to chat): "), BorderLayout.PAGE_START);
        userList = new JList(new String[]{"1", "2", "3","1", "2", "3","1", "2", "3","1", "2", "3"});
        userList.setSelectionMode(DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPaneInfo = new JScrollPane(userList);
        startChatBtn = new JButton("Start Chat");
        startChatBtn.addActionListener(this);
        panel1.add(scrollPaneInfo, BorderLayout.CENTER);
        panel1.add(startChatBtn, BorderLayout.PAGE_END);

//        JPanel chatPanel = new JPanel();
//        chatPanel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));
//        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
//        messageLabel = new JLabel("Message: ");
//
//        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        chatPanel.add(messageLabel);
//        msgtextPane = new JTextPane();
//        msgtextPane.setPreferredSize(new Dimension(500,300));
//        JScrollPane scrollPaneMsg = new JScrollPane(msgtextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        chatPanel.add(scrollPaneMsg);
//
//        JPanel sendFilePanel = new JPanel();
//        sendFilePanel.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
//        sendFilePanel.setLayout(new BoxLayout(sendFilePanel, BoxLayout.X_AXIS));
//
//        JLabel noticeLabel = new JLabel("Input your message here");
//        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
//        sendFilePanel.add(noticeLabel);
//        sendFilePanel.add(Box.createRigidArea(new Dimension(20,0)));
//
//        sendFileBtn = new JButton("Send File");
//        sendFilePanel.add(sendFileBtn);
////        sendFilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
////        chatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        chatPanel.add(sendFilePanel);
//
//        JPanel sendMsgPanel = new JPanel();
//        sendMsgPanel.setLayout(new BoxLayout(sendMsgPanel, BoxLayout.X_AXIS));
//        inputMsg = new JTextArea(3,10);
//        inputMsg.setLineWrap(true);
//        inputMsg.setWrapStyleWord(true);
//        inputMsg.setBorder(new LineBorder(Color.black));
//        sendMsgPanel.add(inputMsg);
//        sendMsgPanel.add(Box.createRigidArea(new Dimension(20,0)));
//        sendBtn = new JButton("Send");
//        sendMsgPanel.add(sendBtn);
//
//        chatPanel.add(sendMsgPanel);
//        chatPanel.setMaximumSize( chatPanel.getPreferredSize() );
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Setting", null, panel1, "click to show setting");
//        tabbedPane.addTab("Chat", null, chatPanel, "click to show chat");
        add(headerPanel, BorderLayout.PAGE_START);

        add(tabbedPane, BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectBtn) {
            try {
                nameText.setEditable(false);
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                socket = new Socket(host, port);

//                sendBtn.setEnabled(true);
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if (e.getSource() == startChatBtn){
            tabbedPane.addTab("abc", null, createNewTab());
            try {
                clientService = new ClientService(nameText.getText(), socket, msgtextPane, sendFileBtn, sendBtn, inputMsg, messageLabel);
                msgtextPane.setText(msgtextPane.getText() + "\nConnected to server");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == logoutBtn){
            JComponent comp = (JComponent) e.getSource();
            Window win = SwingUtilities.getWindowAncestor(comp);
            win.dispose();
            LoginScreen loginScreen = new LoginScreen();
        }
    }
    public JPanel createNewTab(){
        JPanel chatPanel = new JPanel();
        chatPanel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        messageLabel = new JLabel("Message: ");

        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatPanel.add(messageLabel);
        msgtextPane = new JTextPane();
        msgtextPane.setPreferredSize(new Dimension(500,300));
        JScrollPane scrollPaneMsg = new JScrollPane(msgtextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatPanel.add(scrollPaneMsg);

        JPanel sendFilePanel = new JPanel();
        sendFilePanel.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        sendFilePanel.setLayout(new BoxLayout(sendFilePanel, BoxLayout.X_AXIS));

        JLabel noticeLabel = new JLabel("Input your message here");
        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        sendFilePanel.add(noticeLabel);
        sendFilePanel.add(Box.createRigidArea(new Dimension(20,0)));

        sendFileBtn = new JButton("Send File");
        sendFilePanel.add(sendFileBtn);
//        sendFilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        chatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatPanel.add(sendFilePanel);

        JPanel sendMsgPanel = new JPanel();
        sendMsgPanel.setLayout(new BoxLayout(sendMsgPanel, BoxLayout.X_AXIS));
        inputMsg = new JTextArea(3,10);
        inputMsg.setLineWrap(true);
        inputMsg.setWrapStyleWord(true);
        inputMsg.setBorder(new LineBorder(Color.black));
        sendMsgPanel.add(inputMsg);
        sendMsgPanel.add(Box.createRigidArea(new Dimension(20,0)));
        sendBtn = new JButton("Send");
        sendMsgPanel.add(sendBtn);

        chatPanel.add(sendMsgPanel);
        return chatPanel;
    }
}
