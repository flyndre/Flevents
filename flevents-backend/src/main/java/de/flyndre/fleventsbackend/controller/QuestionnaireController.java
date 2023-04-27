package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.controllerServices.QuestionnaireControllerService;
import de.flyndre.fleventsbackend.dtos.questionaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.dtos.questionaire.Questionnaire;
import de.flyndre.fleventsbackend.dtos.questionnaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

//todo: implement authorization
@RestController
@CrossOrigin
@RequestMapping("/api/questionnaires")
public class QuestionnaireController {

    private final QuestionnaireControllerService questionnaireControllerService;
    private final ModelMapper mapper;

    public QuestionnaireController(QuestionnaireControllerService questionnaireControllerService, ModelMapper mapper) {
        this.questionnaireControllerService = questionnaireControllerService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity getQuestionnaires(@RequestParam String eventId, Authentication auth){
        if(!questionnaireControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(questionnaireControllerService.getQuestionnaires(eventId).stream().map(questionnaire -> mapper.map(questionnaire, Questionnaire.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{questionnaireId}")
    public ResponseEntity getQuestionnaire(@PathVariable String questionnaireId, Authentication auth){
        //TODO: hier fehlt auth
       return new ResponseEntity<>(mapper.map(questionnaireControllerService.getQuestionnaire(questionnaireId), Questionnaire.class),HttpStatus.OK);
    }

    @GetMapping("/{questionnaireId}/answers/{userId}")
    public ResponseEntity getAnswers(@PathVariable String questionnaireId,@PathVariable String userId, Authentication auth){
        //TODO: hier fehlt auth

        return new ResponseEntity<>(mapper.map(questionnaireControllerService.getAnswerFromUser(questionnaireId, userId), AnsweredQuestionnaire.class),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createQuestionnaire(@RequestParam String eventId,@RequestBody Questionnaire bodyQuestionnaire, Authentication auth){
        //TODO: hier fehlt auth

        return new ResponseEntity<>(mapper.map(questionnaireControllerService.createQuestionnaire(eventId, bodyQuestionnaire), Questionnaire.class), HttpStatus.CREATED);
    }
    @PostMapping("/{questionnaireId}")
    public ResponseEntity editQuestionnaire(@PathVariable String questionnaireId,@RequestBody Questionnaire bodyQuestionnaire, Authentication auth){
        //TODO: hier fehlt auth
        return new ResponseEntity<>(mapper.map(questionnaireControllerService.editQuestionnaire(questionnaireId, bodyQuestionnaire), Questionnaire.class), HttpStatus.OK);
    }

    @DeleteMapping("/{questionnaireId}")
    public ResponseEntity deleteQuestionnaire(@PathVariable String questionnaireId, Authentication auth) {
        //TODO: hier fehlt auth
        questionnaireControllerService.deleteQuestionnaire(questionnaireId);
        return new ResponseEntity<>("Deleted.", HttpStatus.ACCEPTED);
    }

    @PostMapping("/{questionnaireId}/answers")
    public ResponseEntity addAnswer(@PathVariable String questionnaireId,@RequestBody AnsweredQuestionnaire answeredQuestionnaire, Authentication auth){
        //TODO: hier fehlt auth
        questionnaireControllerService.addAnswer(questionnaireId, answeredQuestionnaire);
        return new ResponseEntity(HttpStatus.OK);
    }
}
