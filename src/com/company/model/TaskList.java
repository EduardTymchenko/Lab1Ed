package com.company.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

            /**
             * Метод проверяет следующий элемент коллекции
             * @return  true - есть, false - нет
             */
            @Override
            public boolean hasNext() {
                return currentIndex < size();

            }
            /**
             * Метод возвращает следующий элемент коллекции
             * @return  задачу
             */
            @Override
            public Task next() {
                nextOk = true;
                return getTask(currentIndex++);

            }
            /**
             * Метод удаляет текущий элемент коллекции
             */
            @Override
            public void remove(){

                if (!nextOk) throw new IllegalStateException();
                TaskList.this.remove(getTask(--currentIndex));


            }
        };
    }
    /**
     * Метод сравнивает обекты
     */
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

    /**
     * Метод вычисляет хеш-код объекта
     * @return хеш-код, integer
     */
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        for (int i = 0; i < size(); i++) {
            
                result = prime * result + i;
            }
        return result;
    }
    /**
     * Метод выводит на экран полную информацию о задаче
     * @return строку сданными о задаче
     */
        @Override
    public String toString(){
        SimpleDateFormat formatDate = new SimpleDateFormat("[YYYY-MM-dd HH:mm:ss.SSS]");
           String out = "";

        for (int i = 0; i < size(); i++){
            out += i + ". " + getTask(i).getTitle()+" "+ "from: " +
                   formatDate.format(getTask(i).getStartTime()) +" "+ "to: " +
                   formatDate.format(getTask(i).getEndTime()) +" "+ "repeat:" +
                    getTask(i).getRepeatInterval()+" "+
                    "Active:"+getTask(i).isActive()+" "+
                    "Repeat:"+getTask(i).isRepeated()+" "+
                    "\n";
                    }
        return out;
        }
    }