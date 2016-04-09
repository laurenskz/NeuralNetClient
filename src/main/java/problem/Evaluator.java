package problem;

import client.Model;
import machinelearning.NetworkLearners.TicTacToeLearner;
import neuralnetwork.Network;
import org.jenetics.Chromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Laurens on 8-4-2016.
 */
public class Evaluator {

    private static Model model;
    private static List<Network> networks = new LinkedList<>();

    public static void setModel(Model model) {
        Evaluator.model = model;
    }

    public static synchronized Double evaluate(Genotype<DoubleGene> gt) {
        Network network = model.getEmptyNetwork();
        network.setWeights(gt.getChromosome());
        Double rating =  new OthelloEvaluator().evaluate(network);
        return rating;
    }
}
