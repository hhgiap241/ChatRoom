package vn.edu.hcmus.student.sv19127640.chatroom.server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.server
 * Created by ADMIN
 * Date 1/2/2022 - 10:32 AM
 * Description: provide server for every login user
 */
class ServerService implements Runnable {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String username;
    private String password;

    public ServerService(String username, String password, Socket socket) throws IOException {
        this.socket = socket;

        this.username = username;
        this.password = password;
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }


    @Override
    public void run() {
        while (true) {
            String message = null;
            try {
                message = dataInputStream.readUTF();
                if (message.equals("!privatemessage")) { // gui tin nhan dang text
                    String destinationUser = dataInputStream.readUTF(); // doc ten nguoi nhan
                    String content = dataInputStream.readUTF(); // doc noi dung tin nhan
                    // tim nguoi nhan trong danh sach online user
                    System.out.println(username + " send " + content + " to " + destinationUser);
                    for (ServerService service : ServerSide.userList) {
                        if (service.getUsername().equals(destinationUser)) {
                            // send back
                            service.getDataOutputStream().writeUTF("!privatemessage");
                            service.getDataOutputStream().writeUTF(this.username);
                            service.getDataOutputStream().writeUTF(content);
                            service.getDataOutputStream().flush();
                            break;
                        }
                    }
                } else if (message.equals("!publicmessage")) {
                    String content = dataInputStream.readUTF(); // doc noi dung tin nhan
                    // tim nguoi nhan trong danh sach online user
                    System.out.println(username + " send " + content + " to all");
                    for (ServerService service : ServerSide.userList) {
                        // send back
                        service.getDataOutputStream().writeUTF("!publicmessage");
                        service.getDataOutputStream().writeUTF(this.username);
                        service.getDataOutputStream().writeUTF(content);
                        service.getDataOutputStream().flush();
                    }
                } else if (message.equals("!privatefile")) {
                    int fileNameLength = 0;
                    try {
                        String receiver = dataInputStream.readUTF();
                        fileNameLength = dataInputStream.readInt();
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
                                System.out.println(username + " send " + fileName + " to " + receiver);
                                for (ServerService service : ServerSide.userList) {
                                    if (service.getUsername().equals(receiver)) {
                                        // send back
                                        service.getDataOutputStream().writeUTF("!privatefile");
                                        service.getDataOutputStream().writeUTF(this.username);
                                        service.getDataOutputStream().writeInt(fileNameBytes.length);
                                        service.getDataOutputStream().write(fileNameBytes);
                                        service.getDataOutputStream().writeInt(fileContentLength);
                                        service.getDataOutputStream().write(fileContentBytes);
                                        service.getDataOutputStream().flush();
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("!publicfile")) {
                    int fileNameLength = 0;
                    try {
                        fileNameLength = dataInputStream.readInt();
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
                                System.out.println(username + " send " + fileName + " to all");
                                for (ServerService service : ServerSide.userList) {
                                    // send back
                                    service.getDataOutputStream().writeUTF("!publicfile");
                                    service.getDataOutputStream().writeUTF(this.username);
                                    service.getDataOutputStream().writeInt(fileNameBytes.length);
                                    service.getDataOutputStream().write(fileNameBytes);
                                    service.getDataOutputStream().writeInt(fileContentLength);
                                    service.getDataOutputStream().write(fileContentBytes);
                                    service.getDataOutputStream().flush();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("!logout")) {
                    String userToLogout = dataInputStream.readUTF();
                    System.out.println(userToLogout + " request logout");
                    ServerSide.removeFromUserList(userToLogout);
                    ServerSide.updateOnlineUser();
                    ServerSide.numOfUserText.setText(String.valueOf(ServerSide.userList.size()));
                    // send message that user can leave
                    dataOutputStream.writeUTF("!leave");
                    dataOutputStream.flush();
                    socket.close();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * close connection
     *
     * @throws IOException exception
     */
    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}