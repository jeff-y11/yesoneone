package com.geodevai.controller;

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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/services/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPersons(
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

        return ResponseEntity.ok(Map.of(
                "items", persons,
                "count", persons.size(),
                "query", search
        ));
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Map<String, Object>> getPerson(@PathVariable UUID personId) {
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
                .map(person -> ResponseEntity.ok((Map<String, Object>) Map.of(
                        "personId", person.getPersonId(),
                        "firstName", person.getFirstName() != null ? person.getFirstName() : "",
                        "lastName", person.getLastName() != null ? person.getLastName() : "",
                        "externalTenantId", person.getExternalTenantId(),
                        "phoneNumber", person.getPhoneNumber() != null ? person.getPhoneNumber() : "",
                        "email", person.getEmail() != null ? person.getEmail() : "",
                        "leaseStartDate", person.getLeaseStartDate() != null ? person.getLeaseStartDate() : "",
                        "leaseEndDate", person.getLeaseEndDate() != null ? person.getLeaseEndDate() : "",
                        "unitId", person.getUnit() != null ? person.getUnit().getUnitId() : null,
                        "unitNumber", person.getUnit() != null ? person.getUnit().getUnitNumber() : "",
                        "integrationId", person.getIntegration().getIntegrationId(),
                        "lastSyncTime", person.getLastSyncTime() != null ? person.getLastSyncTime() : ""
                )))
                .orElse(ResponseEntity.status(403).build());
    }
}
