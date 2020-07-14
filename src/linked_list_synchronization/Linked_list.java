package linked_list_synchronization;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Linked_list {
	LinkedList<Integer> resource;
	Lock lock;
	Condition readySearch;
	Condition readyInsert;
	Condition readyRemove;
	
	public Linked_list(){
		resource = new LinkedList<Integer>();
		lock = new ReentrantLock(true);
		readySearch = lock.newCondition();
		readyInsert = lock.newCondition();
		readyRemove = lock.newCondition();
	}
	
	public void search(int value) {
		int index = resource.indexOf(value);
		System.out.println("Thread " + Thread.currentThread().getName() +
				" got " + resource.get(index) + " by index " + value);
	}
	
	public void insert(int value) {
		lock.lock();
		
		resource.add(value);
		System.out.println("Thread " + Thread.currentThread().getName() + 
				" inserted " + value);
			
		lock.unlock();
	}
	
	public void remove(int index){
		lock.lock();
		try {
			while(resource.size() == 0) {
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resouce is empty");
				readyRemove.await();
			}
			
			int value = resource.remove(index);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" removed " + value + " by index " + index);
			readyRemove.signal();
			
		}catch(InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	
	public int getSize() {
		return resource.size();
	}
}
