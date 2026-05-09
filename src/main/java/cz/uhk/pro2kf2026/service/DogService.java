package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Dog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DogService {
    Dog getDog(long id);
    void saveDog(Dog dog);
    void deleteDog(long id);
    List<Dog> getAllDogs();
}
