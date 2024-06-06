package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.model.DatosSerie;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IConvierteDatos {
    public <T> T obtenerDatos(String json, Class<T> clase) throws JsonProcessingException;
}
