import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuestionBank {
	
	// holds the questions
	private ArrayList<String> questions = new ArrayList<String>();
	
	// holds the choices
	private ArrayList<String> choices = new ArrayList<String>();
	
	// holds the answers for the questions
	private ArrayList<String> answers = new ArrayList<String>();
	
	// initiate the files
	private File myQuestions = new File("src/files/questions.txt");
	private File answerKey = new File("src/files/answers.txt");
	
	// for finding the choices and answers of the question
	private int currentQuestionIndex = 0;

	private SecureRandom random = new SecureRandom();
	
	// constructor for this class
	QuestionBank() {
		
		/*****Sets up the Questions and Choices array*****/
		
		// put the content of the file into the array questions and choices
		try {
			
			Scanner input = new Scanner(myQuestions);
			
			// while there is text in file
			while(input.hasNextLine()) {
				
				String [] br = input.nextLine().split(":");
				questions.add(br[0]);
				choices.add(br[1]);
			}
			
			// close the scanner
			input.close();
			
		} catch (IOException e) {
			System.out.println("Something went wrong while reading the file...");
		}
		
		/*****Sets up the Answers array*****/
			
		// read in content of file into input and then add each line to answers array
		try {
			Scanner input = new Scanner(answerKey);
			
			// while there is text in file
			while(input.hasNextLine()) {
				
				// add it to answers array
				String answer = input.nextLine();
				answers.add(answer);
			}
			
			// close the scanner
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Something went wrong while reading the file...");
		}
	}
	
	// get a question from the question bank
	public String getQuestion() {
		
		// return a random question
		int question = random.nextInt(questions.size());
		currentQuestionIndex = question;
		return questions.get(question);
	}
	
	// get a question from the question bank
	public String getChoices() {
		return choices.get(currentQuestionIndex);
	}
	
	// get the answer to a question
	public String getAnswer() {
		return answers.get(currentQuestionIndex);
	}
}
