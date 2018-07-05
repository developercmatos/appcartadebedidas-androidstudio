package apprestaurant.com.br.restaurant.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import apprestaurant.com.br.restaurant.R;
import apprestaurant.com.br.restaurant.model.Produto;

public class ListAdapter extends ArrayAdapter<Produto> {

    public ListAdapter(Context context, List<Produto> produtos) {
        super(context, 0, produtos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Lê os dados da posição que o usuário clicou
        Produto produto = getItem(position);
        // Checa de está utilizando o padrão ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_layout, parent, false);


            // mapeia as views do layout do adaptador
            TextView tvNome = (TextView) convertView.findViewById(R.id.tv_item_nome);
            TextView tvDescricao = (TextView) convertView.findViewById(R.id.tv_item_descricao);

            // popula as views
            tvNome.setText(produto.nome);
            tvDescricao.setText(produto.descricao);

            //mapeia a view da imagem
            ImageView imvImagem = (ImageView) convertView.findViewById(R.id.imv_item);
            if (produto.imagem != null) {
                //converte byte[] para Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(produto.imagem, 0, produto.imagem.length);
                //carrega a imagem na ImageView do item da ListView
                imvImagem.setImageBitmap(bitmap);
            } else {
                //carrega a imagem padrão (se não houver imagem no Cursor)
                imvImagem.setImageResource(R.drawable.logoimage);
            }

        }
        return convertView;
    }
}
