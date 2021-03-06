package com.greenfoxacademy.foxclub.service;

import com.greenfoxacademy.foxclub.model.Fox;
import com.greenfoxacademy.foxclub.model.User;
import com.greenfoxacademy.foxclub.repository.FoxRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoxService {

  private List<Fox> listOfFoxes;
  private NutritionService nutritionService;
  private UserService userService;
  private FoxRepository foxRepository;

  @Autowired
  public FoxService(NutritionService nutritionService, UserService userService, FoxRepository foxRepository) {
    this.nutritionService = nutritionService;
    this.userService = userService;
    this.foxRepository = foxRepository;
    this.listOfFoxes = new ArrayList<>();
    initAllFoxes();
  }

  private void initAllFoxes() {
    this.listOfFoxes.add(new Fox("Karak",
        nutritionService.getSelectedFood("hotdog"),
        nutritionService.getSelectedDrink("coke")));
    this.listOfFoxes.add(new Fox("Vuk",
        nutritionService.getSelectedFood("pizza"),
        nutritionService.getSelectedDrink("water")));
    this.listOfFoxes.add(new Fox("Kag",
        nutritionService.getSelectedFood("bread"),
        nutritionService.getSelectedDrink("beer")));
    this.listOfFoxes.add(new Fox("Mr. Green",
        nutritionService.getSelectedFood("salad"),
        nutritionService.getSelectedDrink("water")));
  }

  public List<Fox> getListOfFoxes() {
    return listOfFoxes;
  }

  public boolean checkIsItExist(String name) {
    return listOfFoxes.stream()
        .map(fox -> fox.getName().toLowerCase())
        .collect(Collectors.toList())
        .contains(name.toLowerCase());
  }

  public void createNewFoxWithNameAndAddToTheList(String name) {
    Fox newFox = new Fox(name,
        nutritionService.getSelectedFood("bread"),
        nutritionService.getSelectedDrink("water"));
    listOfFoxes.add(newFox);
    User user = userService.findUserByName(name);
    newFox.setUser(user);
    foxRepository.save(newFox);
  }

  public Fox getSelectedFoxByName(String name) {
    Fox fox = null;
    for (Fox f : listOfFoxes) {
      if (f.getName().equals(name)) {
        fox = f;
        break;
      }
    }
    return fox;
  }
}
