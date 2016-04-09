package client;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Laurens on 7-4-2016.
 */
public class ClientInteraction implements Runnable{


    Model model;

    public ClientInteraction(Model model) {
        this.model = model;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(model.isRunning()){
            String line = scanner.nextLine();
            parse(line);
        }
    }

    private void parse(String line){
        line = line.trim();
        if(line.equals("status")){
            System.out.println("Status:");
            System.out.println("Running for " + formatMilliSeconds(System.currentTimeMillis()-model.getStartTime()));
            System.out.println("Best network: " + model.getMaxFitness());
        }else if(line.equals("save -best")){
            model.getOwnBest().writeTo("bestOthelloNetwork.json");
        }else if(line.equals("exit")){
            model.setRunning(false);
        }else if(line.equals("help")){
            System.out.println("Possible commands");
            System.out.println("status: get the status");
            System.out.println("save -best: save the best network on this pc");
            System.out.println("exit: quit the generation");
        }
        else{
            System.out.println("Unsupported command");
        }
    }

    private String formatMilliSeconds(long millis){
        String format = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return format;
    }
}
