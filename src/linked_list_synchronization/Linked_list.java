package linked_list_synchronization;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Linked_list {
	LinkedList<Integer> resource;
	Lock lock;

	//Variáveis de condição que utilizam wait e signal em cada tipo de thread
	Condition readySearch;
	Condition readyInsert;
	Condition readyRemove;

	//Variáveis contadoras que contam o número de inserções ativas na zona crítica
	private int inserterActive = 0;
	private int insertersRequest = 0;

	//Variáveis contadoras que contam o número de remoções ativas na zona crítica
	private int removerActive = 0;
	private int removersRequest = 0;

	//Variável que auxiliar em saber o número de buscadores ativos na zona crítica
	private int searchers = 0;
	private int searchersRequest = 0;

	//Construtor da lista simplesmente encadeada que recebe como parâmetro o número de elementos na inicialização
	public Linked_list(int size){
		resource = new LinkedList<Integer>();
		Random r = new Random();

		//Preenche ela com valores aleatórios
		for(int i = 0; i < size; i++){
			resource.add(r.nextInt(100));
		}

		lock = new ReentrantLock(true);
		readySearch = lock.newCondition();
		readyInsert = lock.newCondition();
		readyRemove = lock.newCondition();
	}

	//Retorna a própria lista para auxiliar em possiveis consultas fora da classe
	public LinkedList<Integer> getResource(){
		return this.resource;
	}

	//Função que inicializa a operação de busca
	public void startSearch(int value){
		lock.lock();
		try {
			searchersRequest++;
			//Caso a zona crítica esteja sendo ocupada por uma operação de remoção
			//então a operação de busca atual terá que esperar até ser liberado
			while(removerActive > 0) {
				//Se o número de buscadores for menor do que 0,então há buscadores
				//em espera
				System.out.print(Thread.currentThread().getName()+" suspended\n");
				readySearch.await();
			}
			System.out.println("Thread " + Thread.currentThread().getName() + " started");

			searchersRequest--;
			// Incrementa o número de buscadores ativos
			searchers++;
			//Função que busca pelo indice da lista
			int index = resource.indexOf(value);
			System.out.println("Thread " + Thread.currentThread().getName() +
					" got " + resource.get(index) + " by index " + index);
		}catch(InterruptedException e){
			e.printStackTrace();
		}catch(IndexOutOfBoundsException e) {
			//Exceção para caso não tenha o índice buscado
			System.out.println("Thread " + Thread.currentThread().getName() + 
					" trying to access non-existent index");
		}finally {
			lock.unlock();
		}
	}

	//Após terminar a busca, é verificado se tem alguma solicitação de operação de remoção
	public void finishSearch(){
		lock.lock();
		//Decrementa o número de buscadores ativos
		searchers--;
		System.out.println("Thread " + Thread.currentThread().getName() + " finished");

		//Desperta algum removedor dormindo
		if(removersRequest > 0){
			readyRemove.signal();
		}
		lock.unlock();
	}


	//Inicializador da operação de inserção
	public void startInsert(int value) {
		lock.lock();
		try{
			//Incremento do número de requerimento de inserção
			insertersRequest++;
			//Caso a região crítica esteja sendo ocupada por outra operação de inserção ou remoção
			//Esta operaçõ será suspensa
			while(inserterActive > 0 || removerActive > 0){
				readyInsert.await();
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is busy");
			}
			System.out.println("Thread " + Thread.currentThread().getName() + " started");
			insertersRequest--;
			// Incrementa o número de operação de inserção ativa
			inserterActive++;
			//Bloqueia o acesso a outras operações de inserção
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
		//Diminue o número de operação de inserção ativo pra 0
		inserterActive--;
		System.out.println("Thread " + Thread.currentThread().getName() + " finished");

		//Verifica se há removedores requisitando para acorda algum deles, caso nao, verifica os de inserção
		if(removersRequest > 0) {
			readyRemove.signal();
		}else if(insertersRequest > 0 ) {
			readyInsert.signal();
		}
		lock.unlock();
	}

	public void startRemove(int index){
		lock.lock();
		try {
			//Aumenta o número de requerimento de remoção
			removersRequest++;
			//Caso o a região crítica esteja sendo ocupada por inserção,remoção ou buscadores
			//A operação espera
			while(inserterActive > 0 || removerActive > 0 || searchers > 0){
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is busy");
				readyRemove.await();
			}
			removersRequest--;
			//Bloqueia os acesso
			removerActive++;
			System.out.println("Thread " + Thread.currentThread().getName() + " started");

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
		System.out.println("Thread " + Thread.currentThread().getName() + " finished");
		removerActive--;
		//Desperta as operações que foram paradas
		if(insertersRequest > 0 || searchersRequest > 0){
			readyInsert.signal();
			readySearch.signal();
		}else if(removersRequest > 0){
			readyRemove.signal();
		}

		lock.unlock();
	}
	
	public int getSize() {
		return resource.size();
	}
}
