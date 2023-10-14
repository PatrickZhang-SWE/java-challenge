package jp.co.axa.apidemo;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.co.axa.apidemo.entities.Employee;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiDemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private Gson gson;

	@Test
	public void contextLoads() {
	}

	/**
	 * setup gson
	 */
	@Before
	public void setup(){
		gson = new GsonBuilder().create();
	}

	/**
	 * cleanup
	 * @throws Exception
	 */
	@After
	public void clearUp() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/v1/employees/")
				.with(httpBasic("normalUser","userPass"))).andExpect(status().isOk()).andReturn();
		Type listType = new TypeToken<ArrayList<Employee>>(){}.getType();
		List<Employee> employeeList = gson.fromJson(result.getResponse().getContentAsString(),listType);
		employeeList.forEach(employee -> {
			Long id = employee.getId();
			try {
				mockMvc.perform(delete("/api/v1/employees/"+id)
						.with(httpBasic("admin","admin"))).andExpect(status().isOk());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	/**
	 * add an employee and get it.
	 */
	@Test
	public void addAnEmployee() throws Exception {
		Employee employee = new Employee();
		employee.setName("patrick");
		employee.setSalary(100);
		employee.setDepartment("R&D");
		final String[] employeeId = {""};
		mockMvc.perform(post("/api/v1/employees")
						.with(httpBasic("admin","admin"))
						.content(gson.toJson(employee)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(mvcResult -> employeeId[0] = mvcResult.getResponse().getContentAsString());
		mockMvc.perform(get("/api/v1/employees/"+employeeId[0]).with(httpBasic("admin","admin")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string(containsString("patrick")));
	}

	/**
	 * add an employee whose salary is zero.
	 * @throws Exception
	 */
	@Test
	public void addAnInvalidEmployee() throws Exception{
		Employee employee = new Employee();
		employee.setName("patrick");
		employee.setSalary(0);//salary is zero.
		employee.setDepartment("R&D");
		final String[] employeeId = {""};
		mockMvc.perform(post("/api/v1/employees")
						.with(httpBasic("admin","admin"))
						.content(gson.toJson(employee)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * test with an invalid user.
	 * @throws Exception
	 */
	@Test
	public void addAnEmployeeWithInvalidUser() throws Exception {
		Employee employee = new Employee();
		employee.setName("patrick");
		employee.setSalary(100);
		employee.setDepartment("R&D");
		final String[] employeeId = {""};
		mockMvc.perform(post("/api/v1/employees")
						.with(httpBasic("user","password"))
						.content(gson.toJson(employee)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * test with a wrong role.
	 * @throws Exception
	 */
	@Test
	public void addAnEmployeeWithInvalidRole() throws Exception {
		Employee employee = new Employee();
		employee.setName("patrick");
		employee.setSalary(100);
		employee.setDepartment("R&D");
		final String[] employeeId = {""};
		mockMvc.perform(post("/api/v1/employees")
						.with(httpBasic("normalUser","userPass"))
						.content(gson.toJson(employee)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	/**
	 * 1. add a new employee.
	 * 2. delete it.
	 * 3. read it again.
	 * @throws Exception
	 */
	@Test
	public void deleteAnEmployee() throws Exception {
		String id = addAnEmployeeInnerTest();
		mockMvc.perform(delete("/api/v1/employees/"+id)
				.with(httpBasic("admin","admin"))).andExpect(status().isOk());
		mockMvc.perform(get("/api/v1/employees/"+id)
				.with(httpBasic("admin","admin"))).andExpect(status().isNotFound());
	}

	/**
	 * 1. add a new employee.
	 * 2. read this new employee.
	 * 3. change salary and update
	 * 4. read this employee again and check the salary.
	 * @throws Exception
	 */
	@Test
	public void updateEmployee() throws Exception{
		String id = addAnEmployeeInnerTest();
		MvcResult result = mockMvc.perform(get("/api/v1/employees/"+id)
				.with(httpBasic("admin","admin"))).andExpect(status().isOk()).andReturn();
		Employee employee = gson.fromJson(result.getResponse().getContentAsString(),Employee.class);
		int oldSalary = employee.getSalary();
		int newSalary = oldSalary+10;
		employee.setSalary(newSalary);
		mockMvc.perform(put("/api/v1/employees/"+id)
				.with(httpBasic("admin","admin"))
				.content(gson.toJson(employee))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		result = mockMvc.perform(get("/api/v1/employees/"+id)
				.with(httpBasic("admin","admin"))).andExpect(status().isOk()).andReturn();
		employee = gson.fromJson(result.getResponse().getContentAsString(),Employee.class);
		Assert.assertTrue(employee.getSalary()==newSalary);
	}

	/**
	 * 1. create three new employees.
	 * 2. get all of them and check the size.
	 * @throws Exception
	 */
	@Test
	public void listAllEmployees() throws Exception {
		addAnEmployeeInnerTest();
		addAnEmployeeInnerTest();
		addAnEmployeeInnerTest();
		MvcResult result = mockMvc.perform(get("/api/v1/employees/")
				.with(httpBasic("normalUser","userPass"))).andExpect(status().isOk()).andReturn();
		Type listType = new TypeToken<ArrayList<Employee>>(){}.getType();
		List<Employee> employeeList = gson.fromJson(result.getResponse().getContentAsString(),listType);
		Assert.assertTrue(employeeList.size() == 3);
	}

	private String addAnEmployeeInnerTest() throws Exception {
		Employee employee = new Employee();
		employee.setName("patrick");
		employee.setSalary(100);
		employee.setDepartment("R&D");
		final String[] employeeId = {""};
		mockMvc.perform(post("/api/v1/employees")
						.with(httpBasic("admin","admin"))
						.content(gson.toJson(employee)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(mvcResult -> employeeId[0] = mvcResult.getResponse().getContentAsString());
		return employeeId[0];
	}

}
