package linked_list_synchronization;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Linked_list {
	LinkedList<Integer> resource;
	Lock lock;
	
	public Linked_list(){
		resource = new LinkedList<Integer>();
		lock = new ReentrantLock(true);
	}
	
	public void search(int index) {
		resource.get(index);
	}
	
	public void insert(int value) {
		lock.lock();
		
		resource.add(value);
		System.out.println("Thread " + Thread.currentThread().getName() + 
				" inserted " + value);
			
		lock.unlock();
		
	}
	
	public void remove(int index, int value) {
		lock.lock();
		
		while(resource.size() == 0) {
			System.out.println("Thread " + Thread.currentThread().getName() +
					" suspended because resouce is empty");
		}
		
		lock.unlock();
	}
}
