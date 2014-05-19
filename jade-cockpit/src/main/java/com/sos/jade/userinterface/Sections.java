/**
 * 
 */
package com.sos.jade.userinterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KB
 *
 */
public class Sections {

	/**
	 * 
	 */
	public Sections() {
		// TODO Auto-generated constructor stub
	}

	  public List<Section> getSections() {
		    List<Section> lstSections = new ArrayList<Section>();
		    Section Section = new Section();
		    Section.setName("Programming");
		    lstSections.add(Section);
//		    Todo todo = new Todo("Write more about e4");
//		    Section.getTodos().add(todo);
//		    todo = new Todo("Android", "Write a widget.");
//		    Section.getTodos().add(todo);
		    
		    Section = new Section();
		    Section.setName("Leasure");
		    lstSections.add(Section);
//		    todo = new Todo("Skiing");
//		    Section.getTodos().add(todo);
		    
		    return lstSections;
		  }

}
