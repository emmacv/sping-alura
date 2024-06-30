package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.reposotory.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    final private Scanner stdin = new Scanner(System.in);
    private ConsumoAPI consumer = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?apikey=d5bee84b&type=series";

    private final String API_KEY = "d5bee84b";
    private ConvierteDatos conversor = new ConvierteDatos();

    private List<DatosSerie> datosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() throws JsonProcessingException {
        var opcion = -1;

        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                                  
                    0 - Salir
                """;
            System.out.println(menu);
            opcion = stdin.nextInt();
            stdin.nextLine();

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
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private void mostrarSeriesBuscadas() {
        List<Serie> series = /*datosSeries.stream().map(
                Serie::new
        ).toList();*/
                repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() throws JsonProcessingException {
        DatosSerie datos  = getDatosSerie();
        datosSeries.add(datos);
        Serie serie = new Serie(datos);

        repositorio.save(serie);

        System.out.println(datos);
    }

    private DatosSerie getDatosSerie() throws JsonProcessingException {
        System.out.println("Escribe el nombre de la serie que deseas buscar");

        var nombreSerie = stdin.nextLine();
        String url = URL_BASE + "&t=" + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8);
        System.out.println(url);
        var json = consumer.obtenerDatos(url);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);

        return datos;
    }

    private void buscarEpisodioPorSerie() throws JsonProcessingException {
        DatosSerie datosSerie = getDatosSerie();
        List<DatosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= datosSerie.totalTemporadas(); i++) {
            var json = consumer.obtenerDatos(URL_BASE + datosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }
        temporadas.forEach(System.out::println);
    }
/*    public void mostrarMenu() throws JsonProcessingException {
        System.out.print("Type the name of the series you want to search for: ");

        String nombreSerie = stdin.nextLine();
        final String finalUrl = URL_BASE + "&t=" + URLEncoder.encode(nombreSerie, StandardCharsets.UTF_8);

        String json = consumer.obtenerDatos(finalUrl);
        System.out.println(json);

        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        List<DatosTemporada> datosTemporedasList = new ArrayList();

        for (int i = 1; i <= datos.totalTemporadas(); i++) {
            json = consumer.obtenerDatos(
                    finalUrl + "&season=" + i
            );
            datosTemporedasList.add(conversor.obtenerDatos(json, DatosTemporada.class));
        }

        datosTemporedasList.forEach(System.out::println);

        datosTemporedasList.forEach(t -> {
            var episodios = t.episodios();
            System.out.println("list of episodes:");

            episodios.forEach(e -> {
                System.out.println(e.titulo());
            });
        });

        List<DatosEpisodio>  datosEpisodio = datosTemporedasList
                .stream().
                flatMap(t -> t.episodios().stream())
                .toList();

        datosEpisodio
            .stream()
            .filter(e -> !e.evaluacion().equals("N/A"))
            .peek(t -> System.out.println("filtro: " + t))
            .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
            .peek(t -> System.out.println("evaluación: " + t.evaluacion()))
            .map(e -> e.titulo().toUpperCase())
            .peek(t -> System.out.println("Capitalizar: " + t))
            .limit(5)
            .forEach(e -> System.out.println("value: " + e));

            List<Episodio> episodios = datosTemporedasList
                .stream()
                .flatMap(t -> t.episodios()
                        .stream()
                        .map(e -> new Episodio(
                                e.numeroEpisodio(),
                                e.evaluacion(),
                                e.fechaDeLanzamiento(),
                                e.titulo(),
                                t.numero()
                        ))
                ).toList();

            episodios.forEach(System.out::println);

        System.out.println("Enter a date");

        var fecha = stdin.nextInt();
        stdin.nextLine();
        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream().
            filter(e -> e.getFechaDeLanzamniemto() != null && e.getFechaDeLanzamniemto().isAfter(fechaBusqueda))
            .forEach(e -> {
                System.out.println("Temporada: " + e.getTemporada() + "Fecha lanzamiento: " + e.getFechaDeLanzamniemto().format(dtf));
            });

*//*
        System.out.println("Ingrese una porción del título de episodio: ");
        String piece = stdin.nextLine();

        final var first = episodios.stream()
            .filter(e -> {
                var titulo = e.getTitulo();

                return titulo.contains(piece.toLowerCase());
            })
            .findFirst();

        final var s = first.map(e -> e.getTitulo()).orElse("not found");
        System.out.println(s);
*//*

        Map<Integer, Double> evaluacionesPorTemporada = episodios
                .stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(
                        Collectors.groupingBy(
                                Episodio::getTemporada,
                                Collectors.averagingDouble(Episodio::getEvaluacion)
                        )
                );

        System.out.println(evaluacionesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
            .filter(e -> e.getEvaluacion() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));


        System.out.println("Media: " + est.getAverage());
        System.out.println("Peor evalluado: " + est.getMin());
        System.out.println("Mejor evaluado: " + est.getMax());
    }*/
}
