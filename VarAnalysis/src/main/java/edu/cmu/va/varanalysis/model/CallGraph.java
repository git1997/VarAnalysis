package edu.cmu.va.varanalysis.model;

import java.util.ArrayList;
import java.util.List;

import de.fosd.typechef.featureexpr.FeatureExpr;

public class CallGraph {

	private final List<Edge> edges = new ArrayList<>();

	public void addEdge(PositionRange from, PositionRange to,
			FeatureExpr condition) {
		edges.add(new Edge(from, to, condition));
	}

	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("CallGraph(");
		for (Edge e : edges) {
			r.append(e.toString());
			r.append(";");
		}
		r.append(")");
		return r.toString();
	}

	public List<PositionRange> getNodes() {
		List<PositionRange> result = new ArrayList<>();
		for (Edge e : edges) {
			result.add(e.from);
			result.add(e.to);
		}
		return result;
	}

	public List<Edge> getEdges() {
		return edges;
	}

}
