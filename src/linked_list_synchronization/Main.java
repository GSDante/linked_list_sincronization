package linked_list_synchronization;

public class Main {
	// Criação do número de threads de cada tipo de operação
	private static final int NUM_THREADS_INSERT = 10;
	private static final int NUM_THREADS_REMOVER = 5;
	private static final int NUM_THREADS_SEARCHER = 10;

	public static void main(String[] args) {
		//Lista simplesmente encadeada com o número de elementos passados com parâmetro
		Linked_list list = new Linked_list(10);

		//Criando a lista de threads para cada tipo
		Inserter inserters[] = new Inserter[NUM_THREADS_INSERT];
		Remover removers[] = new Remover[NUM_THREADS_REMOVER];
		Searcher searchers[] = new Searcher[NUM_THREADS_SEARCHER];

		//Criando as threads do tipo insert
		for (int i = 0; i < NUM_THREADS_INSERT; i++) {
			inserters[i] = new Inserter("Operação I" + (i+1), list);
		}

		//Criando as threads do tipo remove
		for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
			removers[i] = new Remover("Operação R" + (i+1), list);
		}

		//Criando as threads do tipo search
		for (int i = 0; i < NUM_THREADS_SEARCHER; i++) {
			searchers[i] = new Searcher("Operação S" + (i+1), list);
		}

		//Inicializando as threads do tipo insert
		for (int i = 0; i < NUM_THREADS_INSERT; i++) {
			inserters[i].start();
		}

		//Inicializando as threads do tipo remove
		for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
			removers[i].start();
		}
		//Inicializando as threads do tipo remove
		for (int i = 0; i < NUM_THREADS_SEARCHER; i++) {
			searchers[i].start();
		}

		//Sincronizando todas as threads e lançando exceção caso ocorra
		try {
			for (int i = 0; i < NUM_THREADS_SEARCHER; i++) {
				searchers[i].join();
			}

			for (int i = 0; i < NUM_THREADS_INSERT; i++) {
				inserters[i].join();
			}

			for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
				removers[i].join();
			}


		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
