package com.example.thepiggame;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.thepiggame.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
    }

    ActivityMainBinding binding;

    int jugadorVictoria = 0; //cuando adquiere el valor 1, significa victoria del jugador 1. Con valor 2, victoria de jugador 2.
    int tiradaActual;   //resultado de la última tirada de dado
    int tiradasTurno = 0;   //número de tiradas acumuladas sin pasar turno o sacar 1
    int acumuladoTurno = 0; //resultado acumulado en el turno actual
    int puntuacionJugador1; //puntos totales del jugador1
    int puntuacionJugador2; //puntos totales del jugador2
    boolean turnoJugadorUno = true; //true indica que es turno del jugador 1. False significa turno del jugador 2
    ArrayList<Integer> historicoPuntos = new ArrayList<>(); //Array para guardar los puntos de cada ronda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"¿Serás el primero en llegar a 100 puntos?", Toast.LENGTH_SHORT).show();   //plantea el objetivo del juego

        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());

        binding.botonNuevaPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Pulsa LANZAR para empezar.", Toast.LENGTH_SHORT).show();   //mensaje con la instrucción para empezar la tirada
                reiniciarPartida();
            }
        });

        binding.botonLanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiradaActual = (int) (Math.random() * 6) + 1;   //obtiene un número aleatorio entre 1 y 6

                obtenerTiradaDado(tiradaActual);

                if(tiradaActual==1) {   //en caso de sacar un 1 en el dado, se desencadenan las instrucciones del if
                    turnoJugadorUno =! turnoJugadorUno;
                    tiradasTurno = 0;
                    acumuladoTurno = 0;
                    binding.textoTiradasAcumuladas.setText("" + tiradasTurno + "");
                    binding.textoTirada.setText("" + acumuladoTurno + "");
                    Toast.makeText(MainActivity.this,"¡Qué pifia! Salió el 1", Toast.LENGTH_SHORT).show();
                    historicoPuntos.add(acumuladoTurno);

                    actualizarTurnoUI(turnoJugadorUno);

                } else {    //si la tirada no es un 1, suma los puntos al acumulado
                    acumuladoTurno += tiradaActual;
                    tiradasTurno++;
                    binding.textoTiradasAcumuladas.setText("" + tiradasTurno + "");
                    binding.textoTirada.setText("" + acumuladoTurno + "");
                }
            }
        });

        binding.botonPasarTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(turnoJugadorUno){    //acumula los puntos obtenidos en el casillero del jugador 1
                    puntuacionJugador1 += acumuladoTurno;
                    binding.textResultadoJugador1.setText("" + puntuacionJugador1 + "");
                    historicoPuntos.add(acumuladoTurno);
                    acumuladoTurno = 0;
                    tiradasTurno = 0;
                    binding.textoTiradasAcumuladas.setText("" + tiradasTurno + "");
                    binding.textoTirada.setText("" + acumuladoTurno + "");
                    turnoJugadorUno = !turnoJugadorUno;
                    actualizarTurnoUI(turnoJugadorUno);

                    if(esVictoria(puntuacionJugador1)) {    //comprueba si tras la suma de puntos, el jugador 1 llega al objetivo de 100 puntos.
                        jugadorVictoria = 1;
                        Toast.makeText(MainActivity.this, "VICTORIA DEL JUGADOR 1", Toast.LENGTH_LONG).show();
                        finalizarPartida(jugadorVictoria);
                    }
                } else {    //acumula los puntos obtenidos en el casillero del jugador 2
                    puntuacionJugador2 += acumuladoTurno;
                    binding.textResultadoJugador2.setText("" + puntuacionJugador2 + "");
                    historicoPuntos.add(acumuladoTurno);
                    acumuladoTurno = 0;
                    tiradasTurno = 0;
                    binding.textoTiradasAcumuladas.setText("" + tiradasTurno + "");
                    binding.textoTirada.setText("" + acumuladoTurno + "");
                    turnoJugadorUno = !turnoJugadorUno;
                    actualizarTurnoUI(turnoJugadorUno);

                    if(esVictoria(puntuacionJugador2)){ //comprueba si tras la suma de puntos, el jugador 1 llega al objetivo de 100 puntos.
                        jugadorVictoria = 2;
                        Toast.makeText(MainActivity.this,"VICTORIA DEL JUGADOR 2", Toast.LENGTH_LONG).show();
                        finalizarPartida(jugadorVictoria);
                    }
                }
            }
        });

        //Al pulsar muestra el historial de puntos almacenado en el ArrayList historicoPuntos
        binding.botonHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historicoPuntos != null && !historicoPuntos.isEmpty()) {
                    StringBuilder puntosJugador1 = new StringBuilder();
                    StringBuilder puntosJugador2 = new StringBuilder();

                    binding.botonVolver.setVisibility(View.VISIBLE);
                    binding.botonHistorial.setVisibility(View.VISIBLE);
                    ocultarGanador();
                    binding.viewDerecha.setAlpha(0f);
                    binding.viewIzquierda.setAlpha(0f);
                    binding.textNombreJugador1.setVisibility(View.VISIBLE);
                    binding.textNombreJugador2.setVisibility(View.VISIBLE);


                    // Recorre la lista y separa los puntos
                    for (int i = 0; i < historicoPuntos.size(); i++) {
                        if (i % 2 == 0) { // Indices pares para jugador 2
                            puntosJugador1.append(historicoPuntos.get(i)).append("\n");
                        } else { // Indices impares para jugador 1
                            puntosJugador2.append(historicoPuntos.get(i)).append("\n");
                        }
                    }

                    // Muestra los resultados en los TextViews
                    binding.historicoPuntos1.setText(puntosJugador1.toString().trim());
                    binding.historicoPuntos2.setText(puntosJugador2.toString().trim());

                    binding.historicoPuntos1.setVisibility(View.VISIBLE);
                    binding.historicoPuntos2.setVisibility(View.VISIBLE);
                }
            }
        });

        //Sale de la visualización de puntos históricos
        binding.botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.botonVolver.setVisibility(View.INVISIBLE);
                binding.botonHistorial.setVisibility(View.VISIBLE);
                binding.historicoPuntos1.setVisibility(View.INVISIBLE);
                binding.historicoPuntos2.setVisibility(View.INVISIBLE);
                binding.textNombreJugador1.setVisibility(View.INVISIBLE);
                binding.textNombreJugador2.setVisibility(View.INVISIBLE);
                mostrarGanador(jugadorVictoria);
            }
        });
    }

    //Comprueba si la puntuación del parámetro significa la victoria del jugador
    public boolean esVictoria(int puntos){
        return puntos >= 100;
    }

    //Muestra cartel con el jugador ganador
    public void mostrarGanador(int jugadorVictoria){
        binding.textNombreJugador1.setVisibility(View.INVISIBLE);
        binding.textNombreJugador2.setVisibility(View.INVISIBLE);
        if (jugadorVictoria==1)
            binding.textPlayer1Wins.setVisibility(View.VISIBLE);
        else
            binding.textPlayer2Wins.setVisibility(View.VISIBLE);
    }

    //Oculta cartel con el jugador ganador
    public void ocultarGanador(){
        binding.textPlayer1Wins.setVisibility(View.INVISIBLE);
        binding.textPlayer2Wins.setVisibility(View.INVISIBLE);
    }

    //Actualiza la UI en función del jugador que tenga el turno
    private void actualizarTurnoUI(boolean turnoJugadorUno) {
        if (turnoJugadorUno) {
            binding.viewDerecha.setAlpha(0.8f);
            binding.viewIzquierda.setAlpha(0f);
            binding.textNombreJugador1.setVisibility(View.VISIBLE);
            binding.textNombreJugador2.setVisibility(View.INVISIBLE);
        } else {
            binding.viewIzquierda.setAlpha(0.8f);
            binding.viewDerecha.setAlpha(0f);
            binding.textNombreJugador1.setVisibility(View.INVISIBLE);
            binding.textNombreJugador2.setVisibility(View.VISIBLE);
        }
    }

    //Reinicia las variables, listas y UI para preparar el inicio de una partida
    public void reiniciarPartida() {
        Toast.makeText(MainActivity.this,"Empieza el jugador 1", Toast.LENGTH_SHORT).show();
        historicoPuntos.clear();
        turnoJugadorUno = true;
        jugadorVictoria = 0;
        actualizarTurnoUI(turnoJugadorUno);
        tiradasTurno = 0;
        acumuladoTurno = 0;
        binding.textoTiradasAcumuladas.setText("" + tiradasTurno + "");
        binding.textoTirada.setText("" + acumuladoTurno + "");
        puntuacionJugador1 = 0;
        puntuacionJugador2 = 0;
        binding.textResultadoJugador1.setText("" + puntuacionJugador1 + "");
        binding.textResultadoJugador2.setText("" + puntuacionJugador2 + "");

        binding.botonNuevaPartida.setText("REINICIAR");
        binding.botonPasarTurno.setVisibility(View.VISIBLE);
        binding.botonLanzar.setVisibility(View.VISIBLE);
        binding.imagePig.setVisibility(View.INVISIBLE);
        binding.textTitulo.setVisibility(View.INVISIBLE);
        binding.viewDerecha.setVisibility(View.VISIBLE);
        binding.textoTiradasAcumuladas.setVisibility(View.VISIBLE);
        binding.textoTirada.setVisibility(View.VISIBLE);
        binding.textResultadoJugador1.setVisibility(View.VISIBLE);
        binding.textResultadoJugador2.setVisibility(View.VISIBLE);
        binding.botonPasarTurno.setVisibility(View.VISIBLE);
        binding.imagenDado.setVisibility(View.VISIBLE);
        binding.textObjetivo1.setVisibility(View.VISIBLE);
        binding.textObjetivo2.setVisibility(View.VISIBLE);
        binding.botonVolver.setVisibility(View.INVISIBLE);
        binding.botonHistorial.setVisibility(View.INVISIBLE);
        binding.historicoPuntos1.setVisibility(View.INVISIBLE);
        binding.historicoPuntos2.setVisibility(View.INVISIBLE);
        ocultarGanador();
    }

    //Oculta los elementos innecesarios para mostrar al jugador campeón
    private void finalizarPartida(int jugadorVictoria) {
        binding.botonPasarTurno.setVisibility(View.INVISIBLE);
        binding.botonLanzar.setVisibility(View.INVISIBLE);
        binding.botonNuevaPartida.setText("NUEVA PARTIDA");
        binding.imagenDado.setVisibility(View.INVISIBLE);
        binding.textoTiradasAcumuladas.setVisibility(View.INVISIBLE);
        binding.textoTirada.setVisibility(View.INVISIBLE);
        binding.botonHistorial.setVisibility(View.VISIBLE);
        binding.textNombreJugador1.setVisibility(View.INVISIBLE);
        binding.textNombreJugador2.setVisibility(View.INVISIBLE);
        binding.textObjetivo1.setVisibility(View.INVISIBLE);
        binding.textObjetivo2.setVisibility(View.INVISIBLE);

        binding.textObjetivo1.setVisibility(View.INVISIBLE);
        binding.textObjetivo2.setVisibility(View.INVISIBLE);
        
        mostrarGanador(jugadorVictoria);

        if (jugadorVictoria == 1) {
            binding.viewDerecha.setAlpha(0.8f);
            binding.viewIzquierda.setAlpha(0f);
        } else {
            binding.viewIzquierda.setAlpha(0.8f);
            binding.viewDerecha.setAlpha(0f);
        }
    }

    //En función del resultado aleatorio dado, muestra el correspondiente dado en imagen
    private void obtenerTiradaDado(int tiradaActual) {
        switch (tiradaActual) {
            case 1:
                binding.imagenDado.setImageResource(R.drawable.evil_pig);
                break;
            case 2:
                binding.imagenDado.setImageResource(R.drawable.dice_two);
                break;
            case 3:
                binding.imagenDado.setImageResource(R.drawable.dice_three);
                break;
            case 4:
                binding.imagenDado.setImageResource(R.drawable.dice_four);
                break;
            case 5:
                binding.imagenDado.setImageResource(R.drawable.dice_five);
                break;
            case 6:
                binding.imagenDado.setImageResource(R.drawable.dice_six);
                break;
            default:
                binding.imagenDado.setImageResource(R.drawable.dice_random);
                break;
        }
    }
}