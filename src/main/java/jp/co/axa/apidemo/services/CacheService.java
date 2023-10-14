package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;

public interface CacheService {
    Employee getById(Long id);

    void putInCache(Employee employee);

    void removeInCache(Long id);
}
