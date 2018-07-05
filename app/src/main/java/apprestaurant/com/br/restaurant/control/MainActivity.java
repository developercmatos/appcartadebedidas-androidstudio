package apprestaurant.com.br.restaurant.control;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;

import apprestaurant.com.br.restaurant.R;
import apprestaurant.com.br.restaurant.adapter.ListAdapter;
import apprestaurant.com.br.restaurant.model.Produto;
import apprestaurant.com.br.restaurant.model.ProdutoBD;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, android.support.v7.app.ActionBar.TabListener{

    private static String TAG = "produtos";
    private ProdutoBD produtoBD;
    private EditText etNome;
    private EditText etDescricao;
    private ListView lvProdutos;
    private ImageView imvFoto;
    private static final String GETALL = "getAll";
    private static final String GETBYNAME = "getbyname";
    private static final String SAVE = "save";
    private static final String DELETE = "delete";
    private static Produto produto = null;
    private String nameFind = "";
    ActionBar.Tab tab1, tab2; //uma das abas da activity
    private byte[] imagem = null; //imagem do produto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //obtém a instância do objeto de acesso ao banco de dados
        produtoBD = ProdutoBD.getInstance(this);
        //constrói uma instância da classe de modelo
        produto = new Produto();

        if(isTablet(this)){
            setContentView(R.layout.activity_tab);
            getSupportActionBar().setTitle(R.string.titulo_actionbar_list); //insere um título para a janela

            //mapeia os componentes de UI
            etNome = (EditText) findViewById(R.id.editText_nome);
            etDescricao = (EditText) findViewById(R.id.editText_descricao);
            imvFoto = (ImageView) findViewById(R.id.imageView);
            lvProdutos = (ListView) findViewById(R.id.listView);
            lvProdutos.setOnItemClickListener(this); //adiciona a lista de ouvintes
            new Task().execute(GETALL); //executa a operação GET em segundo plano

        }else{
            //prepara a ActionBar
            getSupportActionBar().setTitle(R.string.titulo_actionbar_list);//insere um título para a janela
            getSupportActionBar().setNavigationMode(getSupportActionBar().NAVIGATION_MODE_TABS);//define o modo de navegação por abas

             /*
                Cria as abas e as adiciona à ActionBar
            */
            //TAB1
            tab1 = getSupportActionBar().newTab().setText("Carta de Drinks");
            tab1.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab1);

            //TAB2
            tab2 = getSupportActionBar().newTab().setText("Add Produto");
            tab2.setTabListener(MainActivity.this);
            getSupportActionBar().addTab(tab2);
        }

    }

    /*
      Trata eventos das Tabs
   */
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        switch (tab.getPosition()){
            case 0:{
                //mapeia os componentes da UI
                setContentView(R.layout.activity_list);

                //insere um título na ActionBar
                getSupportActionBar().setTitle(" Carta de Drinks");

                //mapeia os componentes de activity_list.xml
                lvProdutos = (ListView) findViewById(R.id.listView);
                lvProdutos.setOnItemClickListener(MainActivity.this); //registra o tratador de eventos para cada item da ListView

                new Task().execute(GETALL); //executa a operação GET em segundo plano

                break;
            }
            case 1:{
                //mapeia os componentes da UI
                setContentView(R.layout.activity_cadastro);

                //insere um título na ActionBar
                getSupportActionBar().setTitle("Edit");

                //mapeia os componentes de activity_cadastro.xml
                etNome = (EditText) findViewById(R.id.editText_nome);
                etDescricao = (EditText) findViewById(R.id.editText_descricao);
                imvFoto = (ImageView) findViewById(R.id.imageView);

                break;
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    //infla o menu, mapeia e prepara a SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //infla o menu
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        //mapeia e insere o handler a fila de ouvintes da SearchView
        SearchView mySearchView = (SearchView) menu.findItem(R.id.menuitem_search).getActionView();//obtem a SearchView
        mySearchView.setQueryHint("Digite um nome"); //coloca um hint na SearchView
        mySearchView.setOnQueryTextListener(MainActivity.this); //cadastra o tratador de eventos na lista de tratadores da SearchView


        return true;
    }

    //trata eventos dos itens do menu da ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        tab1.select(); //seleciona a aba 1
        switch (item.getItemId()){
            case R.id.menuitem_salvar:
                if(!etNome.getText().toString().isEmpty() &&
                        !etDescricao.getText().toString().isEmpty()){
                    if(produto._id == null){ //se é uma inclusão
                        produto = new Produto(); //apaga dados antigos
                    }
                    produto.nome = etNome.getText().toString();
                    produto.descricao = etDescricao.getText().toString();
                    produto.imagem = imagem;
                    Log.d(TAG, "Produto que será salvo: " + produto.toString());
                    new Task().execute(SAVE); //executa a operação CREATE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menuitem_cancelar:
                limparFormulario();
                break;
            case R.id.menuitem_excluir:
                if(produto != null && !etNome.getText().toString().isEmpty() &&
                        !etDescricao.getText().toString().isEmpty()){
                    new Task().execute(DELETE); //executa a operação DELETE em segundo plano
                    new Task().execute(GETALL); //executa a operação GET em segundo plano para atualizar a ListView
                }else{
                    Toast.makeText(MainActivity.this, "Selecione um produto na lista.", Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return true;
    }

    //trata eventos da SearchView
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //trata eventos da SearchView
    @Override
    public boolean onQueryTextChange(String newText) {
        if(!isTablet(MainActivity.this)) {
            tab1.select(); //seleciona a aba 1
            //onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
        }
        if(newText.equals("")){
            new Task().execute(GETALL);
        }else{
            new Task().execute(GETBYNAME); //executa a operação GET em segundo plano para atualizar a ListView
            nameFind = newText; //armazena em uma variável global para uso na task
        }

        return true;
    }

    //limpa o formulário
    private void limparFormulario(){
        etNome.setText(null);
        etDescricao.setText(null);
        etNome.requestFocus();
        imvFoto.setImageResource(R.drawable.logoimage);
        imagem = null;
        produto = new Produto(); //apaga dados antigos
    }

    //trata o evento onClick de cada item da lista
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!isTablet(MainActivity.this)) {
            tab2.select(); //seleciona a aba 2
            //onTabSelected(tab2, null); //chama o tratador de eventos para carregar os componentes
        }
        produto = (Produto) adapterView.getAdapter().getItem(i); //obtém o Produto
        Log.d(TAG, produto.toString());
        //carrega nos campos de input
        etNome.setText(produto.nome);
        etDescricao.setText(produto.descricao);
        etNome.requestFocus();
        //carrega a imagem no imageview
        if(produto.imagem != null){
            imvFoto.setImageBitmap(BitmapFactory.decodeByteArray(produto.imagem, 0, produto.imagem.length));
        }

    }


    /**
     * Método para carregar os dados do banco de dados para a ListView.
     */
    public void carregarListView(List<Produto> produtos){

        //cria um objeto da classe ListAdapter, um adaptador List -> ListView
        ListAdapter dadosAdapter = new ListAdapter(this, produtos);
        //associa o adaptador a ListView
        lvProdutos.setAdapter(dadosAdapter);
    }

      /*
        Métodos para carregamento e tratamento da imagem da ImageView.
     */

    /**
     * Método que solicita ao sistema operacional a inicialização de uma Activity que saiba obter e devolver imagens.
     * @param v
     */
    public void carregarImagem(View v){
        //cria uma Intent
        //primeiro argumento: ação ACTION_PICK "escolha um item a partir dos dados e retorne o seu URI"
        //segundo argumento: refina a ação para arquivos de imagem, retornando um URI
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //inicializa uma Activity. Neste caso, uma que forneca acesso a galeria de imagens do dispositivo.
        startActivityForResult(Intent.createChooser(intent,
                "Selecione uma imagem"), 0);
    }


    /**
     * Método que recebe o retorno da Activity de galeria de imagens.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri arquivoUri = data.getData();
            Log.d(TAG, "Uri da imagem: " + arquivoUri);
            imvFoto.setImageURI(arquivoUri);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(arquivoUri));
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true); //reduz e aplica um filtro na imagem
                byte[] img = getBitmapAsByteArray(bitmap); //converte para um fluxo de bytes
                imagem = img; //coloca a imagem no objeto imagem (um array de bytes (byte[]))
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Converte um Bitmap em um array de bytes (bytes[])
     * @param bitmap
     * @return byte[]
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //criam um stream para ByteArray
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream); //comprime a imagem
        return outputStream.toByteArray(); //retorna a imagem como um Array de Bytes (byte[])
    }

    /**
     * Detecção do tipo de screen size.
     * @param context contexto da Activity
     * @return boolean
     */
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*
       Classe interna para realizar as transações no banco de dados
    */
    private class Task extends AsyncTask<String, Void, List<Produto>> {

        long count = 0L; //para armazenar o retorno do salvar e do excluir

        //executa a task em outra Thread
        @Override
        protected List<Produto> doInBackground(String... strings) {
            if(strings[0].equals(GETALL)){
                return produtoBD.getAll(); //get
            }else{
                if(strings[0].equals(GETBYNAME)){
                    return produtoBD.getByname(nameFind); //get
                }else{
                    if(strings[0].equals(SAVE)){
                        count = produtoBD.save(MainActivity.produto); //create e update
                    }else{
                        if(strings[0].equals(DELETE)){
                            count = produtoBD.delete(MainActivity.produto); //delete
                        }
                    }
                }
            }

            return null;
        }

        //ATUALIZA A VIEW
        @Override
        protected void onPostExecute(List<Produto> produtos) {
            if(produtos != null){
                carregarListView(produtos);
            }else if(count > 0){
                Toast.makeText(MainActivity.this, "Operação realizada.", Toast.LENGTH_SHORT).show();
                limparFormulario();
                if(!isTablet(MainActivity.this)) {
                    tab1.select(); //SELECIONA A ABA 1
                    onTabSelected(tab1, null); //chama o tratador de eventos para carregar os componentes
                }
                Log.d(TAG, "Operação realizada.");
            }else{
                Toast.makeText(MainActivity.this, "Erro ao atualizar o produto. Contate o desenvolvedor.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

