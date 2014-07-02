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
//	public static final String PREF_NODE_BUTTONS_VISIBLE				= "buttons_visibility";
//	public static final String PREF_KEY_BUTTON_DE						= "button_de";
//	public static final String PREF_KEY_BUTTON_UK						= "button_uk";
//	public static final String PREF_KEY_BUTTON_US						= "button_us";
//	public static final String PREF_KEY_BUTTON_ES						= "button_es";
	// preferences for filters 
	public static final String PREF_NODE_PREFERENCES					= "preferences";
	public static final String PREF_NODE_PREFERENCES_GENERAL			= "general";
	public static final String PREF_NODE_LAST_USED_FILTER				= "last_used_filter";
	public static final String PREF_KEY_LAST_USED_FILTER				= "last_used_filter";
	public static final String PREF_KEY_REMOVE_DUPLICATES				= "remove_duplicates";
	public static final String FILTER_OPTION_TRANSFER_TIMESTAMP_FROM 	= "transfer_timestamp_from"; 
	public static final String FILTER_OPTION_TRANSFER_TIMESTAMP_TO 		= "transfer_timestamp_to"; 
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
	
}
