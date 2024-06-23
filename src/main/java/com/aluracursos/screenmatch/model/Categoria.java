package com.aluracursos.screenmatch.model;

import java.util.Arrays;
import java.util.stream.Stream;

public enum Categoria {
    ACCION("Action"),
    COMEDIA("Comedy"),
    ROMANCE("Romance"),
    DRAMA("Drama"),
    CRIMEN("Comady"),
    NA("N/A");

    private String categoriaOMDB;

    Categoria (String categoriaOMDB) {
        this.categoriaOMDB = categoriaOMDB;
    }

    public static Categoria fromString(String categoria) {
        final var categoriaStream = Arrays.stream(Categoria.values())
                .filter(c -> categoria.equalsIgnoreCase(categoria))
                .findFirst()
                .orElse(Categoria.valueOf("NA"));

        return categoriaStream;
    }
}
