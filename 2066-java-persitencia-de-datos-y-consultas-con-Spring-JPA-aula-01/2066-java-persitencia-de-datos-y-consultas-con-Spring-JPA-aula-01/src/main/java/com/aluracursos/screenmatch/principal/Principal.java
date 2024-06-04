package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    //La documentación de Spring Data JPA está en el enlace.
    //
    //link: https://spring.io/projects/spring-data-jpa
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=67522d18";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo 
                    5 - top 5 mejores series
                    6 - Busqueda por categoria
                    7 - Busqueda por cantTemporada y evaluacion minima
                    8 - BUsqueda de episodios por titulo
                    9- Top 5 episosdios de serie
                             
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    buscarSeriePorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioporTitulo();
                    break;
                case 9:
                    top5PorSerie();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private void top5PorSerie() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie= serieBuscada.get();
            List<Episodio>topEpisodios= repository.to5Episodios(serie);
            topEpisodios.forEach(episodio -> System.out.printf("serie %s Temporada %s episodio %s evaluacion %s ", episodio.getSerie().getTitulo(), episodio.getTemporada(), episodio.getNumeroEpisodio(), episodio.getEvaluacion()));

        }
    }

    private void buscarEpisodioporTitulo() {
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(episodio -> System.out.printf("serie %s Temporada %s episodio %s evaluacion %s ", episodio.getSerie().getTitulo(), episodio.getTemporada(), episodio.getNumeroEpisodio(), episodio.getEvaluacion()));
    }

    private void buscarSeriePorTemporadaYEvaluacion() {
        System.out.println("Escribe la cantidad de temporadas de la serie que deseas buscar");
        Integer cantidaTemporadas = Integer.valueOf(teclado.nextLine());
        System.out.println("Escribe la evaluacion minima de la serie que deseas buscar");
        double evaluacion = Double.parseDouble(teclado.nextLine());

        List<Serie> seriesPorTemporadaYEvaluacion = repository.seriesPorTemporadaYEvaluacion(cantidaTemporadas, evaluacion);
        seriesPorTemporadaYEvaluacion.forEach(System.out::println);
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Escribe el genero/categoria de la serie que deseas buscar");
        var genero = teclado.nextLine();
        Categoria categoria = Categoria.fromEspaniol(genero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }


    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escriebe el nombre de la serie que quieres ver los episodios");
        String nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream().filter(serie1 -> serie1.getTitulo().toLowerCase().contains(nombreSerie)).findFirst();
        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();

            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream().flatMap(dato -> dato.episodios().stream().map(episodio -> new Episodio(dato.numero(), episodio))).collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }

    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repository.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repository.findAll();// TRAER TODO LOS DATOS DE LA BASE DE DATOS

        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escriebe el nombre de la serie que quiereas buscar");
        String nombreSerie = teclado.nextLine();
         serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("No se encontro laserie");
        }
    }

    private void buscarTop5Series() {
        List<Serie> topSerie = repository.findTop5ByOrderByEvaluacionDesc();
        topSerie.forEach(serie -> System.out.println("Serie: " + serie.getTitulo() + "\n"));
    }

}

