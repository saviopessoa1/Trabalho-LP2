package bsi.LP2.vca;

import java.io.Serializable;

/**
 * Representa uma oficina do evento.
 * Implementa Serializable para poder ser salva em arquivo.
 */
public class Oficina implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private int inscritosAtuais;
    // Conforme PDF: Limite de 30 vagas
    private final int CAPACIDADE_MAXIMA = 30;

    public Oficina(String nome) {
        this.nome = nome;
        this.inscritosAtuais = 0;
    }

    public String getNome() {
        return nome;
    }

    public int getInscritosAtuais() {
        return inscritosAtuais;
    }

    public int getVagasDisponiveis() {
        return CAPACIDADE_MAXIMA - inscritosAtuais;
    }

    /**
     * Verifica se ainda há vagas disponíveis.
     */
    public boolean temVaga() {
        return inscritosAtuais < CAPACIDADE_MAXIMA;
    }

    /**
     * Incrementa o contador de inscritos.
     * Retorna true se sucesso, false se cheia.
     */
    public boolean adicionarInscrito() {
        if (temVaga()) {
            this.inscritosAtuais++;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s [Vagas: %d/%d]", nome, getVagasDisponiveis(), CAPACIDADE_MAXIMA);
    }
}