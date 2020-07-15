package linked_list_synchronization;

import java.util.Random;

public class Remover extends Thread{
    private Linked_list list;

    public Remover(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public void run() {
        try{
            Random r = new Random();
            int valor = r.nextInt(list.getSize());
            list.startRemove(valor);
        }catch (IllegalArgumentException e){
            System.out.println(Thread.currentThread().getName() + " removing in a empty list");
        }finally {
            list.finishRemove();
        }
    }
}
