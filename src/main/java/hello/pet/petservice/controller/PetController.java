package hello.pet.petservice.controller;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.service.PetService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetResponse> createPet(@Valid @RequestBody PetCreateRequest request,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @RequestHeader("X-User-Role") String userRole) {

        PetResponse response = petService.createPet(request, userId, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(@PathVariable Long petId) {
        PetResponse response = petService.getPet(petId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(@RequestHeader("X-User-Id") Long shelterId,
                                                     @RequestParam(required = false) Boolean announced
    ) {
        List<PetResponse> response = petService.getPetsByShelter(shelterId, announced);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long petId,
                                                 @Valid @RequestBody PetPatchRequest request,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @RequestHeader("X-User-Role") String userRole) {
        PetResponse response = petService.updatePet(petId, request, userId, userRole);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId,
                                          @RequestHeader("X-User-Id") Long userId,
                                          @RequestHeader("X-User-Role") String userRole) {
        petService.deletePet(petId, userId, userRole);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{petId}/mark-announced")
    public ResponseEntity<Void> markAsAnnounced(@PathVariable Long petId) {
        petService.markAsAnnounced(petId);
        return ResponseEntity.noContent().build();
    }
}
