package fruitmachine

import java.util.Random

class SlotSpinner {
  def spin: Outcome = {
    val random = new Random()
    val sides: Int = 4
    val slots = (random.nextInt(sides), random.nextInt(sides), random.nextInt(sides), random.nextInt(sides))
    slots match {
      case (a, b, c, d) if a == b && b == c && c == d => AllEqual
      case (a, b, c, d) if a == b || b == c || c == d => TwoAdjacent
      case (a, b, c, d) if Set(a, b, c, d).size == sides => EachDifferent
      case _ => Default
    }
  }
}
