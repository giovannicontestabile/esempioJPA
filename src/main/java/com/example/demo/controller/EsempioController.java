package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EsempioDTO;
import com.example.demo.entity.Esempio;
import com.example.demo.service.EsempioService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/esempio")
@CrossOrigin(origins = "http://localhost:5173")
public class EsempioController {
    private final EsempioService esempioService;

    public EsempioController(EsempioService esempioService) {
        this.esempioService = esempioService;
    }

    @PostMapping    
    public ResponseEntity<EsempioDTO> creaEsempio(@RequestBody EsempioDTO esempioDTO) {
        Esempio esempio = new Esempio();
        esempio.setTesto(esempioDTO.getTesto());
        Esempio nuovoEsempio = esempioService.salvaEsempio(esempio);
        EsempioDTO nuovoEsempioDTO = new EsempioDTO(nuovoEsempio.getId(), nuovoEsempio.getTesto());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuovoEsempioDTO);
    }

    @GetMapping
    public ResponseEntity<List<EsempioDTO>> ottieniTuttiGliEsempi(){
        List<Esempio> esempi = esempioService.ottieniTuttiGliEsempi();
        List<EsempioDTO> esempioDTOs = esempi.stream()
            .map(esempio -> new EsempioDTO(esempio.getId(), esempio.getTesto()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(esempioDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EsempioDTO> ottieniEsempioPerId(@PathVariable Long id) {
        Esempio esempio = esempioService.ottieniEsempioPerId(id);
        if (esempio != null) {
            EsempioDTO esempioDTO = new EsempioDTO(esempio.getId(), esempio.getTesto());
            return ResponseEntity.ok(esempioDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaEsempio(@PathVariable Long id) {
        try {
        esempioService.eliminaEsempio(id);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<EsempioDTO> aggiornaEsempio(@RequestBody EsempioDTO esemmpioDTO) {
        Esempio esempio = esempioService.ottieniEsempioPerId(esemmpioDTO.getId());
        if (esempio == null) {
            return ResponseEntity.notFound().build();
        }
        esempio.setTesto(esemmpioDTO.getTesto());
        Esempio esempioAggiornato = esempioService.aggiornaEsempio(esempio.getId(), esempio);
        EsempioDTO esempioAggiornatoDTO = new EsempioDTO(esempioAggiornato.getId(), esempioAggiornato.getTesto());
        return ResponseEntity.ok(esempioAggiornatoDTO);
    }
    
    
}
