cd compil
jar xf ../lib/log4j-1.2.17.jar org/
cd..
javac -cp ./lib/log4j-1.2.17.jar -sourcepath ./src -d compil src/com/company/controller/MainController.java
jar cef com.company.controller.MainController lab1.jar -C compil . -C src log4j.properties
