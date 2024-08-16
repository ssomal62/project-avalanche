package site.leesoyeon.avalanche.user.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.avalanche.user.domain.model.User;
import site.leesoyeon.avalanche.user.presentation.dto.UserDetailDto;
import site.leesoyeon.avalanche.user.presentation.dto.UserRegistrationDto;
import site.leesoyeon.avalanche.user.shared.enums.UserRole;
import site.leesoyeon.avalanche.user.shared.enums.UserStatus;

@Mapper(componentModel = "spring", imports = {UserRole.class, UserStatus.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "role", expression = "java(UserRole.ROLE_CUSTOMER)")
    @Mapping(target = "status", expression = "java(UserStatus.PENDING)")
    @Mapping(target = "userId", ignore = true)
    User toUser(UserRegistrationDto signupDto);

    UserDetailDto toDto(User user);

}
