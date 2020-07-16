package linked_list_synchronization;

import java.util.Random;
//Classe da thread do tipo search
public class Searcher extends Thread{
    private Linked_list list;

    public Searcher(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public void run() {
        try{
            //Pega um índice randômico para pesquisar na lista
            Random r = new Random();
    	    int index = r.nextInt(list.getSize());
            list.startSearch(list.getResource().get(index));
        }catch (IllegalArgumentException e){
            System.out.println(Thread.currentThread().getName() + " searching in a empty list");
        }finally {
            list.finishSearch();
        }
    }

}
