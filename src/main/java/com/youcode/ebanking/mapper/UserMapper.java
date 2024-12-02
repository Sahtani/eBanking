package com.youcode.ebanking.mapper;

import com.youcode.ebanking.dto.UserDTO;
import com.youcode.ebanking.dto.UserRegistrationDTO;
import com.youcode.ebanking.model.EbUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(EbUser user);

    EbUser userRegistrationDTOToUser(UserRegistrationDTO dto);
}