package br.com.alura.screensound.principal;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.model.TipoArtista;
import br.com.alura.screensound.repository.ArtistaRepository;
import br.com.alura.screensound.service.ConsultaIA;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final ArtistaRepository repositorio;
    private Scanner scan = new Scanner(System.in);

    public Principal(ArtistaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao!= 9) {
            var menu = """
                    *** Screen Sound Músicas ***
                    
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                    
                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = scan.nextInt();
            scan.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void pesquisarDadosDoArtista() {
        System.out.println("\nInforme o nome do artista/banda que deseja pesquisar: ");
        var nomeArtista = scan.nextLine();

        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nomeArtista);

        if (artista.isPresent()) {
            System.out.println("\nPesquisando informações sobre " + nomeArtista + "...");
            System.out.println("\nAguarde enquanto consultamos...");

            String informacoes = ConsultaIA.obterInformacoesArtista(nomeArtista);

            if (informacoes != null && !informacoes.isEmpty()) {
                System.out.println("\nInformações sobre " + nomeArtista + ":");
                System.out.println("══════════════════════════════════════");
                System.out.println(informacoes);
                System.out.println("══════════════════════════════════════");
            } else {
                System.out.println("\nNão foi possível obter informações detalhadas para " + nomeArtista + " da IA.");
            }

            System.out.println("\nDeseja cadastrar músicas para este artista? (s/n)");
            var opcao = scan.nextLine();
            if (opcao.equalsIgnoreCase("s")) {
                cadastrarMusicas();
            }
        } else {
            System.out.println("\nArtista não encontrado no banco de dados local.");
            System.out.println("Deseja:");
            System.out.println("1. Tentar outro nome");
            System.out.println("2. Cadastrar este artista");
            System.out.println("3. Voltar ao menu");
            System.out.print("Opção: ");

            var opcao = scan.nextLine();
            switch (opcao) {
                case "1":
                    pesquisarDadosDoArtista();
                    break;
                case "2":
                    cadastrarArtistas();
                    break;
                default:
            }
        }
    }

    private void buscarMusicasPorArtista() {
        System.out.println("Informe o nome do artista: ");
        var nome = scan.nextLine();
        List<Musica> musicas = repositorio.buscaMusicasPorArtista(nome);
        if (musicas.isEmpty()) {
            System.out.println("Nenhuma musica encontrada!");
        } else {
            musicas.forEach(System.out::println);
        }
    }

    private void listarMusicas() {
        List<Artista> artistas = repositorio.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastrar música de qual artista?");
        var nome = scan.nextLine();
        Optional<Artista> artista = repositorio.findByNomeContainingIgnoreCase(nome);
        if (artista.isPresent()) {
            System.out.println("Informe o nome da música: ");
            var nomeMusica = scan.nextLine();
            Musica musica = new Musica(nomeMusica);
            musica.setArtista(artista.get());
            artista.get().getMusicas().add(musica);
            repositorio.save(artista.get());
        } else {
            System.out.println("Artista nao cadastrado!");
        }
    }

    private void cadastrarArtistas() {
        var cadastrarNovo = "s";
        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Digite o nome do artista: ");
            var nome = scan.nextLine();
            System.out.println("Digite o tipo do artista: (solo, dupla ou banda)");
            var tipo = scan.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase());

            var artista = new Artista(nome, tipoArtista);
            repositorio.save(artista);
            System.out.println("Artista cadastrado com sucesso!");
            System.out.println("Cadastrar novo artista? (s/n)");
            cadastrarNovo = scan.nextLine();
        }
    }
}
