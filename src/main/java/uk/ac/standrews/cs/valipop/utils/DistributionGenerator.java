package uk.ac.standrews.cs.valipop.utils;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.nds.util.FileUtil;
import uk.ac.standrews.cs.valipop.statistics.distributions.InconsistentWeightException;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.statsTables.dataDistributions.AgeDependantEnumeratedDistribution;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.IntegerRange;
import uk.ac.standrews.cs.valipop.utils.specialTypes.labeledValueSets.LabelledValueSet;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DistributionGenerator {


    public static void main(String[] args) throws IOException, InvalidInputFileException, InconsistentWeightException {

        for(String s: args)
            System.out.println(s);

        int forYear;
        try {
            forYear = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {

            throw new RuntimeException(args[0], e);
        }
        String sourcePopulation = args[1];
        String sourceOrganisation = args[2];

        Path outToDir = Paths.get(args[3]);

        String filterOn = args[4];
        String groupY = args[5];
        String groupX = args[6];

        LinkedList<String> lines = new LinkedList<>();
        String labels = "";

        String[] files = {"src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-A-D.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-R-W.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-E-K.csv",
                "src/main/resources/valipop/inputs/icem-scot-1861/icem-scot-1861-counties-L-P.csv"};

        for(String file : files) {
            ArrayList<String> fileLines = new ArrayList<>(InputFileReader.getAllLines(Paths.get(file)));

            if (lines.isEmpty()) {
                labels = fileLines.get(0);
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else if (labels.equals(fileLines.get(0))) {
                lines.addAll(fileLines.subList(1, fileLines.size() - 1));
            } else {
                throw new RuntimeException("File header labels incompatible");
            }
        }

        DataRowSet dataset = new DataRowSet(labels, lines);

        if(dataset.hasLabel(filterOn) && dataset.hasLabel(groupY) && dataset.hasLabel(groupX)) {

            Map<String, DataRowSet> tables = dataset.splitOn(filterOn);

            for(String splitOn : tables.keySet()) {

                DataRowSet table = tables.get(splitOn);

                Map<IntegerRange, LabelledValueSet<String, Double>> dist = table.to2DTableOfProportions(groupX, groupY);

                PrintStream ps = FileUtil.createPrintStreamToFile(Paths.get(outToDir.toString(), splitOn, ".txt").toString());

                boolean first = false;

                for(IntegerRange iR: dist.keySet()) {

                    LabelledValueSet<String, Double> row = dist.get(iR);

                    if(first) {
                        for(String s : row.getLabels())
                            ps.print(s + "\t");
                        ps.println();
                        first = false;
                    }

                    ps.print(iR + " \t");

                    for(String s : row.getLabels())
                        ps.print(row.getValue(labels) + "\t");

                    ps.println();
                }

                //TODO need male/female in here
                AgeDependantEnumeratedDistribution aDEDist = new AgeDependantEnumeratedDistribution(Year.of(forYear), sourcePopulation, sourceOrganisation, dist, new JDKRandomGenerator());
            //    aDEDist.outputToFile(outToDir);


            }


        } else {
            throw new InvalidInputFileException("group or/and filter variables do not appear in file labels");
        }




    }


}