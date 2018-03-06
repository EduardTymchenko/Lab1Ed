package com.company.controller;

import com.company.model.ArrayTaskList;
import com.company.model.Task;
import com.company.model.TaskIO;
import com.company.model.TaskList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MainController {
    //private static final Logger logger = Logger.getLogger(MainController.class);
    private static int toDoit;
    private static String fileName;
    boolean exit = false;
    private TaskList currentList = new ArrayTaskList();

    public static void main(String[] args) throws IOException, ParseException {
        MainController controller = new MainController();
        controller.ViewMenu();
    }

public Task getTask(){
    Task currentTask;
    int indexTask;
    indexTask = enterNumber("Введите номер задачи:","! Такой задачи не существует","! Вы ввели не целое число");
    currentTask = currentList.getTask(indexTask);
    return currentTask;
    }

    public void showTaskDetails(Task inTask) {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.sss");
        System.out.println("0. Название задачи: " + inTask.getTitle());
        System.out.println("1. Время начала задачи: " + formatDate.format(inTask.getStartTime()));
        System.out.println("2. Время окончания задачи: " + formatDate.format(inTask.getEndTime()) );
        System.out.println("3. Время повторения в секундах: " + inTask.getRepeatInterval());
        if (inTask.isActive()){System.out.println("4.Задача активна");}
        else {System.out.println("4. Задача не активна");}
    }


    public void addTask() throws IOException {
        Scanner inData = new Scanner(System.in);
        String nameTask;
        Date beginData ;
        Date endData;
        int currentInterval;

        System.out.print("Введите название задачи:");
        nameTask = inData.nextLine();
        beginData = enterDate("Введите дату и время начала задачи ");
        endData = enterDate("Введите дату и время окончания задачи ");
        currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число сек): ");

if (currentInterval == 0){
    Task currentTask = new Task(nameTask,endData);
    currentList.add(currentTask);
} else {Task currentTask = new Task(nameTask,beginData,endData,currentInterval);
    currentList.add(currentTask);
}
        TaskIO.writeText(currentList, new File(fileName));
    }

    public void deleteTask() throws IOException {
        int indexTask;
            ViewTask();
            indexTask = enterNumber("Введите номер задачи для удаления:","! Такой задачи не существует","! Вы ввели не целое число");
            currentList.remove(currentList.getTask(indexTask));
            TaskIO.writeText(currentList,new File(fileName));
    }

    public Date enterDate(String enterMess){
        Scanner inData = new Scanner(System.in);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        String stringIn;
        Date enterDate = new Date();
        boolean bError = true;
        do {
            try {
                System.out.println(enterMess);
                System.out.print("формат данных (дд.мм.гггг чч:мм): ");
                stringIn = inData.nextLine();
                if (stringIn.length() == 0){enterDate = new Date();
                } else{
                    enterDate = format.parse(stringIn);
                }
                bError = false;
            } catch (ParseException e) {
                System.out.println("! Не верные данные");
            }
        } while (bError);
        return enterDate;
    }
