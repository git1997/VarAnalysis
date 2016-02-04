package edu.iastate.webtesting.evaluation;

import edu.iastate.webtesting.util_clone.Config;

/**
 * 
 * @author HUNG
 *
 */
public class Config2 {
	public static final String SUBJECT_SYSTEM_FOLDER = "/Work/Eclipse/Repositories/WebTesting/quercus-4.0.39/WebContent/WebApps/" + Config.SUBJECT_SYSTEM;
	public static String[] SUBJECT_SYSTEM_ENTRIES; 
	static {
		if (Config.SUBJECT_SYSTEM.equals("AddressBook-6.2.12")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					"birthdays.php",
					//"csv.php", // Doesn't appear in test cases
					"delete.php",
					//"diag.php", // Doesn't appear in test cases
					//"doodle.php", // Doesn't appear in test cases
					"edit.php",
					"export.php",
					"group.php",
					"import.php",
					//"include/format.inc.php", // header
					//"index.json.php", // Doesn't appear in test cases
					"index.php",
					//"map.php", // Doesn't appear in test cases
					//"photo.php", // Doesn't appear in test cases
					//"preferences.php", // Doesn't appear in test cases
					//"translate.php", // Doesn't appear in test cases
					//"vcard.php", // Doesn't appear in test cases
					"view.php",
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("SchoolMate-1.5.4")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {		
					//"header.php", // header
					"index.php"
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("TimeClock-1.04")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					"admin/chngpasswd.php",
					"admin/dbupgrade.php",
					"admin/groupadmin.php",
					"admin/groupcreate.php",
					"admin/groupdelete.php",
					"admin/groupedit.php",
					//"admin/header.php", // header
					//"admin/header_colorpick.php", // header
					//"admin/header_date.php", // header
					//"admin/header_get.php", // header
					//"admin/header_get_sysedit.php", // header
					//"admin/header_post.php", // header
					//"admin/header_post_sysedit.php", // header
					"admin/index.php",
					"admin/officeadmin.php",
					"admin/officecreate.php",
					"admin/officedelete.php",
					"admin/officeedit.php",
					"admin/statusadmin.php",
					"admin/statuscreate.php",
					"admin/statusdelete.php",
					"admin/statusedit.php",
					"admin/sysedit.php",
					"admin/timeadd.php",
					"admin/timeadmin.php",
					"admin/timedelete.php",
					"admin/timeedit.php",
					"admin/useradmin.php",
					"admin/usercreate.php",
					"admin/userdelete.php",
					"admin/useredit.php",
					"admin/usersearch.php",
					//"header.php", // header
					"login.php",
					"login_reports.php",
					"reports/audit.php",
					//"reports/header_get_reports.php", // header
					//"reports/header_post_reports.php", // header
					"reports/index.php",
					"reports/timerpt.php",
					"reports/total_hours.php",
					"timeclock.php",
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("UPB-2.2.7")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					//"about_image.php", // Doesn't appear in test cases
					"admin.php",
					"admin_badwords.php",
					"admin_banuser.php",
					"admin_checkupdate.php",
					"admin_config.php",
					"admin_dbsize.php",
					"admin_forums.php",
					"admin_icons.php",
					"admin_iplog.php",
					"admin_iplog_action.php",
					"admin_members.php",
					"admin_restore.php",
					"admin_smilies.php",
					"ajax.php",
					"board_faq.php",
					//"complete_update.php", // Doesn't appear in test cases
					"delete.php",
					//"downloadattachment.php", // Doesn't appear in test cases
					//"editpost.php", // Doesn't appear in test cases
					//"email.php", // Doesn't appear in test cases
					//"getpass.php", // Doesn't appear in test cases
					"index.php",
					//"install.php", // Doesn't appear in test cases
					"login.php",
					//"logoff.php", // Doesn't appear in test cases
					"managetopic.php",
					//"more_smilies.php", // Doesn't appear in test cases
					//"newpm.php", // Doesn't appear in test cases
					"newpost.php",
					//"pmblocklist.php", // Doesn't appear in test cases
					"pmsystem.php",
					"profile.php",
					"register.php",
					"search.php",
					"showmembers.php",
					//"test.php", // Doesn't appear in test cases
					//"update.php", // Doesn't appear in test cases
					//"update1_0.php", // Doesn't appear in test cases
					//"update2_2_1.php", // Doesn't appear in test cases
					//"update2_2_2.php", // Doesn't appear in test cases
					//"update2_2_3.php", // Doesn't appear in test cases
					//"update2_2_4.php", // Doesn't appear in test cases
					//"update2_2_5.php", // Doesn't appear in test cases
					//"upgrade.php", // Doesn't appear in test cases
					"viewforum.php",
					//"viewpm.php", // Doesn't appear in test cases
					//"viewpm_simple.php", // Doesn't appear in test cases
					"viewtopic.php",
					//"viewtopic_simple.php", // Doesn't appear in test cases
					//"xml.php", // Doesn't appear in test cases
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("WebChess-1.0.0")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					"chess.php",
					"index.php",
					//"install.php", // Doesn't appear in test cases
					//"inviteplayer.php", // Doesn't appear in test cases
					"mainmenu.php",
					"newuser.php",
					//"opponentspassword.php", // Doesn't appear in test cases
					"sendmessage.php",
					"viewmessage.php",
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("WordPress-4.3.1")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					"index.php",
			};
		}
		else if (Config.SUBJECT_SYSTEM.equals("OsCommerce-2.3.4")) {
			SUBJECT_SYSTEM_ENTRIES = new String[] {
					"index.php",
			};
		}
	}
	
	public static final String DATA_MODEL_FILE 				= "/Users/HUNG/Desktop/Web Testing Project/Data/" + Config.SUBJECT_SYSTEM + "/data-model.xml";
	public static final String DATA_MODEL_GENERATED_FILE 	= "/Users/HUNG/Desktop/Web Testing Project/Data/" + Config.SUBJECT_SYSTEM + "/data-model-generated.xml";
	public static final String DEBUG_INFO_FOLDER 			= "/Users/HUNG/Desktop/Web Testing Project/Data/" + Config.SUBJECT_SYSTEM + "/DebugInfo";
	
	public static final String OUTPUT_COVERAGE_DATA_MODEL_FILE 		= "output-coverage-datamodel.xml";
	public static final String OUTPUT_COVERAGE_CMODEL_FILE 			= "output-coverage-cmodel.txt";
	public static final String STRING_COVERAGE_FILE 				= "string-coverage.txt";
	public static final String DECISION_COVERAGE_FILE 				= "decision-coverage.txt";
	public static final String STATEMENT_COVERAGE_FILE 				= "statement-coverage.txt";
	public static final String BRANCH_COVERAGE_FILE 				= "branch-coverage.txt";
	public static final String VALIDATION_BUG_COVERAGE_CLIENT_FILE 	= "validation-bug-coverage-client.txt";
	public static final String VALIDATION_BUG_COVERAGE_SERVER_FILE 	= "validation-bug-coverage-server.txt";
	public static final String VALIDATION_BUG_COVERAGE_FILE 		= "validation-bug-coverage.txt";
	public static final String SPELLING_BUG_COVERAGE_CLIENT_FILE 	= "spelling-bug-coverage-client.txt";
	public static final String SPELLING_BUG_COVERAGE_SERVER_FILE 	= "spelling-bug-coverage-server.txt";
	public static final String SPELLING_BUG_COVERAGE_FILE 			= "spelling-bug-coverage.txt";
	public static final String PHP_BUG_COVERAGE_FILE 				= "php-bug-coverage.txt";
}
