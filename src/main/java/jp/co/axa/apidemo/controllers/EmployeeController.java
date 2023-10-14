package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.Exception.ResourcesNotFoundException;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        return employees;
    }

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable(name="employeeId")Long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId);
        if(Objects.isNull(employee)){
            throw new ResourcesNotFoundException("The employeeId " + employeeId + " doesn't exist.");
        }
        return employee;
    }

    @PostMapping("/employees")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Long saveEmployee(@Valid @RequestBody Employee employee){
        employee = employeeService.saveEmployee(employee);
        System.out.println("Employee Saved Successfully");
        return employee.getId();
    }

    @DeleteMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void deleteEmployee(@PathVariable(name="employeeId")Long employeeId){
        Employee emp = employeeService.getEmployee(employeeId);
        if(Objects.isNull(emp)){
            throw new ResourcesNotFoundException("The employeeId " + employeeId + " doesn't exist.");// give caller a feedback is better
        }
        employeeService.deleteEmployee(employeeId);
        System.out.println("Employee Deleted Successfully");
    }

    @PutMapping("/employees/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void updateEmployee(@Valid @RequestBody Employee employee,
                               @PathVariable(name="employeeId")Long employeeId){
        Employee emp = employeeService.getEmployee(employeeId);
        if(emp != null){
            employee.setId(employeeId);
            employeeService.updateEmployee(employee);
        } else {
            throw new ResourcesNotFoundException("The employeeId " + employeeId + " doesn't exist."); // give caller a feedback is better
        }
    }

}
