package bsi.LP2.vca;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static SistemaGerenciador sistema = new SistemaGerenciador();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        // Renomeei a classe interna lógica para 'SistemaGerenciador' no código abaixo para alinhar,
        // mas assuma que estamos usando a classe GerenciadorEvento criada anteriormente.
        // Instancia o gerenciador (que carrega os dados automaticamente)
        GerenciadorEvento gerenciador = new GerenciadorEvento();

        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
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
                    break;
                case "0":
                    System.out.println("Salvando dados e saindo...");
                    gerenciador.salvarDados();
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
            System.out.println("\nPressione ENTER para continuar...");
            scanner.nextLine();
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n#############################################");
        System.out.println("###   SISTEMA DE INSCRIÇÕES - TEMA VI     ###");
        System.out.println("#############################################");
        System.out.println("1 - Nova Inscrição");
        System.out.println("2 - Consultar Vagas Disponíveis");
        System.out.println("3 - Consultar Participante (por CPF)");
        System.out.println("4 - Listar Menores de Idade em uma Oficina");
        System.out.println("5 - Relatório Estatístico (Geral e Por Oficina)");
        System.out.println("0 - Sair");
        System.out.print("Digite a opção desejada: ");
    }

    private static void realizarInscricao(GerenciadorEvento gerenciador) {
        System.out.println("\n--- NOVA INSCRIÇÃO ---");

        try {
            // 1. CPF
            String cpf;
            while (true) {
                System.out.print("Digite o CPF (apenas números): ");
                cpf = scanner.nextLine().trim();
                if (cpf.isEmpty()) {
                    System.out.println("CPF não pode ser vazio.");
                    continue;
                }
                if (gerenciador.isCpfCadastrado(cpf)) {
                    System.out.println("ERRO: Este CPF já realizou inscrição!");
                    return; // Cancela inscrição
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
                System.out.println("Entrada inválida. Digite M ou F.");
            }

            // 4. Data Nascimento
            LocalDate dataNasc = null;
            while (dataNasc == null) {
                System.out.print("Data de Nascimento (dd/MM/yyyy): ");
                String dataStr = scanner.nextLine();
                try {
                    dataNasc = LocalDate.parse(dataStr, dtf);
                    if (dataNasc.isAfter(LocalDate.now())) {
                        System.out.println("Data não pode ser futura.");
                        dataNasc = null;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Formato inválido. Tente novamente.");
                }
            }

            Participante novoP = new Participante(nome, cpf, sexo, dataNasc);

            // 5. Seleção de Oficinas
            int countOficinas = 0;
            boolean selecionando = true;

            while (selecionando && countOficinas < 3) {
                System.out.println("\n--- Selecione a Oficina (" + (countOficinas+1) + "/3) ---");
                List<Oficina> lista = gerenciador.getOficinas();

                for (int i = 0; i < lista.size(); i++) {
                    System.out.println((i + 1) + ". " + lista.get(i).toString());
                }
                System.out.println("0. Finalizar seleção de oficinas");

                System.out.print("Escolha o número: ");
                String opStr = scanner.nextLine();

                try {
                    int op = Integer.parseInt(opStr);

                    if (op == 0) {
                        if (countOficinas >= 1) {
                            selecionando = false;
                        } else {
                            System.out.println("ALERTA: Você deve selecionar no mínimo 1 oficina!");
                        }
                        continue;
                    }

                    if (op < 1 || op > lista.size()) {
                        System.out.println("Opção inválida.");
                        continue;
                    }

                    Oficina ofSelecionada = lista.get(op - 1);

                    // Validações de Oficina
                    if (!ofSelecionada.temVaga()) {
                        System.out.println("ERRO: Oficina lotada!");
                    } else if (novoP.getOficinas().contains(ofSelecionada.getNome())) {
                        System.out.println("ERRO: Você já selecionou esta oficina.");
                    } else {
                        novoP.adicionarOficina(ofSelecionada.getNome());
                        countOficinas++;
                        System.out.println(">> Oficina " + ofSelecionada.getNome() + " adicionada!");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Por favor, digite um número válido.");
                }
            }

            // Finalizar Cadastro
            gerenciador.registrarParticipante(novoP);
            System.out.println("\nSUCESSO! Inscrição realizada para " + nome);

        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    private static void consultarVagas(GerenciadorEvento gerenciador) {
        System.out.println("\n--- QUADRO DE VAGAS ---");
        for (Oficina of : gerenciador.getOficinas()) {
            System.out.println(of.toString());
        }
    }

    private static void consultarParticipante(GerenciadorEvento gerenciador) {
        System.out.print("\nDigite o CPF para busca: ");
        String cpf = scanner.nextLine();
        System.out.println(gerenciador.consultarPorCpf(cpf));
    }

    private static void listarMenoresEmOficina(GerenciadorEvento gerenciador) {
        System.out.println("\n--- LISTAR MENORES DE IDADE ---");
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

                System.out.println("\nMenores de Idade em " + nomeOficina + ":");
                if (menores.isEmpty()) {
                    System.out.println("(Nenhum registrado)");
                } else {
                    for (String s : menores) System.out.println(" - " + s);
                }
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }
}