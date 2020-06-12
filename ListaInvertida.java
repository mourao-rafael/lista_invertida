import java.io.*;
import java.util.ArrayList;

/**
 * Classe referente ao dicionario da lista invertida.
 * @author Rafael Mourao Cerqueira Figueiredo
 * @version 1.0 -> 03/06/2020
 */
class Dicionario{
    private class Termo{
        // Atributos:
        private String termo;
        private long endereco;

        // Construtores:
        Termo(String termo, long endereco){
            this.termo = termo;
            this.endereco = endereco;
        }
        Termo(byte[] dados) throws Exception{
            this.fromByteArray(dados);
        }

        // Metodos:
        public String getTermo(){ return this.termo; }
        public long getEndereco(){ return this.endereco; }

        public byte[] toByteArray() throws Exception{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream printter = new DataOutputStream(b);
    
            printter.writeUTF(this.termo);
            printter.writeLong(this.endereco);
    
            return b.toByteArray();
        }

        public void fromByteArray(byte[] dados) throws Exception{
            DataInputStream reader = new DataInputStream(new ByteArrayInputStream(dados));
            this.termo = reader.readUTF();
            this.endereco = reader.readLong();
        }
    }



    // Atributos de controle para a tabela dos registros:
    public static final String[] cabecalhoTabela ={"ID", "TERMO", "ENDERECO (HEX)"};
    public static final int tamColEndereco = Long.toHexString(Long.MAX_VALUE).length(); // tamanho do maior long possivel
    public int tamColId = cabecalhoTabela[0].length(); // armazena o tamanho da coluna dos ids
    public int tamColTermo = cabecalhoTabela[1].length(); // armazena o tamanho da coluna dos termos

    // Atributos:
    private RandomAccessFile arq; // arquivo do dicionario
    private int tamanho;

