/*package com.aluracursos.screenmatch.principal;

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
        ).toList();
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
        public void mostrarMenu() throws JsonProcessingException {
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
    }
}
*/
package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "TU-APIKEY-OMDB";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar Series por categoría
                    7 - filtrar series por temporadas y evaluación
                    8 - Buscar episodios por titulo
                    9 - Top 5 episodios por Serie
                                  
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
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

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
        System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }



    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }

    }
    private void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()) );
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Escriba el genero/categoría de la serie que desea buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoría " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }
    public void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemparadaYEvaluacion(totalTemporadas,evaluacion);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    private void  buscarEpisodiosPorTitulo(){
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));

    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();

        serieBuscada.ifPresent(this::printFoundEpisodios);

        if (serieBuscada.isEmpty()){
            System.out.println("Serie no encontrada");
        }
    }

    private void printFoundEpisodios(Serie serie) {
        List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
        topEpisodios.forEach(e ->
                System.out.printf("Serie: %s - Temporada %s - Episodio %s - Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
    }
}

