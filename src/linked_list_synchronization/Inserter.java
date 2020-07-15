package linked_list_synchronization;

import java.util.Random;

import static java.lang.Math.random;

public class Inserter extends Thread{
    private Linked_list list;

    public Inserter(String name, Linked_list list ){
        super(name);
        this.list = list;
    }
    public void run() {
        try{
            Random r = new Random();
            int valor = r.nextInt(100) ;
            list.startInsert(valor);
        }catch (IllegalArgumentException e){
            System.out.println(Thread.currentThread().getName() + " inserting in a empty list");
        }finally {
            list.finishInsert();
        }
    }
}
