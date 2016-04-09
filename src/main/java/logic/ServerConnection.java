package logic;


import client.Model;
import neuralnetwork.Network;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Laurens on 7-4-2016.
 */
public class ServerConnection implements Runnable {

    public static final int SERVER_PORT = 7788;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Model model;

    public ServerConnection(String ip, Model model){
        this.model = model;
        try {
            this.socket = new Socket(ip,SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            initialConnect();
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(Network network){
        writer.println(network.toJSON().toString());
        writer.flush();
    }


    @Override
    public void run() {
        while(model.isRunning()){
            try {
                String line = reader.readLine();
                synchronized (model.getNetworks()){
                    model.add(new Network(new JSONObject(line)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialConnect(){
        String line;
        try{
            while(!(line = reader.readLine()).equals("Done")){
                model.add(new Network(new JSONObject(line)));
            }
        }catch (IOException e){

        }
    }
}
