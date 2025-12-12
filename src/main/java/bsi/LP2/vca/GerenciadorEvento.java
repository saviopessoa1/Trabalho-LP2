package
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar toda a lógica do sistema.
 * Realiza persistência (salvar/carregar arquivos) e validações.
 */
public class GerenciadorEvento {

    private List<Participante> participantes;
    private List<Oficina> oficinas;

    // Nome do arquivo onde os dados serão salvos
    private final String ARQUIVO_DADOS = "dados_evento_ifba.bin";

    public GerenciadorEvento() {
        this.participantes = new ArrayList<>();
        this.oficinas = new ArrayList<>();

        // Tenta carregar dados anteriores. Se não existir, inicia do zero.
        if (!carregarDados()) {
            inicializarOficinasPadrao();
        }
    }

    /**
     * Inicializa as oficinas exigidas no TEMA VI caso seja a primeira execução.
     */
    private void inicializarOficinasPadrao() {
        System.out.println("Inicializando oficinas padrão...");
        oficinas.add(new Oficina("jQuery"));
        oficinas.add(new Oficina("Arduino"));
        oficinas.add(new Oficina("Desenvolvimento para Android"));
        oficinas.add(new Oficina("Layout Responsivo com HTML5 e CSS3"));
        oficinas.add(new Oficina("C++: Desenvolvimento para iOS"));
        oficinas.add(new Oficina("Google Apps"));
    }

    public List<Oficina> getOficinas() {
        return oficinas;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    /**
     * Validação: Verifica se o CPF já existe na lista.
     */
    public boolean isCpfCadastrado(String cpf) {
        for (Participante p : participantes) {
            if (p.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registra um novo participante e atualiza a contagem nas oficinas.
     */
    public void registrarParticipante(Participante p) {
        participantes.add(p);

        // Atualiza a contagem de inscritos dentro do objeto Oficina
        for (String nomeOficinaEscolhida : p.getOficinas()) {
            for (Oficina of : oficinas) {
                if (of.getNome().equals(nomeOficinaEscolhida)) {
                    of.adicionarInscrito();
                }
            }
        }
        salvarDados(); // Persistência automática a cada cadastro
    }

    /**
     * Busca participante por CPF para exibir detalhes.
     */
    public String consultarPorCpf(String cpf) {
        for (Participante p : participantes) {
            if (p.getCpf().equals(cpf)) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n=== DETALHES DO PARTICIPANTE ===\n");
                sb.append(p.toString()).append("\n");
                sb.append("Oficinas Inscritas:\n");
                for (String of : p.getOficinas()) {
                    sb.append(" - ").append(of).append("\n");
                }
                return sb.toString();
            }
        }
        return "Participante não encontrado com este CPF.";
    }

    /**
     * Retorna lista de menores de 18 em uma oficina específica.
     */
    public List<String> listarMenoresEmOficina(String nomeOficina) {
        List<String> menores = new ArrayList<>();
        for (Participante p : participantes) {
            if (p.getOficinas().contains(nomeOficina) && p.isMenorDeIdade()) {
                menores.add(p.getNome() + " (CPF: " + p.getCpf() + ")");
            }
        }
        return menores;
    }

    /**
     * Gera o relatório estatístico completo exigido no PDF.
     */
    public String gerarEstatisticas() {
        if (participantes.isEmpty()) return "Nenhum dado para gerar estatísticas.";

        long total = participantes.size();
        long masc = participantes.stream().filter(p -> p.getSexo().equals("M")).count();
        long fem = participantes.stream().filter(p -> p.getSexo().equals("F")).count();

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== ESTATÍSTICAS GERAIS ===\n");
        sb.append(String.format("Total de Inscritos: %d\n", total));
        sb.append(String.format("Porcentagem Homens: %.1f%%\n", (masc * 100.0 / total)));
        sb.append(String.format("Porcentagem Mulheres: %.1f%%\n", (fem * 100.0 / total)));

        sb.append("\n=== POR OFICINA ===\n");
        for (Oficina of : oficinas) {
            long qtdNaOficina = of.getInscritosAtuais();

            // Contar faixas etárias nesta oficina específica
            long menores = 0;
            long maiores = 0;

            for (Participante p : participantes) {
                if (p.getOficinas().contains(of.getNome())) {
                    if (p.isMenorDeIdade()) menores++;
                    else maiores++;
                }
            }

            sb.append(String.format(">> %s:\n", of.getNome()));
            sb.append(String.format("   Total Inscritos: %d\n", qtdNaOficina));
            if (qtdNaOficina > 0) {
                sb.append(String.format("   Menores de Idade: %.1f%% (%d)\n", (menores * 100.0 / qtdNaOficina), menores));
                sb.append(String.format("   Maiores de Idade: %.1f%% (%d)\n", (maiores * 100.0 / qtdNaOficina), maiores));
            } else {
                sb.append("   (Sem inscritos para calcular porcentagem)\n");
            }
        }
        return sb.toString();
    }

    // --- MANIPULAÇÃO DE ARQUIVOS (PERSISTÊNCIA) ---

    public void salvarDados() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(participantes);
            oos.writeObject(oficinas);
            // System.out.println("Dados salvos automaticamente.");
        } catch (IOException e) {
            System.err.println("Erro Crítico: Não foi possível salvar os dados no disco: " + e.getMessage());
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
            System.err.println("Aviso: Arquivo de dados existe mas não pôde ser lido (pode estar corrompido ou vazio). Iniciando novo.");
            return false;
        }
    }
}