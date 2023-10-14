package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CacheServiceImpl implements CacheService{
    private static  Map<Long, Employee> CACHE; // use in-memory cache for demonstration

    @PostConstruct
    public void init(){
        Map<Long, Employee> cache = new LinkedHashMap<Long, Employee>(100,.75F,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size()>100;
            }
        };
        CACHE = Collections.synchronizedMap(cache);
    }


    @Override
    public Employee getById(Long id) {
        return CACHE.getOrDefault(id,null);
    }

    @Override
    public void putInCache(Employee employee) {
        Long id = employee.getId();
        if(Objects.isNull(id)){
            return;
        }
        CACHE.put(id,employee);
    }

    @Override
    public void removeInCache(Long id) {
        CACHE.remove(id);
    }
}
