package org.example.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.domain.Permission;
public interface PermissionService extends IService<Permission> {
    // 根据用户ID查询对应的权限
    List<Permission> findByUserId(Integer id);
}
