package hello.pet.petservice.repository;

import hello.pet.petservice.entity.Pet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByShelterId(Long shelterId);

    List<Pet> findAllByShelterIdAndAnnounced(Long shelterId, Boolean announced);
}
