package fruitmachine

object FruitMachineApp extends App {
  val fruitMachine = FruitMachine(new SlotSpinner, 20, 1000)
  val player: Option[Player] = Some(Player(100))

  (1 to 20).foldLeft((player, fruitMachine)) {
    case ((Some(p), m), _) => {
      println(p, "machine: " + m.jackPot)
      m.play(p)
    }
    case (x, _) =>
      println("busted!")
      x
  }
}
