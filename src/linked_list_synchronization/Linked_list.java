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

	//Variáveis booleanas que auxiliam no bloqueio de buscas e inserções enquanto há remoção ou, no caso de inserção,
	// enquanto há operação de inserção
	private boolean blockSearchers = false;
	private boolean blockInserters = false;

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
			//Caso a zona crítica esteja sendo ocupada por uma operação de remoção
			//então a operação de busca atual terá que esperar até ser liberado
			if (blockSearchers) {
				//Se o número de buscadores for menor do que 0,então há buscadores
				//em espera
				System.out.print(Thread.currentThread().getName()+" suspended\n");
				readySearch.await();
			}
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
			if(inserterActive > 0 || blockInserters ){
				readyInsert.await();
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is busy");
			}
			// Incrementa o número de operação de inserção ativa
			inserterActive++;
			//Bloqueia o acesso a outras operações de inserção
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
		//Diminue o número de operação de inserção ativo pra 0
		inserterActive--;
		//Desbloqueia o acesso para outros inseridores
		blockInserters = false;
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
			//Caso não tenha indice para remover
			while(resource.size() < index) {
				System.out.println("Thread " + Thread.currentThread().getName() +
						" suspended because resource is empty");
				//Caso em que o remove quer remover um index que não existe e não há mais threads de inserção
				if(insertersRequest == 0){
					System.out.println(" index is not exist and no had more inserters");
					break;
				}
			}
			//Bloqueia os acesso
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
		//Desperta as operações que foram paradas
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
