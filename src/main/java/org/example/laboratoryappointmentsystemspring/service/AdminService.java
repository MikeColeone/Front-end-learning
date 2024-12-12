package org.example.laboratoryappointmentsystemspring.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.example.laboratoryappointmentsystemspring.dox.Admin;
import org.example.laboratoryappointmentsystemspring.repository.AdminRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    @Resource
    private final AdminRepository adminRepository;

    public void add(Admin admin) {
        Admin savedAdmin = adminRepository.save(admin);
        if (ObjectUtil.isNotNull(dbLabadmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(labadmin.getPassword())) {
            labadmin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(labadmin.getName())) {
            labadmin.setName(labadmin.getUsername());
        }
        labadmin.setRole(RoleEnum.LABADMIN.name());
        labadminMapper.insert(labadmin);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        labadminMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            labadminMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Labadmin labadmin) {
        labadminMapper.updateById(labadmin);
    }

    /**
     * 根据ID查询
     */
    public Labadmin selectById(Integer id) {
        return labadminMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Labadmin> selectAll(Labadmin labadmin) {
        return labadminMapper.selectAll(labadmin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Labadmin> selectPage(Labadmin labadmin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Labadmin> list = labadminMapper.selectAll(labadmin);
        return PageInfo.of(list);
    }
}