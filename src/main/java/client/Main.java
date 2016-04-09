package client;

import logic.ServerConnection;
import problem.Jenetics;

/**
 * Created by Laurens on 7-4-2016.
 */
public class Main {

    public static void main(String[] args) {
        if(args.length==0){
            System.err.println("Please supply an IP");
            System.exit(-1);
        }
        Model model = new Model();
        ServerConnection serverConnection = new ServerConnection(args[0],model);
        Jenetics jenetics = new Jenetics(model,serverConnection);
        new Thread(new ClientInteraction(model)).start();
        jenetics.start();
    }
}
