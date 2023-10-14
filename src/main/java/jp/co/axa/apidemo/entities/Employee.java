package jp.co.axa.apidemo.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_NAME")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max=256, message = "Length of name must be between 2 and 256")
    private String name;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_SALARY")
    @NotNull(message = "salary is required")
    @Min(value = 1, message = "salary must be greater than 0")
    private Integer salary;

    @Getter
    @Setter
    @Column(name="DEPARTMENT")
    @NotBlank(message = "department is required")
    private String department;

}
