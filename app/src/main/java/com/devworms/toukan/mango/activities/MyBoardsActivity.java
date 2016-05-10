package com.devworms.toukan.mango.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.devworms.toukan.mango.R;
import com.devworms.toukan.mango.main.StarterApplication;
import com.devworms.toukan.mango.util.Specs;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.Utils;
import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.ImageLoadSpec;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyBoardsActivity extends AppCompatActivity {

    private PDKCallback myBoardsCallback;
    private PDKResponse myBoardsResponse;
    private ListView _listView;
    private Button _botonIzq, _botonDer;
    private TextView _txtMensajes;
    private TargetImageView _img_receta;
    private BoardsAdapter _boardsAdapter;

    public String urlImagen;
    private boolean _loading = false;
    private static final String BOARD_FIELDS = "id,name,description,creator,image,counts,created_at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_boards);

        _botonIzq = (Button) findViewById(R.id.botonIzq);
        _botonDer = (Button) findViewById(R.id.botonDer);
        _txtMensajes = (TextView) findViewById(R.id.txtMensajes);
        _img_receta =  (TargetImageView) findViewById(R.id.image_receta);

        this.urlImagen = (String) getIntent().getStringExtra("url_imagen");



        FastImageLoader.prefetchImage(urlImagen, Specs.IMG_IX_IMAGE);
        ImageLoadSpec spec = FastImageLoader.getSpec(Specs.IMG_IX_IMAGE);
        _img_receta.loadImage(urlImagen, spec.getKey());



        _botonIzq.setText("Cancelar");
        _botonDer.setText("Aceptar");
        _txtMensajes.setText("Seleccione uno de sus tableros");

        _botonDer.setVisibility(View.GONE);
        _botonIzq.setVisibility(View.VISIBLE);


        setTitle("My Boards");
        _boardsAdapter = new BoardsAdapter(this, _botonIzq, _botonDer, _txtMensajes);


        _listView = (ListView) findViewById(R.id.listView);

        _listView.setAdapter(_boardsAdapter);
        _listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_boards, menu);
            }
        });

        myBoardsCallback = new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                _loading = false;
                myBoardsResponse = response;
                _boardsAdapter.setBoardList(response.getBoardList());
            }

            @Override
            public void onFailure(PDKException exception) {
                _loading = false;
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        };
        _loading = true;
    }

    private void fetchBoards() {
        _boardsAdapter.setBoardList(null);


        PDKClient.getInstance().getMyBoards(BOARD_FIELDS, myBoardsCallback);

    }

    private void loadNext() {
        if (!_loading && myBoardsResponse.hasNext()) {
            _loading = true;
            myBoardsResponse.loadNext(myBoardsCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchBoards();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_board_delete:
                deleteBoard(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_boards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_board:
                createNewBoard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewBoard() {
        Intent i = new Intent(this, CreateBoardActivity.class);
        startActivity(i);
    }

    private void deleteBoard(int position) {
        PDKClient.getInstance().deleteBoard(_boardsAdapter.getBoardList().get(position).getUid(), new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), "Response: " + response.getStatusCode());
                fetchBoards();
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), "error: " + exception.getDetailMessage());
            }
        });
    }

    private class BoardsAdapter extends BaseAdapter implements  View.OnClickListener {

        private List<PDKBoard> _boardList;
        private Context _context;
        private Button _botonIzq, _botonDer;
        private TextView _txtMensajes;

        public BoardsAdapter(Context c, Button botonIzq, Button botonDer, TextView txtMensajes)  {
            _context = c;
            _botonDer = botonDer;
            _botonIzq = botonIzq;

            _botonIzq.setOnClickListener(this);
            _botonDer.setOnClickListener(this);
            _txtMensajes = txtMensajes;
        }

        public void setBoardList(List<PDKBoard> list) {
            if (_boardList == null) _boardList = new ArrayList<PDKBoard>();
            if (list == null) _boardList.clear();
            else _boardList.addAll(list);
            notifyDataSetChanged();
        }

        public List<PDKBoard> getBoardList() {
            return _boardList;
        }
        @Override
        public int getCount() {
            return _boardList == null ? 0 : _boardList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            String pinImageUrl = "http://pruebas.devworms.com/HOME1.png";

            String board = _boardList.get(position).getUid();
            String noteText = "prueba";

            if (!Utils.isEmpty(noteText) &&!Utils.isEmpty(board) && !Utils.isEmpty(pinImageUrl)) {
                PDKClient
                        .getInstance().createPin(noteText, board, pinImageUrl, "www.devworms.com", new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        _botonIzq.setVisibility(View.GONE);
                        _botonDer.setVisibility(View.VISIBLE);
                        _txtMensajes.setText("Esta receta fue compartida");
                        ((Activity)(_context)).finish();
                        StarterApplication.bCompartido = true;
                        StarterApplication.pdkClient.logout();
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        _botonIzq.setVisibility(View.VISIBLE);
                        _botonDer.setVisibility(View.GONE);
                        _txtMensajes.setText("Ocurrió un error, intente más tarde");
                        StarterApplication.bCompartido = false;

                    }
                });
            } else {
               // Toast.makeText(_context, "Required fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            //load more pins if about to reach end of list
            if (_boardList.size() - position < 5) {
                loadNext();
            }

            if (convertView == null){
                LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.textViewItem = (TextView) convertView.findViewById(android.R.id.text1);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            PDKBoard boardItem = _boardList.get(position);
            if (boardItem != null) {
                viewHolder.textViewItem.setText(boardItem.getName());
            }

            return convertView;
        }

        @Override
        public void onClick(View v) {

            ((Activity)(_context)).finish();
        }


        private class ViewHolderItem {
            TextView textViewItem;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
