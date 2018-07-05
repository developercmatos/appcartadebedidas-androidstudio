package apprestaurant.com.br.restaurant.model;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import apprestaurant.com.br.restaurant.R;
import apprestaurant.com.br.restaurant.control.MainActivity;
import apprestaurant.com.br.restaurant.fragment.Fragment_Map;


public class PrincipalActivity extends AppCompatActivity {

    Button btnPrincipal, btnMaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnPrincipal = (Button) findViewById(R.id.btnIdPrincipal);
        btnMaps = (Button) findViewById(R.id.btnMaps);

        btnPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this,EmpresaActivity.class);
                startActivity(intent);

            }
        });
    }
}
