package linked_list_synchronization;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Linked_list {
	LinkedList<Integer> resource;

	//Variáveis de lock de leitura e escrita
	ReentrantReadWriteLock lockSearch;
	ReentrantReadWriteLock lockWriteRemove;
	ReentrantReadWriteLock lockInsert;

	//Variavel auxiliar para pode bloquear os insert simultaneos
	Lock lockUniqueInsert;

	//Construtor da lista simplesmente encadeada que recebe como parâmetro o número de elementos na inicialização
	public Linked_list(int size){
		resource = new LinkedList<Integer>();
		Random r = new Random();

		//Preenche ela com valores aleatórios
		for(int i = 0; i < size; i++){
			resource.add(r.nextInt(100));
		}

		lockUniqueInsert = new ReentrantLock(true);
		lockSearch  = new ReentrantReadWriteLock(true);
		lockInsert  = new ReentrantReadWriteLock(true);
		lockWriteRemove  = new ReentrantReadWriteLock(true);

	}

	//Retorna a própria lista para auxiliar em possiveis consultas fora da classe
	public LinkedList<Integer> getResource(){
		return this.resource;
	}

	//Função que inicializa a operação de busca
	public void Search(int value){
		lockSearch.readLock().lock();
		try {
			System.out.println("Thread " + Thread.currentThread().getName() + " started");
			int index = resource.indexOf(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" got " + resource.get(index) + " by index " + index);

		}catch(IndexOutOfBoundsException e) {
			//Exceção para caso não tenha o índice buscado
			System.out.println("Thread " + Thread.currentThread().getName() + 
					" trying to access non-existent index");
		}finally {
			System.out.println("Thread " + Thread.currentThread().getName() + " finished");
			lockSearch.readLock().unlock();

		}
	}


	//Inicializador da operação de inserção
	public void Insert(int value) {
		lockInsert.readLock().lock();
		lockUniqueInsert.lock();
		try{
			System.out.println("Thread " + Thread.currentThread().getName() + " started");

			//Operação de inserção
			resource.add(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" inserted " + value);
		}finally{
			System.out.println("Thread " + Thread.currentThread().getName() + " finished");

			lockUniqueInsert.unlock();
			lockInsert.readLock().unlock();
		}

	}


	public void Remove(int index){
		lockWriteRemove.writeLock().lock();
		try {
			System.out.println("Thread " + Thread.currentThread().getName() + " started");
			int value = resource.remove(index);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" removed " + value + " by index " + index);

		}catch(IndexOutOfBoundsException e) {
			System.out.println("Thread " + Thread.currentThread().getName() + 
					" trying to remove in non-existent index");
		}finally {
			System.out.println("Thread " + Thread.currentThread().getName() + " finished");
			lockWriteRemove.writeLock().unlock();
		}
	}


	
	public int getSize() {
		return resource.size();
	}
}
