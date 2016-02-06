package application.operations.miner;

import org.jgrapht.graph.DefaultEdge;

public class LabeledEdge extends DefaultEdge{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5182062259208912883L;
	private String v1;
    private String v2;
    private String label;

    public LabeledEdge(String v1, String v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public String getV1() {
        return v1;
    }

    public String getV2() {
        return v2;
    }

    public String toString() {
        return label;
    }
}
