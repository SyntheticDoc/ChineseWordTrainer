/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Deus
 */
public class CSVReader {
    private static CSVReader instance;
    private static ConsoleHandler console;
    
    private CSVReader(ConsoleHandler console) {
	this.console = console;
    }
    
    public static CSVReader getInstance(ConsoleHandler console) {
	if(instance == null) {
	    instance = new CSVReader(console);
	}
	
	return instance;
    }
    
    public ArrayList<Word> readWords() {
	ArrayList<Word> result = new ArrayList<>();
	console.logMsg("Starting to read CSV-Files...");
	
//	for(int i = 1; i < 13; i++) {
//	    for(Word w : readCSVFile_old(i)) {
//		result.add(w);
//	    }
//	}
//	
//	writeToFile(result);
	
	result = readCSVFile();
	
	if(result == null || result.isEmpty()) {
	    return null;
	}
	
	int counter = 0;
	
	for(Word w : result) {
	    if(!w.getLesson().equals("11") && !w.getLesson().equals("12")) {
		counter++;
	    }
	}
	
	System.out.println("Words learned up to L10: " + counter);
	
	console.logMsg("Successfully read " + result.size() + " words");
	
	return result;
    }
    
    public void writeToFile(ArrayList<Word> words) {
	try(Writer wr = new OutputStreamWriter(new FileOutputStream("AllWords.txt"), "UTF-8")) {
	    for(Word w : words) {
		wr.write(w.getCSVString());
		wr.write("\n");
	    }
	} catch (FileNotFoundException e) {
	    console.logErr("ERROR in CSVReader.writeToFile(): Could not write word, Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
	} catch (UnsupportedEncodingException e) {
	    console.logErr("ERROR in CSVReader.writeToFile(): Could not write word, Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
	} catch (Exception e) {
	    console.logErr("ERROR in CSVReader.writeToFile(): Could not write word, Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
	}
    }
    
    private ArrayList<Word> readCSVFile() {
	ArrayList<Word> result = new ArrayList<>();
	
	System.out.println("Reading CSV-File...");
	
	try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("AllWords.txt"), "UTF-8"))) {
	    String line;
	    
	    while((line = reader.readLine()) != null) {
		String[] s1 = line.split("\"");
		
		if(s1.length == 3) {
		    String[] s1_1 = s1[0].split(";");
		    String[] s1_2 = s1[2].split(";");
		    
		    try {
			Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4]);
			result.add(w);
			// console.cprintln("Created word: " + w.toString());
		    } catch (Exception e) {
			console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
			return null;
		    }
		} else if(s1.length == 1) {
		    String[] s1_1 = s1[0].split(";");
		    
		    try {
			Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1_1[4], s1_1[5], s1_1[6], s1_1[7]);
			result.add(w);
			// console.cprintln("Created word: " + w.toString());
		    } catch (Exception e) {
			console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
			return null;
		    }
		} else {
		    console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: s1.length=" + s1.length + " (Should be 1 or 3)");
		    return null;
		}
	    }
	} catch (IOException e) {
	    console.logErr("ERROR in CSVReader.readCSVFile(): " + e.getMessage() + getStackTraceString(e.getStackTrace()));
	    return null;
	}
	
	return result;
    }
    
    private ArrayList<Word> readCSVFile_old2() {
	ArrayList<Word> result = new ArrayList<>();
	
	System.out.println("Reading CSV-File...");
	
	try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("AllWords.txt"), "UTF-8"))) {
	    String line;
	    
	    while((line = reader.readLine()) != null) {
		String[] s1 = line.split("\"");
		
		if(s1.length == 3) {
		    String[] s1_1 = s1[0].split(";");
		    String[] s1_2 = s1[2].split(";");
		    
		    try {
			Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4], Boolean.parseBoolean(s1_2[5]), Boolean.parseBoolean(s1_2[6]), 
				Boolean.parseBoolean(s1_2[7]), s1_2[8], Boolean.parseBoolean(s1_2[9]), Boolean.parseBoolean(s1_2[10]), 
				"" + s1_2[11] + "-" + s1_2[12] + "|0-0|0-0|0-0|0-0");
			result.add(w);
			// console.cprintln("Created word: " + w.toString());
		    } catch (Exception e) {
			console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
			return null;
		    }
		} else if(s1.length == 1) {
		    String[] s1_1 = s1[0].split(";");
		    
		    try {
			Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1_1[4], s1_1[5], s1_1[6], s1_1[7], s1_1[8], Boolean.parseBoolean(s1_1[9]), 
				Boolean.parseBoolean(s1_1[10]), Boolean.parseBoolean(s1_1[11]), s1_1[12], Boolean.parseBoolean(s1_1[13]), 
				Boolean.parseBoolean(s1_1[14]), "" + s1_1[15] + "-" + s1_1[16] + "|0-0|0-0|0-0|0-0");
			result.add(w);
			// console.cprintln("Created word: " + w.toString());
		    } catch (Exception e) {
			console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
			return null;
		    }
		} else {
		    console.logErr("ERROR in CSVReader.readCSVFile(): Cannot build word for line \"" + line + "\". Reason: s1.length=" + s1.length + " (Should be 1 or 3)");
		    return null;
		}
	    }
	} catch (IOException e) {
	    console.logErr("ERROR in CSVReader.readCSVFile(): " + e.getMessage() + getStackTraceString(e.getStackTrace()));
	    return null;
	}
	
	return result;
    }
    
