package vn.edu.hcmus.student.sv19127640.chatroom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom
 * Created by ADMIN
 * Date 12/21/2021 - 3:35 AM
 * Description: ...
 */
public class LoginScreen extends JFrame implements ActionListener {
    private JPanel loginPanel;
    private JLabel header;
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    private JCheckBox showPass;
    private JButton loginBtn;
    private JButton cancelBtn;
    private JButton registerBtn;
    private Account account;

    public LoginScreen(){
        Container container = this.getContentPane();
        account = new Account();
        loginPanel = new JPanel(new GridBagLayout());
        usernameLabel = new JLabel("Username: ");
        usernameText = new JTextField(10);
        passwordLabel = new JLabel("Password: ");
        passwordText = new JPasswordField(10);
        showPass = new JCheckBox("Show password");
        showPass.addActionListener(this);
        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
        registerBtn = new JButton("Register new account");
        registerBtn.addActionListener(this);

        header = new JLabel("LOGIN FORM", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont (18.0f));
        header.setForeground(Color.blue);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridy = 1;
        gbc.gridx = 0;
        loginPanel.add(usernameLabel, gbc);
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.gridx = 2;
        loginPanel.add(showPass, gbc);

        gbc.gridy = 4;
        gbc.gridx = 1;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        JPanel footerContent = new JPanel();
        footerContent.setLayout(new BoxLayout(footerContent, BoxLayout.X_AXIS));

        footerContent.add(loginBtn);
        footerContent.add(Box.createRigidArea(new Dimension(10,0)));
        footerContent.add(cancelBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(footerContent);
        loginPanel.add(buttonPanel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(usernameText, gbc);
        gbc.gridy = 2;
        loginPanel.add(passwordText, gbc);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        loginPanel.add(header, gbc);
        gbc.gridy = 5;
        loginPanel.add(registerBtn, gbc);

        container.add(loginPanel);
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(400, 300);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerBtn){
            this.dispose();
            new RegisterScreen();
        }else if (e.getSource() == loginBtn){
            String username = usernameText.getText();
            String password = String.valueOf(passwordText.getPassword());
            account.setUsername(username);
            account.setPassword(password);
            if (account.isValidAccount()){
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Success");
            }else{
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Wrong username or password. Please check again!!!");
            }
        }else if (e.getSource() == showPass){
            if (showPass.isSelected())
                passwordText.setEchoChar((char) 0);
            else
                passwordText.setEchoChar('*');
        }else if (e.getSource() == cancelBtn){
            usernameText.setText("");
            passwordText.setText("");
        }
    }
}
