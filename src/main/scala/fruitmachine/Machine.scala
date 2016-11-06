package fruitmachine

case class FruitMachine(private val slotSpinner: SlotSpinner,
                        private val costOfSinglePlay: Int,
                        jackPot: Int) {

  def play(player: Player): (Option[Player], FruitMachine) = {
    val result: Option[(Option[Player], FruitMachine)] = for {
      chargedPlayer <- chargeIfApplicable(costOfSinglePlay)(player)
      newJackpot = jackPot + costOfSinglePlay
      moneyToPay = moneyOwed(costOfSinglePlay)(newJackpot)
      moneyPayable = moneyThatCanBePaid(newJackpot)(moneyToPay)
      paidPlayer = payPlayer(chargedPlayer)(moneyPayable)
      creditsToCharge = calculateCreditsToCharge(newJackpot)(moneyToPay)
      finalJackpot = calculateMoneyLeft(newJackpot)(moneyToPay)
      creditedPlayer = paidPlayer.copy(credits = paidPlayer.credits + creditsToCharge)
    } yield (Option(creditedPlayer), copy(jackPot = finalJackpot))

    result getOrElse(None, this)
  }

  private val kickOutPlayer: Player => Option[Player] = player => player.money match {
    case money if money >= 0 => Some(player)
    case _ => None
  }

  private val moneyThatCanBePaid: Int => Int => Int = moneyInMachine => moneyToPay => {
    if (moneyInMachine >= moneyToPay) moneyToPay else moneyInMachine
  }

  private val calculateCreditsToCharge: Int => Int => Int = moneyInMachine => moneyToPay => {
    if (moneyInMachine >= moneyToPay) 0 else moneyToPay - moneyInMachine
  }

  private val calculateMoneyLeft: Int => Int => Int = moneyInMachine => moneyToPay => {
    if (moneyInMachine >= moneyToPay) moneyInMachine - moneyToPay else 0
  }

  private val chargePlayer: Int => Player => Player = costOfPlaying => player =>
    if (player.credits > 0) {
      val creditsLeft = player.credits - costOfPlaying max 0
      val moneyToDeduct = costOfPlaying - player.credits max 0
      player.copy(money = player.money - moneyToDeduct, credits = creditsLeft)
    } else player.copy(money = player.money - costOfPlaying)

  private val chargeIfApplicable: Int => Player => Option[Player] = kickOutPlayer compose chargePlayer(_)

  private val payPlayer: Player => Int => Player = (player: Player) => (moneyOwed: Int) => {
    player.copy(money = player.money + moneyOwed)
  }

  private val moneyOwed: Int => Int => Int = { costOfSinglePlay => jackPot =>
    slotSpinner.spin match {
      case AllEqual => jackPot
      case Default => 0
      case EachDifferent => jackPot / 2
      case TwoAdjacent => costOfSinglePlay * 5
    }
  }
}

sealed trait Outcome

case object EachDifferent extends Outcome

case object AllEqual extends Outcome

case object Default extends Outcome

case object TwoAdjacent extends Outcome

case class Player(money: Int, credits: Int = 0)

