package edu.iastate.webslice.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.iastate.analysis.references.detection.DataFlowManager;
import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.instrumentation.WebDebugger;
import edu.iastate.symex.php.nodes.StatementNode;
import edu.iastate.symex.util.Timer;
import edu.iastate.webslice.core.ShowStatisticsOnReferences;

/**
 * 
 * @author HUNG
 *
 */
public class Evaluation {
	
	private static String ADDRESS_BOOK 	= "AddressBook";
	private static String SCHOOL_MATE 	= "SchoolMate";
	private static String TIME_CLOCK 	= "TimeClock";
	private static String UPB 			= "UPB";
	private static String WEB_CHESS 	= "WebChess";
	
	private static String projectName = ADDRESS_BOOK;
										//SCHOOL_MATE;
										//TIME_CLOCK;
										//UPB;
										//WEB_CHESS;
	
	public static boolean DISCARD_CROSS_ENTRY_EDGES = false;
	
	private static HashMap<String, String> projectNameToPath = new HashMap<String, String>();
	private static HashMap<String, List<String>> projectNameToEntries = new HashMap<String, List<String>>();
		
	static {
		projectNameToPath.put(ADDRESS_BOOK, "/Work/Data/Web Projects/Server Code/AddressBook-6.2.12");
		projectNameToEntries.put(ADDRESS_BOOK, Arrays.asList(new String[] {
			"birthdays.php",
			"csv.php",
			"diag.php",
			"doodle.php",
			"edit.php",
			"export.php",
			"group.php",
			"import.php",
			"index.php",
			"map.php",
			"photo.php",
			"preferences.php",
			"translate.php",
			"vcard.php",
			"view.php"
		}));
		
		projectNameToPath.put(SCHOOL_MATE, "/Work/Data/Web Projects/Server Code/SchoolMate-1.5.4");
		projectNameToEntries.put(SCHOOL_MATE, Arrays.asList(new String[] {
			"index.php"
		}));
		
		projectNameToPath.put(TIME_CLOCK, "/Work/Data/Web Projects/Server Code/TimeClock-1.04");
		projectNameToEntries.put(TIME_CLOCK, Arrays.asList(new String[] {
			"display.php",
			"index.php",
			"login_reports.php",
			"login.php",
			"logout.php",
			"phpweather.php",
			"timeclock.php"
		}));
		
		projectNameToPath.put(UPB, "/Work/Data/Web Projects/Server Code/UPB-2.2.7");
		projectNameToEntries.put(UPB, Arrays.asList(new String[] {
			"about_image.php",
			"admin_badwords.php",
			"admin_banuser.php",
			"admin_checkupdate.php",
			"admin_config.php",
			"admin_dbsize.php",
			"admin_forums.php",
			"admin_icons.php",
			"admin_iplog_action.php",
			"admin_iplog.php",
			"admin_members.php",
			"admin_navigation.php",
			"admin_restore.php",
			"admin_smilies.php",
			"admin.php",
			"ajax.php",
			"board_faq.php",
			"complete_update.php",
			"delete.php",
			"downloadattachment.php",
			"editpost.php",
			"getpass.php",
			"index.php",
			"login.php",
			"logoff.php",
			"managetopic.php",
			"more_smilies.php",
			"newpm.php",
			"newpost.php",
			"pmblocklist.php",
			"pmsystem.php",
			"profile.php",
			//"register.php",  // takes too long
			"search.php",
			"showmembers.php",
			"update.php",
			"upgrade.php",
			"viewforum.php",
			"viewpm_simple.php",
			"viewtopic.php",
			"xml.php"
		}));
		
		projectNameToPath.put(WEB_CHESS, "/Work/Data/Web Projects/Server Code/webchess-1.0.0");
		projectNameToEntries.put(WEB_CHESS, Arrays.asList(new String[] {
			"index.php",
			"inviteplayer.php",
			"mainmenu.php",
			"newuser.php",
			"opponentspassword.php",
			"sendmessage.php",
			"viewmessage.php"
		}));
	}

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		String projectPath = projectNameToPath.get(projectName);
		List<String> entries = projectNameToEntries.get(projectName);
		
		Timer timer = new Timer();
		CountExecutedStatements countExecutedStatements = new CountExecutedStatements();
		WebDebugger.setListener(countExecutedStatements);
		
		ReferenceManager referenceManager = new ReferenceManager();
		for (String entry : entries) {
			ReferenceManager refManager = new ReferenceDetector().detect(new File(projectPath, entry));
			referenceManager.getDataFlowManager().addDataFlows(refManager.getDataFlowManager());
		}
		
		WebDebugger.setListener(null);
		String time = timer.getElapsedSecondsInText();
		
		System.out.println(new ShowStatisticsOnReferences().showStatistics(referenceManager));
		
		System.out.println("========== INFEASIBLE EDGES ==========");
		System.out.println("Infeasible edges: " + (DataFlowManager.totalEdges - DataFlowManager.feasibleEdges) + " / " + DataFlowManager.totalEdges);
		
		System.out.println("========== EXECUTION SUMMARY ==========");
		System.out.println("Entries: " + entries.size());
		System.out.println("Executed statements: " + countExecutedStatements.getCount());
		System.out.println("Time: " + time );
	}
	
	/**
	 * This class is used to count executed statements.
	 */
	static class CountExecutedStatements implements WebDebugger.IListener {
		
		private int count = 0;

		@Override
		public void onStatementExecuteStart(StatementNode statement, Env env) {
			count++;
		}

		@Override
		public void onStatementExecuteEnd(StatementNode statement, Env env) {
		}
		
		public int getCount() {
			return count;
		}
		
	}
	
}
