package edu.cmu.va.varanalysis.model;

import de.fosd.typechef.featureexpr.FeatureExpr;

public class Edge {
	PositionRange from;
	PositionRange to;
	private FeatureExpr condition;

	Edge(PositionRange from, PositionRange to, FeatureExpr condition) {
		this.from = from;
		this.to = to;
		this.condition = condition;
	}

	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append(from.toString());
		r.append("--");
		r.append(to.toString());
		r.append(" IF ");
		r.append(condition.toString());
		return r.toString();
	}

	public PositionRange getFrom() {
		return from;
	}

	public PositionRange getTo() {
		return to;
	}
}