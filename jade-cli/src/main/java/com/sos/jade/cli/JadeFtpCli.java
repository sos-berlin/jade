/**
 * 
 */
package com.sos.jade.cli;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * @author KB
 *
 */
public class JadeFtpCli {
	
	/**
	 * 
	 */
	public JadeFtpCli() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Vector<String> objA = new Vector<String>();
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				String s;
				s = bufferRead.readLine();
				if (s == null || s.length() <= 0) {
					break;
				}
				objA.add(s);
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (objA.size() > 0) {
			JadeCliWorkerClass objWorker = new JadeCliWorkerClass();
			objWorker.setCommands (objA);
			objWorker.run();
		}
	}
}
