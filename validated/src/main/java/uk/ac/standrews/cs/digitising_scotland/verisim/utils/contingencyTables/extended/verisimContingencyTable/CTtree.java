package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.ContingencyTable;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.RunnableNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.DoubleNodes.*;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes.SourceNodeInt;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SourceType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class CTtree extends Node<String, SourceType, Number, Number> implements ContingencyTable {

    private LinkedList<RunnableNode> deathTasks = new LinkedList<>();
    private LinkedList<RunnableNode> ageTasks = new LinkedList<>();
    private LinkedList<RunnableNode> nciyTasks = new LinkedList<>();
    private LinkedList<RunnableNode> nciapTasks = new LinkedList<>();
    private LinkedList<RunnableNode> sepTasks = new LinkedList<>();

    private LinkedList<RunnableNode> otherTasks = new LinkedList<>();

    private LinkedList<RunnableNode> delayedTasks = new LinkedList<>();

    public static final double NODE_MIN_COUNT = 0.01;

    private PopulationStatistics expected;

    private Date endDate;

    private SourceNodeInt simNode;
    private SourceNodeDouble statNode;

    public CTtree(PeopleCollection population, PopulationStatistics expected, Date startDate, Date endDate) {
        this.expected = expected;
        this.endDate = endDate;

//        for(IPersonExtended p : population.getAll()) {
//            processPerson(p, new YearDate(0), SourceType.SIM);
//
//            if(p.aliveOnDate(new ExactDate(31, 12, 1854))) {
//                processPerson(p, new YearDate(0), SourceType.STAT);
//            }
//
//        }

        YearDate prevY = new YearDate(startDate.getYear() - 1);
        for(IPersonExtended person : population.getPeople()) {
            if(person.aliveInYear(prevY)) {
                processPerson(person, prevY, SourceType.STAT);
            }
        }

        for (YearDate y = startDate.getYearDate(); DateUtils.dateBefore(y, endDate);
             y = y.advanceTime(1, TimeUnit.YEAR).getYearDate()) {

            // for every person in population
            for(IPersonExtended person : population.getPeople()) {

                // who was alive or died in the year of consideration
                if(person.bornInYear(y) && person.diedInYear(y)
                        ||
                        person.aliveInYear(y) && !person.bornInYear(y)) {
                    processPerson(person, y, SourceType.SIM);
                }
            }
        }



        executeDelayedTasks();

//        CTtable table = new CTtable(this);
//        CTtable table2 = new CTtable(table);
//
////        Collection<Node> leafs = getLeafNodes();
////
////        ArrayList<ArrayList<String>> table = new ArrayList<>();
////
////        for(Node leaf : leafs) {
////            table.add(leaf.toStringAL());
////        }
////
////        ArrayList<ArrayList<String>> table2 = (ArrayList<ArrayList<String>>) table.clone();
////
////        table2.remove(0);
//
//        System.out.println("TREE MADE");

    }

    public CTtree() {

    }

    public Collection<Node> getLeafNodes() {

        Collection<Node> childNodes = new ArrayList<>();

        childNodes.addAll(simNode.getLeafNodes());
        childNodes.addAll(statNode.getLeafNodes());

        return childNodes;

    }

    @Override
    public String getVariableName() {
        return null;
    }


    public Date getEndDate() {
        return endDate;
    }


    public PopulationStatistics getInputStats() {
        return expected;
    }

    public Node addChildA(SourceType childOption) {
        if(childOption == SourceType.SIM) {
            simNode = new SourceNodeInt(childOption, this);
            return simNode;
        } else {
            statNode = new SourceNodeDouble(childOption, this);
            return statNode;
        }

    }

    public Node getChild(SourceType option) throws ChildNotFoundException {
        if(option == SourceType.SIM) {
            if(simNode != null) {
                return simNode;
            } else {
                throw new ChildNotFoundException();
            }


        } else {
            if (statNode != null) {
                return statNode;
            } else {
                throw new ChildNotFoundException();
            }
        }
    }


    @Override
    public void addDelayedTask(RunnableNode node) {
//        delayedTasks.addLast(node);

        if(node instanceof DiedNodeDouble) {
            deathTasks.add(node);
        } else if(node instanceof AgeNodeDouble) {
            ageTasks.add(node);
        } else if(node instanceof NumberOfChildrenInYearNodeDouble) {
            nciyTasks.add(node);
        } else if(node instanceof NumberOfPreviousChildrenInAnyPartnershipNodeDouble) {
            nciapTasks.add(node);
        } else if(node instanceof SeparationNodeDouble) {
            sepTasks.add(node);
        }

    }

    @Override
    public void executeDelayedTasks() {

//        while(!delayedTasks.isEmpty()) {
//            RunnableNode n = delayedTasks.removeFirst();
//            System.out.println(n.toString());
//            n.runTask();
//        }


        while(!deathTasks.isEmpty()) {
            RunnableNode n = deathTasks.removeFirst();
            System.out.println(n.toString());
            n.runTask();
        }

        while(nciyTasks.size() + sepTasks.size() + nciapTasks.size() + ageTasks.size() != 0) {

            while (nciyTasks.size() + sepTasks.size() + nciapTasks.size() != 0) {

//                while (!nciyTasks.isEmpty()) {
//                    RunnableNode n = nciyTasks.removeFirst();
//                    System.out.println(n.toString());
//                    n.runTask();
//                }

                while (!sepTasks.isEmpty()) {
                    RunnableNode n = sepTasks.removeFirst();
//                    System.out.println(n.toString());
                    n.runTask();
                }

                while (!nciapTasks.isEmpty()) {
                    RunnableNode n = nciapTasks.removeFirst();
//                    System.out.println(n.toString());
                    n.runTask();
                }

            }

//            while (!ageTasks.isEmpty()) {
            for(int i = 0; i < 2; i ++) {
                if(ageTasks.isEmpty()) {
                    break;
                }
                RunnableNode n = ageTasks.removeFirst();
                AgeNodeDouble a = (AgeNodeDouble) n;
                YOBNodeDouble y = (YOBNodeDouble) a.getAncestor(new YOBNodeDouble());
                System.out.println("Age --- " + y.getOption().toString() + " --- " + a.getOption().toString());
                n.runTask();
            }

        }













//        while(!otherTasks.isEmpty()) {
//            RunnableNode n = otherTasks.removeFirst();
//            System.out.println(n.toString());
//            n.runTask();
//        }

    }

    public void processPerson(IPersonExtended person, Date currentDate, SourceType source) {

        try {
            getChild(source).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChildA(source).processPerson(person, currentDate);
        }

    }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption) { return null; }

    @Override
    public Node<SourceType, ?, Number, ?> addChild(SourceType childOption, Number initCount) {
        return null;
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

    }

    @Override
    public Node getRootNode() {
        return null;
    }

    @Override
    public void incCount(Number byCount) {

    }

    @Override
    public void incCountByOne() {

    }
}