# VarAnalysis

Implementation of the following research projects:

	+ PhpSync, "Auto-locating and fix-propagating for HTML validation errors to PHP server-side code", ASE 2011

	+ Varis, "Building Call Graphs for Embedded Client-Side Code in Dynamic Web Applications", FSE 2014

	+ WebSlice, "Cross-language Program Slicing for Dynamic Web Applications", FSE 2015

Source Code Structure
=====================
	+ featureexprlib_2.9.1-0.3.3_plugin		Library for solving constraints
	+ Symex									Implementation of PhpSync (now called Symex)
	+ SymexUI								Eclipse plug-in view for Symex
	+ WebParsers							Implemenation of Varis and WebSlice
	+ WebParsersUI							Eclipse plug-in view for WebParsers

Getting Started
===============
	0. Install Eclipse for Java.
	1. Experiment with the following files:
		+ Symex/src/main/java/edu/iastate/symex/run/RunSymexForFile.java: Run Symex 
		+ WebParsers/src/main/java/edu/iastate/parsers/html/run/RunVarisForFile.java: Run Varis
		+ WebParsers/src/main/java/edu/iastate/analysis/references/detection/RunReferenceDetectorForFile.java: Run WebSlice
	2. Experiment with the Eclipse views provided by SymexUI and WebParsersUI.

Contact
=======
Please feel free to contact the author if you have any questions.
