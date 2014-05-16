import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import java.util.Scanner;
import java.io.FileNotFoundException;

public class DBDtoPHP {

	/*Constants corresponding to the menu choices*/
	public static final int TEXT = 1;
	public static final int PHONE = 2;
	public static final int EMAIL = 3;
	public static final int CURRENCY = 4;
	public static final int DATE = 5;
	public static final int CHECKBOX = 6;
	public static final int BOLDSECTION = 7;
	public static final int QUIT = 9;

	public static String FIELDNAME;
	public static Integer FIELDSIZE;
	public static Double FIELDDIVIDE;
	public static Double LEFTSIZE;
	public static Double RIGHTSIZE;
	public static Double TOPSIZE;
	public static boolean SAMELINE = false;

	// main method. Driver for the whole program
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			System.out.println("Unable to set look at feel to local settings. " +
					"Continuing with default Java look and feel.");
		}
		try {

			/*Read in the file*/
			Scanner fileScanner = new Scanner(getFile());
			int choice;
			String formTitle;
			String textBeforeField;
			String textAfterField;
			Scanner keyboard;

			String[] fieldNames = new String[100];
			double[] topSizes = new double[100];
			int[] fieldSizes = new int[100];
			int i = 0;

			/*Create a new file*/
			PrintWriter output = new PrintWriter("GeneratedHTML.php", "UTF-8");
			
			/*Append to an existing php file*/
			/*PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("misc_adden.php", true)));*/
			
			writeTopSpacer(output);
			writeHeader(output);
			keyboard = new Scanner(System.in);
			formTitle = getField(keyboard, "Enter the title of the form ");
			pageTitle(output, formTitle);

			/*Ignore non-custom fields*/
			while (fileScanner.hasNextLine()){

				getFieldName(fileScanner);
				getTop(fileScanner);
				getLeft(fileScanner);
				getRight(fileScanner);

				fieldNames[i] = FIELDNAME;
				topSizes[i] = TOPSIZE;
				fieldSizes[i] = (int) ((RIGHTSIZE - LEFTSIZE)/6);

				i++;
			}

			for (int j = 0; j < i-1; j++){
				keyboard = new Scanner(System.in);
				showMenu();
				choice = getChoice(keyboard, fieldNames[j]);
				if (choice == QUIT)
					break;
				textBeforeField = getField(keyboard, "Enter a title for the field ");
				textAfterField = getField(keyboard, "Enter text to appear after the field ");


				/*Find out if it's on the same line*/
				if (j!= 0){
					if (topSizes[j] - topSizes[j-1] < 5)
						SAMELINE = true;
					else
						SAMELINE = false;
					closeTable(output); //Closing the previous field's table
				}

				/*Call the appropriate function for the type of field*/
				if( choice == TEXT )
					text(output, fieldSizes[j], textBeforeField, fieldNames[j], textAfterField);
				else if( choice == PHONE )
					phone(output, fieldSizes[j], textBeforeField, fieldNames[j], textAfterField);
				else if( choice == EMAIL)
					email(output, fieldSizes[j], textBeforeField, fieldNames[j], textAfterField);
				else if( choice == CURRENCY )
					currency(output, fieldSizes[j], textBeforeField, fieldNames[j], textAfterField);
				else if( choice == DATE)
					date(output, fieldSizes[j], textBeforeField, fieldNames[j], textAfterField);
				else if( choice == CHECKBOX)
					checkbox(output, textBeforeField, fieldNames[j], textAfterField);
				else if( choice == BOLDSECTION){
					boldsection(output, textBeforeField);
					j--;
				}
				else
					System.out.println("\n\nGoodbye.");

			}
			/*Close the table of the last field name*/
			SAMELINE = false;
			closeTable(output);

			fileScanner.close();
			writeFooter(output);
			output.close();
			System.out.println("\n\nPHP file created!");
			
			/*Open the file after the PHP file is created*/
			if (Desktop.isDesktopSupported()) {
			    Desktop.getDesktop().open((new File("/Users/Andrew/Documents/workspace/HTML Writer/GeneratedHTML.php")));
			}
			
			
		}
		catch(FileNotFoundException e) {
			System.out.println("Problem reading the data file. Exiting the program." + e);
		} 

		/*Catch statement*/
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();}

	}

	private static void pageTitle(PrintWriter output, String title){
		output.println("\t\t\t<TR align=\"left\" valign=\"top\">");
		output.println("\t\t\t\t<TD><SPAN class=\"title\">" + title + "</SPAN></TD>");
		output.println("\t\t\t</TR>");
		output.println("\t\t\t<TR align=\"left\" valign=\"top\">");
		output.println("\t\t\t\t<TD><IMG src=\"/products/forms_online/images/spacer.gif\" alt=\"\" width=\"1\" height=\"10\" border=\"0\"></TD>");
		output.println("\t\t\t</TR>");
		output.println("\t\t\t<TR align=\"left\" valign=\"top\">");
		output.println("\t\t\t\t<TD>");
	}

	private static void getLeft (Scanner fileScanner){

		if (!fileScanner.hasNextLine())
			return;
		String input = fileScanner.nextLine();
		String[] split = input.split("=");
		if (split[0].equals("Left"))
			LEFTSIZE = Double.valueOf(split[1]);
		else
			getLeft(fileScanner);

	}

	private static void getRight (Scanner fileScanner){

		if (!fileScanner.hasNextLine())
			return;
		String input = fileScanner.nextLine();
		String[] split = input.split("=");
		if (split[0].equals("Right"))
			RIGHTSIZE = Double.valueOf(split[1]);
		else
			getRight(fileScanner);

	}

	private static void getTop (Scanner fileScanner){

		if (!fileScanner.hasNextLine())
			return;
		String input = fileScanner.nextLine();
		String[] split = input.split("=");
		if (split[0].equals("Top"))
			TOPSIZE = Double.valueOf(split[1]);
		else
			getTop(fileScanner);

	}


	private static void getFieldName (Scanner fileScanner){

		if (!fileScanner.hasNextLine())
			return;
		String input = fileScanner.nextLine();
		if (input.contains("M_")){
			FIELDNAME = input.substring(1, input.length()-1);
		}
		else
			getFieldName(fileScanner);

	}

	private static void text(PrintWriter output, int fieldsize, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t\t<? echo build_text_field(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"]," + (fieldsize+2) + "," + fieldsize + ", null, true) ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}

	private static void phone(PrintWriter output, int fieldsize, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t\t<? echo build_text_field(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"]," + (fieldsize+2) + "," + fieldsize + ", null, true, \"if (value) {if (checkFormat(value,'phone')) {value=formattedInput; defaultValue=value} else {value=defaultValue; focus(); select()}} else if (value != defaultValue) defaultValue=value\") ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}

	private static void email(PrintWriter output, int fieldsize, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t\t<? echo build_text_field(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"]," + (fieldsize+2) + "," + fieldsize + ", null, true, \"if (value) {if (checkFormat(value,'email')) {value=formattedInput; defaultValue=value} else {value=defaultValue; focus(); select()}} else if (value != defaultValue) defaultValue=value\") ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}

	private static void currency(PrintWriter output, int fieldsize, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t\t<? echo build_text_field(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"]," + (fieldsize+2) + "," + fieldsize + ", null, true, \"if (value) {if (checkFormat(value,'currency'," + (fieldsize-3) + ")) {value=formattedInput; defaultValue=value} else {value=defaultValue; focus(); select()}} else if (value != defaultValue) defaultValue=value\") ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}

	private static void date(PrintWriter output, int fieldsize, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t\t<? echo build_text_field(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"], 12, 10, null, true) ?></TD>");
		output.println("\t\t\t\t\t\t\t<TD><? echo build_date_button(\"" + fieldname + "\") ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}

	private static void checkbox(PrintWriter output, String title, String fieldname, String textAfterField){
		openTable(output, title);
		output.println("\t\t\t\t\t\t\t<? echo build_checkbox(\"" + fieldname + "\", $unit_data[\"" + fieldname +"\"]) ?>");
		if (textAfterField.length()!= 0){
			output.println("\t\t\t\t\t\t\t\t" + textAfterField);}
	}
	
	private static void boldsection(PrintWriter output, String title){
		openTable(output, "<STRONG>" + title + "</STRONG></TD>");
		output.println("\t\t\t\t\t\t</TR>");
		output.println("\t\t\t\t\t\t<TR align=\"left\" valign=\"top\">");
		output.println("\t\t\t\t\t\t\t<TD><IMG src=\"/products/forms_online/images/spacer.gif\" alt=\"\" width=\"1\" height=\"10\" border=\"0\">");
	}

	private static void writeTopSpacer(PrintWriter output){
		output.println("<TR align=\"left\" valign=\"top\">");
		output.println("\t<TD class=\"separator\"><IMG src=\"/products/forms_online/images/spacer.gif\" alt=\"\" width=\"1\" height=\"1\" border=\"0\"></TD>");
		output.println("</TR>");
	}
	
	private static void writeHeader(PrintWriter output){
		output.println("<TR align=\"left\" valign=\"top\">");
		output.println("\t<TD class=\"formsection\">");
		output.println("\t\t<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
	}

	private static void writeFooter(PrintWriter output){
		output.println("\t\t\t\t</TD>");
		output.println("\t\t\t</TR>");
		output.println("\t\t</TABLE>");
		output.println("\t</TD>");
		output.println("</TR>");
	}

	private static void openTable(PrintWriter output, String title){
		if (SAMELINE == false){
			output.println("\t\t\t\t\t<TABLE cellpadding=\"0\" cellspacing=\"3\" border=\"0\">");
			output.println("\t\t\t\t\t\t<TR align=\"left\" valign=\"middle\">");}
		if (SAMELINE == true)
			output.println("\t\t\t\t\t\t\t<TD><IMG src=\"/products/forms_online/images/spacer.gif\" alt=\"\" width=\"10\" height=\"1\" border=\"0\"></TD>");
		output.println("\t\t\t\t\t\t\t<TD>" + title);
	}

	private static void closeTable(PrintWriter output){
		output.println("\t\t\t\t\t\t\t</TD>");
		if (SAMELINE == false){
			output.println("\t\t\t\t\t\t</TR>");
			output.println("\t\t\t\t\t</TABLE>");}
	}

	/*	 get choice from the user
	 *   keyboard != null and is connected to System.in
	 *   return an int that is >= SEARCH and <= QUIT  */
	private static int getChoice(Scanner keyboard, String fieldName) {
		int choice = getInt(keyboard, "Enter field type of " + fieldName);
		keyboard.nextLine();
		while( choice < TEXT || choice > QUIT){
			System.out.println("\n" + choice + " is not a valid choice");
			choice = getInt(keyboard, "Enter numericla choice: ");
			keyboard.nextLine();
		}
		return choice;
	}

	// ensure an int is entered from the keyboard
	// pre: s != null and is connected to System.in
	private static int getInt(Scanner s, String prompt) {
		System.out.print(prompt);
		while( !s.hasNextInt() ){
			s.nextLine();
			System.out.println("That was not an int.");
			System.out.print(prompt);
		}
		return s.nextInt();
	}

	private static String getField(Scanner keyboard, String displayText) {
		String textfield = getText(keyboard, displayText);
		return textfield;
	}

	// ensure an int is entered from the keyboard
	// pre: s != null and is connected to System.in
	private static String getText(Scanner s, String prompt) {
		System.out.print(prompt);
		while( !s.hasNextLine() ){
			s.nextLine();
			System.out.println("That was not a string");
			System.out.print(prompt);
		}
		return s.nextLine();
	}

	/*Show the user menu*/
	private static void showMenu() {
		System.out.println("\nOptions:");
		System.out.println("Enter " + TEXT + " for a text field");
		System.out.println("Enter " + PHONE + " for a phone field");
		System.out.println("Enter " + EMAIL+ " for an email field");
		System.out.println("Enter " + CURRENCY + " for a currency field");
		System.out.println("Enter " + DATE + " for a date field");
		System.out.println("Enter " + CHECKBOX + " for a checkbox");
		System.out.println("Enter " + BOLDSECTION + " to create a title for a section");
		System.out.println("Enter " + QUIT + " to quit.\n");
	}

	/** Method to choose a file using a traditional window.
	 * @return the file chosen by the user. Returns null if no file picked.
	 */
	public static File getFile() {
		// create a GUI window to pick the text to evaluate
		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("Select dbd file.");
		int retval = chooser.showOpenDialog(null);
		File f =null;
		chooser.grabFocus();
		if (retval == JFileChooser.APPROVE_OPTION)
			f = chooser.getSelectedFile();
		return f;
	}
}