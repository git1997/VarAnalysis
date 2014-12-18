//package edu.iastate.webslice.evaluation;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//
//import edu.iastate.analysis.references.Reference;
//import edu.iastate.analysis.references.detection.FindReferencesInFile;
//import edu.iastate.analysis.references.detection.ReferenceManager;
//import edu.iastate.symex.analysis.WebSliceEvaluation;
//import edu.iastate.symex.util.Timer;
//import edu.iastate.webslice.core.ShowStatisticsOnReferences;
//
///**
// * 
// * @author HUNG
// *
// * TODO Review this code.
// */
//public class Evaluation {
//	
//	private static String ADDRESS_BOOK = "AddressBook";
//	private static String SCHOOL_MATE = "SchoolMate";
//	private static String TIME_CLOCK = "TimeClock";
//	private static String UPB = "UPB";
//	private static String WEB_CHESS = "WebChess";
//	
//	private static HashMap<String, String> projectNameToPath = new HashMap<String, String>();
//	private static HashMap<String, List<String>> projectNameToEntries = new HashMap<String, List<String>>();
//		
//	static {
//		projectNameToPath.put(ADDRESS_BOOK, "/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12");
//		String[] entriesAddrsesBook = new String[] {
//			"birthdays.php",
//			"csv.php",
//			"diag.php",
//			"doodle.php",
//			"edit.php",
//			"export.php",
//			"group.php",
//			"import.php",
//			"index.php",
//			"map.php",
//			"photo.php",
//			"preferences.php",
//			"translate.php",
//			"vcard.php",
//			"view.php"
//		};
//		projectNameToEntries.put(ADDRESS_BOOK, Arrays.asList(entriesAddrsesBook));
//		
//		projectNameToPath.put(SCHOOL_MATE, "/Work/To-do/Data/Web Projects/Server Code/SchoolMate-1.5.4");
//		String[] entriesSchoolMate = new String[] {
//			"index.php"	
//		};
//		projectNameToEntries.put(SCHOOL_MATE, Arrays.asList(entriesSchoolMate));
//		
//		projectNameToPath.put(TIME_CLOCK, "/Work/To-do/Data/Web Projects/Server Code/TimeClock-1.04");
//		String[] entriesTimeClock = new String[] {
//			"display.php",
//			"index.php",
//			"login_reports.php",
//			"login.php",
//			"logout.php",
//			"phpweather.php",
//			"timeclock.php"
//		};
//		projectNameToEntries.put(TIME_CLOCK, Arrays.asList(entriesTimeClock));
//		
//		projectNameToPath.put(UPB, "/Work/To-do/Data/Web Projects/Server Code/UPB-2.2.7");
//		String[] entriesUPB = new String[] {
//			"about_image.php",
//			"admin_badwords.php",
//			"admin_banuser.php",
//			"admin_checkupdate.php",
//			"admin_config.php",
//			"admin_dbsize.php",
//			"admin_forums.php",
//			"admin_icons.php",
//			"admin_iplog_action.php",
//			"admin_iplog.php",
//			"admin_members.php",
//			"admin_navigation.php",
//			"admin_restore.php",
//			"admin_smilies.php",
//			"admin.php",
//			"ajax.php",
//			"board_faq.php",
//			"complete_update.php",
//			"delete.php",
//			"downloadattachment.php",
//			"editpost.php",
//			"getpass.php",
//			"index.php",
//			"login.php",
//			"logoff.php",
//			"managetopic.php",
//			"more_smilies.php",
//			"newpm.php",
//			"newpost.php",
//			"pmblocklist.php",
//			"pmsystem.php",
//			"profile.php",
//			//"register.php",  // takes too long
//			"search.php",
//			"showmembers.php",
//			"update.php",
//			"upgrade.php",
//			"viewforum.php",
//			"viewpm_simple.php",
//			"viewtopic.php",
//			"xml.php"
//		};
//		projectNameToEntries.put(UPB, Arrays.asList(entriesUPB));
//		
//		projectNameToPath.put(WEB_CHESS, "/Work/To-do/Data/Web Projects/Server Code/webchess-1.0.0");
//		String[] entriesWebChess = new String[] {
//			"index.php",
//			"inviteplayer.php",
//			"mainmenu.php",
//			"newuser.php",
//			"opponentspassword.php",
//			"sendmessage.php",
//			"viewmessage.php"
//		};
//		projectNameToEntries.put(WEB_CHESS, Arrays.asList(entriesWebChess));
//	}
//	
//	private static String projectName = 	ADDRESS_BOOK;
//											//SCHOOL_MATE;
//											//TIME_CLOCK;
//											//UPB;
//											//WEB_CHESS;
//
//	private static HashMap<String, Reference> mapLocationToReference = new HashMap<String, Reference>();
//	
//	private static HashMap<Reference, HashSet<Reference>> graph = new HashMap<Reference, HashSet<Reference>>(); 
//	
//	private static int executedStatements = 0;
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		
//		Timer timer = new Timer();
//		
//		WebSliceEvaluation.listener = new WebSliceEvaluation.IListener() {
//
//			@Override
//			public void onStatementExecute() {
//				Evaluation.onStatementExecute();
//			}
//			
//		};
//		
//		
//		String projectPath = projectNameToPath.get(projectName);
//		List<String> entries = projectNameToEntries.get(projectName);
//		
//		for (String entry : entries) {
//			FindReferencesInFile findReferencesInFiles = new FindReferencesInFile(new File(projectPath + "/" + entry));
//			ReferenceManager referenceManager = findReferencesInFiles.execute();
//			
//			for (Reference reference1 : referenceManager.getReferences) {
//				Reference node1 = getOrCreateNode(reference1);
//				for (Reference reference2 : reference1.getDataFlowToReferences()) {
//					Reference node2 = getOrCreateNode(reference2);
//					graph.get(node1).add(node2);
//				}
//			}
//		}
//		
//		for (Reference reference1 : graph.keySet()) {
//			reference1.clearDataflow();
//			for (Reference reference2 : graph.get(reference1))
//				reference1.addDataflowToReference(reference2);
//		}
//		
//		System.out.println(new ShowStatisticsOnReferences().showStatistics(new ArrayList<Reference>(graph.keySet())));
//		System.out.println("Executed statements: " + executedStatements);
//		
//		System.out.println("[Evaluation] Done in " + timer.getElapsedSecondsInText() + ".");
//	}
//	
//	private static void onStatementExecute() {
//		executedStatements++;
//	}
//	
//	private static Reference getOrCreateNode(Reference reference) {
//		Reference ref = mapLocationToReference.get(reference.toString());
//		if (ref != null)
//			return ref;
//		else {
//			mapLocationToReference.put(reference.toString(), reference);
//			graph.put(reference, new HashSet<Reference>());
//			return reference;
//		}
//	}
//	
//}
