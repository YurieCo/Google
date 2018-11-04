package com.perisic.beds.peripherals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.perisic.beds.questionnaire.QuestionSet;

/**
 * A Simple Graphical User Interface to implement a questionnaire. To do:
 * Enhance it according to the specific need of the project.
 * 
 * @author ME
 *
 */
public class QuestionsGUI extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1077856539035586635L;

	// The GUI only interacts with the QuestionnairePanel class.
	private QuestionSet questionnaire = new QuestionSet();
	private Properties properties;

	/**
	 * this method is used for processQuestionary
	 */
	private void processQuestionnaire() {

		for (int i = 0; i < questionnaire.numberOfQuestions(); i++) {

			String message = questionnaire.getQuestion(i);
			String[] options = questionnaire.getOptions(i);

			int selectedValue = JOptionPane.showOptionDialog(null, message, "Question " + i, JOptionPane.DEFAULT_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			questionnaire.submitAnswer(i, options[selectedValue]);
		}
	}

	/**
	 * Two possible actions are implemented here: start the questionnaire and
	 * analyses the answers. To do: refactor code to separate into different
	 * concerns; also: password protect the actions (questionnaire, analysis of
	 * data).
	 */
	public void actionPerformed(ActionEvent e) {

		// since the showLogin expression will be evaluete only if
		// e.getSource().equals(nextButton) is true, therefore we do not break any logic
		if (e.getSource().equals(nextButton)
				&& showLogin("password start questionary", (String) properties.get("start_questionary"))) {
			processQuestionnaire();
		} else if (e.getSource().equals(analyzeButton)
				&& showLogin("password for Start show questionary", (String) properties.get("show_questionary"))) {
			questionnaire.reportAnswers();
		}

	}
	
	/**
	 * 
	 * @param title string which will be used when building window
	 * @param trueValue if case that input value to password field matches to the input value then it return true
	 * @return true or false depending on the 
	 */
	
	private boolean showLogin(String title, String trueValue) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter a password:");
		JPasswordField pass = new JPasswordField(10);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		// bellow we check once it hit ok button we check if password are equals to the
		// one which is in the restriction.properties
		String password = new String(pass.getPassword());
		if (option == 0 && password.equals(trueValue)) // pressing OK button
		{
			return true;
		}

		return false;
	}

	JButton nextButton = new JButton("Start Questionnaire");
	JButton analyzeButton = new JButton("Analyze Answers");

	/**
	 * Constructor
	 */

	private QuestionsGUI() {
		super();
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();

		panel.add(nextButton);
		nextButton.addActionListener(this);

		panel.add(analyzeButton);
		analyzeButton.addActionListener(this);

		getContentPane().add(panel);
		panel.repaint();

		loadRestriction("src/main/resources/rectriction.properties");
	}

	/**
	 * load restrictions, like password for questionary and password for show
	 * questionary
	 * 
	 * @param filename: string which is a path to this file
	 */

	private void loadRestriction(String filename) {
		try (FileInputStream stream = new FileInputStream(filename)) {
			properties = new Properties();
			properties.load(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		QuestionsGUI myGUI = new QuestionsGUI();
		myGUI.setVisible(true);

	}

}