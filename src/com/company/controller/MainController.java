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
    // номер задачи в текущем списке
    private int indexTask;


    public static void main(String[] args) throws IOException, ParseException {
        MainController controller = new MainController();
        LOGGER.info("Запуск приложения");
        controller.viewMenu();
    }
    /**
     * Метод для вывода активных Задач на сутки
     *
     */
    public void myCalendar (){
        LOGGER.info("Выбран п.2 \"Календарь\"");
        SimpleDateFormat formatDate = new SimpleDateFormat(IN_FORMAT_DATE);
        Date startDate = enterDate("Введите дату и время начала календаря ");
        Date endDate = enterDate("Введите дату и время окончания календаря ");
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
     * Метод добавления новой задачи
     *
     */
    public void addTask() throws IOException {
        LOGGER.info("Выбран п.3 \"Добавить задачу\" ");
        String nameTask;
        Date beginData = new Date() ;
        Date endData;
        int currentInterval = 0;
        int repeatTask;
        do{
            repeatTask = enterPozitivInt("Задача будет повторяться (1), не будет (0). Введите [0-1]");
            if (repeatTask <= 1){
                break;
            }
        }while (true);

        System.out.print("Введите название задачи:");
        nameTask = inData.nextLine();
        setExit(nameTask);
        if (repeatTask == 1){
            beginData = enterDate("Введите дату и время начала задачи ");
        }
        endData = enterDate("Введите дату и время окончания задачи ");
        if (repeatTask == 1){
            do{
                currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число мин. > 0): ");
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
        LOGGER.info("Добавлена новая задача " + nameTask);
        TaskIO.writeText(currentList, new File(fileName));
        LOGGER.info("Задача \""+nameTask+"\" записана в файл");
    }
    /**
     * Метод удаление задачи из списка по номеру в списке
     *
     */
    public void deleteTask() throws IOException {
        LOGGER.info("Выбран п.5 \"Удалить задачу\"");
        if(currentList.size() == 0){
            return;
        }
        indexTask = enterNumber("Введите номер задачи для удаления:");
        String toLoggerDelNname = currentList.getTask(indexTask).getTitle();
        currentList.remove(currentList.getTask(indexTask));
        TaskIO.writeText(currentList,new File(fileName));
        LOGGER.info("Задача "+ toLoggerDelNname+" удалена из файла");
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
        Date enterDate;
        int deltaYear;
        do {
            try {
                System.out.println(enterMess);
                System.out.print("формат данных (дд.мм.гггг чч:мм), Enter - текущая дата: ");
                stringIn = inData.nextLine();
                setExit(stringIn);
                if (stringIn.length() == 0){
                    enterDate = new Date();
                } else{
                    enterDate = format.parse(stringIn);
                    // Преобразуем в календарь, получаем года и их разницу
                    Calendar enterToCalendar = Calendar.getInstance();
                    Calendar currentToCalendar = Calendar.getInstance();
                    enterToCalendar.setTime(enterDate);
                    currentToCalendar.setTime(new Date());
                    deltaYear = enterToCalendar.get(Calendar.YEAR) - currentToCalendar.get(Calendar.YEAR);
                    if (deltaYear > MAX_YEAR){
                        throw new ParseException("Время задачи более " + MAX_YEAR + " лет",0);
                    }
                }
                break;
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(),e);
                System.out.println("! Не верные данные");
            }
        } while (true);
        return enterDate;
    }

    /**
     * Метод используется для проверки ввода выбора номера задачи
     *@param messEnter String для вывода приглашения
     *@exception InvalidDataExeption ошибка, ввод не существующего номера задачи
     *@exception NumberFormatException ошибка, ввод не целого числа
     *@return дату в формате "dd.MM.yyyy hh:mm"
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
            System.out.println("! Такой задачи не существует");
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(),e);
            System.out.println("! Вы ввели не целое число");
        }
    }while (true);
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
                System.out.println("! Вы ввели не целое число");
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
                System.out.println("! Вы ввели отрицательное число");
            }
        }while (true);
        return currentInt;
    }
    /**
     * Метод используется для проверки редактирования существующих задач
     */
    public void editTask() throws IOException {
        LOGGER.info("Выбран п.4 \"Редактировать задачу\"");
        if(currentList.size() == 0){
            return;
        }
        indexTask = enterNumber("Введите номер задачи:");
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
            System.out.println("0. Название задачи: " + nameTask);
            System.out.println("1. Время начала задачи: " + formatDate.format(beginData));
            System.out.println("2. Время окончания задачи: " + formatDate.format(endData));
            if (currentTask.isRepeated()){
                repeatTask = "ПОВТОРЯЕМАЯ";
            }else{
                repeatTask = "НЕПОВТОРЯЕМАЯ";
            }
            System.out.println("3. Задача " + repeatTask +". Время повторения в минутах: " + currentInterval);
            if (currentTask.isActive()){
                System.out.println("4. Задача активна");
            } else {
                System.out.println("4. Задача не активна");
            }
            System.out.println("5. Изменить тип задачи");
            System.out.println("6. Сохранить и выйти");
            editMeny = enterPozitivInt("Введите пункт меню для редактирования:");

            switch (editMeny) {

                case 0:
                    System.out.print("0. Ведите название задачи: ");
                    nameTask = inData.nextLine();
                    setExit(nameTask);
                    break;
                case 1:
                    beginData = enterDate("Введите дату и время начала задачи ");
                    break;
                case 2:
                    endData = enterDate("Введите дату и время окончания задачи ");
                    break;
                case 3:
                    if(!currentTask.isRepeated()){
                        System.out.println("! Невозможно изменить. Измените тип задачи п.5");
                    } else {
                        do{
                            currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число мин.)>0: ");
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
                        beginData = enterDate("Введите дату и время начала задачи ");
                        endData = enterDate("Введите дату и время окончания задачи ");
                        do {
                            currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число мин.)>0: ");
                            if (currentInterval > 0){
                                break;
                            }
                        }while (true);
                    }
                    break;
                case 6:
                    TaskIO.writeText(currentList, new File(fileName));
                    LOGGER.info("Задача \"" + nameTask + "\" изменина");
                    return;
                default:
                        System.out.println("! Такого пункта не существует");
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
     * Метод используется для вывода всех существующих задач
     */
    public void viewTask(){
        LOGGER.info("Выбран п.1 \"Все задачи\"");
        System.out.println("*** Список всех задач ***");
        if (currentList.size() == 0){
            System.out.println("Список задач пуст");
        } else {
                System.out.print(currentList.toString());
            }
        }


    /**
     * Метод используется для основного меню
     */
    public void viewMenu() throws IOException, ParseException {
        int toDoit;
        System.out.println("### Welcome to TASK MANAGER ###");
        System.out.println("* Для выхода из программы введите exit");
        //выбор файла для работы
        getBD();
        TaskIO.readText(currentList,new File(fileName));
        System.out.println();
        while (true) {
            System.out.println("*** Главное меню ***");
            System.out.println("1 - Все задачи");
            System.out.println("2 - Календарь");
            System.out.println("3 - Добавить задачу");
            System.out.println("4 - Редактировать задачу");
            System.out.println("5 - Удалить задачу");
            System.out.println("6 - Сохранить в файл");
            System.out.println("7 - Выход");

            toDoit = enterPozitivInt("Введите пункт меню [1-7]:");
            // обработка путкта меню
            switch (toDoit) {
                case 1:
                    System.out.println();
                    viewTask();
                    break;
                case 2:
                    System.out.println("\n*** Календарь ***");
                    myCalendar();
                    break;
                case 3:
                    System.out.println("\n*** Ввод новой задачи ***");
                    addTask();
                    break;
                case 4:
                    System.out.println("\n*** Редактирование задачи ***");
                    viewTask();
                    editTask();
                    break;
                case 5:
                    System.out.println("\n*** Удалить задачу ***");
                    viewTask();
                    deleteTask();
                    break;
                case 6:
                    saveToFile();
                    break;
                case 7:
                    LOGGER.info("Проложение завершено по команде п.7 \"Выход\" ");
                    System.exit(0);
                    default:
                        System.out.println("! Такого пункта не существует");
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
                LOGGER.info("Файл для записи имени последнего списка задач создан новый");
            } else if (lastFile.length() == 0) {
                System.out.println("Последний список задач недоступен");
                LOGGER.info("Последний список задач недоступен файл пустой");
            } else {
                fileName = bufRead.readLine();
                System.out.println("Для продолжения работы с " + fileName + " нажмите Enter."); }
        }catch (IOException e){
            LOGGER.error(e.getMessage(),e);
        }
        //Проверяем существование папки, нет создаем
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)){
            File newDir = new File(dirPath);
            //Если директория не создана выход из программы
            if (!newDir.mkdir()) {
                System.out.println("Каталог создать не удалось");
                LOGGER.error("Папку " + dirPath + "создать не удалось. Программа завершена");
                System.exit(0);
            }
        }
        File curentDir = new File(dirPath);
        if(curentDir.listFiles().length == 0) {
            System.out.println("Файлов нет");
        } else {
            for (File item : curentDir.listFiles()){
                System.out.println(item.getName());
            }
        }
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
                    LOGGER.debug("Создан новый " + dirPath + fileSeparator + fileName );
                }
            } catch (IOException ex){
                LOGGER.error(ex.getMessage(),ex);
            }
        } else {
            System.out.println("Выбран файл: " + fileName);
        }
        // сохраняем имя последней базы задач
        try (BufferedWriter bufWrite = new BufferedWriter(new FileWriter("lastList.txt"))){
            bufWrite.write(fileName);
        }catch (IOException e){
            LOGGER.error(e.getMessage(),e);
        }
        fileName = dirPath+fileSeparator+fileName;
        LOGGER.info("Текущий файл списка задач " + fileName);
    }
    public void setExit (String inputString){
        if (inputString.equals("exit")){
            LOGGER.info("Программа завершена по команде exit");
            System.exit(0);
        }
    }
    public void saveToFile() throws IOException {
        String newFileName;
        do{
            System.out.println("Введите имя файла:");
            newFileName = inData.nextLine();
            setExit(newFileName);
            if(newFileName.length() > 0){
                break;
            }
        }while (true);
        // сохраняем имя последней базы задач
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

