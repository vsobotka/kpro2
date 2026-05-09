package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.model.Dog;
import cz.uhk.pro2kf2026.repository.DogRepository;
import cz.uhk.pro2kf2026.service.DogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceImplTest {

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private DogServiceImpl dogService;

    private Dog testDog;

    @BeforeEach
    void setUp() {
        // Příprava testovacího pejska před každým testem
        testDog = new Dog();
        // Pokud máš v modelu setId(), můžeš odkomentovat:
        // testDog.setId(1L);
    }

    @Test
    void getDog_ShouldReturnDogIfExists() {
        when(dogRepository.findById(1L)).thenReturn(Optional.of(testDog));

        Dog result = dogService.getDog(1L);

        assertNotNull(result);
        assertEquals(testDog, result);
    }

    @Test
    void getDog_ShouldReturnNullIfNotFound() {
        when(dogRepository.findById(99L)).thenReturn(Optional.empty());

        Dog result = dogService.getDog(99L);

        assertNull(result);
    }

    @Test
    void saveDog_ShouldCallRepositorySave() {
        dogService.saveDog(testDog);

        verify(dogRepository, times(1)).save(testDog);
    }

    @Test
    void deleteDog_ShouldDeleteIfExists() {
        when(dogRepository.findById(1L)).thenReturn(Optional.of(testDog));

        dogService.deleteDog(1L);

        verify(dogRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDog_ShouldNotDeleteIfNotExists() {
        when(dogRepository.findById(99L)).thenReturn(Optional.empty());

        dogService.deleteDog(99L);

        // Ověříme, že se metoda deleteById nikdy nezavolala, protože pes neexistoval
        verify(dogRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllDogs_ShouldReturnListOfDogs() {
        List<Dog> dogs = Arrays.asList(new Dog(), new Dog());
        when(dogRepository.findAll()).thenReturn(dogs);

        List<Dog> result = dogService.getAllDogs();

        assertEquals(2, result.size());
        verify(dogRepository, times(1)).findAll();
    }
}