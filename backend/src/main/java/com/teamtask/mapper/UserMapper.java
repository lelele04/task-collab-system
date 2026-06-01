package com.teamtask.mapper;

import com.teamtask.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO `user`(username, password, email, phone, role, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE `user` SET username=#{username}, email=#{email}, phone=#{phone}, " +
            "update_time=NOW() WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE `user` SET password=#{password}, update_time=NOW() WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Delete("DELETE FROM `user` WHERE id=#{id}")
    int delete(Long id);

    @Select("SELECT * FROM `user` WHERE id=#{id}")
    User findById(Long id);

    @Select("SELECT * FROM `user` WHERE username=#{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM `user` WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<User> searchByKeyword(String keyword);

    @Select("SELECT * FROM `user` ORDER BY create_time DESC")
    List<User> findAll();
}