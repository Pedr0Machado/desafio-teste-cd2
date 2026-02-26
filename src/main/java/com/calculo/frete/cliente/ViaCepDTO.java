package com.calculo.frete.cliente; 

import lombok.Data;

@Data
public class ViaCepDTO {
    private String cep;
    private String uf;
    private String ddd;
}