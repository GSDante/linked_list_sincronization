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
    	Random r = new Random();
    	int index0 = r.nextInt(list.getSize());
    	System.out.println("fora: "+ index0);
    	try {
	    	
	    	int index = index0;
	        list.remove(index);
    	}catch(IllegalArgumentException e) {
    		System.out.println(Thread.currentThread().getName() + 
    				" is pushing on empty list");
    	}
    }
}
