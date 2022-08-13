package com.bank.mspasive.models.dao;

import com.bank.mspasive.models.documents.Pasive;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PasiveDao extends ReactiveMongoRepository<Pasive, String> {

}
