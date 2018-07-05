package apprestaurant.com.br.restaurant.model;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import apprestaurant.com.br.restaurant.R;
import apprestaurant.com.br.restaurant.fragment.Fragment_Map;

public class EmpresaActivity extends AppCompatActivity {

    Button btnmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        btnmap = (Button) findViewById(R.id.btnmap);

        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment_Map fragment_map = new Fragment_Map();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_Layout, fragment_map).commit();
            }
        });
    }


}
