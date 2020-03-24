package main.java.music;

public class ElectricGuitar extends StringedInstrument {
  public ElectricGuitar() {
    super.numberOfStrings = 6;
  }

  public ElectricGuitar(int numberOfStrings) {
    super.numberOfStrings = numberOfStrings;
  }

  @Override
  void sound() {
    System.out.println("Twang");
  }
}
