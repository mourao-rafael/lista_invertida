import java.io.*;

/**
 * Classe para realizar registros de entidades Pessoa.
 * Criada para testar a estrutura da Lista Invertida
 */
public class Pessoa implements Registro{
    // Atributos:
    private int id;
    private String nome;
    private int idade;
    // Atributos usado apenas para a formatacao da tabela de registros:
    //(olhar metodo "visualizarRegistros" na classe "TesteListaInvertida.java")
    protected static CRUD<Pessoa> crud;
    protected static final ListaInvertida listaInv = new ListaInvertida("listaInv", "dicionario");
    static{ // inicializador estatico (executa assim que a classe eh carregada)
        try{
            crud = new CRUD<>(Pessoa.class.getDeclaredConstructor(byte[].class), "pessoas", listaInv);
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        }
    }
    public static final String[] cabecalhoTabela ={"ID", "NOME", "IDADE"};
    public static int tamColId = cabecalhoTabela[0].length(); // armazena o tamanho da coluna dos ids
    public static int tamColNome = cabecalhoTabela[1].length(); // armazena o tamanho da coluna dos nomes
    public static final int tamColIdade = cabecalhoTabela[2].length(); // este valor nao sera alterado, ja que nao existe ninguem com idade de mais de 5 ("IDADE".length()) algarismos

    // Construtores:
    Pessoa(String nome, int idade){
        this.id = -1;
        this.nome = nome;
        this.idade = idade;
        if(this.nome.length() > tamColNome) tamColNome = this.nome.length(); // definir tamanho da coluna dos nomes
    }
    Pessoa(byte[] dados){
        try{
            this.fromByteArray(dados);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Metodos:
    public int getId(){ return this.id; }
    public void setId(int id){ this.id = id; }
    public String chaveSecundaria(){ return this.nome; }

    public byte[] toByteArray() throws Exception{
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream printter = new DataOutputStream(b);

        printter.writeInt(this.id);
        printter.writeUTF(this.nome);
        printter.writeInt(this.idade);

        return b.toByteArray();
    }

    public void fromByteArray(byte[] dados) throws Exception{
        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(dados));

        this.id = reader.readInt();
        this.nome = reader.readUTF();
        this.idade = reader.readInt();

        if(this.nome.length() > tamColNome) tamColNome = this.nome.length(); // definir tamanho da coluna dos nomes
    }

    private static final String div = "#";
    public String toString(){
        String s = null;
        if(this.id != -1){
            s = Integer.toString( this.id ) +div+ this.nome +div+ Integer.toString( this.idade );
        }
        return s;
    }

    /**
     * Metodo ESTATICO - Constroi a tabela dos registros de Pessoas
     * @return String com a tabela
     */
    public static String construirTabela(String[] registros){
        tamColId = Math.max(cabecalhoTabela[0].length(), Integer.toString(crud.getMaxId()).length()); // definir tamanho da coluna dos ids

        // Construir cabecalho da tabela:
        String tabela = String.format("| %s | %s | %s |", Registro.centralizar(cabecalhoTabela[0], tamColId), Registro.centralizar(cabecalhoTabela[1], tamColNome), Registro.centralizar(cabecalhoTabela[2], tamColIdade));
        int larguraTabela = tabela.length();
        tabela = " " + "_".repeat(larguraTabela-2)  // adicionar limite superior da tabela
               + "\n" + tabela
               + "\n " + "=".repeat(larguraTabela-2); // adiciona limite inferior do cabecalho

        // Inserir os registros na tabela:
        for(String reg : registros){ // para cada registro:
            String[] dados = reg.split(div);

            // Inserir os dados do registro atual:
            tabela += "\n| "+Registro.centralizar(dados[0], tamColId)+" | "+Registro.preencher(dados[1], tamColNome)+" | "+Registro.centralizar(dados[2], tamColIdade)+" |";
            // Adicionar limite inferior do registro:
            tabela += "\n " + "-".repeat(larguraTabela-2);
        }

        return tabela;
    }
    public static String construirTabela(){
        return construirTabela(crud.registrosToString()); // recuperar todos os registros
    }
}
