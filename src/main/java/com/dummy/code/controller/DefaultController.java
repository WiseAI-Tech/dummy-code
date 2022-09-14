package com.dummy.code.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class DefaultController {
	/* This Class Is Only To Specify Default First Level Mapping */

	@RequestMapping(value = { "" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView defaultPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:admin/");

		return model;
	}

	@RequestMapping(value = { "admin" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView defaultAdminPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:admin/");

		return model;
	}
}