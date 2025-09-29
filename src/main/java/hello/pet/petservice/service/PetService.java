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

    /**
     * Pet 생성
     */
    public PetResponse createPet(PetCreateRequest request) {
        Pet pet = Pet.builder()
                     .animalType(request.getAnimalType())
                     .breed(request.getBreed())
                     .gender(request.getGender())
                     .age(request.getAge())
                     .health(request.getHealth())
                     .personality(request.getPersonality())
                     .imageUrl(request.getImageUrl())
                     .build();

        Pet savedPet = petRepository.save(pet);
        return PetResponse.from(savedPet);
    }

    /**
     * Pet 조회
     */
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

    /**
     * Pet 삭제
     */
    public void deletePet(Long petId) {
        Pet pet = findById(petId);
        petRepository.delete(pet);
    }

    /**
     * Pet 단건 조회 (내부 메서드)
     */
    private Pet findById(Long petId) {
        return petRepository.findById(petId)
                            .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
    }
}
