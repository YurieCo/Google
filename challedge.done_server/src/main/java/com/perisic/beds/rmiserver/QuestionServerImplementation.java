package com.perisic.beds.rmiserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.perisic.beds.configurable.QuestionDAO;
import com.perisic.beds.rmiinterface.Question;
import com.perisic.beds.rmiinterface.RemoteQuestions;

/**
 * Implementation of the questionnaire. Note that chosen answers are collected
 * in this object as well. That means that if the object is destroyed, for
 * instance server restart the collected data is all gone. To do: make data
 * persistent, for instance link collected data to a database or save data in a
 * text file.
 * 
 * @author ME
 *
 */

public class QuestionServerImplementation extends UnicastRemoteObject implements RemoteQuestions {
	private static final long serialVersionUID = -3763231206310491048L;
	Vector<Question> myQuestions = new Vector<Question>();
	private final static QuestionDAO questionDAO = new QuestionDAO(); // that class is used for grabbing / put data to database
	private boolean has_been_update = false; //this is a marker which is used for

	/**
	 * All questions and answers are initialised in the constructor of this class.
	 * To do: read questions and options from an external data file.
	 * 
	 * @throws RemoteException
	 */

	private void readDataFromExternalFile(String source) {
		String line = "";
		String cvsSplitBy = ";";
		try (BufferedReader br = new BufferedReader(new FileReader(source))) {

			// read data from file
			while ((line = br.readLine()) != null) {
				String[] data = line.split(cvsSplitBy);
				String question = data[0];
				myQuestions.add(new Question(question, Arrays.copyOfRange(data, 1, data.length)));
			}


			System.out.println("successful load data and file " + source + " will be removed from disk");
			br.close();
			File file = new File(source);
			if (file.delete()) {
				System.out.println("file removed from disk");
			} else {
				System.out.println("unable to remove");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this function read config from config file and grabs the data
	 * 
	 * @param source is filepath
	 */
	private void readConfig(String source) {
		Path path = Paths.get(source);

		if (Files.exists(path)) {
			System.out.println("attemp to load data from file " + source);
			readDataFromExternalFile(source);
		}
	}

	QuestionServerImplementation() throws RemoteException {
		super();
		Properties prop;

		try (FileInputStream stream = new FileInputStream("src/main/resources/config.properties")) {
			prop = new Properties();
			prop.load(stream);
			
			readConfig(prop.getProperty("cvs_file"));
			
			if (prop.getProperty("persistance", null).equalsIgnoreCase("True")) {
				Vector<Question> tempdata = questionDAO.fetchQuestions();
				myQuestions.addAll(tempdata); // add all data from database to
			}
			// first we load data from file and add it to database
			

		} catch (Exception e) {
			e.printStackTrace();
			readConfig("file_doesnt_exits.txt");
		}


	}

	/**
	 * Implementation of remote interface method.
	 */
	@Override
	public int getNumberOfQuestions() throws RemoteException {
		return myQuestions.size();
	}

	/**
	 * Implementation of remote interface method.
	 */
	@Override
	public Question getQuestion(int i) throws RemoteException {
		return myQuestions.elementAt(i);
	}

	/**
	 * Implementation of remote interface method.
	 */
	@Override
	public void submitAnswer(int i, String answer) throws RemoteException {
		myQuestions.elementAt(i).addAnswer(answer);
		has_been_update = false;
	}

	/**
	 * Implementation of remote interface method.
	 */
	@Override
	public Vector<Question> getData() {
		if (has_been_update != true) {
			questionDAO.feedData(myQuestions);
			has_been_update = true;
		}
		
		return myQuestions;
	}
	
	

}
