package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CacheService cacheService;

    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    public Employee getEmployee(Long employeeId) {
        Employee employee = cacheService.getById(employeeId); // get from cache first.
        if(Objects.nonNull(employee)){
            return employee; // return from cache.
        }
        Optional<Employee> optEmp = employeeRepository.findById(employeeId);
        if(optEmp.isPresent()){
            Employee rawEmployee = optEmp.get();
            cacheService.putInCache(rawEmployee); // write back to cache
            return rawEmployee;
        }
        return null;
    }

    public Employee saveEmployee(Employee employee){
        employee = employeeRepository.save(employee);
        cacheService.putInCache(employee);// save to cache for later read.
        return employee; // we need to return id to caller.
    }

    public void deleteEmployee(Long employeeId){
        cacheService.removeInCache(employeeId); // remove from cache too.
        employeeRepository.deleteById(employeeId);
    }

    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
        cacheService.putInCache(employee); //update cache content after update.
    }
}