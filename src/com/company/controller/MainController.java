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
        logger.info("Запуск приложения");
        controller.ViewMenu();
    }
    /**
     * Метод для вывода активных Задач на сутки
     *
     */
    public void myCalendar (){
        SimpleDateFormat formatDate = new SimpleDateFormat(IN_FORMAT_DATE);
        Date startDate = enterDate("Введите дату и время начала календаря ");
        Date endDate = enterDate("Введите дату и время окончания календаря ");
        Map<Date,Set<Task>> map;
        map = Tasks.calendar(currentList,startDate,endDate);

        for (Map.Entry<Date,Set<Task>> entry: map.entrySet()) {
        String tmpDate = formatDate.format(entry.getKey());
        String stringTask = calendarTask(entry.getValue()).toString();
            System.out.print(tmpDate + " " + stringTask );
        }
    }

    /**
     * Метод используется в myCalendar ()
     *@return список тип Задача
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
        indexTask = enterNumber("Введите номер задачи:","! Такой задачи не существует","! Вы ввели не целое число");
        currentTask = currentList.getTask(indexTask);
        return  currentTask;
    }
    /**
     * Метод детального вывода информации о задаче
     *@param inTask класса Task
     */
    public void showTaskDetails(Task inTask) {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.sss");
        System.out.println("0. Название задачи: " + inTask.getTitle());
        System.out.println("1. Время начала задачи: " + formatDate.format(inTask.getStartTime()));
        System.out.println("2. Время окончания задачи: " + formatDate.format(inTask.getEndTime()) );
        System.out.println("3. Время повторения в минутах: " + inTask.getRepeatInterval()/60);
        if (inTask.isActive()){System.out.println("4.Задача активна");}
        else {System.out.println("4. Задача не активна");}
    }

    /**
     * Метод добавления новой задачи
     *
     */
    public void addTask() throws IOException {
        String nameTask;
        Date beginData ;
        Date endData;
        int currentInterval;

        System.out.print("Введите название задачи:");
        nameTask = inData.nextLine();
        setExit(nameTask);
        beginData = enterDate("Введите дату и время начала задачи ");
        endData = enterDate("Введите дату и время окончания задачи ");
        currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число мин.): ");
        currentInterval = 60*currentInterval;

