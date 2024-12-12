package org.example.laboratoryappointmentsystemspring.repository;

import org.example.laboratoryappointmentsystemspring.dox.Admin;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {

    //新增管理员
    Admin save(Admin admin);

    //删除
    int deleteById(String id);

    Admin findById(String id);
    int updateById(Labadmin labadmin);

    /**
     * 根据ID查询
     */
    Labadmin selectById(Integer id);
    List<Admin> findAll();

    @Select("select * from labadmin where username = #{username}")
    Labadmin selectByUsername(String username);
}