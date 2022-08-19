package com.bank.mspasive.services.impl;

import com.bank.mspasive.models.documents.Parameter;
import com.bank.mspasive.models.utils.ResponseParameter;
import com.bank.mspasive.services.ParameterService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ParameterServiceImpl implements ParameterService {

    private final WebClient webClient;

    public ParameterServiceImpl(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://localhost:8087").build();
    }

    @Override
    public Mono<ResponseParameter> findByCode(Integer code)
    {
        return webClient.get()
                .uri("/api/parameter/catalogue/"+ code)
                .retrieve()
                .bodyToMono(ResponseParameter.class);
    }

    public List<Parameter> getParameter(List<Parameter> listParameter, Integer code) {
        List<Parameter> list = listParameter.stream().filter(x -> x.getCode().toString().equals(code.toString()) )
                .collect(Collectors.toList());

        return list;
    }
}