if (currentInterval == 0){
    Task currentTask = new Task(nameTask,endData);
    currentList.add(currentTask);
} else {Task currentTask = new Task(nameTask,beginData,endData,currentInterval);
    currentList.add(currentTask);
}
logger.info("Добавлена новая задача " + nameTask);
        TaskIO.writeText(currentList, new File(fileName));
        logger.info("Задача \""+nameTask+"\" записана в файл");
    }
    /**
     * Метод удаление задачи из списка по номеру в списке
     *
     */
    public void deleteTask() throws IOException {
        int indexTask;
            ViewTask();
            indexTask = enterNumber("Введите номер задачи для удаления:","! Такой задачи не существует","! Вы ввели не целое число");
            String toLoggerDelNname = currentList.getTask(indexTask).getTitle();
            currentList.remove(currentList.getTask(indexTask));
            TaskIO.writeText(currentList,new File(fileName));
            logger.info("Задача "+ toLoggerDelNname+" удалена из файла");
    }
    /**
     * Метод используется для проверки ввода даты
     *@param enterMess String для вывода приглашения
     *@return дату в формате "dd.MM.yyyy hh:mm"
     *@exception ParseException ошибка ввода формата даты
     *
     */
    public Date enterDate(String enterMess){
        SimpleDateFormat format = new SimpleDateFormat(IN_FORMAT_DATE);
        format.setLenient(false); //жесткая проверка формата
        String stringIn;
        Date enterDate = new Date();

        int deltaYear = 0;
        boolean bError = true;
        do {
            try {
                System.out.println(enterMess);
                System.out.print("формат данных (дд.мм.гггг чч:мм): ");
                stringIn = inData.nextLine();
                setExit(stringIn);
                if (stringIn.length() == 0){enterDate = new Date();
                } else{
                    enterDate = format.parse(stringIn);
                    // Преобразуем в календарь, получаем года и их разницу
                    Calendar enterToCalendar = Calendar.getInstance();
                    Calendar currentToCalendar = Calendar.getInstance();
                    enterToCalendar.setTime(enterDate);
                    currentToCalendar.setTime(new Date());
                    deltaYear = enterToCalendar.get(Calendar.YEAR) - currentToCalendar.get(Calendar.YEAR);
                    if (deltaYear > MAX_YEAR) throw new ParseException("Время задачи более " + MAX_YEAR + " лет",0);
                }
                bError = false;
            } catch (ParseException e) {
                logger.error(e.getMessage(),e);
                System.out.println("! Не верные данные");
            }
        } while (bError);
        return enterDate;
    }

    /**
     * Метод используется для проверки ввода выбора номера задачи
     *@param messEnter String для вывода приглашения
     *@param messDataError String для вывода сообщения InvalidDataExeption
     *@param messFormatError String для вывода сообщения NumberFormatException
     *@exception InvalidDataExeption ошибка, ввод не существующего номера задачи
     *@exception NumberFormatException ошибка, ввод не целого числа
     *@return дату в формате "dd.MM.yyyy hh:mm"
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
     * Метод используется для проверки ввода положительного целого цисла
     *@param enterMess String для вывода приглашения
     *@exception InvalidDataExeption ошибка, ввод отрицательного числа
     *@exception NumberFormatException ошибка, ввод не целого числа
     *@return целое положительное цисло
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
                System.out.println("! Вы ввели отрицательное число");
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(),e);
                System.out.println("! Вы ввели не целое число");
            }
        } while (bError);
        return currentInt;
    }
    /**
     * Метод используется для проверки редактирования существующих задач
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
          System.out.println("5. Сохранить и выйти");
          editMeny = enterPozitivInt("Введите пункт меню для редактирования:");
          switch (editMeny){
              case 0:System.out.print("0. Ведите название задачи: ");
              nameTask = inData.nextLine();
              setExit(nameTask);
              editTask.setTitle(nameTask);
                  break;
              case 1: beginData = enterDate("Введите дату и время начала задачи ");
                  editTask.setTime(beginData,editTask.getEndTime(),editTask.getRepeatInterval());
                  break;
              case 2: endData = enterDate("Введите дату и время окончания задачи ");
                  editTask.setTime(editTask.getStartTime(),endData,editTask.getRepeatInterval());
                  break;
              case 3: currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число мин.): ");
                  currentInterval = 60*currentInterval;
                  editTask.setTime(editTask.getStartTime(),editTask.getEndTime(),currentInterval);
                  break;
              case 4: activ=!activ; editTask.setActive(activ);
                  break;
              case 5: TaskIO.writeText(currentList, new File(fileName));
                     logger.info("Задача \""+ nameTask + "\" изменина");
                    exitEdit = true;
              break;
          }

      }while (!exitEdit);
    }

    // Viewer
    /**
     * Метод используется для вывода всех существующих задач
     */
    public void ViewTask()  {
    int index = 0;

        System.out.println("*** Список всех задач ***");
        for(Task task : currentList){
            System.out.println(index + ". " + task.getTitle());
            index++;
        }
    }

    /**
     * Метод используется для основного меню
     */
    public void ViewMenu() throws IOException, ParseException {
        String inViewMenu;
        System.out.println("### Welcome to TASK MANAGER ###");
        System.out.println("* Для выхода с приложения введите exit");
        getBD(); //выбор файла для работы
        TaskIO.readText(currentList,new File(fileName));

        System.out.println();
        while (!exit) {
            boolean bError = true;
            System.out.println("*** Главное меню ***");
            System.out.println("1 - Все задачи");
            System.out.println("2 - Календарь");
            System.out.println("3 - Добавить задачу");
            System.out.println("4 - Редактировать задачу");
            System.out.println("5 - Удалить задачу");
            System.out.println("6 - Детально о задаче");
            System.out.println("7 - Выход");

            do {
                try {
                    System.out.print("Введите пункт меню [1-7]:");
                    inViewMenu = inData.nextLine();
                    setExit(inViewMenu);
                    toDoit = Integer.parseInt(inViewMenu);

                    if (toDoit <= 0 || toDoit > 7) {
                        throw new InvalidDataExeption("The menu item does not exist");
                    }
                    bError = false;
                } catch (InvalidDataExeption ei) {
                    logger.error(ei.getMessage(),ei);
                    System.out.println("! Пункт меню не существует");
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(),e);
                    System.out.println("! Вы ввели не целое число");
                }
            } while (bError);

// обработка путкта меню
            switch (toDoit) {
                case 1: logger.info("Выбран п.1 \"Все задачи\""); System.out.println();ViewTask();
                    break;
                case 2: logger.info("Выбран п.2 \"Календарь\""); System.out.println("\n*** Календарь на сутки ***"); myCalendar();
                    break;
                case 3: logger.info("Выбран п.3 \"Добавить задачу\" ");System.out.println("\n*** Ввод новой задачи ***"); addTask();
                    break;
                case 4: logger.info("Выбран п.4 \"Редактировать задачу\"");System.out.println("\n*** Редактирование задачи ***"); ViewTask(); editTask();
                    break;
                case 5: logger.info("Выбран п.5 \"Удалить задачу\"");System.out.println("\n*** Удалить задачу ***"); deleteTask();
                    break;
                case 6: logger.info("Выбран п.6 \"Детально о задаче\"");System.out.println("\n*** Детально о задаче ***"); ViewTask(); showTaskDetails(getTask());
                    break;
                case 7: logger.info("Проложение завершено по команде п.7 \"Выход\" "); exit = true;
                    break;

            }
        }
    }

    /**
     * Метод используется для выбора или создания файла задач
     */
    public void getBD(){
        String dirPath = "BD";
        // получаем разделитель пути в текущей операционной системе
        String fileSeparator = System.getProperty("file.separator");
        File lastFile = new File("lastList.txt");

        // читаем имя последней базы задач
        try (BufferedReader bufRead = new BufferedReader(new FileReader("lastList.txt"))){
            if (!lastFile.exists()) {
                lastFile.createNewFile();
                System.out.println("Последний список задач недоступен");
                logger.info("Файл для записи имени последнего списка задач создан новый");
            } else if (lastFile.length() == 0) {
                System.out.println("Последний список задач недоступен");
                logger.info("Последний список задач недоступен файл пустой");
            } else {
                fileName = bufRead.readLine();
                System.out.println("Для продолжения работы с " + fileName + " нажмите Enter."); }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        //Проверяем существование папки, нет создаем
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)){
            File newDir = new File(dirPath);
           boolean createDir = newDir.mkdir();
           //Если директория не создана выход из программы
           if (!createDir) {System.out.println("Каталог создать не удалось");
           logger.error("Папку " + dirPath + "создать не удалось. Программа завершена");
           exit = true;}
        }
        File curentDir = new File(dirPath);
        for (File item : curentDir.listFiles()){
                System.out.println(item.getName());
            }
            if(curentDir.listFiles().length == 0) {System.out.println("Файлов нет");}

            do {
                System.out.println("Ведите имя файла (если такого нет, будет создан):");
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
                        System.out.println("Создан новый файл: " + fileName);
                        logger.debug("Создан новый " + dirPath + fileSeparator + fileName );
                    }
                } catch (IOException ex){
                    logger.error(ex.getMessage(),ex);
                }
            } else {
                System.out.println("Выбран файл: " + fileName);
            }
// сохраняем имя последней базы задач
        try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("lastList.txt"))){
            bufWrite.write(fileName);
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
            fileName = dirPath+fileSeparator+fileName;
            logger.info("Текущий файл списка задач " + fileName);
    }
    private void setExit (String inputString){
        if (inputString.equals("exit")){
            logger.info("Программа завершена по команде exit");
            System.exit(0);
        }
    }
}

