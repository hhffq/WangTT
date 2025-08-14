package com.changgou.system.dao;


import com.changgou.system.pojo.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface AdminMapper extends Mapper<Admin> {

    @Select("SELECT * FROM `tb_admin` where login_name = #{loginName} and `status` = '1'")
    Admin selectByLoginName(@Param("loginName")String loginName);
}
