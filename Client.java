package demo.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class Client extends JFrame implements ActionListener {

    private JTabbedPane tabbedPane;
    private JButton login, logout, signup, exit, clean, introduction, send, invite, talk;
    private JPanel pChat, pLogin;
    private JTextField nick, write;
    private JPasswordField pass;
    private JTextArea message, home;
    private DefaultListModel listFriends;
    private JList<String> boxFriend;
    private Hashtable<String,JTextArea> listJTArea;
    private Hashtable<String, JFrame> listJFrame;
    
    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String best;

    public Client() {
        super("Chat: Client");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        this.setSize(300, 490);
        listJTArea = new Hashtable<String, JTextArea>();
        listJFrame = new Hashtable<String, JFrame>();
        this.creatJFrame();
        this.setResizable(false);
        this.setVisible(true);
    }

//-----[ Tạo giao diện ]--------//
    public void creatJFrame() {
        this.setLayout(new CardLayout());
        this.creatPLogin();
        this.creatPChat();
        
    }

    public void creatPLogin() {
        pLogin = new JPanel();
        pLogin.setLayout(new GridLayout(5, 1));
        pLogin.add(new JLabel());

        JPanel pL1 = new JPanel();
        pL1.setLayout(new FlowLayout(FlowLayout.CENTER));
        pL1.add(new JLabel("                             Nick                                "));
        nick = new JTextField(15);
        pL1.add(nick);

        JPanel pL2 = new JPanel();
        pL2.setLayout(new FlowLayout(FlowLayout.CENTER));
        pL2.add(new JLabel("                             Pass                              "));
        pass = new JPasswordField(15);
        pL2.add(pass);

        JPanel pL3 = new JPanel();
        login = new JButton("Login");
        signup = new JButton("Signup");
        exit = new JButton("Exit");

        login.addActionListener(this);
        signup.addActionListener(this);
        exit.addActionListener(this);
        pL3.setLayout(new FlowLayout(FlowLayout.CENTER));
        pL3.add(login);
        pL3.add(signup);
        pL3.add(exit);

        JPanel pL4 = new JPanel();
        introduction = new JButton("Introduction");
        //introduction.setBackground(this.getBackground());
        introduction.addActionListener(this);
        pL4.add(introduction);

        pLogin.add(pL1);
        pLogin.add(pL2);
        pLogin.add(pL3);
        pLogin.add(pL4);
        pLogin.setVisible(true);
        this.add(pLogin);
    }

    public void creatPChat() {
        pChat = new JPanel(new FlowLayout());

        tabbedPane = new JTabbedPane();

        JPanel pHome = new JPanel(new BorderLayout());
        home = new JTextArea(28, 14);
        home.setEditable(false);
        pHome.add(new JLabel("    "), BorderLayout.EAST);
        pHome.add(new JScrollPane(home), BorderLayout.CENTER);
        pHome.add(new JLabel("    "), BorderLayout.WEST);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clean = new JButton("Clean");
        clean.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        p.add(clean);
        p.add(logout);
        pHome.add(p, BorderLayout.SOUTH);

        JPanel pSMS = new JPanel();
        pSMS.setLayout(new BorderLayout());

        message = new JTextArea();
        
        message.setEditable(false);
        pSMS.add(new JLabel("    "), BorderLayout.EAST);
        pSMS.add(new JLabel("    "), BorderLayout.WEST);
        pSMS.add(new JScrollPane(message), BorderLayout.CENTER);

        JPanel pWrite = new JPanel();
        write = new JTextField(18);
        send = new JButton("Send");
        send.addActionListener(this);
        pWrite.add(new JLabel("    "), BorderLayout.NORTH);
        pWrite.add(write, BorderLayout.CENTER);
        pWrite.add(send, BorderLayout.WEST);
        pSMS.add(pWrite, BorderLayout.SOUTH);

        JPanel pConnected = new JPanel(new BorderLayout());
        
        listFriends = new DefaultListModel();
        boxFriend = new JList(listFriends);
        //boxFriend.setSelectionModel(ListSelectionModel.SINGLE_SELECTION);
        boxFriend.setSelectedIndex(0);
        boxFriend.setVisibleRowCount(5);
        pConnected.add(new JLabel("   "), BorderLayout.NORTH);
        pConnected.add(new JLabel("   "), BorderLayout.EAST);
        pConnected.add(new JScrollPane(boxFriend),BorderLayout.CENTER);
        pConnected.add(new JLabel("   "), BorderLayout.WEST);
        
        JPanel pTalk = new JPanel(new FlowLayout(FlowLayout.CENTER));
        invite = new JButton("Invite");
        invite.addActionListener(this);
        talk = new JButton("Talk");
        talk.addActionListener(this);
        pTalk.add(invite);
        pTalk.add(talk);
//        pTalk.add(new JLabel("   "), BorderLayout.EAST);
//        pTalk.add(invite, BorderLayout.CENTER);
//        pTalk.add(new JLabel("   "), BorderLayout.WEST);
        pConnected.add(pTalk,BorderLayout.SOUTH);
            
        tabbedPane.addTab("HOME", null, pHome, "");
        tabbedPane.addTab("ROOM", null, pSMS, "");
        tabbedPane.addTab("FRIENDS", null, pConnected, "");

        pChat.add(tabbedPane);
        pChat.setVisible(false);
        this.add(pChat);
    }
    
    public void creatJPanel(String to){
        
        JFrame p = new JFrame(nick.getText()+ " trò chuyện cùng " + to);
        listJFrame.put(to, p);
        p.setLayout(new BorderLayout());
        p.setSize(300,450);
        p.addWindowListener(new WindowAdapter() {
            public void closingWindow(WindowEvent e){
                p.setVisible(false);
            }
        });
        JTextArea jta = new JTextArea();
        listJTArea.put(to, jta);
        jta.setEditable(false);
        p.add(new JLabel("    "), BorderLayout.NORTH);
        p.add(new JLabel("    "), BorderLayout.EAST);
        p.add(new JLabel("    "), BorderLayout.WEST);
        p.add(new JScrollPane(jta), BorderLayout.CENTER);

        JPanel pWrite2 = new JPanel();
        JTextField write2 = new JTextField(15);
        JButton send2 = new JButton("Send");
        send2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = write2.getText()+"\n";
                if (msg.compareTo("\n") != 0) {
                write2.setText("");
                jta.append("Tôi : " + msg);
                sendMSG("9"+to+"#"+msg);
        }
            }
        });
        
        pWrite2.add(new JLabel("     "), BorderLayout.NORTH);
        pWrite2.add(write2, BorderLayout.CENTER);
        pWrite2.add(send2, BorderLayout.WEST);
        p.add(pWrite2, BorderLayout.SOUTH);
        
        p.setVisible(true);
    }
    

