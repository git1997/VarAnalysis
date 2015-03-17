package edu.iastate.webslice.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author HUNG
 *
 */
public class SubjectSystems {
	
	public static String ADDRESS_BOOK	= "AddressBook";
	public static String SCHOOL_MATE 	= "SchoolMate";
	public static String TIME_CLOCK 	= "TimeClock";
	public static String UPB 			= "UPB";
	public static String WEB_CHESS 		= "WebChess";
	
	public static String projectName = 	ADDRESS_BOOK;
										//SCHOOL_MATE;
										//TIME_CLOCK;
										//UPB;
										//WEB_CHESS;
	
	public static HashMap<String, String> projectNameToPath = new HashMap<String, String>();
	public static HashMap<String, List<String>> projectNameToEntries = new HashMap<String, List<String>>();
		
	static {
		projectNameToPath.put(ADDRESS_BOOK, "/Work/Data/Web Projects/Server Code/AddressBook-6.2.12");
		projectNameToPath.put(SCHOOL_MATE, "/Work/Data/Web Projects/Server Code/SchoolMate-1.5.4");
		projectNameToPath.put(TIME_CLOCK, "/Work/Data/Web Projects/Server Code/TimeClock-1.04");
		projectNameToPath.put(UPB, "/Work/Data/Web Projects/Server Code/UPB-2.2.7-installed");
		projectNameToPath.put(WEB_CHESS, "/Work/Data/Web Projects/Server Code/webchess-1.0.0");
	}
	
	static {
		projectNameToEntries.put(ADDRESS_BOOK, Arrays.asList(new String[] {
				"birthdays.php",
				"csv.php",
				"delete.php",
				"diag.php",
				"doodle.php",
				"edit.php",
				"export.php",
				"group.php",
				"import.php",
				//"include/format.inc.php", // header
				"index.json.php",
				"index.php",
				"map.php",
				"photo.php",
				"preferences.php",
				"translate.php",
				"vcard.php",
				"view.php",
		}));
		
		projectNameToEntries.put(SCHOOL_MATE, Arrays.asList(new String[] {
				//"header.php", // header
				"index.php",
		}));
		
		projectNameToEntries.put(TIME_CLOCK, Arrays.asList(new String[] {
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
		}));
		
		projectNameToEntries.put(UPB, Arrays.asList(new String[] {
				"about_image.php",
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
				"complete_update.php",
				"delete.php",
				"downloadattachment.php",
				"editpost.php",
				"email.php",
				"getpass.php",
				"index.php",
				"install.php",
				"login.php",
				"logoff.php",
				"managetopic.php",
				"more_smilies.php",
				"newpm.php",
				"newpost.php",
				"pmblocklist.php",
				"pmsystem.php",
				"profile.php",
				"register.php",
				"search.php",
				"showmembers.php",
				"test.php",
				"update.php",
				"update1_0.php",
				"update2_2_1.php",
				"update2_2_2.php",
				"update2_2_3.php",
				"update2_2_4.php",
				"update2_2_5.php",
				"upgrade.php",
				"viewforum.php",
				"viewpm.php",
				"viewpm_simple.php",
				"viewtopic.php",
				"viewtopic_simple.php",
				"xml.php",
		}));
		
		projectNameToEntries.put(WEB_CHESS, Arrays.asList(new String[] {
				"chess.php",
				"index.php",
				"install.php",
				"inviteplayer.php",
				"mainmenu.php",
				"newuser.php",
				"opponentspassword.php",
				"sendmessage.php",
				"viewmessage.php",
		}));
	}
	
	public static String projectPath = projectNameToPath.get(projectName);
	public static List<String> projectEntries = projectNameToEntries.get(projectName);

}
