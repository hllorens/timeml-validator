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
                    if (!type.equalsIgnoreCase("normal") && !type.equalsIgnoreCase("minimum")) {
                        throw new Exception("Type (-t) must be either 'normal' or 'minimum'.");
                    }
                }

                if(type.equalsIgnoreCase("minimum")){
                    type="tml-min-consistency";
                }else{
                    type="tml";
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
                            if (!xmlfile.isWellFormed()  || !validateTEXTDCT(f)) {
                                if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                                    throw new Exception("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                                } else {
                                    System.out.println("File: " + xmlfile.getFile().getCanonicalPath() + " is not a valid TimeML XML file.");
                                }
                            }
                        }
                    }

                }



            }
            System.out.println("All the files are valid!");
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
            Element dct = ((Element) ((NodeList) ((Element) doc.getElementsByTagName("DCT").item(0)).getElementsByTagName("TIMEX3")).item(0));
            if (dct == null) {
                throw new Exception("ERROR: No DCT TIMEX found.");
            }

            NodeList text = doc.getElementsByTagName("TEXT");
            if (text.getLength() != 1) {
                throw new Exception("ERROR: None or more than one TEXT tag found.");
            }
            return true;
        }catch(Exception e){
            System.err.println("Errors found (TimeML_Normalizer):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return false;
        }
    }

}

