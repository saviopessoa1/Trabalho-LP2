package bsi.LP2.vca.Savio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Definição de Cores e Estilos
    public static final String RESET = "\u001B[0m";
    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String CIANO = "\u001B[36m";
    public static final String NEGRITO = "\u001B[1m";

    private static GerenciadorEvento gerenciador = new GerenciadorEvento();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        limparTela();
        exibirLogo();

        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
            System.out.print(NEGRITO + "➜ Escolha uma opção: " + RESET);
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1": realizarInscricao(gerenciador); break;
                case "2": consultarVagas(gerenciador); break;
                case "3": consultarParticipante(gerenciador); break;
                case "4": listarMenoresEmOficina(gerenciador); break;

                // Estatísticas
                case "5": System.out.println(gerenciador.getEstatisticaSexo()); pausar(); break;
                case "6": System.out.println(gerenciador.getEstatisticaTotalPorOficina()); pausar(); break;
                case "7": System.out.println(gerenciador.getEstatisticaFaixaEtaria()); pausar(); break;

                // Exportar TXT
                case "8":
                    if(gerenciador.exportarRelatorioTxt()) {
                        msgSucesso("Arquivo 'relatorio_geral.txt' gerado na pasta do projeto!");
                    } else {
                        msgErro("Falha ao gerar relatório TXT.");
                    }
                    break;

                case "0":
                    System.out.println("\n" + AMARELO + "💾 Salvando dados e encerrando..." + RESET);
                    gerenciador.salvarDados();
                    System.out.println(VERDE + "✔ Sistema finalizado com sucesso. Até logo!" + RESET);
                    rodando = false;
                    break;
                default:
                    msgErro("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n" + AZUL + "╔═══════════════════════════════════════╗");
        System.out.println("║            MENU PRINCIPAL             ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ " + CIANO + "[1]" + AZUL + " Nova Inscrição                     ║");
        System.out.println("║ " + CIANO + "[2]" + AZUL + " Consultar Vagas Disponíveis        ║");
        System.out.println("║ " + CIANO + "[3]" + AZUL + " Consultar Participante (CPF)       ║");
        System.out.println("║ " + CIANO + "[4]" + AZUL + " Consultar Menores por Oficina      ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ " + AMARELO + "[5]" + AZUL + " Estatísticas: Por Sexo             ║");
        System.out.println("║ " + AMARELO + "[6]" + AZUL + " Estatísticas: Total por Oficina    ║");
        System.out.println("║ " + AMARELO + "[7]" + AZUL + " Estatísticas: Faixa Etária         ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ " + VERDE   + "[8]" + AZUL + " Exportar Relatório para TXT        ║");
        System.out.println("║ " + VERMELHO + "[0]" + AZUL + " Sair e Salvar                      ║");
        System.out.println("╚═══════════════════════════════════════╝" + RESET);
    }

    private static void exibirLogo() {
        System.out.println(CIANO + NEGRITO);
        System.out.println("   ██╗███████╗██████╗  █████╗ ");
        System.out.println("   ██║██╔════╝██╔══██╗██╔══██╗");
        System.out.println("   ██║█████╗  ██████╔╝███████║");
        System.out.println("   ██║██╔══╝  ██╔══██╗██╔══██║");
        System.out.println("   ██║██║     ██████╔╝██║  ██║");
        System.out.println("   ╚═╝╚═╝     ╚═════╝ ╚═╝  ╚═╝");
        System.out.println("   SISTEMA DE EVENTOS - TEMA VI" + RESET);
        System.out.println("   ---------------------------");
    }

    private static void msgSucesso(String msg) {
        System.out.println("\n" + VERDE + "✔ SUCESSO: " + msg + RESET);
        pausar();
    }

    private static void msgErro(String msg) {
        System.out.println("\n" + VERMELHO + "✖ ERRO: " + msg + RESET);
        pausar();
    }

    private static void pausar() {
        System.out.println("\n" + NEGRITO + "[Pressione ENTER para continuar]" + RESET);
        scanner.nextLine();
        limparTela();
        exibirLogo();
    }

    private static void limparTela() {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private static void realizarInscricao(GerenciadorEvento gerenciador) {
        System.out.println("\n" + CIANO + ">>> CADASTRO DE NOVO PARTICIPANTE" + RESET);
        try {
            // 1. CPF
            String cpf;
            while (true) {
                System.out.print("Digite o CPF (apenas números): ");
                cpf = scanner.nextLine().trim();
                if (cpf.isEmpty()) { System.out.println(VERMELHO + "CPF vazio." + RESET); continue; }
                if (!cpf.matches("\\d+")) { System.out.println(VERMELHO + "Apenas números!" + RESET); continue; }
                if (cpf.length() != 11) { System.out.println(VERMELHO + "Deve ter 11 dígitos." + RESET); continue; }
                if (gerenciador.isCpfCadastrado(cpf)) { msgErro("CPF já cadastrado!"); return; }
                break;
            }

            // 2. Nome
            System.out.print("Nome Completo: ");
            String nome = scanner.nextLine().trim();

            // 3. Sexo
            String sexo;
            while(true) {
                System.out.print("Sexo (M/F): ");
                sexo = scanner.nextLine().toUpperCase().trim();
                if(sexo.equals("M") || sexo.equals("F")) break;
                System.out.println(VERMELHO + "Digite M ou F." + RESET);
            }

            // 4. Data Nascimento
            LocalDate dataNasc = null;
            while (dataNasc == null) {
                System.out.print("Data de Nascimento (dd/MM/yyyy): ");
                try {
                    dataNasc = LocalDate.parse(scanner.nextLine(), dtf);
                    if (dataNasc.isAfter(LocalDate.now())) { System.out.println(VERMELHO + "Data futura!" + RESET); dataNasc = null; }
                } catch (DateTimeParseException e) { System.out.println(VERMELHO + "Formato inválido." + RESET); }
            }

            Participante novoP = new Participante(nome, cpf, sexo, dataNasc);

            // 5. Seleção de Oficinas
            int countOficinas = 0;
            boolean selecionando = true;

            while (selecionando && countOficinas < 3) {
                System.out.println("\n" + AMARELO + "--- SELEÇÃO DE OFICINAS (" + (countOficinas+1) + "/3) ---" + RESET);
                List<Oficina> lista = gerenciador.getOficinas();

                for (int i = 0; i < lista.size(); i++) {
                    Oficina of = lista.get(i);
                    String cor = of.temVaga() ? VERDE : VERMELHO;
                    String info = String.format("%s [Inscritos: %d/30]", of.getNome(), of.getInscritosAtuais());
                    System.out.println(cor + (i + 1) + ". " + info + RESET);
                }

                // --- AQUI ESTÁ A MUDANÇA SOLICITADA ---
                // Adicionei uma linha em branco antes (\n), cor VERMELHA, NEGRITO e colchetes.
                System.out.println("\n" + VERMELHO + NEGRITO + "[ 0. Finalizar seleção ]" + RESET);
                System.out.println("--------------------------------");

                System.out.print("Escolha: ");
                try {
                    int op = Integer.parseInt(scanner.nextLine());

                    if (op == 0) {
                        if (countOficinas >= 1) {
                            selecionando = false;
                        } else {
                            System.out.println(VERMELHO + "⚠ Você deve selecionar no mínimo 1 oficina!" + RESET);
                        }
                        continue;
                    }

                    if (op < 1 || op > lista.size()) {
                        System.out.println(VERMELHO + "Opção inválida." + RESET);
                        continue;
                    }

                    Oficina ofSelecionada = lista.get(op - 1);

                    if (!ofSelecionada.temVaga()) {
                        System.out.println(VERMELHO + "✖ Oficina lotada! Escolha outra." + RESET);
                    } else if (novoP.getOficinas().contains(ofSelecionada.getNome())) {
                        System.out.println(AMARELO + "⚠ Você já selecionou esta oficina." + RESET);
                    } else {
                        novoP.adicionarOficina(ofSelecionada.getNome());
                        countOficinas++;
                        System.out.println(VERDE + "✔ " + ofSelecionada.getNome() + " adicionada!" + RESET);
                    }

                } catch (NumberFormatException e) {
                    System.out.println(VERMELHO + "Por favor, digite um número válido." + RESET);
                }
            }

            gerenciador.registrarParticipante(novoP);
            msgSucesso("Inscrição realizada para " + nome);

        } catch (Exception e) {
            msgErro("Erro inesperado: " + e.getMessage());
        }
    }

    private static void consultarVagas(GerenciadorEvento gerenciador) {
        System.out.println("\n" + CIANO + "╔═══════════════════════════════════════╗");
        System.out.println("║           QUADRO DE VAGAS             ║");
        System.out.println("╠═══════════════════════════════════════╣" + RESET);
        for (Oficina of : gerenciador.getOficinas()) {
            String corVaga = of.temVaga() ? VERDE : VERMELHO;
            String infoVagas = String.format("%-39s", String.format("%s [%d/30]", of.getNome(), of.getInscritosAtuais()));
            System.out.println("║ " + corVaga + infoVagas + RESET + " ║");
        }
        System.out.println(CIANO + "╚═══════════════════════════════════════╝" + RESET);
        pausar();
    }

    private static void consultarParticipante(GerenciadorEvento gerenciador) {
        System.out.print("\nDigite o CPF para busca: ");
        System.out.println(gerenciador.consultarPorCpf(scanner.nextLine()));
        pausar();
    }

    private static void listarMenoresEmOficina(GerenciadorEvento gerenciador) {
        System.out.println("\n" + AMARELO + "--- CONSULTAR MENORES POR OFICINA ---" + RESET);
        List<Oficina> lista = gerenciador.getOficinas();
        for (int i = 0; i < lista.size(); i++) {
            Oficina of = lista.get(i);
            System.out.println(CIANO + (i+1) + ". " + RESET + of.getNome() + " [Inscritos: " + of.getInscritosAtuais() + "/30]");
        }
        System.out.print(NEGRITO + "Selecione a oficina: " + RESET);
        try {
            int op = Integer.parseInt(scanner.nextLine());
            if (op > 0 && op <= lista.size()) {
                String nome = lista.get(op-1).getNome();
                List<String> menores = gerenciador.listarMenoresEmOficina(nome);
                System.out.println("\n" + AZUL + "=== Menores em " + NEGRITO + nome + RESET + AZUL + " ===" + RESET);
                if (menores.isEmpty()) System.out.println(AMARELO + "(Nenhum registrado)" + RESET);
                else for (String s : menores) System.out.println(" • " + s);
                pausar();
            } else msgErro("Opção inválida.");
        } catch (NumberFormatException e) { msgErro("Entrada inválida."); }
    }
}


