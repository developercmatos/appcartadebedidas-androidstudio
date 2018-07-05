package apprestaurant.com.br.restaurant.model;

import java.io.Serializable;
import java.util.Arrays;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long _id;
    public String nome;
    public String descricao;
    public byte[] imagem;

    @Override
    public String toString() {
        return "Produto{" +
                "_id=" + _id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", imagem=" + Arrays.toString(imagem) +
                '}';
    }
}
