package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    SerieRepository serieRepository;
    public List<SerieDTO> obtenerTodasLasSeries() {
        return this.convierteDatos(serieRepository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return this.convierteDatos(serieRepository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> convierteDatos(List<Serie> s) {
        return s.stream()
        .map(serie -> new SerieDTO(
                serie.getId(),
                serie.getTitulo(),
                serie.getTotalTemporadas(),
                serie.getEvaluacion(),
                serie.getGenero(),
                serie.getActores(),
                serie.getPoster(),
                serie.getSinopsis()
        ))
        .toList();
    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes() {
        return convierteDatos(serieRepository.lanzamientosMasRecientes());
    }

    public SerieDTO obtenerPorId(long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isEmpty()) return null;
        Serie s = serie.get();

        return new SerieDTO(
                s.getId(),
                s.getTitulo(),
                s.getTotalTemporadas(),
                s.getEvaluacion(),
                s.getGenero(),
                s.getActores(),
                s.getPoster(),
                s.getSinopsis()
        );
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(long id) {
        Optional<Serie> serie = serieRepository.findById(id);

        if (serie.isEmpty()) return null;

        Serie s = serie.get();

        return s.getEpisodios()
                .stream()
                .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getTitulo(),
                        e.getNumeroEpisodio()
                ))
                .toList();
    }

    public List<EpisodioDTO> obtenerTemporadasPorNombre(long id, long numeroTemporada) {
        return serieRepository.obtenerTemporadasPorNumero(id, numeroTemporada)
                .stream()
                .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getTitulo(),
                        e.getNumeroEpisodio()
                ))
                .toList();
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero);

        return convierteDatos(serieRepository.findByGenero(categoria));
    }
}
