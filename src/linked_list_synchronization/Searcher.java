package linked_list_synchronization;

public class Searcher extends Thread{
    private Linked_list list;

    public Searcher(String name, Linked_list list) {
        super(name);
        this.list = list;
    }

    @Override
    public int run(int item) {
        return list.search(item);
    }

}
