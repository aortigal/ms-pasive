package com.bank.mspasive.services.impl;

import com.bank.mspasive.handler.ResponseHandler;
import com.bank.mspasive.models.dao.PasiveDao;
import com.bank.mspasive.models.documents.Parameter;
import com.bank.mspasive.models.documents.Pasive;
import com.bank.mspasive.models.utils.Mont;
import com.bank.mspasive.services.ParameterService;
import com.bank.mspasive.services.PasiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PasiveServiceImpl implements PasiveService {
    @Autowired
    private PasiveDao dao;

    @Autowired
    private ParameterService parameterService;
    private static final Logger log = LoggerFactory.getLogger(PasiveServiceImpl.class);

    @Override
    public Mono<ResponseHandler> create(Pasive p) {
        log.info("[INI] Create Pasive");
        p.setCreatedDate(LocalDateTime.now());
        return dao.save(p)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .map(pasive -> new ResponseHandler("Done", HttpStatus.OK, pasive))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] Create Pasive"));
    }

    @Override
    public Mono<ResponseHandler> setMontData(String id,Mont m) {
        log.info("[INI] setMont Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .flatMap(p -> {
                    p.setMont(p.getMont()-m.getMont());
                    return dao.save(p)
                            .map(pasive -> new ResponseHandler("Done", HttpStatus.OK, null));
                })
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] setMont Pasive"));
    }

    @Override
    public Mono<ResponseHandler> findAll() {
        log.info("[INI] FindAll Pasive");

        return dao.findAll()
                .doOnNext(person -> log.info(person.toString()))
                .collectList().map(pasives -> new ResponseHandler("Done", HttpStatus.OK, pasives))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] FindAll Pasive"));
    }

    @Override
    public Mono<ResponseHandler> find(String id) {
        log.info("[INI] Find Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .map(pasive -> new ResponseHandler("Done", HttpStatus.OK, pasive))
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Pasive"));
    }

    @Override
    public Mono<ResponseHandler> getMontData(String id) {
        log.info("[INI] getMontData Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .map(pasive -> {
                    Mont mont = new Mont();
                    mont.setMont(pasive.getMont());
                    mont.setIdPasive(id);

                    return new ResponseHandler("Done", HttpStatus.OK, mont);
                })
                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Pasive"));
    }

    @Override
    public Mono<ResponseHandler> update(String id, Pasive p) {
        log.info("[INI] update Pasive");
        return dao.existsById(id).flatMap(check -> {
                    if (check){
                        p.setUpdateDate(LocalDateTime.now());
                        return dao.save(p)
                                .doOnNext(pasive -> log.info(pasive.toString()))
                                .map(pasive -> new ResponseHandler("Done", HttpStatus.OK, pasive))
                                .onErrorResume(error -> Mono.just(new ResponseHandler(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
                    }
                    else
                        return Mono.just(new ResponseHandler("Not found", HttpStatus.NOT_FOUND, null));

                })
                .doFinally(fin -> log.info("[END] Update Pasive"));
    }

    @Override
    public Mono<ResponseHandler> delete(String id) {
        log.info("[INI] delete Pasive");
        return dao.existsById(id).flatMap(check -> {
                    if (check)
                        return dao.deleteById(id).then(Mono.just(new ResponseHandler("Done", HttpStatus.OK, null)));
                    else
                        return Mono.just(new ResponseHandler("Not found", HttpStatus.NOT_FOUND, null));
                })
                .doFinally(fin -> log.info("[END] Delete Pasive"));
    }

    @Override
    public Mono<ResponseHandler> findByCode(String id) {
        log.info("[INI] findByCode Type Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .flatMap(pasive ->
                        {
                            return parameterService.findByCode(pasive.getPasivesType().getValue())
                                    .doOnNext(responseParameter -> log.info(responseParameter.toString()))
                                    .flatMap(responseParameter ->
                                    {
                                        List<Parameter> listParameter = responseParameter.getData();

                                        if(!listParameter.isEmpty())
                                        {

                                            listParameter.forEach(parameter -> {
                                                if(parameter.getValue().equals("2") && parameter.getArgument().equals("true"))
                                                {
                                                    LocalDateTime localDateTime = pasive.getSpecificDay().toInstant()
                                                            .atZone(ZoneId.systemDefault())
                                                            .toLocalDate().atStartOfDay();
                                                    parameter.setArgument(localDateTime.getDayOfMonth()+"");
                                                }
                                            });


                                            return Mono.just(new ResponseHandler("Done", HttpStatus.OK, listParameter));
                                        }
                                        else
                                        {
                                            return Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null));
                                        }
                                    });
                        }
                )
                .switchIfEmpty(Mono.just(new ResponseHandler("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Type Pasive"));

    }
}
