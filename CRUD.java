import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

/**
 * "Mini-CRUD", criado com finalidade UNICA de testar o funcionamento da Lista Invertida criada no projeto.
 * @author Rafael Mourao Cerqueira Figueiredo
 * @version 1.0 - 02/06/2020
 */
public class CRUD <T extends Registro>{
    // Atributos:
    private RandomAccessFile arq;
    private Constructor<T> construtor;
    private ListaInvertida listaInv;

    // Construtor:
    CRUD(Constructor<T> construtor, String nomeArq, ListaInvertida listaInv){
        try{
            this.construtor = construtor;
            this.arq = new RandomAccessFile(nomeArq+".db", "rw");
            this.listaInv = listaInv;

            if(this.arq.length() == 0) arq.writeInt(0); // inicializa cabecalho do arquivo
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Metodos:
    /**
     * Retorna o maior id do arquivo de registros.
     */
    public int getMaxId(){
        try{
            arq.seek(0);
            return arq.readInt();
        } catch(java.io.IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    public int create(T objeto){ // retorna o id do registro realizado
        int id = -1;

        try{
            // Definir id do novo registro:
            arq.seek(0);
            objeto.setId( (id = arq.readInt()+1) );
            // Atualizar ultimo id usado:
            arq.seek(0);
            arq.writeInt(id);

            // Realizar novo registro:
            byte[] dados = objeto.toByteArray();
            arq.seek(arq.length());
            arq.writeInt(dados.length); // Escreve o tamanho do novo registro
            arq.write(dados);

            // Inserir chave secundaria na lista invertida:
            this.listaInv.create(objeto.getId(), objeto.chaveSecundaria());
        } catch(Exception e){
            e.printStackTrace();
        }

        return id;
    }

    public T read(int id){
        T objeto = null;

        try{
            arq.seek(0);

            if(id <= arq.readInt()){
                for(int i=1; i<id; i++){ // caminhar ate o inicio do registro em questao
                    int tam = arq.readInt(); // le o tamanho do proximo registro
                    arq.skipBytes( tam ); // pular o registro atual
                }

                // Ler o registro em qeustao:
                byte[] dados = new byte[ arq.readInt() ];
                arq.read(dados);
                objeto = this.construtor.newInstance(dados);
            }
            
        } catch(Exception e){
            e.printStackTrace();
            objeto = null;
        }

        return objeto;
    }

    /**
     * Imprime TODOS os registros do respectivo arquivo do CRUD em um String[].
     * @return String[] com todos os registros do CRUD em questao.
     */
    public String[] registrosToString(){
        String[] registros = null;
        
        try{
            arq.seek(0);
            registros = new String[arq.readInt()]; // como a remocao nao foi implementada, o ultimo registro usado correponde ao numero de registros criados
            
            for(int i=0; i<registros.length; i++){
                byte[] dados = new byte[arq.readInt()];
                arq.read(dados);
                registros[i] = construtor.newInstance(dados).toString();
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }

        return registros;
    }

    /**
     * Converte ids de registros para String com os dados do registro em questão.
     * @param ids int[] lista de ids dos registro a serem convertidos
     * @return String[] com todos os dados dos registros impressos.
     */
    public String[] registrosToString(int[] ids){
        String[] registros = new String[ids.length];

        try{
            arq.seek(0); // vai para o inicio do arquivo de registros
            arq.skipBytes(Integer.BYTES); // pular o ultimo id usado

            for(int i=0; i<ids.length; i++){
                T objeto;
                byte[] dados;

                do{
                    dados = new byte[arq.readInt()]; // le o tamanho do proximo registro
                    arq.read(dados); // le os dados
                }while((objeto = this.construtor.newInstance(dados)).getId() < ids[i]); // como o arquivo eh sequencial, procurar o registro de forma sequencial

                registros[i] = objeto.toString(); // encontrado o registro, converter seus dados para String
            }
        }
        catch(Exception e){
            e.printStackTrace();
            registros = new String[]{""};
        }

        return registros;
    }

    /**
     * Verifica se o arquivo de registros está vazio.
     */
    public boolean semRegistros(){
        try{
            return this.arq.length() == Integer.BYTES;
        } catch(java.io.IOException e){
            e.printStackTrace();
            return true;
        }
    }
}