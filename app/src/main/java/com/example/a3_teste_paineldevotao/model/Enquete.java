package com.example.a3_teste_paineldevotao.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo que representa a enquete armazenada no Firestore.
 *
 * Esta classe centraliza TODOS os campos da enquete:
 * - Textos de título e opções
 * - Contadores de votos
 * - (ATIVIDADE 5) Campos de rodapé e data/hora de encerramento
 *
 * Ela serve como “ponte” entre o Firestore e as Activities.
 */

public class Enquete {


    private String tituloEnquete;
    private String textoOpcaoA;
    private String textoOpcaoB;
    private String textoOpcaoC;

    // Contadores de votos
    private long opcaoA;
    private long opcaoB;
    private long opcaoC;

    // =====================================================================
    // 2)  NOVOS CAMPOS ADICIONADOS (ATIVIDADE 5)
    // =====================================================================
    /**
     * ATIVIDADE 5:
     * Estes campos foram adicionados para permitir:
     * - Mensagem de rodapé configurável
     * - Definição de data/hora limite para encerrar a votação
     */
    private String mensagemRodape;          // Novo campo (string livre)
    private String dataHoraEncerramento;    // Guardada como String (ex.: "31/12/2025 20:00")


    // =====================================================================
    // CONSTRUTORES (originais, sem mudanças)
    // =====================================================================

    public Enquete() {}

    public Enquete(String tituloEnquete,
                   String textoOpcaoA,
                   String textoOpcaoB,
                   String textoOpcaoC,
                   long opcaoA,
                   long opcaoB,
                   long opcaoC) {

        this.tituloEnquete = tituloEnquete;
        this.textoOpcaoA = textoOpcaoA;
        this.textoOpcaoB = textoOpcaoB;
        this.textoOpcaoC = textoOpcaoC;

        this.opcaoA = opcaoA;
        this.opcaoB = opcaoB;
        this.opcaoC = opcaoC;
    }


    // =====================================================================
    //  GETTERS E SETTERS ORIGINAIS
    // =====================================================================

    public String getTituloEnquete() { return tituloEnquete; }
    public void setTituloEnquete(String tituloEnquete) { this.tituloEnquete = tituloEnquete; }

    public String getTextoOpcaoA() { return textoOpcaoA; }
    public void setTextoOpcaoA(String textoOpcaoA) { this.textoOpcaoA = textoOpcaoA; }

    public String getTextoOpcaoB() { return textoOpcaoB; }
    public void setTextoOpcaoB(String textoOpcaoB) { this.textoOpcaoB = textoOpcaoB; }

    public String getTextoOpcaoC() { return textoOpcaoC; }
    public void setTextoOpcaoC(String textoOpcaoC) { this.textoOpcaoC = textoOpcaoC; }

    public long getOpcaoA() { return opcaoA; }
    public void setOpcaoA(long opcaoA) { this.opcaoA = opcaoA; }

    public long getOpcaoB() { return opcaoB; }
    public void setOpcaoB(long opcaoB) { this.opcaoB = opcaoB; }

    public long getOpcaoC() { return opcaoC; }
    public void setOpcaoC(long opcaoC) { this.opcaoC = opcaoC; }


    // =====================================================================
    //  NOVOS GETTERS E SETTERS (ATIVIDADE 5)
    // =====================================================================

    public String getMensagemRodape() { return mensagemRodape; }
    public void setMensagemRodape(String mensagemRodape) { this.mensagemRodape = mensagemRodape; }

    public String getDataHoraEncerramento() { return dataHoraEncerramento; }
    public void setDataHoraEncerramento(String dataHoraEncerramento) { this.dataHoraEncerramento = dataHoraEncerramento; }


    // =====================================================================
    // 6) MÉTODO toMap() ATUALIZADO
    // =====================================================================

    /**
     * Converte a enquete para Map<String, Object>, formato aceito pelo Firestore.
     *
     * ATENÇÃO — ATIVIDADE 5:
     * Os novos campos "mensagemRodape" e "dataHoraEncerramento"
     * foram adicionados ao Map para permitir update/merge no Firestore.
     */
    public Map<String, Object> toMap() {

        Map<String, Object> dados = new HashMap<>();

        // ---------- Campos originais ----------
        dados.put("tituloEnquete", tituloEnquete);
        dados.put("textoOpcaoA", textoOpcaoA);
        dados.put("textoOpcaoB", textoOpcaoB);
        dados.put("textoOpcaoC", textoOpcaoC);

        dados.put("opcaoA", opcaoA);
        dados.put("opcaoB", opcaoB);
        dados.put("opcaoC", opcaoC);

        // ---------- NOVOS CAMPOS (ATIVIDADE 5) ----------
        dados.put("mensagemRodape", mensagemRodape);
        dados.put("dataHoraEncerramento", dataHoraEncerramento);

        return dados;
    }
}
