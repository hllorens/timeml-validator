
TimeML-validator

A simple java application to check if your TimeML annotations are valid against the TimeML xsl schema.

LICENSE
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
   
   
COMPILATION AND JARING

javac -cp lib/commons-cli-1.2.jar:lib/NLP_BasicKit.jar:lib/Utils_BasicKit.jar  -d build/classes src/timemlvalidator/Main.java

jar cfm dist/TimeML-validator.jar manifest.mf -C build/classes/  .

INSTALLATION

Just copy the TimeML-validator folder with the lib and program-data subfolders to any destination on your system.

USAGE

java -jar [path-to-the-jar/TimeML-validator.jar] file/s or folder/s

EXAMPLE (if TimeML-validator is included in the CLASSPATH)

java -jar TimeML-validator myfile.tml

OPTIONS

-d for debugging information
-t for choosing normal (default) or minimum checking

The "minimum" checking is less strict that the normal checking. This for example does require the ids to be, for example, "t+number" for TIMEX and does not check the TIMEX values. For detailed differences diff tml.xsd and tml-min-concistency.xsd.


