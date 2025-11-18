package hello.pet.petservice.service;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.entity.PetStatus;
import hello.pet.petservice.exception.ForbiddenException;
import hello.pet.petservice.facade.ImageServiceFacade;
import hello.pet.petservice.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final ImageServiceFacade imageServiceFacade;

    @Value("${S3_BASE_URL}")
    private String s3BaseUrl;

    @Value("${DEFAULT_PET_IMAGE_URL}")
    private String defaultPetImageUrl;

    public PetResponse createPet(PetCreateRequest request, MultipartFile image, Long userId, String userRole) {
        validateShelterRole(userRole);

        String imageS3Key = imageServiceFacade.uploadPetImage(userId, image);
        Pet savedPet = petRepository.save(request.toEntity(userId, imageS3Key));

        return PetResponse.from(savedPet, s3BaseUrl, defaultPetImageUrl);
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                               .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
        return PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> getPetsByShelter(Long shelterId, String status) {
        List<Pet> pets;

        if (status == null) {
            pets = petRepository.findAllByShelterId(shelterId);
        } else {
            PetStatus petStatus = PetStatus.valueOf(status.toUpperCase());
            pets = petRepository.findAllByShelterIdAndStatus(shelterId, petStatus);
        }

        return pets.stream()
                   .map(pet -> PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl))
                   .toList();
    }

    public PetResponse updatePet(Long petId, PetPatchRequest request, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalStateException("입양 가능 상태의 펫만 수정할 수 있습니다.");
        }

        pet.updateInfo(request);
        return PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl);
    }

    public void deletePet(Long petId, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalStateException("입양 가능 상태의 펫만 삭제할 수 있습니다.");
        }

        pet.softDelete();
        petRepository.save(pet);
    }

    public void markAsAnnounced(Long petId, Long userId, String userRole) {
        log.info("펫 상태를 공고 중으로 변경 - petId: {}, userId: {}", petId, userId);

        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        // 멱등성: 이미 ANNOUNCED면 스킵
        if (pet.getStatus() == PetStatus.ANNOUNCED) {
            log.info("이미 ANNOUNCED 상태입니다. 스킵합니다. petId: {}", petId);
            return;
        }

        // AVAILABLE 상태 또는 Saga 보상 흐름의 ADOPTED 상태에서만 변경 가능
        if (pet.getStatus() != PetStatus.AVAILABLE && pet.getStatus() != PetStatus.ADOPTED) {
            throw new IllegalStateException(
                    String.format("현재 상태(%s)에서는 공고 등록할 수 없습니다. AVAILABLE 또는 ADOPTED 상태여야 합니다.",
                            pet.getStatus())
            );
        }

        pet.markAsAnnounced();
        petRepository.save(pet);

        log.info("펫 상태를 공고 중으로 변경 완료 - petId: {}, 이전 상태: {}",
                petId, pet.getStatus());
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

        // 멱등성: 이미 ADOPTED 상태면 스킵
        if (pet.getStatus() == PetStatus.ADOPTED) {
            log.info("이미 입양 완료된 펫입니다. 스킵합니다. petId: {}", petId);
            return;
        }

        pet.markAsAdopted();
        log.info("펫 입양 완료 처리 - petId: {}", petId);
    }

    private Pet findById(Long petId) {
        return petRepository.findByIdAndStatusNot(petId, PetStatus.DELETED)
                            .orElseThrow(() -> new EntityNotFoundException("Pet을 찾을 수 없습니다. id=" + petId));
    }

    private void validateShelterRole(String userRole) {
        if (!"SHELTER".equals(userRole)) {
            throw new ForbiddenException("보호소 권한이 필요합니다.");
        }
    }

    private void validateShelterAuthority(Long userId, String userRole, Pet pet) {
        validateShelterRole(userRole);
        if (!pet.getShelterId().equals(userId)) {
            throw new ForbiddenException("해당 펫을 관리할 권한이 없습니다.");
        }
    }
}
