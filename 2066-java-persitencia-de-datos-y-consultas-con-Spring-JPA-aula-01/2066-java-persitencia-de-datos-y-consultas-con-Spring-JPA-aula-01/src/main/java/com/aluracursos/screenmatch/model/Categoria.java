package com.aluracursos.screenmatch.model;

public enum Categoria {
    ACCION("Action" , "accion") ,
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen");

    private String categoriaOmdb;
    private String categoriaEspniol;
    Categoria (String categoriaOmdb, String categoriaEspaniol){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspniol=categoriaEspaniol;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
    public static Categoria fromEspaniol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspniol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
