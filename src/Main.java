import dominio.usuario.Admin;
import dominio.usuario.Fabrica;
import dominio.usuario.Loja;
import dominio.usuario.Perfil;
import menu.MenuAdmin;
import menu.MenuFabrica;
import menu.MenuLoja;
import sistema.SistemaShowroom;

import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        SistemaShowroom sistema = new SistemaShowroom();

        // Dados de TESTE/DESENVOLVIMENTO. Não são credenciais reais.
        sistema.adicionarPerfil(new Admin("admin", "admin123"));
        sistema.adicionarPerfil(new Loja("CDA CAXIAS", "caxias", "caxias123"));
        sistema.adicionarPerfil(new Loja("CDA NOVO HAMBURGO", "novohamburgo", "nh123"));
        sistema.adicionarPerfil(new Loja("CDA PORTO ALEGRE", "portoalegre", "poa123"));
        sistema.adicionarPerfil(new Fabrica("fabrica", "fabrica123"));

        while (true) {

            System.out.println("\n========== SISTEMA SHOWROOM ==========");
            System.out.println("1 - Login");
            System.out.println("0 - Sair");

            System.out.print("Escolha uma opção: ");
            if (!scanner.hasNextLine()) {
                break;
            }
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

            Optional<Perfil> perfilAutenticado = sistema.autenticar(login, senha);

            if (perfilAutenticado.isEmpty()) {
                System.out.println("\nLogin ou senha incorretos.");
                continue;
            }

            System.out.println("\nLogin realizado com sucesso!");
            Perfil perfil = perfilAutenticado.get();

            if (perfil instanceof Admin) {
                new MenuAdmin(scanner, sistema).iniciar();
            } else if (perfil instanceof Loja loja) {
                new MenuLoja(scanner).iniciar(loja, sistema);
            } else if (perfil instanceof Fabrica) {
                new MenuFabrica(scanner, sistema).iniciar();
            }
        }

        scanner.close();
    }
}