    // Construtores:
    Dicionario(String nomeArq){
        try{
            this.arq = new RandomAccessFile(nomeArq+".db", "rw");
            if(this.arq.length() == 0) arq.writeInt(0); // inicializa o arquivo, caso nao exista

            this.tamanho = 0; // quantidade de termos registrados
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Metodos:
    /**
     * Registra um novo par no dicionario
     * @param termo termo do par a ser inserido
     * @param endereco endereco do par a ser inserido
     */
    public void create(String termo, long endereco){
        try{
            // Atualizar tamanho do dicionario:
            arq.seek(0);
            this.tamanho = arq.readInt() + 1;
            arq.seek(0);
            arq.writeInt(this.tamanho);

            // Inserir par [termo, endereco]:
            byte[] dados = new Termo(termo.toLowerCase(), endereco).toByteArray();
            arq.seek(arq.length()); // move para o final do arquivo
            arq.writeInt(dados.length); // escrever o tamanho do novo registro
            arq.write(dados); // escrever o novo registro
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Usando o termo como chave de pesquisa, retorna o endereco do respectivo par
     * @param termo String com o termo chave
     * @return long com o endereco apontado pelo termo em questao
     */
    public long read(String termo){
        long end = -1;

        try{
            arq.seek(0);
            this.tamanho = arq.readInt(); // le o campo tamanho
            
            for(int i=0; i<this.tamanho && end==-1; i++){
                // Ler o proximo registro do dicionario:
                byte[] proxRegistro = new byte[arq.readInt()];
                arq.read(proxRegistro);
                Termo t = new Termo(proxRegistro);

                // Verificar se proximo registro corresponde ao termo desejado:
                if(t.getTermo().equals(termo)) end = t.getEndereco(); // se encontrou o termo, armazenar o endereco
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return end;
    }

    /**
     * Constroi a tabela dos registros do Dicionario
     * @return String com a tabela
     */
    public String construirTabela(){
        // Construir cabecalho da tabela:
        String tabela = String.format("| %s | %s | %s |", Registro.centralizar(cabecalhoTabela[0], tamColId), Registro.centralizar(cabecalhoTabela[1], tamColTermo), Registro.centralizar(cabecalhoTabela[2], tamColEndereco));
        int larguraTabela = tabela.length();
        tabela = " " + "_".repeat(larguraTabela-2)  // adicionar limite superior da tabela
               + "\n" + tabela
               + "\n " + "=".repeat(larguraTabela-2); // adiciona limite inferior do cabecalho
        
        // Construir o restante da tabela:
        try{
            // Definir o tamanho da coluna de IDs:
            arq.seek(0);
            this.tamanho = arq.readInt();
            tamColId = Math.max(cabecalhoTabela[0].length(), Integer.toString(this.tamanho).length());

            // Definir o tamanho da coluna dos termos:
            Termo termos[] = new Termo[this.tamanho];
            for(int i=0; i<this.tamanho; i++){
                byte[] dados = new byte[ arq.readInt() ];
                arq.read(dados);
                termos[i] = new Termo(dados);
                if(termos[i].getTermo().length() > tamColTermo) tamColTermo = termos[i].getTermo().length(); // atualizar tamanho da coluna dos termos
            }

            // Inserir os registros na tabela:
            int i = 0;
            for(Termo t : termos){
                // Inserir os dados lidos na tabela:
                tabela += "\n| "+ Registro.centralizar(Integer.toString(++i), tamColId);
                tabela += " | " + Registro.preencher(t.getTermo(), tamColTermo);
                tabela += " | " + Registro.centralizar( Long.toHexString(t.getEndereco()), tamColEndereco) + " |";
                
                // Adicionar limite inferior do registro:
                tabela += "\n+"+"─".repeat(tamColId+2) + "+"+"─".repeat(tamColTermo+2) + "+"+"─".repeat(tamColEndereco+2);
            }
        } catch(Exception e){
            e.printStackTrace();
            tabela = null;
        }

        return tabela;
    }
}


/**
 * Esta classe eh referente a atividade "Projeto Individual com Revisao por Pares - Lista Invertida", AEDs-III
 * @author Rafael Mourao Cerqueira Figueiredo
 * @version 1.0 -> 03/06/2020
 */
public class ListaInvertida {
    private static final String[] stopwords = {" e ", " de ", " da ", " dos "}; // palavras irrelevantes para as pesquisas
    // Atributos:
    private RandomAccessFile arq;
    private Dicionario dicionario;
    private int ordemBloco; // define quantos registros serao feitos por bloco

    // Construtores:
    ListaInvertida(String nomeArq, String nomeArqDicionario){
        this(nomeArq, nomeArqDicionario, 3);
    }
    ListaInvertida(String nomeArq, String nomeArqDicionario, int ordemBloco){
        try{
            this.arq = new RandomAccessFile(nomeArq+".db", "rw");
            this.dicionario = new Dicionario(nomeArqDicionario);
            this.ordemBloco = ordemBloco;
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Metodos:
    /**
     * Cria um novo bloco no arquivo, sendo que os blocos seguem a seguinte estrutura:
     * @param id id a ser inserido no novo bloco
     * @return endereco do novo bloco
     */
    private long novoBloco(int id){
        /**
         * ESTRUTURA DO BLOCO -> [ n ][ k1 | k2 |...| kO ][ prox ]
         *      LEGENDA:
         *      n -> (int) Numero de registros feitos no bloco atual
         *      kO -> (int[]) Onde 'O' refere-se a ordem do bloco
         *      prox -> (long) ponteiro para a continuacao do bloco
         */
        try{
            long endereco = arq.length();
            arq.seek(endereco);

            // Escrever o campo "n":
            arq.writeInt(1);
            // Escrever o campo das chaves:
            arq.writeInt(id);
            for(int i=1; i<this.ordemBloco; i++) arq.writeInt(-1);
            // Escrever o campo "proximo":
            arq.writeLong(-1);

            return endereco;
        } catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Insere um novo nome na lista (e insere as palavras do nome separadamente, porem todas vinculadas ao mesmo id)
     * @param id id do registro a ser realizado
     * @param nome nome a ser registrado
     * @throws Exception caso nao seja possivel realizar a insercao do novo registro
     */
    public void create(int id, String nome) throws Exception{
        // Remover stopwords:
        for(String stopword : stopwords){
            nome = nome.replace(stopword, " ");
        }

        // Atualizar/criar os registros dos termos:
        for(String termo : nome.split(" ")){
            // Remover os acentos do termo:
            termo = termo.toLowerCase();
            termo.replaceAll("[áàâã]","a").replaceAll("[éèêẽ]","e").replaceAll("[éèêẽ]","e").replaceAll("[íìîĩ]","i").replaceAll("[óòôõ]","o").replaceAll("[úùûũ]","u").replace("ç","c");

            long endereco = dicionario.read(termo);

            if(endereco != -1){ // Se o termo ja existe:
                arq.seek(endereco);
                int n;
                boolean inserido = false;
                while((n = arq.readInt())==this.ordemBloco && !inserido){ // Ir para o bloco disponivel:
                    arq.skipBytes(Integer.BYTES * this.ordemBloco); // pula o campo das chaves
                    endereco = arq.readLong(); // ler o endereco (campo prox)

                    // Se o proximo bloco ja existe:
                    if(endereco != -1) arq.seek(endereco); // move para o endereco lido
                    else{ // Se o proximo bloco ainda nao existe:
                        // Criar novo bloco:
                        long campoProx = arq.getFilePointer() - Long.BYTES; // salvar endereco do campo "prox"
                        endereco = novoBloco(id); // criar novo bloco
                        // Registrar novo bloco no bloco antecessor:
                        arq.seek(campoProx); // move para o campo "prox" do bloco anterior
                        arq.writeLong( endereco ); // registrar endereco do novo bloco

                        inserido = true;
                    }
                }

                if(!inserido){
                    long campoN = arq.getFilePointer() - Integer.BYTES; // armazenar endereco do campo N
                    arq.skipBytes(Integer.BYTES * n); // pular os registros ja existentes
                    arq.writeInt(id); // registrar novo id
                    // Atualizar campo n:
                    arq.seek(campoN);
                    arq.writeInt(n+1);
                }
            }

            else{ // Se o termo nao existe:
                endereco = novoBloco(id); // Cria novo bloco
                dicionario.create(termo, endereco); // Cria novo par no dicionario
            }
        }
    }

    /**
     * Faz a intersecao entre listas de ids
     * @param listas ArrayList<int[]> com as listas de ids
     * @return intersecao entre as listas recebias
     * @throws Exception caso nao exista uma intersecao entre as listas recebidas
     */
    private int[] intersecao(ArrayList<int[]> listas) throws Exception{
        ArrayList<Integer> intersecao = new ArrayList<>();
        int[] ids = listas.get(0); // usar a primeira lista de ids como base
        
        for(int id : ids){
            boolean found = true;
            
            // Para cada id na primeira lista, verificar se o mesmo faz intersecao com todas as outras:
            for(int i=1; i<listas.size() && found; i++){
                int[] aux = listas.get(i);
                
                found = false;
                for(int j=0; j<aux.length && !found; j++){
                    if(aux[j] == id) found = true;
                }
            }
            
            if(found) intersecao.add(id);
        }
        
        int[] resp = null;
        if(intersecao.size() == 0){
            System.out.println("Nenhum registro foi encontrado com os termos inseridos.");
        }
        else{
            resp = new int[ intersecao.size() ];
            for(int i=0; i<resp.length; i++) resp[i] = intersecao.get(i).intValue();
        }
        
        return resp;
    }

    /**
     * Recupera a lista de ids vinculadas a determinada palavra.
     * @param termo ao qual a lista de ids esta vinculada
     * @return int[] lista de ids vinculados ao termo recebido
     * @throws Exception caso nao exista nenhum registro com o termo recebido
     */
    private int[] read(String termo) throws Exception{
        ArrayList<Integer> lista = new ArrayList<>();
        int[] ids = null;
        long endereco = dicionario.read(termo);
        
        if(endereco != -1){
            int n;
            do{
                arq.seek(endereco);
                n = arq.readInt();
                for(int i=0; i<n; i++){
                    lista.add( arq.readInt() );
                }

            }while(n==this.ordemBloco && (endereco = arq.readLong())!=-1);

            // Converter o ArrayList para int[]:
            ids = new int[lista.size()];
            for(int i=0; i<ids.length; i++){
                ids[i] = lista.get(i).intValue();
            }
        }
        else System.out.println("Não há nenhum registro com a palavra \""+ termo +"\"!");
        
        return ids;
    }

    /**
     * Procura registros dos nomes que possuem determinada(s) palavra(s)
     * @param palavras lista de palavras que os nomes devem conter
     * @return lista de ids cujos nomes atendem ao criterio selecionado
     */
    public int[] read(String[] palavras){
        try{
            ArrayList<int[]> listasIds = new ArrayList<>(); // array list que armazena as listas de ids de cada termo
            int[] aux = null;

            // Recuperar as listas de ids de cada termo:
            for(int i=0; i<palavras.length  &&  (aux=read(palavras[i]))!=null; i++){
                listasIds.add( aux );
            }
            
            // Calcular a intersecao entre as listas dos termos:
            if(aux != null) aux = intersecao(listasIds);
            return aux;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Constroi as tabelas do dicionario e da lista invertida propriamente dita
     * @return String com a tabela
     */
    public String construirTabela(){
        // Recuperar tabela de registros do dicionario:
        String tabela = "DICIONARIO:\n" + this.dicionario.construirTabela() + "\n\n\n";
        
        // Adicionar os registros dos blocos da lista invertida:
        try{
            // Definir tamanhos:
            int maxTamEndereco = Long.toHexString(Long.MAX_VALUE).length(); // tamanho do campo com o endereco
            int maxTamN = Integer.toString(this.ordemBloco).length(); // maior tamanho do campo N
            int maxTamChaves = Math.max(2, Integer.toString( Pessoa.crud.getMaxId() ).length()); // maior tamanho das chaves (IDs)

            // Construir o cabecalho:
            tabela += "BLOCOS (LISTA INVERTIDA):\n";
            String cabecalho = "| " + Registro.preencherEsq("ENDEREÇO (HEX)", maxTamEndereco) + " -> ["+Registro.centralizar("N", maxTamN)+"]";
            cabecalho += " [" + Registro.centralizar("k0", maxTamChaves);
            for(int i=1; i<this.ordemBloco; i++) cabecalho += " | k" + (i+1);
            cabecalho += "] [" + Registro.centralizar("PRÓXIMO", maxTamEndereco) + "] |";
            
            // Construir limites superiores e inferiores da tabela:
            int largura = cabecalho.length();
            tabela += " " + "_".repeat(largura-2) + "\n" + cabecalho + " => ESTRUTURA DO BLOCO";
            tabela += "\n " + "=".repeat(largura-2);

            // Construir os registros dos blocos:
            arq.seek(0);
            int count = 0;
            while(arq.getFilePointer() != arq.length()){
                tabela += "\n| " + Registro.preencherEsq(Long.toHexString(arq.getFilePointer()), maxTamEndereco); // endereco
                tabela += " -> [" + Registro.centralizar(Integer.toString(arq.readInt()), maxTamN) + "]"; // campo N
                
                tabela += " [" + Registro.preencherEsq(Integer.toString( arq.readInt() ), maxTamChaves); // a primeira chave sempre existe
                for(int i=1; i<this.ordemBloco; i++){ // preencher as demais chaves
                    int k = arq.readInt();
                    tabela += " | " + Registro.preencherEsq((k==-1 ? "" : Integer.toString(k)), maxTamChaves);
                }

                long prox = arq.readLong();
                tabela += "] [" + Registro.centralizar((prox==-1 ? "" : Long.toHexString(prox)), maxTamEndereco); // campo proximo
                tabela += "] | => BLOCO " + (++count);
            }
        } catch(Exception e){
            e.printStackTrace();
            tabela = null;
        }

        return tabela;
    }
}