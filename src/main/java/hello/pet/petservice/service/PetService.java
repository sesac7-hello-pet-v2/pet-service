package hello.pet.petservice.service;

import hello.pet.petservice.dto.request.PetCreateRequest;
import hello.pet.petservice.dto.request.PetPatchRequest;
import hello.pet.petservice.dto.response.PetResponse;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.entity.PetStatus;
import hello.pet.petservice.exception.AlreadyAnnouncedException;
import hello.pet.petservice.exception.ForbiddenException;
import hello.pet.petservice.facade.AnnouncementServiceFacade;
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
    private final AnnouncementServiceFacade announcementServiceFacade;

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
        Pet pet = findById(petId);
        return PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl);
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
                   .map(pet -> PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl))
                   .toList();
    }

    public PetResponse updatePet(Long petId, PetPatchRequest request, Long userId, String userRole) {
        Pet pet = findById(petId);
        validateShelterAuthority(userId, userRole, pet);

        pet.updateInfo(request);
        return PetResponse.from(pet, s3BaseUrl, defaultPetImageUrl);
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

        if (announcementServiceFacade.hasActiveAnnouncements(petId)) {
            throw new IllegalStateException("이 펫은 공고에 등록되어 있어 삭제할 수 없습니다.");
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
