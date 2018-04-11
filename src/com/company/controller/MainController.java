package com.company.controller;

import com.company.model.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * @author Created by EduardTymchenko 11.03.2018.
 * @version 1.0
 */

public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    private static String fileName = "";
    private final  String IN_FORMAT_DATE = "dd.MM.yyyy HH:mm";
    private final int MAX_YEAR = 100;
    private Scanner inData = new Scanner(System.in);
    private TaskList currentList = new ArrayTaskList();
    // ����� ������ � ������� ������
    private int indexTask;


    public static void main(String[] args) throws IOException, ParseException {
        MainController controller = new MainController();
        LOGGER.info("������ ����������");
        controller.viewMenu();
    }
    /**
     * ����� ��� ������ �������� ����� �� �����
     *
     */
    public void myCalendar (){
        LOGGER.info("������ �.2 \"���������\"");
        SimpleDateFormat formatDate = new SimpleDateFormat(IN_FORMAT_DATE);
        Date startDate = enterDate("������� ���� � ����� ������ ��������� ");
        Date endDate = enterDate("������� ���� � ����� ��������� ��������� ");
        Map<Date,Set<Task>> map;
        Set<Task> setTask ;
        map = Tasks.calendar(currentList,startDate,endDate);
        for (Map.Entry<Date,Set<Task>> entry: map.entrySet()) {
            ArrayTaskList listTask = new ArrayTaskList();
            String tmpDate = formatDate.format(entry.getKey());
            setTask = entry.getValue();
            for (Task enterTask : setTask){
                listTask.add(enterTask);
            }
            String stringTask = listTask.toString();
            System.out.print(tmpDate + " " + stringTask );
        }
    }

    /**
     * ����� ���������� ����� ������
     *
     */
    public void addTask() throws IOException {
        LOGGER.info("������ �.3 \"�������� ������\" ");
        String nameTask;
        Date beginData = new Date() ;
        Date endData;
        int currentInterval = 0;
        int repeatTask;
        do{
            repeatTask = enterPozitivInt("������ ����� ����������� (1), �� ����� (0). ������� [0-1]");
            if (repeatTask <= 1){
                break;
            }
        }while (true);

        System.out.print("������� �������� ������:");
        nameTask = inData.nextLine();
        setExit(nameTask);
        if (repeatTask == 1){
            beginData = enterDate("������� ���� � ����� ������ ������ ");
        }
        endData = enterDate("������� ���� � ����� ��������� ������ ");
        if (repeatTask == 1){
            do{
                currentInterval = enterPozitivInt("������� �������� ���������� ������(����� ����� ���. > 0): ");
                currentInterval = 60*currentInterval;
                if(currentInterval > 0){
                    break;
                }
            }while (true);

        }
        if (repeatTask == 0){
            Task currentTask = new Task(nameTask,endData);
            currentList.add(currentTask);
        } else {
            Task currentTask = new Task(nameTask,beginData,endData,currentInterval);
            currentList.add(currentTask);
        }
        LOGGER.info("��������� ����� ������ " + nameTask);
        TaskIO.writeText(currentList, new File(fileName));
        LOGGER.info("������ \""+nameTask+"\" �������� � ����");
    }
    /**
     * ����� �������� ������ �� ������ �� ������ � ������
     *
     */
    public void deleteTask() throws IOException {
        LOGGER.info("������ �.5 \"������� ������\"");
        if(currentList.size() == 0){
            return;
        }
        indexTask = enterNumber("������� ����� ������ ��� ��������:");
        String toLoggerDelNname = currentList.getTask(indexTask).getTitle();
        currentList.remove(currentList.getTask(indexTask));
        TaskIO.writeText(currentList,new File(fileName));
        LOGGER.info("������ "+ toLoggerDelNname+" ������� �� �����");
    }
    /**
     * ����� ������������ ��� �������� ����� ����
     *@param enterMess String ��� ������ �����������
     *@return ���� � ������� "dd.MM.yyyy hh:mm"
     *@exception ParseException ������ ����� ������� ����
     *
     */
    public Date enterDate(String enterMess){
        SimpleDateFormat format = new SimpleDateFormat(IN_FORMAT_DATE);
        format.setLenient(false); //������� �������� �������
        String stringIn;
        Date enterDate;
        int deltaYear;
        do {
            try {
                System.out.println(enterMess);
                System.out.print("������ ������ (��.��.���� ��:��), Enter - ������� ����: ");
                stringIn = inData.nextLine();
                setExit(stringIn);
                if (stringIn.length() == 0){
                    enterDate = new Date();
                } else{
                    enterDate = format.parse(stringIn);
                    // ����������� � ���������, �������� ���� � �� �������
                    Calendar enterToCalendar = Calendar.getInstance();
                    Calendar currentToCalendar = Calendar.getInstance();
                    enterToCalendar.setTime(enterDate);
                    currentToCalendar.setTime(new Date());
                    deltaYear = enterToCalendar.get(Calendar.YEAR) - currentToCalendar.get(Calendar.YEAR);
                    if (deltaYear > MAX_YEAR){
                        throw new ParseException("����� ������ ����� " + MAX_YEAR + " ���",0);
                    }
                }
                break;
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(),e);
                System.out.println("! �� ������ ������");
            }
        } while (true);
        return enterDate;
    }

    /**
     * ����� ������������ ��� �������� ����� ������ ������ ������
     *@param messEnter String ��� ������ �����������
     *@exception InvalidDataExeption ������, ���� �� ������������� ������ ������
     *@exception NumberFormatException ������, ���� �� ������ �����
     *@return ���� � ������� "dd.MM.yyyy hh:mm"
     */
    public int enterNumber(String messEnter){
    int currentIndex = 0;
    String inEnterNumber;
    do {
        try {
            System.out.print(messEnter);
            inEnterNumber = inData.nextLine();
            setExit(inEnterNumber);
            currentIndex =Integer.parseInt(inEnterNumber) ;
            if (currentIndex < 0 || currentIndex > currentList.size()-1) {
                throw new InvalidDataExeption("The  item does not exist");
            }
            break;
        } catch (InvalidDataExeption e) {
            LOGGER.error(e.getMessage(),e);
            System.out.println("! ����� ������ �� ����������");
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(),e);
            System.out.println("! �� ����� �� ����� �����");
        }
    }while (true);
    return currentIndex;
}
    /**
     * ����� ������������ ��� �������� ����� �������������� ������ �����
     *@param enterMess String ��� ������ �����������
     *@exception InvalidDataExeption ������, ���� �������������� �����
     *@exception NumberFormatException ������, ���� �� ������ �����
     *@return ����� ������������� �����
     */
    public int enterPozitivInt (String enterMess){
        int currentInt;
        String inPozitivInt;
        do {
            try {
                System.out.print(enterMess);
                inPozitivInt = inData.nextLine();
                setExit(inPozitivInt);
                currentInt = Integer.parseInt(inPozitivInt);
                if (currentInt < 0) {
                    throw new IllegalArgumentException("Negative number");
                }
                break;
            } catch (NumberFormatException e) {
                LOGGER.error(e.getMessage(), e);
                System.out.println("! �� ����� �� ����� �����");
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
                System.out.println("! �� ����� ������������� �����");
            }
        }while (true);
        return currentInt;
    }
    /**
     * ����� ������������ ��� �������� �������������� ������������ �����
     */
    public void editTask() throws IOException {
        LOGGER.info("������ �.4 \"������������� ������\"");
        if(currentList.size() == 0){
            return;
        }
        indexTask = enterNumber("������� ����� ������:");
        Task currentTask = currentList.getTask(indexTask);

        String nameTask = currentTask.getTitle();
        Date beginData = currentTask.getStartTime();
        Date endData = currentTask.getEndTime();
        int currentInterval = currentTask.getRepeatInterval()/60;
        boolean activ = false;
        String repeatTask;
        int editMeny;
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.sss");
        do {
            System.out.println("0. �������� ������: " + nameTask);
            System.out.println("1. ����� ������ ������: " + formatDate.format(beginData));
            System.out.println("2. ����� ��������� ������: " + formatDate.format(endData));
            if (currentTask.isRepeated()){
                repeatTask = "�����������";
            }else{
                repeatTask = "�������������";
            }
            System.out.println("3. ������ " + repeatTask +". ����� ���������� � �������: " + currentInterval);
            if (currentTask.isActive()){
                System.out.println("4. ������ �������");
            } else {
                System.out.println("4. ������ �� �������");
            }
            System.out.println("5. �������� ��� ������");
            System.out.println("6. ��������� � �����");
            editMeny = enterPozitivInt("������� ����� ���� ��� ��������������:");

            switch (editMeny) {

                case 0:
                    System.out.print("0. ������ �������� ������: ");
                    nameTask = inData.nextLine();
                    setExit(nameTask);
                    break;
                case 1:
                    beginData = enterDate("������� ���� � ����� ������ ������ ");
                    break;
                case 2:
                    endData = enterDate("������� ���� � ����� ��������� ������ ");
                    break;
                case 3:
                    if(!currentTask.isRepeated()){
                        System.out.println("! ���������� ��������. �������� ��� ������ �.5");
                    } else {
                        do{
                            currentInterval = enterPozitivInt("������� �������� ���������� ������(����� ����� ���.)>0: ");
                            if (currentInterval >0){
                                break;
                            }
                        } while (true);
                    }
                    break;
                case 4:
                    activ = !activ;
                    break;
                case 5:
                    if (currentTask.isRepeated()){
                        beginData = endData;
                        currentInterval = 0;
                        currentTask.setTime(endData);
                    } else{
                        beginData = enterDate("������� ���� � ����� ������ ������ ");
                        endData = enterDate("������� ���� � ����� ��������� ������ ");
                        do {
                            currentInterval = enterPozitivInt("������� �������� ���������� ������(����� ����� ���.)>0: ");
                            if (currentInterval > 0){
                                break;
                            }
                        }while (true);
                    }
                    break;
                case 6:
                    TaskIO.writeText(currentList, new File(fileName));
                    LOGGER.info("������ \"" + nameTask + "\" ��������");
                    return;
                default:
                        System.out.println("! ������ ������ �� ����������");
            }
            currentTask.setTitle(nameTask);
            currentTask.setActive(activ);
            if (currentInterval != 0){
                currentTask.setTime(beginData,endData,currentInterval*60);
            } else {
                currentTask.setTime(endData);
            }
        }while (true);
    }

    // Viewer
    /**
     * ����� ������������ ��� ������ ���� ������������ �����
     */
    public void viewTask(){
        LOGGER.info("������ �.1 \"��� ������\"");
        System.out.println("*** ������ ���� ����� ***");
        if (currentList.size() == 0){
            System.out.println("������ ����� ����");
        } else {
                System.out.print(currentList.toString());
            }
        }


    /**
     * ����� ������������ ��� ��������� ����
     */
    public void viewMenu() throws IOException, ParseException {
        int toDoit;
        System.out.println("### Welcome to TASK MANAGER ###");
        System.out.println("* ��� ������ �� ��������� ������� exit");
        //����� ����� ��� ������
        getBD();
        TaskIO.readText(currentList,new File(fileName));
        System.out.println();
        while (true) {
            System.out.println("*** ������� ���� ***");
            System.out.println("1 - ��� ������");
            System.out.println("2 - ���������");
            System.out.println("3 - �������� ������");
            System.out.println("4 - ������������� ������");
            System.out.println("5 - ������� ������");
            System.out.println("6 - ��������� � ����");
            System.out.println("7 - �����");

            toDoit = enterPozitivInt("������� ����� ���� [1-7]:");
            // ��������� ������ ����
            switch (toDoit) {
                case 1:
                    System.out.println();
                    viewTask();
                    break;
                case 2:
                    System.out.println("\n*** ��������� ***");
                    myCalendar();
                    break;
                case 3:
                    System.out.println("\n*** ���� ����� ������ ***");
                    addTask();
                    break;
                case 4:
                    System.out.println("\n*** �������������� ������ ***");
                    viewTask();
                    editTask();
                    break;
                case 5:
                    System.out.println("\n*** ������� ������ ***");
                    viewTask();
                    deleteTask();
                    break;
                case 6:
                    saveToFile();
                    break;
                case 7:
                    LOGGER.info("���������� ��������� �� ������� �.7 \"�����\" ");
                    System.exit(0);
                    default:
                        System.out.println("! ������ ������ �� ����������");
            }
        }
    }

    /**
     * ����� ������������ ��� ������ ��� �������� ����� �����
     */
    public void getBD(){
        String dirPath = "BD";
        // �������� ����������� ���� � ������� ������������ �������
        String fileSeparator = System.getProperty("file.separator");
        File lastFile = new File("lastList.txt");
        // ������ ��� ��������� ���� �����
        try (BufferedReader bufRead = new BufferedReader(new FileReader("lastList.txt"))){
            if (!lastFile.exists()) {
                lastFile.createNewFile();
                System.out.println("��������� ������ ����� ����������");
                LOGGER.info("���� ��� ������ ����� ���������� ������ ����� ������ �����");
            } else if (lastFile.length() == 0) {
                System.out.println("��������� ������ ����� ����������");
                LOGGER.info("��������� ������ ����� ���������� ���� ������");
            } else {
                fileName = bufRead.readLine();
                System.out.println("��� ����������� ������ � " + fileName + " ������� Enter."); }
        }catch (IOException e){
            LOGGER.error(e.getMessage(),e);
        }
        //��������� ������������� �����, ��� �������
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)){
            File newDir = new File(dirPath);
            //���� ���������� �� ������� ����� �� ���������
            if (!newDir.mkdir()) {
                System.out.println("������� ������� �� �������");
                LOGGER.error("����� " + dirPath + "������� �� �������. ��������� ���������");
                System.exit(0);
            }
        }
        File curentDir = new File(dirPath);
        if(curentDir.listFiles().length == 0) {
            System.out.println("������ ���");
        } else {
            for (File item : curentDir.listFiles()){
                System.out.println(item.getName());
            }
        }
        do {
            System.out.println("������ ��� ����� (���� ������ ���, ����� ������):");
            String tmpFileName =  inData.nextLine();
            setExit(tmpFileName);
            if (tmpFileName.length() != 0 ){
                fileName = tmpFileName;
            }
        }while (fileName.length() == 0);

        File currentFile = new File(dirPath+fileSeparator+fileName);
        if (!currentFile.exists()){
            try{
                boolean created = currentFile.createNewFile();
                if (created) {
                    System.out.println("������ ����� ����: " + fileName);
                    LOGGER.debug("������ ����� " + dirPath + fileSeparator + fileName );
                }
            } catch (IOException ex){
                LOGGER.error(ex.getMessage(),ex);
            }
        } else {
            System.out.println("������ ����: " + fileName);
        }
        // ��������� ��� ��������� ���� �����
        try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("lastList.txt"))){
            bufWrite.write(fileName);
        }catch (IOException e){
            LOGGER.error(e.getMessage(),e);
        }
        fileName = dirPath+fileSeparator+fileName;
        LOGGER.info("������� ���� ������ ����� " + fileName);
    }
    public void setExit (String inputString){
        if (inputString.equals("exit")){
            LOGGER.info("��������� ��������� �� ������� exit");
            System.exit(0);
        }
    }
    public void saveToFile() throws IOException {
        String newFileName;
        do{
            System.out.println("������� ��� �����:");
            newFileName = inData.nextLine();
            setExit(newFileName);
            if(newFileName.length() > 0){
                break;
            }
        }while (true);
        // ��������� ��� ��������� ���� �����
        try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("lastList.txt"))){
            bufWrite.write(newFileName);
        }catch (IOException e){
            LOGGER.error(e.getMessage(),e);
        }
        String fileSeparator = System.getProperty("file.separator");
        String newFileString = new File(fileName).getParent().toString() + fileSeparator + newFileName;
        fileName = newFileString;
        TaskIO.writeText(currentList,new File(fileName));
    }
}

