package site.leesoyeon.probabilityrewardsystem.user.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.probabilityrewardsystem.user.dto.UserRegistrationDto;
import site.leesoyeon.probabilityrewardsystem.user.entity.User;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserRole;
import site.leesoyeon.probabilityrewardsystem.user.enums.UserStatus;

@Mapper(componentModel = "spring", imports = {UserRole.class, UserStatus.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "role", expression = "java(UserRole.ROLE_CUSTOMER)")
    @Mapping(target = "status", expression = "java(UserStatus.PENDING)")
    User toUser(UserRegistrationDto signupDto);

}
