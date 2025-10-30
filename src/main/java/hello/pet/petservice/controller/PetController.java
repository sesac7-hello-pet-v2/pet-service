package hello.pet.petservice.controller;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetResponse> createPet(@RequestParam("animalType") String animalType,
                                                 @RequestParam("breed") String breed,
                                                 @RequestParam("gender") String gender,
                                                 @RequestParam("health") String health,
                                                 @RequestParam("personality") String personality,
                                                 @RequestParam("age") Integer age,
                                                 @RequestPart(value = "image", required = false) MultipartFile image,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @RequestHeader("X-User-Role") String userRole) {
        // RequestParam으로 받은 필드들을 PetCreateRequest 객체로 변환
        PetCreateRequest request = PetCreateRequest.builder()
                .animalType(hello.pet.petservice.entity.AnimalType.valueOf(animalType))
                .breed(breed)
                .gender(hello.pet.petservice.entity.Gender.valueOf(gender))
                .health(hello.pet.petservice.entity.Health.valueOf(health))
                .personality(personality)
                .age(age)
                .build();

        PetResponse response = petService.createPet(request, image, userId, userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(@PathVariable Long petId) {
        PetResponse response = petService.getPet(petId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(@RequestHeader("X-User-Id") Long shelterId,
                                                     @RequestParam(required = false) String status
    ) {
        List<PetResponse> response = petService.getPetsByShelter(shelterId, status);
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
    public ResponseEntity<Void> markAsAnnounced(@PathVariable Long petId,
                                                @RequestHeader("X-User-Id") Long userId,
                                                @RequestHeader("X-User-Role") String userRole) {
        petService.markAsAnnounced(petId, userId, userRole);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{petId}/mark-available")
    public ResponseEntity<Void> markAsAvailable(@PathVariable Long petId,
                                                @RequestHeader("X-User-Id") Long userId,
                                                @RequestHeader("X-User-Role") String userRole) {
        petService.markAsAvailable(petId, userId, userRole);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{petId}/mark-adopted")
    public ResponseEntity<Void> markAsAdopted(@PathVariable Long petId,
                                              @RequestHeader("X-User-Id") Long userId,
                                              @RequestHeader("X-User-Role") String userRole) {
        petService.markAsAdopted(petId, userId, userRole);
        return ResponseEntity.noContent().build();
    }
}
