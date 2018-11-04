package com.perisic.beds.rmiserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

import com.perisic.beds.rmiinterface.Question;
import com.perisic.beds.rmiinterface.RemoteQuestions;

import utils.HibernateUtil;

/**
 * Use this class to start the server.
 * 
 * @author ME
 *
 */
public class StartServer {

	/**
	 * this function is used to set up classpath enviroment variables 
	 * which is need in order to say where the class that implements interface are
	 */
	private static void setCLASSPATH() {
		final String PATH_TO_BIN = "target/bin ";
		List<String> CLASSPATH_METADATA = Arrays.asList(new String[] {
				PATH_TO_BIN + RemoteQuestions.class.getCanonicalName(), PATH_TO_BIN + Question.class.getCanonicalName(),
				org.hibernate.collection.internal.PersistentMap.class.getCanonicalName(),
				"src "+HibernateUtil.class.getCanonicalName()});

		String CLASSPATH = String.join(";", CLASSPATH_METADATA);
		System.out.println(CLASSPATH);
		System.setProperty("CLASSPATH", CLASSPATH);

	}

	/**
	 * Entry point of the server.
	 * 
	 * @param args not used.
	 */

	public static void main(String[] args) {
		RemoteQuestions questions = null;
		HibernateUtil.loadConfig("src/main/resources/connection.properties");// load once hibernate configuration
		setCLASSPATH(); //set classpath otherwise 

		System.out.println("Attempting to start the Question Server...");
		try {
			questions = new QuestionServerImplementation();

			Registry reg = LocateRegistry.createRegistry(1099);
			reg.rebind("QuestionService1819", questions);
			
			System.out.println("Service started. Welcome to the RMI Question Service!");

		} catch (RemoteException e) {
			System.out.println("An error occured: " + e.toString());
			e.printStackTrace();
		}
		
		
	}

}
