package com.company.model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable{
    private String title;
    private boolean active;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    /**
     * Конструктор неактивной задачи без повторения
     * @param title String название задачи
     * @param time Date время выполнения задачи
     */
    public Task(String title, Date time){
        this.title = title;
        this.time = time;
        active = false;
    }
    /**
     * Конструктор неактивной задачи повторяющеяся через интервал времени
     * @param title String название задачи
     * @param start Date время начала выполнения задачи
     * @param end Date время окончания выполнения задачи
     * @param interval Integer время повторения в секундах
     */
    public Task(String title, Date start, Date end, int interval){
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
    }
    /**
     * Метод получения названия задачи
     * @return  название задачи, String
     */
    public String getTitle(){
        return title;
    }
    /**
     * Метод присвоения названия задачи
     * @param title String название задачи
     */
    public void setTitle(String title){
        this.title = title;
    }
    /**
     * Метод получения состояния активности задачи
     * @return true - активна, false - неактивна
     */
    public boolean isActive(){
        return active;
    }
    /**
     * Метод установки состояния активности задачи
     * @param active (true - активна, false - неактивна)
     */
    public void setActive(boolean active){
        this.active = active;
    }

    /**
     * Метод получения времени выполнения задачи (для неповторяющихся)
     * @return  время выполнения
     */
    public Date getTime(){
        if (isRepeated()) {
            return start;
        } else {
            return time;
        }
    }
    /**
     * Метод установки времени выполнения задачи (для неповторяющихся)
     * @param time  время выполнения
     */
    public void setTime(Date time){
        if (time.before(new Date(0))) {
                try {
                    throw new IllegalArgumentException();
                } catch (IllegalArgumentException e) {
                    time = new Date(0);
                }
        }
        if (isRepeated()){
            start = null;
            end = null;
            interval = 0;
        }
        this.time = time;
    }

    /**
     * Метод получения времени начала выполнения задачи (для повторяющихся)
     * @return  время начала выполнения
     */
    public Date getStartTime(){
        if (!isRepeated()){
            return time;
        } else {
            return start;
        }
    }
    /**
     * Метод получения времени окончания выполнения задачи (для повторяющихся)
     * @return  время окончания выполнения
     */
    public Date getEndTime(){
        if (!isRepeated()){
            return time;
        } else {
            return end;
        }
    }
    /**
     * Метод получения интервала выполнения задачи (для повторяющихся)
     * @return  время окончания выполненияю, integer секундах
     */
    public int getRepeatInterval(){
        if (!isRepeated()){
            return 0;
        } else {
            return interval;
        }
    }
    /**
     * Метод установки времени выполнения задачи (для повторяющихся)
     * @param start время начала выполнения задачи
     * @param end время окончания выполнения задачи
     * @param interval интервал выполнения задачи
     */
    public void setTime(Date start, Date end, int interval) {

        if (start.before(new Date(0))) {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                start = new Date(0);
            }
        }
        if (end.before(new Date(0))) {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                end = new Date(0);
            }
        }
        if (interval < 0) {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                interval = 0;
            }
        }
        if (!isRepeated()){
            time = null;
        }
        this.start=start;
        this.end=end;
        this.interval = interval;
    }
    /**
     * Метод получения значения типа задачи
     * @return true повторяемая задача
     * @return false неповторяемая задача
     */
    public boolean isRepeated(){
        if (interval!=0) {
            return true;}
        else return false;
    }
    /**
     * Метод возращает значение следующего времени выполнения  задачи
     * @param current время после которого определяется следующее повторение задачи
     * @return  время следующего повторения задачи или null, если задача больше не будет повторятся
     */
    public Date nextTimeAfter(Date current) {
        if (!isRepeated()& active){
            if (current.after(time) || current.equals(time)) return null;
            else return time;

        } else if (isRepeated() & active) {
            if(current.before(start)) return start;
            else if(current.after(end)) return null;
            else {
                for (long i = start.getTime(); i <= end.getTime(); i = i + interval * 1000) {
                    if ((i > current.getTime()) && (i != start.getTime()) ) return new Date(i);
                    continue;
                    
                }
            }
            }
            return null;
    }


}