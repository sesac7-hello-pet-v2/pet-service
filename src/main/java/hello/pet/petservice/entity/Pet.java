package hello.pet.petservice.entity;

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

    public void updateInfo(String breed, Gender gender, int age, Health health, String personality,
                           String imageUrl, AnimalType animalType) {
        this.breed = breed;
        this.gender = gender;
        this.age = age;
        this.health = health;
        this.personality = personality;
        this.imageUrl = imageUrl;
        this.animalType = animalType;
    }
}
