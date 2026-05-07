package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import com.example.demo.entity.Esempio;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface EsempioRepository extends JpaRepository<Esempio, Long> {
    public Esempio findByTesto(String testo);
    
}