//---------[ Socket ]-----------//	
    private void go() {
        try {
            client = new Socket("localhost", 2207);
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());
            best = "";
            //client.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối, xem lại dây mạng đi hoặc room chưa mở.", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Client().go();
    }

    public void openPChat(String nick) {
        this.setTitle(nick + "'s ChatApp");
        pChat.setVisible(true);
        pLogin.setVisible(false);
        //this.remove(pLogin);
        message.append("Đã đăng nhập thành công\n");
        dataStream = new DataStream(this, this.dis);
    }

    protected void sendMSG(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMSG() {
        String data = null;
        try {
            data = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void getMSG(String msg1, String msg2) {
        int flag = Integer.parseInt(msg1);
        switch (flag) {
            // tin nhắn nhóm
            case 3:
                this.message.append(msg2);
                break;
            // update danh sách online
            case 4:
                this.listFriends.addElement(msg2);
                //this.sendMSG("6"+msg2);
                break;
            case 5:
                this.listFriends.removeElement(msg2);
                break;
            case 6:
                String[] list;
                list = msg2.split("#");
                for(int i=0;i < list.length; i++){
                this.listFriends.addElement(list[i]);
                }
                break;
            //tin nhắn riêng
            case 7:
                int result = JOptionPane.showConfirmDialog(this,msg2 + " muốn trò truyện với bạn?", "Kết bạn", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    sendMSG("8"+msg2);
                    best = msg2;
                    creatJPanel(msg2);
                    listJFrame.get(msg2).setVisible(true);
                    
                }else if(result == JOptionPane.NO_OPTION){
                    sendMSG(""+msg2);
                }
                break;
                
            case 8:
                JOptionPane.showMessageDialog(this, msg2 + " đã đồng ý lời mời của bạn." );
                creatJPanel(msg2);
                listJFrame.get(msg2).setVisible(true);
                best = msg2;
                break; 
            case 9:
                //JOptionPane.showMessageDialog(this, msg2 + " đã từ chối lời mời của bạn." );
//                int i = msg2.indexOf(":");
//                String s1 = msg2.substring(0,i);
//                System.out.println(s1);
                listJTArea.get(best).append(msg2);
                
                break;
            // server đóng cửa
            case 0:
                JOptionPane.showMessageDialog(this, "Server đã đóng!!!");
                dataStream.stopThread();
                exit();
                break;
            // bổ sung sau
            default:
               
                break;
        }
    }
    
//---------------------------------------------- 
    
    private void checkSend(String msg) {
        if (msg.compareTo("\n") != 0) {
            this.message.append("Tôi : " + msg);
            sendMSG("3" + msg);
        }
    }

    private boolean checkLogin(String nick, String password) {
        nick = nick.trim();
        if (nick.compareTo("") == 0 || password.compareTo("") == 0) {
            return false;
        } else {
            sendMSG("2" + nick + "#" + password);
            int flag = Integer.parseInt(getMSG());
            if (flag == 2) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkSignup(String nick, String password) {
        nick = nick.trim();
        if (nick.compareTo("") == 0 || password.compareTo("") == 0) {
            return false;
        } else if (nick.indexOf("#") > -1 || password.indexOf("#") > -1) {
            return false;
        } else {
            sendMSG("1" + nick + "#" + password);
            int flag = Integer.parseInt(getMSG());
            if (flag == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkInvite(String name){
        return name != null && !listJFrame.containsKey(name);
    }
    
    private boolean checkTalk(String name){
        return name != null && listJFrame.containsKey(name);
    }
    
    private void exit() {
        try {
            sendMSG("5 exit");
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == send) {
            checkSend(write.getText() + "\n");
            write.setText("");
        }else if (e.getSource() == talk) {
            
            if(checkTalk(this.boxFriend.getSelectedValue())){
                best = this.boxFriend.getSelectedValue();
                listJFrame.get(best).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra. Vui lòng lưa chọn lại");
            }
        }else if (e.getSource() == invite) {
            
            if(checkInvite(this.boxFriend.getSelectedValue())){
                sendMSG("7"+this.boxFriend.getSelectedValue());
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra. Vui lòng lưa chọn lại");
            }
        } else if (e.getSource() == clean) {
            home.setEditable(true);
            home.setText("");
            home.setEditable(false);
        } else if (e.getSource() == logout) {
            dataStream.stopThread();
            this.exit();
//            this.go();
//            pLogin.setVisible(true);
//            pChat.setVisible(false);
        } else if (e.getSource() == login) {
            if (checkLogin(nick.getText(), pass.getText())) {
                this.openPChat(nick.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi đăng nhập: Vui lòng đăng nhập lại!", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == signup) {
            if (checkSignup(nick.getText(), pass.getText())) {
                this.openPChat(nick.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi đăng ký: Vui lòng đăng ký lại!", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            }
        }else if(e.getSource() == introduction){
            JOptionPane.showMessageDialog(this, "Hướng dẫn:\n"
                    + "Nhấn nút Login để đăng nhập\n"
                    + "Nhấn nút Signup để đăng ký\n"
                    + "Nhấn nút exit để thoát\n");
        }
    }

}
