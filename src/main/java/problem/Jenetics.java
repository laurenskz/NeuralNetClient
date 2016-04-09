package problem;

import client.Model;
import logic.ServerConnection;
import machinelearning.NetworkLearners.TicTacToeLearner;
import neuralnetwork.Network;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStream;
import org.jenetics.util.Factory;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laurens on 8-4-2016.
 */
public class Jenetics {

    private Model model;
    private ServerConnection serverConnection;
    private long iteration;

    public Jenetics(Model model, ServerConnection serverConnection) {
        this.model = model;
        Evaluator.setModel(model);
        this.serverConnection = serverConnection;
    }


    public void start(){
        List<Phenotype<DoubleGene,Double>> populationList = setInitialPopulation();
        Population<DoubleGene,Double> population = new Population<>(populationList);
        Factory<Genotype<DoubleGene>> gtf = populationList.get(0).getGenotype();

        Engine<DoubleGene, Double> engine = Engine
                .<DoubleGene, Double>builder(Evaluator::evaluate, gtf)
                .build();
        iteration = 1;
        while(model.isRunning()){
            EvolutionResult<DoubleGene,Double>  result = engine.stream(population, iteration)
                    .limit(1)
                    .collect(EvolutionResult.toBestEvolutionResult());
            Phenotype<DoubleGene,Double> best = result.getBestPhenotype();
            uploadBestNetwork(best);
            population = result.getPopulation();
            iteration = result.getGeneration();
            synchronized (model.getNetworks()){
                for (Network network : model.getNetworks()) {
                    population.add(Phenotype.of(Genotype.of(network.getFactory()), iteration,Evaluator::evaluate));
                }
                model.getNetworks().clear();
            }
        }
    }

    private List<Phenotype<DoubleGene,Double>> setInitialPopulation() {
        List<Phenotype<DoubleGene,Double>> phenotypes;
        synchronized (model.getNetworks()){
            phenotypes = new ArrayList<>(model.getNetworks().size());
            for (int i = 0; i < model.getNetworks().size(); i++) {
                phenotypes.add(Phenotype.of(Genotype.of(model.getNetworks().get(i).getFactory()),iteration,Evaluator::evaluate));
            }
            model.getNetworks().clear();
        }
        return phenotypes;
    }

    private void uploadBestNetwork(Phenotype<DoubleGene, Double> best) {
        Network network = model.getEmptyNetwork();
        network.setWeights(best.getGenotype().getChromosome());
        float rating = Evaluator.evaluate(Genotype.of(network.getFactory())).floatValue();
        network.setRating(rating);
        if(rating>model.getMaxFitness()){
            model.setMaxFitness(rating);
            model.setOwnBest(network);
            serverConnection.upload(network);
        }
    }

