package com.perisic.beds.questionnaire;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Vector;

import com.perisic.beds.rmiinterface.Question;

/**
 * This class provides represents a set of answered questions and methods are provided 
 * to display these answers in various formats: raw data, bar chart, JSON. 
 * To do: add more methods to systematically analyse the answers according to the needs 
 * of the project. 
 * @author ME
 */

public class Answers {

	private Vector<Question> myData; 
	
	/** 
	 * Initialises the object with the data to be analysed. 
	 * @param myData a vector with answered questions.
	 */
	public Answers(Vector<Question> myData) {
		super();
		this.myData = myData;
	}

	/**
	 * This method is an example of a simple POST request to send JSON data to a web service and retrieves
	 * a bar chart with the results. 
	 * @param what The data encoded as JSON. 
	 * @return An URL from where to retrieve a bar chart with the data. 
	 * @throws Exception
	 */

	private String getImageFromData(String what) {

		try {
			String url = "http://perisic.com/uob/analysis.php";
			URL obj;

			obj = new URL(url);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "CIS007-3 Example Code 18/19");


			String urlParameters = "data="+what;

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString(); 
		} catch (Exception e) {
			System.out.println("Something went wrong with connecting to the server.");
			e.printStackTrace();
			return null; 
		}

	}
	/**
	 * Provides a very basic summary of the answers. 
	 * @return basic statistics. 
	 */

	public String basicAnalysis() {	
		StringBuffer report = new StringBuffer(); 
		for(int i = 0; i < myData.size(); i++ ) { 
			Question qq = myData.elementAt(i); 

			report.append(qq.getQuestionText()); 
			String [] answers = qq.getAnswers(); 			
			for(int j = 0; j < answers.length; j++ ) { 
				report.append(" "+answers[j]+" - "+ qq.getFrequency(answers[j])+";");
			}
			report.append(System.lineSeparator()); 
		}
		return report.toString(); 


	}
	/**
	 * Accepts json data ad produces a jpg image of a bar chart, based on the data. 
	 * @return URL that links to the jpg image. 
	 */

	public String getBarChartURL() { 
		return getImageFromData(getJSON()); 
	}

	/** 
	 * translates the data into JSON format. 
	 * @param questions
	 * @return
	 */

	public String getJSON() {

		String report = "{"; 
		for(int i = 0; i < myData.size(); i++ ) { 
			Question qq = myData.elementAt(i);
			if( i > 0 ) { report += ","; }
			report += "\"" + qq.getQuestionText() +"\": {"; 

			String [] answers = qq.getAnswers(); 			
			for(int j = 0; j < answers.length; j++ ) { 
				if( j > 0 ) { report += ","; } 
				report += "\""+answers[j]+"\" : "+ qq.getFrequency(answers[j]);
			}
			report += "}";
		}
		report += "}"; 
		return report; 
	}


}
