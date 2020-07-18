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
	ReentrantReadWriteLock rwLock;

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
		rwLock  = new ReentrantReadWriteLock(true);

	}

	//Retorna a própria lista para auxiliar em possiveis consultas fora da classe
	public LinkedList<Integer> getResource(){
		return this.resource;
	}

	//Função  da operação de busca que tem o mesmo papel de leitura e pode ser executado com outra de inserção
	public void Search(int value){
		rwLock.readLock().lock();
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
			//Liberação de recurso para outra operação de escrita
			rwLock.readLock().unlock();

		}
	}


	//Inicializador da operação de inserção
	public void Insert(int value) {
		//Lock de leitura para poder executar junto com outras operações de busca
		rwLock.readLock().lock();

		//Lock de exclusividade em relação a outras operações de inserção
		lockUniqueInsert.lock();
		try{
			System.out.println("Thread " + Thread.currentThread().getName() + " started");

			//Operação de inserção
			resource.add(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" inserted " + value);
		}finally{
			System.out.println("Thread " + Thread.currentThread().getName() + " finished");
			//Liberação de acesso para outras inserções
			lockUniqueInsert.unlock();
			//Liberação geral para outras operações
			rwLock.readLock().unlock();
		}

	}

	//Função remove que utiliza o lock de escrita do ReadLock para ter exclusividade do acesso
	public void Remove(int index){
		rwLock.writeLock().lock();
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
			rwLock.writeLock().unlock();
		}
	}


	
	public int getSize() {
		return resource.size();
	}
}
