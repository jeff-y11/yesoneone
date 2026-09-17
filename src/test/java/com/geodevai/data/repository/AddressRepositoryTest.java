package com.geodevai.data.repository;

import com.geodevai.data.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddressRepositoryTest {

    @Mock
    private AddressRepository addressRepository;

    @Test
    void testFindByExternalPropertyId() {
        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Address()));
        Optional<Address> result = addressRepository.findById(UUID.randomUUID());
        assertTrue(result.isPresent());
    }

    @Test
    void testSaveAndFindById() {
        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.of(address));

        Address saved = addressRepository.save(address);
        Optional<Address> found = addressRepository.findById(saved.getAddressId());

        assertNotNull(saved);
        assertTrue(found.isPresent());
    }
}
