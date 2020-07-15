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
	private int inserterActive = 0;
	private int insertersRequest = 0;
	private int removerActive = 0;
	private int removersRequest = 0;
	private int searchers = 0;
	private boolean blockSearchers = false;
	private boolean blockInserters = false;
	
	public Linked_list(){
		resource = new LinkedList<Integer>();
		resource.add(3);
		resource.add(5);
		resource.add(4);
		resource.add(3);
		resource.add(3);
		lock = new ReentrantLock(true);
		readySearch = lock.newCondition();
		readyInsert = lock.newCondition();
		readyRemove = lock.newCondition();
	}

	public LinkedList<Integer> getResource(){
		return this.resource;
	}

	public void startSearch(int value){
		lock.lock();
		try {
			if (blockSearchers) {
				//Se o número de buscadores for menor do que 0,então há buscadores
				//em espera
				System.out.print(Thread.currentThread().getName()+" suspended\n");
				readySearch.await();
			}
			// Incrementa o número de buscadores ativos
			searchers++;
			int index = resource.indexOf(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" got " + resource.get(index) + " by index " + index);
		}catch(InterruptedException e){
			e.printStackTrace();
		}catch(IndexOutOfBoundsException e) {
			System.out.println("Thread " + Thread.currentThread().getName() + 
					" trying to access non-existent index");
		}finally {
			lock.unlock();
		}
	}

	public void finishSearch(){
		lock.lock();
		searchers--;
		if(removersRequest > 0){
			readyRemove.signal();
		}
		lock.unlock();
	}


	
	public void startInsert(int value) {
		lock.lock();
		try{
			insertersRequest++;
			if(inserterActive > 0 || blockInserters ){
				readyInsert.await();
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is busy");
			}
			// Incrementa o número de inseridores ativos
			inserterActive++;

			//Bloqueia o acesso a outros inseridores
			blockInserters = false;
			resource.add(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" inserted " + value);
		}catch (InterruptedException e){
			e.printStackTrace();
		}finally {
			lock.unlock();
		}

	}

	public void finishInsert() {
		lock.lock();
		inserterActive--;
		//Desbloqueia o acesso para outros inseridores
		blockInserters = false;
		if(removersRequest > 0) {
			readyRemove.signal();
		}else if(insertersRequest > 0) {
			readyInsert.signal();
		}
		lock.unlock();
	}

	public void startRemove(int index){
		lock.lock();
		try {
			removersRequest++;
			if(inserterActive > 0 || removerActive > 0 || searchers > 0){
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is busy");
			}
			while(resource.size() < index) {
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is empty");
				readyRemove.await();
				//Caso em que o remove quer remover um index que não existe e não há mais threads de inserção
				if(insertersRequest == 0){
					System.out.println(" index is not exist and no had more inserters");
					break;
				}
			}
			blockSearchers = true;
			blockInserters = true;
			removerActive++;
			int value = resource.remove(index);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" removed " + value + " by index " + index);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}catch(IndexOutOfBoundsException e) {
			System.out.println("Thread " + Thread.currentThread().getName() + 
					" trying to remove in non-existent index");
		}finally {
			lock.unlock();
		}
	}

	public void finishRemove(){
		lock.lock();
		removerActive--;
		if(insertersRequest > 0){
			blockInserters = false;
			blockSearchers = false;
			readyInsert.signal();
			readySearch.signalAll();
		}else if(removersRequest > 0){
			readyRemove.signal();
		}

		lock.unlock();
	}
	
	public int getSize() {
		return resource.size();
	}
}
