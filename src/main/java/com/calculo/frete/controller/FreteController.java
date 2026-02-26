package com.calculo.frete.controller;

import com.calculo.frete.entity.Cotacao;
import com.calculo.frete.service.FreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    @Autowired
    private FreteService service;

    @PostMapping("/calcular")
    public ResponseEntity<Cotacao> calcular(@RequestBody Cotacao request) {
        // Ajustei para chamar o método calcularFrete que criamos no Service
        return ResponseEntity.ok(service.calcularFrete(request));
    }
}