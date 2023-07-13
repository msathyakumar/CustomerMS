package com.microservice.telecom.CustomerMS.Customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.telecom.CustomerMS.dto.PlanDTO;

@FeignClient(name = "PlanMS", url = "http://localhost:8100/")
public interface CustPlanFeign {
	@GetMapping("/plan/{planId}")
	public PlanDTO planData(@PathVariable("planId") int planId);

}
