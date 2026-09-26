import dominio.cliente.Cliente;
import dominio.usuario.Admin;
import dominio.usuario.Loja;
import menu.MenuAdmin;
import menu.MenuLoja;
import sistema.SistemaShowroom;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        SistemaShowroom sistema = new SistemaShowroom();

        Admin admin = new Admin("admin", "admin123");

        Loja caxias = new Loja(
                "CDA CAXIAS",
                "caxias",
                "caxias123"
        );

        Loja novoHamburgo = new Loja(
                "CDA NOVO HAMBURGO",
                "novohamburgo",
                "nh123"
        );

        Loja portoAlegre = new Loja(
                "CDA PORTO ALEGRE",
                "portoalegre",
                "poa123"
        );

        while (true) {

            System.out.println("\n========== SISTEMA SHOWROOM ==========");
            System.out.println("1 - Login");
            System.out.println("0 - Sair");

            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            if (opcao.equals("0")) {
                System.out.println("Encerrando sistema...");
                break;
            }

            if (!opcao.equals("1")) {
                System.out.println("Opção inválida.");
                continue;
            }

            System.out.print("\nLogin: ");
            String login = scanner.nextLine();

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            if (admin.getLogin().equals(login)
                    && admin.getSenha().equals(senha)) {

                System.out.println("\nLogin realizado com sucesso!");

                MenuAdmin menuAdmin = new MenuAdmin(scanner);
                menuAdmin.iniciar();

            } else if (caxias.getLogin().equals(login)
                    && caxias.getSenha().equals(senha)) {

                System.out.println("\nLogin realizado com sucesso!");

                MenuLoja menuLoja = new MenuLoja(scanner);
                menuLoja.iniciar(caxias, sistema);

            } else if (novoHamburgo.getLogin().equals(login)
                    && novoHamburgo.getSenha().equals(senha)) {

                System.out.println("\nLogin realizado com sucesso!");

                MenuLoja menuLoja = new MenuLoja(scanner);
                menuLoja.iniciar(novoHamburgo, sistema);

            } else if (portoAlegre.getLogin().equals(login)
                    && portoAlegre.getSenha().equals(senha)) {

                System.out.println("\nLogin realizado com sucesso!");

                MenuLoja menuLoja = new MenuLoja(scanner);
                menuLoja.iniciar(portoAlegre, sistema);

            } else {
                System.out.println("\nLogin ou senha incorretos.");
            }
        }

        scanner.close();
    }
}