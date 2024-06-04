package com.aluracursos.screenmatch.model;

import com.aluracursos.screenmatch.service.ConsultaChatGPT;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;
@Entity // mapea la clase perimite CRUD
@Table(name="series")// especificar el nombre de la tabla se puede llamar diferente
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// hace que se vaya aumentando el id
    private Long Id; // indentifiacoms las instancias, busqueda eficiente,
    @Column(unique = true)// hace que el atributo titulo sea unico
    private String titulo;
    private Integer totalTemporadas;
    private Double evaluacion;
    private String poster;
    @Enumerated(EnumType.STRING) // especificamos que el atributo genero es un enumerado y se alamcena como string
    private Categoria genero;
    private String actores;
    private String sinopsis;
   // @Transient // ignora la lista y no la mapea
    @OneToMany(mappedBy = "serie", cascade =CascadeType.ALL ,fetch = FetchType.EAGER)// cambios es cascada (all tien todos las acciones refrehs, deletec , select  ), carga lso datos de forma anciosa
    private List<Episodio> episodios;

    public Serie(DatosSerie datosSerie){
        this.titulo = datosSerie.titulo();
        this.totalTemporadas = datosSerie.totalTemporadas();
        this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0);
        this.poster = datosSerie.poster();
        this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
        this.actores = datosSerie.actores();
        this.sinopsis = datosSerie.sinopsis(); //ConsultaChatGPT.obtenerTraduccion(datosSerie.sinopsis()) ; consulta en chatgpt
    }

    public Serie() {

    }

    @Override
    public String toString() {
        return  "genero=" + genero +
                "titulo='" + titulo + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", evaluacion=" + evaluacion +
                ", poster='" + poster + '\'' +
                ", actores='" + actores + '\'' +
                ", sinopsis='" + sinopsis + '\''+
                "episodios= "+episodios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getActores() {
        return actores;
    }

    public void setActores(String actores) {
        this.actores = actores;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(episodio->episodio.setSerie(this));
        this.episodios = episodios;
    }
}
