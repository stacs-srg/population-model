/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.implementations;

import uk.ac.standrews.cs.digitising_scotland.verisim.Config;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NBirthLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NDeathLogic;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.init.InitLogic;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.ContigencyTableFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.DesiredPopulationStatisticsFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.populationAnalytics.AnalyticsRunner;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.simulationSummaryLogging.SummaryRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.InsufficientNumberOfPeopleException;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.CustomLog4j;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.ProgramTimer;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.FileUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.fileUtils.InvalidInputFileException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.sourceEventRecords.RecordGenerationFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OBDModel {

    public static final String CODE_VERSION = "dev-bf";

    private static Logger log;

    private Config config;
    private SummaryRow summary;

    private PopulationStatistics desired;

    private Population population;

    private AdvancableDate currentTime;

    private EventLogic deathLogic = new NDeathLogic();
    private EventLogic birthLogic = new NBirthLogic();


    public static void setUpFileStructureAndLogs(String runPurpose, String startTime, String resultsPath) throws IOException {
        // This has to be ran prior to creating the Config file as the file structure and log file need to be created
        // prior to the first logging event that occurs when generating the config
        // And errors in this method are sent to standard error

        FileUtils.makeDirectoryStructure(runPurpose, startTime, resultsPath);
        log = CustomLog4j.setup(FileUtils.pathToLogDir(runPurpose, startTime, resultsPath), new OBDModel());
    }

    public OBDModel() {}

    public OBDModel(String startTime, Config config) throws IOException, InvalidInputFileException {

        this.config = config;

        // Set up simulation parameters
        currentTime = config.getTS();

        population = new Population(config);

        // get desired population info
        desired = DesiredPopulationStatisticsFactory.initialisePopulationStatistics(config);

        InitLogic.setUpInitParameters(config, desired);

        summary = new SummaryRow(Paths.get(config.getResultsSavePath().toString(), config.getRunPurpose(), startTime),
                config.getVarPath(), startTime, config.getRunPurpose(), CODE_VERSION, config.getSimulationTimeStep(), config.getInputWidth(),
                config.getT0(), config.getTE(), DateUtils.differenceInDays(config.getT0(), config.getTE()),
                config.getBirthFactor(), config.getDeathFactor(), config.getRecoveryFactor(),
                config.getMaxProportionOBirthsDueToInfidelity(), config.getMinBirthSpacing(), config.getOutputRecordFormat());

    }

    public void runSimulation() throws PreEmptiveOutOfMemoryWarning {

        ProgramTimer simTimer = new ProgramTimer();

        boolean runPassed = false;

        while (!runPassed) {

            try {
                runPassed = runSimulationTimeLoop();

            } catch (InsufficientNumberOfPeopleException e) {
                System.err.println("Simulation run incomplete due to insufficient number of people in population to " +
                        "perform requested events");
                System.err.println(e.getMessage());

                summary.setSimRunTime(simTimer.getRunTimeSeconds());
                summary.setCompleted(false);

                summary.outputSummaryRowToFile();
                deathLogic.resetEventCount();
                birthLogic.resetEventCount();

                simTimer = new ProgramTimer();

            } catch (PersonNotFoundException e) {
                throw new Error("Expected person not found in simulation - fatal error", e);
            }

        }

        MemoryUsageAnalysis.log();

        summary.setTotalPop(population.getAllPeople().getNumberOfPeople());
        summary.setSimRunTime(simTimer.getRunTimeSeconds());

    }

    private boolean runSimulationTimeLoop() throws InsufficientNumberOfPeopleException, PersonNotFoundException, PreEmptiveOutOfMemoryWarning {

        summary.setCompleted(true);

        while(DateUtils.dateBeforeOrEqual(currentTime, config.getTE())) {

            if(!InitLogic.inInitPeriod(currentTime) && population.getLivingPeople().getAll().size() < 100) {
                summary.setCompleted(false);
                break;
            }

            MemoryUsageAnalysis.log();

            String yearLine = "";

            if(DateUtils.dateBeforeOrEqual(currentTime, config.getT0())) {
                summary.setStartPop(population.getLivingPeople().getNumberOfPeople());
            }

            yearLine += currentTime.toString() + "\t";

            int bornAtTS = birthLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            yearLine += bornAtTS + "\t";

            if (InitLogic.inInitPeriod(currentTime) &&
                    DateUtils.matchesInterval(currentTime, InitLogic.getTimeStep(), config.getTS())) {

                int initAtTS = InitLogic.handleInitPeople(config, currentTime, population, desired);
                yearLine += initAtTS + "\t";

            } else if(!InitLogic.inInitPeriod(currentTime)) {
                yearLine += 0 + "\t";
            }

            int killedAtTS = deathLogic.handleEvent(config, currentTime, config.getSimulationTimeStep(), population, desired);
            yearLine += killedAtTS + "\t";

            if(inSimDates()) {
                population.getPopulationCounts().updateMaxPopulation(population.getLivingPeople().getNumberOfPeople());
            }

            currentTime = currentTime.advanceTime(config.getSimulationTimeStep());

            yearLine += population.getLivingPeople().getNumberOfPeople() + "\t" + population.getDeadPeople().getNumberOfPeople();
            log.info(yearLine);

        }


        log.info("TKilled\t" + deathLogic.getEventCount());
        log.info("TBorn\t" + birthLogic.getEventCount());
        log.info("Ratio\t" + deathLogic.getEventCount() / (double) birthLogic.getEventCount());


        summary.setEndPop(population.getLivingPeople().getNumberOfPeople());
        summary.setPeakPop(population.getPopulationCounts().getPeakPopulationSize());

        return true;
    }

    public void analyseAndOutputPopulation() throws PreEmptiveOutOfMemoryWarning {
        analyseAndOutputPopulation(true);
    }

    public void analyseAndOutputPopulation(boolean outputSummaryRow) throws PreEmptiveOutOfMemoryWarning {

        if(config.getOutputTables()) {
            ContigencyTableFactory.generateContigencyTables(population.getAllPeople(), desired, config, summary, 0, 150);
        }

        ProgramTimer recordTimer = new ProgramTimer();
        RecordGenerationFactory.outputRecords(config.getOutputRecordFormat(), FileUtils.getRecordsDirPath().toString(),
                population.getAllPeople());
        summary.setRecordsRunTime(recordTimer.getRunTimeSeconds());


        try {
            AnalyticsRunner.runAnalytics(population.getAllPeople(), new PrintStream(FileUtils.getDetailedResultsPath().toFile(), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MemoryUsageAnalysis.log();

        summary.setMaxMemoryUsage(MemoryUsageAnalysis.getMaxSimUsage());
        MemoryUsageAnalysis.reset();

        if(outputSummaryRow) {
            summary.outputSummaryRowToFile();
        }

        log.info("OBDModel --- Output complete");

    }




    private boolean inSimDates() {
        return DateUtils.dateBeforeOrEqual(config.getT0(), currentTime);
    }

    public Population getPopulation() {
        return population;
    }

    public PopulationStatistics getDesiredPopulationStatistics() {
        return desired;
    }

    public SummaryRow getSummaryRow() {
        return summary;
    }

}