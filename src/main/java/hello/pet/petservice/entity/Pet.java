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

    @Column(length = 500)
    private String imageUrl;

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
        if (request.getImageUrl() != null && request.getImageUrl().matches("^https?://.*")) {
            this.imageUrl = request.getImageUrl();
        }
    }
}
