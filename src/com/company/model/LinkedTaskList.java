package com.company.model;

public class LinkedTaskList extends TaskList{

    private int pointer; // begin 1    
    private Node first;
    private Node last;

    private static class Node{
        Task data;
        Node next;
        Node prev;
        Node(Node prev, Task element, Node next) {
            this.data = element;
            this.next = next;
            this.prev = prev;
        }
    }

    public void add(Task task){

        if (task.equals(null)) {
            try {
                throw new NullPointerException();
            }
            catch (NullPointerException e) {
                return;
            }
        }
         Node newNode = new Node(null,task, null);

        if (first == null && last == null) {
            first = newNode;
            last = newNode;
            first.next = last;
            last.prev = first;

        } else {
            newNode.prev = last;
            last.next = newNode;
            last = newNode;

        }
        pointer++;
    }

    public int size(){

        return (pointer);
    }

    public Task getTask(int index){
        if (index >= pointer || index < 0) {
            try {
                throw new NullPointerException("Task with an index:" + index + " does not exist");
            } catch (NullPointerException e){
                index=pointer-1;
            }
        }

        if (pointer == 0) return null;
        Node x = first;
        if (index == 0 && pointer == 1){
            return first.data;
        }else {

            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x.data;
        }
    }

    public boolean remove (Task task){
    
        if (task == null) {
                return false;
            }
        
        if (pointer == 0) return false;

        for (Node i = first; i != null; i=i.next) {

            if (task.equals(i.data)){
                if (i.equals(first) && pointer==1){
                    first = last = null;
                } else
                if (i.equals(first) && pointer > 1)  {

                    first = i.next;
                    first.prev = null;

                } else if (i.equals(last)){
                    last = i.prev;
                    last.next = null;
                } else{
                Node prevNode = i.prev;
                Node nextNode = i.next;
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
                i.prev = null;
                i.next = null;
                i.data = null;
                }

                pointer--;
                return true;
            }
        }
        return false;
    }

/*
    public LinkedTaskList incoming(int from, int to){
        LinkedTaskList incom = new LinkedTaskList();
        for (int i = 0; i < pointer; i++){
            if (getTask(i).isActive() & (getTask(i).nextTimeAfter(from) != -1)
                    & (getTask(i).nextTimeAfter(from) <= to))
                incom.add(getTask(i));
        }
        return incom;
    }*/
}