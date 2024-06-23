package com.aluracursos.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double evaluacion;
    private LocalDate fechaDeLanzamniemto;

    public Episodio(Integer numeroEpisodio, String evaluacion, String fechaLanzamiento, String titulo, Integer temporada) {
        this.numeroEpisodio = numeroEpisodio;
        this.titulo = titulo;
        this.temporada = temporada;

        try {
            this.evaluacion = Double.valueOf(evaluacion);
            this.fechaDeLanzamniemto = LocalDate.parse(fechaLanzamiento);

        } catch (NumberFormatException e) {
            this.evaluacion = 0.0;
        } catch (DateTimeParseException e) {
            this.fechaDeLanzamniemto = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public LocalDate getFechaDeLanzamniemto() {
        return fechaDeLanzamniemto;
    }

    public void setFechaDeLanzamniemto(LocalDate fechaDeLanzamniemto) {
        this.fechaDeLanzamniemto = fechaDeLanzamniemto;
    }

    @Override
    public String toString() {
        return "Episodio{" +
                "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", evaluacion=" + evaluacion +
                ", fechaDeLanzamniemto=" + fechaDeLanzamniemto +
                '}';
    }
}
