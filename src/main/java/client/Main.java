package client;

import logic.ServerConnection;
import problem.Jenetics;

/**
 * Created by Laurens on 7-4-2016.
 */
public class Main {

    public static void main(String[] args) {
        String ip;
        if(args.length==0){
//            System.err.println("Please supply an IP");
            ip = "baboea.nl";
        }else{
            ip = args[0];
        }
        Model model = new Model();
        ServerConnection serverConnection = new ServerConnection(ip,model);
        Jenetics jenetics = new Jenetics(model,serverConnection);
        new Thread(new ClientInteraction(model)).start();
        jenetics.start();
    }
}
