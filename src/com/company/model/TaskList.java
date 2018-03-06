package com.company.model;

import java.io.Serializable;
import java.util.Iterator;


public abstract class TaskList implements Iterable<Task>,Serializable {
    abstract public void add(Task task);
    abstract public boolean remove(Task task);
    abstract public int size();
    abstract public Task getTask(int index);
    


    @Override
    public Iterator<Task> iterator() {
        return new Iterator<Task>(){
            int currentIndex = 0;
            boolean nextOk;


            @Override
            public boolean hasNext() {
                return currentIndex < size();

            }

            @Override
            public Task next() {

                nextOk = true;
                return getTask(currentIndex++);

            }

            @Override
            public void remove(){

                if (!nextOk) throw new IllegalStateException();
                TaskList.this.remove(getTask(--currentIndex));


            }
        };
    }

  @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        TaskList that = (TaskList) o;
        if (this.size() != that.size()) return false;
        
        
         
            if (this.toString().equals(that.toString())) {
                return true;
            }
        
return false;
    }


    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        for (int i = 0; i < size(); i++) {
            
                result = prime * result + i;
            }
        
        return result;
    }

        @Override
    public String toString(){
           String out = "";
        for (int i = 0; i < size(); i++){
            out += getTask(i).getTitle()+" "+
                    getTask(i).getStartTime()+" "+
                    getTask(i).getEndTime()+" "+
                    getTask(i).getRepeatInterval()+" "+
                    "Active:"+getTask(i).isActive()+" "+
                    "Repeat:"+getTask(i).isRepeated()+" "+
                    "\n";
                    }
        return out;
        }


/*
        public TaskList incoming(int from, int to){
            TaskList incom ;
            if (this instanceof LinkedTaskList) {
                incom = new LinkedTaskList();
            } else {
                incom = new ArrayTaskList();
            }

            for (int i = 0; i < size(); i++){
                if (getTask(i).isActive() & (getTask(i).nextTimeAfter(from) != -1)
                        & (getTask(i).nextTimeAfter(from) <= to))
                    incom.add(getTask(i));
            }
            return incom;
        }
*/

    }