package com.microservice.telecom.CustomerMS.Customer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservice.telecom.CustomerMS.dto.CustomerDTO;
import com.microservice.telecom.CustomerMS.dto.LoginDTO;
import com.microservice.telecom.CustomerMS.dto.PlanDTO;
import com.microservice.telecom.CustomerMS.service.CustomerService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@RestController
@RefreshScope
//@RibbonClient(name = "custribbon")
public class CustomerController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	RestTemplate template;
	
	@Value("${customer.name}")
	String name;
	
	
//	@Value("${friend.uri}")
//	String friendUri;
	
//	@Value("${plan.uri}")
//	String planUri;
	
	@Autowired
	CustomerService custService;
//	@Autowired
//	DiscoveryClient client;
	// Create a new customer
	
	@Autowired
	CustPlanFeign feignClient;
	
	@PostMapping(value = "/customers",  consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer {}", custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	
	@PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer {} with password {}", loginDTO.getPhoneNo(),loginDTO.getPassword());
			logger.info("customer name {}",name);
			return	custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	//@HystrixCommand(fallbackMethod = "fallbackmethod")
	@GetMapping(value = "/customers/{phoneNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {

		logger.info("Profile request for customer {}", phoneNo);
		CustomerDTO customerDto = custService.getCustomerProfile(phoneNo);
		
//		List<ServiceInstance> planms = client.getInstances("PLANMS");
//		ServiceInstance instance = planms.get(0);
//		URI planUri = instance.getUri();
//		
//		List<ServiceInstance> families = client.getInstances("FRIENDFAMILYMS");
//		ServiceInstance family = families.get(0);
//		URI friendUri = family.getUri();
		
		/*
		 * PlanDTO planDto =
		 * template.getForObject("http://PLANMS/"+"/plan/"+customerDto.getCurrentPlan().
		 * getPlanId(), PlanDTO.class); customerDto.setCurrentPlan(planDto);
		 */
//		System.out.println(planUri);
//		System.out.println(friendUri);
		//List<Long> friends = template.getForObject("http://custribbon/getFriendFamily/"+phoneNo, List.class);
		//using feign client
		PlanDTO planDto =feignClient.planData(customerDto.getCurrentPlan().getPlanId());
		customerDto.setCurrentPlan(planDto);
		List<Long> friends = template.getForObject("http://FRIENDFAMILYMS/"+"/getFriendFamily/"+phoneNo, List.class);
		customerDto.setFriendAndFamily(friends);
		
		
		/* before ribbon
		 * logger.info("Profile request for customer {}", phoneNo); CustomerDTO
		 * customerDto = custService.getCustomerProfile(phoneNo);
		 * 
		 * List<ServiceInstance> planms = client.getInstances("PLANMS"); ServiceInstance
		 * instance = planms.get(0); URI planUri = instance.getUri();
		 * 
		 * List<ServiceInstance> families = client.getInstances("FRIENDFAMILYMS");
		 * ServiceInstance family = families.get(0); URI friendUri = family.getUri();
		 * 
		 * PlanDTO planDto = new
		 * RestTemplate().getForObject(planUri+"/customer/plan/"+customerDto.
		 * getCurrentPlan().getPlanId(), PlanDTO.class);
		 * customerDto.setCurrentPlan(planDto); System.out.println(planUri);
		 * System.out.println(friendUri); //List<Long> friends =
		 * template.getForObject("http://custribbon/getFriendFamily/"+phoneNo,
		 * List.class); List<Long> friends = new
		 * RestTemplate().getForObject(friendUri+"/getFriendFamily/"+phoneNo,
		 * List.class); customerDto.setFriendAndFamily(friends);
		 */
		
		return customerDto;
	}
	
	public CustomerDTO fallbackmethod(Long phoneNo) {
		return new CustomerDTO();
	}
}
