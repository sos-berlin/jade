package com.sos.jade.backgroundservice.constants;

/**
 * @author SP
 *
 */
public abstract class JadeBSConstants {
	// primary nodes names
	public static final String PRIMARY_NODE_MAIN_VIEW 					= "main-view";
	public static final String PRIMARY_NODE_MENU_BAR 					= "menu-bar";
	public static final String PRIMARY_NODE_HISTORY_TABLE 				= "history-table";
	public static final String PRIMARY_NODE_DETAIL_TABLE 				= "detail-table";
	public static final String PRIMARY_NODE_FILTER 						= "filter";
	// preferences for tables display
	public static final String PREF_NODE_ORDER 							= "table_column_order";
	public static final String PREF_NODE_WIDTHS 						= "table_column_widths";
	public static final String PREF_NODE_COLLAPSE 						= "table_column_collapse";
	public static final String PREF_KEY_ORDER 							= "column_order";
	// preferences for languages
	public static final String PREF_NODE_AVAILABLE_LANGS				= "available_languages";
	public static final String PREF_KEY_DE 								= "de";
	public static final String PREF_KEY_UK 								= "uk";
	public static final String PREF_KEY_US 								= "us";
	public static final String PREF_KEY_ES 								= "es";
	// preferences for details
	public static final String PREF_NODE_VISIBLE_DETAILS				= "visible-details";
	// preferences for filters 
	public static final String PREF_NODE_PREFERENCES					= "preferences";
	public static final String PREF_NODE_PREFERENCES_GENERAL			= "general";
	public static final String PREF_NODE_LAST_USED_FILTER				= "last_used_filter";
	public static final String PREF_KEY_LAST_USED_FILTER				= "last_used_filter";
	public static final String PREF_KEY_REMOVE_DUPLICATES				= "remove_duplicates";
	public static final String PREF_KEY_AUTO_REFRESH					= "auto_refresh";
	public static final String FILTER_OPTION_TRANSFER_START_FROM 		= "transfer_start_from"; 
	public static final String FILTER_OPTION_TRANSFER_START_TO 			= "transfer_start_to"; 
	public static final String FILTER_OPTION_PROTOCOL 					= "protocol"; 
	public static final String FILTER_OPTION_STATUS 					= "status"; 
	public static final String FILTER_OPTION_OPERATION 					= "operation"; 
	public static final String FILTER_OPTION_SOURCE_FILE 				= "source_file"; 
	public static final String FILTER_OPTION_SOURCE_HOST 				= "source_host"; 
	public static final String FILTER_OPTION_TARGET_FILE 				= "target_file"; 
	public static final String FILTER_OPTION_TARGET_HOST 				= "target_host"; 
	public static final String FILTER_OPTION_MANDATOR 					= "mandator"; 
	// general
	public static final String DELIMITER 								= "|";
	public static final String ENTRY_DELIMITER 							= ";";
	public static final String DELIMITER_REGEX 							= "[|]";
	public static final String EQUAL_CHAR 								= "=";
	public static final String JS_AUTH_SERVER							= "http://sp:40177";
	
}
