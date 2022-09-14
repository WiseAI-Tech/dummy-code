package com.dummy.code.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("")
public class IgnoreController {
	@RequestMapping(value = { "/template/uib/datepicker" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView defaultPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("template/uib/datepicker/datepicker");

		return model;
	}
}