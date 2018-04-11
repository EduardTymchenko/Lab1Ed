package com.company.model;

import java.util.*;

public class Tasks {
    /**
     * Метод возвращает список задач, которые должны выполняться в заданый промежуток времени
     * @param  tasks список всех задач
     * @param start время начала интервала
     * @param end время окончания интервала
     * @return список задач
     */
    public static ArrayList<Task> incoming(Iterable<Task> tasks, Date start, Date end){
        ArrayList<Task> incomin = new ArrayList<>();
        for (Task t : tasks) {
            Date nextTime = t.nextTimeAfter(start);
            if (nextTime != null && nextTime.compareTo(end) <= 0) {
                incomin.add(t);
            }
        }
        return incomin;
    }
    /**
     * Метод возвращает сортированый по дате и времени календарь выполнения задач
     * @param  tasks список всех задач
     * @param start время начала интервала
     * @param end время окончания интервала
     * @return список задач за указанный интервал,
     * причем задачи могут повторятся, если выполняются несколько раз за заданный период
     */
    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end) {
        TreeMap<Date, Set<Task>> calendar = new TreeMap<>();
        Iterable<Task> inc = incoming(tasks, start, end);
        for (Task task : inc) {
            Date tmp = task.nextTimeAfter(start);
            while(tmp != null && tmp.compareTo(end) <= 0) {
                if (calendar.containsKey(tmp)) {
                    calendar.get(tmp).add(task);
                } else {
                    Set<Task> setOfTasks = new HashSet<>();
                    setOfTasks.add(task);
                    calendar.put(tmp, setOfTasks);
                }
                tmp = task.nextTimeAfter(tmp);
            }
        }
        return calendar;
    }
}
