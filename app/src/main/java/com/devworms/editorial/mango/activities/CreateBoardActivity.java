package com.devworms.editorial.mango.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devworms.editorial.mango.R;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.Utils;


public class CreateBoardActivity extends AppCompatActivity implements View.OnClickListener{

    EditText boardName, boardDesc;
    Button saveButton;
    TextView responseView;

    private Button botonIzq, botonDer;
    private TextView txtMensajes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        setTitle("New Board");
        boardName = (EditText) findViewById(R.id.board_create_name);
        boardDesc = (EditText) findViewById(R.id.board_create_desc);

        saveButton = (Button) findViewById(R.id.save_button);

        botonIzq = (Button) findViewById(R.id.botonIzq);
        botonDer = (Button) findViewById(R.id.botonDer);
        txtMensajes = (TextView) findViewById(R.id.txtMensajes);


        botonIzq.setText("Cancelar");
        botonDer.setText("Aceptar");
        txtMensajes.setText("Ingrese la información");

        botonDer.setOnClickListener(this);
        botonIzq.setOnClickListener(this);

        botonDer.setVisibility(View.GONE);
        botonIzq.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveBoard();
            }
        });

    }

    private void onSaveBoard() {
        String bName = boardName.getText().toString();
        if (!Utils.isEmpty(bName)) {
            PDKClient.getInstance().createBoard(bName, boardDesc.getText().toString(), new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {
                    Log.d(getClass().getName(), response.getData().toString());

                    txtMensajes.setText("Tablero creado");
                    botonDer.setVisibility(View.VISIBLE);
                    botonIzq.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.e(getClass().getName(), exception.getDetailMessage());

                    txtMensajes.setText("Ocurrió un error");
                    botonDer.setVisibility(View.GONE);
                    botonIzq.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "Board name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
}
