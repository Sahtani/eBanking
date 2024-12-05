package com.youcode.ebanking.mapper;

import com.youcode.ebanking.dto.UserResponseDTO;
import com.youcode.ebanking.dto.UserRegistrationDTO;
import com.youcode.ebanking.model.EbUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO userToUserResponseDTO(EbUser user);

    EbUser userRegistrationDTOToUser(UserRegistrationDTO dto);
}