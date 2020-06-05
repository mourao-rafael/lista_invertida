public interface Registro{
    /**
     * Metodo para centralizar uma String num determinado espaco.
     * @param s String a ser centalizada
     * @param n espaco no qual a String deve ser centralizada (deve ser >= tamanho da string)
     * @return String com tamanho 'n', com a String 's' centralizada
     */
    public static String centralizar(String s, int n){
        String espacamento = " ".repeat( (n-s.length())/2 );
        return espacamento + s + espacamento + ((n-s.length())%2==1 ? " " : "");
    }

    /**
     * Basicamente, preenche uma string com espacos ate determinado tamanho
     * @param s String a ser preenchida
     * @param n tamanho para o qual a String deve ser preenchida (deve ser >= tamanho da string)
     * @return String com tamanho 'n', com a String 's' alinhada a esquerda e o restante preenchido por espacos
     */
    public static String preencher(String s, int n){
        return s + " ".repeat(n-s.length());
    }

    /**
     * Semelhante ao metodo acima (preencher), porem aqui, alinha a string para a direita
     * @param s String a ser preenchida
     * @param n tamanho para o qual a String deve ser preenchida (deve ser >= tamanho da string)
     * @return String com tamanho 'n', com a String 's' alinhada a DIREITA e o restante preenchido por espacos
     */
    public static String preencherEsq(String s, int n){
        return " ".repeat(n - s.length()) + s;
    }

    // Metodos a serem implementados:
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] dados) throws Exception;

    public int getId();
    public void setId(int id);

    public String chaveSecundaria();
    public String toString();
}
