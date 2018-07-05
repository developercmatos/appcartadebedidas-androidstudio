package apprestaurant.com.br.restaurant.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProdutoBD extends SQLiteOpenHelper {

    private static String TAG = "produtos_bd";
    private static final String NOME_BD = "produtos.sqlite";
    private static final int VERSAO = 1;
    private static ProdutoBD produtoBD = null; //Singleton


    public ProdutoBD(Context context) {
        //context, nome do banco, factory, versão
        super(context, NOME_BD, null, VERSAO);
    }

    public static ProdutoBD getInstance(Context context){
       if(produtoBD == null){
           produtoBD = new ProdutoBD(context);
           return produtoBD;
       } else{
           return produtoBD;
       }
    }

    /*
   Métodos do ciclo de vida do BD
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table if not exists produto" +
                "( _id integer primary key autoincrement, " +
                " nome text, " +
                " descricao text, " +
                " imagem blob);";
        Log.d(TAG, "Criando a tabela produto. Aguarde ...");
        db.execSQL(sql);
        Log.d(TAG, "Tabela produtos criada");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // COMANDO QUE RODA EM CASO DE MUDANÇA DE VERSÃO DO BANCO DE DADOS
        Log.d("testes", "Upgrade da versão " + oldVersion + " para "
                + newVersion + ", destruindo tudo.");
        db.execSQL("DROP TABLE IF EXISTS produto");
        onCreate(db); // chama onCreate e recria o banco de dados
        Log.i("testes", "Executou o script de upgrade da tabela produto.");
    }
    /*
    Métodos para o CRUD
     */

    //Salva um produto
    public long save(Produto produto){
        SQLiteDatabase db = getWritableDatabase(); //Abrindo a conexão com o BD

        try{

            ContentValues values = new ContentValues();
            values.put("nome", produto.nome);
            values.put("descricao", produto.descricao);
            values.put("imagem", produto.imagem); //insere o valor (a imagem) na tupla)

            //realiza a operação
            if(produto._id == null){
                //insere no banco de dados
                return db.insert("produto", null, values);
            }else{
                //altera no banco de dados
                values.put("_id", produto._id);
                return db.update("produto", values, "_id=" + produto._id, null);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return 0;
    }

    //Deleta um produto
    public long delete(Produto produto){
        SQLiteDatabase db = getWritableDatabase(); //abrindo a conexão com o BD
        try{
            return db.delete("produto", "_id=?", new String[]{String.valueOf(produto._id)});
        }
        finally {
            db.close();
        }
    }

    //retorna a lista de produtos
    public List<Produto> getAll(){
        SQLiteDatabase db = getReadableDatabase();
        try {
            //retorna uma List para os registros contidos no banco de dados
            // select * from produto
            return toList(db.rawQuery("SELECT  * FROM produto", null));
        } finally {
            db.close();
        }
    }

    public List<Produto> getByname(String nome){
        SQLiteDatabase db = getReadableDatabase();
        try {
            //retorna uma List para os registros contidos no banco de dados
            // select * from produto
            return toList(db.rawQuery("SELECT  * FROM produto where nome LIKE'" + nome + "%'", null));
        } finally {
            db.close();
        }
    }
    //converte de Cursor para List
    private List<Produto> toList(Cursor c) {
        List<Produto> produtos = new ArrayList<Produto>();

        if (c.moveToFirst()) {
            do {
                Produto produto = new Produto();

                // recupera os atributos do cursor para o produto
                produto._id = c.getLong(c.getColumnIndex("_id"));
                produto.nome = c.getString(c.getColumnIndex("nome"));
                produto.descricao = c.getString(c.getColumnIndex("descricao"));
                produto.imagem = c.getBlob(c.getColumnIndex("imagem"));

                produtos.add(produto);

            } while (c.moveToNext());
        }

        return produtos;
    }



}


