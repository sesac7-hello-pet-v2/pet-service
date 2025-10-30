package hello.pet.petservice.repository;

import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.entity.PetStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByIdAndStatusNot(Long id, PetStatus status);

    List<Pet> findAllByShelterIdAndStatusNot(Long shelterId, PetStatus status);

    List<Pet> findAllByShelterIdAndStatus(Long shelterId, PetStatus status);
}
