package edu.umb.cs443.hw2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {
    GridView gridView;
    private static int w = 5, curx, cury;
    private Random r = new Random();
    static String[] tiles = new String[w * w];
    private static int NumX = 0;
    private Thread LocationO;
    private Thread InitializeX;
    private static int treasures = 0;
    private Handler Game = new Handler();
    private static int cells = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, tiles);

        gridView.setAdapter(adapter);

        Game = new Handler() {
            public void handleMessage(Message msg) {
                TextView cellsview =(TextView)findViewById(R.id.textCell);
                cellsview.setText(cells + "Cells");
                TextView treasureview=(TextView)findViewById(R.id.textTreasure);
                treasureview.setText(treasures + "Treasures");

                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        };

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                Toast.makeText(getApplicationContext(), (CharSequence) (new Integer(position).toString()), Toast.LENGTH_SHORT).show();
                                                try {
                                                    PositioningO(position);
                                                }
                                                catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
        );
        init();
        InitializeX();
    }

    public void reset(View view) {
        NumX = 0;
        InitializeX();
        init();
    }

    void init(){
        for (int i = 0; i < tiles.length; i++) tiles[i] = " ";
        curx = r.nextInt(w);
        cury = r.nextInt(w);
        tiles[cury * w + curx] = "O";
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        Game.sendEmptyMessage(0);
        cells = 0;
        treasures = 0;
    }

    public void quit(View view) {
        finish();
        System.exit(0);
    }

    // Creating the treasure 'X' and making sure that the limit is 4 at any given time
    void InitializeX() {
        if (InitializeX == null || NumX == 0 || !InitializeX.isAlive()) {
            final Runnable Begin = new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            while (NumX < 4) {
                                Thread.sleep(850);
                                int xposition = r.nextInt(w);
                                int yposition = r.nextInt(w);
                                if (!(tiles[yposition * w + xposition].equals("O")) && (!(tiles[yposition * w + xposition].equals("X")))) {
                                    tiles[yposition * w + xposition] = "X";
                                    Game.sendEmptyMessage(0);
                                    NumX = NumX + 1;
                                }
                                else {
                                    break;
                                }
                            }
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            InitializeX = new Thread(Begin);
            InitializeX.start();
        }
    }

    // Moving the Player across the tiles
    public void PositioningO(final int position) throws InterruptedException {
        final Runnable Change = new  Runnable() {
            @Override
            public void run() {
                int CoordinateY = position / 5;
                while (cury != CoordinateY) {
                    if (cury < CoordinateY) {
                        tiles[cury * w + curx] = " ";
                        Game.sendEmptyMessage(0);
                        cury++;
                        cells = cells + 1;
                        if (tiles[cury * w + curx].equals("X")) {
                            NumX--;
                            treasures = treasures + 1;
                            InitializeX(); }
                        tiles[cury * w + curx] = "O";
                        Game.sendEmptyMessage(0);
                    }
                    else if (cury > CoordinateY) {
                        tiles[cury * w + curx] = " ";
                        Game.sendEmptyMessage(0);
                        cury--;
                        cells = cells + 1;
                        if (tiles[cury * w + curx].equals("X")) {
                            NumX--;
                            treasures = treasures + 1;
                            InitializeX();
                        }
                        tiles[cury * w + curx] = "O";
                        Game.sendEmptyMessage(0);
                    }
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int CoordinateX = position % 5;
                while (curx != CoordinateX) {
                    if (curx < CoordinateX) {
                        tiles[cury * w + curx] = " ";
                        Game.sendEmptyMessage(0);
                        curx++;
                        cells = cells + 1;
                        if (tiles[cury * w + curx].equals("X")) {
                            NumX--;
                            treasures = treasures + 1;
                            InitializeX();
                        }
                        tiles[cury * w + curx] = "O";
                        Game.sendEmptyMessage(0);
                    }
                    else if (curx > CoordinateX) {
                        tiles[cury * w + curx] = " ";
                        Game.sendEmptyMessage(0);
                        curx--;
                        cells = cells + 1;
                        if(tiles[cury * w + curx].equals("X")) {
                            NumX--;
                            treasures = treasures + 1;
                            InitializeX();
                        }
                        tiles[cury * w + curx] = "O";
                        Game.sendEmptyMessage(0);
                    }
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        LocationO = new Thread(Change);
        LocationO.start();
    }
}