package linked_list_synchronization;

public class Searcher extends Thread{
    private Linked_list list;

    public Searcher(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public void run() {
    	int valor = (int) (Math.random() * list.getSize()) + 1;
        list.search(valor);
    }

}
