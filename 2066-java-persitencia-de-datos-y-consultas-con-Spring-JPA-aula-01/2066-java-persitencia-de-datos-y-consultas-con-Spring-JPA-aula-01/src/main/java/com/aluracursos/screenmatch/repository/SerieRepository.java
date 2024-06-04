package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie,Long> {
    //derive query se utilizan palabras claves en ingles que identifica JPA para acceder a la informacion de la base de datos verbo introductorio + palabra clave "By" + criterios de búsqueda
    //Como verbos introductorios, tenemos find, read, query, count y get.
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();

    List<Serie> findByGenero(Categoria genero);

    List<Serie> findByTotalTemporadasAndEvaluacionGreaterThanEqual(int numeroTemporadas, double evaluacionMinima);


    //query nativa
    @Query(value = "select * from series where series.total_temporadas<=10 and series.evaluacion>=6", nativeQuery = true)
    List<Serie> seriesPorTemporadaYEvaluacion();

    //query JPQL  (s es el apodo de la variable Serie)
    @Query("select s from Serie s where s.totalTemporadas<= :totalTemporada and s.evaluacion>=:evaluacion")
    List<Serie> seriesPorTemporadaYEvaluacion(int totalTemporada, Double evaluacion);


    //JOIN relacion inteserccion entre uno y otro
    @Query("SELECT episodio FROM Serie serie JOIN serie.episodios episodio WHERE episodio.titulo ILIKE %:nombreEpisodios%")
    List<Episodio> episodiosPorNombre( String nombreEpisodios);

    @Query("select  episodio from Serie serie join serie.episodios episodio where serie= :serie  order by episodio.evaluacion desc limit  5")
    List<Episodio> to5Episodios(Serie serie);
}


