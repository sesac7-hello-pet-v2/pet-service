package hello.pet.petservice.service;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.entity.PetStatus;
import hello.pet.petservice.exception.AlreadyAnnouncedException;
import hello.pet.petservice.exception.ForbiddenException;
import hello.pet.petservice.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public List<PetResponse> getPetsByShelter(Long shelterId, String status) {
        List<Pet> pets;

        if (status == null) {
            pets = petRepository.findAllByShelterIdAndStatusNot(shelterId, PetStatus.DELETED);
        } else {
            PetStatus petStatus = PetStatus.valueOf(status.toUpperCase());
            pets = petRepository.findAllByShelterIdAndStatus(shelterId, petStatus);
        }

        return pets.stream()
                   .map(PetResponse::from)
                   .collect(Collectors.toList());
    }

    public PetResponse updatePet(Long petId, PetPatchRequest request, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        pet.updateInfo(request);
        return PetResponse.from(pet);
    }

    public void deletePet(Long petId, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() == PetStatus.DELETED) {
            throw new IllegalStateException("이미 삭제된 펫입니다.");
        }

        if (pet.getStatus() == PetStatus.ADOPTED) {
            throw new IllegalStateException("입양 완료된 펫은 삭제할 수 없습니다.");
        }

        pet.softDelete();
        petRepository.save(pet);
    }

    public void markAsAnnounced(Long petId, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() == PetStatus.ANNOUNCED) {
            throw new AlreadyAnnouncedException("이미 공고가 등록된 펫입니다.");
        }

        if (pet.getStatus() == PetStatus.ADOPTED) {
            throw new IllegalStateException("이미 입양된 펫은 공고할 수 없습니다.");
        }

        pet.markAsAnnounced();
    }

    public void markAsAvailable(Long petId, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() != PetStatus.ANNOUNCED) {
            throw new IllegalStateException("공고 중인 펫만 입양 가능 상태로 변경할 수 있습니다.");
        }

        pet.markAsAvailable();
    }

    public void markAsAdopted(Long petId, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() == PetStatus.ADOPTED) {
            throw new IllegalStateException("이미 입양 완료된 펫입니다.");
        }

        pet.markAsAdopted();
    }

    private Pet findById(Long petId) {
        return petRepository.findByIdAndStatusNot(petId, PetStatus.DELETED)
                            .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
    }

    private void validateShelterAuthority(Long userId, String userRole, Pet pet) {
        if (!"SHELTER".equals(userRole)) {
            throw new ForbiddenException("보호소 권한이 필요합니다.");
        }

        if (!pet.getShelterId().equals(userId)) {
            throw new ForbiddenException("해당 펫을 관리할 권한이 없습니다.");
        }
    }
}
