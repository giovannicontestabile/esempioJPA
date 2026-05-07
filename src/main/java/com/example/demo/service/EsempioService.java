package com.example.demo.service;

import com.example.demo.entity.Esempio;
import java.util.List;

public interface EsempioService {

    public List<Esempio> ottieniTuttiGliEsempi();

    public Esempio ottieniEsempioPerId(Long id);

    public Esempio salvaEsempio(Esempio esempio);

    public void eliminaEsempio(Long id);

    public Esempio aggiornaEsempio(Long id, Esempio esempio);
} 
    

