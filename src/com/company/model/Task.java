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

    public Task(String title, Date time){
        this.title = title;
        this.time = time;
        active = false;
    }
    public Task(String title, Date start, Date end, int interval){
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    // methods for tasks that do not repeat
    public Date getTime(){
        if (isRepeated()) {
            return start;
        } else {
            return time;
        }
    }
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
    // methods for tasks that repeat
    public Date getStartTime(){
        if (!isRepeated()){
            return time;
        } else {
            return start;
        }
    }
    public Date getEndTime(){
        if (!isRepeated()){
            return time;
        } else {
            return end;
        }
    }
    public int getRepeatInterval(){
        if (!isRepeated()){
            return 0;
        } else {
            return interval;
        }
    }
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
       /* if (interval == 0) {
            try {
                throw new IllegalArgumentException();
            }
            catch(IllegalArgumentException e){
                time = this.end;
            setTime(time);
            }
        } */

        if (!isRepeated()){
            time = null;
        }
        this.start=start;
        this.end=end;
        this.interval = interval;
    }
    public boolean isRepeated(){
        if (interval!=0) {
            return true;}
        else return false;
    }

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