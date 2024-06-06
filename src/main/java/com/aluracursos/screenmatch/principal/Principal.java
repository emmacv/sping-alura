package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    final private Scanner stdin = new Scanner(System.in);
    private ConsumoAPI consumer = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?apikey=d5bee84b";
    private ConvierteDatos conversor = new ConvierteDatos();
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
    }
}
