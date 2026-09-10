package com.ledgerlite.ledgerliteapp.owner;

import com.ledgerlite.ledgerliteapp.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/owners")
@AllArgsConstructor
public class OwnerController {

    private OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(
            @Valid @RequestBody CreateOwnerRequest request
    ){
        OwnerDTO created = ownerService.createOwner(request);
        URI location = URI.create("/owners/" +created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwnerBuId(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OwnerDTO>> getAllOwners(
            @PageableDefault(size =  20, sort = "createdAt")
            Pageable pageable
    ){
        return ResponseEntity.ok(ownerService.getAllOwners(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerDTO> updateOwner(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOwnerRequest request
    ){
        return ResponseEntity.ok(ownerService.updateOwner(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(
            @PathVariable UUID id
    ){
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }

}

