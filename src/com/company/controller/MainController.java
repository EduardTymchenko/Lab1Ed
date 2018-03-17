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
    private static final Logger logger = Logger.getLogger(MainController.class);
    private static int toDoit;
    private static String fileName = "";
    private final  String IN_FORMAT_DATE = "dd.MM.yyyy HH:mm";
    private final int MAX_YEAR = 100;
    private Scanner inData = new Scanner(System.in);
    boolean exit = false;
    private TaskList currentList = new ArrayTaskList();

    public static void main(String[] args) throws IOException, ParseException {
        MainController controller = new MainController();
        logger.info("������ ����������");
        controller.ViewMenu();
    }
    /**
     * ����� ��� ������ �������� ����� �� �����
     *
     */
    public void myCalendar (){
        SimpleDateFormat formatDate = new SimpleDateFormat(IN_FORMAT_DATE);
        Date startDate = enterDate("������� ���� � ����� ������ ��������� ");
        Date endDate = enterDate("������� ���� � ����� ��������� ��������� ");
        Map<Date,Set<Task>> map;
        map = Tasks.calendar(currentList,startDate,endDate);

        for (Map.Entry<Date,Set<Task>> entry: map.entrySet()) {
        String tmpDate = formatDate.format(entry.getKey());
        String stringTask = calendarTask(entry.getValue()).toString();
            System.out.print(tmpDate + " " + stringTask );
        }
    }

    /**
     * ����� ������������ � myCalendar ()
     *@return ������ ��� ������
     */
    public ArrayTaskList calendarTask (Set<Task> set){
    Set<Task> setTask = new HashSet<>(set);
    ArrayTaskList listTask = new ArrayTaskList();
    for (Task task: setTask){
    listTask.add(task);
    }
        return listTask;
    }


    public Task getTask(){
        Task currentTask;
        int indexTask;
        indexTask = enterNumber("������� ����� ������:","! ����� ������ �� ����������","! �� ����� �� ����� �����");
        currentTask = currentList.getTask(indexTask);
        return  currentTask;
    }
    /**
     * ����� ���������� ������ ���������� � ������
     *@param inTask ������ Task
     */
    public void showTaskDetails(Task inTask) {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.sss");
        System.out.println("0. �������� ������: " + inTask.getTitle());
        System.out.println("1. ����� ������ ������: " + formatDate.format(inTask.getStartTime()));
        System.out.println("2. ����� ��������� ������: " + formatDate.format(inTask.getEndTime()) );
        System.out.println("3. ����� ���������� � �������: " + inTask.getRepeatInterval()/60);
        if (inTask.isActive()){System.out.println("4.������ �������");}
        else {System.out.println("4. ������ �� �������");}
    }

    /**
     * ����� ���������� ����� ������
     *
     */
    public void addTask() throws IOException {
        String nameTask;
        Date beginData ;
        Date endData;
        int currentInterval;

        System.out.print("������� �������� ������:");
        nameTask = inData.nextLine();
        setExit(nameTask);
        beginData = enterDate("������� ���� � ����� ������ ������ ");
        endData = enterDate("������� ���� � ����� ��������� ������ ");
        currentInterval = enterPozitivInt("������� �������� ���������� ������(����� ����� ���.): ");
        currentInterval = 60*currentInterval;

if (currentInterval == 0){
    Task currentTask = new Task(nameTask,endData);
    currentList.add(currentTask);
} else {Task currentTask = new Task(nameTask,beginData,endData,currentInterval);
    currentList.add(currentTask);
}
logger.info("��������� ����� ������ " + nameTask);
        TaskIO.writeText(currentList, new File(fileName));
        logger.info("������ \""+nameTask+"\" �������� � ����");
    }
    /**
     * ����� �������� ������ �� ������ �� ������ � ������
     *
     */
    public void deleteTask() throws IOException {
        int indexTask;
            ViewTask();
            indexTask = enterNumber("������� ����� ������ ��� ��������:","! ����� ������ �� ����������","! �� ����� �� ����� �����");
            String toLoggerDelNname = currentList.getTask(indexTask).getTitle();
            currentList.remove(currentList.getTask(indexTask));
            TaskIO.writeText(currentList,new File(fileName));
            logger.info("������ "+ toLoggerDelNname+" ������� �� �����");
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
        Date enterDate = new Date();

        int deltaYear = 0;
        boolean bError = true;
        do {
            try {
                System.out.println(enterMess);
                System.out.print("������ ������ (��.��.���� ��:��): ");
                stringIn = inData.nextLine();
                setExit(stringIn);
                if (stringIn.length() == 0){enterDate = new Date();
                } else{
                    enterDate = format.parse(stringIn);
                    // ����������� � ���������, �������� ���� � �� �������
                    Calendar enterToCalendar = Calendar.getInstance();
                    Calendar currentToCalendar = Calendar.getInstance();
                    enterToCalendar.setTime(enterDate);
                    currentToCalendar.setTime(new Date());
                    deltaYear = enterToCalendar.get(Calendar.YEAR) - currentToCalendar.get(Calendar.YEAR);
                    if (deltaYear > MAX_YEAR) throw new ParseException("����� ������ ����� " + MAX_YEAR + " ���",0);
                }
                bError = false;
            } catch (ParseException e) {
                logger.error(e.getMessage(),e);
                System.out.println("! �� ������ ������");
            }
        } while (bError);
        return enterDate;
    }

    /**
     * ����� ������������ ��� �������� ����� ������ ������ ������
     *@param messEnter String ��� ������ �����������
     *@param messDataError String ��� ������ ��������� InvalidDataExeption
     *@param messFormatError String ��� ������ ��������� NumberFormatException
     *@exception InvalidDataExeption ������, ���� �� ������������� ������ ������
     *@exception NumberFormatException ������, ���� �� ������ �����
     *@return ���� � ������� "dd.MM.yyyy hh:mm"
     */
    public int enterNumber(String messEnter, String messDataError, String messFormatError){
    int currentIndex = 0;
    boolean bError = true;
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
            bError = false;
        } catch (InvalidDataExeption e) {
            logger.error(e.getMessage(),e);
            System.out.println(messDataError);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(),e);
            System.out.println(messFormatError);
        }
    }while (bError);
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
        boolean bError = true;
        int currentInt = 0;
        String inPozitivInt;
        do {
            try {
                System.out.print(enterMess);
                inPozitivInt = inData.nextLine();
                setExit(inPozitivInt);
                currentInt = Integer.parseInt(inPozitivInt);
                if (currentInt < 0 ) {
                    throw new InvalidDataExeption("Negative number");
                }
                bError = false;
            } catch (InvalidDataExeption e){
                logger.error(e.getMessage(),e);
                System.out.println("! �� ����� ������������� �����");
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(),e);
                System.out.println("! �� ����� �� ����� �����");
            }
        } while (bError);
        return currentInt;
    }
    /**
     * ����� ������������ ��� �������� �������������� ������������ �����
     */
    public void editTask() throws IOException {
        boolean exitEdit = false;
        int editMeny;
        String nameTask = "";
        Date beginData ;
        Date endData;
        int currentInterval;
        boolean activ = false;

      Task editTask = getTask();
      do {
          showTaskDetails(editTask);
          System.out.println("5. ��������� � �����");
          editMeny = enterPozitivInt("������� ����� ���� ��� ��������������:");
          switch (editMeny){
              case 0:System.out.print("0. ������ �������� ������: ");
              nameTask = inData.nextLine();
              setExit(nameTask);
              editTask.setTitle(nameTask);
                  break;
              case 1: beginData = enterDate("������� ���� � ����� ������ ������ ");
                  editTask.setTime(beginData,editTask.getEndTime(),editTask.getRepeatInterval());
                  break;
              case 2: endData = enterDate("������� ���� � ����� ��������� ������ ");
                  editTask.setTime(editTask.getStartTime(),endData,editTask.getRepeatInterval());
                  break;
              case 3: currentInterval = enterPozitivInt("������� �������� ���������� ������(����� ����� ���.): ");
                  currentInterval = 60*currentInterval;
                  editTask.setTime(editTask.getStartTime(),editTask.getEndTime(),currentInterval);
                  break;
              case 4: activ=!activ; editTask.setActive(activ);
                  break;
              case 5: TaskIO.writeText(currentList, new File(fileName));
                     logger.info("������ \""+ nameTask + "\" ��������");
                    exitEdit = true;
              break;
          }

      }while (!exitEdit);
    }

    // Viewer
    /**
     * ����� ������������ ��� ������ ���� ������������ �����
     */
    public void ViewTask()  {
    int index = 0;

        System.out.println("*** ������ ���� ����� ***");
        for(Task task : currentList){
            System.out.println(index + ". " + task.getTitle());
            index++;
        }
    }

    /**
     * ����� ������������ ��� ��������� ����
     */
    public void ViewMenu() throws IOException, ParseException {
        String inViewMenu;
        System.out.println("### Welcome to TASK MANAGER ###");
        System.out.println("* ��� ������ � ���������� ������� exit");
        getBD(); //����� ����� ��� ������
        TaskIO.readText(currentList,new File(fileName));

        System.out.println();
        while (!exit) {
            boolean bError = true;
            System.out.println("*** ������� ���� ***");
            System.out.println("1 - ��� ������");
            System.out.println("2 - ���������");
            System.out.println("3 - �������� ������");
            System.out.println("4 - ������������� ������");
            System.out.println("5 - ������� ������");
            System.out.println("6 - �������� � ������");
            System.out.println("7 - �����");

            do {
                try {
                    System.out.print("������� ����� ���� [1-7]:");
                    inViewMenu = inData.nextLine();
                    setExit(inViewMenu);
                    toDoit = Integer.parseInt(inViewMenu);

                    if (toDoit <= 0 || toDoit > 7) {
                        throw new InvalidDataExeption("The menu item does not exist");
                    }
                    bError = false;
                } catch (InvalidDataExeption ei) {
                    logger.error(ei.getMessage(),ei);
                    System.out.println("! ����� ���� �� ����������");
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(),e);
                    System.out.println("! �� ����� �� ����� �����");
                }
            } while (bError);

// ��������� ������ ����
            switch (toDoit) {
                case 1: logger.info("������ �.1 \"��� ������\""); System.out.println();ViewTask();
                    break;
                case 2: logger.info("������ �.2 \"���������\""); System.out.println("\n*** ��������� �� ����� ***"); myCalendar();
                    break;
                case 3: logger.info("������ �.3 \"�������� ������\" ");System.out.println("\n*** ���� ����� ������ ***"); addTask();
                    break;
                case 4: logger.info("������ �.4 \"������������� ������\"");System.out.println("\n*** �������������� ������ ***"); ViewTask(); editTask();
                    break;
                case 5: logger.info("������ �.5 \"������� ������\"");System.out.println("\n*** ������� ������ ***"); deleteTask();
                    break;
                case 6: logger.info("������ �.6 \"�������� � ������\"");System.out.println("\n*** �������� � ������ ***"); ViewTask(); showTaskDetails(getTask());
                    break;
                case 7: logger.info("���������� ��������� �� ������� �.7 \"�����\" "); exit = true;
                    break;

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
                logger.info("���� ��� ������ ����� ���������� ������ ����� ������ �����");
            } else if (lastFile.length() == 0) {
                System.out.println("��������� ������ ����� ����������");
                logger.info("��������� ������ ����� ���������� ���� ������");
            } else {
                fileName = bufRead.readLine();
                System.out.println("��� ����������� ������ � " + fileName + " ������� Enter."); }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        //��������� ������������� �����, ��� �������
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)){
            File newDir = new File(dirPath);
           boolean createDir = newDir.mkdir();
           //���� ���������� �� ������� ����� �� ���������
           if (!createDir) {System.out.println("������� ������� �� �������");
           logger.error("����� " + dirPath + "������� �� �������. ��������� ���������");
           exit = true;}
        }
        File curentDir = new File(dirPath);
        for (File item : curentDir.listFiles()){
                System.out.println(item.getName());
            }
            if(curentDir.listFiles().length == 0) {System.out.println("������ ���");}

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
                        logger.debug("������ ����� " + dirPath + fileSeparator + fileName );
                    }
                } catch (IOException ex){
                    logger.error(ex.getMessage(),ex);
                }
            } else {
                System.out.println("������ ����: " + fileName);
            }
// ��������� ��� ��������� ���� �����
        try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("lastList.txt"))){
            bufWrite.write(fileName);
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
            fileName = dirPath+fileSeparator+fileName;
            logger.info("������� ���� ������ ����� " + fileName);
    }
    private void setExit (String inputString){
        if (inputString.equals("exit")){
            logger.info("��������� ��������� �� ������� exit");
            System.exit(0);
        }
    }
}

