package demo.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connected extends Thread {

    public Socket client;
    public Server server;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean run;

    public Connected(Server server, Socket client) {
        try {
            this.server = server;
            this.client = client;
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());
            run = true;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        // xữ lý đăng nhập
        ArrayList<String> msg = new ArrayList<String>();
        String name = "";
        while (run) {
            msg = getMSG();
            int flag = Integer.parseInt(msg.get(0));
            String data = msg.get(1);
            String nick, pass, list[];

            if (flag == 1) {
                list = data.split("#");
                nick = list[0];
                pass = list[1];
                if (checkSignup(nick)) {
                    sendMSG("111");
                } else {
                    name = nick;
                    server.update.append(nick + " đã kết nối với room\n");
                    server.broadcast("3",nick, nick + " đã vào room với anh em\n");
                    server.listConnected.put(nick, this);
                    server.listUser.put(nick, nick + "#" + pass);
                    server.addUser(nick, pass);
                    sendMSG("1");
                    server.broadcast("4",nick,nick);
                }
            } else if (flag == 2) {
                list = data.split("#");
                nick = list[0];
                pass = list[1];

                if (!checkLogin(nick, pass)) {
                    sendMSG("111");
                } else {
                    name = nick;
                    server.update.append(nick + " đã kết nối với room\n");
                    server.broadcast("3",nick, nick + " đã vào room với anh em\n");
                    server.listConnected.put(nick, this);
                    sendMSG("2");
                    server.broadcast("4",nick,nick);
                    server.up(nick);
                }
            } else if (flag == 5) {
                 run = false;
                exit(name);
               
            } else if (flag == 3) {
                server.broadcast("3",name, name + " : " + data);
//            } else if(flag == 6){
//                server.single("6",data,name);
            } else if(flag == 7){
                server.single("7",data,name);
            } else if(flag == 8){
                server.single("8", data, name);
            }else if(flag == 9){
                int i = data.indexOf("#");
                String s1 =data.substring(0,i);
                String s2 = data.substring(i+1);
                server.single("9", s1,name+" : "+ s2);
            } else {
                logout();
            } 
        }
    }
    
    private void logout() {
        try {
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exit(String nick) {
        try {
            server.broadcast("5",nick,nick);
            dos.close();
            dis.close();
            client.close();
            server.update.append(nick + " đã thoát\n");
            server.broadcast("3",nick, nick + " đã thoát\n");
            server.listConnected.remove(nick);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLogin(String nick, String pass) {
        if (server.listUser.containsKey(nick)) {
            String s = nick + "#" + pass;
            return server.listUser.containsValue(s);
        } else {
            return false;
        }
    }

    private boolean checkSignup(String nick) {
        return server.listUser.containsKey(nick);
    }

    private void sendMSG(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMSG(String msg1, String msg2) {
        sendMSG(msg1 + msg2);
    }

    private ArrayList<String> getMSG() {
        try {
            
            String msg = new String(dis.readUTF());
            ArrayList<String> data = new ArrayList<String>();
            data.add(msg.substring(0, 1));
            data.add(msg.substring(1));
            return data;
        } catch (IOException ex) {
            Logger.getLogger(Connected.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