    public static void main(String[] args) {
        double sum = 0;
        JSONArray array = new JSONArray("[0.6066852,0.33734363,0.85707223,0.06497158,-0.39641175,0.012199858,0.47440153,-0.09087894,0.9902094,0.40242332,0.60077536,0.29004982,-0.15609899,0.60178834,-0.070747405,0.72342986,0.5261211,0.77237964,0.68046325,0.9399871,0.9846303,-0.52417314,-0.22800061,-0.4999046,0.7824335,0.76884806,0.65898925,0.8380994,0.68878365,0.40598938,-0.39797243,0.16151018,-0.76999366,0.68207926,-0.017472602,0.7084564,0.11814754,-0.08925238,0.48263437,-0.89778876,0.39174443,0.97467875,0.8354505,0.9847051,0.03732655,-0.90978295,0.8572507,0.7060451,0.9959399,0.13243681,-0.7342919,0.9838226,0.33136013,0.11324376,0.6644268,-0.18339439,0.89181346,0.5778215,0.9736337,-0.23384139,-0.79703224,0.7227353,-0.5762157,-0.3037052,0.15029383,0.49666375,0.83095413,0.25741035,0.71777636,-0.61488336,0.18320318,0.9968875,-0.14776142,-0.5856666,0.9879707,0.07188189,0.43718362,-0.4383628,0.79106927,-0.32457557,0.3964521,0.11640052,-0.7847747,0.3099703,0.7668882,0.8911499,-0.922391,0.24155791,0.38552138,-0.6320925,-0.197932,0.4356394,-0.5550941,0.655,0.893741,0.64076585,0.3599276,0.40230605,0.24556528,0.57477945,-0.18951088,-0.7400782,0.5470482,0.30514038,0.36096373,0.97136474,-0.02887756,0.33505303,0.82942444,-0.20393932,0.043411653,-0.34455115,0.97363603,0.58175975,0.46635967,0.1542698,0.93917274,-0.5102971,0.16865481,-0.47084737,0.43055606,-0.9313858,0.46924892,0.34051156,0.87619483,0.9820669,0.85697865,0.68275887,0.5237479,-0.46835873,0.89442664,-0.7806214,-0.6418526,-0.12789957,0.026282167,0.8474291,-0.52973455,-0.5739435,0.0043098773,0.5214149,0.9403675,-0.10603502,0.71755856,0.3290671,-0.60225064,0.13659339,0.47215348,0.73836136,0.94036746,-0.48292983,0.07734086,-0.55219156,-0.17319638,0.77961844,0.6526368,0.29263958,0.8377442,0.806533,-0.01722869,0.1831882,0.44668874,-0.56157,-0.86897534,0.7168526,-0.30405307,0.019809382,0.97576916,-0.02985042,0.053984176,0.64657795,-0.2854507,-0.934608,0.6060563,0.16409408,0.85524863,-0.20457779,-0.55444264,0.90024424,-0.27291748,-0.6913786,-0.18742043,-0.35801968,0.87464446,0.1956085,-0.41874987,-0.30840233,-0.39178655,0.5134751,-0.52556217,0.6816906,0.7601132,0.7375901,0.84647435,-0.3421608,-0.08044009,0.08114285,-0.013807368,-0.60145324,-0.59575975,0.7442787,0.38259217,0.95105976,-0.6279961,0.60397786,0.7771052,0.4048397,0.9960057,-0.8136931,0,0.47582963,-0.8269742,0.6555191,-0.37259793,-0.67850316,-0.818961,-0.20584166,-0.15471916,0.5716486,0.8709815,-0.29465264,0.5668141,0.66011524,0.07398753,-0.5358657,-0.88583654,0.64215845,0.8342698,0.14242078,0.20117295,0.46287766,0.5959386,-0.54392135,-0.0789242,-0.4789627,-0.3261379,0.59525484,-0.08582022,0.76378065,-0.007584343,-0.3030568,0.21829689,-0.03063385,0.76136726,0.9795138,0.9111619,0,-0.12552696,0.8823528,0.8715018,0.5902834,0,-0.48606378],\"Rows\":14},{\"Cols\":14,\"Data\":[-0.3710883,-0.29413068,-0.54343957,0.8161392,0.8871161,0.7255193,0.62400174,0,-0.54173267,-0.123650685,-0.8462834,0,0.36114413,0.23891106,0.41168782,-0.15277918,0.83087283,0.9830927,0.602269,-0.0111864675,0.9917805,0.28001535,0.79582524,0.123183995,0.26634192,0.19068192,-0.7890567,-0.48877546,0.79918647,0.072163545,0.060290575,0.59979135,0.9276285,0.86255014,-0.69982487,-0.55906045,-0.639919,0.21561764,0,-0.95988387,0.5584465,-0.829931,0.5196366,0.2310137,0.46610838,0.64231604,0.42557493,-0.64945924,-0.73392767,0.9856952,-0.3002701,0.99079555,-0.5708215,0.553338,0.5649967,0.81759834,0.7785167,0.41511405,0.6784495,0.85970664,0.9096629,0.9666041,0.011004064,-0.054445352,0.9176725,0.19338495,-0.2538542,0.59273297,-0.4226392,0.81338835,0.8558652,0.48916134,-0.6011798,0.07585366,0.83167464,0.33502513,0.1785753,-0.74590206,0.5451169,0.83979845,0.3261297,0.53880495,0.99841523,0.49074745,0.45133817,0.9362805,0.6294939,0.7625388,0.737672,-0.5873051,0.672724,0.6515914,-0.6110651,0.14882828,0.9767449,0.6715477,0.99699575,0.4713529,0,0.28951365,-0.17770675,-0.086195245,0.28474635,0.48783737,-0.6632354,-0.9028372,0.8428276,-0.28842753,0.17252553,0.15327868,-0.6643972,0.6398644,0.7420396,0.9741717,0.35035434,0.9992342,-0.32779172,0.83420247,0.78185326,-0.9583865,-0.5238385,0.5270975,0.63036966,0.2728812,-0.30399346,0.7888822]");
        for (int i = 0; i < array.length(); i++) {
            sum+=array.getDouble(i);
        }
        System.out.println(sum);
    }
}
