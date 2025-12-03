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

public class ListaVotantesActivity extends AppCompatActivity {

    private ListView listViewVotantes;
    private EnqueteRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_votantes); // Confirme se o nome do layout está correto

        // Inicializa o ListView
        listViewVotantes = findViewById(R.id.listViewVotantes);

        // Inicializa o Repositório
        repository = new EnqueteRepository(this);

        // Se o listViewVotantes for null aqui, o app vai fechar
        if (listViewVotantes == null) {
            Toast.makeText(this, "Erro: ListView não encontrado!", Toast.LENGTH_LONG).show();
            finish(); // Fecha a tela para evitar crash
            return;
        }

        carregarLista();
    }

    private void carregarLista() {
        repository.listarVotos(new EnqueteRepository.ListaVotantesCallback() {
            @Override
            public void onListaCarregada(List<DocumentSnapshot> documentos) {
                // Previne crash se a atividade já tiver sido fechada
                if (isFinishing() || isDestroyed()) return;

                List<String> dadosFormatados = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                int i = 1;
                for (DocumentSnapshot doc : documentos) {
                    String opcao = doc.getString("opcaoEscolhida");
                    Timestamp ts = doc.getTimestamp("timestamp");

                    String dataStr = "—";
                    if (ts != null) {
                        dataStr = sdf.format(ts.toDate());
                    }

                    // Se algum dado for null, usa um valor padrão para não quebrar
                    if (opcao == null) opcao = "?";

                    String item = "Votante " + i + "\nOpção: " + opcao + "\nData: " + dataStr;
                    dadosFormatados.add(item);
                    i++;
                }

                // Verifica se a lista está vazia
                if (dadosFormatados.isEmpty()) {
                    dadosFormatados.add("Nenhum voto registrado ainda.");
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
                if (!isFinishing()) {
                    Toast.makeText(ListaVotantesActivity.this, "Erro ao carregar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}