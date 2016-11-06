package fruitmachine

import java.util.Random
import org.mockito.Mockito._

class FruitMachineTest extends BaseTest {
  val random = new Random(0)
  val initialMoney = 300
  val player = Player(initialMoney)

  val slotSpinner = mock[SlotSpinner]
  val costOfSinglePlay = 20
  val fruitMachine = FruitMachine(slotSpinner, costOfSinglePlay, 0)
  val betMoney = 20

  override protected def beforeEach(): Unit = {
    when(slotSpinner.spin).thenReturn(Default)
  }

  it should "Play a round" in {
    val (Some(newPlayer), machine) = fruitMachine.play(player)

    newPlayer shouldBe Player(initialMoney - betMoney)
  }

  it should "play multiple rounds" in {
    val (Some(playa), money) = fruitMachine.play(player)
    val (Some(newPlayer), machine) = fruitMachine.play(playa)

    newPlayer shouldBe Player(initialMoney - betMoney - betMoney)
  }

  it should "win when the odds are right" in {
    when(slotSpinner.spin).thenReturn(AllEqual)
    val (Some(newPlayer), machine) = fruitMachine.play(player)

    newPlayer shouldBe Player(initialMoney)
    machine.jackPot shouldBe 0
  }

  it should "kick out player when money is insufficient" in {
    val (newPlayer, machine) = fruitMachine.play(player.copy(money = costOfSinglePlay - 1))

    newPlayer shouldBe None
    machine.jackPot shouldBe fruitMachine.jackPot
  }

  it should "come with a float" in {
    when(slotSpinner.spin).thenReturn(AllEqual)
    val float = 100
    val fruitMachine = FruitMachine(slotSpinner, costOfSinglePlay, float)
    val (Some(newPlayer), machine) = fruitMachine.play(player)

    newPlayer shouldBe Player(initialMoney + float)
    machine.jackPot shouldBe 0
  }

  it should "pay half jackpot when each slot has different color" in {
    when(slotSpinner.spin).thenReturn(EachDifferent)
    val float = 100

    val fruitMachine = FruitMachine(slotSpinner, costOfSinglePlay, float)
    val (Some(newPlayer), machine) = fruitMachine.play(player)

    newPlayer shouldBe Player((initialMoney - costOfSinglePlay) + ((float + costOfSinglePlay) / 2))
    machine.jackPot shouldBe (fruitMachine.jackPot + costOfSinglePlay) / 2
  }

  it should "give 5 times the cost of single play when two colours are adjacent" in {
    when(slotSpinner.spin).thenReturn(TwoAdjacent)
    val float = 500

    val fruitMachine = FruitMachine(slotSpinner, costOfSinglePlay, float)
    val (Some(newPlayer), machine) = fruitMachine.play(player)

    newPlayer shouldBe Player(initialMoney - costOfSinglePlay + costOfSinglePlay * 5)
    machine.jackPot shouldBe fruitMachine.jackPot - (costOfSinglePlay * 5) + costOfSinglePlay
  }

  it should "credit the user with free plays when full prize is more than money available" in {
    when(slotSpinner.spin).thenReturn(TwoAdjacent)
    val float = 75
    val fruitMachine = FruitMachine(slotSpinner, costOfSinglePlay, float)
    val (Some(newPlayer), machine) = fruitMachine.play(player)
    val moneyWon: Int = costOfSinglePlay * 5

    newPlayer shouldBe Player(initialMoney + fruitMachine.jackPot, moneyWon - (fruitMachine.jackPot + costOfSinglePlay))
    machine.jackPot shouldBe 0
  }

  it should "use up credits if available" in {
    val (Some(newPlayer), machine) = fruitMachine.play(player.copy(credits = 100))

    newPlayer shouldBe Player(initialMoney, 100 - costOfSinglePlay)
    machine.jackPot shouldBe fruitMachine.jackPot + costOfSinglePlay
  }
}

