package com.greenfoxacademy.foxclub.controller;

import com.greenfoxacademy.foxclub.model.User;
import com.greenfoxacademy.foxclub.service.ActionService;
import com.greenfoxacademy.foxclub.service.FoxService;
import com.greenfoxacademy.foxclub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

  private ActionService actionService;
  private FoxService foxService;
  private UserService userService;

  @Autowired
  public MainController(FoxService foxService, ActionService actionService, UserService userService) {
    this.foxService = foxService;
    this.actionService = actionService;
    this.userService = userService;
  }

  @GetMapping(value = "/")
  public String getIndex(@RequestParam(required = false) String name, Model model) {
    model.addAttribute("foxes", foxService.getListOfFoxes());
    model.addAttribute("selectedFox", foxService.getSelectedFoxByName(name));
    if (name != null) {
      model.addAttribute("actions",
          actionService.getActionHistoryAsListOfStringsWithNewActionOnTop(foxService.getSelectedFoxByName(name)));
    }
    return "index";
  }

  @GetMapping(value = "/login")
  public String getLoginView() {
    return "login";
  }

  @PostMapping(value = "/login")
  public String getNameFromLogin(String name, String option, Model model) {
    if (name.isEmpty()) {
      return "redirect:/";
    }
    if (foxService.checkIsItExist(name)) {
      switch (option) {
        case "go":
          return "redirect:/?name=" + name;
        case "create":
          model.addAttribute("alreadyExist", true);
          return "login";
      }
    } else {
      switch (option) {
        case "go":
          model.addAttribute("alreadyExist", false);
          return "login";
        case "create":
          foxService.createNewFoxWithNameAndAddToTheList(name);
          return "redirect:/?name=" + name;
      }
    }
    return null;
  }

  @GetMapping(value = "/register")
  public String getRegisterView(Model model,
                                @RequestParam(required = false) Boolean passwordVerificationFailed,
                                @RequestParam(required = false) Boolean usernameVerificationFailed) {
    model.addAttribute("newUser", new User());
    if (passwordVerificationFailed != null) {
      model.addAttribute("passwordVerificationFailed", passwordVerificationFailed);
    }
    if (usernameVerificationFailed != null) {
      model.addAttribute("usernameVerificationFailed", usernameVerificationFailed);
    }
    return "register";
  }

  @PostMapping(value = "/register")
  public String registerNewUser(@ModelAttribute User user, String passwordVerification) {
    if (userService.verifiedPassword(user, passwordVerification) && userService.verifiedUsername(user)) {
      userService.addNewUser(user);
      return "redirect:/loginWithUser";
    }
    if (!userService.verifiedPassword(user, passwordVerification) && !userService.verifiedUsername(user)) {
      return "redirect:/register?passwordVerificationFailed=true&usernameVerificationFailed=true";
    } else if (!userService.verifiedPassword(user, passwordVerification)) {
      return "redirect:/register?passwordVerificationFailed=true";
    } else {
      return "redirect:/register?usernameVerificationFailed=true";
    }
  }

  @GetMapping(value = "/loginWithUser")
  public String getLoginWithUserView(Model model,
                                     @RequestParam(required = false) Boolean invalidUserdata) {
    if (invalidUserdata != null) {
      model.addAttribute("invalidUserdata", invalidUserdata);
    }
    return "loginWithUser";
  }

  @PostMapping(value = "/loginWithUser")
  public String getUserDatasFromLoginView(String username, String password, Model model) {
    if (userService.validateUserData(username, password)) {
      foxService.createNewFoxWithNameAndAddToTheList(username);
      return "redirect:/?name=" + username;
    }
    return "redirect:/loginWithUser?invalidUserdata=true";
  }
}
