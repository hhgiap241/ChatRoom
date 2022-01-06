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
                } else if (message.equals("!logout")){
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