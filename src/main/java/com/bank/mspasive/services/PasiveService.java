package com.bank.mspasive.services;

import com.bank.mspasive.handler.ResponseHandler;
import com.bank.mspasive.models.documents.Pasive;
import com.bank.mspasive.models.utils.Mont;
import reactor.core.publisher.Mono;

public interface PasiveService {
    Mono<ResponseHandler> create(Pasive p);

    Mono<ResponseHandler> setMontData(String id, Mont m);

    Mono<ResponseHandler> findAll();

    Mono<ResponseHandler> find(String id);

    Mono<ResponseHandler> getMontData(String id);

    Mono<ResponseHandler> update(String id, Pasive p);

    Mono<ResponseHandler> delete(String id);

    Mono<ResponseHandler> findByCode(String id);
}
