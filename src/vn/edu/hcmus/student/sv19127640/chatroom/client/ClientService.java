package vn.edu.hcmus.student.sv19127640.chatroom.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.client
 * Created by ADMIN
 * Date 1/2/2022 - 10:31 AM
 * Description: ...
 */
class ClientService implements ActionListener {
    private Socket socket;
    private String username;
    private JTextPane textPane;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private File fileToSend;
    private JButton sendFileBtn;
    private JButton sendBtn;
    private JTextArea inputMsg;
    private JLabel messageLabel;
    private JButton endChatBtn;
    private JTabbedPane tabbedPane;
    private JList userList;


    public ClientService(String username, Socket socket, JTextPane msgtextPane, JButton sendFileBtn, JButton sendBtn, JTextArea inputMsg, JLabel messageLabel, JButton endChatBtn, JTabbedPane tabbedPane, JList userList) throws IOException {
        this.socket = socket;
        this.tabbedPane = tabbedPane;
        this.username = username;
        this.textPane = msgtextPane;
        this.sendBtn = sendBtn;
        sendBtn.addActionListener(this);
        this.sendFileBtn = sendFileBtn;
        sendBtn.addActionListener(this);
        this.endChatBtn = endChatBtn;
        endChatBtn.addActionListener(this);
        this.inputMsg = inputMsg;
        this.messageLabel = messageLabel;
        this.userList = userList;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        receive();
    }

    private void receive() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String signal = dataInputStream.readUTF();
                        System.out.println(signal);
                        if (signal.equals("!text")) {
                            String sender = dataInputStream.readUTF();
                            String content = dataInputStream.readUTF();
                            textPane.setText(textPane.getText() + "\n" + sender + ": " + content);
                        } else if (signal.equals("!updateonlineuser")) {
                            String[] content = dataInputStream.readUTF().split("\\|");
                            Vector<String> onlineUsers = new Vector<>();
                            for (int i = 0; i < content.length; i++){
                                if (!content[i].equals(username))
                                    onlineUsers.add(content[i]);
                            }
                            userList.removeAll(); // remove all from list
                            userList.setListData(onlineUsers);
                        }
                    } catch (Exception e) {
                        try {
                            close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    public void send(String message, String receiver) {
        String current = textPane.getText();
        textPane.setText(current + "\nYou: " + message);
        try {
            dataOutputStream.writeUTF("!text");
            dataOutputStream.writeUTF(receiver);
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File fileToSend) {
        // If a file has not yet been selected then display this message.
        if (fileToSend == null) {
            textPane.setText(textPane.getText() + "\nPlease choose a file to send first!");
            // If a file has been selected then do the following.
        } else {
            try {
                // Create an input stream into the file you want to send.
                FileInputStream fileInputStream = new FileInputStream(fileToSend.getAbsolutePath());
                // Create a socket connection to connect with the server.
//                Socket socket = new Socket("localhost", 1234);
                // Create an output stream to write to write to the server over the socket connection.
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                // Get the name of the file you want to send and store it in filename.
                String fileName = fileToSend.getName();
                // Convert the name of the file into an array of bytes to be sent to the server.
                byte[] fileNameBytes = fileName.getBytes();
                // Create a byte array the size of the file so don't send too little or too much data to the server.
                byte[] fileBytes = new byte[(int) fileToSend.length()];
                // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                fileInputStream.read(fileBytes);
                // Send the length of the name of the file so server knows when to stop reading.
                dataOutputStream.writeInt(fileNameBytes.length);
                // Send the file name.
                dataOutputStream.write(fileNameBytes);
                // Send the length of the byte array so the server knows when to stop reading.
                dataOutputStream.writeInt(fileBytes.length);
                // Send the actual file.
                dataOutputStream.write(fileBytes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendBtn) {
            textPane.setText(inputMsg.getText());
            System.out.println("Send button clicked");
        } else if (e.getSource() == sendFileBtn) {
            System.out.println("Send file clicked");
        } else if (e.getSource() == endChatBtn) {
            System.out.println(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
            tabbedPane.remove(tabbedPane.getSelectedIndex());
        }
    }
}
