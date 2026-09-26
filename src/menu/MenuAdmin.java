package menu;

import java.util.Scanner;

public class MenuAdmin {

    private Scanner scanner;

    public MenuAdmin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void iniciar() {

        while (true) {

            System.out.println("\n========== MENU ADMIN ==========");
            System.out.println("1 - Cadastrar cotação do ouro");
            System.out.println("2 - Visualizar faturamento total");
            System.out.println("3 - Visualizar faturamento por loja");
            System.out.println("4 - Listar clientes");
            System.out.println("5 - Listar modelos");
            System.out.println("6 - Ver status dos pedidos");
            System.out.println("0 - Sair");

            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {

                case "1":
                    System.out.println("\nCadastrar cotação do ouro");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "2":
                    System.out.println("\nFaturamento total");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "3":
                    System.out.println("\nFaturamento por loja");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "4":
                    System.out.println("\nClientes");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "5":
                    System.out.println("\nModelos");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "6":
                    System.out.println("\nStatus dos pedidos");
                    System.out.println("Funcionalidade em desenvolvimento.");
                    break;

                case "0":
                    System.out.println("Saindo da conta...");
                    return;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}