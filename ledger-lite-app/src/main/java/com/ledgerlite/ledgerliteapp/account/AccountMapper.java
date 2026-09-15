package com.ledgerlite.ledgerliteapp.account;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "accountType.id", target = "accountTypeId")
    AccountDto toDto(Account account);
}