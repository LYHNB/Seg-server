package com.lxxx.controller.admin;

import com.lxxx.constant.JwtClaimsConstant;
import com.lxxx.dto.EmployeeDTO;
import com.lxxx.dto.EmployeeLoginDTO;
import com.lxxx.dto.EmployeePageQueryDTO;
import com.lxxx.entity.Employee;
import com.lxxx.log.Log;
import com.lxxx.properties.JwtProperties;
import com.lxxx.result.PageResult;
import com.lxxx.result.Result;
import com.lxxx.service.EmployeeService;
import com.lxxx.utils.JwtUtil;
import com.lxxx.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @GetMapping("/test")
    @ApiOperation(value = "测试")
    public Result<?> test() {
        return Result.success();
    }


    /**
     * @Description: 员工登录
     * @Param: [employeeLoginDTO]
     * @return: com.lxxx.result.Result<com.lxxx.vo.EmployeeLoginVO>
     * @Author: ILx
     * @Date: 2024/6/27
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    @Log(title = "登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登陆成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ID, employee.getId());
        claims.put(JwtClaimsConstant.USERNAME, employee.getUsername());
        String token = JwtUtil.createJwt(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        //BeanUtils.copyProperties(employee,employeeLoginVO);

        return Result.success(employeeLoginVO);
    }

    /**
     * @Description: 员工分页查询
     * @param: [employeePageQueryDTO]
     * @return: com.lxxx.result.Result<com.lxxx.result.PageResult>
     * @Author: ILx
     * @Date: 2024/6/27
     */
    @GetMapping("/employee/page")
    @ApiOperation(value = "员工分页查询")
    @Log(title = "员工列表查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 员工删除
     *
     * @param: [id]
     * @return: com.lxxx.result.Result
     * @Author: ILx
     * @Date: 2024/7/15
     */
    @DeleteMapping("/employee/delete/{id}")
    @ApiOperation(value = "员工删除")
    @Log(title = "员工删除")
    public Result<?> delete(@PathVariable Long id) {
        log.info("员工删除操作,id{}", id);
        employeeService.delete(id);
        return Result.success();
    }

    /**
     * 员工启用禁用
     *
     * @param: [id, status]
     * @return: com.lxxx.result.Result<?>
     * @Author: ILx
     * @Date: 2024/7/24
     */
    @PutMapping("/employee/{id}/status")
    @ApiOperation(value = "员工启用禁用")
    @Log(title = "员工状态设置")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("员工status修改id:{},status:{}", id, status);
        employeeService.updataStatus(id, status);
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param: [employeeAddDTO]
     * @return: com.lxxx.result.Result<?>
     * @Author: ILx
     * @Date: 2024/7/24
     */
    @PostMapping("/employee/add")
    @ApiOperation(value = "新增员工")
    @Log(title = "新增员工")
    public Result<?> addEmp(@RequestBody EmployeeDTO employeeAddDTO) {
        employeeService.addEmp(employeeAddDTO);
        return Result.success();
    }

    /*@GetMapping("/employee/{id}")
    public Result<EmployeeDTO> getById(@PathVariable Long id) {
        employeeService.getById(id);
    }*/

    /**
     * 编辑员工
     *
     * @param: [employeeAddDTO]
     * @return: com.lxxx.result.Result<?>
     * @Author: ILx
     * @Date: 2024/7/29
     */
    @PutMapping("/employee/update")
    @ApiOperation("编辑员工信息")
    @Log(title = "编辑员工信息")
    public Result<?> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
