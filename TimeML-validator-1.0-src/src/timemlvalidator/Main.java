/*
  Copyright 2012 Hector Llorens

   Licensed under the Apache License, Version 2.0;
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


package timemlvalidator;

import java.io.*;
import utils_bk.FileUtils;
import nlp_files.XMLFile;
import org.apache.commons.cli.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.*;


/**
 *
 * @author Hector Llorens
 * @since 2012
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String type = "tml"; // normal
        String input_files[];
	int incorrect_files=0;

        try {


            Options opt = new Options();
            //addOption(String opt, boolean hasArg, String description)
            opt.addOption("h", "help", false, "Print this help");
            opt.addOption("t", "type", true, "Type of validation: normal (default) or minimum");
            opt.addOption("d", "debug", false, "Debug mode: Output errors stack trace (default: disabled)");

            PosixParser parser = new PosixParser();
            CommandLine cl_options = parser.parse(opt, args);
            HelpFormatter hf = new HelpFormatter();
            if (cl_options.hasOption('h')) {
                hf.printHelp("TimeML-validator", opt);
                System.exit(0);
            } else {
                if (cl_options.hasOption('d')) {
                    System.setProperty("DEBUG", "true");
                }

                if (cl_options.hasOption('t')) {
                    type = cl_options.getOptionValue("t");
                    if (!type.equalsIgnoreCase("normal") && !type.equalsIgnoreCase("minimum") && !type.equalsIgnoreCase("just-ids")) {
                        throw new Exception("Type (-t) must be either 'normal' or 'minimum' or 'just-ids'.");
                    }
                }

                if(type.equalsIgnoreCase("minimum")){
                    type="tml-min-consistency";
                }else{
		        if(type.equalsIgnoreCase("just-ids")){
		            type="tml-just-ids";
		        }else{                	 
	                    type="tml";
	                }
                }

                input_files = cl_options.getArgs();

                for (int i = 0; i < input_files.length; i++) {
                    File f = new File(input_files[i]);
                    if (!f.exists()) {
                        throw new FileNotFoundException("File does not exist: " + f);
                    }

                    if (f.isFile()) {
                        File[] files = {f};
                        XMLFile xmlfile = new XMLFile();
                        xmlfile.loadFile(f);
                        xmlfile.overrideExtension(type);
                        if (!xmlfile.isWellFormed() || !validateTEXTDCT(f)) {
                            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                throw new Exception("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                            } else {
                                System.out.println("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                                incorrect_files++;
                            }
                        }
                    } else {
                        File[] files = f.listFiles(FileUtils.onlyFilesFilter);
                        if (files.length == 0) {
                            throw new Exception("Empty folder: " + f.getName());
                        }
                        for (int fn = 0; fn < files.length; fn++) {
                            XMLFile xmlfile = new XMLFile();
                            xmlfile.loadFile(files[fn]);
                            xmlfile.overrideExtension(type);
                            if (!xmlfile.isWellFormed()  || !validateTEXTDCT(xmlfile.getFile())) {
                                if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                    throw new Exception("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                                } else {
                                    System.out.println("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                                    incorrect_files++;
                                }
                            }
                        }
                    }

                }



            }
	    if(incorrect_files==0)
            	System.out.println("\n\tAll the files are valid!\n\n");
            else
            	System.out.println("\n\tERROR:"+incorrect_files+" files are INCORRECT.\n\n");
            	
        } catch (Exception e) {
            System.err.println("Errors found:\n\t" + e.getMessage() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
            }
            System.exit(1);
        }

    }

    public static boolean validateTEXTDCT(File f){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            NodeList dctnodes = doc.getElementsByTagName("DCT");
            if (dctnodes.getLength() == 0) {
                throw new Exception("ERROR: <DCT> tag not found.");
            }
            if (dctnodes.getLength() > 1) {
                throw new Exception("ERROR: More than one <DCT> tag found.");
            }
            if(((Element) dctnodes.item(0)).getElementsByTagName("TIMEX3").getLength()!=1){
                throw new Exception("ERROR: <DCT> must contain one and only one <TIMEX3> tag. Expected: <DCT><TIMEX3 tid=\"t0\" type=... value=... temporalFunction=\"false\" functionInDocument=\"CREATION_TIME\">...some timex...</TIMEX3></DCT>");            	
            }
            NodeList text = doc.getElementsByTagName("TEXT");
            if (text.getLength() == 0) {
                throw new Exception("ERROR: <TEXT> tag not found.");
            }
            if (text.getLength() > 1) {
                throw new Exception("ERROR: More than one <TEXT> tag found.");
            }
            return true;
        }catch(Exception e){
            System.err.println("Errors found:\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return false;
        }
    }

}

