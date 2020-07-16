package linked_list_synchronization;

import java.util.Random;

import static java.lang.Math.random;
//Classe da thread insert
public class Inserter extends Thread{
    private Linked_list list;

    public Inserter(String name, Linked_list list ){
        super(name);
        this.list = list;
    }

    //Funcionamento da thread que pega algum valor aleatório para inserir na lista
    public void run() {
        try{
            Random r = new Random();
            int valor = r.nextInt(100) ;
            list.startInsert(valor);
        }catch (IllegalArgumentException e){
            System.out.println(Thread.currentThread().getName() + " inserting in a empty list");
        }finally {
            //Quando terminar de fazer a operação, então chama a função abaixo
            list.finishInsert();
        }
    }
}
