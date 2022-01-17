package vn.edu.hcmus.student.sv19127640.chatroom.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * vn.edu.hcmus.student.sv19127640.chatroom
 * Created by ADMIN
 * Date 12/21/2021 - 4:33 AM
 * Description: register screen
 */
public class RegisterScreen extends JFrame implements ActionListener {
    /**
     * attributes
     */
    private JPanel registerPanel;
    private JLabel header;
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordText;
    private JCheckBox showPass;
    private JButton loginBtn;
    private JButton cancelBtn;
    private JButton submitBtn;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLable;
    private JTextField portField;

    /**
     * default constructor
     */
    public RegisterScreen(){
        Container container = this.getContentPane();
        registerPanel = new JPanel(new GridBagLayout());
        usernameLabel = new JLabel("Username: ");
        usernameText = new JTextField(10);
        passwordLabel = new JLabel("Password: ");
        passwordText = new JPasswordField(10);
        confirmPasswordLabel = new JLabel("Confirm Password: ");
        confirmPasswordText = new JPasswordField(10);
        showPass = new JCheckBox("Show password");
        showPass.addActionListener(this);
        submitBtn = new JButton("Submit");
        submitBtn.addActionListener(this);
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
        loginBtn = new JButton("Go to login page");
        loginBtn.addActionListener(this);
        hostLabel = new JLabel("Host address: ");
        hostField = new JTextField(10);
        hostField.setText("127.0.0.1");
        portLable = new JLabel("Port: ");
        portField = new JTextField(10);
        portField.setText("3000");

        header = new JLabel("REGISTRATION FORM", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont (18.0f));
        header.setForeground(Color.blue);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridy = 1;
        gbc.gridx = 0;
        registerPanel.add(hostLabel, gbc);
        gbc.gridy = 2;
        registerPanel.add(portLable, gbc);
        gbc.gridy = 3;
        registerPanel.add(usernameLabel, gbc);
        gbc.gridy = 4;
        registerPanel.add(passwordLabel, gbc);
        gbc.gridy = 5;
        registerPanel.add(confirmPasswordLabel, gbc);

        gbc.gridy = 6;
        gbc.gridx = 2;
        registerPanel.add(showPass, gbc);

        gbc.gridy = 7;
        gbc.gridx = 1;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        JPanel footerContent = new JPanel();
        footerContent.setLayout(new BoxLayout(footerContent, BoxLayout.X_AXIS));

        footerContent.add(submitBtn);
        footerContent.add(Box.createRigidArea(new Dimension(10,0)));
        footerContent.add(cancelBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(footerContent);
        registerPanel.add(buttonPanel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPanel.add(hostField, gbc);
        gbc.gridy = 2;
        registerPanel.add(portField, gbc);
        gbc.gridy = 3;
        registerPanel.add(usernameText, gbc);
        gbc.gridy = 4;
        registerPanel.add(passwordText, gbc);
        gbc.gridy = 5;
        registerPanel.add(confirmPasswordText, gbc);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        registerPanel.add(header, gbc);

        gbc.gridy = 8;
        registerPanel.add(loginBtn, gbc);

        container.add(registerPanel);
        this.setTitle("Register");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(500, 350);
    }

    /**
     * button press handling
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn){
            this.dispose();
            new LoginScreen();
        }else if (e.getSource() == submitBtn){
            String username = usernameText.getText();
            String password1 = String.valueOf(passwordText.getPassword());
            String password2 = String.valueOf(confirmPasswordText.getPassword());
            if (username.length() == 0 || password1.length() == 0 || password2.length() == 0){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Please fill out all input field!");
            }
            else {
                try {
                    this.socket = new Socket(hostField.getText(), Integer.parseInt(portField.getText()));
                    this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
                    this.dataInputStream = new DataInputStream(this.socket.getInputStream());
                    // send request to server
                    this.dataOutputStream.writeUTF("!signup");
                    this.dataOutputStream.writeUTF(username);
                    this.dataOutputStream.writeUTF(password1);
                    this.dataOutputStream.writeUTF(password2);
                    this.dataOutputStream.flush();
                    String resultFromServer = this.dataInputStream.readUTF(); // read responds from server
                    if (resultFromServer.equals("!successsignup")){
                        this.dataInputStream.close();
                        this.dataOutputStream.close();
                        socket.close();
                        // redirect to login screen
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Create new account successfully!");
                                new LoginScreen();
                            }
                        });
                        this.dispose();
                    }else if (resultFromServer.equals("!passdontmatch")){
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Two passwords don't match. Please check again!");
                    }else if (resultFromServer.equals("!existsusername")){
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "This username is already exists!");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }else if (e.getSource() == showPass){
            if (showPass.isSelected()) {
                passwordText.setEchoChar((char) 0);
                confirmPasswordText.setEchoChar((char) 0);
            } else {
                passwordText.setEchoChar('*');
                confirmPasswordText.setEchoChar('*');
            }
        }else if (e.getSource() == cancelBtn){
            usernameText.setText("");
            passwordText.setText("");
            confirmPasswordText.setText("");
        }
    }
}
