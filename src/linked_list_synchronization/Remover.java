package linked_list_synchronization;

import java.util.Random;

//Classe da thread do tipo remove
public class Remover extends Thread{
    private Linked_list list;

    public Remover(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public void run() {
        try{
            //Pega um índice randômico para remover a partir dele na lista
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
