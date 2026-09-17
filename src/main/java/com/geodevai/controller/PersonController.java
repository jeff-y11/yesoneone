package com.geodevai.controller;

import com.geodevai.data.dto.PersonResponse;
import com.geodevai.data.dto.SearchResponse;
import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Person;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.PersonRepository;
import com.geodevai.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<SearchResponse<PersonResponse>> getPersons(
            @RequestParam(required = false, defaultValue = "") String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        Organization org = user.getOrganization();
        List<Person> persons;

        if (search.isBlank()) {
            persons = personRepository.findByOrganization(org);
        } else {
            persons = personRepository.searchByOrganizationAndQuery(org, search);
        }

        List<PersonResponse> items = persons.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new SearchResponse<>(items, items.size(), search));
    }

    @GetMapping("/{personId}")
    public ResponseEntity<PersonResponse> getPerson(@PathVariable UUID personId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow();

        if (user.getOrganization() == null) {
            return ResponseEntity.status(403).build();
        }

        return personRepository.findById(personId)
                .filter(person -> person.getIntegration().getOrganization().getOrganizationId()
                        .equals(user.getOrganization().getOrganizationId()))
                .map(person -> ResponseEntity.ok(toResponse(person)))
                .orElse(ResponseEntity.status(403).build());
    }

    private PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getPersonId(),
                person.getFirstName(),
                person.getLastName(),
                person.getExternalTenantId(),
                person.getPhoneNumber(),
                person.getEmail(),
                person.getLeaseStartDate(),
                person.getLeaseEndDate(),
                person.getUnit() != null ? person.getUnit().getUnitId() : null,
                person.getUnit() != null ? person.getUnit().getUnitNumber() : null,
                person.getIntegration().getIntegrationId(),
                person.getLastSyncTime()
        );
    }
}
