package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Dog;
import cz.uhk.pro2kf2026.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;

    @Autowired
    public DogServiceImpl(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    @Override
    public Dog getDog(long id) {
        return dogRepository.findById(id).orElse(null);
    }

    @Override
    public void saveDog(Dog dog) {
        dogRepository.save(dog);
    }

    @Override
    public void deleteDog(long id) {
        dogRepository.findById(id).ifPresent(dog -> dogRepository.deleteById(id));
    }

    @Override
    public List<Dog> getAllDogs() {
        return dogRepository.findAll();
    }
}
