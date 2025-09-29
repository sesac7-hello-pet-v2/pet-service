package hello.pet.petservice.service;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetUpdateRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

    private final PetRepository petRepository;

    public PetResponse createPet(PetCreateRequest request) {
        Pet savedPet = petRepository.save(request.toEntity());
        return PetResponse.from(savedPet);
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(Long petId) {
        Pet pet = findById(petId);
        return PetResponse.from(pet);
    }

    /**
     * Pet 수정
     */
    public PetResponse updatePet(Long petId, PetUpdateRequest request) {
        Pet pet = findById(petId);

        pet.updateInfo(
                request.getBreed(),
                request.getGender(),
                request.getAge(),
                request.getHealth(),
                request.getPersonality(),
                request.getImageUrl(),
                request.getAnimalType()
        );

        return PetResponse.from(pet);
    }

    public void deletePet(Long petId) {
        Pet pet = findById(petId);
        petRepository.delete(pet);
    }

    private Pet findById(Long petId) {
        return petRepository.findById(petId)
                            .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
    }
}
