package edu.iastate.webtesting.outputcoverage;

/**
 * 
 * @author HUNG
 *
 */
public class OutputCoverage {
	private CModelCoverage cModelCoverage;
	private DataModelCoverage dataModelCoverage;
	
	public OutputCoverage(CModelCoverage cModelCoverage, DataModelCoverage dataModelCoverage) {
		this.cModelCoverage = cModelCoverage;
		this.dataModelCoverage = dataModelCoverage;
	}
	
	public CModelCoverage getCModelCoverage() {
		return cModelCoverage;
	}
	
	public DataModelCoverage getDataModelCoverage() {
		return dataModelCoverage;
	}
	
	/*
	 * Utility methods
	 */
	
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("========== CModel Coverage ==========" + System.lineSeparator());
		str.append(cModelCoverage.toDebugString());
		str.append("========== DataModel Coverage ==========" + System.lineSeparator());
		str.append(dataModelCoverage.toDebugString());
		return str.toString();
	}
}
