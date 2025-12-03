package com.example.a3_teste_paineldevotao;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3_teste_paineldevotao.data.EnqueteRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfigurarEnqueteActivity extends AppCompatActivity {

    private static final String TAG = "ConfigEnquete";

    // Campos originais
    private EditText edtTituloEnquete;
    private EditText edtOpcaoA;
    private EditText edtOpcaoB;
    private EditText edtOpcaoC;
    private Button btnSalvarConfig;

    // ================================================================
    // ATIVIDADE 5 — NOVOS CAMPOS ADICIONADOS
    // ================================================================
    private EditText edtMensagemRodape;
    private EditText edtDataEncerramento;

    // Repositório
    private EnqueteRepository enqueteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configurar_enquete);

        configurarToolbar();
        aplicarInsets();
        inicializarRepository();
        inicializarViews();          // Atualizado para incluir campos novos
        carregarConfiguracoesAtuais();
        configurarBotaoSalvar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // =====================================================================
    // Toolbar
    // =====================================================================

    private void configurarToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarConfig);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Configurar enquete");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try { toolbar.setTitleCentered(false); } catch (Exception ignored) {}
    }

    // =====================================================================
    // Layout e Repository
    // =====================================================================

    private void aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutConfigRoot),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }

    private void inicializarRepository() {
        enqueteRepository = new EnqueteRepository(this);
    }

    // =====================================================================
    // Inicialização visual — AGORA COM OS CAMPOS NOVOS
    // =====================================================================

    private void inicializarViews() {
        edtTituloEnquete = findViewById(R.id.edtTituloEnquete);
        edtOpcaoA = findViewById(R.id.edtOpcaoA);
        edtOpcaoB = findViewById(R.id.edtOpcaoB);
        edtOpcaoC = findViewById(R.id.edtOpcaoC);
        btnSalvarConfig = findViewById(R.id.btnSalvarConfig);

        // ================================================================
        // ATIVIDADE 5 — CAMPOS NOVOS DE RODAPÉ E DATA LIMITE
        // ================================================================
        edtMensagemRodape = findViewById(R.id.edtMensagemRodape);
        edtDataEncerramento = findViewById(R.id.edtDataEncerramento);
    }

    // =====================================================================
    // Carregar configuração COMPLETA (texto + rodapé + horário)
    // =====================================================================

    private void carregarConfiguracoesAtuais() {

        // AGORA CHAMAMOS O NOVO MÉTODO
        enqueteRepository.carregarConfiguracoesCompleta(new EnqueteRepository.ConfiguracaoCompletaCallback() {
            @Override
            public void onConfig(String titulo,
                                 String opcA,
                                 String opcB,
                                 String opcC,
                                 String rodape,
                                 String fim) {

                if (titulo != null) edtTituloEnquete.setText(titulo);
                if (opcA != null) edtOpcaoA.setText(opcA);
                if (opcB != null) edtOpcaoB.setText(opcB);
                if (opcC != null) edtOpcaoC.setText(opcC);

                // ============================================================
                // ATIVIDADE 5 — Preencher campos novos
                // ============================================================
                if (rodape != null) edtMensagemRodape.setText(rodape);
                if (fim != null) edtDataEncerramento.setText(fim);
            }

            @Override
            public void onErro(Exception e) {
                Log.e(TAG, "Erro ao carregar configurações: ", e);
                Toast.makeText(ConfigurarEnqueteActivity.this,
                        "Erro ao carregar configurações.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =====================================================================
    // Botão para salvar — AGORA COM VALIDAÇÃO COMPLETA
    // =====================================================================

    private void configurarBotaoSalvar() {
        btnSalvarConfig.setOnClickListener(v -> {

            // ------------------------
            // Leitura dos campos antigos
            // ------------------------
            String titulo = edtTituloEnquete.getText().toString().trim();
            String opcaoA = edtOpcaoA.getText().toString().trim();
            String opcaoB = edtOpcaoB.getText().toString().trim();
            String opcaoC = edtOpcaoC.getText().toString().trim();

            // ------------------------
            // ATIVIDADE 5 — Novos campos
            // ------------------------
            String rodape = edtMensagemRodape.getText().toString().trim();
            String dataFim = edtDataEncerramento.getText().toString().trim();

            // ------------------------
            // Validações
            // ------------------------
            if (titulo.isEmpty()) {
                Toast.makeText(this, "Informe a pergunta da enquete.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (opcaoA.isEmpty() || opcaoB.isEmpty() || opcaoC.isEmpty()) {
                Toast.makeText(this, "Preencha as três opções.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ============================================================
            // ATIVIDADE 5 — Validar data limite da enquete
            // ============================================================
            if (!dataFim.isEmpty()) {
                SimpleDateFormat sdf =
                        new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                sdf.setLenient(false);

                try {
                    Date data = sdf.parse(dataFim);
                    if (data.before(new Date())) {
                        Toast.makeText(this,
                                "A data de encerramento não pode ser no passado.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Formato inválido. Use: dd/MM/yyyy HH:mm",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // ============================================================
            // SALVANDO TUDO — NOVO MÉTODO COM 6 CAMPOS
            // ============================================================
            enqueteRepository.salvarConfiguracoes(
                    titulo,
                    opcaoA,
                    opcaoB,
                    opcaoC,
                    rodape,        // NOVO
                    dataFim,       // NOVO
                    new EnqueteRepository.OperacaoCallback() {
                        @Override
                        public void onSucesso() {
                            Toast.makeText(ConfigurarEnqueteActivity.this,
                                    "Configurações salvas.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onErro(Exception e) {
                            Toast.makeText(ConfigurarEnqueteActivity.this,
                                    "Erro ao salvar.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }
}
