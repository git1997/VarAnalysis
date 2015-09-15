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
	0. Install Eclipse for Java and PHP Development Tools (PDT) on top of Eclipse.
	1. Experiment with the following files:
		+ Symex/src/main/java/edu/iastate/symex/run/RunSymexForFile.java: Run Symex 
		+ WebParsers/src/main/java/edu/iastate/parsers/html/run/RunVarisForFile.java: Run Varis
		+ WebParsers/src/main/java/edu/iastate/analysis/references/detection/RunReferenceDetectorForFile.java: Run WebSlice
	2. Experiment with the Eclipse views provided by SymexUI and WebParsersUI.

Contact
=======
Please feel free to contact the author if you have any questions.

FAQs
=======
	1. What is the version of Eclipse that you use?
		I'm using Eclipse for Scala, but I think the plug-ins should work for other Eclipse environments as well. Here is what's written in the About section of my current IDE: "Scala IDE build of Eclipse SDK ... Build id: 3.0.1-vfinal-20130718-1727-Typesafe ... Visit http://scala-ide.org/ ... Eclipse SDK Version: 3.7.2".
	2. When I import these projects into my Eclipse, there are some compiling errors. What should I do?
		Some of these projects depend on other projects. Make sure you're not missing dependencies among projects (e.g., the featureexprlib project should be included from Symex). Also, make sure that you have installed PHP Development Tools (PDT) on top of Eclipse.
	3. How do I install PHP Development Tools (PDT)?
		http://stackoverflow.com/questions/2397978/how-to-install-a-php-ide-plugin-for-eclipse-directly-from-the-eclipse-environmen
		https://eclipse.org/pdt/ (Scroll down to Update existing Eclipse)
	4. When I run Symex, I have a FileNotFoundException error. How can I fix it?
		The input to Symex is a PHP file so you should change the PHP_FILE constant in RunSymexForFile.java to a file on your computer.
	5. I am confused about the SymexUI project, how do I use it?
		SemexUI is for creating a plug-in in Eclipse that shows the result of symbolic execution in a tree view. If you just want to experiment with the symbolic execution engine without a GUI, you can ignore it for now, and come back to it later once you are more familiar with Eclipse plug-in development in general (http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Ffirstplugin.htm)
	6. When I click on some buttons like "Run Analysis", nothing will show (I have imported some PHP projects to it). Could you give me some advice?
		Those buttons generally work for a PHP file that is currently active in Eclipse. First you need to select a PHP file in your PHP project, then open it. Then you click on one of the buttons, it will analyze that file and show some results.
	7. What is the best way to debug an issue?
		You can run in Debug mode and follow what happens when a button is clicked. It may require some familiarity with Eclipse plugin development. 
		Also, observe the Console view in the main Eclipse window, you will see error messages there if any, that may give some clue what the problem is.
