package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended;

import org.apache.bcel.generic.POP;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.recording.PopulationStatistics;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.SourceNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.Table;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class Node<Op, cOp> {

    private double count = 0;
    private Op option;
    private Map<cOp, Node<cOp, ?>> children = new HashMap<>();
    private Node<?, Op> parent;

    public abstract void makeChildren();
    public abstract Node<cOp, ?> addChild(cOp childOption, double initCount);
    public abstract Node<cOp, ?> addChild(cOp childOption);
    public abstract void advanceCount();
    public abstract void calcCount();
    public abstract void processPerson(IPersonExtended person, Date currentDate);

    public Node() {}

    public Node(Op option, Node<?, Op> parent) {
        this.option = option;
        this.parent = parent;
    }

    public Node(Op option, Node<?, Op> parent, double initCount) {
        this(option, parent);
        this.count = initCount;
    }

    public Node<cOp, ?> addChild(Node<cOp, ?> child) {
        children.put(child.getOption(), child);
        return child;
    }

    public void incChild(cOp childOption, double byCount) {
        try {
            getChild(childOption).incCount(byCount);
        } catch (ChildNotFoundException e) {
            addChild(childOption, byCount);
        }
    }

    public void incCount(double byCount) {
        count += byCount;
    }

    public Op getOption() {
        return option;
    }

    public double getCount() {
        return count;
    }

    public Collection<Node<cOp, ?>> getChildren() {
        return children.values();
    }

    public Node<cOp, ?> getChild(cOp childOption) throws ChildNotFoundException {
        Node<cOp, ?> n = children.get(childOption);

        if(n == null) {
            throw new ChildNotFoundException();
        }

        return n;
    }

    public Node<?, Op> getParent() {
        return parent;
    }

    public void addDelayedTask(RunnableNode node) {
        getParent().addDelayedTask(node);
    }

    public Node getAncestor(Node nodeType) {
        if(nodeType instanceof SourceNode) {
            return this;
        } else {
            return getParent().getAncestor(nodeType);
        }
    }

    public void setCount(double count) {
        this.count = count;
    }

    public PopulationStatistics getInputStats() {
        return getAncestor(new Table()).getInputStats();
    }



}
