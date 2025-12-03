package com.example.a3_teste_paineldevotao;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3_teste_paineldevotao.data.EnqueteRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ATIVIDADE 3: Tela restrita para listar quem votou.
 * Explicação para o professor:
 * Esta tela acessa a subcoleção 'votos' e itera sobre os documentos.
 * Em um cenário real, isso permite auditoria. Usamos ListView por simplicidade.
 */
public class ListaVotantesActivity extends AppCompatActivity {

    private ListView listViewVotantes;
    private EnqueteRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_votantes); // CORRIGIDO

        listViewVotantes = findViewById(R.id.listViewVotantes);
        repository = new EnqueteRepository(this);

        carregarLista();
    }

    private void carregarLista() {
        repository.listarVotos(new EnqueteRepository.ListaVotantesCallback() {
            @Override
            public void onListaCarregada(List<DocumentSnapshot> documentos) {

                List<String> dadosFormatados = new ArrayList<>();
                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                int i = 1;

                for (DocumentSnapshot doc : documentos) {

                    String opcao = doc.getString("opcaoEscolhida");
                    Timestamp ts = doc.getTimestamp("timestamp");

                    String dataStr = "—";
                    if (ts != null) {
                        dataStr = sdf.format(ts.toDate());
                    }

                    String item =
                            "Votante " + i +
                                    "\nOpção: " + opcao +
                                    "\nData: " + dataStr;

                    dadosFormatados.add(item);
                    i++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ListaVotantesActivity.this,
                        android.R.layout.simple_list_item_1,
                        dadosFormatados
                );

                listViewVotantes.setAdapter(adapter);
            }

            @Override
            public void onErro(Exception e) {
                Toast.makeText(
                        ListaVotantesActivity.this,
                        "Erro ao carregar lista.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
