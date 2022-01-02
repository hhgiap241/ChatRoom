package vn.edu.hcmus.student.sv19127640.chatroom.server;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.server
 * Created by ADMIN
 * Date 1/2/2022 - 10:32 AM
 * Description: ...
 */
class ServerService {
    private Socket socket;
    private JTextPane textPane;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ArrayList<Socket> userList;

    public ServerService(JTextPane textPane, Socket socket, ArrayList<Socket> userList) throws IOException {
        this.socket = socket;
        this.userList = userList;
        this.textPane = textPane;

        receive();
    }

    public void receive() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        dataInputStream = new DataInputStream(socket.getInputStream());
                        String line = dataInputStream.readUTF();
                        if (line.contains("exit")) {
                            userList.remove(socket);
                            textPane.setText(textPane.getText() + "\n" + "Disconnected to client " + socket);
                            close();
                            continue;
                        }
                        if (line != null) {
                            for (Socket item : userList) {
                                if (item.getPort() != socket.getPort()) {
                                    dataOutputStream = new DataOutputStream(item.getOutputStream());
                                    dataOutputStream.writeUTF(line);
                                }
                            }
                            textPane.setText(textPane.getText() + "\n" + line);
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

    public void send(String message) {
        Thread thread = new Thread() {
            public void run() {
                String current = textPane.getText();
                textPane.setText(current + "\nSent: " + message);
                try {
                    for (Socket item: userList){
                        dataOutputStream = new DataOutputStream(item.getOutputStream());
                        dataOutputStream.writeUTF("Server: " + message);
                        dataOutputStream.flush();
                    }
                } catch (IOException e) {
                    try {
                        close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}