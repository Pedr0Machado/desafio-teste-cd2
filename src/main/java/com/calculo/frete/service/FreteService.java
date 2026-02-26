package com.calculo.frete.service;
import com.calculo.frete.cliente.ViaCepDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.calculo.frete.entity.Cotacao;
import com.calculo.frete.repository.CotacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class FreteService {

    @Autowired
    private CotacaoRepository repository;

    public Cotacao calcularFrete(Cotacao cotacao) {
        // 1. Consultar ViaCEP com tratamento de erro
        ViaCepDTO origem = consultarCep(cotacao.getCepOrigem());
        ViaCepDTO destino = consultarCep(cotacao.getCepDestino());

        // 2. Lógica base: R$ 1,00 por KG
        double valorFrete = cotacao.getPeso() * 1.0;
        int diasEntrega = 10; // Padrão: Estados diferentes

        // 3. Aplicar Regras de Negócio
        // Prioridade 1: DDDs iguais (50% de desconto)
        if (origem.getDdd() != null && origem.getDdd().equals(destino.getDdd())) {
            valorFrete *= 0.50;
            diasEntrega = 1;
        } 
        // Prioridade 2: Estados iguais (75% de desconto)
        else if (origem.getUf() != null && origem.getUf().equals(destino.getUf())) {
            valorFrete *= 0.25; 
            diasEntrega = 3;
        }

        // 4. Preencher dados calculados
        cotacao.setVlTotalFrete(valorFrete);
        cotacao.setDataPrevistaEntrega(LocalDate.now().plusDays(diasEntrega));
        cotacao.setDataConsulta(LocalDateTime.now());

        // 5. Salvar no Banco de Dados
        return repository.save(cotacao);
    }

    private ViaCepDTO consultarCep(String cep) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Remove qualquer traço ou espaço que o usuário possa ter enviado
            String cepLimpo = cep.replaceAll("[^0-9]", "");
            String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
            
            ViaCepDTO dados = restTemplate.getForObject(url, ViaCepDTO.class);

            if (dados == null || dados.getCep() == null) {
                throw new RuntimeException("CEP não encontrado: " + cep);
            }
            return dados;
        } catch (Exception e) {
            // Isso evita o erro 500 genérico e ajuda a identificar o problema no log
            throw new RuntimeException("Erro ao consultar o CEP " + cep + ": " + e.getMessage());
        }
    }
}