package client;

import neuralnetwork.Network;
import org.jenetics.Genotype;
import problem.Evaluator;

import java.util.List;
import java.util.Vector;

/**
 * Created by Laurens on 7-4-2016.
 */
public class Model {

    private boolean running = true;
    private List<Network> networks = new Vector<Network>();
    private double maxFitness;
    private Network baseNetwork = null;
    private Network ownBest = null;

    public Network getOwnBest() {
        return ownBest;
    }

    public void setOwnBest(Network ownBest) {
        this.ownBest = ownBest;
    }

    public double getMaxFitness() {
        return maxFitness;
    }

    public void setMaxFitness(double maxFitness) {
        this.maxFitness = maxFitness;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }


    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getStartTime() {

        return startTime;
    }

    public void add(Network network){
        if(network.getRating()>maxFitness){
            maxFitness = network.getRating();
        }
        if(baseNetwork==null)baseNetwork = new Network(network);
        networks.add(network);
    }

    public Network getEmptyNetwork(){
        return new Network(baseNetwork);
    }

    private final long startTime = System.currentTimeMillis();

    public synchronized boolean isRunning() {
        return running;
    }
}
