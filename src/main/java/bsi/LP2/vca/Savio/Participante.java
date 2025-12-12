package bsi.LP2.vca.Savio;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o aluno/participante.
 * Armazena dados pessoais e a lista de oficinas escolhidas.
 */
public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String sexo; // "M" ou "F"
    private LocalDate dataNascimento;
    private List<String> nomesOficinasInscritas;

    public Participante(String nome, String cpf, String sexo, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.nomesOficinasInscritas = new ArrayList<>();
    }

    // --- Métodos de Negócio ---

    public void adicionarOficina(String nomeOficina) {
        this.nomesOficinasInscritas.add(nomeOficina);
    }

    /**
     * Calcula a idade com base na data atual (LocalDate.now()).
     */
    public int getIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    /**
     * Verifica se é menor de idade (< 18).
     */
    public boolean isMenorDeIdade() {
        return getIdade() < 18;
    }

    public String getFaixaEtaria() {
        return isMenorDeIdade() ? "Menor de Idade" : "Maior de Idade";
    }

    // --- Getters ---

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getSexo() {
        return sexo;
    }

    public List<String> getOficinas() {
        return nomesOficinasInscritas;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Nome: %s | CPF: %s | Sexo: %s | Nasc: %s (%d anos - %s)",
                nome, cpf, sexo, dataNascimento.format(dtf), getIdade(), getFaixaEtaria());
    }
}