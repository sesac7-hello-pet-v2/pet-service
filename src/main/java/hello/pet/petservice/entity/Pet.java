package hello.pet.petservice.entity;

import hello.pet.petservice.dto.request.PetPatchRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "pets")
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long shelterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType animalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Health health;

    @Column(nullable = false, length = 1000)
    private String personality;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String breed;

    /**
     * S3에 저장된 이미지의 키 값
     * 예: "123/pet/1234567890_abc12.jpg"
     * 실제 URL은 S3_BASE_URL + imageS3Key로 조합
     * null인 경우 기본 이미지 사용
     */
    @Column(name = "image_s3_key", length = 500)
    private String imageS3Key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PetStatus status = PetStatus.AVAILABLE;

    private LocalDateTime deletedAt;

    public void markAsAnnounced() {
        this.status = PetStatus.ANNOUNCED;
    }

    public void markAsAvailable() {
        this.status = PetStatus.AVAILABLE;
    }

    public void markAsAdopted() {
        this.status = PetStatus.ADOPTED;
    }

    public void updateInfo(PetPatchRequest request) {
        if (request.getAnimalType() != null) {
            this.animalType = request.getAnimalType();
        }
        if (request.getBreed() != null && !request.getBreed().isBlank()) {
            this.breed = request.getBreed();
        }
        if (request.getGender() != null) {
            this.gender = request.getGender();
        }
        if (request.getHealth() != null) {
            this.health = request.getHealth();
        }
        if (request.getPersonality() != null && !request.getPersonality().isBlank()) {
            this.personality = request.getPersonality();
        }
        if (request.getAge() != null && request.getAge() > 0) {
            this.age = request.getAge();
        }
    }

    public void softDelete() {
        this.status = PetStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.status = PetStatus.AVAILABLE;
        this.deletedAt = null;
    }
}
