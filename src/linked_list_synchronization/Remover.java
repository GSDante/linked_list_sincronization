package linked_list_synchronization;

public class Remover extends Thread{
    private Linked_list list;

    public Remover(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public void run() {
        int item = (int) (Math.random() * list.size()) + 1;
        list.remove(item);
    }
}
