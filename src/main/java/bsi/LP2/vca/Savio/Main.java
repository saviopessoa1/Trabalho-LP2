package bsi.LP2.vca.Savio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Cores ANSI para o terminal (Funciona em IntelliJ, VSCode, Linux, Mac e novos Windows Terminal)
    public static final String RESET = "\u001B[0m";
    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String CIANO = "\u001B[36m";
    public static final String NEGRITO = "\u001B[1m";

    // A classe correta é GerenciadorEvento
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
                case "1":
                    realizarInscricao(gerenciador);
                    break;
                case "2":
                    consultarVagas(gerenciador);
                    break;
                case "3":
                    consultarParticipante(gerenciador);
                    break;
                case "4":
                    listarMenoresEmOficina(gerenciador);
                    break;
                case "5":
                    System.out.println(gerenciador.gerarEstatisticas());
                    pausar();
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

    // --- MÉTODOS VISUAIS ---

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

    private static void exibirMenuPrincipal() {
        System.out.println("\n" + AZUL + "╔═══════════════════════════════════════╗");
        System.out.println("║            MENU PRINCIPAL             ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ " + CIANO + "[1]" + AZUL + " Nova Inscrição                     ║");
        System.out.println("║ " + CIANO + "[2]" + AZUL + " Consultar Vagas Disponíveis        ║");
        System.out.println("║ " + CIANO + "[3]" + AZUL + " Consultar Participante (CPF)       ║");
        System.out.println("║ " + CIANO + "[4]" + AZUL + " Listar Menores em Oficina          ║");
        System.out.println("║ " + CIANO + "[5]" + AZUL + " Relatório Estatístico              ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ " + VERMELHO + "[0]" + AZUL + " Sair e Salvar                      ║");
        System.out.println("╚═══════════════════════════════════════╝" + RESET);
    }

    private static void msgSucesso(String msg) {
        System.out.println("\n" + VERDE + "✔ SUCESSO: " + msg + RESET);
        pausar();
    }

    private static void msgErro(String msg) {
        System.out.println("\n" + VERMELHO + "✖ ERRO: " + msg + RESET);
        pausar();
    }

    private static void msgInfo(String msg) {
        System.out.println(AMARELO + "ℹ " + msg + RESET);
    }

    private static void pausar() {
        System.out.println("\n" + NEGRITO + "[Pressione ENTER para continuar]" + RESET);
        scanner.nextLine();
        limparTela();
        exibirLogo(); // Redesenha o logo para manter a identidade visual
    }

    private static void limparTela() {
        // Tenta limpar o console imprimindo várias linhas vazias (método compatível com Java puro)
        for (int i = 0; i < 50; i++) System.out.println();
    }

    // --- LÓGICA DE INTERAÇÃO ---

    private static void realizarInscricao(GerenciadorEvento gerenciador) {
        System.out.println("\n" + CIANO + ">>> CADASTRO DE NOVO PARTICIPANTE" + RESET);

        try {
            // 1. CPF
            String cpf;
            while (true) {
                System.out.print("Digite o CPF (apenas números): ");
                cpf = scanner.nextLine().trim();
                if (cpf.isEmpty()) {
                    System.out.println(VERMELHO + "CPF não pode ser vazio." + RESET);
                    continue;
                }
                if (gerenciador.isCpfCadastrado(cpf)) {
                    msgErro("Este CPF já realizou inscrição!");
                    return;
                }
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
                System.out.println(VERMELHO + "Entrada inválida. Digite M ou F." + RESET);
            }

            // 4. Data Nascimento
            LocalDate dataNasc = null;
            while (dataNasc == null) {
                System.out.print("Data de Nascimento (dd/MM/yyyy): ");
                String dataStr = scanner.nextLine();
                try {
                    dataNasc = LocalDate.parse(dataStr, dtf);
                    if (dataNasc.isAfter(LocalDate.now())) {
                        System.out.println(VERMELHO + "Data não pode ser futura." + RESET);
                        dataNasc = null;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println(VERMELHO + "Formato inválido. Tente novamente." + RESET);
                }
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
                    // Ajuste na exibição para mostrar Inscritos/Total
                    String infoVagas = String.format("%s [Inscritos: %d/30]", of.getNome(), of.getInscritosAtuais());
                    System.out.println(cor + (i + 1) + ". " + infoVagas + RESET);
                }
                System.out.println("0. Finalizar seleção");

                System.out.print("Escolha o número: ");
                String opStr = scanner.nextLine();

                try {
                    int op = Integer.parseInt(opStr);

                    if (op == 0) {
                        if (countOficinas >= 1) selecionando = false;
                        else System.out.println(VERMELHO + "⚠ Selecione no mínimo 1 oficina!" + RESET);
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
                    System.out.println(VERMELHO + "Digite um número válido." + RESET);
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
            // Ajuste na exibição para mostrar Inscritos/Total
            String infoVagas = String.format("%-39s", String.format("%s [%d/30]", of.getNome(), of.getInscritosAtuais()));
            System.out.println("║ " + corVaga + infoVagas + RESET + " ║");
        }
        System.out.println(CIANO + "╚═══════════════════════════════════════╝" + RESET);
        pausar();
    }

    private static void consultarParticipante(GerenciadorEvento gerenciador) {
        System.out.print("\nDigite o CPF para busca: ");
        String cpf = scanner.nextLine();
        System.out.println(gerenciador.consultarPorCpf(cpf));
        pausar();
    }

    private static void listarMenoresEmOficina(GerenciadorEvento gerenciador) {
        System.out.println("\n" + AMARELO + "--- LISTAR MENORES DE IDADE ---" + RESET);
        List<Oficina> lista = gerenciador.getOficinas();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getNome());
        }
        System.out.print("Selecione a oficina: ");
        try {
            int op = Integer.parseInt(scanner.nextLine());
            if (op > 0 && op <= lista.size()) {
                String nomeOficina = lista.get(op - 1).getNome();
                List<String> menores = gerenciador.listarMenoresEmOficina(nomeOficina);

                System.out.println("\nMenores de Idade em " + NEGRITO + nomeOficina + RESET + ":");
                if (menores.isEmpty()) {
                    System.out.println("(Nenhum registrado)");
                } else {
                    for (String s : menores) System.out.println(" - " + s);
                }
                pausar();
            } else {
                msgErro("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            msgErro("Entrada inválida.");
        }
    }
}