//    private ArrayList<Word> readCSVFile_old(int fileNum) {
//	ArrayList<Word> result = new ArrayList<>();
//	String filename;
//	
//	if(fileNum < 10) {
//	    filename = "Vokabeln,␣0" + fileNum + ".␣Lektion.csv";
//	} else {
//	    filename = "Vokabeln,␣" + fileNum + ".␣Lektion.csv";
//	}
//	
//	System.out.println("Reading CSV-File " + filename + "...");
//	
//	try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
//	    String line;
//	    
//	    while((line = reader.readLine()) != null) {
//		String[] s1 = line.split("\"");
//		
//		if(s1.length == 3) {
//		    String[] s1_1 = s1[0].split(";");
//		    String[] s1_2 = s1[2].split(";");
//		    
//		    if(s1_1.length != 4) {
//			console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: s1_1.length=" + s1_1.length + " (should be 4). Aborting reading file...");
//			continue;
//			// return null;
//		    }
//		    
//		    if(s1_2.length == 8) {
//			try {
//			    Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4], Boolean.parseBoolean(s1_2[5]), Boolean.parseBoolean(s1_2[6]), Boolean.parseBoolean(s1_2[7]), null, false, false, 0, 0);
//			    result.add(w);
//			    // console.cprintln("Created word: " + w.toString());
//			} catch (Exception e) {
//			    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//			}
//		    } else if(s1_2.length == 9) {
//			try {
//			    Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4], Boolean.parseBoolean(s1_2[5]), Boolean.parseBoolean(s1_2[6]), Boolean.parseBoolean(s1_2[7]), s1_2[8], false, false, 0, 0);
//			    result.add(w);
//			    // console.cprintln("Created word: " + w.toString());
//			} catch (Exception e) {
//			    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//			}
//		    } else if(s1_2.length == 10) {
//			try {
//			    Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4], Boolean.parseBoolean(s1_2[5]), Boolean.parseBoolean(s1_2[6]), Boolean.parseBoolean(s1_2[7]), s1_2[8], Boolean.parseBoolean(s1_2[9]), false, 0, 0);
//			    result.add(w);
//			    // console.cprintln("Created word: " + w.toString());
//			} catch (Exception e) {
//			    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//			}
//		    } else if(s1_2.length == 11) {
//			try {
//			    Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1[1], s1_2[1], s1_2[2], s1_2[3], s1_2[4], Boolean.parseBoolean(s1_2[5]), Boolean.parseBoolean(s1_2[6]), Boolean.parseBoolean(s1_2[7]), s1_2[8], Boolean.parseBoolean(s1_2[9]), Boolean.parseBoolean(s1_2[10]), 0, 0);
//			    result.add(w);
//			    // console.cprintln("Created word: " + w.toString());
//			} catch (Exception e) {
//			    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//			}
//		    } else {
//			console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: s1_2.length=" + s1_2.length + " (should be 9 or 11). Aborting reading file...");
//			continue;
//		    }
//		} else if(s1.length == 1) {
//		    String[] s1_1 = s1[0].split(";");
//		    
//		    try {
//			Word w = new Word(s1_1[0], s1_1[1], s1_1[2], s1_1[3], s1_1[4], s1_1[5], s1_1[6], s1_1[7], s1_1[8], Boolean.parseBoolean(s1_1[9]), Boolean.parseBoolean(s1_1[10]), Boolean.parseBoolean(s1_1[11]), s1_1[12], false, false, 0, 0);
//			result.add(w);
//			// console.cprintln("Created word: " + w.toString());
//		    } catch (Exception e) {
//			console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//		    }
//		} else {
//		    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): Cannot build word for line \"" + line + "\". Reason: s1.length=" + s1.length + " (Should be 1 or 3)");
//		}
//	    }
//	} catch (IOException e) {
//	    console.logErr("ERROR in CSVReader.readCSVFile(" + fileNum + "): " + e.getMessage() + getStackTraceString(e.getStackTrace()));
//	}
//	
//	return result;
//    }
    
    private String getStackTraceString(StackTraceElement[] stacktrace) {
	StringBuilder result = new StringBuilder();
	
	for(StackTraceElement e : stacktrace) {
	    result.append("\n");
	    result.append(e.toString());
	}
	
	return result.toString();
    }
}
