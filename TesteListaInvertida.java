import java.util.*;

/**
 * Esta classe tem finalidade UNICA de testar o funcionamento da Lista Invertida criada no projeto.
 * @author Rafael Mourao Cerqueira Figueiredo
 * @version 1.0 - 02/06/2020
 */
public class TesteListaInvertida {
    private static final Scanner leitor = new Scanner(System.in);

    public static void main(String args[]){
        if( Pessoa.crud.semRegistros() ) criarRegistros();
        visualizarRegistros();
        
        
        System.out.print("Digite os termos que você deseja pesquisar separados por espaço (ou aperte [enter] para abortar): ");
        String entrada = leitor.nextLine();
        while((entrada).length() != 0){
            // Recuperar lista de ids que atendem aos criterios desejados:
            int[] ids = Pessoa.listaInv.read( entrada.split(" ") );
            if(ids != null){
                System.out.println("\n\n\nREGISTROS QUE ATENDEM AOS REQUISITOS ENVIADOS:");
                System.out.println(Pessoa.construirTabela( Pessoa.crud.registrosToString(ids) ));
            }
            
            // Repetir leitura:
            System.out.print("\n\n\nDigite os termos que você deseja pesquisar (separados por espaço): ");
            entrada = leitor.nextLine();
        }
        System.out.println("\n\n\nOperação abortada!");
    }
    
    /**
     * Metodo para criar registros de pessoas:
     */
    static void criarRegistros(){
        Pessoa []pessoas = new Pessoa[]{
            new Pessoa("Pedro Alvares Cabral", 28),
            new Pessoa("Joao Pedro Magalhaes", 20),
            new Pessoa("Alvaro de Souza Rocha", 14),
            new Pessoa("Luiza de Souza Cerqueira", 40),
            new Pessoa("Lucas Rocha Pinho", 57),
            new Pessoa("Clara Magalhaes Pinto", 34),
            new Pessoa("Maria Antonia de Pinho", 46),
            new Pessoa("Henrique Prates Fagundes", 57),
            new Pessoa("Rafael Mourao Cerqueira Figueiredo", 23),
            new Pessoa("Henrique Martins Ferreira Pinto", 68),
            new Pessoa("Carlos Henrique de Paiva", 20),
            new Pessoa("Joao Lucas Fernandes", 34),
            new Pessoa("Pedro Henrique Fagundes", 23),
            new Pessoa("Clara Fernandes e Souza", 18),
            new Pessoa("Bianca Alvares de Melo", 18),
            new Pessoa("Marcos Henrique Ribeiro", 12),
            new Pessoa("Laura Paiva Henriques", 20),
            new Pessoa("Rafael de Melo Pinto", 15),
            new Pessoa("Joao Pedro Henrique Pinto", 12)
        };

        for(Pessoa p : pessoas){
            Pessoa.crud.create(p);
        }
    }

    /**
     * Metodo para imprimir os registros em tabelas, dentro do arquivo "Visualizar_Registros.txt"
     */
    static void visualizarRegistros(){
        try{
            System.out.print("REGISTROS DE PESSOAS:\n" + Pessoa.construirTabela() + "\n\n\n");
            System.out.print(Pessoa.listaInv.construirTabela() + "\n\n\n");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}