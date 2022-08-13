package com.bank.mspasive.controllers;

import com.bank.mspasive.handler.ResponseHandler;
import com.bank.mspasive.models.dao.PasiveDao;
import com.bank.mspasive.models.documents.Parameter;
import com.bank.mspasive.models.documents.Pasive;
import com.bank.mspasive.models.utils.Mont;
import com.bank.mspasive.services.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@RestController
@RequestMapping("/api/pasive")
public class PasiveControllers {

    @Autowired
    private PasiveDao dao;

    @Autowired
    private ParameterService parameterService;

    private static final Logger log = LoggerFactory.getLogger(PasiveControllers.class);


    @PostMapping
    public Mono<ResponseEntity<Object>> Create(@Valid @RequestBody Pasive p) {
        log.info("[INI] Create Pasive");
        p.setCreatedDate(LocalDateTime.now());
        return dao.save(p)
                .doOnNext(pasive -> log.info(pasive.toString()))
                        .map(pasive -> ResponseHandler.response("Done", HttpStatus.OK, pasive))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] Create Pasive"));
    }

    @PostMapping("/mont/{id}")
    public Mono<ResponseEntity<Object>> setMontData(@PathVariable String id, @RequestBody Mont m) {
        log.info("[INI] setMont Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .flatMap(p -> {
                            p.setMont(p.getMont()-m.getMont());
                            return dao.save(p)
                                    .map(pasive -> ResponseHandler.response("Done", HttpStatus.OK, null));
                        })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] setMont Pasive"));
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> FindAll() {
        log.info("[INI] FindAll Pasive");

        return dao.findAll()
                .doOnNext(person -> log.info(person.toString()))
                .collectList().map(pasives -> ResponseHandler.response("Done", HttpStatus.OK, pasives))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] FindAll Pasive"));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> Find(@PathVariable String id) {
        log.info("[INI] Find Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .map(pasive -> ResponseHandler.response("Done", HttpStatus.OK, pasive))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Pasive"));
    }

    @GetMapping("/mont/{id}")
    public Mono<ResponseEntity<Object>> getMontData(@PathVariable String id) {
        log.info("[INI] Find Pasive");
        return dao.findById(id)
                .doOnNext(pasive -> log.info(pasive.toString()))
                .map(pasive -> {
                    Mont mont = new Mont();
                    mont.setMont(pasive.getMont());
                    mont.setIdPasive(id);

                    return ResponseHandler.response("Done", HttpStatus.OK, mont);
                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Pasive"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> Update(@PathVariable("id") String id,@Valid @RequestBody Pasive p) {
        log.info("[INI] Update Pasive");
        return dao.existsById(id).flatMap(check -> {
            if (check){
                p.setUpdateDate(LocalDateTime.now());
                return dao.save(p)
                        .doOnNext(pasive -> log.info(pasive.toString()))
                        .map(pasive -> ResponseHandler.response("Done", HttpStatus.OK, pasive))
                        .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)));
            }
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));

        })
        .doFinally(fin -> log.info("[END] Update Pasive"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> Delete(@PathVariable("id") String id) {
        log.info("[INI] Delete Pasive");
        return dao.existsById(id).flatMap(check -> {
            if (check)
                return dao.deleteById(id).then(Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)));
            else
                return Mono.just(ResponseHandler.response("Not found", HttpStatus.NOT_FOUND, null));
        })
        .doFinally(fin -> log.info("[END] Delete Pasive"));
    }

    @GetMapping("/type/{id}")
    public Mono<ResponseEntity<Object>> FindType(@PathVariable String id) {
        log.info("[INI] Find Type Pasive");
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


                                        return Mono.just(ResponseHandler.response("Done", HttpStatus.OK, listParameter));
                                    }
                                    else
                                    {
                                        return Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null));
                                    }
                                });
                    }
                )
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find Type Pasive"));

    }


}
