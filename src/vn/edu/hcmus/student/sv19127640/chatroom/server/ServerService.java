package vn.edu.hcmus.student.sv19127640.chatroom.server;
import java.io.*;
import java.net.Socket;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom.server
 * Created by ADMIN
 * Date 1/2/2022 - 10:32 AM
 * Description: provide service for every login user
 */
class ServerService implements Runnable {
    /**
     * attributes
     */
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String username;
    private String password;

    /**
     * constructor with parameters
     * @param username String
     * @param password String
     * @param socket Socket
     * @throws IOException Exception
     */
    public ServerService(String username, String password, Socket socket) throws IOException {
        this.socket = socket;
        this.username = username;
        this.password = password;
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }

    /**
     * thread
     */
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
                        // check if file name is > 0
                        if (fileNameLength > 0) {
                            byte[] fileNameBytes = new byte[fileNameLength];
                            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            String fileName = new String(fileNameBytes);
                            int fileContentLength = dataInputStream.readInt();
                            if (fileContentLength > 0) {
                                byte[] fileContentBytes = new byte[fileContentLength];
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
                        if (fileNameLength > 0) {
                            byte[] fileNameBytes = new byte[fileNameLength];
                            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            String fileName = new String(fileNameBytes);
                            int fileContentLength = dataInputStream.readInt();
                            if (fileContentLength > 0) {
                                byte[] fileContentBytes = new byte[fileContentLength];
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

    /**
     * getter socket
     * @return Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * getter DataOutputStream
     * @return DataOutputStream
     */
    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    /**
     * getter DataInputStream
     * @return DataInputStream
     */
    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    /**
     * getter username
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * getter password
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * close connection
     * @throws IOException exception
     */
    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }
}