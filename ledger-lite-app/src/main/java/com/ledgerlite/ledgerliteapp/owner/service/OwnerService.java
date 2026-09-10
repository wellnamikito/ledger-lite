package com.ledgerlite.ledgerliteapp.owner.service;

import com.ledgerlite.ledgerliteapp.owner.CreateOwnerRequest;
import com.ledgerlite.ledgerliteapp.owner.OwnerDTO;
import com.ledgerlite.ledgerliteapp.owner.UpdateOwnerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OwnerService {

    OwnerDTO createOwner(CreateOwnerRequest request);

    OwnerDTO getOwnerById(UUID id);

    Page<OwnerDTO> getAllOwners(Pageable pageable);

    OwnerDTO updateOwner(UUID id, UpdateOwnerRequest request);

    void deleteOwner(UUID id);
}
