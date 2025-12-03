package com.example.a3_teste_paineldevotao;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3_teste_paineldevotao.data.EnqueteRepository;
import com.example.a3_teste_paineldevotao.data.FirebaseManager;
import com.example.a3_teste_paineldevotao.model.Enquete;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PainelVotacao";

    // ==========================================================
    // UI ORIGINAL
    // ==========================================================
    private TextView txtSubtitulo;
    private TextView txtPergunta;

    private TextView txtTituloResultados;
    private TextView txtTotalA;
    private TextView txtTotalB;
    private TextView txtTotalC;
    private TextView txtTotalGeral;
    private TextView txtSeuVoto;

    private Button btnVotarA;
    private Button btnVotarB;
    private Button btnVotarC;
    private Button btnReset;

    // ==========================================================
    // ATIVIDADE 1 (metadados do voto)
    // ==========================================================
    private TextView txtDataVoto;
    private TextView txtUidUsuario;
    private TextView txtDeviceInfo;

    // ==========================================================
    // ATIVIDADE 5 (rodapé + data limite)
    // ==========================================================
    private TextView txtRodape;
    private String dataEncerramentoString;

    // ==========================================================
    // Firebase / Repositório
    // ==========================================================
    private FirebaseManager firebaseManager;
    private FirebaseAuth auth;
    private EnqueteRepository enqueteRepository;
    private ListenerRegistration listenerResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        configurarToolbar();
        aplicarInsets();

        inicializarFirebase();
        inicializarViews();
        fazerLoginAnonimo();
        configurarBotoes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Listener da enquete (método OFICIAL existente no repositório)
        listenerResultados = enqueteRepository.observarEnquete(new EnqueteRepository.EnqueteListener() {
            @Override
            public void onEnqueteAtualizada(Enquete enquete) {
                atualizarUIComEnquete(enquete);
            }

            @Override
            public void onErro(Exception e) {
                Log.e(TAG, "Erro ao observar enquete", e);
            }
        });

        carregarVotoUsuario();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerResultados != null) {
            listenerResultados.remove();
        }
    }

    // ==========================================================
    // Inicialização
    // ==========================================================
    private void configurarToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Painel de Votação");
        }
    }

    private void aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutMain), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void inicializarFirebase() {
        firebaseManager = FirebaseManager.getInstance(this);
        auth = firebaseManager.getAuth();
        enqueteRepository = new EnqueteRepository(this);
        enqueteRepository.inicializarSeNecessario();
    }

    private void inicializarViews() {
        txtSubtitulo = findViewById(R.id.txtSubtitulo);
        txtPergunta = findViewById(R.id.txtPergunta);

        txtTituloResultados = findViewById(R.id.txtTituloResultados);
        txtTotalA = findViewById(R.id.txtTotalA);
        txtTotalB = findViewById(R.id.txtTotalB);
        txtTotalC = findViewById(R.id.txtTotalC);
        txtTotalGeral = findViewById(R.id.txtTotalGeral);
        txtSeuVoto = findViewById(R.id.txtSeuVoto);

        btnVotarA = findViewById(R.id.btnVotarA);
        btnVotarB = findViewById(R.id.btnVotarB);
        btnVotarC = findViewById(R.id.btnVotarC);
        btnReset = findViewById(R.id.btnReset);

        txtDataVoto = findViewById(R.id.txtDataVoto);
        txtUidUsuario = findViewById(R.id.txtUidUsuario);
        txtDeviceInfo = findViewById(R.id.txtDeviceInfo);
        txtRodape = findViewById(R.id.txtRodape);
    }

    private void fazerLoginAnonimo() {
        if (auth.getCurrentUser() != null) {
            return;
        }

        auth.signInAnonymously().addOnSuccessListener(e -> {
            Toast.makeText(this, "Conectado anonimamente", Toast.LENGTH_SHORT).show();
        });
    }

    // ==========================================================
    // Atualizar tela com dados da enquete
    // ==========================================================
    private void atualizarUIComEnquete(Enquete e) {

        txtPergunta.setText(e.getTituloEnquete());
        btnVotarA.setText(e.getTextoOpcaoA());
        btnVotarB.setText(e.getTextoOpcaoB());
        btnVotarC.setText(e.getTextoOpcaoC());

        long A = e.getOpcaoA();
        long B = e.getOpcaoB();
        long C = e.getOpcaoC();
        long total = A + B + C;

        txtTotalA.setText("Opção A: " + A + " (" + (total > 0 ? A * 100 / total : 0) + "%)");
        txtTotalB.setText("Opção B: " + B + " (" + (total > 0 ? B * 100 / total : 0) + "%)");
        txtTotalC.setText("Opção C: " + C + " (" + (total > 0 ? C * 100 / total : 0) + "%)");
        txtTotalGeral.setText("Total de votos: " + total);

        // Rodapé (Atividade 5)
        txtRodape.setVisibility(
                e.getMensagemRodape() != null ? View.VISIBLE : View.GONE
        );
        txtRodape.setText(e.getMensagemRodape());

        dataEncerramentoString = e.getDataHoraEncerramento();
    }

    // ==========================================================
    // ATIVIDADE 1 — Carregar detalhes do voto
    // ==========================================================
    private void carregarVotoUsuario() {

        enqueteRepository.carregarDetalhesVotoUsuario(dados -> {

            if (dados != null) {

                String opcao = (String) dados.get("opcaoEscolhida");
                txtSeuVoto.setText("Seu voto: opção " + opcao);

                Timestamp ts = (Timestamp) dados.get("timestamp");
                if (ts != null) {
                    String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(ts.toDate());
                    txtDataVoto.setText("Data do voto: " + data);
                }

                txtUidUsuario.setText("UID: " + dados.get("uid"));
                txtDeviceInfo.setText("Dispositivo: " +
                        dados.get("deviceModel") + " (Android " +
                        dados.get("androidVersion") + ")");

            } else {

                txtSeuVoto.setText("Seu voto: ainda não votou");
                txtDataVoto.setText("Data do voto: —");
                txtUidUsuario.setText("UID: " + auth.getUid());
                txtDeviceInfo.setText("Dispositivo: —");
            }
        });
    }

    // ==========================================================
    // Registrar voto (com limite de horário)
    // ==========================================================
    private void registrarVoto(String opcao) {

        if (dataEncerramentoString != null && !dataEncerramentoString.isEmpty()) {
            try {
                Date limite = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .parse(dataEncerramentoString);

                if (new Date().after(limite)) {
                    Toast.makeText(this, "Votação encerrada", Toast.LENGTH_LONG).show();
                    return;
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro na data limite: " + e.getMessage());
            }
        }

        enqueteRepository.registrarVoto(opcao, new EnqueteRepository.RegistrarVotoCallback() {
            @Override
            public void onVotoRegistrado(String opcao) {
                carregarVotoUsuario();
                Toast.makeText(MainActivity.this, "Voto registrado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onJaVotou(String opcaoExistente) {
                txtSeuVoto.setText("Seu voto: opção " + opcaoExistente);
                Toast.makeText(MainActivity.this, "Você já votou", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(Exception e) {
                Toast.makeText(MainActivity.this, "Erro ao votar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================================
    // Botões
    // ==========================================================
    private void configurarBotoes() {
        btnVotarA.setOnClickListener(v -> registrarVoto("A"));
        btnVotarB.setOnClickListener(v -> registrarVoto("B"));
        btnVotarC.setOnClickListener(v -> registrarVoto("C"));
        btnReset.setOnClickListener(v -> mostrarDialogoReset());
    }

    // ==========================================================
    // ATIVIDADE 3 — Diálogo com acesso à lista de votantes
    // ==========================================================
    private void mostrarDialogoReset() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zerar votos");
        builder.setMessage("Digite o código do professor:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Confirmar", (d, w) -> {
            if ("1234".equals(input.getText().toString().trim())) {
                resetarEnquete();
            } else {
                Toast.makeText(this, "Código incorreto", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Ver Lista", (d, w) -> {
            if ("1234".equals(input.getText().toString().trim())) {
                startActivity(new Intent(MainActivity.this, ListaVotantesActivity.class));
            } else {
                Toast.makeText(this, "Código incorreto", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (d, w) -> d.dismiss());
        builder.show();
    }

    private void resetarEnquete() {
        enqueteRepository.resetarEnquete(new EnqueteRepository.OperacaoCallback() {
            @Override
            public void onSucesso() {
                txtSeuVoto.setText("Seu voto: ainda não votou");
                Toast.makeText(MainActivity.this, "Enquete zerada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(Exception e) {
                Toast.makeText(MainActivity.this, "Erro ao zerar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
