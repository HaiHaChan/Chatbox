package demo.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataStream extends Thread {

    private boolean run;
    private DataInputStream dis;
    private Client client;

    public DataStream(Client client, DataInputStream dis) {
        run = true;
        this.client = client;
        this.dis = dis;
        this.start();
    }

    private ArrayList<String> getMSG() {
        ArrayList<String> data = new ArrayList<String>();
        try {
            String msg = dis.readUTF();
            data.add(msg.substring(0,1));
            data.add(msg.substring(1));
            return data;
        } catch (IOException ex) {
            Logger.getLogger(DataStream.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void run() {
        String msg1, msg2;
        while (run) {
            
                ArrayList<String> msg = new ArrayList<String>();
                msg = getMSG();
                msg1= msg.get(0);
                msg2= msg.get(1);
                client.getMSG(msg1, msg2);
        }
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        this.run = false;
    }
}
