package vn.edu.hcmus.student.sv19127640.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom
 * Created by ADMIN
 * Date 12/21/2021 - 4:33 AM
 * Description: ...
 */
public class RegisterScreen extends JFrame implements ActionListener {
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
    private Account account;

    public RegisterScreen(){
        Container container = this.getContentPane();
        account = new Account();
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

        header = new JLabel("REGISTRATION FORM", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont (18.0f));
        header.setForeground(Color.blue);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridy = 1;
        gbc.gridx = 0;
        registerPanel.add(usernameLabel, gbc);
        gbc.gridy = 2;
        registerPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        registerPanel.add(confirmPasswordLabel, gbc);

        gbc.gridy = 4;
        gbc.gridx = 2;
        registerPanel.add(showPass, gbc);

        gbc.gridy = 5;
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
        registerPanel.add(usernameText, gbc);
        gbc.gridy = 2;
        registerPanel.add(passwordText, gbc);
        gbc.gridy = 3;
        registerPanel.add(confirmPasswordText, gbc);


        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        registerPanel.add(header, gbc);

        gbc.gridy = 6;
        registerPanel.add(loginBtn, gbc);


        container.add(registerPanel);
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(500, 300);
    }


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
            account.setUsername(username);
            account.setPassword(password1);
            if (account.isExistUsername()){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "This username is already exists!");
            }else{
                if (!password1.equals(password2)){
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Two passwords don't match. Please check again!");
                }else{ // success
                    account.saveToFile();
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Create new account successfully!");
                    // redirect to login screen
                    this.dispose();
                    new LoginScreen();
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
