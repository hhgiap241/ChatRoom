package vn.edu.hcmus.student.sv19127640.chatroom.auth;

import java.io.*;

/**
 * vn.edu.hcmus.student.sv19127640.chatroom
 * Created by ADMIN
 * Date 12/21/2021 - 4:57 AM
 * Description: class to verify account
 */
public class Account {
    /**
     * attributes
     */
    private String username;
    private String password;

    /**
     * default constructor
     */
    public Account(){
        username = null;
        password = null;
    }

    /**
     * constructor with parameter
     * @param username String
     * @param password String
     */
    public Account(String username, String password){
       this.username = username;
       this.password = password;
    }

    /**
     * save account to file account.txt
     */
    public void saveToFile(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("account.txt", true));
            String line = this.username + "|" + this.password;
            bufferedWriter.write(line + "\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get an account from string
     * @param line String
     * @return String array
     */
    public String[] getAccount (String line){
        String[] tokenizer = line.split("\\|");
        return tokenizer;
    }

    /**
     * setter username
      * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * setter passwrd
     * @param password String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * check if this username is existed or not
     * @return boolean
     */
    public boolean isExistUsername(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("account.txt"));
            while(true){
                String line = bufferedReader.readLine();
                if (line != null){
                    String[] tokenizer = this.getAccount(line);
                    if (tokenizer[0].equals(this.username))
                        return true;
                }else
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * check if this account is have the right username and password or not
     * @return boolean
     */
    public boolean isValidAccount(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("account.txt"));
            while(true){
                String line = bufferedReader.readLine();
                if (line != null){
                    String[] tokenizer = this.getAccount(line);
                    if (tokenizer[0].equals(this.username) && tokenizer[1].equals(this.password))
                        return true;
                }else
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
