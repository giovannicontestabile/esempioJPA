package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.service.EsempioService;
import com.example.demo.entity.Esempio;
import com.example.demo.repository.EsempioRepository;

@Service
public class EsempioServiceImpl implements EsempioService {

    //inizializzare il repository di esempio

    private final EsempioRepository esempioRepository;

    public EsempioServiceImpl(EsempioRepository esempioRepository) {
        this.esempioRepository = esempioRepository;
    }

    public List<Esempio> ottieniTuttiGliEsempi() {
        return esempioRepository.findAll();
    }

    public Esempio ottieniEsempioPerId(Long id) {
        return esempioRepository.findById(id).orElse(null);
    }

    public Esempio salvaEsempio(Esempio esempio) {
        return esempioRepository.save(esempio);
    }

    public void eliminaEsempio(Long id) {
        esempioRepository.deleteById(id);
    }

    public Esempio aggiornaEsempio(Long id, Esempio esempio) {
        if (esempioRepository.existsById(id)) {
            esempio.setId(id);
            return esempioRepository.save(esempio);
        }
        return null;
    }

}