// для выбора номера задачи
    public int enterNumber(String messEnter, String messDataError, String messFormatError){
    Scanner inIndex = new Scanner(System.in);
    int currentIndex = 0;
    boolean bError = true;
    do {
        try {
            System.out.print(messEnter);
           currentIndex =Integer.parseInt(inIndex.nextLine()) ;
            if (currentIndex < 0 || currentIndex > currentList.size()-1) {
                throw new InvalidDataExeption("The menu item does not exist");
            }
            bError = false;
        } catch (InvalidDataExeption e) {
            System.out.println(messDataError);
        } catch (NumberFormatException e) {
            System.out.println(messFormatError);
        }
    }while (bError);
    return currentIndex;
}

    public int enterPozitivInt (String enterMess){
        Scanner inData = new Scanner(System.in);
        boolean bError = true;
        int currentInt = 0;
        do {
            try {
                System.out.print(enterMess);
                currentInt = Integer.parseInt(inData.nextLine());
                if (currentInt < 0 ) {
                    throw new InvalidDataExeption("The menu item does not exist");
                }
                bError = false;
            } catch (InvalidDataExeption e){
                System.out.println("! Вы ввели отрицательное число");
            } catch (NumberFormatException e) {
                System.out.println("! Вы ввели не целое число");
            }
        } while (bError);
        return currentInt;
    }

    public void editTask() throws IOException {
        Scanner inData = new Scanner(System.in);
        boolean exitEdit = false;
        int editMeny;
        String nameTask;
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
              editTask.setTitle(inData.nextLine());
                  break;
              case 1: beginData = enterDate("Введите дату и время начала задачи ");
                  editTask.setTime(beginData,editTask.getEndTime(),editTask.getRepeatInterval());
                  break;
              case 2: endData = enterDate("Введите дату и время окончания задачи ");
                  editTask.setTime(editTask.getTime(),endData,editTask.getRepeatInterval());
                  break;
              case 3: currentInterval = enterPozitivInt("Введите интервал выполнения задачи(целое число сек): ");
                  editTask.setTime(editTask.getTime(),editTask.getEndTime(),currentInterval);
                  break;
              case 4: activ=!activ; editTask.setActive(activ);
                  break;
              case 5: TaskIO.writeText(currentList, new File(fileName)); exitEdit = true;
              break;
          }

      }while (!exitEdit);
    }

    // Viewer
    // вывод списка задач (всех или календаря)
    public void ViewTask()  {
    int index = 0;
        System.out.println("*** Список всех задач ***");
        for(Task task : currentList){
            System.out.println(index + ". " + task.getTitle());
            index++;
        }
    }

    // вывод меню
    public void ViewMenu() throws IOException, ParseException {
        //getBD(); //выбор файла для работы
        fileName = "BD\\1.txt"; //для отладки
        TaskIO.readText(currentList,new File(fileName));

        Scanner inMenu = new Scanner(System.in);

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
                    toDoit = Integer.parseInt(inMenu.nextLine());

                    if (toDoit <= 0 || toDoit > 7) {
                        throw new InvalidDataExeption("The menu item does not exist");
                    }
                    bError = false;
                } catch (InvalidDataExeption ei) {
                    System.out.println("! Пункт меню не существует");
                } catch (NumberFormatException e) {
                    System.out.println("! Вы ввели не целое число");
                }
            } while (bError);

// обработка путкта меню
            switch (toDoit) {
                case 0:

                    break;
                case 1: ViewTask();
                    break;
                case 2:
                    break;

                case 3: System.out.println("*** Ввод новой задачи ***"); addTask();
                    break;
                case 4: System.out.println("*** Редактирование задачи ***"); ViewTask(); editTask();
                    break;
                case 5: System.out.println("*** Удалить задачу ***"); deleteTask();
                    break;
                case 6: System.out.println("*** Детально о задаче ***"); ViewTask(); showTaskDetails(getTask());
                    break;
                case 7: exit = true;
                    break;
            }
        }
    }

    //Выбор файла для звписи
    public void getBD(){
        Scanner nameFile = new Scanner(System.in);
        String dirPath = "BD";
        // получаем разделитель пути в текущей операционной системе
        String fileSeparator = System.getProperty("file.separator");
        //Проверяем существование папки, нет создаем
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)){
            File newDir = new File(dirPath);
           boolean createDir = newDir.mkdir();
           //Если директория не создана выход из программы
           if (!createDir) {System.out.println("Каталог создать не удалось");
           exit = true;}
        } else {
            File curentDir = new File(dirPath);
            for (File item : curentDir.listFiles()){
                System.out.println(item.getName());
            }
            if(curentDir.listFiles().length == 0) {System.out.println("Файлов нет");}
            System.out.println("Ведите имя файла (если такого нет, будет создан):");
            fileName = nameFile.nextLine();
            File currentFile = new File(dirPath+fileSeparator+fileName);
            if (!currentFile.exists()){
                try{
                    boolean created = currentFile.createNewFile();
                    if (created) {
                        System.out.println("Создан новый файл: " + fileName);
                    }
                } catch (IOException ex){
                    System.out.println(ex.getMessage());
                }
            } else {System.out.println("Выбран файл: " + fileName);}
        }
    fileName = dirPath+fileSeparator+fileName;
    }
}

