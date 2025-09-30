package hello.pet.petservice.service;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.exception.ForbiddenException;
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

    public PetResponse createPet(PetCreateRequest request, Long userId, String userRole) {
        if (!"SHELTER".equals(userRole)) {
            throw new ForbiddenException("보호소 권한이 필요합니다.");
        }

        Pet savedPet = petRepository.save(request.toEntity(userId));
        return PetResponse.from(savedPet);
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(Long petId) {
        Pet pet = findById(petId);
        return PetResponse.from(pet);
    }

    public PetResponse updatePet(Long petId, PetPatchRequest request, Long userId, String userRole) {
        if (!"SHELTER".equals(userRole)) {
            throw new ForbiddenException("보호소 권한이 필요합니다.");
        }

        Pet pet = findById(petId);

        if (!pet.getShelterId().equals(userId)) {
            throw new ForbiddenException("해당 펫을 수정할 권한이 없습니다.");
        }

        pet.updateInfo(request);
        return PetResponse.from(pet);
    }

    public void deletePet(Long petId, Long userId, String userRole) {
        if (!"SHELTER".equals(userRole)) {
            throw new ForbiddenException("보호소 권한이 필요합니다.");
        }

        Pet pet = findById(petId);

        if (!pet.getShelterId().equals(userId)) {
            throw new ForbiddenException("해당 펫을 삭제할 권한이 없습니다.");
        }

        petRepository.delete(pet);
    }

    private Pet findById(Long petId) {
        return petRepository.findById(petId)
                            .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
    }
}
