package linked_list_synchronization;

import static java.lang.Math.random;

public class Inserter extends Thread{
    private Linked_list list;

    public Inserter(String name, Linked_list list ){
        super(name);
        this.list = list;
    }
    public void run() {
        int item = (int) (random() * 100) + 1;
        list.insert(item);
    }
}
