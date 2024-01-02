package com.LaboratorioIntegrato.DatabasePopulator.service;

import com.LaboratorioIntegrato.DatabasePopulator.interfaces.interfaccia_squadre;
import com.LaboratorioIntegrato.DatabasePopulator.model.api.teams_venues.Response_venue;
import com.LaboratorioIntegrato.DatabasePopulator.model.api.teams_venues.Root_venue;
import com.LaboratorioIntegrato.DatabasePopulator.model.api.teams_venues.Team;
import com.LaboratorioIntegrato.DatabasePopulator.model.db.Squadra;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.lang.module.Configuration;
import java.util.Arrays;
import java.util.List;
@Service

public class SquadraService {

    @Autowired
    WebClient webClient;
    interfaccia_squadre interfacciasquadre;
    StadioService stadioService;

    @Autowired
    public SquadraService(interfaccia_squadre interfacciasquadre, StadioService stadioService) {
        this.interfacciasquadre = interfacciasquadre;
        this.stadioService = stadioService;

    }




    public List<Squadra> RitornaSquadre()
    {
        Squadra[] squadreA = interfacciasquadre.RitornaSquadre();
        return Arrays.stream(squadreA).toList();
    }




    public List<Response_venue> getSquadreStadi()
    {
        Flux<Root_venue> SquadreStadiFlux = webClient.get()
                .uri("https://v3.football.api-sports.io/teams?league=135&season=2022")
                .retrieve()
                .bodyToFlux(Root_venue.class);
        List<Root_venue> tutto = SquadreStadiFlux.collectList().block();
        Root_venue root = tutto.get(0);
        List<Response_venue> risposta = root.response;
        return risposta;
    }

    public ResponseEntity<?> SquadretadioSplit()
    {
        try {

            List<Response_venue> risposta = getSquadreStadi();
            stadioService.MettiStadi(risposta);

            MettiSquadre(risposta);

            return new ResponseEntity<>(true,HttpStatus.OK);

        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }


    }


    public void MettiSquadre(@NotNull List<Response_venue> risposta) {

            for (Response_venue risp : risposta) {
                Team team = risp.team;
                interfacciasquadre.InserisciSquadra(team.id, team.name, team.code,risp.venue.id, team.country, team.founded,team.logo);
            }
    }



}
