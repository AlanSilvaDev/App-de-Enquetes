package com.example.a3_teste_paineldevotao.data;

import android.content.Context;
import android.os.Build; // ATIVIDADE 2: necessário para pegar modelo do dispositivo
import androidx.annotation.Nullable;

import com.example.a3_teste_paineldevotao.model.Enquete;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;   // ATIVIDADE 3: usado para ordenar votos
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório responsável por TODA a comunicação com Firestore.
 * Todas as alterações solicitadas nas Atividades 1–5 foram aplicadas
 * e estão claramente marcadas no código abaixo.
 */
public class EnqueteRepository {

    private final FirebaseManager firebaseManager;
    private final DocumentReference enqueteRef;

    public EnqueteRepository(Context context) {
        this.firebaseManager = FirebaseManager.getInstance(context);
        this.enqueteRef = firebaseManager.getEnqueteRef();
    }

    // =====================================================================
    //  Inicialização da enquete original (sem alterações)
    // =====================================================================
    public void inicializarSeNecessario() {
        enqueteRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot == null || !snapshot.exists()) {
                Enquete enquetePadrao = new Enquete(
                        "Em qual opção você deseja votar?",
                        "Opção A",
                        "Opção B",
                        "Opção C",
                        0, 0, 0
                );
                enqueteRef.set(enquetePadrao.toMap());
            }
        });
    }

    // =====================================================================
    //  ATIVIDADE 5: Adicionar novos campos ao Listener (rodapé + data limite)
    // =====================================================================
    public ListenerRegistration observarEnquete(EnqueteListener listener) {
        return enqueteRef.addSnapshotListener((snapshot, error) -> {

            if (error != null || snapshot == null || !snapshot.exists()) {
                listener.onErro(error);
                return;
            }

            Enquete enquete = new Enquete();

            // Campos já existentes
            enquete.setTituloEnquete(snapshot.getString("tituloEnquete"));
            enquete.setTextoOpcaoA(snapshot.getString("textoOpcaoA"));
            enquete.setTextoOpcaoB(snapshot.getString("textoOpcaoB"));
            enquete.setTextoOpcaoC(snapshot.getString("textoOpcaoC"));

            Long a = snapshot.getLong("opcaoA");
            Long b = snapshot.getLong("opcaoB");
            Long c = snapshot.getLong("opcaoC");

            enquete.setOpcaoA(a != null ? a : 0);
            enquete.setOpcaoB(b != null ? b : 0);
            enquete.setOpcaoC(c != null ? c : 0);

            // ===============================
            // ATIVIDADE 5 – ADIÇÃO AQUI
            // ===============================
            enquete.setMensagemRodape(snapshot.getString("mensagemRodape"));
            enquete.setDataHoraEncerramento(snapshot.getString("dataHoraEncerramento"));

            listener.onEnqueteAtualizada(enquete);
        });
    }

    // =====================================================================
    //  ATIVIDADE 1: Carregar detalhes completos do voto do usuário
    // =====================================================================
    public interface VotoUsuarioDetalhadoCallback {
        void onVotoCarregado(@Nullable Map<String, Object> dadosVoto);
    }

    public void carregarDetalhesVotoUsuario(VotoUsuarioDetalhadoCallback callback) {
        DocumentReference votoRef = firebaseManager.getUserVoteRef();
        if (votoRef == null) {
            callback.onVotoCarregado(null);
            return;
        }

        votoRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot != null && snapshot.exists()) {
                callback.onVotoCarregado(snapshot.getData());
            } else {
                callback.onVotoCarregado(null);
            }
        }).addOnFailureListener(e -> callback.onVotoCarregado(null));
    }

    // =====================================================================
    //  ATIVIDADE 2: Registrar voto com metadados (modelo + Android)
    // =====================================================================
    public void registrarVoto(String opcao, RegistrarVotoCallback callback) {

        DocumentReference votoRef = firebaseManager.getUserVoteRef();

        if (votoRef == null) {
            callback.onErro(new IllegalStateException("Usuário não logado."));
            return;
        }

        votoRef.get().addOnSuccessListener(snapshot -> {

            if (snapshot != null && snapshot.exists()) {
                callback.onJaVotou(snapshot.getString("opcaoEscolhida"));
                return;
            }

            // Determina qual contador atualizar
            String campo =
                    opcao.equals("A") ? "opcaoA" :
                            opcao.equals("B") ? "opcaoB" : "opcaoC";

            enqueteRef.update(campo, FieldValue.increment(1))
                    .addOnSuccessListener(unused -> {

                        Map<String, Object> voto = new HashMap<>();
                        voto.put("opcaoEscolhida", opcao);
                        voto.put("timestamp", FieldValue.serverTimestamp());

                        // ===============================
                        // ATIVIDADE 2 — METADADOS
                        // ===============================
                        voto.put("deviceModel", Build.MODEL);
                        voto.put("androidVersion", Build.VERSION.RELEASE);
                        voto.put("uid", firebaseManager.getAuth().getUid());

                        votoRef.set(voto)
                                .addOnSuccessListener(u -> callback.onVotoRegistrado(opcao))
                                .addOnFailureListener(callback::onErro);

                    })
                    .addOnFailureListener(callback::onErro);

        }).addOnFailureListener(callback::onErro);
    }

    // =====================================================================
    //  ATIVIDADE 3: Listar votantes com ordenação por timestamp
    // =====================================================================
    public interface ListaVotantesCallback {
        void onListaCarregada(List<DocumentSnapshot> docs);
        void onErro(Exception e);
    }

    public void listarVotos(ListaVotantesCallback callback) {

        enqueteRef.collection("votos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot ->
                        callback.onListaCarregada(querySnapshot.getDocuments()))
                .addOnFailureListener(callback::onErro);
    }

    // =====================================================================
    //  ATIVIDADE 4: Reset com LOG de auditoria
    // =====================================================================
    public void resetarEnquete(OperacaoCallback callback) {

        Map<String, Object> dados = new HashMap<>();
        dados.put("opcaoA", 0L);
        dados.put("opcaoB", 0L);
        dados.put("opcaoC", 0L);

        enqueteRef.set(dados, SetOptions.merge())
                .addOnSuccessListener(unused ->
                        enqueteRef.collection("votos").get().addOnSuccessListener(qs -> {

                            for (DocumentSnapshot doc : qs.getDocuments()) {
                                doc.getReference().delete();
                            }

                            // ===============================
                            // ATIVIDADE 4 — LOG AQUI
                            // ===============================
                            Map<String, Object> logData = new HashMap<>();
                            logData.put("tipo", "reset_votacao");
                            logData.put("observacao", "Reset solicitado via app");
                            logData.put("timestamp", FieldValue.serverTimestamp());

                            enqueteRef.collection("logs").add(logData);

                            callback.onSucesso();
                        })
                )
                .addOnFailureListener(callback::onErro);
    }

    // =====================================================================
    //  ATIVIDADE 5: Configurações estendidas (Rodapé + Data Limite)
    // =====================================================================
    public void salvarConfiguracoes(String titulo,
                                    String opcaoA,
                                    String opcaoB,
                                    String opcaoC,
                                    String rodape,
                                    String dataFim,
                                    OperacaoCallback callback) {

        Map<String, Object> dados = new HashMap<>();
        dados.put("tituloEnquete", titulo);
        dados.put("textoOpcaoA", opcaoA);
        dados.put("textoOpcaoB", opcaoB);
        dados.put("textoOpcaoC", opcaoC);

        // ===============================
        // ATIVIDADE 5 — CAMPOS NOVOS
        // ===============================
        dados.put("mensagemRodape", rodape);
        dados.put("dataHoraEncerramento", dataFim);

        enqueteRef.set(dados, SetOptions.merge())
                .addOnSuccessListener(unused -> callback.onSucesso())
                .addOnFailureListener(callback::onErro);
    }

    public interface ConfiguracaoCompletaCallback {
        void onConfig(String t, String a, String b, String c, String rodape, String fim);
        void onErro(Exception e);
    }

    public void carregarConfiguracoesCompleta(ConfiguracaoCompletaCallback callback) {

        enqueteRef.get().addOnSuccessListener(s -> {

            if (s != null && s.exists()) {
                callback.onConfig(
                        s.getString("tituloEnquete"),
                        s.getString("textoOpcaoA"),
                        s.getString("textoOpcaoB"),
                        s.getString("textoOpcaoC"),
                        s.getString("mensagemRodape"),
                        s.getString("dataHoraEncerramento")
                );
            } else {
                callback.onErro(null);
            }

        }).addOnFailureListener(callback::onErro);
    }


    // =====================================================================
    //  Callbacks originais (sem alterações)
    // =====================================================================

    public interface EnqueteListener {
        void onEnqueteAtualizada(Enquete enquete);
        void onErro(@Nullable Exception e);
    }

    public interface OperacaoCallback {
        void onSucesso();
        void onErro(@Nullable Exception e);
    }

    public interface RegistrarVotoCallback {
        void onVotoRegistrado(String opcao);
        void onJaVotou(@Nullable String opcaoExistente);
        void onErro(@Nullable Exception e);
    }
}
