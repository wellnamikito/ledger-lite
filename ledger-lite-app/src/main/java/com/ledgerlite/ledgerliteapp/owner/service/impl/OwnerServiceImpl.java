package com.ledgerlite.ledgerliteapp.owner.service.impl;

import com.ledgerlite.ledgerliteapp.account.AccountRepository;
import com.ledgerlite.ledgerliteapp.common.exception.OwnerHasActiveAccountsException;
import com.ledgerlite.ledgerliteapp.common.exception.OwnerNotFoundException;
import com.ledgerlite.ledgerliteapp.owner.*;
import com.ledgerlite.ledgerliteapp.owner.service.OwnerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private OwnerRepository ownerRepository;

    private OwnerMapper ownerMapper;

    private AccountRepository accountRepository;


    @Override
    @Transactional
    public OwnerDTO createOwner(CreateOwnerRequest request) {
       Owner owner = ownerMapper.toEntity(request);
       owner.setId(UUID.randomUUID());
       owner.setCreatedAt(OffsetDateTime.now());

       Owner saved = ownerRepository.save(owner);

       return ownerMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerDTO getOwnerById(UUID id) {
        Owner owner = findOwnerOrThrow(id);
        return ownerMapper.toDto(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OwnerDTO> getAllOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable)
                .map(ownerMapper::toDto);
    }

    @Override
    @Transactional
    public OwnerDTO updateOwner(UUID id, UpdateOwnerRequest request) {
        Owner owner = findOwnerOrThrow(id);

        owner.setLastName(request.lastName());
        owner.setFirstName(request.firstName());
        owner.setMiddleName(request.middleName());

        Owner saved = ownerRepository.save(owner);

        return ownerMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteOwner(UUID id) {
        if(!ownerRepository.existsById(id)){
            throw new OwnerNotFoundException(id);
        }

        if(accountRepository.existsByOwnerId(id)){
            throw new OwnerHasActiveAccountsException(id);
        }
        ownerRepository.deleteById(id);
    }

    private Owner findOwnerOrThrow(UUID id){
        return ownerRepository.findById(id)
                .orElseThrow(() ->
                    new OwnerNotFoundException(id)
                );
    }


}

