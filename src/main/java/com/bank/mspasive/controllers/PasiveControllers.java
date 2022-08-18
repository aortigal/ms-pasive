package com.bank.mspasive.controllers;

import com.bank.mspasive.handler.ResponseHandler;
import com.bank.mspasive.models.documents.Pasive;
import com.bank.mspasive.models.utils.Mont;
import com.bank.mspasive.services.PasiveService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/pasive")
public class PasiveControllers {

    @Autowired
    private PasiveService pasiveService;


    @PostMapping
    public Mono<ResponseHandler> create(@Valid @RequestBody Pasive p) {
        return pasiveService.create(p);
    }

    @PostMapping("/mont/{id}")
    public Mono<ResponseHandler> setMontData(@PathVariable String id, @RequestBody Mont m) {
        return pasiveService.setMontData(id, m);
    }

    @GetMapping
    public Mono<ResponseHandler> findAll() {
        return pasiveService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseHandler> find(@PathVariable String id) {
        return pasiveService.find(id);
    }

    @GetMapping("/mont/{id}")
    public Mono<ResponseHandler> getMontData(@PathVariable String id) {
        return getMontData(id);
    }

    @PutMapping("/{id}")
    public Mono<ResponseHandler> update(@PathVariable("id") String id,@Valid @RequestBody Pasive p) {
        return pasiveService.update(id, p);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseHandler> delete(@PathVariable("id") String id) {
        return pasiveService.delete(id);
    }

    @GetMapping("/type/{id}")
    @CircuitBreaker(name="parameter", fallbackMethod = "fallBackParameter")
    public Mono<ResponseHandler> findByCode(@PathVariable String id) {
        return pasiveService.findByCode(id);

    }

    public Mono<ResponseHandler> fallBackParameter(RuntimeException runtimeException){
        return Mono.just(new ResponseHandler("Microservicio Parameter no responde",HttpStatus.BAD_REQUEST,runtimeException.getMessage() ));
    }
}
