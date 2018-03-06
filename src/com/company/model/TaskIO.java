package com.company.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TaskIO {

    public static void write(TaskList tasks, OutputStream out) throws IOException {
        //use try-with-resources for Java 7 and later
        try (DataOutputStream outStream =  new DataOutputStream(out))
        {
            outStream.writeInt(tasks.size());
            for (Task task : (Iterable<Task>) tasks) {
                outStream.writeInt(task.getTitle().length());
                outStream.writeUTF(task.getTitle());
                outStream.writeBoolean(task.isActive());
                outStream.writeInt(task.getRepeatInterval());
                if (task.isRepeated()) {
                    outStream.writeLong(task.getStartTime().getTime());
                    outStream.writeLong(task.getEndTime().getTime());
                } else {
                    outStream.writeLong(task.getTime().getTime());
                }
            }
        } catch (IOException e){
            System.out.println("Could not open OutputStream:" + out);
        }
    }

    public static void read(TaskList tasks, InputStream in) throws IOException{

        try (DataInputStream inStream = new DataInputStream(in))
        {
            int count = inStream.readInt();
            for(int i = 0; i < count; i++){
                Task task;
                int length = inStream.readInt();
                String title = inStream.readUTF();
                boolean active = inStream.readBoolean();
                int interval = inStream.readInt();
                Date startTime = new Date(inStream.readLong());
                if (interval != 0) {
                    Date endTime = new Date(inStream.readLong());
                    task = new Task(title, startTime, endTime, interval);

                } else {
                    task = new Task(title, startTime);
                }
                task.setActive(active);
                tasks.add(task);
            }
        } catch (IOException e){
            System.out.println("Could not open OutputStream:" + in);
        }
    }

    public static void writeBinary(TaskList tasks, File file) throws IOException {

        try (FileOutputStream fileOut = new FileOutputStream(file))
        {
            write(tasks,fileOut);
        }
    }

    public static void readBinary(TaskList tasks, File file) throws IOException{

        try (FileInputStream fileInput = new FileInputStream(file)){
            read(tasks, fileInput);
        }
    }

    public static void write(TaskList tasks, Writer out) throws IOException {
        final String TIME_PATTERN = "[yyyy-MM-dd HH:mm:ss.sss]";
        SimpleDateFormat DATE = new SimpleDateFormat(TIME_PATTERN);
        String uotString = "";
        int numberLine = 1;

        try {

            for(Task task : tasks) {
                uotString += '"' + task.getTitle() + '"';

                if (!task.isRepeated()) {

                    uotString +=" at " + DATE.format(task.getTime());

                } else {
                    uotString +=" from " + DATE.format(task.getStartTime()) + " to " + DATE.format(task.getEndTime()) +
                            " every [";

                    int tmpInterval = task.getRepeatInterval();
                    if(tmpInterval/86400 == 1){
                        uotString +="1 day";
                        tmpInterval = tmpInterval - 86400;
                    }
                    else if (tmpInterval/86400 > 1) {
                        uotString += tmpInterval/86400 +" days";
                        tmpInterval = tmpInterval%86400;
                    }

                    if (tmpInterval/3600 == 1){
                        if(uotString.matches("(.*)day(.*)")) uotString +=" ";
                        uotString +="1 hour";
                        tmpInterval = tmpInterval - 3600;
                    }
                    else if (tmpInterval/3600 > 1){
                        if(uotString.matches("(.*)day(.*)")) uotString +=" ";
                        uotString +=tmpInterval/3600 +" hours";
                        tmpInterval = tmpInterval%3600;
                    }

                    if (tmpInterval/60 == 1) {
                        if(uotString.matches("(.*)day(.*)")
                                || uotString.matches("(.*)hour(.*)")) uotString +=" ";
                        uotString +="1 minute";
                        tmpInterval = tmpInterval - 60;
                    }
                    else if (tmpInterval/60 > 1){
                        if(uotString.matches("(.*)day(.*)")
                                || uotString.matches("(.*)hour(.*)")) uotString +=" ";
                        uotString +=tmpInterval/60 +" minutes";
                        tmpInterval = tmpInterval%60;
                    }

                    if (tmpInterval == 1) {
                        if(uotString.matches("(.*)day(.*)")
                                || uotString.matches("(.*)hour(.*)")
                                || uotString.matches("(.*)minute(.*) ")) uotString +=" ";
                        uotString +="1 second";}
                    else if(tmpInterval > 1){
                        if(uotString.matches("(.*)day(.*)")
                                || uotString.matches("(.*)hour(.*)")
                                || uotString.matches("(.*)minute(.*) ")) uotString +=" ";
                        uotString +=tmpInterval +" seconds";
                    }

                    uotString +="]";
                }
                if (!task.isActive()) uotString +=" inactive";

                if (numberLine < tasks.size()) uotString +=";\n";
                else uotString +=".\n";

                out.write(uotString);
                uotString = "";
                numberLine++;
            }

        } finally {
            out.flush();
            out.close();
        }
    }

    public static void read(TaskList tasks, Reader in) throws IOException {
        BufferedReader buffReader = null;
        try {
            buffReader = new BufferedReader(in);
            String line;

            while ((line = buffReader.readLine()) != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                String title_tmp= "";
                String time_tmp = "";
                Date timeDate = null;
                String timeEnd_tmp = "";
                Date timeEndDate = null;
                String intervalString = "";
                int interval_tmp=0;
                Task task;
                //for title
                title_tmp = line.substring(1,line.lastIndexOf("\""));

                //for time
                if (line.matches("(.*)at(.*)")){
                    time_tmp = line.substring(line.indexOf("[")+1,line.indexOf("]"));
                    try {

                        timeDate = format.parse(time_tmp);
                    } catch (ParseException ex){}
                    task = new Task(title_tmp,timeDate);
                    tasks.add(task);
                } else {
                    time_tmp = line.substring(line.indexOf("[")+1,line.indexOf("]"));
                    for(String to : line.split(" to ")) timeEnd_tmp = to;
                    timeEnd_tmp = timeEnd_tmp.substring(timeEnd_tmp.indexOf("[")+1,timeEnd_tmp.indexOf("]"));
                    try { timeDate = format.parse(time_tmp);
                        
                        timeEndDate = format.parse(timeEnd_tmp);
                    } catch (ParseException ex){}
                    // **** interval
                    intervalString = line.substring(line.lastIndexOf("[")+1,line.lastIndexOf("]"));

                    if(intervalString.matches("(.*)day(.*)")){
                        interval_tmp = Integer.parseInt(intervalString.substring(0,intervalString.indexOf(" ")));
                        interval_tmp *= 86400;
                        for(String to : intervalString.split("day(s?) ")) intervalString = to;
                    }
                    if(intervalString.matches("(.*)hour(.*)")){
                        interval_tmp +=3600*Integer.parseInt(intervalString.substring(0,intervalString.indexOf(" ")));
                        for(String to : intervalString.split("hour(s?) ")) intervalString = to;
                    }
                    if(intervalString.matches("(.*)minute(.*)")){
                        interval_tmp +=60*Integer.parseInt(intervalString.substring(0,intervalString.indexOf(" ")));
                        for(String to : intervalString.split("minute(s?) ")) intervalString = to;
                    }
                    if(intervalString.matches("(.*)second(.*)")){
                        interval_tmp +=Integer.parseInt(intervalString.substring(0,intervalString.indexOf(" ")));
                        for(String to : intervalString.split("second(s?) ")) intervalString = to;
                    }
                    task = new Task(title_tmp,timeDate,timeEndDate,interval_tmp);
                    tasks.add(task);
                }
                if (!line.matches("(.*)inactive(.*)")){task.setActive(true);}
            }
        } finally {
            buffReader.close();
        }

    }

    public static void writeText(TaskList tasks, File file) throws IOException {
        //use try before Java 7
        FileWriter writerFile = null;
        try {
            writerFile = new FileWriter(file);
            write(tasks, writerFile);
        } catch (IOException e){
            System.out.println("File not open to write:" + file);
        }
        finally {
            writerFile.close();
        }

    }
    public static void readText(TaskList tasks, File file) throws IOException, ParseException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            read(tasks, fileReader);
        } catch (IOException e){
            System.out.println("File not open to read:" + file);
        }
        finally {
            fileReader.close();
        }
    }
}