package demo.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame implements ActionListener {
    
    private JButton close;
    public JTextArea update;
    private ServerSocket server;
    public Hashtable<String, Connected> listConnected;
    public Hashtable<String, String> listUser;
    
    public Server() {
        super("Chat : Server");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }            
        });
        setSize(300, 400);
        addItem();
        setVisible(true);
    }    
    
    private void addItem() {
        setLayout(new BorderLayout());
        
        add(new JLabel("Trạng thái server : \n"), BorderLayout.NORTH);
        add(new JLabel("     "), BorderLayout.EAST);
        add(new JLabel("     "), BorderLayout.WEST);
        
        update = new JTextArea(10, 20);
        update.setEditable(false);
        add(new JScrollPane(update), BorderLayout.CENTER);
        
        close = new JButton("Close Server");
        close.addActionListener(this);
        add(close, BorderLayout.SOUTH);
        
        update.append("Máy chủ đã được mở.\n");
    }
    
    private void go() {
        try {
            listConnected = new Hashtable<String, Connected>();
            //getUser();
            getUser();
            server = new ServerSocket(2207);
            update.append("Máy chủ bắt đầu phục vụ\n");
            while (true) {
                Socket client = server.accept();
                new Connected(this, client);
            }
        } catch (IOException e) {
            update.append("Không thể khởi động máy chủ\n");
            JOptionPane.showMessageDialog(this,"Không thể khởi động máy chủ");
            System.exit(0);
        }
    }
    
    private void getUser(){

        BufferedReader br = null;
        try {
            listUser = new Hashtable<String, String>();
            File f = new File("/home/tranyi/NetBeansProjects/Demo/data.txt");
            br = new BufferedReader(new FileReader(f));
            while (true) {
                String s = br.readLine();
                if(s == null) break;
                String[] list = s.split("#");
                listUser.put(list[0], s);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
    }
    
    public void addUser(String nick, String pass){
        BufferedWriter bw = null;
        try {
            String s = nick +"#" + pass;
            File f = new File("/home/tranyi/NetBeansProjects/Demo/data.txt");
            bw = new BufferedWriter(new FileWriter(f,true));
            bw.write(s);
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        new Server().go();
    }
    
    public void actionPerformed(ActionEvent e) {
        close();
    }
    
    public void close(){
        Enumeration e = listConnected.keys();
        String name = "";
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            listConnected.get(name).sendMSG("0", "");
        }
        
        try {
            server.close();
        } catch (IOException e1) {
            update.append("Không thể dừng được máy chủ\n");
        }
        System.exit(0);
        
    }
    
    public void single(String flag,String to, String data){
        listConnected.get(to).sendMSG(flag, data);
    }
    
    public void broadcast(String flag,String from, String msg) {
        Enumeration e = listConnected.keys();
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(from) != 0) {
                listConnected.get(name).sendMSG(flag, msg);
            }
        }
    }
    
    public void up(String to) {
        Enumeration e = listConnected.keys();
        String name = null;
        String msg = "";
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(to) != 0) {
                if (msg.equals("")){
                    msg += name;
                } else {
                    msg = msg + "#" +name;
                }
            }
        }
        listConnected.get(to).sendMSG("6", msg);
    }

}
   