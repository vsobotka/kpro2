package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.UserRepository;
import cz.uhk.pro2kf2026.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        // testUser.setId(1L);
        testUser.setUsername("dominik");
        testUser.setPassword("supertajneheslo");
    }

    @Test
    void getUser_ShouldReturnUserIfExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(testUser, result);
    }

    @Test
    void saveUser_WithNewPassword_ShouldEncodeAndSave() {
        when(passwordEncoder.encode("supertajneheslo")).thenReturn("zahasovaneheslo");

        userService.saveUser(testUser);

        // Ověříme, že se heslo změnilo na to zahešované
        assertEquals("zahasovaneheslo", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void saveUser_WithBlankPassword_ShouldKeepOldPassword() {
        // Původní uživatel uložený v databázi
        User originalUser = new User();
        originalUser.setPassword("stareDobreHeslo");

        // Uživatel z formuláře (např. při editaci), který má prázdné heslo
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setPassword("   "); // schválně samé mezery

        when(userRepository.findById(1L)).thenReturn(Optional.of(originalUser));

        userService.saveUser(updatedUser);

        // Očekáváme, že si uživatel ponechá to původní
        assertEquals("stareDobreHeslo", updatedUser.getPassword());

        // Nikdy bychom neměli volat hešování na prázdné heslo
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void deleteUser_ShouldDeleteIfExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        when(userRepository.findByUsername("dominik")).thenReturn(testUser);

        UserDetails userDetails = userService.loadUserByUsername("dominik");

        assertNotNull(userDetails);
        // Předpokládám, že MyUserDetails správně vytahuje username z entity User
        assertEquals("dominik", userDetails.getUsername());
    }
}
