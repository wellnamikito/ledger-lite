package com.ledgerlite.ledgerliteapp.account;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "accountTypeId", target = "accountType.id")
    @Mapping(source = "ownerId", target = "owner.id")
    AccountDto toDto(Account account);
}