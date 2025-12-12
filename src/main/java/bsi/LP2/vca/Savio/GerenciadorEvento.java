package bsi.LP2.vca.Savio;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorEvento {

    private List<Participante> participantes;
    private List<Oficina> oficinas;
    private final String ARQUIVO_DADOS = "dados_evento_ifba.bin";

    public GerenciadorEvento() {
        this.participantes = new ArrayList<>();
        this.oficinas = new ArrayList<>();
        if (!carregarDados()) {
            inicializarOficinasPadrao();
        }
    }

    private void inicializarOficinasPadrao() {
        oficinas.add(new Oficina("jQuery"));
        oficinas.add(new Oficina("Arduino"));
        oficinas.add(new Oficina("Desenvolvimento para Android"));
        oficinas.add(new Oficina("Layout Responsivo com HTML5 e CSS3"));
        oficinas.add(new Oficina("C++: Desenvolvimento para iOS"));
        oficinas.add(new Oficina("Google Apps"));
    }

    public List<Oficina> getOficinas() { return oficinas; }
    public List<Participante> getParticipantes() { return participantes; }

    public boolean isCpfCadastrado(String cpf) {
        for (Participante p : participantes) {
            if (p.getCpf().equals(cpf)) return true;
        }
        return false;
    }

    public void registrarParticipante(Participante p) {
        participantes.add(p);
        for (String nomeOficinaEscolhida : p.getOficinas()) {
            for (Oficina of : oficinas) {
                if (of.getNome().equals(nomeOficinaEscolhida)) {
                    of.adicionarInscrito();
                }
            }
        }
        salvarDados();
    }

    public String consultarPorCpf(String cpf) {
        for (Participante p : participantes) {
            if (p.getCpf().equals(cpf)) {
                StringBuilder sb = new StringBuilder();
                sb.append("╔═══════════════════════════════════════════════╗\n");
                sb.append("║           DETALHES DO PARTICIPANTE            ║\n");
                sb.append("╠═══════════════════════════════════════════════╣\n");
                sb.append(String.format("║ Nome: %-39s ║\n", truncar(p.getNome(), 39)));
                sb.append(String.format("║ CPF:  %-39s ║\n", p.getCpf()));
                sb.append(String.format("║ Idade:%-39s ║\n", p.getIdade() + " anos (" + p.getFaixaEtaria() + ")"));
                sb.append("╠═══════════════════════════════════════════════╣\n");
                sb.append("║ OFICINAS INSCRITAS:                           ║\n");
                for (String of : p.getOficinas()) {
                    sb.append(String.format("║  • %-42s ║\n", truncar(of, 42)));
                }
                sb.append("╚═══════════════════════════════════════════════╝");
                return sb.toString();
            }
        }
        return "❌ Participante não encontrado com este CPF.";
    }

    public List<String> listarMenoresEmOficina(String nomeOficina) {
        List<String> menores = new ArrayList<>();
        for (Participante p : participantes) {
            if (p.getOficinas().contains(nomeOficina) && p.isMenorDeIdade()) {
                menores.add(p.getNome() + " (CPF: " + p.getCpf() + ")");
            }
        }
        return menores;
    }

    // --- MÉTODOS ESTATÍSTICOS ---

    public String getEstatisticaSexo() {
        if (participantes.isEmpty()) return "⚠ Nenhum dado para gerar estatísticas.";
        long total = participantes.size();
        long masc = participantes.stream().filter(p -> p.getSexo().equals("M")).count();
        long fem = participantes.stream().filter(p -> p.getSexo().equals("F")).count();

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append("║           ESTATÍSTICA: POR SEXO                    ║\n");
        sb.append("╠════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Total de Inscritos: %-30d ║\n", total));
        sb.append(String.format("║ Homens:             %-30s ║\n", String.format("%d (%.1f%%)", masc, (masc * 100.0 / total))));
        sb.append(String.format("║ Mulheres:           %-30s ║\n", String.format("%d (%.1f%%)", fem, (fem * 100.0 / total))));
        sb.append("╚════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    public String getEstatisticaTotalPorOficina() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append("║        ESTATÍSTICA: INSCRITOS POR OFICINA          ║\n");
        sb.append("╠════════════════════════════════════════════════════╣\n");
        for (Oficina of : oficinas) {
            sb.append(String.format("║ %-50s ║\n", truncar(of.getNome(), 50)));
            sb.append(String.format("║ -> Total: %-41d ║\n", of.getInscritosAtuais()));
            sb.append("╟────────────────────────────────────────────────────╢\n");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 54);
        sb.append("╚════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    public String getEstatisticaFaixaEtaria() {
        if (participantes.isEmpty()) return "⚠ Nenhum dado para gerar estatísticas.";
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════╗\n");
        sb.append("║        ESTATÍSTICA: FAIXA ETÁRIA POR OFICINA       ║\n");
        sb.append("╠════════════════════════════════════════════════════╣\n");

        for (Oficina of : oficinas) {
            long qtd = of.getInscritosAtuais();
            long menores = 0, maiores = 0;
            for (Participante p : participantes) {
                if (p.getOficinas().contains(of.getNome())) {
                    if (p.isMenorDeIdade()) menores++; else maiores++;
                }
            }
            sb.append(String.format("║ %-50s ║\n", truncar(of.getNome(), 50)));
            if (qtd > 0) {
                sb.append(String.format("║    Menores: %-39s ║\n", String.format("%.1f%% (%d)", (menores * 100.0 / qtd), menores)));
                sb.append(String.format("║    Maiores: %-39s ║\n", String.format("%.1f%% (%d)", (maiores * 100.0 / qtd), maiores)));
            } else {
                sb.append("║    (Sem inscritos)                                 ║\n");
            }
            sb.append("╟────────────────────────────────────────────────────╢\n");
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 54);
        sb.append("╚════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    private String truncar(String str, int largura) {
        if (str.length() > largura) return str.substring(0, largura - 3) + "...";
        return str;
    }

    // --- NOVA FUNCIONALIDADE: EXPORTAR PARA TXT ---
    public boolean exportarRelatorioTxt() {
        String nomeArquivo = "relatorio_geral.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("=================================================");
            writer.println("   RELATÓRIO GERAL DO SISTEMA DE EVENTOS - IFBA");
            writer.println("   Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println("=================================================");
            writer.println();

            // Removemos os caracteres de borda especiais para garantir legibilidade no bloco de notas
            writer.println(getEstatisticaSexo().replaceAll("[╔╗╚╝╠╣║╟═─]", ""));
            writer.println();
            writer.println(getEstatisticaTotalPorOficina().replaceAll("[╔╗╚╝╠╣║╟═─]", ""));
            writer.println();
            writer.println(getEstatisticaFaixaEtaria().replaceAll("[╔╗╚╝╠╣║╟═─]", ""));

            writer.println();
            writer.println("=================================================");
            writer.println("LISTA COMPLETA DE INSCRITOS:");
            for(Participante p : participantes) {
                writer.println("- " + p.getNome() + " (CPF: " + p.getCpf() + ")");
                writer.println("  Oficinas: " + p.getOficinas());
                writer.println("-------------------------------------------------");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Erro ao exportar TXT: " + e.getMessage());
            return false;
        }
    }

    // --- MANIPULAÇÃO DE ARQUIVOS BINÁRIOS ---
    public void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(participantes);
            oos.writeObject(oficinas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean carregarDados() {
        File f = new File(ARQUIVO_DADOS);
        if (!f.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            this.participantes = (List<Participante>) ois.readObject();
            this.oficinas = (List<Oficina>) ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }
}


