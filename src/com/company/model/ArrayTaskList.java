package com.company.model;

public class ArrayTaskList extends TaskList {
    private static final int DEFAULT_SIZE = 10; // ? only final
    private int pointer = 0;
    private Task [] elements = new Task[DEFAULT_SIZE];
    /**
     * Метод добавления в список указанной задачи
     * @param  task задача для добавления
     */
    public void add(Task task){
        if (task.equals(null)) {
            try {
                throw new NullPointerException();
            }
            catch (NullPointerException e) {
                return;
            }
        }
        if (pointer < elements.length-1){
            elements[pointer] = task;
            pointer++;
        } else {

            Task [] tempArray = new Task[elements.length+DEFAULT_SIZE];
            System.arraycopy(elements, 0, tempArray, 0, elements.length);
            elements = tempArray;
            elements[pointer] = task;
            pointer++;
        }
    }
    /**
     * Метод удаляет из списока указанную задачу
     * @param  task задача для удаления
     * @return true, если задача была в списке и удалена
     */
    public boolean remove (Task task){
        int removePointer = 0;

        for (Task indexTask : elements ) {

            if (task.equals(indexTask)) {
                if ((removePointer == 0) & (pointer == 1)) {
                    elements[0] = null;
                    pointer = 0;
                } else
                if ((removePointer+1) == elements.length){
                    elements[removePointer] = null;
                    pointer = removePointer;
                } else {
                    System.arraycopy(elements, removePointer+1, elements, removePointer, pointer - removePointer );
                    elements[pointer] = null;
                    pointer--;
                }
                if ((elements.length - (pointer+1)) == DEFAULT_SIZE){
                    Task [] tempArray = new Task[elements.length-DEFAULT_SIZE];
                    System.arraycopy(elements, 0, tempArray, 0, elements.length - DEFAULT_SIZE );
                    elements = tempArray;
                }
                return true;
            }
            removePointer++;
        }
        return false;
    }
    /**
     * Метод возвращает количество задач в списке
     * @return число integer задач в списке
     */
    public int size(){
        return (pointer);
    }
    /**
     * Метод возвращает задачу по индексу
     * @param index индекс задачи, первый элеменнт 0
     * @return задачу, Task
     */
    public Task getTask(int index){
        if (index > pointer || index < 0) {
            try {
                throw new NullPointerException("Task with an index:" + index + " does not exist");
            } catch (NullPointerException e){
                index=pointer;
            }
        }
        return elements[index];
    }
}