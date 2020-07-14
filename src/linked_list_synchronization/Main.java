package linked_list_synchronization;

public class Main {

	private static final int CAPACITY = 100;
	private static final int NUM_THREADS_INSERT = 3;
	private static final int NUM_THREADS_REMOVER = 5;
	private static final int NUM_THREADS_SEARCHER = 7;

	public static void main(String[] args) {
		Linked_list list = new Linked_list(CAPACITY);

		Inserter inserters[] = new Inserter[NUM_THREADS_INSERT];
		Remover removers[] = new Remover[NUM_THREADS_REMOVER];
		Searcher searchers[] = new Searcher[NUM_THREADS_SEARCHER]

		for (int i = 0; i < NUM_THREADS_INSERT; i++) {
			inserters[i] = new Inserter("Operação I " + (i+1), list);
		}

		for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
			removers[i] = new Remover("Operação R " + (i+1), list);
		}

		for (int i = 0; i < NUM_THREADS_INSERT; i++) {
			searchers[i] = new Searcher("Operação S " + (i+1), list);
		}

		for (int i = 0; i < NUM_THREADS_INSERT; i++) {
			inserters[i].start();
		}

		for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
			removers[i].start();
		}

		for (int i = 0; i < NUM_THREADS_SEARCHER; i++) {
			searchers[i].start();
		}

		try {
			for (int i = 0; i < NUM_THREADS_INSERT; i++) {
				inserters[i].join();
			}

			for (int i = 0; i < NUM_THREADS_REMOVER; i++) {
				removers[i].join();
			}

			for (int i = 0; i < NUM_THREADS_SEARCHER; i++) {
				searchers[i].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
